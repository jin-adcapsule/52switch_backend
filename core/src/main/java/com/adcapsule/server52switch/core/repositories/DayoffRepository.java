package com.adcapsule.server52switch.core.repositories;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.adcapsule.server52switch.core.models.Dayoff;
import com.adcapsule.server52switch.core.repositories.projection.Projection.DayoffTypeProjection;
public interface DayoffRepository extends MongoRepository<Dayoff, String> {
    List<Dayoff> findByEmployeeId(int  employeeId);
    Optional<Dayoff> findByEmployeeIdAndDayoffDate(int employeeId, String dayoffDate);
    @Query(value = "{ '_id': ?0,'dayoffDate': ?1 }")
    Optional<Dayoff> findByIdAndDayoffDate(String objectId, String dayoffDate);
    List<Dayoff> findByRequestKey(String  requestKey);
    //Keywords like BetweenInclusive or Inclusive are not recognized by mongo
    @Query("{ 'employeeId': ?0, 'requestDate': { '$gte': ?1, '$lte': ?2 } }")
    List<Dayoff> findByEmployeeIdAndRequestDateBetweenInclusive(
        int employeeId, 
        String startDate, 
        String endDate
    );
    List<Dayoff> findBySupervisorIdAndRequestStatus(int supervisorId, String requestStatus);
    @Query("{ 'employeeId': { $in: ?0 },'requestStatus':{ $in: ?1 }, 'requestDate': { '$gte': ?2, '$lte': ?3} }")
    List<Dayoff> findByEmployeeIdInAndRequestStatusAndRequestDateBetweenInclusive(
        List<Integer> employeeId, 
        List<String> requestStatusList,
        String startDate, 
        String endDate
    );
    @Query("{ 'employeeId': ?0,'requestStatus':?1 , 'dayoffDate': ?2 }")
    List<Dayoff> findByEmployeeIdAndRequestStatusAndDate(
        int employeeId, 
        String requestStatus,
        String Date
    );
    @Query(value="{ 'employeeId': ?0,'requestStatus':?1 , 'dayoffDate': ?2 }", fields="{ 'dayoffType' : 1}")
    List<DayoffTypeProjection> findDayoffTypeByEmployeeIdAndRequestStatusAndDate(
        int employeeId, 
        String requestStatus,
        String Date
    );
}
