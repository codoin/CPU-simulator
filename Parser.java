import java.util.LinkedList;

public class Parser {
    LinkedList<Token> list;
    private TokenManager manager;
    String output = "";

    //takes in list of token to parse and create token manager
    Parser (LinkedList<Token> lexerlist){ 
        list = lexerlist;
        manager = new TokenManager(lexerlist);
    }

    //processes the program as instructions (statements)
    //program -> statements
    String Parse() throws Exception{
        //removes newlines
        AcceptSeperators();

        //while list is not empty
        //statements -> statement NEWLINE statements | statement NEWLINE
        while(manager.MoreTokens()){
            //if the next is an instruction
            if(isInstruction()){
                //stores binary statement in a string
                //statement needs a newline after it
                output += parseInstruction();
                if(manager.MoreTokens() && !AcceptSeperators()){
                    throw new Exception("NO NEWLINE");
                }
            }
            else{
                throw new Exception("Can't Parse");
            }
        }
        return output;
    }
    //finds the instruction (MATH,COPY,HALT,etc.) and generate the correct instruction code
    //statement -> math | branch | halt | copy | jump | call | push | pop | load | store | return | peek | pop | interrupt
    String parseInstruction() throws Exception{
        Token temp = null;
        String rd = "";

        //shortcut statements for HALT, COPY, and JUMP because of simplicity
        if(manager.MatchAndRemove(Token.TokenTypes.HALT) != null){
            return "00000000000000000000000000000000";
        }
        else if(manager.MatchAndRemove(Token.TokenTypes.COPY) != null){
            //gets the immediate then returns immediate + opcode
            return parseDestOnly("000", "0000") + "00001";
        }
        else if(manager.MatchAndRemove(Token.TokenTypes.JUMP) != null){
            int count = countRegister();
            if(count == 0 || count == 1){
                //in DEST ONLY rd is not used but need to differentiate from No R's jump
                temp = manager.MatchAndRemove(Token.TokenTypes.REGISTER);
                if(temp != null){
                    rd = toBinary(Integer.parseInt(temp.GetValue())).substring(27,32);
                }
            }
            else{
                throw new Exception("JUMP: Register Count");
            }

            //finds immediate
            temp = manager.MatchAndRemove(Token.TokenTypes.NUMBER);
            if(temp != null){
                //if No R: returns immediate + opcode
                //else DEST only: return immediate + function + rd + opcode
                if(rd.isEmpty()){
                    String imm = toBinary(Integer.parseInt(temp.GetValue())).substring(5,32);
                    return imm + "00100";
                }
                else{
                    String imm = toBinary(Integer.parseInt(temp.GetValue())).substring(14,32);
                    return imm + "0000" + rd + "00101";
                }
            }
            else{
                throw new Exception("JUMP: Can't Parse");
            }
        }
        else if(manager.MatchAndRemove(Token.TokenTypes.RETURN) != null){
            return "00000000000000000000000000010000";
        }
        else{
            return parseStatement();
        }
    }

    String parseStatement() throws Exception{
        //checks for remaining instruction tokentypes 
        //if none match then throw exception
        if(manager.Peek(0).GetToken().equals(Token.TokenTypes.MATH)){
            return parseMath();
        }
        else if(manager.Peek(0).GetToken().equals(Token.TokenTypes.BRANCH)){
            return parseBranch();
        }
        else if(manager.Peek(0).GetToken().equals(Token.TokenTypes.CALL)){
            return parseCall();
        }
        else if(manager.Peek(0).GetToken().equals(Token.TokenTypes.PUSH)){
            return parsePush();
        }
        else if(manager.Peek(0).GetToken().equals(Token.TokenTypes.LOAD)){
            return parseLoad();
        }   
        else if(manager.Peek(0).GetToken().equals(Token.TokenTypes.STORE)){
            return parseStore();
        }
        else if(manager.Peek(0).GetToken().equals(Token.TokenTypes.PEEK)){
            return parsePeek();
        }
        else if(manager.Peek(0).GetToken().equals(Token.TokenTypes.POP)){
            return parsePop();
        }
        else{
            throw new Exception("INSTRUCTION: Can't Parse");
        }
    }

    //math -> MATH MOP twoR | MATH MOP threeR
    String parseMath() throws Exception{
        String code = getInstructionCode();

        //finds Math Operation function
        if(!isMathOperation()){
            throw new Exception("MATH: NO MATH OPERATION");
        }
        String function = getMathOperation();

        //formats the opcode
        String opcode = code + getInstructionFormat();

        //parses based on register count (2R or 3R)
        int count = countRegister();
        if(count == 2){
            return parseTwoR(code, function) + opcode;
        }
        else if(count == 3){
            return parseThreeR(code, function) + opcode;
        }
        else{
            throw new Exception("MATH: REGISTER COUNT");
        }
    }

    //branch -> BRANCH BOP twoR | BRANCH BOP threeR
    String parseBranch() throws Exception{
        String code = getInstructionCode();

        //finds Boolean Operation function
        if(!isBooleanOperation()){
            throw new Exception("BRANCH: NO BOOLEAN OPERATION");
        }
        String function = getBooleanOperation();

        //formats opcode
        String opcode = code + getInstructionFormat();

        //parses based on register count (2R or 3R)
        int count = countRegister();
        if(count == 2){
            return parseTwoR(code, function) + opcode;
        }
        else if(count == 3){
            return parseThreeR(code, function) + opcode;
        }
        else{
            throw new Exception("BRANCH: REGISTER COUNT");
        }
    }

    //call -> CALL noR | CALL destOnly | CALL BOP twoR | CALL COP threeR
    String parseCall() throws Exception{
        String code = getInstructionCode();
        String function = "";

        //finds Boolean Operation function, some do not
        if(isBooleanOperation()){
            function = getBooleanOperation();
        }

        //formats opcode
        String opcode = code + getInstructionFormat();

        //parses based on register count (NoR, Dest Only, 2R or 3R)
        int count = countRegister();
        if(count == 0){
            return parseNoR() + opcode;
        }
        else if(count == 1){
            return parseDestOnly(code, "0000") + opcode;
        }
        else if(count == 2){
            if(function.isEmpty()){
                throw new Exception("CALL: NO FUNCTION");
            }
            return parseTwoR(code, function) + opcode;
        }
        else if(count == 3){
            if(function.isEmpty()){
                throw new Exception("CALL: NO FUNCTION");
            }
            return parseThreeR(code, function) + opcode;
        }
        else{
            throw new Exception("CALL: REGISTER COUNT");
        }
    }

    //push -> PUSH MOP destOnly | PUSH MOP twoR | PUSH MOP threeR
    String parsePush() throws Exception{
        String code = getInstructionCode();

        //finds Math Operation function
        if(!isMathOperation()){
            throw new Exception("PUSH: NO MATH OPERATION");
        }
        String function = getMathOperation();

        //formats opcode
        String opcode = code + getInstructionFormat();

        //parses based on register count (Dest Only, 2R or 3R)
        int count = countRegister();
        if(count == 0){
            throw new Exception("PUSH: UNUSED");
        }
        else if(count == 1){
            return parseDestOnly(code, function) + opcode;
        }
        else if(count == 2){
            return parseTwoR(code, function) + opcode;
        }
        else if(count == 3){
            return parseThreeR(code, function) + opcode;
        }
        else{
            throw new Exception("PUSH: REGISTER COUNT");
        }
    }

    //load -> LOAD destOnly | LOAD twoR | LOAD threeR
    String parseLoad() throws Exception{
        //formats opcode
        String code = getInstructionCode();
        String opcode = code + getInstructionFormat();

        //parses based on register count (Dest Only, 2R or 3R)
        int count = countRegister();
        if(count == 1){
            return parseDestOnly(code, "0000") + opcode;
        }
        else if(count == 2){
            return parseTwoR(code, "0000") + opcode;
        }
        else if(count == 3){
            return parseThreeR(code, "0000") + opcode;
        }
        else{
            throw new Exception("LOAD: REGISTER COUNT");
        }
    }

    //store -> STORE destOnly | STORE twoR | STORE threeR
    String parseStore() throws Exception{
        //formats opcode
        String code = getInstructionCode();
        String opcode = code + getInstructionFormat();

        //parses based on register count (Dest Only, 2R or 3R)
        int count = countRegister();
        if(count == 0){
            throw new Exception("STORE: UNUSED");
        }
        else if(count == 1){
            return parseDestOnly(code, "0000") + opcode;
        }
        else if(count == 2){
            return parseTwoR(code, "0000") + opcode;
        }
        else if(count == 3){
            return parseThreeR(code, "0000") + opcode;
        }
        else{
            throw new Exception("STORE: REGISTER COUNT");
        }
    }

    //peek -> PEEK twoR | PEEK threeR
    String parsePeek() throws Exception{
        //formats opcode
        String code = getInstructionCode();
        String opcode = code + getInstructionFormat();

        //parses based on register count (2R or 3R)
        int count = countRegister();
        if(count == 2){
            return parseTwoR(code, "0000") + opcode;
        }
        else if(count == 3){
            return parseThreeR(code, "0000") + opcode;
        }
        else{
            throw new Exception("PEEK: REGISTER COUNT");
        }
    }

    //pop -> POP destOnly
    String parsePop() throws Exception{
        //formats opcode
        String code = getInstructionCode();
        String opcode = code + getInstructionFormat();

        //parses based on register count (Dest Only)
        int count = countRegister();
        if(count == 1){
            return parseDestOnly(code, "0000") + opcode;
        }
        else{
            throw new Exception("POP: REGISTER COUNT");
        }
    }

    //noR -> NUMBER
    String parseNoR() throws Exception{
        //parses immediate of size 27
        Token temp = manager.MatchAndRemove(Token.TokenTypes.NUMBER);
        if(temp != null){
            return toBinary(Integer.parseInt(temp.GetValue())).substring(5,32);
        }
        else{
            throw new Exception("NO R: Can't Parse");
        }
    }

    //destOnly -> REGISTER NUMBER
    String parseDestOnly(String code, String function) throws Exception{
        String rd, imm;
        //parses rd
        Token temp = manager.MatchAndRemove(Token.TokenTypes.REGISTER);
        if(temp != null){
            rd = toBinary(Integer.parseInt(temp.GetValue())).substring(27,32);
        }
        else{
            throw new Exception("NOT A REGISTER");
        }

        //parses immediate of size 18
        //if instruction is POP, doesnt use immediate
        if(code.equals("110")){
            imm = "000000000000000000";
        }
        else{
            temp = manager.MatchAndRemove(Token.TokenTypes.NUMBER);
            if(temp != null){
                imm = toBinary(Integer.parseInt(temp.GetValue())).substring(14,32);
            }
            else{
                throw new Exception("NOT A NUMBER");
            }
        }
        
        return imm + function + rd;
    }

    //twoR -> REGISTER REGISTER REGISTER
    String parseTwoR(String code, String function) throws Exception{
        String rd, rs, imm;

        //parses rd
        Token temp = manager.MatchAndRemove(Token.TokenTypes.REGISTER);
        if(temp != null){
            rd = toBinary(Integer.parseInt(temp.GetValue())).substring(27,32);
        }
        else{
            throw new Exception("2R: NOT A REGISTER");
        }

        //parses rs
        temp = manager.MatchAndRemove(Token.TokenTypes.REGISTER);
        if(temp != null){
            rs = toBinary(Integer.parseInt(temp.GetValue())).substring(27,32);
        }
        else{
            throw new Exception("2R: NOT A REGISTER");
        }

        //parses immediate of size 13
        //MATH & PUSH does not use immediate
        if(code.equals("000") || code.equals("011")){
            imm = "0000000000000";
        }
        else{
            temp = manager.MatchAndRemove(Token.TokenTypes.NUMBER);
            if(temp != null){
                imm = toBinary(Integer.parseInt(temp.GetValue())).substring(19,32);
            }
            else{
                throw new Exception("2R: NOT A NUMBER");
            }
        }

        return imm + rs + function + rd;
    }

    //threeR -> REGISTER REGISTER REGISTER
    String parseThreeR(String code, String function) throws Exception{
        String rd, rs1, rs2, imm;

        //parses rd
        Token temp = manager.MatchAndRemove(Token.TokenTypes.REGISTER);
        if(temp != null){
            rd = toBinary(Integer.parseInt(temp.GetValue())).substring(27,32);
        }
        else{
            throw new Exception("3R: NOT A REGISTER");
        }

        //parses rs1 
        temp = manager.MatchAndRemove(Token.TokenTypes.REGISTER);
        if(temp != null){
            rs1 = toBinary(Integer.parseInt(temp.GetValue())).substring(27,32);
        }
        else{
            throw new Exception("3R: NOT A REGISTER");
        }

        //parses rs2
        temp = manager.MatchAndRemove(Token.TokenTypes.REGISTER);
        if(temp != null){
            rs2 = toBinary(Integer.parseInt(temp.GetValue())).substring(27,32);
        }
        else{
            throw new Exception("3R: NOT A REGISTER");
        }

        //parses immediate of size 8
        //MATH, PUSH, LOAD, STORE, PEEK does not use immediate
        if(code.equals("000") || code.equals("011") || code.equals("100") || code.equals("101") || code.equals("110")){
            imm = "00000000";
        }
        else{
            temp = manager.MatchAndRemove(Token.TokenTypes.NUMBER);
            if(temp != null){
                imm = toBinary(Integer.parseInt(temp.GetValue())).substring(24,32);
            }
            else{
                throw new Exception("3R: NOT A NUMBER");
            }
        }
        return imm + rs1 + rs2 + function + rd;
    }

    //removes unneccessary consectutive newlines
    private boolean AcceptSeperators(){
        boolean isSeperator = false;
        while (manager.MoreTokens() && manager.MatchAndRemove(Token.TokenTypes.NEWLINE) != null){
            isSeperator = true;
        }
        return isSeperator;
    }

    //if next token is an instruction on the instruction definition matrix
    boolean isInstruction(){
        Token t = manager.Peek(0);
        if(t.GetToken() == Token.TokenTypes.MATH || t.GetToken() == Token.TokenTypes.COPY || t.GetToken() == Token.TokenTypes.HALT || t.GetToken() == Token.TokenTypes.BRANCH || t.GetToken() == Token.TokenTypes.JUMP || t.GetToken() == Token.TokenTypes.CALL || t.GetToken() == Token.TokenTypes.PUSH || t.GetToken() == Token.TokenTypes.LOAD || t.GetToken() == Token.TokenTypes.RETURN || t.GetToken() == Token.TokenTypes.STORE || t.GetToken() == Token.TokenTypes.PEEK || t.GetToken() == Token.TokenTypes.POP || t.GetToken() == Token.TokenTypes.INTERRUPT){
            return true;
        }
        return false;
    }

    //finds instruction token and generate the instruction code
    String getInstructionCode() throws Exception{
        String instruction;
        if(manager.MatchAndRemove(Token.TokenTypes.MATH) != null || manager.MatchAndRemove(Token.TokenTypes.COPY) != null){
            instruction = "000";  
        }
        else if(manager.MatchAndRemove(Token.TokenTypes.BRANCH) != null || manager.MatchAndRemove(Token.TokenTypes.JUMP) != null){
            instruction = "001";
        }
        else if(manager.MatchAndRemove(Token.TokenTypes.CALL) != null){
            instruction = "010";
        }
        else if(manager.MatchAndRemove(Token.TokenTypes.PUSH) != null){
            instruction = "011";
        }
        else if(manager.MatchAndRemove(Token.TokenTypes.LOAD) != null || manager.MatchAndRemove(Token.TokenTypes.RETURN) != null){
            instruction = "100";
        }
        else if(manager.MatchAndRemove(Token.TokenTypes.STORE) != null){
            instruction = "101";
        }
        else if(manager.MatchAndRemove(Token.TokenTypes.PEEK) != null || manager.MatchAndRemove(Token.TokenTypes.POP) != null || manager.MatchAndRemove(Token.TokenTypes.INTERRUPT) != null){
            instruction = "110";
        }
        else{
            throw new Exception("Wrong Instruction");
        }
        return instruction;
    }

    //return true if next token is a Math Operation
    boolean isMathOperation(){
        Token t = manager.Peek(0);
        if(t.GetToken() == Token.TokenTypes.ADD || t.GetToken() == Token.TokenTypes.OR || t.GetToken() == Token.TokenTypes.XOR || t.GetToken() == Token.TokenTypes.NOT || t.GetToken() == Token.TokenTypes.LEFT || t.GetToken() == Token.TokenTypes.RIGHT || t.GetToken() == Token.TokenTypes.ADD || t.GetToken() == Token.TokenTypes.SUBTRACT || t.GetToken() == Token.TokenTypes.MULTIPLY){
            return true;
        }
        return false;
    }

    //finds Math Operation token and generate function code
    String getMathOperation() throws Exception{
        String funct;
        if(manager.MatchAndRemove(Token.TokenTypes.AND) != null){
            funct = "1000";
        }
        else if(manager.MatchAndRemove(Token.TokenTypes.OR) != null){
            funct = "1001";
        }
        else if(manager.MatchAndRemove(Token.TokenTypes.XOR) != null){
            funct = "1010";
        }
        else if(manager.MatchAndRemove(Token.TokenTypes.NOT) != null){
            funct = "1011";
        }
        else if(manager.MatchAndRemove(Token.TokenTypes.LEFT) != null){
            if(manager.MatchAndRemove(Token.TokenTypes.SHIFT) != null){
                funct = "1100";
            }
            else{
                throw new Exception("NOT MATH OPERATION");
            }
        }
        else if(manager.MatchAndRemove(Token.TokenTypes.RIGHT) != null){
            if(manager.MatchAndRemove(Token.TokenTypes.SHIFT) != null){
                funct = "1101";
            }
            else{
                throw new Exception("NOT MATH OPERATION");
            }
        }
        else if(manager.MatchAndRemove(Token.TokenTypes.ADD) != null){
            funct = "1110";
        }
        else if(manager.MatchAndRemove(Token.TokenTypes.SUBTRACT) != null){
            funct = "1111";
        }
        else if(manager.MatchAndRemove(Token.TokenTypes.MULTIPLY) != null){
            funct = "0111";
        }
        else{
            throw new Exception("NOT MATH OPERATION");
        }
        return funct;
    }

    //return true if next token is a Boolean Operation
    boolean isBooleanOperation(){
        Token t = manager.Peek(0);
        if(t.GetToken() == Token.TokenTypes.EQ || t.GetToken() == Token.TokenTypes.NEQ || t.GetToken() == Token.TokenTypes.LT || t.GetToken() == Token.TokenTypes.GE || t.GetToken() == Token.TokenTypes.GT || t.GetToken() == Token.TokenTypes.LE){
            return true;
        }
        return false;
    }

    //finds Boolean Operation token and generate function code
    String getBooleanOperation() throws Exception{
        String funct;
        if(manager.MatchAndRemove(Token.TokenTypes.EQ) != null){
            funct = "0000";
        }
        else if(manager.MatchAndRemove(Token.TokenTypes.NEQ) != null){
            funct = "0001";
        }
        else if(manager.MatchAndRemove(Token.TokenTypes.LT) != null){
            funct = "0010";
        }
        else if(manager.MatchAndRemove(Token.TokenTypes.GE) != null){
            funct = "0011";
        }
        else if(manager.MatchAndRemove(Token.TokenTypes.GT) != null){
            funct = "0100";
        }
        else if(manager.MatchAndRemove(Token.TokenTypes.LE) != null){
            funct = "0101";
        }
        else{
            throw new Exception("NOT BOOLEAN OPERATION");
        }
        return funct;
    }

    //count the amount cosecutive of registers in the list
    int countRegister(){
        int count  = 0;
        while(manager.Peek(count) != null && manager.Peek(count).GetToken() == Token.TokenTypes.REGISTER){
            count++;
        }
        return count;
    }

    //count the amount of consecutive registers in the lexerlist to find instruction format code
    String getInstructionFormat() throws Exception{
        String instruction;
        int count = countRegister();
        //0 Register = NO R
        if(count == 0){
            instruction = "00";
        }
        //1 Register = Dest Only
        else if(count == 1){
            instruction = "01";
        }
        //2 Register = 2R
        else if(count == 2){
            instruction = "11";
        }
        //3 Register = 3R
        else if(count == 3){
            instruction = "10";
        }
        else{
            throw new Exception("Too Many Registers");
        }
        return instruction;
    }

    //convert int value to binary
    String toBinary(int value){
        String binary = "";
        int temp = value;

        //if positive, set last bit to 0. Else set true
        if(value >= 0){
            binary = "0";
        }
        else{
            binary = "1";
        }

        temp = Math.abs(temp);

        //coverts absolute value of int to binary word
        //and set the correct bits
        for(int i = 30; i >= 0; i--){
            if(temp/((int)Math.pow(2, i)) >0){
                temp -= Math.pow(2, i);
                binary += "1";
            }
            else{
                binary += "0";
            }
        }

        //if value is negative 
        if(value < 0){
            String negBinary = "1";
            //invert bits
            //change 1 to 0 and 0 to 1
            for(int i = 30; i >= 0; i--){
                if(binary.charAt((binary.length()-1) - i) == '1'){
                    negBinary += "0";
                }
                else{
                    negBinary += "1";
                }
            }
            
            String convert = "";
            String var = "";
            //adds 1
            //if true, then has to carry over the 1. else does not carry over
            if(negBinary.charAt(0) == '1'){
                //loops through word to see where the carry over stops
                //change 1 to 0 and 0 to 1
                for(int i = 0; i <= 30; i++){
                    if(negBinary.charAt((binary.length()-1) - i) == '1'){
                        convert += "0";
                    }
                    else{
                        convert += "1";
                        //need to reverse the string bc working on the binary the opposite direction
                        var = reverse(convert);
                        //adds changed string to remainder
                        convert = negBinary.substring(0,(negBinary.length()-1)-i);
                        convert += var;
                        break;
                    }
                }
            }
            else{
                //change last bit to "1"
                convert = negBinary.substring(0,negBinary.length()-2);
                convert += "1";
            }
            return convert;
        }
        return binary;
    }

    //reverses a string
    //used to convert int to binary
    String reverse(String input){
        //convert string to char[]
        char[] array = input.toCharArray();
        int start = 0;
        int end = array.length-1;
        char temp;
        while(end > start){
            //switch char
            temp = array[start];
            array[start] = array[end];
            array[end] = temp;
            end--;
            start++;
        }

        //return char[] as String
        return new String(array);
    }
}