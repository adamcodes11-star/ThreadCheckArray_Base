import java.util.ArrayList;

/**
 * A {@link Runnable} worker that recursively solves the Subset Sum decision problem
 * on a shared {@code ArrayList<Integer>}.
 * <p>
 * Two threads run this class concurrently. The thread named {@code "thread1"} assumes the
 * last element is part of the solution, while the other thread assumes it is not.
 * The first thread that finds a solution raises the shared flag in {@link SharedData}
 * and publishes its solution vector ({@code winArray}).
 * Access to the shared object is guarded by {@code synchronized} blocks.
 * </p>
 */
public class ThreadCheckArray implements Runnable {
    /** Local flag indicating that this thread found a solution. */
    private boolean flag;
    /** Solution vector: {@code winArray[i]} is {@code true} if element {@code i} is in the subset. */
    private boolean[] winArray;
    /** Shared data object used by both threads. */
    SharedData sd;
    /** The input numbers, stored as an {@code ArrayList<Integer>}. */
    ArrayList<Integer> array;
    /** The target sum. */
    int b;

    /**
     * Creates a new worker that reads its input from the given shared data.
     * The boolean solution vector is sized dynamically using {@code array.size()}.
     *
     * @param sd the shared data containing the {@code ArrayList<Integer>} and the target sum
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
     * Recursively checks whether a subset of the first {@code n} elements sums to {@code b}.
     * Stops early if another thread already found a solution.
     *
     * @param n the number of elements (from the start of the list) to consider
     * @param b the remaining target sum
     */
    void rec(int n, int b) {
        synchronized (sd) {
            if (sd.getFlag()) return;
        }
        if (n == 1) {
            if (b == 0 || b == array.get(n - 1)) {
                flag = true;
                synchronized (sd) { sd.setFlag(true); }
            }
            if (b == array.get(n - 1)) winArray[n - 1] = true;
            return;
        }

        rec(n - 1, b - array.get(n - 1));
        if (flag) winArray[n - 1] = true;
        synchronized (sd) {
            if (sd.getFlag()) return;
        }
        rec(n - 1, b);
    }

    /**
     * Thread entry point. Starts the recursion according to the thread name and,
     * if a solution was found, publishes the solution vector to the shared data.
     */
    public void run() {
        if (array.size() != 1)
            if (Thread.currentThread().getName().equals("thread1"))
                rec(array.size() - 1, b - array.get(array.size() - 1));
            else
                rec(array.size() - 1, b);
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
