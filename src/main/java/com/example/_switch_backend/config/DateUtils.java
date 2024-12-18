package com.example._switch_backend.config;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class DateUtils {

    //input datestring expected as yyyy-mm-dd and ascending sorted
    public static boolean areDatesConsecutive(List<String> sortedDates) {
        // Return true if the list is empty or has only one date
        if (sortedDates == null || sortedDates.size() <= 1) {
            return true;
        }

        // Parse the date strings into LocalDate objects
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<LocalDate> dateList = sortedDates.stream()
                .map(date -> LocalDate.parse(date, formatter))
                .toList();

        // Check if the difference between consecutive dates is always 1 day
        for (int i = 1; i < dateList.size(); i++) {
            if (!dateList.get(i - 1).plusDays(1).equals(dateList.get(i))) {
                return false; // Dates are not continuous
            }
        }

        return true; // All dates are continuous
    }

    public static String getDateListToText(List<String> DateList){
        if (DateList == null || DateList.isEmpty()) {
            return ""; // Handle empty or null input gracefully
        }
        String text;
        int size = DateList.size();
         // Single date case
        if (size == 1) {
            text = formatDate_E(DateList.get(0));
        } 
        // Continuous dates case
        else if (DateUtils.areDatesConsecutive(DateList)) {
            text = formatDate_E(DateList.get(0)) + " ~ " + formatDate_E(DateList.get(size - 1));
        } 
        // Two or three dates case
        else if (size <= 3) {
            // Format and join all dates
            text = DateList.stream()
                    .map(DateUtils::formatDate_E) // Apply formatting to each date
                    .collect(Collectors.joining(", "));
        } 
        // More than three non-continuous dates case
        else {
            text = formatDate_E(DateList.get(0)) + "외 "+(size-1) +'일';
        }
        return text;
    } 
    public static String formatDate_E(String date) {
        // Convert string date to LocalDate
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // Format date as yy.mm.dd(요일)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd(E)", java.util.Locale.KOREAN);
        return localDate.format(formatter);
    }
}
