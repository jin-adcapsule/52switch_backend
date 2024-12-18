package com.example._switch_backend.resolvers;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.example._switch_backend.models.RequestDTO;
import com.example._switch_backend.services.SupervisorService;


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
        @Argument String objectId
    ) {
        String pendingStatusText="대기중";
        return supervisorService.getPendingRequests(objectId,pendingStatusText);
    }
    // Mutation answering to request
    @MutationMapping
    public Boolean answerRequest(
        @Argument String objectId,
        @Argument String requestStatus, 
        @Argument String answerComment, 
        @Argument String requestKey
    ) {
        return supervisorService.answerRequest(objectId,requestStatus,answerComment,requestKey);
    }
    
    // Query to fetch request history
    @QueryMapping
    public List<RequestDTO> getRequestHistory(
        @Argument String objectId,
        @Argument String startDate,
        @Argument String endDate,
        @Argument List<String> requestStatusList
        ) {

            return supervisorService.getRequestHistory(objectId,startDate,endDate,requestStatusList);
        }




}