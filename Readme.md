Java practical test assignment
You need to create a RESTful API based on the web Spring Boot application:
controller, responsible for the resource named Users.
1. It has the following fields:
   1.1. Email (required). Add validation against email pattern
   1.2. First name (required)
   1.3. Last name (required)
   1.4. Birth date (required). Value must be earlier than current date
   1.5. Address (optional)
   1.6. Phone number (optional)
2. It has the following functionality:
   2.1. Create user. It allows to register users who are more than [18] years old.
   The value [18] should be taken from properties file.
   2.2. Edit user
   2.3. Replace user
   2.4. Delete user
   2.5. Search for users by birth date range. Add the validation which checks
   that “From” is less than “To”. Should return a list of objects
3. Code is covered by unit tests using Spring
4. Code has exception handlers
5. Service responsible for business logic can be an interface, no need to
   implement it
6. Use of database is not necessary
7. Latest version of Spring Boot. Java version of your choice