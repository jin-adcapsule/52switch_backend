package com.adcapsule.server52switch.core.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        return locationRepository.findById(objectId)
            .orElseThrow(() -> new RuntimeException("Group not found with Id: " + objectId));
    }
    public List<Location> findAll(){
        return locationRepository.findAll();
    }

    public List<Map<String, String>> findAllIndexes() {
        List<Location> locations = locationRepository.findAllIndexes();
        List<Map<String, String>> result = new ArrayList<>();
        for (Location location : locations) {
            Map<String, String> locationMap = new HashMap<>();
            locationMap.put("collection", "Location");
            locationMap.put("indexKey", "locationId");
            locationMap.put("indexValue", location.getId()); // Use actual data from location
            locationMap.put("indexShowKey", "workplace");
            locationMap.put("indexShowValue", location.getWorkplace()); // Use actual data from location
            result.add(locationMap);
        }
        return result;
    }
    
}
