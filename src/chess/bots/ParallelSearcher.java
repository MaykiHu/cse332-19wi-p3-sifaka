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
        BestMove<M> best = minimax(this.evaluator, board, ply);
        return best.move;
    }
    
	@SuppressWarnings("unchecked")
	static <M extends Move<M>, B extends Board<M, B>> BestMove<M> minimax(Evaluator<B> evaluator, B board, int depth) {
        List<M> moves = board.generateMoves();
        Move<M>[] arr = (Move<M>[]) new Move[moves.size()];
        for (int i = 0; i < moves.size(); i++) {
        	arr[i] = moves.get(i);
        }
        return (BestMove<M>) searchBestMove(arr, board);
    }
    
    private static final int DIVIDE_CUTOFF = 2; // Maybe = to depth?
	@SuppressWarnings("serial")
	private static class SearchTask<M extends Move<M>, B extends Board<M, B>> extends RecursiveTask<BestMove<M>> {
    	int lo; int hi; Move<M>[] arr; B board; Evaluator<B> evaluator; int cutoff;
    	
    	SearchTask(M[] arr, int lo, int hi, B board) {
    		this.arr = arr;
    		this.lo = lo;
    		this.hi = hi;
    		this.board = board;    		
    	}
    	
		@SuppressWarnings({ "unchecked", "rawtypes" })
		protected BestMove<M> compute() {
			if (hi - lo <= DIVIDE_CUTOFF) {
				SimpleSearcher.minimax(evaluator, board, cutoff);
			}
			
			int mid = lo + (hi - lo) / 2;
			SearchTask left = new SearchTask(arr, lo, mid, board);
			SearchTask right = new SearchTask(arr, mid, hi, board);
			left.fork();
			BestMove<M> rightBest = right.compute();
			BestMove<M> leftBest = (BestMove<M>) left.join();
			if (rightBest.value > leftBest.value) {
				return right.compute();
			} else {
				return (BestMove<M>) left.join();
			}
		}
    }
    
    static final ForkJoinPool POOL = new ForkJoinPool();
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static <M extends Move<M>, B extends Board<M, B>> BestMove<M> searchBestMove(Move<M>[] arr, B board) {
    	SearchTask task = new SearchTask(arr, 0, arr.length, board);
    	return POOL.invoke(task);
    }
}