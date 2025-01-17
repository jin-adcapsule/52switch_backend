package com.adcapsule.server52switch.backoffice.validators;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adacpsule.server52switch.shared.inputs.GroupInput;
import com.adcapsule.server52switch.core.models.Group;
import com.adcapsule.server52switch.core.repositories.EmployeeRepository;
import com.adcapsule.server52switch.core.repositories.GroupRepository;

@Service
public class GroupValidator {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    /**
     * Validates a new Group object before saving it in the database.
     * Checks for the uniqueness of groupName, and the presence of parentGroupId and groupSupervisorOid.
     */
    public List<String> validateNewGroup(Group group) {
        List<String> errors = new ArrayList<>();

        // Validate that groupName is unique
        validateUniqueGroupName(group.getGroupName(), errors);

        // Validate the presence of parentGroupId in existing groups
        validateParentGroup(group.getParentGroupId(), errors);

        // Validate the presence of groupSupervisorOid in the employee collection
        validateGroupSupervisor(group.getGroupSupervisorOid(), errors);

        return errors;
    }

    /**
     * Validates an existing Group object, ensuring only provided fields are updated.
     */
    public List<String> validateExistingGroup(GroupInput group) {
        List<String> errors = new ArrayList<>();

        // Check if groupName needs to be unique if updated
        if (group.getGroupName() != null) {
            validateUniqueGroupName(group.getGroupName(), errors);
        }

        // Check if parentGroupId and groupSupervisorOid need to be validated
        if (group.getParentGroupId() != null) {
            validateParentGroup(group.getParentGroupId(), errors);
        }
        if (group.getGroupSupervisorOid() != null) {
            validateGroupSupervisor(group.getGroupSupervisorOid(), errors);
        }

        return errors;
    }

    /**
     * Validates that the groupName is unique in the database.
     */
    private void validateUniqueGroupName(String groupName, List<String> errors) {
        if (groupRepository.existsByGroupName(groupName)) {
            errors.add("Group name must be unique.");
        }
    }

    /**
     * Validates the presence of parentGroupId in the existing groups.
     */
    private void validateParentGroup(String parentGroupId, List<String> errors) {
        if (parentGroupId != null && !groupRepository.existsById(parentGroupId)) {
            errors.add("Parent group does not exist.");
        }
    }

    /**
     * Validates the presence of groupSupervisorOid in the Employee collection.
     */
    private void validateGroupSupervisor(String groupSupervisorOid, List<String> errors) {
        if (groupSupervisorOid != null && !employeeRepository.existsById(groupSupervisorOid)) {
            errors.add("Group supervisor does not exist in the employee database.");
        }
    }
}
