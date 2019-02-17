package alexyuisingwu.slidingpuzzlesolver;

public class Driver {

    public static void main(String[] args) {

        for (int emptyInd = 0; emptyInd < 16; emptyInd++) {
            for (byte[] partition : Partitions.PARTITIONS_4_4[emptyInd]) {
                new PartitionDatabaseGenerator(
                        (byte) 4,
                        (byte) 4,
                        (byte) emptyInd,
                        partition).generateDatabase().printToFile();
            }

        }

    }

    private static void timeDatabaseGeneration(PartitionDatabaseGenerator generator) {

        long start = System.nanoTime();
        PatternDatabase db = generator.generateDatabase();
        long generationEnd = System.nanoTime();
        db.printToFile();
        long end = System.nanoTime();

        System.out.println(String.format("%f seconds to generate database", (generationEnd - start) * Math.pow(10, -9)));
        System.out.println(String.format("%f seconds to write database to file", (end - generationEnd) * Math.pow(10, -9)));
    }
}