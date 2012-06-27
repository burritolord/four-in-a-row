/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package connect4.gui;
import connectfour.Board;
import connectfour.GamePiece;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
/**
 *
 * @author zesty
 */
public class BoardPanel extends JPanel implements Runnable{
    public static final int SIZE = 100;
    private final int SPEED = 10;
    private final int PERIOD = 12;
    private final int MAX_FRAME_SKIPS = 5;
    private final int PWIDTH = 700, PHEIGHT = 700;
    
    private volatile GamePiece[] players;
    private volatile Board board;
    
    private Graphics dbg = null;
    private Image dbImage = null;
    private Connect4Game parentComponent;
       
    private volatile int currentPlayer;
    private volatile boolean falling;
    private boolean isTie;
    private volatile boolean running;   // stops animation
    private volatile boolean gameOver;  // for game termination
    private volatile boolean isPaused;
    
    private Thread animator;
        
    public BoardPanel(Connect4Game parent){
        parentComponent = parent; 
        players = new GamePiece[2];
        players[0] = new GamePiece(GamePiece.PLAYER1,GamePiece.BLACK);
        players[1] = new GamePiece(GamePiece.PLAYER2,GamePiece.RED);
        board = new Board();
        
        currentPlayer = 0;
        running = true;
        falling = false;
	isTie = false;
        gameOver = false;
        isPaused = false;
        initGui();
    }
    
    @Override
    public void addNotify(){
        super.addNotify();
        startGame();
    }
    
    public void initGui(){
        MouseAdapt mouse = new MouseAdapt();
        
        setLayout(new FlowLayout());
        setFocusable(true);
        requestFocusInWindow();
        
        addMouseListener(mouse);
        addMouseMotionListener(mouse);
        addKeyListener(new KeyAdapt());
        
        setSize(new Dimension(PWIDTH,PHEIGHT));
    }
    
    private void startGame(){
        if(animator == null || !running){
            animator = new Thread(this);
            animator.start();
        }
    }
    
    public void stopGame(){
        running = false;
    }
    
    public void pauseGame(){
        isPaused = true;
    }
    
    public void resumeGame(){
        isPaused = false;
    }
    
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        if(dbImage != null){
            g.drawImage(dbImage,0,0,null);
            Toolkit.getDefaultToolkit().sync();
        }
    }
    
    @Override
    public void run(){
        long beforeTime, afterTime, timeDiff, sleepTime;
        long overSleepTime = 0L;
        long excess = 0L;
        beforeTime = System.nanoTime();
        
        while(running){
            gameUpdate();
            gameRender();
            paintScreen();
            
            afterTime = System.nanoTime();
            timeDiff = afterTime - beforeTime;
            sleepTime = (PERIOD*1000000L) - timeDiff - overSleepTime;
            if(sleepTime > 0){  // time left over
                try{
                    Thread.sleep(sleepTime/1000000L);
                }
                catch(InterruptedException ex){ }
                overSleepTime = System.nanoTime() - afterTime - sleepTime;
            }
            else{   // sleepTime <= 0; frame took longer than the period
                excess -= sleepTime/1000000L;    // store time went over in ms
                overSleepTime = 0L;
            }
            
            beforeTime = System.nanoTime();
            int skips = 0;
            while((excess > PERIOD) && (skips < MAX_FRAME_SKIPS)){
                excess -= PERIOD;
                gameUpdate();
                skips++;
            }
        }
    }
    
    
    private void gameUpdate(){
        int column = players[currentPlayer].getColumn();
        int actualHeight = players[currentPlayer].getY();
        int desiredHeight = (board.getHeight(column) * SIZE) + SIZE;
        
        if(!gameOver && !isPaused){
            // update
            if(desiredHeight <= actualHeight && desiredHeight > 0){
//                players[currentPlayer].setX(0);
                falling = false;
                players[currentPlayer].setY(0);
                
                board.placePiece(players[currentPlayer]);
                System.out.println(board.toString());

                if(board.checkWinCondition(column)){
                    gameOver = true;
                }
		else if(board.isTie()){
                    gameOver = true;
		    isTie = true;
		}
                else{
                    currentPlayer = (currentPlayer + 1) % 2;
                }
            }
            if(falling){
                players[currentPlayer].setY(actualHeight + SPEED);
            }
            
        }
    }
    
    private void gameRender(){        
        if(dbImage == null){
            dbImage = createImage(PWIDTH, PHEIGHT);
            if(dbImage == null){
                System.out.println("dbImage is null");
                return;
            }
            else{
                dbg = dbImage.getGraphics();
            }
        }
        
        // clear the background
        dbg.setColor(Color.GRAY);
        dbg.fillRect(0,0,PWIDTH,PHEIGHT);
        
        // draw animated game piece
        players[currentPlayer].draw(dbg);

        // draw board
        board.draw(dbg,players);      
        
        if(gameOver){
	    if(isTie){
	        JOptionPane.showMessageDialog(null, "Tie");
	    }
	    else{
                JOptionPane.showMessageDialog(null, "Player " + (currentPlayer+1) + " won!");
	    }
            board.clear();
            gameOver = false;
        }
    }
    
    /***************************************************************************
     * Actively render the buffer to the screen
    ***************************************************************************/
    private void paintScreen(){
        Graphics g;
        try{
            g = this.getGraphics(); // get the panels graphic context
            if((g != null) && (dbImage != null)){
                g.drawImage(dbImage,0,0,null);
                g.dispose();
            }
        }
        catch(Exception ex){
            System.out.println("Graphics context error: " + ex);
        }
    }
    
    /***************************************************************************
     * MouseAdapter used to set the x and y positions of the current players piece
     **************************************************************************/
     class MouseAdapt extends MouseAdapter{
        @Override
        public void mouseClicked(MouseEvent e){
            // set piece in motion if no other piece was falling and 
            // there is room in the selected column
            if(!falling){
                players[currentPlayer].setX((e.getX() / SIZE) * SIZE);
                if(board.getHeight(players[currentPlayer].getColumn()) >= 0){
                    falling = true;
                    System.out.println(falling);
                }
            }
        }
        
        @Override
        public void mouseMoved(MouseEvent e){
            if(!falling && !isPaused){
                players[currentPlayer].setX((e.getX() / SIZE) * SIZE);
            }
            //System.out.print("pieceX: " + pieceX);
        }
    }
     
    class KeyAdapt extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e){
            int column = players[currentPlayer].getColumn();
            int code = e.getKeyCode();
            
            if(code == KeyEvent.VK_P){
                isPaused = !isPaused;
            }
            else if((column > 0) && (code == KeyEvent.VK_LEFT) && !falling){
                players[currentPlayer].setX((column-1)*SIZE);
            }
            else if((column < 6) && (code == KeyEvent.VK_RIGHT) && !falling){
                players[currentPlayer].setX((column+1)*SIZE);
            }
            else if((code == KeyEvent.VK_DOWN) &&
                    (board.getHeight(column) >= 0) &&
                    !falling){
                falling = true; 
            }
        }
    }
}
