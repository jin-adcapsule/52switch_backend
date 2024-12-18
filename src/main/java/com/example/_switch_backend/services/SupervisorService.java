package com.example._switch_backend.services;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example._switch_backend.models.Dayoff;
import com.example._switch_backend.models.RequestDTO;
import com.example._switch_backend.repositories.DayoffRepository;

@Service
public class SupervisorService {
    private final DayoffRepository dayoffRepository;
    private final EmployeeService employeeService; 
    private final GroupService groupService; 
    
   // private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");//just to compare date inforation
    
   @Autowired
   public SupervisorService( DayoffRepository dayoffRepository,EmployeeService employeeService,GroupService groupService) {
       this.dayoffRepository = dayoffRepository;

       this.employeeService = employeeService;
       this.groupService = groupService;
   }
/**
     * Mutate the status and comment of a request identified by its request key.
     *
     * This method updates the requestStatus and answerComment fields for all
     * Dayoff documents matching the given requestKey. If no documents are found,
     * it returns false.
     *
     * Edge cases:
     * - If the requestKey does not match any documents, the method returns false.
     * - If any exception occurs during the update, the method logs the exception
     *   and returns false.
     *
     * @param _id The unique identifier of the supervisor performing the update.
     * @param requestStatus The new status to assign to the request.
     * @param answerComment A comment explaining the supervisor's decision.
     * @param requestKey The unique key identifying the request.
     * @return true if the update is successful, false otherwise.
     */
    public Boolean answerRequest(
        String _id,
        String requestStatus, 
        String answerComment, 
        String requestKey
        ) {
        try {
            // Find the list of Dayoff documents by requestKey
            List<Dayoff> dayoffList = dayoffRepository.findByRequestKey(requestKey);
            if (dayoffList.isEmpty()) {
                System.out.println("No Dayoff documents found with the provided requestKey: " + requestKey);
                return false; // No documents found
            }

            // Update each document's status and add the answer comment
            for (Dayoff dayoff : dayoffList) {
                dayoff.setRequestStatus(requestStatus); // Update status
                dayoff.setAnswerComment(answerComment); // Add answer comment
            }

            // Save the updated documents back to the repository
            dayoffRepository.saveAll(dayoffList);

            return true; // Successfully updated and saved
            }catch (Exception e) {
                e.printStackTrace(); // Log the exception for debugging
                return false;
            }
        }

    /**
     * Fetch a list of pending requests assigned to a supervisor.
     *
     * This method retrieves all pending requests (e.g., Dayoff) associated with
     * a supervisor by their ID. It converts the results into a list of RequestDTO
     * objects for easier manipulation in the frontend.
     *
     * Edge cases:
     * - If no pending requests are found, the returned list will be empty.
     * - If any exception occurs, a runtime exception is thrown with the error details.
     *
     * @param _id The unique identifier of the supervisor.
     * @param pendingStatusText The status indicating pending requests (e.g., "Pending").
     * @return A sorted list of RequestDTO objects representing the pending requests.
     */
    public List<RequestDTO> getPendingRequests(
        String _id,
        String pendingStatusText
        ) {
        try {
            int employeeId = employeeService.getEmployeeIdById(_id);
            List<Dayoff> dayoffs = dayoffRepository.findBySupervisorIdAndRequestStatus(employeeId, pendingStatusText);
            //Group Dayoff Requests
            Map<String, List<Dayoff>> dayoffGrouped = dayoffs.stream()
                .collect(Collectors.groupingBy(Dayoff::getRequestKey));
            List<RequestDTO> dayoffRequests= dayoffGrouped.entrySet().stream()
                .map(entry -> {
                    String requestKey = entry.getKey();
                    List<Dayoff> groupedDayoffs = entry.getValue();
                    // Collect all dayoffDates into a list
                    List<String> dayoffDates = groupedDayoffs.stream()
                        .map(Dayoff::getDayoffDate)
                        .sorted() // Sort in ascending order
                        .collect(Collectors.toList());
                    // Get employee name dynamically using employeeService
                        String employeeName = employeeService
                        .getEmployeeMiniByEmployeeId(groupedDayoffs.get(0).getEmployeeId())
                        .get("name")
                        .toString();
                    // Combine data from the grouped sublist to create a single DTO
                    RequestDTO requestDTO= new RequestDTO(
                        groupedDayoffs.get(0).getEmployeeId(),   // Assuming all in group have same employeeId
                        employeeName,
                        "Dayoff",
                        groupedDayoffs.get(0).getRequestStatus(), // Assuming all in group have the same requestStatus
                        groupedDayoffs.get(0).getSupervisorId(), // Assuming same supervisorId for all
                        requestKey,                             // Use the requestKey for the group
                        groupedDayoffs.get(0).getRequestDate(), // Take requestDate from the first element
                        groupedDayoffs.get(0).getRequestComment()
                    );
                            // Set dayoff-specific fields
                    requestDTO.setDayoffOptionalFields(
                        groupedDayoffs.get(0).getDayoffType(),
                        dayoffDates
                    );
                    return requestDTO;
                })
                .collect(Collectors.toList());
/*
            int employeeId = employeeService.getEmployeeIdById(_id);
            String employeeName = employeeService.getEmployeeMiniByEmployeeId(employeeId).get("name").toString();
     // Fetch pending requests from all collections
                List<RequestDTO> dayoffRequests = dayoffRepository
                        .findBySupervisorIdAndRequestStatus(employeeId, pendingStatusText)
                        .stream()
                        .map(dayoff -> {
                            RequestDTO requestDTO = new RequestDTO(
                                dayoff.getEmployeeId(),
                                employeeName,
                                "Dayoff",
                                dayoff.getRequestStatus(),
                                dayoff.getSupervisorId(),
                                dayoff.getRequestKey(),
                                dayoff.getRequestDate(),
                                dayoff.getRequestComment()
                                );
                            // Dynamically add Dayoff-specific fields
                            //requestDTO.setDayoffOptionalFields(dayoff.getDayoffType(), dayoff.getDayoffDate());
                            return requestDTO;
                            }
                        )
                        .collect(Collectors.toList());
   
                List<RequestDTO> earlyLeaveRequests = earlyLeaveRepository
                        .findBySupervisorIdAndStatus(employeeId, pendingStatusText)
                        .stream()
                        .map(earlyLeave -> new RequestDTO(
                                earlyLeave.getEmployeeId(),
                                "EarlyLeave",
                                earlyLeave.getStatus(),
                                earlyLeave.getSupervisorId(),
                                earlyLeave.getTimestamp(),
                                earlyLeave.getApplyDate(),
                                earlyLeave.getComment(),
                                null, // No dayoffType for EarlyLeave
                                earlyLeave.getLeaveDate()
                        ))
                        .collect(Collectors.toList());

                List<RequestDTO> sicknessLeaveRequests = sicknessLeaveRepository
                        .findBySupervisorIdAndStatus(employeeId, pendingStatusText)
                        .stream()
                        .map(sicknessLeave -> new RequestDTO(
                                sicknessLeave.getEmployeeId(),
                                "SicknessLeave",
                                sicknessLeave.getStatus(),
                                sicknessLeave.getSupervisorId(),
                                sicknessLeave.getTimestamp(),
                                sicknessLeave.getApplyDate(),
                                sicknessLeave.getComment(),
                                null, // No dayoffType for SicknessLeave
                                sicknessLeave.getSicknessDate()
                        ))
                        .collect(Collectors.toList());

                // Combine all requests into a single list
                List<RequestDTO> allRequests = new ArrayList<>();
                allRequests.addAll(dayoffRequests);
                allRequests.addAll(earlyLeaveRequests);
                allRequests.addAll(sicknessLeaveRequests);
    */
                    // Combine all requests into a single list
                    List<RequestDTO> allRequests = new ArrayList<>();
                    allRequests.addAll(dayoffRequests);
                // Sort by timestamp (or another field as needed)
                return allRequests.stream()
                        .sorted(Comparator.comparing(RequestDTO::getRequestDate))
                        .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("Invalid date format. Use 'yyyy-MM-dd' for startDate and endDate.");
        }
    }

    /**
     * Fetch a list of request history between two dates and statuses for a supervisor's group.
     *
     * This method retrieves requests that match the specified date range and statuses
     * for employees under a supervisor's group. It converts the results into a list of
     * RequestDTO objects for easier manipulation in the frontend.
     *
     * Edge cases:
     * - If no requests match the criteria, the returned list will be empty.
     * - If any exception occurs, a runtime exception is thrown with the error details.
     *
     * @param _id The unique identifier of the supervisor.
     * @param startDate The start date for filtering requests (inclusive).
     * @param endDate The end date for filtering requests (inclusive).
     * @param requestStatusList A list of statuses to filter requests by.
     * @return A sorted list of RequestDTO objects representing the request history.
     */
    public List<RequestDTO> getRequestHistory(
        String _id,
        String startDate,
        String endDate,
        List<String> requestStatusList
        ) {
        try {
            int employeeId = employeeService.getEmployeeIdById(_id);

            List<Integer> memberEidList=employeeService.findGroupMembersBySupervisorId(employeeId); // getting member eid list 
            System.out.println(memberEidList);
            List<Dayoff> dayoffs = dayoffRepository.findByEmployeeIdInAndRequestStatusAndRequestDateBetweenInclusive(memberEidList, requestStatusList,startDate,endDate);
            //Group Dayoff Requests
            Map<String, List<Dayoff>> dayoffGrouped = dayoffs.stream()
                .collect(Collectors.groupingBy(Dayoff::getRequestKey));
            List<RequestDTO> dayoffRequestList= dayoffGrouped.entrySet().stream()
                .map(entry -> {
                    String requestKey = entry.getKey();
                    List<Dayoff> groupedDayoffs = entry.getValue();
                    // Collect all dayoffDates into a list
                    List<String> dayoffDates = groupedDayoffs.stream()
                        .map(Dayoff::getDayoffDate)
                        .sorted() // Sort in ascending order
                        .collect(Collectors.toList());
                    // Get employee name dynamically using employeeService
                        String employeeName = employeeService
                        .getEmployeeMiniByEmployeeId(groupedDayoffs.get(0).getEmployeeId())
                        .get("name")
                        .toString();
                    // Combine data from the grouped sublist to create a single DTO
                    RequestDTO requestDTO= new RequestDTO(
                        groupedDayoffs.get(0).getEmployeeId(),   // Assuming all in group have same employeeId
                        employeeName,
                        "Dayoff",
                        groupedDayoffs.get(0).getRequestStatus(), // Assuming all in group have the same requestStatus
                        groupedDayoffs.get(0).getSupervisorId(), // Assuming same supervisorId for all
                        requestKey,                             // Use the requestKey for the group
                        groupedDayoffs.get(0).getRequestDate(), // Take requestDate from the first element
                        groupedDayoffs.get(0).getRequestComment()
                    );
                            // Set dayoff-specific fields
                    requestDTO.setDayoffOptionalFields(
                        groupedDayoffs.get(0).getDayoffType(),
                        dayoffDates
                    );
                    return requestDTO;
                })
                .collect(Collectors.toList());
            // Combine all requests into a single list
            List<RequestDTO> allRequests = new ArrayList<>();
            allRequests.addAll(dayoffRequestList);
            // Sort by timestamp (or another field as needed)
            return allRequests.stream()
                    .sorted(Comparator.comparing(RequestDTO::getRequestDate))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace(); // Log the full stack trace for debugging
        throw new RuntimeException("ERROR WITH getRequestHistory: " + e.getMessage(), e);
        }
    }


}

