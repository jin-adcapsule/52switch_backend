package com.adcapsule.server52switch.core.repositories;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.adcapsule.server52switch.core.dtos.AttendanceStatusDTO;
import com.adcapsule.server52switch.core.models.Attendance;
@Repository
public interface AttendanceRepository extends MongoRepository<Attendance, String> {
    List<Attendance> findByEmployeeId(int  employeeId);
    Optional<Attendance> findByEmployeeIdAndDate(int employeeId, String formattedDate);
    List<Attendance> findByEmployeeIdAndDateBetween(int employeeId, String startDate, String endDate);
    @Query(value="{ 'employeeOid': ?0, 'date': ?1 }", fields = "{'status': 1, '_id': 0 }")
    AttendanceStatusDTO findStatusByobjectOidAndDate(String objectId, String dateToday);
    //Keywords like BetweenIncdlusive or Inclusive are not recognized by mongo
    @Query("{ 'employeeId': ?0, 'date': { '$gte': ?1, '$lte': ?2 } }")
    List<Attendance> findByEmployeeIdAndDateBetweenInclusive(int employeeId, String startDate, String endDate);
    @Query("{'employeeId': ?0,'$and': [{ 'date': { '$gte': ?2, '$lte': ?3 } },{ '$or': [{ 'workTypeList': { '$in': ?1 } },{ 'checkInStatus': { '$in': ?1 } },{ 'checkOutStatus': { '$in': ?1 } }]}]}")
    List<Attendance> findByEmployeeIdInAndWorkTypeAndDateBetweenInclusive(
        int  employeeId, 
        List<String> workTypeList,
        String startDate, 
        String endDate
    );
}
