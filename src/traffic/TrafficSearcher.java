package traffic;

import java.util.List;

import chess.bots.BestMove;
import cse332.chess.interfaces.AbstractSearcher;
import cse332.chess.interfaces.Board;
import cse332.chess.interfaces.Evaluator;
import cse332.chess.interfaces.Move;

public class TrafficSearcher<M extends Move<M>, B extends Board<M, B>> extends
        AbstractSearcher<M, B> {

    public M getBestMove(B board, int myTime, int opTime) {
    	return alphabeta(this.evaluator, board, ply, -evaluator.infty(), evaluator.infty()).move;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	static <M extends Move<M>, B extends Board<M, B>> BestMove<M> alphabeta(Evaluator<B> evaluator, B board, int depth, int alpha, int beta) {
    	if (board == null) {
    		throw new IllegalArgumentException();
    	}
        if (depth == 0) {
        	return new BestMove(evaluator.eval(board));
        } 
        List<M> moves = board.generateMoves();
        if (moves.isEmpty()) {
        	return new BestMove(evaluator.eval(board));
        }
        M bestMove = null;
        for (M move : moves) {
        	board.applyMove(move);
        	int value = -alphabeta(evaluator, board, depth - 1, -beta, -alpha).value;
        	board.undoMove();
        	if (value > alpha) {
        		alpha = value;
        		bestMove = move;
        	}
        	
        	if (alpha >= beta) {
        		return new BestMove(bestMove, alpha);
        	}
        }
        return new BestMove(bestMove, alpha);
    }    
}