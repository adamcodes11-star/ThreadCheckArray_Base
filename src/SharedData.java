import java.util.ArrayList;

/**
 * Holds the input and shared result of a multithreaded subset sum search.
 *
 * <p>The input is stored in an {@code ArrayList<Integer>}. Each position in
 * the primitive {@code boolean[]} solution vector indicates whether the
 * corresponding input value belongs to the selected subset.</p>
 *
 * <p>Worker threads must synchronize on this object when accessing shared
 * mutable state. This class does not perform synchronization internally.</p>
 */
public class SharedData {
    /** Input values used by the search. */
    private ArrayList<Integer> array;

    /** Selection vector published by a worker that finds a solution. */
    private boolean[] winArray;

    /** Whether a worker has found a solution. */
    private boolean flag;

    /** Target sum. */
    private final int b;

    /**
     * Creates shared search data and an initially unselected solution vector.
     * The supplied list is retained by reference and must not be modified
     * while the worker threads are running.
     *
     * @param array non-null input values stored as an {@code ArrayList<Integer>}
     * @param b target sum to find
     */
    public SharedData(ArrayList<Integer> array, int b) {
        this.array = array;
        this.b = b;
        this.winArray = new boolean[array.size()];
    }

    /**
     * Returns the selection vector for the input values.
     *
     * @return the current solution vector as a primitive {@code boolean[]}
     */
    public boolean[] getWinArray() {
        return winArray;
    }

    /**
     * Publishes the selection vector produced by a successful worker.
     *
     * @param winArray solution vector with one entry per input value
     */
    public void setWinArray(boolean[] winArray) {
        this.winArray = winArray;
    }

    /**
     * Returns the input list retained by this object.
     *
     * @return input values as an {@code ArrayList<Integer>}
     */
    public ArrayList<Integer> getArray() {
        return array;
    }

    /**
     * Returns the target sum.
     *
     * @return the target sum
     */
    public int getB() {
        return b;
    }

    /**
     * Reports whether a worker has found a solution.
     *
     * @return {@code true} if a solution has been found; otherwise {@code false}
     */
    public boolean getFlag() {
        return flag;
    }

    /**
     * Updates the shared solution-found flag.
     *
     * @param flag whether a solution has been found
     */
    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
