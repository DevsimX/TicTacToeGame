package Client;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class TicTacToeGUI {
    private JFrame frame;
    private JButton[][] boardButtons = new JButton[3][3];
    private JTextArea chatArea;
    private JTextField chatInput;
    private JButton quitButton;
    private JLabel statusLabel;
    private JLabel countdownTimerLabel;
    private int countdownValue = 20;
    private Timer timer;
    private Player player;
    public TicTacToeGUI(Player player) {
        this.player = player;

        frame = new JFrame("Tic Tac Toe");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLayout(new BorderLayout());

        JPanel boardPanel = new JPanel(new GridLayout(3, 3));
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                boardButtons[i][j] = new JButton("");
                int finalI = i;
                int finalJ = j;
                boardButtons[i][j].addActionListener(e -> {
                    if(player.getPlayerState() == Player.PlayerState.IN_GAME){
                        if(player.getRoundState() == Player.RoundState.MY_TURN){
                            // Handle board button click
                            JButton source = (JButton) e.getSource();
                            if (!source.getText().equals("")) {
                                showErrorDialog("This slot is not available!");
                            }else{
                                player.sendMove(finalI, finalJ);
                                timer.stop();
                                resetTimer();
                            }
                        }else
                            showErrorDialog("It is not your turn now!");
                    }else {
                        if(player.getPlayerState() == Player.PlayerState.WAITING_FOR_GAME)
                            showErrorDialog("You haven't connected to the game!");
                        else if(player.getPlayerState() == Player.PlayerState.STOPPED)
                            showErrorDialog("Game has stopped!");
                    }
                });
                boardPanel.add(boardButtons[i][j]);
            }
        }

        chatArea = new JTextArea(10, 20);
        chatArea.setEditable(false);
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        chatInput = new JTextField(20);
        chatInput.addActionListener(e -> {
            if (player.getPlayerState() == Player.PlayerState.WAITING_FOR_GAME) {
                showErrorDialog("You have not connected to the game!");
            } else if(player.getPlayerState() == Player.PlayerState.STOPPED){
                showErrorDialog("Game has stopped!");
            } else if(chatInput.getText().isEmpty() || chatInput.getText().length() > 20){
                showErrorDialog("The length of your message should be in 1 to 20!");
            }else{
                if (chatArea.getLineCount() > 10) {
                    chatArea.replaceRange("", 0, chatArea.getText().indexOf("\n") + 1);
                }
                player.sendChat(player.getUsername()+":"+chatInput.getText());
                chatInput.setText("");
            }
        });

        statusLabel = new JLabel("Finding Player...");
        statusLabel.setHorizontalAlignment(JLabel.CENTER);

        quitButton = new JButton("Quit");
        quitButton.addActionListener(e -> {
            int choice = JOptionPane.showOptionDialog(frame,
                    "Do you want to find a new match or quit?",
                    "Game Over",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new String[]{"Find Match", "Quit"},
                    "default");

            if (choice == JOptionPane.NO_OPTION) {
                System.exit(0);
            } else {
                // Logic for finding a new match
                statusLabel.setText("Finding PlayerHandler...");
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        boardButtons[i][j].setText("");
                    }
                }
            }
        });

        countdownTimerLabel = new JLabel("Timer: 20");
        frame.add(countdownTimerLabel, BorderLayout.WEST);

        timer = new Timer(1000, e -> {
            if(countdownValue > 0)
                countdownValue--;
            countdownTimerLabel.setText("Timer: " + countdownValue);

            if (countdownValue <= 0) {
                // handle timeout, pick random cell, etc.
                handleTimeout();
                timer.stop();
            }
        });
        JPanel eastPanel = new JPanel(new BorderLayout());
        eastPanel.add(chatScrollPane, BorderLayout.CENTER);
        eastPanel.add(chatInput, BorderLayout.SOUTH);

        frame.add(boardPanel, BorderLayout.CENTER);
        frame.add(eastPanel, BorderLayout.EAST);
        frame.add(statusLabel, BorderLayout.NORTH);
        frame.add(quitButton, BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);
    }

    public void showErrorDialog(String msg){
        JOptionPane.showMessageDialog(frame,msg,"Error",JOptionPane.ERROR_MESSAGE);
    }

    public void showDialog(String msg){
        JOptionPane.showMessageDialog(frame,msg);
    }

    public void appendChat(String username, String msg){
        chatArea.append(username + ": " + msg + "\n");
    }

    public void changeStatus(String username, String symbol){
        statusLabel.setText(username + " turn (" + symbol + ")");
        if (player.getRoundState() == Player.RoundState.MY_TURN) {
            timer.start();
        } else {
            timer.stop();
        }
    }

    public void gameStop(){
        if(player.getRoundState() == Player.RoundState.MY_TURN)
            timer.stop();
        statusLabel.setText("Game stopped!\nWaiting for reconnection...");
    }

    public void gameEnd(String msg){
        boolean findMatch = showGameEndDialog(msg);
        if (!findMatch) {
            System.exit(0);
        } else {
            resetGame();
            player.continueGame();
        }
    }

    private void resetGame(){
        statusLabel.setText("Finding Player...");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                boardButtons[i][j].setText("");
            }
        }
    }

    private boolean showGameEndDialog(String msg){
        int choice = JOptionPane.showOptionDialog(frame,
                msg + "\nDo you want to find a new match or quit?",
                "Game Over",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[]{"Find Match", "Quit"},
                "default");

        return choice == JOptionPane.YES_OPTION;
    }

    public void updateBoard(int x, int y, String symbol){
        boardButtons[x][y].setText(symbol);
    }

    private void resetTimer(){
        countdownValue = 20;
        countdownTimerLabel.setText("Timer: " + countdownValue);
    }



    private void handleTimeout() {
        ArrayList<String> emptySlots = getEmptySlots();
        if (emptySlots.isEmpty()) return;
        Random random = new Random();
        String randomSlot = emptySlots.get(random.nextInt(emptySlots.size()));

        // Send move to server and reset timer
        String[] line = randomSlot.split(":",2);
        int x = Integer.parseInt(line[0]);
        int y = Integer.parseInt(line[1]);
        player.sendMove(x,y);
        resetTimer();
    }

    private ArrayList<String> getEmptySlots() {
        ArrayList<String> emptySlots = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (boardButtons[i][j].getText().isEmpty()) {
                    emptySlots.add(i+":"+j);
                }
            }
        }
        return emptySlots;
    }

    public void updateRankInfo(){
        if(player.getPlayerState() == Player.PlayerState.WAITING_FOR_GAME){
            statusLabel.setText(this.player.getRank() + " Finding Player...");
        }
    }

    public void showServerLostDialog(){
        timer.stop();
        JOptionPane pane = new JOptionPane("Server unavailable. The application will close in 5 seconds.", JOptionPane.ERROR_MESSAGE);
        JDialog dialog = pane.createDialog(frame, "Error");
        new Timer(5000, e -> {
            dialog.setVisible(false);
            System.exit(0);
        }).start();
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setModal(true);
        dialog.setVisible(true);
    }

    public void loadBoard(String[] msg){
        for(int i = 1; i < msg.length ; i++){
            int j = i-1;
            int x = j/3;
            int y = j%3;
            if(!msg[i].equals("-"))
                boardButtons[x][y].setText(msg[i]);
        }
    }
}