import java.util.*;
import java.util.stream.Collectors;

/**
 * Main class for the Permanent Resource Allocation System simulation.
 * This program models a system where processes request resources that cannot be freed once allocated.
 */
public class PermanentResourceAllocator {

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
 * Represents the complete resource allocation graph containing processes, resources, and their connections.
 * Manages the simulation of process execution and resource allocation.
 */
class ResourceGraph {
    /** List of all resources in the system */
    private final List<Resource> resources;

    /** List of all processes in the system */
    private final List<Process> processes;

    /** List of all edges connecting nodes in the graph */
    private final List<GraphEdge> edges;

    /** Random number generator for creating random connections */
    private final Random random = new Random();

    /** Counter for simulation steps */
    private int simulationStep = 0;

    /**
     * Constructs a new ResourceGraph with specified numbers of resources and processes.
     * @param resourceCount The number of resource nodes to create
     * @param processCount The number of process nodes to create
     */
    public ResourceGraph(int resourceCount, int processCount) {
        this.resources = new ArrayList<>();
        this.processes = new ArrayList<>();
        this.edges = new ArrayList<>();
        initializeNodes(resourceCount, processCount);
        createRandomEdges();
    }

    /**
     * Initializes the nodes of the graph with the specified counts.
     * @param resourceCount Number of resources to create
     * @param processCount Number of processes to create
     */
    private void initializeNodes(int resourceCount, int processCount) {
        // Create resource nodes
        for (int i = 0; i < resourceCount; i++) {
            resources.add(new Resource(i));
        }

        // Create process nodes
        for (int i = 0; i < processCount; i++) {
            processes.add(new Process(i));
        }
    }

    /**
     * Creates random edges between processes and resources.
     */
    private void createRandomEdges() {
        assignProcessesToResources();
        createResourceRequests();
    }

    /**
     * Creates assignment edges from resources to processes.
     */
    private void assignProcessesToResources() {
        for (Process process : processes) {
            if (!resources.isEmpty()) {
                Resource assignedResource = getRandomResource();
                edges.add(new GraphEdge(assignedResource, process, EdgeType.ASSIGNMENT));
            }
        }
    }

    /**
     * Creates request edges from processes to resources.
     */
    private void createResourceRequests() {
        for (Process process : processes) {
            // Each process requests 1-3 random resources
            int requiredResourcesCount = random.nextInt(3) + 1;
            for (int i = 0; i < requiredResourcesCount && !resources.isEmpty(); i++) {
                Resource requestedResource = getRandomResource();
                if (!edgeExists(process, requestedResource)) {
                    edges.add(new GraphEdge(process, requestedResource, EdgeType.REQUEST));
                }
            }
        }
    }

    /**
     * Gets a random resource from the available resources.
     * @return A randomly selected Resource
     */
    private Resource getRandomResource() {
        return resources.get(random.nextInt(resources.size()));
    }

    /**
     * Checks if an edge already exists between two nodes.
     * @param source The source node
     * @param destination The destination node
     * @return true if an edge exists, false otherwise
     */
    private boolean edgeExists(GraphNode source, GraphNode destination) {
        return edges.stream()
                .anyMatch(edge -> edge.getSource().equals(source) &&
                        edge.getDestination().equals(destination));
    }

    /**
     * Displays the initial state of the resource graph.
     */
    public void displayInitialState() {
        System.out.println("\nINITIAL RESOURCE GRAPH STATE:");
        printCurrentState();
        System.out.println("\nStarting simulation...\n");
    }

    /**
     * Simulates the execution of all processes in the system.
     */
    public void simulateProcessExecution() {
        // Execute processes in order of their resource requirements (fewest first)
        for (Process process : getProcessExecutionOrder()) {
            simulationStep++;
            System.out.printf("=== STEP %d: EXECUTING PROCESS %s ===%n", simulationStep, process);

            List<Resource> requiredResources = getRequiredResources(process);
            boolean canExecute = attemptResourceAcquisition(process, requiredResources);

            printCurrentState();
            checkForDeadlocks();

            if (canExecute) {
                completeProcessExecution(process, requiredResources);
                printCurrentState();
            } else {
                System.out.printf("%s BLOCKED - REQUIRED RESOURCES UNAVAILABLE%n", process);
            }

            System.out.println();
        }
    }

    /**
     * Gets the execution order for processes (sorted by number of required resources).
     * @return List of processes in execution order
     */
    private List<Process> getProcessExecutionOrder() {
        return processes.stream()
                .sorted(Comparator.comparingInt(process -> getRequiredResources(process).size()))
                .collect(Collectors.toList());
    }

    /**
     * Gets all resources required by a specific process.
     * @param process The process to check
     * @return List of required resources
     */
    private List<Resource> getRequiredResources(Process process) {
        return edges.stream()
                .filter(edge -> edge.getSource().equals(process) &&
                        edge.getDestination() instanceof Resource)
                .map(edge -> (Resource) edge.getDestination())
                .collect(Collectors.toList());
    }

    /**
     * Attempts to acquire all required resources for a process.
     * @param process The process requesting resources
     * @param requiredResources List of resources needed
     * @return true if all resources were acquired, false otherwise
     */
    private boolean attemptResourceAcquisition(Process process, List<Resource> requiredResources) {
        // Check if all required resources are available
        boolean allResourcesAvailable = requiredResources.stream()
                .noneMatch(Resource::isAllocated);

        if (allResourcesAvailable) {
            allocateResources(process, requiredResources);
            return true;
        }

        updateResourceRequestStatuses(process, requiredResources);
        return false;
    }

    /**
     * Allocates resources to a process.
     * @param process The process receiving resources
     * @param requiredResources List of resources to allocate
     */
    private void allocateResources(Process process, List<Resource> requiredResources) {
        requiredResources.forEach(resource -> {
            resource.allocate();
            updateEdgeStatus(process, resource, EdgeStatus.ACQUIRED);
        });
    }

    /**
     * Updates the status of resource requests based on availability.
     * @param process The process making requests
     * @param requiredResources List of requested resources
     */
    private void updateResourceRequestStatuses(Process process, List<Resource> requiredResources) {
        requiredResources.forEach(resource -> {
            if (resource.isAllocated()) {
                updateEdgeStatus(process, resource, EdgeStatus.BLOCKED);
            } else {
                updateEdgeStatus(process, resource, EdgeStatus.REQUESTED);
            }
        });
    }

    /**
     * Marks a process as completed and displays completion message.
     * @param process The completed process
     * @param requiredResources List of resources used by the process
     */
    private void completeProcessExecution(Process process, List<Resource> requiredResources) {
        process.markAsCompleted();
        System.out.printf("%s COMPLETED PERMANENTLY USING RESOURCES: %s%n",
                process,
                requiredResources.stream()
                        .map(Resource::toString)
                        .collect(Collectors.joining(", ")));
    }

    /**
     * Updates the status of an edge between nodes.
     * @param source The source node
     * @param destination The destination node
     * @param status The new status to set
     */
    private void updateEdgeStatus(GraphNode source, GraphNode destination, EdgeStatus status) {
        edges.stream()
                .filter(edge -> edge.getSource().equals(source) &&
                        edge.getDestination().equals(destination))
                .findFirst()
                .ifPresent(edge -> edge.setStatus(status));
    }

    /**
     * Checks for deadlocks in the current system state.
     */
    private void checkForDeadlocks() {
        if (detectDeadlock()) {
            System.out.println("DEADLOCK DETECTED: Circular wait condition exists");
        } else {
            System.out.println("No deadlock condition detected");
        }
    }

    /**
     * Detects if a deadlock exists in the current system state.
     * @return true if deadlock detected, false otherwise
     */
    private boolean detectDeadlock() {
        Map<Process, List<Resource>> waitForGraph = createWaitForGraph();
        return hasCyclicDependencies(waitForGraph);
    }

    /**
     * Creates a wait-for graph showing which processes are waiting for which resources.
     * @return The wait-for graph mapping
     */
    private Map<Process, List<Resource>> createWaitForGraph() {
        Map<Process, List<Resource>> waitForGraph = new HashMap<>();

        for (Process process : processes) {
            if (!process.isCompleted()) {
                List<Resource> waitingFor = getRequiredResources(process).stream()
                        .filter(Resource::isAllocated)
                        .collect(Collectors.toList());

                if (!waitingFor.isEmpty()) {
                    waitForGraph.put(process, waitingFor);
                }
            }
        }
        return waitForGraph;
    }

    /**
     * Checks for cyclic dependencies in the wait-for graph.
     * @param waitForGraph The wait-for graph to analyze
     * @return true if cycles exist, false otherwise
     */
    private boolean hasCyclicDependencies(Map<Process, List<Resource>> waitForGraph) {
        Set<Process> visited = new HashSet<>();
        Set<Process> currentlyVisiting = new HashSet<>();

        for (Process process : waitForGraph.keySet()) {
            if (!visited.contains(process)) {
                if (detectCycle(process, visited, currentlyVisiting, waitForGraph)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Recursively detects cycles in the wait-for graph using DFS.
     * @param process The current process being examined
     * @param visited Set of already visited processes
     * @param currentlyVisiting Set of processes in the current recursion stack
     * @param waitForGraph The wait-for graph
     * @return true if a cycle is detected, false otherwise
     */
    private boolean detectCycle(Process process, Set<Process> visited,
                                Set<Process> currentlyVisiting,
                                Map<Process, List<Resource>> waitForGraph) {
        if (currentlyVisiting.contains(process)) {
            return true; // Cycle detected
        }
        if (visited.contains(process)) {
            return false; // Already checked this path
        }

        visited.add(process);
        currentlyVisiting.add(process);

        List<Resource> waitingFor = waitForGraph.get(process);
        if (waitingFor != null) {
            for (Resource resource : waitingFor) {
                Optional<Process> owner = findResourceOwner(resource);
                if (owner.isPresent() && detectCycle(owner.get(), visited, currentlyVisiting, waitForGraph)) {
                    return true;
                }
            }
        }

        currentlyVisiting.remove(process);
        return false;
    }

    /**
     * Finds the process that currently owns a resource.
     * @param resource The resource to check
     * @return Optional containing the owning process if found
     */
    private Optional<Process> findResourceOwner(Resource resource) {
        return processes.stream()
                .filter(Process::isCompleted)
                .filter(process -> getRequiredResources(process).contains(resource))
                .findFirst();
    }

    /**
     * Prints the current state of the system.
     */
    private void printCurrentState() {
        System.out.println("CURRENT SYSTEM STATE:");
        printResourceStates();
        printProcessStates();
        printEdgeStates();
    }

    /**
     * Prints the status of all resources.
     */
    private void printResourceStates() {
        System.out.println("Resources: " +
                resources.stream()
                        .map(resource -> String.format("%s(%s)",
                                resource,
                                resource.isAllocated() ? "allocated" : "available"))
                        .collect(Collectors.joining(", ")));
    }

    /**
     * Prints the status of all processes.
     */
    private void printProcessStates() {
        System.out.println("Processes: " +
                processes.stream()
                        .map(process -> String.format("%s(%s)",
                                process,
                                process.isCompleted() ? "completed" : "pending"))
                        .collect(Collectors.joining(", ")));
    }

    /**
     * Prints all connections in the graph with their statuses.
     */
    private void printEdgeStates() {
        System.out.println("Connections:");
        edges.forEach(edge -> System.out.printf("  %s -> %s (%s)%n",
                edge.getSource(),
                edge.getDestination(),
                edge.getStatus()));
    }
}

/**
 * Represents a directed edge between two nodes in the graph.
 */
class GraphEdge {
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

/**
 * Abstract base class for all graph nodes (resources and processes).
 */
abstract class GraphNode {
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

/**
 * Represents a resource node in the graph.
 */
class Resource extends GraphNode {
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

/**
 * Represents a process node in the graph.
 */
class Process extends GraphNode {
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
    public boolean isCompleted() { return completed; }

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