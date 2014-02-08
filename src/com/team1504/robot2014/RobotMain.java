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
    public static Solenoid extend_solenoid_1;
    public static Solenoid extend_solenoid_2;
    public static Solenoid retract_solenoid_1;
    public static Solenoid retract_solenoid_2;
   
    private static ToggleButton pick_up_sol_toggle;
    private static ToggleButton photon_cannon_toggle;
    
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
            
            extend_solenoid_1 = new Solenoid(RobotMap.PICKUP_EXTEND_1_PORT);
            extend_solenoid_2 = new Solenoid(RobotMap.PICKUP_EXTEND_2_PORT);
            retract_solenoid_1 = new Solenoid(RobotMap.PICKUP_RETRACT_1_PORT);
            retract_solenoid_2 = new Solenoid(RobotMap.PICKUP_RETRACT_2_PORT);
            
            photon_cannon = new Relay(RobotMap.PHOTON_CANNON_PORT, Relay.Direction.kForward);
            
            pick_up_sol_toggle = new ToggleButton(operator_joystick, RobotMap.PICKUP_SOLENOID_BUTTON_INDEX);
            photon_cannon_toggle = new ToggleButton(operator_joystick, RobotMap.PHOTON_CANNON_TOGGLE_INDEX);
            
//            toggle_automation_button = new DigitalIOButton(RobotMap.AUTOMATION_TOGGLE_BUTTON_PORT);
//            zone_one_button = new DigitalIOButton(RobotMap.ZONE_ONE_BUTTON_PORT);
//            zone_two_button = new DigitalIOButton(RobotMap.ZONE_TWO_BUTTON_PORT);
//            zone_three_button = new DigitalIOButton(RobotMap.ZONE_THREE_BUTTON_PORT);
            
        } 
        catch (CANTimeoutException ex) 
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
        long start_time = System.currentTimeMillis();
        double distance = 0;
        long loop_time = start_time;
        long last_loop_time = loop_time;
        
        pick_up.set_position(RobotMap.PICK_UP_DOWN);
        extend_solenoid_1.set(pick_up.get_position());
        retract_solenoid_1.set(!pick_up.get_position());
        
        double[] auton_commands = new double[3];
        auton_commands[0] = 0.3;
        auton_commands[1] = 0;
        auton_commands[2] = 0;
        
        double auton_offset = 180;
        mecanum.set_front(auton_offset);
        auton_commands = mecanum.front_side(auton_commands);
        
        
        while ((Math.abs(System.currentTimeMillis() - 8000) <= start_time) || distance >= 10)
        {
            loop_time = System.currentTimeMillis();
            
            mecanum.drive_mecanum(auton_commands);
           
            try
            {
                front_left_jaguar.setX(mecanum.get_front_left());
                back_left_jaguar.setX(mecanum.get_back_left());
                back_right_jaguar.setX(mecanum.get_back_right());
                front_right_jaguar.setX(mecanum.get_front_right());
            } catch (CANTimeoutException ex){
              ex.printStackTrace();
            }
            
            double avg_speed = 0;
            try {
                avg_speed = (front_left_jaguar.getSpeed() + back_left_jaguar.getSpeed() + front_right_jaguar.getSpeed() + back_right_jaguar.getSpeed())/4;
            } catch (CANTimeoutException ex) {
                ex.printStackTrace();
            }
            double speed_fpms = avg_speed*2.094/60000; //8" diameter on wheel means 8pi inches per revolution,divided by 12 is in ft,  min to ms is 60000
            distance = distance + (speed_fpms * (loop_time - last_loop_time));
            last_loop_time = loop_time;
        }
        
    }

    /**
     * This function is called once each time the robot enters operator control.
     */
    public void operatorControl() 
    {
        date = new Date();
        logging_timer.reset();
        logging_timer.start();
        
        boolean photon_cannon_state = false;
        boolean rotation_button_pressed = false;
        
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
            
            double pickup_val = 0;
            
            pickup_val = operator_joystick.getY() * -1;
            if (Math.abs(pickup_val) < 0.1) pickup_val = 0;
            double throttle = (operator_joystick.getThrottle() + 1)*0.5;
            pickup_val *= throttle;
            
            if(operator_joystick.getRawButton(RobotMap.PICK_UP_BUTTON_STOP))
            {
                pick_up.set_speed(RobotMap.PICK_UP_STOP);
            }

            else if(operator_joystick.getRawButton(RobotMap.PICK_UP_BUTTON_REVERSE))
            {
                pick_up.set_speed(RobotMap.PICK_UP_REVERSE);
            }

            else if(operator_joystick.getRawButton(RobotMap.PICK_UP_BUTTON_MED))
            {
                pick_up.set_speed(RobotMap.PICK_UP_MED);
            }

            else if(operator_joystick.getRawButton(RobotMap.PICK_UP_BUTTON_MAX))
            {
                pick_up.set_speed(RobotMap.PICK_UP_MAX);
            }
            
            if (operator_joystick.getTrigger())
            {
                shooter_thread.fire(true);
            }
            else
            {
                shooter_thread.fire(false);
            }
            
            rotation_button_pressed = driver_left_joystick.getRawButton(RobotMap.ROTATION_BUTTON_INDEX);
            if(driver_left_joystick.getRawButton(RobotMap.ROTATION_BUTTON_INDEX))
            {
                front_angle = front_angle == 0.? 180.:0.; 
            }

            if (photon_cannon_toggle.should_toggle())
            {      
               photon_cannon_state = !photon_cannon_state;
               if(photon_cannon_state)
               {
                  photon_cannon.set(Relay.Value.kOn);
               }
               else
               {
                   photon_cannon.set(Relay.Value.kOff);
               }
            }
            
            try
            {
                pick_up_jaguar.setX(pickup_val);
                
                front_left_jaguar.setX(mecanum.get_front_left());
                back_left_jaguar.setX(mecanum.get_back_left());
                back_right_jaguar.setX(mecanum.get_back_right());
                front_right_jaguar.setX(mecanum.get_front_right());
                
                //the previous state of the position of the button
                if (pick_up_sol_toggle.should_toggle())
                {
                     extend_solenoid_1.set(pick_up.get_position());
                     retract_solenoid_1.set(!pick_up.get_position());

                }
                
//                pick_up_jaguar.setX(pick_up.get_jaguar_value());
                
                ds_LCD.clear();
                ds_LCD.println(DriverStationLCD.Line.kUser1, 1, "Jag FL Speed: " + front_left_jaguar.getSpeed());
                ds_LCD.println(DriverStationLCD.Line.kUser2, 1, "Jag BL Speed: " + back_left_jaguar.getSpeed());
                ds_LCD.println(DriverStationLCD.Line.kUser3, 1, "Jag BR Speed: " + back_right_jaguar.getSpeed());
                ds_LCD.println(DriverStationLCD.Line.kUser4, 1, "Jag FR Speed: " + front_right_jaguar.getSpeed());
                
                ds_LCD.updateLCD();
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
