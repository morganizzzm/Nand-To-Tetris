import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class JackAssembler {

    public static void assembleFolder(String path) throws IOException {
        File file = new File(path);
        if (file.exists()){
            if (file.getName().endsWith(".asm")){
                assembleSingleFile(path);
                return;
            }
            File[] files = file.listFiles();
            assert files != null;
            if (files.length == 0 ){
                throw new RuntimeException("No files to assemble");
            }
            for (File f: files){
                if (f.getAbsolutePath().endsWith(".asm")){
                    assembleSingleFile(f.getAbsolutePath());
                }
            }
        }
    }



    public static void  firstPass(Parser parser, SymbolTable symbolTable){
        int i = 0;
        while (parser.hasMoreCommands()){
            parser.advance();
            if (parser.commandType() == parser.L_COMMAND){
                String label = parser.currentCommand.substring(1, parser.currentCommand.length()-1);
                if (!symbolTable.contains(label)){
                    symbolTable.addEntry(label, i);
                }
                continue;
            }
            i++;
        }
    }

    public static void secondPass(Parser parser, SymbolTable symbolTable){
        int n = 16;
        while (parser.hasMoreCommands()){
            parser.advance();
            if (parser.commandType() == parser.A_COMMAND){
                String subString = parser.currentCommand.substring(1);
                boolean isNumeric = subString.chars().allMatch( Character::isDigit );
                if (!isNumeric && !symbolTable.contains(subString)){
                    symbolTable.addEntry(subString, n++);
                }
            }

        }

    }

    public static void translateProgram(Parser parser, SymbolTable symbolTable,  Code code, BufferedWriter outputWriter)
            throws IOException {
        while (parser.hasMoreCommands()){
            parser.advance();
            int[] cur = translateCommandToBinary(parser, symbolTable, code);
            if (cur.length == 0){
                continue;
            }
            for (int i=0; i<16; i++){
                outputWriter.write(cur[i]+"");
            }
            outputWriter.newLine();
        }
        outputWriter.flush();
        outputWriter.close();
    }

    public static int[] translateCommandToBinary(Parser parser, SymbolTable symbolTable,  Code code) {
        if (parser.commandType() == parser.A_COMMAND) {
            int num = symbolTable.getLabelCode(parser.currentCommand.substring(1));
            if (num == -1) {
                num = Integer.parseInt(parser.currentCommand.substring(1));
            }
            return toBinary(num, 0);
        }
        if (parser.commandType() == parser.L_COMMAND) {
            return new int[]{};
        }
        int[] firstBits = new int[]{1,1,1};
        int[] dest = code.dest(parser.dest());
        int[] comp = code.comp(parser.comp());
        int[] jump = code.jump(parser.jump());

        if (code.ifShift(parser.comp())){
            firstBits[1] = 0;
        }
        return new int[]{firstBits[0], firstBits[1], firstBits[2], comp[0], comp[1], comp[2], comp[3], comp[4], comp[5], comp[6],
                                   dest[0], dest[1], dest[2], jump[0], jump[1], jump[2]};
    }


    public static int[] toBinary(int decimal, int firstbit){
        int[] decimalToBinary = new int[15];
        int[] ansbinary = new int[]{firstbit, 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        int index = 0;
        while(decimal > 0){
            decimalToBinary[index++] = decimal%2;
            decimal = decimal/2;
        }
        int j=index-1;
        for (int i=16-index; i<16; i++){
            ansbinary[i] = decimalToBinary[j--];
        }
        return ansbinary;
    }


    public static void main(String[] args) throws IOException {
        if (args.length !=1) {
            throw new IllegalArgumentException();
        }

        assembleFolder(args[0]);

    }

    private static void assembleSingleFile(String inFile) throws IOException {
        SymbolTable symbolTable = new SymbolTable();
        Code code = new Code();
        firstPass(new Parser(inFile), symbolTable);
        secondPass(new Parser(inFile), symbolTable);
        String outPass = inFile.replace(".asm", ".hack");
        BufferedWriter outputWriter = new BufferedWriter(new FileWriter(outPass));
        translateProgram(new Parser(inFile), symbolTable, code, outputWriter);
    }
}
