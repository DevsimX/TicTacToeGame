package Server;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RankSystem {
    private final ConcurrentHashMap<String, Integer> scores;
    private final List<String> orderedUsers;
    private final Set<String> uniqueUsers; // To ensure uniqueness of usernames
    private final ReadWriteLock lock;

    public RankSystem() {
        scores = new ConcurrentHashMap<>();
        orderedUsers = new ArrayList<>();
        uniqueUsers = new HashSet<>();
        lock = new ReentrantReadWriteLock();
    }

    public void addPlayer(String username) {
        lock.writeLock().lock();
        try {
            if(!uniqueUsers.contains(username)) {
                scores.put(username, 0);
                uniqueUsers.add(username);
                insertUserInOrder(username, 0);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void updateRank(String username, String result) {
        lock.writeLock().lock();
        try {
            int currentScore = scores.getOrDefault(username, 0);
            scores.put(username, currentScore);
            orderedUsers.remove(username);

            switch (result) {
                case "WIN":
                    currentScore += 5;
                    break;
                case "DRAW":
                    currentScore += 2;
                    break;
                case "LOSE":
                    currentScore -= 5;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown result: " + result);
            }

            scores.put(username, currentScore);
            insertUserInOrder(username, currentScore);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void insertUserInOrder(String username, int score) {
        // Use binary search to find the correct position
        int position = Collections.binarySearch(orderedUsers, username, (u1, u2) -> scores.get(u2).compareTo(scores.get(u1)));

        // If not found, binarySearch returns (-(insertion point) - 1)
        if (position < 0) {
            position = -(position + 1);
        }

        orderedUsers.add(position, username);
    }
    public int fetchRank(String username) {
        lock.readLock().lock();
        try {
            int rank = orderedUsers.indexOf(username);
            return (rank != -1) ? rank + 1 : -1;
        } finally {
            lock.readLock().unlock();
        }
    }
}
