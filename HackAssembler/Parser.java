import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.Scanner;

public class Parser {
    public final int A_COMMAND=0;
    public final int C_COMMAND=1;
    public final int L_COMMAND=2;

    String[] destOptions = {"M", "D", "MD", "A", "AM", "AD","AMD"};
    String[] compOptions = {"0",   "1",   "-1", "D",  "A", "M",  "!D", "!A",  "!M",  "-D",
            "-A",  "-M", "D+1","A+1", "M+1", "D-1", "A-1", "M-1", "D+A", "D+M",
            "D-A", "D-M", "A-D", "M-D", "D&A", "D&M", "D|A", "D|M",
            "A>>", "A<<", "D<<", "D>>", "M>>", "M<<"};

    String[] jumpOptions = {"", "JGT","JEQ","JGE", "JLT", "JNE", "JLE", "JMP"};


    public final String ERROR = "";
    String currentCommand = "";
    Scanner scanner;
    public Parser(String pathToFile) throws FileNotFoundException, IllegalArgumentException{
        checkFileFormat(pathToFile);
        this.scanner = new Scanner(new File(pathToFile));
        this.currentCommand = "";
    }

    void checkFileFormat(String path) throws IllegalArgumentException{
        String fileName = new File(path).getName();
        int dotIndex = fileName.lastIndexOf('.');
        String extension= (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
        if (!extension.equals("asm")){
            throw new IllegalArgumentException();
        }
    }


    int commandType(){
        if (this.currentCommand.charAt(0) == '('){
            return L_COMMAND;
        }
        if (this.currentCommand.charAt(0) == '@'){
            return A_COMMAND;
        }
        return C_COMMAND;
    }

    String symbol() {
        if (commandType() == L_COMMAND || commandType() == A_COMMAND){
            return Character.toString(this.currentCommand.charAt(0));
        }
        return ERROR;
    }

    String dest() {
        if (commandType() == C_COMMAND) {
            return sortOfInstruction("=", destOptions, 0);
        }
        return ERROR;
    }


    String comp() {
        if (commandType() == C_COMMAND) {
            String ans = sortOfInstruction(";", compOptions, 0);
            if (Objects.equals(ans, "")){
               return  sortOfInstruction("=", compOptions, 1);
            }
            return ans;
        }
        return ERROR;
    }

    String jump() {
        if (commandType() == C_COMMAND) {
            return sortOfInstruction(";", jumpOptions, 1);
        }
        return ERROR;
    }



    String sortOfInstruction(String delimiter, String[] optionsList, int idx ){
        String[] instruction = this.currentCommand.split(delimiter);
        if (instruction.length == 2) {
            if (belongsOptions(instruction[idx], optionsList)){
                return instruction[idx];
            }
        }
        return ERROR;
    }


    boolean belongsOptions(String comp, String[] optionsList){
        for (String s: optionsList){
            if (Objects.equals(s, comp)){
                return true;
            }
        }
        return false;
    }




    boolean hasMoreCommands() {
        return this.scanner.hasNext();
    }

    void advance(){
        while (true){
            String comment = scanner.nextLine();
            comment = comment.replaceAll("\\s+", "");
            String[] noComments = comment.split("//+");
            if (noComments[0].length()<=1 ){
                continue;
            }
            this.currentCommand = noComments[0];
            break;
        }

    }

}


