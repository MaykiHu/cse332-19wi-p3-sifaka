package experiments;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import chess.board.ArrayBoard;
import chess.board.ArrayMove;
import chess.game.SimpleEvaluator;
import cse332.chess.interfaces.Searcher;

public class CutoffCounter {
	private static int TRIAL_COUNT = 3;
	
    public static ArrayMove getBestMove(String fen, Searcher<ArrayMove, ArrayBoard> searcher, int depth, int cutoff) { 
        searcher.setDepth(depth);
        searcher.setCutoff(cutoff);
        searcher.setEvaluator(new SimpleEvaluator());

        return searcher.getBestMove(ArrayBoard.FACTORY.create().init(fen), 0, 0);
    }
    
    public static void printMove(String fen, Searcher<ArrayMove, ArrayBoard> searcher, int depth, int cutoff) {
        getBestMove(fen, searcher, depth, cutoff);
    }
    
	@SuppressWarnings({ "resource", "static-access" })
	public static void main(String[] args) throws FileNotFoundException {
        //ParallelSearcher<ArrayMove, ArrayBoard> searcher = new ParallelSearcher<>();
        JamboreeSearcher<ArrayMove, ArrayBoard> searcher = new JamboreeSearcher<>();
        
        int ply = 5;
        for (int divideCutoff = 1; divideCutoff <= 4; divideCutoff++) {
        	Scanner boards = new Scanner(new File("BoardInputs.txt"));
            int numBoard = 0;
            double sum = 0;
	        while (boards.hasNextLine()) {
	        	numBoard++;
	        	String input = boards.nextLine().substring(5); // Which board state is tested: 1-start, 2-mid ish, 3-end ish
	        	searcher.DIVIDE_CUTOFF = divideCutoff;
	        	for (int i = 0; i < TRIAL_COUNT; i++) {
		        	long startTime = System.nanoTime();
		        	printMove(input, searcher, ply, ply / 2); // Cutoff is ply / 2
		        	long endTime = System.nanoTime();
		        	long elapsedTime = endTime - startTime;
		        	sum += elapsedTime;
		        	System.out.println("Board " + numBoard + " took " + elapsedTime / 1000000 + " milliseconds for divide cutoff " + divideCutoff + ".");
	        	}
	        	System.out.println();
	        	System.out.println("Average ms/board is: " + sum / TRIAL_COUNT / 1000000);
	        }
        }
    }
}