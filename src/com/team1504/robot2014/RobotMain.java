/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package com.team1504.robot2014;

//import com.team1504.HMC5883L_I2C;
import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStationLCD;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.buttons.Button;
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
    private static ToggleButton toggle_front;
    
    //Shooter
    private static Shooter shooter;
    
    //Pickup mechanism
    private static PickUp pick_up;
    
    public static Solenoid pickup_sol_ex_1;
    public static Solenoid pickup_sol_ex_2;
    public static Solenoid pickup_sol_ret_1;
    public static Solenoid pickup_sol_ret_2;
   
    private static ToggleButton pick_up_sol_toggle;
    
    //Photon Cannon
    private static Relay photon_cannon;
    private static ToggleButton photon_cannon_toggle;
    
    //Vision
    private static ComModule pi;
    
//    private static HMC5883L_I2C compass;
    
    //Automation
    private static boolean is_automated;
    
    private static Compressor compressor;
    
    //Logging
    private static Logger logger;
    long start_time;
    
    //Driver Station
    private static DriverStation ds;
    private static DriverStationLCD ds_LCD;
    
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
        
        
        pickup_sol_ex_1 = new Solenoid(RobotMap.PICKUP_EXTEND_1_PORT);
        pickup_sol_ex_2 = new Solenoid(RobotMap.PICKUP_EXTEND_2_PORT);
        pickup_sol_ret_1 = new Solenoid(RobotMap.PICKUP_RETRACT_1_PORT);
        pickup_sol_ret_2 = new Solenoid(RobotMap.PICKUP_RETRACT_2_PORT);
        
        pick_up_sol_toggle = new ToggleButton(operator_joystick, RobotMap.PICKUP_SOLENOID_BUTTON_RETRACT);

//        compass = new HMC5883L_I2C(RobotMap.COMPASS_MODULE_ADDRESS);       
        
        photon_cannon = new Relay(RobotMap.PHOTON_CANNON_PORT, Relay.Direction.kForward);
        photon_cannon_toggle = new ToggleButton(operator_joystick, RobotMap.PHOTON_CANNON_TOGGLE_INDEX);
        toggle_front = new ToggleButton(driver_left_joystick, RobotMap.ROTATION_BUTTON_DEFAULT);
        
        compressor = new Compressor(RobotMap.PRESSURE_DIGITAL_INPUT, RobotMap.COMPRESSOR_RELAY_NUM);
//        pi = new ComModule(RobotMap.RASPBERRY_PI_IP_ADDRESS, 1504);
//        pi.start();
        
        mecanum = new Mecanum();
        pick_up = new PickUp();
        shooter = new Shooter();
        
        ds = DriverStation.getInstance();
        ds_LCD = DriverStationLCD.getInstance();
        
        logger = new Logger();
        
        shooter.enable();
        
        compressor.start();
        
        is_automated = false;
    }
    
    /**
     * This function is called once each time the robot enters autonomous mode.
     */
    public void autonomous() 
    {
        logger.start_logging();
        start_time = System.currentTimeMillis();
        double distance = 0;
        long loop_time = start_time;
        long last_loop_time = loop_time;
        
        pick_up.set_position(RobotMap.PICK_UP_DOWN);
        pickup_sol_ex_1.set(pick_up.get_position());
        pickup_sol_ex_2.set(pick_up.get_position());
        pickup_sol_ret_1.set(!pick_up.get_position());
        pickup_sol_ret_2.set(!pick_up.get_position());
        
        double[] auton_commands = new double[3];
        auton_commands[0] = 0.3;
        auton_commands[1] = 0;
        auton_commands[2] = 0;
        
        double auton_offset = 180;
        mecanum.set_front(auton_offset);
        auton_commands = mecanum.front_side(auton_commands);
        
        
        while ( ((Math.abs(System.currentTimeMillis() - 8000) <= start_time) && distance <= 10) && isAutonomous() && isEnabled())
        {
            try {
                distance = (front_left_jaguar.getPosition() + back_left_jaguar.getPosition() + back_right_jaguar.getPosition() + front_right_jaguar.getPosition()) / 4.0;
            } catch (CANTimeoutException ex) {
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
        logger.stop_logging();
    }

    /**
     * This function is called once each time the robot enters operator control.
     */
    public void operatorControl() 
    {
        logger.reset_files();
        logger.start_logging();
        boolean photon_cannon_state = false;
        
        boolean last_trigger = false;
        
        long loop_time = System.currentTimeMillis();
        
        shooter.re_enable();
        
        double front_angle = 0;
        double last_wheel_position = 0;
        
        while(isOperatorControl() && isEnabled())
        {
            System.out.println(System.currentTimeMillis() - loop_time);
            loop_time = System.currentTimeMillis();
            //Com Debugging
//            Object[] packet = new Object[1];
//            packet[0] = new Long(System.currentTimeMillis() - start_time);
//            
//            pi.update_out_packet(packet);
            
            //Pickup Debugging            
//            double pickup_val = 0;
//            
//            pickup_val = operator_joystick.getY() * -1;
//            if (Math.abs(pickup_val) < 0.1) pickup_val = 0;
//            double throttle = (operator_joystick.getThrottle() + 1)*0.5;
//            pickup_val *= throttle;
//            
            //Mecanum Drive Handling
            
            double throttle = (operator_joystick.getThrottle() + 1) * -0.5;
            pick_up.set_pass_offset(throttle);
            
            double[] center_pt  = new double[2];
            center_pt[0] = 0.0;
            center_pt[1] = 0.0;
            mecanum.set_center_point(center_pt);
            
            if (driver_right_joystick.getRawButton(RobotMap.ROTATION_BUTTON_DEFAULT))
            {
//                logger.write_s(System.currentTimeMillis() + " Swapped front");
                mecanum.set_front(0);
            }
            else if (driver_right_joystick.getRawButton(RobotMap.ROTATION_BUTTON_90))
            {
                mecanum.set_front(90);
            }        
            else if (driver_right_joystick.getRawButton(RobotMap.ROTATION_BUTTON_180))
            {
                mecanum.set_front(180);
            }
            else if (driver_right_joystick.getRawButton(RobotMap.ROTATION_BUTTON_270))
            {
                mecanum.set_front(270);
            }
            
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
                commands[0] = driver_left_joystick.getTrigger()? -1*driver_left_joystick.getY(): -0.65*driver_left_joystick.getY();
                commands[1] = driver_left_joystick.getTrigger()? driver_left_joystick.getX(): 0.65 * driver_left_joystick.getX();
                commands[2] = driver_left_joystick.getTrigger()? -1*driver_right_joystick.getX(): -0.5 * driver_right_joystick.getX();
                mecanum.drive_mecanum(commands);
            }
            
            //Pickup Handling
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
            if (!last_trigger && operator_joystick.getTrigger() && operator_joystick.getRawButton(RobotMap.SHOOTER_TOSS_BUTTON))
            {
                logger.write_s(System.currentTimeMillis() + " Fired");
                shooter.fire(true, 1);
            }
            else if (!last_trigger && operator_joystick.getTrigger(GenericHID.Hand.kLeft))
            {
                shooter.fire(true, 0);
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
                pickup_sol_ex_1.set(pick_up.get_position());
                pickup_sol_ret_1.set(!pick_up.get_position());
            }
            else if (operator_joystick.getRawButton(RobotMap.PICKUP_SOLENOID_BUTTON_EXTEND))
            {
                logger.write_s(System.currentTimeMillis() + " Toggled Solenoid");
                pick_up.set_position(RobotMap.PICK_UP_DOWN);
                pickup_sol_ex_1.set(pick_up.get_position());
                pickup_sol_ret_1.set(!pick_up.get_position());
            }
            
//            Object[] packet_out = new Object[5];
//            packet_out[0] = new Long(System.currentTimeMillis());
//            packet_out[1] = new Double();
//            packet_out[2] = null;
//            packet_out[3] = new Double(compass.getHeading());
//            packet_out[4] = new Double(0);
//            
//            
//            pi.update_out_packet(packet_out);
            
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
                ds_LCD.println(DriverStationLCD.Line.kUser1, 1, "Jag FL Speed: " + front_left_jaguar.getSpeed());
                ds_LCD.println(DriverStationLCD.Line.kUser2, 1, "Jag BL Speed: " + back_left_jaguar.getSpeed());
                ds_LCD.println(DriverStationLCD.Line.kUser3, 1, "Jag BR Speed: " + back_right_jaguar.getSpeed());
                ds_LCD.println(DriverStationLCD.Line.kUser4, 1, "Jag FR Speed: " + front_right_jaguar.getSpeed());
                
                ds_LCD.println(DriverStationLCD.Line.kUser5, 1, "Shooter Pot: " + shooter.get_angle());
                
                ds_LCD.updateLCD();
                
                //Update Log File
                String log_line = "";
                log_line = log_line + "Front_Left: " + front_left_jaguar.getSpeed() + " " + front_left_jaguar.getBusVoltage() + " " + front_left_jaguar.getOutputVoltage() + " " + front_left_jaguar.getOutputCurrent() + " " + front_left_jaguar.getTemperature() + " ";
                log_line = log_line + "Back_Left: " + back_left_jaguar.getSpeed() + " " + back_left_jaguar.getBusVoltage() + " " + back_left_jaguar.getOutputVoltage() + " " + back_left_jaguar.getOutputCurrent() + " " + back_left_jaguar.getTemperature() + " ";
                log_line = log_line + "Back_Right: " + back_right_jaguar.getSpeed() + " " + back_right_jaguar.getBusVoltage() + " " + back_right_jaguar.getOutputVoltage() + " " + back_right_jaguar.getOutputCurrent() + " " + back_right_jaguar.getTemperature() + " ";
                log_line = log_line + "Front_Right: " + front_right_jaguar.getSpeed() + " " + front_right_jaguar.getBusVoltage() + " " + front_right_jaguar.getOutputVoltage() + " " + front_right_jaguar.getOutputCurrent() + " " + front_right_jaguar.getTemperature() + " ";

                log_line = log_line + "Pickup: " + pick_up_jaguar.getSpeed() + " ";
                
                log_line = log_line + "Shooter :" + shooter.get_shooter_speed() + " " + shooter.get_angle();
                logger.write_f(System.currentTimeMillis() + log_line);
            } 
            catch (CANTimeoutException ex)
            {
                ex.printStackTrace();
            }
        }
        shooter.disable();
    }
    /**
     * This function is called once each time the robot enters test mode.
     */
    public void test() 
    {
    
    }
}
