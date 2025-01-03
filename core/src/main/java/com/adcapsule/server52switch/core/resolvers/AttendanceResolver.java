package com.adcapsule.server52switch.core.resolvers;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.adcapsule.server52switch.core.configs.Config;
import com.adcapsule.server52switch.core.dtos.AttendanceHistory;
import com.adcapsule.server52switch.core.dtos.AttendanceStatusDTO;
import com.adcapsule.server52switch.core.services.AttendanceService;

@Controller
public class AttendanceResolver {

    private final AttendanceService attendanceService;// Use the service instance
    //private final Sinks.Many<AttendanceStatus> sink = Sinks.many().replay().all(); // Replay all emitted values..multicast().onBackpressureBuffer();
    //private final Map<String, String> activeSubscriptions = new ConcurrentHashMap<>();
    //private final Map<String, Set<String>> activeSubscriptions = new ConcurrentHashMap<>();

    @Autowired
    public AttendanceResolver( AttendanceService attendanceService){//},AttendanceSubscriptionPublisher subscriptionPublisher) {
        this.attendanceService = attendanceService;
        //this.subscriptionPublisher = subscriptionPublisher;
    }

    // Query to fetch employee attendance
    @QueryMapping
    public List<AttendanceHistory> getEmployeeAttendance(
        @Argument String objectId,
        @Argument String startDate,
        @Argument String endDate,
        @Argument List<String> workTypeList
    ) {
        // Map workTypeList items from text to value
        List<String> workTypeList_value= workTypeList.stream()
            .map(Config.workTypeTextToValueMap::get) // Get the corresponding text from the map
            .filter(Objects::nonNull)    // Exclude null values in case of unmatched keys
            .collect(Collectors.toList());
        System.out.println("valueWorkType:");
        System.out.println(workTypeList_value);
        // Delegate the logic to the service
        return attendanceService.getEmployeeAttendance(objectId, startDate, endDate, workTypeList_value);
    }
    /* 
    @PostConstruct
    public void emitTestData() {
        // Emit test data for verification
        System.out.println("emitTestData is excuted");
        sink.tryEmitNext(new AttendanceStatus("6731bc0cbc054ba2e52b53e1", "2024-12-04", true));
    }
        */
    @MutationMapping
    public AttendanceStatusDTO markAttendance(@Argument String objectId, @Argument boolean status) {
        if (objectId == null || objectId.isEmpty()) {
            throw new IllegalArgumentException("objectId cannot be null or empty");
        }
        try {
             // Capture the current server time as an Instant
            Instant serverReceivedTime = Instant.now();
            // Convert Instant to Date for compatibility with existing methods
            Date serverReceivedDate = Date.from(serverReceivedTime);
            // Call the service method and return the result
            AttendanceStatusDTO attendanceStatusDTO = attendanceService.createOrUpdateAttendance(objectId, serverReceivedDate, status);
            return attendanceStatusDTO;
        } catch (ParseException e) {
            throw new RuntimeException("error while markAttendance resolver");
        }
    }

   
    @QueryMapping
    public AttendanceStatusDTO getAttendanceStatus(@Argument String objectId){
        if (objectId == null || objectId.isEmpty()) {
            throw new IllegalArgumentException("objectId cannot be null or empty");
        }
        AttendanceStatusDTO attendanceStatusDTO = attendanceService.getAttendanceStatus(objectId);
        return attendanceStatusDTO;
    }
}
