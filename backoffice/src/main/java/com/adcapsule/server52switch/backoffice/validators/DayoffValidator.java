package com.adcapsule.server52switch.backoffice.validators;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adacpsule.server52switch.shared.inputs.DayoffInput;
import com.adcapsule.server52switch.core.configs.DateUtils;
import com.adcapsule.server52switch.core.models.Dayoff;
import com.adcapsule.server52switch.core.repositories.DayoffRepository;
import com.adcapsule.server52switch.core.repositories.EmployeeRepository;

@Service
public class DayoffValidator {

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private DayoffRepository dayoffRepository;



    /**
     * Validates a new Dayoff object before saving it in the database.
     * Checks employeeOid, groupId, locationId, supervisorOid, status field, and dayoffDate.
     *
     * @param dayoff The Dayoff object to be validated.
     * @return List of validation errors.
     */
    public List<String> validateNewDayoff(Dayoff dayoff) {
        List<String> errors = new ArrayList<>();

        // Validate employeeOid, groupId, locationId, and supervisorOid existence
        validateEmployeeOid(dayoff.getEmployeeOid(), errors);
        validateSupervisorOid(dayoff.getSupervisorOid(), errors);

        // Validate dayoffDate, requestStatus, requestKey
        validateDayoffDate(dayoff.getDayoffDate(), errors);
        validateStatus(dayoff.getRequestStatus(), errors);
        validateRequestKey(dayoff.getRequestKey(), errors);
         // Validate uniqueness of the combination of employeeOid and dayoffDate
         validateUniqueDayoff(dayoff.getEmployeeOid(), dayoff.getDayoffDate(), errors);

        return errors;
    }

    /**
     * Validates an existing Dayoff object before updating it in the database.
     * Only validates fields that are not null or empty in the input.
     *
     * @param dayoff The Dayoff object to be validated.
     * @return List of validation errors.
     */
    public List<String> validateExistingDayoff(DayoffInput dayoff) {
        List<String> errors = new ArrayList<>();

        // Update only the fields that are not null
        if (dayoff.getEmployeeOid() != null) {
            validateEmployeeOid(dayoff.getEmployeeOid(), errors);
        }

        if (dayoff.getSupervisorOid() != null) {
            validateSupervisorOid(dayoff.getSupervisorOid(), errors);
        }
        if (dayoff.getDayoffDate() != null) {
            validateDayoffDate(dayoff.getDayoffDate(), errors);
        }
        if (dayoff.getRequestStatus() != null) {
            validateStatus(dayoff.getRequestStatus(), errors);
        }
        if (dayoff.getRequestKey() != null) {
            validateRequestKey(dayoff.getRequestKey(), errors);
        }
        // Validate uniqueness of the combination of employeeOid and dayoffDate for existing dayoffs
        if (dayoff.getEmployeeOid() != null && dayoff.getDayoffDate() != null) {
            validateUniqueDayoff(dayoff.getEmployeeOid(), dayoff.getDayoffDate(), errors);
        }
        return errors;
    }

    // Validation helper methods

    private void validateEmployeeOid(String employeeOid, List<String> errors) {
        if (employeeOid == null || employeeOid.isEmpty()) {
            errors.add("Employee OID cannot be empty");
        } else if (!employeeRepository.existsById(employeeOid)) {
            errors.add("Employee OID does not exist in the system");
        }
    }


    private void validateSupervisorOid(String supervisorOid, List<String> errors) {
        if (supervisorOid == null || supervisorOid.isEmpty()) {
            errors.add("Supervisor OID cannot be empty");
        } else if (!employeeRepository.existsById(supervisorOid)) {
            errors.add("Supervisor OID does not exist in the system");
        }
    }

    private void validateDayoffDate(String dayoffDate, List<String> errors) {
        if (dayoffDate == null || dayoffDate.isEmpty()) {
            errors.add("Day-off date cannot be empty");
        } else if (!DateUtils.isValidDateString(dayoffDate)) {
            errors.add("Invalid date format. The date must be in yyyy-MM-dd format.");
        }
    }

    /**
     * Validates that the status is one of the allowed values: "pending", "approved", or "denied".
     *
     * @param status The status to be validated.
     * @param errors List to collect validation errors.
     */
    private void validateStatus(String status, List<String> errors) {
        if (status == null || status.isEmpty()) {
            errors.add("Status cannot be empty");
        } else if (!(status.equals("pending") || status.equals("approved") || status.equals("denied"))) {
            errors.add("Status must be one of the following: 'pending', 'approved', or 'denied'");
        }
    }

    private void validateRequestKey(String requestKey, List<String> errors) {
        if (requestKey == null || requestKey.isEmpty()) {
            errors.add("Request Key cannot be empty");
        }
        // Additional request key validation logic can be added here if necessary
    }
     /**
     * Validates the uniqueness of the combination of employeeOid and dayoffDate.
     *
     * @param employeeOid The employee OID.
     * @param dayoffDate  The dayoff date.
     * @param errors      List to collect validation errors.
     */
    private void validateUniqueDayoff(String employeeOid, String dayoffDate, List<String> errors) {
        if (dayoffRepository.existsByEmployeeOidAndDayoffDate(employeeOid, dayoffDate)) {
            errors.add("An existing dayoff request already exists for this employee on this date.");
        }
    }
}
