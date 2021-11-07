public class Game{
  
  private Board board;
  
  public Game(){
    board = new Board(6,6,2);
  }

  public void userInput(int row, int column, int player) throws NoAccessToCellException{
    try{
      board.chooseCell(row,column,player);
    }catch (NoAccessToCellException a){
      throw (a);
    }
  }

  public Board getBoard(){
    return board;
  }
}