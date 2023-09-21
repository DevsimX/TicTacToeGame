package Client;

import java.io.*;
import java.net.Socket;

public class Player {
    private final Socket socket;
    private final PrintWriter out;
    private final BufferedReader in;
    private final String username;

    public Player(String username, Socket socket) throws IOException {
        this.socket = socket;
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.username = username;
        out.println(username);
    }

    public void sendMove(int x, int y) {
        out.println("MOVE:" + x + ":" + y);
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
}

