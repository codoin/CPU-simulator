import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class JunitComputer3 {
    Processor p = new Processor();

    //new Word function
    @Test
    public void DecrementTest() throws Exception{
        Word dec = new Word();
        dec.set(13);
        dec.decrement();
        Assert.assertEquals(dec.counter.getUnsigned(),12);
        dec.decrement();
        Assert.assertEquals(dec.counter.getUnsigned(),11);
    }
    //Branch Testing
    @Test
    public void BranchNoRTest() throws Exception{
        //Immediate:000000000000000000000000101 Opcode:00100
        MainMemory.load(addString("000000000000000000000000101 00100")); //set PC to 5
        MainMemory.load(addString("00000000000000000000000000000000")); //halt
        p.run();
        Assert.assertEquals(p.PC.getSigned(),5);
    }
    @Test
    public void BranchDestOnlyTest() throws Exception{
        //Immediate:000000000000000000000000101 Opcode:00100
        MainMemory.load(addString("000000000000000000000000101 00100")); //set PC to 5
        //Immediate:000000000000001101 Function:0000 Rd:00000 Opcode:00101 
        MainMemory.load(addString("000000000000001101 0000 00000 00101")); //set PC to PC + 13
        MainMemory.load(addString("00000000000000000000000000000000")); //halt
        p.run();
        Assert.assertEquals(p.PC.getSigned(),18);
    }
    @Test
    public void Branch3RTest() throws Exception{
        //Immediate:000000000000000101 Function:0000 Rd:00001 Opcode:00001
        MainMemory.load(addString("000000000000000101 0000 00001 00001")); //set R1 to 5
        //Immediate:000000000000000101 Function:0000 Rd:00010 Opcode:00001
        MainMemory.load(addString("000000000000000101 0000 00010 00001")); //set R2 to 5
        //Immediate:00000111 Rs1:00001 Rs2:00010 Function:0000 Rd:00000 Opcode:00110
        MainMemory.load(addString("00000111 00001 00010 0000 00000 00110")); //if R1 == R2? PC + 7 : PC
        MainMemory.load(addString("00000000000000000000000000000000")); //halt
        p.run();
        Assert.assertEquals(p.PC.getSigned(),7);
    }
    @Test
    public void Branch3RTest2() throws Exception{
        //Immediate:000000000000000101 Function:0000 Rd:00001 Opcode:00001
        MainMemory.load(addString("000000000000000101 0000 00001 00001")); //set R1 to 5
        //Immediate:000000000000000101 Function:0000 Rd:00010 Opcode:00001
        MainMemory.load(addString("000000000000000101 0000 00010 00001")); //set R2 to 5
        //Immediate:00000111 Rs1:00001 Rs2:00010 Function:0001 Rd:00000 Opcode:00110
        MainMemory.load(addString("00000111 00001 00010 0001 00000 00110")); //if R1 != R2? PC + 7 : PC
        MainMemory.load(addString("00000000000000000000000000000000")); //halt
        p.run();
        Assert.assertEquals(p.PC.getSigned(),0);
    }
    @Test
    public void Branch2RTest() throws Exception{
        //Immediate:000000000000000101 Function:0000 Rd:00110 Opcode:00001
        MainMemory.load(addString("000000000000000101 0000 00110 00001")); //set R6 to 5
        //Immediate:000000000000001111 Function:0000 Rd:00010 Opcode:00001
        MainMemory.load(addString("000000000000001111 0000 00010 00001")); //set R2 to 15
        //Immediate:0000000101010 Rs:00110 Function:0101 Rd:00010 Opcode:00111
        MainMemory.load(addString("0000000101010 00110 0101 00010 00111")); //if Rs <= Rd? PC + 42
        MainMemory.load(addString("00000000000000000000000000000000")); //halt
        p.run();
        Assert.assertEquals(p.PC.getSigned(),42);
    }
    @Test
    public void Branch2RTest2() throws Exception{
        //Immediate:000000000000000101 Function:0000 Rd:00110 Opcode:00001
        MainMemory.load(addString("000000000000000101 0000 00110 00001")); //set R6 to 5
        //Immediate:000000000000001111 Function:0000 Rd:00010 Opcode:00001
        MainMemory.load(addString("000000000000001111 0000 00010 00001")); //set R2 to 15
        //Immediate:0000000101010 Rs:00110 Function:0011 Rd:00010 Opcode:00111
        MainMemory.load(addString("0000000101010 00110 0011 00010 00111")); //if Rs >= Rd? PC + 42
        MainMemory.load(addString("00000000000000000000000000000000")); //halt
        p.run();
        Assert.assertEquals(p.PC.getSigned(),0);
    }

    //Call Testing
    @Test
    public void CallNoRTest() throws Exception{
        //Immediate:000000000000000000000010111 Opcode:01000
        MainMemory.load(addString("000000000000000000000010111 01000")); //Push PC to stack and set PC to 5
        MainMemory.load(addString("00000000000000000000000000000000")); //halt
        p.run();
        Assert.assertEquals(p.PC.getSigned(),23);
        Assert.assertEquals(MainMemory.read(p.SP.counter).getUnsigned(),23);
    }
    @Test
    public void CallDestOnlyTest() throws Exception{
        //Immediate:000000000000000101 Function:0000 Rd:00110 Opcode:00001
        MainMemory.load(addString("000000000000000101 0000 00110 00001")); //set R6 to 5
        //Immediate:000000000000001101 Function:0000 Rd:00110 Opcode:01001 
        MainMemory.load(addString("000000000000001101 0000 00110 01001")); //set PC to Rd + 13
        MainMemory.load(addString("00000000000000000000000000000000")); //halt
        p.run();
        Assert.assertEquals(p.PC.getSigned(),18);
        Assert.assertEquals(MainMemory.read(p.SP.counter).getUnsigned(),18);
    }
    @Test
    public void Call3RTest() throws Exception{
        //Immediate:000000000000000101 Function:0000 Rd:00001 Opcode:00001
        MainMemory.load(addString("000000000000000101 0000 00001 00001")); //set R1 to 5
        //Immediate:000000000000110101 Function:0000 Rd:00010 Opcode:00001
        MainMemory.load(addString("000000000000110101 0000 00010 00001")); //set R2 to 53
        //Immediate:00011100 Rs1:00001 Rs2:00010 Function:0010 Rd:00000 Opcode:01010
        MainMemory.load(addString("00011100 00001 00010 0010 00010 01010")); //if R1 < R2? Push PC and Rd + 28 : PC
        MainMemory.load(addString("00000000000000000000000000000000")); //halt
        p.run();
        Assert.assertEquals(p.PC.getSigned(),81);
        Assert.assertEquals(MainMemory.read(p.SP.counter).getUnsigned(),81);
    }
    @Test
    public void Call2RTest() throws Exception{
        //Immediate:000000000000000000000000101 Opcode:00100
        MainMemory.load(addString("000000000000000000000000101 00100")); //set PC to 5
        //Immediate:000000000001100101 Function:0000 Rd:00110 Opcode:00001
        MainMemory.load(addString("000000000001100101 0000 00110 00001")); //set R6 to 101
        //Immediate:000000000000001000 Function:0000 Rd:00010 Opcode:00001
        MainMemory.load(addString("000000000000001000 0000 00010 00001")); //set R2 to 8
        //Immediate:0000000101010 Rs:00110 Function:0101 Rd:00010 Opcode:01011
        MainMemory.load(addString("0000000101010 00110 0101 00010 01011")); //if Rs > Rd? Push PC and PC + 42 : PC
        MainMemory.load(addString("00000000000000000000000000000000")); //halt
        p.run();
        Assert.assertEquals(p.PC.getSigned(),5);
    }

    //Push Testing
    @Test
    public void PushDestOnlyTest() throws Exception{
        //Immediate:000000000000000101 Function:0000 Rd:00110 Opcode:00001
        MainMemory.load(addString("000000000000000101 0000 00110 00001")); //set R6 to 5
        //Immediate:000000000000001101 Function:0111 Rd:00110 Opcode:01101 
        MainMemory.load(addString("000000000000001101 0111 00110 01101")); //Write Rd * 13 to memory
        MainMemory.load(addString("00000000000000000000000000000000")); //halt
        p.run();
        Assert.assertEquals(MainMemory.read(p.SP.counter).getUnsigned(),65);
    }
    @Test
    public void Push3RTest() throws Exception{
        //Immediate:000000000000000101 Function:0000 Rd:00001 Opcode:00001
        MainMemory.load(addString("000000000000000101 0000 00001 00001")); //set R1 to 5
        //Immediate:000000000000110101 Function:0000 Rd:00010 Opcode:00001
        MainMemory.load(addString("000000000000110101 0000 00010 00001")); //set R2 to 53
        //Immediate:00011100 Rs1:00001 Rs2:00010 Function:1111 Rd:00000 Opcode:01110
        MainMemory.load(addString("00011100 00001 00010 1111 00010 01110")); //Write Rs1 - Rs2 to memory
        MainMemory.load(addString("00000000000000000000000000000000")); //halt
        p.run();
        Assert.assertEquals(MainMemory.read(p.SP.counter).getSigned(),-48);
    }
    @Test
    public void Push2RTest() throws Exception{
        //Immediate:000000000001100101 Function:0000 Rd:00111 Opcode:00001
        MainMemory.load(addString("000000000001100101 0000 00111 00001")); //set R7 to 101
        //Immediate:000000000000001000 Function:0000 Rd:00010 Opcode:00001
        MainMemory.load(addString("000000000000001000 0000 00010 00001")); //set R2 to 8
        //Immediate:0000000101010 Rs:00111 Function:1110 Rd:00010 Opcode:01111
        MainMemory.load(addString("0000000101010 00111 1110 00010 01111")); //Write Rd + Rs1 to memory
        MainMemory.load(addString("00000000000000000000000000000000")); //halt
        p.run();
        Assert.assertEquals(MainMemory.read(p.SP.counter).getSigned(),109);
    }

    //Load Testing
    @Test
    public void LoadNoRTest() throws Exception{
        //Immediate:000000000000000101 Function:0000 Rd:00110 Opcode:00001
        MainMemory.load(addString("000000000000000101 0000 00110 00001")); //set R6 to 5
        //Immediate:000000000000001101 Function:0111 Rd:00110 Opcode:01101 
        MainMemory.load(addString("000000000000001101 0111 00110 01101")); //Write Rd * 13 to memory
        //Immediate:000000000000000000000000000 Opcode:10000
        MainMemory.load(addString("000000000000000000000000000 10000")); //Pops head of stack and stores it in PC
        MainMemory.load(addString("00000000000000000000000000000000")); //halt
        p.run();
        Assert.assertEquals(p.PC.getSigned(),65);
    }
    @Test
    public void LoadDestOnlyTest() throws Exception{
        //writes 128 to memory[30]
        Word address = new Word();
        Word value = new Word();
        address.set(30);
        value.set(128);
        MainMemory.write(address, value);
        //Immediate:000000000000010001 Function:0000 Rd:00110 Opcode:00001
        MainMemory.load(addString("000000000000010001 0000 00110 00001")); //set R6 to 17
        //Immediate:000000000000001101 Function:0000 Rd:00110 Opcode:10001 
        MainMemory.load(addString("000000000000001101 0000 00110 10001")); //set Rd to the memory[Rd + Immediate]
        MainMemory.load(addString("00000000000000000000000000000000")); //halt
        p.run();
        Assert.assertEquals(p.registers[6].getSigned(),128);
    }
    @Test
    public void Load3RTest() throws Exception{
        //writes 256 to memory[28]
        Word address = new Word();
        Word value = new Word();
        address.set(28);
        value.set(256);
        MainMemory.write(address, value);
        //Immediate:000000000000000101 Function:0000 Rd:00001 Opcode:00001
        MainMemory.load(addString("000000000000000101 0000 00001 00001")); //set R1 to 5
        //Immediate:000000000000010111 Function:0000 Rd:00010 Opcode:00001
        MainMemory.load(addString("000000000000010111 0000 00010 00001")); //set R2 to 23
        //Immediate:00011100 Rs1:00001 Rs2:00010 Function:0000 Rd:00011 Opcode:10010
        MainMemory.load(addString("00011100 00001 00010 0000 00011 10010")); //set Rd to memory[Rs1 + Rs2]
        MainMemory.load(addString("00000000000000000000000000000000")); //halt
        p.run();
        Assert.assertEquals(p.registers[3].getSigned(),256);
    }
    @Test
    public void Load2RTest() throws Exception{
        //writes 1000 to memory[12]
        Word address = new Word();
        Word value = new Word();
        address.set(12);
        value.set(1000);
        MainMemory.write(address, value);
        //Immediate:000000000000001000 Function:0000 Rd:00110 Opcode:00001
        MainMemory.load(addString("000000000000001000 0000 00110 00001")); //set R6 to 8
        //Immediate:0000000000100 Rs:00110 Function:0000 Rd:00010 Opcode:10011
        MainMemory.load(addString("0000000000100 00110 0000 00010 10011")); //set Rd to memory[Rs1 + Immediate]
        MainMemory.load(addString("00000000000000000000000000000000")); //halt
        p.run();
        Assert.assertEquals(p.registers[2].getSigned(),1000);
    }

    //Store Testing
    @Test
    public void StoreDestOnlyTest() throws Exception{
        //Immediate:000000000000010001 Function:0000 Rd:00110 Opcode:00001
        MainMemory.load(addString("000000000000010001 0000 00110 00001")); //set R6 to 17
        //Immediate:000000001011001101 Function:0000 Rd:00110 Opcode:10101 
        MainMemory.load(addString("000000001011001101 0000 00110 10101")); //store Immediate to memory[Rd]
        MainMemory.load(addString("00000000000000000000000000000000")); //halt
        p.run();
        Word address = new Word();
        address.set(17);
        Assert.assertEquals(MainMemory.read(address).getSigned(),717);
    }
    @Test
    public void Store3RTest() throws Exception{
        //Immediate:000000000000000101 Function:0000 Rd:00001 Opcode:00001
        MainMemory.load(addString("000000000000000101 0000 00001 00001")); //set R1 to 5
        //Immediate:000000000000010111 Function:0000 Rd:00010 Opcode:00001
        MainMemory.load(addString("000000000000010111 0000 00010 00001")); //set R2 to 23
        //Immediate:00011100 Rs1:00001 Rs2:00010 Function:0000 Rd:00001 Opcode:10110
        MainMemory.load(addString("00011100 00001 00010 0000 00001 10110")); //store Rs2 to memory[Rd + Rs1]
        MainMemory.load(addString("00000000000000000000000000000000")); //halt
        p.run();
        Word address = new Word();
        address.set(10);
        Assert.assertEquals(MainMemory.read(address).getSigned(),23);
    }
    @Test
    public void Store2RTest() throws Exception{
        //Immediate:000000000000000101 Function:0000 Rd:00001 Opcode:00001
        MainMemory.load(addString("000000000000000101 0000 00001 00001")); //set R1 to 5
        //Immediate:000000000000001000 Function:0000 Rd:00110 Opcode:00001
        MainMemory.load(addString("000000000000001000 0000 00110 00001")); //set R6 to 8
        //Immediate:0000000000100 Rs:00110 Function:0000 Rd:00001 Opcode:10111
        MainMemory.load(addString("0000000000100 00110 0000 00001 10111")); //store Rs1 to memory[Rd + Immediate]
        MainMemory.load(addString("00000000000000000000000000000000")); //halt
        p.run();
        Word address = new Word();
        address.set(9);
        Assert.assertEquals(MainMemory.read(address).getSigned(),8);
    }

    //Pop Testing
    @Test
    public void PopDestOnlyTest() throws Exception{
        //Immediate:000000000000010001 Function:0000 Rd:00110 Opcode:00001
        MainMemory.load(addString("000000000000010001 0000 00110 00001")); //set R6 to 17
        //Immediate:000000000000001101 Function:0111 Rd:00110 Opcode:01101 
        MainMemory.load(addString("000000000000001101 0111 00110 01101")); //Write Rd * 13 to memory
        //Immediate:000000001011001101 Function:0000 Rd:00001 Opcode:11001 
        MainMemory.load(addString("000000001011001101 0000 00001 11001")); //store memory[SP++] to Rd
        MainMemory.load(addString("00000000000000000000000000000000")); //halt
        p.run();
        Assert.assertEquals(p.registers[1].getSigned(),221);
    }

    //Peek Testing
    @Test
    public void Peek3RTest() throws Exception{
        //writes 365 to memory[996]
        Word address = new Word();
        Word value = new Word();
        address.set(996);
        value.set(123123);
        MainMemory.write(address, value);
        //Immediate:000000000000000101 Function:0000 Rd:00001 Opcode:00001
        MainMemory.load(addString("000000000000000101 0000 00001 00001")); //set R1 to 5
        //Immediate:000000000000010111 Function:0000 Rd:00010 Opcode:00001
        MainMemory.load(addString("000000000000010111 0000 00010 00001")); //set R2 to 23
        //Immediate:00000000 Rs1:00001 Rs2:00010 Function:0000 Rd:00001 Opcode:11010
        MainMemory.load(addString("00000000 00001 00010 0000 00001 11010")); //store memory[sp – (Rs1 + Rs2)] to Rd
        MainMemory.load(addString("00000000000000000000000000000000")); //halt
        p.run();
        Assert.assertEquals(MainMemory.read(address).getSigned(),123123);
        Assert.assertEquals(p.registers[1].getSigned(),123123);
    }
    @Test
    public void Peek2RTest() throws Exception{
        //writes 365 to memory[1012]
        Word address = new Word();
        Word value = new Word();
        address.set(1012);
        value.set(365);
        MainMemory.write(address, value);
        //Immediate:000000000000000101 Function:0000 Rd:00001 Opcode:00001
        MainMemory.load(addString("000000000000000101 0000 00001 00001")); //set R1 to 5
        //Immediate:000000000000001000 Function:0000 Rd:00110 Opcode:00001
        MainMemory.load(addString("000000000000001000 0000 00110 00001")); //set R6 to 8
        //Immediate:0000000000100 Rs:00110 Function:0000 Rd:00001 Opcode:11011
        MainMemory.load(addString("0000000000100 00110 0000 00001 11011")); //store  memory[sp – (Rs + Immediate)] to Rd
        MainMemory.load(addString("00000000000000000000000000000000")); //halt
        p.run();
        Assert.assertEquals(MainMemory.read(address).getSigned(),365);
        Assert.assertEquals(p.registers[1].getSigned(),365);
    }

    @Before
    public void setup(){
        p.setup();
        MainMemory.memory = new Word[1024];
        Main.processor.setup();
        InstructionCache.reset();
        L2Cache.reset();
    }
    //helper method to convert string into array
    String[] addString(String s){
        s = s.replaceAll(" ", "");
        String[] string = s.split("(?!^)");
        String[] temp = new String[32];
        int counter = 0;
        for (int i = 31; i >= 0; i--){
            temp[counter++] = string[i];
        }
        return temp;
    }
}