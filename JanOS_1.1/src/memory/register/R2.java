package memory.register;

public class R2 implements Register{
    private int data;
    private static Register r2;
    static {
        r2 = new R2();
    }
    private R2(){
        data = 0;
    }

    @Override
    public int getData() {
        return data;
    }

    @Override
    public void setData(int data) {
        this.data  = data;
    }

    @Override
    public String getName() {
        return "Register 2";
    }
    public static Register getR2(){
        return r2;
    }
}
