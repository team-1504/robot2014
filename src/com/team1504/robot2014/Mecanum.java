/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.team1504.robot2014;

import com.sun.squawk.util.MathUtils;


/**
 *
 * @author Eashwar
 */
public class Mecanum  
{            
    private double front_left_val;
    private double back_left_val;
    private double front_right_val;
    private double back_right_val;
    double correct_multiplier;
    double rotation_offset;
    double[] orbit_offset = new double[2];
    
    public Mecanum()
    {
        correct_multiplier = 1;
        
    }
    public void front_rotation(double rot_offset)
    {
       rotation_offset = rot_offset*Math.PI/180;
    }
    
    public void orbit_point(double[] directions, double[] offset)
    {
        orbit_offset = new double[2];
    }
    
    public double[] detent(double[] directions)
    {
        double theta = MathUtils.atan2(directions[0], directions[1]); //forward is [0]; right is [1]
        double dx = correct_x(theta) * distance(directions[1], directions[0]) * correct_multiplier;
        double dy = correct_y(theta) * distance(directions[1], directions[0]) * correct_multiplier;
        
        directions[0] = MathUtils.pow(directions[0], 3) + dy;
        directions[1] = MathUtils.pow(directions[1], 3) + dx;
        return directions;
    }
    
    private double[] front_side(double[] direct, double rotation_offset)
    {
        double[] new_direction = new double[3];
        new_direction[0] = direct[0] * Math.cos(rotation_offset) + direct[1] * Math.sin(rotation_offset);
        new_direction[1] = direct[1] * Math.cos(rotation_offset) - direct[0] * Math.sin(rotation_offset);
        new_direction[2] = direct[2];
        return new_direction;
    }

    public void drive_mecanum(double[] directions)
    {
        double forward = directions[0];
        double right = directions[1];
        double ccw = directions[2];
           
        double max = Math.max(1.0, Math.abs(forward) + Math.abs(right) + Math.abs(ccw));
//        System.out.println(max);    

//        System.out.println(forward + " " + right + " " + counter_clockwise);
        directions = detents(directions);
        directions = front_side(directions);
        
        directions = orbit_point(directions);
        
        front_left_val = ((forward + right - ccw) * RobotMap.FRONT_LEFT_MAGIC_NUMBER / max);        
        back_left_val = ((forward - right - ccw) * RobotMap.BACK_LEFT_MAGIC_NUMBER / max);
        back_right_val = ((forward + right + ccw) * RobotMap.BACK_RIGHT_MAGIC_NUMBER / max);
        front_right_val = ((forward - right + ccw) * RobotMap.FRONT_RIGHT_MAGIC_NUMBER / max);
        
//        System.out.println(front_left_val + " " + back_left_val + " " + back_right_val + " " + front_right_val);
    }
    
    public double[] detents(double[] dircn)
    {
        double theta = MathUtils.atan2(dircn[0], dircn[1]);
        double dx = correct_x(theta) * distance(dircn[1], dircn[0]) * correct_multiplier;
        double dy = correct_y(theta) * distance(dircn[1], dircn[0]) * correct_multiplier;
        
        dircn[0] = MathUtils.pow(dircn[0], 3) + dy;
        dircn[1] = MathUtils.pow(dircn[1], 3) + dx;
        
        return dircn;

    }
    public double[] front_side(double[] dircn)
    {
        double[] dir_offset = new double[3];
        dir_offset[0] = dircn[0] * Math.cos(rotation_offset) + dircn[1] * Math.sin(rotation_offset);
        dir_offset[1] = dircn[1] * Math.cos(rotation_offset) + dircn[0] * Math.sin(rotation_offset);
        dir_offset[2] = dircn[2];
        return dir_offset;
    }
    
    public double[] orbit_point(double[] dircn)
    {
        //do the thing guise
        return dircn;
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
//    public void tankcanum_drive(double left_y, double left_x, double right_y, double right_x)
//    {
//       // double ccw = (right_y - left_y)/2;
//       // double forward = (left_y + right_y)/2;
//       // double right = (left_x + right_x)/2;
//       // mecanum_drive(forward, right, ccw);
//        
//        double max = Math.max(1.0, Math.abs(left_y) + Math.abs(left_x) + Math.abs((right_y - left_y)/2));
//       
//        front_left_val = ((left_y + left_x + ((right_y - left_y)/2)) / max);
//        back_left_val = ((left_y - left_x - ((right_y - left_y)/2)) / max);
//        back_right_val = (((left_y - left_x + ((right_y - left_y)/2)) * -1) / max);
//        front_right_val = (((left_y + left_x + ((right_y - left_y)/2)) * -1) / max);
//        
//        
//
//        //double first = Math.max(Math.abs(fl_setx), Math.abs(bl_setx));
//        //double second = Math.max(Math.abs(fr_setx), Math.abs(br_setx));
//       // double max = Math.max(Math.abs(first), Math.abs(second));
//        
//   }
}
