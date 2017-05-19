package workwithdatabase.models;

public class Agent {

    public final int id;
    public final String name;
    public final String phone;
    public final String city;

    public Agent(int id, String name, String phone, String city) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.city = city;
    }

    @Override
    public String toString() {
        return name;
    }

}
