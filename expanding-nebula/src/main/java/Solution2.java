import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Solution2 {
    public static void main(String[] args) {
        System.out.println(Solution.solution(new boolean[][]{{true, false, true}, {false, true, false}, {true, false, true}}));
    }

    public static int solution(boolean[][] g) {
        return new Solver().findPreviousGrids(new ConcreteGrid(g)).size();
    }

    static class Solver {
        Map<ConcreteGrid, Set<VirtualGrid>> cacheForGrids = new HashMap<>();

        static VirtualGrid treatContourAsGrid(Contour contour) {
            // TODO - [23-12-16][frank.burger]: explain why each contour can be treated as a grid
            return new VirtualGrid(contour);
        }

        Set<VirtualGrid> findPreviousGrids(ConcreteGrid currentGrid) {
            if (cacheForGrids.containsKey(currentGrid)) {
                return cacheForGrids.get(currentGrid);
            }

            Set<VirtualGrid> result;
            if (currentGrid.isLinear()) {
                result = findPreviousOuterContours(currentGrid.getContour(), currentGrid.getContour())
                        .stream()
                        .map(Solver::treatContourAsGrid)
                        .collect(Collectors.toSet());
            } else {
                result = findPreviousGrids(currentGrid.withoutContour())
                        .stream()
                        .flatMap(previousInnerGrid ->
                                         findPreviousOuterContours(previousInnerGrid.getContour(), currentGrid.getContour())
                                                 .stream()
                                                 .map(Solver::treatContourAsGrid))
                        .collect(Collectors.toSet());
            }
            cacheForGrids.put(currentGrid, result);
            return result;
        }

        Set<Contour> findPreviousOuterContours(Contour previousInnerContours, Contour currentInnerContours) {
            return null;
        }
    }

    static class Grid {
        Contour contour;

        Contour getContour() {
            return contour;
        }
    }

    static class ConcreteGrid extends Grid {
        boolean[][] grid;

        ConcreteGrid(boolean[][] grid) {
            this.grid = grid;
            this.contour = new Contour(grid);
        }

        static boolean[][] removeContour(boolean[][] grid) {
            if (grid.length < 2 || grid[0].length < 2) {
                throw new IllegalArgumentException("Grid should have at least 2 rows and 2 columns");
            }
            return Stream.of(grid)
                         .limit(grid.length - 1)
                         .map(row -> Arrays.copyOf(row, grid[0].length - 1))
                         .toArray(boolean[][]::new);
        }

        boolean isLinear() {
            return grid.length == 1 || grid[0].length == 1;
        }

        ConcreteGrid withoutContour() {
            return new ConcreteGrid(removeContour(grid));
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof ConcreteGrid)) {
                return false;
            }
            ConcreteGrid otherGrid = (ConcreteGrid) other;
            return Arrays.deepEquals(grid, otherGrid.grid);
        }

        @Override
        public int hashCode() {
            return Arrays.deepHashCode(grid);
        }
    }

    static class VirtualGrid extends Grid {
        VirtualGrid(Contour contour) {
            this.contour = contour;
        }
    }

    static class Contour {
        boolean[] right;
        boolean[] bottom;

        Contour(boolean[][] grid) {
            int rowCount = grid.length;
            int columnCount = grid[0].length;

            right = new boolean[rowCount];
            for (int i = 0; i < rowCount; i++) {
                right[i] = grid[i][columnCount - 1];
            }
            bottom = Arrays.copyOf(grid[rowCount - 1], columnCount);
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof Contour)) {
                return false;
            }
            Contour otherContour = (Contour) other;
            return Arrays.equals(right, otherContour.right) && Arrays.equals(bottom, otherContour.bottom);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(right) + Arrays.hashCode(bottom);
        }
    }
}