package com.adcapsule.server52switch.core.services;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adcapsule.server52switch.core.models.Location;
import com.adcapsule.server52switch.core.repositories.LocationRepository;

@Service
public class LocationService {

    private final LocationRepository locationRepository;

    @Autowired
    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }
    /**
     * Retrieve location information for a specified workplace.
     *
     * This method fetches the location details associated with the given workplace.
     * If no location is found, an empty Optional is returned.
     *
     * Edge cases:
     * - If the workplace does not exist in the repository, the returned Optional will be empty.
     * - Assumes workplace names are unique identifiers in the repository.
     *
     * @param workplace the name of the workplace to retrieve location information for.
     * @return an Optional containing the Location if found, or an empty Optional otherwise.
     */

    /**
     * Retrieve the working hours for a specified workplace.
     *
     * This method retrieves the "workhourOn" property of the location associated with the
     * specified workplace. If the workplace does not exist in the repository, null is returned.
     *
     * Edge cases:
     * - If the workplace does not exist, this method will return null.
     * - Assumes each workplace has at most one associated "workhourOn" value.
     *
     * @param workplace the name of the workplace to retrieve working hours for.
     * @return the "workhourOn" value if the workplace exists, or null otherwise.
     */
    public String getWorkhourOn(String workplace) {
        Optional<Location> location = locationRepository.findByWorkplace(workplace);
        return location.map(Location::getWorkhourOn).orElse(null); // Return workhourOn or null if not found
    }
        /**
     * Retrieve a location document by its ObjectId.
     *
     * This method fetches a location document from the repository based on its unique ObjectId.
     *
     * Edge cases:
     * - Throws an exception if the location is not found.
     * - Throws an exception if the ObjectId format is invalid.
     *
     * @param objectId The string representation of the ObjectId.
     * @return The location object.
     */
    public Location getLocationById(String objectId) {
        ObjectId _id;
        try {
            _id = new ObjectId(objectId); // Convert String to ObjectId
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid ObjectId format: " + objectId);
        }
        return locationRepository.findById(objectId)
            .orElseThrow(() -> new RuntimeException("Group not found with Id: " + objectId));
    }
}
