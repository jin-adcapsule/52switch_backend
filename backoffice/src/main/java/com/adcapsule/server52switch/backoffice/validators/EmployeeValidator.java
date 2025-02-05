package com.adcapsule.server52switch.backoffice.validators;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.adacpsule.server52switch.shared.inputs.EmployeeInput;
import com.adcapsule.server52switch.core.configs.DateUtils;
import com.adcapsule.server52switch.core.models.Employee;
import com.adcapsule.server52switch.core.repositories.EmployeeRepository;
import com.adcapsule.server52switch.core.repositories.GroupRepository;
import com.adcapsule.server52switch.core.repositories.LocationRepository;

@Service
public class EmployeeValidator {

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private GroupRepository groupRepository;

    /**
     * Validates the employee object before it is saved/updated in the database.
     * 
     * @param employee The Employee object to be validated.
     * @return List of validation errors.
     */
    public List<String> validateNewEmployee(Employee employee) {
        List<String> errors = new ArrayList<>();

        // Validate employeeId is positive and unique
        validateEmployeeId(employee.getEmployeeId(), errors);

        // Validate name, email, phone, etc.
        validateName(employee.getName(), errors);
        validateEmail(employee.getEmail(), errors);
        validatePhone(employee.getPhone(), errors);
        validatePosition(employee.getPosition(), errors);
        validateJoindate(employee.getJoindate(), errors);

        // Validate dayoffPerYear and other business logic
        validateDayoffPerYear(employee.getDayoffPerYear(), errors);

        // Validate location and group existence
        validateLocation(employee.getLocationId(), errors);
        validateGroup(employee.getGroupId(), errors);

        return errors;
    }
    public List<String> validateExistingEmployee(EmployeeInput employeeInput) {
        List<String> errors = new ArrayList<>();
        // Update only the fields that are not null or empty
        if (employeeInput.getEmployeeId() != null) {
                    
            validateEmployeeId(employeeInput.getEmployeeId(), errors);
        }
        if (employeeInput.getName() != null) {
            validateName(employeeInput.getName(), errors);
        }
        if (employeeInput.getEmail() != null) {
            validateEmail(employeeInput.getEmail(), errors);
        }
        if (employeeInput.getPosition() != null) {
            validatePosition(employeeInput.getPosition(), errors);
        }
        if (employeeInput.getPhone() != null) {
            validatePhone(employeeInput.getPhone(), errors);
        }
        if (employeeInput.getJoindate() != null) {
            validateJoindate(employeeInput.getJoindate(), errors);
        }
        if (employeeInput.getGroupId() != null) {
            validateGroup(employeeInput.getGroupId(), errors);
        }
        if (employeeInput.getLocationId() != null) {
            validateLocation(employeeInput.getLocationId(), errors);
        }
        if (employeeInput.getDayoffPerYear() != null) {
            validateDayoffPerYear(employeeInput.getDayoffPerYear(), errors);
        }
        
        return errors;
    }
    private void validateEmployeeId(int employeeId, List<String> errors) {
        if (employeeId <= 0) {
            errors.add("Invalid employeeId");
        } else if (isEmployeeIdDuplicate(employeeId)) {
            errors.add("employeeId is already in use");
        }
    }

    private void validateName(String name, List<String> errors) {
        if (!StringUtils.hasText(name)) {
            errors.add("Name cannot be empty");
        }
    }

    private void validateEmail(String email, List<String> errors) {
        if (!isValidEmail(email)) {
            errors.add("Invalid email format");
        } else if (isEmailDuplicate(email)) {
            errors.add("Email is already in use");
        }
    }

    private void validatePhone(String phone, List<String> errors) {
        if (!isValidPhone(phone)) {
            errors.add("Invalid phone format");
        } else if (isPhoneDuplicate(phone)) {
            errors.add("Phone number is already in use");
        }
    }

    private void validatePosition(String position, List<String> errors) {
        if (!StringUtils.hasText(position)) {
            errors.add("Position cannot be empty");
        }
    }

    private void validateJoindate(String joindate, List<String> errors) {
        if (!DateUtils.isValidyyyymmddString(joindate)) {
            errors.add("Joindate cannot be parsed");
        }
    }

    private void validateDayoffPerYear(int dayoffPerYear, List<String> errors) {
        if (dayoffPerYear < 0) {
            errors.add("dayoffPerYear cannot be negative");
        }
    }

    private void validateLocation(String locationId, List<String> errors) {
        if (locationId == null || locationId.isEmpty()) {
            errors.add("locationId cannot be empty");
        } else if (!locationRepository.existsById(locationId)) {
            errors.add("locationId does not exist in the system");
        }
    }

    private void validateGroup(String groupId, List<String> errors) {
        if (groupId == null || groupId.isEmpty()) {
            errors.add("groupId cannot be empty");
        } else if (!groupRepository.existsById(groupId)) {
            errors.add("groupId does not exist in the system");
        }
    }

    private boolean isEmployeeIdDuplicate(int employeeId) {
        return employeeRepository.findByEmployeeId(employeeId).isPresent();
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email != null && email.matches(emailPattern);
    }

    private boolean isEmailDuplicate(String email) {
        return employeeRepository.findByEmail(email).isPresent();
    }

    private boolean isValidPhone(String phone) {
        String phonePattern = "^[0-9]{10,11}$";
        return phone != null && phone.matches(phonePattern);
    }

    private boolean isPhoneDuplicate(String phone) {
        return employeeRepository.findByPhone(phone).isPresent();
    }

    
}
