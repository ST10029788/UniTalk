# UniTalk: Your VC Studies Companion
This Android Application, created as part of the OPSC7312 module, is designed to enhance your college experience by integrating essential academic and social features into a single platform. Whether you’re studying for exams, connecting with peers, or keeping up with campus news, UniTalk has got you covered.

## Table of Contents
1. [Features](#features)
2. [Aim](#aim)
3. [Technologies Used](#technologies-used)
4. [Running the App](#running-the-app)
5. [Testing](#testing)
6. [Documentation](#documentation)
7. [Video Presentation](#video-presentation)
8. [Screenshots](#screenshots)
9. [License](#license)
10. [Developer Details](#developer-details)

## Features

1. **Socials Page**: 
   - Users can upload and download images and videos.
   - A gallery featuring images from Varsity College’s social events and activities.
   - Comment on media shared by others, creating a social environment for students to engage with each other.
   
2. **Marketplace**:
   - View ads within a 60km radius for items such as textbooks, laptops, and electronics.
   - Upload, edit, or delete personal ads.
   - Users can favorite ads and chat with the poster to discuss further details about the listings.
   - A platform for students to buy and sell textbooks and other educational materials, with in-app communication for easy negotiations between buyers and sellers.
   
3. **Media Features**:
   - Browse and view uploaded college materials, such as student notes, papers, and tests.
   - Provides an easy-to-access repository of educational content.
   
4. **Notice Board**:
   - Admins can post updates, and users will receive a push notification to inform them of new content.
   
5. **Chatroom Communication**:
   - Chat with other users in real-time.
   - Share pictures within the chatroom, enhancing the communication experience.
   - A dedicated chatroom for discussions related to university life, coursework, and more. Users can also share media within the chat.
   
6. **Quick Access to YouTube Lectures**:
   - Easily access YouTube educational videos relevant to your coursework.

8. **Role-based Access**:
   - Student users can access features like Socials, Marketplace, Media, and Chatroom.
   - Admin users have permissions to upload media content and notices.
   - Email domains such as `@varsitycollege.co.za`, `@iie.ac.za`, and `@vcconnect.edu.za` are required for registration.

 9. **AI Assistant**: 
   Provides academic support by answering student questions.

10. **Easy Information Access via Tiled Dashboard**: 
   - A visually distinct dashboard organizes key features for quick access. Sections are clearly labeled with icons to aid accessibility, including for neurodivergent individuals and non-technically inclined users.

11. **Admin Features**:
   - Admin users can create, publish, and delete notices.
   - Admins can upload student materials and manage educational content.

12. **Account Management**:
   - Users can manage their profiles, edit their details, verify their student status, and delete their accounts if necessary.

13. **About Us**:
   - Provides information about the app developers and the project.

14. **Settings**:
   - Users can personalize their experience by switching between dark and light themes, changing their password, verifying their account and managing other preferences.

15. **Log Out**:
   - Users can log out securely, and will be redirected to the login screen for added security.


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

## Documentation

## Video Presentation

## Screenshots

## License 
This project is licensed under the MIT License - see the LICENSE file for details.

## Developer Details
Developed as part of our OPSC7312 module. 

