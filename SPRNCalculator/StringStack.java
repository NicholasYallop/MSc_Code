/*
Stack
Sub class
Accepts Strings 
*/

public class StringStack extends Stack{
  private String[] lifo;

  public StringStack(int initial_size){
    super(initial_size);
    lifo = new String[max_size];
  }

  public void push(String data){
    if (head < max_size){
      lifo[head] = data;
      head++;
    }
  }
  public String pop(){
    if (!this.isEmpty()){
      String out = lifo[head-1];
      head--;
      return out;
    }
    return null;
  }
  public String peek(){
    if (!this.isEmpty()){
      String out = lifo[head-1];
      return out;
    }
    return null;
  }
  public String[] getLifo(){
    return lifo;
  }
}