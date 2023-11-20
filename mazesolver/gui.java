package mazesolver;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
class GUI extends JPanel implements MouseListener, MouseMotionListener {

    static boolean setStart, setEnd, mouseInUse;
    public static boolean running;
    private int lastX, lastY;
    Grid grid;
    Menu menu;
    IConnectUI connetionLayer;

    public GUI(BackgroundWorker cl) {
        GUI.setStart = false;
        GUI.setEnd = false;
        GUI.mouseInUse = false;
        GUI.running = false;
        this.lastX = -1;
        this.lastY = -1;
        this.grid = new Grid(this);
        this.menu = new Menu(0, 0);
        this.connetionLayer = cl;

        setPreferredSize(new Dimension(MazeSolver.width, MazeSolver.height));
        setFocusable(true);
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(new keyboardHandler());
    }

    @Override
    public void paint(Graphics g) {
        grid.draw(g);
        menu.draw(g);
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        if (!running) {
            int x = (me.getX()) / MazeSolver.nodeSize;
            int y = (me.getY()) / MazeSolver.nodeSize;

            if (SwingUtilities.isLeftMouseButton(me)) {
                if (setStart) {
                    grid.setStart(x, y);
                } else if (setEnd) {
                    grid.setEnd(x, y);
                } else {
                    grid.setWall(x, y);
                }
            } else if (SwingUtilities.isRightMouseButton(me)) {
                grid.setEmpty(x, y);
            }

            this.repaint();
        }
    }

    @Override
    public void mousePressed(MouseEvent me) {
        Menu.showAlert = false;
        this.repaint();
        if (!running) {
            mouseInUse = true;
            repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        mouseInUse = false;
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        if (!running) {
            // Get te cursor position
            int x = (me.getX()) / MazeSolver.nodeSize;
            int y = (me.getY()) / MazeSolver.nodeSize;

            // Return if mouse is over grid
            if (me.getX() < 0 || x >= grid.getCols()) {
                return;
            }
            if (me.getY() < 0 || y >= grid.getRows()) {
                return;
            }

            // Calculate velocity of mouse
            int vX = Math.abs(x - lastX);
            int vY = Math.abs(y - lastY);

            // If distance steps by 1 node
            if (vX >= 1 || vY >= 1) {
                if (SwingUtilities.isLeftMouseButton(me)) {
                    grid.setWall(x, y);
                } else if (SwingUtilities.isRightMouseButton(me)) {
                    grid.setEmpty(x, y);
                }
                this.repaint();

                //Update last position
                lastX = x;
                lastY = y;
            }

            this.repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent me) {
    }

    private class keyboardHandler implements KeyListener {

        @Override
        public void keyPressed(KeyEvent e) {
            // If user press key
            switch (e.getKeyCode()) {
                // Start placing
                case KeyEvent.VK_S:
                    if (!running) {
                        setStart = true;
                        repaint();
                    }
                    break;
                // End placing
                case KeyEvent.VK_E:
                    if (!running) {
                        setEnd = true;
                        repaint();
                    }
                    break;
                // Menu toggling
                case KeyEvent.VK_M:
                    menu.toggle();
                    repaint();
                    break;
                // Grid clearing
                case KeyEvent.VK_C:
                    if (!running) {
                        grid.erase();
                        repaint();
                    }
                    break;
                // Maze randomizing
                case KeyEvent.VK_R:
                    if (!running) {
                        GeneratorFactory.getGenerator(grid).generate();
                        repaint();
                    }
                    break;
                // Solver run/stop
                case KeyEvent.VK_SPACE:
                    if (grid.getStart() != null && grid.getEnd() != null) {
                        running = !running;
                        if (running) {
                            connetionLayer.setGrid(grid);
                            connetionLayer.setStarted(true);
                        }
                        Menu.alertMessage = 0;
                        Menu.showAlert = false;
                    } else {
                        Menu.alertMessage = 1;
                        Menu.showAlert = true;
                        repaint();
                    }
                    break;
                // Solver choose
                case KeyEvent.VK_1:
                    if (!running) {
                        AlgorithmFactory.setID(1);
                        repaint();
                    }
                    break;
                case KeyEvent.VK_2:
                    if (!running) {
                        AlgorithmFactory.setID(2);
                        repaint();
                    }
                    break;
                case KeyEvent.VK_3:
                    if (!running) {
                        AlgorithmFactory.setID(3);
                        repaint();
                    }
                    break;
                case KeyEvent.VK_4:
                    if (!running) {
                        AlgorithmFactory.setID(4);
                        repaint();
                    }
                    break;
                // Adjusting speed of the algorithm
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_UP:
                    if (Menu.speedMultipler < 200) {
                        Menu.speedMultipler += 5;
                        repaint();
                    }
                    break;
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_DOWN:
                    if (Menu.speedMultipler > 5) {
                        Menu.speedMultipler -= 5;
                        repaint();
                    }
                    break;
            }
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyReleased(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_S:
                    setStart = false;
                    repaint();
                    break;
                case KeyEvent.VK_E:
                    setEnd = false;
                    repaint();
                    break;
            }
        }
    }
}
class Menu {

    public static boolean showAlert;
    public static short speedMultipler, alertMessage;
    int x, y;
    boolean hidden;

    public Menu(int x, int y) {
        Menu.showAlert = false;
        Menu.speedMultipler = 100;
        Menu.alertMessage = 0;
        this.x = x;
        this.y = y;
        this.hidden = true;
    }

    public static int getDelay() {
        return MazeSolver.delay * (205 - speedMultipler) / 100;
    }

    public void draw(Graphics g) {
        drawAlertBox(g);

        if (hidden) {
            // Draw small square with "M"
            g.setColor(new Color(41, 43, 45, 160));
            g.fillOval(20, 520, 40, 40);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 16));
            g.drawString("(M)", 29, 545);
        } else {
            // Draw oval box with text and data
            g.setColor(new Color(41, 43, 45, 160));
            g.fillRect(0, 360, 260, 360);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 16));
            if (GUI.mouseInUse) {
                g.setColor(new Color(61, 201, 7));
            }
            g.drawString("Mouse: add/remove walls", 15, 385);

            g.setColor(Color.WHITE);
            if (GUI.setStart) {
                g.setColor(new Color(7, 101, 238));
            }
            g.drawString("Mouse + S key: set start", 15, 415);

            g.setColor(Color.WHITE);
            if (GUI.setEnd) {
                g.setColor(new Color(238, 29, 7));
            }
            g.drawString("Mouse + E key: set end", 15, 445);

            g.setColor(Color.WHITE);
            g.drawString("C key: clear the grid", 15, 475);
            g.drawString("R key: generate random maze", 15, 505);

            if (GUI.running) {
                g.setColor(new Color(238, 29, 7));
            }
            g.drawString("Space: run/stop choosen algorithm", 15, 535);
            g.setColor(Color.WHITE);
            g.drawString("Arrows: adjust speed: " + speedMultipler + "%", 15, 565);

            // Draw oval with alghoritms
            g.setColor(new Color(41, 43, 45, 160));
            g.fillRect(740, 460, 180, 340);

            g.setColor(Color.WHITE);
            if (AlgorithmFactory.getID() == 1) {
                g.setColor(new Color(42, 170, 42));
            }
            g.drawString("[1]  BFS alghoritm", 765, 460);

            g.setColor(Color.WHITE);
            if (AlgorithmFactory.getID() == 2) {
                g.setColor(new Color(42, 170, 42));
            }
            g.drawString("[2]  DFS alghoritm", 765, 490);

            g.setColor(Color.WHITE);
            if (AlgorithmFactory.getID() == 3) {
                g.setColor(new Color(42, 170, 42));
            }
            g.drawString("[3]  A* alghoritm", 765, 520);

            g.setColor(Color.WHITE);
            if (AlgorithmFactory.getID() == 4) {
                g.setColor(new Color(42, 170, 42));
            }
            g.drawString("[4]  Best FS ", 765, 550);
        }
    }

    private void drawAlertBox(Graphics g) {
        // Draw oval box with text and data
        if (showAlert) {
            g.setColor(new Color(41, 43, 45, 240));
            g.fillRect(250, 0, 400, 50);
            g.setFont(new Font("Arial", Font.PLAIN, 18));
            g.setColor(Color.WHITE);
            switch (alertMessage) {
                case 1:
                    g.drawString("Please select start and end node", 320, 30);
                    break;
                case 2:
                    g.drawString("Solution could not be found", 340, 30);
                    break;
                case 3:
                    g.drawString("Solution has been found", 350, 30);
                    break;
                default:
                    break;
            }
        }
    }

    public void toggle() {
        this.hidden = !this.hidden;
    }
}
