package memory.register;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/*
指令寄存器
 */
public class CommandR1 {
    private static LinkedList<String> commandlist = new LinkedList<>();
    public static void load(List<String> list){
        commandlist = new LinkedList<>(list);
    }
    public static void addCommand(String command){
        commandlist.add(command);
    }
    public static LinkedList<String> getCommandlist(){
        return commandlist;
    }
    public static void clear(){
        commandlist.clear();
    }
    public static void run(){
        commandlist.removeFirst();
    }
    public static String process(){
        return commandlist.getFirst();
    }
}
