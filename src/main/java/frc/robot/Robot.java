// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.


/*
 * Welcome to the Everybot team's starter code for the KitBot.
 *
 * We decided to create a time-based version of the KitBot code that uses the CAN bus
 * to mimic our code from past years for teams who are used to our style and
 * time-based programming.
 */

package frc.robot;

import java.util.ArrayList;
import java.util.List;

// Imports that allow the usage of REV Spark Max motor controllers
import com.revrobotics.CANSparkBase;
import com.revrobotics.CANSparkBase.IdleMode;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class Robot extends TimedRobot {
  /*
   * Autonomous selection options.
   */
  private static final String kNothingAuto = "do nothing";
  private static final String kLaunchAndDrive = "launch drive";
  private static final String kLaunch = "launch";
  private static final String kDrive = "drive";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  /*
   * Drive motor controller instances.
   *
   * Change the id's to match your robot.
   * Change kBrushed to kBrushless if you are uisng NEOs.
   * The rookie kit comes with CIMs which are brushed motors.
   * Use the appropriate other class if you are using different controllers.
   */

  CANSparkBase leftRear = new CANSparkMax(1, MotorType.kBrushless);
  CANSparkBase leftFront = new CANSparkMax(2, MotorType.kBrushless);
  CANSparkBase rightRear = new CANSparkMax(3, MotorType.kBrushless);
  CANSparkBase rightFront = new CANSparkMax(4, MotorType.kBrushless);

  /*
   * A class provided to control your drivetrain. Different drive styles can be passed to differential drive:
   * https://github.com/wpilibsuite/allwpilib/blob/main/wpilibj/src/main/java/edu/wpi/first/wpilibj/drive/DifferentialDrive.java
   */
  DifferentialDrive m_drivetrain;

  /*
   * Mechanism motor controller instances.
   *
   * Like the drive motors, set the CAN id's to match your robot or use different
   * motor controller classses (VictorSPX) to match your robot as necessary.
   *
   * Both of the motors used on the KitBot mechansim are CIMs which are brushed motors
   */
  CANSparkBase m_launchWheel = new CANSparkMax(6, MotorType.kBrushless);
  CANSparkBase m_feedWheel = new CANSparkMax(5, MotorType.kBrushless);

    /**
   * The starter code uses the most generic joystick class.
   *
   * To determine which button on your controller corresponds to which number, open the FRC
   * driver station, go to the USB tab, plug in a controller and see which button lights up
   * when pressed down
   *
   * Buttons index from 0
   */
  Joystick m_driverController = new Joystick(0);

  /*
   * Many teams prefer two students to control their robot, typically one on drive, the other on mechanism
   * Uncomment below and replace m_driverController with m_manipController in the places you want the second
   * controller to have control
  /*

  //Joystick m_manipController = new Joystick(1);



  // --------------- Magic numbers. Use these to adjust settings. ---------------

 /**
   * How many amps can an individual drivetrain motor use.
   */
  static final int DRIVE_CURRENT_LIMIT_A = 20;

  /**
   * How many amps the feeder motor can use.
   */
  static final int FEEDER_CURRENT_LIMIT_A = 20;

  /**
   * Percent output to run the feeder when expelling note
   */
  static final double FEEDER_OUT_SPEED = 1.0;

  /**
   * Percent output to run the feeder when intaking note
   */
  static final double FEEDER_IN_SPEED = -.4;

  /**
   * Percent output for amp or drop note, configure based on polycarb bend
   */
  static final double FEEDER_AMP_SPEED = .4;

  /**
   * How many amps the launcher motor can use.
   *
   * In our testing we favored the CIM over NEO, if using a NEO lower this to 60
   */
  static final int LAUNCHER_CURRENT_LIMIT_A = 20;

  /**
   * Percent output to run the launcher when intaking AND expelling note
   */
  static final double LAUNCHER_SPEED = 1.0;

  /**
   * Percent output for scoring in amp or dropping note, configure based on polycarb bend
   * .14 works well with no bend from our testing
   */
  static final double LAUNCHER_AMP_SPEED = .17;

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */

  static List<Command> commands = new ArrayList<Command>();

  @Override
  public void robotInit() {
    // commands
    
    Command a_forward = new Command("forward", "moves the robot forward", 1, 1, 1.5, 1, 0);
    Command a_nothing = new Command("nothing", "do nothing", 0, 0, 0, 0, 0);

    for(Command c : commands) {
      m_chooser.addOption(c.name, c.name);
    }
    
    m_chooser.setDefaultOption(a_forward.name, a_forward.name);
    SmartDashboard.putData("Auto choices", m_chooser);



    /*
     * Apply the current limit to the drivetrain motors
     */
    leftRear.setSmartCurrentLimit(DRIVE_CURRENT_LIMIT_A);
    leftFront.setSmartCurrentLimit(DRIVE_CURRENT_LIMIT_A);
    rightRear.setSmartCurrentLimit(DRIVE_CURRENT_LIMIT_A);
    rightFront.setSmartCurrentLimit(DRIVE_CURRENT_LIMIT_A);

    /*
     * Tells the rear wheels to follow the same commands as the front wheels
     */
    leftRear.follow(leftFront);
    rightRear.follow(rightFront);

    /*
     * One side of the drivetrain must be inverted, as the motors are facing opposite directions
     */
    leftFront.setInverted(true);
    rightFront.setInverted(false);

    m_drivetrain = new DifferentialDrive(leftFront, rightFront);

    /*
     * Mechanism wheel(s) spinning the wrong direction? Change to true here.
     *
     * Add white tape to wheel to help determine spin direction.
     */
    // m_feedWheel.setInverted(true);
    // m_launchWheel.setInverted(true);

    /*
     * Apply the current limit to the launching mechanism
     */
    m_feedWheel.setSmartCurrentLimit(FEEDER_CURRENT_LIMIT_A);
    m_launchWheel.setSmartCurrentLimit(LAUNCHER_CURRENT_LIMIT_A);
  }

  /**
   * This function is called every 20 ms, no matter the mode. Use this for items like diagnostics
   * that you want ran during disabled, autonomous, teleoperated and test modes.
   */
  @Override
  public void robotPeriodic() {
    SmartDashboard.putNumber("Time (seconds)", Timer.getFPGATimestamp());
  }


  /*
   * Auto constants, change values below in autonomousInit()for different autonomous behaviour
   *
   * A delayed action starts X seconds into the autonomous period
   *
   * A time action will perform an action for X amount of seconds
   *
   * Speeds can be changed as desired and will be set to 0 when
   * performing an auto that does not require the system
   */
  double AUTO_LAUNCH_DELAY_S;
  double AUTO_DRIVE_DELAY_S;

  double AUTO_DRIVE_TIME_S;

  double AUTO_DRIVE_SPEED;
  double AUTO_LAUNCHER_SPEED;

  double autonomousStartTime;

  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();

    leftRear.setIdleMode(IdleMode.kBrake);
    leftFront.setIdleMode(IdleMode.kBrake);
    rightRear.setIdleMode(IdleMode.kBrake);
    rightFront.setIdleMode(IdleMode.kBrake);

    // default values? safe to remove
    // AUTO_LAUNCH_DELAY_S = 2;
    // AUTO_DRIVE_DELAY_S = 3;

    // AUTO_DRIVE_TIME_S = 2.0;
    // AUTO_DRIVE_SPEED = -0.5;
    // AUTO_LAUNCHER_SPEED = 1;
    
    /*
     * Depeding on which auton is selected, speeds for the unwanted subsystems are set to 0
     * if they are not used for the selected auton
     *
     * For kDrive you can also change the kAutoDriveBackDelay
     */
    

    // assigns com to currently selected auto
    Command com = null;
    for(Command c : commands) {
      if(m_autoSelected.equals(c.name)) {
        com = c;
      }
    }

    AUTO_LAUNCH_DELAY_S = com.AUTO_LAUNCH_DELAY_S;
    AUTO_DRIVE_DELAY_S = com.AUTO_DRIVE_DELAY_S;
    AUTO_DRIVE_TIME_S = com.AUTO_DRIVE_TIME_S;
    AUTO_DRIVE_SPEED = com.AUTO_DRIVE_SPEED;
    AUTO_LAUNCHER_SPEED = com.AUTO_LAUNCHER_SPEED;

    autonomousStartTime = Timer.getFPGATimestamp();
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {

    double timeElapsed = Timer.getFPGATimestamp() - autonomousStartTime;

    /*
     * Spins up launcher wheel until time spent in auto is greater than AUTO_LAUNCH_DELAY_S
     *
     * Feeds note to launcher until time is greater than AUTO_DRIVE_DELAY_S
     *
     * Drives until time is greater than AUTO_DRIVE_DELAY_S + AUTO_DRIVE_TIME_S
     *
     * Does not move when time is greater than AUTO_DRIVE_DELAY_S + AUTO_DRIVE_TIME_S
     */
    if(timeElapsed < AUTO_LAUNCH_DELAY_S)
    {
      m_launchWheel.set(AUTO_LAUNCHER_SPEED);
      m_drivetrain.arcadeDrive(0, 0);

    }
    else if(timeElapsed < AUTO_DRIVE_DELAY_S)
    {
      m_feedWheel.set(AUTO_LAUNCHER_SPEED);
      m_drivetrain.arcadeDrive(0, 0);
    }
    else if(timeElapsed < AUTO_DRIVE_DELAY_S + AUTO_DRIVE_TIME_S)
    {
      m_launchWheel.set(0);
      m_feedWheel.set(0);
      m_drivetrain.arcadeDrive(AUTO_DRIVE_SPEED, 0);
    }
    else
    {
      m_drivetrain.arcadeDrive(0, 0);
    }
    /* For an explanation on differintial drive, squaredInputs, arcade drive and tank drive see the bottom of this file */
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {
    /*
     * Motors can be set to idle in brake or coast mode.
     *
     * Brake mode effectively shorts the leads of the motor when not running, making it more
     * difficult to turn when not running.
     *
     * Coast doesn't apply any brake and allows the motor to spin down naturally with the robot's momentum.
     *
     * (touch the leads of a motor together and then spin the shaft with your fingers to feel the difference)
     *
     * This setting is driver preference. Try setting the idle modes below to kBrake to see the difference.
     */
    leftRear.setIdleMode(IdleMode.kCoast);
    leftFront.setIdleMode(IdleMode.kCoast);
    rightRear.setIdleMode(IdleMode.kCoast);
    rightFront.setIdleMode(IdleMode.kCoast);
  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {

    /*
     * Spins up the launcher wheel
     */
    if (m_driverController.getRawButton(1)) {
      m_launchWheel.set(LAUNCHER_SPEED);
    }
    else if(m_driverController.getRawButtonReleased(1))
    {
      m_launchWheel.set(0);
    }

    /*
     * Spins feeder wheel, wait for launch wheel to spin up to full speed for best results
     */
    if (m_driverController.getRawButton(6))
    {
      m_feedWheel.set(FEEDER_OUT_SPEED);
    }
    else if(m_driverController.getRawButtonReleased(6))
    {
      m_feedWheel.set(0);
    }

    /*
     * While the button is being held spin both motors to intake note
     */
    if(m_driverController.getRawButton(5))
    {
      m_launchWheel.set(-LAUNCHER_SPEED);
      m_feedWheel.set(FEEDER_IN_SPEED);
    }
    else if(m_driverController.getRawButtonReleased(5))
    {
      m_launchWheel.set(0);
      m_feedWheel.set(0);
    }

    /*
     * While the amp button is being held, spin both motors to "spit" the note
     * out at a lower speed into the amp
     *
     * (this may take some driver practice to get working reliably)
     */
    if(m_driverController.getRawButton(2))
    {
      m_feedWheel.set(FEEDER_AMP_SPEED);
      m_launchWheel.set(LAUNCHER_AMP_SPEED);
    }
    else if(m_driverController.getRawButtonReleased(2))
    {
      m_feedWheel.set(0);
      m_launchWheel.set(0);
    }
  
    /*
     * Negative signs are here because the values from the analog sticks are backwards
     * from what we want. Pushing the stick forward returns a negative when we want a
     * positive value sent to the wheels.
     *
     * If you want to change the joystick axis used, open the driver station, go to the
     * USB tab, and push the sticks determine their axis numbers
     */
    m_drivetrain.arcadeDrive(-m_driverController.getRawAxis(1), -m_driverController.getRawAxis(4), false);
  }
}

/*
 * The kit of parts drivetrain is known as differential drive, tank drive or skid-steer drive.
 *
 * There are two common ways to control this drivetrain: Arcade and Tank
 *
 * Arcade allows one stick to be pressed forward/backwards to power both sides of the drivetrain to move straight forwards/backwards.
 * A second stick (or the second axis of the same stick) can be pushed left/right to turn the robot in place.
 * When one stick is pushed forward and the other is pushed to the side, the robot will power the drivetrain
 * such that it both moves fowards and turns, turning in an arch.
 *
 * Tank drive allows a single stick to control of a single side of the robot.
 * Push the left stick forward to power the left side of the drive train, causing the robot to spin around to the right.
 * Push the right stick to power the motors on the right side.
 * Push both at equal distances to drive forwards/backwards and use at different speeds to turn in different arcs.
 * Push both sticks in opposite directions to spin in place.
 *
 * arcardeDrive can be replaced with tankDrive like so:
 *
 * m_drivetrain.tankDrive(-m_driverController.getRawAxis(1), -m_driverController.getRawAxis(5))
 *
 * Inputs can be squared which decreases the sensitivity of small drive inputs.
 *
 * It literally just takes (your inputs * your inputs), so a 50% (0.5) input from the controller becomes (0.5 * 0.5) -> 0.25
 *
 * This is an option that can be passed into arcade or tank drive:
 * arcadeDrive(double xSpeed, double zRotation, boolean squareInputs)
 *
 *
 * For more information see:
 * https://docs.wpilib.org/en/stable/docs/software/hardware-apis/motors/wpi-drive-classes.html
 *
 * https://github.com/wpilibsuite/allwpilib/blob/main/wpilibj/src/main/java/edu/wpi/first/wpilibj/drive/DifferentialDrive.java
 *
 */
