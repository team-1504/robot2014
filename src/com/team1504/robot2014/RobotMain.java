/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package com.team1504.robot2014;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStationLCD;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.can.CANTimeoutException;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SimpleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class RobotMain extends SimpleRobot 
{
    //Drive System
    private static Mecanum mecanum;
    private static CANJaguar front_left_jaguar, back_left_jaguar, back_right_jaguar, front_right_jaguar; 
    private static Joystick driver_left_joystick, driver_right_joystick, operator_joystick;
    private static ToggleButton orbit_reset, turn_lock;
    
    //Shooter
    private static Shooter shooter;
    private static ToggleButton fire_button;
    private static ToggleButton manual_button;
    
    public static Solenoid latch_solenoid_extend;
    public static Solenoid latch_solenoid_retract;
    
    //Pickup mechanism
    private static PickUp pick_up;
    private static CANJaguar pick_up_jaguar;
    
    private static Solenoid pickup_solenoid_extend;
    private static Solenoid pickup_solenoid_retract;
    
    //Photon Cannon
    private static Relay photon_cannon;
    private static ToggleButton photon_cannon_toggle;
    
    //Logging
    private static Logger logger;
    private long start_time;
    
    //Driver Station
    private static DriverStation ds;
    private static DriverStationLCD ds_LCD;
    
    //Misc
    private static HMC5883L_I2C compass;
    private static Compressor compressor;
    
    //Automation
    private static boolean is_automated;
    NetworkTable test_table, inform_table, robot_table;
    
    public RobotMain()
    {
        //Initialization
        try 
        {
            operator_joystick = new Joystick(RobotMap.OPERATOR_JOYSTICK_PORT);
            driver_left_joystick = new Joystick(RobotMap.DRIVER_LEFT_JOYSTICK_PORT);
            driver_right_joystick = new Joystick(RobotMap.DRIVER_RIGHT_JOYSTICK_PORT);
            
            
            front_left_jaguar = new CANJaguar(RobotMap.FRONT_LEFT_JAGUAR_PORT);
            front_left_jaguar.changeControlMode(CANJaguar.ControlMode.kPercentVbus);
            front_left_jaguar.setSpeedReference(CANJaguar.SpeedReference.kQuadEncoder);
            front_left_jaguar.setPositionReference(CANJaguar.PositionReference.kQuadEncoder);
            front_left_jaguar.configEncoderCodesPerRev(250);
            
            back_left_jaguar = new CANJaguar(RobotMap.BACK_LEFT_JAGUAR_PORT);
            back_left_jaguar.changeControlMode(CANJaguar.ControlMode.kPercentVbus);
            back_left_jaguar.setSpeedReference(CANJaguar.SpeedReference.kQuadEncoder);
            back_left_jaguar.setPositionReference(CANJaguar.PositionReference.kQuadEncoder);
            back_left_jaguar.configEncoderCodesPerRev(250);
            
            back_right_jaguar = new CANJaguar(RobotMap.BACK_RIGHT_JAGUAR_PORT);
            back_right_jaguar.changeControlMode(CANJaguar.ControlMode.kPercentVbus);
            back_right_jaguar.setSpeedReference(CANJaguar.SpeedReference.kQuadEncoder);
            back_right_jaguar.setPositionReference(CANJaguar.PositionReference.kQuadEncoder);
            back_right_jaguar.configEncoderCodesPerRev(250);
            
            front_right_jaguar = new CANJaguar(RobotMap.FRONT_RIGHT_JAGUAR_PORT);
            front_right_jaguar.changeControlMode(CANJaguar.ControlMode.kPercentVbus);
            front_right_jaguar.setSpeedReference(CANJaguar.SpeedReference.kQuadEncoder);
            front_right_jaguar.setPositionReference(CANJaguar.PositionReference.kQuadEncoder);
            front_right_jaguar.configEncoderCodesPerRev(250);
            
            
            pick_up_jaguar = new CANJaguar(RobotMap.PICK_UP_JAGUAR_PORT);
            pick_up_jaguar.changeControlMode(CANJaguar.ControlMode.kPercentVbus);
            pick_up_jaguar.setSpeedReference(CANJaguar.SpeedReference.kQuadEncoder);
            pick_up_jaguar.configEncoderCodesPerRev(250);
            
        } 
        catch (CANTimeoutException ex) 
        {
            ex.printStackTrace();
        }        
        
        mecanum = new Mecanum();
        orbit_reset = new ToggleButton(driver_left_joystick, 10);
        turn_lock = new ToggleButton(driver_right_joystick, 1);
        
        pick_up = new PickUp();
        pickup_solenoid_extend = new Solenoid(RobotMap.PICKUP_EXTEND_PORT);
        pickup_solenoid_retract = new Solenoid(RobotMap.PICKUP_RETRACT_PORT);
        
        shooter = new Shooter();
        fire_button = new ToggleButton(operator_joystick, 1);
        manual_button = new ToggleButton(operator_joystick, RobotMap.SHOOTER_MANUAL_BUTTON);
           
        
        photon_cannon = new Relay(RobotMap.PHOTON_CANNON_PORT, Relay.Direction.kForward);
        photon_cannon_toggle = new ToggleButton(operator_joystick, RobotMap.PHOTON_CANNON_TOGGLE_INDEX);
        
//        compass = new HMC5883L_I2C(RobotMap.COMPASS_MODULE_ADDRESS);
        compressor = new Compressor(RobotMap.PRESSURE_DIGITAL_INPUT, RobotMap.COMPRESSOR_RELAY_NUM);
        logger = new Logger(front_left_jaguar, back_left_jaguar, back_right_jaguar, front_right_jaguar, pick_up_jaguar, shooter, driver_left_joystick, driver_right_joystick, operator_joystick, mecanum, compass);
        
        ds = DriverStation.getInstance();
        ds_LCD = DriverStationLCD.getInstance();
        
        shooter.enable();
        compressor.start();
        is_automated = false;
        
        inform_table = NetworkTable.getTable("inform_table");
        robot_table = NetworkTable.getTable("robot_table");
        test_table = NetworkTable.getTable("datatable");
    }
    
    /**
     * This function is called once each time the robot enters autonomous mode.
     */
    public void autonomous() 
    {
        robot_table.putNumber("game_mode", 0);
        shooter.reset();
        shooter.enable();
        shooter.start();
        
        logger.reset();
        logger.enable();
        logger.start();
        
        
        start_time = System.currentTimeMillis();
        double distance = 0;
        long loop_time = start_time;
        boolean has_deployed_arm = false;
        
        
        try 
        {
            Thread.sleep(300);
        } 
        catch (InterruptedException ex) 
        {
            ex.printStackTrace();
        }
        
        double[] auton_commands = new double[3];
        auton_commands[0] = 0.3;
        auton_commands[1] = 0;
        auton_commands[2] = 0;
        
        double auton_offset = 180;
        mecanum.set_front(auton_offset);
        
        while ( ((Math.abs(System.currentTimeMillis() - 4000) <= start_time) && distance <= 10) && isAutonomous() && isEnabled())
        {
            if (!has_deployed_arm && (Math.abs(System.currentTimeMillis() - 3000) >= start_time))
            {
                pick_up.set_position(RobotMap.PICK_UP_DOWN);
                
                try 
                {
                    pick_up_jaguar.setX(0.5);
                } 
                catch (CANTimeoutException ex) 
                {
                    ex.printStackTrace();
                }
                
                pickup_solenoid_extend.set(pick_up.get_position());
                pickup_solenoid_retract.set(!pick_up.get_position());
                
                try 
                {
                    pick_up_jaguar.setX(0.0);
                } 
                catch (CANTimeoutException ex) 
                {
                    ex.printStackTrace();
                }
                
                has_deployed_arm = true;
            }
            
            try 
            {
                distance = (front_left_jaguar.getPosition() + back_left_jaguar.getPosition() + back_right_jaguar.getPosition() + front_right_jaguar.getPosition()) / 4.0;
            } 
            catch (CANTimeoutException ex) 
            {
                ex.printStackTrace();
            }
            distance = 0.106103295 * distance;
            
            mecanum.drive_mecanum(auton_commands);
           
            try
            {
                front_left_jaguar.setX(mecanum.get_front_left());
                back_left_jaguar.setX(mecanum.get_back_left());
                back_right_jaguar.setX(mecanum.get_back_right());
                front_right_jaguar.setX(mecanum.get_front_right());
            } 
            catch (CANTimeoutException ex)
            {
              ex.printStackTrace();
            }
        }
        auton_commands[0] = 0.0;
        mecanum.drive_mecanum(auton_commands);
        try 
        {
            front_left_jaguar.setX(mecanum.get_front_left());
            back_left_jaguar.setX(mecanum.get_back_left());
            back_right_jaguar.setX(mecanum.get_back_right());
            front_right_jaguar.setX(mecanum.get_front_right());
        } 
        catch (CANTimeoutException ex) 
        {
            ex.printStackTrace();
        }
        try 
        {
            Thread.sleep(200);
        } 
        catch (InterruptedException ex) 
        {
            ex.printStackTrace();
        }
        shooter.fire(true, 1);
        logger.disable();
    }

    /**
     * This function is called once each time the robot enters operator control.
     */
    public void operatorControl() 
    {
        robot_table.putNumber("game_mode", 1);
        logger.reset();
        logger.enable();
        logger.start();
        
        shooter.reset();
        shooter.enable();
        shooter.start();
        
        double compass_offset = 0;
        double lock_press_time = 0;
        boolean has_turned = true;
        
        boolean photon_cannon_state = false;
        boolean has_reset = true;
        
        double throttle;
        
        double[] center_pt = new double[2];
        
        start_time = System.currentTimeMillis();
        long pick_up_extend_time = start_time;
        long last_time = start_time;
        
        
        while(isOperatorControl() && isEnabled())
        {
//            System.out.println("Loop Time: " + (System.currentTimeMillis() - last_time));

            test_table.putNumber("X", 1504);
            test_table.putNumber("Y", 500);
            
            if (manual_button.is_rising())
            {
                shooter.enable_manual();
            }
            else if (operator_joystick.getRawButton(RobotMap.SHOOTER_MANUAL_BUTTON))
            {
                shooter.write_manual(-operator_joystick.getY());
            }
            else if (manual_button.is_falling())
            {
                shooter.disable_manual();
            }
            
            //Mecanum Drive Handling
            if (orbit_reset.is_rising())
            {
                center_pt[0] = 0;
                center_pt[1] = 0;
            }
            mecanum.set_center_point(center_pt);
            
            if (driver_right_joystick.getRawButton(RobotMap.ROTATION_BUTTON_DEFAULT))
            {
                mecanum.set_front(180);
                compass_offset = Math.PI;
            }
            else if (driver_right_joystick.getRawButton(RobotMap.ROTATION_BUTTON_90))
            {
                mecanum.set_front(270);
                compass_offset = (3.0/2.0) * Math.PI;
            }        
            else if (driver_right_joystick.getRawButton(RobotMap.ROTATION_BUTTON_180))
            {
                mecanum.set_front(0);
                compass_offset = 0;
            }
            else if (driver_right_joystick.getRawButton(RobotMap.ROTATION_BUTTON_270))
            {
                mecanum.set_front(90);
                compass_offset = Math.PI / 2.0;
            }
            
            throttle = ((-operator_joystick.getThrottle() + 1.0) / 4.0) + 0.5;
            shooter.set_max_speed(throttle);
            System.out.println("Throttle: " + throttle);
            
            if (driver_right_joystick.getTrigger())
            {
                mecanum.field_independent(true);
            }
            else
            {
                mecanum.field_independent(false);
            }
            
            double[] commands = new double[3];
            if (mecanum.is_field_independent())
            {
                mecanum.set_front((compass_offset + compass.getHeading() - RobotMap.COMPASS_ANGLE_REFERENCE)%(2*Math.PI));
                if (Math.abs(driver_right_joystick.getX()) > 0.2)
                {
                    commands[2] = driver_right_joystick.getX();
                }
                else if (Math.abs(driver_right_joystick.getX()) < 0.2)
                {
                    mecanum.update_heading(compass.getHeading());
                }
            }
            if (is_automated)
            {
                
            }
            else
            {
                commands[0] = driver_left_joystick.getTrigger()? -1*driver_left_joystick.getY(): -0.65*driver_left_joystick.getY();
                commands[1] = driver_left_joystick.getTrigger()? driver_left_joystick.getX(): 0.65 * driver_left_joystick.getX();
                commands[2] = driver_right_joystick.getTrigger()? commands[2]: driver_left_joystick.getTrigger()? -1*driver_right_joystick.getX(): -0.5 * driver_right_joystick.getX();
                mecanum.drive_mecanum(commands);
            }
            
            //Pickup Handling
            if (!has_reset && (System.currentTimeMillis() - pick_up_extend_time) > 250)
            {
                pick_up.set_raw_speed(0.0);
                has_reset = true;
            }
            
            if(operator_joystick.getRawButton(RobotMap.PICK_UP_BUTTON_STOP))
            {
                pick_up.set_speed(RobotMap.PICK_UP_STOP);
            }
            else if(operator_joystick.getRawButton(RobotMap.PICK_UP_BUTTON_REVERSE))
            {
                pick_up.set_speed(RobotMap.PICK_UP_OUT);
            }
            else if(operator_joystick.getRawButton(RobotMap.PICK_UP_BUTTON_MED))
            {
                pick_up.set_speed(RobotMap.PICK_UP_MED);
            }
            else if(operator_joystick.getRawButton(RobotMap.PICK_UP_BUTTON_MAX))
            {
                pick_up.set_speed(RobotMap.PICK_UP_MAX);
            }

            //Shooter Handling
            if (fire_button.is_rising())
            {
                logger.write_s(System.currentTimeMillis() + " Fired");
                if(operator_joystick.getRawButton(RobotMap.SHOOTER_TOSS_BUTTON))
                {
                    shooter.fire(true, 1);
                }
                else
                {
                    shooter.fire(true, 0);
                }
            }
            else if (!operator_joystick.getTrigger())
            {
                shooter.fire(false);
            }
            
            //Photon Cannon Handling
            if (photon_cannon_toggle.is_rising())
            {
                photon_cannon_state = !photon_cannon_state;
                logger.write_s(System.currentTimeMillis() + " Toggled Photon Cannon");
                if(photon_cannon_state)
                {
                   photon_cannon.set(Relay.Value.kOn);
                }
                else
                {
                    photon_cannon.set(Relay.Value.kOff);
                }
            }
            
            //Set Solenoids
            if (operator_joystick.getRawButton(RobotMap.PICKUP_SOLENOID_BUTTON_RETRACT))        
            {
                logger.write_s(System.currentTimeMillis() + " Toggled Solenoid");
                pick_up.set_position(RobotMap.PICK_UP_UP);
                pickup_solenoid_extend.set(pick_up.get_position());
                pickup_solenoid_retract.set(!pick_up.get_position());
                compressor.start();
            }
            else if (operator_joystick.getRawButton(RobotMap.PICKUP_SOLENOID_BUTTON_EXTEND))
            {
                logger.write_s(System.currentTimeMillis() + " Toggled Solenoid");
                pick_up.set_position(RobotMap.PICK_UP_DOWN);
                pickup_solenoid_extend.set(pick_up.get_position());
                pickup_solenoid_retract.set(!pick_up.get_position());
                pick_up.set_raw_speed(1.0);
                pick_up_extend_time = System.currentTimeMillis();
                has_reset = false;
                compressor.stop();
            }
            
            try
            {
                //Set Jaguars
                front_left_jaguar.setX(mecanum.get_front_left());
                back_left_jaguar.setX(mecanum.get_back_left());
                back_right_jaguar.setX(mecanum.get_back_right());
                front_right_jaguar.setX(mecanum.get_front_right());
                
                pick_up_jaguar.setX(pick_up.get_speed());
                
                //Write to Driver's Station
                ds_LCD.clear();
                ds_LCD.println(DriverStationLCD.Line.kUser1, 1, "Battery Voltage: " + front_left_jaguar.getBusVoltage());
                ds_LCD.println(DriverStationLCD.Line.kUser3, 1, "Shooter Angle: " + shooter.get_angle());
                ds_LCD.println(DriverStationLCD.Line.kUser2, 1, "Loop Time: " + (System.currentTimeMillis() - last_time));
                last_time = System.currentTimeMillis();
                ds_LCD.updateLCD();
            } 
            catch (CANTimeoutException ex)
            {
                ex.printStackTrace();
            }
        }
        shooter.disable();
        logger.disable();
    }
    /**
     * This function is called once each time the robot enters test mode.
     */
    public void test() 
    {
    
    }
}
