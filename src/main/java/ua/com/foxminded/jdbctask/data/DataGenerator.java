package ua.com.foxminded.jdbctask.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.Test;

public class DataGenerator {
    int GROUP_SIZE_LOWER_LIMIT = 10;
    int GROUP_SIZE_UPPER_LIMIT = 30;
    int NUMBER_OF_GROUPS = 10;
    int NUMBER_OF_STUDENTS = 200;
    int STUDENT_ASSIGNED = 1;
    
    private static Random random = new Random();
    
    Map<Integer, int[]> assignStudentsToGroups() {
        Map<Integer, int[]> studentDistribution = new HashMap<>();
        int[] randomPermutation = permuteRandomly(NUMBER_OF_STUDENTS);
        int[][] studentDistribution2 = new int[NUMBER_OF_STUDENTS][3];
        
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
                }
            }
            int[] studentsInGroupIds = new int[numberOfStudentsInGroup];
            for(int studentNumber = startStudentNumber; studentNumber < startStudentNumber + numberOfStudentsInGroup; studentNumber++) {
                int studentId = randomPermutation[studentNumber];
                studentDistribution2[studentId][0] = STUDENT_ASSIGNED;
                studentDistribution2[studentId][1] = groupId;
                studentsInGroupIds[studentNumber] = studentId;
            }
            studentDistribution.put(groupId, studentsInGroupIds);
            startStudentNumber += numberOfStudentsInGroup;
        }
        return studentDistribution;
    }
    
    int[] permuteRandomly(int n) {
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
