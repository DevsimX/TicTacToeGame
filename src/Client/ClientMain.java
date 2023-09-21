package Client;

import javax.swing.*;
import java.io.IOException;
import java.net.Socket;

public class ClientMain {
    public static final String SERVER_ADDRESS = "localhost";
    public static final int SERVER_PORT = 12345;
    private static Player player;

    private static TicTacToeGUI gui;

    public static void main(String[] args) {
        String username = args[0];

        try {
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            player = new Player(username, socket);
            gui = new TicTacToeGUI(player);
            listenToServer();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private static void listenToServer(){
        try{
            String line;
            while ((line = player.getIn().readLine()) != null) {
                if (line.startsWith("CHAT:")) {
                    String[] parts = line.split(":", 3);
                    SwingUtilities.invokeLater(() -> {
                        gui.appendChat(parts[1], parts[2]);
                    });
                } else if (line.startsWith("START:")) {
                    String[] parts = line.split(":", 3);
                    if(parts[1].equals(player.getUsername())){
                        gui.myTurnStart();
                    }
                    SwingUtilities.invokeLater(() -> {
                        gui.gameStart(parts[1], parts[2]);
                    });
                } else if (line.equals("WIN") || line.equals("LOSE") || line.equals("DRAW")) {
                    // Handle game end
                } else if(line.startsWith("SYMBOL:")){
                    String[] parts = line.split(":",2);
                    gui.setSymbol(parts[1]);
                }
                else {
                    String[] coords = line.split(",");
                    int x = Integer.parseInt(coords[0]);
                    int y = Integer.parseInt(coords[1]);
                    char mark = coords[2].charAt(0);
//                    gui.markCell(x, y, mark);
                }
            }
        }catch (IOException e){
            //TODO
        }
    }
}

