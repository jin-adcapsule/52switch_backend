package com.adcapsule.server52switch.core.resolvers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import com.adcapsule.server52switch.core.services.CredentialService;


@Controller
public class CredentialResolver {
    @Autowired
    private CredentialService credentialService;


    @MutationMapping
    public Boolean  saveFCMToken(@Argument String objectId, @Argument String fcmToken) {
        return credentialService.saveFCMToken(objectId, fcmToken);
    }
    
}
