/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.team1504.robot2014;

import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.DigitalModule;
import edu.wpi.first.wpilibj.I2C;

/**
 *
 * @author Gavaga
 */
public class PiComModule
{    
    private int out_ct_I2C;
    private int out_ct_PWM;
    private int out_ct_analog;
    private int out_ct_digital;
    
    private int in_ct_I2C;
    private int in_ct_PWM;
    private int in_ct_analog;
    private int in_ct_digital;
    
    private I2C i2c;
    
    private AnalogChannel[] analog_channels_in;
    private AnalogChannel[] analog_channels_out;
    
    private DigitalModule rpi_module;
    
    public PiComModule(int module_index, int I2C_out, int PWM_out, int analog_out, int digital_out, int I2C_in, int PWM_in, int analog_in, int digital_in)
    {
        rpi_module = DigitalModule.getInstance(module_index);
        
        i2c = rpi_module.getI2C(RobotMap.RPI_I2C_ADDRESS);
        
        out_ct_I2C = I2C_out;
        out_ct_PWM = PWM_out;
        out_ct_analog = analog_out;
        out_ct_digital = digital_out;
        
        in_ct_I2C = I2C_in;
        in_ct_PWM = PWM_in;
        in_ct_analog = analog_in;
        in_ct_digital = digital_in;
        
        i2c = rpi_module.getI2C(RobotMap.RPI_I2C_ADDRESS);
        
        //TODO: Setup PWM initial stuff
              
        //initialize analog in channels
        analog_channels_out = new AnalogChannel[in_ct_analog];
        for (int analog_out_index = 0; analog_out_index < analog_channels_out.length; ++analog_out_index)
        {
            analog_channels_out[analog_out_index] = new AnalogChannel(RobotMap.RPI_GPIO_CHANNELS[analog_out_index]);
        }        
        //initialize analog out channels
        analog_channels_in = new AnalogChannel[out_ct_analog];
        for (int analog_in_index = 0; analog_in_index < analog_channels_in.length; ++analog_in_index)
        {
            analog_channels_in[analog_in_index] = new AnalogChannel(RobotMap.RPI_GPIO_CHANNELS[in_ct_analog + analog_in_index]);
        }
        
        //allocate digital out channels
        for (int i = 0; i < out_ct_digital; ++i)
        {
            rpi_module.allocateDIO(RobotMap.RPI_GPIO_CHANNELS[get_in_analog_count() + get_out_analog_count() + i], false);            
        }
        //allocate digital in channels
        for (int i = 0; i < in_ct_digital; ++i)
        {
            rpi_module.allocateDIO(RobotMap.RPI_GPIO_CHANNELS[get_in_analog_count() + get_out_analog_count() + get_out_digital_count() + i], true);
        }
    }
    
    public int get_out_channel_count()
    {
        return out_ct_I2C + out_ct_PWM + out_ct_analog + out_ct_digital;
    }    
    public int get_out_I2C_count()
    {
        return out_ct_I2C;
    }
    public int get_out_PWM_count()
    {
        return out_ct_PWM;
    }    
    public int get_out_analog_count()
    {
        return out_ct_analog;
    }
    public int get_out_digital_count()
    {
        return out_ct_digital;
    }    
    
    public int get_in_channel_count()
    {
        return in_ct_I2C + in_ct_PWM + in_ct_analog + in_ct_digital;
    }
    public int get_in_I2C_count()
    {
        return in_ct_I2C;
    }
    public int get_in_PWM_count()
    {
        return in_ct_PWM;
    }    
    public int get_in_analog_count()
    {
        return in_ct_analog;
    }
    public int get_in_digital_count()
    {
        return in_ct_digital;
    }
    
    public double read_I2C(int local_channel_ref)
    {
        //TODO: Actually implement
        return 0;
    }
    public double read_PWM(int local_channel_ref)
    {
        return rpi_module.getPWM(RobotMap.RPI_PWM_CHANNELS[local_channel_ref]);
    }
    public double read_PWM(int local_channel_ref, double bot, double top)
    {
        return (bot + (top-bot)*((double)read_PWM(local_channel_ref)/255.));        
    }
    public double read_analog(int local_channel_ref)
    {
        return ((analog_channels_in[local_channel_ref].getLSBWeight() * 1e-9) * analog_channels_in[local_channel_ref].getVoltage()) - (analog_channels_in[local_channel_ref].getOffset() * 1e-9);
    }
    public double read_analog(int local_channel_ref, double bot, double top)
    {
        double value = read_analog(local_channel_ref);
        double conv = bot + ((value + 10.) / 20.)*(top-bot);
        return conv;
    }
    public boolean read_digital(int local_channel_ref)
    {
        return rpi_module.getDIO(RobotMap.RPI_GPIO_CHANNELS[get_in_analog_count() + get_out_analog_count() + get_out_digital_count() + local_channel_ref]);
    }
    
    public void write_I2C(int local_channel_ref, double value)
    {
        //TODO: Actually Implement
    }
    public void write_PWM(int local_channel_ref, int value)
    {
        rpi_module.setPWM(RobotMap.RPI_PWM_CHANNELS[local_channel_ref], value);
    }
    public void write_PWM(int local_channel_ref, double value, double bot, double top)
    {
        int val = (int)(255*((value - bot)/(top-bot)));
        write_PWM(local_channel_ref, val);
    }
    public void write_analog(int local_channel_ref, double value)
    {
        //TODO: Actually Implement              
    }
    public void write_digital(int local_channel_ref, boolean val)
    {
        rpi_module.setDIO(local_channel_ref, val);
    }
}
