import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class VMTranslator {



    public static void translateFile(String pathOutFile, String pathInFile) throws IOException {
        Parser parser = new Parser(pathInFile);
        CodeWriter codeWriter = new CodeWriter(pathOutFile);
        codeWriter.setFileName(pathInFile);
        while (parser.hasMoreCommands()){
            parser.advance();
            performCommand(parser, codeWriter);
        }
        codeWriter.close();
    }




    public static void performCommand(Parser parser, CodeWriter codeWriter) throws IOException {
        int command = parser.commandType();
        if (command == Parser.C_ARITHMETIC){
            codeWriter.writeArithmetic(parser.arg1());
        }
        if (command == Parser.C_POP || command == Parser.C_PUSH){
            codeWriter.pushAndPop(parser.arg1(), parser.arg2(), command);
        }
        if (command == Parser.C_LABEL){
            codeWriter.writeLabel(parser.arg1());
        }

    }



    public static void main(String[] args) throws IOException {
        if (args.length != 1){
            throw new IllegalArgumentException("Please add a path to the directory/file!");
        }

        File directory = new File(args[0]);
        if (!directory.exists()){
            throw new RuntimeException("Cannot open the file!");
        }
        if (directory.getName().endsWith(".vm")){
            translateFile(generateOutfile(args[0]), args[0]);
            return;
        }
        File[] files = directory.listFiles();
        assert files != null;
        if (files.length == 0 ){
            throw new RuntimeException("No files to translate to ASM");
        }
        for (File f: files){
            if (f.getAbsolutePath().endsWith(".vm")){
                translateFile(generateOutfile(f.getAbsolutePath()), f.getAbsolutePath());
            }
        }
    }

    public static String generateOutfile(String inFilePath) {
        return  inFilePath.replace(".vm", ".asm");
    }

}
