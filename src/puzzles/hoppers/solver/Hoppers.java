package puzzles.hoppers.solver;

import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;
import puzzles.hoppers.model.HoppersConfig;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * The Hoppers class contains methods to create a board from a
 * file and solve the Hoppers game using the Solver class.
 * @author cmp2318
 */
public class Hoppers {

    /**
     * Constructs a new instance of the Hoppers class.
     */
    public Hoppers(){
    }

    /**
     * Reads in a file containing the board dimensions and character representation
     * of the board and constructs the board.
     * @param filename the name of the file to be read in
     * @return the constructed board as a 2D char array, or null if an error
     * occurs during file reading
     */
    public char[][] createBoard(String filename){
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
     * Main method of the Hoppers class, which reads in a file, creates a board, and solves the Hoppers game using the Solver class.
     * @param args an array of command-line arguments that contains the name of the file to be read in
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Hoppers filename");
        }
        else {
            String filename = args[0];

            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                String[] dimensions = reader.readLine().split(" ");
                System.out.println("File: " + filename);
                int rows = Integer.parseInt(dimensions[0]);
                int cols = Integer.parseInt(dimensions[1]);

                Hoppers hoppers = new Hoppers();

                char[][] board = hoppers.createBoard(filename);

                Configuration starter = new HoppersConfig(rows, cols, board );

                List<String> solve;

                solve = Solver.solve(starter);

                int stepNum = 0;
                System.out.println("Total configs: " + Solver.getTotalConfigs());
                System.out.println("Unique configs: " + Solver.getUniqueConfigs());
                if (solve.size() == 0) {
                    System.out.println("No solution");
                }
                for (String step: solve) {
                    System.out.println("Step " + stepNum + ": ");
                    System.out.println(step);
                    stepNum++;
                }
            } catch (IOException e) {
                System.err.println("Error reading input file: " + e.getMessage());
            }
        }
    }
}
