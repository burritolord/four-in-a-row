/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package connectfour;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;

/**
 *
 * @author zesty
 */
public class Board {
    private byte[][] board;
    private byte[] heightPosition;
    private Image image = new ImageIcon("../images/boardimage.png").getImage();
    
    private static final int WIDTH = 7;
    private static final int HEIGHT = 6;
    
    public Board(){
        board = new byte[HEIGHT][WIDTH];
        heightPosition = new byte[WIDTH];  
        clear();
    }
    
    /*****************************************
     * Return boolean indicating whether or not
     * the move was successful
     *****************************************/
    public boolean placePiece(GamePiece player){
        int column = player.getColumn();
        if(heightPosition[column] >= 0){
            board[heightPosition[column]--][column] = player.getPlayer();
            return true;
        }
        return false;
    }
    
    public byte[][] getBoardGrid(){
        return board.clone();
    }
    
    public void draw(Graphics g, GamePiece[] players){
        int size = GamePiece.SIZE;
        g.drawImage(image, 0, 100, null);
        // draw pieces
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                int player = board[i][j];
                if(player == players[0].getPlayer())
                    g.drawImage(players[0].getImage(), j*size, i*size+size, null);
                if(player == players[1].getPlayer())
                    g.drawImage(players[1].getImage(), j*size, i*size+size, null);
            }
        }        
    }
    
    public void clear(){
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                board[i][j] = 0;
            }
        }
        for (int i = 0; i < heightPosition.length; i++) {
            heightPosition[i] = (byte)(board.length - 1);
        }
    }
    
    // not tested
    public boolean checkWinCondition(int lastMove){
        byte piece = board[heightPosition[lastMove]+1][lastMove];
        int columnHeight = board.length - (heightPosition[lastMove]+1);
        int checkWidth = board[0].length - 3;
//        int xIntercept = (heightPosition[lastMove]+1) - lastMove;
//        int yIntercept = -xIntercept;
//        boolean flag = true;
                
        // check vertical
        if(columnHeight >=4 && 
           (board[heightPosition[lastMove]+1][lastMove] &
            board[heightPosition[lastMove]+2][lastMove] &
            board[heightPosition[lastMove]+3][lastMove] &
            board[heightPosition[lastMove]+4][lastMove]) == piece){
            return true;
        }
        
        // check horizontal
        for (int i = 0; i < checkWidth; i++) {
            if((board[heightPosition[lastMove]+1][i] &
                board[heightPosition[lastMove]+1][i+1] &
                board[heightPosition[lastMove]+1][i+2] &
                board[heightPosition[lastMove]+1][i+3]) == piece){
                return true;
            }
        }
        
        // check diagonal /
        for (int i = board.length-1; i >= 3 ; i--) {
            for (int j = 0; j < checkWidth ; j++) {
                if((board[i][j] &
                    board[i-1][j+1] &
                    board[i-2][j+2] &
                    board[i-3][j+3]) == piece){
                        return true;
                }
            }
        }
        
        // check diagonal \
        for (int i = board.length-1; i >= 3 ; i--) {
            for (int j = board[0].length-1; j > 2; j--) {
                if((board[i][j] &
                    board[i-1][j-1] &
                    board[i-2][j-2] &
                    board[i-3][j-3]) == piece){
                        return true;
                }
            }
        }
        
        return false;
    }

    public boolean isTie(){
    	int sum = 0;
	for(int i = 0; i < heightPosition.length; i++){
	    sum += heightPosition[i];
	}
	if(-7 == sum)
	    return true;
	return false;
    }
    
    public int getHeight(int column){
        return heightPosition[column];
    }
    
    public Image getImage(){
        return image;
    }
    
    /***************************************************************************
     * Print string representation of board with height on bottom. Example
     * below shows player1 went in column 1 and player2 in column 4
     * 0000000
     * 0000000
     * 0000000
     * 0000000
     * 0000000
     * 0100200
     * 5455455
     **************************************************************************/
    @Override
    public String toString(){
        String returnVal = "";
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                returnVal += board[i][j];
            }
            returnVal += "\n";
        }
        for (int i = 0; i < heightPosition.length; i++) {
            returnVal += heightPosition[i];
        }
        
        return returnVal;
    }
    
}
