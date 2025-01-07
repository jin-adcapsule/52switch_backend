package com.adcapsule.server52switch.core.resolvers;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.adcapsule.server52switch.core.dtos.RequestDTO;
import com.adcapsule.server52switch.core.services.SupervisorService;


@Controller
public class SupervisorResolver {

    private final SupervisorService supervisorService;// Use the service instance

    @Autowired
    public SupervisorResolver( SupervisorService supervisorService){
        this.supervisorService = supervisorService;
        //this.subscriptionPublisher = subscriptionPublisher;
    }
    // Query to fetch pending Requests
    @QueryMapping
    public List<RequestDTO> getPendingRequests(
        @Argument String employeeOid
    ) {
        String pendingStatusText="대기중";
        return supervisorService.getPendingRequests(employeeOid,pendingStatusText);
    }
    // Mutation answering to request
    @MutationMapping
    public Boolean answerRequest(
        @Argument String employeeOid,
        @Argument String requestStatus, 
        @Argument String answerComment, 
        @Argument String requestKey
    ) {
        return supervisorService.answerRequest(employeeOid,requestStatus,answerComment,requestKey);
    }
    
    // Query to fetch request history
    @QueryMapping
    public List<RequestDTO> getRequestHistory(
        @Argument String employeeOid,
        @Argument String startDate,
        @Argument String endDate,
        @Argument List<String> requestStatusList
        ) {

            return supervisorService.getRequestHistory(employeeOid,startDate,endDate,requestStatusList);
        }




}