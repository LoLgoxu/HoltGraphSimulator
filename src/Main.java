import java.util.*;

/**
 * Main class for the Permanent Resource Allocation System simulation.
 * This program models a system where processes request resources that cannot be freed once allocated.
 */
public class Main {

    /**
     * Main entry point for the application.
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Get user input for system configuration
        System.out.print("Enter number of Resource nodes: ");
        int resourceCount = scanner.nextInt();

        System.out.print("Enter number of Process nodes: ");
        int processCount = scanner.nextInt();

        // Initialize and run the simulation
        ResourceGraph resourceGraph = new ResourceGraph(resourceCount, processCount);
        resourceGraph.displayInitialState();
        resourceGraph.simulateProcessExecution();

        scanner.close();
    }
}

/**
 * Enumerates the possible types of edges in the graph.
 */
enum EdgeType {
    /** Edge representing a resource assigned to a process */
    ASSIGNMENT,
    /** Edge representing a process requesting a resource */
    REQUEST
}

/**
 * Enumerates the possible statuses of edges in the graph.
 */
enum EdgeStatus {
    /** Resource is assigned to process */
    ASSIGNED,
    /** Resource is being requested */
    REQUESTED,
    /** Resource has been acquired */
    ACQUIRED,
    /** Resource request is blocked */
    BLOCKED
}