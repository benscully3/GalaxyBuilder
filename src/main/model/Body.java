package model;

public abstract class Body {
    protected double mass;
    protected double radius;
    protected String name;

    // MODIFIES: this
    // EFFECT: change the galaxies name
    public void changeName(String newName){
        this.name = newName;
    }

    public double getMass(){

        return mass;
    }
    public double getRadius(){

        return this.radius;
    }

    public String getName() {
        return this.name;
    }
}
