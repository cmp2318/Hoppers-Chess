package puzzles.hoppers.model;

import puzzles.common.solver.Configuration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * The HoppersConfig class represents a configuration for the Hoppers game.
 * It implements the Configuration interface and provides methods to check
 * if the current board state is a solution,
 * to get all neighbors of the current configuration,
 * and to find all possible jumps for a given frog at a given position.
 * @author cmp2318
 */
public class HoppersConfig implements Configuration{
    /**
     * The number of rows in the game board.
     */
    private final int numRows;
    /**
     * The number of columns in the game board.
     */
    private final int numCols;
    /**
     * The two-dimensional char array representing the game board.
     * Each element of the array represents a cell on the board.
     */
    private final char[][] board;
    /**
     * Constant representing an empty cell on the game board.
     */
    public static final char EMPTY = '.';
    /**
     * Constant representing an invalid cell on the game board.
     */
    public static final char INVALID = '*';
    /**
     * Constant representing a red frog on the game board.
     */
    public static final char RED_FROG = 'R';
    /**
     * Constant representing a green frog on the game board.
     */
    public static final char GREEN_FROG = 'G';

    /**
     * Constructor for the Hoppers game configuration.
     *
     * @param numRows the number of rows in the game board
     * @param numCols the number of columns in the game board
     * @param board the initial game board as a two-dimensional character array
     */
    public HoppersConfig(int numRows, int numCols, char[][] board)  {
        this.numRows = numRows;
        this.numCols = numCols;
        this.board = board;
    }

    /**
     * Returns a reference to the game board array.
     *
     * @return the game board as a two-dimensional character array
     */

    public char[][] getBoard() {
        return board;
    }

    /**
     * Checks if the current board state is a solution, i.e. all green pieces have been removed.
     *
     * @return true if the current board state is a solution, false otherwise
     */

    @Override
    public boolean isSolution() {
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                if (board[row][col] == 'G') {
                    return false;
                }
            }
        }
        return true;


    }


    /**
     * Overrides the method from the parent class Configuration.
     * Returns a collection of configurations that are one move away from the current configuration.
     * @return List of neighbors
     */
    @Override
    public Collection<Configuration> getNeighbors() {
        List<Configuration> neighbors = new ArrayList<>();


        for(int r = 0;r<numRows;r++){

            for (int c = 0;c<numCols;c++){
                if(board[r][c] == RED_FROG){


                    List<Configuration> newNeighbors = checkJumps(r, c, RED_FROG);
                    neighbors.addAll(newNeighbors);
                } else if (board[r][c] == GREEN_FROG) {
                    List<Configuration> newNeighbors = checkJumps(r, c, GREEN_FROG);
                    neighbors.addAll(newNeighbors);
                }
            }
        }

        return neighbors;

    }


    /**
     * Finds all possible jumps for the given frog at the given position.
     *
     * @param row the row of the frog
     * @param col the column of the frog
     * @param frog the type of the frog
     * @return a list of all possible configurations resulting from the jumps
     */
    private List<Configuration> checkJumps(int row, int col, char frog) {
        List<Configuration> neighbors = new ArrayList<>();
        int n = board.length;
        int m = board[0].length;


        // check 4 cardinal positions
        int[][] moves = { {-2, 0}, {0, 2}, {2, 0}, {0, -2} };
        for (int[] move : moves) {
            int r = row + move[0];
            int c = col + move[1];
            if (r < 0 || r >= n || c < 0 || c >= m || board[r][c] != GREEN_FROG) {
                continue;
            }
            int rr = row + 2 * move[0];
            int cc = col + 2 * move[1];
            if (rr < 0 || rr >= n || cc < 0 || cc >= m || board[rr][cc] != EMPTY) {
                continue;
            }
            char[][] neighborBoard = copyBoard(board);
            neighborBoard[row][col] = EMPTY;
            neighborBoard[r][c] = EMPTY;
            neighborBoard[rr][cc] = frog;
            neighbors.add(new HoppersConfig(numRows, numCols, neighborBoard));
        }


        // check the 4 diagonal positions
        moves = new int[][] { {-1, -1}, {-1, 1}, {1, 1}, {1, -1} };
        for (int[] move : moves) {
            int r = row + move[0];
            int c = col + move[1];
            if (r < 0 || r >= n || c < 0 || c >= m || board[r][c] != GREEN_FROG) {
                continue;
            }
            int rr = row + 2 * move[0];
            int cc = col + 2 * move[1];
            if (rr < 0 || rr >= n || cc < 0 || cc >= m || board[rr][cc] != EMPTY) {
                continue;
            }
            char[][] neighborBoard = copyBoard(board);
            neighborBoard[row][col] = EMPTY;
            neighborBoard[r][c] = EMPTY;
            neighborBoard[rr][cc] = frog;
            neighbors.add(new HoppersConfig(numRows, numCols, neighborBoard));
        }


        return neighbors;


    }

    /**
     * Returns a copy of the input board.
     *
     * @param board the board to be copied
     * @return a copy of the input board
     */
    public static char[][] copyBoard(char[][] board) {
        int numRows = board.length;
        int numCols = board[0].length;
        char[][] newBoard = new char[numRows][numCols];


        for (int i = 0; i < numRows; i++) {
            System.arraycopy(board[i], 0, newBoard[i], 0, numCols);
        }


        return newBoard;
    }


    /**
     * Reads a board configuration from a file and returns it as a 2D character array.
     * The file should have the dimensions of the board on the first line (separated by a space),
     * and the board configuration on subsequent lines, with each cell separated by a space.
     * @param filename the path to the file containing the board configuration
     * @return a 2D character array representing the board configuration, or null if an error occurs
     */
    public static char[][] createBoard(String filename){
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String[] dimensions = reader.readLine().split(" ");
            int rows = Integer.parseInt(dimensions[0]);
            int cols = Integer.parseInt(dimensions[1]);
            char[][] grid = new char[rows][cols];
            for (int row = 0; row < rows; row++) {
                String line = reader.readLine();
                for (int col = 0; col < cols; col++) {
                    grid[row][col] = line.charAt(col * 2);
                }
            }


            return grid;
        } catch (IOException e) {
            System.err.println("Error reading input file: " + e.getMessage());
            return null;
        }
    }


    /**
     * Returns a string representation of the current HoppersConfig object.
     *
     * @return a string representation of the current HoppersConfig object.
     */
    @Override
    public String toString() {
        StringBuilder resultBuilder = new StringBuilder();
        String resultString = null;


        // Add column numbers
        resultBuilder.append("    ");
        for (int c = 0; c < numCols; c++) {
            resultBuilder.append(c).append(" ");
        }
        resultBuilder.append("\n");
        resultBuilder.append("  ");
        for (int c = 0; c < numCols; c++) {
            resultBuilder.append("-").append("-");
        }
        resultBuilder.append("\n");


        // Add row numbers and board values
        for (int r = 0; r < numRows; r++) {
            resultBuilder.append(r).append(" |").append(" ");
            for (int c = 0; c < numCols; c++) {
                resultBuilder.append(board[r][c]).append(" ");
            }
            resultBuilder.append("\n");
            resultString = resultBuilder.toString();
        }

        return resultString;
    }
}



