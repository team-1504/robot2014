/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.team1504.robot2014;


/**
 *
 * @author Eashwar
 */
public class PickUp {

    
    private boolean pick_up_solenoid_position = RobotMap.PICK_UP_UP;
    private double pick_up_jaguar_value = 0;
    private double pass_offset = 0;
    
    public void set_speed(int pick_up_state)
    {
        if(pick_up_state == RobotMap.PICK_UP_MAX){
            pick_up_jaguar_value = 0.7;
        }
        else if(pick_up_state == RobotMap.PICK_UP_MED){
            pick_up_jaguar_value = 0.5;
        }
        else if (pick_up_state == RobotMap.PICK_UP_OUT){
            pick_up_jaguar_value = -1 - pass_offset; 
        }
        else if (pick_up_state == RobotMap.PICK_UP_STOP){
           pick_up_jaguar_value = 0;    
        }       
    }
    public void set_position(boolean solenoid)
    {
        pick_up_solenoid_position = solenoid;
    }
    
    public void set_pass_offset(double offset)
    {
        pass_offset = offset;
    }
    
    public void set_raw_speed(double set_value)
    {
        pick_up_jaguar_value = set_value;
    }
    
    public boolean get_position()
    {
        return pick_up_solenoid_position;
    }
    
    public double get_speed()
    {
        return pick_up_jaguar_value;
    }
}
