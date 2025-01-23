package com.adcapsule.server52switch.core.services;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.adcapsule.server52switch.core.configs.Config;
import com.adcapsule.server52switch.core.configs.DateUtils;
import com.adcapsule.server52switch.core.dtos.AttendanceStatusAndDetailsDTO;
import com.adcapsule.server52switch.core.models.Employee;
import com.adcapsule.server52switch.core.models.Location;

@Service
@EnableScheduling
public class AttendanceCheckScheduler {

    private final AttendanceService attendanceService; 
    
    private final EmployeeService employeeService;
    private final NotificationService notificationService;


    @Autowired
    public AttendanceCheckScheduler(AttendanceService attendanceService, EmployeeService employeeService,NotificationService notificationService) {
        this.attendanceService = attendanceService;
        this.employeeService = employeeService;
        this.notificationService = notificationService;
    }
    
   

    public List<Long> generateScheduledTimesForToday() {
        List<Location> locations = employeeService.getAllLocations();
        Set<Long> scheduledTimes = new LinkedHashSet<>();  // Use a LinkedHashSet to maintain order and uniqueness
        int alertNotiMinutes = Config.alertNotiMinutes;
        int lateNotiMinutes = Config.lateNotiMinutes;
        for (Location location : locations){
            // Parse the times (assuming workhourOn, workhourHalf, workhourOff are strings like "08:00" or "12:00")
            long workhourOnTime = DateUtils.parsehhmmStringToLong(location.getWorkhourOn());
            long workhourHalfTime = DateUtils.parsehhmmStringToLong(location.getWorkhourHalf());
            //long workhourOffTime = DateUtils.parsehhmmStringToLong(location.getWorkhourOff());
            
            // Toggle check: subtract minutes buffer for early toggle check
            scheduledTimes.add(workhourOnTime + alertNotiMinutes * 60 * 1000);  // minutes before workhourOn
            scheduledTimes.add(workhourHalfTime + alertNotiMinutes * 60 * 1000);  // minutes before workhourHalf
            //scheduledTimes.add(workhourOffTime + alertNotiMinutes * 60 * 1000);  // minutes before workhourOff
            // Late check: Add minutes buffer for each time to avoid checking right at the exact time
            scheduledTimes.add(workhourOnTime + lateNotiMinutes * 60 * 1000);  // minutes after workhourOn
            scheduledTimes.add(workhourHalfTime + lateNotiMinutes * 60 * 1000);  // minutes after workhourHalf
            //scheduledTimes.add(workhourOffTime + lateNotiMinutes * 60 * 1000);  // minutes after workhourOff
        }
        // Convert Set back to List if needed (optional)
        List<Long> scheduledTimesList = new ArrayList<>(scheduledTimes);
    
        return scheduledTimesList;
    }
    @Scheduled(cron = Config.tickTime)  // Run every day at certain time
    public void checkEmployeeAttendanceForToday() {

        // Initialize a scheduled executor service
        var scheduler = Executors.newSingleThreadScheduledExecutor();
        // Map to store references to scheduled tasks by scheduled time (this avoids duplicates)
        Map<Long, ScheduledFuture<?>> scheduledTasks = new HashMap<>();

        List<Long> scheduledTimes = generateScheduledTimesForToday();
        // Iterate over each scheduled time and schedule the task if not already scheduled
        for (Long scheduledTime : scheduledTimes) {
           // If the task for this time is already scheduled, skip it
            if (!scheduledTasks.containsKey(scheduledTime)) {
                long delay = scheduledTime - System.currentTimeMillis();  // Calculate delay from now
                if (delay > 0) {
                    // Schedule the task to run at scheduledTime and store the reference
                    ScheduledFuture<?> task = scheduler.schedule(() -> checkAttendanceForScheduledTime(scheduledTime), delay, TimeUnit.MILLISECONDS);
                    scheduledTasks.put(scheduledTime, task);  // Track the task by scheduled time
                }
            }
        }
        System.out.println("Scheduled times list:");
        System.out.println(scheduledTasks);
    }
    /**
     * daily noti hours.
     */
    @PostConstruct
    public void initializeSetNotiTimes() {
        // Logic to schedule Noti Times
        System.out.println("Setting Notification Times...");
        checkEmployeeAttendanceForToday();
    }
    public void checkAttendanceForScheduledTime(Long scheduledTime) {
        // Fetch all employees' attendance for the day
        List<Employee> employees = employeeService.findAll();
        int alertNotiMinutes = Config.alertNotiMinutes;
        int lateNotiMinutes = Config.lateNotiMinutes;
        for (Employee employee : employees) {
            String employeeOid = employee.getId();
            String employeeName = employee.getName();
            AttendanceStatusAndDetailsDTO attendanceDetails = attendanceService.getAttendanceStatusAndDetails(employeeOid);

            // Check if the employee should check in today
            if (attendanceDetails != null && !"".equals(attendanceDetails.getStartTime())) {//*****************************handling null case as empty string*/
                // Skip the employee if they already checked in (status is true)
                if (attendanceDetails.getStatus() ==null||attendanceDetails.getStatus()) {//*****************************status null is isTodayWeekend  isTodayFullDayoff isTodayHoliday
                    continue;  // Employee has already checked in, no need to notify
                }
                long expectedCheckInTime = DateUtils.parsehhmmStringToLong(attendanceDetails.getStartTime());
                // Check if the scheduled time falls within 30 minutes of the expected check-in time
                if (scheduledTime >= expectedCheckInTime +(lateNotiMinutes-1) * 60 * 1000 && scheduledTime <= expectedCheckInTime + (lateNotiMinutes+1) * 60 * 1000) {
                    notificationService.sendNotificationToEmployeeOid(employeeOid,lateNotiMinutes+"분 지각 중입니다.","출근 확인 해주세요.","attendance");
                    notificationService.sendNotificationToSupervisor(employeeOid,employeeName+"님이 "+lateNotiMinutes+"분 지각 중입니다.","확인해주세요","supervisor");
                } else if(scheduledTime >= expectedCheckInTime + (alertNotiMinutes-1) * 60 * 1000 && scheduledTime <= expectedCheckInTime + (alertNotiMinutes+1) * 60 * 1000) {
                    notificationService.sendNotificationToEmployeeOid(employeeOid,"곧 출근 시간입니다.",alertNotiMinutes+"분 이내에 출근 확인 해주세요.","attendance");
                }
            }
        }
    }
}
