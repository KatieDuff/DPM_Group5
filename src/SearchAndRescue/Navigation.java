package SearchAndRescue;


public class Navigation {
	// Motor Constants
	final static int FORWARD_SPEED = 5, MOTOR_SPEED = 15, ACCELERATION = 3000;

	// Error Constants
	final static double DEG_ERR = 3.0, POS_ERR = 1.00;

	private final Odometer odometer;
	private final TwoWheeledRobot robot;

	// Reseting motors

	public Navigation(Odometer odo) {
		this.odometer = odo;
		this.robot = odo.getTwoWheeledRobot();
		// Reseting motors
		this.robot.leftMotor.stop();
		this.robot.rightMotor.stop();
		this.robot.leftMotor.setAcceleration(ACCELERATION);
		this.robot.rightMotor.setAcceleration(ACCELERATION);

	}

	public void travelTo(double x, double y) {
		double minAng;
		//Wile the error in the position is greater than the error threshold keep travelling 
		while (Math.abs(x - this.odometer.getX()) > POS_ERR
				|| Math.abs(y - this.odometer.getY()) > POS_ERR) {
			minAng = (Math.atan2(x - this.odometer.getX(), y - odometer.getY()))
					* (180.0 / Math.PI);
			minAng = minAngle(minAng);
			this.turnTo(minAng);
			robot.setForwardSpeed(FORWARD_SPEED);
		}
		robot.setForwardSpeed(0);
		robot.setRotationSpeed(0);

	}

	public void turnTo(double angle) {
		double error = fixDegAngle(angle - this.odometer.getTheta());
		while (Math.abs(error) > DEG_ERR) {
			robot.setForwardSpeed(0);
			error = fixDegAngle(angle - this.odometer.getTheta());
			if (error < -180) {
				robot.setRotationSpeed(MOTOR_SPEED);
			} else if (error < 0) {
				robot.setRotationSpeed(-MOTOR_SPEED);
			} else if (error > 180) {
				robot.setRotationSpeed(-MOTOR_SPEED);
			} else if (error > 0) {
				robot.setRotationSpeed(MOTOR_SPEED);
			}
		}
		robot.setRotationSpeed(0);
	}

	//Determines the minimum turning angle
	public double minAngle(double angle) {
		if (angle < -180) {
			return 360 + angle;
		} else if (angle > 180) {
			return angle - 360;
		} else
			return angle;
	}

	//Executes wrap around on angles < 0 and > 360
	public static double fixDegAngle(double angle) {
		if (angle < 0) {
			return (360 + angle % 360);
		} else {
			return angle % 360;
		}
	}
}
