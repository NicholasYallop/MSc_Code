import java.util.*;
import computation.contextfreegrammar.*;
import computation.parser.*;
import computation.parsetree.*;
import computation.derivation.*;

public class BruteParser implements IParser {
   
  private List<Derivation> generatedDerivations = new ArrayList<>(); //Most recently generated batch of possible derivations
  private ContextFreeGrammar generatingGrammar; //Most recently used grammar
  private int generatedLength; //Most recently used word length

  /* Function to populate a list of derivations given a current list and  derivation index, rule index and rule application indicies */
  private void populateNextDerivation(ContextFreeGrammar cfg, List<Derivation> d, List<Derivation> dNext, int j, int k, int l){
    Derivation DerivationToAdd = new Derivation(d.get(j));
    DerivationToAdd.addStep(d.get(j).getLatestWord().replace(l,cfg.getRules().get(k).getExpansion()),cfg.getRules().get(k),l);
    dNext.add(DerivationToAdd);
  }

  /* Function to populate generatedDerivations using brute force method */
  public void generate(int n, ContextFreeGrammar cfg){
    generatingGrammar = cfg;
    generatedLength = n; 
    List<Derivation> Derivations = new ArrayList<>(); //list of derivations to populate
    Derivations.add(new Derivation(new Word(cfg.getStartVariable()))); //derivation list starts off with just start variable
    List<Derivation> NextDerivations = new ArrayList<>(); //list of derivations to hold
    for (int i=0 ; i<((2*n)-1) ; i++){ //cycle step count
      for (int j=0 ; j<Derivations.size() ; j++){ //cycle through current Derivations list
        for (int k=0 ; k<cfg.getRules().size() ; k++){ //cycle through cfg's rules
          for (int l=0 ; l<Derivations.get(j).getLatestWord().length() ; l++){ //cycle through the selected word's symbols
            if (i<n-1){ //for the first n-1 steps, we add variables to get the required length
              if (cfg.getRules().get(k).getExpansion().length()==2 && cfg.getRules().get(k).getVariable()==Derivations.get(j).getLatestWord().get(l)){ //if the rule increases length, and it's left-side variable appears in the word: add the word with the rule applied to next Derivations
                if (!NextDerivations.contains(Derivations.get(j).getLatestWord().replace(l,cfg.getRules().get(k).getExpansion()))){
                  populateNextDerivation(cfg, Derivations, NextDerivations, j,k,l);
                }
              }
            }
            else{ //on and after the nth step, we turn variables into terminals
              if (cfg.getRules().get(k).getExpansion().length()==1 && cfg.getRules().get(k).getVariable()==Derivations.get(j).getLatestWord().get(l)){//if the rule does not increase length, and it's left-side variable appears in the word: add the word with the rule applied to next Derivations
                if (!NextDerivations.contains(Derivations.get(j).getLatestWord().replace(l,cfg.getRules().get(k).getExpansion()))){
                  populateNextDerivation(cfg, Derivations, NextDerivations, j,k,l);
                }
              }
            }
          }
        }
      }
      Derivations = NextDerivations; //Derivations becomes next Derivations
      NextDerivations = new ArrayList<>(); //next Derivations is cleared for next iteration
    }
    generatedDerivations = Derivations;
  }

  /* Function to check whether a word has a derivation in input grammar */
  public boolean isInLanguage(ContextFreeGrammar cfg, Word w){  
    try{
      if (!cfg.equals(generatingGrammar) || (w.length()!=generatedLength)){
        this.generate(w.length(), cfg);
      }
    }catch (Exception e){
      this.generate(w.length(), cfg);
    }
    for (int m=0 ; m<generatedDerivations.size() ; m++){
      if (generatedDerivations.get(m).getLatestWord().equals(w)){
        return true; //return true if any entry in Derivations is equal to w
      }
    }
    return false; //return false if no reason to return true was found
  }

  
  /* Function to generate tree using brute force method */
  public ParseTreeNode generateParseTree(ContextFreeGrammar cfg, Word w){
    this.generate(w.length(), cfg);
    List<Derivation> wDerivations = new ArrayList<>(); //List to populate with possible derivations of w
    List<ParseTreeNode> Trees = new ArrayList<>(); //Trees list for final output
    for (Derivation d : generatedDerivations){
      if (d.getLatestWord().equals(w)){
        wDerivations.add(d); //Populate wDerivations 
      }
    }
    if (wDerivations.size()==0){ //Non-null test
      System.out.println("No tree exists for "+ w.toString());
      return (new ParseTreeNode(cfg.getStartVariable())); //If no derivations then escape
    }
    for (Derivation d : wDerivations){
      List<ParseTreeNode> NodeHolder = new ArrayList<>();
      for (int i=0 ; i<d.getLatestWord().length() ; i++){
        NodeHolder.add(new ParseTreeNode(d.getLatestWord().get(i))); //Create parents at all terminals
      }
      Iterator<Step> sIterator =  d.iterator();
      while (sIterator.hasNext()){
        Step CurrentStep = sIterator.next();
        if (CurrentStep.getRule()!=null){ //If no rule was applied, no step to undo
          if (CurrentStep.getRule().getExpansion().get(0).equals(CurrentStep.getWord().get(CurrentStep.getIndex()))){ //Check rule application is lined up
            if (CurrentStep.getRule().getExpansion().length() > 1){ //Does the rule lengthen the word?
              if (CurrentStep.getRule().getExpansion().get(1).equals(CurrentStep.getWord().get(CurrentStep.getIndex()+1))){
              //Does the second symbol in the rule's expansion line up too?
                ParseTreeNode NodeToAdd = new ParseTreeNode(CurrentStep.getRule().getVariable(),NodeHolder.get(CurrentStep.getIndex()),NodeHolder.get(CurrentStep.getIndex()+1)); //Combine the two children under the new parent
                NodeHolder.set(CurrentStep.getIndex(), NodeToAdd); //Replace the first child with the parent
                NodeHolder.remove(CurrentStep.getIndex()+1); //Delete second child
              }
              else{
                System.out.println("Error: TreeMaker : No second variable to replace");
              }
            }
            else{
              ParseTreeNode NodeToAdd = new ParseTreeNode(CurrentStep.getRule().getVariable(),NodeHolder.get(CurrentStep.getIndex())); //Create parent over child
              NodeHolder.set(CurrentStep.getIndex(), NodeToAdd); //Replace child with parent
            }
          }
          else{
            System.out.println("Error : TreeMaker : Word does not have variable at specified index");
          }
        }
      }
      if (NodeHolder.size()==1 && !Trees.contains(NodeHolder.get(0))){
        Trees.add(NodeHolder.get(0)); //Add created tree to output dump
      }
      else if (!Trees.contains(NodeHolder.get(0))){
        System.out.println("Error : TreeMaker : Tree doesn't have single parent");
      }
    }
    return Trees.get(0); //Return first tree in output dump
  }
}