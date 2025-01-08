package com.adcapsule.server52switch.core.repositories;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.adcapsule.server52switch.core.models.Dayoff;
import com.adcapsule.server52switch.core.repositories.projection.Projection.DayoffTypeAndDateProjection;
import com.adcapsule.server52switch.core.repositories.projection.Projection.DayoffTypeProjection;
@Repository
public interface DayoffRepository extends MongoRepository<Dayoff, String> {
    List<Dayoff> findByEmployeeOid(String  employeeOid);
    Optional<Dayoff> findByEmployeeOidAndDayoffDate(String employeeOid, String dayoffDate);
    @Query(value = "{ '_id': ?0,'dayoffDate': ?1 }")
    Optional<Dayoff> findByIdAndDayoffDate(String objectId, String dayoffDate);
    List<Dayoff> findByRequestKey(String  requestKey);
    //Keywords like BetweenInclusive or Inclusive are not recognized by mongo
    @Query("{ 'employeeOid': ?0, 'requestDate': { '$gte': ?1, '$lte': ?2 } }")
    List<Dayoff> findByEmployeeOidAndRequestDateBetweenInclusive(
        String employeeOid, 
        String startDate, 
        String endDate
    );
    List<Dayoff> findBySupervisorOidAndRequestStatus(String supervisorOid, String requestStatus);
    @Query("{ 'employeeOid': { $in: ?0 },'requestStatus':{ $in: ?1 }, 'requestDate': { '$gte': ?2, '$lte': ?3} }")
    List<Dayoff> findByEmployeeOidInAndRequestStatusAndRequestDateBetweenInclusive(
        List<String> employeeOidList, 
        List<String> requestStatusList,
        String startDate, 
        String endDate
    );

    @Query("{ 'employeeOid': ?0,'requestStatus':?1 , 'dayoffDate': ?2 }")
    List<Dayoff> findByEmployeeOidAndRequestStatusAndDate(
        String employeeOid, 
        String requestStatus,
        String Date
    );
    @Query(value="{ 'employeeOid': ?0,'requestStatus':?1 , 'dayoffDate': ?2 }", fields="{ 'dayoffType' : 1}")
    List<DayoffTypeProjection> findDayoffTypeByEmployeeOidAndRequestStatusAndDate(
        String employeeOid, 
        String requestStatus,
        String Date
    );
    @Query(value="{ 'employeeOid': ?0, 'dayoffType': { $in: ?1 },'requestStatus':?2 , 'dayoffDate': ?3 }", fields="{ 'dayoffType' : 1}")
    List<DayoffTypeProjection> findDayoffTypeByEmployeeOidAndworkTypeInAndRequestStatusAndDate(
        String employeeOid, 
        List<String> dayoffTypeQueryList,
        String requestStatus,
        String Date
    );
    
    @Query(value="{ 'employeeOid': ?0,'requestStatus':?1 ,'dayoffDate': { '$gte': ?2, '$lte': ?3} }\" }", fields="{ 'dayoffDate' : 1,'dayoffType' : 1}")
    List<DayoffTypeAndDateProjection> findDayoffTypeAndDateByEmployeeOidAndRequestStatusAndDateBetweenInclusive(
        String employeeOid, 
        String requestStatus,
        String startDate,
        String endDate
    );
}
