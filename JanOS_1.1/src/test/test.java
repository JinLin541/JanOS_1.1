package test;


import attemper.Attemper;
import core.Core;
import core.PCB;
import job.Job;
import org.junit.Test;

import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;

public class test {
    @Test
    public void test4() throws IOException {
        Core core = new Core();
        Core.setResourcesName("M","N","Q");
        Core.setAvailable(20,20,19);
        Job job = new Job("./src/job/srcFile/job2.txt");
        Job job2 = new Job("./src/job/srcFile/job2.txt");
        job.setName("A");
        job2.setName("B");
        Attemper.addJob(job);
        Attemper.addJob(job2);
        //高级调度
        Attemper.submitJob();
        System.out.println(Attemper.readyList);
        //检查banker
        PCB pcb = Attemper.nextPCB();
        String sequence = Core.AskBanker(pcb,"M",12);
        PCB pcb2 = Attemper.nextPCB();
        String sequence2 = Core.AskBanker(pcb,"N",8);
        System.out.println(sequence);
        System.out.println(sequence2);
    }
    public void setAvailable(int...values) {
        int i = 0;
        System.out.println(values[i]);
        i++;
        System.out.println(values[i]);
    }
    @Test
    public void test3() throws FileNotFoundException {
        setAvailable(1,2);
    }
    @Test
    public void test2() throws IOException {
        File file = new File("./src/test/a.txt");
        FileWriter fileWriter = new FileWriter(file);
        for(int i = 0;i<100;i++){
            fileWriter.write(String.valueOf(i)+"\n");
        }
        fileWriter.close();
    }
    public static void main(String[] args) throws Exception{
        while(true){
            switch ("hhh"){
                case "hh":
                    System.out.println("hh");
                    break;
                default:
                    System.out.println("asas");
            }
        }
    }
}
