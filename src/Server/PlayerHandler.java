package Server;

import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class PlayerHandler implements Runnable {
    private final Socket socket;
    private final PrintWriter out;
    private final BufferedReader in;
    private final String username;
    private String symbol;

    public PlayerHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.username = in.readLine();
    }

    @Override
    public void run() {
        // Listen for messages from this client and process them
        try{
            String line;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("MOVE:")) {
                    String[] parts = line.split(":", 3);

                } else {
                }
            }
        }catch (IOException e){
            //TODO
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
}

