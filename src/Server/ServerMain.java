package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
    public static final int PORT = 12345;

    public static void main(String[] args) {
        try(ServerSocket serverSocket = new ServerSocket(PORT)) {
            PlayerPool playerPool = new PlayerPool();

            while(true) {
                Socket clientSocket = serverSocket.accept();
                handleClient(clientSocket,playerPool);
            }
        } catch(IOException e) {
            //Todo
        }
    }

    private static void handleClient(Socket socket,PlayerPool playerPool) {
        try {
            PlayerHandler playerHandler = new PlayerHandler(socket);
            GameSession gameSession = playerPool.addPlayer(playerHandler);
            if(gameSession != null){
                new Thread()
            }
        } catch (IOException e) {
            //Todo
        }
    }

    private void listenToClient()
}

