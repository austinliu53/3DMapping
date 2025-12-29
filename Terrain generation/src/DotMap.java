import java.util.ArrayList;

public class DotMap { // This is a class that converts 3D points to 2D points based on camera position and orientation. 
    // Each DotMap object has a x, y position of what the point would look like in 3d.
    public static Display display ;
    public static ArrayList<double[]> screenMap = new ArrayList<double[]>(); // Contains all the 2d dots
    public static ArrayList<Vertex> vertexMap = new ArrayList<Vertex>(); // Contains all the 3d voxels
    public static double[] camera;      // The 3d coordinates the camera
    private static double pitch;        // Pitch angle radians
    private static double yaw;          // Yaw angle radians
    private static double focalLength;  // Focal length

    private Vertex vertex;           // The vertex associated with this DotMap. Is a voxel but contains the shape 
    private double[] voxel;             // Voxel (3D) position
    private double[] dot;               // 2D point position

    public DotMap(Vertex v) {           // Voxel, pitch, yaw

        vertex = v;
        voxel = v.coords;
        dot = voxelToDot(); // Calculates the screen position based on the voxel and camera parameters

        screenMap.add(dot); // Adds the calculated dot to the screenMap
        vertexMap.add(v);   //

    }

    

    public static void updateCamera(double[] c, double p, double y, double fL) {
        camera = c.clone();
        
        yaw = y;
        pitch = p;
        
        focalLength = fL;

        ArrayList<Vertex> tempVertices = new ArrayList<>(vertexMap);

        screenMap.clear();
        vertexMap.clear();
        for (Vertex v: tempVertices) {
            
            new DotMap(v);

        }

       
    }

    // point is a 3-item array representing the x, y, z coordinates of a point in 3D space
    // camera is also the same format
    // Pitch is the up and down angle of the camera
    // Yaw is the left and right angle of the camera

    public double[] voxelToDot() { // Point on the virtual 3D grid
        double[] dot = new double[2]; // This is the coordinate on the screen, to be returned
        
        double[] yawPoint = yaw();
        double[] pitchPoint = pitch(yawPoint);

        
        //System.out.println("============================================================");
        //System.out.println("DotMap 82: Pitchedpoint x:" + pitchPoint[0]);
        //System.out.println("DotMap 82: Pitchedpoint y:" + pitchPoint[1]);
        //System.out.println("DotMap 81: Pitchedpoint z:" + pitchPoint[2]);
        
        // if the voxel is behind the rotated camera

        if (pitchPoint[2] + Math.pow(pitchPoint[0], 2) + Math.pow(pitchPoint[0], 2) <= 8) {// Z-distance from the camera
            return new double[] {Double.NaN, Double.NaN};
        } 

        if (pitchPoint[2] + Math.pow(pitchPoint[0], 2) + Math.pow(pitchPoint[0], 2) <= 8) {// Z-distance from the camera
            //return new double[] {Double.NaN, Double.NaN};
        } 
        
        dot[0] = display.screenWidth / 2.0 + projectX(pitchPoint); // Convert it to the screen's coordinates
        dot[1] = display.screenHeight / 2.0 + projectY(pitchPoint); 
        

        

        //System.out.println("DotMap 61: Voxel Y: " + voxel[1] + " Camera Y: " + camera[1]);
        //System.out.println("DotMap 39 " + dot[0] + " " + dot[1]);
        return dot;
    } 

    public double[] yaw() {
        double[] rotatedPoint = new double[3];

        rotatedPoint[0] = Math.cos(yaw) * (voxel[0] - camera[0]) + Math.sin(yaw) * (voxel[2] - camera[2]); 
        rotatedPoint[1] = voxel[1];
        rotatedPoint[2] = -Math.sin(yaw) * (voxel[0] - camera[0]) + Math.cos(yaw) * (voxel[2] - camera[2]);
        return rotatedPoint;
    }

    public double[] pitch(double[] vox) {
        double[] rotatedPoint = new double[3];
        
        rotatedPoint[0] = vox[0];
        rotatedPoint[1] = Math.cos(pitch) * (vox[1]) - Math.sin(pitch) * (vox[2]); // Some math stuff that rotates the voxel around the camera using circle functions
        rotatedPoint[2] = Math.sin(pitch) * (vox[1]) + Math.cos(pitch) * (vox[2]);
        return rotatedPoint;
    }

    

    public double projectX(double[] y_p) {
        return focalLength * (y_p[0]) / (y_p[2]);
    }

    public double projectY(double[] y_p) {
        return focalLength * (y_p[1]) / (y_p[2]);
    }

    public Vertex getVertex() {
        return vertex;
    }

    public double[] getDot() {
        return dot;
    }

    public static void setDisplay(Display d) {
        display = d;
        
    }
}
