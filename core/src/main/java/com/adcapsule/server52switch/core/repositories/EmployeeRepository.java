package com.adcapsule.server52switch.core.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.adcapsule.server52switch.core.models.Employee;
import com.adcapsule.server52switch.core.repositories.projection.Projection.DayoffInfoProjection;
import com.adcapsule.server52switch.core.repositories.projection.Projection.EmployeeIdProjection;
import com.adcapsule.server52switch.core.repositories.projection.Projection.GroupIdProjection;
import com.adcapsule.server52switch.core.repositories.projection.Projection.IdProjection;
import com.adcapsule.server52switch.core.repositories.projection.Projection.LocationIdProjection;
import com.adcapsule.server52switch.core.repositories.projection.Projection.NameProjection;

public interface EmployeeRepository extends MongoRepository<Employee, String> {
    Optional<Employee> findByEmployeeId(int employeeId);
    //Optional<Employee> findById(String _id);
    Optional<Employee> findByPhone(String phone);
        // Finds only the employeeId by _id
    @Query(value = "{ '_id': ?0 }", fields = "{ 'employeeId': 1, '_id': 0 }")
    Optional<Employee> findEmployeeIdById(String objectId);
    @Query(value = "{ 'employeeId': ?0 }", fields = "{'groupId': 1, '_id': 0 }")
    GroupIdProjection findGroupIdByEmployeeId(int employeeId);
    @Query(value = "{ '_id': ?0 }", fields = "{'groupId': 1, '_id': 0 }")
    GroupIdProjection findGroupIdById(String employeeOid);
    @Query(value = "{ '_id': ?0 }", fields = "{'groupId': 1,'dayoffRemaining':1, '_id': 0 }")
    Optional<DayoffInfoProjection> findDayoffInfoById(String employeeOid);
    @Query(value = "{ '_id': ?0 }", fields = "{'locationId': 1, '_id': 0 }")
    LocationIdProjection findLocationIdById(String employeeOid);
    @Query(value = "{ 'groupId': ?0 }", fields = "{ 'employeeId': 1, '_id': 0 }")
    List<EmployeeIdProjection> findEmployeeIdListbyGroupId(String groupId);
    @Query(value = "{ '_id': ?0 }", fields = "{'name': 1, '_id': 0}")
    Optional<NameProjection> findNameById(String employeeOid);
    @Query(value = "{ 'groupId': ?0 }", fields = "{'_id': 1 }")
    List<IdProjection> findEmployeeOidListbyGroupId(String groupId);
}
