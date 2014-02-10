/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.team1504.robot2014;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.ServerSocketConnection;
import javax.microedition.io.SocketConnection;

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
    private static int port;
    
    private static boolean is_transmitting;
    private static boolean is_listening;
    
    private PiComModule com_thread;
    
    public ComModule(int packet_length, int port)
    {
        this.packet_in_length = packet_length;
        this.port = port;
        com_thread = new PiComModule();
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
        int max_time = 0;
        for (int i = 0; i < packets_in.size(); ++i)
        {
            if (((Integer)((Object[])packets_in.elementAt(i))[0]).intValue() > max_time)
            {
                index = i;
                max_time = ((Integer)((Object[])packets_in.elementAt(i))[0]).intValue();
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
        private DataInputStream pi_in;
        private DataOutputStream pi_out;
        
        public PiComModule()
        {
            ServerSocketConnection pi_com;
            SocketConnection pi_socket;
            try 
            {
                pi_com = (ServerSocketConnection) Connector.open("socket:" + RobotMap.RASPBERRY_PI_IP_ADDRESS + ":" + port);
                pi_socket = (SocketConnection)pi_com.acceptAndOpen();
                pi_in = pi_socket.openDataInputStream();
                pi_out = pi_socket.openDataOutputStream();
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
            
            if(is_transmitting)
            {
                for (int i = 0; i < packet_out.length; ++i)
                {
                    if (packet_out[i] instanceof Double)
                    {
                        out += ((Double)packet_out[i]).doubleValue();
                    }
                    else if (packet_out[i] instanceof Integer)
                    {
                        out += ((Integer)packet_out[i]).intValue();
                    }
                    else if (packet_out[i] instanceof Boolean)
                    {
                        out += ((Boolean)packet_out[i]).booleanValue()? 1: 0;
                    }
                    out += " ";
                }
                try 
                {
                    pi_out.writeUTF(out);
                } 
                catch (IOException ex) 
                {
                    ex.printStackTrace();
                }
            }
            if(is_listening)
            {
                try 
                {
                    in = pi_in.readUTF();
                } 
                catch (IOException ex) 
                {
                    ex.printStackTrace();
                }
            }
            String[] in_array = Utils.split(in, ' ');
            Object[] packet_in = new Object[packet_in_length];
            for (int i = 0; i < in_array.length; ++i)
            {
                switch(RobotMap.PACKET_FORMAT[i])
                {
                    case 0:
                        packet_in[i] = in_array[i].equals("1")? new Boolean(true): new Boolean(false);
                        break;
                    case 1:
                        packet_in[i] = new Integer(Integer.parseInt(in_array[i]));
                        break;
                    case 2:
                        packet_in[i] = new Double(Double.parseDouble(in_array[i]));
                        break;
                    default:
                        packet_in[i] = in_array[i];
                }
            }
            
            packets_in.addElement(packet_in);
        }
    }
}
