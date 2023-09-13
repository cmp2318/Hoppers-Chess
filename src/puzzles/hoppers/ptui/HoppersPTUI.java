package puzzles.hoppers.ptui;

import puzzles.common.Observer;
import puzzles.hoppers.model.HoppersModel;
import puzzles.hoppers.solver.Hoppers;
import java.io.IOException;
import java.util.Scanner;

/**
 * This class extends Hoppers and implements Observer to create a PTUI for the Hoppers game.
 * It allows the user to interact with the game board by entering commands, and displays the game
 * board and available commands. The class also creates a HoppersModel object and adds itself
 * as an observer to the model. The PTUI is updated whenever there is a change to the model.
 * @author cmp2318
 */
public class HoppersPTUI extends Hoppers implements Observer<HoppersModel, String> {
    /**
     * instance of the model throughout the game.
     */
    private static HoppersModel model;

    /**
     * A boolean variable to track if it is time for the
     * jump move to be made.
     */
    private boolean jumpMove;

    /**
     * The row index of the starting position of a move.
     */
    private int startRow;

    /**
     * The column index of the starting position of a move.
     */
    private int startCol;

    /**
     * The filename of the file containing the board configuration.
     */
    private String filename;

    /**
     * Initializes the game with the given filename, creates a new HoppersModel,
     * and adds this PTUI as an observer to the model.
     * Also sets the jumpMove to false and displays the initial configuration
     * of the game board along with the help menu.
     * @param filename the name of the file containing the game configuration to load
     * @throws IOException if there is an error reading the file
     */
    public void init(String filename) throws IOException {
        this.filename = filename;
        model = new HoppersModel(filename);
        model.addObserver(this);

        this.jumpMove = false;

        System.out.println(model.getConfig().toString());
        displayHelp();


    }

    /**
     * This method is called by the HoppersModel
     * when there is an update to the board.
     * It prints the updated board configuration.
     * @param model the object that wishes to inform this object
     *                about something that has happened.
     * @param data optional data the server.model can send to the observer
     *
     */
    @Override
    public void update(HoppersModel model, String data) {

        // for demonstration purposes
        System.out.println(data);
        System.out.println(model.getConfig().toString());

    }

    /**
     * Displays the help menu for the game,
     * which lists the available commands and their usage.
     */
    private void displayHelp() {
        System.out.println( "h(int)              -- hint next move" );
        System.out.println( "l(oad) filename     -- load new puzzle file" );
        System.out.println( "s(elect) r c        -- select cell at r, c" );
        System.out.println( "q(uit)              -- quit the game" );
        System.out.println( "r(eset)             -- reset the current game" );
    }

    /**
     * Runs the PTUI for the Hoppers game. It repeatedly prompts the user for commands and
     * performs the corresponding action until the user quits the game.
     * It also updates the board configuration and handles errors that occur while executing commands.
     */
    public void run() {
        Scanner in = new Scanner( System.in );
        for ( ; ; ) {
            System.out.print( "> " );
            String line = in.nextLine();
            String[] words = line.split( "\\s+" );
            if (words.length > 0) {

                //gets the row and column if s, then selects the position
                if(words[0].startsWith("s")){
                    if(jumpMove){
                        int endRow = Integer.parseInt(words[1]);
                        int endCol = Integer.parseInt(words[2]);

                        model.jump(startRow, startCol, endRow, endCol);
                        jumpMove = false;
                    }
                    else{
                        startRow = Integer.parseInt(words[1]);
                        startCol = Integer.parseInt(words[2]);

                        System.out.println("Selected: (" + startRow + ", " + startCol + ")");
                        model.displayBoard();

                        jumpMove = true;
                    }

                }
                if(words[0].startsWith("r")){
                    try {
                        System.out.println("Reset: ");
                        init(filename);
                        continue;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                if(words[0].startsWith("h")){
                    model.updateBoard();
                }

                if(words[0].startsWith("l")){
                    try {
                        System.out.println("Loaded:");
                        init(words[1]);
                        continue;

                    } catch (IOException e) {
                        System.out.println("The input file is not found or is inaccessible");
                    } catch (ArrayIndexOutOfBoundsException e){
                        System.out.println("No file name provided");
                    }
                }

                if (words[0].startsWith( "q" )) {
                    break;
                }
                else {
                    displayHelp();
                }

            }
        }
    }

    /**
     * The main method that starts the game
     * @param args the String of intiial arguments provided with the
     *             run configuration
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java HoppersPTUI filename");
        } else {
            try {
                HoppersPTUI ptui = new HoppersPTUI();
                ptui.init(args[0]);
                ptui.run();


            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }
        }
    }
}
