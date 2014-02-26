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
import java.io.PrintStream;
import java.util.Calendar;
import javax.microedition.io.Connector;

/**
 *
 * @author Team 1504
 */
public class Logger 
{
    private static LoggingThread logger;
    private CANJaguar fl, bl, br, fr, pickup, sh_1, sh_2;
    private Shooter sh;
    private Joystick joy_left, joy_right, joy_op;
    private Mecanum mec;
    private HMC5883L_I2C comp;
    private boolean is_logging;
    private boolean write_freq;
    
    private long start_time;
    
    List frequent_queue;
    List sparse_queue;
    
    public Logger(CANJaguar fl, CANJaguar bl, CANJaguar br, CANJaguar fr, CANJaguar pickup, Shooter sh, Joystick l, Joystick r, Joystick o, Mecanum m, HMC5883L_I2C c)
    {
        start_time = System.currentTimeMillis();
        this.fl = fl;
        this.bl = bl;
        this.br = br;
        this.fr = fr;
        this.sh_1 = sh_1;
        this.sh_2 = sh_2;
        this.pickup = pickup;
        this.sh = sh;
        this.joy_left = l;
        this.joy_right = r;
        this.joy_op = o;
        this.mec = m;
        this.comp = c;
        frequent_queue = new List();
        sparse_queue = new List();
    }
    
    public void reset()
    {
        Calendar cal = Calendar.getInstance();
        String f_name;
        f_name = "log_" + cal.getTime().getTime();
        logger = new LoggingThread(f_name + ".log");
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
        enable();
        logger.start();
    }
    
    public void enable()
    {
        is_logging = true;
    }
    
    public void disable()
    {
        is_logging = false;
    }
    
    private class LoggingThread extends Thread
    {
        private PrintStream log_stream;
        
        public LoggingThread(String f_log_name)
        {
            FileConnection fc;
            try {
                fc = (FileConnection)Connector.open("file:///"+f_log_name, Connector.WRITE);
                fc.create();
                DataOutputStream f_log = fc.openDataOutputStream();
                log_stream = new PrintStream(f_log);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            log_stream.println("Timestamp Driver_Joy_Y Driver_Joy_X Driver_Joy_W FL_Out BL_Out BR_Out FR_Out FL_Jag_Speed FL_Bus_Voltage FL_Out_Voltage FL_Out_Current FL_Temp BL_Jag_Speed BL_Bus_Voltage BL_Out_Voltage BL_Out_Current BL_Temp BR_Jag_Speed BR_Bus_Voltage BR_Out_Voltage BR_Out_Current BR_Temp FR_Jag_Speed FR_Bus_Voltage FR_Out_Voltage FR_Out_Current FR_Temp");
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
                        log_stream.println(frequent_queue.get(0));
                        frequent_queue.remove(0);
                    }
                }
                if (sparse_queue.size() > 12)
                {
                    System.out.println("Flushing Sparse Queue");
                    while (sparse_queue.size() > 0)
                    {
                        log_stream.println("S: " + sparse_queue.get(0));
                        sparse_queue.remove(0);
                    }
                }
                gen_add_freq_line();
                
                if (frequent_queue.size() > 0)
                {
                    log_stream.println(frequent_queue.get(0));
//                    System.out.println("Wrote Freqeuent Log: " + ((String)frequent_queue.get(0)));
                    frequent_queue.remove(0);
                }
                if (sparse_queue.size() > 0)
                {
                    log_stream.println("S: " + sparse_queue.get(0));
//                    System.out.println("Wrote Sparse Log: " + ((String)sparse_queue.get(0)));
                    sparse_queue.remove(0);
                }
            }
        }
        
        public void gen_add_freq_line()
        {
            double time = (System.currentTimeMillis() - start_time)/1000.0;
            String log_line = "";
            try {
                log_line = log_line + (-joy_left.getY()) + " " + joy_left.getX() + " " + joy_right.getX() + " ";
                log_line = log_line + mec.get_front_left() + " " + mec.get_back_left() + " " + mec.get_back_right() + " " + mec.get_front_right() + " ";
                
                log_line = log_line + fl.getSpeed() + "," + " " + fl.getBusVoltage() + "," + " " + fl.getOutputVoltage() + "," + " " + fl.getOutputCurrent() + "," + " " + fl.getTemperature() + "," + " ";
                log_line = log_line + bl.getSpeed() + "," + " " + bl.getBusVoltage() + "," + " " + bl.getOutputVoltage() + "," + " " + bl.getOutputCurrent() + "," + " " + bl.getTemperature() + "," + " ";
                log_line = log_line + br.getSpeed() + "," + " " + br.getBusVoltage() + "," + " " + br.getOutputVoltage() + "," + " " + br.getOutputCurrent() + "," + " " + br.getTemperature() + "," + " ";
                log_line = log_line + fr.getSpeed() + "," + " " + fr.getBusVoltage() + "," + " " + fr.getOutputVoltage() + "," + " " + fr.getOutputCurrent() + "," + " " + fr.getTemperature() + "," + " ";
                log_line = log_line + pickup.getSpeed() + "," + " " + pickup.getBusVoltage() + "," + " " + pickup.getOutputVoltage() + "," + " " + pickup.getOutputCurrent() + "," + " " + pickup.getTemperature() + "," + " ";
                log_line = log_line + sh_1.getSpeed() + "," + " " + sh_1.getBusVoltage() + "," + " " + sh_1.getOutputVoltage() + "," + " " + sh_1.getOutputCurrent() + "," + " " + sh_1.getTemperature() + "," + " ";
                log_line = log_line + sh_2.getSpeed() + "," + " " + sh_2.getBusVoltage() + "," + " " + sh_2.getOutputVoltage() + "," + " " + sh_2.getOutputCurrent() + "," + " " + sh_2.getTemperature() + "," + " ";

//                log_line = log_line + pickup.getSpeed() + " ";
            } catch (CANTimeoutException ex) {
                ex.printStackTrace();
            }

            log_line = log_line + sh.get_shooter_speed() + "," + " " + sh.get_angle();
            
//            log_line = log_line + "Compass: " + comp.getHeading();
            
            log_line = time + "," + " " + log_line;
            frequent_queue.add(log_line);
//            System.out.println("freq_gen_time: " + (System.currentTimeMillis() - start_time));
        }
    }
}
