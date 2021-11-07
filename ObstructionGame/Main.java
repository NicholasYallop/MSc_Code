
import java.io.*;
/* 
 * Building on the discussion of coupling and cohesion for 
 * the O's and X's (tic-tac-toe) game.  Implement the game
 * Obstruction 
 * 
 * http://www.papg.com/show?2XMX 
 * http://www.lkozma.net/game.html 
 */ 

class Main {
  public static void main(String[] args) {
    Game game = new Game();
    boolean GameWon = false;
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in)); 
    boolean AcceptInput;
    int row =0;
    int rows = game.getBoard().getRows();
    int column = 0; 
    int columns = game.getBoard().getColumns();
    int player=0;
    int players = game.getBoard().getPlayers();
    int[][] boardstate = game.getBoard().getBoardstate();

    try {
      //Keep on accepting input from the command-line
      while(!GameWon) {
        AcceptInput = false; row = -1;
        while (!AcceptInput){
          System.out.println("Input row number (less than 7):");
          String command = reader.readLine();
          if(command == null){
            //Exit code 0 for a graceful exit
            System.exit(0);
          }
          try{
            row = Integer.valueOf(command)-1;
          }catch (NumberFormatException n){
            ;
          }
          if (0<=row && row<rows){
            AcceptInput = true;
          }else{
            System.out.println("Try again.");
          }
        }

        AcceptInput = false; column = -1;
        while (!AcceptInput){
          System.out.println("Input column number (less than 7):");
          String command = reader.readLine();
          if(command == null){
            //Exit code 0 for a graceful exit
            System.exit(0);
          }
          try{
            column = Integer.valueOf(command)-1;
          }catch (NumberFormatException n){
            ;
          }
          if (0<=column && column<columns){
            AcceptInput = true;
          }else{
            System.out.println("Try again.");
          }
        }
        try{
          game.userInput(row,column,player);
        }catch (NoAccessToCellException a){
          System.out.println("Cell occupied. Try again.");
          continue;
        }
        player = (player+1)%players;
        System.out.println(player);
        Display.sysout(game.getBoard());
        GameWon = true;
        int i; int j;
        for (i=0 ; i<rows ; i++){
          for (j=0 ; j<columns ; j++){
            if (boardstate[i][j]==-1){
              GameWon = false;
            }
          }
        }     
      }
    } 
    catch(IOException e) {
      System.err.println(e.getMessage());
      System.exit(1);
    }
  }
}