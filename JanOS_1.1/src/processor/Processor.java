package processor;

import attemper.Attemper;
import core.Core;
import core.PCB;
import job.Job;
import memory.Memory;
import memory.register.R1;
import memory.register.R2;
import memory.register.Register;

import java.io.IOException;
import java.util.Arrays;

public class Processor {
    /*
    定义一些处理器的指令
     */
    public static void ADD(Register r1,Register r2){
        //把r1和r2相加的值存入r1
        System.out.println(r1.getName()+"  add  "+r2.getName());
        Core.usingTime(15);
        r1.setData(r1.getData()+r2.getData());
    }
    public static void LOAD(int data, Register register) throws InterruptedException {
        register.setData(data);
        Core.usingTime(10);
        System.out.println("executing LOAD");
        System.out.println("put "+data+" into  "+register.getName());
        Thread.sleep(500);
    }
    public static int STORE(Register r) throws InterruptedException {
        Core.usingTime(10);
        System.out.println("executing STORE....");
        Thread.sleep(500);
        return r.getData();
    }
    public static String visitReadMemory(int location) throws InterruptedException {
        Core.usingTime(30);
        System.out.println("reading Memory  "+location);
        Thread.sleep(1000);
        return Memory.read(location);
    }
    public static int changeLocation(int releventLocation,Register r) throws InterruptedException {
        Core.usingTime(10);
        System.out.println("changing location used register "+r.getName());
        Thread.sleep(200);
        return releventLocation+r.getData();
    }
    public static void visitWriteMemory(int data,int location) throws InterruptedException, IOException {
        Core.usingTime(30);
        Memory.write(location,data);
        System.out.println("writing Memory  "+location+"  "+"data"+data);
        Thread.sleep(1000);
    }
    public static String bankerAlgorithms(PCB pcb, String resourceName, int requestValue) throws InterruptedException {
        Core.usingTime(30);
        System.out.println("request"+" "+resourceName+" "+requestValue);
        System.out.println("executing the bankerAlgorithms.....");
        Thread.sleep(1500);
        String sequence = Core.AskBanker(pcb,resourceName,requestValue);
        System.out.println("Security sequence "+sequence);
        return sequence;
    }
    public static void removeContentFromMemory(int location,int size) throws InterruptedException, IOException {
        Core.usingTime(30);
        System.out.println("Removing");
        Thread.sleep(500);
        Memory.unitMemory(location,size);

    }


    public static void main(String[] args) throws IOException {
//        Processor.LOAD(1, R1.getR1());
//        System.out.println(R1.getR1().getData());
//        visitWriteMemory(1,10);
//        System.out.println(Arrays.toString(Memory.getInfo()));
    }
}
