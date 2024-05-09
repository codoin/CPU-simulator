import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    static Processor processor = new Processor();
    //processes assembly file and write to output file
    //Example command in terminal: 
    //javac Main.java 
    //java Main assembly.txt output.txt
    public static void main(String[] args) throws Exception {
        Path path = Paths.get(args[0]);
        String file = args[1];

        //reads file as string
        String content = new String(Files.readAllBytes(path));

        //makes list of token from the file
        Lexer lex = new Lexer(content);

        //recursively generate output
        Parser parser = new Parser(lex.Lex());

        //write binary to output file
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(parser.Parse());
        writer.close();
    }

    //loads each line of the program
    static void loadFile(String file) throws Exception{
        //read file to string
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String program = reader.readLine();
        program = program.replaceAll("\n","");
        int count = 0;
        String line = "";

        //load each line of the program
        for(int i = 0; i < program.length(); i++){
            System.out.print(program.charAt(i));
            line += program.charAt(i);
            count++;
            if(count % 32 == 0){
                System.out.println();
                MainMemory.load(addString(line));
                line = "";
            }
        }
        reader.close();
    }

    //helper method to convert string into array
    static String[] addString(String s){
        s = s.replaceAll(" ", "");
        String[] string = s.split("(?!^)");
        String[] temp = new String[32];
        int counter = 0;
        for (int i = 31; i >= 0; i--){
            temp[counter++] = string[i];
        }
        return temp;
    }
}