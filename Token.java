public class Token {
    public enum TokenTypes {REGISTER, NUMBER, NEWLINE, MATH, ADD, SUBTRACT, MULTIPLY, AND, OR, NOT, XOR, COPY, HALT, BRANCH, JUMP, CALL, PUSH, LOAD, RETURN, STORE, PEEK, POP, INTERRUPT, EQ, NEQ, GT, LT, GE, LE, SHIFT, LEFT, RIGHT};
    TokenTypes token;
    String TokenValue;
    int linenum, charposition;
    
    Token (TokenTypes type, int line, int position){ //constructor
        token = type;
        linenum = line;
        charposition = position;
    }
    Token (TokenTypes type, int line, int position, String TokenVal){ //should call other constructor and then add value
        token = type;
        linenum = line;
        charposition = position;
        TokenValue = TokenVal;
    }
    String ToString(){ //format of tokens
        return token + " (" + TokenValue + ") ";
    }
    TokenTypes GetToken(){ //return token type
        return token;
    }
    String GetValue(){
        return TokenValue;
    }
}