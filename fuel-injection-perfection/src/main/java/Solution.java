import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Solution {
    private static LinkedList<Path> queue;
    private static Map<BigDecimal, Boolean> visited;
    public static void main(String[] args) {
        System.out.println(Solution.solution("134038309232842384230234928432423034823432900029348981340383092328423842302349284324230348234329000293489851340383092328423842302349284324230348234329000293489851340383092328423842302349284324230348234329000293489851340383092328423842302349284324230348234329000293489851340383092328423842302349284324230348234329000293489851340383092328423842302349284324230348234329000293489855"));
        System.out.println(Solution.solution("2"));
        System.out.println(Solution.solution("15"));
    }

    public static int solution(String x) {
        queue = new LinkedList<>();
        visited = new HashMap<>();
        addToQueueIfNotVisited(new BigDecimal(x), 0);
        while (!queue.isEmpty()) {
            Path path = queue.poll();

            if (path.at.equals(BigDecimal.ONE)) {
                return path.length;
            }

            addToQueueIfNotVisited(path.at.add(BigDecimal.ONE), path.length + 1);
            addToQueueIfNotVisited(path.at.subtract(BigDecimal.ONE), path.length + 1);
            if (path.at.remainder(BigDecimal.valueOf(2)).equals(BigDecimal.ZERO)) {
                addToQueueIfNotVisited(path.at.divide(BigDecimal.valueOf(2)), path.length + 1);
            }
        }
        throw new RuntimeException("No solution");
    }

    private static void addToQueueIfNotVisited(BigDecimal nextAt, int nextLength) {
        visited.computeIfAbsent(nextAt, k -> {
            queue.add(new Path(nextAt, nextLength));
            return true;
        });
    }

    static class Path {
        BigDecimal at;
        int length;

        Path(BigDecimal at, int length) {
            this.at = at;
            this.length = length;
        }
    }
}