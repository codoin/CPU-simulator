import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class JunitCache{
    Processor p = new Processor();

    @Before
    public void setup(){
        MainMemory.memory = new Word[1024];
        Main.processor.setup();
        InstructionCache.reset();
        L2Cache.reset();
    }
    
    @Test
    public void SumArrayTest() throws Exception{
        Word address = new Word();
        Word value = new Word();

        //20 integers in an array
        for(int i = 0; i < 20; i++){
            //writes 365+i to memory[200+i]
            address.set(200+i);
            value.set(365+i);
            MainMemory.write(address, value);
        }
        
        Main.main(new String[]{"test1.txt","output.txt"});
        Main.loadFile("output.txt");
        Main.processor.run();

        Assert.assertEquals(Main.processor.registers[1].getSigned(),7490);
    }

    @Test
    public void LinkedListTest() throws Exception{
        Word address = new Word();
        Word value = new Word();
        int random;
        int total = 0;

        //20 item linked list of numbers
        for(int i = 0; i < 20; i++){
            random = (int)(Math.random()*100) + 300;
            total += random;
            value.set(random);
            //write random value to address pointer
            MainMemory.write(value, value);

            //writes pointer to memory[200+i]
            address.set(200+i);
            MainMemory.write(address, value);
        }
        
        Main.main(new String[]{"test2.txt","output.txt"});
        Main.loadFile("output.txt");
        Main.processor.run();

        Assert.assertEquals(Main.processor.registers[1].getSigned(),total);
    }

    @Test
    public void BackwardsArrayTest() throws Exception{
        Word address = new Word();
        Word value = new Word();

        //20 integers in an array
        for(int i = 0; i < 20; i++){
            //writes 365+i to memory[200+i]
            address.set(200+i);
            value.set(365+i);
            MainMemory.write(address, value);
        }
        
        Main.main(new String[]{"test3.txt","output.txt"});
        Main.loadFile("output.txt");
        Main.processor.run();

        Assert.assertEquals(Main.processor.registers[1].getSigned(),7490);
    }

    String[] toArray(String s){
        String[] temp = new String[s.length()];
        for(int i = 0; i < s.length(); i++){
            temp[i] = s.charAt(i)+"";
        }
        return temp;
    }
}