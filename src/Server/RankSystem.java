package Server;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

public class RankSystem {
    private static class Player implements Comparable<Player> {
        final String username;
        int score;
        final int timestamp;

        Player(String username, int score, int timestamp) {
            this.username = username;
            this.score = score;
            this.timestamp = timestamp;
        }

        @Override
        public int compareTo(Player other) {
            if (this.score != other.score) {
                return Integer.compare(other.score, this.score); // higher score first
            }
            return Integer.compare(this.timestamp, other.timestamp); // earlier timestamp first
        }
    }

    private final ConcurrentSkipListSet<Player> rankedPlayers;
    private final Map<String, Player> playerByUsername;
    private final AtomicInteger timestampGenerator;

    public RankSystem() {
        rankedPlayers = new ConcurrentSkipListSet<>();
        playerByUsername = new HashMap<>();
        timestampGenerator = new AtomicInteger(1);
    }

    public synchronized void addPlayer(String username) {
        Player player = new Player(username, 0, timestampGenerator.getAndIncrement());
        rankedPlayers.add(player);
        playerByUsername.put(username, player);
    }

    public synchronized void updateRank(String username, String result) {
        int scoreChange = switch (result) {
            case "WIN" -> 5;
            case "DRAW" -> 2;
            case "LOSE" -> -5;
            default -> throw new IllegalArgumentException("Unknown result: " + result);
        };

        Player player = playerByUsername.get(username);
        if (player != null) {
            rankedPlayers.remove(player);
            player.score += scoreChange;
            rankedPlayers.add(player);
        }
    }

    public int fetchRank(String username) {
        int rank = 1;
        Player target = playerByUsername.get(username);
        if (target == null) return -1;

        for (Player player : rankedPlayers) {
            if (player.username.equals(username)) {
                return rank;
            }
            rank++;
        }
        return -1;
    }
}
