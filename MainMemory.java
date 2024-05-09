public class MainMemory {
    static Word[] memory = new Word[1024]; //DRAM
    //checks memory for word with address and returns new word in the address
    public static Word read(Word address) throws Exception{
        //if address is in bounds and not null
        if(address.getUnsigned() >= 0 && address.getUnsigned() < 1024 && memory[(int)address.getUnsigned()] != null){
            return memory[(int)address.getUnsigned()];
        }
        return null;
    }
    //puts word into memory at address if not the address is empty
    public static void write(Word address, Word value){
        //store word in address if null
        if(memory[(int)address.getUnsigned()] == null){
            Word temp = new Word();
            temp.copy(value);
            memory[(int)address.getUnsigned()] = temp;
        }
    }
    //processes an array of 0's and 1's into the DRAM
    public static void load(String[] data) throws Exception{
        Word temp = new Word();
        int count = 0;
        //for each "0" or "1"
        for(String s: data){
            //sets the bits in the word
            if(s.equals("1")){
                temp.setBit(count++, new Bit(true));
            }
            else{
                temp.setBit(count++, new Bit(false));
            }
            //if enough for whole word
            if(count == 32){
                //find empty address to write to
                for(int i = 0; i < 1024; i++){
                    Word address = new Word();
                    address.set(i);
                    //if address is empty, write word
                    if(read(address) == null){
                        write(address, temp);
                        break;
                    }
                }
                //reset word
                count = 0;
                temp.set(0);
            }
        }
        //if partial word remaining
        if(temp.getUnsigned() > 0){
            //find empty address to write to
            for(int i = 0; i < 1024; i++){
                Word address = new Word();
                address.set(i);
                if(read(address) == null){
                    write(address, temp);
                    break;
                }
            }
        }
    }
}
