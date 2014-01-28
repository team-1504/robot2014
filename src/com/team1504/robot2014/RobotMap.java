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
    public static final int PICK_UP_JAGUAR_PORT = 30;
    
    public static final int EXTEND_1_PORT = 2;
    public static final int EXTEND_2_PORT = 4;
    public static final int RETRACT_1_PORT = 1;
    public static final int RETRACT_2_PORT = 3;
    
    public static final int SOLENOID_BUTTON_INDEX = 2;
    
    public static final int FRONT_LEFT_MAGIC_NUMBER = 1;
    public static final int BACK_LEFT_MAGIC_NUMBER = 1;
    public static final int BACK_RIGHT_MAGIC_NUMBER = -1;
    public static final int FRONT_RIGHT_MAGIC_NUMBER = -1;
    
    public static final int SHOOTER_JAGUAR_PORT_1 = 20;
    public static final int SHOOTER_JAGUAR_PORT_2 = 21;
    public static final int SHOOTER_RELEASE_SOLENOID_PORT = 0;
    
    public static final int PICK_UP_BUTTON_STOP = 2;
    public static final int PICK_UP_BUTTON_REVERSE = 3;
    public static final int PICK_UP_BUTTON_MED = 4;
    public static final int PICK_UP_BUTTON_MAX = 5;
    
    
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
