public class NoAccessToCellException extends Exception{
  public NoAccessToCellException(){
    super("Player cannot access cell");
  }
}