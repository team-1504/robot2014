/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.team1504.robot2014;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.can.CANTimeoutException;

/**
 *
 * @author Gavaga
 */
public class Mecanum 
{
    CANJaguar wheel_front_left, wheel_back_left, wheel_back_right, wheel_front_right;
    public Mecanum(CANJaguar fl, CANJaguar bl, CANJaguar br, CANJaguar fr)
    {
        wheel_front_left = fl;
        wheel_back_left = bl;
        wheel_back_right = br;
        wheel_front_right = fr;
        
        //0 3
        //1 2
    }
    public void driveMecanum(double vx, double vy, double w)
    {
        double[] wheels = new double[4];
        wheels[0] = vx + vy - w;
        wheels[1] = vx - vy - w;
        wheels[2] = vx + vy + w;
        wheels[3] = vx - vy + w;
        
        double max = 0;
        for (int i = 0; i < 4; ++i)
        {
            if (Math.abs(wheels[i]) > max)
            {
                max = wheels[i];
            }
        }
        try
        {
            wheel_front_left.setX(wheels[0]);
            wheel_back_left.setX(wheels[1]);
            wheel_back_right.setX(wheels[2]);
            wheel_front_right.setX(wheels[3]);
        }
        catch (CANTimeoutException e)
        {
            System.out.println(e);
        }
    }
}
