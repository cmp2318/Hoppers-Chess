package puzzles.strings;

import puzzles.clock.ClockConfig;
import puzzles.common.solver.Configuration;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Jose Thomas Diaz jtd4400
 */
public class StringsConfig implements Configuration {
    /**
     * placeholder for start of type String
     */
    private String start;
    /**
     * placeholder for end of type String
     */
    private String end;
    /**
     * placeholder for neighbors of type ArrayList, holds Configurations
     */
    private ArrayList<Configuration> neighbors = new ArrayList<>();

    /**
     * Constructor for StringsConfig, sets the start and end Strings
     * @param start String
     * @param end String
     */
    public StringsConfig(String start, String end) {
        this.start = start;
        this.end = end;
    }

    /**
     * Gets the starting String of the current StringsConfig
     * @return String
     */
    public String getStart() {
        return this.start;
    }

    /**
     * Checks whether or not the current StringsConfig is a solution
     * @return boolean, true if the StringsConfig is a solution, false otherwise
     */
    @Override
    public boolean isSolution() {
        return start.equals(end);
    }

    /**
     * Gets the neighboring configurations for the current StringsConfig
     * @return Collection<Configuration> representing the neighboring configurations
     */
    @Override
    public Collection<Configuration> getNeighbors() {
        for (int i = 0; i < start.length(); i++) {
            StringBuilder s = new StringBuilder(start);
            char forwardConfig = (char) (start.charAt(i) + 1);
            char backwardConfig = (char) (start.charAt(i) - 1);
            String normalForward = s.replace(i, i+1, String.valueOf(forwardConfig)).toString();
            String normalBackward = s.replace(i, i+1, String.valueOf(backwardConfig)).toString();
            if (forwardConfig <= 90 && backwardConfig >= 65) {
                this.neighbors.add(new StringsConfig(normalBackward, end));
                this.neighbors.add(new StringsConfig(normalForward, end));

            }
            if (forwardConfig <= 90 && backwardConfig < 65) {
                String zCase = s.replace(i, i+1, String.valueOf('Z')).toString();
                this.neighbors.add(new StringsConfig(zCase, end));
                this.neighbors.add(new StringsConfig(normalForward, end));

            }
            if (forwardConfig > 90 && backwardConfig >= 65) {
                String aCase = s.replace(i, i+1, String.valueOf('A')).toString();
                this.neighbors.add(new StringsConfig(normalBackward, end));
                this.neighbors.add(new StringsConfig(aCase, end));

            }
        }
        return this.neighbors;
    }

    /**
     * Checks whether or not the current Configuration is equal to a given Object
     * @param other Object that is in the format of StringsConfig
     * @return boolean, true is the two are equal, false otherwise
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof StringsConfig) {
            StringsConfig otherString = (StringsConfig) other;
            return this.start.equals(otherString.getStart());
        }
        return false;
    }

    /**
     * Changes the hashCode of the StringsConfig
     * @return int
     */
    @Override
    public int hashCode() {
        return start.hashCode();
    }

    /**
     * Changes the information of the StringsConfig to a very simple format
     * @return String
     */
    @Override
    public String toString() {
        return start;
    }
}
