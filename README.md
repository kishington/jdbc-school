# School database administration console app using JDBC

The application inserts/updates/deletes data in PostgreSQL database using JDBC.

Tables in the database (these are Java types, SQL analogs that fit the most are actually used):
<pre>
groups(
	group_id int,
	group_name string
)
students(
	student_id int,
	group_id int,
	first_name string,
	last_name string
)
courses(
	course_id int,
	course_name string,
	course_description string
)
</pre>

#### Application functionality:
1. On startup all necessary tables are created in the database. If a table alerady exists, it is droped.
2. Data generation 
  * 10 groups with randomly generated names. The names contain 2 characters, hyphen, 2 numbers
  * 10 courses (Maths, Bilogy, etc.) 
  * 200 students. Full names are randomly combined from 20 first names and 20 last names.
  * Students are randomly assigned to groups. Each group contains from 10 to 30 students. Some groups could be with no students.
  * Relation MANY-TO-MANY between tables STUDENTS and COURSES is created. Each student is randomly assigned 1 to 3 courses.
3. Run SQL Queries from the application menu:
  * Find all groups with student count less than or equals to a number
  * Find all students related to a course with a given name
  * Add a new student
  * Delete students by STUDENT_ID
  * Assign a student to a course
  * Remove a student from one of his or hes courses
