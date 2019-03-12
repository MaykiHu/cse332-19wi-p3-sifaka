package experiments;

import chess.board.ArrayBoard;
import chess.board.ArrayMove;
import chess.game.SimpleEvaluator;
import cse332.chess.interfaces.Move;
import cse332.chess.interfaces.Searcher;

import experiments.SimpleSearcher;
import experiments.ParallelSearcher;
import experiments.AlphaBetaSearcher;
import experiments.JamboreeSearcher;

import tests.TestsUtility;
import experiments.SearcherTests;
import tests.gitlab.TestingInputs;

public class NodeCounter extends SearcherTests {

	public static void main(String[] args) { new NodeCounter().run(); }
    public static void init() { 
    	STUDENT = new SimpleSearcher<ArrayMove, ArrayBoard>();
    	//STUDENT = new ParallelSearcher<ArrayMove, ArrayBoard>();
    	//STUDENT = new AlphaBetaSearcher<ArrayMove, ArrayBoard>();
    	//STUDENT = new JamboreeSearcher<ArrayMove, ArrayBoard>();
    }
	
	@Override
	protected void run() {

	    ALLOWED_TIME = Integer.MAX_VALUE;
	    //test("depth1", TestingInputs1.FENS_TO_TEST.length); // Figure out how to change this in our SearcherTests in experiments
        //test("depth2", TestingInputs1.FENS_TO_TEST.length);
        //test("depth3", TestingInputs1.FENS_TO_TEST.length);
        //test("depth4", TestingInputs1.FENS_TO_TEST.length);
        test("depth5Test1", TestingInputs1.FENS_TO_TEST.length);
        
		finish();
	} 
}
