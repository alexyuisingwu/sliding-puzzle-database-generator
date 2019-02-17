package alexyuisingwu.slidingpuzzlesolver;

//import java.util.List;
//import java.util.ArrayList;


public class Driver {

    public static void main(String[] args) {

//        for (int emptyInd = 0; emptyInd < 16; emptyInd++) {
//            for (byte[] partition : Partitions.PARTITIONS_4_4[emptyInd]) {
//                long start = System.nanoTime();
//                PartitionDatabaseGenerator generator = new PartitionDatabaseGenerator(
//                        (byte) 4,
//                        (byte) 4,
//                        (byte) emptyInd,
//                        partition);
//
//                generator.generateDatabase().printToFile();
//                long end = System.nanoTime();
//                System.out.println(String.format("%f seconds", (end - start) * Math.pow(10, -9)));
//            }
//
//        }
        PartitionDatabaseGenerator generator = new PartitionDatabaseGenerator(
                (byte) 4,
                (byte) 4,
                (byte) 0,
                new byte[]{1,2,4,5,8,9});

        timeDatabaseGeneration(generator);
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