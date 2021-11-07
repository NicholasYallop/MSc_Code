import java.util.*;
import computation.contextfreegrammar.*;
import computation.parser.*;
import computation.parsetree.*;
import computation.derivation.*;

public class Parser implements IParser{

  CKYParser TempCKY = new CKYParser(); //Create new CKYParser instance
  BruteParser TempBrute = new BruteParser();//Create new CKYParser instance
  /*
  *!!!!!           USE_CKY controls which algorithm is used           !!!!!
  */
  private static final boolean USE_CKY = true;

  /* Function to call the isInLanguage in the respective algorithm class */
  public boolean isInLanguage(ContextFreeGrammar cfg, Word w){
    if (USE_CKY){
      return TempCKY.isInLanguage(cfg,w);
    }else{
      return TempBrute.isInLanguage(cfg,w);
    }
  }
  
  /* Function to call the generateParseTree in the respective algorithm class */
  public ParseTreeNode generateParseTree(ContextFreeGrammar cfg, Word w){
    if (USE_CKY){
      return TempCKY.generateParseTree(cfg,w);
    }else{
      return TempBrute.generateParseTree(cfg,w);
    }
  }
}