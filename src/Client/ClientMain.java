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
        disconnectedFromServer();
    }

    private static void disconnectedFromServer(){
        player.setPlayerState(Player.PlayerState.WAITING_FOR_GAME);
        SwingUtilities.invokeLater(() -> {
            gui.gameEndSinceServerCrash();
        });
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
                } else if (line.equals("START")) {
                    // Game start
                    player.gameStart();
                } else if (line.startsWith("WINNER:") || line.equals("DRAW")) {
                    if(line.startsWith("WINNER:")){
                        String[] parts = line.split(":", 2);
                        String username = parts[1];
                        SwingUtilities.invokeLater(() -> {
                            gui.gameEnd("Player "+username+" wins!");
                        });
                    }else {
                        SwingUtilities.invokeLater(() -> {
                            gui.gameEnd("Match Drawn!");
                        });
                    }
                    player.gameEnd();
                } else if(line.startsWith("SYMBOL:")){
                    String[] parts = line.split(":",2);
                    player.setSymbol(parts[1]);
                } else if(line.startsWith("ERROR:")){
                    String[] parts = line.split(":",2);
                    gui.showErrorDialog(parts[1]);
                } else if(line.startsWith("MOVE:")){
                    String[] parts = line.split(":",4);
                    int x = Integer.parseInt(parts[1]);
                    int y = Integer.parseInt(parts[2]);
                    String symbol = parts[3];
                    gui.updateBoard(x,y,symbol);
                } else if(line.startsWith("LABEL:")){
                    String[] parts = line.split(":",2);
                    String msg = parts[1];
                    SwingUtilities.invokeLater(() -> {
                        gui.changeStatusLabel(msg);
                    });
                } else if(line.startsWith("RANK:")){
                    String[] parts = line.split(":",2);
                    String rank = parts[1];
                    System.out.println(rank);
                    player.setRank(rank);
                    gui.updateRankInfo();
                } else if(line.equals("STOP")){
                    player.setPlayerState(Player.PlayerState.STOPPED);
                    gui.gameStop();
                } else if(line.equals("RESUME")){
                    player.setPlayerState(Player.PlayerState.IN_GAME);
                } else if (line.startsWith("BOARD")){
                    player.setPlayerState(Player.PlayerState.IN_GAME);
                    String[] parts = line.split(":",10);
                    gui.loadBoard(parts);
                } else if(line.equals("TURN")){
                    gui.myTurnStart();
                }
                else {
                    //TODO
                }
            }
        }catch (IOException e){
            //TODO
        }
    }
}

