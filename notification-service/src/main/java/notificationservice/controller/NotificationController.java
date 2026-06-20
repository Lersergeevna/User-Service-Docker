package notificationservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import notificationservice.dto.NotificationRequest;
import notificationservice.dto.NotificationResponse;
import notificationservice.service.EmailNotificationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final EmailNotificationService emailNotificationService;

    @PostMapping
    public NotificationResponse sendNotification(@Valid @RequestBody NotificationRequest request) {
        return emailNotificationService.sendNotification(request);
    }
}