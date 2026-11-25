package com.studentmanagement.controller;

import com.studentmanagement.client.EnrollmentNotice;
import com.studentmanagement.client.NotificationStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
public class NotificationStubController {

    @PostMapping("/enrollment")
    public NotificationStatus receiveEnrollment(@RequestBody EnrollmentNotice notice) {
        // Stub: pretend to dispatch a notification, return a status
        return new NotificationStatus("NOTIF-" + notice.studentId(), "SENT");
    }

    @GetMapping("/{id}/status")
    public NotificationStatus getStatus(@PathVariable String id) {
        return new NotificationStatus(id, "SENT");
    }
}
