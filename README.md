# Notes API Test Project

This is a development-phase API testing project built in **Java** using **RestAssured** with **TestNG**, connected to a **Mockoon**-powered local mock backend.

## Purpose

Just a personal playground for building, testing and designing REST APIs.

## Current State

- **Login test only**
    - Based on mocked `/login` endpoint via Mockoon
    - Successful credentials loaded from a `.gitignored` file
    - Failure scenarios use JSON fixtures in the repo
- Tests written in Java with RestAssured
- Not integrated with CI — local testing only for now

## Project Structure

ApiTests/
├── app/
│   ├── mockoon/
│   │ └── notes_api_mock.json
│   └── src/test/
│       ├── java/
│       └── resources/
│           ├── login-failure-scenarios.json
│           └── testng.xml
│   └── build.gradle
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── .gitignore
├── gradlew
├── gradlew.bat
├── README.md
└── settings.gradle

## Notes

This is a mock/test-only setup. No real APIs, credentials, or external dependencies involved.

