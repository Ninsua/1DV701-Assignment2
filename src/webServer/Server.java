/*	READ ME:
 * 		The program takes the following arguments: root_directory_path port
 * 		If no port argument is provided, the default port will be used (8080)
 */

package webServer;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Server {
	// Maximum threads in the thread pool is number of CPUs + 1.
	private static final int MAX_THREADS = Runtime.getRuntime().availableProcessors() + 1;
	private static final int DEFAULT_SERVER_PORT = 8080;
	private static int MYPORT = 0;

	public static void main(String[] args) {
		ExecutorService threadPool;
		MYPORT = DEFAULT_SERVER_PORT;
		ServerSocket socket;
		String rootPath;

		// Check and set arguments
		try {
			if (args.length <= 0) {
				System.err.println("Invalid arguments. Usage: root_directory port" + "\n" + "port is optional");
				System.exit(1);
			}

			if (!validRootPath(args[0])) {
				System.err.println("Provided root directory does not exist or is not a directory");
				System.exit(1);
			}

			if (validPort(args[1]))
				MYPORT = Integer.parseInt(args[1]);
			else
				printInvalidPortErrorMsg();
				

		} catch (IndexOutOfBoundsException | NumberFormatException e ) { // If an invalid input was given, the default will be used
			printInvalidPortErrorMsg();
		}

		threadPool = Executors.newFixedThreadPool(MAX_THREADS);
		rootPath = args[0];

		File rootDirectory = new File(rootPath);

		try {
			socket = new ServerSocket();

			socket.bind(new InetSocketAddress(MYPORT));

			System.out.println("Server is running...");

			// Runs new task in thread from the thread pool whenever a connection is
			// established
			while (true) {
				Socket clientSocket = null;
				try {
					clientSocket = socket.accept();
					System.out.println("Incoming connection accepted");
					threadPool.execute(new workerThread(clientSocket, true, rootDirectory));
				} catch (IOException e) {
					System.err.println("Could not accept connection");
				}
			}

		} catch (IOException e) {
			System.err.printf("Cannot bind to port %d", MYPORT);
		}
	}

	// Makes sure given path exists and is a directory
	private static boolean validRootPath(String pathString) {
		Path path = Paths.get(pathString);
		if (Files.notExists(path) || !Files.isDirectory(path))
			return false;

		return true;
	}

	// Makes a basic check to see if provided port is valid or not
	private static boolean validPort(String port) {
		try {
			int portAsInteger = Integer.parseInt(port);
			if (portAsInteger < 0 || portAsInteger > 65535) // Port cannot be less than 0 or more than 65535
				return false;
		} catch (NumberFormatException e) {
			return false; // If the port is not parsable to int, valid = false.
		}
		return true;
	}
	
	private static void printInvalidPortErrorMsg() {
		System.err.println("Invalid port detected. Binding to default port: " + DEFAULT_SERVER_PORT);
	}

}