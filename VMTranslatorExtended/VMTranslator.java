import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class VMTranslator {

    public static void translateToASMFolder(String path) throws IOException {
        File file = new File(path);
        CodeWriter codeWriter = new CodeWriter(generateOutfile(path));
        if (file.exists()){
            codeWriter.writeInit();
            if (file.getName().endsWith(".vm")){
                translateToASMFile(codeWriter, path);
                codeWriter.close();
                return;
            }
            File[] files = file.listFiles();
            assert files != null;
            if (files.length == 0 ){
                throw new RuntimeException("No files to translate to VM");
            }
            for (File f: files){
                if (f.getAbsolutePath().endsWith(".vm")){
                    translateToASMFile(codeWriter, f.getAbsolutePath());
                }
            }
            codeWriter.close();
            return;
        }
        throw new RuntimeException("File doesn't exist");
    }



    public static void translateToASMFile(CodeWriter codeWriter, String pathToFile) throws IOException {
        Parser parser = new Parser(pathToFile);
        codeWriter.setFileName(pathToFile);
        while (parser.hasMoreCommands()){
            parser.advance();
            performCommand(parser, codeWriter);
        }
    }


    public static void performCommand(Parser parser, CodeWriter codeWriter) throws IOException {
        int command = parser.commandType();
        if (command == Parser.C_ARITHMETIC){
            codeWriter.writeArithmetic(parser.arg1());
        }
        if (command == Parser.C_POP || command == Parser.C_PUSH){
            codeWriter.pushAndPop(parser.arg1(), parser.arg2(), command);
        }
        if (command == Parser.C_CALL){
            codeWriter.writeCall(parser.arg1(), Integer.parseInt(parser.arg2()));
        }
        if (command == Parser.C_LABEL){
            codeWriter.writeLabel(parser.arg1());
        }
        if (command == Parser.C_IF){
            codeWriter.writeIf(parser.arg1());
        }
        if (command == Parser.C_GOTO){
            codeWriter.writeGoTo(parser.arg1());
        }
        if (command == Parser.C_RETURN){
            codeWriter.writeReturn();
        }
        if (command == Parser.C_FUNCTION){
            codeWriter.writeFunction(parser.arg1(), Integer.parseInt(parser.arg2()));
        }
    }



    public static void main(String[] args) throws IOException {
        if (args.length != 1){
            throw new IllegalArgumentException("Please add a path to the directory/file!");
        }

        translateToASMFolder(args[0]);


    }

    public static String generateOutfile(String inFilePath) {
        File  file = new File(inFilePath);
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1){
            return inFilePath + "/" + fileName+".asm";
        }
        return  file.getParentFile()+"/"+fileName.substring(0, dotIndex)+".asm";
    }

}
