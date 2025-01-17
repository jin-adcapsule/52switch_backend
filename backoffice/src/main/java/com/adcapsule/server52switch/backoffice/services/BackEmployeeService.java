package com.adcapsule.server52switch.backoffice.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adacpsule.server52switch.shared.inputs.EmployeeInput;
import com.adcapsule.server52switch.backoffice.configs.BeanUtilsHelper;
import com.adcapsule.server52switch.backoffice.validators.EmployeeValidator;
import com.adcapsule.server52switch.core.models.Employee;
import com.adcapsule.server52switch.core.repositories.EmployeeRepository;
@Service
public class BackEmployeeService {
    private final EmployeeRepository employeeRepository;
    private final EmployeeValidator employeeValidator;


    @Autowired
    public BackEmployeeService(EmployeeRepository employeeRepository,EmployeeValidator employeeValidator) {
        this.employeeRepository = employeeRepository;
        this.employeeValidator = employeeValidator;

        
    }

    //Basic CRUD//////////////////
    public List<Employee> findAll(){
        return employeeRepository.findAll();
    }

    public String updateEmployee(String employeeOid, EmployeeInput employeeInput) {
        
        try {
            // Find employee by OID
            Employee existingEmployee = employeeRepository.findById(employeeOid).orElse(null);
            if (existingEmployee==null){
                return "No employee exists with Id ";
            }
            // System.out.println("heeee");
            // System.out.println(existingEmployee.getPhone());
            // Validate the employeeInput fields (only changed fields will be validated)
            List<String> errors = employeeValidator.validateExistingEmployee(employeeInput);
            if (!errors.isEmpty()) {
                return String.join(", ", errors);
            }
            // // System.out.println(errors);
            // if (errors.isEmpty()) {
            //     Employee employee = existingEmployee;
            //     // Update only the fields that are not null or empty
            //     if (employeeInput.getEmployeeId() != null) {
                    
            //         employee.setEmployeeId(employeeInput.getEmployeeId());
            //     }
            //     if (employeeInput.getName() != null) {
            //         employee.setName(employeeInput.getName());
            //     }
            //     if (employeeInput.getEmail() != null) {
            //         employee.setEmail(employeeInput.getEmail());
            //     }
            //     if (employeeInput.getPosition() != null) {
            //         employee.setPosition(employeeInput.getPosition());
            //     }
            //     if (employeeInput.getPhone() != null) {
            //         employee.setPhone(employeeInput.getPhone());
            //     }
            //     if (employeeInput.getJoindate() != null) {
            //         employee.setJoindate(employeeInput.getJoindate());
            //     }
            //     if (employeeInput.getGroupId() != null) {
            //         employee.setGroupId(employeeInput.getGroupId());
            //     }
            //     if (employeeInput.getLocationId() != null) {
            //         employee.setLocationId(employeeInput.getLocationId());
            //     }
            //     if (employeeInput.getDayoffPerYear() != null) {
            //         employee.setDayoffPerYear(employeeInput.getDayoffPerYear());
            //     }
            //     // Save the updated employee back to the repository
            //     employeeRepository.save(employee);
            //     return "success";

            // }else{
            //     return String.join(", ", errors);}

            // Update only non-null fields in the existing entity
            BeanUtilsHelper.updateEntityFields(existingEmployee, employeeInput);
            // Save the updated employee
            employeeRepository.save(existingEmployee);
            return "success";
        } catch (Exception e) {
            // Handle exceptions (e.g., database errors)
            return "Handle exceptions"; // Return false if an error occurs
        }
    }

    /**
     * Save or update employee data.
     * 
     * @param employee The Employee object to be saved or updated.
     * @return a success message if valid, or validation error messages.
     */
    public String saveNewEmployee(Employee employee) {
        // Validate the employee object
        List<String> validationErrors = employeeValidator.validateNewEmployee(employee);

        if (!validationErrors.isEmpty()) {
            // If there are validation errors, return them as a string or you can throw an exception
            return String.join(", ", validationErrors);
        }

        // If validation passes, save the employee to the database
        employeeRepository.save(employee);
        return "Employee saved successfully!";
    }

    
}
