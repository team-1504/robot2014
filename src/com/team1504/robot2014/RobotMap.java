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
    public static final double COMPASS_ANGLE_REFERENCE = 0;
    
    public static final int OPERATOR_JOYSTICK_PORT = 1;
    public static final int DRIVER_LEFT_JOYSTICK_PORT = 2;
    public static final int DRIVER_RIGHT_JOYSTICK_PORT = 3;
    
    public static final int FRONT_LEFT_JAGUAR_PORT = 10;
    public static final int BACK_LEFT_JAGUAR_PORT = 11;
    public static final int BACK_RIGHT_JAGUAR_PORT = 12;
    public static final int FRONT_RIGHT_JAGUAR_PORT = 13;
    
    public static final int ROTATION_BUTTON_DEFAULT = 3;
    public static final int ROTATION_BUTTON_90 = 5;
    public static final int ROTATION_BUTTON_180 = 2;
    public static final int ROTATION_BUTTON_270 = 4;
    
    public static final int FRONT_LEFT_MAGIC_NUMBER = 1;
    public static final int BACK_LEFT_MAGIC_NUMBER = 1;
    public static final int BACK_RIGHT_MAGIC_NUMBER = -1;
    public static final int FRONT_RIGHT_MAGIC_NUMBER = -1;
    
    public static final int SHOOTER_JAGUAR_PORT_1 = 20;
    public static final int SHOOTER_JAGUAR_PORT_2 = 21;
    public static final int SHOOTER_POT_MODULE_NUM = 5;
    public static final int SHOOTER_TOSS_BUTTON = 2;
    
    public static final int SHOOTER_MANUAL_BUTTON = 12;
    
    public static final int LATCH_EXTEND_PORT = 7;
    public static final int LATCH_RETRACT_PORT = 5;
    
    public static final double SHOOTER_GOAL_SPEED_MAX = 0.808594;
    public static final double SHOOTER_TOSS_SPEED_MAX = 0.75;
    public static final double SHOOTER_POT_RELEASE_VAL_GOAL = 4.55;
    public static final double SHOOTER_POT_RELEASE_VAL_TOSS = 4.9;
    public static final double SHOOTER_POT_BASE_VAL = 5.80;
    public static final double SHOOTER_ANGLE_TOLERANCE = 0.05;
    
    public static final int PICK_UP_JAGUAR_PORT = 30;
    
    public static final int PICKUP_EXTEND_PORT = 8;
    public static final int PICKUP_RETRACT_PORT = 6;
    
    public static final int PICKUP_SOLENOID_BUTTON_RETRACT = 11;
    public static final int PICKUP_SOLENOID_BUTTON_EXTEND = 9;
    
    public static final int PICK_UP_BUTTON_STOP = 3;
    public static final int PICK_UP_BUTTON_REVERSE = 5;
    public static final int PICK_UP_BUTTON_MED = 4;
    public static final int PICK_UP_BUTTON_MAX = 6;
    
    public static final int PICK_UP_STOP = 0;
    public static final int PICK_UP_MAX = 1;
    public static final int PICK_UP_MED = 2;
    public static final int PICK_UP_OUT = 3;
    
    public static final boolean PICK_UP_UP = false;
    public static final boolean PICK_UP_DOWN = true;
    
    public static final int COMPASS_MODULE_ADDRESS = 2;
    
    public static final int COMPRESSOR_RELAY_NUM = 1;
    public static final int PRESSURE_DIGITAL_INPUT = 14;
    
    public static final int PHOTON_CANNON_PORT = 8;
    public static final int PHOTON_CANNON_TOGGLE_INDEX = 7;
    
    public static final String RASPBERRY_PI_IP_ADDRESS = "10.15.4.7";
    
    
    public static final int[] NULL_PACKET_FORMAT = {2, 1, 0, 1, 2, 3};
    public static final int[] INFORM_PACKET_FORMAT = {2, 1, 0, 3, 3};
    public static final int[] CMD_PACKET_FORMAT = {2, 1, 3, 3, 3, 3, 3, 3, 1, 1, 0, 0, 0};
    public static final int[] CRIO_PACKET_FORMAT = {2, 3, 3, 3};
    public static final int[] INDEXED_TYPE_SIZES = {1, 4, 8, 8};
    
    //Automation Controls
    public static final int AUTOMATION_TOGGLE_BUTTON_PORT = 0;
    public static final int ZONE_ONE_BUTTON_PORT = 0;
    public static final int ZONE_TWO_BUTTON_PORT = 0;
    public static final int ZONE_THREE_BUTTON_PORT = 0;
}
