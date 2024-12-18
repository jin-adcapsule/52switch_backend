package com.example._switch_backend.services;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example._switch_backend.config.Config;
import com.example._switch_backend.models.Dayoff;
import com.example._switch_backend.models.DayoffHistory;
import com.example._switch_backend.repositories.DayoffRepository;
import com.example._switch_backend.repositories.projection.Projection.DayoffTypeProjection;

@Service
public class DayoffService {
    private final DayoffRepository dayoffRepository;

    private final EmployeeService employeeService; 
   // private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");//just to compare date inforation
    
   @Autowired
   public DayoffService(DayoffRepository dayoffRepository, EmployeeService employeeService) {
       this.dayoffRepository = dayoffRepository;
       this.employeeService = employeeService;
   }
    public Optional<Dayoff> findByIdAndDayoffDate(String objectId, String dayoffdate){
        int employeeId = employeeService.getEmployeeIdById(objectId);
        return dayoffRepository.findByEmployeeIdAndDayoffDate(employeeId, dayoffdate);
    }
    public Dayoff createOrUpdateDayoff(String objectId, String requestKey, String dayoffdate,String dayoffType, String requestComment,int beforeDateRemaining, Date currentdate) throws ParseException {

        // Format the current date as 'yyyy-MM-dd'
        //dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        //String formattedDate = dateFormat.format(new Date());
        
        // Parse workhourOn into a Date object for comparison
        //Date currentDate =  currentdate;String timestamp = Config.gettimestamp_yyyymmddHHmmss_String(currentDate);

        String currentDateInKST = Config.getCurrentDate_String();
    // Resolve employeeId from objectId
        int employeeId = employeeService.getEmployeeIdById(objectId);
        // Check if a record already exists for this employeeId and dayoffdate
        Dayoff dayoff =null;
        
        // Create a new Dayoff record
        
        dayoff = new Dayoff();
        
        dayoff.setEmployeeId(employeeId);
        dayoff.setRequestDate(currentDateInKST); // Set apply date to current date
        dayoff.setDayoffType(dayoffType);
        dayoff.setDayoffDate(dayoffdate);
        dayoff.setRequestKey(requestKey);
        dayoff.setRequestComment(requestComment);
        dayoff.setRequestStatus("대기중");
        dayoff.setSupervisorId(employeeService.getSupervisorEidbyEmployeeId(employeeId));
        dayoff.setBeforeDateRemaining(beforeDateRemaining);
        // Save and return the Dayoff record
        
        return dayoffRepository.save(dayoff);
    }




// Fetch employee attendance between two dates
    public List<DayoffHistory> getEmployeeDayoff(
        String _id, 
        String startDate, 
        String endDate, 
        List<String> requestStatusList
        ) {
        try {

            int employeeId = employeeService.getEmployeeIdById(_id);

            // Fetch dayoff records based on filters
            List<Integer> employeeIdList = Arrays.asList(employeeId);
            
            List<Dayoff> dayoffs = dayoffRepository.findByEmployeeIdInAndRequestStatusAndRequestDateBetweenInclusive(employeeIdList,requestStatusList, startDate, endDate);
            //Group Dayoff Requests
            Map<String, List<Dayoff>> dayoffGrouped = dayoffs.stream()
            .collect(Collectors.groupingBy(Dayoff::getRequestKey));

            // Map each grouped sublist to a single DTO
            return dayoffGrouped.entrySet().stream()
            .map(entry -> {
                String requestKey = entry.getKey();
                List<Dayoff> groupedDayoffs = entry.getValue();
                // Collect all dayoffDates into a list
                List<String> dayoffDates = groupedDayoffs.stream()
                    .map(Dayoff::getDayoffDate)
                    .sorted() // Sort in ascending order
                    .collect(Collectors.toList());
                // Combine data from the grouped sublist to create a single DTO
                return new DayoffHistory(
                    groupedDayoffs.get(0).getEmployeeId(),   // Assuming all in group have same employeeId
                    groupedDayoffs.get(0).getRequestDate(), // Take requestDate from the first element
                    dayoffDates,
                    groupedDayoffs.get(0).getDayoffType(),  // Assuming all in group have the same dayoffType
                    requestKey,                             // Use the requestKey for the group
                    groupedDayoffs.get(0).getRequestStatus(), // Assuming all in group have the same requestStatus
                    groupedDayoffs.get(0).getSupervisorId(), // Assuming same supervisorId for all
                    groupedDayoffs.get(0).getRequestComment()
                );
            })
            .sorted(Comparator.comparing(dto -> dto.getDayoffDates().get(0))) // Sort by the earliest date
            .collect(Collectors.toList()); // Collect all DTOs into a List
            } catch (Exception e) {
            throw new RuntimeException("Invalid date format. Use 'yyyy-MM-dd' for startDate and endDate.");
        }
    }
    public List<Dayoff> findByEmployeeIdAndRequestStatusAndDate(int employeeId, String requestStatus, String Date){
        return dayoffRepository
                .findByEmployeeIdAndRequestStatusAndDate(employeeId,requestStatus, Date);
    }
    public List<String> findDayoffTypeByEmployeeIdAndRequestStatusAndDate(int employeeId, String requestStatus, String Date){
        return dayoffRepository
                .findDayoffTypeByEmployeeIdAndRequestStatusAndDate(employeeId,requestStatus, Date)
                .stream()
                .map(DayoffTypeProjection::getDayoffType) // Access the `dayoffType` field
                .collect(Collectors.toList());
    }
}

