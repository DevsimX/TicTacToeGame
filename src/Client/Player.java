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
    private char symbol;

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
        this.playerState = PlayerState.WAITING_FOR_GAME;
    }

    public String getUsername() {
        return username;
    }

    public BufferedReader getIn() {
        return in;
    }

    public Socket getSocket() {
        return socket;
    }

    public PrintWriter getOut() {
        return out;
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

    public char getSymbol() {
        return symbol;
    }

    public void setSymbol(char symbol) {
        this.symbol = symbol;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }
}

