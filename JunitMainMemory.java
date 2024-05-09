import org.junit.Assert;
import org.junit.Test;

public class JunitMainMemory {
    @Test
    public void LoadTest() throws Exception{
        String[] loadedBits = addString("11111111111111111111111111111111");
        Word address = new Word();
        address.set(0);
        MainMemory.load(loadedBits);
        long value = MainMemory.read(address).getUnsigned();
        Assert.assertEquals(value,4294967295L);

        loadedBits = addString("1000000000000000000000000000000011000000000000000000000000000000");
        address.set(1);
        MainMemory.load(loadedBits);
        value = MainMemory.read(address).getUnsigned();
        Assert.assertEquals(value,1);

        address.set(2);
        value = MainMemory.read(address).getUnsigned();
        Assert.assertEquals(value,3);

        loadedBits = addString("101");
        address.set(3);
        MainMemory.load(loadedBits);
        value = MainMemory.read(address).getUnsigned();
        Assert.assertEquals(value,5);
    }

    @Test
    public void ReadWriteTest() throws Exception{
        Word address = new Word(), value = new Word();
        address.set(21);
        value.set(1290);
        MainMemory.write(address, value);
        Assert.assertEquals(MainMemory.read(address).getUnsigned(),1290);

        value.set(0);
        MainMemory.write(address, value);
        Assert.assertEquals(MainMemory.read(address).getUnsigned(),1290);

        address.set(22);
        value.set(123);
        MainMemory.write(address, value);
        Assert.assertEquals(MainMemory.read(address).getUnsigned(),123);

        address.set(1023);
        value.set(99999);
        MainMemory.write(address, value);
        Assert.assertEquals(MainMemory.read(address).getUnsigned(),99999);

        address.set(1022);
        value.set(-99999);
        MainMemory.write(address, value);
        Assert.assertEquals(MainMemory.read(address).getSigned(),-99999);
    }

    @Test (expected = NullPointerException.class)
    public void ReadTest() throws Exception{
        Word address = new Word(), value = new Word();
        address.set(18);
        value.set(10);
        MainMemory.write(address, value);
        address.set(19);
        Assert.assertEquals(MainMemory.read(address).getUnsigned(),1290);
    }

    @Test
    public void IncrementTest() throws Exception{
        Word test = new Word();
        test.set(15);
        test.increment();
        Assert.assertEquals(test.counter.getUnsigned(),16);

        test.increment();
        Assert.assertEquals(test.counter.getUnsigned(),17);

        test.increment();
        Assert.assertEquals(test.counter.getUnsigned(),18);

        test.increment();
        Assert.assertEquals(test.counter.getUnsigned(),19);
    }

    //helper method to convert string into array
    String[] addString(String s){
        return s.split("(?!^)");
    }
}
