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
    
    private boolean is_detented;
    private boolean is_orbiting;
    
    private double mult_correction;
    private double rotation_offset;
    private double[] orbit_offset = new double[2];
    
    public Mecanum()
    {
        is_detented = true;
        is_orbiting = true;
        mult_correction = 0.25;
    }
    
    public void is_orbiting(boolean orb)
    {
        is_orbiting = orb;
    }
    
    public void is_detented(boolean det)
    {
        is_detented = det;
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
    
    public double[] null_zone(double[] dircn, double size)
    {
        double[] null_zone = new double[3];
        for (int i = 0; i < 3; ++i)
        {
            null_zone[i] = Math.abs(dircn[i]) < size ? 0: dircn[i];
        }
        return null_zone;
    }
    
    public double[] detents(double[] dircn)
    {
        double speed = Utils.distance(dircn[0], dircn[1]);
        double theta = MathUtils.atan2(dircn[0], dircn[1]);
        double theta_n = ( (int)(8.0 * (theta / (2.0 * Math.PI)) + 0.5)) * (Math.PI / 4.0);
//        double dx = correct_x(theta) * distance(dircn[1], dircn[0]) * mult_correction;
//        double dy = correct_y(theta) * distance(dircn[1], dircn[0]) * mult_correction;
//        
        double[] detented = new double[3];
        
//        detented[0] = MathUtils.pow(dircn[0], 3) + dy;
//        detented[1] = MathUtils.pow(dircn[1], 3) + dx;
//        detented[2] = dircn[2];
        detented[0] = speed * Math.sin(theta_n);
        detented[1] = speed * Math.cos(theta_n);
        detented[2] = dircn[2];
        
        System.out.println("detented vals: " + dircn[0] + " " + dircn[1]);
        
        return detented;
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
        
        double[] k = {y-1, y+1, 
                     1-x, -1-x};
        
        double p =  Math.sqrt( (k[0] * k[0] + k[2] * k[2]) / 2) * Math.cos( (Math.PI / 4) + MathUtils.atan2(k[0], k[2]));
        double r =  Math.sqrt( (k[1] * k[1] + k[2] * k[2]) / 2) * Math.cos(-(Math.PI / 4) + MathUtils.atan2(k[1], k[2]));
        double q = -Math.sqrt( (k[1] * k[1] + k[3] * k[3]) / 2) * Math.cos( (Math.PI / 4) + MathUtils.atan2(k[1], k[3]));
        
        double[] corrected = new double[3];
        corrected[0] = (dircn[2] * r + (dircn[0] - dircn[2]) * q + dircn[0] * p) / (q + p);
        corrected[1] = (-dircn[2] * r + dircn[1] * q - (-dircn[1] - dircn[2]) * p) / (q + p);
        corrected[2] = (2 * dircn[2]) / (q + p);
        return corrected;
    }

    public void drive_mecanum(double[] directions)
    {  
        double[] dircns;
        
        dircns = null_zone(directions, 0.1);
        if (is_detented)
        {
            dircns = detents(dircns);
        }
        dircns = front_side(dircns);
        if (is_orbiting)
        {
            dircns = orbit_point(dircns);
        }
        
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
