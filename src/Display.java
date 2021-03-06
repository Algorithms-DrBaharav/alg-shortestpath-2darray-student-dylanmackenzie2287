
// NO NEED to change anything here!
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JComboBox;

import java.awt.Color;
import java.awt.Font;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.awt.Rectangle;

/*
 * 
 * 
 *   We will keep things NOT too flexible here,
 *   so as to make the design and code simpler.
 * 
 *  Things will be in fixed-location on screen.
 *  This is called Absolute-positioning (as compared to using layouts)
 *  http://docs.oracle.com/javase/tutorial/uiswing/layout/none.html
 *  All the definition are grouped below
 *  The graphic tries to follow the look of:
 *  http://www.bitstorm.org/gameoflife/ 
 * 
 *   Display is responsible for drawing the game.
 *   Display :
 *   	Creates and manages interactions with the game object
 *      Creates and manages the items display (getting the Frame from main)
 *      Handles all non-button events. That means, getting mouse clicks, 
 *      determining which cell they hit, and managing response.
 * 
 * 
 */
@SuppressWarnings("serial")
public class Display extends JComponent {

    int frameWidth, frameHeight;
    boolean loopModeOn = false;
    int stepCounter = 0;

    private final static long STEP_TIME_MILLIS = 150; // time in milliseconds

    private final static int CELL_COLS = 50;
    private final static int CELL_ROWS = 40;

    private final static int CELL_SIDE_PIXELS = 10;
    private final static int CELL_TOP_X = 20;
    private final static int CELL_TOP_Y = 20;

    private final static Color COLOR_ALIVE = Color.YELLOW;
    private final static Color COLOR_DEAD = Color.BLUE;
    private final static Color COLOR_NONE = Color.GRAY;
    private final static Color COLOR_GRID = Color.WHITE;
    private final static Color COLOR_START = Color.GREEN;
    private final static Color COLOR_END = Color.PINK;
    private final static Color COLOR_FONT_CELL = Color.RED;
    
    
    // Graphics locations
    private final Rectangle MODEL_RECT;
    private final Rectangle NEXT_RECT;
    private final Rectangle START_RECT;
    private final Rectangle N8_RECT;
    private final Rectangle STEP_RECT;

    private final JButton startButton = new JButton();
    private final JButton n8Button = new JButton();
    private final JButton nextButton = new JButton();
    private final JLabel stepLabel = new JLabel();

    private final JComboBox<String> modelChooser = new JComboBox<>(PathFinderGame.modelNames);

    private PathFinderGame game;

    public Display(int frameWidth, int frameHeight) {
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;

        MODEL_RECT = new Rectangle(20, frameHeight - 70, 200, 20);
        NEXT_RECT = new Rectangle(250, frameHeight - 70, 80, 20);
        START_RECT = new Rectangle(340, frameHeight - 70, 80, 20);
        N8_RECT = new Rectangle(450, frameHeight - 70, 40, 20);
        STEP_RECT = new Rectangle(frameWidth - 50, frameHeight - 70, 40, 20);

        // create a game
        game = new PathFinderGame(CELL_ROWS, CELL_COLS);

        putButtons();

        // The mouse listener is for event on the Grid.
        // for all the rest, there are individual listeners
        class myMouseListener implements MouseListener {

            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                Rectangle grid = new Rectangle(
                        CELL_TOP_X, CELL_TOP_Y,
                        CELL_COLS * CELL_SIDE_PIXELS,
                        CELL_ROWS * CELL_SIDE_PIXELS);

                if (grid.contains(x, y)) {
                    int ii = (x - CELL_TOP_X) / CELL_SIDE_PIXELS;
                    int jj = (y - CELL_TOP_Y) / CELL_SIDE_PIXELS;
                    game.flipCell(jj, ii);

                    repaint();
                }
            }

            public void mouseClicked(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }

        }

        myMouseListener listener = new myMouseListener();
        addMouseListener(listener);

        init();
    }

    private void init() {

    }

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        drawCells(g2);
        drawGrid(g2);
        drawButtons();

        if (loopModeOn) {
            try {
                Thread.sleep(STEP_TIME_MILLIS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            game.step();

            repaint();
            
            if (game.gameEnded() ) {
                loopModeOn = false;
                // put in 'close loop mode'
                startButton.setText("Stop");
            }
        }

    }

    private void drawGrid(Graphics2D g2) {
        g2.setColor(COLOR_GRID);

        int x1 = CELL_TOP_X;
        int x2 = CELL_TOP_X + CELL_COLS * CELL_SIDE_PIXELS;
        int y1, y2;

        for (int ii = 0; ii <= CELL_ROWS; ii++) {
            y1 = CELL_TOP_Y + ii * CELL_SIDE_PIXELS;
            g2.drawLine(x1, y1, x2, y1);
        }

        y1 = CELL_TOP_Y;
        y2 = CELL_TOP_Y + CELL_ROWS * CELL_SIDE_PIXELS;
        for (int jj = 0; jj <= CELL_COLS; jj++) {
            x1 = CELL_TOP_X + jj * CELL_SIDE_PIXELS;
            g2.drawLine(x1, y1, x1, y2);
        }

    }

    private void drawCells(Graphics2D g2) {

        int fontSize = 8;
        g2.setFont(new Font("TimesRoman", Font.PLAIN, fontSize));

        for (int ii = 0; ii < CELL_ROWS; ii++) {
            int ytop = CELL_TOP_Y + ii * CELL_SIDE_PIXELS;

            for (int jj = 0; jj < CELL_COLS; jj++) {

                int xleft = CELL_TOP_X + jj * CELL_SIDE_PIXELS;
                int val = game.getCell(ii, jj);
                Color c = COLOR_NONE;
                if (val == -1) {
                    c = COLOR_DEAD;
                }

                if (val > 0) {
                    c = COLOR_ALIVE;
                }
                if (ii==game.getStartRow()  && jj==game.getStartCol())
                    c = COLOR_START;
                if (ii==game.getEndRow()  && jj==game.getEndCol())
                    c = COLOR_END;
                g2.setColor(c);
                g2.fillRect(xleft, ytop, CELL_SIDE_PIXELS, CELL_SIDE_PIXELS);
                if (val > 0) {
                    g2.setColor(COLOR_FONT_CELL);
                    int dx = Math.round( (val<10) ? CELL_SIDE_PIXELS/4 : 0 ) ;
                    g2.drawString("" + val, xleft+dx, ytop+CELL_SIDE_PIXELS);
                }

            }
        }    
    }

    private void drawButtons() {
        stepLabel.setText(Integer.toString(game.getStepCounter()));
    }

    private void putButtons() {

        startButton.setText("Start");
        startButton.setBounds(START_RECT);
        class StartListener implements ActionListener {

            public void actionPerformed(ActionEvent e) {
                if (!game.gameEnded() ) {
                    loopModeOn = !loopModeOn;
                    String str = (loopModeOn) ? "Stop" : "Start";
                    startButton.setText(str);
                    repaint();
                }
            }
        }
        startButton.addActionListener(new StartListener());
        startButton.setVisible(true);
        add(startButton);

        nextButton.setText("Next");
        nextButton.setBounds(NEXT_RECT);
        class NextListener implements ActionListener {

            public void actionPerformed(ActionEvent e) {
                if (!game.gameEnded() )
                    game.step();
                repaint();
            }
        }
        nextButton.addActionListener(new NextListener());
        nextButton.setVisible(true);
        add(nextButton);

        stepLabel.setText(Integer.toString(game.getStepCounter()));
        stepLabel.setBounds(STEP_RECT);
        stepLabel.setVisible(true);
        add(stepLabel);

        modelChooser.setBounds(MODEL_RECT);

       
        n8Button.setText("N8");
        n8Button.setBounds(N8_RECT);
        class N8Listener implements ActionListener {

            public void actionPerformed(ActionEvent e) {
                
                // hang to toggle!!
                if (!game.gameEnded() ) {
                    game.setN8( !game.getN8()) ;
                    String str = (game.getN8()) ? "N8" : "N4";
                    n8Button.setText(str);
                    repaint();
                }
            }
        }
        n8Button.addActionListener(new N8Listener());
        n8Button.setVisible(true);
        add(n8Button);
        
        
        
        
        
        // get selected item
        class ComboListener implements ActionListener {

            public void actionPerformed(ActionEvent e) {
                String str = (String) modelChooser.getSelectedItem();
                game.setPattern(str);
                repaint();
            }
        }

        modelChooser.addActionListener(new ComboListener());
        modelChooser.setVisible(true);
        add(modelChooser);
    }

}
