public class Bit{
    private boolean value;
    //constructor using a default value of the bit
    protected Bit(boolean val){
        value = val;
    }

    //changes the value of the bit to val
    protected void set(boolean val){
        value = val;
    }
    //changes the value of the bit to the opposite
    protected void toggle(){
        value = !value;
    }
    //turns bit value to true
    protected void set(){
        value = true;
    }
    //turns bit value to false
    protected void clear(){
        value = false;
    }
    protected boolean getValue(){
        return value;
    }
    //performs AND operation and returns a new bit with the result
    protected Bit and(Bit other){
        if(value){
            if(other.getValue()){
                return new Bit(true);
            }
        }
        return new Bit(false);
    }
    //performs OR operation and returns a new bit with the result
    protected Bit or(Bit other){
        if(value){
            return new Bit(true);
        }
        else if(other.getValue()){
            return new Bit(true);
        }
        else{
            return new Bit(false);
        }
    }
    //performs XOR operation and returns a new bit with the result
    protected Bit xor(Bit other){
        if(value){
            if(other.getValue()){
                return new Bit(false);
            }
            return new Bit(true);
        }
        else if(other.getValue()){
            if(value){
                return new Bit(false);
            }
            return new Bit(true);
        }
        else{
            return new Bit(false);
        }
    }
    //performs NOT operation and returns a new bit with the result
    protected Bit not(){
        return new Bit(!value);
    }
    public String toString(){
        if(value){
            return "t";
        }
        return "f";
    }
}