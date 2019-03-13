package experiments;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.LongAdder;

import cse332.chess.interfaces.AbstractSearcher;
import cse332.chess.interfaces.Board;
import cse332.chess.interfaces.Evaluator;
import cse332.chess.interfaces.Move;

public class JamboreeSearcher<M extends Move<M>, B extends Board<M, B>> extends AbstractSearcher<M, B> {
	public static int DIVIDE_CUTOFF = 1;
	private static final double PERCENTAGE_SEQUENTIAL = 0.25;
	private static final ForkJoinPool POOL = new ForkJoinPool();
	public static LongAdder NODE_COUNT = new LongAdder();
	
	public M getBestMove(B board, int myTime, int opTime) {
		SearchTask<M, B> bestMoveTask = new SearchTask<M, B>(null, -1, -1, board, ply, cutoff, evaluator, -evaluator.infty(), evaluator.infty());
		return POOL.invoke(bestMoveTask).move;
	}

	@SuppressWarnings("serial")
	private static class SearchTask<M extends Move<M>, B extends Board<M, B>> extends RecursiveTask<BestMove<M>> {
		int lo; int hi; List<M> moves; B board; Evaluator<B> evaluator; int cutoff; int depth; M move; int alpha; int beta;

		public SearchTask(List<M> moves, int lo, int hi, B board, int depth, int cutoff, Evaluator<B> evaluator,
				int alpha, int beta) {
			this.moves = moves;
			this.lo = lo;
			this.hi = hi;
			this.board = board;
			this.depth = depth;
			this.cutoff = cutoff;
			this.evaluator = evaluator;
			this.alpha = alpha;
			this.beta = beta;
		}

		public SearchTask(M move, B board, int depth, int cutoff, Evaluator<B> evaluator, int alpha, int beta) {
			this.move = move;
			this.board = board;
			this.depth = depth;
			this.cutoff = cutoff;
			this.evaluator = evaluator;
			this.alpha = alpha;
			this.beta = beta;
		}

		@Override
		@SuppressWarnings("unchecked")
		public BestMove<M> compute() {
			BestMove<M> bestMove = new BestMove<M>(null, alpha);
			if (moves == null) { // If we need to populate board -- this is when we sequential the % few before rest in parallel
				if (move != null) { // If we have to apply a move
					board = board.copy();
					board.applyMove(move);
					NODE_COUNT.add(1);
				}
				if (depth <= cutoff) {
					return AlphaBetaSearcher.alphabeta(evaluator, board, depth, alpha, beta);
				}
				moves = board.generateMoves();
				if (moves.isEmpty()) { 
		    		if (board.inCheck()) {
		    			return new BestMove<M>(null, -evaluator.mate() - depth);
		    		} else {
		    			return new BestMove<M>(null, -evaluator.stalemate());
		    		}
		    	}
				int seqCutoff = (int) (PERCENTAGE_SEQUENTIAL * moves.size());
				for (int i = 0; i < seqCutoff; i++) {
					int value = -(new SearchTask<M, B>(moves.get(i), board, depth - 1, cutoff, evaluator, -beta, -alpha).compute().value);
					if (value > alpha) {
						alpha = value;
						bestMove.value = alpha;
						bestMove.move = moves.get(i);
					}
					if (alpha >= beta) {
						return bestMove;
					}
				}
				lo = seqCutoff;
				hi = moves.size();
			} 
			if (hi - lo <= DIVIDE_CUTOFF) { // Stop divide conquer
				SearchTask<M, B>[] tasks = new SearchTask[hi - lo - 1]; // Tasks that need to be forked
				for (int i = 0; i < tasks.length; i++) {
					tasks[i] = new SearchTask<M, B>(moves.get(i + lo), board, depth - 1, cutoff, evaluator, -beta, -alpha);
					tasks[i].fork();
				}
				BestMove<M>[] results = new BestMove[tasks.length + 1]; // All the results from compute, the one below is the solo compute
				results[tasks.length] = new SearchTask<M, B>(moves.get(hi - 1), board, depth - 1, cutoff, evaluator, -beta, -alpha).compute().negate();
				for (int i = 0; i < results.length; i++) {
					if (i != results.length - 1) { // For all the forked tasks, join them
						results[i] = tasks[i].join().negate();
					}
					if (results[i].value > bestMove.value) {
						bestMove.value = results[i].value;
						bestMove.move = moves.get(i + lo);
					}
				}
				return bestMove;
			} else { // Keep divide conquer
				int mid = lo + (hi - lo) / 2;
				SearchTask<M, B> left = new SearchTask<>(moves, lo, mid, board, depth, cutoff, evaluator, alpha, beta);
				SearchTask<M, B> right = new SearchTask<>(moves, mid, hi, board, depth, cutoff, evaluator, alpha, beta);
				left.fork();
				BestMove<M> rightBest = right.compute();
				BestMove<M> leftBest = left.join();
				if (rightBest.value > leftBest.value) {
					if (rightBest.value > alpha) {
						return rightBest;
					} else {
						return bestMove;
					}
				} else {
					if (leftBest.value > alpha) {
						return leftBest;
					} else {
						return bestMove;
					}
				}
			}
		}
	}
}
