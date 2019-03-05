package chess.bots;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import cse332.chess.interfaces.AbstractSearcher;
import cse332.chess.interfaces.Board;
import cse332.chess.interfaces.Evaluator;
import cse332.chess.interfaces.Move;

public class JamboreeSearcher<M extends Move<M>, B extends Board<M, B>> extends
        AbstractSearcher<M, B> {

    public M getBestMove(B board, int myTime, int opTime) {
    	/* Calculate the best move */
        return alphabeta(this.evaluator, board, ply, super.cutoff, -evaluator.infty(), evaluator.infty()).move;
    }
    
	static <M extends Move<M>, B extends Board<M, B>> BestMove<M> alphabeta(Evaluator<B> evaluator,
			B board, int depth, int cutoff, int alpha, int beta) {
        List<M> moves = board.generateMoves();
        BestMove<M> parallelMove = searchBestMove(moves, board, depth, cutoff, evaluator, -evaluator.infty(), evaluator.infty());
        return parallelMove;
    }
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	static <M extends Move<M>, B extends Board<M, B>> BestMove<M> sequential(List<M> moves, Evaluator<B> evaluator, B board, int depth, 
			int alpha, int beta) {
    	if (board == null) {
    		throw new IllegalArgumentException();
    	}
        if (depth == 0) {
        	return new BestMove(evaluator.eval(board));
        } 
        if (moves.isEmpty()) {
        	if (board.inCheck()) {
        		return new BestMove(-evaluator.mate() - depth);
        	} else {
        		return new BestMove(-evaluator.stalemate());
        	}
        }
        M bestMove = null;
        for (int i = 0; i < (int)(PERCENTAGE_SEQUENTIAL * moves.size()); i++) {
        	board.applyMove(moves.get(i));
        	int value = -sequential(moves, evaluator, board, depth - 1, -beta, -alpha).value;
        	board.undoMove();
        	if (value > alpha) {
        		alpha = value;
        		bestMove = moves.get(i);
        	}
        	
        	if (alpha >= beta) {
        		return new BestMove(bestMove, alpha);
        	}
        }
        return new BestMove(bestMove, alpha);
    }
	
    private static final int DIVIDE_CUTOFF = 2;
    private static final double PERCENTAGE_SEQUENTIAL = 0.5;
    private static final ForkJoinPool POOL = new ForkJoinPool();
	@SuppressWarnings("serial")
	private static class SearchTask<M extends Move<M>, B extends Board<M, B>> extends RecursiveTask<BestMove<M>> {
    	int lo; int hi; List<M> moves; B board; Evaluator<B> evaluator; int cutoff; int depth; M move; int alpha; int beta;
    	
    	SearchTask(List<M> moves, int lo, int hi, B board, int depth, int cutoff, Evaluator<B> evaluator, int alpha, int beta) {
    		this.moves = moves;
    		this.lo = lo;
    		this.hi = hi;
    		this.board = board;    	
    		this.depth = depth;
    		this.evaluator = evaluator;
    		this.cutoff = cutoff;
    		this.alpha = alpha;
    		this.beta = beta;
    	}
    	
		SearchTask(M move, B board, int depth, int cutoff, Evaluator<B> evaluator, int alpha, int beta) {
    		this.move = move;
    		this.board = board;
    		this.depth = depth;
    		this.cutoff = cutoff;
    		this.evaluator = evaluator;
    		this.alpha = alpha;
    		this.beta = beta;
    	}
    	
		@SuppressWarnings({ "unchecked", "rawtypes" })
		protected BestMove<M> compute() {
			M bestMove = null;
			if (move != null) { // Have child apply moves
				B newBoard = board.copy();
				newBoard.applyMove(move);
				List<M> newMoves = newBoard.generateMoves();
				SearchTask curr = new SearchTask(newMoves, 0, newMoves.size(), newBoard, depth - 1, cutoff, evaluator, alpha, beta);
				return curr.compute();
			} 
			if (depth <= cutoff) {
				return AlphaBetaSearcher.alphabeta(evaluator, board, depth, alpha, beta);
			} if (moves.isEmpty()) {
	        	if (board.inCheck()) {
	        		return new BestMove(-evaluator.mate() - depth);
	        	} else {
	        		return new BestMove(-evaluator.stalemate());
	        	}
	        } else if (lo < (int) (PERCENTAGE_SEQUENTIAL * moves.size())) {
				BestMove<M> move = sequential(moves, evaluator, board, depth, alpha, beta);
				lo += (int) (PERCENTAGE_SEQUENTIAL * moves.size());
				if (-move.value > alpha) {
					alpha = -move.value;
					bestMove = move.move;
				}
				if (alpha >= beta) {
					return new BestMove(bestMove, alpha);
				}
				return new BestMove(bestMove, alpha);
			} else if (hi - lo <= DIVIDE_CUTOFF) {
				SearchTask[] tasks = new SearchTask[hi - lo];
				BestMove<M>[] results = (BestMove<M>[]) new BestMove[hi - lo];
				for (int i = 0; i < hi - lo; i++) {
					tasks[i] = new SearchTask(moves.get(i + lo), board, depth, cutoff, evaluator, alpha, beta);
					if (i != hi - lo - 1) {
						tasks[i].fork();
					}
				}
				tasks[hi - lo - 1].compute();
				for (int i = 0; i < tasks.length; i++) {
					results[i] = (BestMove<M>) tasks[i].join();
					if (-results[i].value > alpha) {
		        		alpha = -results[i].value;
		        		bestMove = move;
		        	}
		        	if (alpha >= beta) {
		        		return new BestMove(bestMove, alpha);
		        	}
				}
				return new BestMove(bestMove, alpha);
			} else {
				int mid = lo + (hi - lo) / 2;
				SearchTask left = new SearchTask(moves, lo, mid, board, depth, cutoff, evaluator, alpha, beta);
				SearchTask right = new SearchTask(moves, mid, hi, board, depth, cutoff, evaluator, alpha, beta);
				left.fork();
				BestMove<M> rightBest = right.compute();
				BestMove<M> leftBest = (BestMove<M>) left.join();
				if (alpha > rightBest.value && alpha > leftBest.value) {
					return new BestMove(bestMove, alpha);
				} else if (rightBest.value > leftBest.value) {
					return rightBest;
				} else {
					return leftBest;
				}
			}
		}
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static <M extends Move<M>, B extends Board<M, B>> BestMove<M> searchBestMove(
			 List<M> moves, B board, int depth, int cutoff, Evaluator<B> evaluator, int alpha, int beta) {
    	SearchTask task = new SearchTask(moves, 0, moves.size(), board, depth, cutoff, evaluator, alpha, beta);
    	return (BestMove<M>) POOL.invoke(task);
    }
}