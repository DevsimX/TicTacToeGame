/*
 * Name: Yutian
 * Surname: Xia
 * Student ID: 1252909
 */
package Client;

import java.io.*;
import java.net.Socket;

public class Player {
    public enum PlayerState {
        WAITING_FOR_GAME, IN_GAME, STOPPED
    }
    public enum RoundState {
        MY_TURN, NOT_MY_TURN
    }
    private final Socket socket;
    private final PrintWriter out;
    private final BufferedReader in;
    private final String username;
    private PlayerState playerState = PlayerState.WAITING_FOR_GAME;
    private RoundState roundState = RoundState.NOT_MY_TURN;
    private String rank;
    private String symbol;

    public Player(String username, Socket socket) throws IOException {
        this.socket = socket;
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.username = username;
        out.println(username);
    }

    public void sendMove(int x, int y) {
        myTurnEnd();
        out.println("MOVE:" + x + ":" + y);
    }

    private void myTurnEnd(){
        this.setRoundState(RoundState.NOT_MY_TURN);
    }

    public void sendChat(String msg){
        out.println("CHAT:"+msg);
    }

    public void continueGame(){
        out.println("CONTINUE");
    }

    public void gameEnd(){
        this.symbol = "";
        this.playerState = PlayerState.WAITING_FOR_GAME;
    }

    public String getUsername() {
        return username;
    }

    public BufferedReader getIn() {
        return in;
    }

    public PlayerState getPlayerState() {
        return playerState;
    }

    public void setPlayerState(PlayerState playerState) {
        this.playerState = playerState;
    }

    public RoundState getRoundState() {
        return roundState;
    }

    public void setRoundState(RoundState roundState) {
        this.roundState = roundState;
    }
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public void gameStart(){
        this.playerState = PlayerState.IN_GAME;
    }

    public void quitGame(){
        this.out.println("QUIT");
    }
}

