package puzzles.clock;
import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;

import java.util.List;

/**
 * @author Jose Thomas Diaz jtd4400
 */
public class Clock {
    /**
     * Executes the main function of the Clock class
     * Checks for arguments
     * Makes a starting configuration and solves it
     * From the arguments, prints out the hours, starting hour, ending hour
     * Prints the total amount of configurations it took to find a solution, as well as the amount of unique configurations
     * Prints each step for the shortest path to the desired hour
     * @param args the arguments of the program of type String[]
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println(("Usage: java Clock start stop"));
        } else {
            Configuration starter = new ClockConfig(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
            List<String> solve;
            solve = Solver.solve(starter);
            int stepNum = 0;
            System.out.println("Hours: " + args[0] + ", Start: " + args[1] + ", End: " + args[2]);
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
