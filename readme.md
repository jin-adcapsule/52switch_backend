***

### 52Switch Attendance and Day-Off Management System

***

**52Switch** is a comprehensive attendance and day-off management application designed to streamline employee attendance tracking and day-off requests/approvals.<br>The project is built using **Flutter** for the client application, **Spring Boot** with **GraphQL** for the server, and **MongoDB Atlas** as the database.

[52SWITCH Project Documentation KR](https://gilded-brush-9bc.notion.site/52SWITCH-2025-01-09-151fbe5a819680628769e0db7e4aace6?pvs=4)

## Basic Features
- **Day-Off Requests:** Employees can request day-off directly from the app.
- **Supervisor Panel:** Manage approve/reject day-off requests from supervisee.
- **User-Friendly Interface:** Mobile-friendly interface with customizable themes.
- **Secure Authentication:** Multi step phone number-based login system.<br>1. deviced stored token<br>2. device phone number authentication<br>3. db stored token and phone number

## Enhancement beyond current Application
- **Holiday:** Calendar now disables holidays.
- **Background Notification:** Notification now activated also in background both for IOS and Android.
- **Work Start Toggle Nofification:** each employee's work start time is computed by location / day off (half) / holiday. Toggle Notification is sent to each employee.    

***

### 52Switch Server

***
## Prerequisites
- **Backend**: Spring-boot(JDK17, Maven), GraphQL 
- **Database**:MongoDB Atlas Driver
- **Authentication**: Firebase Authentication
- **Notification**: Firebase Cloud Messaging(FCM)
- **Docker**
- **AWS EC2 Update on CI/CD**

## Access onto Cloud Server
   (Please contact the project owner for accessible host ip address)<br>
   Github action activates cloud server temporarily by each push or pull.
   ```plaintext
   awsHostPublicIP:8080
   ```
## Local Setup Instructions

### Step 1. Required parameters and file:
   (contact the project owner for DB credential, firebase key and holiday api key)<br>
   Ensure this is placed in the correct directories :
   ```plaintext
   src/main/resources/52switch_firebase_key.json
   ``` 
### Step 2. Run local server:
   Environment clean and run:
   ```bash
   $root/mvn clean install;.\run_with_env.ps1
   ```

For further assistance or refinements, feel free to reach out:

**Contact:**  
Chaejin Lim  
📧 [jin.chaejin.lim@adcapsule.co.kr](mailto:jin.chaejin.lim@adcapsule.co.kr)