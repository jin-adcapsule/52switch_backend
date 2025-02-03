package com.adcapsule.server52switch.core.resolvers;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.adcapsule.server52switch.core.configs.Config;
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
        String pendingStatus="pending";
        return supervisorService.getPendingRequests(employeeOid,pendingStatus);
    }
    // Mutation answering to request
    @MutationMapping
    public Boolean answerRequest(
        @Argument String employeeOid,
        @Argument String requestStatus, 
        @Argument String answerComment, 
        @Argument String requestKey
    ) {
        String requestStatusValue=Config.requestStatusTextToValueMap.get(requestStatus);
        return supervisorService.answerRequest(employeeOid,requestStatusValue,answerComment,requestKey);
    }
    
    // Query to fetch request history
    @QueryMapping
    public List<RequestDTO> getRequestHistory(
        @Argument String employeeOid,
        @Argument String startDate,
        @Argument String endDate,
        @Argument List<String> requestStatusList
        ) {
            // Convert the requestStatusList using the map
            List<String> mappedRequestStatusList = requestStatusList.stream()
                .map(status -> Config.requestStatusTextToValueMap.getOrDefault(status, status)) // Map the text to value or keep it as is
                .collect(Collectors.toList());
            return supervisorService.getRequestHistory(employeeOid,startDate,endDate,mappedRequestStatusList);
        }




}