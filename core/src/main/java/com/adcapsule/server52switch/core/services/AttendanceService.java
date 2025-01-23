package com.adcapsule.server52switch.core.services;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adcapsule.server52switch.core.configs.Config;
import com.adcapsule.server52switch.core.configs.DateUtils;
import com.adcapsule.server52switch.core.dtos.AttendanceHistory;
import com.adcapsule.server52switch.core.dtos.AttendanceStatusAndDetailsDTO;
import com.adcapsule.server52switch.core.dtos.AttendanceStatusDTO;
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
            String dateToday = DateUtils.getyyyymmddStringNow();
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
            boolean isTodayHoliday = holidayService.findByHolidayDate(DateUtils.getyyyymmddStringNow()).orElse(null) !=null;
            // Get approved day-off/workhour requests for today
            List<Map<String,String>> requestWorkhourKeyMapList = requestService.getRequestByTodayAndApprovedStatus(employeeOid);
            // Process each request and determine the earliest startTime and latest endTime
            Map<String,Object> expectedTimesAndWorkTypeListMap = employeeService.getExpectedTimesAndWorkTypeList(employeeOid,requestWorkhourKeyMapList);
            //check whether today is fulldayoff day
            List<String> workTypeListResponse = (List<String>)expectedTimesAndWorkTypeListMap.get("workTypeListResponse");
            List<String> fulldayOffList=Config.fullDayoffList;
            boolean isTodayFullDayoff=workTypeListResponse.stream().anyMatch(fulldayOffList::contains);
            
            return new AttendanceStatusAndDetailsDTO(
                (isTodayWeekend || isTodayFullDayoff ||isTodayHoliday) ? null : todayStatus,
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
        
        long parsedCheckTime = DateUtils.getLongDateNow();
        String customDateStringNow = DateUtils.parseLongToCustomDate(parsedCheckTime);// yyyy-mm-dd String with custom day starting hour
        
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
    
    public AttendanceHistory resolveAttendanceHistoryDTO(Attendance attendance,List<String> workTypeQueryList){
        // search request whether there is approved request for today. if exists, apply to starthour or to endhour 
        String dateString = attendance.getDate();
        String employeeOid = attendance.getEmployeeOid();
        // Get approved day-off/workhour requests for today
        List<Map<String,String>> requestWorkhourKeyMapList=requestService.getRequestByWorkTypeInAndApprovedStatusAndDate(employeeOid,workTypeQueryList,dateString);

        // Process each request and determine the earliest startTime and latest endTime
        Map<String,Object> expectedTimesAndWorkTypeListMap=employeeService.getExpectedTimesAndWorkTypeList(employeeOid,requestWorkhourKeyMapList);

        List<String> workTypeListResponse = (List<String>) expectedTimesAndWorkTypeListMap.get("workTypeListResponse");
        if (workTypeListResponse == null) {
            workTypeListResponse = new ArrayList<>(); // or handle appropriately
        }
        AttendanceHistory response = null;
        try {
            response = new AttendanceHistory(
                attendance.getEmployeeOid(),
                attendance.getDate(),
                attendance.getLocationId(),
                attendance.getCheckInTime(),
                attendance.getCheckOutTime(),
                attendance.getStatus(),
                workTypeListResponse, // Ensure this is a List<String>
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
        if (response == null) {
            System.err.println("AttendanceHistory is null, skipping.");
            return null;
        }
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
            if (_id == null || startDate == null || endDate == null || workTypeQueryList == null) {
                throw new IllegalArgumentException("Input parameters cannot be null");
            }
        
            try {
                String employeeOid = _id;
        
                // Fetch Employee Attendance History
                List<AttendanceHistory> response = getEmployeeAttendance(employeeOid, startDate, endDate, workTypeQueryList);
                if (response == null) {
                    response = new ArrayList<>();
                }

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate start = LocalDate.parse(startDate, formatter);
                LocalDate end = LocalDate.parse(endDate, formatter);

                // Get approved Dayoff Dates
                List<Dayoff> approvedDayoffDayoffs = requestService.findByRequestStatusAndDateBetweenInclusive(employeeOid, "approved", startDate, endDate);
                if (approvedDayoffDayoffs == null) {
                    approvedDayoffDayoffs = new ArrayList<>();
                }

                List<String> approvedDayoffDateStrings = approvedDayoffDayoffs.stream()
                    .filter(Objects::nonNull)
                    .map(Dayoff::getDayoffDate)
                    .collect(Collectors.toList());

                // Get existing Attendance
                List<Attendance> existingAttendances = attendanceRepository.findByEmployeeOidAndDateBetweenInclusive(employeeOid, startDate, endDate);
                if (existingAttendances == null) {
                    existingAttendances = new ArrayList<>();
                }

                List<String> existingAttendanceStrings = existingAttendances.stream()
                    .filter(Objects::nonNull)
                    .map(Attendance::getDate)
                    .collect(Collectors.toList());

                // Get Holidays
                List<Holiday> holidayDates = holidayService.findHolidaysByDateRange(startDate, endDate);
                if (holidayDates == null) {
                    holidayDates = new ArrayList<>();
                }

                List<String> holidayDateStrings = holidayDates.stream()
                    .filter(Objects::nonNull)
                    .map(Holiday::getHolidayDate)
                    .collect(Collectors.toList());

                // Get weekdays between the start and end date
                List<String> allDates = DateUtils.getWeekdaysBetween(start, end);
                if (allDates == null) {
                    allDates = new ArrayList<>();
                }

                // Filter dates based on conditions and add AttendanceHistory for work types like "absent" and "holiday"
                if (workTypeQueryList.contains("absent")) {
                    List<String> availableDates = allDates.stream()
                    .filter(date -> date != null) // Ensure no null dates are processed
                        .filter(date -> !approvedDayoffDateStrings.contains(date))
                        .filter(date -> !existingAttendanceStrings.contains(date))
                        .filter(date -> !holidayDateStrings.contains(date))
                        .collect(Collectors.toList());
        
                    List<AttendanceHistory> absents = availableDates.stream()
                        .map(date -> new AttendanceHistory(employeeOid, date, null, null, null, false, List.of("absent"), null, null))
                        .collect(Collectors.toList());
        
                    response.addAll(absents);

                }

                if (workTypeQueryList.contains("holiday")) {
                    List<AttendanceHistory> holidays = holidayDateStrings.stream()
                        .map(date -> new AttendanceHistory(employeeOid, date, null, null, null, false, List.of("holiday"), null, null))
                        .collect(Collectors.toList());
        
                    response.addAll(holidays);

                }

                response = response.stream()
                    .sorted(Comparator.comparing(AttendanceHistory::getDate).reversed())
                    .collect(Collectors.toList());
        
                return response;
        
            } catch (Exception e) {
                // Log the exception with the full stack trace to help identify the issue
                e.printStackTrace();
                throw new RuntimeException("Error while Service getEmployeeHistory. " +  e.getMessage(), e);
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
                .filter(Objects::nonNull)  // Remove nulls
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error while Service getEmployeeAttendance.");
        }

        
    }




}