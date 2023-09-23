package Server;

import java.io.*;
import java.net.Socket;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class PlayerHandler implements Runnable {
    public enum PlayerState {
        WAITING_FOR_GAME, IN_GAME
    }
    private final Socket socket;
    private final PrintWriter out;
    private final BufferedReader in;
    private final String username;
    private String symbol;
    private PlayerState state = PlayerState.WAITING_FOR_GAME;
    private GameSession gameSession;
    private Consumer<String> removePlayerFromPoolFunction;
    private Consumer<PlayerHandler> continueGameFunction;
    private Function<String,Integer> fetchRankFunction;
    private BiConsumer<String,String> updateRankFunction;
    private BiConsumer<PlayerHandler,GameSession> handlePlayerDisconnectFunction;

    public PlayerHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.username = in.readLine();
    }

    @Override
    public void run() {
        // Listen for messages from this client and process them
        try {
            String line;
            while ((line = in.readLine()) != null) {
                switch (state) {
                    case WAITING_FOR_GAME:
                        if(line.equals("CONTINUE")){
                            this.continueGameFunction.accept(this);
                            this.sendRank();
                        }
                        // Handle messages specific to waiting state
                        break;
                    case IN_GAME:
                        if (line.startsWith("MOVE:") && gameSession != null) {
                            String[] parts = line.split(":", 3);
                            int x = Integer.parseInt(parts[1]);
                            int y = Integer.parseInt(parts[2]);
                            gameSession.processPlayerMove(this, x,y);
                        } else if (line.startsWith("CHAT:") && gameSession != null) {
                            String[] parts = line.split(":", 3);
                            String username = parts[1];
                            String msg = parts[2];
                            gameSession.broadcastChatMsg(username,msg);
                        }
                        // Handle other in-game messages
                        break;
                }
            }
        } catch (IOException e) {
            // Other error handling
        }

        removePlayerFromPoolFunction.accept(username);
        handlePlayerDisconnectFunction.accept(this,this.gameSession);
        if (gameSession != null) {
            gameSession.notifyPlayerDisconnected(this);
        }
    }

    public void sendMessage(String msg) {
        out.println(msg);
    }

    public Socket getSocket() {
        return socket;
    }

    public PrintWriter getOut() {
        return out;
    }

    public BufferedReader getIn() {
        return in;
    }

    public String getUsername() {
        return username;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setGameSession(GameSession session) {
        this.gameSession = session;
        this.state = PlayerState.IN_GAME;
    }

    public void removeGameSession() {
        this.gameSession = null;
        this.state = PlayerState.WAITING_FOR_GAME;
    }

    public void setRemovePlayerFromPoolFunction(Consumer<String> removePlayerFromPoolFunction) {
        this.removePlayerFromPoolFunction = removePlayerFromPoolFunction;
    }

    public void setContinueGameFunction(Consumer<PlayerHandler> continueGameFunction) {
        this.continueGameFunction = continueGameFunction;
    }

    public void setFetchRankFunction(Function<String, Integer> fetchRankFunction) {
        this.fetchRankFunction = fetchRankFunction;
    }

    public void setUpdateRankFunction(BiConsumer<String, String> updateRankFunction) {
        this.updateRankFunction = updateRankFunction;
    }

    public void sendRank(){
        this.out.println("RANK:"+this.fetchRank());
    }

    public String fetchRank(){
        return "Rank#"+ this.fetchRankFunction.apply(this.username);
    }

    public void updateRank(String result){
        this.updateRankFunction.accept(username,result);
    }

    public void setHandlePlayerDisconnectFunction(BiConsumer<PlayerHandler, GameSession> handlePlayerDisconnectFunction) {
        this.handlePlayerDisconnectFunction = handlePlayerDisconnectFunction;
    }
}

