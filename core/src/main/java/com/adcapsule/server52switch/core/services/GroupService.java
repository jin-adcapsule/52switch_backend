package com.adcapsule.server52switch.core.services;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adcapsule.server52switch.core.models.Group;
import com.adcapsule.server52switch.core.repositories.GroupRepository;
import com.adcapsule.server52switch.core.repositories.projection.Projection.IdProjection;

@Service
public class GroupService {
    private final GroupRepository groupRepository; 
    
    @Autowired
        public GroupService(GroupRepository groupRepository) {

            this.groupRepository = groupRepository;

            
        }

   

    /**
     * Check if an employee is a supervisor of any group.
     *
     * @param employeeId the ID of the employee to check.
     * @return true if the employee is a supervisor, false otherwise.
     */
    public boolean existsByGroupSupervisorEid(int employeeId) {
        return groupRepository.existsByGroupSupervisorEid(employeeId);
    }

    public List<String> findGroupIdListBySupervisorId(int supervisorId) {
        return groupRepository.findGroupIdListBySupervisorId(supervisorId)
            .stream()
            .map(IdProjection::getId) // Access the `_id` field through the projection
            .collect(Collectors.toList());
    }
    public List<String> findGroupIdListBySupervisorOid(String supervisorOid) {
        return groupRepository.findGroupIdListBySupervisorOid(supervisorOid)
            .stream()
            .map(IdProjection::getId) // Access the `_id` field through the projection
            .collect(Collectors.toList());
    }
    /**
     * Fetches all subgroups (direct and nested) for groups supervised by a given supervisor ID.
     * 
     * @param supervisorOid The ID of the supervisor to filter groups.
     * @return A list of all subgroups (recursively fetched).
     *         The result excludes duplicate groups and prevents infinite loops in case of cycles.
     */
    public List<Group> getAllSubGroupsBySupervisorOid(String supervisorOid) {
        // Step 1: Fetch root groups supervised by the given ID
        List<Group> rootGroups = groupRepository.findByGroupSupervisorOid(supervisorOid);

        // Step 2: Initialize a Set to store all subgroups (avoids duplicates)
        Set<Group> allSubGroups = new HashSet<>();
        Set<String> visited = new HashSet<>(); // Tracks already-visited group IDs to prevent infinite recursion

        // Step 3: Recursively find all subgroups of root groups
        for (Group rootGroup : rootGroups) {
            findSubGroupsRecursive(rootGroup.getSubGroup(), allSubGroups, visited);
        }

        // Step 4: Convert the result to a list and return
        return new ArrayList<>(allSubGroups);
    }

/**
 * Recursively fetches subgroups and adds them to the result set.
 * 
 * @param subGroupIds A list of subgroup IDs to process.
 * @param result      A Set to store unique subgroups (avoids duplicates).
 * @param visited     A Set to track visited group IDs (prevents infinite loops in cyclic relationships).
 */
    private void findSubGroupsRecursive(List<String> subGroupIds, Set<Group> result, Set<String> visited) {
        if (subGroupIds == null || subGroupIds.isEmpty()) {
            return; // Base case: No subgroups to process
        }
    
        for (String subGroupId : subGroupIds) {
            if (visited.contains(subGroupId)) {
                continue; // Skip already visited groups
            }
            visited.add(subGroupId); // Mark group ID as visited
    
            // Fetch the subgroup by ID
            Group subGroup = groupRepository.findById(subGroupId).orElse(null);
            if (subGroup != null) {
                result.add(subGroup); // Add subgroup to the result
                findSubGroupsRecursive(subGroup.getSubGroup(), result, visited); // Recursive call for its subgroups
            }
        }
    }
    /**
     * Retrieve a group document by its ObjectId.
     *
     * This method fetches a group document from the repository based on its unique ObjectId.
     *
     * Edge cases:
     * - Throws an exception if the group is not found.
     * - Throws an exception if the ObjectId format is invalid.
     *
     * @param objectId The string representation of the ObjectId.
     * @return The Group object.
     */
    public Group getGroupById(String objectId) {
        ObjectId _id;
        try {
            _id = new ObjectId(objectId); // Convert String to ObjectId
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid ObjectId format: " + objectId);
        }
        return groupRepository.findById(objectId)
            .orElseThrow(() -> new RuntimeException("Group not found with Id: " + objectId));
    }

}
