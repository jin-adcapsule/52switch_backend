package com.example._switch_backend.controllers;

import org.springframework.stereotype.Component;

import com.example._switch_backend.models.Attendance;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
public class AttendanceSubscriptionPublisher {

    private final Sinks.Many<Attendance> sink = Sinks.many().multicast().onBackpressureBuffer();

    public void publish(Attendance attendance) {
        sink.tryEmitNext(attendance);
    }

    public Flux<Attendance> getPublisher() {
        return sink.asFlux();
    }
}