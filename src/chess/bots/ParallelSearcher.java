package chess.bots;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import cse332.chess.interfaces.AbstractSearcher;
import cse332.chess.interfaces.Board;
import cse332.chess.interfaces.Evaluator;
import cse332.chess.interfaces.Move;


public class ParallelSearcher<M extends Move<M>, B extends Board<M, B>> extends AbstractSearcher<M, B> {
	private static final int DIVIDE_CUTOFF = 2;
    private static final ForkJoinPool POOL = new ForkJoinPool();
    
    public M getBestMove(B board, int myTime, int opTime) {
    	/* Calculate the best move */
        return minimax(this.evaluator, board, ply, super.cutoff).move;
    }
    
	static <M extends Move<M>, B extends Board<M, B>> BestMove<M> minimax(Evaluator<B> evaluator, B board, int depth, int cutoff) {
        List<M> moves = board.generateMoves();
        return searchBestMove(moves, board, depth, cutoff, evaluator);
    }
    
	@SuppressWarnings("serial")
	private static class SearchTask<M extends Move<M>, B extends Board<M, B>> extends RecursiveTask<BestMove<M>> {
    	int lo; int hi; List<M> moves; B board; Evaluator<B> evaluator; int cutoff; int depth; M move;
    	
    	SearchTask(List<M> moves, int lo, int hi, B board, int depth, int cutoff, Evaluator<B> evaluator) {
    		this.moves = moves;
    		this.lo = lo;
    		this.hi = hi;
    		this.board = board;    	
    		this.depth = depth;
    		this.evaluator = evaluator;
    		this.cutoff = cutoff;
    	}
    	
		SearchTask(M move, B board, int depth, int cutoff, Evaluator<B> evaluator) {
    		this.move = move;
    		this.board = board;
    		this.depth = depth;
    		this.cutoff = cutoff;
    		this.evaluator = evaluator;
    	}
    	
		@SuppressWarnings({ "unchecked", "rawtypes" })
		protected BestMove<M> compute() {
			if (move != null) { // Have child apply moves
				B newBoard = board.copy();
				newBoard.applyMove(move);
				List<M> newMoves = newBoard.generateMoves();
				SearchTask curr = new SearchTask(newMoves, 0, newMoves.size(), newBoard, depth - 1, cutoff, evaluator);
				return curr.compute();
			} 

			if (depth <= cutoff) {
				return SimpleSearcher.minimax(evaluator, board, depth);
			} else if (hi - lo <= DIVIDE_CUTOFF) {
				SearchTask[] tasks = new SearchTask[hi - lo];
				BestMove<M>[] results = (BestMove<M>[]) new BestMove[hi - lo];
				for (int i = 0; i < hi - lo; i++) {
					tasks[i] = new SearchTask(moves.get(i + lo), board, depth, cutoff, evaluator);
					tasks[i].fork();
				}
				int bestValue = -evaluator.infty();
				M bestMove = null;
				for (int i = 0; i < tasks.length; i++) {
					results[i] = (BestMove<M>) tasks[i].join();
					if (-results[i].value > bestValue) {
						bestValue = -results[i].value;
						bestMove = moves.get(lo + i);
					}
				}
				return new BestMove(bestMove, bestValue);
			} else {
				int mid = lo + (hi - lo) / 2;
				SearchTask left = new SearchTask(moves, lo, mid, board, depth, cutoff, evaluator);
				SearchTask right = new SearchTask(moves, mid, hi, board, depth, cutoff, evaluator);
				left.fork();
				BestMove<M> rightBest = right.compute();
				BestMove<M> leftBest = (BestMove<M>) left.join();
				if (rightBest.value > leftBest.value) {
					return rightBest;
				} else {
					return leftBest;
				}
			}
		}
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static <M extends Move<M>, B extends Board<M, B>> BestMove<M> searchBestMove(
			 List<M> moves, B board, int depth, int cutoff, Evaluator<B> evaluator) {
    	SearchTask task = new SearchTask(moves, 0, moves.size(), board, depth, cutoff, evaluator);
    	return (BestMove<M>) POOL.invoke(task);
    }
}