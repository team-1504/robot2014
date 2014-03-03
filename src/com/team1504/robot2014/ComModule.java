/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.team1504.robot2014;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.ServerSocketConnection;
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
    private static boolean is_enabled;
    
    private PiComModule com_thread;
    
    public ComModule(int port)
    {
        com_thread = new PiComModule(port);
        packet_in_buffer = new Vector();
    }
    
    public void enable()
    {
        is_enabled = true;
    }
    
    public void disable()
    {
        is_enabled = false;
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
        DataInputStream com_in;
        OutputStream com_out;
        
        int prt;
        
        public PiComModule(int port)
        {
            prt = port;
            is_init = false;
        }
        
        public void init()
        {
            is_init = true;
            ServerSocketConnection pi_com;
            StreamConnection pi_sock;
            try 
            {
                pi_com = (ServerSocketConnection) Connector.open("socket://1189");
                pi_sock = pi_com.acceptAndOpen();
                com_in = pi_sock.openDataInputStream();
                com_out = pi_sock.openOutputStream();
            } 
            catch (IOException ex) 
            {
                com_thread = new PiComModule(prt);
            }
        }
        
        public void run()
        {
            init();
            int buf_size = 0;
            for (int i = 0; i < RobotMap.CRIO_PACKET_FORMAT.length; ++i)
            {
                buf_size += RobotMap.INDEXED_TYPE_SIZES[RobotMap.CRIO_PACKET_FORMAT[i]];
            }
            while (is_enabled)
            {
                String in = "";
                String out = "";
                if(is_transmitting)
                {
                    byte[] out_bytes = new byte[buf_size];
                    int i = 0;
                    for (int e = 0; e < packet_out.length; ++e)
                    {
                        if (packet_out[e] instanceof Double)
                        {
                            long lng = Double.doubleToLongBits(((Double)packet_out[e]).doubleValue());
//                            System.out.println("Long_bits: " + Long.toHexString(lng));
                            for (int j = 0; j < 8; ++j)
                            {
                                out_bytes[i++] = (byte) (lng >> (8 - i - 1 << 3));
                            }
//                            System.out.println("Added Double @" + i + ": " + ((Double)packet_out[e]).doubleValue());
                        }
                        else if (packet_out[e] instanceof Long)
                        {
                            long lng = ((Long)packet_out[e]).longValue();
                            for (int j = 0; j < 8; ++j)
                            {
                                out_bytes[i++] = (byte) (lng >> (8 - i - 1 << 3));
                            }
//                            System.out.println("Added Long @ " + i + ": " + Long.toHexString(((Long)packet_out[e]).longValue()));
                        }
                        else if (packet_out[e] instanceof Integer)
                        {
                            int igr = ((Integer)packet_out[e]).intValue();
                            for (int j = 0; j < 4; ++j) 
                            {
                                out_bytes[i++] = (byte) (igr >> (4 - i - 1 << 3));
                            }
//                            System.out.println("Added Int @ " + i + ": " + ((Integer)packet_out[e]).intValue());
                        }
                        else if (packet_out[e] instanceof Boolean)
                        {
                            out_bytes[i++] = (byte)(((Boolean)packet_out[e]).booleanValue()? 1: 0);
//                            System.out.println("Added Boolean @ " + i + ": " + ((Boolean)packet_out[e]).booleanValue());
                        }
                    }
                    try {
                        com_out.write(out_bytes, 0, buf_size);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        com_thread = new PiComModule(prt);
                        break;
                    }
//                    System.out.print("THREAD_COM: " + out_bytes.length + " bytes sent; message:");
                    for (int n = 0; n < out_bytes.length; ++n)
                    {
                        System.out.print( Integer.toHexString( ((int)out_bytes[n]) & 0xFF ) );
                    }
                    System.out.println();
                    
                }
                if(is_listening)
                {
//                    System.out.println("THREAD_COM: reading");
                    Object[] packet_in;
                    int i = 0;
                    long timestamp = 0;
                    int type = 0;
                    try
                    {
                        timestamp = com_in.readLong();
                        type = com_in.readInt();
//                        System.out.println("TIMESTAMP: " + timestamp + " TYPE: " + type);
                    } 
                    catch (IOException ex) 
                    {
                        ex.printStackTrace();
                        com_thread = new PiComModule(prt);
                        break;
                    }
//                    System.out.println(Long.toHexString(timestamp));
//                    int type = parse_int(in, i);
//                    i += 4;
//                    System.out.println(i);
                    int[] packet_format = type == 1? RobotMap.INFORM_PACKET_FORMAT: type == 2? RobotMap.CMD_PACKET_FORMAT: RobotMap.NULL_PACKET_FORMAT;


                    packet_in = new Object[packet_format.length];
                    packet_in[0] = new Long(timestamp);
                    packet_in[1] = new Integer(type);

                    for (int e = 2; e < packet_format.length; ++e)
                    {
                        switch(packet_format[e])
                        {
                            case 0:
                                boolean n = false;
                                try 
                                {
                                    n = com_in.readByte() == 1;
//                                    System.out.println("Boolean: " + n);
                                }  
                                catch (IOException ex) 
                                {
                                    ex.printStackTrace();
                                    com_thread = new PiComModule(prt);
                                    
                                    break;
                                }
                                packet_in[e] = (n ? Boolean.TRUE : Boolean.FALSE);
                                ++i;
                                break;
                            case 1:
                                int integer = 0;
                                try {
                                    integer = com_in.readInt();
//                                    System.out.println("Integer: " + integer);
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                    com_thread = new PiComModule(prt);
                                    break;
                                }
                                packet_in[e] = new Integer(integer);
                                i += 4;
                                break;
                            case 2:
                                long lng = 0;
                                try {
                                    lng = com_in.readLong();
//                                    System.out.println("Long: " + lng);
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                    com_thread = new PiComModule(prt);
                                    break;
                                }
                                packet_in[e] = new Long(lng);
                                i += 8;
                                break;
                            case 3:
                                double doub = 0;
                                try {
                                    doub = com_in.readDouble();
//                                    System.out.println("Double: " + doub);
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                    com_thread = new PiComModule(prt);
                                    break;
                                }
                                packet_in[e] = new Double(doub);
                                i += 8;
                                break;
                            default:
                                break;
                        }
                    }
                    if (timestamp > 0)
                    {
                        packet_in_buffer.addElement(packet_in);
                    }

                }
            }
        }
        
        public boolean parse_bool(String packet, int start_index)
        {
            boolean n;
            n = (packet.charAt(start_index) == 1);
//            System.out.println("Parsed bool: " + n);
            return n;
        }
        
        public int parse_int(String packet, int start_index)
        {
            int integer = 0;
            byte[] b = packet.substring(start_index, start_index + 4).getBytes();
            for (int j = 0; j < 4; ++j) 
            {
                integer |= b[j];
                integer = integer << 8;
            }
//            System.out.println("Parsed int: " + integer);
            return integer;
        }
        
        public long parse_long(String packet, int start_index)
        {
            long lng = 0;
            byte[] b = packet.substring(start_index, start_index + 8).getBytes();
//            for (int i = 0; i < 8; ++i)
//            {
//                System.out.printf("%x", b[i]);
//            }
            System.out.println();
            for (int j = 0; j < 8; ++j)
            {
                lng |= b[j];
                lng = lng << 8;
            }
//            System.out.println("Parsed long: " + lng);
            return lng;
        }
        
        public double parse_double(String packet, int start_index)
        {
            double doub = 0;
            long l = 0;
            byte[] b = packet.substring(start_index, start_index + 8).getBytes();
            for (int j = 0; j < 8; ++j)
            {
                l |= b[j];
                l = l << 8;
            }
            doub = Double.longBitsToDouble(l);
//            System.out.println("Parsed double: " + doub);
            return doub;
        }
    }
}
