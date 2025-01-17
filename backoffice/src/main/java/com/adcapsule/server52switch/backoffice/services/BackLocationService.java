package com.adcapsule.server52switch.backoffice.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adacpsule.server52switch.shared.inputs.LocationInput;
import com.adcapsule.server52switch.backoffice.configs.BeanUtilsHelper;
import com.adcapsule.server52switch.backoffice.validators.LocationValidator;
import com.adcapsule.server52switch.core.models.Location;
import com.adcapsule.server52switch.core.repositories.LocationRepository;

@Service
public class BackLocationService {
    private final LocationRepository locationRepository;
    private final LocationValidator locationValidator;

    @Autowired
    public BackLocationService(LocationRepository locationRepository, LocationValidator locationValidator) {
        this.locationRepository = locationRepository;
        this.locationValidator = locationValidator;
    }

    // Basic CRUD
    public List<Location> findAll() {
        return locationRepository.findAll();
    }

    public String updateLocation(String id, LocationInput input) {
        try {
            // Find existing Location by Id
            Location existingLocation = locationRepository.findById(id).orElse(null);
            if (existingLocation == null) {
                return "No Location exists with Id ";
            }

            // Validate the input fields
            List<String> errors = locationValidator.validateExistingLocation(input);
            if (!errors.isEmpty()) {
                return String.join(", ", errors);
            }

            // Update only non-null fields
            BeanUtilsHelper.updateEntityFields(existingLocation, input);

            // Save the updated Location
            locationRepository.save(existingLocation);
            return "success";
        } catch (Exception e) {
            // Handle exceptions (e.g., database errors)
            return "Error updating Location: " + e.getMessage();
        }
    }
}
