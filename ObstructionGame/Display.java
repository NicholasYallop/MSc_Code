public class Display{

  public static void sysout(Board b){
    int[][] boardstate = b.getBoardstate();
    String output;
    int i; int j;
    int rows = b.getRows();
    int columns = b.getColumns();
    for (i= 0 ; i<rows ; i++){
      output = "|";
      for (j=0 ; j<columns ; j++){
        output += " ";
        if (boardstate[i][j]>=0){
          output += boardstate[i][j];
        }else{
          output += " ";
        }
        output += " |";
      }
      System.out.println(output);
    }
  }
}