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
    private double[] wheels;                 //0 3
                                             //1 2
    private double[] magic_numbers;

    public Mecanum(double m_fl, double m_bl, double m_br, double m_fr)
    {
        magic_numbers[0] = m_fl;
        magic_numbers[1] = m_bl;
        magic_numbers[2] = m_br;
        magic_numbers[3] = m_fr;
    }
    
    public void drive_mecanum(double forward, double right, double counter_clockwise)
    {        
        double max = Math.max(1.0, Math.abs(forward) + Math.abs(right) + Math.abs(counter_clockwise));
        
        wheels[0] = magic_numbers[0]*(forward + right - counter_clockwise)/max;
        wheels[1] = magic_numbers[1]*(forward - right - counter_clockwise)/max;
        wheels[2] = magic_numbers[2]*(forward + right + counter_clockwise)/max;
        wheels[3] = magic_numbers[3]*(forward - right + counter_clockwise)/max;
    }
    
    public double get_front_left()
    {
        return wheels[0];
    }    
    public double get_back_left()
    {
        return wheels[1];
    }    
    public double get_back_right()
    {
        return wheels[2];
    }
    public double get_front_right()
    {
        return wheels[3];
    }
}
