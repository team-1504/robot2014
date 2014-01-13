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
    private static double wheel_front_left, wheel_back_left, wheel_back_right, wheel_front_right;

    public static void drive_mecanum(double vx, double vy, double w)
    {
        double[] wheels = new double[4];
        
        wheels[0] = vx + vy - w;
        wheels[1] = vx - vy - w;
        wheels[2] = vx + vy + w;
        wheels[3] = vx - vy + w;
        
        double max = Math.max(1.0, Math.abs(vx) + Math.abs(vy) + Math.abs(w));
        
        wheel_front_left = wheels[0]/max;
        wheel_back_left = wheels[1]/max;
        wheel_back_right = wheels[2]/max;
        wheel_front_right = wheels[3]/max;
    }
    
    public static double get_front_left()
    {
        return wheel_front_left;
    }    
    public static double get_back_left()
    {
        return wheel_back_left;
    }    
    public static double get_back_right()
    {
        return wheel_back_right;
    }
    public static double get_front_right()
    {
        return wheel_front_right;
    }
}
