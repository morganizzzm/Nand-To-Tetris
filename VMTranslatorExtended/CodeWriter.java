import java.io.*;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;

public class CodeWriter {
    public final String D = "D";
    public final String M = "M";
    public final String A = "A";
    ////////////////////////////////////
    public final String PUSH = "push";
    public final String POP = "pop";
    ////////////////////////////////////
    public final String ADD = "add";
    public final String C_ADD = "D+A";

    public final String SUB = "sub";
    public final String C_SUB = "A-D";

    public final String NEG = "neg";
    public final String C_NEG = "-D";

    public final String EQ = "eq";
    public final String C_EQ = "JEQ";

    public final String GT = "gt";
    public final String C_GT = "JGT";

    public final String LT = "lt";
    public final String C_LT = "JLT";

    public final String AND = "and";
    public final String C_AND = "D&A";

    public final String OR = "or";
    public final String C_OR = "D|A";

    public final String NOT = "not";
    public final String C_NOT = "!D";
    public final String SHIFTL = "shiftleft";
    public final String C_SHIFTL = "D<<";

    public final String SHIFTR= "shiftright";
    public final String C_SHIFTR = "D>>";
    //////////////////////////////////////

    public final String LCL = "local";
    public final String ARG = "argument";
    public final String THIS = "this";
    public final String THAT = "that";
    public final String PTR = "pointer";
    public final String TEMP = "temp";
    public final String CONST = "constant";
    public final String STATIC = "static";
    public final String REG = "reg";
    /////////////////////////////////////////
    public final int R_R0 = 0;
    public final String S_SP = "R0";

    public final int R_LCL = 1;
    public final String S_LCL = "LCL";
    public final int R_ARG = 2;
    public final String S_ARG ="ARG";
    public final int R_THIS = 3;
    public final String S_THIS = "THIS";
    public final int R_PTR = 3;
    public final int R_THAT = 4;
    public final String S_THAT = "THAT";

    public final int R_TEMP = 5;

    public final String S_FRAME = "R13";
    public final String S_RET = "R14";
    public final String S_COPY = "R15";
    ////////////////////////////////////////////////////////////
    HashMap<String,Integer> addressSegs= new HashMap<>();
    HashMap<String,String> addressSegsNames= new HashMap<>();
    HashMap<String,Integer> regSegs= new HashMap<>();
    ///////////////////////////////////////////////////////////
    BufferedWriter outFile;
    String fileName;
    Integer curLabelName;
    public CodeWriter(String pathToFile) throws IOException, IllegalArgumentException{
        //checkFileFormat(pathToFile);
        this.outFile = new BufferedWriter(new FileWriter(pathToFile));
        this.fileName = "";
        this.curLabelName  = 0;
        fillAddressSegs();
        fillRegSegs();
    }

    void setFileName (String name){
        String[] splitBySlash = name.split ("/");
        String[] nameSplit = splitBySlash[splitBySlash.length-1].split("\\.");
        this.fileName = nameSplit[0];
    }

    String getFileName (){
        return this.fileName;
    }

    void fillAddressSegs(){
        addressSegs.put(LCL,R_LCL);
        addressSegs.put(ARG,R_ARG);
        addressSegs.put(THIS,R_THIS);
        addressSegs.put(THAT,R_THAT);
        addressSegsNames.put(LCL, "LCL");
        addressSegsNames.put(ARG, "ARG");
        addressSegsNames.put(THIS, "THIS");
        addressSegsNames.put(THAT, "THAT");

    }
    void writeInit() throws IOException {
        commandA("256");
        commandC(D, A, null);
        commandA("R0");
        commandC("M", "D", null);
        writeCall("Sys.init", 0);
    }

    void writeLabel(String label) throws IOException {
        commandL(label);
    }

    void writeGoTo(String label) throws IOException {
       // this.outFile.write("////////////////////////////goto /////////////////");

        commandA(label);
        commandC(null, "0", "JMP");
    }

    void writeIf(String label) throws IOException{
        //this.outFile.write("////////////////////////////if /////////////////");

        decrementSP();
        changeDtoSP();
        commandA(label);
        commandC(null, D, "JNE");
    }

    void argReposition(int nVars) throws IOException {
        segPlusIdx( Integer.toString(-nVars-5), S_SP);
        commandA(S_ARG);
        commandC(M, D, null);
    }

    void reposition(String first, String second) throws IOException {
        commandA(second);
        commandC(D, M, null);
        commandA(first);
        commandC(M, D, null);
    }

    void writeCall(String functionName, int nVars) throws IOException {

        String returnAddress = generateLabelName();
        push(CONST, returnAddress);
        push(REG, Integer.toString(R_LCL));
        push(REG, Integer.toString(R_ARG));
        push(REG, Integer.toString(R_THIS));
        push(REG, Integer.toString(R_THAT));
        argReposition(nVars);
        reposition(S_LCL, S_SP);

        writeGoTo(functionName);
        commandL(returnAddress);
    }

    void writeReturn() throws IOException{
        //frame = LCL

        reposition(S_FRAME, "R1");
        //retAddr = *(frame - 5)
        commandA("5");

        commandC(A, "D-A", null );
        commandC(D, M, null);
        commandA(S_RET);
        commandC(M, D, null);
        //*ARG = pop()


        pop(ARG, "0");

        //SP = ARG+1
        commandA(S_ARG);
        commandC(D, M, null);
        commandA("R0");
        commandC(M, "D+1", null);
        //THAT = *(frame-1)
        restoreSeg(S_THAT );
        //THIS = *(frame-2)
        restoreSeg(S_THIS);
        //ARG = *(frame-3)
        restoreSeg(S_ARG);
        //LCL = *(frame-4)
        restoreSeg(S_LCL);
        //goto retAddr
        commandA(S_RET);
        commandC(A, M, null);
        commandC(null, "0", "JMP");

    }

    void restoreSeg(String segment) throws IOException {
        commandA(S_FRAME);
        commandC(D, M, null);
        commandC(D, "D-1", null);
        commandA(S_FRAME);
        commandC(M, D, null);
        commandC(A, D, null);
        commandC(D, M, null);
        commandA(segment);
        commandC(M, D, null);
    }

    void writeFunction(String functionName, int nVars) throws IOException {

        commandL(functionName);
        for (int i=0; i<nVars; i++){
            push(CONST, "0");
        }
    }

    void fillRegSegs(){
        regSegs.put(REG, R_R0);
        regSegs.put(PTR, R_PTR);
        regSegs.put(TEMP, R_TEMP);

    }

    void unaryCMP(String comp) throws IOException {
        decrementSP();                         //@SP M=M-1
        changeDtoSP();                         //@SP A=M D=M
        commandC(D, comp, null);  //D=comp
        changeSPtoD();                         //@SP A=M M=D
        incerementSP();                        //@SP M=M+1


    }

    void compareCMP(String jump) throws IOException {
        this.curLabelName++;
        String secondNeg = generateLabelName();
        String secondNegFirstPos = generateLabelName();
        String secondPosFirstNeg = generateLabelName();
        String fine  = generateLabelName();
        String labelTrue = generateLabelName();
        String labelFalse = generateLabelName();

        decrementSP();                                //@SP M=M-1
        changeDtoSP();                                //@SP A=M D=M
        commandA(S_COPY);                             //@R13
        commandC(M, D, null);                   //M=D
        commandA(secondNeg);                         //@secNegi
        commandC(null , D, C_LT);                 //D;JLT
        //sec is positive
        decrementSP();                                //@SP M=M-1
        changeDtoSP();                                //@SP A=M D=M
        commandA(secondPosFirstNeg);                  //@secPosFstNeg
        commandC(null, D, C_LT);                   //D;JLT
        commandA(S_COPY);                             //@R13
        commandC(D,"D-M", null);         //D=D-M
        /////if both sec and fst positive
        commandA(fine);                               //@FINE
        commandC(null, "0", "JMP");  //0;JMP
        //sec is negative
        commandL(secondNeg);                          //(secNeg)
        decrementSP();                                //@SP M=M-1
        changeDtoSP();                                //@SP A=M D=M
        ////if Sec is negative but Fst is positive
        commandA(secondNegFirstPos);                 //@secNegFstPos
        commandC(null,D, C_GT);                   //D;JGT
        //sec is negative and fst is negative
        commandA(S_COPY);                             //@R13
        commandC(D,"D-M", null);          //D=D-M
        commandA(fine);                               //@FINE
        commandC(null, "0", "JMP");  //0;JMP
        //sec is positive and fst is negative
        commandL(secondPosFirstNeg);                  //(secPosFstNeg)
        commandC(D, "-1", null);          //D=-1
        commandA(fine);                               //@FINE
        commandC(null, "0", "JMP");  //0;JMP
        //sec is negative and fst is positive
        commandL(secondNegFirstPos);                  //@secNegFstPos
        commandC(D, "1", null);           //D=1

        ////FINE
        commandL(fine);                               //fine
        commandA(labelTrue);                          //@LABELi
        commandC(null, D, jump);                //D;JUMP
        ///condition doesn't hold
        commandC(D, "0", null);           //D=0
        commandA(labelFalse);                         //LABELi+1
        commandC(null, "0", "JMP");  //0;JMP
        ///condition holds
        commandL(labelTrue);                           //(Labeli)
        commandC(D,"-1", null);            //D=-1
        commandL(labelFalse);                          //(Labeli+1)
        changeSPtoD();                                 //@SP A=M M=D
        incerementSP();                                //@SP M=M+1


    }


    void binaryCMP(String comp) throws IOException{
        decrementSP();                        //@SP M=M-1
        changeDtoSP();                        //@SP A=M D=M
        decrementSP();                        //@SP M=M-1
        changeAtoSP();                        //@SP A=M A=M

        commandC(D, comp, null);  //D=comp
        changeSPtoD();                         //@SP A=M M=D
        incerementSP();                        //@SP M=M+1

    }

    void writeArithmetic(String comp) throws IOException {

        //binary commands
        if (Objects.equals(comp,ADD)){
            binaryCMP(C_ADD);
        }
        if (Objects.equals(comp,SUB)){
            binaryCMP(C_SUB);
        }
        if (Objects.equals(comp,AND)){
            binaryCMP(C_AND);
        }
        if (Objects.equals(comp,OR)){
            binaryCMP(C_OR);
        }
        //unary commands
        if (Objects.equals(comp,NEG)){
            unaryCMP(C_NEG);
        }
        if (Objects.equals(comp,NOT)){
            unaryCMP(C_NOT);
        }
        if (Objects.equals(comp,SHIFTL)){
            unaryCMP(C_SHIFTL);
        }
        if (Objects.equals(comp,SHIFTR)){
            unaryCMP(C_SHIFTR);
        }
        //compare commands
        if (Objects.equals(comp,EQ)){
            compareCMP(C_EQ);
        }
        if (Objects.equals(comp,GT)){
            compareCMP(C_GT);
        }
        if (Objects.equals(comp,LT)){
            compareCMP(C_LT);
        }
    }


    void pushAndPop(String seg, String idx, int command) throws IOException {

        if (command == Parser.C_PUSH){
            push(seg, idx);
        }
        if (command == Parser.C_POP){
            pop(seg, idx);
        }
    }


    void push(String seg, String idx) throws IOException {
        if (Objects.equals(STATIC, seg)){
            staticToStack(idx);
        }
        if (Objects.equals(CONST, seg)){
            constantToStack(idx);
        }
        if (Objects.equals(THIS, seg) ||
                Objects.equals(THAT, seg) ||
                Objects.equals(LCL, seg) ||
                Objects.equals(ARG, seg)){
            virtualToStack(idx, generateVirtualName(seg));
        }
        if (regSegs.containsKey(seg)){

            regToStack(idx, seg);
        }
        incerementSP();
    }

    void staticToStack(String idx) throws IOException {
        commandA(generateStaticName(idx));         //@Foo.idx
        commandC(D, M, null); //D=M
        changeSPtoD();                          //@SP A=M  M=D
    }


    void constantToStack(String idx) throws IOException {
        commandA(idx);
        commandC(D, A, null);
        changeSPtoD();
    }


    void virtualToStack(String idx, String segment) throws IOException {
        segPlusIdx(idx, segment);
        commandC(D, M, null);     //D=M
        changeSPtoD();                                 //@SP A=M M=D
    }


    void regToStack(String idx, String segment) throws IOException {
        commandA(generateRegName(segment, Integer.parseInt(idx)));        //@segment
        commandC(D, M, null);    //D=M
        changeSPtoD();
    }


    void pop(String seg, String idx) throws IOException {

        decrementSP();
        if (Objects.equals(seg, STATIC)){
            staticOUTStack(idx);
        }
        if (Objects.equals(THIS, seg) ||
                Objects.equals(THAT, seg) ||
                Objects.equals(LCL, seg) ||
                Objects.equals(ARG, seg)){

            virtualOUTStack(idx, generateVirtualName(seg));
        }
        if (regSegs.containsKey(seg)){
            regOUTStack(idx, seg);
        }

    }

    void staticOUTStack(String idx) throws IOException {
        dereferenceSP();
        commandC(D, M, null);
        commandA(generateStaticName(idx));
        commandC(M, D, null);
    }



    void virtualOUTStack(String idx, String segment) throws IOException {
        segPlusIdx(idx, segment);
        commandA(S_COPY);                              //@R15
        commandC(M, D, null);     //M=D
        dereferenceSP();                               //@SP A=M
        commandC(D, M, null);    //D=M
        commandA(S_COPY);                              //@R15
        commandC(A, M, null);    //A=M
        commandC(M, D, null);    //M=D
    }

    private void segPlusIdx(String idx, String segment) throws IOException {
        String comp = "D+A";
        if (Integer.parseInt(idx)<0){
            idx = Integer.toString(-1*Integer.parseInt(idx));
            comp = "A-D";
        }
        commandA(idx);                                //@index
        commandC(D, A, null);    //D=A
        commandA(segment);                            //@segment
        commandC(A, M, null);   //A=M
        commandC("AD", comp, null);  //AD=A+D
    }


    void regOUTStack(String idx, String segment) throws IOException {
        changeDtoSP();
        commandA(generateRegName(segment, Integer.parseInt(idx)));
        commandC(M, D, null);
    }

    void close() throws IOException {
        this.outFile.flush();
        this.outFile.close();
    }

    void dereferenceSP() throws IOException {
        commandA("SP");
        commandC(A,M, null);
    }

    void changeSPtoD() throws IOException{
        dereferenceSP();
        commandC(M, D, null);
    }


    void changeAtoSP() throws IOException{
        dereferenceSP();
        commandC(A, M, null);
    }




    void changeDtoSP() throws IOException{
        dereferenceSP();
        commandC(D, M, null);
    }

    void incerementSP() throws  IOException{
        commandA("SP");
        commandC(M, "M+1", null);
    }

    void decrementSP() throws  IOException{
        commandA("SP");
        commandC(M, "M-1", null);
    }

    void commandL(String labelName) throws IOException {
        this.outFile.write("(" + labelName + ")"+"\n");
    }


    void commandA(String addressName) throws IOException{
        this.outFile.write("@"+addressName+"\n");
    }

    void commandC(String dest, String comp, String jump) throws IOException {
        if (jump == null){
            this.outFile.write(dest+"="+comp+"\n");
        }
        if (dest == null){
            this.outFile.write(comp+";" + jump+"\n");
        }
    }

    String generateStaticName(String idx){
        return this.fileName + "."+ idx;
    }

    String generateRegName(String segment, int idx){
        if (segment == null){
            return "R" + idx;
        }
        return "R" + (regSegs.get(segment) + idx);
    }


    String generateLabelName(){
        String label = "LABEL" + this.curLabelName;
        this.curLabelName++;
        return label;
    }

    String generateVirtualName(String seg){
        if (Objects.equals(seg, LCL)){
            return "LCL";
        }
        if (Objects.equals(seg, ARG)){
            return "ARG";
        }
        if (Objects.equals(seg, THIS)){
            return "THIS";
        }
        if (Objects.equals(seg, THAT)){
            return "THAT";
        }
        return "";
    }

}
