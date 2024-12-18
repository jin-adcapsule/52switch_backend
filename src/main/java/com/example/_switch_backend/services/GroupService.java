package com.example._switch_backend.services;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example._switch_backend.models.Group;
import com.example._switch_backend.repositories.GroupRepository;
import com.example._switch_backend.repositories.projection.Projection.IdProjection;

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
