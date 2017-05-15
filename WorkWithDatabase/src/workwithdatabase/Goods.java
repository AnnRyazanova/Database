package workwithdatabase;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Goods {
    public final IntegerProperty id;
    public final StringProperty nomenclature;
    public final StringProperty measure;
    
    public Goods(int id, String nomenclature, String measure) {
        this.id = new SimpleIntegerProperty(id);
        this.nomenclature = new SimpleStringProperty(nomenclature);
        this.measure = new SimpleStringProperty(measure);
    }
}
