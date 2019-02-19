package alexyuisingwu.slidingpuzzlesolver;

import alexyuisingwu.util.ArrayUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class PatternDatabase {

    private final byte numRows;
    private final byte numCols;
    private final byte emptyInd;

    private final byte[] partition;

    private byte[] db;
    private NDArrayHelper dbHelper;

    // multiplier for each partition value for indexing into db (row-major)
    // (simulating N-dimensional array with flattened array)
    private int[] strides;

    public PatternDatabase(byte numRows, byte numCols, byte emptyInd, byte[] partition) {
        this.numRows = numRows;
        this.numCols = numCols;

        this.emptyInd = emptyInd;

        this.partition = partition;

        int numTiles = numRows * numCols;

        // TODO: consider compressing
        // (each byte can pack 2 numbers in 16-tile puzzle, as indices go from 0 to 15)
        this.db = ArrayUtils.createByteArray((int) Math.pow(numTiles, partition.length), (byte) -1);

        int[] shape = ArrayUtils.createIntArray(partition.length, numTiles);

        this.dbHelper = new NDArrayHelper(this.db.length, shape);
    }


    // Updates pattern database with specified heuristic value if lowest value so far
    // Returns whether update successful (lowest heuristic value for tiles recorded so far)
    // NOTE: Java's byte is signed (-128 to 127)
    // (not an issue for 4x4 puzzles as maximum distance for solving entire puzzle
    // is 80)
    // something to keep in mind for comparison, indexing, /, % operations however
    public boolean update(byte[] tiles, byte heuristicValue) {

        int index = this.dbHelper.getIndex(tiles);
        int currVal = this.db[index];

        // NOTE: will be inaccurate if either value is > 127
        if (currVal == -1 || heuristicValue < currVal) {
            this.db[index] = heuristicValue;
            return true;
        }
        return false;
    }

    public void printToFile() {
        this.printToFile(String.format("./src/main/resources/databases/%d rows/%d columns/Empty %d", this.numRows, this.numCols, this.emptyInd));
    }

    public void printToFile(String filePath) {

        StringBuilder fileName = new StringBuilder(String.format("%d|", this.emptyInd));

        for (int i = 0; i < this.partition.length - 1; i++) {
            fileName.append(this.partition[i]);
            fileName.append(',');
        }

        fileName.append(this.partition[this.partition.length - 1]);
        fileName.append(".db");

        this.printToFile(filePath, fileName.toString());
    }

    public void printToFile(String filePath, String fileName) {
        File f = new File(filePath, fileName);

        try {
            Files.createDirectories(new File(filePath).toPath());
            Files.write(f.toPath(), this.db);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}