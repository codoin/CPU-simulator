import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class JunitAssembler {
    Processor p = new Processor();

    @Before
    public void setup(){
        MainMemory.memory = new Word[1024];
        Main.processor.setup();
        InstructionCache.reset();
        L2Cache.reset();
    }
    
    //NoR, DestOnly, 2R and 3R
    @Test
    public void MathTest() throws Exception{
        //copy R1 15
        //copy R2 45
        //copy R3 5
        //math add R3 R1 R2
        //math multiply R1 R3
        //halt
        Main.main(new String[]{"math.txt","output.txt"});
        Main.loadFile("output.txt");
        Main.processor.run();
        Assert.assertEquals(Main.processor.registers[1].getSigned(),900);
        Assert.assertEquals(Main.processor.registers[2].getSigned(),45);
        Assert.assertEquals(Main.processor.registers[3].getSigned(),60);
    }

    //DestOnly, 2R, and 3R
    @Test
    public void BranchTest() throws Exception{
        //copy R1 7
        //copy R2 30
        //branch ge R6 R2 R1 76
        //branch lt R2 R1 12
        //jump R1 54
        //halt
        Main.main(new String[]{"branch.txt","output.txt"});
        Main.loadFile("output.txt");
        Main.processor.run();
        Assert.assertEquals(Main.processor.PC.getSigned(),142);
    }

    //NoR
    @Test
    public void JumpTest() throws Exception{
        //jump 13
        //halt
        Main.main(new String[]{"branch2.txt","output.txt"});
        Main.loadFile("output.txt");
        Main.processor.run();
        Assert.assertEquals(Main.processor.PC.getSigned(),13);
    }

    @Test
    public void Jump2Test() throws Exception{
        //jump 5
        //jump R1 12
        //halt
        Main.main(new String[]{"branch3.txt","output.txt"});
        Main.loadFile("output.txt");
        Main.processor.run();
        Assert.assertEquals(Main.processor.PC.getSigned(),17);
    }

    //call NoR and 2R
    @Test
    public void CallTest() throws Exception{
        //call 12
        //copy R1 1
        //copy R2 1
        //call eq R1 R2 41
        //halt
        Main.main(new String[]{"call.txt","output.txt"});
        Main.loadFile("output.txt");
        Main.processor.run();
        Assert.assertEquals(Main.processor.PC.getSigned(),53);
    }

    //DestOnly
    @Test
    public void Call2Test() throws Exception{
        //copy R1 24
        //copy R2 1
        //copy R3 32
        //call R1 45
        //halt
        Main.main(new String[]{"call2.txt","output.txt"});
        Main.loadFile("output.txt");
        Main.processor.run();
        Assert.assertEquals(Main.processor.PC.getSigned(),69);
    }

    //3R
    @Test
    public void Call3Test() throws Exception{
        //copy R1 24
        //copy R2 1
        //copy R3 32
        //call neq R2 R1 R3 12
        //halt
        Main.main(new String[]{"call3.txt","output.txt"});
        Main.loadFile("output.txt");
        Main.processor.run();
        Assert.assertEquals(Main.processor.PC.getSigned(),13);
    }

    //DestOnly
    @Test
    public void PushTest() throws Exception{
        //copy R6 5
        //push left shift R6 1
        //halt
        Main.main(new String[]{"push.txt","output.txt"});
        Main.loadFile("output.txt");
        Main.processor.run();
        Assert.assertEquals(MainMemory.read(Main.processor.SP.counter).getUnsigned(),10);
    }

    //2R
    @Test
    public void Push2Test() throws Exception{
        //copy R1 5
        //copy R2 53
        //push subtract R1 R2
        //halt
        Main.main(new String[]{"push2.txt","output.txt"});
        Main.loadFile("output.txt");
        Main.processor.run();
        Assert.assertEquals(MainMemory.read(Main.processor.SP.counter).getSigned(),-48);
    }

    //3R
    @Test
    public void Push3Test() throws Exception{
        //copy R1 5
        //copy R2 53
        //push subtract R0 R1 R2
        //halt
        Main.main(new String[]{"push3.txt","output.txt"});
        Main.loadFile("output.txt");
        Main.processor.run();
        Assert.assertEquals(MainMemory.read(Main.processor.SP.counter).getSigned(),-48);
    }

    //NoR
    @Test
    public void ReturnTest() throws Exception{
        //copy R6 5
        //push multiply R6 13
        //return
        //halt
        Main.main(new String[]{"return.txt","output.txt"});
        Main.loadFile("output.txt");
        Main.processor.run();
        Assert.assertEquals(Main.processor.PC.getSigned(),65);
    }

    //DestOnly and 3R
    @Test
    public void LoadTest() throws Exception{
        //writes 256 to memory[28]
        Word address = new Word();
        Word value = new Word();
        address.set(28);
        value.set(256);
        MainMemory.write(address, value);

        //writes 128 to memory[30]
        address = new Word();
        value = new Word();
        address.set(30);
        value.set(128);
        MainMemory.write(address, value);

        //copy R6 17
        //copy R1 5
        //copy R2 23
        //load R6 13
        //load R3 R1 R2
        //halt
        Main.main(new String[]{"load.txt","output.txt"});
        Main.loadFile("output.txt");
        Main.processor.run();
        Assert.assertEquals(Main.processor.registers[6].getSigned(),128);
        Assert.assertEquals(Main.processor.registers[3].getSigned(),256);
    }

    //2R
    @Test
    public void Load2Test() throws Exception{
        //writes 1000 to memory[12]
        Word address = new Word();
        Word value = new Word();
        address.set(12);
        value.set(1000);
        MainMemory.write(address, value);
        
        //copy R6 8
        //load R2 R6 4
        //halt
        Main.main(new String[]{"load2.txt","output.txt"});
        Main.loadFile("output.txt");
        Main.processor.run();
        Assert.assertEquals(Main.processor.registers[2].getSigned(),1000);
    }

    //DestOnly and 3R
    @Test
    public void StoreTest() throws Exception{
        //copy R1 5
        //copy R2 23
        //copy R6 17
        //store R6 717
        //store R1 R1 R2
        //halt
        Main.main(new String[]{"store.txt","output.txt"});
        Main.loadFile("output.txt");
        Main.processor.run();
        Word address = new Word();
        address.set(17);
        Assert.assertEquals(MainMemory.read(address).getSigned(),717);
        address.set(10);
        Assert.assertEquals(MainMemory.read(address).getSigned(),23);
    }

    //2R
    @Test
    public void Store2Test() throws Exception{
        //copy R1 5
        //copy R6 8
        //store R1 R6 4
        //halt
        Main.main(new String[]{"store2.txt","output.txt"});
        Main.loadFile("output.txt");
        Main.processor.run();
        Word address = new Word();
        address.set(9);
        Assert.assertEquals(MainMemory.read(address).getSigned(),8);
    }

    @Test
    public void PopTest() throws Exception{
        //copy R6 17
        //push multiply R6 13
        //pop R1
        //halt
        Main.main(new String[]{"pop.txt","output.txt"});
        Main.loadFile("output.txt");
        Main.processor.run();
        Assert.assertEquals(Main.processor.registers[1].getSigned(),221);
    }
    
    @Test
    public void PeekTest() throws Exception{
        //writes 365 to memory[1012]
        Word address = new Word();
        Word value = new Word();
        address.set(1012);
        value.set(365);
        MainMemory.write(address, value);

        //writes 123123 to memory[996]
        address = new Word();
        value = new Word();
        address.set(996);
        value.set(123123);
        MainMemory.write(address, value);

        //copy R1 5
        //copy R2 23
        //copy R6 8
        //peek R1 R2 R1
        //peek R5 R6 4
        //halt

        Main.main(new String[]{"peek.txt","output.txt"});
        Main.loadFile("output.txt");
        Main.processor.run();

        address.set(996);
        Assert.assertEquals(MainMemory.read(address).getSigned(),123123);
        Assert.assertEquals(Main.processor.registers[1].getSigned(),123123);
        address.set(1012);
        Assert.assertEquals(MainMemory.read(address).getSigned(),365);
        Assert.assertEquals(Main.processor.registers[5].getSigned(),365);
    }

    @Test
    public void Test1() throws Exception{
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
    public void Test2() throws Exception{
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
    public void Test3() throws Exception{
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
