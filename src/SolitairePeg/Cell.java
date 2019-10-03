package SolitairePeg;

/*
 * Signifies a cell on the board.
 */

public enum  Cell {
    INVALID('-', 0),
    EMPTY('0', 0),
    PEG('X', 1);

    private char input_char;
    private int bit;

    Cell(char input_char, int bit) {
        this.input_char = input_char;
        this.bit = bit;
    }

    static Cell fromChar(char c) {
        for(Cell s: values())
            if(s.input_char == c)
                return s;
        return null;
    }

    public int bit() {
        return bit;
    }
}