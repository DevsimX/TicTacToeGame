package Server;


import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class GameSession{
    private PlayerHandler playerHandlerX;
    private PlayerHandler playerHandlerO;
    private PlayerHandler currentPlayer;
    private char[][] board = new char[3][3];
    private Timer stopTimer;

    public GameSession(PlayerHandler playerHandlerX, PlayerHandler playerHandlerO) {
        this.playerHandlerX = playerHandlerX;
        this.playerHandlerO = playerHandlerO;
        currentPlayer = playerHandlerX;

        //Board initialization
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = '-';
            }
        }

        // Symbol initialization
        this.playerHandlerX.setSymbol("X");
        this.playerHandlerX.getOut().println("SYMBOL:X");

        this.playerHandlerO.setSymbol("O");
        this.playerHandlerO.getOut().println("SYMBOL:O");

        playerHandlerX.setGameSession(this);
        playerHandlerO.setGameSession(this);

        gameStart();
    }

    private void gameStart(){
        String msg = "START";
        broadcastMessage(msg);
        broadcastCurrentTurnMessage();
    }

    private void broadcastMessage(String msg){
        this.playerHandlerX.getOut().println(msg);
        this.playerHandlerO.getOut().println(msg);
    }

    private void broadcastCurrentTurnMessage(){
        currentPlayer.getOut().println("TURN");
        String username = this.currentPlayer.getUsername();
        String msg = "LABEL:" + currentPlayer.fetchRank()+ " " + currentPlayer.getUsername() + "'s turn("+this.currentPlayer.getSymbol()+")";
        broadcastMessage(msg);
    }

    public synchronized char checkWin() {
        //Check each row
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == board[i][1] && board[i][1] == board[i][2] && board[i][0] != '-') {
                return board[i][0];
            }
        }

        //Check each column
        for (int i = 0; i < 3; i++) {
            if (board[0][i] == board[1][i] && board[1][i] == board[2][i] && board[0][i] != '-') {
                return board[0][i];
            }
        }

        //Check both diagonals
        if (board[0][0] == board[1][1] && board[1][1] == board[2][2] && board[0][0] != '-') {
            return board[0][0];
        }
        if (board[0][2] == board[1][1] && board[1][1] == board[2][0] && board[0][2] != '-') {
            return board[0][2];
        }

        // No winner
        return '-';
    }

    private synchronized boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '-') {
                    return false;
                }
            }
        }
        return true;
    }

    public synchronized void broadcastChatMsg(String username, String msg){
        String chatMsg = "CHAT:"+username+":"+msg;
        broadcastMessage(chatMsg);
    }

    public synchronized void notifyPlayerDisconnected(PlayerHandler playerHandler){
        PlayerHandler playerToNotice = playerHandler == playerHandlerO ? playerHandlerX : playerHandlerO;
        playerToNotice.sendMessage("STOP");
    }

    private void gameEnd(){
        this.playerHandlerX.removeGameSession();
        this.playerHandlerO.removeGameSession();
    }

    public synchronized void processPlayerMove(PlayerHandler playerHandler, int x, int y){
        if(board[x][y] == '-'){
            if(playerHandler == playerHandlerO){
                board[x][y] = 'O';
                broadcastMessage("MOVE:"+x+":"+y+":"+"O");
                handlerWinner(playerHandlerO, playerHandlerX);
            }else if(playerHandler == playerHandlerX){
                board[x][y] = 'X';
                broadcastMessage("MOVE:"+x+":"+y+":"+"X");
                handlerWinner(playerHandlerX, playerHandlerO);
            }else {
                //TODO
            }
        }else {
            playerHandler.getOut().println("ERROR:The place has been selected!");
        }
    }

    private void handlerWinner(PlayerHandler winnerHandler, PlayerHandler otherHandler) {
        char winner = checkWin();
        if (winner != '-') {
            broadcastMessage("WINNER:" + winnerHandler.getUsername());
            winnerHandler.updateRank("WIN");
            otherHandler.updateRank("LOSE");
            gameEnd();
        } else if (isBoardFull()) {
            broadcastMessage("DRAW");
            winnerHandler.updateRank("DRAW");
            otherHandler.updateRank("DRAW");
            gameEnd();
        } else {
            currentPlayer = otherHandler;
            broadcastCurrentTurnMessage();
        }
    }

    public void gameCrashEnd(){
        playerHandlerX.updateRank("DRAW");
        playerHandlerO.updateRank("DRAW");
        broadcastMessage("DRAW");
        gameEnd();
    }

    public PlayerHandler returnOppositePlayer(PlayerHandler playerHandler){
        return  playerHandler == playerHandlerX ? playerHandlerO : playerHandlerX;
    }

    public Timer getStopTimer() {
        return stopTimer;
    }

    public void setStopTimer(Timer stopTimer, TimerTask task) {
        this.stopTimer = stopTimer;
        this.stopTimer.schedule(task,30000);
    }

    public void playerReconnect(PlayerHandler playerHandler){
        if(Objects.equals(playerHandlerX.getUsername(), playerHandler.getUsername())){
            playerHandler.setSymbol(playerHandlerX.getSymbol());
            playerHandler.setGameSession(this);
            if(currentPlayer == playerHandlerX)
                currentPlayer = playerHandler;
            playerHandlerX = playerHandler;
            playerHandlerO.sendMessage("RESUME");
            playerHandlerX.sendMessage("BOARD"+getBoardInfo());
        }else {
            playerHandler.setSymbol(playerHandlerO.getSymbol());
            playerHandler.setGameSession(this);
            if(currentPlayer == playerHandlerO)
                currentPlayer = playerHandler;
            playerHandlerO = playerHandler;
            playerHandlerX.sendMessage("RESUME");
            playerHandlerO.sendMessage("BOARD"+getBoardInfo());
        }
        broadcastCurrentTurnMessage();
    }

    private String getBoardInfo(){
        StringBuilder res = new StringBuilder();
        for (char[] i :board
             ) {
            for (char j:i
                 ) {
                res.append(":").append(j);
            }
        }
        return res.toString();
    }
}
