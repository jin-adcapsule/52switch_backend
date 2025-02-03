package com.adcapsule.server52switch.core.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.adcapsule.server52switch.core.models.Location;
@Repository
public interface LocationRepository extends MongoRepository<Location, String> {
    Optional<Location> findByWorkplace(String workplace);
    boolean existsByWorkplace(String workplace);
   // Custom query to select specific fields for all documents
    @Query(value = "{}",fields = "{'workplace': 1, '_id': 1}")
    List<Location> findAllIndexes();
}
