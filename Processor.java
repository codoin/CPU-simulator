public class Processor {
    Word PC, SP;
    Bit halt;
    Word currentInstruction;
    Word Opcode, Rd, Function, Rs2, Rs1, Immediate;
    ALU alu;
    Word[] registers;
    static int currentClockCycle;

    //enum types to make code more readable and easier
    enum registerType{NoR,Dest,ThreeR,TwoR};
    registerType regType;
    enum OpCode{Math,Branch,Call,Push,Load,Store,Peek,Pop,Interrupt};
    OpCode opType;

    //intializes Program Counter and Stack Pointer
    //sets PC to 0 and SP to 1024
    //intializes all variables
    void setup(){
        PC = new Word();
        SP = new Word();
        PC.set(0);
        PC.counter = new Word();
        PC.counter.set(0);
        SP.set(1024);
        halt = new Bit(false);
        currentInstruction = new Word();
        Opcode = new Word();
        Rd = new Word();
        Function = new Word();
        Rs2 = new Word();
        Rs1 = new Word();
        Immediate = new Word();
        alu = new ALU();
        registers = new Word[32];
        currentClockCycle = 0;
    }
    //while not halted, fetch an instruction, get the instruction, execute the instruction, store the results
    void run() throws Exception{
        while(!halt.getValue()){
            fetch();
            decode();
            execute();
            store();  
        }
        System.out.println("Clock Cycles: " + currentClockCycle);
    }
    //reads the current instruction's opcode
    //organizes the current instruction into 0-3 registers
    //uses masking and rightShifting to get parts of current instruction
    void decode(){
        //reset the values of the opcode, rd, function, registers, and immediate
        Opcode.set(0);
        Rd.set(0);
        Function.set(0);
        Rs1.set(0);
        Rs2.set(0);
        Immediate.set(0);

        //gets the bits for operation code by bit masking
        Word temp = new Word();
        temp.setBitString("00000000000000000000000000011111");;
        Opcode.copy(currentInstruction.and(temp));

        //gets the first 2 bits in opcode for the architecture
        Bit first = Opcode.getBit(0);
        Bit second = Opcode.getBit(1);

        if(!first.getValue() && !second.getValue()){ //if No Register (0R) - 00
            //gets immediate value from currentInstruction
            temp.setBitString("11111111111111111111111111100000");
            Immediate.copy(currentInstruction.and(temp).rightShift(5));
            regType = registerType.NoR;
        }
        else{
            //gets the Rd value by bit masking
            temp.setBitString("00000000000000000000001111100000");
            Rd.copy(currentInstruction.and(temp).rightShift(5));

            //gets the Function value by bit masking
            temp.setBitString("00000000000000000011110000000000");
            Function.copy(currentInstruction.and(temp).rightShift(10));

            if(second.getValue()){ //if uses 1-3 Registers
                temp.setBitString("00000000000001111100000000000000");

                if(first.getValue()){//if 2 Register (2R) - 11 
                    //gets Rs1 value
                    Rs1.copy(currentInstruction.and(temp).rightShift(14));

                    //gets immdiate value
                    temp.setBitString("11111111111110000000000000000000");
                    Immediate.copy(currentInstruction.and(temp).rightShift(19));
                    regType = registerType.TwoR;
                }
                else{//if 3 Register (3R) - 10
                    //gets Rs1 and Rs2 values
                    Rs2.copy(currentInstruction.and(temp).rightShift(14));

                    temp.setBitString("00000000111110000000000000000000");
                    Rs1.copy(currentInstruction.and(temp).rightShift(19));

                    //gets immdiate value
                    temp.setBitString("11111111000000000000000000000000");
                    Immediate.copy(currentInstruction.and(temp).rightShift(24));
                    regType = registerType.ThreeR;
                }
            }
            else{ //Dest Only (1R) - 01
                temp.setBitString("11111111111111111100000000000000");
                Immediate.copy(currentInstruction.and(temp).rightShift(14));
                regType = registerType.Dest;
            }
        }
    }

    //checks for opcode, passes the function instruction to ALU, then copies the value to Rd
    void execute() throws Exception{
        Word RegisterRs1, RegisterRs2, RegisterRd;
        int location1 = 0, location2 = 0, locationRd = 0;
        //the add and subtract function for quick calculations
        Bit[] add = new Bit[]{new Bit(false),new Bit(true),new Bit(true),new Bit(true)};
        Bit[] subtract = new Bit[]{new Bit(true),new Bit(true),new Bit(true),new Bit(true)};
        Bit[] funct = new Bit[4];
        //find math operation
        for(int i = 0; i < 4; i++){
            funct[i] = Function.getBit(i);
        }
        //figures out the locations of the registers stored in Rs1, Rs2, and Rd and retrieves them
        for(int i = 0; i < 5; i++){
            if(Rs1.getBit(i).getValue()){
                location1 += Math.pow(2, i);
            }
        }
        for(int i = 0; i < 5; i++){
            if(Rs2.getBit(i).getValue()){
                location2 += Math.pow(2, i);
            }
        }
        for(int i = 0; i < 5; i++){
            if(Rd.getBit(i).getValue()){
                locationRd += Math.pow(2, i);
            }
        }
        RegisterRs1 = registers[location1];
        RegisterRs2 = registers[location2];
        RegisterRd = registers[locationRd];

        //checks for Math opcode(000)
        if(!Opcode.getBit(2).getValue() && !Opcode.getBit(3).getValue() && !Opcode.getBit(4).getValue()){
            //if No R: HALT (00)
            if(regType == registerType.NoR){
                halt.set();
                return;
            }
            //if Dest Only: Copy Immediate to Rd (01)
            else if(regType == registerType.Dest){
                //Sets the bits of Immediate to Rd
                for(int i = 0; i < 18; i++){
                    Rd.setBit(i, Immediate.getBit(i));
                }
            }
            //if 2R: Do MathOp with Rd and Rs to Rd (11)
            else if(regType == registerType.TwoR){
                MathOp(RegisterRd,RegisterRs1,funct,Rd);
            }
            //3R: Do MathOp with Rs1 and Rs2 to Rd (10)
            else{
                MathOp(RegisterRs1,RegisterRs2,funct,Rd);
            }   
            opType = OpCode.Math;         
        }

        //checks for Branch Opcode(001)
        else if(Opcode.getBit(2).getValue() && !Opcode.getBit(3).getValue() && !Opcode.getBit(4).getValue()){
            //No R: JUMP: store Immediate to PC
            if(regType == registerType.NoR){
                for(int i = 0; i < 32; i++){
                    PC.setBit(i, Immediate.getBit(i));
                }
            }
            //Dest Only: JUMP: PC to PC + Immediate
            else if(regType == registerType.Dest){
                MathOp(PC,Immediate,add,PC);
            }
            //2R: Do BoolOp with Rs and Rd. PC + imm or PC stored to PC
            else if(regType == registerType.TwoR){
                //if Boolean Operation with Rs1 and Rd is true, Do Math Operation with PC and Immediate
                if(BooleanOp(RegisterRs1,RegisterRd)){
                    MathOp(PC,Immediate,add,PC);
                }
            }
            //3R: Do BoolOp with Rs1 and Rs2 to store PC + Immediate or PC to PC
            else{
                //if Boolean Operation with Rs1 and Rs2 is true, Do Math Operation with PC and Immediate
                if(BooleanOp(RegisterRs1,RegisterRs2)){
                    MathOp(PC,Immediate,add,PC);
                }
            }     
            opType = OpCode.Branch;
        }
        //Call (010)
        else if(!Opcode.getBit(2).getValue() && Opcode.getBit(3).getValue() && !Opcode.getBit(4).getValue()){
            //No R: Push PC: stores Immediate to PC
            if(!Opcode.getBit(0).getValue() && !Opcode.getBit(1).getValue()){
                //Push PC in Store
                //store immediate into PC
                for(int i = 0; i < 27; i++){
                    PC.setBit(i, Immediate.getBit(i));
                }
            }
            //Dest Only: Push PC: store Rd + Immediate to PC
            else if(Opcode.getBit(0).getValue() && !Opcode.getBit(1).getValue()){
                //Push PC in Store
                MathOp(RegisterRd,Immediate,add,PC);
            }
            //2R: Push PC: If BoolOp of Rs1 and Rd, store PC + Immediate to PC or not
            else if(Opcode.getBit(0).getValue() && Opcode.getBit(1).getValue()){
                //if Boolean Operation with Rs1 and Rsd is true, Do Math Operation with PC and Immediate
                if(BooleanOp(RegisterRs1,RegisterRd)){
                    //push PC to stack
                    SP.decrement();
                    L2Cache.write(SP.counter, PC);
                    MathOp(PC, Immediate, add, PC);
                }
            }
            //3R: Push PC: If BoolOp of Rs1 and Rs2, Store Rd + Immediate to PC or not
            else{
                //if Boolean Operation with Rs1 and Rs2 is true, Do Math Operation with PC and Immediate
                if(BooleanOp(RegisterRs1,RegisterRs2)){
                    //push PC to stack
                    SP.decrement();
                    L2Cache.write(SP.counter, PC);
                    MathOp(RegisterRd, Immediate, add, PC);
                }
            }
            opType = OpCode.Call;
        }
        //Push (011)
        else if(Opcode.getBit(2).getValue() && Opcode.getBit(3).getValue() && !Opcode.getBit(4).getValue()){
            //No R: UNUSED
            if(regType == registerType.NoR){
                //do nothing
            }
            //Dest Only: store Rd MathOp Immediate to Memory[--sp] 
            else if(regType == registerType.Dest){
                //Does Math Operation to Rd and Immediate
                Word result = new Word();
                MathOp(RegisterRd, Immediate, funct, result);
                //store the result in the stack
                SP.decrement();
                L2Cache.write(SP.counter, result);
            }
            //2R: store Rd Math op Rs Memory[--sp]
            else if(regType == registerType.TwoR){
                //Does Math Operation to Rd and Rs1
                Word result = new Word();
                MathOp(RegisterRd, RegisterRs1, funct, result);
                //store result in stack
                SP.decrement();
                L2Cache.write(SP.counter, result);
            }
            //3R: store Rs1 Math op Rs2 Memory[--sp]
            else{
                //Does Math Operation to Rs1 and Rs2
                Word result = new Word();
                MathOp(RegisterRs1, RegisterRs2, funct, result);
                //store result in stack
                SP.decrement();
                L2Cache.write(SP.counter, result);
            }
            opType = OpCode.Push;
        }
        //Load (100)
        else if(!Opcode.getBit(2).getValue() && !Opcode.getBit(3).getValue() && Opcode.getBit(4).getValue()){
            //No R: Return: Store pop in PC
            if(regType == registerType.NoR){
                //read top of the stack
                Word temp = L2Cache.read(SP.counter);
                //increment stack to "pop"
                SP.increment();

                //store "pop" into PC
                for(int i = 0; i < 32; i++){
                    PC.setBit(i, temp.getBit(i));
                }
            }
            //Dest Only: Store memory[Rd + immediate] to Rd
            else if(regType == registerType.Dest){
                //adds Rd and Immediate
                Word result = new Word();
                Word temp;
                MathOp(RegisterRd, Immediate, add, result);
                //finds value in memory[result]
                temp = L2Cache.read(result);

                //store index in memory into Rd
                for(int i = 0; i < 32; i++){
                    Rd.setBit(i, temp.getBit(i));
                }
            }
            //2R: Store memory[Rs + immediate] to Rd
            else if(regType == registerType.TwoR){
                //adds Rs1 and Immediate
                Word result = new Word();
                Word temp;
                MathOp(RegisterRs1, Immediate, add, result);
                //finds value in memory[result]
                temp = L2Cache.read(result);

                //store index in memory into Rd
                for(int i = 0; i < 32; i++){
                    Rd.setBit(i, temp.getBit(i));
                }
            }
            //3R: Store memory[Rs1 + Rs2] to Rd
            else{
                //adds Rs1 and Rs1
                Word result = new Word();
                Word temp;
                MathOp(RegisterRs1, RegisterRs2, add, result);
                //finds value in memory[result]
                temp = L2Cache.read(result);

                //store index in memory into Rd
                for(int i = 0; i < 32; i++){
                    Rd.setBit(i, temp.getBit(i));
                }
            }
            opType = OpCode.Load;
        }
        //Store (101)
        else if(Opcode.getBit(2).getValue() && !Opcode.getBit(3).getValue() && Opcode.getBit(4).getValue()){
            //No R: UNUSED
            if(regType == registerType.NoR){
                //do nothing
            }
            //Dest Only: Write immediate in Memory[Rd]
            else if(regType == registerType.Dest){
                L2Cache.write(RegisterRd, Immediate);
            }
            //2R: Write Rs in Memory[Rd + immediate]
            else if(regType == registerType.TwoR){
                Word result = new Word();
                MathOp(RegisterRd, Immediate, add, result);
                L2Cache.write(result, RegisterRs1);
            }
            //3R: Write Rs2 in Memory[Rd + Rs1]
            else{
                Word result = new Word();
                MathOp(RegisterRd, RegisterRs1, add, result);
                L2Cache.write(result, RegisterRs2);
            }
            opType = OpCode.Store;
        }
        //Pop/Interrupt (110)
        else if(!Opcode.getBit(2).getValue() && Opcode.getBit(3).getValue() && Opcode.getBit(4).getValue()){
            //No R: INTERRUPT: Push PC: store intvec[immediate] to PC
            if(regType == registerType.NoR){
                //do not immplement yet
                opType = OpCode.Interrupt;
            }
            //Dest Only: POP: store Memory[sp++] to Rd
            else if(regType == registerType.Dest){
                Word temp = L2Cache.read(SP.counter);
                SP.increment();
                //store index in memory into Rd
                for(int i = 0; i < 32; i++){
                    Rd.setBit(i, temp.getBit(i));
                }
                opType = OpCode.Pop;
            }
            //2R: PEEK: store Memory[sp - (Rs + immediate)] to Rd
            else if(regType == registerType.TwoR){
                Word result = new Word();
                Word result2 = new Word();
                Word temp;
                //intializes SP counter if not used
                SP.increment();
                SP.decrement();
                //finds the address in memory
                MathOp(RegisterRs1, Immediate, add, result);
                MathOp(SP.counter, result, subtract, result2);
                //finds word at address
                temp = L2Cache.read(result2);
                //store index in memory into Rd
                for(int i = 0; i < 32; i++){
                    Rd.setBit(i, temp.getBit(i));
                }
                opType = OpCode.Peek;
            }
            //3R: PEEK: store Memory[sp - (Rs1 + Rs2)] in Rd
            else{
                Word result = new Word();
                Word result2 = new Word();
                Word temp;
                //intializes SP counter if not used
                SP.increment();
                SP.decrement();
                //finds the address in memory
                MathOp(RegisterRs1, RegisterRs2, add, result);
                MathOp(SP.counter, result, subtract, result2);
                //finds word at address
                temp = L2Cache.read(result2);
                //store index in memory into Rd
                for(int i = 0; i < 32; i++){
                    Rd.setBit(i, temp.getBit(i));
                }
                opType = OpCode.Peek;
            }
        }
    }
    //copies the result of execute (executeResult) to Rd register
    void store() throws Exception{
        if(opType == OpCode.Math){
            int total = 0;

            //stores value of execute in Rd to variable executeResult
            Word executeResult = new Word();
            executeResult.copy(Rd);

            //reset Rd to the location of the register
            decode();

            //convert Rd into into index to store alu result
            for(int i = 0; i < 5; i++){
                if(Rd.getBit(i).getValue()){
                    total += Math.pow(2, i);
                }
            }
            
            //register 0 is always 0 (never change register 0)
            if(total != 0){
                registers[total] = executeResult;
            }

            //show that method is working
            System.out.println("Store in Register[" + total + "] = " + executeResult.getUnsigned()); 
        }
        else if(opType == OpCode.Call){
            if(regType == registerType.NoR || regType == registerType.Dest){
                //Push PC to stack
                SP.decrement();
                L2Cache.write(SP.counter, PC);
            }
        }
        else if(opType == OpCode.Load){
            if(regType == registerType.Dest || regType == registerType.TwoR || regType == registerType.ThreeR){
                int total = 0;

                //stores value of execute in Rd to variable executeResult
                Word executeResult = new Word();
                executeResult.copy(Rd);

                //reset Rd to the location of the register
                decode();

                //convert Rd into into index to store alu result
                for(int i = 0; i < 5; i++){
                    if(Rd.getBit(i).getValue()){
                        total += Math.pow(2, i);
                    }
                }
                
                //register 0 is always 0 (never change register 0)
                if(total != 0){
                    registers[total] = executeResult;
                }

                //show that method is working
                System.out.println("Store in Register[" + total + "] = " + executeResult.getUnsigned()); 
            }
        }
        else if(opType == OpCode.Peek || opType == OpCode.Pop){
            int total = 0;

            //stores value of execute in Rd to variable executeResult
            Word executeResult = new Word();
            executeResult.copy(Rd);

            //reset Rd to the location of the register
            decode();

            //convert Rd into into index to store alu result
            for(int i = 0; i < 5; i++){
                if(Rd.getBit(i).getValue()){
                    total += Math.pow(2, i);
                }
            }
            
            //register 0 is always 0 (never change register 0)
            if(total != 0){
                registers[total] = executeResult;
            }

            //show that method is working
            System.out.println("Store in Register[" + total + "] = " + executeResult.getUnsigned()); 
        }
    }
    //gets the next instuction, then move PC over 1
    void fetch() throws Exception{
        //reads address and caches the next 8 instruction
        currentInstruction = InstructionCache.read(PC.counter);

        PC.increment();
    }
    //math op and store result into storeLocation
    void MathOp(Word location1, Word location2, Bit[] function, Word storeLocation) throws Exception{
        //puts value of registers of index location1 and location2 into ALU
        alu = new ALU(location1,location2);

        //if function is MULTIPLY
        if(function[0].getValue() && function[1].getValue() && function[2].getValue() && !function[3].getValue()){
            currentClockCycle += 10;
        }
        //if any other ALU function
        else{
            currentClockCycle += 2;
        }

        //execute math operation
        alu.doOperation(function);
        //stores result in storeLocation
        for(int i = 0; i < 32; i++){
            storeLocation.setBit(i, alu.result.getBit(i));
        }
    }
    //does boolean operation on two words
    boolean BooleanOp(Word location1, Word location2){
        boolean condition = false; 
        //gets boolean function type
        Bit[] funct = new Bit[4];
        for(int i = 0; i < 4; i++){
            funct[i] = Function.getBit(i);
        }

        //returns true if boolean operation is true 
        //Equals
        if(!funct[0].getValue() && !funct[1].getValue() && !funct[2].getValue() && !funct[3].getValue()){
            System.out.println("EQ");
            if(location1.getUnsigned() == location2.getUnsigned()){
                condition = true;
            }
        }
        //Not Equal
        else if(funct[0].getValue() && !funct[1].getValue() && !funct[2].getValue() && !funct[3].getValue()){
            System.out.println("NEQ");
            if(location1.getUnsigned() != location2.getUnsigned()){
                condition = true;
            }
        }
        //Less Than
        else if(!funct[0].getValue() && funct[1].getValue() && !funct[2].getValue() && !funct[3].getValue()){
            System.out.println("LT");
            if(location1.getUnsigned() < location2.getUnsigned()){
                condition = true;
            }
        }
        //Greater Than or Equal
        else if(funct[0].getValue() && funct[1].getValue() && !funct[2].getValue() && !funct[3].getValue()){
            System.out.println("GE");
            if(location1.getUnsigned() >= location2.getUnsigned()){
                condition = true;
            }
        }
        //Greater Than
        else if(!funct[0].getValue() && !funct[1].getValue() && funct[2].getValue() && !funct[3].getValue()){
            System.out.println("GT");
            if(location1.getUnsigned() > location2.getUnsigned()){
                condition = true;
            }
        }
        //Less than or Equal
        else if(funct[0].getValue() && !funct[1].getValue() && funct[2].getValue() && !funct[3].getValue()){
            System.out.println("LE");
            if(location1.getUnsigned() <= location2.getUnsigned()){
                condition = true;
            }
        }
        return condition;
    }
}