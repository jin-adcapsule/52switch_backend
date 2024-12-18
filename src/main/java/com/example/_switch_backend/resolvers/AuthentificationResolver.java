package com.example._switch_backend.resolvers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.example._switch_backend.services.AuthentificationService;

@Controller
public class AuthentificationResolver {
    @Autowired
    private AuthentificationService authentificationService;
   
    @QueryMapping
    public Map<String, Object>  validateUidAndPhone(@Argument String uid, @Argument String phone) {
        return authentificationService.validateUidAndPhone(uid, phone);
    }
    
}
