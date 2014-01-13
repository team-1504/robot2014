/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.team1504.robot2014;

/**
 *
 * @author Gavaga
 */
public class RobotMap 
{
    public static final int OPERATOR_JOYSTICK_PORT = 1;
    public static final int DRIVER_LEFT_JOYSTICK_PORT = 2;
    public static final int DRIVER_RIGHT_JOYSTICK_PORT = 3;
    
    public static final int FRONT_LEFT_JAGUAR_PORT = 10;
    public static final int BACK_LEFT_JAGUAR_PORT = 11;
    public static final int BACK_RIGHT_JAGUAR_PORT = 12;
    public static final int FRONT_RIGHT_JAGUAR_PORT = 13;
    
    public static final int WINCH_JAGUAR_PORT = 14;    
    public static final int SHOOTER_RELEASE_SOLENOID_PORT = 0;
    
    
    //Pi Interface Available Ports
    //
    public static final int[] RPI_PWM_CHANNELS = {0,1,2,3,4,5,6,7,8};
    public static final int[] RPI_GPIO_CHANNELS = {0,1,2,3,4,5,6,7,8};
    
    public static final int RPI_I2C_ADDRESS = 1;
    
    //Automation Controls
    public static final int AUTOMATION_TOGGLE_BUTTON_PORT = 0;
    public static final int ZONE_ONE_BUTTON_PORT = 0;
    public static final int ZONE_TWO_BUTTON_PORT = 0;
    public static final int ZONE_THREE_BUTTON_PORT = 0;
}
