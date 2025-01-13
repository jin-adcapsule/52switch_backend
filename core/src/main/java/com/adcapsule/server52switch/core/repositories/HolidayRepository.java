package com.adcapsule.server52switch.core.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.adcapsule.server52switch.core.models.Holiday;

public interface HolidayRepository extends MongoRepository<Holiday, String> {

    @Query("{ 'holidayDate': { $gte: ?0, $lte: ?1 } }")
    List<Holiday> findHolidaysByDateRange(String startDate, String endDate);
    Optional<Holiday> findByHolidayDate(String holidayDate);
    @Query("{ 'holidayDate': { '$gte': ?0 } }")
    List<Holiday> findHolidaysAfterOrOn(String date);

}
