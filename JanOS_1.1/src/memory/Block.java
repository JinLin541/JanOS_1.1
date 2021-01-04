package memory;

public class Block {
    private int initLocation;
    private int size;

    public int getInitLocation() {
        return initLocation;
    }

    public Block(int initLocation, int size) {
        this.initLocation = initLocation;
        this.size = size;
    }

    public void setInitLocation(int initLocation) {
        this.initLocation = initLocation;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "Block{" +
                "initLocation=" + initLocation +
                ", size=" + size +
                '}';
    }
}
