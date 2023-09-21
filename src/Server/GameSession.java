package Server;

public class GameSession implements Runnable {
    private PlayerHandler playerHandlerX;
    private PlayerHandler playerHandlerO;
    private PlayerHandler currentPlayer;
    private char[][] board = new char[3][3];

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
    }

    @Override
    public void run() {
        teleportCurrentPlayer();
        // Game loop here

    }

    private void teleportCurrentPlayer(){
        String msg = "START:"+this.currentPlayer.getUsername() + ":" + this.currentPlayer.getSymbol();
        this.playerHandlerX.getOut().println(msg);
        this.playerHandlerO.getOut().println(msg);
    }

    public boolean checkWin() {
        // Check win condition
        return false;
    }

    public PlayerHandler getPlayerHandlerX() {
        return playerHandlerX;
    }

    public PlayerHandler getPlayerHandlerO() {
        return playerHandlerO;
    }
}
