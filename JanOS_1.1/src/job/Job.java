package job;

import memory.Page;
import java.io.File;
import java.util.LinkedList;
import java.util.Scanner;

public class Job {
    private  File file;
    private  LinkedList<Page> pageLinkedList;
    private int priority;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Job(String path){
        setFile(path);
        pageLinkedList = new LinkedList<>();
    }
    public File getFile() {
        return file;
    }
    public void setPriority(int order){
        this.priority = order;
    }
    public LinkedList<Page> getPageLinkedList(){
        return pageLinkedList;
    }
    public int getPriority(){
        return priority;
    }
    public void setFile(File file) {
        this.file = file;
    }
    public void setFile(String path){
        file = new File(path);
    }

    @Override
    public String toString() {
        return "Job{" +
                "file=" + file +
                ", pageLinkedList=" + pageLinkedList +
                ", priority=" + priority +
                '}';
    }
}
