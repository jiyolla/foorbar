import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Solution {

    public static void main(String[] args) {
        System.out.println(Solution.solution(new int[]{3, 2}, new int[]{1, 1}, new int[]{2, 1}, 4));
        System.out.println(Solution.solution(new int[]{300, 275}, new int[]{150, 150}, new int[]{185, 100}, 500));
        System.out.println(Solution.solution(new int[]{1000, 877}, new int[]{478, 902}, new int[]{2, 37}, 10000));
        System.out.println(Solution.solution(new int[]{1250, 1250}, new int[]{506, 773}, new int[]{2, 37}, 10000));
        System.out.println(Solution.solution(new int[]{1250, 1250}, new int[]{3, 3}, new int[]{1000, 1000}, 10000));
        System.out.println(Solution.solution(new int[]{1250, 1250}, new int[]{3, 3}, new int[]{1000, 1000}, 10));
        System.out.println(Solution.solution(new int[]{3, 2}, new int[]{1, 1}, new int[]{2, 1}, 3000));
    }

    // I AM SO HAPPY WITH THIS SOLUTION!!!
    public static int solution(int[] dimensions, int[] your_position, int[] trainer_position, int distance) {
        Cord you = new Cord(your_position[0], your_position[1]);
        Cord trainer = new Cord(trainer_position[0], trainer_position[1]);

        Map<Direction, Integer> youToTrainer = new RayTracer().findRayTrace(dimensions[0], dimensions[1], you, trainer, distance);
        Map<Direction, Integer> youToYou = new RayTracer().findRayTrace(dimensions[0], dimensions[1], you, you, distance);

        return (int) youToTrainer.keySet()
                                 .stream()
                                 .filter(direction -> !youToYou.containsKey(direction) || youToTrainer.get(direction) < youToYou.get(direction))
                                 .count();
    }
    
    static class RayTracer {
        private LinkedList<Cord> queue;
        private Set<Cord> visited;
        private Map<Direction, Integer> solutions;

        public Map<Direction, Integer> findRayTrace(int width, int height, Cord source, Cord target, int maxDistance) {
            clear();

            // The direct path
            process(source, target, maxDistance);
            
            while (!queue.isEmpty()) {
                Cord virtualTarget = queue.poll();

                // Left, Bottom, Right, Top
                process(source, virtualTarget.reflect(Axis.VERTICAL, 0), maxDistance);
                process(source, virtualTarget.reflect(Axis.HORIZONTAL, 0), maxDistance);
                process(source, virtualTarget.reflect(Axis.VERTICAL, width), maxDistance);
                process(source, virtualTarget.reflect(Axis.HORIZONTAL, height), maxDistance);
            }
            return solutions;
        }

        private void clear() {
            queue = new LinkedList<>();
            visited = new HashSet<>();
            solutions = new HashMap<>();
        }

        private void process(Cord source, Cord target, int distance) {
            if (!visited.contains(target) && source.withinDistance(target, distance)) {
                visited.add(target);
                queue.add(target);
                solutions.putIfAbsent(source.getDirectionTo(target), source.getSquaredDistance(target));
            }
        }
    }

    enum Axis {
        HORIZONTAL,
        VERTICAL
    }

    static class Direction {
        // Tail = (0, 0)
        private final Cord coprimeHead;

        Direction(Cord source, Cord target) {
            int dx = target.getX() - source.getX();
            int dy = target.getY() - source.getY();

            if (dx == 0 && dy == 0) {
                this.coprimeHead = new Cord(0, 0);
            } else if (dx == 0) {
                this.coprimeHead = new Cord(0, dy > 0 ? 1 : -1);
            } else if (dy == 0) {
                this.coprimeHead = new Cord(dx > 0 ? 1 : -1, 0);
            } else {
                BigInteger gcd = BigInteger.valueOf(dx).gcd(BigInteger.valueOf(dy));
                this.coprimeHead = new Cord(dx / gcd.intValue(), dy / gcd.intValue());
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Direction)) return false;
            Direction direction = (Direction) o;
            return coprimeHead.equals(direction.coprimeHead);
        }

        @Override
        public int hashCode() {
            return Objects.hash(coprimeHead);
        }
    }

    static class Cord {
        private final int x;
        private final int y;

        Cord(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public boolean withinDistance(Cord other, int distance) {
            return getSquaredDistance(other) <= distance*distance;
        }
        
        public int getSquaredDistance(Cord other) {
            return (x - other.x)*(x - other.x) + (y - other.y)*(y - other.y);
        }
        
        public Direction getDirectionTo(Cord target) {
            return new Direction(this, target);
        }

        /**
         * @param axis the axis to reflect against.
         *             Note that given a horizontal axis, a vertical reflection is performed and vice versa.
         * @param deviation the distance of the axis from the origin
         * @return the reflected Cord
         */
        public Cord reflect(Axis axis, int deviation) {
            switch (axis) {
                case HORIZONTAL:
                    return new Cord(x, 2*deviation - y);
                case VERTICAL:
                    return new Cord(2*deviation - x, y);
                default:
                    throw new RuntimeException("Unexpected axis: " + axis);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Cord)) return false;
            Cord cord = (Cord) o;
            return x == cord.x && y == cord.y;
        }

        @Override
        public int hashCode() {
            return 65535*x + y;
        }
    }
}