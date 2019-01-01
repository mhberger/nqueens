// Copyright 2017 John Hurst
// John Hurst (john.b.hurst@gmail.com)
// 2017-12-30

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

import static java.lang.Integer.parseInt;
import static java.time.temporal.ChronoUnit.NANOS;
import static java.time.temporal.ChronoUnit.SECONDS;

public class QueensHalfForkJoin {

  private static final int PARALLELISM_DEPTH = 2;

  static class Board extends RecursiveTask<Integer> {

    private int size;
    private int row;
    private long cols;
    private long diags1;
    private long diags2;

    Board(int size) {
      this.size = size;
    }

    private Board place(int col) {
      Board result = new Board(this.size);
      result.row = this.row + 1;
      result.cols = this.cols | 1 << col;
      result.diags1 = this.diags1 | 1 << (this.row + col);
      result.diags2 = this.diags2 | 1 << (this.row - col + this.size - 1);
      return result;
    }

    private boolean ok(int col) {
      return ((this.cols & (1 << col)) |
        (this.diags1 & (1 << this.row + col)) |
        (this.diags2 & (1 << this.row - col + this.size - 1))) == 0;
    }

    protected Integer compute() {
      if (this.row == this.size) {
        return 1;
      }
      if (this.row >= PARALLELISM_DEPTH) {
        return solveSequential();
      }
      else {
        List<Board> boards = new ArrayList<>(this.size);
        for (int col = 0; col < this.size; col++) {
          if (ok(col)) {
            Board board = place(col);
            boards.add(board);
            board.fork();
          }
        }
        int result = 0;
        for (Board board : boards) {
          result += board.join();
        }
        return result;
      }
    }

    private int solveSequential() {
      if (this.row == this.size) {
        return 1;
      }
      int result = 0;
      for (int col = 0; col < this.size; col++) {
        if (ok(col)) {
          result += place(col).solveSequential();
        }
      }
      return result;
    }

    public int solveRest() {
      return this.compute();
    }

    public int solve() {
      int result = 0;
      for (int col = 0; col < this.size / 2; col++) {
        result += 2 * place(col).solveRest();
      }
      if (size % 2 == 1) {
        result += place(size/2).solveRest();
      }
      return result;
    }
  }

  public static void main(String[] args) {
    int from = args.length >= 1 ? parseInt(args[0]) : 0;
    int to = args.length >= 2 ? parseInt(args[1]) : from;

    for (int size = from; size <= to; size++) {
      Instant start = Instant.now();
      Board board = new Board(size);
      int count = board.solve();
      Instant end = Instant.now();
      Duration duration = Duration.between(start, end);
      double seconds = duration.get(SECONDS) + duration.get(NANOS) / 1000000000.0;
      System.out.println(String.format("%d,%d,%.3f", size, count, seconds));
    }
  }
}

