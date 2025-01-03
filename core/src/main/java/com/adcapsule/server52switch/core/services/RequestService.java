package com.adcapsule.server52switch.core.services;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adcapsule.server52switch.core.configs.Config;


@Service
public class RequestService {

    private final DayoffService dayoffService;
    


    @Autowired
    public RequestService(DayoffService dayoffService) {
        this.dayoffService = dayoffService;
    }


    //hard coded
    public List<Map<String,String>> getRequestByTodayAndApprovedStatus(int employeeId){

        String currentDateInKST = Config.getCurrentDate_String();
        String statusApproved = Config.requestStatusToTextMap.get("approved");
        // Fetch dayoff records based on filters
        List<String> dayoffTypes = dayoffService.findDayoffTypeByEmployeeIdAndRequestStatusAndDate(employeeId, statusApproved, currentDateInKST);    
        
        List<Map<String,String>> dayoffKeyMapList = Config.getWorkTypeAndWorkTimeToday(dayoffTypes);//workhourStart,workhourEnd,key as dayoffTypeValue
        return dayoffKeyMapList ;
    }

}
