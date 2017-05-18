package workwithdatabase.models;

public class Warehouse {
    public final int id;
    public final String city;
    public final String name;
    
    public Warehouse(int id, String city, String name) {
        this.id = id;
        this.city = city;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
