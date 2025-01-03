package com.adcapsule.server52switch.core.repositories.projection;

public class Projection {

        public interface EmployeeIdProjection {

                Integer getEmployeeId();
        
        }
        
        public interface IdProjection {
                String getId(); // Maps to `_id` field in MongoDB
        }

        public interface DayoffTypeProjection {
        String getDayoffType(); // This maps to the "dayoffType" field in MongoDB
        }
        
        public interface GroupIdProjection {
                String getGroupId(); 
        }
        public interface StatusProjection {
                Boolean getStatus(); 
        }
        public interface LocationIdProjection {
                String getLocationId(); 
        }
        public interface NameProjection {
                String getName(); 
        }
        public interface DayoffInfoProjection {
                String getGroupId(); 
                Integer getDayoffRemaining();
        }
}
