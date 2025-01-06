package com.adcapsule.server52switch.core.services;
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

import com.adcapsule.server52switch.core.configs.Config;
import com.adcapsule.server52switch.core.dtos.DayoffHistory;
import com.adcapsule.server52switch.core.models.Dayoff;
import com.adcapsule.server52switch.core.repositories.DayoffRepository;
import com.adcapsule.server52switch.core.repositories.projection.Projection.DayoffTypeProjection;

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
        String employeeOid = objectId;
        //int employeeId = employeeService.getEmployeeIdById(objectId);
        return dayoffRepository.findByEmployeeOidAndDayoffDate(employeeOid, dayoffdate);
    }
    public Dayoff createOrUpdateDayoff(String objectId, String requestKey, String dayoffdate,String dayoffType, String requestComment,int beforeDateRemaining, Date currentdate) throws ParseException {
        String employeeOid = objectId;
        String currentDateInKST = Config.getCurrentDate_String();
        // Check if a record already exists for this employeeId and dayoffdate
        Dayoff dayoff;
        // Create a new Dayoff record
        dayoff = new Dayoff();
        
        dayoff.setEmployeeOid(employeeOid);
        dayoff.setRequestDate(currentDateInKST); // Set apply date to current date
        dayoff.setDayoffType(dayoffType);
        dayoff.setDayoffDate(dayoffdate);
        dayoff.setRequestKey(requestKey);
        dayoff.setRequestComment(requestComment);
        dayoff.setRequestStatus("대기중");
        dayoff.setSupervisorOid(employeeService.getSupervisorOidbyEmployeeOid(objectId));
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
            //int employeeId = employeeService.getEmployeeIdById(_id);
            // Fetch dayoff records based on filters
            //List<Integer> employeeIdList = Arrays.asList(employeeId);
            String employeeOid = _id;
            List<String> employeeOidList = Arrays.asList(employeeOid);
            List<Dayoff> dayoffs = dayoffRepository.findByEmployeeOidInAndRequestStatusAndRequestDateBetweenInclusive(employeeOidList,requestStatusList, startDate, endDate);
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
                    groupedDayoffs.get(0).getEmployeeOid(),   // Assuming all in group have same employeeId
                    groupedDayoffs.get(0).getRequestDate(), // Take requestDate from the first element
                    dayoffDates,
                    groupedDayoffs.get(0).getDayoffType(),  // Assuming all in group have the same dayoffType
                    requestKey,                             // Use the requestKey for the group
                    groupedDayoffs.get(0).getRequestStatus(), // Assuming all in group have the same requestStatus
                    groupedDayoffs.get(0).getSupervisorOid(), // Assuming same supervisorId for all
                    groupedDayoffs.get(0).getRequestComment()
                );
            })
            .sorted(Comparator.comparing(dto -> dto.getDayoffDates().get(0))) // Sort by the earliest date
            .collect(Collectors.toList()); // Collect all DTOs into a List
            } catch (Exception e) {
            throw new RuntimeException("Invalid date format. Use 'yyyy-MM-dd' for startDate and endDate.");
        }
    }
    public List<Dayoff> findByEmployeeOidAndRequestStatusAndDate(String employeeOid, String requestStatus, String Date){
        return dayoffRepository
                .findByEmployeeOidAndRequestStatusAndDate(employeeOid,requestStatus, Date);
    }
    public List<String> findDayoffTypeByEmployeeOidAndRequestStatusAndDate(String employeeOid, String requestStatus, String Date){
        return dayoffRepository
                .findDayoffTypeByEmployeeOidAndRequestStatusAndDate(employeeOid,requestStatus, Date)
                .stream()
                .map(DayoffTypeProjection::getDayoffType) // Access the `dayoffType` field
                .collect(Collectors.toList());
    }
}

