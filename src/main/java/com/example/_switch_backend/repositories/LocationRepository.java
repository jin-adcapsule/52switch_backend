package com.example._switch_backend.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example._switch_backend.models.Location;

public interface LocationRepository extends MongoRepository<Location, String> {
    Optional<Location> findByWorkplace(String workplace);

}
