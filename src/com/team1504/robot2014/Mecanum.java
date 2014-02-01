/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.team1504.robot2014;

import com.sun.squawk.util.MathUtils;


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
    double correct_multiplier;
    double rotation_offset;
    
    public Mecanum()
    {
        correct_multiplier = 1;
        
    }
    
    public void front_rotation(double rot_offset)
    {
       rotation_offset = rot_offset;
    }
    
    public void drive_mecanum(double[] directions)
    {
        double forward = directions[0];
        double right = directions[1];
        double ccw = directions[2];
        
        double theta = MathUtils.atan2(forward, right);
        double dx = correct_x(theta) * distance(right, forward) * correct_multiplier;
        double dy = correct_y(theta) * distance(right, forward) * correct_multiplier;
        
        double forward_sans_offset = MathUtils.pow(forward, 3) + dy;
        double right_sans_offset = MathUtils.pow(right, 3) + dx;
        
        double max = Math.max(1.0, Math.abs(forward) + Math.abs(right) + Math.abs(ccw));
//        System.out.println(max);    
        
//        System.out.println(forward + " " + right + " " + counter_clockwise);
        forward = forward_sans_offset * Math.cos(rotation_offset) + right * Math.sin(rotation_offset);
        right = right_sans_offset * Math.cos(rotation_offset) - forward * Math.sin(rotation_offset);
        
        front_left_val = ((forward + right - ccw) * RobotMap.FRONT_LEFT_MAGIC_NUMBER / max);        
        back_left_val = ((forward - right - ccw) * RobotMap.BACK_LEFT_MAGIC_NUMBER / max);
        back_right_val = ((forward + right + ccw) * RobotMap.BACK_RIGHT_MAGIC_NUMBER / max);
        front_right_val = ((forward - right + ccw) * RobotMap.FRONT_RIGHT_MAGIC_NUMBER / max);
        
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
    public void set_correct_multiplier(double correct_multiplier)
    {
        this.correct_multiplier = correct_multiplier;
    }
    
    public static double distance(double x, double y)
    {
        return Math.sqrt(x*x + y*y);
    }
    private double correct_x(double theta)
    {
       return -Math.sin(theta) * (-Math.sin(8*theta) - 0.25 * Math.sin(4*theta)); 
    }
    private double correct_y(double theta)
    {
       return Math.cos(theta) * (-Math.sin(8*theta) - 0.25 * Math.sin(4*theta)); 
    }
}
