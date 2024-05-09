import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class JunitALU {
    Word word1 = new Word(), word2 = new Word(), word3 = new Word(), word4 = new Word();
    ALU alu;

    @Before
    public void init(){
        word1.set(0);
        word2.set(0);
        alu = new ALU(word1,word2);
    }

    @Test
    public void AddTest(){
        word1.set(12);
        word2.set(4);
        Assert.assertEquals(alu.add(word1, word2).getSigned(),16);

        word1.set(256);
        word2.set(42);
        Assert.assertEquals(alu.add(word1, word2).getSigned(),298);

        word1.set(-256);
        word2.set(42);
        Assert.assertEquals(alu.add(word1, word2).getSigned(),-214);

        word1.set(256);
        word2.set(-42);
        Assert.assertEquals(alu.add(word1, word2).getSigned(),214);
    }

    @Test
    public void Add4Test(){
        word1.set(283);
        word2.set(123);
        word3.set(124);
        word4.set(421);
        Assert.assertEquals(alu.add4(word1, word2, word3, word4).getSigned(),951);
    }

    @Test
    public void SubtractTest(){
        word1.set(12);
        word2.set(4);
        Assert.assertEquals(alu.subtract(word1, word2).getSigned(),8);

        word1.set(4);
        word2.set(12);
        Assert.assertEquals(alu.subtract(word1, word2).getSigned(),-8);

        word1.set(23094);
        word2.set(1239);
        Assert.assertEquals(alu.subtract(word1, word2).getSigned(),21855);

        word1.set(94);
        word2.set(-1239);
        Assert.assertEquals(alu.subtract(word1, word2).getSigned(),1333);
    }

    @Test 
    public void MultiplyTest(){
        word1.set(12);
        word2.set(4);
        Assert.assertEquals(alu.multiply(word1, word2).getSigned(),48);
        word1.set(12);
        word2.set(-4);
        Assert.assertEquals(alu.multiply(word1, word2).getSigned(),-48);
        word1.set(132);
        word2.set(-412);
        Assert.assertEquals(alu.multiply(word1, word2).getSigned(),-54384);
    }

    @Test (expected = AssertionError.class)
    public void MultiplyOverflowTest(){
        word1.set(-12);
        word2.set(-4);
        Assert.assertEquals(alu.multiply(word1, word2).getSigned(),-48);
    }

    @Test 
    public void AndTest() throws Exception{
        Bit[] code = new Bit[]{new Bit(false),new Bit(false),new Bit(false),new Bit(true)};
        alu.op1.set(123);
        alu.op2.set(456);
        alu.doOperation(code);
        Assert.assertEquals(alu.result.getSigned(),72);
        alu.op1.set(11111);
        alu.op2.set(4522226);
        alu.doOperation(code);
        Assert.assertEquals(alu.result.getSigned(),98);
    }

    @Test 
    public void XorTest() throws Exception{
        Bit[] code = new Bit[]{new Bit(false),new Bit(true),new Bit(false),new Bit(true)};
        alu.op1.set(123);
        alu.op2.set(456);
        alu.doOperation(code);
        Assert.assertEquals(alu.result.getSigned(),435);
        alu.op1.set(98132);
        alu.op2.set(12398);
        alu.doOperation(code);
        Assert.assertEquals(alu.result.getSigned(),85818);
    }

    @Test 
    public void OrTest() throws Exception{
        Bit[] code = new Bit[]{new Bit(true),new Bit(false),new Bit(false),new Bit(true)};
        alu.op1.set(123);
        alu.op2.set(456);
        alu.doOperation(code);
        Assert.assertEquals(alu.result.getSigned(),507);
        alu.op1.set(9832);
        alu.op2.set(12);
        alu.doOperation(code);
        Assert.assertEquals(alu.result.getSigned(),9836);
    }

    @Test 
    public void NotTest() throws Exception{
        Bit[] code = new Bit[]{new Bit(true),new Bit(true),new Bit(false),new Bit(true)};
        alu.op1.set(-1);
        alu.op2.set(456);
        alu.doOperation(code);
        Assert.assertEquals(alu.result.getSigned(),0);
        alu.op1.set(12310);
        alu.op2.set(456);
        alu.doOperation(code);
        Assert.assertEquals(alu.result.getSigned(),-12311);
    }

    @Test 
    public void LeftShiftTest() throws Exception{
        Bit[] code = new Bit[]{new Bit(false),new Bit(false),new Bit(true),new Bit(true)};
        alu.op1.set(34525);
        alu.op2.set(10);
        alu.doOperation(code);
        Assert.assertEquals(alu.result.getSigned(),35353600);
        alu.op1.set(213);
        alu.op2.set(1);
        alu.doOperation(code);
        Assert.assertEquals(alu.result.getSigned(),426);
    }

    @Test 
    public void RightShiftTest() throws Exception{
        Bit[] code = new Bit[]{new Bit(true),new Bit(false),new Bit(true),new Bit(true)};
        alu.op1.set(34525);
        alu.op2.set(10);
        alu.doOperation(code);
        Assert.assertEquals(alu.result.getSigned(),33);
        alu.op1.set(14209);
        alu.op2.set(6);
        alu.doOperation(code);
        Assert.assertEquals(alu.result.getSigned(),222);
    }
}
