import static java.lang.Math.max;

public class Solution {
    public static void main(String[] args) {
        System.out.println(Solution.solution(0, 3));
    }

    public static int solution(int start, int length) {
        int result = 0;
        for (int i = 0; i < length; i++) {
            result ^= xorOfConsecutiveNumbers(start + i * length, start + i * length + length - i - 1);
        }
        return result;
    }

    private static int xorOfConsecutiveNumbers(int start, int end) {
        // zero breaks the algorithm, and because zero is the identity element of xor just skip it. (also end is greater then zero)
        if (start == 0) {
            start = 1;
        }
        return xorOf1toN(start - 1) ^ xorOf1toN(end);
    }

    private static int xorOf1toN(int n) {
        switch (n%4) {
            case 0:
                return n;
            case 1:
                return 1;
            case 2:
                return n + 1;
            case 3:
                return 0;
            default:
                throw new RuntimeException("Unexpected mod: " + n%4);
        }
    }
}