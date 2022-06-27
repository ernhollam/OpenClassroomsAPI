# SafetyNet Alerts

This application allows emergency services systems anticipating hazard situations. Indeed, SafetyNet Alerts provides real-time information about inhabitants and fire stations in your city.

Here are some situations where SafetyNet Alerts will help you save lives by giving the information you need:

- If a fire breaks out, SafetyNet Alerts provides information to text everyone in the area by collecting the phone numbers of people living in the burning building.
- In the event of a flood, SafetyNet Alerts states specific information about people in the area by listing potential victims, their ages and their medical history (treatments, allergies, etc).

## Getting started

### Prerequisites

Things you need to install the software and how to install them:

- Java 11.0.14
- SpringBoot 2.6.6
- Maven 3.8.4

### Running App

#### Cloning the project to your local environment

Import the code into an IDE of your choice and run SafetynetalertsApplication.java to launch the application.

#### Data base

The database is a JSON file shall be located in `src/main/resources/data.json`.
The file must include a list of names, addresses and other information about people in your local jurisdiction. There also must be address correspondence with fire stations.
Template for the JSON file:

```
{
  "persons" : [ {
    "firstName" : "",
    "lastName" : "",
    "address" : "",
    "city" : "",
    "zip" : 12345,
    "phone" : "",
    "email" : ""
    } ],
  "firestations" : [ {
    "address" : "",
    "station" : 3
  } ],
  "medicalrecords" : [ {
    "firstName" : "",
    "lastName" : "",
    "birthdate" : "MM/dd/yyyy",
    "medications" : [ "", "" ],
    "allergies" : [ "" ]
  } ]
}
```

### Testing

SafetyNet Alerts has unit tests and integration tests. These tests are triggered from maven-surefire plugin during the build phase.

To run the tests and generate the JaCoCo and Surefire reports, in your IDE Terminal, run the following command:
`mvn clean verify site`.
This will generate a `site` folder within the `target` folder. By opening the `index.html` file, you'll land on the summary page of project information.

## Usage

### Actuators

Actuator endpoints let you monitor and interact with your application. The following built-in endpoints are enabled for SafetyNet Alerts:

- [health](http://localhost:8080/actuator/health) Shows application health information.
- [httptrace](http://localhost:8080/actuator/httptrace) Displays HTTP trace information (by default, the last 100 HTTP request-response exchanges)
- [info](http://localhost:8080/actuator/info) Displays arbitrary application info.
- [mappings](http://localhost:8080/actuator/mappings) Displays a collated list of all `@RequestMapping` paths
- [metrics](http://localhost:8080/actuator/metrics) Shows “metrics” information for the current application.

### CRUD endpoints

Basic Create, Read, Update and Delete operations are available on the app's entities at the endpoints:

- `/person`
- `/firestation`
- `/medicalRecord`

### SafetyNet Alerts endpoints

`/firestation?stationNumber={station_number}`
Returns a list of people covered by the corresponding fire station.
e.g. if the station number is 1, it returns the inhabitants covered by station number 1. The list includes the following specific information: first name, last name, address and phone number.
Furthermore, it provides the number of adults and number of children (an individual aged 18 or younger) in the service area.

`/childAlert?address={address}`
Returns a list of children living at this address.
The list includes the first and last name of each child, their age as well as a list of other household members. If there is no child, this endpoint returns an empty string.

`/phoneAlert?firestation={firestation_number}`
Returns a list of phone numbers of people covered by the fire station. It can be used to send emergency text message to specific households.

`/fire?address={address}`
Returns the list of inhabitants living at the given address as well as the fire station number covering the address.
The list includes person's name, phone number, age and medical history.

`/flood/?stations={list_of_station_numbers}`
Returns a list of all households served by the stations.
The inhabitants are sorted and filtered by address. It includes people's name, phone number and age as well as their medical records.

`/personInfo?firstName={firstName(not_required)}&lastName={lastName}`
returns specific information about someone including their name, address, age, email address and medical history. If there are people with the same name, they also appear in the list.

`/communityEmail?city={city}`
Returns email addresses of all the inhabitants of the city