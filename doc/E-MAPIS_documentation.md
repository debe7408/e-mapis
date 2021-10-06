# Table of contents

- [Table of contents](#table-of-contents)
- [1. Requirement’s specification](#1-requirements-specification)
  - [1.1. Purpose and summary of the project](#11-purpose-and-summary-of-the-project)
  - [1.2. A high-level overview of the system and functional requirements.](#12-a-high-level-overview-of-the-system-and-functional-requirements)
  - [1.3. Non-functional requirements](#13-non-functional-requirements)
- [2. System architecture requirements](#14-system-architecture-requirements)
  - [2.1. System requirements](#15-system-requirements)
  - [2.2. System requirements](#16-system-requirements)s

# 1. Requirement’s specification

## 1.1. Purpose and summary of the project

E-MAPIS is an application that, by collecting data about user's trips, car information, and geolocational data, delivers statistics for fuel or energy consumption for vehicles, and e-vehicles. By storing the statistics, the user will be able to view them at a later time to help determine and estimate the amount of fuel or energy that the trip will require. The application seeks to provide the user with stress-free planning of the trips and avoid unnecessary problems. Moreover, the data collected about fuel consumption for specific vehicles will be compared to the data provided in the vehicle's manual.

## 1.2. A high-level overview of the system and functional requirements.

| Technical:                                                                                                  |
| ----------------------------------------------------------------------------------------------------------- |
| As a Customer, I expect to be able to run the application on my Android device.                             |
| As a Customer, I want to create an account to have a layer of security with my data.                        |
| As a Customer, I want to have an account so I would not lose my statistical data in case of device failure. |
|                                                                                                             |

| Data Collection:                                                                                                             |
| ---------------------------------------------------------------------------------------------------------------------------- |
| As a Customer, I will provide the application with various trip and vehicle data to get statistics about my trips.           |
| As a Customer, I will be given a collection of statistics to help me plan trips better.                                      |
| As a Customer, I will be able to track my fuel/energy consumption throughout the period of time and this way track expenses. |
|                                                                                                                              |

| Functionalities:                                                                                                                         |
| ---------------------------------------------------------------------------------------------------------------------------------------- |
| As a Customer, I expect to be able to select my vehicle specification from a dropdown menu or add my own.                                |
| As a Customer, I expect my vehicle specification to be saved for a faster trip start, but easily changed if needed in the settings menu. |
| As a Customer, I want to be able to pause/resume my trip whenever needed.                                                                |
| As a Customer, I want to have the ability to input new energy/fuel levels if I refueled my vehicle.                                      |
|                                                                                                                                          |

In the flowchart below, an example of how the application from the user's perspective flows is displayed.

![Flowchart 1. An example of a high-level](/assets/images/FlowUsersPerspective.png)

_Flowchart 1. System flow from the user's perspective_

Listed below are a wireframe representation of our the application side will work from the user's perspective

![Wireframe Diagram 1. Visual user's pespective](/assets/images/WireframeDiagram.png)
_Wireframe 1. Visual user's perspective_

## 1.3. Non-functional requirements

- **SECURITY**

The app will be using HTTPS requests to ensure data encryption. Sensitive geolocation data will be collected, to combat the privacy risk of such data collection, we will be cutting short parts of the trip (the beginning, the ending) and this way, even if there is a data breach, no precise locations would be revealed. There will also be an account creation step to have another layer of security.

- **COMPATIBILITY**

Application's minimum API level required to run the app is API level 22 or Android 5.1, since, by AndroidStudio statistics, 92.3% of devices can run it. The device to run the application will have to have at least 1.5 GB RAM to make the Android system work efficiently and fast.

- **USABILITY**

Most users, that have been using their Android smartphones and various applications on the device, will have no difficulties using and adapting to the E-MAPIS application since it uses a user-friendly interface, which does not have many entities or widgets in the main view. In addition, multiple themes will be available to combat color blindness.

- **SCALABILITY**

To ensure that the system can handle the projected increase in user traffic, data volumes, etc., various tests are done to determine the application's limits and its causes. Since the application is mostly based on databases, testing parameters are testing the size of the database in relation to the number of users, sent queries, data imports, etc.

- **PERFORMANCE**

The application itself will do minimal calculations in itself because everything is considered to be calculated in the database. Therefore, the application will not use a lot of the device's resources (this is yet to be determined fully when the application is developed). Responding to the user's data input should not take more than 5 seconds, because these are simple queries to the database, while responding to the user's navigation between pannels, will depend mostly on the hardware and its overload, it should not take more than also 5 seconds with minimal requirements.

- **ARCHITECTURAL DECISIONS**

For the relational database management system - PostgreSQL will be used.
For the service that is the medium for the connection between the
application and DBMS server - PostgREST will be used.
The application will be written using AndroidStudio in Java and XML.

# 2. System Architecture

## 2.1. Architectural goal and contraints

- **E-MAPIS must be compatible and run on Vilnius University resources and infrastructure.**
- **All E-MAPIS functionality should be available on most mobile Android devices with Internet and GPS connections.**
- **Data sent and received back from the server is sensitive, as it is mostly GIS data, therefore, secure and encrypted connection must be ensured.**

## 2.2. Physical architecture overeview

The system will be run on 2 hardware devices:

- **Open Nebula serve**

A server on a cloud computing platform Open Nebula with an Ubuntu-Server 16 installed will store data and ensure a connection with the user's devices. The server is running PostgreSQL relational database with PostGIS extension to enable GIS data types support (Figure 1.1). In addition, PostgREST software adds restful API functionality to the database (Figure 1.3). This way we can read, write, update and delete data from the server from any Android device with E-MAPIS installed (Figure 1.2).

![Figure 1. User - Server connection](/assets/images/sysArch.png)

_Figure 1. User - Server connection_

- **Android device**

Android device will be running E-MAPIS application powered by Java and XML. Application is using Retrofit, which is an HTTP client for Android and Java, that turns HTTP API into Java interface, that way data can be sent to and received from the server. For GIS data tracking, E-MAPIS is going to be using ArcGIS Runtime Android SDK, which will track the actual mobile device location at a given time (Figure 1.4).
