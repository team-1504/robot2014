/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package com.team1504.robot2014;


import com.team1504.HMC5883L_I2C;
import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.DigitalModule;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStationLCD;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Relay;
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
    private static Shooter shooter_thread;
    
    //Thing those Mechanical Guys Wanted
    private static Button solenoid_button;
    private static Solenoid extend_solenoid_1;
    private static Solenoid extend_solenoid_2;
    private static Solenoid retract_solenoid_1;
    private static Solenoid retract_solenoid_2;
    
    //Compass
    private static HMC5883L_I2C compass;
    
    //Driver Station
    private static DriverStation ds;
    private static DriverStationLCD ds_LCD;
    
    //Photon cannony photon_cannon;
    private static Relay photon_cannon;
    
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
            
            front_left_jaguar.changeControlMode(CANJaguar.ControlMode.kPercentVbus);
            front_left_jaguar.setSpeedReference(CANJaguar.SpeedReference.kQuadEncoder);
            front_left_jaguar.configEncoderCodesPerRev(250);
            
            back_left_jaguar.changeControlMode(CANJaguar.ControlMode.kPercentVbus);
            back_left_jaguar.setSpeedReference(CANJaguar.SpeedReference.kQuadEncoder);
            back_left_jaguar.configEncoderCodesPerRev(250);
            
            back_right_jaguar.changeControlMode(CANJaguar.ControlMode.kPercentVbus);
            back_right_jaguar.setSpeedReference(CANJaguar.SpeedReference.kQuadEncoder);
            back_right_jaguar.configEncoderCodesPerRev(250);
            
            front_right_jaguar.changeControlMode(CANJaguar.ControlMode.kPercentVbus);
            front_right_jaguar.setSpeedReference(CANJaguar.SpeedReference.kQuadEncoder);
            front_right_jaguar.configEncoderCodesPerRev(250);
            
            pick_up_jaguar = new CANJaguar(RobotMap.PICK_UP_JAGUAR_PORT);
            
            operator_joystick = new Joystick(RobotMap.OPERATOR_JOYSTICK_PORT);
            driver_left_joystick = new Joystick(RobotMap.DRIVER_LEFT_JOYSTICK_PORT);
            driver_right_joystick = new Joystick(RobotMap.DRIVER_RIGHT_JOYSTICK_PORT);
            
            extend_solenoid_1 = new Solenoid(RobotMap.EXTEND_1_PORT);
            extend_solenoid_2 = new Solenoid(RobotMap.EXTEND_2_PORT);
            retract_solenoid_1 = new Solenoid(RobotMap.RETRACT_1_PORT);
            retract_solenoid_2 = new Solenoid(RobotMap.RETRACT_2_PORT);
            
            photon_cannon = new Relay(RobotMap.PHOTON_CANNON_PORT, Relay.Direction.kForward);
            
                    
            compass = new HMC5883L_I2C(RobotMap.COMPASS_MODULE_ADDRESS);
            
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
        
        ds = DriverStation.getInstance();
        ds_LCD = DriverStationLCD.getInstance();
        
        shooter_thread = new Shooter();
        
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
        boolean prev_photon_button = false;
        boolean photon_state = false;
        date = new Date();
        logging_timer.reset();
        logging_timer.start();
        
        double front_angle = 0;
        
        while(isOperatorControl() && isEnabled())
        {
            double[] commands = new double[3];
            if (is_automated)
            {
                Object[] command_packet = pi.get_packet_in();
                for (int i = 0; i < 3; ++i)
                {
                    commands[i] = ((Double)command_packet[i]).doubleValue();
                }
                mecanum.drive_mecanum(commands);      
            }
            else
            {
                commands[0] = -1*driver_left_joystick.getY();
                commands[1] = driver_left_joystick.getX();
                commands[2] = driver_right_joystick.getX();
                mecanum.drive_mecanum(commands);
            }
            
            try
            {
                front_left_jaguar.setX(mecanum.get_front_left());
                back_left_jaguar.setX(mecanum.get_back_left());
                back_right_jaguar.setX(mecanum.get_back_right());
                front_right_jaguar.setX(mecanum.get_front_right());                
                
                if (operator_joystick.getTrigger())
                {
                    shooter_thread.fire(true);
                }
                else
                {
                    shooter_thread.fire(false);
                }
                
                boolean rotation_button_pressed = driver_left_joystick.getRawButton(RobotMap.ROTATION_BUTTON_INDEX);
                if(driver_left_joystick.getRawButton(RobotMap.ROTATION_BUTTON_INDEX))
                {
                    front_angle = front_angle == 0.? 180.:0.; 
                }
                    
                if (driver_left_joystick.getRawButton(RobotMap.PHOTON_CANNON_PORT) && !prev_photon_button)
                {                     
                   photon_state = !photon_state;   
                   if(photon_state)
                   {
                      photon_cannon.set(Relay.Value.kOn);
                   }
                   else
                   {
                       photon_cannon.set(Relay.Value.kOff);
                   }
                }
                prev_photon_button = driver_left_joystick.getRawButton(RobotMap.PHOTON_CANNON_PORT);
            
             boolean button_pressed = operator_joystick.getRawButton(RobotMap.SOLENOID_BUTTON_INDEX);
              //the previous state of the position of the button
                if (!prev_button_state && button_pressed)
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
                
                
                ds_LCD.println(DriverStationLCD.Line.kUser1, 1, "Jag FL Speed: " + front_left_jaguar.getSpeed());
                ds_LCD.println(DriverStationLCD.Line.kUser1, 1, "Jag BL Speed: " + back_left_jaguar.getSpeed());
                ds_LCD.println(DriverStationLCD.Line.kUser1, 1, "Jag BR Speed: " + back_right_jaguar.getSpeed());
                ds_LCD.println(DriverStationLCD.Line.kUser1, 1, "Jag FR Speed: " + front_right_jaguar.getSpeed());
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
