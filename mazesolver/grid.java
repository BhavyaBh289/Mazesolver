package mazesolver;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
class Grid {

    final int rows;
    final int cols;
    GUI g;
    List<Node> nodes;

    public Grid(GUI g) {
        // Calculating dimension of grid
        this.rows = MazeSolver.height / MazeSolver.nodeSize;
        this.cols = MazeSolver.width / MazeSolver.nodeSize;
        this.g = g;
        this.nodes = new ArrayList<>();

        // Creating nodes (default type EMPTY)
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                nodes.add(new Node(x, y));
            }
        }
    }

    public void draw(Graphics g) {
        // Draw nodes
        for (Node n : nodes) {
            n.draw(g);
        }

        // Draw x axis
        g.setColor(new Color(16, 17, 17));
        for (int i = 1; i < cols; i++) {
            g.drawLine(i * MazeSolver.nodeSize, 0, i * MazeSolver.nodeSize, MazeSolver.height);
        }

        // Draw y axis
        g.setColor(new Color(16, 17, 17));
        for (int i = 1; i < rows; i++) {
            g.drawLine(0, i * MazeSolver.nodeSize, MazeSolver.width, i * MazeSolver.nodeSize);
        }
    }

    public void repaint() {
        g.repaint();
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public void setStart(int x, int y) {
        // Remove old start
        for (Node n : nodes) {
            if (n.getType() == Node.Types.START) {
                n.setType(Node.Types.EMPTY);
            }
        }

        // Set type of node to start
        Node n = getNode(x, y);
        n.setType(Node.Types.START);
    }

    public Node getStart() {
        for (Node n : nodes) {
            // Find start and return that node
            if (n.getType() == Node.Types.START) {
                return n;
            }
        }
        // Else return null
        return null;
    }

    public void setEnd(int x, int y) {
        // Remove old end
        for (Node n : nodes) {
            if (n.getType() == Node.Types.END) {
                n.setType(Node.Types.EMPTY);
            }
        }

        // Set type of node to end
        Node n = getNode(x, y);
        n.setType(Node.Types.END);
    }

    public Node getEnd() {
        for (Node n : nodes) {
            // Find end and return that node
            if (n.getType() == Node.Types.END) {
                return n;
            }
        }
        // Else return null
        return null;
    }

    public void setWall(int x, int y) {
        Node n = getNode(x, y);
        n.setType(Node.Types.WALL);
    }

    public void setEmpty(int x, int y) {
        Node n = getNode(x, y);
        if (n.getType() == Node.Types.WALL) {
            n.setType(Node.Types.EMPTY);
        }
    }

    public void erase() {
        // Set all nodes to empty
        for (Node n : nodes) {
            n.setType(Node.Types.EMPTY);
        }
    }

    public void clear() {
        // Set all nodes to empty
        for (Node n : nodes) {
            Node.Types t = n.getType();
            // But skip: start, end and wall
            // Usefull to clear solution
            if (t == Node.Types.START || t == Node.Types.END || t == Node.Types.WALL) {
                continue;
            }
            n.setType(Node.Types.EMPTY);
        }
    }

    public Node getNode(int x, int y) {
        // Check coords if theyre wrong return null
        if (x < 0 || x >= cols || y < 0 || y >= rows) {
            return null;
        }

        // Else return propely node
        return nodes.get(x + y * cols);
    }

    public List<Node> getNeighbors(int x, int y) {
        // List for neighbours
        List<Node> result = new LinkedList<>();

        Node neighbor;
        // Check in X axis
        for (int xoff = -1; xoff < 2; xoff += 2) {
            neighbor = getNode(x + xoff, y);
            // Skip walls
            if (neighbor != null && neighbor.getType() != Node.Types.WALL) {
                result.add(neighbor);
            }
        }
        // Check in Y axis
        for (int yoff = -1; yoff < 2; yoff += 2) {
            neighbor = getNode(x, y + yoff);
            // Skip walls
            if (neighbor != null && neighbor.getType() != Node.Types.WALL) {
                result.add(neighbor);
            }
        }

        return result;
    }
    public List<Node> getNodes(){
        return nodes;
    }
}
class Node {

    final int x;
    final int y;
    Types type;

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
        this.type = Types.EMPTY;
    }

    public void setType(Types t) {
        this.type = t;
    }

    public Types getType() {
        return this.type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void draw(Graphics g) {
        // Set propely color
        switch (type) {
            case EMPTY:
                g.setColor(new Color(70, 78, 91));
                break;
            case WALL:
                g.setColor(new Color(0, 0, 0));
                break;
            case START:
                g.setColor(new Color(7, 101, 238));
                break;
            case END:
                g.setColor(new Color(238, 29, 7));
                break;
            case VISITED:
                g.setColor(new Color(127, 130, 127));
                break;
            case SOLUTION:
                g.setColor(new Color(42, 170, 42));
                break;

        }

        // Draw square with nodeSize size
        g.fillRect(x * MazeSolver.nodeSize, y * MazeSolver.nodeSize, MazeSolver.nodeSize, MazeSolver.nodeSize);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 13 * hash + this.x;
        hash = 13 * hash + this.y;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Node other = (Node) obj;
        if (this.x != other.x) {
            return false;
        }
        if (this.y != other.y) {
            return false;
        }
        return true;
    }

    // Enum to recognize node by program (color for human)
    public static enum Types {
        EMPTY, WALL, START, END, VISITED, SOLUTION
    }
}
