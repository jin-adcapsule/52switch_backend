package com.adcapsule.server52switch.core.resolvers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.adcapsule.server52switch.core.repositories.LocationRepository;

@Controller
public class LocationResolver {
    @Autowired
    private LocationRepository employeeRepository;


    
}
