package de.freegroup.twogo.plotter;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;


public class StepperPlotter extends Plotter implements SerialPortEventListener {
    SerialPort serialPort;
    private final Object responseSync = new Object();

    private final int ANGLE = 90;
    private final int SPEED = 100;

    double currentLatitude;
    double currentLongitude;

    /** The port we're normally going to use. */
    private static final String PORT_NAMES[] = {
            "/dev/tty.usbserial-A9005evA", // Mac OS X
            "/dev/ttyACM0", // Raspberry Pi
            "/dev/ttyUSB0", // Linux
            "COM3", // Windows
    };

    /**
     * A BufferedReader which will be fed by a InputStreamReader
     * converting the bytes into characters
     * making the displayed results codepage independent
     */
    private BufferedReader input;
    /** The output stream to the port */
    private OutputStream output;
    /** Milliseconds to block while waiting for port open */
    private static final int TIME_OUT = 2000;
    /** Default bits per second for COM port. */
    private static final int DATA_RATE = 9600;

    public StepperPlotter(double blLatitude, double blLongitude, double trLatitude, double trLongitude, int width, int height) {
        super(blLatitude, blLongitude, trLatitude, trLongitude, width, height);

        CommPortIdentifier portId = null;
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

        //First, Find an instance of serial port as set in PORT_NAMES.
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
            System.out.println(currPortId.getName());
            for (String portName : PORT_NAMES) {
                if (currPortId.getName().equals(portName)) {
                    portId = currPortId;
                    break;
                }
            }
        }
        if (portId == null) {
            System.out.println("Could not find COM port.");
            return;
        }

        try {
            // open serial port, and use class name for the appName.
            serialPort = (SerialPort) portId.open(this.getClass().getName(), TIME_OUT);

            // set port parameters
            serialPort.setSerialPortParams(DATA_RATE,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

            // open the streams
            input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
            output = serialPort.getOutputStream();

            // add event listeners
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    @Override
    public void moveTo(double latitude, double longitude) {
        this.currentLatitude = latitude;
        this.currentLongitude = longitude;

        Point end   = this.map(latitude, latitude);
        send("G01 X"+end.x+" Y"+end.y+" Z0 F500");
    }

    @Override
    public void lineTo( double latitude, double longitude) throws Exception
    {
        Point start = this.map(this.currentLatitude, this.currentLongitude);
        Point end   = this.map(latitude, longitude);

        send("G01 X"+start.x+" Y"+start.y+" Z"+ANGLE+" F"+SPEED);
        send("G01 X"+end.x+" Y"+end.y+" Z"+ANGLE+" F"+SPEED);

        this.currentLatitude  = latitude;
        this.currentLongitude = longitude;
    }

    private void send(String send) {
        System.out.println("sending:"+send);
        synchronized (responseSync) {
            try {
                output.write(send.getBytes());
                output.write("\n".getBytes());
                output.flush();
                responseSync.wait();
                // when we reach this line a valid response is available in
                // responseData field
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    /**
     * This should be called when you stop using the port.
     * This will prevent port locking on platforms like Linux.
     */
    public synchronized void close() {
        if (serialPort != null) {
            serialPort.removeEventListener();
            serialPort.close();
        }
    }

    /**
     * Handle an event on the serial port. Read the data and print it.
     */
    public synchronized void serialEvent(SerialPortEvent oEvent) {
        synchronized (responseSync) {
            if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
                try {
                    String inputLine = input.readLine();
                    System.out.println("["+inputLine+"]");
                    // when packet end reached:
                    if(inputLine.startsWith(">")) {
                        responseSync.notifyAll();
                    }
                } catch (Exception e) {
                    System.err.println(e.toString());
                }
            }
        }
        // Ignore all the other eventTypes, but you should consider the other ones.
    }

 }