/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.team1504.robot2014;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.ServerSocketConnection;
import javax.microedition.io.SocketConnection;
import javax.microedition.io.StreamConnection;

/**
 *
 * @author Gavaga
 * 
 * Thread watching PWM Communications with the Raspberry Pi
 * Continuously writing current send values, and keeping updated
 * variables with values read from the pi.
 * 
 */
public class ComModule
{
    private static Vector packet_in_buffer;
    private static Object[] packet_out;
    
    private static boolean is_transmitting;
    private static boolean is_listening;
    
    private PiComModule com_thread;
    
    public ComModule(String address, int port)
    {
        com_thread = new PiComModule(address, port);
    }
    
    public void start()
    {
        is_listening = true;
        is_transmitting = true;
        com_thread.start();
    }
    
    public void stop_listening()
    {
        is_listening = false;
    }
    
    public void stop_transmitting()
    {
        is_transmitting = false;
    }
    
    public Object[] get_packet_in()
    {
        int index = 0;
        long max_time = 0;
        for (int i = 0; i < packet_in_buffer.size(); ++i)
        {
            long time = ((Long)((Object[])packet_in_buffer.elementAt(i))[0]).longValue();
            if (time > max_time)
            {
                index = i;
                max_time = time;
            }
        }
        Object[] packet = ((Object[])packet_in_buffer.elementAt(index));
        clear_packet_buffer();
        return packet;
    }
    
    public void clear_packet_buffer()
    {
        packet_in_buffer.removeAllElements();
    }
    
    public void update_out_packet(Object[] packet)
    {
        packet_out = packet;
    }
    
    
    private class PiComModule extends Thread
    {
        boolean is_init;
        InputStream com_in;
        BufferedOutputStream com_out;
        
        String addr;
        int prt;
        
        public PiComModule(String address, int port)
        {
            addr = address;
            prt = port;
            is_init = false;
        }
        
        public void init()
        {
            is_init = true;
            ServerSocketConnection pi_com;
            StreamConnection com_con;
            try 
            {
                pi_com = (ServerSocketConnection) Connector.open("socket://" + addr + ":" + prt);
                com_con = pi_com.acceptAndOpen();
                com_in = com_con.openInputStream();
                com_out = com_con.openDataOutputStream();
            } 
            catch (IOException ex) 
            {
                ex.printStackTrace();
            }
        }
        
        public void run()
        {
            init();
            String in = "";
            String out = "";
            byte[] in_raw = new byte[256];
            
            if(is_transmitting)
            {
                for (int i = 0; i < packet_out.length; ++i)
                {
                    char[] val;
                    if (packet_out[i] instanceof Double)
                    {
                        val = new char[8];
                        long lng = Double.doubleToLongBits(((Double)packet_out[i]).doubleValue());
                        for (int j = 0; j < 8; ++j) 
                        {
                            val[j] = (char)((lng >> ((7 - j) * 8)) & 0xff);
                        }
                        out += val;
                    }
                    else if (packet_out[i] instanceof Long)
                    {
                        val = new char[8];
                        long lng = ((Long)packet_out[i]).longValue();
                        for (int j = 0; j < 8; ++j)
                        {
                            val[j] = (char)((lng >> ((7 - j) * 8)) & 0xff);
                        }
                    }
                    else if (packet_out[i] instanceof Integer)
                    {
                        val = new char[4];
                        int igr = ((Integer)packet_out[i]).intValue();
                        for (int j = 0; j < 4; ++j) 
                        {
                            val[j] = (char)((igr >> ((3 - j) * 8)) & 0xff);
                        }
                        out += val;
                    }
                    else if (packet_out[i] instanceof Boolean)
                    {
                        val = new char[1];
                        val[0] = (char)(((Boolean)packet_out[i]).booleanValue()? 1: 0);
                        out += val;
                    }
                }
                try {
                    com_out.write(out.getBytes());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                System.out.println("THREAD_COM: packet_sent: " + out);
            }
            if(is_listening)
            {
                int size = 0;
                for (int i = 0; i < RobotMap.PACKET_FORMAT.length; ++i)
                {
                    size += RobotMap.INDEXED_TYPE_SIZES[RobotMap.PACKET_FORMAT[i]];
                }
                try 
                {
                    int n = com_in.read(in_raw);
                    if (n < 0)
                    {
                        System.out.println("THREAD_COM: error reading from stream");
                    }
                    in = new String(in_raw);
                    System.out.println("THREAD_COM: raw_packet_get:" + in);
                } 
                catch (IOException ex) 
                {
                    ex.printStackTrace();
                }
            }
            Object[] packet_in = new Object[RobotMap.PACKET_FORMAT.length];
            for (int i = 0; i < in.length();)
            {
                switch(RobotMap.PACKET_FORMAT[i])
                {
                    case 0:
                        boolean n;
                        n = (in.charAt(i) == 1);
                        packet_in[i] = (n ? Boolean.TRUE : Boolean.FALSE);
                        ++i;
                        break;
                    case 1:
                        int integer = 0;
                        for (int j = 0; j < 4; ++j) 
                        {
                            integer |= in.charAt(i + j);
                            integer = integer << 8;
                        }
                        packet_in[i] = new Integer(integer);
                        i += 4;
                        break;
                    case 2:
                        long lng = 0;
                        for (int j = 0; j < 8; ++j)
                        {
                            lng |= in.charAt(i + j);
                            lng = lng << 8;
                        }
                        packet_in[i] = new Long(lng);
                        i += 8;
                        break;
                    case 3:
                        double doub = 0;
                        long l = 0;
                        for (int j = 0; j < 8; ++j)
                        {
                            l |= in.charAt(i + j);
                            l = l << 8;
                        }
                        doub = Double.longBitsToDouble(l);
                        packet_in[i] = new Double(doub);
                        i += 8;
                        break;
                    default:
                        break;
                }
            }
            
            packet_in_buffer.addElement(packet_in);
        }
    }
}
