package ua.com.foxminded.jdbctask.data;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import ua.com.foxminded.jdbctask.models.Course;

class AssignerTest {
    private static final Assigner assigner = new Assigner();

    @Test
    void testAssignCoursesToStudents_notLessThanMinNumberCoursesAssigned() {
        int[][] studentsCourses = assigner.assignCoursesToStudents();
        for (int studentId = 0; studentId < studentsCourses.length; studentId++) {
            int numberOfCourses = studentsCourses[studentId].length;
            assertTrue(numberOfCourses >= Assigner.MIN_NUMBER_OF_COURSES);
        }
    }

    @Test
    void testAssignCoursesToStudents_notMoreThanMaxNumberCoursesAssigned() {
        int[][] studentsCourses = assigner.assignCoursesToStudents();
        for (int studentId = 0; studentId < studentsCourses.length; studentId++) {
            int numberOfCourses = studentsCourses[studentId].length;
            assertTrue(numberOfCourses <= Assigner.MAX_NUMBER_OF_COURSES);
        }
    }

    @Test
    void testAssignCoursesToStudents_courseIdIsValid() {
        int numberOfAvailableCourses = Course.getTotalNumberOfAvailableCourses();
        int[][] studentsCourses = assigner.assignCoursesToStudents();
        for (int studentId = 0; studentId < studentsCourses.length; studentId++) {
            int numberOfCourses = studentsCourses[studentId].length;
            for (int courseNumber = 0; courseNumber < numberOfCourses; courseNumber++) {
                int courseId = studentsCourses[studentId][courseNumber];
                assertTrue(courseId < numberOfAvailableCourses);
            }
        }
    }

    @Test
    void testAssignStudentsToGroups_GroupSizeNotLessThanMin() {
        int[][] studentDistribution = assigner.assignStudentsToGroups();
        for (int groupId = 0; groupId < Assigner.NUMBER_OF_GROUPS; groupId++) {
            int numberOfStudentsInGroup = countStudentsInGroup(studentDistribution, groupId);
            if (numberOfStudentsInGroup != 0) {
                assertTrue(numberOfStudentsInGroup >= Assigner.GROUP_SIZE_LOWER_LIMIT);
            }
        }
    }

    @Test
    void testAssignStudentsToGroups_GroupSizeNotMoreThanMax() {
        int[][] studentDistribution = assigner.assignStudentsToGroups();
        for (int groupId = 0; groupId < Assigner.NUMBER_OF_GROUPS; groupId++) {
            int numberOfStudentsInGroup = countStudentsInGroup(studentDistribution, groupId);
            if (numberOfStudentsInGroup != 0) {
                assertTrue(numberOfStudentsInGroup <= Assigner.GROUP_SIZE_UPPER_LIMIT);
            }
        }
    }

    @Test
    void testAssignStudentsToGroups_GroupIdIsValid() {
        int[][] studentDistribution = assigner.assignStudentsToGroups();
        for (int studentId = 0; studentId < Assigner.NUMBER_OF_STUDENTS; studentId++) {
            boolean studentAssigned = studentDistribution[studentId][0] == Assigner.STUDENT_ASSIGNED;
            if (studentAssigned) {
                int groupId = studentDistribution[studentId][1];
                assertTrue(groupId < Assigner.NUMBER_OF_GROUPS);
            }
        }
    }

    private int countStudentsInGroup(int[][] studentDistribution, int groupId) {
        int numberOfStudentsInGroup = 0;
        for (int studentId = 0; studentId < Assigner.NUMBER_OF_STUDENTS; studentId++) {
            boolean studentAssignedToGivenGroup = (studentDistribution[studentId][0] == Assigner.STUDENT_ASSIGNED)
                    && (studentDistribution[studentId][1] == groupId);
            if (studentAssignedToGivenGroup) {
                numberOfStudentsInGroup++;
            }
        }
        return numberOfStudentsInGroup;
    }

}
