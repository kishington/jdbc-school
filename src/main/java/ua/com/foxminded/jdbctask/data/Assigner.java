package ua.com.foxminded.jdbctask.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ua.com.foxminded.jdbctask.models.Course;

public class Assigner {
    
    private static Random random = new Random();

    int[][] assignCoursesToStudents() {
        int[][] studentsCourses = new int[Constants.NUMBER_OF_STUDENTS][];
        List<Integer> coursesAssigned = new ArrayList<>();
        for (int studentId = 0; studentId < Constants.NUMBER_OF_STUDENTS; studentId++) {
            int numberOfCourses = Constants.MIN_NUMBER_OF_COURSES
                    + random.nextInt(Constants.MAX_NUMBER_OF_COURSES - Constants.MIN_NUMBER_OF_COURSES + 1);
            int[] coursesIds = new int[numberOfCourses];
            coursesAssigned.clear();
            for (int courseNumber = 0; courseNumber < numberOfCourses; courseNumber++) {
                int courseId;
                do {
                    courseId = random.nextInt(Course.getTotalNumberOfAvailableCourses());
                } while (coursesAssigned.contains(courseId));
                coursesAssigned.add(courseId);
                coursesIds[courseNumber] = courseId;
            }
            studentsCourses[studentId] = coursesIds;
        }
        return studentsCourses;
    }

    int[][] assignStudentsToGroups() {
        int[] randomPermutation = permuteRandomly(Constants.NUMBER_OF_STUDENTS);
        int[][] studentDistribution = new int[Constants.NUMBER_OF_STUDENTS][2];

        int numberOfStudentsLeft = Constants.NUMBER_OF_STUDENTS;
        int startStudentNumber = 0;
        for (int groupId = 0; groupId < Constants.NUMBER_OF_GROUPS; groupId++) {
            int numberOfStudentsInGroup;
            if (numberOfStudentsLeft < Constants.GROUP_SIZE_LOWER_LIMIT) {
                break;
            } else {
                if (numberOfStudentsLeft >= Constants.GROUP_SIZE_UPPER_LIMIT) {
                    numberOfStudentsInGroup = Constants.GROUP_SIZE_LOWER_LIMIT + random.nextInt(Constants.GROUP_SIZE_UPPER_LIMIT - Constants.GROUP_SIZE_LOWER_LIMIT + 1);
                    numberOfStudentsLeft -= numberOfStudentsInGroup;
                } else {
                    numberOfStudentsInGroup = Constants.GROUP_SIZE_LOWER_LIMIT + random.nextInt(numberOfStudentsLeft - Constants.GROUP_SIZE_LOWER_LIMIT + 1);
                    numberOfStudentsLeft -= numberOfStudentsInGroup;
                }
            }
            for (int studentNumber = startStudentNumber; studentNumber < startStudentNumber + numberOfStudentsInGroup; studentNumber++) {
                int studentId = randomPermutation[studentNumber];
                studentDistribution[studentId][0] = Constants.STUDENT_ASSIGNED;
                studentDistribution[studentId][1] = groupId;
            }
            startStudentNumber += numberOfStudentsInGroup;
        }
        return studentDistribution;
    }

    private int[] permuteRandomly(int numberOfIntegersStartingFromOne) {
        int[] permutation = new int[numberOfIntegersStartingFromOne];
        for (int i = 0; i < numberOfIntegersStartingFromOne; i++) {
            permutation[i] = i;
        }
        for (int i = numberOfIntegersStartingFromOne; i > 0; i--) {
            int index = random.nextInt(i);
            int temp = permutation[i - 1];
            permutation[i - 1] = permutation[index];
            permutation[index] = temp;
        }
        return permutation;
    }
}
