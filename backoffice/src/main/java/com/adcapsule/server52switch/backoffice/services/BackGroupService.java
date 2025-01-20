package com.adcapsule.server52switch.backoffice.services;

import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adacpsule.server52switch.shared.inputs.GroupInput;
import com.adcapsule.server52switch.backoffice.configs.BeanUtilsHelper;
import com.adcapsule.server52switch.backoffice.validators.GroupValidator;
import com.adcapsule.server52switch.core.models.Group;
import com.adcapsule.server52switch.core.repositories.GroupRepository;
import com.adcapsule.server52switch.core.services.GroupService;

@Service
public class BackGroupService {
    private final GroupRepository groupRepository;
    private final GroupService groupService;
    private final GroupValidator groupValidator;

    @Autowired
    public BackGroupService(GroupRepository groupRepository,GroupService groupService, GroupValidator groupValidator) {
        this.groupRepository = groupRepository;
        this.groupService = groupService;
        this.groupValidator = groupValidator;
    }

    // Basic CRUD
    public List<Group> findAll() {
        return groupRepository.findAll();
    }

    public String updateGroup(String id, GroupInput input) {
        try {
            // Find existing Group by Id
            Group existingGroup = groupRepository.findById(id).orElse(null);
            if (existingGroup == null) {
                return "No Group exists with Id ";
            }

            // Validate the input fields
            List<String> errors = groupValidator.validateExistingGroup(input);
            if (!errors.isEmpty()) {
                return String.join(", ", errors);
            }

            // Update only non-null fields
            BeanUtilsHelper.updateEntityFields(existingGroup, input);

            // Save the updated Group
            groupRepository.save(existingGroup);
            return "success";
        } catch (Exception e) {
            // Handle exceptions (e.g., database errors)
            return "Error updating Group: " + e.getMessage();
        }
    }
     public String genNewGroup(GroupInput input) {
        
        try {
            Group newGroup = new Group();
           
            // Copy properties from AttendanceInput to the newAttendance object
            BeanUtilsHelper.updateEntityFields(newGroup, input);
            // Validate the input fields (only changed fields will be validated)
            List<String> errors = groupValidator.validateNewGroup(newGroup);
            if (!errors.isEmpty()) {
                return String.join(",", errors);
            }
            // Save the updated one
            groupRepository.save(newGroup);
            return "success";
        } catch (BeansException e) {
            // Handle exceptions (e.g., database errors) and return an error message
            return "Error occurred while saving group:"+ e.getMessage(); // Return false if an error occurs
        }
    }
    
}
