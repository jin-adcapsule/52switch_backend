package com.adcapsule.server52switch.core.services;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.adcapsule.server52switch.core.configs.DotenvConfig;
import com.adcapsule.server52switch.core.models.Holiday;
import com.adcapsule.server52switch.core.models.Holiday.HolidayItem;
import com.adcapsule.server52switch.core.repositories.HolidayRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ExternalApiService {

    @Value("${holiday.api.url}")
    private String apiUrl;
    private ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate;
    private final HolidayRepository holidayRepository;
    private SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyyMMdd");
    private SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd")
    ;

    public ExternalApiService(RestTemplate restTemplate, HolidayRepository holidayRepository) {
        this.restTemplate = restTemplate;
        this.holidayRepository = holidayRepository;
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
            List<HolidayItem> items = parseApiResponse(response,String.valueOf(year));

            // Save to MongoDB
            if (!items.isEmpty()) {
                Holiday holiday = holidayRepository.findByYear(year);
                if (holiday == null) {
                    holiday = new Holiday();
                    holiday.setYear(year);
                }
                holiday.setHolidayList(items);
                holiday.setUpdatedAt(new Date());
                holidayRepository.save(holiday);
            }
        }catch (Exception e){
            // Catching any exceptions to get insights into the problem
            e.printStackTrace();
        }
        
    }

    /**
     * Parse the API response and extract holiday items.
     */
    private List<HolidayItem> parseApiResponse(String response,String year) {
        List<HolidayItem> holidayItems = new ArrayList<>();
        try {
            // Parse the JSON response into a JsonNode
            JsonNode rootNode = objectMapper.readTree(response);
            // Add the default holiday item at the beginning of the list
            HolidayItem defaultHolidayItem = new HolidayItem();
            defaultHolidayItem.setHolidayName("근로자의 날");  // Default holiday name
            defaultHolidayItem.setHolidayDate(year+"-05-01");     // Default holiday date
            defaultHolidayItem.setIsHoliday(true);                // Default isHoliday flag
            holidayItems.add(defaultHolidayItem);   
            // Navigate through the JSON structure
            JsonNode itemsNode = rootNode.path("response").path("body").path("items").path("item");

            // Check if the "item" is an array and parse it
            if (itemsNode.isArray()) {
                for (JsonNode item : itemsNode) {
                    String dateName = item.path("dateName").asText();
                    boolean isHoliday = item.path("isHoliday").asText().equals("Y");
                    String locdate = item.path("locdate").asText();
                    // Format locdate to yyyy-MM-dd
                    String formattedDate = formatDate(locdate);
                    // Create a new HolidayItem object and set values
                    HolidayItem holidayItem = new HolidayItem();
                    holidayItem.setHolidayName(dateName);
                    holidayItem.setIsHoliday(isHoliday);
                    holidayItem.setHolidayDate(formattedDate);

                    // Add to the list
                    holidayItems.add(holidayItem);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Handle any parsing errors if needed
        }
        return holidayItems;
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
        fetchAndStoreHolidayData(currentYear);
        fetchAndStoreHolidayData(currentYear + 1);
    }
    /**
     * Fetch holiday data when the server starts.
     */
    @PostConstruct
    public void initializeHolidayFetch() {
        scheduleHolidayFetch();
    }
}
