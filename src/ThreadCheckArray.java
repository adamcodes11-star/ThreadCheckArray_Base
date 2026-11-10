import java.util.ArrayList;

/**
 * Searches one branch of the subset sum problem using shared input data.
 *
 * <p>Input values are held in an {@code ArrayList<Integer>}, while each
 * worker keeps its own primitive {@code boolean[]} selection vector.
 * The worker named {@code thread1} explores subsets that include the last
 * input value; the other worker explores subsets that exclude it.</p>
 *
 * <p>Access to the shared completion flag and publication of the solution
 * vector is protected by synchronization on the {@link SharedData} object.
 * The input list must not be modified while workers are running.</p>
 */
public class ThreadCheckArray implements Runnable {
    /** Whether this worker has found a solution. */
    private boolean flag;
    /** Selection vector for the solution found by this worker. */
    private boolean[] winArray;
    /** Input and result state shared by the workers. */
    SharedData sd;
    /** Input values retained from the shared data. */
    ArrayList<Integer> array;
    /** Target sum for the search. */
    int b;

    /**
     * Initializes a worker from the shared search data.
     * The selection vector is sized using the input list's {@code size()}.
     *
     * @param sd shared data containing a non-empty input list and target sum
     */
    public ThreadCheckArray(SharedData sd) {
        this.sd = sd;
        synchronized (sd) {
            array = sd.getArray();
            b = sd.getB();
        }
        winArray = new boolean[array.size()];
    }

    /**
     * Recursively searches the first {@code n} input values for the remaining
     * target sum, considering inclusion and exclusion of each value.
     * The search stops when a worker has reported a solution.
     *
     * @param n number of input values remaining to consider, at least one
     * @param b remaining sum required from those values
     */
    void rec(int n, int b) {
        synchronized (sd) {
            if (sd.getFlag()) return;
        }
        if (n == 1) {
            if(b == 0 || b == array.get(n-1)) {
                flag = true;
                synchronized (sd) { sd.setFlag(true); }
            }
            if (b == array.get(n-1)) winArray[n-1] = true;
            return;
        }

        rec(n-1, b - array.get(n-1));
        if (flag) winArray[n-1] = true;
        synchronized (sd) {
            if (sd.getFlag()) return;
        }
        rec(n-1, b);
    }

    /**
     * Searches the branch assigned by the current thread's name.
     * A successful worker records the last element's selection when needed
     * and publishes its solution vector while holding the shared data lock.
     */
    public void run() {
        if (array.size() != 1)
            if (Thread.currentThread().getName().equals("thread1"))
                rec(array.size()-1, b - array.get(array.size() - 1));
            else
                rec(array.size()-1, b);
        if (array.size() == 1)
            if (b == array.get(0) && !flag) {
                winArray[0] = true;
                flag = true;
                synchronized (sd) { sd.setFlag(true); }
            }
        if (flag) {
            if (Thread.currentThread().getName().equals("thread1"))
                winArray[array.size() - 1] = true;
            synchronized (sd) { sd.setWinArray(winArray); }
        }
    }
}
