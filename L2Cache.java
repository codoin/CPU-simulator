public class L2Cache {
    static Word[] memory1 = new Word[8];
    static Word[] memory2 = new Word[8];
    static Word[] memory3 = new Word[8];
    static Word[] memory4 = new Word[8];
    static Word memoryAddress1 = null;
    static Word memoryAddress2 = null;
    static Word memoryAddress3 = null;
    static Word memoryAddress4 = null;

    //fills instruction cache
    public static Word getCache(Word address) throws Exception{
        InstructionCache.memoryAddress = new Word();
        InstructionCache.memoryAddress.copy(address);

        //if the address matches L2 cached memory
        if(memoryAddress1 != null && (address.getUnsigned() - memoryAddress1.getUnsigned()) < 8 && (address.getUnsigned() - memoryAddress1.getUnsigned()) >= 0){
            fill(memory1);
            return InstructionCache.memory[(int)address.getUnsigned() - (int)memoryAddress1.getUnsigned()];
        }
        //if the address matches L2 cached memory
        else if(memoryAddress2 != null && (address.getUnsigned() - memoryAddress2.getUnsigned()) < 8 && (address.getUnsigned() - memoryAddress2.getUnsigned()) >= 0){
            fill(memory2);
            return InstructionCache.memory[(int)address.getUnsigned() - (int)memoryAddress2.getUnsigned()];
        }
        //if the address matches L2 cached memory
        else if(memoryAddress3 != null && (address.getUnsigned() - memoryAddress3.getUnsigned()) < 8 && (address.getUnsigned() - memoryAddress3.getUnsigned()) >= 0){
            fill(memory3);
            return InstructionCache.memory[(int)address.getUnsigned() - (int)memoryAddress3.getUnsigned()];
        }
        //if the address matches L2 cached memory
        else if(memoryAddress4 != null && (address.getUnsigned() - memoryAddress4.getUnsigned()) < 8 && (address.getUnsigned() - memoryAddress4.getUnsigned()) >= 0){
            fill(memory4);
            return InstructionCache.memory[(int)address.getUnsigned() - (int)memoryAddress4.getUnsigned()];
        }
        //if address doesnt match
        else{
            //writes memoryAddress to match address
            memoryAddress1 = new Word();
            memoryAddress2 = new Word();
            memoryAddress3 = new Word();
            memoryAddress4 = new Word();

            memoryAddress1.copy(address);

            //writes the next 8 instructions
            int addressVal = (int)address.getUnsigned();
            Word temp = new Word();
            temp.copy(address);
            for(int i = 0; i < 8; i++){
                setCache(i, MainMemory.read(temp),memory1);
                addressVal++;
                temp.set(addressVal);
            }

            memoryAddress2.copy(temp);

            //writes the next 8 instructions
            for(int i = 0; i < 8; i++){
                setCache(i, MainMemory.read(temp),memory2);
                addressVal++;
                temp.set(addressVal);
            }

            memoryAddress3.copy(temp);

            //writes the next 8 instructions
            for(int i = 0; i < 8; i++){
                setCache(i, MainMemory.read(temp),memory3);
                addressVal++;
                temp.set(addressVal);
            }

            memoryAddress4.copy(temp);

            //writes the next 8 instructions
            for(int i = 0; i < 8; i++){
                setCache(i, MainMemory.read(temp),memory4);
                addressVal++;
                temp.set(addressVal);
            }

            //cache miss takes 350 clock cycles
            Processor.currentClockCycle += 350;

            fill(memory1);
            return InstructionCache.memory[0];
        }
    }
    //writes the value into cache
    public static void setCache(int address, Word value, Word[]memory){
        if(value != null){
            Word temp = new Word();
            temp.copy(value);
            memory[address] = temp;
        }   
    }
    //fill the instruction cache with the values in L2 cache
    public static void fill(Word[] l2Memory){
        //filling instruction cache takes 50 clock cycles
        Processor.currentClockCycle += 50;
        for(int i = 0; i < 8; i++){
            if(l2Memory[i] != null){
                Word temp = new Word();
                temp.copy(l2Memory[i]);
                InstructionCache.memory[i] = temp;
            }
        } 
    }
    public static Word read(Word address) throws Exception{
        //reading cache takes 50 clock cycles
        Processor.currentClockCycle += 50;

        //if the address matches L2 cached memory
        if(memoryAddress1 != null && (address.getUnsigned() - memoryAddress1.getUnsigned()) < 8 && (address.getUnsigned() - memoryAddress1.getUnsigned()) >= 0){
            return memory1[(int)address.getUnsigned() - (int)memoryAddress1.getUnsigned()];
        }
        //if the address matches L2 cached memory
        else if(memoryAddress2 != null && (address.getUnsigned() - memoryAddress2.getUnsigned()) < 8 && (address.getUnsigned() - memoryAddress2.getUnsigned()) >= 0){
            return memory2[(int)address.getUnsigned() - (int)memoryAddress2.getUnsigned()];
        }
        //if the address matches L2 cached memory
        else if(memoryAddress3 != null && (address.getUnsigned() - memoryAddress3.getUnsigned()) < 8 && (address.getUnsigned() - memoryAddress3.getUnsigned()) >= 0){
            return memory3[(int)address.getUnsigned() - (int)memoryAddress3.getUnsigned()];
        }
        //if the address matches L2 cached memory
        else if(memoryAddress4 != null && (address.getUnsigned() - memoryAddress4.getUnsigned()) < 8 && (address.getUnsigned() - memoryAddress4.getUnsigned()) >= 0){
            return memory4[(int)address.getUnsigned() - (int)memoryAddress4.getUnsigned()];
        }
        //if address doesnt match
        else{
            //writes memoryAddress to match address
            memoryAddress1 = new Word();
            memoryAddress2 = new Word();
            memoryAddress3 = new Word();
            memoryAddress4 = new Word();

            memoryAddress1.copy(address);

            //writes the next 8 instructions
            int addressVal = (int)address.getUnsigned();
            Word temp = new Word();
            temp.copy(address);
            for(int i = 0; i < 8; i++){
                setCache(i, MainMemory.read(temp),memory1);
                addressVal++;
                temp.set(addressVal);
            }

            memoryAddress2.copy(temp);

            //writes the next 8 instructions
            for(int i = 0; i < 8; i++){
                setCache(i, MainMemory.read(temp),memory2);
                addressVal++;
                temp.set(addressVal);
            }

            memoryAddress3.copy(temp);

            //writes the next 8 instructions
            for(int i = 0; i < 8; i++){
                setCache(i, MainMemory.read(temp),memory3);
                addressVal++;
                temp.set(addressVal);
            }

            memoryAddress4.copy(temp);

            //writes the next 8 instructions
            for(int i = 0; i < 8; i++){
                setCache(i, MainMemory.read(temp),memory4);
                addressVal++;
                temp.set(addressVal);
            }
            return memory1[0];
        }
    }
    public static void write(Word address, Word value) throws Exception{
        //writing to cache takes 50 clock cycles
        Processor.currentClockCycle += 50;
        
        //if the address matches L2 cached memory
        if(memoryAddress1 != null && (address.getUnsigned() - memoryAddress1.getUnsigned()) < 8 && (address.getUnsigned() - memoryAddress1.getUnsigned()) >= 0){
            memory1[(int)address.getUnsigned() - (int)memoryAddress1.getUnsigned()] = value;
        }
        //if the address matches L2 cached memory
        else if(memoryAddress2 != null && (address.getUnsigned() - memoryAddress2.getUnsigned()) < 8 && (address.getUnsigned() - memoryAddress2.getUnsigned()) >= 0){
            memory2[(int)address.getUnsigned() - (int)memoryAddress2.getUnsigned()] = value;
        }
        //if the address matches L2 cached memory
        else if(memoryAddress3 != null && (address.getUnsigned() - memoryAddress3.getUnsigned()) < 8 && (address.getUnsigned() - memoryAddress3.getUnsigned()) >= 0){
            memory3[(int)address.getUnsigned() - (int)memoryAddress3.getUnsigned()] = value;
        }
        //if the address matches L2 cached memory
        else if(memoryAddress4 != null && (address.getUnsigned() - memoryAddress4.getUnsigned()) < 8 && (address.getUnsigned() - memoryAddress4.getUnsigned()) >= 0){
            memory4[(int)address.getUnsigned() - (int)memoryAddress4.getUnsigned()] = value;
        }
        MainMemory.write(address, value);
    }
    //resets cached memory and address
    public static void reset(){
        memory1 = new Word[8];
        memory2 = new Word[8];
        memory3 = new Word[8];
        memory4 = new Word[8];
        memoryAddress1 = null;
        memoryAddress2 = null;
        memoryAddress3 = null;
        memoryAddress4 = null;
    }
}