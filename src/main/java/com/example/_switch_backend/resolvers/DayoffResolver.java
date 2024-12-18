package com.example._switch_backend.resolvers;
import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.example._switch_backend.config.Config;
import com.example._switch_backend.models.Dayoff;
import com.example._switch_backend.models.DayoffHistory;
import com.example._switch_backend.services.DayoffService;


@Controller
public class DayoffResolver {

    private final DayoffService dayoffService;// Use the service instance

    @Autowired
    public DayoffResolver( DayoffService dayoffService){
        this.dayoffService = dayoffService;
        //this.subscriptionPublisher = subscriptionPublisher;
    }
    // Query to fetch employee dayoff
    @QueryMapping
    public List<DayoffHistory> getEmployeeDayoff(
        @Argument String objectId,
        @Argument String startDate,
        @Argument String endDate,
        @Argument List<String> requestStatusList
    ) {

        // Delegate the logic to the service
        return dayoffService.getEmployeeDayoff(objectId, startDate, endDate, requestStatusList);
    }
    @MutationMapping
    public List<String> requestDayoff(
        @Argument String objectId,
        @Argument List<String> dateList,
        @Argument String dayoffType,
        @Argument String requestComment,
        @Argument int beforeDateRemaining
    ) {
        List<String> responseMessages = new ArrayList<>();
        // Capture the current server time as an Instant
        Instant serverReceivedTime = Instant.now();
        // Convert Instant to Date for compatibility with existing methods
        Date serverReceivedDate = Date.from(serverReceivedTime);
        // Parse the dateString inline
        if (dateList == null || dateList.isEmpty()) {
            throw new IllegalArgumentException("dateString cannot be null or empty.");
        }
        for (String date : dateList) {
            Optional<Dayoff> existingDayoff = dayoffService.findByIdAndDayoffDate(objectId, date);
            if(existingDayoff.isPresent()){
                responseMessages.add("Failed: already applied on date: " + date);
            }
        }
        if(responseMessages.isEmpty()){ // empty meanse no request on duplicated dates existing
            List<Dayoff> dayoffList = new ArrayList<>();
            String requestKey = Config.generateUniqueKey("Default");//generate uniquekey per request
            for (String date : dateList) {
                try {

                    // Call the service method for each date
                    Dayoff dayoff = dayoffService.createOrUpdateDayoff(objectId, requestKey,date, dayoffType, requestComment,beforeDateRemaining,serverReceivedDate);
    
                    // Add the created/updated Dayoff to the list
                    dayoffList.add(dayoff);
                } catch (ParseException e) {
                    throw new RuntimeException("Invalid date format for dateList. Ensure dates are in 'yyyy-MM-dd' format.", e);
                    }
                }
            responseMessages.add("Success");
        }
        return responseMessages;
     }   
    
}