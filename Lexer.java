import java.util.LinkedList;
import java.util.HashMap;

public class Lexer {
    StringHandler handler;
    int line = 0, charPosition = 1;
    HashMap<String, Token.TokenTypes> keywords;

    //creates string handler and hashmaps of keywords
    public Lexer (String wholeString){ 
        handler = new StringHandler(wholeString);
        keywords = new HashMap<String, Token.TokenTypes>();
        putWords();
    }

    void putWords(){ //list of keywords in hashmap
        keywords.put("math", Token.TokenTypes.MATH);
        keywords.put("copy", Token.TokenTypes.COPY);
        keywords.put("halt", Token.TokenTypes.HALT);
        keywords.put("branch", Token.TokenTypes.BRANCH);
        keywords.put("jump", Token.TokenTypes.JUMP);
        keywords.put("call", Token.TokenTypes.CALL);
        keywords.put("push", Token.TokenTypes.PUSH);
        keywords.put("load", Token.TokenTypes.LOAD);
        keywords.put("return", Token.TokenTypes.RETURN);
        keywords.put("store", Token.TokenTypes.STORE);
        keywords.put("peek", Token.TokenTypes.PEEK);
        keywords.put("pop", Token.TokenTypes.POP);
        keywords.put("interrupt", Token.TokenTypes.INTERRUPT);
        keywords.put("shift", Token.TokenTypes.SHIFT);
        keywords.put("left", Token.TokenTypes.LEFT);
        keywords.put("right", Token.TokenTypes.RIGHT);
        keywords.put("ge", Token.TokenTypes.GE);
        keywords.put("le", Token.TokenTypes.LE);
        keywords.put("eq", Token.TokenTypes.EQ);
        keywords.put("neq", Token.TokenTypes.NEQ);
        keywords.put("and", Token.TokenTypes.AND);
        keywords.put("lt", Token.TokenTypes.LT);
        keywords.put("gt", Token.TokenTypes.GT);
        keywords.put("not", Token.TokenTypes.NOT);
        keywords.put("add", Token.TokenTypes.ADD);
        keywords.put("subtract", Token.TokenTypes.SUBTRACT);
        keywords.put("multiply", Token.TokenTypes.MULTIPLY);
        keywords.put("or", Token.TokenTypes.OR);
        keywords.put("xor", Token.TokenTypes.XOR);
        keywords.put("\n", Token.TokenTypes.NEWLINE);
    }

    //goes through file and makes a list of tokens
    LinkedList<Token> Lex() throws Exception{ 
        LinkedList<Token> data = new LinkedList<Token>();

        //runs until file is empty
        while (!(handler.IsDone())){ 
            
            if(handler.Peek(0)==' '){//skips spaces
                handler.Swallow(1);
                charPosition++;
            }
            else if(handler.Peek(0)=='\t'){//skips tabs
                handler.Swallow(1);
                charPosition+=1;
            }
            else if(Character.isDigit(handler.Peek(0)) || handler.Peek(0)=='-'){//finds a digit
                data.add(ProcessNumber());
            }
            else if(handler.Peek(0)=='R'){//finds a register
                data.add(ProcessRegister());
            }
            else if(handler.Peek(0)=='\r'){//finds carriage return
                charPosition++;
                handler.Swallow(1);
            }
            else if(handler.Peek(0)=='\n'){//finds linefeed, increments line number, swallow char, set char position to 0
                data.add(new Token(Token.TokenTypes.NEWLINE,line,charPosition));
                line++;
                handler.Swallow(1);
                charPosition=0;
            }
            else{ //last case finds keyword or throw exception
                data.add(ProcessKeyword());
            }          
        }
        //linkedlist of tokens
        return data;
    }
    //goes through file until the end of register and creates a token
    private Token ProcessRegister() { 
        int position = charPosition;
        String word = "";
        //while lexer is not done, check for letters or numbers
        while(!(handler.IsDone()) && (Character.isLetter(handler.Peek(0)) || Character.isDigit(handler.Peek(0)))){ 
            word+=handler.GetChar();
            charPosition++;
        }
        if (keywords.containsKey(word)){ //if the word is a keyword return as symbol
            return new Token(keywords.get(word),line, position, word);
        }
        else{
            return new Token(Token.TokenTypes.REGISTER, line, position, word.substring(1, word.length())); //returns word token
        }
    }
    //goes through file until end of number and creates a token
    private Token ProcessNumber() throws Exception{
        int position = charPosition;
        String number = "";
        //while list lexer is not done, check if digit
        while(!(handler.IsDone()) && (Character.isDigit(handler.Peek(0)) || handler.Peek(0)=='-')){
            number+=handler.GetChar();
            charPosition++;
        }
        charPosition+=position;
        return new Token(Token.TokenTypes.NUMBER, line, position, number);
    }
    //goes through file to find keyword and creates a token or throw exception
    private Token ProcessKeyword() throws Exception{
        int position = charPosition;
        String word = "";
        //while lexer is not done, check for letters or numbers
        while(!(handler.IsDone()) && (Character.isLetter(handler.Peek(0)))){ 
            word+=handler.GetChar();
            charPosition++;
        }
        //if the word is a keyword return as a token
        if (keywords.containsKey(word)){ 
            return new Token(keywords.get(word),line, position, word);
        }
        else{
            throw new Exception("Error: " + word + " is not a Keyword");
        }
    }
}