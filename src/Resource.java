/**
 * Represents a resource node in the graph.
 */
public class Resource extends GraphNode {
    /** Flag indicating if the resource is currently allocated */
    private boolean allocated;

    /**
     * Constructs a new Resource.
     * @param id The unique identifier for this resource
     */
    public Resource(int id) {
        super(id);
        this.allocated = false;
    }

    /**
     * Checks if the resource is allocated.
     * @return true if allocated, false otherwise
     */
    public boolean isAllocated() {
        return allocated;
    }

    /**
     * Allocates the resource.
     * @throws IllegalStateException if resource is already allocated
     */
    public void allocate() {
        if (allocated) {
            throw new IllegalStateException("Resource already allocated");
        }

        allocated = true;
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