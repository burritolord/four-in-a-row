package connect4.gui;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import connectfour.Board;


/*
 * options to add to pause menu
 * Continue
 * Controls
 * New Game
 * Main menu
 * Quit
 * 
 * Stuff in main menu
 * Single Player
 * Multiplayer
 *    -Online
 *    -Offline
 * About
 */
public class Connect4Game extends JFrame implements WindowListener{
    private BoardPanel board;
    
    public static void main(String[] args){
        new Connect4Game();
    }
    
    public Connect4Game(){
        super("Connect Four");
        initGui();
    }
    
    public void initGui(){
        board = new BoardPanel(this);
        
        getContentPane().add(board,BorderLayout.CENTER);
	addWindowListener(this);
        
        getContentPane().setPreferredSize(new Dimension(board.getWidth(),board.getHeight()));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        this.pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    // ------------------- window listener methods ---------------
    @Override
    public void windowActivated(WindowEvent e){
        board.resumeGame();
    }
    
    @Override
    public void windowDeactivated(WindowEvent e){
        board.pauseGame();
    }
    
    @Override
    public void windowDeiconified(WindowEvent e){
        board.resumeGame();
    }
    
    @Override
    public void windowIconified(WindowEvent e){
        board.pauseGame();
    }
    
    @Override
    public void windowClosing(WindowEvent e){
        
    }
    
    @Override
    public void windowClosed(WindowEvent e){
        
    }
    
    @Override
    public void windowOpened(WindowEvent e){
        
    }
}
