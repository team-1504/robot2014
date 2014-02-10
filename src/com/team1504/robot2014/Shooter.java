/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.team1504.robot2014;

import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.can.CANTimeoutException;

/**
 *
 * @author Gavaga
 */
public class Shooter
{
    private static final double DEFAULT_FIRE_DISTANCE = 0;
    private static final long DEFAULT_RAMP_TIME = 250;
    private static final double DEFAULT_ANGLE = 0.7;
    private static final double POT_RANGE = 90;
    
    private static boolean is_firing;
    
    private static int ramp_time;
    
    private static double stop_angle;
    
    private static CANJaguar shooter_jag_1;
    private static CANJaguar shooter_jag_2;
    
    private static AnalogChannel pot;
    
    private static ShooterThread sh_thread;
    
    private static Solenoid solenoid_1 = RobotMain.pickup_sol_ex_2;
    private static Solenoid solenoid_2 = RobotMain.pickup_sol_ret_2;
    private static boolean solenoid = true;
    
    public Shooter()
    {
        try 
        {
            shooter_jag_1 = new CANJaguar(RobotMap.SHOOTER_JAGUAR_PORT_1);
            shooter_jag_2 = new CANJaguar(RobotMap.SHOOTER_JAGUAR_PORT_2);
        }
        catch (CANTimeoutException ex)
        {
            ex.printStackTrace();
        }
        pot = new AnalogChannel(RobotMap.SHOOTER_POT_MODULE_NUM);
        stop_angle = DEFAULT_ANGLE;
        
        solenoid_set(solenoid);
        
        is_firing = false;
        
        sh_thread = new ShooterThread();
        sh_thread.start();   
    }
    
    private void solenoid_set(boolean solenoid)
    {
        solenoid_1.set(solenoid);
        solenoid_2.set(!solenoid);
    }
    
    public void fire(boolean firing)
    {
        is_firing = firing;
    }
    
    private class ShooterThread extends Thread
    {
        public void run() 
        {
            long last_loop_time = System.currentTimeMillis();

            double value = 0;

            while(is_firing && get_angle() < stop_angle)
            {
                solenoid = false;
                solenoid_set(solenoid);
                value += ((double)(System.currentTimeMillis() - last_loop_time)) / ramp_time;
                last_loop_time = System.currentTimeMillis();
                set_shooter_speed(( value >= 1) ? 1: value );
            }
        }
        
        private double get_angle()
        {
            double volts = ((pot.getLSBWeight() * 1e-9) * pot.getVoltage()) - (pot.getOffset() * 1e-9);
            return (volts + 10) * (POT_RANGE / 20.);
        }
        
        private void set_shooter_speed(double speed)
        {
            try
            {
                shooter_jag_1.setX(speed);
                shooter_jag_2.setX(speed);
            }
            catch (CANTimeoutException ex)
            {
                ex.printStackTrace();
            }
        }
        
    }
}
