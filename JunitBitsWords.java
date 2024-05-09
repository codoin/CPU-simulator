import org.junit.Assert;
import org.junit.Test;

public class JunitBitsWords {

    @Test
    public void BitSetTest(){
        Bit test = new Bit(false);
        Assert.assertFalse(test.getValue());
        test.set(true);
        Assert.assertTrue(test.getValue());
        test.set(false);
        Assert.assertFalse(test.getValue());
        test.set();
        Assert.assertTrue(test.getValue());
    }

    @Test
    public void BitToggleTest(){
        Bit test = new Bit(true);
        test.toggle();
        Assert.assertFalse(test.getValue());
        test.toggle();
        Assert.assertTrue(test.getValue());
    }

    @Test
    public void BitClearTest(){
        Bit test = new Bit(true);
        test.clear();
        Assert.assertFalse(test.getValue());
    }

    @Test
    public void BitAndTest(){
        Bit bit1 = new Bit(true);
        Bit bit2 = new Bit(true);
        Bit result = bit1.and(bit2);

        Assert.assertTrue(result.getValue());

        bit1.toggle();
        result = bit1.and(bit2);

        Assert.assertFalse(result.getValue());

        bit2.toggle();
        result = bit1.and(bit2);

        Assert.assertFalse(result.getValue());

        bit2.toggle();
        result = bit1.and(bit2);

        Assert.assertFalse(result.getValue());
    }

    @Test
    public void BitOrTest(){
        Bit bit1 = new Bit(true);
        Bit bit2 = new Bit(true);
        Bit result = bit1.or(bit2);

        Assert.assertTrue(result.getValue());

        bit1.toggle();
        result = bit1.or(bit2);

        Assert.assertTrue(result.getValue());

        bit2.toggle();
        result = bit1.or(bit2);

        Assert.assertFalse(result.getValue());

        bit2.toggle();
        result = bit1.or(bit2);

        Assert.assertTrue(result.getValue());
    }

    @Test
    public void BitXorTest(){
        Bit bit1 = new Bit(true);
        Bit bit2 = new Bit(true);
        Bit result = bit1.xor(bit2);

        Assert.assertFalse(result.getValue());

        bit1.toggle();
        result = bit1.xor(bit2);

        Assert.assertTrue(result.getValue());

        bit2.toggle();
        result = bit1.xor(bit2);

        Assert.assertFalse(result.getValue());

        bit2.toggle();
        result = bit1.xor(bit2);

        Assert.assertTrue(result.getValue());
    }

    @Test
    public void BitNotTest(){
        Bit bit1 = new Bit(true);
        Bit result = bit1.not();
        Assert.assertFalse(result.getValue());
        result = result.not();
        Assert.assertTrue(result.getValue());
    }

    @Test
    public void BitToStringTest(){
        Bit bit1 = new Bit(true);
        Assert.assertEquals("t",bit1.toString());
        bit1.toggle();
        Assert.assertEquals("f",bit1.toString());
    }

    @Test
    public void WordSetAndGetTest(){
        Word test = new Word();
        test.setBit(0, new Bit(true));
        Bit bit = test.getBit(0);
        Assert.assertTrue(bit.getValue());
        test.setBit(5, new Bit(false));
        bit = test.getBit(5);
        Assert.assertFalse(bit.getValue());
    }

    @Test
    public void WordAndTest(){
        Word word1 = new Word();
        Word word2 = new Word();
        word1.set(-1); //this sets word to all true
        word2.set(-1);

        Word result = word1.and(word2);

        Assert.assertEquals("t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t",result.toString());

        word1.set(0);
        result = word1.and(word2);

        Assert.assertEquals("f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f",result.toString());

        word2.set(0);
        result = word1.and(word2);

        Assert.assertEquals("f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f",result.toString());

        word1.set(-1);
        result = word1.and(word2);

        Assert.assertEquals("f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f",result.toString());

        word1.set(214647);
        word2.set(-1);
        result = word1.and(word2);

        Assert.assertEquals("f,f,f,f,f,f,f,f,f,f,f,f,f,f,t,t,f,t,f,f,f,t,t,f,f,t,t,t,f,t,t,t",result.toString());
    }

    @Test
    public void WordOrTest(){
        Word word1 = new Word();
        Word word2 = new Word();
        word1.set(-1); //this sets word to all true
        word2.set(-1);

        Word result = word1.or(word2);

        Assert.assertEquals("t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t",result.toString());

        word1.set(0);
        result = word1.or(word2);

        Assert.assertEquals("t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t",result.toString());

        word2.set(0);
        result = word1.or(word2);

        Assert.assertEquals("f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f",result.toString());

        word1.set(-1);
        result = word1.or(word2);

        Assert.assertEquals("t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t",result.toString());

        word1.set(214647);
        word2.set(-1);
        result = word1.or(word2);

        Assert.assertEquals("t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t",result.toString());
    }

    @Test
    public void WordXorTest(){
        Word word1 = new Word();
        Word word2 = new Word();
        word1.set(-1); //this sets word to all true
        word2.set(-1);

        Word result = word1.xor(word2);

        Assert.assertEquals("f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f",result.toString());

        word1.set(0);
        result = word1.xor(word2);

        Assert.assertEquals("t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t",result.toString());

        word2.set(0);
        result = word1.xor(word2);

        Assert.assertEquals("f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f",result.toString());

        word1.set(-1);
        result = word1.xor(word2);

        Assert.assertEquals("t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t",result.toString());

        word1.set(214647);
        word2.set(-1);
        result = word1.xor(word2);

        Assert.assertEquals("t,t,t,t,t,t,t,t,t,t,t,t,t,t,f,f,t,f,t,t,t,f,f,t,t,f,f,f,t,f,f,f",result.toString());
    }

    @Test
    public void WordNotTest(){
        Word word1 = new Word();
        word1.set(-1); //this sets word to all true
        Assert.assertEquals("t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t",word1.toString());
        word1 = word1.not();
        Assert.assertEquals("f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f",word1.toString());
        word1.set(19023096);
        Assert.assertEquals("f,f,f,f,f,f,f,t,f,f,t,f,f,f,t,f,f,t,f,f,f,t,f,f,t,t,t,t,t,f,f,f",word1.toString());
        word1 = word1.not();
        Assert.assertEquals("t,t,t,t,t,t,t,f,t,t,f,t,t,t,f,t,t,f,t,t,t,f,t,t,f,f,f,f,f,t,t,t",word1.toString());
    }

    @Test
    public void WordShiftTest(){
        Word word1 = new Word();
        word1.set(1);
        Assert.assertEquals("f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,t",word1.toString());
        word1 = word1.leftShift(1);
        Assert.assertEquals("f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,t,f",word1.toString());
        word1 = word1.leftShift(1);
        Assert.assertEquals("f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,t,f,f",word1.toString());
        word1 = word1.leftShift(29);
        Assert.assertEquals("t,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f",word1.toString());
        word1 = word1.leftShift(1);
        Assert.assertEquals("f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f",word1.toString());
        word1.set(17);
        Assert.assertEquals("f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,t,f,f,f,t",word1.toString());
        word1 = word1.rightShift(2);
        Assert.assertEquals("f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,t,f,f",word1.toString());
    }

    @Test
    public void WordSignUnsignTest(){
        Word word1 = new Word();
        word1.set(-1234);
        Assert.assertEquals(word1.getSigned(),-1234);
        Assert.assertEquals(word1.getSigned(),-1234);
        word1.set(345);
        Assert.assertEquals("f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,t,f,t,f,t,t,f,f,t",word1.toString());
        Assert.assertEquals(word1.getSigned(),345);
        Assert.assertEquals(word1.getUnsigned(),345);
        Assert.assertEquals(word1.getUnsigned(),345);
        word1.setBit(31, new Bit(true));
        Assert.assertEquals("t,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,t,f,t,f,t,t,f,f,t",word1.toString());
        Assert.assertEquals(word1.getUnsigned(),2147483993L);
    }

    @Test
    public void WordCopyTest(){
        Word word1 = new Word();
        Word word2 = new Word();
        word1.set(192837981);
        word2.copy(word1);
        Assert.assertEquals(word2.getSigned(),word1.getSigned());
    }

}
