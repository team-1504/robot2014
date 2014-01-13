/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.team1504.robot2014;

import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalModule;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.Timer;

/**
 *
 * @author Gavaga
 * 
 * Thread watching PWM Communications with the Raspberry Pi
 * Continuously writing current send values, and keeping updated
 * variables with values read from the pi.
 * 
 */
public class PiSignaler implements Runnable
{
    private static PiComModule rpi_module;
    
    private static volatile Object[] packet_in;
    private static volatile Object[] packet_out;
    
    public void run() 
    {
        while(Thread.currentThread().isAlive())
        {
            update_in_packet();
            
            for (int i = 0; i < packet_out.length; ++i)
            {
                if (i < rpi_module.get_out_I2C_count())
                {
                    rpi_module.write_I2C(i, ((Double)packet_out[i]).doubleValue() );
                }
                else if (i < rpi_module.get_out_I2C_count() + rpi_module.get_out_PWM_count())
                {
                    rpi_module.write_PWM(i - rpi_module.get_out_I2C_count(), ((Integer)packet_out[i]).intValue());
                }
                else if (i < rpi_module.get_out_I2C_count() + rpi_module.get_out_PWM_count() + rpi_module.get_out_analog_count())
                {
                    rpi_module.write_analog(i - rpi_module.get_out_I2C_count() - rpi_module.get_out_PWM_count(), ((Double)packet_out[i]).doubleValue());
                }
                else
                {
                    rpi_module.write_digital(rpi_module.get_out_channel_count() - rpi_module.get_out_digital_count() + i, ((Boolean)packet_out[i]).booleanValue());
                }
            }                    
                    
            try
            {
                Thread.currentThread().sleep(25);
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
            }
        }       
    }
    
    public PiSignaler(PiComModule rpi)
    {
        rpi_module = rpi;
        packet_in = new Object[rpi_module.get_in_channel_count()];
        packet_out = new Object[rpi_module.get_out_channel_count()];
    }
    
    //return command packet to main thread. 
    //Double vx, Double vy, Double omega, Double shoot_dist, Double pass_speed, Boolean shoot, Boolean suck, Boolean pass 
    public void update_in_packet()
    {        
        for (int i = 0; i < rpi_module.get_in_I2C_count(); ++i)
        {
            packet_in[i] = new Double(0); //TODO: Implement          
        }
        for (int p = 0; p < rpi_module.get_in_PWM_count(); ++p)
        {
            packet_in[rpi_module.get_in_I2C_count() + p] = new Double(rpi_module.read_PWM(p));
        }
        for (int a = 0; a < rpi_module.get_in_analog_count(); ++a)
        {
            packet_in[rpi_module.get_in_I2C_count() + rpi_module.get_in_PWM_count() + a] = new Double(rpi_module.read_analog(a));
        }
        for (int d = 0; d < rpi_module.get_in_digital_count(); ++d)
        {
            packet_in[rpi_module.get_in_I2C_count() + rpi_module.get_in_PWM_count() + rpi_module.get_in_analog_count() + d] = new Boolean(rpi_module.read_digital(d));
        }        
    }   
    
    public synchronized Object[] get_packet_in()
    {
        return packet_in;
    }
    
    public synchronized void update_out_packet(Object[] packet)
    {
        packet_out = packet;
    }
}
