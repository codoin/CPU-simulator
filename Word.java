public class Word {
    private Bit[] word;
    Word counter;

    //word constructor that sets all bits to 0 on default
    protected Word(){
        word = new Bit[32];
        for(int i = 0; i < 32; i++){
            word[i] = new Bit(false);
        }
    }

    protected Bit getBit(int i){
        return new Bit(word[i].getValue());
    }
    protected void setBit(int i, Bit val){
        word[i].set(val.getValue());
    }
    //sets the array of bits by string of 1 and 0 
    protected void setBitString(String bits){
        //checks for correct string size
        if(bits.length()>32){
            System.out.println("String too long");
            return;
        }
        //puts string value into array
        String[] data = new String[32];
        int count = 0;
        data = bits.split("(?!^)");
        
        for(int i = 31; i >= 0; i--){
            //sets the bits in the word
            if(data[i].equals("1")){
                setBit(count++, new Bit(true));
            }
            else{
                setBit(count++, new Bit(false));
            }
        }
    }
    //performs AND operation on two words and returns a new word with the result
    protected Word and(Word other){
        Word andWord = new Word();

        for(int i = 0; i < 32; i++){
            andWord.setBit(i,getBit(i).and(other.getBit(i)));
        }

        return andWord;
    }
    //performs OR operation on two words and returns a new word with the result
    protected Word or(Word other){
        Word orWord = new Word();

        for(int i = 0; i < 32; i++){
            orWord.setBit(i,getBit(i).or(other.getBit(i)));
        }

        return orWord;
    }
    //performs XOR operation on two words and returns a new word with the result
    protected Word xor(Word other){
        Word xorWord = new Word();

        for(int i = 0; i < 32; i++){
            xorWord.setBit(i,getBit(i).xor(other.getBit(i)));
        }

        return xorWord;
    }
    //performs NOT operation on a word and returns a new word with the result
    protected Word not(){
        Word notWord = new Word();

        for(int i = 0; i < 32; i++){
            notWord.setBit(i,getBit(i).not());
        }

        return notWord;
    }
    //right shift this word by amount bits
    protected Word rightShift(int amount){
        Word rightWord = new Word();

        for(int i = amount; i < 32; i++){
            rightWord.setBit(i-amount,getBit(i));
        }

        return rightWord;
    }
    //left shift this word by amount bits
    protected Word leftShift(int amount){
        Word leftWord = new Word();

        for(int i = 0; i < 32-amount; i++){
            leftWord.setBit(i+amount,getBit(i));
        }

        return leftWord;
    }
    //returns a string of t and f corresponding to the word
    public String toString(){
        String string = "";
        for(int i = 31; i >= 0; i--){
            if(getBit(i).getValue()){
                string += "t";
            }
            else{
                string += "f";
            }

            if(i != 0){
                string += ",";
            }
        }
        return string;
    }
    //returns word's value as an unsigned long 
    protected long getUnsigned(){
        long value = 0;
        //goes through all the bits and check if the bit is true 
        //if bit value is true, add 2^(bit number - 1) to the total
        for(int i = 0; i < 32; i++){
            if(getBit(i).getValue()){
                value += Math.pow(2, i);
            }
        }
        return value;
    }
    //returns word's as a signed integer
    protected int getSigned(){
        Word temp = new Word();
        int value = 0;

        //copies values to temp
        for(int i=0; i<=31; i++){
            temp.setBit(i,word[i]);
        }

        //checks if the value is negative
        if(temp.getBit(31).getValue()){
            //inverts bits in the word
            temp = temp.not();

            //adds 1 to the word
            if(temp.getBit(0).getValue()){ //has to carry over
                //loops through word to see where to carry over stops
                for(int i = 0; i <= 30; i++){
                    if(temp.getBit(i).getValue()){
                        temp.setBit(i, new Bit(false));
                    }
                    else{
                        temp.setBit(i, new Bit(true));
                        break;
                    }
                }
            }
            else{//does not need to carry over
                temp.setBit(0, new Bit(true));
            }

            //converts word's value to int
            for(int i = 0; i < 31; i++){
                if(temp.getBit(i).getValue()){
                    value += Math.pow(2, i);
                }
            }
            value *= -1;
        }
        else{ //if value is positive
            //coverts word's value to int
            for(int i = 0; i < 31; i++){
                if(temp.getBit(i).getValue()){
                    value += Math.pow(2, i);
                }
            }
        }

        return value;
    }
    //copies the bit values of other word
    protected void copy(Word other){
        for(int i = 0; i < 32; i++){
            setBit(i, other.getBit(i));
        }
    }
    //sets the word to the value of the signed int value
    protected void set(int value){
        int temp = value;

        //if positive, set last bit to 0. Else set true
        if(value >= 0){
            setBit(31, new Bit(false));
        }
        else{
            setBit(31, new Bit(true));
        }

        temp = Math.abs(temp);

        //coverts absolute value of int to binary word
        //and set the correct bits
        for(int i = 30; i >= 0; i--){
            if(temp/((int)Math.pow(2, i)) >0){
                temp -= Math.pow(2, i);
                setBit(i, new Bit(true));
            }
            else{
                setBit(i, new Bit(false));
            }
        }

        //if value is negative 
        if(value < 0){
            //invert bits
            for(int i = 30; i >= 0; i--){
                if(getBit(i).getValue()){
                    setBit(i, new Bit(false));
                }
                else{
                    setBit(i, new Bit(true));
                }
            }
            
            //adds 1
            //if true, then has to carry over the 1. else does not carry over
            if(getBit(0).getValue()){
                //loops through word to see where the carry over stops
                for(int i = 0; i <= 30; i++){
                    if(getBit(i).getValue()){
                        setBit(i, new Bit(false));
                    }
                    else{
                        setBit(i, new Bit(true));
                        break;
                    }
                }
            }
            else{
                setBit(0, new Bit(true));
            }
        }
    }

    //increase value by 1
    void increment(){
        Bit Cin = new Bit(true);
        if(counter == null){
            counter = new Word();
            for(int i = 0; i <= 30; i++){
                Bit bit = getBit(i);
                //if the sum equals 1
                if(bit.xor(Cin).getValue()){
                    counter.setBit(i, new Bit(true));
                }
                //if the sum equals 0
                else{
                    counter.setBit(i, new Bit(false));
                }

                //if there is a carry of 1
                if(bit.and(Cin).getValue()){
                    Cin.set(true);
                }
                //if there is a carry of 0
                else{
                    Cin.set(false);
                }
            }
        }
        else{
            for(int i = 0; i <= 30; i++){
                Bit bit = counter.getBit(i);
                //if the sum equals 1
                if(bit.xor(Cin).getValue()){
                    counter.setBit(i, new Bit(true));
                }
                //if the sum equals 0
                else{
                    counter.setBit(i, new Bit(false));
                }

                //if there is a carry of 1
                if(bit.and(Cin).getValue()){
                    Cin.set(true);
                }
                //if there is a carry of 0
                else{
                    Cin.set(false);
                }
            }
        }
    }
    //adds the counter and -1 to decrement
    void decrement(){
        Word decrement = new Word();
        decrement.set(-1);
        if(counter == null){
            counter = new Word();
            Bit Cin = new Bit(false);
            Bit bit1, bit2;
            for(int i = 0; i < 32; i++){
                //gets the digits of the 2 words
                bit1 = getBit(i);
                bit2 = decrement.getBit(i);

                //if the sum equals 1
                if(bit1.xor(bit2).xor(Cin).getValue()){
                    counter.setBit(i, new Bit(true));
                }
                //if the sum equals 0
                else{
                    counter.setBit(i, new Bit(false));
                }

                //if there is a carry of 1
                if(bit1.and(bit2).or((bit1.xor(bit2)).and(Cin)).getValue()){
                    Cin.set(true);
                }
                //if there is a carry of 0
                else{
                    Cin.set(false);
                }
            }
        }
        else{
            Bit Cin = new Bit(false);
            Bit bit1, bit2;
            for(int i = 0; i < 32; i++){
                //gets the digits of the 2 words
                bit1 = counter.getBit(i);
                bit2 = decrement.getBit(i);

                //if the sum equals 1
                if(bit1.xor(bit2).xor(Cin).getValue()){
                    counter.setBit(i, new Bit(true));
                }
                //if the sum equals 0
                else{
                    counter.setBit(i, new Bit(false));
                }

                //if there is a carry of 1
                if(bit1.and(bit2).or((bit1.xor(bit2)).and(Cin)).getValue()){
                    Cin.set(true);
                }
                //if there is a carry of 0
                else{
                    Cin.set(false);
                }
            }
        }
    }
}
