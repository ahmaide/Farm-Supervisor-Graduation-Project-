# Farm Supervisor Graduation Project
Farm Supervisor is an Android Mobile Application (alongside two micro services), that tends to help the Palestinian farmers in the agricultural work by giving plant related recommendations, warnings, as the appplication is currently limited to the Avocado plant in its current phase.


### The Mobile Application
The mobile application is built using Android Studio with Java programming language gradle dependancy

### The Spring Boot Authentication Application
This service is built with Java's Spring Boot framework with maven dependancy, as this service hosts to a PostGreSQL database, as it is responsible for user data such as (lands, crops, and specilizations), it also applies authentication to the application using JWT tokens.

### The Django Recommendations Service Application
This service is built with Python's Django framework, as this service hosts a Mongo databse which contains all the plant related data (currently Avocado) for the recommendations, this application hosts the system's recommendations, warnings, information (regarding pests, diseas, caring methods, cures, and needed tools), finally the application contains all the plants relations with users locations and weather conditions.
