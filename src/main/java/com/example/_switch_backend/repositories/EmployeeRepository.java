package com.example._switch_backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.example._switch_backend.models.Employee;
import com.example._switch_backend.repositories.projection.Projection.EmployeeIdProjection;
import com.example._switch_backend.repositories.projection.Projection.GroupIdProjection;

public interface EmployeeRepository extends MongoRepository<Employee, String> {
    Optional<Employee> findByEmployeeId(int employeeId);
    //Optional<Employee> findById(String _id);
    Optional<Employee> findByPhone(String phone);
        // Finds only the employeeId by _id
    @Query(value = "{ '_id': ?0 }", fields = "{ 'employeeId': 1, '_id': 0 }")
    Optional<Employee> findEmployeeIdById(String objectId);
    @Query(value = "{ 'employeeId': ?0 }", fields = "{'groupId': 1, '_id': 0 }")
    GroupIdProjection findGroupIdByEmployeeId(int employeeId);
    @Query(value = "{ 'groupId': ?0 }", fields = "{ 'employeeId': 1, '_id': 0 }")
    List<EmployeeIdProjection> findEmployeeIdListbyGroupId(String groupId);
}
