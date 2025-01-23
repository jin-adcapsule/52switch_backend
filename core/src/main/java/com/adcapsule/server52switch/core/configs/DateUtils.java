package com.adcapsule.server52switch.core.configs;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class DateUtils {
    private static final TimeZone KR_TIMEZONE = TimeZone.getTimeZone("Asia/Seoul");
    private static final int hourDayStart = 3;
     
    public static String getyyyymmddStringNow() { // Method to get the current date formatted in a specific time zone (KST)
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Format for date comparison
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul")); // Set to KST time zone
        return dateFormat.format(new Date());
    }
    public static long parsehhmmStringToLong(String timeString) {

        try {
           // Get today's date at midnight (00:00:00) and combine it with parsed time
            LocalTime parsedTime = LocalTime.parse(timeString); // Parse the time (HH:mm)

            // Get today's date and combine it with the parsed time
            LocalDate today = LocalDate.now();

            // Combine today's date with the parsed time and set the time zone to KST
            Date combinedDate = Date.from(today.atTime(parsedTime)
            .atZone(ZoneId.of("Asia/Seoul"))  // Set to Seoul's time zone
            .toInstant());

            return combinedDate.getTime();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse time", e);
        }
    }
    public static long getLongDateNow(){return System.currentTimeMillis();}
    // Get the start of the day (3:00 AM)
    public static Date getCustomDayStart(Date date) {
        Calendar calendar = Calendar.getInstance(KR_TIMEZONE);
        calendar.setTime(date);

        // Set the time to 3:00 AM on the given date
        calendar.set(Calendar.HOUR_OF_DAY, 3);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    // Get the end of the day (2:59 AM the next day)
    public static Date getCustomDayEnd(Date date) {
        Calendar calendar = Calendar.getInstance(KR_TIMEZONE);
        calendar.setTime(date);

        // Set the time to 2:59 AM on the next day
        calendar.add(Calendar.DAY_OF_MONTH, 1); // Move to the next day
        calendar.set(Calendar.HOUR_OF_DAY, 2);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        return calendar.getTime();
    }
    /**
     * Convert a long timestamp to a custom date string with a custom day start hour.
     * @param timestamp The long timestamp to be converted.
     * @return A formatted date string (yyyy-MM-dd) adjusted to the custom day start.
     */
    public static String parseLongToCustomDate(Long timestamp) {
        // Check if the timestamp is null
        if (timestamp == null) {return null;}
        // Define the date format (yyyy-MM-dd)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

        // Create a Calendar instance and set the timestamp
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
        calendar.setTimeInMillis(timestamp);

        // Adjust for the custom day start
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        if (hourOfDay < hourDayStart) {
            // If the current time is before the custom day start, move back one day
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }

        // Reset the time to the custom day start
        calendar.set(Calendar.HOUR_OF_DAY, hourDayStart);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // Format and return the adjusted date
        return sdf.format(calendar.getTime());
    }
/**
     * Convert a long timestamp to a custom time string (hh:mm) considering the custom day start.
     * @param timestamp The long timestamp to be converted.
     * @return A formatted time string (hh:mm) adjusted to the custom day start.
     */
    public static String parseLongToCustomTime(Long timestamp) {
        // Check if the timestamp is null
        if (timestamp == null) {return null;}
        // Create a Calendar instance and set the timestamp
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
        calendar.setTimeInMillis(timestamp);

        // Get the hour and minute
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

       // Adjust the hour to the custom day start logic
        int customHour;
        if (hourOfDay < hourDayStart) {
            // Before the custom day start: map to "previous day" extended hours
            customHour = hourOfDay + 24;
        } else {
            // After the custom day start: no adjustment needed
            customHour = hourOfDay;
        }
        // Return the custom time in hh:mm format
        return String.format("%02d:%02d", customHour, minute);
    }
    public static boolean isValidyyyymmddString(String dateStr) {
        // Define the expected date format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);  // Make the parser strict (no leniency for invalid dates)
        try {
            // Try parsing the string into a Date object
            sdf.parse(dateStr);
            return true;  // Return true if parsing is successful
        } catch (ParseException e) {
            return false;  // Return false if parsing fails (invalid date)
        }
    }
    public static Integer parsehhmmStringToMinutes(String time){
        if (isValidhhmmString(time)){
            return (Integer.parseInt(time.substring(0, 2)) * 60) + Integer.parseInt(time.substring(3, 5));
        }else{return null;}
    }
    // Validate if the time is in "hh:mm" format and is not null
    public static boolean isValidhhmmString(String time) {
        // Check if the time is not null and matches the "hh:mm" pattern
        if (time == null || !time.matches("^([01]?[0-9]|2[0-3]):([0-5]?[0-9])$")) {
            return false;
        }
        
        // Extract hours and minutes
        int hour = Integer.parseInt(time.substring(0, 2));
        int minute = Integer.parseInt(time.substring(3, 5));

        // Validate hours and minutes within the allowed ranges
        return (hour >= 0 && hour <= 23) && (minute >= 0 && minute <= 59);
    }
    // Convert Date to long (milliseconds) considering KR timezone
    public static long parseDatetoLong(Date date) {
        Calendar calendar = Calendar.getInstance(KR_TIMEZONE);
        calendar.setTime(date);
        return calendar.getTimeInMillis();
    }

    // Convert long (milliseconds) to Date considering KR timezone
    public static Date parseLongToDate(long timestamp) {
        Calendar calendar = Calendar.getInstance(KR_TIMEZONE);
        calendar.setTimeInMillis(timestamp);
        return calendar.getTime();
    }

    // Convert Date to String in yyyy-MM-dd format considering KR timezone
    public static String parseDateToyyyymmddString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(KR_TIMEZONE);
        return sdf.format(date);
    }

    // Convert String (yyyy-MM-dd) to Date considering KR timezone
    public static Date parseyyyymmddStringToDate(String dateStr) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(KR_TIMEZONE);
        return sdf.parse(dateStr);
    }

    // Convert Date to String in hh:mm:ss format considering KR timezone
    public static String parseDateTohhmmssString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(KR_TIMEZONE);
        return sdf.format(date);
    }

    // Convert String (hh:mm:ss) to Date considering KR timezone
    public static Date parsehhmmssStringToDate(String timeStr) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(KR_TIMEZONE);
        return sdf.parse(timeStr);
    }

    // Convert Date to String in yyyy-MM-dd HH:mm:ss format considering KR timezone
    public static String parseDateToyyyymmddhhmmssString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(KR_TIMEZONE);
        return sdf.format(date);
    }

    // Convert String (yyyy-MM-dd HH:mm:ss) to Date considering KR timezone
    public static Date parseyyyymmddhhmmssStringToDate(String dateTimeStr) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(KR_TIMEZONE);
        return sdf.parse(dateTimeStr);
    }


    
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
    public static List<String> getWeekdaysBetween(LocalDate startDate, LocalDate endDate) {
        return Stream.iterate(startDate, date -> date.plusDays(1))
                .limit(startDate.until(endDate).getDays() + 1)
                .filter(date -> {
                    // Exclude weekends (Saturday and Sunday)
                    DayOfWeek dayOfWeek = date.getDayOfWeek();
                    return dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY;
                })
                .map(LocalDate::toString)
                .collect(Collectors.toList());
    }
    public static boolean isTodayWeekend() {
        // Get today's date
        LocalDate today = LocalDate.now();

        // Get the day of the week
        DayOfWeek dayOfWeek = today.getDayOfWeek();

        // Check if the day is Saturday or Sunday
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }
    public static long getDurationHoursBetweenNowAndDate(Date dateToCompare){
        // Convert Date to Instant for comparison
        Instant dateToCompareInstant = dateToCompare.toInstant();
        Instant nowInstant = Instant.now();
        Duration duration = Duration.between(dateToCompareInstant,nowInstant);
        return duration.toHours();
    } 
}
