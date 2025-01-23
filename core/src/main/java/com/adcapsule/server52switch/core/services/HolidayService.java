package com.adcapsule.server52switch.core.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adcapsule.server52switch.core.models.Holiday;
import com.adcapsule.server52switch.core.repositories.HolidayRepository;
@Service
public class HolidayService {
    private final HolidayRepository holidayRepository; 
    
    @Autowired
        public HolidayService(HolidayRepository holidayRepository) {

            this.holidayRepository = holidayRepository;

            
        }
        public Holiday findRandomHoliday(){//pick first Holiday
            // Use aggregation to fetch a random document
            List<Holiday> holidays = holidayRepository.findAll(); // Fetch all holidays
    
            if (holidays.isEmpty()) {
                return null; // No holidays available
            }
            return holidays.get(0);
        }
        public List<Holiday> findHolidaysAfterOrOn(String date){
            return holidayRepository.findHolidaysAfterOrOn(date);
        }
        public List<Holiday> findHolidaysByDateRange(String startDate, String endDate){
            return holidayRepository.findHolidaysByDateRange(startDate, endDate);
        }

        public void saveHoliday(Holiday holiday){
            holidayRepository.save(holiday);
        }
        
        public Optional<Holiday> findByHolidayDate(String holidayDate){
            return holidayRepository.findByHolidayDate(holidayDate);
        }

        

}
