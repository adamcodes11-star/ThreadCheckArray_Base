# ThreadCheckArray — Exercise 2

Software Engineering Methods: Configuration Management, AI-Assisted Refactoring, and Merge Conflict Resolution.

## Project overview

This Java project solves the Subset Sum decision problem: given a collection of integers and a target sum `b`, determine whether a subset sums to that target. A successful search produces a primitive `boolean[]` selection vector: `true` means that the input value at the corresponding index is selected.

### Recursive search and worker threads

`ThreadCheckArray` implements `Runnable`. Its recursive method considers two possibilities for each input value: include it and subtract it from the remaining target, or exclude it and leave the remaining target unchanged.

For inputs containing more than one value, the program divides the search between two workers:

- `thread1` searches subsets that include the last input value.
- `thread2` searches subsets that exclude the last input value.

Recursive calls run inside each worker; they do not create additional threads. Each worker maintains its own selection vector. A successful worker sets the shared completion flag, records its selected values as recursion unwinds, and publishes its vector. The other worker checks the shared flag to stop searching when a solution has already been found.

### Synchronization

Both workers reference the same `SharedData` object. The existing `synchronized (sd)` blocks protect shared completion-flag access and solution publication. The input list is treated as read-only during the search. The main method calls `join()` on both threads before inspecting and displaying the result, so it waits for solution publication to finish.

The refactoring preserves the existing search logic and synchronization. It does not add new algorithmic behavior or input validation.

## Source files

| File | Responsibility |
| --- | --- |
| `src/SharedData.java` | Holds the input values, target sum, shared completion flag, and solution vector. |
| `src/ThreadCheckArray.java` | Implements a recursive search worker and publishes a successful result. |
| `src/TestThreadCheckArray.java` | Reads console input, starts and joins the workers, and displays the result. |

## Refactoring to ArrayList

Only the integer input collection named `array` changes from `int[]` to `ArrayList<Integer>`.

| Original operation | Refactored operation |
| --- | --- |
| `int[] array` | `ArrayList<Integer> array` |
| `array.length` | `array.size()` |
| `array[index]` | `array.get(index)` for reads |
| Filling the input array | Adding input values with `array.add(value)` in the entry point |

The solution vectors remain primitive `boolean[]` arrays. Their length is based on `array.size()`. Constructors, getter return types, and call sites must agree on the new input type after both students' changes are merged.

At the Student A documentation stage, `SharedData.java` and `ThreadCheckArray.java` have been refactored on `main`. Student B's changes to the entry point and shared worker file still need to be merged. Until then, compilation errors in `TestThreadCheckArray.java` are expected because its input type still uses the original array representation.

## Javadoc

Javadoc comments document class responsibilities, constructor parameters, methods, and return values. Generic type names inside comments use safe formatting such as `{@code ArrayList<Integer>}` to prevent the angle brackets from being interpreted as HTML tags.

After merging and verifying the project, generate the HTML documentation in IntelliJ IDEA using **Tools → Generate JavaDoc**, select the whole project, and choose a `docs` directory in the project root. Open `docs/index.html` to inspect the generated documentation. The `docs/` directory is ignored by Git but must be included in the final submission ZIP, as required by the assignment.

## Authors and Git collaboration

| Role | Author | Branch | Responsibilities |
| --- | --- | --- | --- |
| Student A | Adam — GitHub: `adamcodes11-star` | `main` | Create the repository, invite the collaborator, refactor `SharedData.java` and `ThreadCheckArray.java`, and create this README. |
| Student B | Full name and GitHub username to be added | `feature/arraylist-refactor-b` | Clone the original project, create the feature branch, refactor `TestThreadCheckArray.java` and `ThreadCheckArray.java`, and merge and resolve conflicts. |

Repository: https://github.com/adamcodes11-star/ThreadCheckArray_Base

Both students start from the original project commit and work independently on their branches. Both edit `ThreadCheckArray.java` so overlapping changes can produce a merge conflict. Student B pulls Student A's updates into `main`, merges the feature branch, and resolves any conflict using IntelliJ IDEA's visual 3-Way Merge tool. After the resolved merge is pushed, Student A pulls the updated `main`.

Following the merge, both students verify the program, generate HTML Javadoc, and inspect the branch graph and **Annotate with Git Blame**. The submission report must contain genuine evidence of the work performed, including the conflict-resolution window.

## Running and verification after the merge

The assignment specifies BellSoft Liberica Full JDK 25 LTS. Configure the project SDK, then run `TestThreadCheckArray.main()` in IntelliJ IDEA.

Use the assignment's smoke-test input:

```text
Array size: 4
Numbers: 1 2 3 7
Target: 5
```

Expected result, with spacing simplified:

```text
Solution for b : 5,n = 4
I: 0 1 2 3
A: 1 2 3 7
C: 0 1 1 0
```

The selected values are `2` and `3`, which sum to `5`. This is the expected test result; successful execution must be confirmed after merging and recorded in the submission report. No completed merge or successful smoke test is claimed at this documentation stage.

## License

This coursework is subject to the **Braude College Academic License** specified in the supplied exercise materials. Those materials prohibit distribution, publishing, or sharing outside the official course boundary. Consult the supplied course materials for the applicable terms.
