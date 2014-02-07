/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.team1504.robot2014;

import edu.wpi.first.wpilibj.DigitalModule;
import edu.wpi.first.wpilibj.I2C;

/**
 *
 * @author Team 1504
 */
public class I2CPotentiometer
{
    private final int BUF_SIZE = 8;
    private final int TRANS_SIZE = 8;
    
    private I2C i2c;
    private int i2c_addr;
    
    public I2CPotentiometer(int mod_num, int addr)
    {
        i2c_addr = addr;
        i2c = (DigitalModule.getInstance(mod_num)).getI2C(addr);
    }
    
    public double get_angle()
    {
        byte[] buf = new byte[BUF_SIZE];
        i2c.read(i2c_addr, TRANS_SIZE, buf);
        double raw = Utils.bytes_to_double(buf);
        return convert_to_angle(raw);
    }
    
    public double convert_to_angle(double raw)
    {
        return raw;
    }
}
