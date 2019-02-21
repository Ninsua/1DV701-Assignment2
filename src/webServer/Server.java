/*	READ ME:
 * 		The program takes the following arguments: port buffersize -debug
 * 		If no arguments are provided, the default values will be used. PORT: 4950 Buffer size: 1024
 *
 * 		The debug mostly prints exception stack traces and things that might be interesting to know
 * 		regarding stream writing/read and buffer information.
 */

package webServer;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    //Maximum threads in the thread pool is number of CPUs + 1.
    private static final int MAX_THREADS = Runtime.getRuntime().availableProcessors() + 1;
    protected static final int DEFAULT_SERVER_PORT = 80;
    protected static final int DEFAULT_BUFSIZE = 1024;
    protected static int MYPORT = 0;
    protected static boolean DEBUG_MODE = false;

    public static void main(String[] args) {
        ExecutorService threadPool;
        MYPORT = DEFAULT_SERVER_PORT;
        String rootPath;

        //Check and set arguments
        try {
            if (args.length <= 0) {
                System.err.println("Usage: root_directory port -debug" + "\n" + "port and -debug are optional");
                System.exit(1);
            }

            if (!validRootPath(args[0])) {
                System.err.println("Provided root directory does not exist or is not a directory");
                System.exit(1);
            }

            if (validPort(args[1]))
                MYPORT = stringToInt(args[1]);

            if (validDebugArgument(args[2]))
                DEBUG_MODE = true;

        } catch (IndexOutOfBoundsException e) {    //If an invalid input was given, the default will be used
            if (DEBUG_MODE)
                e.printStackTrace();
        }

        threadPool = Executors.newFixedThreadPool(MAX_THREADS);
        ServerSocket socket;
        rootPath = args[0];

        File rootDirectory = new File(rootPath);

        try {
            socket = new ServerSocket();

            socket.bind(new InetSocketAddress(MYPORT));

            if (DEBUG_MODE)
                System.out.println("Server is running...");

            //Runs new task in thread from the thread pool whenever a connection is established
            //while (true) {
            Socket clientSocket = null;
            try {
                clientSocket = socket.accept();
                if (DEBUG_MODE)
                    System.out.println("Incoming connection accepted");
                threadPool.execute(new workerThread(clientSocket, true, rootDirectory));
            } catch (IOException e) {
                if (DEBUG_MODE)
                    System.err.println("Could not accept connection");
            }
            //}

            //Kill running threads
            threadPool.shutdownNow();

        } catch (IOException e) {
            System.err.printf("Cannot bind to port %d", MYPORT);
        }
    }

    //Makes sure given path exists and is a directory
    private static boolean validRootPath(String pathString) {
        Path path = Paths.get(pathString);
        return !Files.notExists(path) && Files.isDirectory(path);
    }

    //Makes a basic check to see if provided port is valid or not
    private static boolean validPort(String port) {
        try {
            int portAsInteger = Integer.parseInt(port);
            if (portAsInteger < 0 || portAsInteger > 65535) //Port cannot be less than 0 or more than 65535
                return false;
        } catch (NumberFormatException e) {
            return false;    //If the port is not parsable to int, valid = false.
        }
        return true;
    }

    private static boolean validDebugArgument(String debugString) {
        return "-debug".compareTo(debugString) == 0;
    }

    private static int stringToInt(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}