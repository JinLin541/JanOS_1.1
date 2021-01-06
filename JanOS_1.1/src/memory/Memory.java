package memory;

import com.sun.deploy.util.BlackList;
import job.Job;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
/*
内存模型
 */
public class Memory{
    private static String[] info = new String[512];

    public static String[] getInfo() {
        return info;
    }

    private static LinkedList<Block> blankList = new LinkedList<>();
    static{
        //初始化空块链表
        blankList.add(new Block(0,512));
    }
    /*
    请求内存空间,-1表示没找到，大于0表示找到
     */
    public static int requestMemory(Page page, int requestSize) throws IOException {
        int flag = -1;
        String[] buff = page.getInfo();
        for(Block x : blankList){
            //如果找到合适的空白块，就把作业加入进去
            if(x.getSize() - requestSize > 0 && x.getSize() - requestSize < 5){

                int start = x.getInitLocation();
                for(int i = start;i < start+page.getSize();i++){
                    info[i] = buff[i-start];
                }
                flag = x.getInitLocation();
                x.setInitLocation(x.getInitLocation()+requestSize);
                x.setSize(x.getSize()-requestSize);
                break;
            }else if(x.getSize() > requestSize && x.getSize() - requestSize > 5){
                int start = x.getInitLocation();
                for(int i = start;i < start+page.getSize();i++){
                    info[i] = buff[i-start];
                }
                int xSize = x.getSize();
                flag = x.getInitLocation();
                x.setInitLocation(x.getInitLocation()+requestSize);
                x.setSize(xSize-requestSize);
                break;
            }
        }
        write();
        return flag;
    }

    public static void update(int location,String content) throws IOException {
        info[location] = content;
        write();
    }
    public static void updateWithoutSize(int location,String blockLocation) throws IOException {
        String size =info[location].split(" ")[1];
        update(location,blockLocation+" "+size+" load");
    }
    public static void updateWithoutSizeForUnload(int location,String blockLocation) throws IOException {
        String size =info[location].split(" ")[1];
        update(location,blockLocation+" "+size+" unload");
    }

    public static void unitMemory(int initLocation,int size) throws IOException {
        //从String数组里面删除内容
        for(int i = initLocation;i<size+initLocation;i++){
            info[i] = "";
        }
        //判断有没有邻接的空白块
//        for(Block block : blankList){
//            int index = blankList.indexOf(block);
//            Block nextOne;
//            if(index != blankList.size()-1){
//                 nextOne = blankList.get(index+1);
//            }else{
//                 nextOne = new Block(-1,0);
//            }
//            if(block.getInitLocation() == initLocation + size){
//                block.setInitLocation(initLocation);
//                block.setSize(size+block.getSize());
//            }else if(block.getInitLocation()+block.getSize() == initLocation){
//                block.setSize(size+block.getSize());
//            }else if(initLocation == block.getInitLocation() + block.getSize() && initLocation+size == nextOne.getInitLocation()){
//                block.setSize(block.getSize() + size + nextOne.getSize());
//            }else{
//                Block newOne = new Block(initLocation,size);
//                blankList.add(newOne);
//                blankList.sort(new Comparator<Block>() {
//                    @Override
//                    public int compare(Block o1, Block o2) {
//                        return Integer.compare(o1.getSize(),o2.getSize());
//                    }
//                });
//            }
//        }
        boolean unityflag = false;
        for(int i = 0; i < blankList.size();i++){
            Block nextOne;
            if(i != blankList.size()-1){
                nextOne = blankList.get(i +1);
            }else{
                nextOne = new Block(-1,0);
            }
            if(blankList.get(i).getInitLocation() == initLocation + size){
                blankList.get(i).setInitLocation(initLocation);
                blankList.get(i).setSize(size+blankList.get(i).getSize());
                unityflag = true;
            }else if(blankList.get(i).getInitLocation()+blankList.get(i).getSize() == initLocation){
                blankList.get(i).setSize(size+blankList.get(i).getSize());
                unityflag = true;
            }else if(initLocation == blankList.get(i).getInitLocation() + blankList.get(i).getSize() && initLocation+size == nextOne.getInitLocation()){
                blankList.get(i).setSize(blankList.get(i).getSize() + size + nextOne.getSize());
                unityflag = true;
            }
        }
        if(!unityflag){
            Block newOne = new Block(initLocation,size);
            blankList.add(newOne);
            //最坏匹配算法
            blankList.sort(new Comparator<Block>() {
                @Override
                public int compare(Block o1, Block o2) {
                    return Integer.compare(o1.getSize(),o2.getSize());
                }
            });
        }
        write();
    }
    public static void write() throws IOException {
        String path = "JanOS_1.1/src/memory/memoryInfo.txt";
        FileWriter fileWriter = new FileWriter(path);
        for(int i = 0; i < info.length;i++){
            fileWriter.write(String.valueOf(i)+" "+info[i]+"\n");
        }
        fileWriter.close();
    }
    /*
    根据绝对地址找到内存的指令
     */
    public static String read(int location){
        return info[location];
    }
    public static void write(int location,int data) throws IOException {
        info[location] = String.valueOf(data);
        write();
    }

    public static void main(String[] args) throws IOException {
        write();
//        String path = "JanOS_1.1/src/job/srcFile/job1.txt";
//        Scanner scanner = new Scanner(new File(path));
//        String[] buff = new String[18];
//        for(int i = 0;i<9;i++){
//            buff[i]  = scanner.nextLine();
//        }
//        for(int i = 9;i<18;i++){
//            buff[i]  = buff[i-9];
//        }
//        System.out.println(Arrays.toString(buff));
//        Page page = new Page(buff);
//        System.out.println(requestMemory(page,18));
//
//        System.out.println(blankList);
//        System.out.println(blankList.size());
//        unitMemory(5,7);
//
//        System.out.println(blankList);
//        Page page2 = new Page(new String[]{
//                "1","2","3","4","5","6","7","8"
//        });
//        int i = requestMemory(page2,8);
//        System.out.println(i);
//
//        System.out.println(blankList);
//        unitMemory(23,3);
//
//        System.out.println(blankList);
//        System.out.println(read(0));
    }

}
