package com.studentmanagement.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

public interface NotificationServiceClient {

    @PostExchange("/notifications/enrollment")
    NotificationStatus notifyEnrollment(@RequestBody EnrollmentNotice notice);

    @GetExchange("/notifications/{id}/status")
    NotificationStatus getStatus(@PathVariable String id);
}
