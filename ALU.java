public class ALU {
    public Word op1 = new Word(), op2 = new Word(), result = new Word();

    //sets the 2 word that are getting executed
    ALU(Word word1, Word word2){
        op1 = word1;
        op2 = word2;
    }
    ALU(){
        
    }

    //look at array of bit and determine what operation to execute
    //throws exception if not an available operation
    public void doOperation(Bit[] operation) throws Exception{
        if(operation[0].getValue()){
            if(operation[1].getValue()){
                if(operation[2].getValue()){
                    if(operation[3].getValue()){
                        //1111 SUBT
                        result.copy(subtract(op1, op2));
                        return;
                    }
                    else{
                        //0111 MULT
                        result.copy(multiply(op1, op2));
                        return;
                    }
                }
                else{
                    if(operation[3].getValue()){
                        //1011 NOT
                        result.copy(op1.not());
                        return;
                    }
                    else{
                        //0011
                        throw new Exception("Incorrect Bit Value");
                    }
                }
            }
            else{
                if(operation[2].getValue()){
                    if(operation[3].getValue()){
                        //1101 RIGHT
                        int shiftAmount = op2.leftShift(27).rightShift(27).getSigned();
                        result.copy(op1.rightShift(shiftAmount));
                        return;
                    }
                    else{
                        //0101
                        throw new Exception("Incorrect Bit Value");
                    }
                }
                else{
                    if(operation[3].getValue()){
                        //1001 OR
                        result.copy(op1.or(op2));
                        return;
                    }
                    else{
                        //0001
                        throw new Exception("Incorrect Bit Value");
                    }
                }
            }
        }
        else{
            if(operation[1].getValue()){
                if(operation[2].getValue()){
                    if(operation[3].getValue()){
                        //1110 ADD
                        result.copy(add(op1, op2));
                        return;
                    }
                    else{
                        //0110
                        throw new Exception("Incorrect Bit Value");
                    }
                }
                else{
                    if(operation[3].getValue()){
                        //1010 XOR
                        result.copy(op1.xor(op2));
                        return;
                    }
                    else{
                        //0010
                        throw new Exception("Incorrect Bit Value");
                    }
                }
            }
            else{
                if(operation[2].getValue()){
                    if(operation[3].getValue()){
                        //1100 LEFT
                        int shiftAmount = op2.leftShift(27).rightShift(27).getSigned();
                        result.copy(op1.leftShift(shiftAmount));
                        return;
                    }
                    else{
                        //0100
                        throw new Exception("Incorrect Bit Value");
                    }
                }
                else{
                    if(operation[3].getValue()){
                        //1000 AND
                        result.copy(op1.and(op2));
                        return;
                    }
                    else{
                        //0000
                        throw new Exception("Incorrect Bit Value");
                    }
                }
            }
        }
    }

    //adds 2 words bit by bit accounting for carry overs
    //returns result word
    public Word add2(Word word1, Word word2){
        Word result = new Word();
        Bit Cin = new Bit(false);
        Bit bit1, bit2;
        for(int i = 0; i < 32; i++){
            //gets the digits of the 2 words
            bit1 = word1.getBit(i);
            bit2 = word2.getBit(i);

            //if the sum equals 1
            if(bit1.xor(bit2).xor(Cin).getValue()){
                result.setBit(i, new Bit(true));
            }
            //if the sum equals 0
            else{
                result.setBit(i, new Bit(false));
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
        return result;
    }

    //adds 4 words together bit by bit accounting for carries
    //returns result word
    public Word add4(Word word1, Word word2, Word word3, Word word4){
        Word result = new Word();
        Bit cin = new Bit(false);
        Bit bit1, bit2, bit3, bit4;
        int carry = 0;
        for(int i = 0; i < 32; i++){
            //gets the digits of all 4 words
            bit1 = word1.getBit(i);
            bit2 = word2.getBit(i);
            bit3 = word3.getBit(i);
            bit4 = word4.getBit(i);

            //if the sum equals 1
            if(bit1.xor(bit2).xor(bit3).xor(bit4).xor(cin).getValue()){
                result.setBit(i, new Bit(true));
            }
            //if the sum equals 0
            else{
                result.setBit(i, new Bit(false));
            }

            //adds the bit values to a sum
            if(bit1.getValue()){
                carry++;
            }
            if(bit2.getValue()){
                carry++;
            }
            if(bit3.getValue()){
                carry++;
            }
            if(bit4.getValue()){
                carry++;
            }

            //finds the amount of carry 
            carry = carry/2;

            //if carry is not even then carry to the next digits
            if(carry % 2 != 0){
                cin.set(true);
            }
            else{
                cin.set(false);
            }
        }
        return result;
    }
    //adds 2 words
    //returns resulting word
    Word add(Word word1, Word word2){
        return add2(word1,word2);
    }
    //substracts word1 by word2
    //return resulting word
    Word subtract(Word word1, Word word2){
        //makes word2 negative
        Word addOne = new Word();
        addOne.setBit(0, new Bit(true));;
        word2 = word2.not();
        word2 = add(word2, addOne);

        //adds negative word2 to word1
        return add(word1, word2);
    }

    //multiples word1 by word2
    //returns word
    Word multiply(Word word1, Word word2){
        Word word_1 = new Word(), word_2 = new Word(), word_3 = new Word(), word_4 = new Word();
        Word[] words = new Word[8];

        //using 8 4-way adders to add 4 words
        for (int i = 0; i < 8; i++){
            //if value is true, copy the word and left shift by position in word
            if(word2.getBit(4*i).getValue()){
                word_1 = word1.leftShift(4*i);
            }
            if(word2.getBit(4*i+1).getValue()){
                word_2 = word1.leftShift(4*i+1);
            }
            if(word2.getBit(4*i+2).getValue()){
                word_3 = word1.leftShift(4*i+2);
            }
            if(word2.getBit(4*i+3).getValue()){
                word_4 = word1.leftShift(4*i+3);
            }
            //adds 4 words and stored into array
            words[i] = add4(word_1, word_2, word_3, word_4);

            //reset temporary words
            word_1.set(0);
            word_2.set(0);
            word_3.set(0);
            word_4.set(0);
        }
        //adds the final 4-way adders
        return add2(add4(words[0],words[1],words[2],words[3]),add4(words[4],words[5],words[6],words[7]));
    }
}