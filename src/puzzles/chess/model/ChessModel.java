package puzzles.chess.model;

import puzzles.chess.solver.Chess;
import puzzles.common.Observer;
import puzzles.common.solver.Configuration;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static puzzles.common.solver.Solver.solve;

public class ChessModel {
    /** the collection of observers of this model */
    private final List<Observer<ChessModel, String>> observers = new LinkedList<>();

    /** the current configuration */
    private ChessConfig currentConfig;
    private File currentFile;
    private static int amtSelect;
    private String selected;
    private static ArrayList<int[]> loc = new ArrayList<>();
    public enum ChessState {
        SOLVEABLE,
        NO_SOLUTION,
        ILLEGAL_MOVE,
        NEW_GAME,
        INVALID_FILE,
        END,
        PC_SELECTED,
        WRONG_PIECE,
        CAPTURED,
        RELOAD
    }
    private static final EnumMap<ChessState, String> STATES =
            new EnumMap<>(Map.of(
                    ChessState.SOLVEABLE, "Next step!",
                    ChessState.NO_SOLUTION, "There is no solution to the current puzzle!",
                    ChessState.ILLEGAL_MOVE, "Can't capture from",
                    ChessState.NEW_GAME, "New game!",
                    ChessState.INVALID_FILE, "Failed to load:",
                    ChessState.END, "Game Over",
                    ChessState.PC_SELECTED, "Selected",
                    ChessState.WRONG_PIECE, "There is no piece there!",
                    ChessState.CAPTURED, "Captured",
                    ChessState.RELOAD, "Puzzle Reset!"
            ));
    private static ChessState state;

    /**
     * The view calls this to add itself as an observer.
     *
     * @param observer the view
     */
    public void addObserver(Observer<ChessModel, String> observer) {
        this.observers.add(observer);
    }

    /**
     * The model's state has changed (the counter), so inform the view via
     * the update method
     */
    private void alertObservers(String data) {
        for (var observer : observers) {
            observer.update(this, data);
        }
    }

    public ChessModel(String filename) throws IOException {
        this.currentConfig = new ChessConfig(filename);
        this.currentFile = new File(filename);
        this.state = ChessState.SOLVEABLE;
    }
    public void hint() {
        try {
            int[] dims = currentConfig.getDimensions();
            String next = solve(this.currentConfig).get(1);
            String[] b = next.split("\n");
            String[][] board = new String[dims[0]][dims[1]];
            int i = 0;
            for (String s: b) {
                board[i] = s.split(" ");
                i++;
            }
            this.currentConfig.updateBoard(board);
        } catch (NullPointerException n) {
            this.state = ChessState.NO_SOLUTION;
        } finally {
            this.alertObservers(STATES.get(state));
        }




    }
    public void load(File file) throws IOException {
        this.state = ChessState.NEW_GAME;
        try {
            this.currentConfig = new ChessConfig(file.toString());
            this.currentFile = file;

        } catch (IOException e) {
            this.state = ChessState.INVALID_FILE;

        } finally {
            this.alertObservers(this.state.toString());
        }

    }
    public void select(int p1, int p2) {
        state = ChessState.SOLVEABLE;
        ChessConfig c = new ChessConfig(currentConfig);
        if (amtSelect == 1) {
            if (c.validMove(loc.get(0), new int[]{p1,p2}, selected)) {
                state = ChessState.CAPTURED;
            } else {
                state = ChessState.ILLEGAL_MOVE;
            }
            if (state == ChessState.CAPTURED) {
                this.currentConfig = new ChessConfig(currentConfig, selected, loc.get(0), new int[]{p1,p2});
                this.alertObservers(state.name() + " (" + p1 + ", " + p2 + ")");
            } if (state == ChessState.ILLEGAL_MOVE) {
                this.alertObservers(state.toString());
            }
            loc.clear();
            amtSelect--;
            return;
        } if (amtSelect == 0) {
            if (!this.currentConfig.pieceAt(new int[]{p1, p2}).equals(".")) {
                this.selected = this.currentConfig.pieceAt(new int[]{p1, p2});
                loc.add(new int[]{p1,p2});
                state = ChessState.PC_SELECTED;
                this.alertObservers(ChessState.PC_SELECTED.name() + "(" + p1 + ", " + p2 + ")");
                amtSelect++;
            } else {
                this.alertObservers(ChessState.WRONG_PIECE.name());
            }
        }

    }
    public void quit() {
        this.state = ChessState.END;
        this.alertObservers(this.state.toString());

    }
    public void fail(String f) {
        this.state = ChessState.INVALID_FILE;
        this.alertObservers(this.state.name() + " " + f);
    }
    public void reset() throws IOException {
        this.load(currentFile);
        this.state = ChessState.RELOAD;
        this.alertObservers(this.state.name());
    }
    @Override
    public String toString() {
        return this.currentConfig.toString();
    }
}
