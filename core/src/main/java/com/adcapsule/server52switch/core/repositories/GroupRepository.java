package com.adcapsule.server52switch.core.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.adcapsule.server52switch.core.models.Group;
import com.adcapsule.server52switch.core.repositories.projection.Projection.IdProjection;
import org.springframework.stereotype.Repository;
@Repository
public interface GroupRepository extends MongoRepository<Group, String> {
    List<Group> findByGroupSupervisorOid(String supervisorOid);
    // Check if an employee is a supervisor of any group
    boolean existsByGroupSupervisorEid(int employeeId);
    // Custom query to find group by supervisorid
    @Query(value = "{ 'groupSupervisorEid': ?0 }", fields = "{ '_id': 1 }")
    List<IdProjection> findGroupIdListBySupervisorId(int supervisorId);
    @Query(value = "{ 'groupSupervisorOid': ?0 }", fields = "{ '_id': 1 }")
    List<IdProjection> findGroupIdListBySupervisorOid(String supervisorOid);
    // Custom query to find group by supervisorid
    //@Query(value = "{ 'groupSupervisorEid': ?0 }", fields = "{ 'groupMembers': 1, '_id': 0 }")
    //List<Document> _findGroupMembersBySupervisorId(int supervisorId);
}