/**
 * Represents a directed edge between two nodes in the graph.
 */
public class GraphEdge {
    /** The source node of the edge */
    private final GraphNode source;

    /** The destination node of the edge */
    private final GraphNode destination;

    /** The current status of the edge */
    private EdgeStatus status;

    /** The type of edge (assignment or request) */
    private final EdgeType type;

    /**
     * Constructs a new GraphEdge.
     * @param source The source node
     * @param destination The destination node
     * @param type The type of edge
     */
    public GraphEdge(GraphNode source, GraphNode destination, EdgeType type) {
        this.source = source;
        this.destination = destination;
        this.type = type;
        this.status = (type == EdgeType.ASSIGNMENT) ? EdgeStatus.ASSIGNED : EdgeStatus.REQUESTED;
    }

    // Getters and setters
    public GraphNode getSource() { return source; }
    public GraphNode getDestination() { return destination; }
    public EdgeStatus getStatus() { return status; }
    public EdgeType getType() { return type; }
    public void setStatus(EdgeStatus status) { this.status = status; }
}

