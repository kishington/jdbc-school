CREATE TABLE students_courses
( 
   student_id INT REFERENCES students,
   course_id INT REFERENCES courses,
   PRIMARY KEY (student_id, course_id)
);
