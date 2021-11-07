/*
Stack
Super class
*/

public class Stack{
  protected int head;
  protected int max_size;

  public Stack(int initial_size){
    max_size = initial_size;
    head = 0;
  }

  public boolean isEmpty(){
    return (head == 0);
  }
  public boolean isFull(){
    return (head == max_size);
  }
  public int getHead(){
    return head;
  }
}
