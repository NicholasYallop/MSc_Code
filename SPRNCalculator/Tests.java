import org.junit.*;
import org.junit.Assert.*;
import org.junit.runner.*;

public class Tests{
  SRPN TestSRPN = new SRPN(23);
  private static final int[] RAND_LIST = new int[] {1804289383,846930886,1681692777,1714636915,1957747793,424238335,719885386,1649760492,596516649,1189641421,1025202362,1350490027,783368690,1102520059,2044897763,1967513926,1365180540,1540383426,304089172,1303455736,35005211,521595368};
  @Before
  public void beforeTests(){
  }
  @Test
  public void RandTest(){
    for (int i=0 ; i<RAND_LIST.length ; i++){
      TestSRPN.processCommand("r");
      Assert.assertEquals(RAND_LIST[i], (int)TestSRPN.memory.peek());
    }
  }

  @Test 
  public void CommentTest(){
    TestSRPN.processCommand("# this is");
    TestSRPN.processCommand("a comment #");
    Assert.assertEquals(0, (int)TestSRPN.memory.peek());
  }

  @Test 
  public void HexTest(){
    TestSRPN.processCommand("0014");
    Assert.assertEquals(12, (int)TestSRPN.memory.peek());
  }
}