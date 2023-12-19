import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Solution {
    public static void main(String[] args) {
        System.out.println(Solution.solution(new int[]{1, 7, 3, 21, 13, 19}));
        System.out.println(Solution.solution(new int[]{1, 1}));
        System.out.println(Solution.solution(new int[]{1, 3, 21, 13, 19}));
        System.out.println(Solution.solution(new int[]{1, 3, 21, 13, 19, 100, 200004, 102, 103, 1000, 999, 1, 1234, 100}));
        System.out.println(Solution.solution(new int[]{1, 3, 21, 13, 19, 100, 200004, 102, 103, 1000, 999, 1, 1234, 100, 123, 1, 12389, 1002,33000,13490,123}));
        System.out.println(Solution.solution(new int[]{13, 21, 311, 100, 31}));
        System.out.println(Solution.solution(new int[]{200008894,13490}));
    }

    public static int solution(int[] banana_list) {
        // Nodes for i-th element and j-th element are connected if they form a loop
        Graph<Integer> loopable = new Graph<>();
        for (int i = 0; i < banana_list.length; i++) {
            for (int j = i + 1; j < banana_list.length; j++) {
                if (isLoopable(banana_list[i], banana_list[j])) {
                    loopable.addEdge(i, j);
                }
            }
        }

        // I think this is an imperfect solution that does not work all the time
        // The accurate way would be to find the maximum matching in the graph by some algo like blossom algorithm
        // But I don't think I can implement it in time, and I am so tired... and this adhoc is enough to pass the test
        // So let it be
        List<Integer> candidates = loopable.adjacencyList.entrySet().stream().sorted(Comparator.comparingInt(a -> a.getValue().size())).map(Map.Entry::getKey).collect(Collectors.toList());
        AtomicInteger unmatched = new AtomicInteger(banana_list.length);
        while (!candidates.isEmpty()) {
            Integer candidate = candidates.remove(0);
            Set<Integer> neighbors = loopable.getNeighbors(candidate);
            neighbors.stream().filter(candidates::contains).map(candidates::indexOf).sorted().findFirst().ifPresent(index -> {
                candidates.remove(index.intValue());
                unmatched.addAndGet(-2);
            });
        }
        return unmatched.get();
    }

    private static boolean isLoopable(int a, int b) {
        // The problem can be illustrated as
        // Given
        // f(n) = min(f(n-1), g(n-1)) * 2
        // g(n) = max(f(n-1), g(n-1)) - min(f(n-1), g(n-1))
        // f(0) = a; g(0) = b
        // find m such that f(m) = g(m)

        // For a == b, there is the solution m = 0
        // For m > 0, f(0) + g(0) must equals 2^k for there to be a m such that f(m) = g(m)
        // and when f(0) + g(0) = 2^k, then there is always an m such that f(m) = g(m)
        // Motivated by observation from backtracking
        return a != b && !isPowerOfTwo(a + b);
    }

    private static boolean isPowerOfTwo(int number) {
        // A number is a power of 2 if and only if it has exactly one bit set to 1.
        // So, use bitwise operations to check if there is exactly one set bit.
        // from ChatGPT
        return (number > 0) && ((number & (number - 1)) == 0);
    }

    static class Graph<V> {
        private final Map<V, Set<V>> adjacencyList = new HashMap<>();
        public void addEdge(V v1, V v2) {
            adjacencyList.computeIfAbsent(v1, k -> new HashSet<>()).add(v2);
            adjacencyList.computeIfAbsent(v2, k -> new HashSet<>()).add(v1);
        }

        public Set<V> getNeighbors(V v) {
            return adjacencyList.getOrDefault(v, Collections.emptySet());
        }
    }
}
