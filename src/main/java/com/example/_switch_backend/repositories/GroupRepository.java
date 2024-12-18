package com.example._switch_backend.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.example._switch_backend.models.Group;
import com.example._switch_backend.repositories.projection.Projection.IdProjection;

public interface GroupRepository extends MongoRepository<Group, String> {
    
    // Check if an employee is a supervisor of any group
    boolean existsByGroupSupervisorEid(int employeeId);
    // Custom query to find group by supervisorid
    @Query(value = "{ 'groupSupervisorEid': ?0 }", fields = "{ '_id': 1 }")
    List<IdProjection> findGroupIdListBySupervisorId(int supervisorId);
    // Custom query to find group by supervisorid
    //@Query(value = "{ 'groupSupervisorEid': ?0 }", fields = "{ 'groupMembers': 1, '_id': 0 }")
    //List<Document> _findGroupMembersBySupervisorId(int supervisorId);
}