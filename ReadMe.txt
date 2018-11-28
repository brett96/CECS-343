"sqlite-jdbc-3.16.1(2).jar" is needed to access the database.  Import it into the project ("Referenced Libraries")

Driver from: https://www.sqlite.org/download.html

Importing Schedules:
  - Schedule will be imported from the file "import.csv"
  - This file must exist in the project directory in order to be imported

Exporting Schedules:
  - Schedule will be exported to the file "schedule.csv" in the project directory
  - To import this file, it must be renamed to "import.csv"

Modifying/Deleting Appointments:
  - A user must be logged in to modify or delete an appointment 
  - Once a user is signed in, a dropdown menu will be displayed with all of the current user's appointments
  - Use this dropdown menu to select the appointment you want to modify or delete
  - Once the appointment is selected, choose the desired function from the appointments menu
