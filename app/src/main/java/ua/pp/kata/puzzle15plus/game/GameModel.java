package ua.pp.kata.puzzle15plus.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * Handle the board model
 */
public class GameModel implements Serializable{
    private int[][] mBoard; // board, whereby board[i][j] represents tile in row i and column j
    private int mStepsNumber; // Number of steps made so far
    private int mBlank_tile_R, mBlank_tile_C; // coordinates of blank tile
    private int[] mWinningState;

    /**
     * Create the board.
     * @param DIMENSION size of the board.
     */
    public GameModel(final int DIMENSION) {
        mStepsNumber = 0;

        fillWinningState(DIMENSION);
        fillRandomBoard(DIMENSION);
    }

    /**
     * Recreat game model from existing parameters
     * @param board board model
     * @param steps number of steps already done in the game
     * @param blank_tileR row of the blank tile
     * @param blank_tileC column of the blank tile
     * @param winningState board representing the winning state
     */
    public GameModel(int[][] board, int steps, int blank_tileR, int blank_tileC, int[] winningState) {
        this.mBoard = board;
        this.mStepsNumber = steps;
        this.mBlank_tile_R = blank_tileR;
        this.mBlank_tile_C = blank_tileC;
        this.mWinningState = winningState;
    }

    /**
     * Attempt to move the tile.
     * @param row current row position
     * @param column current column position
     * @return row and column of the target position, return {-1, -1} if tile doesn't move
     */
    int[] moveTile(int row, int column) {
        if (isBlankTile(row + 1, column)) {
            swapTiles(row, column, row + 1, column);
//            return DOWN;
            return new int[] {row + 1, column};
        }
        if (isBlankTile(row - 1, column)) {
            swapTiles(row, column, row - 1, column);
//            return UP;
            return new int[] {row - 1, column};
        }
        if (isBlankTile(row, column + 1)) {
            swapTiles(row, column, row, column + 1);
//            return RIGHT;
            return new int[] {row, column + 1};
        }
        if (isBlankTile(row, column - 1)) {
            swapTiles(row, column, row, column - 1);
//            return LEFT;
            return new int[] {row, column - 1};
        }
        return new int[] {-1, -1};
    }

    /**
     * Attempt to move the tile.
     * @param index index number of the tile
     * @return direction in which movement was made
     */
    int[] moveTile(int index) {
        int[] coordRC = findTile(index);
        return moveTile(coordRC[0], coordRC[1]);
    }

    /**
     * @return is the board in the won state
     */
    boolean isWon() {
        int[][] board = getBoard();
        int size = mWinningState.length;
        for (int i = 0, dim = board.length; i < size; i++) {
            int row = i / dim;
            int col = i % dim;
            if (board[row][col] != mWinningState[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param sequence of numbers to form a board of
     * @param DIM dimension
     * @return if the chosen board is solvable
     */
    private boolean isSolvable(final int[] sequence, final int DIM) {
        // Detecting unsolvable puzzles:
        // https://www.cs.princeton.edu/courses/archive/fall12/cos226/assignments/8puzzle.html

        // the number of inversions
        int inversions = 0;
        for (int i = 0, size = sequence.length; i < size - 1; i++) {
            for (int j = i + 1; j < size; j++) {
                // move after empty tile
                if (sequence[j] == 0) {
                    j = j + 1;
                    if (j == size) continue;
                }
                if (sequence[i] > sequence[j]) inversions++;
            }
        }
        // the low bit will always be set on an odd number
        if ( (DIM & 1) != 0 ) { // odd board size
            return (inversions & 1) == 0; // is the even number of inversions?
        } else { // even board size
            int sum = mBlank_tile_R + inversions;
            return (sum & 1) != 0;
        }
    }

    /**
     * Fill array with numbers in the winning order for the board.
     * @param dim dimension of the board
     */
    private void fillWinningState(int dim) {
        final int SIZE = dim * dim;
        int[] state = new int[SIZE];

        for (int i = 0; i < SIZE; i++) {
            state[i] = (i + 1) % SIZE;
        }
        setWinningState(state);
    }

    /**
     * Fill board with the random solvable number combination
     * @param DIMENSION dimension of the created board
     */
    private void fillRandomBoard(final int DIMENSION) {
        int[][] board = new int[DIMENSION][DIMENSION];
        final int SIZE = DIMENSION * DIMENSION;
        int[] boardState = new int[SIZE];

        // find solvable combination
        do {
            int size = mWinningState.length;
            ArrayList<Integer> numbers = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                numbers.add(mWinningState[i]);
            }

            // fill temp array with random numbers
            for (int i = 0; i < SIZE; i++) {
                Random generator = new Random();
                int random = generator.nextInt(numbers.size());
                boardState[i] = numbers.remove(random);

                if (boardState[i] == 0) { // blank tile
                    mBlank_tile_R = i / DIMENSION; // need to isSolvable method
                }
            }


        } while (!isSolvable(boardState, DIMENSION));

        // fill the board
        for (int i = 0; i < SIZE; i++) {
            int row = i / DIMENSION;
            int col = i % DIMENSION;
            board[row][col] = boardState[i];
            if (boardState[i] == 0) { // blank tile
                mBlank_tile_R = row;
                mBlank_tile_C = col;
            }
        }

        setBoard(board);

        if (isWon()) {
            fillRandomBoard(DIMENSION);
        }
    }

    /**
     * @param r row coordinate
     * @param c column coordinate
     * @return if the tile with given coordinates is the blank tile
     */
    private boolean isBlankTile (int r, int c) {
        return r == mBlank_tile_R && c == mBlank_tile_C;
    }

    /**
     * Swap tiles with the given coordinates on the board. Second tile is the blank tile.
     * @param fromR row of the first tile
     * @param fromC column of the first tile
     * @param toR row of the second tile
     * @param toC column of the second tile
     */
    private void swapTiles(int fromR, int fromC, int toR, int toC) {
        // update board
        int[][] board = getBoard();
        int temp = board[fromR][fromC];
        board[fromR][fromC] = board[toR][toC];
        board[toR][toC] = temp;
        setBoard(board);

        // update coordinates of blank tile
        mBlank_tile_R = fromR;
        mBlank_tile_C = fromC;

        // increase number of steps made so far
        mStepsNumber++;
    }

    /**
     * Find the tile on the board.
     * @param index number on the tile.
     * @return array with row and column of the tile, [-1][-1] if not found.
     */
    private int[] findTile(int index) {
        int[] coordinates = new int[2];
        coordinates[0] = -1;
        coordinates[1] = -1;
        int[][] board = getBoard();

        find:
        for (int i = 0, dim = board.length; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                if (board[i][j] == index) {
                    coordinates[0] = i;
                    coordinates[1] = j;
                    break find;
                }
            }
        }
        return coordinates;
    }

    public int[][] getBoard() {
        return mBoard;
    }

    private void setBoard(int[][] board) {
        mBoard = board;
    }

    private void setWinningState(int[] state) {
        mWinningState = state;
    }

    int[] getWinningState() {
        return mWinningState;
    }

    int getStepsNumber() {
        return mStepsNumber;
    }

    int[] getBlankTileRC() {
        return new int[] {mBlank_tile_R, mBlank_tile_C};
    }
}
