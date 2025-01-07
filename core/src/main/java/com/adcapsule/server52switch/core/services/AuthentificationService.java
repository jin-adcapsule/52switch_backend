package com.adcapsule.server52switch.core.services;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adcapsule.server52switch.core.dtos.EmployeeValDTO;
import com.adcapsule.server52switch.core.models.Attendance;
import com.adcapsule.server52switch.core.models.Employee;
import com.adcapsule.server52switch.core.repositories.AttendanceRepository;
import com.adcapsule.server52switch.core.repositories.EmployeeRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
////firebase

@Service
public class AuthentificationService {

    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final GroupService groupService;

    @Autowired
    public AuthentificationService(EmployeeRepository employeeRepository, AttendanceRepository attendanceRepository, GroupService groupService) {
        this.employeeRepository = employeeRepository;
        this.attendanceRepository =  attendanceRepository;
        this.groupService =  groupService;

    }
    

    public EmployeeValDTO validateUidAndPhone(String uid, String phone) {
        try {
            // Retrieve user record from Firebase by UID
            UserRecord userRecord = getUserWithRetry(uid, 5, 500); // Retry 5 times with 500ms delay//UserRecord userRecord = FirebaseAuth.getInstance().getUser(uid);
            // Extract phone number from user record
            String firebasePhone = userRecord.getPhoneNumber();
            if(firebasePhone ==null){
                throw new IllegalArgumentException("Phonenumber is null");
            }
            String formattedPhone = firebasePhone.startsWith("+82") ? "0" + firebasePhone.substring(3) : firebasePhone;
            // Check if phone number matches
            if (formattedPhone == null || !formattedPhone.equals(phone)) {
                throw new IllegalArgumentException("UID and phone number do not match.");
            }

            // Retrieve employee data from your database
            Employee employee = employeeRepository.findByPhone(phone)
                .orElseThrow(() -> new IllegalArgumentException("Phone number not found in the database."));
            String employeeOid = employee.getId();
            String employeeName = employee.getName();
            LocalDate today = LocalDate.now();
            boolean isCurrentlyMarked = attendanceRepository.findByEmployeeOidAndDate(employeeOid, today.toString())
                    .map(Attendance::getStatus)
                    .orElse(false);
            //check employee is allocated as group leader
            boolean isSupervisor = groupService.existsByGroupSupervisorOid(employeeOid);   
            // update fcmToken
            // Map Attendance to AttendanceHistory DTO
            return new EmployeeValDTO(
                    employeeOid,
                    employeeName,
                    isSupervisor,
                    isCurrentlyMarked
  
                );
        } catch (IllegalArgumentException e) {
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
