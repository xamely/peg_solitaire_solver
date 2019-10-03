package SolitairePeg;

import java.util.*;

public class SolitairePegSolver {
    private static List<String> start_board = new ArrayList<String>() {
        { add("--XXX--"); }
        { add("--XXX--"); }
        { add("XXXXXXX"); }
        { add("XXX0XXX"); }
        { add("XXXXXXX"); }
        { add("--XXX--"); }
        { add("--XXX--"); }
    };
    static Comparator<Board> comparator = new Comparator<Board>() {
        @Override
        public int compare(Board o1, Board o2) {
            if (o1.cost == o2.cost)
                return o2.distance - o1.distance;
            return o1.cost - o2.cost;
        }
    };
    static private PriorityQueue<Board> priorityQueue = new PriorityQueue<>(11, comparator);
    static private Set<Long> visitedStates = new HashSet<>();
    static int[][] deltas = new int[][]{{-2, 0}, {2, 0}, {0, -2}, {0, 2}};

    public static void main(String[] args) {
        long startTime = System.nanoTime();
        Board currentState = new Board(start_board, null, 0);
        priorityQueue.add(currentState);
        for (Long st : currentState.getSymmetricConfigs())
            visitedStates.add(st);

        while (!currentState.isGoalState() && !priorityQueue.isEmpty()) {
            currentState = priorityQueue.poll();

            for (int x = 0; x < 7; x++)
                for (int y = 0; y < 7; y++) {
                    if (currentState.m_board[x][y] == Cell.PEG) {
                        for(int index = 0; index < 4; index++) {
                            int dx = deltas[index][0];
                            int dy = deltas[index][1];
                            if(currentState.validMove(x, y, dx, dy)) {
                                Board new_board = currentState.move(x, y, dx, dy);
                                if(visitedStates.contains(new_board.bitMap()))
                                    continue;
                                visitedStates.add(new_board.state);
                                for(Long st: new_board.getSymmetricConfigs())
                                    visitedStates.add(st);

                                priorityQueue.add(new_board);
                            }
                        }
                    }
                }
            if(!priorityQueue.isEmpty())
                currentState = priorityQueue.peek();
        }

        System.out.println("Steps count: " + currentState.distance);
        System.out.println("Solution time: " + (System.nanoTime() - startTime) / 1000000000.0);

        System.out.println("Solution by steps in reverse order: ");
        String[] arr = new String[7];
        while (currentState != null) {
            System.out.println();
            currentState.board.toArray(arr);
            for (int i = 0; i < 7; i++)
                System.out.println(arr[i]);
            currentState = currentState.prev_board;
        }
    }
}