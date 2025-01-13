package com.adcapsule.server52switch.core.services;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.adcapsule.server52switch.core.configs.DateUtils;
import com.adcapsule.server52switch.core.configs.DotenvConfig;
import com.adcapsule.server52switch.core.models.Holiday;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ExternalApiService {

    @Value("${holiday.api.url}")
    private String apiUrl;
    private ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate;
    private final HolidayService holidayService;
    private SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyyMMdd");
    private SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd")
    ;

    public ExternalApiService(RestTemplate restTemplate, HolidayService holidayService) {
        this.restTemplate = restTemplate;
        this.holidayService = holidayService;
    }
    private String encode(String rawString){return URLEncoder.encode(rawString.trim(), StandardCharsets.UTF_8);}
    /**
     * Fetch holiday data for the given year.
     */
    public void fetchAndStoreHolidayData(int year) {
        try { // Load encoding key from DotenvConfig
            String rawencodingKey=DotenvConfig.get("HOLIDAY_API_KEY").trim();
            String encodedKey = encode(rawencodingKey);
                    // Build URL with query parameters, ensuring the serviceKey is URL-encoded
            URI uri = UriComponentsBuilder.fromHttpUrl(apiUrl)
                    .path("/getRestDeInfo")
                    .queryParam("numOfRows", String.valueOf(200))
                    .queryParam("solYear", String.valueOf(year))
                    .queryParam("ServiceKey", rawencodingKey)  // Encoding is handled here
                    .build()
                    .toUri(); // Convert the builder to URI
                    
            // Set the headers to accept JSON response
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "application/json"); // Request JSON response

            // Prepare the request entity with headers
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            // Make the GET request with the headers and get response body
            //String response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class).getBody();
            // Fetch the API response
            String response = restTemplate.getForObject(uri, String.class);
            // Parse and process the API response
            List<Holiday> holidays = parseApiResponse(response, year);

            // Save each holiday as a separate document
            for (Holiday holiday : holidays) {
                holiday.setUpdatedAt(new Date());
                // Check if a holiday with the same holidayDate already exists
                Optional<Holiday> existingHoliday = holidayService.findByHolidayDate(holiday.getHolidayDate());

                if (existingHoliday.isPresent()) {
                    // Update existing holiday
                    Holiday existing = existingHoliday.get();
                    existing.setHolidayName(holiday.getHolidayName());
                    existing.setIsHoliday(holiday.getIsHoliday());
                    existing.setUpdatedAt(new Date()); // Set the updated date
                    holidayService.saveHoliday(existing);
                } else {
                    // Save as a new holiday
                    holidayService.saveHoliday(holiday);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Parse the API response and extract holiday items.
     */
    private List<Holiday> parseApiResponse(String response, int year) {
        List<Holiday> holidays = new ArrayList<>();
        try {
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode itemsNode = rootNode.path("response").path("body").path("items").path("item");

            // Add a default holiday (e.g., Labor Day)
            Holiday defaultHoliday = new Holiday();
            defaultHoliday.setHolidayName("근로자의 날");
            defaultHoliday.setHolidayDate(year + "-05-01");
            defaultHoliday.setIsHoliday(true);
            holidays.add(defaultHoliday);

            // Parse the rest of the holidays
            if (itemsNode.isArray()) {
                for (JsonNode item : itemsNode) {
                    String dateName = item.path("dateName").asText();
                    boolean isHoliday = "Y".equals(item.path("isHoliday").asText());
                    String locdate = item.path("locdate").asText();
                    String formattedDate = formatDate(locdate);

                    Holiday holiday = new Holiday();
                    holiday.setHolidayName(dateName);
                    holiday.setHolidayDate(formattedDate);
                    holiday.setIsHoliday(isHoliday);

                    holidays.add(holiday);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return holidays;
    }

    // Helper method to format the locdate to 'yyyy-MM-dd' format
    private String formatDate(String locdate) {
        try {
            Date date = inputDateFormat.parse(locdate);  // Parse the date in yyyyMMdd format
            return outputDateFormat.format(date);        // Return the formatted date in yyyy-MM-dd
        } catch (Exception e) {
            e.printStackTrace();
            return locdate; // If there's an error, return the original locdate
        }
    }
    /**
     * Scheduled task to fetch and store holiday data for this year and next year.
     */
    @Scheduled(cron = "0 0 0 * * ?") // Runs daily at midnight
    public void scheduleHolidayFetch() {
        int currentYear = java.time.Year.now().getValue();
        fetchAndStoreHolidayData(currentYear - 1);
        fetchAndStoreHolidayData(currentYear);
        fetchAndStoreHolidayData(currentYear + 1);
    }
    /**
     * Fetch holiday data when the server starts.
     */
    @PostConstruct
    public void initializeHolidayFetch() {
        if (shouldSkipHolidayFetch()) {
            System.out.println("Holiday fetch skipped: Last update was less than 23 hours ago.");
            return;
        }
        // Logic to schedule or perform the holiday fetch
        System.out.println("Fetching and updating holiday data...");
        scheduleHolidayFetch();
    }
    private boolean shouldSkipHolidayFetch() {
        // Fetch the random holiday document
        Holiday randomHoliday = holidayService.findRandomHoliday(); // Custom repository method
        
        if (randomHoliday == null) {
            System.out.println("No holiday document found. Proceeding with fetch.");
            return false; // No document found; fetch should proceed
        }

        Date updatedAt = randomHoliday.getUpdatedAt();
        if (updatedAt == null) {
            System.out.println("Holiday document missing updatedAt. Proceeding with fetch.");
            return false; // Missing timestamp; fetch should proceed
        }

        // Calculate the duration since the last update
        
        return DateUtils.getDurationHoursBetweenNowAndDate(updatedAt) < 23; // Skip fetch if less than 23 hours have passed
    }

}
