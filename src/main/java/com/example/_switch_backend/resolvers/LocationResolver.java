package com.example._switch_backend.resolvers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.example._switch_backend.models.Location;
import com.example._switch_backend.repositories.LocationRepository;

@Controller
public class LocationResolver {
    @Autowired
    private LocationRepository employeeRepository;

    @QueryMapping
    public Location getLocationInfo(@Argument String workplace) {
        return employeeRepository.findByWorkplace(workplace)
                .orElseThrow(() -> new RuntimeException("workplace not found: " + workplace));
    }
    
}
