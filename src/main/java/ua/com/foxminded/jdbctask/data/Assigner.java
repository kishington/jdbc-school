package ua.com.foxminded.jdbctask.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ua.com.foxminded.jdbctask.models.Course;

public class Assigner {
    private static final int GROUP_SIZE_LOWER_LIMIT = 10;
    private static final int GROUP_SIZE_UPPER_LIMIT = 30;
    private static final int MIN_NUMBER_OF_COURSES = 1;
    private static final int MAX_NUMBER_OF_COURSES = 3;
   
    static final int STUDENT_ASSIGNED = 1;
    static final int NUMBER_OF_GROUPS = 10;
    static final int NUMBER_OF_STUDENTS = 200;
    
    private static Random random = new Random();
    
    int[][] assignCoursesToStudents() {     
        int[][] studentsCourses = new int[NUMBER_OF_STUDENTS][];   
        List<Integer> coursesAssigned = new ArrayList<>();      
        for (int studentId = 0; studentId < NUMBER_OF_STUDENTS; studentId++) {
            int numberOfCourses = MIN_NUMBER_OF_COURSES + random.nextInt(MAX_NUMBER_OF_COURSES - MIN_NUMBER_OF_COURSES + 1);
            int[] coursesIds = new int[numberOfCourses];
            coursesAssigned.clear();
            for(int courseNumber = 0; courseNumber < numberOfCourses; courseNumber++) {
                int courseId;
                do {
                    courseId = random.nextInt(Course.getTotalNumberOfAvailableCourses());
                } while(coursesAssigned.contains(courseId));
                coursesAssigned.add(courseId);
                coursesIds[courseNumber] = courseId;
            }
            studentsCourses[studentId] = coursesIds;
        }
        return studentsCourses;
    }
    
    int[][] assignStudentsToGroups() {
        int[] randomPermutation = permuteRandomly(NUMBER_OF_STUDENTS);
        int[][] studentDistribution = new int[NUMBER_OF_STUDENTS][2];
        
        int numberOfStudentsLeft = NUMBER_OF_STUDENTS;
        int startStudentNumber = 0;
        for(int groupId = 0; groupId < NUMBER_OF_GROUPS; groupId++) {
            int numberOfStudentsInGroup;
            if (numberOfStudentsLeft < GROUP_SIZE_LOWER_LIMIT) {
                break;
            } else {
                if( numberOfStudentsLeft >= GROUP_SIZE_UPPER_LIMIT) {
                    numberOfStudentsInGroup = GROUP_SIZE_LOWER_LIMIT + random.nextInt(GROUP_SIZE_UPPER_LIMIT - GROUP_SIZE_LOWER_LIMIT + 1);
                    numberOfStudentsLeft -= numberOfStudentsInGroup;
                } else {
                    numberOfStudentsInGroup = GROUP_SIZE_LOWER_LIMIT + random.nextInt(numberOfStudentsLeft - GROUP_SIZE_LOWER_LIMIT + 1);
                    numberOfStudentsLeft -= numberOfStudentsInGroup;
                }
            }
            for(int studentNumber = startStudentNumber; studentNumber < startStudentNumber + numberOfStudentsInGroup; studentNumber++) {
                int studentId = randomPermutation[studentNumber];
                studentDistribution[studentId][0] = STUDENT_ASSIGNED;
                studentDistribution[studentId][1] = groupId;
            }
            startStudentNumber += numberOfStudentsInGroup;
        }
        return studentDistribution;
    }
    
    private int[] permuteRandomly(int n) {
        int[] permutation = new int[n];
        for(int i = 0; i < n; i++) {
            permutation[i] = i;
        }
        for (int i = n ; i > 0 ; i--) {
            int index = random.nextInt(i);
            int temp = permutation[i-1];
            permutation[i-1] = permutation[index];
            permutation[index] = temp;
        }
        return permutation;
    }
}
