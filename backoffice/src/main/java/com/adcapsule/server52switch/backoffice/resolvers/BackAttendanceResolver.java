package com.adcapsule.server52switch.backoffice.resolvers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import com.adacpsule.server52switch.shared.inputs.AttendanceInput;
import com.adcapsule.server52switch.backoffice.services.BackAttendanceService;
@Controller
public class BackAttendanceResolver {
    @Autowired
    private BackAttendanceService backAttendanceService;
    @MutationMapping
    public String updateAttendance(@Argument String id, @Argument AttendanceInput input) {
        // Call service to update the employee record
        return backAttendanceService.updateAttendance(id,input);
    }
}
