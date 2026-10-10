package com.studentmanagement.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.javamastery.module10.Student;
import com.javamastery.module10.StudentManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class StudentWebController {

    private final StudentManagementService studentManagementService;

    @GetMapping("/students")
    public String listStudents(Model model, @AuthenticationPrincipal OAuth2User principal) {
        model.addAttribute("students", studentManagementService.getAllStudents());
        if (principal != null) {
            model.addAttribute("userEmail", principal.getAttribute("email"));
        }
        return "students"; // resolves to templates/students.html
    }

    @PostMapping("/students")
    public String addStudent(@RequestParam String id,
                             @RequestParam String name,
                             @RequestParam String email,
                             @RequestParam double gpa) {
        studentManagementService.addStudent(new Student(id, name, email, gpa));
        return "redirect:/students"; // redirect-after-post
    }
}