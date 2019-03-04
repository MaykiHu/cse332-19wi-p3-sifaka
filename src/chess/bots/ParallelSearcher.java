package chess.bots;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import cse332.chess.interfaces.AbstractSearcher;
import cse332.chess.interfaces.Board;
import cse332.chess.interfaces.Evaluator;
import cse332.chess.interfaces.Move;


public class ParallelSearcher<M extends Move<M>, B extends Board<M, B>> extends
        AbstractSearcher<M, B> {
    public M getBestMove(B board, int myTime, int opTime) {
    	/* Calculate the best move */
        return minimax(this.evaluator, board, ply, super.cutoff).move;
    }
    
	@SuppressWarnings("unchecked")
	static <M extends Move<M>, B extends Board<M, B>> BestMove<M> minimax(Evaluator<B> evaluator, B board, int depth, int cutoff) {
        List<M> moves = board.generateMoves();
        Move<M>[] arr = (Move<M>[]) new Move[moves.size()];
        for (int i = 0; i < moves.size(); i++) {
        	arr[i] = moves.get(i);
        }
        return searchBestMove(arr, board, depth, cutoff, evaluator);
    }
    
    private static final int DIVIDE_CUTOFF = 2; // Maybe = to depth?
    private static final ForkJoinPool POOL = new ForkJoinPool();
	@SuppressWarnings("serial")
	private static class SearchTask<M extends Move<M>, B extends Board<M, B>> extends RecursiveTask<BestMove<M>> {
    	int lo; int hi; Move<M>[] arr; B board; Evaluator<B> evaluator; int cutoff; int depth;
    	
    	SearchTask(M[] arr, int lo, int hi, B board, int depth, int cutoff, Evaluator<B> evaluator) {
    		this.arr = arr;
    		this.lo = lo;
    		this.hi = hi;
    		this.board = board;    	
    		this.depth = depth;
    		this.evaluator = evaluator;
    		this.cutoff = cutoff;
    	}
    	
    	@SuppressWarnings("unchecked")
		SearchTask(Move<M> move, B board, int depth, int cutoff, Evaluator<B> evaluator) {
    		B newBoard = board.copy();
    		newBoard.applyMove(move.copy());
    		List<M> newMoves = newBoard.generateMoves();
    		Move<M>[] newArr = (Move<M>[]) new Move[newMoves.size()];
            for (int i = 0; i < newMoves.size(); i++) {
            	newArr[i] = newMoves.get(i);
            }
            arr = newArr;
            this.depth = depth;
            this.board = newBoard;
            this.depth = depth;
            this.cutoff = cutoff;
            this.evaluator = evaluator;
            this.lo = 0;
            this.hi = arr.length;
    	}
    	
		@SuppressWarnings({ "unchecked", "rawtypes" })
		protected BestMove<M> compute() {
			if (depth <= cutoff) {
				SimpleSearcher.minimax(evaluator, board, depth);
			} else if (hi - lo <= DIVIDE_CUTOFF) {
				SearchTask[] tasks = new SearchTask[hi - lo];
				for (int i = 0; i < hi - lo; i++) {
					tasks[i] = new SearchTask(arr[i].copy(), board, depth - 1, cutoff, evaluator);
					tasks[i].fork();
				}
				for (int i = 0; i < tasks.length; i++) {
					tasks[i].join();
				}
			}
			int mid = lo + (hi - lo) / 2;
			SearchTask left = new SearchTask(arr, lo, mid, board, depth, cutoff, evaluator);
			SearchTask right = new SearchTask(arr, mid, hi, board, depth, cutoff, evaluator);
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
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static <M extends Move<M>, B extends Board<M, B>> BestMove<M> searchBestMove(
			 Move<M>[] arr, B board, int depth, int cutoff, Evaluator<B> evaluator) {
    	SearchTask task = new SearchTask(arr, 0, arr.length, board, depth, cutoff, evaluator);
    	return (BestMove<M>) POOL.invoke(task);
    }
}