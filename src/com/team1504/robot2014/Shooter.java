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
    
    private static final double RAMP_STEP = 0.1;
    
    private static boolean is_firing;
    
    private static CANJaguar shooter_jag_1;
    private static CANJaguar shooter_jag_2;
    
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
    
    public synchronized void stop_firing()
    {
        is_firing = false;
    }
    
    public synchronized void fire()
    {
        if (is_firing)
        {
            return;
        }
        ramp_and_run();
    }
    
    public synchronized void ramp_and_run()
    {        
        is_firing = true;
        long last_loop_time = System.currentTimeMillis();
        
        int value = 0;
        
        while(is_firing)
        {
            set_shooter_speed((value++)*RAMP_STEP);
        }
    }
    public void run() 
    {
        
    }
}
