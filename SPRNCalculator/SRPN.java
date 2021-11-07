
/**
 * Program class for an SRPN calculator.
 */

public class SRPN {
  ///////////
  /*
  Fields 
  */
  DoubleStack numbers;
  FunctStack functors;
  StringStack output;
  StringStack outputRev;
  public DoubleStack memory;
  boolean commenting = false;

  ///////////
  /*
  SPRN constructor 
  */
  public SRPN(int max_inputs){
    numbers = new DoubleStack(max_inputs*2);
    functors = new FunctStack(max_inputs*2);
    output = new StringStack(max_inputs*2);
    outputRev = new StringStack(max_inputs*2);
    memory = new DoubleStack(max_inputs);
  }
  
  /////////////
  /*
  Function to process input into stacks of numbers & functions. Uses associativity and precedence to combine for an output stack, then run through the output while executing functions. Pushes final answers to a memory stack. 
  */
  public void processCommand(String command){
    command = " " + command + " ";
    boolean bufferFull = false;
    boolean removeNeg = false;//to skip a '-' and parse as neg num 
    command = (parseAsHashtag(command));//comment check
    while (command.length()>0){
      if (command.length()>0){
        String initialCommand = command;//command hold
        while (!numbers.isEmpty()){
          //dump any numbers to output ASAP; they have most precedence
          output.push(String.valueOf(numbers.pop()));
        }
        try{//parse characters as int until format error
          int i = 1;
          int j = 0;
          if (command.charAt(0) == '-'){
            j++;
            try{
              command = command.substring(1);
              removeNeg = true; 
            }catch (StringIndexOutOfBoundsException e){
              j--;
              removeNeg = false;
            }
          }else{
            removeNeg = false;
          }//'j' and removeNeg to allow parsing of negative numbers
          while (i+j<=initialCommand.length()){
            String testInput = String.valueOf(initialCommand.subSequence(0,i+j));
            //check lengthening subsequence until format exception
            if (Double.valueOf(testInput)>=Double.valueOf(Integer.MAX_VALUE)){//max value buffer
              if (i!=1){
                numbers.pop();
              }
              numbers.push(Integer.MAX_VALUE);
              bufferFull = true;
            }else{
              if (Double.valueOf(testInput)<=Double.valueOf(Integer.MIN_VALUE)){//min value buffer
                if (i!=1){
                  numbers.pop();
                }
                numbers.push(Integer.MIN_VALUE);
                bufferFull = true;
              }else{//inside buffer range
                double doubleUserInput = Double.valueOf(testInput);
                if (!bufferFull){
                  if (i!=1){
                    numbers.pop();
                  }
                  numbers.push(doubleUserInput);
                }
              }
            }
            command = command.substring(1);//shrink command by 1
            i++;
          }
        }catch(NumberFormatException n){//reached end of digits
          while (!numbers.isEmpty()){
            //dump any numbers to output ASAP; they have most precedence
            output.push(String.valueOf(numbers.pop()));
          }
          if (removeNeg){
            //if the 'number' removed from command was just '-', put it back
            command = "-" + command;
          }
          try{
            char currentChar = command.charAt(0);
            Functor functHold = new Functor(currentChar);
            if (!Character.isDigit(currentChar)){
              bufferFull = false;//disable 'reached buffer'
            }
            if (functHold.isFunctor()){
              while (!functors.isEmpty() && ( (functors.peek().getPrecedence()<functHold.getPrecedence()) || functors.peek().getPrecedence()==functHold.getPrecedence() && functHold.isLeftAssociative())){ 
                //while there are functors to pop, push functors of weaker precedence to output 
                output.push(String.valueOf(functors.pop().getMarker()));
              }
              functors.push(functHold);//push functor to memory
            }else{
              //non-digit and unknown function 
              //OR part of number longer than Integer can parse
              if (!functHold.isFunctor() && currentChar!=' ' && !Character.isDigit(currentChar)){
                System.out.println("Unrecognised operator or operand \""+String.valueOf(currentChar)+"\"");
              }
            }
            if (!Character.isDigit(currentChar)){
              command = command.substring(1);
            } 
          }catch(StringIndexOutOfBoundsException e){
            System.out.println("Command parsing error.");
            break;
          }
        }
      }
    }
    while (!numbers.isEmpty()){//dump any numbers to output
      output.push(String.valueOf(numbers.pop()));
    }
    while (!functors.isEmpty()){//dump any functors to output
      output.push(String.valueOf(functors.pop().getMarker()));
    }
    while (!output.isEmpty()){//invert output
      outputRev.push((output.pop()));
    }
    while (!outputRev.isEmpty()){
      //run through the inverted output, pushing to memory or operating on memory stack cells 
      String StringHold = outputRev.pop();
      if (!memory.isFull()){  
        try{
          memory.push(Double.valueOf(StringHold));
        }catch (NumberFormatException n){
          Functor currentFunct = new Functor(StringHold.toCharArray()[0]);
          if (currentFunct.isFunctor()){
            executeFunction(StringHold.toCharArray()[0]);
          }else{
            System.out.println("Functor error.");
          }
        }
      }else{
        //display and equals are the only functors able to operate on full memory stack
        if (StringHold.toCharArray()[0] == 'd' || StringHold.toCharArray()[0] == '='){
          Functor currentFunct = new Functor(StringHold.toCharArray()[0]);
          if (currentFunct.isFunctor()){
            executeFunction(StringHold.toCharArray()[0]);
          }else{
            System.out.println("Functor error.");
          }
        }else{
          //tried to execute function on full memory
          System.out.println("Stack overflow.");
        }
      }
    }
    
  }

  /////////////
  /*
  Function to cut out comments. 
  */
  public String parseAsHashtag(String input){
    String outString = new String();
    int i;
    //cycle through command. If a # is found with spaces or nothing to both sides then turn on/off commenting.
    for (i=0 ; i<input.length() ; i++){
      boolean startsCommenting = commenting;
      //if 'commenting' swaps then current char is # to be cut out
      if (input.charAt(i)=='#'){
        try{
          if (input.charAt(i-1)==' '){
            try{
              if (input.charAt(i+1)==' '){//"... # ..."
                commenting ^= true;

              }
            }catch(StringIndexOutOfBoundsException e){//"... #"
              commenting ^= true;
            }
          }
        }catch(StringIndexOutOfBoundsException e){
          try{
            if (input.charAt(i+1)==' '){//"# ..."
              commenting ^= true;
            }
          }catch(StringIndexOutOfBoundsException f){//"#"
            commenting ^= true;
          }
        }
      }
      if (!commenting && startsCommenting==commenting){
        outString += String.valueOf(input.charAt(i));
      }
    }
    return outString;
  }

  /////////////
  /*
  Function to execute a given functor 
  */
  public void executeFunction(char currentChar){
    Functor charHold = new Functor(currentChar);
    if (charHold.isFunctor()){//functor
      double pop1 = 0;
      double pop2 = 0;
      if (charHold.isStackShrinker()){//need to pop?
        boolean didpop = false;
        //double pop start
        if (!memory.isEmpty()){
          pop1 = memory.pop();
          if (!memory.isEmpty()){
            pop2 = memory.pop();
            //sucessful double pop
            didpop = true;
            // EXECUTE FUNCTION
            String functOutput = charHold.execute(pop1, pop2);
            //returns request for memory stack as String
            if (functOutput == "DivisionByZero"){
              if (!memory.isFull()){
                memory.push(pop2);
                memory.push(pop1);
              }else{
                System.out.println("Stack overflow.");
              }
              System.out.println("Divide by 0.");
            }else{
              try{
                double intHold = Double.valueOf(functOutput);
                if (!memory.isFull()){
                  memory.push(intHold);
                }else{
                  System.out.println("Stack overflow.");
                }
              }catch(NumberFormatException e){
                //catches functors that require poppping, but don't provide an output
                memory.push(pop2);
                memory.push(pop1);
                System.out.println("popped execution error");
              }
            }
          }else{
            memory.push(pop1);
          }
        }
        if (!didpop){
          System.out.println("Stack underflow.");
        }
      }else{//no need to pop
        char marker = charHold.getMarker();
        if (marker == '='){
          //try to peek numbers, then output, then memory
          if (!numbers.isEmpty()){
            System.out.println((int)numbers.peek());
          }else{
            if (!output.isEmpty() && Character.isDigit(output.peek().toCharArray()[0])){
              System.out.println(Double.valueOf(output.peek()).intValue());
            }else{
              if (!memory.isEmpty()){
                System.out.println((int)memory.peek());
              }else{
                System.out.println("Stack empty.");
              }
            }
          }
        }
        if (marker == 'd'){
          //display memory stack
          /*tried to *copy* memory and print pops, but pops affected original memory stack... couldn't fix... had to build new stack out here...*/
          double[] dummylifo = memory.getLifo();
          int headlocation = memory.getHead();
          int i=0;
          while (i<headlocation){
            System.out.println(String.valueOf((int)dummylifo[i]));
            i++;
          }
        }
        if (marker == 'r'){
          try{
            double intHold = Double.valueOf(charHold.execute(pop1, pop2));
            if (!memory.isFull()){
              memory.push((intHold));
            }else{
              System.out.println("Stack overflow.");
            }
          }catch (NumberFormatException e){
            ;//if 'r' doesn't return a number then skip
          }
        }
      }
    }else{//tried to execute non-functor character
      if (currentChar != ' ' && currentChar != 0){
        System.out.println("Unrecognised operator or operand \""+currentChar+"\"");
      }
    }
  }
}
