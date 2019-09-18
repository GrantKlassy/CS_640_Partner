/*
CS640 Fall 2019
*/
import java.net.*;
import java.io.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Date;

public class Iperfer {
	public static void main(String[] args) throws IOException {


		for (int i = 0; i < args.length; i++) {
			System.out.println("args[" + i + "] = " + args[i]);
		}

		String mode = args[0];

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


	}
	
	////initialize the counter
		static int count = 0;
		static double rate = 0;
	public static void stat() {
		//Print stats
		System.out.println("sent=" + count + " KB rate=" + rate + " Mbps");
	}

	public static void runClient(String[] args) throws IOException {

		System.out.println("in runClient");

		// Check argument length and correct order
		if (args == null) {
			System.err.println("Error: invalid arguments 1");
			System.exit(1);
		}
		if (args.length != 7 ) {
			System.err.println("Error: invalid arguments 2");
			System.exit(1);
		}
		if (!args[3].equals("-p")) {
			System.err.println("Error: invalid arguments 3");
			System.exit(1);
		}
		if (!args[5].equals("-t")) {
			System.err.println("Error: invalid arguments 4");
			System.exit(1);
		}

		// Parse the arguments
		int portNumber = Integer.parseInt(args[4]);
		String host = args[2];
		double time = Double.parseDouble(args[6]);

		/*//initialize the counter
		int count = 0;
		double rate = 0;*/

		// Check the port number
		if (portNumber < 1024 || portNumber > 65535) {
			System.err.println("Error: port number must be in the range 1024 to 65535");
			System.exit(1);
		}

		System.out.println("Connecting to " + host + " @ port number " + portNumber);

		// Start counting up to "time" seconds

		// A TimerTask to exit after x seconds
		TimerTask task = new TimerTask() {
			public void run() {
				//System.out.println("Task performed on: " + new Date() + "n" + "Thread's name: " + Thread.currentThread().getName());
				// TODO Print something
				stat();
				System.exit(0);
			}
		};

		Timer timer = new Timer("Timer");
		System.out.println("Starting timer");

		// Set up the connection
		Socket clientSoc = new Socket(host, portNumber);
		System.out.println("Connection Established\n");

		// Get input and output stream
		DataOutputStream outStream = new DataOutputStream(clientSoc.getOutputStream());
		PrintWriter writer = new PrintWriter(outStream, true);
		DataInputStream inStream = new DataInputStream(clientSoc.getInputStream());
		BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));


		// TODO Figure out how to send all zeros
		Character[] sendString = new Character[500];
		for (int i = 0; i < sendString.length; i++) {
			sendString[i] = '0';
		}


		// TODO Check if this is ok
		timer.schedule(task, ((int)time * 1000));

		while(true) {
			// Count up at every 1000 byte
			count++;
			rate = count / time / 1000 * 8;
			// Write 1000 bytes to the writer
			writer.print(sendString);
		}



		/* 1. Create a socket that connects to the server (identified by the host name and port number) */
		/* 2. Get handles to the input and output stream of the socket */
		/* 3. Get a handle to the standart input stream to get the user's input (that needs to be sent over to the server) */
		/* 4a. Block until the user enters data to the standard input stream */
		/* 4b. Write the users input the input stream of the socket (sends data to the server) */
		/* 4c. Read the output stream of the socket (reads data sent by the server) */
		/* 5. Close the socket */

	/*
		Socket clientSoc = new Socket(host, portNumber);
		System.out.println("Connection Established\n");

		DataOutputStream outStream = new DataOutputStream(clientSoc.getOutputStream());
		PrintWriter writer = new PrintWriter(outStream, true);

		DataInputStream inStream = new DataInputStream(clientSoc.getInputStream());
		BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));

		BufferedReader stdIn = new BufferedReader (new InputStreamReader (System.in));

		System.out.println("Enter data to be sent to server: ");
		String stdInput;
		while ((stdInput = stdIn.readLine()) !=null){
			writer.println(stdInput);
			System.out.println("Text received --> " + reader.readLine());
		}

		clientSoc.close();
	*/


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
		System.out.println("Waiting for client connections");

		//2. Block until a client requests a connection to this application
		Socket clientSoc = serverSoc.accept();

		//3. Get handles to the output and input stream of the socket
		DataOutputStream outStream = new DataOutputStream(clientSoc.getOutputStream());
		PrintWriter writer = new PrintWriter(outStream, true);

		DataInputStream inStream = new DataInputStream(clientSoc.getInputStream());
		BufferedReader reader = new BufferedReader(new InputStreamReader(clientSoc.getInputStream()));

		System.out.println("Connection established\n");
		String text;

		//4. Block until you read a line from the client (Incoming data can read from the input stream)
		//5. Echo back the line read from the client (Write the incoming data to the output stream)
		//5a. Read data bytes sent from client using the input stream
		//5b. Print the data received to the standard output
		//5c. Send back the data received using the output stream
		while ((text = reader.readLine()) != null){
			System.out.println("Text received ==> " + text);
			writer.println(text);
		}

		// Close sockets
		clientSoc.close();
		serverSoc.close();

/*

		//1. Create a ServerSocket that listens on the specified port
		ServerSocket serverSoc = new ServerSocket(serverPort);

		System.out.println("Waiting for client connections");

		//2. Block until a client requests a connection to this application
		Socket clientSoc = serverSoc.accept();

		//3. Get handles to the output and input stream of the socket
		DataOutputStream outStream = new DataOutputStream(clientSoc.getOutputStream());
		PrintWriter writer = new PrintWriter(outStream, true);

		DataInputStream inStream = new DataInputStream(clientSoc.getInputStream());
		BufferedReader reader = new BufferedReader(new InputStreamReader(clientSoc.getInputStream()));

		System.out.println("Connection established\n");
		String text;


		//4. Block until you read a line from the client (Incoming data can read from the input stream)
		//5. Echo back the line read from the client (Write the incoming data to the output stream)
		//5a. Read data bytes sent from client using the input stream
		//5b. Print the data received to the standard output
		//5c. Send back the data received using the output stream
		while ((text = reader.readLine()) != null){
			System.out.println("Text received ==> " + text);
			writer.println(text);
		}

		// Close sockets
		clientSoc.close();
		serverSoc.close();
*/

	}
}
