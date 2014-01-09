/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package com.team1504.robot2014;


import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStationLCD;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Watchdog;
import edu.wpi.first.wpilibj.can.CANTimeoutException;
import java.util.Date;

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
    private static CANJaguar front_left_jag, back_left_jag, back_right_jag, front_right_jag;
    private static Joystick driver_left_joystick, driver_right_joystick, operator_joystick;
    
    //Driver Station
    private static DriverStation ds;
    private static DriverStationLCD ds_LCD;
    
    //Logging
    private static Date date;
    private static Timer logging_timer;
    
    public RobotMain()
    {
        //Initialization
        try 
        {
            front_left_jag = new CANJaguar(RobotMap.FRONT_LEFT_JAGUAR_PORT);
            back_left_jag = new CANJaguar(RobotMap.BACK_LEFT_JAGUAR_PORT);
            back_right_jag = new CANJaguar(RobotMap.BACK_RIGHT_JAGUAR_PORT);
            front_right_jag = new CANJaguar(RobotMap.FRONT_RIGHT_JAGUAR_PORT);
            
            operator_joystick = new Joystick(RobotMap.OPERATOR_JOYSTICK_PORT);
            driver_left_joystick = new Joystick(RobotMap.DRIVER_LEFT_JOYSTICK_PORT);
            driver_right_joystick = new Joystick(RobotMap.DRIVER_RIGHT_JOYSTICK_PORT);
            
            mecanum = new Mecanum(front_left_jag, back_left_jag, back_right_jag, front_right_jag);
        } catch (CANTimeoutException ex) 
        {
            ex.printStackTrace();
        }
    }
    
    /**
     * This function is called once each time the robot enters autonomous mode.
     */
    public void autonomous() 
    {
        logging_timer = new Timer();
        logging_timer.start();
        
    }

    /**
     * This function is called once each time the robot enters operator control.
     */
    public void operatorControl() 
    {
        date = new Date();
        logging_timer.reset();
        logging_timer.start();
        
        while(isOperatorControl() && isEnabled())
        {
            mecanum.driveMecanum(driver_left_joystick.getX(), driver_left_joystick.getY(), driver_right_joystick.getX());
        }
    }
    
    /**
     * This function is called once each time the robot enters test mode.
     */
    public void test() 
    {
    
    }
}
