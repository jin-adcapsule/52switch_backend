package com.adcapsule.server52switch.core.resolvers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import com.adcapsule.server52switch.core.services.NotificationService;


@Controller
public class NotificationResolver {
    @Autowired
    private NotificationService notificationService;

    @MutationMapping
    public String sendNotification(@Argument String token, @Argument String title, @Argument String message) {
        notificationService.sendNotification(token, title, message);
        return "Notification sent successfully";
    }
    @MutationMapping
    public String sendNotificationToSupervisor(@Argument String objectId, @Argument String title, @Argument String message) {
        notificationService.sendNotificationToSupervisor(objectId, title, message);
        return "Notification sent successfully";
    }
    @MutationMapping
    public String sendNotificationToEmployeeId(@Argument int employeeId, @Argument String title, @Argument String message) {
        notificationService.sendNotificationToEmployeeId(employeeId, title, message);
        return "Notification sent successfully";
    }
}
