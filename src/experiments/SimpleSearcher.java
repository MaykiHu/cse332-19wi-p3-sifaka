package experiments;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import cse332.chess.interfaces.AbstractSearcher;
import cse332.chess.interfaces.Board;
import cse332.chess.interfaces.Evaluator;
import cse332.chess.interfaces.Move;

/**
 * This class should implement the minimax algorithm as described in the
 * assignment handouts.
 */
public class SimpleSearcher<M extends Move<M>, B extends Board<M, B>> extends
        AbstractSearcher<M, B> {
	public static AtomicInteger NODE_COUNT = new AtomicInteger();
	
    public M getBestMove(B board, int myTime, int opTime) {
        /* Calculate the best move */
        return minimax(this.evaluator, board, ply).move;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	static <M extends Move<M>, B extends Board<M, B>> BestMove<M> minimax(Evaluator<B> evaluator, B board, int depth) {
    	if (board == null) {
    		throw new IllegalArgumentException();
    	}
        if (depth == 0) {
        	return new BestMove(evaluator.eval(board));
        } 
        List<M> moves = board.generateMoves();
        if (moves.isEmpty()) {
        	if (board.inCheck()) {
        		return new BestMove(-evaluator.mate() - depth);
        	} else {
        		return new BestMove(-evaluator.stalemate());
        	}
        }
        int bestValue = -evaluator.infty();
        M bestMove = null;
        for (M move : moves) {
        	board.applyMove(move);
        	NODE_COUNT.addAndGet(1);
        	ParallelSearcher.NODE_COUNT.add(1);
        	int value = -minimax(evaluator, board, depth - 1).value;
        	board.undoMove();
        	if (value > bestValue) {
        		bestValue = value;
        		bestMove = move;
        	}
        }
        return new BestMove(bestMove, bestValue);
    }
}