package experiments;

import chess.board.ArrayBoard;
import chess.board.ArrayMove;
import chess.game.SimpleEvaluator;
import cse332.chess.interfaces.Move;
import cse332.chess.interfaces.Searcher;

import tests.*;
import experiments.TestingInputs1;

import java.util.Arrays;

public abstract class SearcherTests extends TestsUtility {
    protected static Searcher<ArrayMove, ArrayBoard> STUDENT;

    protected static ArrayMove getBestMove(String fen, Searcher<ArrayMove, ArrayBoard> searcher, int depth, int cutoff) {
        searcher.setDepth(depth);
        searcher.setCutoff(cutoff);

        return searcher.getBestMove(ArrayBoard.FACTORY.create().init(fen), 0, 0);
    }

    protected static boolean checkResult(String fen, String[] valid, Searcher<ArrayMove, ArrayBoard> searcher, int depth, int cutoff) {
        ArrayMove result = getBestMove(fen, searcher, depth, cutoff);
        return Arrays.asList(valid).contains(result.toString());
    }  

    protected static int depth(int d, int c, int test) {
        STUDENT.setEvaluator(new SimpleEvaluator());
        int result = 0;
        if (test == 1) {
	        for (Object[] input : TestingInputs1.FENS_TO_TEST) { 
	            result += checkResult((String)input[0], ((String[][])input[1])[d - 2], STUDENT, d, c) ? 1 : 0;
	        }
        }
        if (test == 2) {
	        for (Object[] input : TestingInputs2.FENS_TO_TEST) { 
	            result += checkResult((String)input[0], ((String[][])input[1])[d - 2], STUDENT, d, c) ? 1 : 0;
	        }
        }
        if (test == 3) {
	        for (Object[] input : TestingInputs3.FENS_TO_TEST) { 
	            result += checkResult((String)input[0], ((String[][])input[1])[d - 2], STUDENT, d, c) ? 1 : 0;
	        }
        }
        return result;
    }

    public static int depth2() { return depth(2, 1, 1); }
    public static int depth3() { return depth(3, 1, 1); }
    public static int depth4() { return depth(4, 2, 1); }
    public static int depth5Test1() { return depth(5, 3, 1); }
    public static int depth5Test2() { return depth(5, 3, 2); }
    public static int depth5Test3() { return depth(5, 3, 3); }
    public static int depth6() { return depth(6, 3, 1); }

}
