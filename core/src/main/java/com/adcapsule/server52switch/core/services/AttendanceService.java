package com.adcapsule.server52switch.core.services;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adcapsule.server52switch.core.configs.Config;
import com.adcapsule.server52switch.core.configs.DateUtils;
import com.adcapsule.server52switch.core.dtos.AttendanceHistory;
import com.adcapsule.server52switch.core.dtos.AttendanceStatusAndDetailsDTO;
import com.adcapsule.server52switch.core.dtos.AttendanceStatusDTO;
import com.adcapsule.server52switch.core.dtos.LocationInfoDTO;
import com.adcapsule.server52switch.core.models.Attendance;
import com.adcapsule.server52switch.core.models.Dayoff;
import com.adcapsule.server52switch.core.models.Holiday;
import com.adcapsule.server52switch.core.repositories.AttendanceRepository;


@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeService employeeService; 
    private final RequestService requestService; 
    private final HolidayService holidayService;
   // private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");//just to compare date inforation
    
   @Autowired
   public AttendanceService(AttendanceRepository attendanceRepository, EmployeeService employeeService,RequestService requestService, HolidayService holidayService) {
       this.attendanceRepository = attendanceRepository;
       this.employeeService = employeeService;
       this.requestService = requestService;
       this.holidayService = holidayService;
   }
    public List<Attendance> findAll(){
        return attendanceRepository.findAll();          
    }
    public AttendanceStatusDTO getAttendanceStatus(String employeeOid) {
        try {
            String dateToday = Config.getCurrentDate_String();
            AttendanceStatusDTO AttendanceStatusDTO = attendanceRepository.findStatusByobjectOidAndDate(employeeOid, dateToday);
            if (AttendanceStatusDTO == null) {

                return new AttendanceStatusDTO(false); // or handle accordingly
            }
            return AttendanceStatusDTO;
        } catch (Exception e) {
            throw new RuntimeException("error while getAttendanceStatus service ");
        }
    }
    public AttendanceStatusAndDetailsDTO getAttendanceStatusAndDetails(String employeeOid) {
        try {
            boolean todayStatus= getAttendanceStatus(employeeOid).getStatus();
            boolean isTodayWeekend = DateUtils.isTodayWeekend();
            // Get approved day-off/workhour requests for today
            List<Map<String,String>> requestWorkhourKeyMapList= requestService.getRequestByTodayAndApprovedStatus(employeeOid);
            // Process each request and determine the earliest startTime and latest endTime
            Map<String,Object> expectedTimesAndWorkTypeListMap=getExpectedTimesAndWorkTypeList(employeeOid,requestWorkhourKeyMapList);
            //check whether today is fulldayoff day
            List<String> workTypeListResponse = (List<String>)expectedTimesAndWorkTypeListMap.get("workTypeListResponse");
            List<String> fulldayOffList=Config.fullDayoffList;
            boolean isTodayFullDayoff=workTypeListResponse.stream().anyMatch(fulldayOffList::contains);
            
            return new AttendanceStatusAndDetailsDTO(
                (isTodayWeekend || isTodayFullDayoff) ? null : todayStatus,
                workTypeListResponse,
                (String)expectedTimesAndWorkTypeListMap.get("startTime"),
                (String)expectedTimesAndWorkTypeListMap.get("endTime"),
                (String)expectedTimesAndWorkTypeListMap.get("locationName")
            );
        } catch (Exception e) {
            throw new RuntimeException("error while getAttendanceStatus service ");
        }
    }
    
    
    public AttendanceStatusDTO createOrUpdateAttendance(String objectId, Boolean status) throws ParseException {
        
        long parsedCheckTime = DateUtils.longDateNow();
        String customDateStringNow = DateUtils.longToCustomDate(parsedCheckTime);// yyyy-mm-dd String with custom day starting hour
        
        String employeeOid = objectId;
        String locationId = employeeService.findLocationIdById(employeeOid);
       
        Optional<Attendance> existingAttendance = attendanceRepository.findByEmployeeOidAndDate(employeeOid, customDateStringNow);
        Attendance attendance;
        if (existingAttendance.isPresent()) {
            // Update existing record
            attendance = existingAttendance.get();
            if (status) { // Check-in logic
                if (attendance.getCheckInTime() == null || parsedCheckTime<attendance.getCheckInTime()) {//when checkintime will be initiated
                    attendance.setCheckInTime(parsedCheckTime);  
                }
            } else { // Check-out logic
                if (attendance.getCheckOutTime() == null || parsedCheckTime>attendance.getCheckOutTime()) {
                    attendance.setCheckOutTime(parsedCheckTime);

                }
            }
        } else {
            // Create new record
            attendance = new Attendance();
            attendance.setEmployeeOid(employeeOid);
            attendance.setDate(customDateStringNow);
            attendance.setLocationId(locationId); // later by bluetooth device
            if (status) {//when checkintime will be initiated (first toggle on today)
                attendance.setCheckInTime(parsedCheckTime);     
            } else {
                attendance.setCheckOutTime(parsedCheckTime);
            }

        }    
        attendance.setStatus(status);
        attendanceRepository.save(attendance);
        return new AttendanceStatusDTO(status);
    }
    public Map<String,Object> getExpectedTimesAndWorkTypeList(String employeeOid,List<Map<String,String>> requestWorkhourKeyMapList){
        //if full dayoff then e.g. [{workhourStart=null, key=dayoffFull, workhourEnd=null}]
        List<String> workTypeListResponse= new ArrayList<>();      
        String startTime = null;
        String endTime = null;  
        // Get location details for this employee
        LocationInfoDTO locationDetail = employeeService.getLocationAndWorkDetailsByEmployeeOid(employeeOid);
        // Flag to check if we should prioritize null
        boolean prioritizeNull = false;
        // Process each request and determine the earliest startTime and latest endTime 
        for (Map<String,String> requestWorkhourKeyMap : requestWorkhourKeyMapList) {
            // Add the work type to the response list
            workTypeListResponse.add(requestWorkhourKeyMap.get("key"));
            // Check if "null" should be prioritized
            String workhourStartKey = requestWorkhourKeyMap.get("workhourStart");
            String workhourEndKey = requestWorkhourKeyMap.get("workhourEnd");
            if ("null".equals(workhourStartKey) || "null".equals(workhourEndKey)) {
                prioritizeNull = true;
            }

            // If "null" is not prioritized, map workhour keys to actual times
            if (!prioritizeNull) {
                String requestStartTime = locationDetail.getTimebyKey(workhourStartKey);
                String requestEndTime = locationDetail.getTimebyKey(workhourEndKey);

                // Update the overall startTime and endTime using comparison
                startTime = Config.getEarliestStringTime(startTime, requestStartTime);
                endTime = Config.getLatestStringTime(endTime, requestEndTime);
            }
        }  
        // If "null" was prioritized, reset startTime and endTime to null
        if (prioritizeNull) {
            startTime = "";
            endTime = "";
        }
        Map<String,Object> response = new HashMap<>();
        response.put("locationName",locationDetail.getWorkplace());
        response.put("startTime",startTime);
        response.put("endTime",endTime);
        response.put("workTypeListResponse",workTypeListResponse);
        return response;
    }
    public AttendanceHistory resolveAttendanceHistoryDTO(Attendance attendance,List<String> workTypeQueryList){
        // search request whether there is approved request for today. if exists, apply to starthour or to endhour 
        String dateString = attendance.getDate();
        String employeeOid = attendance.getEmployeeOid();
        // Get approved day-off/workhour requests for today
        List<Map<String,String>> requestWorkhourKeyMapList=requestService.getRequestByWorkTypeInAndApprovedStatusAndDate(employeeOid,workTypeQueryList,dateString);

        // Process each request and determine the earliest startTime and latest endTime
        Map<String,Object> expectedTimesAndWorkTypeListMap=getExpectedTimesAndWorkTypeList(employeeOid,requestWorkhourKeyMapList);

        AttendanceHistory response = null;
        try {
            response = new AttendanceHistory(
                attendance.getEmployeeOid(),
                attendance.getDate(),
                attendance.getLocationId(),
                attendance.getCheckInTime(),
                attendance.getCheckOutTime(),
                attendance.getStatus(),
                (List<String>) expectedTimesAndWorkTypeListMap.get("workTypeListResponse"), // Ensure this is a List<String>
                (String) expectedTimesAndWorkTypeListMap.get("startTime"), // Ensure this is a String
                (String) expectedTimesAndWorkTypeListMap.get("endTime") // Ensure this is a String
            );
        } catch (ClassCastException e) {
            // Log the error or handle it appropriately
            System.err.println("Error casting values: " + e.getMessage());
        } catch (Exception e) {
            // Catch any other general exceptions
            System.err.println("Error creating AttendanceHistory: " + e.getMessage());
        }
        // Check if workTypeListResponse intersects with workTypeQueryList
        List<String> intersection = new ArrayList<>(response.getWorkTypeValueList());
        intersection.retainAll(workTypeQueryList); // Retain only the common elements
        // If there is no intersection, skip returning the AttendanceHistory
        if (intersection.isEmpty()) {
            return null; // No match with workTypeQueryList, so we return null (skip the case)
        }
        return response;
    }
    public List<AttendanceHistory> getEmployeeHistory(
        String _id, 
        String startDate, 
        String endDate, 
        List<String> workTypeQueryList
        ) {
            try {  
                String employeeOid = _id;
                // System.out.println("Debugcheck");
                // System.out.println(workTypeQueryList);
                // Create objects with attended dates with  
                List<AttendanceHistory> response = getEmployeeAttendance(
                    employeeOid, 
                    startDate, 
                    endDate, 
                    workTypeQueryList
                    );
                // System.out.println("Debugcheck");
                // System.out.println(_id);
                // Convert the start and end dates to LocalDate
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate start = LocalDate.parse(startDate, formatter);
                LocalDate end = LocalDate.parse(endDate, formatter);
                // Get approved Full dayoff date List
                List<Dayoff> approvedDayoffDayoffs=requestService.findByRequestStatusAndDateBetweenInclusive(employeeOid,"approved", startDate, endDate);
                List<String> approvedDayoffDateStrings = approvedDayoffDayoffs.stream()
                    .map(Dayoff::getDayoffDate)
                    .collect(Collectors.toList());
                // Get any Attendance Existed 
                List<Attendance> existingAttendances=attendanceRepository.findByEmployeeOidAndDateBetweenInclusive(employeeOid,startDate, endDate);
                List<String> existingAttendanceStrings = existingAttendances.stream()
                    .map(Attendance::getDate)
                    .collect(Collectors.toList());
                // Get Holidays
                List<Holiday> holidayDates = holidayService.findHolidaysByDateRange(startDate,endDate);
                List<String> holidayDateStrings = holidayDates.stream()
                    .map(Holiday::getHolidayDate)
                    .collect(Collectors.toList());
                        // Get a list of weekday dates between start and end date (inclusive)
                List<String> allDates = DateUtils.getWeekdaysBetween(start, end);
                
                // Filter out dates that are:
                // - Already in the attendance list
                // - Holidays
                if (workTypeQueryList.contains("absent")){
                    List<String> availableDates = allDates.stream()
                        .filter(date -> (approvedDayoffDateStrings.isEmpty() || !approvedDayoffDateStrings.contains(date))) // Exclude if not empty
                        .filter(date -> (existingAttendanceStrings.isEmpty() || !existingAttendanceStrings.contains(date))) // Exclude if not empty
                        .filter(date -> (holidayDateStrings.isEmpty() || !holidayDateStrings.contains(date))) // Exclude if not empty
                        .collect(Collectors.toList());

                    // Create AttendanceHistory objects with 'absent' work type for available dates
                    List<AttendanceHistory> absents = availableDates.stream()
                        .map(date -> {
                            List<String> workTypeList = new ArrayList<>();
                            workTypeList.add("absent"); // Mark as absent for missing attendance
                            return new AttendanceHistory(
                                employeeOid, 
                                date, 
                                null, // You can replace this with the actual location if needed
                                null, // Check-in time is null as it's absent
                                null, // Check-out time is null as it's absent
                                false, // Status can be false for absent days
                                workTypeList, 
                                null, // Example: expected check-in time
                                null // Example: expected check-out time
                            );
                        }).collect(Collectors.toList());
                    response.addAll(absents);
                }
                if (workTypeQueryList.contains("holiday")){
                    // Create AttendanceHistory objects with 'absent' work type for available dates
                    List<AttendanceHistory> holidays = holidayDateStrings.stream()
                        .map(date -> {
                            List<String> workTypeList = new ArrayList<>();
                            workTypeList.add("holiday"); // Mark as absent for missing attendance
                            return new AttendanceHistory(
                                employeeOid, 
                                date, 
                                null, // You can replace this with the actual location if needed
                                null, // Check-in time is null as it's absent
                                null, // Check-out time is null as it's absent
                                false, // Status can be false for absent days
                                workTypeList, 
                                null, // Example: expected check-in time
                                null // Example: expected check-out time
                            );
                        }).collect(Collectors.toList());
                    response.addAll(holidays);

                }
            response =response.stream().sorted(Comparator.comparing(AttendanceHistory::getDate).reversed()) // Sort by date descending
                .collect(Collectors.toList());  // Collect back into a list
     
        return response;
        
        }catch (Exception e) {
            throw new RuntimeException("Error while Service getEmployeeHistory."+e);
        }

        
    }

    // Fetch employee attendance between two dates
    public List<AttendanceHistory> getEmployeeAttendance(
        String _id, 
        String startDate, 
        String endDate, 
        List<String> workTypeQueryList
        ) {
        try {

            //int employeeId = employeeService.getEmployeeIdById(_id);
            String employeeOid = _id;
            
            // Fetch attendance records based on filters
            List<Attendance> attendances = attendanceRepository.findByEmployeeOidAndDateBetweenInclusive(employeeOid,startDate,endDate);
            // Map Attendance to AttendanceHistory DTOs with enriched data
            return attendances.stream()
                //.sorted(Comparator.comparing(Attendance::getDate).reversed()) // Sort by date descending
                .map(attendance -> resolveAttendanceHistoryDTO(attendance,workTypeQueryList))
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error while Service getEmployeeAttendance.");
        }

        
    }




}