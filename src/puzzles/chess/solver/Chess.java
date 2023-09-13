package puzzles.chess.solver;

import puzzles.chess.model.ChessConfig;
import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;
import puzzles.strings.StringsConfig;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * @author Jose Thomas Diaz jtd4400
 */
public class Chess {
    /**
     * Performs the primary function of the Chess class:
     * Checks if the provided arguments can satisfy the needs of the functions,
     * Using try-with resources, makes a new Scanner,
     * Makes a starting configuration based off of the information given by the file,
     * Displays the name of the file,
     * Displays the board of the starting configuration,
     * Solves the configuration,
     * Displays the total amount of configurations generated before the best solution was found,
     * Displays the total amount of unique configurations generated before the best solution was found,
     * If no solution was found, displays "No solution",
     * Else, displays all the steps associated with the shortest path between the beginning and solution configurations
     * @param args String[], the provided command line arguments
     * @throws IOException if the file cannot be found
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java Chess filename");
        } else {
            try (Scanner reader = new Scanner(args[0])) {
                Configuration starter = new ChessConfig(args[0]);
                System.out.println("File " + args[0]);
                System.out.println(starter);
                List<String> solve;
                solve = Solver.solve(starter);
                int stepNum = 0;
                System.out.println("Total configs: " + Solver.getTotalConfigs());
                System.out.println("Unique configs: " + Solver.getUniqueConfigs());
                if (solve.size() == 0) {
                    System.out.println("No solution");
                }
                for (String step: solve) {
                    System.out.println("Step " + stepNum + ": \n" + step);
                    stepNum++;
                }
            }

        }
    }
}
