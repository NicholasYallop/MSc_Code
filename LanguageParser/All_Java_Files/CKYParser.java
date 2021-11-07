import java.util.*;
import computation.contextfreegrammar.*;
import computation.parser.*;
import computation.parsetree.*;
import computation.derivation.*;

public class CKYParser implements IParser {

  /* Function to check whether a word has a derivation in input grammar */
  public boolean isInLanguage(ContextFreeGrammar cfg, Word w){
    ArrayList<Rule>[][] CKYMatrix = generate(cfg,w);//get the CKY matrix for the word
    try{
      //if the entry for the subword of 'the start to the end of the word' contains the start symbol, the word can be generated from the grammar
      for (Rule r : CKYMatrix[0][w.length()-1]){
        if (r.getVariable()==cfg.getStartVariable()){
          return true;
        }
      }
      return false;
    }catch (Exception e){
      System.out.println("Error : CKYisInLanguage : Cell entry format error");
      return false;
    }
  }

  /* Function to return Cocke-Kasami-Younger matrix for given grammar and word */
  public ArrayList<Rule>[][] generate(ContextFreeGrammar cfg, Word w){
    ArrayList<Rule>[][] SolutionMatrix = (ArrayList<Rule>[][]) new ArrayList[w.length()][w.length()];
    //^^ n*n matrix as outlined in CKY theory
    for (int CellWordLength=1 ; CellWordLength<=w.length() ; CellWordLength++){ //Define length of subword to consider
      for (int i=0 ; i<w.length() ; i++){ //cycle from right to left
        int j = i+(CellWordLength-1); //y=mx+c ; m=1 ; c is word length & defines the offset from diagonal
        if (0<=i && i<w.length() && 0<=j && j<w.length()){ //i,j is in the matrix
          SolutionMatrix[i][j] = new ArrayList<>(); //Instantiate each matrix entry
          if (i!=j){
            if (j>=0 && i<j){
              Word CellWord = w.subword(i,j);
              for (int k=0 ; k<CellWord.length() ; k++){//index of breakpoint between parts of subword
                //for every rule in the grammar that extends the word, check that it expands into the variables in i,i+k and i+k+1,j i.e. the two subwords can concatenate using this rule -- if so, add that rule to i,j
                for (Rule cfgr : cfg.getRules()){
                  if (cfgr.getExpansion().length() == 2){
                    for (Rule r1 : SolutionMatrix[i][i+k]){
                      for (Rule r2 : SolutionMatrix[i+k+1][j]){
                        if (r1.getVariable()==cfgr.getExpansion().get(0) &&  r2.getVariable()==cfgr.getExpansion().get(1)){
                          SolutionMatrix[i][j].add(cfgr);
                        }
                      }
                    }
                  }
                }
              }          
            }
          }else{//along the diagonal add any rule that steps directly to the ith symbol in the word
            Word CellWord = new Word(w.get(i));
            for (Rule r : cfg.getRules()){  
              if (r.getExpansion().equals(CellWord)){
                SolutionMatrix[i][j].add(r); 
              }
            }
          }
        }
      }
    }
    return SolutionMatrix;
  }

  /* Function to generate tree using Cocke-Kasami-Younger method */
  public ParseTreeNode generateParseTree(ContextFreeGrammar cfg, Word w){
    ArrayList<Rule>[][] CKYMatrix = generate(cfg,w); //Matrix of variables that generate subword(i,j) of w
    List<List<ParseTreeNode>> ParallelTrees = new ArrayList<List<ParseTreeNode>>(); 
    //^^to store multiple lists of children in a given instance of the trees, we'll pick the one with with the biggest tree
    //  a list of nodes is a single tree being built and should have set length
    ArrayList<Integer> RuleAmounts = new ArrayList<Integer>(); //used to judge the size of the largest tree in a node list
    List<Word> StartWords = new ArrayList<Word>(); //used to ensure only correct children lists are passed on
    ParallelTrees.add(new ArrayList<ParseTreeNode>());//instantiation with empty tree list
    RuleAmounts.add(0);//instantiation with 0
    StartWords.add(new Word());//instantiation with empty word
    boolean TreeExists = false;//START OF NON-NULL TEST
    for (Rule r : CKYMatrix[0][w.length()-1]){
      if (r.getVariable()==cfg.getStartVariable()){
        TreeExists = true;
      }
    }
    if (!TreeExists){
      System.out.println("No tree exists for "+ w.toString());
      return (new ParseTreeNode(cfg.getStartVariable())); //If no derivations then escape
    }                          //END OF NON-NULL TEST
    for (int i=0 ; i<w.length() ; i++){ //iterator to create the first list of nodes from the CKY diagonal
      List<List<ParseTreeNode>> PTtemp = new ArrayList<List<ParseTreeNode>>(); //next step of node lists
      List<Integer> RAtemp = new ArrayList<Integer>();//next step of rulle amounts list
      List<Word> SWtemp = new ArrayList<Word>();//next step of start word list
      int l=0;
      while (l<ParallelTrees.size()){//cycle over trees
        for (Rule r : CKYMatrix[i][i]){//cycle over rules on diagonal
          if (!(r.getVariable()==cfg.getStartVariable()) || !(w.length()==1)){//no need to go straight to start variable
            List<ParseTreeNode> DummyTree= new ArrayList<ParseTreeNode>(ParallelTrees.get(l)); 
            DummyTree.add(new ParseTreeNode(r.getVariable(),new ParseTreeNode(r.getExpansion().get(0))));//create copy of list with variable-terminal pair
            if (!PTtemp.contains(DummyTree)){
              PTtemp.add(new ArrayList<ParseTreeNode>(DummyTree));//add new list to next step
              RAtemp.add(new Integer(RuleAmounts.get(l)+1));//add new rule apllication entry 
              if (StartWords.get(l).length()>0){
                SWtemp.add(new Word(StartWords.get(l).toString() + r.getExpansion().get(0).toString()));//concatenate terminal with word entry
              }else{
                SWtemp.add(new Word(r.getExpansion().get(0)));//add terminal to word entry
              }
            }
          }
        }
        l+=1;
      }
      ParallelTrees = new ArrayList<List<ParseTreeNode>>(PTtemp);//move along step
      RuleAmounts = new ArrayList<Integer>(RAtemp);//move along step
      StartWords = new ArrayList<Word>(SWtemp);//move along step
    }
    //Iterator to remove trees that don't generate the word
    Iterator<List<ParseTreeNode>> PTiterator = ParallelTrees.iterator();
    Iterator<Word> SWiterator = StartWords.iterator();
    while (PTiterator.hasNext() && SWiterator.hasNext()){
      List<ParseTreeNode> TreeHold = PTiterator.next();
      Word SWHold = SWiterator.next();
      if (!SWHold.equals(w)){
        PTiterator.remove();
        SWiterator.remove();
      }
    }
    if (ParallelTrees.size()==0){
      System.out.println("no start words");
    }

    // Comment below can be used to print out all trees 
    /*
    for (List<ParseTreeNode> lTemp : ParallelTrees){
      if (lTemp.size()==w.length()){
        for (ParseTreeNode nTemp : lTemp){
          nTemp.print();
        }
      }
      System.out.println("-----------------------------------");
    }
    */
    
    for (int SubwordLength=2 ; SubwordLength<=w.length() ; SubwordLength++){ //Iterator to over subword length
      float Progress = 100*(SubwordLength -2)/(w.length()-2);
      System.out.println("Processing... "+Progress+"%");
      for (int i=0 ; i<w.length() ; i++){ //cycle from left to right in CKY Matrix
        int j = i + (SubwordLength-1); //subword is ith to jth elements in w
        if (0<=i && i<w.length() && 0<=j && j<w.length() && i<j){ //i,j is in matrix
          List<List<ParseTreeNode>> PTtemp = new ArrayList<List<ParseTreeNode>>();//next step to populate with all new trees 
          List<Integer> RAtemp = new ArrayList<Integer>();//next step to populate with all new rule amounts
          for (int k=0 ; k<(SubwordLength-1) ; k++){ //cycle through breakpoints inside subword
            int l=0;
            while (l<ParallelTrees.size()){//cycle over all trees
              PTtemp.add(new ArrayList<ParseTreeNode>(ParallelTrees.get(l)));//add current tree to next step
              RAtemp.add(RuleAmounts.get(l));//add current rule amount to next step
              for (Rule r : CKYMatrix[i][j]){
                if (r.getExpansion().length()==2){ //cycle through rules that lengthen word
                  for (Rule r1 : CKYMatrix[i][i+k]){ //cycle through rules that make ith to i+kth elements
                    for (Rule r2 : CKYMatrix[i+k+1][j]){ //cycle through rules that make i+k+1th to jth elements
                      if (r1.getVariable().equals(r.getExpansion().get(0)) && r2.getVariable().equals(r.getExpansion().get(1)) && r.getExpansion().get(0).equals(ParallelTrees.get(l).get(i).getSymbol()) && r.getExpansion().get(1).equals(ParallelTrees.get(l).get(i+k+1).getSymbol())){
                      //^^check ith to i+kth rule is applied on the first in ith to jth rule's expansion
                      //^^check i+k+1th to jth rule rule is applied on the second in ith to jth rule's expansion
                      //If both; r expands into r1 & r2 i.e. use CKY entries to select only possible rule applications
                      //^^check r creates the parents ith and i+kth nodes in the current list
                      //If so; combine the two nodes as childen under the variable of the rule
                        List<ParseTreeNode> DummyTree = new ArrayList<ParseTreeNode>(ParallelTrees.get(l));
                        DummyTree.set(i,new ParseTreeNode(r.getVariable(), ParallelTrees.get(l).get(i),ParallelTrees.get(l).get(i+k+1)));//create new tree with rule applied
                        DummyTree.set(i+k+1, new ParseTreeNode(new Terminal('.')));//delete second half subword in new tree
                        if (!PTtemp.contains(DummyTree)){
                          PTtemp.add(new ArrayList<ParseTreeNode>(DummyTree));//add to next step
                          RAtemp.add(RuleAmounts.get(l)+1);//add to next step
                        }
                      }
                    }
                  }
                }
              }
              l+=1;
            }
            ParallelTrees = new ArrayList<List<ParseTreeNode>>(PTtemp);//move along step
            RuleAmounts = new ArrayList<Integer>(RAtemp);//move along step
          }
        }
      } 
      //Iterator to remove trees without a long enough subword for the next diagonal in the matrix  
      PTiterator = ParallelTrees.iterator();
      Iterator<Integer> RAiterator = RuleAmounts.iterator();
      while (PTiterator.hasNext() && RAiterator.hasNext()){
        List<ParseTreeNode> TreeHold = PTiterator.next();
        Integer RAHold = RAiterator.next();
        if (RAHold!=(SubwordLength-1+w.length())){
          PTiterator.remove();
          RAiterator.remove();
        }
      }
      
      if (ParallelTrees.size()==0){
        System.out.println("No tree found");
        return (new ParseTreeNode(cfg.getStartVariable()));
      } 
    }
    //cycle through all trees, find the one with most rule applications & starts with the start variable and return it
    int OutputTreeIndex = 0;
    for (int l=0 ; l<ParallelTrees.size() ; l++){
      if (RuleAmounts.get(l)>RuleAmounts.get(OutputTreeIndex) && cfg.getStartVariable().equals(ParallelTrees.get(l).get(0).getSymbol())){
        OutputTreeIndex = l;
      }
    }
    return ParallelTrees.get(OutputTreeIndex).get(0);
  } 
}