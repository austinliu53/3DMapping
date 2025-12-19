import java.util.ArrayList;

public class DotMap { // This is a class that converts 3D points to 2D points based on camera position and orientation. 
    // Each DotMap object has a x, y position of what the point would look like in 3d.
    public static Display display ;
    public static ArrayList<double[]> screenMap = new ArrayList<double[]>(); // Contains all the 2d dots
    public static ArrayList<Vertex> vertexMap = new ArrayList<Vertex>(); // Contains all the 3d voxels
    public static double[] camera;      // The 3d coordinates the camera
    private static double roll;        // Pitch angle radians
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

    public Vertex getVertex() {
        return vertex;
    }

    public double[] getDot() {
        return dot;
    }

    public static void updateCamera(double[] c, double p, double y, double fL) {
        camera = c;

        
        
        yaw = y;
        pitch = p;
        roll = calcPitches()[1];
        pitch = calcPitches()[0];
        
        focalLength = fL;

        screenMap.clear();
        vertexMap.clear();
        for (int i = 0; i < vertexMap.size(); i++) {
            new DotMap(vertexMap.get(i));
        }

        // hey copilot how do i determine if the point is behind me
        // Answer: If the z-coordinate of the point is less than the z-coordinate of the camera, then the point is behind the camera.
        // what if the camera is rotated
        // Answer: You need to take into account the camera's rotation when determining if a point is behind the camera. 
        // You can do this by transforming the point's coordinates into the camera's coordinate system using the inverse of the camera's rotation matrix. 
        // Once you have the point in the camera's coordinate system, you can check if its z-coordinate is less than zero to determine if it is behind the camera.

    }

    public static double[] calcPitches() {
        double[] pitches = new double[2];
        
        pitches[0] = Math.cos(yaw) * pitch; // pitch
        pitches[1] = -Math.sin(yaw) * pitch; // roll
        //System.out.println("DotMap 57: pitch: " + pitch);

       //System.out.println("DotMap 59: Pitch: " + pitches[0] + " Roll: " + pitches[1]);

        return pitches;
    }

    // point is a 3-item array representing the x, y, z coordinates of a point in 3D space
    // camera is also the same format
    // Pitch is the up and down angle of the camera
    // Yaw is the left and right angle of the camera

    public double[] voxelToDot() { // Point on the virtual 3D grid
        double[] dot = new double[2]; // This is the coordinate on the screen
        
        double[] pitchPoint = pitch();
        double[] rollPoint = roll(pitchPoint);
        double[] yawPoint = yaw(rollPoint);

        dot[0] = projectX(yawPoint);
        dot[1] = projectY(yawPoint);

        // if the voxel is behind the rotated camera
        System.out.println("DotMap 61: Voxel Y: " + voxel[1] + " Camera Y: " + camera[1]);
        if ((voxel[1] - camera[1]) > 0) {
            dot[0] = Double.NaN;
            dot[1] = Double.NaN;
        }
        //System.out.println("DotMap 39 " + dot[0] + " " + dot[1]);
        return dot;
    } 

    public double[] pitch() {
        double[] rotatedPoint = new double[3];
        
        rotatedPoint[0] = voxel[0];
        rotatedPoint[1] = Math.cos(pitch) * (voxel[1] - camera[1]) - Math.sin(pitch) * (voxel[2] - camera[2]) + camera[1]; // Some math stuff that rotates the voxel around the camera using circle functions
        rotatedPoint[2] = Math.sin(pitch) * (voxel[1] - camera[1]) + Math.cos(pitch) * (voxel[2] - camera[2]) + camera[2];
        return rotatedPoint;
    }

    public double[] roll(double[] vox) {
        double[] rotatedPoint = new double[3];
        
        rotatedPoint[0] = Math.cos(roll) * (vox[0] - camera[0]) + Math.sin(roll) * (vox[1] - camera[1]) + camera[0]; 
        rotatedPoint[1] = -Math.sin(roll) * (vox[0] - camera[0]) + Math.cos(roll) * (vox[1] - camera[1]) + camera[1];
        rotatedPoint[2] = vox[2];

        return rotatedPoint;
        
    }

    public double[] yaw(double[] vox) {
        double[] rotatedPoint = new double[3];

        rotatedPoint[0] = Math.cos(yaw) * (vox[0] - camera[0]) + Math.sin(yaw) * (vox[2] - camera[2]) + camera[0]; 
        rotatedPoint[1] = vox[1];
        rotatedPoint[2] = -Math.sin(yaw) * (vox[0] - camera[0]) + Math.cos(yaw) * (vox[2] - camera[2]) + camera[2];
        return rotatedPoint;
    }

    public double projectX(double[] y_p) {
        return focalLength * (y_p[0] - camera[0]) / (y_p[2] - camera[2]);
    }

    public double projectY(double[] y_p) {
        return focalLength * (y_p[1] - camera[1]) / (y_p[2] - camera[2]);
    }
}
