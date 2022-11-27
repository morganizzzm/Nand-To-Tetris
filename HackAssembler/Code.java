
import java.util.HashMap;
import java.util.Objects;

public class Code {
    HashMap<String, int[]> listRegisters;
    HashMap<String, int[]> listCommands;
    HashMap<String, int[]> listJumps;


    public Code() {
        fillHashMaps();
    }


    int[] dest(String str){
        return listRegisters.get(str);
    }


    int[] comp(String str){
        return listCommands.get(str);
    }

    int[] jump(String str){
        return listJumps.get(str);
    }
    void fillHashMaps(){
        fillRegisters();
        fillCommands();
        fillJumps();
    }
    void fillJumps(){
        listJumps = new HashMap<>();
        listJumps.put("", new int[]{0,0,0});
        listJumps.put("JGT", new int[]{0,0,1});
        listJumps.put("JEQ", new int[]{0,1,0});
        listJumps.put("JGE", new int[]{0,1,1});
        listJumps.put("JLT", new int[]{1,0,0});
        listJumps.put("JNE", new int[]{1,0,1});
        listJumps.put("JLE", new int[]{1,1,0});
        listJumps.put("JMP", new int[]{1,1,1});

    }
    void fillRegisters(){
        listRegisters = new HashMap<>();
        listRegisters.put("", new int[]{0,0,0});
        listRegisters.put("M", new int[]{0,0,1});
        listRegisters.put("D", new int[]{0,1,0});
        listRegisters.put("MD", new int[]{0,1,1});
        listRegisters.put("A", new int[]{1,0,0});
        listRegisters.put("AM", new int[]{1,0,1});
        listRegisters.put("AD", new int[]{1,1,0});
        listRegisters.put("AMD", new int[]{1,1,1});

    }

    boolean ifShift(String command){
        return Objects.equals(command, "A<<") || Objects.equals(command, "A>>") ||
                Objects.equals(command, "D<<") || Objects.equals(command, "D>>") ||
                Objects.equals(command, "M<<") || Objects.equals(command, "M>>");
    }

    void fillCommands(){
        listCommands = new HashMap<>();
        listCommands.put("0",   new int[]{0, 1,0,1,0,1,0});
        listCommands.put("1",   new int[]{0, 1,1,1,1,1,1});
        listCommands.put("-1",  new int[]{0, 1,1,1,0,1,0});
        listCommands.put("D",   new int[]{0, 0,0,1,1,0,0});
        listCommands.put("A",   new int[]{0, 1,1,0,0,0,0});
        listCommands.put("M",   new int[]{1, 1,1,0,0,0,0});
        listCommands.put("!D",  new int[]{0, 0,0,1,1,0,1});
        listCommands.put("!A",  new int[]{0, 1,1,0,0,0,1});
        listCommands.put("!M",  new int[]{1, 1,1,0,0,0,1});
        listCommands.put("-D",  new int[]{0, 0,0,1,1,1,1});
        listCommands.put("-A",  new int[]{0, 1,1,0,0,1,1});
        listCommands.put("-M",  new int[]{1, 1,1,0,0,1,1});
        listCommands.put("D+1", new int[]{0, 0,1,1,1,1,1});
        listCommands.put("A+1", new int[]{0, 1,1,0,1,1,1});
        listCommands.put("M+1", new int[]{1, 1,1,0,1,1,1});
        listCommands.put("D-1", new int[]{0, 0,0,1,1,1,0});
        listCommands.put("A-1", new int[]{0, 1,1,0,0,1,0});
        listCommands.put("M-1", new int[]{1, 1,1,0,0,1,0});
        listCommands.put("D+A", new int[]{0, 0,0,0,0,1,0});
        listCommands.put("D+M", new int[]{1, 0,0,0,0,1,0});
        listCommands.put("D-A", new int[]{0, 0,1,0,0,1,1});
        listCommands.put("D-M", new int[]{1, 0,1,0,0,1,1});
        listCommands.put("A-D", new int[]{0, 0,0,0,1,1,1});
        listCommands.put("M-D", new int[]{1, 0,0,0,1,1,1});
        listCommands.put("D&A", new int[]{0, 0,0,0,0,0,0});
        listCommands.put("D&M", new int[]{1, 0,0,0,0,0,0});

        listCommands.put("D|A", new int[]{0, 0,1,0,1,0,1});
        listCommands.put("D|M", new int[]{1, 0,1,0,1,0,1});
        listCommands.put("A<<", new int[]{0,1,0,0,0,0,0});
        listCommands.put("D<<", new int[]{0,1,1,0,0,0,0});
        listCommands.put("M<<", new int[]{1,1,0,0,0,0,0});
        listCommands.put("A>>", new int[]{0,0,0,0,0,0,0});
        listCommands.put("D>>", new int[]{0,0,1,0,0,0,0});
        listCommands.put("M>>", new int[]{1,0,0,0,0,0,0});



    }
}

