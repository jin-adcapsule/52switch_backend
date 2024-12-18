package com.example._switch_backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
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
            System.out.println("Notification sent successfully: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void sendNotificationToSupervisor(String objectId, String title, String message) {
        //get SupervisorOid
        String SupervisorOid = employeeService.getSupervisorOidbyEmployeeOid(objectId);
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
            .build();
        //FirebaseMessaging.getInstance().sendAsync(firebaseMessage);
        try {
            String response = FirebaseMessaging.getInstance().send(firebaseMessage);
            
            System.out.println("Notification sent successfully: " + response+" To: "+SupervisorOid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void sendNotificationToEmployeeId(int employeeId, String title, String message) {
        //get SupervisorOid
        String employeeOid = employeeService.getEmployeeMiniByEmployeeId(employeeId).get("employeeOid").toString();
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
            .build();
        //FirebaseMessaging.getInstance().sendAsync(firebaseMessage);
        try {
            String response = FirebaseMessaging.getInstance().send(firebaseMessage);
            
            System.out.println("Notification sent successfully: " + response+" To: "+employeeOid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
