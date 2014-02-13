/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.team1504.robot2014;

import com.sun.squawk.microedition.io.FileConnection;
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
    private boolean is_logging;
    
    public Logger()
    {
        Calendar cal = Calendar.getInstance();
        String f_name, s_name;
        f_name = "log_" + cal.get(Calendar.YEAR) + "." + cal.get(Calendar.MONTH) + "." + cal.get(Calendar.DAY_OF_MONTH) + "." + cal.get(Calendar.HOUR) + "." + cal.get(Calendar.MINUTE);
        s_name = f_name + "_sparse";
        logger = new LoggingThread(f_name + ".log", s_name + ".log");
    }
    
    public void write_f(String line)
    {
        logger.write_frequent_log(line);
    }
    
    public void write_s(String line)
    {
        logger.write_sparse_log(line);
    }
    
    public void start()
    {
        logger.start();
        start_logging();
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
        
        public synchronized void write_frequent_log(String line)
        {
            if (is_logging)
            {
                try {
                    freq_log.writeUTF(line + "%n");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        
        public synchronized void write_sparse_log(String line)
        {
            if (is_logging)
            {
                try {
                    sparse_log.writeUTF(line + "%n");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
