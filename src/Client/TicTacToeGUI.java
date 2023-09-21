package Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TicTacToeGUI {
    private JFrame frame;
    private JButton[][] boardButtons = new JButton[3][3];
    private JTextArea chatArea;
    private JTextField chatInput;
    private JButton quitButton;
    private JLabel statusLabel;
    private Player player;

    private String symbol;

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
                boardButtons[i][j].setEnabled(false);
                int finalI = i;
                int finalJ = j;
                boardButtons[i][j].addActionListener(e -> {
                    // Handle board button click
                    JButton source = (JButton) e.getSource();
                    player.sendMove(finalI, finalJ);
                    if (source.getText().equals("")) {
                        source.setText(symbol);
                    }
                });
                boardPanel.add(boardButtons[i][j]);
            }
        }

        chatArea = new JTextArea(10, 20);
        chatArea.setEditable(false);
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        chatInput = new JTextField(20);
        chatInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle chat message send
                if (chatArea.getLineCount() > 10) {
                    chatArea.replaceRange("", 0, chatArea.getText().indexOf("\n") + 1);
                }
                chatArea.append(player.getUsername() + ": " + chatInput.getText() + "\n");
                chatInput.setText("");
            }
        });

        statusLabel = new JLabel("Finding Player...");

        quitButton = new JButton("Quit");
        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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

    public void appendChat(String username, String msg){
        chatArea.append(username + ": " + msg + "\n");
    }

    public void gameStart(String username, String symbol){
        statusLabel.setText(username + "'s turn (" + symbol + ")");
    }

    public void myTurnStart(){
        setBoardState(true);
    }

    public void myTurnEnd(){
        setBoardState(false);
    }

    private void setBoardState(Boolean state){
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                boardButtons[i][j].setEnabled(state);
            }
        }
    }

    public void setSymbol(String symbol){
        this.symbol = symbol;
    }
}


