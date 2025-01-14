package com.adcapsule.server52switch.core.resolvers;
import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.adcapsule.server52switch.core.configs.Config;
import com.adcapsule.server52switch.core.dtos.DayoffHistory;
import com.adcapsule.server52switch.core.dtos.DayoffInfoDTO;
import com.adcapsule.server52switch.core.models.Dayoff;
import com.adcapsule.server52switch.core.services.DayoffService;


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
        @Argument String employeeOid,
        @Argument String startDate,
        @Argument String endDate,
        @Argument List<String> requestStatusList
    ) {
        // Convert the requestStatusList using the map
        List<String> mappedRequestStatusList = requestStatusList.stream()
            .map(status -> Config.requestStatusTextToValueMap.getOrDefault(status, status)) // Map the text to value or keep it as is
            .collect(Collectors.toList());

        // Delegate the logic to the service
        return dayoffService.getEmployeeDayoff(employeeOid, startDate, endDate, mappedRequestStatusList);
    }
    @QueryMapping
    public DayoffInfoDTO getDayoffInfo(@Argument String employeeOid) {
        return dayoffService.getDayoffInfoByEmployeeOid(employeeOid);
    }
    @MutationMapping
    public List<String> requestDayoff(
        @Argument String employeeOid,
        @Argument List<String> dateList,
        @Argument String dayoffType,
        @Argument String requestComment,
        @Argument int beforeDateRemaining
    ) {
        String dayoffTypeValue= Config.workTypeTextToValueMap.get(dayoffType);
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
            Optional<Dayoff> existingDayoff = dayoffService.findByIdAndDayoffDate(employeeOid, date);
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
                    Dayoff dayoff = dayoffService.createOrUpdateDayoff(employeeOid, requestKey,date, dayoffTypeValue, requestComment,beforeDateRemaining,serverReceivedDate);
    
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