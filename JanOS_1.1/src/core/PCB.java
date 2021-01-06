package core;

import com.sun.org.apache.xpath.internal.operations.Bool;
import job.Job;
import memory.Memory;
import memory.Page;

import java.io.FileNotFoundException;
import java.util.*;

public class PCB{
    private int pageInitLocation;
    private static int countCreateNumber = 0;
    private int UID;
    private int pageNumber;
    //当前读取的段号
    private int readingPageNumber;
    //main方法的位移
    private int mainS;
    //当前段内位移
    private int pageS;
    //当前页的大小
    private int pageSize;
    //进程的名字
    private String name;
    //进程中 MAX resource信息
    private  Map<String,Integer> maxMap = new LinkedHashMap<>();
    //进程中的 Need resource信息
    private Map<String,Integer> needMap = new LinkedHashMap<>();
    //进程中获取的 Allocation resource 信息
    private Map<String,Integer> allocationMap = new LinkedHashMap<>();
    //预分配的temperList
    private Map<String,Integer> temperOriginMap;
    //clock置换算法
    private Map<Integer, Boolean> recordPageMap = new LinkedHashMap<>();

    public PCB(int pageInitLocation, int pageNumber) {
        countCreateNumber++;
        UID = countCreateNumber;
        //把readingPageNumber初始化为0
        this.readingPageNumber = 0;
        this.pageInitLocation = pageInitLocation;
        //有一段是数据
        this.pageNumber = pageNumber-1;
        //初始化recordPageMap
        //因为不希望数据区被替换掉，所以pageNumber要减一
//        for(int i = 0; i < this.pageNumber;i++){
//            //初始化全部未被访问
//            recordPageMap.put(i,false);
//        }
        for(int i = 0;i<this.pageNumber;i++){
            String content = Memory.read(pageInitLocation+i);
            String loadInfo = content.split(" ")[2];
            if(loadInfo.equals("load")){
                recordPageMap.put(i,false);
            }
        }

    }

    public int getUID() {
        return UID;
    }

    public void setUID(int UID) {
        this.UID = UID;
    }

    public void updateRecordMap(){
        for(int i = 0;i<pageNumber;i++){
            String content = Memory.read(pageInitLocation+i);
            String loadInfo = content.split(" ")[2];
            if(loadInfo.equals("load")){
                recordPageMap.put(i,false);
            }
        }
    }

    public Map<Integer, Boolean> getRecordPageMap() {
        return recordPageMap;
    }

    public void addElementToAllocation(Map<String,Integer> map1){
        allocationMap.putAll(map1);
    }
    @Deprecated
    public  void addElementToMAX(String name,int value){
        maxMap.put(name,value);
    }

    public Map<String, Integer> getMap() {
        return maxMap;
    }

    public  void addElementToMAX(Map<String,Integer> map1){
        maxMap.putAll(map1);
        for(String key : Core.getResourcesName()){
            if(!maxMap.containsKey(key)){
                //如果没有系统的资源，就默认为0
                maxMap.put(key,0);
            }
        }
        for(String key : maxMap.keySet()){
            //初始化 Allocation
            addElementToAllocation(key,0);
        }
        for(String key : maxMap.keySet()){
            //初始化 Need
            int x = maxMap.get(key);
            addElementToNeed(key,x);
        }

    }

    public void addElementToNeed(String name,int data){
        needMap.put(name,data);
    }
    public void addElementToAllocation(String name,int data){
        allocationMap.put(name,data);
    }

    public Map<String, Integer> getMaxMap() {
        return maxMap;
    }

    public Map<String, Integer> getNeedMap() {
        return needMap;
    }

    public Map<String, Integer> getAllocationMap() {
        return allocationMap;
    }
    //预分配的方法接口
    public  void pre_allocate(String requestName,int requestValues){
        temperOriginMap = new LinkedHashMap<>();
        //把原来的信息先放到temper里面保存
        for(String key : needMap.keySet()){
            temperOriginMap.put(key,needMap.get(key));
        }
        //找到need，减去value
        for(String key : needMap.keySet()){
            if(key.equals(requestName)){
                int origin = needMap.get(key);
                needMap.put(requestName,origin-requestValues);
            }
        }
        //找到allocation的值，加上去
        for(String key : allocationMap.keySet()){
            if(key.equals(requestName)){
                int origin = allocationMap.get(key);
                allocationMap.put(requestName,origin+requestValues);
            }
        }
    }
    //收回预分配
    public void retrieve(){
        allocationMap = new LinkedHashMap<>();
        needMap = temperOriginMap;
        temperOriginMap = null;
        for(String key : needMap.keySet()){
            allocationMap.put(key,maxMap.get(key)-needMap.get(key));
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }



    public int getPageInitLocation() {
        return pageInitLocation;
    }

    public void setPageInitLocation(int pageInitLocation) {
        this.pageInitLocation = pageInitLocation;
    }

    public int getMainS() {
        return mainS;
    }

    public void setMainS(int mainS) {
        this.mainS = mainS;
    }

    @Override
    public String toString() {
        return "PCB{" +
                "pageInitLocation=" + pageInitLocation +
                ", pageNumber=" + pageNumber +
                ", readingPageNumber=" + readingPageNumber +
                ", pageS=" + pageS +
                ", pageSize=" + pageSize +
                ", name='" + name + '\'' +
                '}';
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getReadingPageNumber() {
        return readingPageNumber;
    }

    public void setReadingPageNumber(int readingPageNumber) {
        this.readingPageNumber = readingPageNumber;
    }

    public int getPageS() {
        return pageS;
    }

    public void setPageS(int pageS) {
        this.pageS = pageS;
    }
}
//public class PCB {
//    private Job job;
//    private LinkedList<String> commandList;
//    private LinkedList<Page> pageLinkedList;
//    //用于中断处理的地址
//    private int readingLocation;
//
//    public LinkedList<Page> getPageLinkedList() {
//        return pageLinkedList;
//    }
//
//    public void setPageLinkedList(LinkedList<Page> pageLinkedList) {
//        this.pageLinkedList = pageLinkedList;
//    }
//
//    public int getReadingLocation() {
//        return readingLocation;
//    }
//
//    public void setReadingLocation(int readingLocation) {
//        this.readingLocation = readingLocation;
//    }
//
//    public PCB(Job job) throws FileNotFoundException {
//        readingLocation = 0;
//        pageLinkedList = new LinkedList<>();
//        commandList = new LinkedList<>();
//        this.job = job;
//        Scanner scanner = new Scanner(job.getFile());
//        while(scanner.hasNext()){
//            commandList.add(scanner.nextLine());
//        }
//    }
//    public LinkedList<String> getCommandList(){
//        return commandList;
//    }
//    public void clear(){
//        commandList.clear();
//    }
//    public void protect(List<String> list){
//        commandList = new LinkedList<>(list);
//    }
//    public Job getJob() {
//        return job;
//    }
//
//    public void setJob(Job job) {
//        this.job = job;
//    }
//}
