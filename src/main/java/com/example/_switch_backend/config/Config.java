package com.example._switch_backend.config;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.stream.Collectors;

public class Config {

  public static final Map<String,String> requestStatusTextToValueMap= new HashMap<String, String>()
    {
        {

            put("승인", "approved");
            put("거절", "denied");
            put("대기중", "pending");
        }
    }; 
  public static Map<String,String> requestStatusToTextMap= invertMapUsingStreams(requestStatusTextToValueMap);
  public static final Map<String,String> workTypeTextToValueMap= new HashMap<String, String>()
    {
        {

            put("정상근무", "workFull");
            put("오전반차", "dayoffFirstHalf");
            put("오후반차", "dayoffSecondHalf");
            put("휴가", "dayoffFull");
            put("경조휴가", "dayoffExtra");
            put("휴직", "dayoffLOA");

            put("지각", "lateArrival");
            put("정상출근", "onTimeArrival");
            put("조기퇴근", "earlyLeft");
            put("정상퇴근", "onTimeLeft");
            put("근무중", "working");

        }
    };   
  public static Map<String,String> workTypeToTextMap= invertMapUsingStreams(workTypeTextToValueMap);

  public static final Map<String, Map<String, String>> workTypeToLocationKeyMap = Map.of(
        "workFull", Map.of("workhourStart", "workhourOn", "workhourEnd", "workhourOff"),
        "dayoffFirstHalf", Map.of("workhourStart", "workhourOn", "workhourEnd", "workhourHalf"),
        "dayoffSecondHalf", Map.of("workhourStart", "workhourHalf", "workhourEnd", "workhourOff"),
        "dayoffFull", Map.of("workhourStart", "null", "workhourEnd", "null"),
        "dayoffExtra", Map.of("workhourStart", "null", "workhourEnd", "null"),
        "dayoffLOA", Map.of("workhourStart", "null", "workhourEnd", "null")
    );



    
  // get inversed map
  public static <V, K> Map<V, K> invertMapUsingStreams(Map<K, V> map) {
      Map<V, K> inversedMap = map.entrySet()
          .stream()
          .collect(Collectors.toMap(Entry::getValue, Entry::getKey));
      return inversedMap;
  }
  //Method to get the generated uniquekey combined with timestamp
  public static String generateUniqueKey(String option) {

    long timestamp = System.currentTimeMillis();
    SecureRandom random = new SecureRandom();
    byte[] randomBytes = new byte[16];
    random.nextBytes(randomBytes);
    String randomPart = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    switch (option){
      case "random":
        return randomPart;
      case "timestamp":
        return String.valueOf(timestamp);
      case "default":
      default: // Handles unexpected input
        return timestamp + "-" + randomPart; 
    }
    
  }
  // Method to get the current date formatted in a specific time zone (KST)
  public static String getCurrentDate_String() {
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Format for date comparison
      dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul")); // Set to KST time zone

      // Format the current date and return as a string
      return dateFormat.format(new Date());
  }

// Method to format a given Date to KST with a specific format
  public static Date getCheckTime_Date_Date(Date checkTime) {
      if (checkTime != null) {
          ZonedDateTime kstTime = checkTime.toInstant().atZone(ZoneId.of("Asia/Seoul"));
          return Date.from(kstTime.toInstant()); // Convert ZonedDateTime back to Date and return it
      }
      return null;// Return null if checkInTime is null
  }
  // Method convert date to string in kst
  public static String getCheckTime_Date_String(Date checkTime) {
    if (checkTime != null) {
        ZonedDateTime kstTime = checkTime.toInstant().atZone(ZoneId.of("Asia/Seoul"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        return kstTime.format(formatter);// Format and return the string    
    }
    return null;// Return null if checkInTime is null
    }
  // Method convert date to string in kst
  public static String getCheckTime_HHmm_String(Date checkTime) {
    if (checkTime != null) {
        ZonedDateTime kstTime = checkTime.toInstant().atZone(ZoneId.of("Asia/Seoul"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return kstTime.format(formatter);// Format and return the string    
    }
    return null;// Return null if checkInTime is null
    }

  // Method convert date to string in kst
  public static String gettimestamp_yyyymmddHHmmss_String(Date checkTime) {
    if (checkTime != null) {
        ZonedDateTime kstTime = checkTime.toInstant().atZone(ZoneId.of("Asia/Seoul"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return kstTime.format(formatter);// Format and return the string    
    }
    return null;// Return null if checkInTime is null
    }

  /**
   * Compares two time strings in 'hh:mm' format and returns a list of times in ascending order.
   * Null values are excluded from the list. If both time strings are null, the method returns null.
   * 
   * Use Case:
   * - Sort two time strings to determine the earlier time.
   * - Exclude null values to focus only on valid times.
   * - Handle edge cases where both inputs are null gracefully.
   * 
   * Behavior:
   * - Input: Two time strings in 'hh:mm' format or null.
   * - Output: A sorted list of valid time strings in ascending order, or null if no valid times are provided.
   * 
   * Edge Cases:
   * - If both inputs are null, return null.
   * - If one input is null, return a single-element list with the non-null time.
   * 
   * @param time1 First time string in 'hh:mm' format or null.
   * @param time2 Second time string in 'hh:mm' format or null.
   * @return A sorted list of times in ascending order, excluding nulls, or null if both inputs are null.
   */
  public static List<String> compareAndSortTimes(String time1, String time2, boolean ascending) {
      // Initialize an empty list to hold valid times
      List<String> times = new ArrayList<>();

      // Add non-null time strings to the list
      if (time1 != null) {times.add(time1);}
      if (time2 != null) {times.add(time2);}
      // If the list is empty (both times are null), return null
      if (times.isEmpty()) {return null;}
      // Sort the list in ascending or descending order based on the boolean flag
      if (ascending) {
          Collections.sort(times);
      } else {
          Collections.sort(times, Collections.reverseOrder());
      }
      // Return the sorted list
      return times;
    }
  //MappingMethod for dayofftype to StartTime and EndTime
  public static List<Map<String,String>> getWorkTypeAndWorkTimeToday(List<String> dayoffTypes){
      List<String> workTypesToday = new ArrayList<>(dayoffTypes); // Create a copy of the provided dayoffTypes
      if(workTypesToday.isEmpty()){workTypesToday.add("정상근무");}//if no dayoff then
      List<Map<String, String>> WorkhourKeyMapLists= new ArrayList<>();
      for (String workType : workTypesToday) {
          String workTypeValue=workTypeTextToValueMap.get(workType);
          Map<String,String> locationKeysetforWorkType = workTypeToLocationKeyMap.get(workTypeValue);
          // Create a new map to avoid modifying the original map
          Map<String, String> newMap = new HashMap<>(locationKeysetforWorkType);
          // Add the key-value pair
          newMap.put("key", workTypeValue);
      
          WorkhourKeyMapLists.add(newMap);
      }
    return WorkhourKeyMapLists;
  }
  
}
