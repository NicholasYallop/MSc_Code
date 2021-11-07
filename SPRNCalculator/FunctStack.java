/*
Stack
Sub class
Accepts Functors class 
*/

public class FunctStack extends Stack{
  private Functor[] lifo;

  public FunctStack(int initial_size){
    super(initial_size);
    lifo = new Functor[max_size];
  }

  public void push(Functor data){
    if (head < max_size){
      lifo[head] = data;
      head++;
    }
  }
  public Functor pop(){
    if (!this.isEmpty()){
      Functor out = lifo[head-1];
      head--;
      return out;
    }
    return (new Functor('a'));
  }
  public Functor peek(){
    if (!this.isEmpty()){
      Functor out = lifo[head-1];
      return out;
    }
    return (new Functor('a'));
  }
  public Functor[] getLifo(){
    return lifo;
  }
}