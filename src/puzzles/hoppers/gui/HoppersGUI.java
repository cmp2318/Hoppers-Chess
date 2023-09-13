package puzzles.hoppers.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import puzzles.common.Observer;
import puzzles.hoppers.model.HoppersConfig;
import puzzles.hoppers.model.HoppersModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

/**
 *
 * The HoppersGUI class provides a graphical user interface for playing the Hoppers game.
 * It extends the Application class and implements the Observer interface to monitor the changes in the HoppersModel.
 * It also provides methods to display the game board, handle user input, and load the board configuration from a file.
 * @author cmp2318
 */

public class HoppersGUI extends Application implements Observer<HoppersModel, String> {
    /** The size of all icons, in square dimension */
    private final static int ICON_SIZE = 75;

    /** the font size for labels and buttons */
    private final static int FONT_SIZE = 12;

    /** The resources directory is located directly underneath the gui package */
    final static String RESOURCES_DIR = "resources/";

    /**
     * Represents an Image object that contains the red frog image.
     */
    private final Image redFrog = new Image(Objects.requireNonNull(getClass().
            getResourceAsStream(RESOURCES_DIR + "red_frog.png")));

    /**
     * Represents an Image object that contains the green frog image.
     */
    private final Image greenFrog = new Image(Objects.requireNonNull(getClass().
            getResourceAsStream(RESOURCES_DIR + "green_frog.png")));

    /**
     * Represents an Image object that contains the lily pad image.
     */
    private final Image lilyPad = new Image(Objects.requireNonNull(getClass().
            getResourceAsStream(RESOURCES_DIR + "lily_pad.png")));

    /**
     * Represents an Image object that contains the water image.
     */
    private final Image water = new Image(Objects.requireNonNull(getClass().
            getResourceAsStream(RESOURCES_DIR + "water.png")));

    /**
     * Represents the 2D array that stores the current state of the game board as characters.
     */
    public char[][] textBoard;

    /**
     * Represents the string input that is used to initialize the game board.
     */
    private String gridInput;

    /**
     * Represents the number of rows in the game board.
     */
    private int rowsNum;

    /**
     * Represents the number of columns in the game board.
     */
    private int cols;

    /**
     * Represents the row index of the starting position for the jump.
     */
    private int startRow;

    /**
     * Represents the column index of the starting position for the jump.
     */
    private int startCol;

    /**
     * Represents a boolean value indicating whether it is currently the player's turn to jump.
     */
    private boolean jumpTurn;

    /**
     * Represents the HoppersModel object that handles the game logic.
     */
    private HoppersModel hoppersModel;

    /**
     * Represents the name of the file that was loaded to initialize the game board.
     */
    private String loadedFilename;

    /**
     * Represents the name of what is displayed as the title.
     */
    private String name;

    /**
     * Initializes the application by loading the board configuration
     * from a file specified as the command line argument.
     * Sets up the observer to monitor changes in the HoppersModel.
     */
    public void init() {
        String filename = getParameters().getRaw().get(0);
        gridInput = getGridInput(filename);
        hoppersModel = new HoppersModel(filename);
        textBoard = hoppersModel.getBoard();
        loadedFilename = filename;

        this.hoppersModel.addObserver(this);

        File file = new File(filename);

        this.name = "Loaded: " + file.getName();

        jumpTurn = false;
    }

    /**
     * Initializes the application by loading the board configuration from the specified file.
     * Sets up the observer to monitor changes in the HoppersModel.
     * @param filename the name of the file containing the board configuration
     */
    public void init(String filename) {
        gridInput = getGridInput(filename);
        hoppersModel = new HoppersModel(filename);
        textBoard = hoppersModel.getBoard();

        this.hoppersModel.addObserver(this);

        File file = new File(filename);


        this.name = "Loaded: " + file.getName();

        jumpTurn = false;
    }

    /**
     * Reads the board configuration from a file and returns it as a string.
     * @param fileName the name of the file containing the board configuration
     * @return a string representing the board
     */
    public String getGridInput(String fileName){

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            StringBuilder line = new StringBuilder();


            String[] dimensions = reader.readLine().split(" ");
            rowsNum = Integer.parseInt(dimensions[0]);
            cols = Integer.parseInt(dimensions[1]);

            for (int row = 0; row < rowsNum; row++) {
                line.append(reader.readLine());
                line.append("\n");

            }

            return line.toString();

        } catch (IOException e) {
            System.err.println("Error reading input file: " + e.getMessage());
            return null;
        }

    }

    /**
     * Updates the game board displayed by the GUI based on the given board and
     * refreshes the stage.
     *
     * @param board the new board to be displayed
     * @param stage the stage to display the board on
     */
    public void updateBoard(char[][] board, Stage stage){

        StringBuilder sb = new StringBuilder();

        for (char[] row : board) {
            for (char c: row){
                sb.append(c).append(' ');
            }

            sb.append(System.lineSeparator());
        }

        gridInput = sb.toString();

        setStage(stage);
    }

    /**
     * Displays the game board on the given stage.
     *
     * @param stage the stage to display the board on
     */
    public void setStage(Stage stage){

        stage.setTitle("HoppersGUI");

        VBox vBox = new VBox();

        Font font = new Font(FONT_SIZE);

        HBox titleBox = new HBox();

        //create the label to be displayed at the top of the board
        Label label = new Label(name);
        label.setFont(font);
        label.setAlignment(Pos.CENTER);

        titleBox.getChildren().add(label);
        titleBox.setAlignment(Pos.CENTER);

        //create the gridpane that will house the tiles as buttons
        GridPane gridPane = new GridPane();
        gridPane.setHgap(-1);
        gridPane.setVgap(-1);
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        String[] rows = gridInput.split("\n");

        // Create a button for each character in the grid and give the frogs function
        for (int row = 0; row < this.rowsNum; row++) {
            for (int col = 0; col < this.cols; col++) {
                char c = rows[row].charAt(col * 2);
                Button button = new Button();
                ImageView imageView = switch (c) {
                    case HoppersConfig.RED_FROG -> new ImageView(redFrog);
                    case HoppersConfig.GREEN_FROG -> new ImageView(greenFrog);
                    case HoppersConfig.EMPTY -> new ImageView(lilyPad);
                    case HoppersConfig.INVALID -> new ImageView(water);
                    default -> null;
                };
                if (imageView != null) {
                    button.setGraphic(imageView);
                    button.setMinSize(ICON_SIZE, ICON_SIZE);
                    button.setMaxSize(ICON_SIZE, ICON_SIZE);
                    int finalRow = row;
                    int finalCol = col;
                    if (c == HoppersConfig.RED_FROG || c == HoppersConfig.GREEN_FROG) {
                        button.setOnAction(event -> {
                            startRow = finalRow;
                            startCol = finalCol;
                            jumpTurn = true;
                        });
                    } else if (c == HoppersConfig.EMPTY) {
                        button.setOnAction(event -> {
                            if (jumpTurn) {
                                hoppersModel.jump(startRow, startCol, finalRow, finalCol);
                                updateBoard(hoppersModel.getBoard(), stage);
                            }
                        });
                    }
                    gridPane.add(button, col, row);
                }
            }
        }
        gridPane.setHgap(-1);
        gridPane.setVgap(-1);

        HBox hBox = new HBox();

        //create the functioning buttons
        Button loadButton = new Button("Load");
        loadButton.setOnAction(event -> load(stage));

        Button resetButton = new Button("Reset");
        resetButton.setOnAction(event -> reset(stage));

        Button hintButton = new Button("Hint");
        hintButton.setOnAction(event -> hint(stage));

        loadButton.setFont(font);
        resetButton.setFont(font);
        hintButton.setFont(font);

        hBox.getChildren().add(loadButton);
        hBox.getChildren().add(resetButton);
        hBox.getChildren().add(hintButton);

        hBox.setAlignment(Pos.CENTER);

        vBox.getChildren().add(titleBox);
        vBox.getChildren().add(gridPane);
        vBox.getChildren().add(hBox);

        Scene scene = new Scene(vBox);
        stage.setScene(scene);

        stage.show();
    }

    /**
     * method to handle the action when load is pressed
     * @param stage the current stage
     */
    public void load(Stage stage){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
                new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {

            String filepath = selectedFile.getAbsolutePath();
            File file = new File(filepath);

            update(hoppersModel, "Loaded: " + file.getName());

            gridInput = getGridInput(filepath);
            try {
                loadedFilename = filepath;

                hoppersModel = new HoppersModel(filepath);
                init(loadedFilename);
                updateBoard(hoppersModel.getBoard(), stage);
                start(stage);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
    }

    /**
     * method to handle the action when reset is pressed
     * @param stage the current stage
     */
    public void reset(Stage stage){
        try {
            init(loadedFilename);
            update(hoppersModel, "Puzzle Reset!");
            start(stage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * method to handle the action when hint is pressed
     * @param stage the current stage
     */
    public void hint(Stage stage){
        update(hoppersModel, "Next Step:");
        hoppersModel.updateBoard();
        updateBoard(hoppersModel.getBoard(), stage);
    }

    /**
     * displays the board with the current stage
     */
    @Override
    public void start(Stage stage) throws Exception {
        setStage(stage);
    }

    /**
     * updates the string to be displayed at the top of the game's window
     */
    @Override
    public void update(HoppersModel hoppersModel, String msg) {
        name = msg;
    }


    /**
     * Main method which launches the program
     */
    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("Usage: java HoppersPTUI filename");
        } else {
            System.out.println();

            Application.launch(args);
        }
    }
}
