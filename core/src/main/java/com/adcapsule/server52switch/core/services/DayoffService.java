package com.adcapsule.server52switch.core.services;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adcapsule.server52switch.core.configs.DateUtils;
import com.adcapsule.server52switch.core.dtos.DayoffHistory;
import com.adcapsule.server52switch.core.dtos.DayoffInfoDTO;
import com.adcapsule.server52switch.core.models.Dayoff;
import com.adcapsule.server52switch.core.models.Group;
import com.adcapsule.server52switch.core.models.Holiday;
import com.adcapsule.server52switch.core.repositories.DayoffRepository;
import com.adcapsule.server52switch.core.repositories.projection.Projection.DayoffInfoProjection;
import com.adcapsule.server52switch.core.repositories.projection.Projection.DayoffTypeAndDateProjection;
import com.adcapsule.server52switch.core.repositories.projection.Projection.DayoffTypeProjection;
import com.adcapsule.server52switch.core.repositories.projection.Projection.NameProjection;
@Service
public class DayoffService {
    private final DayoffRepository dayoffRepository;

    private final EmployeeService employeeService; 
    private final HolidayService holidayService;
   // private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");//just to compare date inforation
    
   @Autowired
   public DayoffService(DayoffRepository dayoffRepository, EmployeeService employeeService, HolidayService holidayService) {
       this.dayoffRepository = dayoffRepository;
       this.employeeService = employeeService;
       this.holidayService = holidayService;
   }
   public List<Dayoff> findAll(){
        return dayoffRepository.findAll();
   }
    public Optional<Dayoff> findByIdAndDayoffDate(String objectId, String dayoffdate){
        String employeeOid = objectId;
        //int employeeId = employeeService.getEmployeeIdById(objectId);
        return dayoffRepository.findByEmployeeOidAndDayoffDate(employeeOid, dayoffdate);
    }
    public Dayoff createOrUpdateDayoff(String objectId, String requestKey, String dayoffdate,String dayoffType, String requestComment,int beforeDateRemaining, Date currentdate) throws ParseException {
        String employeeOid = objectId;
        String currentDateInKST = DateUtils.getyyyymmddStringNow();
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
        dayoff.setRequestStatus("pending");
        dayoff.setSupervisorOid(employeeService.getSupervisorOidbyEmployeeOid(objectId));
        dayoff.setBeforeDateRemaining(beforeDateRemaining);
        // Save and return the Dayoff record
        
        return dayoffRepository.save(dayoff);
    }

    public DayoffInfoDTO getDayoffInfoByEmployeeOid(String employeeOid) {

            Optional<DayoffInfoProjection> optionalDayoffInfoProjection = employeeService.findDayoffInfoById(employeeOid);//groupId and dayoffPerYear
            String groupId = optionalDayoffInfoProjection.map(DayoffInfoProjection::getGroupId).orElse(null);
            if(groupId == null){throw new RuntimeException("Group not found with Id");}
            Integer dayoffPerYear = optionalDayoffInfoProjection.map(DayoffInfoProjection::getDayoffPerYear).orElse(-1);//yearly available dayoff
            if(dayoffPerYear < 0){throw new RuntimeException("dayoffPerYear not found with Id");}
            
            String currentYearStart = LocalDate.now().withMonth(1).withDayOfMonth(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String currentYearEnd = LocalDate.now().withMonth(12).withDayOfMonth(31).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            Integer dayoffApproved =dayoffRepository.countByEmployeeOidAndRequestStatusAndDayoffDateBetween(employeeOid,"approved",currentYearStart,currentYearEnd);          
            Integer dayoffRemaining = dayoffPerYear-dayoffApproved;

            if(dayoffRemaining <0){throw new RuntimeException("DayoffRemaining Calc Error");}

            Group group = employeeService.getGroupById(groupId);
            String supervisorOid = group.getGroupSupervisorOid();
            Optional<NameProjection> optionalNameProjection = employeeService.findNameById(supervisorOid);
            String supervisorName = optionalNameProjection.map(NameProjection::getName).orElse(null);
            if(supervisorName == null){throw new RuntimeException("SupervisorName not found with Id");}

            String todayDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());// Format the current date as a string
            List<Holiday> holidayList=holidayService.findHolidaysAfterOrOn(todayDate);
            return new DayoffInfoDTO(supervisorName,supervisorOid,dayoffRemaining,holidayList);

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
    public List<String> findDayoffTypeByEmployeeOidAndWorkTypeInAndRequestStatusAndDate(String employeeOid, List<String> workTypeQueryList,String requestStatus, String Date){
        return dayoffRepository
                .findDayoffTypeByEmployeeOidAndworkTypeInAndRequestStatusAndDate(employeeOid,workTypeQueryList,requestStatus, Date)
                .stream()
                .map(DayoffTypeProjection::getDayoffType) // Access the `dayoffType` field
                .collect(Collectors.toList());
    }
    
    public List<Map<String, Object>> findDayoffTypeAndDateByEmployeeOidAndRequestStatusAndDateBetweenInclusive(String employeeOid, String requestStatus, String startDate, String endDate){
        // Fetch raw data from the repository
        List<DayoffTypeAndDateProjection> rawResults = dayoffRepository
                .findDayoffTypeAndDateByEmployeeOidAndRequestStatusAndDateBetweenInclusive(
                        employeeOid, requestStatus, startDate, endDate);

        // Group by dayoffDate and collect dayoffType as a list
        Map<String, List<String>> groupedResults = rawResults.stream()
                .collect(Collectors.groupingBy(
                        DayoffTypeAndDateProjection::getDayoffDate, // Group by dayoffDate
                        Collectors.mapping(DayoffTypeAndDateProjection::getDayoffType, Collectors.toList()) // Collect dayoffType as a list
                ));

        // Convert the grouped results into the required output structure
        List<Map<String, Object>> result = groupedResults.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("dayoffDate", entry.getKey());
                    map.put("dayoffType", entry.getValue());
                    return map;
                })
                .collect(Collectors.toList());

        return result;
    }
    public List<Dayoff> findByRequestStatusAndWorkTypeListAndDateBetweenInclusive(String employeeOid,String requestStatus, List<String> workTypeList,String startDate,String endDate){
        return dayoffRepository.findByRequestStatusAndWorkTypeListAndDateBetweenInclusive(employeeOid,requestStatus, workTypeList,startDate, endDate);
    }
    public List<Dayoff> findByRequestStatusAndDateBetweenInclusive(String employeeOid,String requestStatus,String startDate,String endDate){
        List<Dayoff> results = dayoffRepository.findByRequestStatusAndDateBetweenInclusive(
            employeeOid, requestStatus, startDate, endDate
        );

    
        return results;
        //return dayoffRepository.findByRequestStatusAndDateBetweenInclusive(employeeOid,requestStatus,startDate, endDate);
    }
    
}

