import java.util.LinkedList;

public class TokenManager {
    private LinkedList<Token> tokenlist; 
    
    TokenManager(LinkedList<Token> list){
        tokenlist = list;
    }
    Token Peek(int j){ //peeks j tokens from current head
        if (j < tokenlist.size()){
            return tokenlist.get(j);
        }
        return null;
    }
    boolean MoreTokens(){ //checks if the list is empty
        if (tokenlist.isEmpty()){
            return false;
        }
        else{
            return true;
        }
    }
    Token MatchAndRemove(Token.TokenTypes t){ //sees if current token in list matches parameter if so removes from list
        Token removedtoken;
        if (MoreTokens() && tokenlist.getFirst().GetToken().equals(t)){
            removedtoken = tokenlist.getFirst();
            tokenlist.removeFirst();
            return removedtoken;
        }
        else{
            return null;
        }
    }
}