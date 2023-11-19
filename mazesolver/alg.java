package mazesolver;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

class AlgorithmFactory {

    private static int id = 1;

    public static void setID(int i) {
        id = i;
    }

    public static int getID() {
        return id;
    }

    public static IAlgorithm getAlgorithm() {
        switch (id) {
            case 1:
                return new BFSAlgorithm();
            case 2:
                return new DFSAlgorithm();
            case 3:
                return new AStarAlgorithm();
            default:
                return null;
        }
    }
}
class AStarAlgorithm implements IAlgorithm {

    @Override
    public void solve(IConnectWorker worker, Grid grid) throws InterruptedException {
        boolean solutionFound = false;
        Node startNode = grid.getStart();
        Node endNode = grid.getEnd();
        grid.clear();

        HashMap<Node, Node> parentMap = new HashMap<>();
        HashSet<Node> visited = new HashSet<>();
        Map<Node, Double> distances = new HashMap<>();
        Queue<Pair> priorityQueue = new PriorityQueue<>();
        for (Node n : grid.getNodes()) {
            distances.put(n, Double.POSITIVE_INFINITY);
        }

        distances.put(startNode, new Double(0));
        priorityQueue.add(new Pair(startNode, 0, 0));
        Node current = null;
        Map<Node, Node> solutionMap = new LinkedHashMap<>();
        solutionMap.put(startNode, null);

        while (!priorityQueue.isEmpty()) {
            if (!GUI.running) {
                return;
            }

            current = priorityQueue.poll().getNode();
            if (!visited.contains(current)) {
                visited.add(current);
                if (endNode.equals(current)) {
                    solutionFound = true;
                    Utils.checkForAlerts(solutionFound, grid);
                    Utils.showSolution(solutionMap, grid);
                    worker.stopRunning();
                    return;
                }

                List<Node> neighbors = grid.getNeighbors(current.getX(), current.getY());
                for (Node neighbor : neighbors) {
                    if (!visited.contains(neighbor)) {
                        if (neighbor != grid.getEnd() && neighbor != grid.getStart()) {
                            neighbor.setType(Node.Types.VISITED);
                            Thread.sleep(Menu.getDelay());
                            grid.repaint();
                        }

                        double predictedDistance = distance(neighbor, endNode);
                        double neighborDistance = distance(current, neighbor);
                        double totalDistance = distance(current, startNode) + neighborDistance + predictedDistance;

                        if (totalDistance < distances.get(neighbor)) {
                            distances.put(neighbor, totalDistance);
                            parentMap.put(neighbor, current);
                            priorityQueue.add(new Pair(neighbor, totalDistance, predictedDistance));
                            solutionMap.put(neighbor, current);
                        }
                    }
                }
            }
        }
        Utils.checkForAlerts(solutionFound, grid);
        GUI.running = false;
    }

    private double distance(Node a, Node b) {
        return Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2));
    }

}

class Pair implements Comparable {

    Node node;
    double totalDistance;
    double predictedDistance;

    public Pair(Node n, double totalDistance, double predictedDistance) {
        this.node = n;
        this.totalDistance = totalDistance;
        this.predictedDistance = predictedDistance;
    }

    @Override
    public int compareTo(Object o) {
        if (this.predictedDistance > ((Pair) o).predictedDistance) {
            return 1;
        } else if (this.predictedDistance < ((Pair) o).predictedDistance) {
            return -1;
        } else {
            return 0;
        }
    }

    public Node getNode() {
        return node;
    }
}
class BFSAlgorithm implements IAlgorithm {

    @Override
    public void solve(IConnectWorker worker, Grid grid) throws InterruptedException {
        boolean solutionFound = false;
        Node start = grid.getStart();
        Node end = grid.getEnd();
        grid.clear();

        Queue<Node> queue = new LinkedList<>();
        Map<Node, Node> solutionMap = new LinkedHashMap<>();
        queue.add(start);
        solutionMap.put(start, null);

        while (queue.size() > 0) {
            if (!GUI.running) {
                return;
            }

            Node state = queue.poll();
            List<Node> childs = grid.getNeighbors(state.getX(), state.getY());
            for (Node chlid : childs) {
                if (worker.workerStopped()) {
                    return;
                }

                if (end.equals(chlid)) {
                    solutionFound = true;
                    Utils.checkForAlerts(solutionFound, grid);
                    solutionMap.put(chlid, state);
                    Utils.showSolution(solutionMap, grid);
                    worker.stopRunning();
                    return;
                }

                if (!solutionMap.containsKey(chlid)) {
                    queue.add(chlid);
                    solutionMap.put(chlid, state);
                    if (chlid != grid.getEnd() && chlid != grid.getStart()) {
                        chlid.setType(Node.Types.VISITED);
                        worker.getGrid().repaint();
                        Thread.sleep(Menu.getDelay());
                    }
                }
            }
        }
        Utils.checkForAlerts(solutionFound, grid);
    }
}
class DFSAlgorithm implements IAlgorithm {

    @Override
    public void solve(IConnectWorker worker, Grid grid) throws InterruptedException {
        boolean solutionFound = false;
        Node start = grid.getStart();
        Node end = grid.getEnd();
        grid.clear();

        Stack<Node> stack = new Stack<>();
        Map<Node, Node> solutionMap = new LinkedHashMap<>();
        stack.add(start);
        solutionMap.put(start, null);

        while (stack.size() > 0) {
            if (!GUI.running) {
                return;
            }

            Node state = stack.peek();
            stack.pop();

            List<Node> childs = grid.getNeighbors(state.getX(), state.getY());
            for (Node child : childs) {
                if (end.equals(child)) {
                    solutionFound = true;
                    Utils.checkForAlerts(solutionFound, grid);
                    solutionMap.put(child, state);
                    Utils.showSolution(solutionMap, grid);
                    worker.stopRunning();
                    return;
                }

                if (!solutionMap.containsKey(child)) {
                    stack.add(child);
                    solutionMap.put(child, state);
                    if (child != grid.getEnd() && child != grid.getStart()) {
                        child.setType(Node.Types.VISITED);
                        worker.getGrid().repaint();
                        Thread.sleep(Menu.getDelay());
                    }
                }
            }
        }
        Utils.checkForAlerts(solutionFound, grid);
        GUI.running = false;
    }
}
interface IAlgorithm {

    public void solve(IConnectWorker worker, Grid grid) throws InterruptedException;

}
class Utils {

    public static void showSolution(Map<Node, Node> solutionMap, Grid grid) {
        Node state = grid.getEnd();
        while (state != grid.getStart()) {
            if (state != grid.getEnd() && state != grid.getStart()) {
                state.setType(Node.Types.SOLUTION);
            }
            Node parent = solutionMap.get(state);
            state = parent;
            grid.repaint();
        }
    }

    public static void checkForAlerts(boolean solved, Grid grid) {
        if (solved) {
            Menu.alertMessage = 3;
            Menu.showAlert = true;
            grid.repaint();
        } else {
            Menu.alertMessage = 2;
            Menu.showAlert = true;
            GUI.running = false;
            grid.repaint();
        }
    }
}

