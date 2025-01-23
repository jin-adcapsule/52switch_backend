package com.adcapsule.server52switch.backoffice.validators;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adacpsule.server52switch.shared.inputs.AttendanceInput;
import com.adcapsule.server52switch.core.configs.DateUtils;
import com.adcapsule.server52switch.core.models.Attendance;
import com.adcapsule.server52switch.core.repositories.EmployeeRepository;
import com.adcapsule.server52switch.core.repositories.LocationRepository;

@Service
public class AttendanceValidator {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private LocationRepository locationRepository;

    /**
     * Validates a new Attendance object before saving it in the database.
     * Checks employeeOid, locationId, status, date, and times.
     *
     * @param attendance The Attendance object to be validated.
     * @return List of validation errors.
     */
    public List<String> validateNewAttendance(Attendance attendance) {
        List<String> errors = new ArrayList<>();

        // Validate employeeOid and locationId existence
        validateEmployeeOid(attendance.getEmployeeOid(), errors);
        validateLocationId(attendance.getLocationId(), errors);

        // Validate status (status can be null, so no error for null)
        //validateStatus(attendance.getStatus(), errors);

        // Validate date format and times
        validateDate(attendance.getDate(), errors);
        validateCheckInOutTimes(attendance.getCheckInTime(), attendance.getCheckOutTime(), errors);

        return errors;
    }

    /**
     * Validates an existing Attendance object before updating it in the database.
     * Only validates fields that are not null or empty in the input.
     *
     * @param attendance The Attendance object to be validated.
     * @return List of validation errors.
     */
    public List<String> validateExistingAttendance(AttendanceInput attendance) {
        List<String> errors = new ArrayList<>();

        // Update only the fields that are not null
        if (attendance.getEmployeeOid() != null) {
            validateEmployeeOid(attendance.getEmployeeOid(), errors);
        }
        if (attendance.getLocationId() != null) {
            validateLocationId(attendance.getLocationId(), errors);
        }
        // if (attendance.getStatus() != null) {
        //     validateStatus(attendance.getStatus(), errors);
        // }
        if (attendance.getDate() != null) {
            validateDate(attendance.getDate(), errors);
        }
        if (attendance.getCheckInTime() != null || attendance.getCheckOutTime() != null) {
            validateCheckInOutTimes(attendance.getCheckInTime(), attendance.getCheckOutTime(), errors);
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

    private void validateLocationId(String locationId, List<String> errors) {
        if (locationId == null || locationId.isEmpty()) {
            errors.add("Location ID cannot be empty");
        } else if (!locationRepository.existsById(locationId)) {
            errors.add("Location ID does not exist in the system");
        }
    }



    private void validateDate(String date, List<String> errors) {
        if (date == null || date.isEmpty()) {
            errors.add("Date cannot be empty");
        } else if (!DateUtils.isValidyyyymmddString(date)) {
            errors.add("Invalid date format. The date must be in yyyy-MM-dd format.");
        }
    }
    private void validateCheckInOutTimes(Long checkInTime, Long checkOutTime, List<String> errors) {
        if (checkInTime == null || checkOutTime == null) {
            errors.add("Check-in and Check-out times cannot be null");
        } else if (checkInTime >= checkOutTime) {
            errors.add("Check-out time must be after Check-in time");
        }
    }
}
