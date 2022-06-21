# SafetyNet Alerts

This application allows emergency services systems anticipating hazard situations. Indeed, SafetyNet Alerts provides real-time information about inhabitants and fire stations in your city.

Here are some situations where SafetyNet Alerts will help you save lives by giving the information you need:

- If a fire breaks out, SafetyNet Alerts provides information to text everyone in the area by collecting the phone numbers of people living in the burning building.
- In the event of a flood, SafetyNet Alerts states specific information about people in the area by listing potential victims, their ages and their medical history (treatments, allergies, etc).

## Getting started

### Prerequisites

Things you need to install the software and how to install them:

- Java 11.0
- Maven 3.6.4

### Running App

#### Cloning the project to your local environment

Import the code into an IDE of your choice and run SafetynetalertsApplication.java to launch the application.

#### Data base

The database is a JSON file with all information about inhabitants, their medical records and fire stations in your city shall be located in `src/main/resources/data.json`.
Template for the json file:

```
{
  "persons" : [ {
    "firstName" : "John",
    "lastName" : "Boyd",
    "address" : "1509 Culver St",
    "city" : "Culver",
    "zip" : "97451",
    "phone" : "841-874-6512",
    "email" : "jaboyd@email.com"
  }, {
    "firstName" : "Felicia",
    "lastName" : "Boyd",
    "address" : "1509 Culver St",
    "city" : "Culver",
    "zip" : "97451",
    "phone" : "841-874-6544",
    "email" : "jaboyd@email.com"
  } ],
  "firestations" : [ {
    "address" : "1509 Culver St",
    "station" : "3"
  }, {
    "address" : "29 15th St",
    "station" : "2"
  } ],
  "medicalrecords" : [ {
    "firstName" : "John",
    "lastName" : "Boyd",
    "birthdate" : "03/06/1984",
    "medications" : [ "aznol:350mg", "hydrapermazol:100mg" ],
    "allergies" : [ "nillacilan" ]
  }, {
    "firstName" : "Felicia",
    "lastName" : "Boyd",
    "birthdate" : "01/08/1986",
    "medications" : [ "tetracyclaz:650mg" ],
    "allergies" : [ "xilliathal" ]
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