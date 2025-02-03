package com.adcapsule.server52switch.backoffice.configs;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BackConstants {
    public static final List<Map<String, Object>> collectionConfig = Arrays.asList(
        new HashMap<String, Object>() {{
            put("collectionName", "Employee");
            put("idxKey", "employeeOid");
            put("idxShowKey", "name");
        }},
        new HashMap<String, Object>() {{
            put("collectionName", "Attendance");
            put("idxKey", "attendanceId");
            put("idxShowKey", "attendanceId");
        }},
        new HashMap<String, Object>() {{
            put("collectionName", "Dayoff");
            put("idxKey", "dayoffId");
            put("idxShowKey", "dayoffId");
           
        }},
        new HashMap<String, Object>() {{
            put("collectionName", "Group");
            put("idxKey", "groupId");
            put("idxShowKey", "groupName");
        }},
        new HashMap<String, Object>() {{
            put("collectionName", "Location");
            put("idxKey", "locationId");
            put("idxShowKey", "workplace");
        }}
    );

}
