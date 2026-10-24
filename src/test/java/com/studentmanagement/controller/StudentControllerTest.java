package com.studentmanagement.controller;

import com.javamastery.module10.Student;
import com.javamastery.module10.StudentManagementService;
import com.javamastery.module10.StudentNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentManagementService studentManagementService;

    @Test
    void getStudentV1_returns200AndJson_whenFound() throws Exception {
        when(studentManagementService.getStudent("S001"))
                .thenReturn(new Student("S001", "Alice", "alice@uni.edu", 3.9));

        mockMvc.perform(get("/api/students/{id}", "S001").header("X-API-Version", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("S001"))
                .andExpect(jsonPath("$.name").value("Alice"));
    }

    @Test
    void getStudentV1_returns404_whenMissing() throws Exception {
        when(studentManagementService.getStudent("S999"))
                .thenThrow(new StudentNotFoundException("S999"));

        mockMvc.perform(get("/api/students/{id}", "S999").header("X-API-Version", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAll_returnsStudentList() throws Exception {
        when(studentManagementService.getAllStudents())
                .thenReturn(List.of(
                        new Student("S001", "Alice", "alice@uni.edu", 3.9),
                        new Student("S002", "Bob", "bob@uni.edu", 3.2)));

        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void create_returns201_whenAdminAndValid() throws Exception {
        String json = "{\"id\":\"S010\",\"name\":\"Ada\",\"email\":\"ada@uni.edu\",\"gpa\":3.95}";

        mockMvc.perform(post("/api/students")
                        .with(httpBasic("admin", "C0mm@nd0"))   // real admin creds from your SecurityConfig
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void create_returns401or403_whenNotAuthenticated() throws Exception {
        String json = "{\"id\":\"S010\",\"name\":\"Ada\",\"email\":\"ada@uni.edu\",\"gpa\":3.95}";

        mockMvc.perform(post("/api/students").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().is4xxClientError());
    }
}