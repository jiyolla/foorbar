public class Solution {
    public static void main(String[] args) {
        System.out.println(Solution.solution("abccbaabccba"));
    }

    public static int solution(String x) {
        for (int numOfParts = x.length(); numOfParts > 0; numOfParts--) {
            if (checkIsDividedEqual(x, numOfParts)) {
                return numOfParts;
            }
        }

        throw new RuntimeException("No solution");
    }
    
    public static boolean checkIsDividedEqual(String sequence, Integer numOfSubsequences) {
        int subsequenceLength = sequence.length() / numOfSubsequences;

        if (subsequenceLength * numOfSubsequences != sequence.length()) {
            return false;
        }
        
        for (int i = 0; i < subsequenceLength; i++) {
            for (int j = 0; j < numOfSubsequences; j++) {
                if (sequence.charAt(i) != sequence.charAt(i + j * subsequenceLength)) {
                    return false;
                }
            }
        }

        return true;
    }
}
