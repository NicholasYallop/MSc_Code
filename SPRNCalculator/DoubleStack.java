/*
Stack
Sub class
Accepts doubles 
*/

public class DoubleStack extends Stack{
  private double[] lifo;
  
  public DoubleStack(int initial_size){
    super(initial_size);
    lifo = new double[initial_size];
  }

  public void push(double data){
    if (head < max_size){
      lifo[head] = data;
      head++;
    }
  }
  public double pop(){
    if (!this.isEmpty()){
      double out;
      out = lifo[head-1];
      head--;
      return out;
    }
    return 0;
  }
  public double peek(){
    if (!this.isEmpty()){
      double out;
      out = lifo[head-1];
      return out;
    }
    return 0;
  }
  public double[] getLifo(){
    return lifo;
  }
}