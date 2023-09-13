package puzzles.chess.ptui;

import puzzles.chess.solver.Chess;
import puzzles.common.Observer;
import puzzles.chess.model.ChessModel;

import java.io.*;
import java.util.Scanner;

public class ChessPTUI extends ConsoleApplication implements Observer<ChessModel, String> {
    private static ChessModel model;
    private PrintWriter out;
    private boolean init;

    public void init(String filename) throws IOException {
        model = new ChessModel(filename);
        model.addObserver(this);
        displayHelp();
    }
    @Override
    public void start(PrintWriter console) throws Exception {
        this.out = console;
        this.init = true;
        super.setOnCommand("hint", 0, ": Get a hint",
                args -> model.hint());
        super.setOnCommand("load", 1, ": Load a file",
                args -> {
                    try {
                        model.load(new File(args[0]));
                    } catch (IOException e) {
                        model.fail(args[0]);
                    }
                });
        super.setOnCommand("select", 2, ": Select a piece to capture another",
                args -> model.select(Integer.parseInt(args[0]), Integer.parseInt(args[1])));
        super.setOnCommand("quit", 0, ": quit the game",
                args -> model.quit());
        super.setOnCommand("reset", 0, ": reset the current game",
                args -> {
                    try {
                        model.reset();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
        super.help(new String[]{});


    }
    @Override
    public void update(ChessModel model, String data) {
        // for demonstration purposes
        System.out.println(data);
        System.out.println(model);
    }

    private void displayHelp() {
        System.out.println( "h(int)              -- hint next move" );
        System.out.println( "l(oad) filename     -- load new puzzle file" );
        System.out.println( "s(elect) r c        -- select cell at r, c" );
        System.out.println( "q(uit)              -- quit the game" );
        System.out.println( "r(eset)             -- reset the current game" );
    }

    public void run() {
        Scanner in = new Scanner( System.in );
        for ( ; ; ) {
            System.out.print( "> " );
            String line = in.nextLine();
            String[] words = line.split( "\\s+" );
            if (words.length > 0) {
                if (words[0].startsWith( "q" )) {
                    break;
                }
                else {
                    displayHelp();
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java ChessPTUI filename");
        } else {
            try {
                ChessPTUI c = new ChessPTUI();
                c.init(args[0]);
                ConsoleApplication.launch(ChessPTUI.class, args);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }


}

