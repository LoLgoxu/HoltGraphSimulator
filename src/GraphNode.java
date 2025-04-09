import java.util.Objects;

/**
 * Abstract base class for all graph nodes (resources and processes).
 */
public abstract class GraphNode {
    /** The unique identifier for this node */
    protected final int id;

    /**
     * Constructs a new GraphNode.
     * @param id The unique identifier for this node
     */
    public GraphNode(int id) {
        this.id = id;
    }

    /**
     * Returns a string representation of the node.
     * @return String in format "Tid" where T is node type and id is the identifier
     */
    @Override
    public String toString() {
        return getClass().getSimpleName().charAt(0) + String.valueOf(id);
    }

    /**
     * Checks equality with another node.
     * @param obj The object to compare
     * @return true if nodes have same ID and type, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        GraphNode other = (GraphNode) obj;
        return id == other.id;
    }

    /**
     * Generates a hash code for the node.
     * @return The hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
