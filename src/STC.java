import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Stack;
import javax.swing.*;

public class STC {
    public static JFrame myFrame;

    public static void main(String[] args) {
        int width = 693;
        int height = 545;
        ImageIcon icon = new ImageIcon(STC.class.getResource("icon.png"));

        myFrame = new JFrame("STC Algorithm");
        myFrame.setIconImage(icon.getImage());
        myFrame.setContentPane((new Map(width, height)));
        myFrame.pack();
        myFrame.setResizable(false);

        // Center
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double screenWidth = screenSize.getWidth();
        double screenHeight = screenSize.getHeight();
        int x = ((int) screenWidth - width) / 2;
        int y = ((int) screenHeight - height) / 2;

        myFrame.setLocation(x, y);
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myFrame.setVisible(true);
    }

    public static class Map extends JPanel {

        /**
         * Nested class for Helper
         */

        public static class Cell {
            public int row;
            public int col;

            public Cell(int row, int col) {
                this.row = row;
                this.col = col;
            }

            public String toString() {
                return " " + row + " " + col + " ";
            }
        }

        public class Edge {
            public Cell from;
            public Cell to;

            public Edge(Cell x, Cell y) {
                this.from = x;
                this.to = y;
            }

            public String toString() {
                return " " + this.from.toString() + " " + this.to.toString() + " ";
            }
        }

        private class MouseHandler implements MouseListener, MouseMotionListener {
            private int cur_row, cur_col, cur_val;

            @Override
            public void mousePressed(MouseEvent evt) {
                int row = (evt.getY() - 10) / squareSize;
                int col = (evt.getX() - 10) / squareSize;
                //System.out.println(row + " + " + col);
                if (row >= 0 && row < rows && col >= 0 && col < colums && !searching && !endOfSearch) {
                    cur_row = row;
                    cur_col = col;
                    cur_val = grid[row][col];
                    if (cur_val == EMPTY) {
                        grid[row][col] = OBST;
                    }
                    if (cur_val == OBST) {
                        grid[row][col] = EMPTY;
                    }
                }
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent evt) {
                int row = (evt.getY() - 10) / squareSize;
                int col = (evt.getX() - 10) / squareSize;
                //System.out.println("Dragged at " + row + " " + col);
                if (row >= 0 && row < rows && col >= 0 && col < colums && !searching && !endOfSearch) {
                    if ((row * colums + col != cur_row * colums + cur_col) && (cur_val == ROBOT)) {
                        int new_val = grid[row][col];
                        if (new_val == EMPTY) {
                            grid[row][col] = cur_val;
                            if (cur_val == ROBOT) {
                                robotStart.row = row;
                                robotStart.col = col;
                            }
                            grid[cur_row][cur_col] = new_val;
                            cur_row = row;
                            cur_col = col;
                            if (cur_val == ROBOT) {
                                robotStart.row = cur_row;
                                robotStart.col = cur_col;
                            }
                            cur_val = grid[row][col];
                        }
                    } else if (grid[row][col] != ROBOT) {
                        grid[row][col] = OBST;
                    }
                }
                repaint();
            }


            @Override
            public void mouseReleased(MouseEvent evt) {
            }

            @Override
            public void mouseEntered(MouseEvent evt) {
            }

            @Override
            public void mouseExited(MouseEvent evt) {
            }

            @Override
            public void mouseClicked(MouseEvent evt) {
            }

            @Override
            public void mouseMoved(MouseEvent evt) {
            }
        }

        private class ActionHandler implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent evt) {
                String cmd = evt.getActionCommand();
                if (cmd.equals("Clear")) {
                    fillGrid();
                } else if (cmd.equals("Animation") && !endOfSearch) {
                    if (!searching) {
                        initST();
                    }
                    message.setText("You can click 'Clear' when algorithm run completely");
                    searching = true;           // Find Spanning Tree
                    timer.setDelay(delay);
                    timer.start();
                    System.out.println("Start timer");
                    description.setText("Finding Spanning Tree");
                } else if (cmd.equals("Exit")) {
                    System.exit(0);
                }
            }
        }

        private class RepaintAction implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent evt) {
                // Draw spanning tree
                final Thread drawST = new Thread() {
                    int i = 0;

                    @Override
                    public void run() {
                        for (Edge e : E) {
                            i++;
                            i = i % 2;
                            if (i % 2 == 1) description.setText("Drawing ST ..   ");
                            else description.setText("Drawing ST .... ");

                            ST.add(e);
                            try {
                                sleep(100);
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                            repaint();
                        }
                    }
                };
                drawST.start();

                Thread showPath = new Thread() {
                    int i = 0;

                    @Override
                    public void run() {
                        try {
                            drawST.join();

                        } catch (InterruptedException err) {
                            err.printStackTrace();
                        }
                        for (Cell c : Path) {
                            i++;
                            i = i % 2;
                            if (i % 2 == 1) description.setText("Drawing Path ..   ");
                            else description.setText("Drawing Path .... ");

                            goPath.add(c);
                            try {
                                sleep(90);
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                            repaint();
                        }
                        description.setText("Draw Path Complete !");
                    }
                };
                showPath.start();

                endOfSearch = true;
                if (endOfSearch) {
                    timer.stop();
                    System.out.println("Timer Stopped");
                }
                repaint();

            }
        }

        /**
         * Atribute of Map class
         */
        ImageIcon icon0 =new ImageIcon(getClass().getClassLoader().getResource("step0.png"));
        Image img0=icon0.getImage();

        ImageIcon icon1 =new ImageIcon(getClass().getClassLoader().getResource("step1.png"));
        Image img1=icon1.getImage();

        ImageIcon icon2 =new ImageIcon(getClass().getClassLoader().getResource("step2.png"));
        Image img2=icon2.getImage();

        ImageIcon rock =new ImageIcon(getClass().getClassLoader().getResource("rock.png"));
        Image Rock = rock.getImage();

        private final static int
                EMPTY = 0,              // State of cell
                OBST = 1,
                ROBOT = 2,
                VERTEX = 3,
                ROUTE = 4,              // Spanning Tree
                STEP = 5;               // Step of robot along with STree


        JLabel message;     // Message to user
        private final static String msgDraw = "Di chuyển Robot => Vẽ chướng ngại vật => nhấn 'Animation'";

        JLabel description;


        int
                rows = 16,
                colums = 16,
                squareSize = 500 / rows;

        int[][] grid;
        Cell robotStart;
        //boolean found;              // Three boolean for find Spanning Tree
        boolean endOfSearch;          // Done Search
        boolean searching;            // Searching

        ArrayList<Cell> V = new ArrayList<Cell>();          // Graph, danh sách các đỉnh của đồ thị
        ArrayList<Edge> E = new ArrayList<Edge>();          // Danh sách cạnh của Cây Khung cần tìm
        ArrayList<Edge> ST = new ArrayList<Edge>();          // Cây khung mà chương trình dựng ra
        ArrayList<Cell> Path = new ArrayList<Cell>();        // Đường đi của robot
        ArrayList<Cell> goPath = new ArrayList<Cell>();   // Đường đi của robot mà chương trình dựng ra
        boolean rightHand = false;
        int delay;
        Timer timer;
        RepaintAction action = new RepaintAction();

        /**
         * Constructor of Map
         *
         * @param width
         * @param height
         */

        public Map(int width, int height) {
            setLayout(null);
            MouseHandler listener = new MouseHandler();
            addMouseListener(listener);
            addMouseMotionListener(listener);
            setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLUE));
            setPreferredSize(new Dimension(width, height));

            grid = new int[rows][colums];


            description = new JLabel("Initializing ...", JLabel.CENTER);
            description.setForeground(Color.BLUE);
            description.setFont(new Font("Helvetica", Font.PLAIN, 16));
            message = new JLabel(msgDraw, JLabel.CENTER);
            message.setForeground(Color.BLUE);
            message.setFont(new Font("Helvetica", Font.PLAIN, 16));

            JButton statusButton = new JButton("Status");
            statusButton.setBackground(Color.pink);
            statusButton.setEnabled(false);

            JButton clearButton = new JButton("Clear");
            clearButton.addActionListener(new ActionHandler());
            clearButton.setBackground(Color.lightGray);
            clearButton.setToolTipText("Nhấn lần đầu để xóa kết quả tìm kiếm. Nhấn lần hai để xóa chướng ngại vật");

            JButton animationButton = new JButton("Animation");
            animationButton.addActionListener(new ActionHandler());
            animationButton.setBackground(Color.lightGray);
            animationButton.setToolTipText("Nhấn vào đây để tiến hành quá trình chạy thuật toán STC");

            JButton exitButton = new JButton("Exit");
            exitButton.addActionListener(new ActionHandler());
            exitButton.setBackground(Color.RED);
            exitButton.setToolTipText("Tắt chương trình");


            add(description);
            add(message);
            add(statusButton);
            add(clearButton);
            add(exitButton);
            add(animationButton);

            message.setBounds(0, 515, 500, 23);
            clearButton.setBounds(520, 20, 170, 50);
            animationButton.setBounds(520, 90, 170, 50);
            statusButton.setBounds(520, 160, 170, 50);
            description.setBounds(520, 220, 170, 50);
            exitButton.setBounds(520, 455, 170, 50);


            // Thêm mấy text chỉ dẫn màu mè : Robot -> Red, Way -> bla bla bla


            delay = 3;
            timer = new Timer(delay, action);

            // Clear at start of program
            fillGrid();
        }

        /**
         * Clear the Grid map
         */
        private void fillGrid() {
            // First Click CLEAR
            if (searching || endOfSearch) {
                for (int r = 0; r < rows; r++) {
                    for (int c = 0; c < colums; c++) {
                        if (grid[r][c] == ROBOT) {
                            robotStart = new Cell(r, c);
                        } else if (grid[r][c] == VERTEX || grid[r][c] == ROUTE || grid[r][c] == STEP) {
                            grid[r][c] = EMPTY;
                        }
                    }
                    searching = false;
                }
            }
            // Second click
            else {
                for (int r = 0; r < rows; r++) {
                    for (int c = 0; c < colums; c++) {
                        grid[r][c] = EMPTY;
                    }
                }
                robotStart = new Cell(1, 1);
            }

            searching = false;
            endOfSearch = false;
            rightHand = false;
            grid[robotStart.row][robotStart.col] = ROBOT;
            ST.removeAll(ST);
            goPath.removeAll(goPath);
            message.setText(msgDraw);
            timer.stop();
            repaint();
        }

        // Danh sách các đỉnh kề với nó trong đồ thị
        private ArrayList<Cell> createSuccesors(Cell current) {
            int r = current.row;
            int c = current.col;
            ArrayList<Cell> temp = new ArrayList<Cell>();

            if (r > 1 && r % 2 == 1 && grid[r - 2][c] != OBST) {
                Cell cell = new Cell(r - 2, c);
                temp.add(cell);
            }
            if (r < rows - 2 && r % 2 == 1 && grid[r + 2][c] != OBST) {
                Cell cell = new Cell(r + 2, c);
                temp.add(cell);
            }
            if (c > 1 && c % 2 == 1 && grid[r][c - 2] != OBST) {
                Cell cell = new Cell(r, c - 2);
                temp.add(cell);
            }
            if (c < colums - 2 && c % 2 == 1 && grid[r][c + 2] != OBST) {
                Cell cell = new Cell(r, c + 2);
                temp.add(cell);
            }
            return temp;
        }


        private int isInList(ArrayList<Cell> list, Cell current) {
            int index = -1;
            for (int i = 0; i < list.size(); i++) {
                if (current.row == list.get(i).row && current.col == list.get(i).col) {
                    index = i;
                    break;
                }
            }
            return index;
        }

        // Dựng đồ thị (các đỉnh thuộc thành phần liên thông với đỉnh Start), dùng Deapth First Search
        // Đồng thời dựng cây khung , vì DFS ouput ra một cây khung của đồ thị
        private void findConnectedComponent(Cell v) {
            Stack<Cell> stack;
            stack = new Stack<Cell>();
            ArrayList<Cell> succesors;
            stack.push(v);
            V.add(v);
            while (!stack.isEmpty()) {
                v = stack.pop();
                succesors = createSuccesors(v);
                for (Cell c : succesors) {
                    if (isInList(V, c) == -1) {
                        stack.push(c);
                        V.add(c);
                        E.add(new Edge(v, c));
                    }
                }
            }
            System.out.println(V.size() + " vertex available of Graph");
            for (Cell c : V) {
                System.out.println(c.row + " " + c.col);
                grid[c.row][c.col] = VERTEX;
                try {
                    Thread.sleep(2);
                } catch (InterruptedException err) {
                    err.printStackTrace();
                }
                repaint();
            }
            grid[robotStart.row][robotStart.col] = ROBOT;
            System.out.println("We have " + E.size() + " edge");
            for (int i = 0; i < E.size(); i++) {
                Cell from = E.get(i).from;
                Cell to = E.get(i).to;
                if (from.row == robotStart.row && from.col == robotStart.col && robotStart.col == to.col && robotStart.row < to.row) rightHand = true;
                System.out.println(from.toString() + "=> " + to.toString());
            }
        }

        // Tìm Spanning Tree
        private void initST() {
            V.removeAll(V);
            E.removeAll(E);
            ST.removeAll(ST);
            findConnectedComponent(robotStart);

            Path.removeAll(Path);
            goPath.removeAll(goPath);
            Path = wallFollow();
        }

        /**
        - Sau khi tìm được Spanning Tree, ta có một mê cung (maze).
            Dùng thuật toán Wall Follower (cụ thể là quy tắc bàn tay phải) để tính toán các ô cần đi
            Ban đầu mặc định Robot quay đầu về hướng Nam (thật ra hướng nào cũng okie). Ô hiện tại là ô RobotStart
            Ở mỗi bước lặp, ta liệt kê danh sách 4 neighbour của ô hiện tại (r,c) là (r-1,c),(r+1,c),(r,c-1),(r,c+1)
            Một ô có thể đến được từ ô hiện tại nếu :
            Chúng thuộc cùng một big-Cell (2x2)
            Hoặc thuộc 2 big-Cell khác nhau, nhưng tâm điểm của 2 big-Cell này tạo thành 1 edge của Spanning Tree
        - Xác định ô kế tiếp để đi
            Nếu có thể rẽ phải => Rẽ phải
            Nếu không, Nếu có thể đi thẳng => Đi thẳng
            Nếu không, Nếu có thể rẽ trái => Rẽ trái
            Các hướng thẳng, trái, phải xác định dựa trên một biến Direction, và vị trí của ô neighbour so với ô hiện tại
        - Quá trình kết thúc khi gặp lại ô ban đầu. Lưu kq trong temp. Temp sẽ có > 2x(E.size()) node.

         */

        //
        boolean onSegment(Cell p, Cell q, Cell r) {
            if (q.row <= Math.max(p.row, r.row) && q.row >= Math.min(p.row, r.row) &&
                    q.col <= Math.max(p.col, r.col) && q.col >= Math.min(p.col, r.col))
                return true;

            return false;
        }

        // Hướng của tam giác (p,q,r)
        int orientation(Cell p, Cell q, Cell r) {
            int val = (q.col - p.col) * (r.row - q.row) -
                    (q.row - p.row) * (r.col - q.col);

            if (val == 0) return 0;  // colinear

            return (val > 0) ? 1 : 2; // clock or counterclock wise
        }

        boolean isIntersect(Cell p1, Cell q1, Cell p2, Cell q2) {
            // Find the four orientations needed for general and
            // special cases
            int o1 = orientation(p1, q1, p2);
            int o2 = orientation(p1, q1, q2);
            int o3 = orientation(p2, q2, p1);
            int o4 = orientation(p2, q2, q1);

            // General case
            if (o1 != o2 && o3 != o4)
                return true;


            // Special Cases
            // p1, q1 and p2 are colinear and p2 lies on segment p1q1
            if (o1 != 0 && onSegment(p1, p2, q1)) return true;

            // p1, q1 and p2 are colinear and q2 lies on segment p1q1
            if (o2 != 0 && onSegment(p1, q2, q1)) return true;

            // p2, q2 and p1 are colinear and p1 lies on segment p2q2
            if (o3 != 0 && onSegment(p2, p1, q2)) return true;

            // p2, q2 and q1 are colinear and q1 lies on segment p2q2
            if (o4 != 0 && onSegment(p2, q1, q2)) return true;

            return false; // Doesn't fall in any of the above cases
        }

        boolean canGo(Cell X, Cell Y, int Ori) {
            int Xr = X.row;
            int Xc = X.col;
            int Yr = Y.row;
            int Yc = Y.col;
            // X là ô hiện tại, chắc chắn OK
            // Y là ô kế tiếp, Cần kiểm tra các bước, nếu okie hết thì return true

            // 1. Thuộc map
            if (Yr < 0 || Yr > rows - 1 || Yc < 0 || Yc > colums - 1) return false;

            // 2. Chung cạnh với X (==r || ==c)
            // -> Cái này hiển nhiên là true do cách chọn ô Y

            // 3. Không là ô vật cản dạng big-Cell (OBST)
            if (Yr % 2 == 1 && Yc % 2 == 1 && grid[Yr][Yc] == OBST) return false;
            if (Yr % 2 == 0 && Yc % 2 == 0 && grid[Yr + 1][Yc + 1] == OBST) return false;
            if (Yr % 2 == 0 && Yc % 2 == 1 && grid[Yr + 1][Yc] == OBST) return false;
            if (Yr % 2 == 1 && Yc % 2 == 0 && grid[Yr][Yc + 1] == OBST) return false;

            // 4. Không bị một cạnh nào của Spanning Tree (Wall) chắn. Phức tạp =.=
            for (Edge e : E) {
                if (!isIntersect(X, Y, e.from, e.to)) continue;
                else {
                    if (Ori == 1) {
                        if (Y.col == e.from.col && e.from.col == e.to.col && Y.row != Math.max(e.from.row, e.to.row)){
                            return false;
                        }
                    }
                    if (Ori == 2) {
                        if (X.col == e.from.col && e.from.col == e.to.col && X.row != Math.max(e.from.row, e.to.row)) {
                            return false;
                        }
                    }
                    if (Ori == 3) {
                        if (X.row == e.from.row && e.from.row == e.to.row && X.col != Math.max(e.from.col, e.to.col)) {
                            return false;
                        }
                    }
                    if (Ori == 4) {
                        if (Y.row == e.from.row && e.from.row == e.to.row && Y.col != Math.max(e.from.col, e.to.col)) {
                            return false;
                        }
                    }
                }

            }
            return true;
        }

        private ArrayList<Cell> wallFollow() {
            ArrayList<Cell> temp = new ArrayList<Cell>();
            Cell curCell = robotStart;
            temp.add(curCell);
            int curDir = 1;           // 1,2,3,4. Tương đương với việc hướng mặt về 4 phía 1-Đông, 2-Tây, 3-Bắc, 4-Nam
            int r, c;
            Cell R, S, L;
            int i = 0;

            if (rightHand) {
                do {
                    System.out.println("Right Hand Algorithm");
                    i++;
                    r = curCell.row;
                    c = curCell.col;

                    if (curDir == 1) {
                        // Xét xem nếu có thể rẽ phải
                        R = new Cell(r + 1, c);
                        S = new Cell(r, c + 1);
                        L = new Cell(r - 1, c);
                        if (canGo(curCell, R, 4)) {
                            curCell = R;
                            temp.add(R);
                            curDir = 4;
                        }
                        // Không thể rẽ phải, xét xem nếu có thể đi thẳng
                        else if (canGo(curCell, S, 1)) {
                            curCell = S;
                            temp.add(S);
                            curDir = 1;
                        }
                        // Không thể rẽ phải, đi thẳng, xét xem nếu có thể rẽ trái
                        else if (canGo(curCell, L, 3)) {
                            curCell = L;
                            temp.add(L);
                            curDir = 3;
                        }

                    } else if (curDir == 2) {
                        R = new Cell(r - 1, c);
                        S = new Cell(r, c - 1);
                        L = new Cell(r + 1, c);
                        // Xét xem nếu có thể rẽ phải
                        if (canGo(curCell, R, 3)) {
                            curCell = R;
                            temp.add(R);
                            curDir = 3;
                        }
                        // Không thể rẽ phải, xét xem nếu có thể đi thẳng
                        else if (canGo(curCell, S, 2)) {
                            curCell = S;
                            temp.add(S);
                            curDir = 2;
                        }
                        // Không thể rẽ phải, đi thẳng, xét xem nếu có thể rẽ trái
                        else if (canGo(curCell, L, 4)) {
                            curCell = L;
                            temp.add(L);
                            curDir = 4;
                        }
                    } else if (curDir == 3) {
                        R = new Cell(r, c + 1);
                        S = new Cell(r - 1, c);
                        L = new Cell(r, c - 1);
                        // Xét xem nếu có thể rẽ phải
                        if (canGo(curCell, R, 1)) {
                            curCell = R;
                            temp.add(R);
                            curDir = 1;
                        }
                        // Không thể rẽ phải, xét xem nếu có thể đi thẳng
                        else if (canGo(curCell, S, 3)) {
                            curCell = S;
                            temp.add(S);
                            curDir = 3;
                        }
                        // Không thể rẽ phải, đi thẳng, xét xem nếu có thể rẽ trái
                        else if (canGo(curCell, L, 2)) {
                            curCell = L;
                            temp.add(L);
                            curDir = 2;
                        }
                    } else {
                        R = new Cell(r, c - 1);
                        S = new Cell(r + 1, c);
                        L = new Cell(r, c + 1);
                        // Xét xem nếu có thể rẽ phải
                        if (canGo(curCell, R, 2)) {
                            curCell = R;
                            temp.add(R);
                            curDir = 2;
                        }
                        // Không thể rẽ phải, xét xem nếu có thể đi thẳng
                        else if (canGo(curCell, S, 4)) {
                            curCell = S;
                            temp.add(S);
                            curDir = 4;
                        }
                        // Không thể rẽ phải, đi thẳng, xét xem nếu có thể rẽ trái
                        else if (canGo(curCell, L, 1)) {
                            curCell = L;
                            temp.add(L);
                            curDir = 1;
                        }
                    }
                    System.out.println(curCell + " " + curDir);
                    if (i >= 500) break;
                } while (curCell.row != robotStart.row || curCell.col != robotStart.col);
            } else {
                System.out.println("Left Hand Algorithm");
                do {
                    i++;
                    r = curCell.row;
                    c = curCell.col;

                    if (curDir == 1) {
                        // Xét xem nếu có thể rẽ phải
                        R = new Cell(r + 1, c);
                        S = new Cell(r, c + 1);
                        L = new Cell(r - 1, c);
                        if (canGo(curCell, L, 3)) {
                            curCell = L;
                            temp.add(L);
                            curDir = 3;
                        }
                        // Không thể rẽ phải, xét xem nếu có thể đi thẳng
                        else if (canGo(curCell, S, 1)) {
                            curCell = S;
                            temp.add(S);
                            curDir = 1;
                        }
                        // Không thể rẽ phải, đi thẳng, xét xem nếu có thể rẽ trái
                        else if (canGo(curCell, R, 4)) {
                            curCell = R;
                            temp.add(R);
                            curDir = 4;
                        }

                    } else if (curDir == 2) {
                        R = new Cell(r - 1, c);
                        S = new Cell(r, c - 1);
                        L = new Cell(r + 1, c);
                        // Xét xem nếu có thể rẽ phải
                        if (canGo(curCell, L, 4)) {
                            curCell = L;
                            temp.add(L);
                            curDir = 4;
                        }
                        // Không thể rẽ phải, xét xem nếu có thể đi thẳng
                        else if (canGo(curCell, S, 2)) {
                            curCell = S;
                            temp.add(S);
                            curDir = 2;
                        }
                        // Không thể rẽ phải, đi thẳng, xét xem nếu có thể rẽ trái
                        else if (canGo(curCell, R, 3)) {
                            curCell = R;
                            temp.add(R);
                            curDir = 3;
                        }
                    } else if (curDir == 3) {
                        R = new Cell(r, c + 1);
                        S = new Cell(r - 1, c);
                        L = new Cell(r, c - 1);
                        // Xét xem nếu có thể rẽ phải
                        if (canGo(curCell, L, 2)) {
                            curCell = L;
                            temp.add(L);
                            curDir = 2;
                        }
                        // Không thể rẽ phải, xét xem nếu có thể đi thẳng
                        else if (canGo(curCell, S, 3)) {
                            curCell = S;
                            temp.add(S);
                            curDir = 3;
                        }
                        // Không thể rẽ phải, đi thẳng, xét xem nếu có thể rẽ trái
                        else if (canGo(curCell, R, 1)) {
                            curCell = R;
                            temp.add(R);
                            curDir = 1;
                        }
                    } else {
                        R = new Cell(r, c - 1);
                        S = new Cell(r + 1, c);
                        L = new Cell(r, c + 1);
                        // Xét xem nếu có thể rẽ phải
                        if (canGo(curCell, L, 1)) {
                            curCell = L;
                            temp.add(L);
                            curDir = 1;
                        }
                        // Không thể rẽ phải, xét xem nếu có thể đi thẳng
                        else if (canGo(curCell, S, 4)) {
                            curCell = S;
                            temp.add(S);
                            curDir = 4;
                        }
                        // Không thể rẽ phải, đi thẳng, xét xem nếu có thể rẽ trái
                        else if (canGo(curCell, R, 2)) {
                            curCell = R;
                            temp.add(R);
                            curDir = 2;
                        }
                    }
                    System.out.println(curCell + " " + curDir);
                    if (i >= 500) break;
                } while (curCell.row != robotStart.row || curCell.col != robotStart.col);
            }

            System.out.println("We have " + temp.size() + " cells in actully Path");
            for (Cell cc : temp) {
                System.out.println(cc.toString());
            }
            return temp;
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.DARK_GRAY);
            g.fillRect(10, 10, colums * squareSize + 1, rows * squareSize + 1);
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < colums; c++) {
                    if (grid[r][c] == EMPTY) {
                        g.setColor(Color.WHITE);
                        g.fillRect(11 + c * squareSize, 11 + r * squareSize, squareSize - 1, squareSize - 1);
                    } else if (grid[r][c] == VERTEX) {
                        g.setColor(Color.WHITE);
                        g.fillRect(11 + c * squareSize, 11 + r * squareSize, squareSize - 1, squareSize - 1);
                        g.setColor(Color.GREEN);
                        /*
                        try {
                            Thread.sleep(7);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        */
                        g.fillOval(4 + c * squareSize, 4 + r * squareSize, 10, 12);
                    } else if (grid[r][c] == ROBOT) {
                        g.setColor(Color.WHITE);
                        g.fillRect(11 + c * squareSize, 11 + r * squareSize, squareSize - 1, squareSize - 1);
                        g.setColor(Color.RED);
                        g.fillOval(2 + c * squareSize, 2 + r * squareSize, 16, 16);
                        //g.fillRect(13 + c * squareSize, 13 + r * squareSize, squareSize - 5, squareSize - 5);
                    } else if (grid[r][c] == OBST) {
                        //g.setColor(Color.BLACK);
                        //g.fillRect(11 + c * squareSize, 11 + r * squareSize, squareSize - 1, squareSize - 1);
                        g.drawImage(Rock,11 + c * squareSize, 11 + r * squareSize, squareSize - 1, squareSize - 1,null);
                    }
                }
            }
            g.setColor(Color.BLUE);
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < colums; c++)
                    if (r % 2 == 0 && c % 2 == 0) {
                        g.drawRect(11 + c * squareSize, 11 + r * squareSize, 2 * squareSize - 1, 2 * squareSize - 1);
                    }
            }
            g.setColor(Color.darkGray);
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < colums; c++)
                    if (r % 2 == 1 && c % 2 == 1 && grid[r][c] != VERTEX && grid[r][c] != ROBOT) {
                        g.fillOval(6 + c * squareSize, 6 + r * squareSize, 8, 10);
                    }
            }

            int step=0;
            for(int i=0;i<goPath.size();i++) {
                step++;
                Cell cell=goPath.get(i);
                int r = cell.row;
                int c = cell.col;
                if(step==goPath.size()){
                    g.drawImage(img1,11 + c * squareSize, 11 + r * squareSize, squareSize - 1, squareSize - 1,null);
                }
                else g.drawImage(img2,11 + c * squareSize, 11 + r * squareSize, squareSize - 1, squareSize - 1,null);
            }

            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.RED);
            g2d.setStroke(new BasicStroke(3));
            for(int i=0;i<ST.size();i++) {
                Edge e=ST.get(i);
                Cell from = e.from;
                Cell to = e.to;
                //System.out.println(from.toString() + "=> " + to.toString());
                g2d.drawLine(10 + from.col * squareSize, 10 + from.row * squareSize, 10 + to.col * squareSize, 10 + to.row * squareSize);
            }


        }
    }
}
