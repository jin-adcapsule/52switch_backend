package com.adcapsule.server52switch.core.dtos;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.adcapsule.server52switch.core.configs.Config;


public class AttendanceHistory {
    private final String employeeOid;
    private final String date;
    private final String checkInTime;
    private final String checkOutTime;
    private final boolean status;
    private final String checkInStatus; 
    private final String checkOutStatus;
    private final List<String> workTypeList;   
    private final String workduration;
    public AttendanceHistory(String employeeOid,String date, Date checkInTime, Date checkOutTime, boolean status, String checkInStatus,String checkOutStatus,List<String> workTypeList) {
        //this.employeeId = employeeId;
        this.employeeOid = employeeOid;
        this.date = formatDate(date);
        this.checkInTime = formatTime(checkInTime);
        this.checkOutTime = revisedCheckOutTime(checkOutTime);
        this.status = status;
        this.workduration = calculateWorkduration(checkInTime,checkOutTime);
        this.checkInStatus = checkInStatus;//getCheckInStatus();
        this.checkOutStatus = revisedCheckOutStatus(checkOutStatus);//getCheckOutStatus();
        this.workTypeList = workTypeList;//getWorkTypeList();


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
    // Helper method to check if a date is today
    private boolean isToday(String date) {
        try {

            LocalDate inputDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yy.MM.dd(E)", java.util.Locale.KOREAN));
            return inputDate.isEqual(LocalDate.now());
        } catch (Exception e) {
            return false; // Return if parsing fails
        }
    }
    //Calculate Checkout Status for days before today and not toggled out
    private String revisedCheckOutStatus(String checkOutStatus){
        if (isToday(date)){
            return checkOutStatus;
        }
        try{
            // Check if checkOutStatus is null or '근무중'
            if (checkOutStatus == null || "근무중".equals(checkOutStatus)) {
                return "정상퇴근"; // Return if not toggled out or the status is '근무중'
            }else{return checkOutStatus;}
            

        }catch (Exception e) {

            return checkOutStatus; // Return if parsing fails
        }
    }
    //Get revised checkouttime depends on revised checkout status
    private String revisedCheckOutTime(Date checkOutTime){
        // Check if today and currently working
        if (isToday(date) && status) {
            return ""; // Currently working, so no check-out time
        } 
        try{
            // Check if checkOutStatus is null or '근무중'
            if (checkOutStatus == null || "근무중".equals(checkOutStatus)) {
                return "NONONONO";//return "정상퇴근"; // Return null if not toggled out or the status is '근무중'
            }else{return formatTime(checkOutTime);}
            

        }catch (Exception e) {

            return formatTime(checkOutTime); // Return if parsing fails
        }
    }
    // Calculate Work Duration
    private String calculateWorkduration(Date checkInTime, Date checkOutTime) {
        if (isToday(date) && status) {
            return "근무중"; // Currently working
        }
        
        try {
            // Ensure both dates are not null
            if (checkInTime == null || checkOutTime == null) {
                System.out.println("Error: One or both Date objects are null");
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
    // Getters and Setters
    
    public String getEmployeeOid() {
        return employeeOid;
    }

    public String getDate() {
        return date;
    }

    public String getCheckInTime() {
        return checkInTime;
    }

    public String getCheckOutTime() {
        return checkOutTime;
    }

    public boolean isStatus() {
        return status;
    }
    public String getWorkduration(){
        return workduration;
    }
    public String getCheckInStatus() {

        return Config.workTypeToTextMap.get(checkInStatus);
    }
    public String getCheckOutStatus() {

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
}