package workwithdatabase.models;

public class Goods {
    public final int id;
    public final String nomenclature;
    public final String measure;
    
    public Goods() { this(0, "", ""); }
    
    public Goods(int id, String nomenclature, String measure) {
        this.id = id;
        this.nomenclature = nomenclature;
        this.measure = measure;
    }

    @Override
    public String toString() {
        return nomenclature;
    }
}
