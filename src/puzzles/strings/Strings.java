package puzzles.strings;

import puzzles.clock.ClockConfig;
import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jose Thomas Diaz jtd4400
 */
public class Strings {
    /**
     * Performs the main function of the Strings Class
     * Checks the arguments for the format of the soon to be generated StringsConfig
     * Creates a starting StringsConfig with args[0] being the starting String, and args[1] being the ending String
     * Solves the StringsConfig
     * From the arguments, prints out the starting String and the ending String
     * Prints out the total number of configurations generated by the solver before a solution was found
     * Prints out the amount of unique configurations generated by the solver before a solution was found
     * Prints out each step of the shortest path found by the solver
     * @param args the arguments of the program of type String[]
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println(("Usage: java Strings start finish"));
        } else {
            Configuration starter = new StringsConfig(args[0], args[1]);
            List<String> solve;
            solve = Solver.solve(starter);
            int stepNum = 0;
            System.out.println("Start: " + args[0] + ", End: " + args[1]);
            System.out.println("Total configs: " + Solver.getTotalConfigs());
            System.out.println("Unique configs: " + Solver.getUniqueConfigs());
            if (solve.size() == 0) {
                System.out.println("No solution");
            }
            for (String step: solve) {
                System.out.println("Step " + stepNum + ": " + step);
                stepNum++;
            }
        }
    }
}
