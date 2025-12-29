import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.MouseEvent;

public class MouseTrack {
    private static Robot robot;
    private static double sensitivity;
    private static Display display;

    private static int centerX, centerY;

    public MouseTrack(int cX, int cY, double s) {
        Point windowLocation = display.getLocationOnScreen();
        centerX = cX + windowLocation.x;
        centerY = cY + windowLocation.y;
        sensitivity = s;

        try {
            
            robot = new Robot();
            robot.mouseMove(centerX, centerY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static double[] update() {

        Point mousePos = MouseInfo.getPointerInfo().getLocation();

        int dX = mousePos.x - centerX;
        int dY = mousePos.y - centerY;

        
        if (dX != 0 || dY != 0) {

            //System.out.println("movement:" + dX + " " + dY);
            
            
            if (robot != null) {
                robot.mouseMove(centerX, centerY);
                //System.out.println("hello");
            }
            return new double[] {dX * sensitivity, dY * sensitivity};
        }
    
        

        return new double[] {0, 0};

    }

    public static void setDisplay(Display d) {
        display = d;
    }

    public static void setCenter(int cX, int cY) {
        centerX = cX;
        centerY = cY;
        robot.mouseMove(centerX, centerY);
    }

    public static void setSensitivity(double s) {
        sensitivity = s;
    }

}
