package memory.register;
/*
使用单例模式
 */
public class R1 implements Register{
    private int data;
    private static Register r1;
    static{
        r1 = new R1();
    }
    private R1(){
        data = 0;
    }
    @Override
    public int getData() {
        return data;
    }

    @Override
    public void setData(int data) {
        this.data = data;
    }

    @Override
    public String getName() {
        return "Register1";
    }
    public static Register getR1(){
        return r1;
    }

    public static void main(String[] args) {
        Register r1 = R1.getR1();
        r1.setData(1);
        System.out.println(r1.getData());
    }
}
