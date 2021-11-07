public class Board{
  private int[][] boardstate;
  int rows;
  int columns;
  int players;

  public Board(int Rows, int Columns, int Players){
    rows = Rows;
    columns = Columns;
    players = Players;
    boardstate = new int[rows][columns];
    int i; int j;
    for (i=0 ; i<rows ; i++){
      for (j=0 ; j<columns ; j++){
        boardstate[i][j]=-1;
      }
    }
  }

  public int getRows(){
    return rows;
  }
  
  public int getColumns(){
    return columns;
  }

  public int getPlayers(){
    return players;
  }

  public int[][] getBoardstate(){
    return boardstate;
  }

  public void chooseCell(int row, int column, int player) throws NoAccessToCellException{
    if (boardstate[row][column]==-1){  
      int i; int j;
      for (i = Math.max(row-1,0) ; i<=Math.min(row+1,rows-1) ; i++){
        for (j = Math.max(column-1,0) ; j<=Math.min(column+1,columns-1) ; j++){
          if (boardstate[i][j]==-1){
            boardstate[i][j] = player;
          }
        }
      }
    }else{
      throw (new NoAccessToCellException());
    }
  }
}