Video Demonstrations: https://drive.google.com/drive/folders/1slJdthD7YTVH6XwCwGNl7oal0xD5uwKH?usp=sharing
# UniTalk: Your VC Studies Companion
This Android Application, created as part of the OPSC7312 module, is designed to enhance your college experience by integrating essential academic and social features into a single platform. Whether you’re studying for exams, connecting with peers, or keeping up with campus news, UniTalk has got you covered.
# UniTalk: Your VC Studies Companion

UniTalk is an Android application created as part of the OPSC7312 module. It is designed to enhance the college experience by integrating essential academic and social features into a single platform. Whether you’re studying for exams, connecting with peers, or keeping up with campus news, UniTalk has got you covered.

---

## Table of Contents
1. [Features](#features)
2. [Aim](#aim)
3. [Technologies Used](#technologies-used)
4. [Running the App](#running-the-app)
5. [Testing](#testing)
6. [Documentation](#documentation)
7. [Video Presentation](#video-presentation)
8. [Screenshots](#screenshots)
9. [References](#references)
10. [License](#license)
11. [Developer Details](#developer-details)

---

## Features

### Updates to the App

The latest UniTalk updates improve user experience with new features in account management, marketplace functionality, and multi-language support.

1. **Sign in/Login**
   - **Biometric Authentication**: Users can use fingerprint or face recognition for secure and convenient login.
   - **Google Sign-In**: Google sign-in is now available.
   - **Password Recovery**: Users can reset their password by receiving a reset email (use a student email for testing).

2. **Account Management**
   - Users can update their phone number and date of birth.
   - Phone numbers are now accessible through marketplace listings for calls or SMS.

3. **Marketplace**
   - **Advert Uploads**: Users can upload listings with details, images, and prices.
   - **Favorites**: Save preferred listings for future reference.
   - **Communication Options**: Use in-app chat, calls, or SMS for interactions about listings.

4. **Language Support**
   - The app adapts to the device’s language settings automatically.

---

### Feature Overview

1. **Socials Page**  
   Users can upload and download images and videos, view a gallery of social events, and comment on media.

2. **Marketplace**
   - View ads within a 60km radius for textbooks, electronics, and more.
   - Upload, edit, and delete ads.
   - Favorite ads and chat with sellers directly.

3. **Media Features**
   - Browse uploaded materials like notes and past papers in a centralized repository.

4. **Notice Board**
   - Admins can post updates, and users receive push notifications for new content.

5. **Chatroom Communication**
   - Chat with peers, share media, and discuss campus life or coursework.

6. **Quick Access to YouTube Lectures**
   - Access educational YouTube videos related to coursework.

7. **Role-Based Access**
   - Student users access features like Socials, Marketplace, and Chatroom.
   - Admins manage uploads and notices. Registration is limited to specific email domains.

8. **AI Assistant**
   - Supports students academically by answering queries (via REST API).

9. **Accessible Dashboard**
   - A visually distinct tiled dashboard improves usability for all, including neurodivergent users.

10. **Admin Features**
    - Admins can manage notices and educational content uploads.

11. **Account Management**
    - Users can edit their details, verify student status, and delete their accounts if needed.

12. **About Us**
    - Details about the developers and project.

13. **Settings**
    - Personalize theme, change password, verify account, and manage preferences.

14. **Log Out**
    - Secure logout, redirecting users to the login screen.

---

## Aim

UniTalk addresses the challenge of managing academic and personal responsibilities in college. By offering a centralized platform for resources, activities, and communication, UniTalk streamlines the student experience, fostering academic and personal growth.

---

## Technologies Used

- **Mobile Frontend**: Kotlin
- **Backend**: Firebase (Authentication, Database, Storage, Hosting)
- **Real-time Database**: Firebase Realtime Database
- **API**: Node.js

---

## Aim
Navigating the complexities of college life requires effective management of academic and personal responsibilities. 
Many students face challenges in staying organized, accessing essential resources, and communicating efficiently with peers and faculty.

To address these challenges, we introduce UniTalk, a comprehensive college management application designed to streamline and enhance the student experience. UniTalk offers a centralized platform for managing academic schedules, accessing educational resources, engaging in campus activities, and connecting with the university community. With features tailored to meet the diverse needs of students, UniTalk aims to simplify college life and support academic and personal growth.


## Technologies Used
Mobile Frontend: Kotlin
Backend: Firebase (For Authentication, Database, Storage, Hosting)
Real-time Database: Firebase Realtime Database
API: Node.js

## Running the App

1. **Install Android Studio:**
   - Ensure you have the latest version of Android Studio installed on your computer. You can download it from the [official website](https://developer.android.com/studio).

2. **Clone or Download the Project:**
   - **Clone the repository:**
     - Open Git Bash or your preferred Git client.
     - Use the `git clone` command followed by the repository URL. For example:
       ```bash
       git clone [https://github.com/ST10029788/UniTalk.git]
       ```
     - This will create a local copy of the repository on your computer.
   - **Download the repository as a ZIP file:**
     - Go to the GitHub page of the repository.
     - Click on the "Code" button and select "Download ZIP."
     - Extract the downloaded ZIP file to a directory of your choice.
3. **Open the Project in Android Studio:**
   - Launch Android Studio.
   - Select "Open an existing Android Studio project" from the welcome screen or go to `File > Open`.
   - Navigate to the directory where you cloned or extracted the project and select it.

4. **Install Dependencies:**
   - Android Studio will prompt you to install any missing SDK components or libraries. Follow the prompts to install them.
   - Open the `build.gradle` file and make sure all dependencies are up to date. You can sync the project with Gradle files by clicking the "Sync Now" button that appears at the top of the editor.

5. **Set Up an Emulator or Connect a Device:**
   - You can run the app on an emulator or a physical Android device.
   - **To set up an emulator:**
     - Go to `AVD Manager` (Android Virtual Device Manager) in Android Studio by clicking on the device icon in the toolbar.
     - Create a new virtual device by following the prompts.
   - **To use a physical device:**
     - Enable `Developer Options` and `USB Debugging` on your Android device.
     - Connect your device to your computer via USB.
    
6. **Build and Run the App:**
   - Click the green play button in the toolbar or select `Run > Run 'app'` from the menu.
   - Choose the target device (emulator or connected device).
   - Android Studio will build the project and install the app on the selected device.

7. **Troubleshooting:**
   - If you encounter any errors, check the `Logcat` tab in Android Studio for detailed error messages.
   - Common issues might include missing libraries, incorrect Gradle versions, or outdated dependencies. Address any issues as needed.

8. **Explore and Modify:**
   - Once the app is running, you can start exploring the code and making modifications as needed.
   - To make changes, edit the files in the `src` directory and rebuild the project by clicking the play button again.


## Testing
For the UniTalk application, we implemented a comprehensive testing strategy that includes unit tests, integration tests, and UI tests.
Unit Testing:

Purpose: To verify that individual components (units) of the code work as intended.
Tools Used: JUnit for Java/Kotlin.
Coverage: Focused on critical functions such as user authentication, data retrieval from Firebase, and input validation. Each function is tested with various inputs to ensure reliability.
Integration Testing:

Purpose: To ensure that different components of the application work together as expected.
Tools Used: Espresso and Firebase Test Lab.
Focus Areas: Testing the interaction between the frontend and backend services, such as user login processes and media uploads.
UI Testing:

Purpose: To validate the user interface against design specifications and ensure a smooth user experience.
Tools Used: Espresso for UI testing and Firebase Test Lab for running tests on various devices.
Scenarios Tested: Navigation between different features (e.g., Socials, Marketplace), responsiveness of the app in dark and light modes, and accessibility features.
User Acceptance Testing (UAT):

Purpose: To gather feedback from actual users to validate the application's usability and functionality.
Method: A group of students participated in testing the app and provided feedback on features, ease of use, and overall experience.
Feedback Implementation: Based on user feedback, adjustments were made to improve navigation and enhance the UI design.
Continuous Integration (CI)
The project utilizes a CI pipeline defined in the ci.yml file to automate the testing process. This setup includes:

Triggering Tests: Tests are automatically run on every push to the repository, ensuring that new changes do not break existing functionality.
Build Verification: Each build is checked for successful compilation and execution of all test cases.
Reporting: Test results are reported in the CI dashboard, allowing developers to quickly identify and address any issues.

## Documentation
A Video presentation, ReadMe file and Part 1 Research is provided in this repository. 

## Video Presentation
[Admin Dashboard Dark Mode.zip](https://github.com/user-attachments/files/17193981/Admin.Dashboard.Dark.Mode.zip)
Google drive link to both videos of Application Demonstration: https://drive.google.com/drive/folders/1slJdthD7YTVH6XwCwGNl7oal0xD5uwKH?usp=sharing

## Screenshots
### Student Features
![IMG-20240929-WA0011](https://github.com/user-attachments/assets/7d8b7e56-d397-42d8-a84d-637ed1029135)
![IMG-20240929-WA0010](https://github.com/user-attachments/assets/498689ed-2354-4eda-920d-310c407d1f8e)
![IMG-20240929-WA0009](https://github.com/user-attachments/assets/264b834f-9549-4971-920d-f30c52e5230c)
![IMG-20240929-WA0008](https://github.com/user-attachments/assets/40b6cf90-0ef4-443c-9dd1-990f7dc32c93)
![IMG-20240929-WA0007](https://github.com/user-attachments/assets/aa8f6f7e-ede6-4bde-a551-49d68a7279e7)
![IMG-20240929-WA0006](https://github.com/user-attachments/assets/2e292888-c304-4bf8-9afd-a07031a5379e)
![IMG-20240929-WA0005](https://github.com/user-attachments/assets/f8d28d54-13a0-4db5-9408-dc1174142297)
![IMG-20240929-WA0014](https://github.com/user-attachments/assets/1fdc20d5-9d94-414f-b8b2-5d5e47f2c104)
![IMG-20240929-WA0013](https://github.com/user-attachments/assets/b227711e-9c6a-4cfb-953b-2f062d35e29c)
![IMG-20240929-WA0012](https://github.com/user-attachments/assets/5b802d84-b965-4db2-9174-4a1e9dd08fe1)

### Admin Features
![Screenshot 2024-09-29 091118](https://github.com/user-attachments/assets/276e6e77-a7f9-4a14-be13-862623eb7934)
![Screenshot 2024-09-29 091124](https://github.com/user-attachments/assets/8f5180de-6e03-45f3-9e89-3c35384b900f)
![Screenshot 2024-09-29 091148](https://github.com/user-attachments/assets/c207575e-2be8-4572-8dc4-0960b78dfdd6)
![Screenshot 2024-09-29 091159](https://github.com/user-attachments/assets/240e2152-32cd-4659-a5c7-a2630239a554)
![Screenshot 2024-09-29 091214](https://github.com/user-attachments/assets/7dbf6f12-fad5-43c9-83b0-551af4559eeb)
![Screenshot 2024-09-29 091107](https://github.com/user-attachments/assets/a71b6fc1-99af-49db-9ff4-8cadd9cb63b0)


### Dark Mode
![Screenshot 2024-09-29 090839](https://github.com/user-attachments/assets/ab38444b-1303-4e62-bff3-8a034c1b4639)
![Screenshot 2024-09-29 090850](https://github.com/user-attachments/assets/b1777a08-13ed-4647-9fd2-56b61aa7383e)
![Screenshot 2024-09-29 090901](https://github.com/user-attachments/assets/11adc06d-e274-4f37-b87d-5bf148827ad4)
![Screenshot 2024-09-29 090918](https://github.com/user-attachments/assets/fa2713fa-35b3-4610-84bc-1805eae4cd33)
![Screenshot 2024-09-29 090944](https://github.com/user-attachments/assets/e7c30359-a2d9-401f-9ef8-a415cac3288c)
![Screenshot 2024-09-29 090817](https://github.com/user-attachments/assets/68c4a115-7df6-4801-830a-d15d6a59c0c4)


## References
Marketplace:

GitHub. (2024). 229394/CollegeIdleApp: The goal of this app is is mainly for the trading of second-hand goods on campus. [online] Available at: https://github.com/229394/CollegeIdleApp [Accessed 1 Sep. 2024].
https://github.com/229394/CollegeIdleApp.git

UI Design, Login, Notes and Papers pages:

‌GitHub. (2024). collegeconnect/CollegeConnect: College Connect is an Android application aimed to be a one-stop-shop for all the college needs irrespective of college and course. It aims to create a helpful platform for all college students. The app uses Material UI for an attractive interface (subject to improvement) which attracts the user to stay on the app for more time and is available in both Light and Dark mode. [online] Available at: https://github.com/collegeconnect/CollegeConnect [Accessed 1 Sep. 2024].
https://github.com/collegeconnect/CollegeConnect.git
‌
Dashboard tiling and design, Cardviews:

GitHub. (2024). abdulazizahwan/MyCollegeApp: Modern College Dashboard UI Design. [online] Available at: https://github.com/abdulazizahwan/MyCollegeApp [Accessed 2 Sep. 2024].
https://github.com/abdulazizahwan/MyCollegeApp.git

Notice board design:

GitHub. (2024). Ravi879/NoticeBoard: This is a notice board android app for college where faculty can post notice and students can receive the notice. [online] Available at: https://github.com/Ravi879/NoticeBoard [Accessed 20 Sep. 2024].
https://github.com/Ravi879/NoticeBoard.git
‌
Marketplace:

GitHub. (2022). Code-Sauce-Official/BechDo-Application: Do you own stuff that you purchased during initial years of college but no longer use? Books .. Electronics .. Drafters .. etc.. Wanna get rid of it? If you said Yes! then we have the right platform for you, BechDo pe BechDo!! Sell & help your own college freshies & pass on the legacy, but take care of the price 😉. [online] Available at: https://github.com/Code-Sauce-Official/BechDo-Application [Accessed 29 Sep. 2024].
https://github.com/Code-Sauce-Official/BechDo-Application.git
‌
File downloads:

GitHub. (2024). harshivam/BBDU_app: An app that makes your college experience better. [online] Available at: https://github.com/harshivam/BBDU_app [Accessed 29 Sep. 2024].
https://github.com/harshivam/BBDU_app.git
‌
‌Searching, general layout, formatting packages:

GitHub. (2024). mahditavakoli1312/collegeApp. [online] Available at: https://github.com/mahditavakoli1312/collegeApp [Accessed 29 Sep. 2024].
https://github.com/mahditavakoli1312/collegeApp.git

About Pages, Sign in, Log in:

GitHub. (2024). Ericgacoki/ElitechComputers: Elitech computer college App. [online] Available at: https://github.com/Ericgacoki/ElitechComputers [Accessed 29 Sep. 2024].
https://github.com/Ericgacoki/ElitechComputers.git

API:
Kaushal Vasava 2023. Retrofit in Android - Kaushal Vasava - Medium. [online] Medium. Available at: https://medium.com/@KaushalVasava/retrofit-in-android-5a28c8e988ce [Accessed 2 Aug. 2024].
Novalsys, I. 2021. CampusGroups. [online] Google.com. Available at: https://play.google.com/store/apps/details?id=com.novalsys.CampusGroups&hl=en [Accessed 2 Aug. 2024].


Kaushal Vasava 2023. Retrofit in Android - Kaushal Vasava - Medium. [online] Medium. Available at: https://medium.com/@KaushalVasava/retrofit-in-android-5a28c8e988ce [Accessed 2 Aug. 2024].

Features:

Novalsys, I. 2021. CampusGroups. [online] Google.com. Available at: https://play.google.com/store/apps/details?id=com.novalsys.CampusGroups&hl=en [Accessed 2 Aug. 2024].

Campus 2021. Campus 365. [online] Google.com. Available at: https://play.google.com/store/apps/details?id=com.campus365.app&hl=en [Accessed 13 Aug. 2024].

2U, I. 2021. Mobile Campus. [online] Google.com. Available at: https://play.google.com/store/apps/details?id=com.twou.android.campus&hl=en [Accessed 10 Aug. 2024].

‌
## License 
This project is licensed under the MIT License - see the LICENSE file for details.

## Developer Details
Developed as part of our OPSC7312 module. 

