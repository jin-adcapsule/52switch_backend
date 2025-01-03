package com.adcapsule.server52switch.backoffice.resolvers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Component;

import com.adcapsule.server52switch.backoffice.services.BackOfficeService;
import com.adcapsule.server52switch.core.models.Employee;
import com.adcapsule.server52switch.core.models.Group;
@Component
public class BackOfficeResolver {

    private final BackOfficeService backOfficeService;

    @Autowired
    public BackOfficeResolver(BackOfficeService backOfficeService) {
        this.backOfficeService = backOfficeService;
    }

    @QueryMapping
    public List<Group> getMyAllGroups(@Argument String employeeOid) {
        return backOfficeService.getMyAllGroups(employeeOid);
    }
    @QueryMapping
    public Map<String,List<Employee>> getMyAllGroupsMembers(@Argument String employeeOid) {
        return backOfficeService.getMyAllGroupsMembers(employeeOid);
    }
}
