package puzzles.hoppers.model;

import puzzles.common.Observer;
import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * The HoppersModel class represents the model of the Hoppers game, which
 * is responsible for updating the state of the game and notifying the observers
 * when the game state changes. This class contains methods to load the game board,
 * add and alert observers, display and update the board, and perform a jump
 * on the board. It uses the HoppersConfig and Solver classes to represent and solve
 * the game board.
 * @author cmp2318
 */
public class HoppersModel {
    /** the collection of observers of this model */
    private final List<Observer<HoppersModel, String>> observers = new LinkedList<>();

    /** the current configuration */
    private HoppersConfig currentConfig;
    /**
     * The game board for the Hoppers game.
     */
    private char[][] board ;
    /**
     * The number of rows on the game board.
     */
    private int rows;
    /**
     * The number of columns on the game board.
     */
    private int cols;

    /**
     * Constructs a HoppersModel object and loads the board from the given file.
     *
     * @param filename the name of the file containing the board configuration
     */
    public HoppersModel(String filename) {

        board = loadBoard(filename);
        currentConfig = new HoppersConfig(rows, cols, board);

        alertObservers("Loaded: " + filename);
    }

    /**
     * The view calls this to add itself as an observer.
     *
     * @param observer the view
     */
    public void addObserver(Observer<HoppersModel, String> observer) {
        this.observers.add(observer);
    }

    /**
     * Returns the current configuration of the game board.
     * @return the current configuration of the game board
     */
    public Configuration getConfig(){
        return currentConfig;
    }

    /**
     * Returns a copy of the game board.
     * @return a copy of the game board
     */
    public char[][] getBoard() {
        return board;
    }

    /**
     * Displays the current configuration of the board by
     * calling the toString() method of the currentConfig object.
     */
    public void displayBoard(){
        System.out.println(currentConfig.toString());
    }

    /**
     * Updates the board by attempting to solve the current
     * configuration and making the next move.
     * If the current configuration is the solution,
     * the method will alert the observers that the game has been won.
     * If there is no solution to the puzzle, the method will alert the observers.
     * If there is a valid next step, the method will update the board with the new
     * configuration and alert the observers.
     */
    public void updateBoard(){
        if (currentConfig.isSolution()){
            alertObservers("This is the solution: ");
            return;
        }

        List<String> path;

        Solver.clearQueue();

        try{
            path = Solver.solve(currentConfig);
            String nextStep = path.get(1);
            String[] lines = nextStep.split("\n");
            char[][] newBoard = new char[rows][cols];

            for (int i = 2; i < lines.length; i++) {
                String[] row = lines[i].split("\\| ")[1].split(" ");
                for (int j = 0; j < row.length; j++) {
                    newBoard[i-2][j] = row[j].charAt(0);
                }
            }
            currentConfig = new HoppersConfig(rows, cols, newBoard);
            board = currentConfig.getBoard();
            alertObservers("Next Step:");
        }
        catch (IndexOutOfBoundsException | NullPointerException e){
            alertObservers("No Solution to this puzzle");
        }



    }

    /**
     * Attempts to perform a jump on the board given the starting and ending positions.
     * The method checks if the selected frog can perform the
     * jump and if the destination is valid.
     * If any of the checks fail, the method will alert
     * the observers with an appropriate message.
     * If the jump is valid, the method will update the board with
     * the new configuration and alert the observers.
     * @param startRow the row number of the starting position
     * @param startCol the column number of the starting position
     * @param endRow the row number of the ending position
     * @param endCol the column number of the ending position
     */
    public void jump(int startRow, int startCol, int endRow, int endCol){
        char[][] board = currentConfig.getBoard();

        char start = board[startRow][startCol];
        int jumpedRow = (startRow+endRow)/2;
        int jumpedCol = (startCol+endCol)/2;

        if(Math.abs(endRow-startRow) == 1){
            alertObservers("Not a valid jump");
            return;
        }

        if ((start!=HoppersConfig.RED_FROG && start!=HoppersConfig.GREEN_FROG)){
            alertObservers("Invalid Selection");
            return;
        }
        if(board[jumpedRow][jumpedCol] != HoppersConfig.RED_FROG &&
                board[jumpedRow][jumpedCol] != HoppersConfig.GREEN_FROG){

            alertObservers("Cannot jump an empty space");
            return;
        }
        if(board[endRow][endCol] == HoppersConfig.GREEN_FROG ||
                board[endRow][endCol] == HoppersConfig.RED_FROG ||
                board[endRow][endCol] == HoppersConfig.INVALID){

            alertObservers("Not a valid space to jump to");
            return;

        }
        if (board[jumpedRow][jumpedCol] == HoppersConfig.RED_FROG){
            alertObservers("Cannot jump the red frog");
            return;
        }

        board[startRow][startCol] = HoppersConfig.EMPTY;
        board[jumpedRow][jumpedCol] = HoppersConfig.EMPTY;
        board[endRow][endCol] = start;


        String result = "Jumped from (" + startRow + ", " + startCol + ") to (" +
                endRow + ", " + endCol + ")" + System.lineSeparator();

        alertObservers(result);

    }



    /**
     * The model's state has changed (the counter), so inform the view via
     * the update method
     */
    private void alertObservers(String msg) {
        for (var observer : observers) {

            observer.update(this, msg);
        }
    }

    /**
     * Reads a board from a file and returns a 2D char array representing the board.
     * The file should have the first line as the dimensions of the board, followed by the
     * characters representing the board layout.
     * @param filename the name of the file containing the board layout
     * @return a 2D char array representing the board
     */
    public char[][] loadBoard(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String[] dimensions = reader.readLine().split(" ");
            rows = Integer.parseInt(dimensions[0]);
            cols = Integer.parseInt(dimensions[1]);

        }
        catch (IOException e) {
            System.err.println("Error reading input file: " + e.getMessage());
        }

        return HoppersConfig.createBoard(filename);
    }

}
