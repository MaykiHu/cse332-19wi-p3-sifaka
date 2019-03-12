package experiments;

import chess.board.ArrayBoard;
import chess.board.ArrayMove;
import chess.game.SimpleEvaluator;
import cse332.chess.interfaces.Move;
import cse332.chess.interfaces.Searcher;

import experiments.SimpleSearcher;
import experiments.ParallelSearcher;

import tests.TestsUtility;
import tests.gitlab.SearcherTests;
import tests.gitlab.TestingInputs;

public class NodeCounter extends SearcherTests {

	public static void main(String[] args) { new NodeCounter().run(); }
    public static void init() { STUDENT = new SimpleSearcher<ArrayMove, ArrayBoard>(); }
	
	@Override
	protected void run() {
        SHOW_TESTS = true;
        PRINT_TESTERR = true;

        ALLOWED_TIME = 30000; // I have to x4 of the time because my computer runs too slowly
	    
        test("depth2", TestingInputs.FENS_TO_TEST.length);
        test("depth3", TestingInputs.FENS_TO_TEST.length);
		
		finish();
	} 
}
