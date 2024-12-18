package com.example._switch_backend.controllers;

import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class GraphQLController {

    // Simple Query Method
    @QueryMapping
    public String hello() {
        return "Hello, GraphQL!";
    }
}
