/*
class to define and execute accepted functors
*/

import java.util.concurrent.ThreadLocalRandom;

public class Functor{
  private int function;//unique int for each functor
  private char marker;//human-legible representation of functor 
  private int precedence;//precedence of given functor instance
  private boolean isFunctor;//whether character is known functor
  private boolean stackShrinker;//whether can operate on full stack
  private boolean leftAssociative;
  private char[] functors = new char[] {'^','*','/','%','+','-','=','d','r'};
  private int[] precedences = new int[] {1, 2, 2, 2, 3, 3, 0, 4, 4};
  //////
  /*
  Constructor
  */
  public Functor(char c){
    isFunctor = false;
    int j;
    for (j=0 ; j<functors.length ; j++){
      if (c==functors[j]){
        isFunctor = true;
        function = j;
        marker = functors[j];
        if (j<6){
          stackShrinker = true;
        }else{
          stackShrinker = false;
        }
        if (j==0 || j==6){
          leftAssociative = false;
        }else{
          leftAssociative = true;
        }
        if (j<precedences.length){
          precedence = precedences[j];
        }
      }
    }
  }
  //////
  /*
  Function to execute current functor instance
  */
  public String execute(double pop1, double pop2){
    if (isFunctor){
      if (function<6){
        double out = 0;
        double Pop1 = pop1;
        double Pop2 = pop2;
        if (marker == '+'){//addition;
          out = (Pop2+Pop1);
        }if (marker == '-'){//subtraction
          out = (Pop2-Pop1);
        }if (marker == '/'){//division
          if (pop1 == 0){
            return("DivisionByZero");
          }else{
            out = (Pop2/Pop1);
          }
        }if (marker == '*'){//multiplication
          out = (Pop2*Pop1);
        }if (marker == '%'){//modulo
          out = (Pop2%Pop1);
        }if (marker == '^'){//exponents
          out = Math.pow(Pop2,Pop1);
        }
        if (out > 2147483647){
          return(String.valueOf(2147483647));
        }else{
          if (out < (-2147483648)){
            return(String.valueOf(-2147483648));
          }else{
            return(String.valueOf(out));
          }
        }
      }
      else{
        if (function == 6){//equals
            return("equals");
        }if (function == 7){//display
          return("display");
        }if (function == 8){//random
          return(String.valueOf(ThreadLocalRandom.current().nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE)));
        }
        return "functor error";
      }
    }
    return("not functor");
  }
  //////
  /*
  ACCESSORS
  */
  public int getFunction(){
    return function;
  }
  public char getMarker(){
    return marker;
  }
  public int getPrecedence(){
    return precedence;
  }
  public boolean isFunctor(){
    return isFunctor;
  }
  public boolean isStackShrinker(){
    return stackShrinker;
  }
  public boolean isLeftAssociative(){
    return leftAssociative;
  }
}