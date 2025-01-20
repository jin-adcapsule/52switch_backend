package com.adcapsule.server52switch.backoffice.services;

import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adacpsule.server52switch.shared.inputs.AttendanceInput;
import com.adcapsule.server52switch.backoffice.configs.BeanUtilsHelper;
import com.adcapsule.server52switch.backoffice.validators.AttendanceValidator;
import com.adcapsule.server52switch.core.models.Attendance;
import com.adcapsule.server52switch.core.repositories.AttendanceRepository;
@Service
public class BackAttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final AttendanceValidator attendanceValidator;


    @Autowired
    public BackAttendanceService(AttendanceRepository attendanceRepository,AttendanceValidator attendanceValidator) {
        this.attendanceRepository = attendanceRepository;
        this.attendanceValidator = attendanceValidator;

        
    }

    //Basic CRUD//////////////////
    public List<Attendance> findAll(){
        return attendanceRepository.findAll();
    }

    
public String updateAttendance(String id, AttendanceInput input) {
        
        try {
            // Find existing instance by OID
            Attendance existingAttendance = attendanceRepository.findById(id).orElse(null);
            if (existingAttendance==null){
                return "No attendance exists with Id ";
            }
            // Validate the input fields (only changed fields will be validated)
            List<String> errors = attendanceValidator.validateExistingAttendance(input);
            if (!errors.isEmpty()) {
                return String.join(", ", errors);
            }

            // Update only non-null fields in the existing entity
            BeanUtilsHelper.updateEntityFields(existingAttendance, input);
            // Save the updated one
            attendanceRepository.save(existingAttendance);
            return "success";
        } catch (Exception e) {
            // Handle exceptions (e.g., database errors)
            return "Handle exceptions"; // Return false if an error occurs
        }
    }
public String genNewAttendance(AttendanceInput input) {
        
        try {
            Attendance newAttendance = new Attendance();
            
            // Copy properties from AttendanceInput to the newAttendance object
            BeanUtilsHelper.updateEntityFields(newAttendance, input);
            // Validate the input fields (only changed fields will be validated)
            List<String> errors = attendanceValidator.validateNewAttendance(newAttendance);
            if (!errors.isEmpty()) {
                return String.join(",", errors);
            }
            // Save the updated one
            attendanceRepository.save(newAttendance);
            return "success";
        } catch (BeansException e) {
            // Handle exceptions (e.g., database errors) and return an error message
            return "Error occurred while saving attendance:"+ e.getMessage(); // Return false if an error occurs
        }
    }
    public Boolean deleteAttendanceById(String id) {
        // Find the attendance by ID to ensure it exists
        Attendance attendance = attendanceRepository.findById(id).orElse(null);
        if (attendance == null) {
            return false;
        }
        // Perform the delete operation
        attendanceRepository.deleteById(id);
        return true;
        
    }
}
