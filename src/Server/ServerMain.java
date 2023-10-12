/*
 * Name: Yutian
 * Surname: Xia
 * Student ID: 1252909
 */
package Server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java -jar server.jar ip port");
            return;
        }

        String ip = args[0];
        int port;
        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid port number.");
            return;
        }

        try(ServerSocket serverSocket = new ServerSocket(port, 50, InetAddress.getByName(ip))) {
            PlayerPool playerPool = new PlayerPool();

            while(true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket, playerPool)).start();
            }
        } catch(IOException e) {
            System.err.println("Failed to start the server: " + e.getMessage());
        }
    }

    private static void handleClient(Socket socket,PlayerPool playerPool) {
        try {
            PlayerHandler playerHandler = new PlayerHandler(socket);
            playerPool.setupPlayerHandler(playerHandler);
        } catch (IOException e) {
            //Todo
        }
    }
}

