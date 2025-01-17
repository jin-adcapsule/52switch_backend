package com.adcapsule.server52switch.backoffice.services;

import java.util.List;

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
    
}
