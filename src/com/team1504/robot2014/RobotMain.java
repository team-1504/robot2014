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
    private static CANJaguar front_left_jaguar, back_left_jaguar, back_right_jaguar, front_right_jaguar, pick_up_jaguar; 
    private static Joystick driver_left_joystick, driver_right_joystick, operator_joystick;
    
    //Shooter
    private static CANJaguar shooter_jaguar_1;
    private static CANJaguar shooter_jaguar_2;
    private static Solenoid shooter_release_solenoid;
    private static Shooter shooter;
    
    //Thing those Mechanical Guys Wanted
    private static Button solenoid_button;
    private static Solenoid extend_solenoid_1;
    private static Solenoid extend_solenoid_2;
    private static Solenoid retract_solenoid_1;
    private static Solenoid retract_solenoid_2;
    
    //Driver Station
    private static DriverStation ds;
    private static DriverStationLCD ds_LCD;
    
    //Solenoid pickup mechanism
    private static PickUp pick_up;
    
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
            pick_up_jaguar = new CANJaguar(RobotMap.PICK_UP_JAGUAR_PORT);
            
            shooter_jaguar_1 = new CANJaguar(RobotMap.SHOOTER_JAGUAR_PORT_1);
            shooter_jaguar_2 = new CANJaguar(RobotMap.SHOOTER_JAGUAR_PORT_2);
//            shooter_release_solenoid = new Solenoid(RobotMap.SHOOTER_RELEASE_SOLENOID_PORT);
            
            operator_joystick = new Joystick(RobotMap.OPERATOR_JOYSTICK_PORT);
            driver_left_joystick = new Joystick(RobotMap.DRIVER_LEFT_JOYSTICK_PORT);
            driver_right_joystick = new Joystick(RobotMap.DRIVER_RIGHT_JOYSTICK_PORT);
            
            extend_solenoid_1 = new Solenoid(RobotMap.EXTEND_1_PORT);
            extend_solenoid_2 = new Solenoid(RobotMap.EXTEND_2_PORT);
            retract_solenoid_1 = new Solenoid(RobotMap.RETRACT_1_PORT);
            retract_solenoid_2 = new Solenoid(RobotMap.RETRACT_2_PORT);
                    
            
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
        mecanum = new Mecanum();
        pick_up = new PickUp();
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
        boolean prev_button_state = false;
        date = new Date();
        logging_timer.reset();
        logging_timer.start();
        
        while(isOperatorControl() && isEnabled())
        {
            if (is_automated)
            {
                Object[] command_packet = pi.get_packet_in();
                mecanum.drive_mecanum( ((Double)command_packet[0]).doubleValue(), ((Double)command_packet[1]).doubleValue(), ((Double)command_packet[2]).doubleValue());
            }
            else
            {
                mecanum.drive_mecanum(-1*driver_left_joystick.getY(), driver_left_joystick.getX(), driver_right_joystick.getX());
            }
            
            try
            {
                front_left_jaguar.setX(mecanum.get_front_left());
                back_left_jaguar.setX(mecanum.get_back_left());
                back_right_jaguar.setX(mecanum.get_back_right());
                front_right_jaguar.setX(mecanum.get_front_right());                
                
                double shooter_x;
                if ( Math.abs(operator_joystick.getY()) < 0.08 )
                {
                    shooter_x = 0;
                }
                else if (operator_joystick.getTrigger())
                {
                    shooter_x = -1*operator_joystick.getY() > 0? 1: -1;
                }
                else
                {
                    shooter_x = -1*operator_joystick.getY();
                }
                shooter_jaguar_1.setX(shooter_x);
                shooter_jaguar_2.setX(shooter_x);
                
//                boolean button_pressed = operator_joystick.getRawButton(RobotMap.SOLENOID_BUTTON_INDEX);
//                if (button_pressed)
//                {
//                    if(!extend_solenoid_1.get())
//                    {
//                        extend_solenoid_1.set(true);
//                        extend_solenoid_2.set(true);
//                        retract_solenoid_1.set(false);
//                        retract_solenoid_2.set(false);
//                    }
//                }
//                else
//                {
//                    if(extend_solenoid_1.get())
//                    {
//                        extend_solenoid_1.set(false);
//                        extend_solenoid_2.set(false);
//                        retract_solenoid_1.set(true);
//                        retract_solenoid_2.set(true);
//                    }
//                }
//            } 
//            catch (CANTimeoutException ex)
//            {
//                ex.printStackTrace();
//            }
            
             boolean button_pressed = operator_joystick.getRawButton(RobotMap.SOLENOID_BUTTON_INDEX);
              //the previous state of the position of the button
                if (prev_button_state)
                {
                    extend_solenoid_1.set(!extend_solenoid_1.get());  
                    extend_solenoid_2.set(!extend_solenoid_2.get());
                    retract_solenoid_1.set(!retract_solenoid_1.get());
                    retract_solenoid_2.set(!retract_solenoid_2.get());
                }
                prev_button_state = button_pressed;
                if(operator_joystick.getRawButton(RobotMap.PICK_UP_BUTTON_STOP))
                {
                    pick_up.set_state(PickUp.PICK_UP_STOP);
                }
                
                else if(operator_joystick.getRawButton(RobotMap.PICK_UP_BUTTON_REVERSE))
                {
                    pick_up.set_state(PickUp.PICK_UP_REVERSE);
                }
                
                else if(operator_joystick.getRawButton(RobotMap.PICK_UP_BUTTON_MED))
                {
                    pick_up.set_state(PickUp.PICK_UP_MED);
                }
               
                else if(operator_joystick.getRawButton(RobotMap.PICK_UP_BUTTON_MAX))
                {
                    pick_up.set_state(PickUp.PICK_UP_MAX);
                }
                
                pick_up_jaguar.setX(pick_up.get_jaguar_value());
            } 
            catch (CANTimeoutException ex)
            {
                ex.printStackTrace();
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
