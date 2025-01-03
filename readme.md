# 52Switch: Attendance and Day-Off Management Application

**52Switch** is a comprehensive attendance and day-off management application designed to streamline employee attendance tracking and day-off requests/approvals. The project is built using **Flutter** for the frontend, **Spring Boot** with **GraphQL** for the backend, and **MongoDB** as the database.

[52SWITCH Project Documentation](https://gilded-brush-9bc.notion.site/Mid-Review-52SWITCH-App-Clone-151fbe5a819680a38f17c3785889ef3b?pvs=4)  
[52SWITCH Project Documentation KR](https://gilded-brush-9bc.notion.site/52SWITCH-151fbe5a819680628769e0db7e4aace6?pvs=4)

## Features
- **Real-Time Attendance Tracking:** Employees can check in and out seamlessly.
- **Day-Off Requests:** Employees can request time off directly from the app.
- **Admin Panel:** Manage attendance and approve/reject day-off requests.
- **User-Friendly Interface:** Mobile-friendly interface with customizable themes.
- **Secure Authentication:** Phone number-based login system with session persistence.
- **Real-Time Updates:** Leverages GraphQL subscriptions for dynamic updates.

---

## Prerequisites
- **Backend**: Java(JDK17), Maven
- **Database**:MongoDB
- **Authentication**: Firebase(using Emulator recommended for test)

## Setup Instructions
### Step 1: Clone the repository:
   ```bash
   git clone https://github.com/jin-adcapsule/52switch-backend.git
   cd 52switch-backend
   ```
### Step 2. Configure required files in correct directories:
   Ensure this is placed in the correct directories (contact the project owner if unsure):
   ```plaintext
   src/main/resources/52switch_firebase_key.json
   ``` 

### Step 3: Run the setup script in root:
   On Linux/Mac:
   ```bash
      bash setup.sh 
   ```
   On Windows:
   ```bash
      setup.bat
   ```
### Step 4. Configure environment files:
   Update the following file with your backend environment variables:
   ```plaintext
   52switch-backend/.env
   ```

### Step 5. (Recommended) Run firebase emulator:
   ```bash
   $root/firebase emulators:start
   ```
### Step 6. Run server:
   Environment clean and setup:
   ```bash
   $root/mvn clean install
   ```
   With firebase emulator, edit first this file and then run:
   ```bash
   $root/.\run_with_env.ps1
   ```
   Or without firebase emulator:
   ```bash
   $root/mvn spring-boot:run    
   ```
      
For further assistance or refinements, feel free to reach out:

**Contact:**  
Chaejin Lim  
📧 [jin.chaejin.lim@adcapsule.co.kr](mailto:jin.chaejin.lim@adcapsule.co.kr)