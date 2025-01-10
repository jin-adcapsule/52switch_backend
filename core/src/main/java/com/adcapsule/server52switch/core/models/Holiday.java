package com.adcapsule.server52switch.core.models;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "holiday")
public class Holiday {

    @Id
    private String id;
    private int year;
    private Date updatedAt;
    private List<HolidayItem> holidayList;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<HolidayItem> getHolidayList() {
        return holidayList;
    }

    public void setHolidayList(List<HolidayItem> holidayList) {
        this.holidayList = holidayList;
    }

    public static class HolidayItem {
        private String holidayName;
        private boolean isHoliday;
        private String holidayDate;

        // Getters and Setters
        public String getHolidayName() {
            return holidayName;
        }

        public void setHolidayName(String holidayName) {
            this.holidayName = holidayName;
        }

        public boolean getIsHoliday() {
            return isHoliday;
        }

        public void setIsHoliday(boolean isHoliday) {
            this.isHoliday = isHoliday;
        }

        public String getHolidayDate() {
            return holidayDate;
        }

        public void setHolidayDate(String holidayDate) {
            this.holidayDate = holidayDate;
        }
    }
}
