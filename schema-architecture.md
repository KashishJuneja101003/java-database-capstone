Section 1: Architecture summary
Write a paragraph or two describing your application's architecture in your own words. You may use this template and modify it:

This Spring Boot application uses both MVC and REST controllers. Thymeleaf templates are used for the Admin and Doctor dashboards, while REST APIs serve all other modules. The application interacts with two databases—MySQL (for patient, doctor, appointment, and admin data) and MongoDB (for prescriptions). All controllers route requests through a common service layer, which in turn delegates to the appropriate repositories. MySQL uses JPA entities while MongoDB uses document models.

Section 2: Numbered flow of data and control
Create a numbered list (1 to 7) describing each step of the data flow based on the architecture diagram. Use your own words or base your summary on the explanation above. You do not have to include the architectural diagram itself.
Example format:

1
2
3
4
1. User accesses AdminDashboard or Appointment pages.
2. The action is routed to the appropriate Thymeleaf or REST controller.
3. The controller calls the service layer...
...