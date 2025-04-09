/**
 * Represents a process node in the graph.
 */
public class Process extends GraphNode {
    /** Flag indicating if the process has completed */
    private boolean completed;

    /**
     * Constructs a new Process.
     * @param id The unique identifier for this process
     */
    public Process(int id) {
        super(id);
        this.completed = false;
    }

    /**
     * Checks if the process has completed.
     * @return true if completed, false otherwise
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * Marks the process as completed.
     * @throws IllegalStateException if process is already completed
     */
    public void markAsCompleted() {
        if (completed) {
            throw new IllegalStateException("Process already completed");
        }
        completed = true;
    }
}