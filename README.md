# Overview
The FitForecast Location app is an Android application designed to integrate with the user's calendar and location services. It provides functionalities like finding gaps in the user's calendar, adding health check-up appointments, and locating nearby hospitals based on the current location.

# Features
Calendar Integration: Checks the user's calendar for gaps and suggests times for health check-ups.
Location Services: Uses the device's location to find nearby hospitals.
Dynamic Button Text: Updates button texts based on the calendar gaps and nearby hospital information.
Permissions Handling: Manages location and calendar permissions.

# Prerequisites
Android Studio installed.
A valid Google Maps API Key for accessing location services.

# Setup and Installation
Clone the Repository: Clone this repository to your local machine or download the code.
git clone https://github.com/your-username/fitforecast-location.git
Open the Project: Open the project in Android Studio.
Add API Key: Add your Google Maps API Key in the Places.initialize() method in the MainActivity.
Places.initialize(applicationContext, "YOUR_API_KEY")
Build the Project: Build the project in Android Studio to resolve dependencies.
Run the App: Run the app on an emulator or a physical device.

# Usage
Find Calendar Gaps: The app checks the calendar for available slots and updates the button texts with the gaps found.
Add Health Check-Up Event: Click on any gap button to add a health check-up event to your calendar at that time.
Find Nearby Hospitals: The app uses your current location to find and display nearby hospitals.

# Permissions
The app requires the following permissions:
Location: To find the nearest hospitals.
Calendar Access: To read and write events to the user's calendar.
