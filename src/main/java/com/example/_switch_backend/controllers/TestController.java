package com.example._switch_backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example._switch_backend.models.Attendance;
import com.example._switch_backend.repositories.AttendanceRepository;

@RestController
public class TestController {
    @Autowired
    private AttendanceRepository attendanceRepository;
    @GetMapping("/test-attendance")
    public List<Attendance> testAttendance() {
        return attendanceRepository.findByEmployeeId(1);
    }
}
