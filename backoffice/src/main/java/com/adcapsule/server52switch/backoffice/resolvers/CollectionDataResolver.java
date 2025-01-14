package com.adcapsule.server52switch.backoffice.resolvers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBObject;

@Component
public class CollectionDataResolver  {

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Map<String, Object>> getCollectionData(String collection) {
        try {
            // Use BasicDBObject or Document to fetch data from MongoDB
            List<BasicDBObject> rawData = mongoTemplate.findAll(BasicDBObject.class, collection);

            // Convert BasicDBObject to Map<String, Object>
            List<Map<String, Object>> data = rawData.stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());

            return data;
        } catch (Exception e) {
            throw new RuntimeException("Error fetching data from collection: " + collection, e);
        }
    }
    @SuppressWarnings("unchecked")
    private Map<String, Object> convertToMap(BasicDBObject dbObject) {
        // Safely cast using @SuppressWarnings only here
        return (Map<String, Object>) dbObject.toMap();
    }
}

