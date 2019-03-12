package experiments;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import chess.board.ArrayBoard;
import chess.board.ArrayMove;
import chess.bots.LazySearcher;
import chess.game.SimpleEvaluator;
import cse332.chess.interfaces.Move;
import cse332.chess.interfaces.Searcher;

public class NodeCounter {

    public static ArrayMove getBestMove(String fen, Searcher<ArrayMove, ArrayBoard> searcher, int depth, int cutoff) { 
        searcher.setDepth(depth);
        searcher.setCutoff(cutoff);
        searcher.setEvaluator(new SimpleEvaluator());

        return searcher.getBestMove(ArrayBoard.FACTORY.create().init(fen), 0, 0);
    }
    
    public static void printMove(String fen, Searcher<ArrayMove, ArrayBoard> searcher, int depth, int cutoff) {
        getBestMove(fen, searcher, depth, cutoff);
    }
    @SuppressWarnings("resource")
	public static void main(String[] args) throws FileNotFoundException {
        SimpleSearcher<ArrayMove, ArrayBoard> searcher = new SimpleSearcher<>();
        //ParallelSearcher<ArrayMove, ArrayBoard> searcher = new ParallelSearcher<>();
        //AlphaBetaSearcher<ArrayMove, ArrayBoard> searcher = new AlphaBetaSearcher<>();
        //JamboreeSearcher<ArrayMove, ArrayBoard> searcher = new JamboreeSearcher<>();
        
        for (int ply = 1; ply <= 5; ply++) { // Test each ply
        	Scanner inputs = new Scanner(new File("/Users/M8yki/git/p3-sifaka/src/experiments/TestingInputs.txt"));
        	while (inputs.hasNextLine()) {
        		String input = inputs.nextLine().substring(5); // Where the position/input should be tested
        		printMove(input, searcher, ply, ply / 2); // Cutoff is ply / 2
        	}
        	System.out.println("Average nodes visited: " + (int)((double) searcher.NODE_COUNT / 64)); // Simple + AlphaBeta
        	//System.out.println("Average nodes visited: " + (int)(ParallelSearcher.NODE_COUNT.doubleValue() / 64));
        	//System.out.println("Average nodes visited: " + (int)(JamboreeSearcher.NODE_COUNT.doubleValue() / 64));
        }
    }
}