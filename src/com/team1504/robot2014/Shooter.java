/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.team1504.robot2014;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.Solenoid;

/**
 *
 * @author Gavaga
 */
public class Shooter 
{
    private static final double DEFAULT_FIRE_DISTANCE = 0;
    private static final double DEFAULT_PULL_DISTANCE = 0;
    private static final double MAX_PULL_DISTANCE = 0;
    private static CANJaguar winch;
    private static Solenoid winch_release;
    
    public Shooter(CANJaguar w, Solenoid wr)
    {
        winch = w;
        winch_release = wr;
    }
    
    public void fire()
    {
        
    }
    
    public void prep_shooter()
    {
        prep_shooter();
    }
    
    public void prep_shooter(double distance)
    {
        
    }
}
