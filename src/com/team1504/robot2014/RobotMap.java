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
    
    public static final int PICK_UP_JAGUAR_PORT = 20;
    
    public static final int PICKUP_EXTEND_1_PORT = 2;
    public static final int PICKUP_EXTEND_2_PORT = 4;
    public static final int PICKUP_RETRACT_1_PORT = 1;
    public static final int PICKUP_RETRACT_2_PORT = 3;
    
    public static final int PICKUP_SOLENOID_BUTTON_INDEX = 2;
    
    public static final int ROTATION_BUTTON_INDEX = 2; 
    
    public static final int FRONT_LEFT_MAGIC_NUMBER = 1;
    public static final int BACK_LEFT_MAGIC_NUMBER = 1;
    public static final int BACK_RIGHT_MAGIC_NUMBER = -1;
    public static final int FRONT_RIGHT_MAGIC_NUMBER = -1;
    
    public static final int SHOOTER_JAGUAR_PORT_1 = 20;
    public static final int SHOOTER_JAGUAR_PORT_2 = 21;
    public static final int SHOOTER_POT_MODULE_NUM = 5;
    public static final double SHOOTER_POT_BASE_VAL = 0;
    public static final double SHOOTER_POT_RELEASE_VAL = 0.35;
    public static final double SHOOTER_ANGLE_TOLERANCE = 0.01;
    
    public static final int PICK_UP_BUTTON_STOP = 2;
    public static final int PICK_UP_BUTTON_REVERSE = 3;
    public static final int PICK_UP_BUTTON_MED = 4;
    public static final int PICK_UP_BUTTON_MAX = 5;
    
    public static final int PICK_UP_STOP = 0;
    public static final int PICK_UP_MAX = 1;
    public static final int PICK_UP_MED = 2;
    public static final int PICK_UP_REVERSE = 3;
    
    public static final boolean PICK_UP_UP = false;
    public static final boolean PICK_UP_DOWN = true;
    
    public static final int COMPASS_MODULE_ADDRESS = 2;
    
    public static final int COMPRESSOR_RELAY_NUM = 1;
    public static final int PRESSURE_ANALOG_INPUT = 14;
    
    public static final int PHOTON_CANNON_PORT = 8;
    public static final int PHOTON_CANNON_TOGGLE_INDEX = 5;
    
    public static final String RASPBERRY_PI_IP_ADDRESS = "10.15.4.7";
    
    public static final int[] PACKET_FORMAT = {1,2,2,2}; //Timestamp(long/int), Delta_x, Delta_y, Delta_theta
    
    //Automation Controls
    public static final int AUTOMATION_TOGGLE_BUTTON_PORT = 0;
    public static final int ZONE_ONE_BUTTON_PORT = 0;
    public static final int ZONE_TWO_BUTTON_PORT = 0;
    public static final int ZONE_THREE_BUTTON_PORT = 0;
}
