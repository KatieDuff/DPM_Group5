import lejos.nxt.NXTRegulatedMotor;

public class TwoWheeledRobot {
	// Need to measure radii with a caliper
	public static final double DEFAULT_LEFT_RADIUS = 2.1;
	public static final double DEFAULT_RIGHT_RADIUS = 2.1;
	public static final double DEFAULT_WIDTH = 14.7;
	private NXTRegulatedMotor leftMotor, rightMotor;
	private double leftRadius, rightRadius, width;
	private double forwardSpeed, rotationSpeed;
	
	public TwoWheeledRobot(NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor, double width, double leftRadius, double rightRadius) {
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.leftRadius = leftRadius;
		this.rightRadius = rightRadius;
		this.width = width;
	}
	
	public TwoWheeledRobot(NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor) {
		this(leftMotor, rightMotor, DEFAULT_WIDTH, DEFAULT_LEFT_RADIUS, DEFAULT_RIGHT_RADIUS);
	}
	
	public TwoWheeledRobot(NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor, double width) {
		this(leftMotor, rightMotor, width, DEFAULT_LEFT_RADIUS, DEFAULT_RIGHT_RADIUS);
	}
	
	// accessors	
	public double getDisplacement() {
		return (leftMotor.getTachoCount() * leftRadius +
				rightMotor.getTachoCount() * rightRadius) *
				Math.PI / 360.0;
	}
	
	public double getHeading() {
		return (leftMotor.getTachoCount() * leftRadius -
				rightMotor.getTachoCount() * rightRadius) / width;
	}
	
	public void getDisplacementAndHeading(double [] data) {
		int leftTacho, rightTacho;
		leftTacho = leftMotor.getTachoCount();
		rightTacho = rightMotor.getTachoCount();
		
		data[0] = (leftTacho * leftRadius + rightTacho * rightRadius) *	Math.PI / 360.0;
		data[1] = (leftTacho * leftRadius - rightTacho * rightRadius) / width;
	}
	
	// mutators
	public void setAcceleration(int accel) {
		leftMotor.setAcceleration(accel); 
		rightMotor.setAcceleration(accel); 
	}
	
	public void setForwardSpeed(double speed) {
		forwardSpeed = speed;
		setSpeeds(forwardSpeed, rotationSpeed);
	}
	
	public void setRotationSpeed(double speed) {
		rotationSpeed = speed;
		setSpeeds(forwardSpeed, rotationSpeed);
	}
	
	public void setSpeeds(double forwardSpeed, double rotationalSpeed) {
		double leftSpeed, rightSpeed;

		this.forwardSpeed = forwardSpeed;
		this.rotationSpeed = rotationalSpeed; 

		leftSpeed = ((forwardSpeed + rotationalSpeed) * width * Math.PI) / (2 * leftRadius * Math.PI);
		rightSpeed = ((forwardSpeed - rotationalSpeed) * width * Math.PI ) / (2 * rightRadius * Math.PI);

		// set motor directions
		if (leftSpeed > 0.0)
			leftMotor.forward();
		else {
			leftMotor.backward();
			leftSpeed = -leftSpeed;
		}
		
		if (rightSpeed > 0.0)
			rightMotor.forward();
		else {
			rightMotor.backward();
			rightSpeed = -rightSpeed;
		}
		
		// set motor speeds
		if (leftSpeed > 200.0)
			leftMotor.setSpeed(200);
		else
			leftMotor.setSpeed((int)leftSpeed);
		
		if (rightSpeed > 200.0)
			rightMotor.setSpeed(200);
		else
			rightMotor.setSpeed((int)rightSpeed);
	}
	
	//Stops the robot
	public void stopRobot(){
		leftMotor.stop();
		rightMotor.stop();
	}
	
	//Moves The robot Forward
	public void moveRobot(double distance){
		rightMotor.rotate(convertDistance(rightRadius, distance), true); 
		leftMotor.rotate(convertDistance(leftRadius, distance), false); 	       
	}
	
	//Rotates the robot
	public void rotateRobot(double angle){
		leftMotor.rotate(convertAngle(leftRadius, width, angle), true);     
		rightMotor.rotate(-convertAngle(rightRadius, width, angle), false); 
	}
	
	private  int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	} 
	   
	private  int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	} 
}