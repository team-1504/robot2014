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
    private static final int DEFAULT_RAMP_TIME = 250;
    private static final double DEFAULT_ANGLE = 0.7;
    private static final double POT_RANGE = 90;
    
    private boolean is_firing;
    private boolean enabled;
    
    private int ramp_time;
    
    private double stop_angle;
    
    private CANJaguar shooter_jag_1;
    private CANJaguar shooter_jag_2;
    
    private AnalogChannel pot;
    
    private ShooterThread sh_thread;
    
    private Solenoid solenoid_1 = RobotMain.pickup_sol_ex_2;
    private Solenoid solenoid_2 = RobotMain.pickup_sol_ret_2;
    private boolean solenoid = true;
    
    public Shooter()
    {
        try 
        {
            shooter_jag_1 = new CANJaguar(RobotMap.SHOOTER_JAGUAR_PORT_1);
            shooter_jag_2 = new CANJaguar(RobotMap.SHOOTER_JAGUAR_PORT_2);
            
            shooter_jag_1.setPositionReference(CANJaguar.PositionReference.kPotentiometer);
            shooter_jag_1.configPotentiometerTurns(2);
        }
        catch (CANTimeoutException ex)
        {
            ex.printStackTrace();
        }
//        pot = new AnalogChannel(RobotMap.SHOOTER_POT_MODULE_NUM);
        stop_angle = DEFAULT_ANGLE;
        
//        solenoid_set(solenoid);
        
        is_firing = false;
        
        ramp_time = DEFAULT_RAMP_TIME;
        
        System.out.println("Shooter starting");
        sh_thread = new ShooterThread(this);
    }
    
    private void set_latch(boolean latch_down)
    {
        solenoid_1.set(latch_down);
        solenoid_2.set(!latch_down);
    }
    
    public void enable()
    {
        enabled = true;
        sh_thread.start();   
    }
    
    public void disable()
    {
        enabled = false;
    }

    public boolean is_enabled()
    {
        return enabled;
    }
            
    public boolean is_firing()
    {
        return is_firing;
    }
    
    public void fire(boolean firing)
    {
        is_firing = firing;
    }
    
    private class ShooterThread extends Thread
    {
        private Shooter sh;
        public ShooterThread(Shooter sh)
        {
            this.sh = sh;
        }
        public void run() 
        {
            boolean just_fired = false;
            while(true)
            {
//                System.out.println("Shooter thread running");
                long last_loop_time = System.currentTimeMillis();

                double value = 0;
                
                if (is_firing)
                {
                    set_latch(true);
                }

                while(sh.is_firing() )//&& Math.abs(get_angle() - stop_angle) > RobotMap.SHOOTER_ANGLE_TOLERANCE)
                {
//                    System.out.println("S: is firing -- " + value);
                    solenoid = false;
                    set_latch(solenoid);
                    value += ((double)(System.currentTimeMillis() - last_loop_time)) / ramp_time;
                    last_loop_time = System.currentTimeMillis();
                    set_shooter_speed(( value >= 1) ? 1: value );
                    just_fired = true;
                }
                if (just_fired)
                {
                    reset_shooter();
                    just_fired = false;
                }
            }
        }
        
        private double get_angle()
        {
//            double volts = ((pot.getLSBWeight() * 1e-9) * pot.getVoltage()) - (pot.getOffset() * 1e-9);
//            return (volts + 10) * (POT_RANGE / 20.);
            double pos = 0;
            try {
                pos = shooter_jag_1.getPosition();
            } catch (CANTimeoutException ex) {
                ex.printStackTrace();
            }
            return pos;
        }
        
        private void reset_shooter()
        {
            for (;Math.abs(get_angle() - RobotMap.SHOOTER_POT_BASE_VAL) > RobotMap.SHOOTER_ANGLE_TOLERANCE;)
            {
                set_shooter_speed(-0.1);
            }
            set_shooter_speed(0.0);
            set_latch(true);
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
