package puzzles.clock;

import puzzles.common.solver.Configuration;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Jose Thomas Diaz jtd4400
 */
public class ClockConfig implements Configuration {
    /**
     * placeholder for hours of type int
     */
    private int hours;
    /**
     * placeholder for start of type int
     */
    private int start;
    /**
     * placeholder for end of type int
     */
    private int end;
    /**
     * placeholder for neighbors of type ArrayList, holds Configurations
     */
    private ArrayList<Configuration> neighbors = new ArrayList<>();

    /**
     * Constructor for ClockConfig, sets the hours, start, and end
     * @param hours int
     * @param start int
     * @param end int
     */
    public ClockConfig(int hours, int start, int end) {
        this.hours = hours;
        this.start = start;
        this.end = end;
    }

    /**
     * Checks whether or not the current configuration is a solution
     * @return boolean, true if the configuration is a solution, false otherwise
     */
    @Override
    public boolean isSolution() {
        return start == end;
    }

    /**
     * Get the current hour of the Configuration
     * @return int
     */
    public int getStart() {
        return this.start;
    }

    /**
     * Get the neighbors of the current hour
     * @return Collection<Configuration> representing the neighbors of the current hour
     */
    @Override
    public Collection<Configuration> getNeighbors() {
        int forwardConfig = start + 1;
        int backwardConfig = start - 1;
        if (forwardConfig <= hours && backwardConfig >= 1) {
            this.neighbors.add(new ClockConfig(hours, backwardConfig, end));
            this.neighbors.add(new ClockConfig(hours, forwardConfig, end));

        }
        if (forwardConfig <= hours && backwardConfig < 1) {
            this.neighbors.add(new ClockConfig(hours, hours, end));
            this.neighbors.add(new ClockConfig(hours, forwardConfig, end));

        }
        if (forwardConfig > hours && backwardConfig >= 1) {
            this.neighbors.add(new ClockConfig(hours, backwardConfig, end));
            this.neighbors.add(new ClockConfig(hours, 1, end));

        }
        return this.neighbors;
    }

    /**
     * Checks whether or not a ClockConfig is equal to another object or not
     * @param other the compared to Object that has a Configuration format
     * @return boolean
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof ClockConfig) {
            ClockConfig otherClock = (ClockConfig) other;
            return this.start == otherClock.getStart();
        }
        return false;
    }

    /**
     * Changes the hashcode of the Configuration
     * @return int
     */
    @Override
    public int hashCode() {
        return hours + start + end;
    }

    /**
     * Changes the information of the ClockConfig to a very simple format
     * @return String
     */
    @Override
    public String toString() {
        return start + "";
    }
}
