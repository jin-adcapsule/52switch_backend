package com.adcapsule.server52switch.core.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification; // Import Notification class

@Service
public class NotificationService {
    private final EmployeeService employeeService;
    private final CredentialService credentialService;

    @Autowired
    public NotificationService(EmployeeService employeeService,CredentialService credentialService) {
        this.employeeService = employeeService;
        this.credentialService = credentialService;
    }
    public void sendNotification(String token, String title, String message) {
       
        Message firebaseMessage = Message.builder()
            .setToken(token)
            .putData("title", title)
            .putData("message", message)
            .setNotification(Notification.builder()
                .setTitle(title)
                .setBody(message)
                .build()) // Notification payload
            .build();
        //FirebaseMessaging.getInstance().sendAsync(firebaseMessage);
        try {
            String response = FirebaseMessaging.getInstance().send(firebaseMessage);
        } catch (FirebaseMessagingException e) {
        }
    }
    public void sendNotificationToSupervisor(String employeeOid, String title, String message,String pageKey) {
        //get SupervisorOid
        String SupervisorOid = employeeService.getSupervisorOidbyEmployeeOid(employeeOid);
        //get token
        String SupervisorToken = credentialService.getFCMToken(SupervisorOid);
        Message firebaseMessage = Message.builder()
            .setToken(SupervisorToken)
            .putData("title", title)
            .putData("message", message)
            .setNotification(Notification.builder()
                .setTitle(title)
                .setBody(message)
                .build()) // Notification payload
            .putData("pageKey",pageKey)
            .build();
        //FirebaseMessaging.getInstance().sendAsync(firebaseMessage);
        try {
            String response = FirebaseMessaging.getInstance().send(firebaseMessage);
            
        } catch (FirebaseMessagingException e) {
        }
    }
    public void sendNotificationToEmployeeOid(String employeeOid, String title, String message,String pageKey) {
        //get token
        String employeeToken = credentialService.getFCMToken(employeeOid);
        
        Message firebaseMessage = Message.builder()
            .setToken(employeeToken)
            .putData("title", title)
            .putData("message", message)
            .setNotification(Notification.builder()
                .setTitle(title)
                .setBody(message)
                .build()) // Notification payload
            .putData("pageKey", pageKey)
            .build();
        //FirebaseMessaging.getInstance().sendAsync(firebaseMessage);
        try {
            String response = FirebaseMessaging.getInstance().send(firebaseMessage);
        } catch (FirebaseMessagingException e) {
        }
    }
}
