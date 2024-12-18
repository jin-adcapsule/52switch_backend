package com.example._switch_backend.models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import com.example._switch_backend.config.DateUtils;


public class DayoffHistory {
    private int employeeId;
    private String dayoffType;
    private String requestComment;
    private String dayoffDate;
    private String requestStatus;
    private String requestDate;
    private String dayoffDateText;
    private String requestKey;
    private int supervisorId;
    private int beforeDateRemaining;
    private List<String> dayoffDates;

    public DayoffHistory(
        int employeeId,
        String requestDate,
        List<String> dayoffDates,
        String dayoffType,
        String requestKey,
        String requestStatus,
        int supervisorId,
        String requestComment
        ) {
        this.employeeId = employeeId;
        this.requestDate = formatDate(requestDate);
        this.dayoffDates= dayoffDates;
        this.dayoffDateText = getDayoffDateListToText(dayoffDates);
        this.dayoffType = dayoffType;
        this.requestKey = requestKey;
        this.supervisorId = supervisorId;
        this.requestStatus = requestStatus;
        this.requestComment= requestComment;

    }
    private String getDayoffDateListToText(List<String> dayoffDateList){
        if (dayoffDateList == null || dayoffDateList.isEmpty()) {
            return ""; // Handle empty or null input gracefully
        }
        String text;
        int size = dayoffDateList.size();
         // Single date case
        if (size == 1) {
            text = formatDate_E(dayoffDateList.get(0));
        } 
        // Continuous dates case
        else if (DateUtils.areDatesConsecutive(dayoffDateList)) {
            text = formatDate_E(dayoffDateList.get(0)) + " ~ " + formatDate_E(dayoffDateList.get(size - 1));
        } 
        // Two or three dates case
        else if (size <= 3) {
            // Format and join all dates
            text = dayoffDateList.stream()
                    .map(this::formatDate_E) // Apply formatting to each date
                    .collect(Collectors.joining(", "));
        } 
        // More than three non-continuous dates case
        else {
            text = formatDate_E(dayoffDateList.get(0)) + "외 "+(size-1) +'일';
        }
        return text;
    } 
    private String formatDate_E(String date) {
        // Convert string date to LocalDate
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // Format date as yy.mm.dd(요일)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd(E)", java.util.Locale.KOREAN);
        return localDate.format(formatter);
    }
    private String formatDate(String date) {
        // Convert string date to LocalDate
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // Format date as yy.mm.dd
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd", java.util.Locale.KOREAN);
        return localDate.format(formatter);
    }
    
    // Getters and Setters
    public int getEmployeeId() {
        return employeeId;
    }

    public List<String> getDayoffDates() {
        return dayoffDates;
    }

    public String getDayoffType() {
        return dayoffType;
    }

    public String getRequestComment() {
        return requestComment;
    }

    public String getRequestStatus() {
        return requestStatus;
    }
    public String getRequestDate() {
        return requestDate;
    }
    public String getRequestKey() {
        return requestKey;
    }
    public int getSupervisorId() {
        return supervisorId;
    }
   

}