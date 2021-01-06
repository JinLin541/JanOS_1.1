package core;

import attemper.Attemper;
import com.sun.scenario.effect.impl.state.LinearConvolveKernel;
import edu.princeton.cs.algs4.In;
import job.Job;
import memory.Memory;
import memory.Page;
import memory.register.CommandR1;
import memory.register.R1;
import memory.register.Register;
import processor.Processor;
import sun.management.snmp.jvmmib.JvmRuntimeMeta;
import sun.plugin.perf.PluginRollup;
import util.Utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Core {
    private static Map<String,Integer> available = new LinkedHashMap<>();
    private static Map<String,Integer> temperAvailable;
    private static int Time = 0;
    public static int getTime(){
        return Time;
    }
    /*
    模拟时间片轮转
     */
    public static void usingTime(int wasteTime){
        Time = Time - wasteTime;
    }
    /*
    时间复位
     */
    public static void resetTime(){
        Time = 200;
    }
    //定义系统的资源名字
    private static String[] resourcesName = new String[]{
            "M"
    };
    public static String[] getResourcesName(){
        return resourcesName;
    }

    public static void setAvailable(int...values) {
        int i = 0;
        for(String key : resourcesName){
            available.put(key,values[i]);
            i++;
        }
    }

    public static Map<String, Integer> getAvailable() {
        return available;
    }

    public static void setResourcesName(String... names){
        StringBuilder stringBuilder = new StringBuilder();
        for(String x : names){
            stringBuilder.append(x);
            stringBuilder.append(" ");
        }
        String total = stringBuilder.toString();
        resourcesName = total.split(" ");
    }
    public static void main(String[] args) throws IOException, InterruptedException {
        Core core = new Core();
        setResourcesName("M","N","Q");
        setAvailable(20,20,19);
        Job job = new Job("JanOS_1.1/src/job/classFile/job2class.txt");
        Job job2 = new Job("JanOS_1.1/src/job/classFile/job2class.txt");
        job.setName("A");
        job2.setName("B");
        Attemper.addJob(job);
        Attemper.addJob(job2);
        //高级调度
        Attemper.submitJob();
        System.out.println(Attemper.readyList);
//        int usedTime = 0;
        boolean blokenFlag = false;
        boolean killFlag = false;
       while(true){
           if(Time < 0){
               Time = 0;
           }
           Time = 300+Time;
//           if(usedTime != 0){
//               //使用完上一个时间片的剩余时间，要置零
//               usedTime = 0;
//           }
           PCB runPcb = Attemper.nextPCB();
           if(runPcb == null){
               break;
           }
           if(!blokenFlag && !killFlag){
               System.out.println("New piece Time");
           }else if(blokenFlag){
               blokenFlag = false;
           }else if(killFlag){
               killFlag = false;
           }
           System.out.println(runPcb.getName()+"  start work");
           while(getTime() > 0){
               if(runPcb.getPageS() == 0){
                   //说明还没有保存段的大小在pcb中，设置pcb中的当前段大小
                   setPCBPageSize(runPcb,runPcb.getReadingPageNumber());
               }
               if(runPcb.getPageS() == runPcb.getPageSize() || runPcb.getPageS() > runPcb.getPageSize()){
                   //说明已经读完了当前段的最后一句
                   if(runPcb.getReadingPageNumber() == 0){
                       //如果是main最后一页，作业结束，杀死进程
                       Attemper.killProcess(runPcb);
                       System.out.println("killed  "+runPcb.getName());
                       killFlag = true;
                       //同时跳出该时间片
//                       usedTime = 200-Time;
                       break;
                   }else{
                       //否则就重新访问内存，读取段表，获取下一段的大小保存到pcb
//                       setPCBPageSize(runPcb,runPcb.getReadingPageNumber()+1);
//                       //把页内位移置为0，reading也要变
//                       runPcb.setPageS(0);
//                       runPcb.setReadingPageNumber(runPcb.getReadingPageNumber()+1);
                       //访问过了就把相应的段的访问位置为true
                       runPcb.setReadingPageNumber(0);
                       runPcb.setPageS(runPcb.getMainS());
                       setPCBPageSize(runPcb,runPcb.getReadingPageNumber());
                       if(runPcb.getPageNumber() == 0) {
                           //如果是main最后一页，作业结束，杀死进程
                           Attemper.killProcess(runPcb);
                           System.out.println("killed  " + runPcb.getName());
                           killFlag = true;
                           //同时跳出该时间片
//                       usedTime = 200-Time;
                           break;
                       }
                   }
               }
              //先把段的相对地址转化为内存中的绝对地址,读取两次内存
               //段表的初始地址加上段号，找到段的初始地址，再加上段页内位移，找到目的语句的物理地址
               String buff = Processor.visitReadMemory(runPcb.getPageInitLocation()+runPcb.getReadingPageNumber());

               String[] buff2 = buff.split(" ");
               //先判断有没有缺页
               if(Integer.parseInt(buff2[0]) == -1){
                   //执行中断，调入缺少的页
                   //寻找可以进行置换的页
                   //使用clock算法
                   boolean finishChoose = false;
                   int choseKey = 0;
                   while(true){
                       for(Integer key : runPcb.getRecordPageMap().keySet()){
                           if(runPcb.getRecordPageMap().get(key)){
                               //如果最近有访问，就置为false
                               runPcb.getRecordPageMap().put(key,false);
                           }else{
                               System.out.println("removing page "+key);
                               int location = Integer.parseInt(getLocationFromPageList(runPcb.getPageInitLocation(),key));
                               Processor.removeContentFromMemory(location,Integer.parseInt(getSizeFromPageList(runPcb.getPageInitLocation(),key)));
                                finishChoose = true;
                                Memory.updateWithoutSizeForUnload(runPcb.getPageInitLocation()+key,
                                        String.valueOf(-1));
                                choseKey = key;
                           }
                       }
                       if(finishChoose){
                           runPcb.getRecordPageMap().remove(choseKey);
                           break;
                       }
                   }
                   int currentLocation = Memory.requestMemory(job.getPageLinkedList().get(runPcb.getReadingPageNumber()),runPcb.getPageSize());
                   Memory.updateWithoutSize(runPcb.getPageInitLocation()+runPcb.getReadingPageNumber(),String.valueOf(currentLocation));
                   System.out.println("adding page "+runPcb.getReadingPageNumber());
                   runPcb.getRecordPageMap().put(runPcb.getReadingPageNumber(),false);
               }else{
                   int pageInitLocation = Integer.parseInt(buff2[0]);
                   int location = pageInitLocation + runPcb.getPageS();
                   //获取语句
                   String command = Processor.visitReadMemory(location);
                   String[] buff3 = command.split(" ");
                   switch(buff3[0]){
                       case "LOAD" :
                           int data = visitData(runPcb,Integer.parseInt(buff3[1]));
                           Processor.LOAD(data, Utils.lookingRegisterForString(buff3[2]));
                           runPcb.getRecordPageMap().put(runPcb.getReadingPageNumber(),true);
                           if(runPcb.getReadingPageNumber() == 0){
                               runPcb.setMainS(runPcb.getMainS()+1);
                           }
                           break;
                       case "STORE":
                           writeData(runPcb,Integer.parseInt(buff3[1]),buff3[2]);
                           runPcb.getRecordPageMap().put(runPcb.getReadingPageNumber(),true);
                           if(runPcb.getReadingPageNumber() == 0){
                               runPcb.setMainS(runPcb.getMainS()+1);
                           }
                           break;
                       case "request":
                           String sequence = Processor.bankerAlgorithms(runPcb,buff3[1],Integer.parseInt(buff3[2]));
                           runPcb.getRecordPageMap().put(runPcb.getReadingPageNumber(),true);
                           if(sequence == null){
                               //堵塞进程
                               blokenFlag = true;
                           }
                           if(runPcb.getReadingPageNumber() == 0){
                               runPcb.setMainS(runPcb.getMainS()+1);
                           }
                           break;
                       case "goto":
                           runPcb.setPageS(-1);
                           runPcb.setReadingPageNumber(Integer.parseInt(buff3[1]));
                           runPcb.setMainS(runPcb.getMainS()+1);
                           setPCBPageSize(runPcb,runPcb.getReadingPageNumber());
                           break;
                       default:
                           //读不到语句，把进程堵塞
                           blokenFlag = true;
                           break;
                   }
               }
               if(blokenFlag){
                   //如果进程发生堵塞，就跳出该时间片，并且设置已经用了的时间，以便给下一个一个作业多一点时间
//                   usedTime = 200-Time;
                   System.out.println("bloken  "+runPcb.getName());
                   break;
               }
               runPcb.setPageS(runPcb.getPageS()+1);
           }
       }

    }
    public static void setPCBPageSize(PCB pcb,int readingPageNumber) throws InterruptedException {
        String buff = Processor.visitReadMemory(pcb.getPageInitLocation()+readingPageNumber);
        String[] buff2 = buff.split(" ");
        pcb.setPageSize(Integer.parseInt(buff2[1]));
    }

    public static int visitData(PCB pcb,int dataLocation) throws InterruptedException {
        //访问一个作业的数据区
        //获取数据段的起始位置
        String buff = Processor.visitReadMemory(pcb.getPageInitLocation()+pcb.getPageNumber());
        //二次访问获取数据
        String[] buff2 = buff.split(" ");
        String buff3 = Processor.visitReadMemory(Integer.parseInt(buff2[0])+dataLocation);
        return Integer.parseInt(buff3);
    }


    public static String getLocationFromPageList(int pageLocation,int i) throws InterruptedException {
        String buff = Processor.visitReadMemory(pageLocation+i);
        return buff.split(" ")[0];
    }

    public static String getSizeFromPageList(int pageLocation,int i) throws InterruptedException {
        String buff = Processor.visitReadMemory(pageLocation+i);
        return buff.split(" ")[1];
    }


    public static void writeData(PCB pcb,int dataDestination,String register) throws InterruptedException, IOException {
        Register register1 = Utils.lookingRegisterForString(register);
        int source = Processor.STORE(register1);
        String buff = Processor.visitReadMemory(pcb.getPageInitLocation()+pcb.getPageNumber());
        //二次访问获取数据
        String[] buff2 = buff.split(" ");
        int location = Integer.parseInt(buff2[0])+dataDestination;
        Processor.visitWriteMemory(source,location);
    }

//    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
//        Core core = new Core();
//        String command;
//        Job job = new Job("JanOS_1.1/src/job/srcFile/job1.txt");
//        Job job2 = new Job("JanOS_1.1/src/job/srcFile/job2class.txt");
//        Attemper.addPCB(new PCB(job));
//        Attemper.addPCB(new PCB(job2));
//        int usedTime = 0;
//        while(true){
//            PCB runpcb = Attemper.nextPCB();
//            CommandR1.load(runpcb.getCommandList());
//            Time = 100-usedTime;
//            int size = CommandR1.getCommandlist().size();
//            boolean brokenFlag = false;
//            boolean finished = false;
//            while(getTime() > 0){
//                if(size == 0){
//                    Core.usedTime(20);
//                    System.out.println("waiting...");
//                    Thread.sleep(1000);
//                }else{
//                    command = CommandR1.process();
//                    String[] splite = command.split(" ");
//                    switch(splite[0]){
//                        case "LOAD":
//                            Thread.sleep(500);
//                            System.out.println(command);
//                            Processor.LOAD(Integer.parseInt(splite[1]), R1.getR1());
//                            System.out.println(R1.getR1().getData());
//                            break;
//                        case "STORE":
//                            break;
//                        case "finished":
//                            Attemper.killProcess(runpcb);
//                            finished = true;
//                            break;
//                        default:
//                            //发生错误，中断
//                            brokenFlag = true;
//                            break;
//                    }
//                    if(brokenFlag){
//                        //发生中断
//                        Attemper.block(runpcb);
//                        System.out.println("broken");
//                        usedTime = 100-Time;
//                        break;
//                    }
//                    if(finished){
//                        System.out.println("finished");
//                        usedTime = 100-Time;
//                        break;
//                    }
//                    //清理运行过的语句
//                    CommandR1.run();
//                    size--;
//                }
//            }
//            //保护现场
//            runpcb.protect(CommandR1.getCommandlist());
//
//            if(!brokenFlag){
//                System.out.println("新的时间片");
//            }
//        }
//    }

    //银行家算法
    //返回安全序列
    public static String AskBanker(PCB pcb,String resourceName,int requestValue){
        StringBuilder stringBuilder = new StringBuilder();
        int need = 0;
        //先判断有没有超过她本身的Need
        for(String key : pcb.getNeedMap().keySet()){
            if(key.equals(resourceName)){
                need = pcb.getNeedMap().get(key);
                if(need < requestValue){
                    return null;
                }
            }
        }
        if(need == 0){
            //说明不够资源分配，没有找到对应的resourceName
            return null;
        }
        //判断有没有超过系统的available
        for(String key : available.keySet()){
            if(key.equals(resourceName)){
                if(requestValue > available.get(key)){
                    return null;
                }
            }
        }

        //预分配
        pcb.pre_allocate(resourceName,requestValue);
        temperAvailable = new LinkedHashMap<>();
        temperAvailable.putAll(available);
        available.put(resourceName,available.get(resourceName)-requestValue);

        //判断有没有安全序列
        String sequence = getSecuritySequence(Attemper.readyList);
        if(sequence == null){
            //没有安全序列
            //收回分配
            pcb.retrieve();
            available = temperAvailable;
            return null;
        }else{
            //真分配
            return sequence;
        }
    }

    //安全序列算法
    public static String getSecuritySequence(List<PCB> list){
        StringBuilder stringBuilder = new StringBuilder();
        int PCB_pool_number = list.size();
        //Fished 数组
        boolean[] finished = new boolean[PCB_pool_number];
        for(int i = 0;i < PCB_pool_number;i++){
            finished[i] = false;
        }
        //work向量
        Map<String,Integer> work = new LinkedHashMap<>();
        for(String key : resourcesName){
            work.put(key,available.get(key));
        }
        int count = 0;
        while(count < resourcesName.length){
            for(int i = 0;i < finished.length;i++){
                if(!finished[i]) {
                    PCB pcb = list.get(i);
                    //finished为false
                    //确保每一个need都小于work
                    boolean qualified = true;
                    for (String key : pcb.getNeedMap().keySet()){
                        int compare = pcb.getNeedMap().get(key);
                        if(compare > work.get(key)){
                            qualified = false;
                            break;
                        }
                    }
                    if(qualified){
                        //把finished置为true
                        finished[i] = true;
                        //把work加上need
                        for(String key : work.keySet()){
                            int plusData = pcb.getAllocationMap().get(key);
                            work.put(key,plusData+work.get(key));
                        }
                        //把pcb的名字加到StringBuilder里面
                        stringBuilder.append(pcb.getName());
                    }
                }
            }
            //判断finished是不是都是true
            boolean completeQualified = true;
            for(boolean x : finished){
                if (!x) {
                    //发一个为false，说明还没有完成
                    completeQualified = false;
                    break;
                }
            }
            if(completeQualified){
                break;
            }
            count++;
        }
        //判断finished是不是都是true
        boolean completeQualified = true;
        for(boolean x : finished){
            if (!x) {
                //发一个为false，说明还没有完成
                completeQualified = false;

                break;
            }
        }
        if(completeQualified){
            return stringBuilder.toString();
        }else{
            return null;
        }
    }

}
