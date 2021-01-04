package resource;

public class M implements Resource{
    private String name;

    public M(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "M{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
