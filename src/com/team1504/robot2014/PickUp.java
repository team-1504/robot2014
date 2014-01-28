/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.team1504.robot2014;

/**
 *
 * @author Lexie
 */
public class PickUp {
    public static final int PICK_UP_STOP = 0;
    public static final int PICK_UP_MAX = 1;
    public static final int PICK_UP_MED = 2;
    public static final int PICK_UP_REVERSE = 3;
    
    
    private boolean pick_up_solenoid_position;
    private double pick_up_jaguar_value;
    
    public void set_state(int pick_up_state)
    {
        if(pick_up_state == PICK_UP_MAX){
            pick_up_jaguar_value = 1;
        }
        else if(pick_up_state == PICK_UP_MED){
            pick_up_jaguar_value = 0.5;
        }
        else if (pick_up_state == PICK_UP_REVERSE){
            pick_up_jaguar_value = -1;
        }
        else if (pick_up_state == PICK_UP_STOP){
           pick_up_jaguar_value = 0;    
        }       
    }
    
    public void write_value(double set_value)
    {
        pick_up_jaguar_value = set_value;
    }
    
    public double get_jaguar_value()
    {
        return pick_up_jaguar_value;
    }
  }

