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
    public String sendNotificationToSupervisor(@Argument String employeeOid, @Argument String title, @Argument String message, @Argument String pageKey) {
        try{
            notificationService.sendNotificationToSupervisor(employeeOid, title, message,pageKey);
            return "Notification sent successfully";
        }catch (Exception e){
            return "Failed to send Notification:" +e.getMessage();
        }
    }
    @MutationMapping
    public String sendNotificationToEmployeeOid(@Argument String employeeOid, @Argument String title, @Argument String message, @Argument String pageKey) {
        try{
            notificationService.sendNotificationToEmployeeOid(employeeOid, title, message,pageKey);
            return "Notification sent successfully";
        }catch (Exception e){
            return "Failed to send Notification:" +e.getMessage();
        }
    }
}
