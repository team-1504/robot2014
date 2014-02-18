/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.team1504.robot2014;

import com.sun.squawk.microedition.io.FileConnection;
import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.can.CANTimeoutException;
import edu.wpi.first.wpilibj.networktables2.util.List;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Calendar;
import javax.microedition.io.Connector;

/**
 *
 * @author Team 1504
 */
public class Logger 
{
    private static LoggingThread logger;
    private CANJaguar fl, bl, br, fr, pickup;
    private Shooter sh;
    private Joystick joy_left, joy_right, joy_op;
    private Mecanum mec;
    private boolean is_logging;
    private boolean write_freq;
    
    List frequent_queue;
    List sparse_queue;
    
    public Logger(CANJaguar fl, CANJaguar bl, CANJaguar br, CANJaguar fr, CANJaguar pickup, Shooter sh, Joystick l, Joystick r, Joystick o, Mecanum m)
    {
        this.fl = fl;
        this.bl = bl;
        this.br = br;
        this.fr = fr;
        this.pickup = pickup;
        this.sh = sh;
        this.joy_left = l;
        this.joy_right = r;
        this.joy_op = o;
        this.mec = m;        
        frequent_queue = new List();
        sparse_queue = new List();
        Calendar cal = Calendar.getInstance();
        String f_name, s_name;
        f_name = "log_" + cal.getTime().getTime();
        s_name = f_name + "_sparse";
        logger = new LoggingThread(f_name + ".log", s_name + ".log");
    }
    
    public void reset_files()
    {
        Calendar cal = Calendar.getInstance();
        String f_name, s_name;
        f_name = "log_" + cal.getTime().getTime();
        s_name = f_name + "_sparse";
        logger = new LoggingThread(f_name + ".log", s_name + ".log");
    }
    
    public void write_f(String line)
    {
        frequent_queue.add(line);
    }
    
    public void write_s(String line)
    {
        sparse_queue.add(line);
    }
    
    public void start()
    {
        start_logging();
        logger.start();
    }
    
    public void start_logging()
    {
        is_logging = true;
    }
    
    public void stop_logging()
    {
        is_logging = false;
    }
    
    private class LoggingThread extends Thread
    {
        private DataOutputStream freq_log;
        private DataOutputStream sparse_log;
        
        public LoggingThread(String f_log_name, String s_log_name)
        {
            FileConnection fc_f, fc_s;
            try {
                fc_f = (FileConnection)Connector.open("file:///"+f_log_name, Connector.WRITE);
                fc_s = (FileConnection)Connector.open("file:///"+s_log_name, Connector.WRITE);
                fc_f.create();
                fc_s.create();
                freq_log = fc_f.openDataOutputStream();
                sparse_log = fc_s.openDataOutputStream();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
        public void run()
        {
//            System.out.println("Running Logging Thread");
            while(is_logging)
            {
                long loop_time = System.currentTimeMillis();
//                System.out.println("LOOOOP");
                if (frequent_queue.size() > 12)
                {
                    System.out.println("Flushing Frequent Queue");
                    while (frequent_queue.size() > 0)
                    {
                        try {
                            freq_log.writeUTF(((String)frequent_queue.get(0)));
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        frequent_queue.remove(0);
                    }
                }
                if (sparse_queue.size() > 12)
                {
                    System.out.println("Flushing Sparse Queue");
                    while (sparse_queue.size() > 0)
                    {
                        try {
                            sparse_log.writeUTF(((String)sparse_queue.get(0)));
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        sparse_queue.remove(0);
                    }
                }
                gen_add_freq_line();
                
                if (frequent_queue.size() > 0)
                {
                    try {
                        freq_log.writeUTF(((String)frequent_queue.get(0)));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
//                    System.out.println("Wrote Freqeuent Log: " + ((String)frequent_queue.get(0)));
                    frequent_queue.remove(0);
                }
                if (sparse_queue.size() > 0)
                {
                    try {
                        sparse_log.writeUTF(((String)sparse_queue.get(0)));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
//                    System.out.println("Wrote Sparse Log: " + ((String)sparse_queue.get(0)));
                    sparse_queue.remove(0);
                }
                if (System.currentTimeMillis() - loop_time > 5)
                {
//                    System.out.println("Logger loop: " + (System.currentTimeMillis() - loop_time));
                }
            }
        }
        
        public void gen_add_freq_line()
        {
            long start_time = System.currentTimeMillis();
            String log_line = "";
            try {
                
//                log_line = log_line + "Front_Left: " + fl.getSpeed() + " " + fl.getBusVoltage() + " " + fl.getOutputVoltage() + " " + fl.getOutputCurrent() + " " + fl.getTemperature() + " ";
//                log_line = log_line + "Back_Left: " + bl.getSpeed() + " " + bl.getBusVoltage() + " " + bl.getOutputVoltage() + " " + bl.getOutputCurrent() + " " + bl.getTemperature() + " ";
//                log_line = log_line + "Back_Right: " + br.getSpeed() + " " + br.getBusVoltage() + " " + br.getOutputVoltage() + " " + br.getOutputCurrent() + " " + br.getTemperature() + " ";
//                log_line = log_line + "Front_Right: " + fr.getSpeed() + " " + fr.getBusVoltage() + " " + fr.getOutputVoltage() + " " + fr.getOutputCurrent() + " " + fr.getTemperature() + " ";

                log_line = log_line + "Joystick axes: " + (-joy_left.getY()) + " " + joy_left.getX() + " " + joy_right.getX() + " ";
                log_line = log_line + "Motor Outputs: " + mec.get_front_left() + " " + mec.get_back_left() + " " + mec.get_back_right() + " " + mec.get_front_right() + " ";
                
                log_line = log_line + "Front_Left: " + fl.getOutputVoltage() + " " + fl.getOutputCurrent() + " " + fl.getTemperature() + " ";
                log_line = log_line + "Back_Left: " + bl.getOutputVoltage() + " " + bl.getOutputCurrent() + " " + bl.getTemperature() + " ";
                log_line = log_line + "Back_Right: " + br.getOutputVoltage() + " " + br.getOutputCurrent() + " " + br.getTemperature() + " ";
                log_line = log_line + "Front_Right: " + fr.getOutputVoltage() + " " + fr.getOutputCurrent() + " " + fr.getTemperature() + " ";

                log_line = log_line + "Pickup: " + pickup.getSpeed() + " ";
            } catch (CANTimeoutException ex) {
                ex.printStackTrace();
            }

            log_line = log_line + "Shooter :" + sh.get_shooter_speed() + " " + sh.get_angle();
            
            log_line = System.currentTimeMillis() + " " + log_line + "%n";
            frequent_queue.add(log_line);
//            System.out.println("freq_gen_time: " + (System.currentTimeMillis() - start_time));
        }
    }
}
