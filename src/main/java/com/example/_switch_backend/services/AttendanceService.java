package com.example._switch_backend.services;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example._switch_backend.config.Config;
import com.example._switch_backend.models.Attendance;
import com.example._switch_backend.models.AttendanceHistory;
import com.example._switch_backend.repositories.AttendanceRepository;


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

    public Attendance createOrUpdateAttendance(String objectId, Date parsedcheckTime, Boolean status) throws ParseException {
        // Format the current date as 'yyyy-MM-dd'
        //dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        //String formattedDate = dateFormat.format(new Date());
        Date parsedCheckTime =  parsedcheckTime;
        
        String currentDateInKST = Config.getCurrentDate_String();
        int employeeId = employeeService.getEmployeeIdById(objectId);
        //String workhourOn = (String) employeeService.getLocationAndWorkDetailsByEmployeeId(employeeId).get("workhourOn");
        
        // Parse workhourOn into a Date object for comparison
        
        String parsedcheckTime_hhmm = null;
        if (parsedCheckTime != null) {
            parsedcheckTime_hhmm = Config.getCheckTime_HHmm_String(parsedCheckTime);
        } else {
            throw new IllegalArgumentException("Parsed check time cannot be null");
        }
        System.out.println(parsedcheckTime_hhmm);
        // search request whether there is approved request for today. if exists, apply to starthour or to endhour 
        
        String startTime = null;
        String endTime = null;        
        List<Map<String,String>> requestWorkhourKeyMapList=requestService.getRequestByTodayAndApprovedStatus(employeeId);
        if (requestWorkhourKeyMapList == null || requestWorkhourKeyMapList.isEmpty()) {
            throw new RuntimeException("No approved requests for today");
        }
        

        List<String> workTypeListToday= new ArrayList<>();

        Map<String,Object> locationDetail = employeeService.getLocationAndWorkDetailsByEmployeeId(employeeId);

        for (Map<String,String> requestWorkhourKeyMap : requestWorkhourKeyMapList) {
            //this would be 'null' for cases of 휴가 경조휴가 휴직
            String _startTimeLocationKey=requestWorkhourKeyMap.get("workhourStart");
            String _endTimeLocationKey=requestWorkhourKeyMap.get("workhourEnd");

            //if querying key is 'null' then those would be null
            String _startTime = (String) locationDetail.get(_startTimeLocationKey);
            String _endTime = (String) locationDetail.get(_endTimeLocationKey);
            List<String> _startTimeSortedList=Config.compareAndSortTimes(startTime, _startTime,true);
            List<String> _endTimeSortedList=Config.compareAndSortTimes(endTime, _endTime,false);
            startTime = (_startTimeSortedList != null)
                ? _startTimeSortedList.get(0)
                : startTime;
            endTime = (_endTimeSortedList != null)
                ? _endTimeSortedList.get(0)
                : endTime;
            workTypeListToday.add(requestWorkhourKeyMap.get("key"));
        }
 
         
        // Check if an attendance record exists for the given date and employeeId
        Optional<Attendance> existingAttendance = attendanceRepository.findByEmployeeIdAndDate(employeeId, currentDateInKST);
        System.out.println(workTypeListToday);
        Attendance attendance;
        if (existingAttendance.isPresent()) {
            // Update existing record
            attendance = existingAttendance.get();

            if (status) { // Check-in logic
                
                if (attendance.getCheckInTime() == null || parsedCheckTime.before(attendance.getCheckInTime())) {//when checkintime will be initiated
                    attendance.setCheckInTime(parsedCheckTime);
   
                    if (startTime == null || parsedcheckTime_hhmm.compareTo(startTime) < 0) {
                        attendance.setCheckInStatus("onTimeArrival");
                        attendance.setWorkTypeList(workTypeListToday);
                    } else {
                        attendance.setCheckInStatus("lateArrival");
                        attendance.setWorkTypeList(workTypeListToday);
                    }
                    
                }
                //whenever toggle(status) on
                attendance.setCheckOutStatus("working");
                

            } else { // Check-out logic
                if (attendance.getCheckOutTime() == null || parsedCheckTime.after(attendance.getCheckOutTime())) {
                    attendance.setCheckOutTime(parsedCheckTime);
                    if (endTime == null || parsedcheckTime_hhmm.compareTo(endTime) > 0) {
                        attendance.setCheckOutStatus("onTimeLeft");
                        attendance.setWorkTypeList(workTypeListToday);
                    } else {
                        attendance.setCheckOutStatus("earlyLeft");
                        attendance.setWorkTypeList(workTypeListToday);
                    }

                }
            }
        } else {
            // Create new record
            
            attendance = new Attendance();
            attendance.setEmployeeId(employeeId);
            attendance.setDate(currentDateInKST);

            if (status) {//when checkintime will be initiated
                attendance.setCheckInTime(parsedCheckTime);
                attendance.setCheckOutStatus("working");    //whenever toggle(status) on  
                if (startTime == null||parsedcheckTime_hhmm.compareTo(startTime) < 0) {
                    attendance.setCheckInStatus("onTimeArrival");
                    attendance.setWorkTypeList(workTypeListToday);
                } else {
                    attendance.setCheckInStatus("lateArrival");
                    attendance.setWorkTypeList(workTypeListToday);
                }
            } else {
                attendance.setCheckOutTime(parsedCheckTime);
                if (endTime == null||parsedcheckTime_hhmm.compareTo(endTime) < 0) {
                    attendance.setCheckOutStatus("onTimeLeft");
                    attendance.setWorkTypeList(workTypeListToday);
                } else {
                    attendance.setCheckOutStatus("earlyLeft");
                    attendance.setWorkTypeList(workTypeListToday);
                }
            }

        }
            
        attendance.setStatus(status);
        return attendanceRepository.save(attendance);
    }


    // Fetch employee attendance between two dates
    public List<AttendanceHistory> getEmployeeAttendance(
        String _id, 
        String startDate, 
        String endDate, 
        List<String> workTypeList
        ) {
        try {

            int employeeId = employeeService.getEmployeeIdById(_id);

            // Fetch attendance records based on filters
            List<Attendance> attendances = attendanceRepository.findByEmployeeIdInAndWorkTypeAndDateBetweenInclusive(employeeId, workTypeList,startDate,endDate);
            
            
            // Map Attendance to AttendanceHistory DTO
            return attendances.stream()
                .sorted(Comparator.comparing(Attendance::getDate).reversed()) // Sort by date descending
                .map(attendance -> new AttendanceHistory(
                    attendance.getEmployeeId(),
                    attendance.getDate(),
                    attendance.getCheckInTime(),
                    attendance.getCheckOutTime(),
                    attendance.getStatus(),
                    attendance.getCheckInStatus(),
                    attendance.getCheckOutStatus(),
                    attendance.getWorkTypeList()
                ))
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Invalid date format. Use 'yyyy-MM-dd' for startDate and endDate.");
        }
    }






}