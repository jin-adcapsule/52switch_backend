package com.adcapsule.server52switch.backoffice.resolvers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import com.adacpsule.server52switch.shared.inputs.DayoffInput;
import com.adcapsule.server52switch.backoffice.services.BackDayoffService;

@Controller
public class BackDayoffResolver {
    @Autowired
    private BackDayoffService backDayoffService;
    @MutationMapping
    public String updateOrNewDayoff(@Argument String id, @Argument DayoffInput input) {
        if (id==null){
            return backDayoffService.genNewDayoff(input);
        } else {
        // Call service to update the employee record
            return backDayoffService.updateDayoff(id,input);
        }
    }
    @MutationMapping
    public Boolean deleteDayoffById(@Argument String id) {
        return backDayoffService.deleteDayoffById(id);
    }
}
