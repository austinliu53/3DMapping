import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Objects;
import javax.swing.*;


public class Display extends JFrame implements KeyListener, ActionListener {

    
    public final int FPS = 60;

    public boolean upPressed, downPressed, leftPressed, rightPressed, wPressed, aPressed, sPressed, dPressed, spacePressed, shiftPresse, escPressed = false; 

    // Game physics

    public double[] camera = {0, 0, -7};
    public double pitch = 0;
    
    public double yaw = 0; // Facing along positive Z axis
    public double focalLength = 500;
    public double xVelocity, zVelocity = 0;

    public double moveFriction = 0.7;
    public double gravity = 0.01;


    // Counting all the polygons that will be rendered.
    public ArrayList<RectPrism> rectPrisms = new ArrayList<RectPrism>();


    // Menus

    public JLayeredPane systemPane = new JLayeredPane();

    public JButton resumeButton = new JButton();
    public JButton startButton = new JButton();


    // Game loop 
    public Timer gameTimer;
    public boolean mouseCaptured;
    public String status = "menu";

    // Mouse tracking
    public MouseTrack mouseTrack;

    
    private final Toolkit toolkit = Toolkit.getDefaultToolkit();
    private final Dimension screenSize = toolkit.getScreenSize();
    public final int screenWidth = (int)screenSize.getWidth();
    public final int screenHeight = (int)screenSize.getHeight();
    private RectPrism rect1;

    public Display() {

        
        // Add listener for the key escape
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    if (status.equals("running")) {
                        status = "escape";
                        toggleMouseCapture();
                    } else if (status.equals("escape")) {
                        status = "running";
                        toggleMouseCapture();
                    }
                    
                }
            }
        });

        
        DotMap.setDisplay(this);
        DotMap.updateCamera(camera, pitch, yaw, focalLength);
        
        this.setTitle("3D Display");
        this.setSize(screenWidth, screenHeight);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);

        // Make a layeredpane

        systemPane.setBounds(0, 0, screenWidth, screenHeight);
        this.add(systemPane);

        // Make them polygons

        rect1 = new RectPrism(4, 4, 4, new double[]{-2, -2, 5}, 0);
        addRectPrism(rect1);

        // Set up key bindings

        setupKeyBindings();

        // Adding menu buttons
        startButton.setText("Start");
        startButton.setBounds(0, 0, 500, 100);
        startButton.setVisible(false);
        startButton.addActionListener(this);

        resumeButton.setText("Resume");
        resumeButton.setBounds(0, 200, 500, 100);
        resumeButton.setVisible(false);
        resumeButton.addActionListener(this);

        systemPane.add(startButton, JLayeredPane.POPUP_LAYER);
        systemPane.add(resumeButton, JLayeredPane.POPUP_LAYER);

        

        // other stuff

        this.setFocusable(true);
        this.setVisible(true);

        
        startGameTimer();
        

    }

    private void startGameTimer() {
        gameTimer = new Timer(1000 / FPS, e -> {

            // Game loop

            if (status.equals("running")) {
                
                updateCamera(MouseTrack.update());
                System.out.println("Display 137");
                repaint();
            } else if (status.equals("menu")) {
                startButton.setVisible(true);
                // show start menu

            }
            
        });
        
        // Verify timer properties
        gameTimer.setInitialDelay(0);
        // Combine multiple rapid events
        gameTimer.start();
        
        //System.out.println("Timer delay: " + gameTimer.getDelay());
        //System.out.println("Timer is running: " + gameTimer.isRunning());
    }
    
    private void updateCamera(double[] direction) {


        yaw += -direction[0] / FPS;
        pitch += direction[1] / FPS;
        
        yaw %= 2 * Math.PI;
        pitch = Math.max(-Math.PI / 2, Math.min(Math.PI / 2, pitch)); // Limit pitch to -90 to +90 degrees

        xVelocity += ((sPressed ? 0.1 : 0) - (wPressed ? 0.1 : 0)) * Math.sin(yaw) + ((dPressed ? 0.1 : 0) - (aPressed ? 0.1 : 0)) * Math.cos(yaw);
        zVelocity += -((sPressed ? 0.1 : 0) - (wPressed ? 0.1 : 0)) * Math.cos(yaw) + ((dPressed ? 0.1 : 0) - (aPressed ? 0.1 : 0)) * Math.sin(yaw);
        camera[0] += xVelocity * 30 / FPS;
        camera[2] += zVelocity * 30 / FPS; 

        xVelocity *= moveFriction;
        zVelocity *= moveFriction;
        //System.out.println("Display80: Pitch: " + pitch + " Yaw: " + yaw + " Roll: " + DotMap.calcPitches()[1]);
        
        DotMap.updateCamera(camera, pitch, yaw, focalLength);

        //System.out.println("Display 76 " + rect1.screenMap.get(0).voxelToDot()[0] + " " + rect1.screenMap.get(0).voxelToDot()[1]);
        //System.out.println("Display 77: yaw:" + yaw + " pitch:" + pitch);
    }



    public void addRectPrism(RectPrism rp) {
        rectPrisms.add(rp);
    }

    public void drawRect(Graphics g, RectPrism rp) {

        g.setColor(Color.WHITE);


        /*
        for (DotMap dm : rp.screenDotMap) { // Drawing them onto the screen
            double[] dot = dm.voxelToDot();
            
            g.fillOval((int) dot[0], (int) dot[1], 5, 5);

            //System.out.println("Display 193 " + (int) (dot[0] + screenWidth/4) + " " + (int) (dot[1] + screenHeight/4));
        }
        */
       
        System.out.println("Display 194" + rp.edges.size());
        for (int[] edge : rp.edges) { // Drawing them onto the screen

            double[] dot1 = rp.screenDotMap.get(edge[0]).voxelToDot();
            double[] dot2 = rp.screenDotMap.get(edge[1]).voxelToDot();
            g.drawLine((int)dot1[0], (int)dot1[1], (int)dot2[0], (int)dot2[1]);
        }

    }

    public void drawCrossHair(Graphics g) {
        g.setColor(Color.WHITE);


        g.drawRoundRect(screenWidth/2 - 10, screenHeight/2 - 1, 20, 2, 2, 2);

        g.drawRoundRect(screenWidth/2 - 1, screenHeight/2 - 10, 2, 20, 2, 2);
        //g.drawLine(screenWidth/2 - 10, screenHeight/2, screenWidth/2 + 10, screenHeight/2);

        //g.drawLine(screenWidth/2, screenHeight/2 - 10, screenWidth/2, screenHeight/2 + 10);

    }


    public void toggleMouseCapture() {
        
        if (mouseCaptured) { // If it is captured, release it
            releaseMouse();

            resumeButton.setVisible(true);
        } else {
            captureMouse();
            
            resumeButton.setVisible(false);
        }

        mouseCaptured = !mouseCaptured;
    }
    
    public void captureMouse() { // Hide the mouse
        // Hide cursor
        setCursor(getToolkit().createCustomCursor(
            new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
            new Point(0, 0),
            "null"
        ));
        
        // Reset mouse to center
        MouseTrack.setCenter(screenWidth/2, screenHeight/2);
        
        // Request focus for key events
        requestFocus();
    }
    
    public void releaseMouse() {
        // Show default cursor
        setCursor(Cursor.getDefaultCursor());
    }

    @Override
    public void paint(Graphics g) {
        //System.out.println("Display 250");
        // Don't call super.paint(g) first - it causes flicker
        // Instead, do double buffering manually
        
        // Create offscreen buffer
        Image offscreen = createImage(screenWidth, screenHeight);
        Graphics bufferGraphics = offscreen.getGraphics();
        
        // Clear background
        bufferGraphics.setColor(Color.BLACK);
        bufferGraphics.fillRect(0, 0, screenWidth, screenHeight);
        
        // Draw to buffer
        for (RectPrism rp : rectPrisms) {
            drawRect(bufferGraphics, rp);
        }

        drawCrossHair(bufferGraphics);


        
        // Draw debug info
        //bufferGraphics.setColor(Color.WHITE);
        //bufferGraphics.drawString("Yaw: " + (int)yaw + "°, Pitch: " + (int)pitch + "°", 10, 20);
        
        // Copy buffer to screen
        g.drawImage(offscreen, 0, 0, this);
        
        // Clean up
        bufferGraphics.dispose();
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        if (mouseTrack != null) {
            MouseTrack.setCenter(x + width / 2, y + height / 2);
        }
    }

    private void setupKeyBindings() {
        JPanel contentPane = (JPanel) this.getContentPane();

        //Pressing
        //Arrow keys
        contentPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), "upPressed");
        contentPane.getActionMap().put("upPressed", new AbstractAction() {@Override public void actionPerformed(ActionEvent e) {upPressed = true;}});

        contentPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"), "downPressed");
        contentPane.getActionMap().put("downPressed", new AbstractAction() {@Override public void actionPerformed(ActionEvent e) {downPressed = true;}});

        contentPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"), "leftPressed");
        contentPane.getActionMap().put("leftPressed", new AbstractAction() {@Override public void actionPerformed(ActionEvent e) {leftPressed = true;}});

        contentPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"), "rightPressed");
        contentPane.getActionMap().put("rightPressed", new AbstractAction() {@Override public void actionPerformed(ActionEvent e) {rightPressed = true;}});

        //WASD
        contentPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("W"), "wPressed");
        contentPane.getActionMap().put("wPressed", new AbstractAction() {@Override public void actionPerformed(ActionEvent e) {wPressed = true;}});

        contentPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("A"), "aPressed");
        contentPane.getActionMap().put("aPressed", new AbstractAction() {@Override public void actionPerformed(ActionEvent e) {aPressed = true;}});

        contentPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("S"), "sPressed");
        contentPane.getActionMap().put("sPressed", new AbstractAction() {@Override public void actionPerformed(ActionEvent e) {sPressed = true;}});

        contentPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("D"), "dPressed");
        contentPane.getActionMap().put("dPressed", new AbstractAction() {@Override public void actionPerformed(ActionEvent e) {dPressed = true;}});

        //ESCAPE
        contentPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESC"), "escPressed");
        contentPane.getActionMap().put("escPressed", new AbstractAction() {@Override public void actionPerformed(ActionEvent e) {escPressed = true;}});


        // Releasing keys
        contentPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released UP"), "upReleased");
        contentPane.getActionMap().put("upReleased", new AbstractAction() {@Override public void actionPerformed(ActionEvent e) {upPressed = false;}});
        
        contentPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released DOWN"), "downReleased");
        contentPane.getActionMap().put("downReleased", new AbstractAction() {@Override public void actionPerformed(ActionEvent e) {downPressed = false;}});
        
        contentPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released LEFT"), "leftReleased");
        contentPane.getActionMap().put("leftReleased", new AbstractAction() {@Override public void actionPerformed(ActionEvent e) {leftPressed = false;}});
        
        contentPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released RIGHT"), "rightReleased");
        contentPane.getActionMap().put("rightReleased", new AbstractAction() {@Override public void actionPerformed(ActionEvent e) {rightPressed = false;}});

        contentPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released W"), "wReleased");
        contentPane.getActionMap().put("wReleased", new AbstractAction() {@Override public void actionPerformed(ActionEvent e) {wPressed = false;}});

        contentPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released A"), "aReleased");
        contentPane.getActionMap().put("aReleased", new AbstractAction() {@Override public void actionPerformed(ActionEvent e) {aPressed = false;}});

        contentPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released S"), "sReleased");
        contentPane.getActionMap().put("sReleased", new AbstractAction() {@Override public void actionPerformed(ActionEvent e) {sPressed = false;}});

        contentPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released D"), "dReleased");
        contentPane.getActionMap().put("dReleased", new AbstractAction() {@Override public void actionPerformed(ActionEvent e) {dPressed = false;}});

    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startButton) {
            status = "running";
            startButton.setVisible(false); // Hide the button

            // Setting up mouse tracker
            
            MouseTrack.setDisplay(this);
            mouseTrack = new MouseTrack(screenWidth/2, screenHeight/2, 0.1);
            toggleMouseCapture();
            
        } else if (e.getSource() == resumeButton) {
            status = "running";
            resumeButton.setVisible(false);
            toggleMouseCapture();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}
