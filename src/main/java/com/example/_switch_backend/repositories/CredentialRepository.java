package com.example._switch_backend.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.example._switch_backend.models.Credential;

public interface CredentialRepository extends MongoRepository<Credential, String> {
    
// Check if an employee is a supervisor of any group
@Query(value = "{ 'employeeOid': ?0 }")
Credential findbyEmployeeOid(String employeeOid);

}