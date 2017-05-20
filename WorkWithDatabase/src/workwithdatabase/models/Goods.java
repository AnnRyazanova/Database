package workwithdatabase.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public final class Goods {

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty nomenclature = new SimpleStringProperty();
    private final StringProperty measure = new SimpleStringProperty();

    public Goods() {
        this(0, "", "");
    }

    public Goods(int id, String nomenclature, String measure) {
        setId(id);
        setNomenclature(nomenclature);
        setMeasure(measure);
    }

    @Override
    public String toString() {
        return nomenclature.get();
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getNomenclature() {
        return nomenclature.get();
    }

    public void setNomenclature(String nomenclature) {
        this.nomenclature.set(nomenclature);
    }

    public String getMeasure() {
        return measure.get();
    }

    public void setMeasure(String measure) {
        this.measure.set(measure);
    }

    public IntegerProperty idProperty() {
        return id;
    }
    
    public StringProperty nomenclatureProperty() {
        return nomenclature;
    }
    
    public StringProperty measureProperty() {
        return measure;
    }
    
}
