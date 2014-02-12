/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.team1504.robot2014;

import java.io.IOException;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.UDPDatagramConnection;

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
    private static Vector packets_in;
    private static Object[] packet_out;
    
    private static int packet_in_length;
    
    private static boolean is_transmitting;
    private static boolean is_listening;
    
    private PiComModule com_thread;
    
    public ComModule(String address, int packet_in_length, int port)
    {
        this.packet_in_length = packet_in_length;
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
        for (int i = 0; i < packets_in.size(); ++i)
        {
            if (((Integer)((Object[])packets_in.elementAt(i))[0]).intValue() > max_time)
            {
                index = i;
                max_time = ((Long)((Object[])packets_in.elementAt(i))[0]).longValue();
            }
        }
        return ((Object[])packets_in.elementAt(index));
    }
    
    public void update_out_packet(Object[] packet)
    {
        packet_out = packet;
    }
    
    
    private class PiComModule extends Thread
    {
        UDPDatagramConnection pi_com;
        
        public PiComModule(String address, int port)
        {
            try 
            {
                pi_com = (UDPDatagramConnection) Connector.open("datagram://" + address + ":" + port);
            } 
            catch (IOException ex) 
            {
                ex.printStackTrace();
            }
        }
        
        public void run()
        {
            String in = "";
            String out = "";
            
            Datagram out_d;
            
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
                try 
                {
                    out_d = pi_com.newDatagram(out.getBytes(), out.length());
                    pi_com.send(out_d);
                } 
                catch (IOException ex) 
                {
                    ex.printStackTrace();
                }
            }
            if(is_listening)
            {
                int size = 0;
                for (int i = 0; i < RobotMap.PACKET_FORMAT.length; ++i)
                {
                    size += RobotMap.INDEXED_TYPE_SIZES[RobotMap.PACKET_FORMAT[i]];
                }
                Datagram in_d;
                try 
                {
                    in_d = pi_com.newDatagram(size);
                    pi_com.receive(in_d);
                    in = new String(in_d.getData());
                } 
                catch (IOException ex) 
                {
                    ex.printStackTrace();
                }
            }
            Object[] packet_in = new Object[packet_in_length];
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
            
            packets_in.addElement(packet_in);
        }
    }
}
