public class InstructionCache {
    static Word[] memory = new Word[8];
    static Word memoryAddress = null;

    public static Word read(Word address) throws Exception{
        //if the address matches cached memory
        if(memoryAddress != null && (address.getUnsigned() - memoryAddress.getUnsigned()) < 8 && (address.getUnsigned() - memoryAddress.getUnsigned()) >= 0){
            //take 10 clock cycles on cache hit
            Processor.currentClockCycle += 10;
            return memory[(int)address.getUnsigned() - (int)memoryAddress.getUnsigned()];
        }
        //if address doesnt match
        else{
            //go to L2 cache and either fill instruction cache or fill L2 and then fill instruction cache
            return L2Cache.getCache(address);
        }
    }
    //write the value into cache
    public static void write(int address, Word value){
        if(value != null){
            Word temp = new Word();
            temp.copy(value);
            memory[address] = temp;
        }   
    }
    //clears the cached memory and address
    public static void reset(){
        memory = new Word[8];
        memoryAddress = null;
    }
}