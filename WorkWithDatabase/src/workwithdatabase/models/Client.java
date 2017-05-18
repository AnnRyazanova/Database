package workwithdatabase.models;

public class Client {
    public final int id;
    public final String name;
    public final String city;
    public final String address;
    public final String phone;
    
    public Client(int id, String name, String city, String address, String phone) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.address = address;
        this.phone = phone;
    }

    @Override
    public String toString() {
        return name;
    }
}
