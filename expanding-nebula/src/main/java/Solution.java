import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Solution {
    public static void main(String[] args) {
        System.out.println(Solution.solution(new boolean[][]{{true, false, true}, {false, true, false}, {true, false, true}}));
    }

    public static int solution(boolean[][] g) {
        return new Solver().findPreviousGrids(new ConcreteGrid(g)).size();
    }

    static class Solver {
        Map<ConcreteGrid, Set<VirtualGrid>> gridsCache = new HashMap<>();
        Map<Contour, Set<DecidingContour>> contoursCache = new HashMap<>();

        static VirtualGrid treatContourAsGrid(Contour contour) {
            return new VirtualGrid(contour);
        }

        Set<VirtualGrid> findPreviousGrids(ConcreteGrid currentGrid) {
            if (gridsCache.containsKey(currentGrid)) {
                return gridsCache.get(currentGrid);
            }

            Set<VirtualGrid> result;
            if (currentGrid.isLinear()) {
                result = findPreviousDecidingContours(currentGrid.getContour())
                        .stream()
                        .map(DecidingContour::getOuter)
                        .map(Solver::treatContourAsGrid)
                        .collect(Collectors.toSet());
            } else {
                result = findPreviousGrids(currentGrid.withoutContour())
                        .stream()
                        .flatMap(previousInnerGrid ->
                                         findPreviousDecidingContours(currentGrid.getContour())
                                                 .stream()
                                                 .filter(decidingContour -> decidingContour.getInner().equals(previousInnerGrid.getContour()))
                                                 .map(DecidingContour::getOuter)
                                                 .map(Solver::treatContourAsGrid))
                        .collect(Collectors.toSet());
            }
            gridsCache.put(currentGrid, result);
            return result;
        }

        Set<DecidingContour> findPreviousDecidingContours(Contour currentContour) {
            if (contoursCache.containsKey(currentContour)) {
                return contoursCache.get(currentContour);
            }

            Set<DecidingContour> result = new HashSet<>();
            Deque<Search> stack = new ArrayDeque<>();
            stack.add(new Search(currentContour));
            while (stack.isEmpty()) {
                stack.pop()
                     .advance()
                     .forEach(search -> {
                            if (search.isComplete()) {
                                result.add(search.toDecidingContour());
                            } else {
                                stack.push(search);
                            }
                      });
            }
            contoursCache.put(currentContour, result);
            return result;
        }

        static class Search {
            // Performance-wise it is tempting to remove the contour from the search state
            Contour contour;
            Map<Cord, Boolean> explored;
            Cord current;

            Search(Contour contour) {
                this.contour = contour;
                explored = new HashMap<>();
                current = new Cord(contour.right.length, contour.bottom.length);
            }

            /**
             * Only valid next searches are returned
             * @return so it could be an emtpy set
             */
            Set<Search> advance() {
                // Find the next cell to explore (the logic here should guarantee single traversal)
                // Try true/false with that cell, that is to create two new searches
                // For new searches, judge their validness if possible, and discard invalid ones
                // Return the valid or maybe valid searches

                // For example, in following grid
                // 7 . . . . . A B
                // 6 . . . . . A B
                // 5 . . . . . A B
                // 4 A A A A A A B
                // 3 B B B B B B B
                // 2 . . . . . . .
                // 1 . . . . . . .
                // 0 1 2 3 4 5 6 7
                // The Search explore through A and Bs in a certain order(that guarantees single traversal)
                // each visited cell is marked with true or false and added to explored Map<Cord, Boolean>
                // the current Cord is used to point to last added cell(depending on the traversing algorithm, it may not be necessary)
                // when all A and Bs are explored, the Search isComplete
                return null;
            }

            boolean isComplete() {
                return explored.size() == contour.right.length * 2 + contour.bottom.length * 2;
            }

            public DecidingContour toDecidingContour() {
                // Construct DecidingContour from explored and contour
                return null;
            }
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

    static class DecidingContour {
        Contour inner;
        Contour outer;

        DecidingContour(Contour inner, Contour outer) {
            this.inner = inner;
            this.outer = outer;
        }

        Contour getInner() {
            return inner;
        }

        Contour getOuter() {
            return outer;
        }
    }

    static class Contour {
        boolean[] right;
        boolean[] bottom;

        Contour(boolean[] right, boolean[] bottom) {
            this.right = right;
            this.bottom = bottom;
        }

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

    static class Cord {
        int x;
        int y;

        Cord(int x, int y) {
            this.x = x;
            this.y = y;
        }

        int getX() {
            return x;
        }

        int getY() {
            return y;
        }
    }
}