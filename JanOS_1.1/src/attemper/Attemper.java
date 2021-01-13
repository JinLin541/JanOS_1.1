package attemper;

import core.Core;
import core.PCB;
import job.Job;
import memory.Memory;
import memory.Page;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

public class Attemper {
    //作业队列
    private static LinkedList<Job> jobLinkedList = new LinkedList<>();
    //作业等待队列
    private static LinkedList<Job> jobWaitList = new LinkedList<>();
    //临时段表
    private static LinkedList<String> temperPageList = new LinkedList<>();
    //PCB就绪队列
    public static LinkedList<PCB> readyList = new LinkedList<>();
    //设置就绪队列的最大值
    private static int PCB_Number = 10;
    //等待队列
    public static LinkedList<PCB> blockList = new LinkedList<>();

    /*
    高级调度（作业调度）
     */
    public static void addJob(Job job){
        jobLinkedList.add(job);
    }
    public static void submitJob() throws IOException {
        Scanner scanner;
        //设置优先级
        for(Job job : jobLinkedList){
            scanner = new Scanner(job.getFile());
            String[] buff = scanner.nextLine().split(" ");
            job.setPriority(Integer.parseInt(buff[1]));
        }
        jobLinkedList.sort(new Comparator<Job>() {
            @Override
            public int compare(Job o1, Job o2) {
                return Integer.compare(o1.getPriority(),o2.getPriority());
            }
        });
        int jobListSize = jobLinkedList.size();
        for(int i = 0;i < jobListSize;i++){
            if(readyList.size() > PCB_Number){
                //如果就绪队列满了，就把剩下的作业放到等待队列中
                int buffSize = jobLinkedList.size();
                for(int j = buffSize-1;j >= i;j++){
                    jobWaitList.add(jobLinkedList.get(i));
                    jobLinkedList.removeLast();
                }
                break;
            }else{
                Map<String,Integer> map = new LinkedHashMap<>();
                scanner = new Scanner(jobLinkedList.get(i).getFile());
                ArrayList<String> arrayList = new ArrayList<>();
                boolean countFlag = false;
                boolean maxFlag = false;
                int number = 0;
                while(scanner.hasNext()){
                    String cmd = scanner.nextLine();
                    if(cmd.equals("function") || cmd.equals("data")){
                        countFlag = true;
                    }else if(cmd.equals("finished")){
                        countFlag = false;
                        String[] buff = new String[arrayList.size()];
                        for(int q = 0;q<buff.length;q++){
                            buff[q] = arrayList.get(q);
                        }
                        Page page = new Page(buff);
                        //给段赋号
                        page.setNumber(number++);
                        jobLinkedList.get(i).getPageLinkedList().add(page);
                        arrayList.clear();
                    }else if(cmd.equals("MAX")){
                        maxFlag = true;
                    }else if(cmd.equals("MAX_FIN")){
                        //停止计数
                        maxFlag = false;
                    } else{
                        if(countFlag){
                            arrayList.add(cmd);
                        }else if(maxFlag){
                            String[] buff = cmd.split(" ");
                            map.put(buff[0],Integer.valueOf(buff[1]));
                        }
                    }
                }
                LinkedList<Page> temper = jobLinkedList.get(i).getPageLinkedList();
                int location = 0;
                int startLocation = Memory.requestMemory(temper.get(0),temper.get(0).getSize());
                int dataLocation = Memory.requestMemory(temper.getLast(),temper.getLast().getSize());
                //先把段表加载到内存中
                for(Page page : temper){
                    //把段加载到内存中
//                    location = Memory.requestMemory(page,page.getSize());
                    //段表由3部分构成，物理地址，页面的大小，状态位
                    temperPageList.add(String.valueOf(-1)+" "+page.getSize()+" unload");
                }
                //把段表写在内存中
                String[] buff3 = new String[temperPageList.size()];
                for(int q = 0; q < buff3.length;q++){
                    buff3[q] = temperPageList.get(q);
                }
                Page pageList = new Page(buff3);
                //段表的起始地址
                int pageListLocation = Memory.requestMemory(pageList,pageList.getSize());

                //先把第一个页面和最后一个页面尝试载入到内存
                if(startLocation == -1 || dataLocation == -1){
                    //说明内存中没有足够的位置来提供程序的最小运行要求
                    //把该作业加载等待队列中去
                    Job job = jobLinkedList.remove(i);
                    jobWaitList.add(job);
                }else if(pageListLocation == -1){
                    //说明段表加载不进去
                    Job job = jobLinkedList.remove(i);
                    jobWaitList.add(job);
                }
                else{

                    Memory.updateWithoutSize(pageListLocation,String.valueOf(startLocation));
                    Memory.updateWithoutSize(pageListLocation+temper.size()-1,String.valueOf(dataLocation));
//                    PCB pcb = new PCB(pageListLocation,temperPageList.size());
//                    String name = jobLinkedList.get(i).getName();
//                    pcb.setName(name);
//                    //把资源加到对应的参数里面
//                    pcb.addElementToMAX(map);
//                    readyList.add(pcb);
                    //预定加载的最大段数
                    int loadSize = temper.size()/2;
                    //如果加载的页面大于3页，就再加载后面的页面
                    if(loadSize != 1){
                        for(int j = 1;j<loadSize;j++){
                            //把剩下的页按照内存分配算法载入到内存
                            location = Memory.requestMemory(temper.get(j),temper.get(j).getSize());
                            if(location != -1){
                                //说明载入成功
                                //修改段表中的访问位和块号
                                Memory.updateWithoutSize(pageListLocation+j,String.valueOf(location));
                            }else{
                                break;
                            }
                        }
                    }
                    PCB pcb = new PCB(pageListLocation,temperPageList.size());
                    String name = jobLinkedList.get(i).getName();
                    pcb.setName(name);
                    //把资源加到对应的参数里面
                    pcb.addElementToMAX(map);
                    readyList.add(pcb);

                }
//                for(Page page : temper){
//                    //把段加载到内存中
//                    location = Memory.requestMemory(page,page.getSize());
//                    temperPageList.add(String.valueOf(location)+" "+page.getSize());
//                }
//                //把段表写在内存中
//                String[] buff3 = new String[temperPageList.size()];
//                for(int q = 0; q < buff3.length;q++){
//                    buff3[q] = temperPageList.get(q);
//                }
//                Page pageList = new Page(buff3);
//                //段表的起始地址
//                int pageListLocation = Memory.requestMemory(pageList,pageList.getSize());
//                PCB pcb = new PCB(pageListLocation,temperPageList.size());
//                String name = jobLinkedList.get(i).getName();
//                pcb.setName(name);
//                //把资源加到对应的参数里面
//                pcb.addElementToMAX(map);
//                readyList.add(pcb);
            }
            temperPageList.clear();
        }
    }
    public static void addPCB(PCB pcb){
        if(PCB_Number > 10){
            blockList.add(pcb);
        }else{
            readyList.add(pcb);
        }
    }
    public static PCB nextPCB(){
//        if(blockList.size()!=0){
//            PCB temper = blockList.removeFirst();
//            addPCB(temper);
//        }
        if(readyList.size() == 0){
            return null;
        }else{
            PCB temper = readyList.removeFirst();
            addPCB(temper);
            return temper;
        }
    }
    public static void block(PCB pcb){
        blockList.add(pcb);
        readyList.remove(pcb);
    }
    public static void killProcess(PCB pcb){
        //先把pcb中的资源还给系统
        for(String key : pcb.getAllocationMap().keySet()){
            Core.getAvailable().put(key,pcb.getAllocationMap().get(key)+Core.getAvailable().get(key));
        }
        readyList.remove(pcb);
        //唤醒就绪队列中的一个进程
        if(blockList.size()!=0){
            readyList.add(blockList.removeFirst());
        }
    }

    public static void main(String[] args) throws IOException {
//        Job job = new Job("JanOS_1.1/src/job/srcFile/job2class.txt");
//        Job job2 = new Job("JanOS_1.1/src/job/srcFile/job2class.txt");
//        addJob(job);
//        addJob(job2);
//        submitJob();

    }



}
