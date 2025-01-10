package com.adcapsule.server52switch.core.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.adcapsule.server52switch.core.models.Holiday;

public interface HolidayRepository extends MongoRepository<Holiday, String> {
    Holiday findByYear(int year);
}
