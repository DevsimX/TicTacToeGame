package Server;

import java.util.concurrent.*;

public class PlayerPool {
    private ConcurrentLinkedQueue<PlayerHandler> waitingPlayerHandlers = new ConcurrentLinkedQueue<>();

    public GameSession addPlayer(PlayerHandler playerHandler) {
        waitingPlayerHandlers.offer(playerHandler);
        return matchPlayers();
    }

    private GameSession matchPlayers() {
        while (waitingPlayerHandlers.size() >= 2) {
            PlayerHandler p1 = waitingPlayerHandlers.poll();
            PlayerHandler p2 = waitingPlayerHandlers.poll();
            GameSession session = new GameSession(p1, p2);
            return session;
        }
        return null;
    }
}

