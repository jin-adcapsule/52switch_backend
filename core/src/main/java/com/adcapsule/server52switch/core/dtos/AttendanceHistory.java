package com.adcapsule.server52switch.core.dtos;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.adcapsule.server52switch.core.configs.Config;


public class AttendanceHistory {
    private final String employeeOid;
    private final String date;
    private final String locationId;
    private final String checkInTime;
    private final String checkOutTime;
    private final boolean status;
    private final String checkInStatus; 
    private final String checkOutStatus;
    private final List<String> workTypeList;   
    private final String workduration;

    public AttendanceHistory(
        String employeeOid,
        String date, 
        String locationId,
        Date checkInTime,
        Date checkOutTime, 
        boolean status, 
        List<String> workTypeList,
        String expectedCheckInTime,
        String expectedCheckOutTime
        ) {
        this.employeeOid = employeeOid;
        this.date = formatDate(date);
        this.locationId = (locationId != null) ? locationId: null;
        this.checkInTime =formatTime(checkInTime);
        this.checkOutTime = revisedCheckOutTime(checkOutTime,expectedCheckOutTime,date,status) ;
        this.status = status;
        this.workduration = calculateWorkduration(checkInTime,checkOutTime);
        this.checkInStatus = (expectedCheckInTime != null&&checkInTime != null) ? resolveCheckInStatus(checkInTime,expectedCheckInTime): null;//getCheckInStatus();
        this.checkOutStatus = (expectedCheckOutTime != null&&checkOutTime !=null) ? resolveCheckOutStatus(checkOutTime,expectedCheckOutTime,date,status): null;//getCheckOutStatus();
        this.workTypeList = resolveWorkTypeList(workTypeList);//getWorkTypeList();

        


    }
    private String formatDate(String date) {
        // Convert string date to LocalDate
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // Format date as yy.mm.dd(요일)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd(E)", java.util.Locale.KOREAN);
        return localDate.format(formatter);
    }

    private String formatTime(Date time) {
        if (time == null) {
            return null;
        }
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        return time.toInstant().atZone(java.time.ZoneId.of("Asia/Seoul")).format(timeFormatter);
    }
    // Helper method to check if a date is today input format is yyyy-mm-dd
    private boolean isToday(String date) {
        try {
            // Parse the input date in ISO-8601 format (yyyy-MM-dd)
            LocalDate inputDate = LocalDate.parse(date);    
            //LocalDate inputDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yy.MM.dd(E)", java.util.Locale.KOREAN));
            return inputDate.isEqual(LocalDate.now());
        } catch (Exception e) {
            return false; // Return if parsing fails
        }
    }
    private String resolveCheckInStatus(Date checkInTimeDate,String expectedCheckInTime){
        String checkInTimeString = null;
        if (checkInTimeDate != null) {
            checkInTimeString = Config.getCheckTime_HHmm_String(checkInTimeDate);
        } else {
            throw new IllegalArgumentException("Parsed check time cannot be null");
        }
        try{    
            if (expectedCheckInTime == null||checkInTimeString.compareTo(expectedCheckInTime) < 0) {//not supposed to check in or early came
                return "onTimeArrival";//"onTimeArrival";
            } else {//expected to check in but came late
                return "lateArrival";//"lateArrival";
            }
        }catch (Exception e) {

            return "error"; // Return if parsing fails
        }

    }
    //Calculate Checkout Status for days before today and not toggled out
    private String resolveCheckOutStatus( Date checkOutTimeDate,String expectedCheckOutTime, String date, boolean status){
        String checkOutTimeString;
        if (checkOutTimeDate != null) {
            checkOutTimeString = Config.getCheckTime_HHmm_String(checkOutTimeDate);
        } else {
            throw new IllegalArgumentException("Parsed check time cannot be null");
        }
        try{             
            
            if (isToday(date)&&status){
                return "working";//return "working"; 
            }   
            // Check if checkOutStatus is null or '근무중'
            if (!isToday(date)&&(checkOutStatus == null || status)) {
                return "onTimeLeft"; // Return if not toggled out or the status is '근무중'
            }else{
                if (expectedCheckOutTime == null||checkOutTimeString.compareTo(expectedCheckOutTime) > 0) {//not supposed to check in or early came
                    return "onTimeLeft";//"onTimeArrival";
                } else {//expected to check in but came late
                    return "earlyLeft";//"lateArrival";
                }
            }
        }catch (Exception e) {

            return "error"; // Return if parsing fails
        }
    }
    //Get revised checkouttime depends on revised checkout status
    private String revisedCheckOutTime(Date checkOutTime,String expectedCheckOutTime, String date, boolean status){
        // Check if today and currently working
  
        if (isToday(date) && status) {
            return ""; // Currently working, so no check-out time
        } 
        try{
            // Check if date is before today and checkOutStatus is null or '근무중'
            if (!isToday(date)&&(checkOutStatus == null || "working".equals(checkOutStatus))) {
                return expectedCheckOutTime;
            }else{
                return formatTime(checkOutTime);
            }
            

        }catch (Exception e) {

            return formatTime(checkOutTime); // Return if parsing fails
        }
    }
    // Calculate Work Duration
    private String calculateWorkduration(Date checkInTime, Date checkOutTime) {
        
        try {
            // Ensure both dates are not null
            if (checkInTime == null || checkOutTime == null) {
                return null;  // Return null or handle the error appropriately
            }
            
            // Calculate the duration in milliseconds
            long durationMillis = checkOutTime.getTime() - checkInTime.getTime();
                    
            // Convert milliseconds to hours and minutes
            long hours = durationMillis / (1000 * 60 * 60); // Convert milliseconds to hours
            long minutes = (durationMillis % (1000 * 60 * 60)) / (1000 * 60); // Convert remaining milliseconds to minutes

            // Format the duration in hh:mm format
            String durationFormatted = String.format("%02d시간%02d분", hours, minutes);


            return durationFormatted;
                
            
        }catch (Exception e) {

            return null; // Return null if parsing fails
        }
    }

    //Calculate Checkout Status for days before today and not toggled out
    private List<String> resolveWorkTypeList(List<String> workTypeList){
         // Create a new list that copies the original workTypeList
        List<String> responseList = new ArrayList<>(workTypeList);
        try{             
            // Add checkInStatus and checkOutStatus to the copied list
            responseList.add(checkInStatus);
            responseList.add(checkOutStatus);

            return responseList;
        }catch (Exception e) {
            // Return the original list in case of an error
            return workTypeList; // Return if parsing fails
        }
    }
    // Getters and Setters
    
    public String getEmployeeOid() {
        return employeeOid;
    }

    public String getDate() {
        return date;
    }
    public String getLocationId() {
        return locationId;
    }
    public String getCheckInTime() {
        return checkInTime;
    }

    public String getCheckOutTime() {
        return checkOutTime;
    }

    public boolean getStatus() {
        return status;
    }
    public String getWorkduration(){
        return workduration;
    }
    public String getCheckInStatus() {
        //return checkInStatus;

        return Config.workTypeToTextMap.get(checkInStatus);
    }
    public String getCheckOutStatus() {
        //return checkOutStatus;
        return Config.workTypeToTextMap.get(checkOutStatus);
    }
    public List<String> getWorkTypeList() {
        // Map workTypeList items from values to text
        return workTypeList.stream()
            .map(key -> Config.workTypeToTextMap.get(key)) // Get mapped value
            .filter(Objects::nonNull)    // Exclude null values in case of unmatched keys
            .map(String.class::cast)                      // Ensure the result is String
            .collect(Collectors.toList());
    }
    public List<String> getWorkTypeValueList() {
        // Map workTypeList items from values to text
        return workTypeList;
    }
}