package alexyuisingwu.slidingpuzzlesolver;

import java.util.Arrays;

public class QueueState {
    private byte emptyInd;

    // tiles[i] = new index/position of the tile that started at position partition[i],
    // where partition is the initial partition array (representing positions of tile in initial partition)
    private byte[] tiles;

    private byte distance;
    private Move lastMove;

    public QueueState(byte emptyInd, byte[] tiles, byte distance, Move lastMove) {
        this.emptyInd = emptyInd;
        this.tiles = tiles;

        this.distance = distance;
        this.lastMove = lastMove;
    }

    public QueueState(QueueState previous, Move move, byte numCols) {

        byte movedInd;

        byte oldEmpty = previous.getEmptyInd();
        byte[] oldTiles = previous.getTiles();

        switch (move) {
            case LEFT:
                movedInd = (byte) (oldEmpty + 1);
                break;
            case RIGHT:
                movedInd = (byte) (oldEmpty - 1);
                break;
            case UP:
                movedInd = (byte) (oldEmpty + numCols);
                break;
            case DOWN:
                movedInd = (byte) (oldEmpty - numCols);
                break;
            default:
                throw new RuntimeException("Invalid direction " + move);
        }

        this.emptyInd = movedInd;

        this.tiles = new byte[oldTiles.length];
        for (int i = 0; i < oldTiles.length; i++) {
            byte tile = oldTiles[i];
            if (tile == movedInd) {
                // if moved tile is in partition, swap with old empty tile
                // because that's where it's moving
                this.tiles[i] = oldEmpty;
            } else {
                this.tiles[i] = tile;
            }
        }

        this.distance = (byte) (previous.getDistance() + 1);

        this.lastMove = move;
    }

    public byte getEmptyInd() {
        return this.emptyInd;
    }

    public byte[] getTiles() {
        return this.tiles;
    }

    public byte getDistance() {
        return this.distance;
    }

    public Move getLastMove() {
        return this.lastMove;
    }


    // Prints visual representation of current state of board
    // numRows = # rows of puzzle
    // numCols = # cols of puzzle
    // initialTiles = initial partition array
    //     - this.tiles[i] = new index/position of the tile that started at position initialTiles[i]
    public void printState(int numRows, int numCols, byte[] initialTiles) {
        String[][] grid = new String[numRows][numCols];
        Arrays.stream(grid).forEach(arr -> Arrays.fill(arr, "-"));

        for (int i = 0; i < this.tiles.length; i++) {
            byte tile = tiles[i];

            int row = tile / numCols;
            int col = tile % numCols;

            grid[row][col] = String.valueOf(initialTiles[i]);
        }

        int emptyRow = this.emptyInd / numCols;
        int emptyCol = this.emptyInd % numCols;

        grid[emptyRow][emptyCol] = "X";

        System.out.println("\nDistance: " + this.distance);
        for (String[] row : grid) {
            System.out.println(Arrays.deepToString(row));
        }


    }

}