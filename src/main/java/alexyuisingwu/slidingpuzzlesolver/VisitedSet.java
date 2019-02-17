package alexyuisingwu.slidingpuzzlesolver;

import alexyuisingwu.util.ArrayUtils;

import java.util.BitSet;

public class VisitedSet{

    // HashSet: 16 Permute 7 entries * 93 bytes/entry = ~5.4 GB
    // boolean ndarray: 16^7 entries * 1 byte/entry = ~268 MB
    // BitSet ndarray: 16^6 entries * 1 bit/entry = ~34 MB (less CPU efficient than boolean[] on average however)
    private final BitSet visited;
    private final NDArrayHelper visitedHelper;

    private int size;

    public VisitedSet(int numTiles, int partitionLength) {

        // # entries = numTiles ^ (partitionLength + 1)
        this.visited = new BitSet((int) Math.pow(numTiles, partitionLength + 1));

        int[] shape = ArrayUtils.createIntArray(partitionLength + 1, numTiles);

        this.visitedHelper = new NDArrayHelper(this.visited.length(), shape);
        this.size = 0;
    }

    public boolean add(byte emptyInd, byte[] partition) {
        // NOTE: new byte[] not created for indexing because
        // allocating memory + copying over expensive (as add() called a lot)
        // NOTE: function not added to NDArrayHelper as adding single byte or varargs + array seems messy
        int index = this.visitedHelper.getIndex(partition) + emptyInd;
        if (!this.visited.get(index)) {
            this.visited.set(index, true);
            this.size++;
            return true;
        }
        return false;
    }

    public boolean contains(byte emptyInd, byte[] partition) {
        return this.visited.get(this.visitedHelper.getIndex(partition) + emptyInd);
    }

    public int size() {
        return this.size;
    }
}
