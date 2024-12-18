package com.example._switch_backend.repositories.projection;

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

}
