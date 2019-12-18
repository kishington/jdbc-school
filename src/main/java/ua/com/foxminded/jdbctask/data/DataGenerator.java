package ua.com.foxminded.jdbctask.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.Test;

public class DataGenerator {
    private static final int GROUP_SIZE_LOWER_LIMIT = 10;
    private static final int GROUP_SIZE_UPPER_LIMIT = 30;
    static final int NUMBER_OF_GROUPS = 10;
    static final int NUMBER_OF_STUDENTS = 200;
    static final int STUDENT_ASSIGNED = 1;
    
    private static Random random = new Random();
    
    
    
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
