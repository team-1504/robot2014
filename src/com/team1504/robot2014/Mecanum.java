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
 * @author Lexie
 */
public class Mecanum  
{            
    private double front_left_val;
    private double back_left_val;
    private double front_right_val;
    private double back_right_val;
    
    public void drive_mecanum(double forward, double right, double counter_clockwise)
    {
        double max = Math.max(1.0, Math.abs(forward) + Math.abs(right) + Math.abs(counter_clockwise));
//        System.out.println(max);    
        
//        System.out.println(forward + " " + right + " " + counter_clockwise);
        
        front_left_val = ((forward + right - counter_clockwise) * RobotMap.FRONT_LEFT_MAGIC_NUMBER / max);        
        back_left_val = ((forward - right - counter_clockwise) * RobotMap.BACK_LEFT_MAGIC_NUMBER / max);
        back_right_val = ((forward + right + counter_clockwise) * RobotMap.BACK_RIGHT_MAGIC_NUMBER / max);
        front_right_val = ((forward - right + counter_clockwise) * RobotMap.FRONT_RIGHT_MAGIC_NUMBER / max);
        
        System.out.println(front_left_val + " " + back_left_val + " " + back_right_val + " " + front_right_val);
    }
    public double get_front_left()
    {
       return front_left_val;
    }
    public double get_back_left()
    {
        return back_left_val;
    }
    public double get_back_right()
    {
        return back_right_val;
    }
    public double get_front_right()
    {
        return front_right_val;
    }

    
    public void tankcanum_drive(double left_y, double left_x, double right_y, double right_x)
    {
       // double ccw = (right_y - left_y)/2;
       // double forward = (left_y + right_y)/2;
       // double right = (left_x + right_x)/2;
       // mecanum_drive(forward, right, ccw);
        
        double max = Math.max(1.0, Math.abs(left_y) + Math.abs(left_x) + Math.abs((right_y - left_y)/2));
       
        front_left_val = ((left_y + left_x + ((right_y - left_y)/2)) / max);
        back_left_val = ((left_y - left_x - ((right_y - left_y)/2)) / max);
        back_right_val = (((left_y - left_x + ((right_y - left_y)/2)) * -1) / max);
        front_right_val = (((left_y + left_x + ((right_y - left_y)/2)) * -1) / max);
        
        

        //double first = Math.max(Math.abs(fl_setx), Math.abs(bl_setx));
        //double second = Math.max(Math.abs(fr_setx), Math.abs(br_setx));
       // double max = Math.max(Math.abs(first), Math.abs(second));
        
   }
}
