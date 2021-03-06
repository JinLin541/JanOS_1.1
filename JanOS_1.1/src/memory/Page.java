package memory;

public class Page {
    private String[] info;
    private int size;
    //段号
    private int number;
    public Page(String[] pageContent){
        this.size = pageContent.length;
        info = new String[size];
        System.arraycopy(pageContent, 0, info, 0, size);
    }
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String[] getInfo() {
        return info;
    }

    public void setInfo(String[] info) {
        this.info = info;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
