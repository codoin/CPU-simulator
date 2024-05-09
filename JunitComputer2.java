import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class JunitComputer2 {
    Processor p = new Processor();

    @Test
    public void SampleTest() throws Exception{
        //Immediate:000000000000000101 Function:0000 Rd:00001 Opcode:00001
        MainMemory.load(addString("000000000000000101 0000 00001 00001")); //set R1 to 5

        //Immediate:00000000 Rs1:00001 Rs2:00001 Function:1110 Rd:00010 Opcode:00010
        MainMemory.load(addString("00000000 00001 00001 1110 00010 00010")); //ADD R1 and R1 to R2

        //Immediate:0000000000000 Rs:00010 Function:1110 Rd:00010 Opcode:00011
        MainMemory.load(addString("0000000000000 00010 1110 00010 00011")); //ADD R2 and Rd to R2

        //Immediate:00000000 Rs1:00001 Rs2:00010 Function:1110 Rd:00011 Opcode:00010
        MainMemory.load(addString("00000000 00001 00010 1110 00011 00010")); //ADD R2 and R1 to R3
        MainMemory.load(addString("00000000000000000000000000000000")); //halt
        p.run();
        Assert.assertEquals(p.registers[1].getUnsigned(),5);
        Assert.assertEquals(p.registers[2].getUnsigned(),20);
        Assert.assertEquals(p.registers[3].getUnsigned(),25);
    }

    @Test
    public void SampleTest2() throws Exception{
        //Immediate:000000000000001101 Function:0000 Rd:00100 Opcode:00001
        MainMemory.load(addString("000000000000001101 0000 00100 00001")); //set R4 to 13

        //Immediate:000000000000001001 Function:0000 Rd:00101 Opcode:00001
        MainMemory.load(addString("000000000000001001 0000 00101 00001")); //set R5 to 9

        //Immediate:00000000 Rs1:00100 Rs2:00101 Function:0111 Rd:00110 Opcode:00010
        MainMemory.load(addString("00000000 00100 00101 0111 00110 00010")); //Multiply R4 and R5 to R6

        //Immediate:0000000000000 Rs:00110 Function:1110 Rd:00110 Opcode:00011
        MainMemory.load(addString("0000000000000 00110 1110 00110 00011")); //ADD R6 and Rd to R6

        //Immediate:00000000 Rs1:00110 Rs2:00100 Function:1111 Rd:00111 Opcode:00010
        MainMemory.load(addString("00000000 00110 00100 1111 00111 00010")); //SUB R6 and R4 to R7
        MainMemory.load(addString("00000000000000000000000000000000")); //halt
        p.run();
        Assert.assertEquals(p.registers[4].getUnsigned(),13);
        Assert.assertEquals(p.registers[5].getUnsigned(),9);
        Assert.assertEquals(p.registers[6].getUnsigned(),234);
        Assert.assertEquals(p.registers[7].getUnsigned(),221);
    }

    @Test
    public void SampleTest3() throws Exception{
        //Immediate:000000000000010001 Function:0000 Rd:01000 Opcode:00001
        MainMemory.load(addString("000000000000010001 0000 01000 00001")); //set R8 to 17

        //Immediate:000000000000111111 Function:0000 Rd:01001 Opcode:00001
        MainMemory.load(addString("000000000000111111 0000 01001 00001")); //set R9 to 63

        //Immediate:00000000 Rs1:01000 Rs2:01001 Function:1000 Rd:01010 Opcode:00010
        MainMemory.load(addString("00000000 01000 01001 1000 01010 00010")); //R8 AND R9 to R10

        //Immediate:0000000000000 Rs:01010 Function:1110 Rd:01010 Opcode:00011
        MainMemory.load(addString("0000000000000 01010 1110 01010 00011")); //ADD R10 and Rd to R10

        //Immediate:00000000 Rs1:01010 Rs2:00100 Function:1010 Rd:01011 Opcode:00010
        MainMemory.load(addString("00000000 01010 01000 1010 01011 00010")); //XOR R10 and R8 to R11
        MainMemory.load(addString("00000000000000000000000000000000")); //halt
        p.run();
        Assert.assertEquals(p.registers[8].getUnsigned(),17);
        Assert.assertEquals(p.registers[9].getUnsigned(),63);
        Assert.assertEquals(p.registers[10].getUnsigned(),34);
        Assert.assertEquals(p.registers[11].getUnsigned(),51);
    }

    @Test
    public void SampleTest4() throws Exception{
        //Immediate:000000000000010001 Function:0000 Rd:01100 Opcode:00001
        MainMemory.load(addString("000000000000000011 0000 01100 00001")); //set R12 to 3

        //Immediate:000000000000111111 Function:0000 Rd:01101 Opcode:00001
        MainMemory.load(addString("000000000000111110 0000 01101 00001")); //set R13 to 62

        //Immediate:00000000 Rs1:01100 Rs2:01101 Function:1001 Rd:01110 Opcode:00010
        MainMemory.load(addString("00000000 01100 01101 1001 01110 00010")); //R12 OR R13 to R14

        //Immediate:0000000000000 Rs:01101 Function:1110 Rd:01101 Opcode:00011
        MainMemory.load(addString("0000000000000 01101 1110 01101 00011")); //ADD R13 and Rd to R13

        //Immediate:00000000 Rs1:01110 Rs2:01101 Function:1101 Rd:01111 Opcode:00010
        MainMemory.load(addString("00000000 01110 01100 1101 01111 00010")); //Right Shift R13 by R12 to R15
        MainMemory.load(addString("00000000000000000000000000000000")); //halt
        p.run();
        Assert.assertEquals(p.registers[12].getUnsigned(),3);
        Assert.assertEquals(p.registers[13].getUnsigned(),124);
        Assert.assertEquals(p.registers[14].getUnsigned(),63);
        Assert.assertEquals(p.registers[15].getUnsigned(),7);
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
