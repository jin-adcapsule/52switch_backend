package com.adcapsule.server52switch.core.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.adcapsule.server52switch.core.models.Credential;
import org.springframework.stereotype.Repository;
@Repository
public interface CredentialRepository extends MongoRepository<Credential, String> {
    
// Check if an employee is a supervisor of any group
@Query(value = "{ 'employeeOid': ?0 }")
Credential findbyEmployeeOid(String employeeOid);

}