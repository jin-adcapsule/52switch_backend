package com.adcapsule.server52switch.core.services;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adcapsule.server52switch.core.configs.Config;
import com.adcapsule.server52switch.core.dtos.AttendanceHistory;
import com.adcapsule.server52switch.core.dtos.AttendanceStatusAndDetailsDTO;
import com.adcapsule.server52switch.core.dtos.AttendanceStatusDTO;
import com.adcapsule.server52switch.core.dtos.LocationInfoDTO;
import com.adcapsule.server52switch.core.models.Attendance;
import com.adcapsule.server52switch.core.repositories.AttendanceRepository;


@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeService employeeService; 
    private final RequestService requestService; 

   // private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");//just to compare date inforation
    
   @Autowired
   public AttendanceService(AttendanceRepository attendanceRepository, EmployeeService employeeService,RequestService requestService) {
       this.attendanceRepository = attendanceRepository;
       this.employeeService = employeeService;
       this.requestService = requestService;
       
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
            // Get approved day-off/workhour requests for today
            List<Map<String,String>> requestWorkhourKeyMapList= requestService.getRequestByTodayAndApprovedStatus(employeeOid);
            // Process each request and determine the earliest startTime and latest endTime
            Map<String,Object> expectedTimesAndWorkTypeListMap=getExpectedTimesAndWorkTypeList(employeeOid,requestWorkhourKeyMapList);
            return new AttendanceStatusAndDetailsDTO(
                todayStatus,
                (List<String>)expectedTimesAndWorkTypeListMap.get("workTypeListResponse"),
                (String)expectedTimesAndWorkTypeListMap.get("startTime"),
                (String)expectedTimesAndWorkTypeListMap.get("endTime"),
                (String)expectedTimesAndWorkTypeListMap.get("locationName")
            );
        } catch (Exception e) {
            throw new RuntimeException("error while getAttendanceStatus service ");
        }
    }
    
    
    public AttendanceStatusDTO createOrUpdateAttendance(String objectId, Date parsedcheckTime, Boolean status) throws ParseException {
        // Format the current date as 'yyyy-MM-dd'
        //dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        //String formattedDate = dateFormat.format(new Date());
        Date parsedCheckTime =  parsedcheckTime;
        
        String currentDateInKST = Config.getCurrentDate_String();
        //int employeeId = employeeService.getEmployeeIdById(objectId);
        String employeeOid = objectId;
        //String workhourOn = (String) employeeService.getLocationAndWorkDetailsByEmployeeId(employeeId).get("workhourOn");
        String locationId = employeeService.findLocationIdById(employeeOid);
        // Parse workhourOn into a Date object for comparison
       
        Optional<Attendance> existingAttendance = attendanceRepository.findByEmployeeOidAndDate(employeeOid, currentDateInKST);
        Attendance attendance;
        if (existingAttendance.isPresent()) {
            // Update existing record
            attendance = existingAttendance.get();

            if (status) { // Check-in logic
                if (attendance.getCheckInTime() == null || parsedCheckTime.before(attendance.getCheckInTime())) {//when checkintime will be initiated
                    attendance.setCheckInTime(parsedCheckTime);  
                }
            } else { // Check-out logic
                if (attendance.getCheckOutTime() == null || parsedCheckTime.after(attendance.getCheckOutTime())) {
                    attendance.setCheckOutTime(parsedCheckTime);

                }
            }
        } else {
            // Create new record
            attendance = new Attendance();
            attendance.setEmployeeOid(employeeOid);
            attendance.setDate(currentDateInKST);
            attendance.setLocationId(locationId);
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
        System.out.println(requestWorkhourKeyMapList);
        // Process each request and determine the earliest startTime and latest endTime
        Map<String,Object> expectedTimesAndWorkTypeListMap=getExpectedTimesAndWorkTypeList(employeeOid,requestWorkhourKeyMapList);
        System.out.println(expectedTimesAndWorkTypeListMap);
        AttendanceHistory response = new AttendanceHistory(
            attendance.getEmployeeOid(),
            attendance.getDate(),
            attendance.getLocationId(),
            attendance.getCheckInTime(),  
            attendance.getCheckOutTime(),   
            attendance.getStatus(),
            (List<String>)expectedTimesAndWorkTypeListMap.get("workTypeListResponse"),//approvedExistingworkTypeList
            (String)expectedTimesAndWorkTypeListMap.get("startTime"),//expectedCheckInTime
            (String)expectedTimesAndWorkTypeListMap.get("endTime") //expectedCheckOutTime
        );
        // Check if workTypeListResponse intersects with workTypeQueryList
        List<String> intersection = new ArrayList<>(response.getWorkTypeValueList());
        intersection.retainAll(workTypeQueryList); // Retain only the common elements
        // If there is no intersection, skip returning the AttendanceHistory
        if (intersection.isEmpty()) {
            return null; // No match with workTypeQueryList, so we return null (skip the case)
        }
        return response;
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
                .sorted(Comparator.comparing(Attendance::getDate).reversed()) // Sort by date descending
                .map(attendance -> resolveAttendanceHistoryDTO(attendance,workTypeQueryList))
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error while Service getEmployeeAttendance.");
        }

        
    }





}