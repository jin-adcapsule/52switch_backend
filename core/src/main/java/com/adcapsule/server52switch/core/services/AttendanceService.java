package com.adcapsule.server52switch.core.services;
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

import com.adcapsule.server52switch.core.configs.Config;
import com.adcapsule.server52switch.core.dtos.AttendanceHistory;
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

   public AttendanceStatusDTO getAttendanceStatus(String objectId) {
        try {
            String dateToday = Config.getCurrentDate_String();
            AttendanceStatusDTO AttendanceStatusDTO = attendanceRepository.findStatusByobjectOidAndDate(objectId, dateToday);
            if (AttendanceStatusDTO == null) {
                System.out.println(dateToday);
                System.out.println(objectId);
                return new AttendanceStatusDTO(false); // or handle accordingly
            }
            System.out.println(AttendanceStatusDTO);
            return AttendanceStatusDTO;
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
        
        // Parse workhourOn into a Date object for comparison
        
        String parsedcheckTime_hhmm = null;
        if (parsedCheckTime != null) {
            parsedcheckTime_hhmm = Config.getCheckTime_HHmm_String(parsedCheckTime);
        } else {
            throw new IllegalArgumentException("Parsed check time cannot be null");
        }

        // search request whether there is approved request for today. if exists, apply to starthour or to endhour 
        
        String startTime = null;
        String endTime = null;        
        List<Map<String,String>> requestWorkhourKeyMapList=requestService.getRequestByTodayAndApprovedStatus(employeeOid);
        if (requestWorkhourKeyMapList == null || requestWorkhourKeyMapList.isEmpty()) {
            throw new RuntimeException("No approved requests for today");
        }
        

        List<String> workTypeListToday= new ArrayList<>();

        LocationInfoDTO locationDetail = employeeService.getLocationAndWorkDetailsByEmployeeOid(employeeOid);

        for (Map<String,String> requestWorkhourKeyMap : requestWorkhourKeyMapList) {
            //this would be 'null' for cases of 휴가 경조휴가 휴직
            String _startTimeLocationKey=requestWorkhourKeyMap.get("workhourStart");
            String _endTimeLocationKey=requestWorkhourKeyMap.get("workhourEnd");

            //if querying key is 'null' then those would be null
            String _startTime = locationDetail.getTimebyKey(_startTimeLocationKey);//should start time by todays dayoff state
            String _endTime = locationDetail.getTimebyKey(_endTimeLocationKey);//should end time by todays dayoff state
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
        Optional<Attendance> existingAttendance = attendanceRepository.findByEmployeeOidAndDate(employeeOid, currentDateInKST);
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
            attendance.setEmployeeOid(employeeOid);
            //attendance.setEmployeeId(employeeId);
            attendance.setDate(currentDateInKST);
            attendance.setExpectedCheckInTime(startTime);
            attendance.setExpectedCheckOutTime(endTime);
            if (status) {//when checkintime will be initiated (first toggle on today)
                attendance.setCheckInTime(parsedCheckTime);
                attendance.setCheckOutStatus("working");      
                if (startTime == null||parsedcheckTime_hhmm.compareTo(startTime) < 0) {//not supposed to check in or early came
                    attendance.setCheckInStatus("onTimeArrival");
                    attendance.setWorkTypeList(workTypeListToday);
                } else {//expected to check in but came late
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
        attendanceRepository.save(attendance);
        return new AttendanceStatusDTO(status);
    }


    // Fetch employee attendance between two dates
    public List<AttendanceHistory> getEmployeeAttendance(
        String _id, 
        String startDate, 
        String endDate, 
        List<String> workTypeList
        ) {
        try {

            //int employeeId = employeeService.getEmployeeIdById(_id);
            String employeeOid = _id;
            // Fetch attendance records based on filters
            List<Attendance> attendances = attendanceRepository.findByEmployeeOidInAndWorkTypeAndDateBetweenInclusive(employeeOid, workTypeList,startDate,endDate);
            
            
            // Map Attendance to AttendanceHistory DTO
            return attendances.stream()
                .sorted(Comparator.comparing(Attendance::getDate).reversed()) // Sort by date descending
                .map(attendance -> new AttendanceHistory(
                    //attendance.getEmployeeId(),
                    attendance.getEmployeeOid(),
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
            throw new RuntimeException("Error while Service getEmployeeAttendance.");
        }

        
    }





}