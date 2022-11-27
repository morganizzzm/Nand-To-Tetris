import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Parser {
    ////segment names

    String[] command;
    private final Scanner scanner;

    public final static int C_ARITHMETIC = 0;
    public  static final int C_PUSH = 1;
    public static final int C_POP = 2;
    public static final int C_LABEL = 3;
    public  static final int C_GOTO = 4;
    public  static final int C_IF = 5;
    public static final int C_FUNCTION = 6;
    public static final int C_RETURN = 7;
    public static final int C_CALL = 8;

    HashMap<String, Integer> commandsBase;


    public Parser(String pathToFile) throws IllegalArgumentException, FileNotFoundException {
        checkFileFormat(pathToFile);
        this.scanner = new Scanner(new File(pathToFile));
        fill_commands();
    }

    void checkFileFormat(String path) throws IllegalArgumentException{
        String fileName = new File(path).getName();
        int dotIndex = fileName.lastIndexOf('.');
        String extension= (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
        if (!extension.equals("vm")){
            throw new IllegalArgumentException();
        }
    }



    void advance(){
        while (true) {
            String line = scanner.nextLine();
            String[] command = line.split("//+");
            if (command.length == 0){
                continue;
            }
            if (command[0].length()<=1 ){
                continue;
            }
            this.command = command[0].split("\\s+");
            return;
        }


    }

    boolean hasMoreCommands(){
        return this.scanner.hasNext();
    }

    int commandType(){
        return commandsBase.get(this.command[0]);
    }


    String arg1(){
        if (commandType() == C_RETURN || commandType() == C_ARITHMETIC){
            return this.command[0];
        }
        return this.command[1];
    }

    String arg2(){

        return this.command[2];
    }
    void fill_commands(){
        commandsBase = new HashMap<>();
        commandsBase.put("label", C_LABEL);
        commandsBase.put("pop", C_POP);
        commandsBase.put("push", C_PUSH);
        commandsBase.put("goto", C_GOTO);
        commandsBase.put("if-goto", C_IF);
        commandsBase.put("call", C_CALL);
        commandsBase.put("return", C_RETURN);
        commandsBase.put("function", C_FUNCTION);
        commandsBase.put("sub", C_ARITHMETIC);
        commandsBase.put("add", C_ARITHMETIC);
        commandsBase.put("neg", C_ARITHMETIC);
        commandsBase.put("eq", C_ARITHMETIC);
        commandsBase.put("gt", C_ARITHMETIC);
        commandsBase.put("lt", C_ARITHMETIC);
        commandsBase.put("and", C_ARITHMETIC);
        commandsBase.put("or", C_ARITHMETIC);
        commandsBase.put("not", C_ARITHMETIC);
        commandsBase.put("shiftleft", C_ARITHMETIC);
        commandsBase.put("shiftright", C_ARITHMETIC);

    }



}
