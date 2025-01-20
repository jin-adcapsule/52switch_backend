package com.adcapsule.server52switch.backoffice.services;

import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adacpsule.server52switch.shared.inputs.DayoffInput;
import com.adcapsule.server52switch.backoffice.configs.BeanUtilsHelper;
import com.adcapsule.server52switch.backoffice.validators.DayoffValidator;
import com.adcapsule.server52switch.core.models.Dayoff;
import com.adcapsule.server52switch.core.repositories.DayoffRepository;

@Service
public class BackDayoffService {
    private final DayoffRepository dayoffRepository;
    private final DayoffValidator dayoffValidator;

    @Autowired
    public BackDayoffService(DayoffRepository dayoffRepository, DayoffValidator dayoffValidator) {
        this.dayoffRepository = dayoffRepository;
        this.dayoffValidator = dayoffValidator;
    }

    // Basic CRUD
    public List<Dayoff> findAll() {
        return dayoffRepository.findAll();
    }

    public String updateDayoff(String id, DayoffInput input) {
        try {
            // Find existing Dayoff by Id
            Dayoff existingDayoff = dayoffRepository.findById(id).orElse(null);
            if (existingDayoff == null) {
                return "No Dayoff exists with Id ";
            }
            // Supplement missing fields in the input with existing values
            if (input.getEmployeeOid() == null) {
                input.setEmployeeOid(existingDayoff.getEmployeeOid());
            }
            if (input.getDayoffDate() == null) {
                input.setDayoffDate(existingDayoff.getDayoffDate());
            }
            // Validate the input fields
            List<String> errors = dayoffValidator.validateExistingDayoff(input);
            if (!errors.isEmpty()) {
                return String.join(", ", errors);
            }

            // Update only non-null fields
            BeanUtilsHelper.updateEntityFields(existingDayoff, input);

            // Save the updated Dayoff
            dayoffRepository.save(existingDayoff);
            return "success";
        } catch (Exception e) {
            // Handle exceptions (e.g., database errors)
            return "Error updating Dayoff: " + e.getMessage();
        }
    }
    public String genNewDayoff(DayoffInput input) {
        
        try {
            Dayoff newDayoff = new Dayoff();
           
            // Copy properties from AttendanceInput to the newAttendance object
            BeanUtilsHelper.updateEntityFields(newDayoff, input);
            // Validate the input fields (only changed fields will be validated)
            List<String> errors = dayoffValidator.validateNewDayoff(newDayoff);
            if (!errors.isEmpty()) {
                return String.join(",", errors);
            }
            // Save the updated one
            dayoffRepository.save(newDayoff);
            return "success";
        } catch (BeansException e) {
            // Handle exceptions (e.g., database errors) and return an error message
            return "Error occurred while saving dayoff:"+ e.getMessage(); // Return false if an error occurs
        }
    }
    public Boolean deleteDayoffById(String id) {
        // Find the dayoff by ID to ensure it exists
        Dayoff dayoff = dayoffRepository.findById(id).orElse(null);
        if (dayoff == null) {
            return false;
        }
        // Perform the delete operation
        dayoffRepository.deleteById(id);
        return true;
        
    }
}
