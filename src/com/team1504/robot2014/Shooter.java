/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.team1504.robot2014;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.can.CANTimeoutException;
import java.lang.Thread;

/**
 *
 * @author Gavaga
 */
public class Shooter extends Thread
{
    private static final double DEFAULT_FIRE_DISTANCE = 0;
    
    private static final double DEFAULT_RAMP_TIME = 0.25;
    private static final double DEFAULT_TIME_STEP = 0.01;
    private static final int DEFAULT_RAMP_TYPE = 0;
    
    private static boolean is_firing;
    
    private static CANJaguar shooter_jag_1;
    private static CANJaguar shooter_jag_2;
    private static Solenoid winch_release;
    
    public Shooter()
    {
        try {
            shooter_jag_1 = new CANJaguar(RobotMap.SHOOTER_JAGUAR_PORT_1);
            shooter_jag_2 = new CANJaguar(RobotMap.SHOOTER_JAGUAR_PORT_2);
        }
        catch (CANTimeoutException ex)
        {
            ex.printStackTrace();
        }
        is_firing = false;
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
    
    public synchronized void fire()
    {
        if (is_firing)
        {
            return;
        }
        fire(DEFAULT_RAMP_TIME, DEFAULT_TIME_STEP, DEFAULT_RAMP_TYPE);
    }
    
    public synchronized void fire(double ramp_time, double time_step, int ramp_type)
    {
        if (is_firing)
        {
            return;
        }
        
        is_firing = true;
        long start_time = System.currentTimeMillis();
        
        final int lps = (int)(1000*time_step);
        
        double dx = time_step / ramp_time;
        
        for (int i = 0; i*dx != 0; ++i)
        {
            set_shooter_speed(i*dx);
            try 
            {
                Thread.sleep((start_time + (i+1)*lps) - System.currentTimeMillis());
            } 
            catch (InterruptedException ex) 
            {
                ex.printStackTrace();
            }
        }
        is_firing = false;
    }
    public void run() 
    {
        
    }
}
