import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Solution {
    public static void main(String[] args) {
        // print a String[]
        Arrays.stream(Solution.solution(
                new String[]{"1.11", "2.0.0", "1.2", "2", "0.1", "1.2.1", "1.1.1", "2.0"})).forEach(System.out::println);

    }

    public static String[] solution(String[] l) {
        // return Arrays.stream(l).sorted(Solution::compare).toArray(String[]::new);
        return Arrays.stream(l).map(Version::new).sorted(new Version.ScoringCompartor()).map(Version::toString).toArray(String[]::new);
    }

    // private static int compare(String version1, String version2) {
    //     int[] version1Parts = Arrays.stream(version1.split("\\.")).mapToInt(Integer::parseInt).toArray();
    //     int[] version2Parts = Arrays.stream(version2.split("\\.")).mapToInt(Integer::parseInt).toArray();
    //     int minNumOfParts = Math.min(version1Parts.length, version2Parts.length);
    //     for (int i = 0; i < minNumOfParts; i++) {
    //         int result = version1Parts[i] - version2Parts[i];
    //         if (result != 0) {
    //             return result;
    //         }
    //     }
    //     return version1Parts.length - version2Parts.length;
    // }
    static class Version {
        private static final int MAX_NUM = 100;
        private final Integer major;
        private final Integer minor;
        private final Integer revision;

        public Version(String version) {
            String[] versionArray = version.split("\\.");
            this.major = Integer.parseInt(versionArray[0]);
            this.minor = versionArray.length > 1 ? Integer.parseInt(versionArray[1]) : null;
            this.revision = versionArray.length > 2 ? Integer.parseInt(versionArray[2]) : null;
        }

        public Integer getMajor() {
            return major;
        }

        public Integer getMinor() {
            return minor;
        }

        public Integer getRevision() {
            return revision;
        }

        public int getNumOfEffectiveParts() {
            return (major == null ? 0 : 1) + (minor == null ? 0 : 1) + (revision == null ? 0 : 1);
        }

        public String toString() {
            if (major == null) {
                return "";
            } else if (minor == null) {
                return major.toString();
            } else if (revision == null) {
                return major + "." + minor;
            } else {
                return major + "." + minor + "." + revision;
            }
        }

        static class ScoringCompartor implements Comparator<Version> {
            @Override
            public int compare(Version o1, Version o2) {
                int result = score(o1) - score(o2);
                if (result == 0) {
                    return breakTieByNumOfEffectiveParts(o1, o2);
                }
                return result;
            }

            private int score(Version version) {
                return (int) (zeroIfNull(version.getMajor()) * Math.pow(MAX_NUM + 1, 2)
                        + zeroIfNull(version.getMinor()) * Math.pow(MAX_NUM + 1, 1)
                        + zeroIfNull(version.getRevision()));
            }

            private int zeroIfNull(Integer value) {
                return value == null ? 0 : value;
            }

            private int breakTieByNumOfEffectiveParts(Version o1, Version o2) {
                return o1.getNumOfEffectiveParts() - o2.getNumOfEffectiveParts();
            }
        }
    }
}
