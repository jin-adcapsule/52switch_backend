package com.adcapsule.server52switch.core.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.adcapsule.server52switch.core.models.Location;

public interface LocationRepository extends MongoRepository<Location, String> {
    Optional<Location> findByWorkplace(String workplace);

}
