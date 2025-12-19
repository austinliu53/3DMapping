import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
//import java.util.Objects;
import javax.swing.*;


public class Display extends JFrame implements KeyListener, ActionListener {

    public double[] camera = {0, 0, -7};
    public double pitch = 0;
    
    public double yaw = 0; // Facing along positive Z axis
    public double focalLength = 200;
    public final int FPS = 60;

    public boolean upPressed, downPressed, leftPressed, rightPressed, wPressed, aPressed, sPressed, dPressed, spacePressed, shiftPressed = false; 
    
    public double xVelocity, zVelocity = 0;
    public double pitchVelocity, yawVelocity = 0;
    public double angleFriction = 0.9;
    public double moveFriction = 0.7;
    public double gravity = 0.01;

    public ArrayList<RectPrism> rectPrisms = new ArrayList<RectPrism>();

    public Timer gameTimer;

    
    private final Toolkit toolkit = Toolkit.getDefaultToolkit();
    private final Dimension screenSize = toolkit.getScreenSize();
    public final double screenWidth = screenSize.getWidth();
    public final double screenHeight = screenSize.getHeight();
    private RectPrism rect1;

    public Display() {

        DotMap.updateCamera(camera, pitch, yaw, focalLength);
        this.setTitle("3D Display");
        this.setSize((int) screenWidth, (int) screenHeight);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        rect1 = new RectPrism(4, 4, 4, new double[]{-2, -2, 5}, 0);
        addRectPrism(rect1);

        setupKeyBindings();

        this.setFocusable(true);
        this.setVisible(true);

        startGameTimer();

        gameTimer.start();

    }

    private void startGameTimer() {
        gameTimer = new Timer(1000 / FPS, e -> {
            updateCamera();
            repaint();
        });
        
        gameTimer.start();
        
        // Verify timer properties
        gameTimer.setInitialDelay(0);
        //gameTimer.setCoalesce(true);  // Combine multiple rapid events
        gameTimer.start();
        
        //System.out.println("Timer delay: " + gameTimer.getDelay());
        //System.out.println("Timer is running: " + gameTimer.isRunning());
    }
    
    private void updateCamera() {


        yawVelocity += (1 * Math.PI * ((leftPressed ? 1 : 0) - (rightPressed ? 1 : 0))) / 360;
        pitchVelocity += (1 * Math.PI * ((downPressed ? 1 : 0) - (upPressed ? 1 : 0))) / 360;

        yaw += yawVelocity * 30 / FPS; // left right
        pitch += pitchVelocity * 30 / FPS; // up down

        yawVelocity *= angleFriction;
        pitchVelocity *= angleFriction;
        
        pitch = Math.max(-Math.PI / 2, Math.min(Math.PI / 2, pitch)); // Limit pitch to -90 to +90 degrees

        xVelocity += ((sPressed ? 0.1 : 0) - (wPressed ? 0.1 : 0)) * Math.sin(yaw) + ((dPressed ? 0.1 : 0) - (aPressed ? 0.1 : 0)) * Math.cos(yaw);
        zVelocity += ((wPressed ? 0.1 : 0) - (sPressed ? 0.1 : 0)) * Math.cos(yaw) + ((dPressed ? 0.1 : 0) - (aPressed ? 0.1 : 0)) * Math.sin(yaw);
        camera[0] += xVelocity * 30 / FPS;
        camera[2] += zVelocity * 30 / FPS; 

        xVelocity *= moveFriction;
        zVelocity *= moveFriction;
        //System.out.println("Display80: Pitch: " + pitch + " Yaw: " + yaw + " Roll: " + DotMap.calcPitches()[1]);
        
        DotMap.updateCamera(camera, pitch, yaw, focalLength);

        //System.out.println("Display 76 " + rect1.screenMap.get(0).voxelToDot()[0] + " " + rect1.screenMap.get(0).voxelToDot()[1]);
        System.out.println("Display 77: yaw:" + yaw + " pitch:" + pitch);
    }

    public void addRectPrism(RectPrism rp) {
        rectPrisms.add(rp);
    }

    public void drawPoints(Graphics g, RectPrism rp) {

        g.setColor(Color.WHITE);

        for (DotMap dm : rp.screenMap) {
            double[] dot = dm.voxelToDot();
            
            g.fillOval((int) (dot[0] + screenWidth/2), (int) (dot[1] + screenHeight/2), 5, 5);

            //System.out.println("Display 95 " + (int) (dot[0] + screenWidth/4) + " " + (int) (dot[1] + screenHeight/4));
        }

    }


    // public void paint(Graphics g) {
    //     super.paint(g);  // CRITICAL: clears background and does setup
    //     g.setColor(Color.BLACK);
    //     g.fillRect(0, 0, getWidth(), getHeight());
        
    //     // Draw all prisms
    //     for (RectPrism rp : rectPrisms) {
    //         drawPoints(g, rp);
    //     }
        
    //     // Draw debug info
    //     g.setColor(Color.WHITE);
    //     g.drawString("Yaw: " + (int)yaw + "°, Pitch: " + (int)pitch + "°", 10, 20);
    //     g.drawString("Use Arrow Keys to rotate", 10, 40);
    //     g.drawString("Prisms: " + rectPrisms.size(), 10, 60);
    // }

    @Override
    public void paint(Graphics g) {
    // Don't call super.paint(g) first - it causes flicker
    // Instead, do double buffering manually
    
    // Create offscreen buffer
    Image offscreen = createImage(getWidth(), getHeight());
    Graphics bufferGraphics = offscreen.getGraphics();
    
    // Clear background
    bufferGraphics.setColor(Color.BLACK);
    bufferGraphics.fillRect(0, 0, getWidth(), getHeight());
    
    // Draw to buffer
    for (RectPrism rp : rectPrisms) {
        drawPoints(bufferGraphics, rp);
    }
    
    // Draw debug info
    bufferGraphics.setColor(Color.WHITE);
    bufferGraphics.drawString("Yaw: " + (int)yaw + "°, Pitch: " + (int)pitch + "°", 10, 20);
    
    // Copy buffer to screen
    g.drawImage(offscreen, 0, 0, this);
    
    // Clean up
    bufferGraphics.dispose();
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
    public void actionPerformed(ActionEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}
