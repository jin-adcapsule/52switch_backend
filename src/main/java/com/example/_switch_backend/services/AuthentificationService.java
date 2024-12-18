package com.example._switch_backend.services;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example._switch_backend.models.Attendance;
import com.example._switch_backend.models.Employee;
import com.example._switch_backend.repositories.AttendanceRepository;
import com.example._switch_backend.repositories.EmployeeRepository;
import com.example._switch_backend.repositories.GroupRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;////firebase

@Service
public class AuthentificationService {

    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final GroupRepository groupRepository;
    private final CredentialService credentialService;
    @Autowired
    public AuthentificationService(EmployeeRepository employeeRepository, AttendanceRepository attendanceRepository, GroupRepository groupRepository,CredentialService credentialService) {
        this.employeeRepository = employeeRepository;
        this.attendanceRepository =  attendanceRepository;
        this.groupRepository =  groupRepository;
        this.credentialService = credentialService;

    }
    

    public Map<String, Object> validateUidAndPhone(String uid, String phone) {
        try {
            //System.out.println("FIREBASE_AUTH_EMULATOR_HOST: " + System.getenv ("FIREBASE_AUTH_EMULATOR_HOST"));

            // Retrieve user record from Firebase by UID
            UserRecord userRecord = getUserWithRetry(uid, 5, 500); // Retry 5 times with 500ms delay//UserRecord userRecord = FirebaseAuth.getInstance().getUser(uid);
            //System.out.println("uid_found:");
            //System.out.println(uid);
            //System.out.println(userRecord);
            // Extract phone number from user record
            String firebasePhone = userRecord.getPhoneNumber();
            String formattedPhone = firebasePhone.startsWith("+82") ? "0" + firebasePhone.substring(3) : firebasePhone;
            // Check if phone number matches
            if (formattedPhone == null || !formattedPhone.equals(phone)) {
                //System.out.println(firebasePhone);
                //System.out.println(phone);
                throw new IllegalArgumentException("UID and phone number do not match.");
            }

            // Retrieve employee data from your database
            Employee employee = employeeRepository.findByPhone(phone)
                .orElseThrow(() -> new IllegalArgumentException("Phone number not found in the database."));
            // Check if there is today's attendance with status true
            LocalDate today = LocalDate.now();
            boolean isCurrentlyMarked = attendanceRepository.findByEmployeeIdAndDate(employee.getEmployeeId(), today.toString())
                    .map(Attendance::getStatus)
                    .orElse(false);
            //check employee is allocated as group leader
            Boolean isSupervisor = groupRepository.existsByGroupSupervisorEid(employee.getEmployeeId());   
            // update fcmToken

            // Return employee details
            Map<String, Object> response = new HashMap<>();
            response.put("objectId", employee.getId());
            response.put("employeeName", employee.getName());
            
            response.put("isSupervisor", isSupervisor);
            response.put("currently_marked", isCurrentlyMarked);
            
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error validating UID and phone number: " + e.getMessage());
        }
    
    }
    private UserRecord getUserWithRetry(String uid, int maxRetries, int delayMs) {
        int attempts = 0;
        while (attempts < maxRetries) {
            try {
                return FirebaseAuth.getInstance().getUser(uid);
            } catch (FirebaseAuthException e) {
                attempts++;
                System.out.println("Attempt " + attempts + " to fetch UID failed: " + e.getMessage());
                if (attempts >= maxRetries) {
                    throw new RuntimeException("Failed to retrieve user after " + maxRetries + " attempts.");
                }
                try {
                    Thread.sleep(delayMs); // Wait before retrying
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Retry interrupted", ie);
                }
            }
        }
        throw new RuntimeException("Unable to retrieve user after retries.");
    }
}
