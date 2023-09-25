package Server;

import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerPool {
    private final ConcurrentHashMap<String,GameSession> baitingGameSessions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, PlayerHandler> waitingPlayerHandlers = new ConcurrentHashMap<>();

    private RankSystem rankSystem;
    public PlayerPool(){
        rankSystem = new RankSystem();
    }

    public void setupPlayerHandler(PlayerHandler playerHandler) {
        playerHandler.setRemovePlayerFromPoolFunction(this::removePlayer);
        playerHandler.setContinueGameFunction(this::addPlayerHandler);
        playerHandler.setFetchRankFunction(rankSystem::fetchRank);
        playerHandler.setUpdateRankFunction(rankSystem::updateRank);
        playerHandler.setHandlePlayerDisconnectFunction(this::handlePlayerDisconnected);
        rankSystem.addPlayer(playerHandler.getUsername());

        new Thread(playerHandler).start();

        playerHandler.sendRank();
        if(!checkReconnected(playerHandler)){
            addPlayerHandler(playerHandler);
        }
    }

    public void addPlayerHandler(PlayerHandler playerHandler){
        waitingPlayerHandlers.put(playerHandler.getUsername(), playerHandler);
        matchPlayers();
    }

    private void matchPlayers() {
        synchronized(waitingPlayerHandlers) {
            while (waitingPlayerHandlers.size() >= 2) {
                PlayerHandler p1 = removeRandomPlayer();
                PlayerHandler p2 = removeRandomPlayer();
                new GameSession(p1, p2);
            }
        }
    }

    private PlayerHandler removeRandomPlayer() {
        List<String> keys = new ArrayList<>(waitingPlayerHandlers.keySet());
        Random random = new Random();
        String randomKey = keys.get(random.nextInt(keys.size()));
        return waitingPlayerHandlers.remove(randomKey);
    }

    public void removePlayer(String username){
        waitingPlayerHandlers.remove(username);
    }

    private boolean checkReconnected(PlayerHandler playerHandler){
        if(baitingGameSessions.containsKey(playerHandler.getUsername())){
            GameSession gameSession = baitingGameSessions.get(playerHandler.getUsername());
            gameSession.getStopTimer().cancel();
            baitingGameSessions.remove(playerHandler.getUsername());
            gameSession.playerReconnect(playerHandler);
            return true;
        }
        return false;
    }

    public void handlePlayerDisconnected(PlayerHandler playerHandler, GameSession gameSession) {
        String disconnectedPlayerUsername = playerHandler.getUsername();
        String oppositePlayerUsername = gameSession.returnOppositePlayer(playerHandler).getUsername();
        baitingGameSessions.compute(disconnectedPlayerUsername, (key, existingGameSession) -> {
            if (existingGameSession != null) {
                Timer timer = existingGameSession.getStopTimer();
                if (timer != null) {
                    timer.cancel();
                }
                baitingGameSessions.remove(oppositePlayerUsername);
                existingGameSession.gameCrashEnd();
                return null;
            } else {
                Timer timer = new Timer();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        baitingGameSessions.remove(disconnectedPlayerUsername);
                        gameSession.gameCrashEnd();
                    }
                };
                gameSession.setStopTimer(timer,task);
                return gameSession;
            }
        });
    }

}
