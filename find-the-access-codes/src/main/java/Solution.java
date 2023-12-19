import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static java.util.Arrays.sort;

public class Solution {
    public static void main(String[] args) {
        System.out.println(Solution.solution(new int[]{1,2,3,4,5,6}));
        System.out.println(Solution.solution(new int[]{1,1,1}));
        System.out.println(Solution.solution(new int[]{1,1,2,2}));
        System.out.println(Solution.solution(new int[]{1,1,4,4}));
        System.out.println(Solution.solution(new int[]{10,2,5,20}));
    }

    public static int solution(int[] l) {
        return new Table(l, 3).getLuckySequencesCount();
    }

    static class Table {
        int[] numbers;
        int sequenceLength;
        // factorsIndices.get(i): indices of numbers in "numbers" that are factors of "numbers[i]"
        Map<Integer, List<Integer>> factorsIndices;
        // dp[i][j]: number of possible cases for left(inclusive) part of "numbers[i]", when numbers[i] is at the j-th position in the sequence
        Integer[][] dp;

        Table (int[] numbers, int sequenceLength) {
            this.numbers = numbers;
            this.sequenceLength = sequenceLength;
            factorsIndices = createFactorsMap(numbers);
            dp = new Integer[numbers.length][sequenceLength];
        }

        private static Map<Integer, List<Integer>> createFactorsMap(int[] numbers) {
            Map<Integer, List<Integer>> factors = new HashMap<>();
            for (int i = numbers.length - 1; i >= 0; i--) {
                int dividend = numbers[i];
                List<Integer> divisorsIndex = new ArrayList<>();
                for (int j = i - 1; j >= 0; j--) {
                    if (dividend % numbers[j] == 0) {
                        divisorsIndex.add(j);
                    }
                }
                factors.put(i, divisorsIndex);
            }
            return factors;
        }

        public int getLuckySequencesCount() {
            return IntStream.rangeClosed(0, numbers.length - 1).map(i -> getDp(i, sequenceLength - 1)).sum();
        }

        private Integer getDp(int index, int position) {
            // Cache hit
            if (dp[index][position] != null) {
                return dp[index][position];
            }

            if (position == 0) {
                dp[index][position] = 1;
            }
            else {
                dp[index][position] = factorsIndices.get(index).stream().map(i -> getDp(i, position - 1)).reduce(0,
                                                                                                                 Integer::sum);
            }

            return dp[index][position];
        }
    }
}
