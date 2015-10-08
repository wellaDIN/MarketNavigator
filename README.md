This is the Java project of group 38 for the third assignment of the coures Computational Intelligence.
The objective is to make a robot able to navigate in a supemarket.

/*REPRESENTATION OF THE MARKET*/
The market will be represented as a MATRIX with 1 or 0. If two adjacent position BOTH contains a 1, then the path
across them is supposed to be available.
The matrix is defined by its width (number of COLUMNS) and height (number of ROWS), so it should look like M[height][width].
These numbers should also be found in the file representing the maze in this order. For clarity, when looping across the labyrinth, 
the index i will always be used for rows (HEIGHT), while the index j for columns (WIDTH).