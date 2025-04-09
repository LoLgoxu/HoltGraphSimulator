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
    public boolean isAllocated() { return allocated; }

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
