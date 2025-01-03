package com.adcapsule.server52switch.core.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adcapsule.server52switch.core.models.Credential;
import com.adcapsule.server52switch.core.repositories.CredentialRepository;
////firebase

@Service
public class CredentialService {

    private final CredentialRepository credentialRepository;

    @Autowired
    public CredentialService(CredentialRepository credentialRepository) {
        this.credentialRepository = credentialRepository;

    }
    

    public boolean saveFCMToken(String employeeOid, String fcmToken) {
        // Try to find the existing credential document by employeeOid
        Credential credentialDoc = credentialRepository.findbyEmployeeOid(employeeOid);
        System.out.println(employeeOid);
        System.out.println(fcmToken);
        
        if (credentialDoc == null) {
            // If no document is found, create a new one
            credentialDoc = new Credential();
            
        } 
        credentialDoc.setEmployeeOid(employeeOid);
        credentialDoc.setFcmToken(fcmToken); // Set the FCM token for the first time
        credentialRepository.save(credentialDoc); // Save the new document
        return true;
    }
    public String getFCMToken(String employeeOid) {
        // Fetch the FCM token from MongoDB
        Credential credentialOpt = credentialRepository.findbyEmployeeOid(employeeOid);
        if (credentialOpt== null) {
            System.out.println("FCM Token not found for user: " + employeeOid);
            return null;
        }

        String fcmToken = credentialOpt.getFcmToken();
        if (fcmToken == null) {
            System.out.println("FCM Token is null");
            return null;
        }
        return fcmToken;
    }
}
