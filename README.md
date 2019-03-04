# sliding-puzzle-database-generator

A sliding puzzle additive disjoint pattern database generator written in Java,
based on Korf and Felner's 
[Disjoint pattern database heuristics paper](https://www.researchgate.net/publication/222830183_Disjoint_pattern_database_heuristics).

This heuristic results in several times less node expansion than the Linear Conflict heuristic in A* or IDA* and generally a much faster solve-time. One thing to keep in mind however, is that this heuristic is admissible but NOT consistent, as only the lowest heuristic value for each tile configuration across all possible empty tile positions is stored in order to save on memory usage.

Currently, the only way to run this application is by writing your own Java file.

Driver.java contains an example of how to generate pattern databases, generating 6-6-3 pattern databases for 4x4 puzzles
for every possible empty position/blank tile.

## Database details

Each partition has its own pattern database. Generated pattern database files should be interpreted as 0-indexed row-major multidimensional byte arrays, where each index corresponds to the current index/position of the corresponding partition tile and each value contains the heuristic value given the partition tile configuration indicated by the indices.

For example, given a a pattern database "db" generated for the partition \[1,2,3\] in a 2x2 puzzle, db\[1,2,3\] = 0 as the cost to travel to that tile configuration from the goal state is 0. db\[0,2,3\] = 1, corresponding to the state where tile 1 has moved to index 0 and tiles 2 and 3 are still in their goal positions. 

As this database uses a sparse mapping of tile configurations to heuristic values, impossible indices (such as those where each index are the same) have default values of 255 (or -1 when interpreted as signed bytes). Generated databases will be n^k bytes large, where n = # tiles in puzzle and k = # tiles in partition.

## Warnings

If you want to try to use this project to generate pattern databases for puzzles with dimensions larger than 4x4, you may need to change the data types, data structures, and algorithms used. 

Currently, many of the data structures (most importantly, the database itself) use the "byte" data type, which in Java is signed. This can be a problem if values become larger than a signed byte's max size (127, though 255 values can be used through Java 8's Unsigned Integer API). For reference, the longest possible solution for a 4x4 puzzle is [80 single-tile moves](https://www.researchgate.net/publication/242916781_The_parallel_search_bench_ZRAM_and_its_applications).

Pattern database generation is done through a backwards breadth first search from the goal partition tile configuration. The main data structures used are a closed/visited set (BitSet), an open set (ArrayDeque), and a database (byte\[\]).

Generating the database for a 6-tile partition requires approximately ~700-800 MB max memory use. Without further optimizations, generating databases for larger partitions/puzzles may be impossible for your machine depending on its specifications. I'd advise optimizing the open set data structure first, as it is the most costly due to Java object overhead and its relative complexity (compared to the 2 other data structures which are essentially primitive arrays).
