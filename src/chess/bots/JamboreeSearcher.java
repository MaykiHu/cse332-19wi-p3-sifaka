package chess.bots;

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
        M move = jamboree(this.evaluator, board, ply, super.cutoff, -evaluator.infty(), evaluator.infty()).move;
        System.err.println(board.generateMoves().contains(move));
        return move;
    }
    
	static <M extends Move<M>, B extends Board<M, B>> BestMove<M> jamboree(Evaluator<B> evaluator,
			B board, int depth, int cutoff, int alpha, int beta) {
        List<M> moves = board.generateMoves();
        BestMove<M> parallelMove = searchBestMove(moves, board, depth, cutoff, evaluator, -evaluator.infty(), evaluator.infty());
        return parallelMove;
    }
	
	// if (move != null) copy board -- check
	// if (depth <= cuttof) run alphabeta -- check
	// if (new move list) run the first %_SEQ * list.size() moves sequentially, create task and then .compute()
	// if (movelist size <= Divide_cuttof) for sequentially
	// else divide and conquer
	
	// make sure to return a move from your current board, also if tie, return left one first
	// make sure to compare %_seq move with divide & conquer move
	
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
			} else if (lo == 0 && lo < (int) (PERCENTAGE_SEQUENTIAL * moves.size())) {
				BestMove<M>[] results = (BestMove<M>[]) new BestMove[(int) (PERCENTAGE_SEQUENTIAL * moves.size())];
				for (int i = 0; i < results.length; i++) {
					results[i] = new SearchTask(moves.get(i), board, depth, cutoff, evaluator, alpha, beta).compute();
					if (-results[i].value > alpha) {
						alpha = -results[i].value;
						bestMove = moves.get(i);
					}
					if (alpha >= beta) {
						bestMove = moves.get(i);
						break; // May change this to a while loop to exit
					}
				}
				lo += (int) (PERCENTAGE_SEQUENTIAL * moves.size());
				SearchTask parTask = new SearchTask(moves, lo, moves.size(), board, depth, cutoff, evaluator, alpha, beta);
				BestMove<M> parMove = parTask.compute();
				if (parMove.value > alpha) {
					return parMove;
				} else {
					return new BestMove(bestMove, alpha);
				}
			} else if (hi - lo <= DIVIDE_CUTOFF) {
				SearchTask[] tasks = new SearchTask[hi - lo - 1];
				BestMove<M>[] results = (BestMove<M>[]) new BestMove[hi - lo];
				for (int i = 0; i < tasks.length; i++) {
					tasks[i] = new SearchTask(moves.get(i + lo), board, depth, cutoff, evaluator, alpha, beta);
					tasks[i].fork();
				}
				results[hi - lo - 1] = new SearchTask(moves.get(hi - lo - 1), board, depth, cutoff, evaluator, alpha, beta).compute();
				for (int i = 0; i < results.length; i++) {
					if (i != hi - lo - 1) {
						results[i] = (BestMove<M>) tasks[i].join();
					}
					if (-results[i].value > alpha) {
		        		alpha = -results[i].value;
		        		bestMove = moves.get(i + lo);
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
			 List<M> moves, B board, int depth, int cutoff, Evaluator<B> evaluator, int alpha, int beta) {
    	SearchTask task = new SearchTask(moves, 0, moves.size(), board, depth, cutoff, evaluator, alpha, beta);
    	return (BestMove<M>) POOL.invoke(task);
    }
}