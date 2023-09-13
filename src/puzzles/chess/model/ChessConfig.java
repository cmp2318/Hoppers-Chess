package puzzles.chess.model;

import puzzles.common.solver.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.*;


/**
 * @author Jose Thomas Diaz jtd4400
 */
public class ChessConfig implements Configuration {
    /**
     * placeholder for the static variable numRoms of type int
     */
    private static int numRows;
    /**
     * placeholder for the static variable numCols of type int
     */
    private static int numCols;
    /**
     * placeholder for board, which represents the current config's board, of type String[][]
     */
    private String[][] board;
    /**
     * placeholder for numPieces, is used to check if the configuration is the solution
     */
    private int numPieces;
    /**
     * placeholder for locations of type HashMap, holds Strings as keys and Integer arrays as values
     */
    private static HashMap<String, int[]> locations;

    /**
     * Constructor for ChessConfig when it is run the first time
     * Sets: locations, numRows, numCols, board, and numPieces
     * @param filename String, the file to open
     * @throws IOException if the file is not found
     */
    public ChessConfig(String filename) throws IOException {
        locations = new LinkedHashMap<>();
        File fn = new File(filename);
        try (Scanner read = new Scanner(fn)) {
            String[] rowCols = read.nextLine().split(" ");
            numRows = Integer.parseInt(rowCols[0]);
            numCols = Integer.parseInt(rowCols[1]);
            board = new String[numRows][numCols];
            for (int i = 0; i < numRows; i++) {
                String[] row = read.nextLine().split(" ");
                int d = 0;
                for (String s: row) {
                    if (!s.equals(".")) {
                        numPieces++;
                    }
                    locations.put(s, new int[]{i, d});
                    d++;
                }
                board[i] = row;
            }
        }
    }
    public ChessConfig(ChessConfig other) {
        String [][] b = new String[numRows][numCols];
        for (int i = 0; i < numRows; i++) {
            System.arraycopy(other.board[i], 0, b[i], 0, numCols);
        }
        this.board = b;
    }
    /**
     * Copy constructor for ChessConfig
     * Copies and changes the: board and numPieces
     * @param other the parent configuration that is being copied
     * @param taker the chess piece that will capture another piece
     * @param capturer the location of the taker
     * @param capture the location of where the taker is going to move to
     */
    public ChessConfig(ChessConfig other, String taker, int[] capturer, int[] capture) {
        String [][] b = new String[numRows][numCols];
        for (int i = 0; i < numRows; i++) {
            System.arraycopy(other.board[i], 0, b[i], 0, numCols);
        }
        b[capturer[0]][capturer[1]] = ".";
        b[capture[0]][capture[1]] = taker;
        this.board = b;
        this.numPieces = other.numPieces - 1;
    }

    /**
     * Checks if the current configuration is a solution
     * @return boolean, true if it is, false otherwise
     */
    @Override
    public boolean isSolution() {
        return numPieces == 1;
    }

    /**
     * Performs a deep copy on the board that can change values on the board
     * @param board String[][], the board that is being copied
     * @param capturer int[], the location of the taker
     * @param capture int[], the location that the taker is going to move to
     * @param taker String, the chess piece that is going to take another piece
     * @return String[][], the new board
     */
    public String[][] copyBoard(String[][] board, int[] capturer, int[] capture, String taker) {
        String[][] b = board.clone();
        b[capturer[0]][capturer[1]] = ".";
        b[capture[0]][capture[1]] = taker;
        return b;
    }
    public int[] getDimensions() {
        return new int[]{numRows, numCols};
    }
    public String pieceAt(int[] loc) {
        return board[loc[0]][loc[1]];
    }
    /**
     * Gives all possible moves for the king piece based on the current location
     * @param board String[][], the chess board
     * @param position int[], the location of the king piece
     * @return ArrayList<Configuration>, every possible move that the king can make to capture another piece
     */
    public Collection<Configuration> kingMoves(String[][] board, int[] position) {
        HashSet<Configuration> kingCaps = new HashSet<>();
        ArrayList<int[]> possibleMoves = new ArrayList<>();
        int defRow = position[0];
        int defCol = position[1];
        possibleMoves.add(new int[]{defRow-1, defCol-1});
        possibleMoves.add(new int[]{defRow-1, defCol});
        possibleMoves.add(new int[]{defRow-1, defCol+1});
        possibleMoves.add(new int[]{defRow, defCol-1});
        possibleMoves.add(new int[]{defRow, defCol+1});
        possibleMoves.add(new int[]{defRow+1, defCol-1});
        possibleMoves.add(new int[]{defRow+1, defCol});
        possibleMoves.add(new int[]{defRow+1, defCol+1});
        for (int i = 0; i < 8; i++) {
            try {
                if (!board[possibleMoves.get(i)[0]][possibleMoves.get(i)[1]].equals(".")) {
                    kingCaps.add(new ChessConfig(this, "K", position, possibleMoves.get(i)));
                }
            } catch (IndexOutOfBoundsException e) {}
        }
        return kingCaps;
    }
    public void capture(int[] p1, int[] p2, String pc) {
        this.board[p1[0]][p1[1]] = ".";
        this.board[p2[0]][p2[1]] = pc;
    }
    public boolean validMove(int[] p1, int[] p2, String pc) {
        Configuration potCap = new ChessConfig(this, pc, p1, p2);
        if (pc.equals("B")) {
            return bishopMoves(copyBoard(board, p1, p2, pc), p1).contains(potCap);
        } if (pc.equals("K")) {
            return kingMoves(copyBoard(board, p1, p2, pc), p1).contains(potCap);
        } if (pc.equals("N")) {
            return knightMoves(copyBoard(board, p1, p2, pc), p1).contains(potCap);
        } if (pc.equals("P")) {
            return pawnMoves(copyBoard(board, p1, p2, pc), p1).contains(potCap);
        } if (pc.equals("Q")) {
            return queenMoves(copyBoard(board, p1, p2, pc), p1).contains(potCap);
        } if (pc.equals("R")) {
            return rookMoves(copyBoard(board, p1, p2, pc), p1).contains(potCap);
        }
        return false;
    }


    /**
     * Gives all possible moves for the bishop piece based on the current location
     * @param board String[][], the chess board
     * @param position int[], the location of the bishop piece
     * @return ArrayList<Configuration>, every possible move that the bishop can make to capture another piece
     */
    public Collection<Configuration> bishopMoves(String[][] board, int[] position) {
        HashSet<Configuration> bishopCaps = new HashSet<>();
        ArrayList<int[]> possibleMoves = new ArrayList<>();
        int defRow = position[0];
        int defCol = position[1];
        possibleMoves.add(new int[]{defRow-1, defCol-1});
        possibleMoves.add(new int[]{defRow-1, defCol+1});
        possibleMoves.add(new int[]{defRow+1, defCol-1});
        possibleMoves.add(new int[]{defRow+1, defCol+1});
        for (int i = 0; i < 4; i++) {
            try {
                int r = 0;
                int c = 0;
                String diagMove = board[possibleMoves.get(i)[0]][possibleMoves.get(i)[1]];
                int[] futMoves = new int[]{possibleMoves.get(i)[0], possibleMoves.get(i)[1]};
                while (diagMove.equals(".")) {
                    if (i == 0) {
                        r--;
                        c--;
                        diagMove = board[possibleMoves.get(i)[0]+r][possibleMoves.get(i)[1]+c];
                        futMoves[0] -= 1;
                        futMoves[1] -= 1;
                    }
                    if (i == 1) {
                        r--;
                        c++;
                        diagMove = board[possibleMoves.get(i)[0]+r][possibleMoves.get(i)[1]+c];
                        futMoves[0] -= 1;
                        futMoves[1] += 1;
                    }
                    if (i == 2) {
                        r++;
                        c--;
                        diagMove = board[possibleMoves.get(i)[0]+r][possibleMoves.get(i)[1]+c];
                        futMoves[0] += 1;
                        futMoves[1] -= 1;
                    }
                    if (i == 3) {
                        r++;
                        c++;
                        diagMove = board[possibleMoves.get(i)[0]+r][possibleMoves.get(i)[1]+c];
                        futMoves[0] += 1;
                        futMoves[1] += 1;
                    }
                }
                bishopCaps.add(new ChessConfig(this, "B", position, futMoves));
            } catch (IndexOutOfBoundsException e) {}
        }
        return bishopCaps;
    }

    /**
     * Gives all possible moves for the rook based on the current location
     * @param board String[][], the chess board
     * @param position int[], the location of the rook piece
     * @return ArrayList<Configuration>, every possible move that the rook can make in order to capture a piece
     */
    public Collection<Configuration> rookMoves(String[][] board, int[] position) {
        HashSet<Configuration> rookCaps = new HashSet<>();
        ArrayList<int[]> possibleMoves = new ArrayList<>();
        int defRow = position[0];
        int defCol = position[1];
        possibleMoves.add(new int[]{defRow-1, defCol});
        possibleMoves.add(new int[]{defRow, defCol-1});
        possibleMoves.add(new int[]{defRow, defCol+1});
        possibleMoves.add(new int[]{defRow+1, defCol});
        for (int i = 0; i < 4; i++) {
            try {
                int r = 0;
                int c = 0;
                String diagMove = board[possibleMoves.get(i)[0]][possibleMoves.get(i)[1]];
                int[] futMove = new int[]{possibleMoves.get(i)[0], possibleMoves.get(i)[1]};
                while (diagMove.equals(".")) {
                    if (i == 0) {
                        r--;
                        diagMove = board[possibleMoves.get(i)[0]+r][possibleMoves.get(i)[1]];
                        futMove[0] -= 1;
                    }
                    if (i == 1) {
                        c--;
                        diagMove = board[possibleMoves.get(i)[0]][possibleMoves.get(i)[1]+c];
                        futMove[1] -= 1;
                    }
                    if (i == 2) {
                        c++;
                        diagMove = board[possibleMoves.get(i)[0]][possibleMoves.get(i)[1]+c];
                        futMove[1] += 1;
                    }
                    if (i == 3) {
                        r++;
                        diagMove = board[possibleMoves.get(i)[0]+r][possibleMoves.get(i)[1]];
                        futMove[0] += 1;

                    }
                }
                rookCaps.add(new ChessConfig(this, "R", position, futMove));


            } catch (IndexOutOfBoundsException e) {}
        }
        return rookCaps;
    }

    /**
     * Gives all possible moves that the queen can make based on the current position
     * @param board String[][], the chess board
     * @param position int[], the position of the queen piece
     * @return ArrayList<Configuration>, every possible move that the queen can make in order to capture a piece
     */
    public Collection<Configuration> queenMoves(String[][] board, int[] position) {
        HashSet<Configuration> queenCaps = new HashSet();
        HashSet<Configuration> bishopCaps = new HashSet<>();
        ArrayList<int[]> possibleMoves = new ArrayList<>();
        int defRow = position[0];
        int defCol = position[1];
        possibleMoves.add(new int[]{defRow-1, defCol-1});
        possibleMoves.add(new int[]{defRow-1, defCol+1});
        possibleMoves.add(new int[]{defRow+1, defCol-1});
        possibleMoves.add(new int[]{defRow+1, defCol+1});
        for (int i = 0; i < 4; i++) {
            try {
                int r = 0;
                int c = 0;
                String diagMove = board[possibleMoves.get(i)[0]][possibleMoves.get(i)[1]];
                int[] bishopFutMoves = new int[]{possibleMoves.get(i)[0], possibleMoves.get(i)[1]};
                while (diagMove.equals(".")) {
                    if (i == 0) {
                        r--;
                        c--;
                        diagMove = board[possibleMoves.get(i)[0]+r][possibleMoves.get(i)[1]+c];
                        bishopFutMoves[0] -= 1;
                        bishopFutMoves[1] -= 1;
                    }
                    if (i == 1) {
                        r--;
                        c++;
                        diagMove = board[possibleMoves.get(i)[0]+r][possibleMoves.get(i)[1]+c];
                        bishopFutMoves[0] -= 1;
                        bishopFutMoves[1] += 1;
                    }
                    if (i == 2) {
                        r++;
                        c--;
                        diagMove = board[possibleMoves.get(i)[0]+r][possibleMoves.get(i)[1]+c];
                        bishopFutMoves[0] += 1;
                        bishopFutMoves[1] -= 1;
                    }
                    if (i == 3) {
                        r++;
                        c++;
                        diagMove = board[possibleMoves.get(i)[0]+r][possibleMoves.get(i)[1]+c];
                        bishopFutMoves[0] += 1;
                        bishopFutMoves[1] += 1;
                    }
                }
                bishopCaps.add(new ChessConfig(this, "Q", position, bishopFutMoves));
            } catch (IndexOutOfBoundsException e) {}
        }
        ArrayList<Configuration> rookCaps = new ArrayList<>();
        possibleMoves.add(new int[]{defRow-1, defCol});
        possibleMoves.add(new int[]{defRow, defCol-1});
        possibleMoves.add(new int[]{defRow, defCol+1});
        possibleMoves.add(new int[]{defRow+1, defCol});
        for (int i = 4; i < 8; i++) {
            try {
                int r = 0;
                int c = 0;
                String diagMove = board[possibleMoves.get(i)[0]][possibleMoves.get(i)[1]];
                int[] rookFutMoves = new int[]{possibleMoves.get(i)[0], possibleMoves.get(i)[1]};
                while (diagMove.equals(".")) {
                    if (i == 4) {
                        r--;
                        diagMove = board[possibleMoves.get(i)[0]+r][possibleMoves.get(i)[1]+c];
                        rookFutMoves[0] -= 1;
                    }
                    if (i == 5) {
                        c--;
                        diagMove = board[possibleMoves.get(i)[0]+r][possibleMoves.get(i)[1]+c];
                        rookFutMoves[1] -= 1;
                    }
                    if (i == 6) {
                        c++;
                        diagMove = board[possibleMoves.get(i)[0]+r][possibleMoves.get(i)[1]+c];
                        rookFutMoves[1] += 1;
                    }
                    if (i == 7) {
                        r++;
                        diagMove = board[possibleMoves.get(i)[0]+r][possibleMoves.get(i)[1]+c];
                        rookFutMoves[0] += 1;
                    }
                }
                rookCaps.add(new ChessConfig(this, "Q", position, rookFutMoves));
            } catch (IndexOutOfBoundsException e) {}
        }
        queenCaps.addAll(rookCaps);
        queenCaps.addAll(bishopCaps);
        return queenCaps;
    }
    public void updateBoard(String[][] board) {
        this.board = board;
        this.numPieces -= 1;
    }

    /**
     * Gives all possible moves that the pawn can make based on the current position
     * @param board String[][], the chess board
     * @param position int[], the current position of the pawn piece
     * @return ArrayList<Configuration>, every possible move that the pawn can make in order to capture another piece
     */
    public Collection<Configuration> pawnMoves(String[][] board, int[] position) {
        HashSet<Configuration> pawnCaps = new HashSet<>();
        ArrayList<int[]> possibleMoves = new ArrayList<>();
        int defRow = position[0];
        int defCol = position[1];
        possibleMoves.add(new int[]{defRow-1, defCol-1});
        possibleMoves.add(new int[]{defRow-1, defCol+1});
        for (int i = 0; i < 2; i++) {
            try {
                String diagMove = board[possibleMoves.get(i)[0]][possibleMoves.get(i)[1]];
                if (!diagMove.equals(".")) {
                    pawnCaps.add(new ChessConfig(this, "P", position, possibleMoves.get(i)));
                }
            } catch (IndexOutOfBoundsException e) {}
        }
        return pawnCaps;
    }

    /**
     * Gives all possible moves the knight can make based on the current position
     * @param board String[][], the chess board
     * @param position int[], the current position of the knight piece
     * @return ArrayList<Configuration>, every possible move that the knight piece can make in order to capture another piece
     */
    public Collection<Configuration> knightMoves(String[][] board, int[] position) {
        HashSet<Configuration> knightCaps = new HashSet();
        ArrayList<int[]> possibleMoves = new ArrayList<>();
        int defRow = position[0];
        int defCol = position[1];
        possibleMoves.add(new int[]{defRow+2, defCol-1});
        possibleMoves.add(new int[]{defRow+2, defCol+1});
        possibleMoves.add(new int[]{defRow+1, defCol-2});
        possibleMoves.add(new int[]{defRow-1, defCol-2});
        possibleMoves.add(new int[]{defRow+1, defCol+2});
        possibleMoves.add(new int[]{defRow-1, defCol+2});
        possibleMoves.add(new int[]{defRow-2, defCol-1});
        possibleMoves.add(new int[]{defRow-2, defCol+1});
        for (int i = 0; i < 8; i++) {
            try {
                if (!board[possibleMoves.get(i)[0]][possibleMoves.get(i)[1]].equals(".")) {
                    knightCaps.add(new ChessConfig(this, "N", position, possibleMoves.get(i)));
                }
            } catch (IndexOutOfBoundsException e) {}
        }
        return knightCaps;
    }

    /**
     * Gets all of the possible moves that can be made in the current configuration
     * @return Collection<Configuration>, all of the moves that can be made by all of the pieces on the board in order to capture another piece
     */
    @Override
    public Collection<Configuration> getNeighbors() {
        Set<Configuration> neighbors = new HashSet<>();
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                String s = board[i][j];
                if (s.equals("K")) {
                    neighbors.addAll(kingMoves(board, new int[]{i,j}));
                }
                if (s.equals("B")) {
                    neighbors.addAll(bishopMoves(board, new int[]{i,j}));
                }
                if (s.equals("R")) {
                    neighbors.addAll(rookMoves(board, new int[]{i,j}));
                }
                if (s.equals("Q")) {
                    neighbors.addAll(queenMoves(board, new int[]{i,j}));
                }
                if (s.equals("P")) {
                    neighbors.addAll(pawnMoves(board, new int[]{i,j}));
                }
                if (s.equals("N")) {
                    neighbors.addAll(knightMoves(board, new int[]{i,j,}));
                }
            }
        }
        return neighbors;
    }

    /**
     * Checks if who ChessConfigs are equal to each other
     * @param other Object, the supposed other ChessConfig
     * @return boolean, true if the boards are the same, false otherwise
     */
    @Override
    public boolean equals(Object other) {
        ChessConfig otherChess = (ChessConfig) other;
        return Arrays.deepEquals(this.board, otherChess.board);
    }

    /**
     * Changes the hashcode of a ChessConfig
     * @return int, the new hashcode, based off of the hashcode of the board
     */
    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    /**
     * Turns the information of the ChessConfig into a more readable format, shows the board
     * @return String, the board
     */
    @Override
    public String toString() {
        StringBuilder boardStep = new StringBuilder();
        for (int i = 0; i < numRows; i ++) {
            for (int j = 0; j < numCols; j++) {
                boardStep.append(board[i][j] + " ");
            }
            boardStep.append("\n");
        }
        return boardStep.toString();
    }

}
