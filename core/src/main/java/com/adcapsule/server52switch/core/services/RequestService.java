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
    public List<Map<String,String>> getRequestByTodayAndApprovedStatus(String employeeOid){

        String currentDateInKST = Config.getCurrentDate_String();
        String statusApproved="approved";//String statusApproved = Config.requestStatusToTextMap.get("approved");
        // Fetch dayoff records based on filters
        System.out.println(statusApproved);
        System.out.println(currentDateInKST);
        List<String> dayoffTypes = dayoffService.findDayoffTypeByEmployeeOidAndRequestStatusAndDate(employeeOid, statusApproved, currentDateInKST);    
        System.out.println("dayoffTypes");
        System.out.println(dayoffTypes);
        List<Map<String,String>> dayoffKeyMapList = Config.getWorkTypeAndWorkTimeToday(dayoffTypes);//workhourStart,workhourEnd,key as dayoffTypeValue
        return dayoffKeyMapList ;
    }

    public List<Map<String,String>> getRequestByWorkTypeInAndApprovedStatusAndDate(String employeeOid,List<String> workTypeQueryList,String dateString){
        String statusApproved="approved";// String statusApproved = Config.requestStatusToTextMap.get("approved");
        // Fetch dayoff records based on filters
        List<String> dayoffTypes = dayoffService.findDayoffTypeByEmployeeOidAndWorkTypeInAndRequestStatusAndDate(employeeOid,workTypeQueryList, statusApproved, dateString);    
        List<Map<String,String>> dayoffKeyMapList = Config.getWorkTypeAndWorkTimeToday(dayoffTypes);//workhourStart,workhourEnd,key as dayoffTypeValue
        return dayoffKeyMapList ;
    }
    
}
