package SolitairePeg;

import java.util.ArrayList;
import java.util.List;

/*
 * Represents a peg-solitaire board and methods.
 */

public class Board {
    private static final int SIZE = 7;
    public List<String> board;
    public Board prev_board;
    public Cell[][] m_board;
    public int cost;
    public int distance;
    public Long state;

    public Board(List<String> board, Board prev_board, int distance) {
        this.board = board;
        this.prev_board = prev_board;
        this.distance = distance;
        m_board = getBoard(board);
        cost = heuristic_weightedCost();
        state = bitMap();
    }

    private static Cell[][] getBoard(List<String> boardList) {
        Cell[][] arrayBoard = new Cell[SIZE][SIZE];
        for(int i = 0; i < SIZE ; i++) {
            char[] row = boardList.get(i).toCharArray();
            for(int j = 0; j < SIZE; j++) {
                arrayBoard[i][j] = Cell.fromChar(row[j]);
            }
        }
        return arrayBoard;
    }

    public List<Long> getSymmetricConfigs() {
        List<Long> configs = new ArrayList<>();
        configs.add(bitMap());
        List<Long> rotations = getRotateConfigs();
        configs.addAll(rotations);
        verticalReflect();
        configs.add(bitMap());
        List<Long> reflectRotations = getRotateConfigs();
        configs.addAll(reflectRotations);
        verticalReflect();
        return configs;
    }

    private List<Long> getRotateConfigs() {
        List<Long> rotations = new ArrayList<>(3);
        for(int i = 0; i < 3; i++) {
            rotation();
            rotations.add(bitMap());
        }
        rotation();
        return rotations;
    }

    private void rotation() {
        for(int i = 0; i < SIZE / 2; i++) {
            for(int j = 0; j <= (SIZE - 1) / 2; j++) {
                Cell temp = m_board[i][j];
                m_board[i][j] = m_board[SIZE - j - 1][i];
                m_board[SIZE - j - 1][i] = m_board[SIZE - i - 1][SIZE - j - 1];
                m_board[SIZE - i - 1][SIZE - j - 1] = m_board[j][SIZE - i - 1];
                m_board[j][SIZE - i - 1] =temp;
            }
        }
    }

    private void verticalReflect() {
        for(int j = 0; j < SIZE / 2; j++)
            for(int i = 0; i < SIZE; i++){
                Cell temp = m_board[i][j];
                m_board[i][j] = m_board[i][SIZE - j - 1];
                m_board[i][SIZE - j - 1] = temp;
            }
    }

    public long bitMap() {
        long bitMap = 0;
        for(int i = 0; i < SIZE; i++) {
            for(int j = 0; j < SIZE; j++) {
                bitMap |= m_board[i][j].bit();
                if( j != SIZE - 1 || i != SIZE - 1) bitMap <<= 1;
            }
        }
        return bitMap;
    }

    private int heuristic_weightedCost() {
        int[][] costMatrix =  new int[][]{
                { 0, 0, 4, 0, 4, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0 },
                { 4, 0, 3, 0, 3, 0, 4 },
                { 0, 0, 0, 1, 0, 0, 0 },
                { 4, 0, 3, 0, 3, 0, 4 },
                { 0, 0, 0, 0, 0, 0, 0 },
                { 0, 0, 4, 0, 4, 0, 0 }};

        int heuristic = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (m_board[i][j] == Cell.PEG) {
                    heuristic += costMatrix[i][j];
                }
            }
        }
        return heuristic;
    }

    private int heuristic_manhattanCost() {
        int value = 0;
        int manDistance;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if(m_board[i][j] == Cell.PEG){
                    manDistance = Math.abs(i-3) + Math.abs(j-3);
                    value += manDistance;
                }
            }
        }
        return value;
    }

    public boolean isGoalState(){
        int pegCount = 0;
        if(m_board[3][3] != Cell.PEG)
            return false;
        for(int i = 0; i < SIZE; i++)
            for(int j = 0; j < SIZE; j++)
                if(m_board[i][j] == Cell.PEG)
                    pegCount++;
        if(1 == pegCount)
            return true;
        return false;
    }

    private static boolean invalidPos(int step_x, int step_y) {
        return (step_x < 0 || step_x >= SIZE || step_y < 0 || step_y >= SIZE)
                || ((step_x < 2 && step_y < 2) || (step_x < 2 && step_y > 4)
                ||(step_x > 4 && step_y < 2) || (step_x > 4 && step_y > 4));
    }

    public boolean validMove(int x, int y, int dx, int dy) {
        int step_x = x + dx / 2, step_y = y + dy / 2,
                jump_x = x + dx, jump_y = y + dy;
        if(invalidPos(step_x, step_y) || invalidPos(jump_x, jump_y))
            return false;

        Cell intCell = m_board[step_x][step_y];
        Cell destCell = m_board[jump_x][jump_y];
        if(intCell == Cell.INVALID || intCell == Cell.EMPTY ||
                destCell == Cell.INVALID || destCell != Cell.EMPTY)
            return false;
        return true;
    }

    public Board move(int x, int y, int dx, int dy) {
        Cell[][] temp_Cell_board = getBoard(board);
        temp_Cell_board[x][y] = Cell.EMPTY;
        temp_Cell_board[x + dx / 2][y + dy / 2] = Cell.EMPTY;
        temp_Cell_board[x + dx][y + dy] = Cell.PEG;

        List<String> new_board_list = new ArrayList<>(7);
        for (int i = 0; i < SIZE; i++) {
            String s = "";
            for (int j = 0; j < SIZE; j++)
                s += ((temp_Cell_board[i][j] == Cell.INVALID) ? "-" : (temp_Cell_board[i][j] == Cell.EMPTY) ? "0" : "X");
            new_board_list.add(s);
        }
        return new Board(new_board_list, new Board(this.board, this.prev_board, this.distance), this.distance + 1);
    }
}

