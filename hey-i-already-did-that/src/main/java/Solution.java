import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.sort;

public class Solution {
    public static void main(String[] args) {
        System.out.println(Solution.solution("210022", 3));
    }

    public static int solution(String n, int b) {
        Map<String, Integer> indices = new HashMap<>();
        indices.put(n, 0);
        for (int i = 1; ; i++) {
            String nextN = next(n, b);
            if (indices.containsKey(nextN)) {
                return i - indices.get(nextN);
            }
            indices.put(nextN, i);
            n = nextN;
        }
    }

    private static String next(String n, int b) {
        char[] chars = n.toCharArray();
        sort(chars);
        String y = new String(chars);
        String x = new StringBuilder(y).reverse().toString();
        String unpadded = Integer.toString(Integer.parseInt(x, b) - Integer.parseInt(y, b), b);
        return String.join("", Collections.nCopies(n.length() - unpadded.length(), "0")) + unpadded;
    }
}
