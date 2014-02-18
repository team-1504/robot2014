/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.team1504.robot2014;

import com.sun.squawk.util.MathUtils;


/**
 *
 * @author Eashwie/Lexar
 */
public class Mecanum  
{            
    private double front_left_val;
    private double back_left_val;
    private double front_right_val;
    private double back_right_val;
    double mult_correction;
    double rotation_offset;
    double[] orbit_offset = new double[2];
    
    public Mecanum()
    {
        mult_correction = 0.25;
    }
    
    public void set_front(double rot_offset)
    {
       rotation_offset = rot_offset*Math.PI/180;
    }
    
    public void set_center_point(double[] center)
    {
        orbit_offset = center;
    }
    
    public void set_multiplicative_correction(double mult_correction)
    {
        this.mult_correction = mult_correction;
    }
    
    public double[] detents(double[] dircn)
    {
        double theta = MathUtils.atan2(dircn[0], dircn[1]);
        double dx = correct_x(theta) * distance(dircn[1], dircn[0]) * mult_correction;
        double dy = correct_y(theta) * distance(dircn[1], dircn[0]) * mult_correction;
        
        dircn[0] = MathUtils.pow(dircn[0], 3) + dy;
        dircn[1] = MathUtils.pow(dircn[1], 3) + dx;
        
        System.out.println("detented vals: " + dircn[0] + " " + dircn[1]);
        
        return dircn;
    }
    
    public double[] front_side(double[] dircn)
    {
        double[] dir_offset = new double[3];
        dir_offset[0] = dircn[0] * Math.cos(rotation_offset) + dircn[1] * Math.sin(rotation_offset);
        dir_offset[1] = dircn[1] * Math.cos(rotation_offset) - dircn[0] * Math.sin(rotation_offset);
        dir_offset[2] = dircn[2];
        return dir_offset;
    }
        
    public double[] orbit_point(double[] dircn)
    {
        double x = orbit_offset[0];
        double y = orbit_offset[1];
        double forward = dircn[0];
        double right = dircn[1];
        double ccw = dircn[2];
        
        double p = Math.sqrt(((MathUtils.pow(y-1, 2) + (MathUtils.pow(1-x, 2))/Math.sqrt(2)))) * Math.cos(45*Math.PI/180 + MathUtils.atan2(y-1, 1-x));
        double r = Math.sqrt(((MathUtils.pow(y+1, 2) + (MathUtils.pow(1-x, 2))/Math.sqrt(2)))) * Math.cos(-45*Math.PI/180 + MathUtils.atan2(y+1, 1-x));
        double q = -Math.sqrt(((MathUtils.pow(y+1, 2) + (MathUtils.pow(-1-x, 2))/Math.sqrt(2)))) * Math.cos(45*Math.PI/180 + MathUtils.atan2(y+1, -1-x));
        
        double new_forward = (ccw * r + (forward - ccw) * q + forward * p)/(q + p);
        double new_right = (-ccw * r + right * q - (-right-ccw) * p)/(q + p);
        double new_ccw = (2 * ccw)/(q+p);
        
        double[] directions = new double[3];
        directions[0] = new_forward;
        directions[1] = new_right;
        directions[2] = new_ccw;
        
        return directions;
    }

    public void drive_mecanum(double[] directions)
    {  
        double[] dircns;
        
//        dircns = detents(directions);
        dircns = front_side(directions);
//        directions = orbit_point(dircns);
        
        double forward = dircns[0];
        double right = dircns[1];
        double ccw = dircns[2];
        
        double max = Math.max(1.0, Math.abs(forward) + Math.abs(right) + Math.abs(ccw));

        
        front_left_val = ((forward + right - ccw) * RobotMap.FRONT_LEFT_MAGIC_NUMBER / max);        
        back_left_val = ((forward - right - ccw) * RobotMap.BACK_LEFT_MAGIC_NUMBER / max);
        back_right_val = ((forward + right + ccw) * RobotMap.BACK_RIGHT_MAGIC_NUMBER / max);
        front_right_val = ((forward - right + ccw) * RobotMap.FRONT_RIGHT_MAGIC_NUMBER / max);
    }

    public double get_front_left(){return front_left_val;}
    public double get_back_left(){return back_left_val;}
    public double get_back_right(){return back_right_val;}
    public double get_front_right(){return front_right_val;}
    
    public static double distance(double x, double y){return Math.sqrt(x*x + y*y);}
    private double correct_x(double theta){return -Math.sin(theta) * (-Math.sin(8*theta) - 0.25 * Math.sin(4*theta));}
    private double correct_y(double theta){return Math.cos(theta) * (-Math.sin(8*theta) - 0.25 * Math.sin(4*theta));}
    
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
