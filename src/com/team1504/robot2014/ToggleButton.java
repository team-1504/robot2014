/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.team1504.robot2014;

import edu.wpi.first.wpilibj.Joystick;

/**
 *
 * @author gavaga
 */
public class ToggleButton 
{
    private Joystick joystick;
    private int button_index;
    private boolean prev_button_state;
    
    public ToggleButton(Joystick stick, int index)
    {
        joystick = stick;
        button_index = index;
    }
    
    public boolean is_rising()
    {
        boolean state = !prev_button_state && joystick.getRawButton(button_index);
        prev_button_state = joystick.getRawButton(button_index);
        return state;
    }
}
