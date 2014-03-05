/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.team1504.robot2014;

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
    private static final int DEFAULT_RAMP_TIME = 50;
    
    private boolean is_firing;
    private boolean enabled;
    private boolean manual;
    
    private int ramp_time;
    
    private double stop_angle;
    private double max_speed;
    
    private CANJaguar shooter_jag_1;
    private CANJaguar shooter_jag_2;
    
    private double shooter_angle;
    private double manual_value;
    
    private ShooterThread sh_thread;
    
    private Solenoid latch_extend_sol;
    private Solenoid latch_retract_sol;
    private boolean solenoid = true;
    
    public Shooter()
    {
        try 
        {
            shooter_jag_1 = new CANJaguar(RobotMap.SHOOTER_JAGUAR_PORT_1);
            shooter_jag_2 = new CANJaguar(RobotMap.SHOOTER_JAGUAR_PORT_2);
            
            shooter_jag_2.setPositionReference(CANJaguar.PositionReference.kPotentiometer);
            shooter_jag_2.configPotentiometerTurns(10);
        }
        catch (CANTimeoutException ex)
        {
            ex.printStackTrace();
        }
        latch_extend_sol = new Solenoid(RobotMap.LATCH_EXTEND_PORT);
        latch_retract_sol = new Solenoid(RobotMap.LATCH_RETRACT_PORT);
        
//        pot = new AnalogChannel(RobotMap.SHOOTER_POT_MODULE_NUM);
        stop_angle = RobotMap.SHOOTER_POT_RELEASE_VAL_GOAL;
        
        is_firing = false;        
        manual = false;
        ramp_time = DEFAULT_RAMP_TIME;        
        sh_thread = new ShooterThread(this);
    }
    
    public void reset()
    {
        enabled = true;
        
        sh_thread = new ShooterThread(this);
    }
    
    public void enable()
    {
        enabled = true;  
    }
    
    public void start()
    {
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
    
    public void enable_manual()
    {
        is_firing = false;
        manual = true;
        set_latch(false);
    }
    
    public void disable_manual()
    {
        manual = false;
    }
    
    public void write_manual(double value)
    {
        manual_value = value;
    }
    
    public void fire(boolean firing, int type)
    {
        if (type == 0)
        {
            stop_angle = RobotMap.SHOOTER_POT_RELEASE_VAL_GOAL;
            max_speed = RobotMap.SHOOTER_GOAL_SPEED_MAX;
        }
        else if (type == 1)
        {
            stop_angle = RobotMap.SHOOTER_POT_RELEASE_VAL_TOSS;
            max_speed = RobotMap.SHOOTER_TOSS_SPEED_MAX;
        }
        if (firing)
        {
            set_latch(false);
        }
        fire(firing);
    }
    
    public void fire(boolean firing)
    {
        is_firing = firing;
    }
    
    private void set_latch(boolean latch_down)
    {
        latch_extend_sol.set(latch_down);
        latch_retract_sol.set(!latch_down);
    }
    
    public void set_max_speed(double speed)
    {
        max_speed = speed;
    }
    
    public double get_angle()
    {
        return shooter_angle;
    }
    
    public double get_shooter_speed()
    {
        double speed = 0;
        try {
            speed = shooter_jag_1.getSpeed();
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }
        return speed;
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
            while(enabled)
            {
//                System.out.println("Shooter thread running");
                long last_loop_time = System.currentTimeMillis();

                double value = 0;
                update_angle();
                
                if (manual)
                {
                    set_shooter_speed(manual_value);
                }
                
                while((Math.abs(shooter_angle - (stop_angle)) > RobotMap.SHOOTER_ANGLE_TOLERANCE && shooter_angle > stop_angle) && sh.is_firing())
                {
                    if (Math.abs(shooter_angle - stop_angle) < 0.3)
                    {
                        max_speed = 1.0;
                    }
                    update_angle();
//                    System.out.println("S: is firing -- " + value);
                    solenoid = false;
                    set_latch(solenoid);
                    value += ((double)(System.currentTimeMillis() - last_loop_time)) / ramp_time;
                    last_loop_time = System.currentTimeMillis();
                    set_shooter_speed(( value >= max_speed) ? max_speed: value );
                    just_fired = true;
                }
                if (just_fired || (sh.is_firing() && shooter_angle < stop_angle))
                {                    
                    reset_shooter();
                    just_fired = false;
                }
                sh.fire(false);
            }
        }
        
        private void update_angle()
        {
            double pos = 0;
            try 
            {
                pos = shooter_jag_2.getPosition();
            } 
            catch (CANTimeoutException ex) 
            {
                ex.printStackTrace();
            }
            shooter_angle = pos;
        }
        
        private void reset_shooter()
        {
            long start_time = System.currentTimeMillis();
            while (Math.abs(shooter_angle - RobotMap.SHOOTER_POT_BASE_VAL) > RobotMap.SHOOTER_ANGLE_TOLERANCE && Math.abs(start_time - System.currentTimeMillis()) < 1000)
            {
                update_angle();
                if (shooter_angle == 0)
                {
                    set_shooter_speed(-0.2);
                }
                else if (Math.abs(shooter_angle - RobotMap.SHOOTER_POT_RELEASE_VAL_GOAL) < 0.1 )
                {
                    set_shooter_speed(-0.6);
                }
                else
                {
                    set_shooter_speed(-0.3);
                }
            }
            set_latch(true);
            set_shooter_speed(0.0);
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
