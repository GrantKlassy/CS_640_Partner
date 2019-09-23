/*
CS640 Fall 2019
*/
import java.net.*;
import java.io.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Date;
import java.util.Arrays;
import java.lang.*;

public class Iperfer {

	static double rate = 0;
	static boolean timerDone = false;

	public static void main(String[] args) throws IOException {

		// Debug print arguments
		boolean DEBUG = false;
		if (DEBUG) {
			for (int i = 0; i < args.length; i++) {
				System.out.println("args[" + i + "] = " + args[i]);
			}
		}

		// Get the client or server mode
		String mode = args[0];

		// Run the client or server mode
		if (mode == null) {
			System.err.println("Error: invalid arguments");
			System.exit(1);
		} else if (mode.equals("-c")) {
			runClient(args);
		} else if (mode.equals("-s")) {
			runServer(args);
		} else {
			// Invalid mode, exit
			System.err.println("Error: invalid arguments");
			System.exit(1);
		}

		// FIXME
		//System.out.println("Hitting the end of main()");
		System.exit(0);
	}

	public static void runClient(String[] args) throws IOException {


		// Check argument length and correct order
		if (args == null || args.length != 7 || !args[3].equals("-p") || !args[5].equals("-t")) {
			System.err.println("Error: invalid arguments 1");
			System.exit(1);
		}

		// Parse the arguments
		int portNumber = Integer.parseInt(args[4]);
		String host = args[2];
		double time = Double.parseDouble(args[6]);

		// Check the port number
		if (portNumber < 1024 || portNumber > 65535) {
			System.err.println("Error: port number must be in the range 1024 to 65535");
			System.exit(1);
		}

		//System.out.println("Connecting to " + host + " @ port number " + portNumber);

		// Start counting up to "time" seconds
		// A TimerTask to exit after x seconds
		TimerTask task = new TimerTask() {
			public void run() {
				timerDone = true;
			}
		};

		Timer timer = new Timer("Timer");
		//System.out.println("Starting timer");

		// Set up the connection
		Socket clientSoc = new Socket(host, portNumber);
		//System.out.println("Connection Established\n");

		// Get input and output stream
		DataOutputStream outStream = new DataOutputStream(clientSoc.getOutputStream());
		PrintWriter writer = new PrintWriter(outStream, true);
		DataInputStream inStream = new DataInputStream(clientSoc.getInputStream());
		BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));

		// https://stackoverflow.com/questions/1176135/socket-send-and-receive-byte-array
		// https://stackoverflow.com/questions/16475457/how-to-initailize-byte-array-of-100-bytes-in-java-with-all-0s
		byte[] zeros = new byte[1000];
		Arrays.fill(zeros, (byte)0);

		// Schedule the timer task to start
		timer.schedule(task, ((int)time * 1000));


		long timeBefore = System.currentTimeMillis();
		long timeAfter = 0;

		// The number of packets we've sent
		int num1000BytePacketsSent = 0;

		// Wait until our timer is done
		while(!timerDone) {

			// Count up at every 1000 byte
			num1000BytePacketsSent++;

			// Write 1000 bytes to the writer
			//outStream.writeInt(zeros.length);
			outStream.write(zeros);
		}
		timeAfter = System.currentTimeMillis();
		long timeDiff = timeAfter - timeBefore;
		System.out.println(timeDiff);

		// Print stats
		// 1KB is 1000 bytes, so our counter is the num KB sent
		// 1 kilobyte (KB) = 1000 bytes (B)
		// 1 megabyte (MB) = 1000 KB
		// 1 byte (B) = 8 bits (b)
		double rate = (((double)num1000BytePacketsSent * 0.008 ) / (double)time);
		System.out.println("sent=" + num1000BytePacketsSent + " KB rate=" + rate + " Mbps");

		// FIXME What do we have to close?
		inStream.close();
		outStream.close();
		clientSoc.close();

	}

	public static void runServer(String[] args) throws IOException {

		// Check args
		if (args == null || args.length != 3 || !args[1].equals("-p")) {
			System.err.println("Error: invalid arguments");
			System.exit(1);
		}

		// Parse the arguments
		int portNumber = Integer.parseInt(args[2]);

		// Check the port number
		if (portNumber < 1024 || portNumber > 65535) {
			System.err.println("Error: port number must be in the range 1024 to 65535");
			System.exit(1);
		}

		//1. Create a ServerSocket that listens on the specified port
		ServerSocket serverSoc = new ServerSocket(portNumber);
//		System.out.println("Waiting for client connections");

		//2. Block until a client requests a connection to this application
		Socket clientSoc = serverSoc.accept();

		//3. Get handles to the output and input stream of the socket
		DataOutputStream outStream = new DataOutputStream(clientSoc.getOutputStream());
		PrintWriter writer = new PrintWriter(outStream, true);

		DataInputStream inStream = new DataInputStream(clientSoc.getInputStream());
		BufferedReader reader = new BufferedReader(new InputStreamReader(clientSoc.getInputStream()));

//		System.out.println("Connection established\n");

		// Grab the time before we start reading
		long timeBefore = 0;
		long timeAfter = 0;

		// BEGIN READING FROM SOCKET
		boolean doneReading = false;
		double bytesRcvd = 0;
		byte[] message = new byte[1000];
		//timeBefore = Instant.now().toEpochMilli();
		timeBefore = System.currentTimeMillis();

		while (!doneReading){
			try {

				//int length = inStream.readInt();
				//if (length > 0) {

					// First time init
					//if (timeBefore == 0) {
					//	timeBefore = Instant.now().toEpochMilli();
					//}

					inStream.readFully(message, 0, 1000);
					bytesRcvd += 1000;
				//}
			} catch (EOFException e) {
				// Get the time after the connection has closed
				//timeAfter = Instant.now().toEpochMilli();
				timeAfter = System.currentTimeMillis();
				doneReading = true;
			}
		}

		// Get the difference in ms between start and end
		System.out.println("Before: " + timeBefore);
		System.out.println("After:  " + timeAfter);
		long diffMs = timeAfter - timeBefore;

		//  FIXME FIXME FIXME FIXME FIXME
		diffMs -= 30;

		System.out.println(diffMs);


		// Print stats
		double rate = ((double)bytesRcvd * 0.000008) / ((double)diffMs / (double)1000.0);
		System.out.println("received=" + (bytesRcvd / 1000) + " KB rate=" + rate);

		// Close sockets
		clientSoc.close();
		serverSoc.close();
		System.exit(0);
	}
}
