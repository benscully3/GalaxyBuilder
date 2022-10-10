package model;

import exceptions.NameAlreadyUsed;

import java.util.HashMap;

// Class to represent a galaxy with SolarSystems and a name
public class Galaxy {
    private String name;
    private HashMap<String, SolarSystem> solarSystems;
    private int solarSystemCount;

    // EFFECT: construct a galaxy with no solar systems and a given name
    public Galaxy(String name) {
        this.name = name;
        solarSystemCount = 0;
        solarSystems = new HashMap<>();
    }

    // MODIFIES: this
    // EFFECT: adds a solar system to the galaxy
    //         check's if name has already been used and throws an error if it has
    public void addSolarSystem(SolarSystem solarSystem) throws NameAlreadyUsed {
        String solarSystemName = solarSystem.getName();
        
        if (solarSystems.get(solarSystemName) == null) {
            solarSystems.put(solarSystemName, solarSystem);
            solarSystemCount = solarSystemCount + 1;
        } else {
            throw new NameAlreadyUsed();
        }
    }

    // REQUIRES: solar system must be in galaxy
    // MODIFIES: this
    // EFFECT: remove a solar system from the galaxy based on name
    public void removeSolarSystem(String solarSystemName) {
        solarSystems.remove(solarSystemName);
        solarSystemCount = solarSystemCount - 1;
    }

    // MODIFIES: this
    // EFFECT: change the galaxies name
    public void changeName(String newName) {
        this.name = newName;
    }

    // EFFECT: return a solar system based on it's name
    public SolarSystem getSolarSystem(String solarSystemName) {
        return solarSystems.get(solarSystemName);
    }

    public HashMap<String, SolarSystem> getSolarSystems() {
        return solarSystems;
    }

    public int getSolarSystemCount() {
        return solarSystemCount;
    }

    public String getName() {
        return name;
    }


}
