// TODO: Add DataFormatRange?
// TODO: Add reset() method
// TODO: Support LiveWindow

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.team1504.robot2014;

import com.sun.squawk.util.MathUtils;
import edu.wpi.first.wpilibj.DigitalModule;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.communication.UsageReporting;

/**
 *
 * @author 2013 Developer
 */
public class HMC5883L_I2C {
    
    // 7-bit I2C address of the HMC5883L
    private static final byte address = 0x1E; // 0011110b
    
    /*
     * Message Format: <mode> <register> <data>
     * Note: <mode> is unnecessary in I2C.read()/I2C.write().
     * Use I2C.read(register, data) and I2C.write(register, data).
     */
    
    /* Modes */
    private static final byte read = 0x3D;  // 00111101b
    private static final byte write = 0x3C; // 00111100b
    /* ***** */
    
    /* Registers */
    private static final byte configurationRegisterA = 0x00;    // 00000000b
    private static final byte configurationRegisterB = 0x01;    // 00000001b
    private static final byte modeRegister = 0x02;              // 00000010b
    private static final byte dataOutputXMSBRegister = 0x03;    // 00000011b
    private static final byte dataOutputXLSBRegister = 0x04;    // 00000100b
    private static final byte dataOutputZMSBRegister = 0x05;    // 00000101b
    private static final byte dataOutputZLSBRegister = 0x06;    // 00000110b
    private static final byte dataOutputYMSBRegister = 0x07;    // 00000111b
    private static final byte dataOutputYLSBRegister = 0x08;    // 00001000b
    private static final byte statusRegister = 0x09;            // 00001001b
    private static final byte identificationRegisterA = 0x0A;   // 00001010b
    private static final byte identificationRegisterB = 0x0B;   // 00001011b
    private static final byte identificationRegisterC = 0x0C;   // 00001100b
    /* ********* */
    
    /* Data */
    private static final byte continuousMeasurementMode = 0x00; // 00000000b
    private static final byte singleMeasurementMode = 0x01;     // 00000001b
    private static final byte idleModeA = 0x02;                 // 00000010b
    private static final byte idleModeB = 0x03;                 // 00000011b
    
    private static final byte CRAData = 0x10;   // 00010000b (Default)
    private static final byte CRBData = 0x20;   // 00100000b (Default)
    /* **** */
    
    private static final double mGPerLSB = 0.92;    // Change if CRBData is changed
    
    private I2C i2c;
    
    private static final double defaultHeading = 0;
    private double zeroHeading;
    
    private double declination = -0.1129; // <http://www.ngdc.noaa.gov/geomagmodels/Declination.jsp>
    // Not strictly necessary, but use + for E, - for W
    
    public HMC5883L_I2C(int moduleNumber)
    {
        DigitalModule module = DigitalModule.getInstance(moduleNumber);
        i2c = module.getI2C(address);
        
        i2c.write(configurationRegisterA, CRAData);
        i2c.write(configurationRegisterB, CRBData);
        i2c.write(modeRegister, continuousMeasurementMode);
        
        UsageReporting.report(UsageReporting.kResourceType_I2C, 1, moduleNumber - 1);
        
        zeroHeading = defaultHeading;
    }
    
    private double angleFromBytes(byte MSB, byte LSB)
    {
        return (MSB << 8 | LSB) * mGPerLSB;
    }
    
    public double getAngle(Axes axis)
    {
        byte[] data = new byte[2];
        i2c.read(axis.register, data.length, data);
        return angleFromBytes(data[0], data[1]);
    }
    
    public AllAxes getAngles()
    {
        AllAxes axes = new AllAxes();
        byte[] data = new byte[6];
        i2c.read(dataOutputXMSBRegister, data.length, data);
        
        axes.xAxis = angleFromBytes(data[0], data[1]);
        axes.yAxis = angleFromBytes(data[2], data[3]);
        axes.zAxis = angleFromBytes(data[4], data[5]);
        
        return axes;
    }
    
    public double getHeading()
    {
        return (MathUtils.atan2(getAngle(Axes.y), getAngle(Axes.x)) + declination + 6.28) % 6.28;
    }
    
    public double getOffset()
    {
        return getHeading() - zeroHeading;
    }
    
    public void defaultHeading()
    {
        zeroHeading = defaultHeading;
    }
    
    public void resetOffset()
    {
        zeroHeading = getHeading();
    }
    
    public static class Axes
    {
        private final byte register;
        public static final Axes x = new Axes(dataOutputXMSBRegister);
        public static final Axes y = new Axes(dataOutputYMSBRegister);
        public static final Axes z = new Axes(dataOutputZMSBRegister);
        
        private Axes(byte register)
        {
            this.register = register;
        }
    }
    
    public static class AllAxes
    {
        public double xAxis;
        public double yAxis;
        public double zAxis;
    }
    
}
