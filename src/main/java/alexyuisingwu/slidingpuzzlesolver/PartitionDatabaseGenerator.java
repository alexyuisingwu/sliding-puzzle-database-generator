package alexyuisingwu.slidingpuzzlesolver;

import java.util.*;

public class PartitionDatabaseGenerator {

    private static final Map<Move, Move> REVERSED_MOVE = Map.of(
            Move.LEFT, Move.RIGHT,
            Move.RIGHT, Move.LEFT,
            Move.UP, Move.DOWN,
            Move.DOWN, Move.UP);

    private byte numRows;
    private byte numCols;

    private byte emptyInd;
    private byte[] partition;

    private PatternDatabase db;


    // TODO: consider caching possible moves per empty index

    // NOTE: max distance storable with signed byte array is 2^7 - 1 = 127

    // (min heuristic distance, especially for small partitions,
    // likely much smaller then max bounds below)

    // (Korf 2008) Linear-time disk-based implicit graph search)
    // 	- upper bound of 4x4 puzzle = 80
    // 	- upper bound of 2x8 puzzle = 140

    // http://cubezzz.dyndns.org/drupal/?q=node/view/559
    // 	- upper bound of solving 5x5 puzzle <= 205

    // NOTE: endianness doesn't matter currently as working with byte array

    // initializes pattern db
    // - max distance < 127 per partition (see above notes)

    public PartitionDatabaseGenerator(byte numRows, byte numCols,
                                      byte emptyInd, byte[] partition) {
        this.numRows = numRows;
        this.numCols = numCols;

        this.emptyInd = emptyInd;
        this.partition = partition;

        this.db = new PatternDatabase(numRows, numCols, emptyInd, partition);
    }

    // NOTE: not a function of QueueState, because
    // QueueState doesn't track #rows or #cols to save space
    private List<QueueState> getNeighbors(QueueState state, Move lastMove) {
        byte emptyInd = state.getEmptyInd();

        List<QueueState> neighbors = new ArrayList<>();

        byte emptyRow = (byte) Integer.divideUnsigned(emptyInd, this.numCols);
        byte emptyCol = (byte) Integer.remainderUnsigned(emptyInd, this.numCols);

        Move reversedMove;

        if (lastMove == null) {
            reversedMove = null;
        } else {
            reversedMove = REVERSED_MOVE.get(lastMove);
        }

        if (emptyRow > 0 && reversedMove != Move.DOWN) {
            neighbors.add(new QueueState(state, Move.DOWN, this.numCols));
        }
        if (emptyRow < this.numRows - 1 && reversedMove != Move.UP) {
            neighbors.add(new QueueState(state, Move.UP, this.numCols));
        }
        if (emptyCol > 0 && reversedMove != Move.RIGHT) {
            neighbors.add(new QueueState(state, Move.RIGHT, this.numCols));
        }
        if (emptyCol < this.numCols - 1 && reversedMove != Move.LEFT) {
            neighbors.add(new QueueState(state, Move.LEFT, this.numCols));
        }

        return neighbors;
    }

    // NOTE: make sure to tune memory limit for size of puzzle/partition (4x4-6-partition => ~2 GB)
    public PatternDatabase generateDatabase() {

        int numTiles = this.numRows * this.numCols;

        VisitedSet visited = new VisitedSet(numTiles, this.partition.length);

        visited.add(this.emptyInd, this.partition);

        // TODO: consider serializing and compressing queue when inserting and decoding on pop
        Queue<QueueState> q = new ArrayDeque<>();
        q.add(new QueueState(this.emptyInd, this.partition, (byte) 0, null));

//        long maxMemoryUsed = 0;

        while (!q.isEmpty()) {
            QueueState curr = q.remove();

//            curr.printState(numRows, numCols, this.partition);

            this.db.update(curr.getTiles(), curr.getDistance());

            for (QueueState neighbor : this.getNeighbors(curr, curr.getLastMove())) {

                // if neighbor added to visited successfully (not already there), add neighbor to queue
                if (visited.add(neighbor.getEmptyInd(), neighbor.getTiles())) {

                    q.add(neighbor);

                }
            }
//            maxMemoryUsed = Math.max(
//                    maxMemoryUsed,
//                    Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
        }

//        System.out.println(String.format("Max memory used: %d B", maxMemoryUsed));
        return this.db;
    }

    // NOTE: Only a proof of concept (not practical for larger partition/puzzle sizes)
    // Explanation:
    // - Branching factor of 4x4 puzzle when avoiding reversing moves = 2
    // - IDFS can't track visited states without devolving to BFS
    // - Many cycles in sliding puzzles (reversing move only trivial example)
    // Example:
    // - 4x4 puzzle with 5-partition {1,2,4,5,8}
    // - Max distance between valid state and starting state = 48 (taken from BFS database generation)
    // - IDFS time complexity quickly makes it unusable:
    //      - On 1.3 GHz Intel Core i5 CPU machine, 2GB max heap size:
    //      - depthLimit = 22 on 4x4 5-partition {1,2,4,5,8} = 14 seconds, 50 MB
    //      - depthLimit = 23 on 4x4 5-partition {1,2,4,5,8} = 28 seconds, 60 MB
    //      - keeping 2* increase (matching branching factor), 23*(48-23)^2 ~ 30 years
    // Additional problems:
    // - IDFS doesn't know when to stop searching (because of cycles, graph never runs out of nodes)
    // - While you can use upper bound of solution length, hard to find tight upper bound
    // - Given branching factor, you have a small practical upper bound to work with (see above)
    public PatternDatabase generateDatabaseIDFS() {
        // TODO: configure depthLimit based on tightest upper bound for puzzle solution length
        // (non-priority, as IDFS not useful compared to BFS for database generation)
        return generateDatabaseIDFS(24);
    }

    // TODO: now that distance doesn't track # of moves (only increases for tiles in partition), update IDFS to track depth
    // generates Pattern Database using IDFS, stopping once depthLimit reached
    public PatternDatabase generateDatabaseIDFS(int depthLimit) {

        Deque<QueueState> stack = new ArrayDeque<>();

        stack.push(new QueueState(this.emptyInd, this.partition, (byte) 0, null));

        long maxMemoryUsed = 0;

        // tracks depth to explore to in current iteration of IDFS
        int depth = 0;

        while (depth < depthLimit) {
            while (!stack.isEmpty()) {
                // NOTE: QueueState doesn't need to store distance or lastMove in IDFS
                // reusing QueueState for convenience (as longest queue = depthLimit so memory use low)
                QueueState curr = stack.pop();

                // only try to update if distance == depth (as otherwise already explored)
                // NOTE: can only use curr.getDistance() as current depth of exploration
                // because all edge costs = 1
                if (curr.getDistance() == depth) {
                    this.db.update(curr.getTiles(), curr.getDistance());
                }

                if (curr.getDistance() < depth) {

                    for (QueueState neighbor : this.getNeighbors(curr, curr.getLastMove())) {
                        stack.push(neighbor);
                    }
                }

                maxMemoryUsed = Math.max(
                        maxMemoryUsed,
                        Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
            }
            depth++;
            // NOTE: clearing instead of creating new stack as stack should only increase in length
            // between iterations.
            // Avoids allocating new memory and expanding backing array
            stack.clear();
            stack.push(new QueueState(this.emptyInd, this.partition, (byte) 0, null));
        }

        System.out.println(String.format("Max memory used: %d B", maxMemoryUsed));
        return this.db;
    }
}