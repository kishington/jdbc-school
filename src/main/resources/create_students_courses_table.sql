CREATE TABLE students_courses
( 
   student_id INT REFERENCES students ON DELETE CASCADE,
   course_id INT REFERENCES courses ON DELETE CASCADE,
   PRIMARY KEY (student_id, course_id)
);
