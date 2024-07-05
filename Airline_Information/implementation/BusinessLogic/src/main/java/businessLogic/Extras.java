package businessLogic;

import dataRecords.ExtrasData;

public class Extras {
    private ExtrasData extrasData;

    public Extras(ExtrasData extrasData) {
        this.extrasData = extrasData;
    }

    public int getId() {
        return this.extrasData.id();
    }

    public String getDescription() {
        return this.extrasData.description();
    }

    public int getPrice() {
        return this.extrasData.price();
    }

    @Override
    public String toString() {
        return "ID: " + this.extrasData.id() + ", Description: " + this.extrasData.description() + ", Price: " + this.extrasData.price(); 
    }
}
