package com.adcapsule.server52switch.core.dtos;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.adcapsule.server52switch.core.configs.Config;


public class AttendanceHistory {
    private int employeeId;
    private String date;
    private String checkInTime;
    private String checkOutTime;
    private boolean status;
    private String checkInStatus; 
    private String checkOutStatus;
    private List<String> workTypeList;   
    private String workduration;
    public AttendanceHistory(int employeeId,String date, Date checkInTime, Date checkOutTime, boolean status, String checkInStatus,String checkOutStatus,List<String> workTypeList) {
        this.employeeId = employeeId;
        this.date = formatDate(date);
        this.checkInTime = formatTime(checkInTime);
        //this.checkOutTime = formatTime(checkOutTime);
        this.status = status;
        this.workduration = getWorkDuration(checkInTime,checkOutTime);
        this.checkInStatus = checkInStatus;//getCheckInStatus();
        this.checkOutStatus = checkOutStatus;//getCheckOutStatus();
        this.workTypeList = workTypeList;//getWorkTypeList();
        // Check if today and currently working
        if (isToday(this.date) && status) {
            this.checkOutTime = ""; // Currently working, so no check-out time
        } else {
            this.checkOutTime = formatTime(checkOutTime);
        }

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
        e.printStackTrace();
        return false; // Return false if parsing fails
    }
}
    // Getters and Setters
    public int getEmployeeId() {
        return employeeId;
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
    // Calculate Work Duration
    public String getWorkDuration(Date checkInTime, Date checkOutTime) {
        /*if (isToday(date) && status) {
            return "근무중"; // Currently working
        }
            */
        try {
            // Ensure both dates are not null
            if (checkInTime == null || checkOutTime == null) {
                System.out.println("Error: One or both Date objects are null");
                return null;  // Return null or handle the error appropriately
            }
            
            // Check if date is today and status is true

    
            /* 
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime checkIn = LocalTime.parse(checkInTime, timeFormatter);
            LocalTime checkOut = LocalTime.parse(checkOutTime, timeFormatter);
            long durationMinutes = java.time.Duration.between(checkIn, checkOut).toMinutes();

            long hours = TimeUnit.MINUTES.toHours(durationMinutes);
            long minutes = durationMinutes % 60;
            */
            // Calculate the duration in milliseconds
            long durationMillis = checkOutTime.getTime() - checkInTime.getTime();
                    
            // Convert milliseconds to hours and minutes
            long hours = durationMillis / (1000 * 60 * 60); // Convert milliseconds to hours
            long minutes = (durationMillis % (1000 * 60 * 60)) / (1000 * 60); // Convert remaining milliseconds to minutes

            // Format the duration in hh:mm format
            String durationFormatted = String.format("%02d시간%02d분", hours, minutes);


            return durationFormatted;
                
            
        }catch (Exception e) {
            e.printStackTrace();
            return null; // Return null if parsing fails
        }
    }
    public String getCheckInStatus() {
        /* 
        if ("lateArrival".equalsIgnoreCase(checkInStatus)) {
            return "지각"; // Late
        } else if ("onTimeArrival".equalsIgnoreCase(checkInStatus)) {
            return "정상출근"; // On time
        }
        return checkInStatus; // Default to the original value if no match
        */
        return Config.workTypeToTextMap.get(checkInStatus);
    }
    public String getCheckOutStatus() {
        /* 
        if ("earlyLeft".equalsIgnoreCase(checkOutStatus)) {
            return "조기퇴근"; // Late
        } else if ("onTimeLeft".equalsIgnoreCase(checkOutStatus)) {
            return "정상퇴근"; // On time
        } else if ("working".equalsIgnoreCase(checkOutStatus)) {
            return "근무중"; // toggled on 
        }
        return checkInStatus; // Default to the original value if no match
        */
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