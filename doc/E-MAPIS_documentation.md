# Table of contents
- [1. Requirement’s specification](#1-requirements-specification)
  - [1.1 Purpose of the system](#11-purpose-of-the-system)
  - [1.2 A high-level overview of the system](#12-a-high-level-overview-of-the-system)
  - [1.3 Complete functional requirements (reasoning/value behind the requirement)](#13-complete-functional-requirements-reasoningvalue-behind-the-requirement)
  - [1.4 Non-functional requirements](#14-non-functional-requirements)

## 1. Requirement’s specification

### 1.1. Purpose of the system

E-MAPIS is an application that, by collecting data about user's trips, car information and geolocational data, delivers statistics for fuel or energy consumption for vehicles, and e-vehicles. By storing the statistics, the user will be able to view them at a later time to help determine and estimate the amount of fuel or energy that the trip will require. The application seeks to provide the user with stress-free planning of the trips and avoid unnecessary problems.  

### 1.2. A high-level overview of the system


| High-level requirements. User stories |
| --- |
| As a Customer, I will be able to use this application on my Android device.|
| As a Customer, I will provide the application with various trip and vehicle data to get statistics about my trips.|
| As a Customer, I will be given a collection of statistics to help me plan trips better.|
| As a Customer, I will be able to track my fuel/energy consumption throughout the period of time and this way track expenses|


In the flowchart below, an example of how the application from the user's perspective flows is displayed.

![Flowchart 1. An example of a high-level](/assets/images/FlowUsersPerspective.png)

*Flowchart 1. System flow from the user's perspective*


### 1.3. Complete functional requirements (reasoning/value behind the requirement)

The main tool for this app will be a relational database, in particular PostgreSQL with a suitable PostgreSQL geographic information system extension PostGIS. It adds support for geographic objects allowing location queries to be run in SQL. To test if the data was correctly inserted, we will use the QGIS tool. Since the weather and traffic highly impact road trips, public APIs containing this type of information will be used. Elevation will also be taken into account because it impacts energy consumption.


![Flowchart 2. Effective route-finding algorithm](/assets/images/Flowchart2.png)

*Flowchart 2. Effective route-finding algorithm*


### 1.4. Non-functional requirements

- **SECURITY**

The app will be using HTTPS requests to ensure data encryption. Sensitive geolocation data will be collected, to combat the privacy risk of such data collection, we will be cutting short parts of the trip (the beginning, the ending) and this way, even if there is a data breach, no precise locations would be revealed. There will also be an account creation step to have another layer of security. 

- **CAPACITY** (NEED TO CHANGE THIS W.I.P)

For the time being, E-MAPIS will not store a big amount of data. Implementing the Vilnius map in the database will take up from 3-10 GB of memory. On the other hand, the application will be collecting GIS data, fuel consumption, and other details from the user, so storage may be in shortage, in which case it will be increased. In the future, depending on the increasing volume demand, storage will be increased to meet such demands.

- **COMPATIBILITY**

Application's minimum API level required to run the app is API level 22 or Android 5.1, since, by AndroidStudio statistics, 92.3% of devices can run it. The device to run the application will have to have at least 1.5 GB RAM memory to make the Android system work efficiently and fast.

- **USABILITY** 

 Most users, that have been using their Android smartphones and various applications on the device, will have no difficulties using and adapting to the E-MAPIS application since it uses a user-friendly interface, which does not have many entities or widgets in the main view. In addition, multiple themes will be available to combat color blindness.

- **SCALABILITY**

To ensure that the system can handle the projected increase in user traffic, data volumes, etc., various tests are done to determine the application's limits and its causes. Since the application is mostly based on databases, testing parameters are testing the size of the database in relation to the number of users, sent queries, data imports, etc.

- **PERFORMANCE**

The application itself will do minimal calculations in itself because everything is considered to be calculated in the database. Therefore, the application will not use a lot of the device's resources (this is yet to be determined fully when the application is developed). Responding to the user's data input should not take more than 5 seconds, because these are simple queries to the database, while responding to the user's navigation between pannels, will depend mostly on the hardware and it's overload, it should not take more than also 5 seconds with minimal requirements.