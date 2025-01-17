package com.adcapsule.server52switch.backoffice.validators;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adacpsule.server52switch.shared.inputs.LocationInput;
import com.adcapsule.server52switch.core.configs.DateUtils;
import com.adcapsule.server52switch.core.models.Location;
import com.adcapsule.server52switch.core.repositories.LocationRepository;

@Service
public class LocationValidator {

    @Autowired
    private LocationRepository locationRepository;

    /**
     * Validates a new Location object before saving it in the database.
     * Ensures that workplace is unique.
     */
    public List<String> validateNewLocation(Location location) {
        List<String> errors = new ArrayList<>();

        // Validate that workplace is unique
        validateUniqueWorkplace(location.getWorkplace(), errors);
        // Validate time fields
        validateWorkTimeFields(location.getWorkhourOn(), location.getWorkhourOff(), location.getWorkhourHalf(), errors);

        return errors;
    }

    /**
     * Validates an existing Location object, ensuring only provided fields are updated.
     */
    public List<String> validateExistingLocation(LocationInput location) {
        List<String> errors = new ArrayList<>();

        // If workplace is provided, validate that it is unique
        if (location.getWorkplace() != null) {
            validateUniqueWorkplace(location.getWorkplace(), errors);
        }
        // Validate time fields if provided
        if (location.getWorkhourOn() != null || location.getWorkhourOff() != null || location.getWorkhourHalf() != null) {
            validateWorkTimeFields(location.getWorkhourOn(), location.getWorkhourOff(), location.getWorkhourHalf(), errors);
        }
        return errors;
    }

    /**
     * Validates that the workplace is unique in the database.
     */
    private void validateUniqueWorkplace(String workplace, List<String> errors) {
        if (locationRepository.existsByWorkplace(workplace)) {
            errors.add("Workplace must be unique.");
        }
    }
    /**
     * Validates time fields for correctness (hh:mm format).
     */
    private void validateWorkTimeFields(String workhourOn, String workhourOff, String workhourHalf, List<String> errors) {
        if (!DateUtils.isValidTimeString(workhourOn)) {
            errors.add("Invalid workhourOn format. Please use hh:mm format.");
        }

        if (!DateUtils.isValidTimeString(workhourOff)) {
            errors.add("Invalid workhourOff format. Please use hh:mm format.");
        }

        if (!DateUtils.isValidTimeString(workhourHalf)) {
            errors.add("Invalid workhourHalf format. Please use hh:mm format.");
        }
    }

    
}
