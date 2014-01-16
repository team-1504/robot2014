/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package com.team1504.robot2014;


import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.DigitalModule;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStationLCD;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Watchdog;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.DigitalIOButton;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
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
    private static CANJaguar front_left_jaguar, back_left_jaguar, back_right_jaguar, front_right_jaguar;
    private static Joystick driver_left_joystick, driver_right_joystick, operator_joystick;
    
    //Shooter
    private static CANJaguar shooter_jaguar;
    private static Solenoid shooter_release_solenoid;
    private static Shooter shooter;
    
    //Driver Station
    private static DriverStation ds;
    private static DriverStationLCD ds_LCD;
    
    //Automation
    private static Button toggle_automation_button;
    private static Button zone_one_button;
    private static Button zone_two_button;
    private static Button zone_three_button;
    
    private static boolean is_automated;
    private static int zone;
    
    private static PiSignaler pi;
//    private static PiSignalerI2C pi;
//    private static PiSignalerSerial pi;
    
    private static PiComModule pi_module;
   
    
    //Logging
    private static Date date;
    private static Timer logging_timer;
    
    public RobotMain()
    {
        //Initialization
        try 
        {            
            front_left_jaguar = new CANJaguar(RobotMap.FRONT_LEFT_JAGUAR_PORT);
            back_left_jaguar = new CANJaguar(RobotMap.BACK_LEFT_JAGUAR_PORT);
            back_right_jaguar = new CANJaguar(RobotMap.BACK_RIGHT_JAGUAR_PORT);
            front_right_jaguar = new CANJaguar(RobotMap.FRONT_RIGHT_JAGUAR_PORT);
            
//            shooter_jaguar = new CANJaguar(RobotMap.WINCH_JAGUAR_PORT);
//            shooter_release_solenoid = new Solenoid(RobotMap.SHOOTER_RELEASE_SOLENOID_PORT);
            
            operator_joystick = new Joystick(RobotMap.OPERATOR_JOYSTICK_PORT);
            driver_left_joystick = new Joystick(RobotMap.DRIVER_LEFT_JOYSTICK_PORT);
            driver_right_joystick = new Joystick(RobotMap.DRIVER_RIGHT_JOYSTICK_PORT);
            
//            toggle_automation_button = new DigitalIOButton(RobotMap.AUTOMATION_TOGGLE_BUTTON_PORT);
//            zone_one_button = new DigitalIOButton(RobotMap.ZONE_ONE_BUTTON_PORT);
//            zone_two_button = new DigitalIOButton(RobotMap.ZONE_TWO_BUTTON_PORT);
//            zone_three_button = new DigitalIOButton(RobotMap.ZONE_THREE_BUTTON_PORT);
//            
//            shooter = new Shooter(shooter_jaguar, shooter_release_solenoid);                        
        } catch (CANTimeoutException ex) 
        {
            ex.printStackTrace();
        }
        mecanum = new Mecanum(1, 1, -1, -1);
        logging_timer = new Timer();
        
        is_automated = false;
//        pi_module = new PiComModule(1, 0, 3, 0, 0, 0, 5, 3, 0);
//        pi = new PiSignaler(pi_module);        
    }
    
    /**
     * This function is called once each time the robot enters autonomous mode.
     */
    public void autonomous() 
    {
        logging_timer.start();
        //(new Thread(pi)).start();
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
            if (is_automated)
            {
                Object[] command_packet = pi.get_packet_in();
                mecanum.drive_mecanum( ((Double)command_packet[0]).doubleValue(), ((Double)command_packet[1]).doubleValue(), ((Double)command_packet[2]).doubleValue());
                
                try
                {
                    front_left_jaguar.setX(mecanum.get_front_left());
                    back_left_jaguar.setX(mecanum.get_back_left());
                    back_right_jaguar.setX(mecanum.get_back_right());
                    front_right_jaguar.setX(mecanum.get_front_right());
                } catch (CANTimeoutException ex)
                {
                    ex.printStackTrace();
                }
            }
            else
            {
                mecanum.drive_mecanum(-1*driver_left_joystick.getY(), driver_left_joystick.getX(), driver_right_joystick.getX());
                
                try
                {
                    front_left_jaguar.setX(mecanum.get_front_left());
                    back_left_jaguar.setX(mecanum.get_back_left());
                    back_right_jaguar.setX(mecanum.get_back_right());
                    front_right_jaguar.setX(mecanum.get_front_right());
                } catch (CANTimeoutException ex)
                {
                    ex.printStackTrace();
                }
            }
            
        }
    }
    
    /**
     * This function is called once each time the robot enters test mode.
     */
    public void test() 
    {
    
    }
}
