package com.example._switch_backend.resolvers;

import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;

import com.example._switch_backend.config.Config;
import com.example._switch_backend.models.Attendance;
import com.example._switch_backend.models.AttendanceHistory;
import com.example._switch_backend.models.AttendanceStatus;
import com.example._switch_backend.services.AttendanceService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Controller
public class AttendanceResolver {

    private final AttendanceService attendanceService;// Use the service instance
    private final Sinks.Many<AttendanceStatus> sink = Sinks.many().replay().all(); // Replay all emitted values..multicast().onBackpressureBuffer();
    //private final Map<String, String> activeSubscriptions = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> activeSubscriptions = new ConcurrentHashMap<>();

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
    public Attendance markAttendance(@Argument String objectId, @Argument boolean status) {
        if (objectId == null || objectId.isEmpty()) {
            throw new IllegalArgumentException("objectId cannot be null or empty");
        }
        try {
             // Capture the current server time as an Instant
            Instant serverReceivedTime = Instant.now();
            // Convert Instant to Date for compatibility with existing methods
            Date serverReceivedDate = Date.from(serverReceivedTime);
            // Call the service method and return the result
            Attendance attendance = attendanceService.createOrUpdateAttendance(objectId, serverReceivedDate, status);
            // Trigger the subscription
            AttendanceStatus updatedStatus = new AttendanceStatus(objectId, Config.getCurrentDate_String(), status);

            sink.tryEmitNext(updatedStatus); // Broadcast update to subscribers
            System.out.println("emit try: " + sink.tryEmitNext(updatedStatus));
            return attendance;
        } catch (ParseException e) {
            throw new RuntimeException("Invalid date format for checkTime. Use 'yyyy-MM-dd'.");
        }
    }

   
    @SubscriptionMapping
    public Flux<AttendanceStatus> toggleStatus(@Argument String objectId) {
        System.out.println("toggleStatus subscription started for objectId: " + objectId);

        if (objectId == null || objectId.isEmpty()) {
            throw new IllegalArgumentException("objectId is required for subscription");
        }

        String sessionId = UUID.randomUUID().toString(); // Unique session ID for each connection
        String today = Config.getCurrentDate_String();

        // Flux for default value and updates
    Flux<AttendanceStatus> updates = sink.asFlux()
    .startWith(new AttendanceStatus(objectId, today, false)) // Emit default status
    .filter(status -> status.getEmployeeOid().equals(objectId) && status.getDate().equals(today)); // Filter by objectId and date

    // Flux for heartbeats
    Flux<AttendanceStatus> heartbeats = Flux.interval(Duration.ofSeconds(30)) // Emit every 30 seconds
        .map(i -> new AttendanceStatus(objectId, today, false)); // Map interval to AttendanceStatus

    // Merge updates and heartbeats
    return Flux.merge(updates)//, heartbeats)
        .doOnSubscribe(sub -> {
            activeSubscriptions.computeIfAbsent(objectId, key -> ConcurrentHashMap.newKeySet()).add(sessionId);
            System.out.println("Subscription added: objectId=" + objectId + ", sessionId=" + sessionId);
        })
        .doOnCancel(() -> {
            Set<String> sessions = activeSubscriptions.get(objectId);
            if (sessions != null) {
                sessions.remove(sessionId);
                if (sessions.isEmpty()) {
                    activeSubscriptions.remove(objectId);
                }
            }
            System.out.println("Subscription canceled: objectId=" + objectId + ", sessionId=" + sessionId);
        })
        .doFinally(signal -> System.out.println("Subscription ended with signal: " + signal));
    }
}
