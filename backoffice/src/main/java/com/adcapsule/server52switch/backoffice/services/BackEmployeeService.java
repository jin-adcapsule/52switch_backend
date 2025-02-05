package com.adcapsule.server52switch.backoffice.services;

import java.util.List;

import org.springframework.beans.BeansException;
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

    public String genNewEmployee(EmployeeInput input) {
        
        try {
            Employee newEmployee = new Employee();
            
            // Copy properties from AttendanceInput to the newAttendance object
            BeanUtilsHelper.updateEntityFields(newEmployee, input);
            // Validate the input fields (only changed fields will be validated)
            List<String> errors = employeeValidator.validateNewEmployee(newEmployee);
            if (!errors.isEmpty()) {
                return String.join(",", errors);
            }
            // Save the updated one
            employeeRepository.save(newEmployee);
            return "success";
        } catch (BeansException e) {
            // Handle exceptions (e.g., database errors) and return an error message
            return "Error occurred while saving employee:"+ e.getMessage(); // Return false if an error occurs
        }
    }
    public Boolean deleteEmployeeById(String id) {
        // Find the employee by ID to ensure it exists
        Employee employee = employeeRepository.findById(id).orElse(null);
        if (employee == null) {
            return false;
        }
        // Perform the delete operation
        employeeRepository.deleteById(id);
        return true;
        
    }
    
}
