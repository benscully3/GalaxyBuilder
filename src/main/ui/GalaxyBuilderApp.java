package ui;

import exceptions.BadCommandException;
import exceptions.NameAlreadyUsedException;
import exceptions.NegativeNumberException;
import model.*;
import persistence.JsonReader;
import persistence.JsonWriter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

import static java.lang.Double.parseDouble;

// Galaxy builder application
public class GalaxyBuilderApp {
    private static final String JSON_STORE = "./data/galaxy.json";
    private Galaxy galaxy;
    private Scanner input;
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;

    // EFFECT: Runs galaxy builder application
    public GalaxyBuilderApp() {
        runGalaxyBuilder();
    }


    // EFFECT: Initialize app and open main page
    private void runGalaxyBuilder() {
        boolean keepGoing = true;
        String command;

        init();

        while (keepGoing) {
            displayMainMenu();
            command = input.next();
            command = command.toLowerCase();

            if (command.equals("q")) {
                keepGoing = false;
            } else {
                processMainCommand(command);
            }
        }
        System.out.println("\nGoodbye! Thanks for playing!");
    }


    // MODIFIES: This
    // EFFECT: Initialize galaxy
    private void init() {
        String command;

        input = new Scanner(System.in);
        input.useDelimiter("\n");
        print("Name your galaxy! (You can change it later)\n");
        command = input.next();
        command = command.toLowerCase();
        galaxy = new Galaxy(command);
        jsonWriter = new JsonWriter(JSON_STORE);
        jsonReader = new JsonReader(JSON_STORE);
    }


    // EFFECT: Display the main menu
    private void displayMainMenu() {
        print("\n GALAXY: " + galaxy.getName());
        print(" ------");
        print("Select from:");
        print("\tn -> Add a new solar system!");
        if (galaxy.getSolarSystemCount() != 0) {
            print("\te -> Edit your solar systems!");
            print("\td -> Display your solar systems!");
        }
        print("\tc -> Change galaxy name");
        print("\ts -> Save galaxy");
        print("\tl -> Load galaxy");
        print("\tq -> Quit");
    }


    // MODIFIES: this
    // EFFECT: process command entered from main menu
    private void processMainCommand(String command) {
        if (command.equals("n")) {
            newSolarSystem();
        } else if (command.equals("c")) {
            changeGalaxyName();
        } else if (command.equals("e")) {
            if (galaxy.getSolarSystemCount() != 0) {
                editSolarSystems();
            } else {
                print("Selection not valid :(, try again");
            }
        } else if (command.equals("d")) {
            if (galaxy.getSolarSystemCount() != 0) {
                displayGalaxy();
            } else {
                print("Selection not valid :(, try again");
            }
        } else if (command.equals("s")) {
            saveGalaxy();
        } else if (command.equals("l")) {
            loadGalaxy();
        } else {
            print("Selection not valid :(, try again");
        }
    }

    // MODIFIES: galaxy
    // EFFECT: change the name of the galaxy
    private void changeGalaxyName() {
        print("\n Enter new galaxy name:");
        String newName = input.next();
        galaxy.changeName(newName);
        print("Changed galaxy name to " + newName);
    }

    // MODIFIES: galaxy
    // EFFECT: loads the galaxy saved to JSON_STORE into the galaxy field
    private void loadGalaxy() {
        try {
            galaxy = jsonReader.read();
            System.out.println("Loaded " + galaxy.getName() + " from " + JSON_STORE);
        } catch (IOException e) {
            System.out.println("Unable to read from file: " + JSON_STORE);
        }
    }

    // EFFECT: saves the current galaxy to JSON_STORE
    private void saveGalaxy() {
        try {
            jsonWriter.open();
            jsonWriter.write(galaxy);
            jsonWriter.close();
            System.out.println("Saved " + galaxy.getName() + " to " + JSON_STORE);
        } catch (FileNotFoundException e) {
            System.out.println("Unable to write to file: " + JSON_STORE);
        }
    }


    // EFFECT: Display galaxy and solar systems within it
    private void displayGalaxy() {
        String name = galaxy.getName();
        HashMap<String, SolarSystem> solarSystems = galaxy.getSolarSystems();
        boolean keepGoing = true;
        String command;

        print("\nYOUR GALAXY: " + name);
        print(" SOLAR SYSTEMS");
        for (SolarSystem solarSystem : solarSystems.values()) {
            displaySolarSystemInfo(solarSystem);
        }
        print("\nType 'back' (without the quotes) to exit to the main page");
        while (keepGoing) {
            command = input.next();
            if (command.equals("back")) {
                keepGoing = false;
            }
        }
    }


    // EFFECT: Display basic info of a solar system
    private void displaySolarSystemInfo(SolarSystem solarSystem) {
        String name = solarSystem.getName();
        String centralBodyType = solarSystem.getCentralBody().getCentralBodyType();
        String planetCount = Integer.toString(solarSystem.getPlanetCount());

        print("\nName: " + name);
        print("\tCentral body: " + centralBodyType);
        print("\tNumber of planets: " + planetCount);
    }


    // MODIFIES: this
    // EFFECT: User creates a new solar system
    private void newSolarSystem() {
        String name;
        CentralBody centralBody;
        SolarSystem solarSystem;
        boolean keepGoing = true;

        displayCentralBodies(true);
        centralBody = processCentralBodyCommand(true);

        while (keepGoing) {
            try {
                print("\nEnter a name for your solar system!:");
                name = input.next();
                name = name.toLowerCase();
                solarSystem = new SolarSystem(name, centralBody);
                galaxy.addSolarSystem(solarSystem);
                print("Congratulations! You made a solar system! Add planets in the edit solar systems page.");
                keepGoing = false;
            } catch (Exception e) {
                print("Looks like a solar system is named that already...");
            }
        }
    }


    // EFFECT: Processes what central body the user chose
    private CentralBody processCentralBodyCommand(boolean binary) {
        String command;

        while (true) {
            command = input.next();
            command = command.toLowerCase();
            if (command.equals("bh")) {
                return makeBlackHole();
            } else if (command.equals("ns")) {
                return makeNeutronStar();
            } else if (command.equals("wd")) {
                return makeWhiteDwarf();
            } else if (command.equals("gs")) {
                return makeGiantStar();
            } else if (command.equals("by")) {
                if (binary) {
                    print("Selection not valid :(, try again");
                } else {
                    return makeBinary();
                }
            } else {
                print("Selection not valid :(, try again");
            }
        }
    }


    // EFFECT: Makes a giant star as a central body
    private CentralBody makeGiantStar() {
        GiantStar giantStar;
        String name;
        double luminosity;

        print("\nName your star!:");
        name = input.next();
        name = name.toLowerCase();
        print("\nChoose a number for your star's brightness! "
                + "(entering 2 means twice as bright as the sun)");
        luminosity = inputPositiveNumber();
        giantStar = new GiantStar(name, luminosity);
        return giantStar;
    }


    // EFFECT: Makes a white dwarf star as a central body
    private CentralBody makeWhiteDwarf() {
        WhiteDwarf whiteDwarf;
        String name;
        double mass = 0;
        boolean keepGoing = true;

        print("\nName your star!:");
        name = input.next();
        name = name.toLowerCase();
        print("\nChoose a number for your star's mass! "
                + "(entering 2 means twice the mass of the sun)");
        print("\nWhite Dwarfs must have a mass under 1.4 solar masses");

        while (keepGoing) {
            mass = inputPositiveNumber();
            if (mass > 1.4) {
                print("Please enter a number under 1.4 :)");
            } else {
                keepGoing = false;
            }
        }
        whiteDwarf = new WhiteDwarf(name, mass);
        return whiteDwarf;
    }


    // EFFECT: Makes a neutron star as a central body
    private CentralBody makeNeutronStar() {
        NeutronStar neutronStar;
        String name;
        double mass = 0;
        boolean keepGoing = true;

        print("\nName your star!:");
        name = input.next();
        name = name.toLowerCase();
        print("\nChoose a number for your star's mass! "
                + "(entering 2 means twice the mass of the sun)");
        print("\n Neutron star mass must be between 1.4 and 2.5 solar masses");

        while (keepGoing) {
            mass = inputPositiveNumber();
            if ((mass < 1.4) || (mass > 2.5)) {
                print("Please enter a number between 1.4 and 2.5 :)");
            } else {
                keepGoing = false;
            }
        }
        neutronStar = new NeutronStar(name, mass);
        return neutronStar;
    }


    // EFFECT: Makes a black hole as a central body
    private CentralBody makeBlackHole() {
        BlackHole blackHole;
        String name;
        double mass = 0;
        boolean keepGoing = true;

        print("\nName your black hole!:");
        name = input.next();
        name = name.toLowerCase();
        print("\nChoose a number for your black hole's mass! "
                + "(entering 2 means twice the mass of the sun)");
        print("\nBlack Hole mass must be at least 2.5 solar masses!");

        while (keepGoing) {
            mass = inputPositiveNumber();
            if (mass < 2.5) {
                print("Please enter a number over 2.5 :)");
            } else {
                keepGoing = false;
            }
        }
        blackHole = new BlackHole(name, mass);
        return blackHole;
    }


    // EFFECT: Makes a binary system as a central body
    private CentralBody makeBinary() {
        Binary binary;
        CentralBody centralBody1;
        CentralBody centralBody2;
        String name;

        print("\nBinaries have two central bodies!");
        print("\nChoose your first central body!");
        displayCentralBodies(true);
        centralBody1 = processCentralBodyCommand(true);

        print("\nChoose your second central body!");
        displayCentralBodies(true);
        centralBody2 = processCentralBodyCommand(true);

        print("\nFinally name the binary!");
        name = input.next();
        binary = new Binary(name, centralBody1, centralBody2);
        return binary;
    }


    // EFFECT: Displays solar systems to edit and passes on user's choice
    private void editSolarSystems() {
        String command;
        boolean keepGoing = true;

        HashMap<String, SolarSystem> solarSystems = galaxy.getSolarSystems();
        print("\nChoose which solar system to edit");
        for (String solarSystemName : solarSystems.keySet()) {
            print("\t" + solarSystemName);
        }
        print("\tback = Back to main page");
        while (keepGoing) {
            command = input.next();
            command = command.toLowerCase();
            if (command.equals("back")) {
                keepGoing = false;
            } else {
                try {
                    processSolarSystemCommand(command, solarSystems.keySet());
                    keepGoing = false;
                } catch (BadCommandException e) {
                    print("\n That solar system doesn't exist :(, try again!");
                }
            }
        }
    }


    // EFFECT: processes the command to choose which solar system to edit
    private void processSolarSystemCommand(String command, Set<String> solarSystemNames) throws BadCommandException {
        for (String solarSystemName : solarSystemNames) {
            if (command.equals(solarSystemName)) {
                SolarSystem solarSystem = galaxy.getSolarSystem(solarSystemName);
                HashMap<String, Planet> planets = solarSystem.getPlanets();
                displayEditSolarSystem(planets, solarSystem.getPlanetCount(), solarSystem);
                return;
            }
        }
        throw new BadCommandException();
    }


    // EFFECT: depending on planet count, show how solar system can be edited
    private void displayEditSolarSystem(HashMap<String, Planet> planets, int planetCount, SolarSystem solarSystem) {
        boolean keepGoing = true;

        if (planetCount == 0) {
            print("\nAdd a planet!");
            addPlanet(solarSystem.getName());
        } else {
            displaySolarSystemOptions(solarSystem);
            while (keepGoing) {
                String command = input.next();
                command = command.toLowerCase();
                if (command.equals("back")) {
                    keepGoing = false;
                } else {
                    try {
                        processEditSolarSystemCommand(command, planets, solarSystem.getName());
                        keepGoing = false;
                    } catch (BadCommandException e) {
                        System.out.println("Selection not valid :(, try again");
                    }
                }
            }
        }
    }


    // EFFECT: display the options for editing a solar system
    private void displaySolarSystemOptions(SolarSystem solarSystem) {
        print("\nChoose what to do:");
        print("\ta -> Add a new planet");
        print("\tr -> Remove a planet");
        print("\td -> Display solar system data");
        if (solarSystem.getCentralBody().canSupernova()) {
            print("\ts -> Supernova");
        }
        print("\tback -> Back to main page");
    }


    // EFFECT: process command to edit a solar system
    private void processEditSolarSystemCommand(String command, HashMap<String, Planet> planets,
                                               String solarSystemName) throws BadCommandException {
        SolarSystem solarSystem = galaxy.getSolarSystem(solarSystemName);
        boolean certain;

        if (command.equals("a")) {
            addPlanet(solarSystemName);
        } else if (command.equals("r")) {
            removePlanet(planets, solarSystem);
        } else if (command.equals("d")) {
            displaySolarSystemData(solarSystemName, planets);
        } else if (command.equals("s")) {
            certain = checkCertain();
            if (certain) {
                supernova(solarSystem);
            }
        } else {
            throw new BadCommandException();
        }
    }


    // EFFECT: Checks if the user is certain they want a supernova
    private boolean checkCertain() {
        String command;

        print("\nAre you sure you want your star to go supernova?");
        print("It will destroy the solar system, leaving only a black hole.");
        print("\ty -> Yes, make it explode!");
        print("\tn -> No, on second thought, I'll pass");
        while (true) {
            command = input.next();
            if (command.equals("y")) {
                return true;
            } else if (command.equals("n")) {
                return false;
            }
        }
    }


    // MODIFIES: this
    // EFFECT: Causes a supernova - clears planets, central body becomes black hole
    private void supernova(SolarSystem solarSystem) {
        CentralBody centralBody;
        double centralBodyMass;
        String centralBodyName;
        String solarSystemName;

        solarSystemName = solarSystem.getName();
        galaxy.removeSolarSystem(solarSystemName);

        centralBody = solarSystem.getCentralBody();
        centralBodyMass = centralBody.getMass();
        centralBodyName = centralBody.getName();
        centralBody = new BlackHole(centralBodyName, centralBodyMass);

        solarSystem = new SolarSystem(solarSystemName, centralBody);
        try {
            galaxy.addSolarSystem(solarSystem);
        } catch (Exception e) {
            // add.SolarSystem throws error, but never will in this case - empty catch block
        }
        drawSupernova();
    }


    // EFFECT: Draw a supernova!
    private void drawSupernova() {
        print("-   * \\ . **   / *  -");
        print("   .  *\\**||*./*  *");
        print("*  -*-*-!BOOM!-*-*-  *");
        print("  *   */**||**\\*  *");
        print("- .*  /   -*   \\ .  -");
    }


    // EFFECT:  display solar system data: central body and planets
    private void displaySolarSystemData(String solarSystemName, HashMap<String, Planet> planets) {
        CentralBody centralBody;
        String command;
        boolean keepGoing = true;

        centralBody = galaxy.getSolarSystem(solarSystemName).getCentralBody();
        displayCentralBody(centralBody);
        print("\n PLANETS:");
        for (Planet p : planets.values()) {
            displayPlanet(p);
        }
        print("\nType 'back' (without the quotes) to exit to the main page");

        while (keepGoing) {
            command = input.next();
            command = command.toLowerCase();
            if (command.equals("back")) {
                keepGoing = false;
            }
        }
    }


    // EFFECT: display central body data
    private void displayCentralBody(CentralBody centralBody) {
        String name = centralBody.getName();
        String mass = String.format("%.2f", centralBody.getMass());
        String radius = String.format("%.2f", centralBody.getRadius());
        String centralBodyType = centralBody.getCentralBodyType();
        print("\n CENTRAL BODY:" + "\nName: " + name + " - " + centralBodyType);
        if (centralBodyType.equals("Binary")) {
            Binary castedBody = (Binary) centralBody;
            print("\t Composed of a " + castedBody.getCentralBody1().getCentralBodyType()
                    + " and a " + castedBody.getCentralBody2().getCentralBodyType());
        }
        print("\tMass: " + mass + " Solar masses");
        if (centralBodyType.equals("Giant Star")) {
            print("\tRadius: " + radius + " Solar radii");
        } else if (centralBodyType.equals("Binary")) {
            ;
        } else {
            print("\tRadius: " + radius + " kilometers");
        }
        if (centralBodyType.equals("Giant Star")) {
            GiantStar giantStar = (GiantStar) centralBody;
            String luminosity = String.format("%.2f", giantStar.getLuminosity());
            print("\tLuminosity: " + luminosity + " Solar luminosities");
        }
    }


    // EFFECT: Display planet data
    private void displayPlanet(Planet p) {
        String moon = "No moon";
        String planetType = "Gas giant planet";

        String name = p.getName();
        String radius = String.format("%.2f", p.getRadius());
        String orbit = String.format("%.2f", p.getOrbitSize());
        String mass = String.format("%.2f", p.getMass());
        if (p.isMoon()) {
            moon = "Has a moon";
        }
        if (p.isRocky()) {
            planetType = "Rocky planet";
        }

        print("\nName:" + name + " - " + planetType + " - " + moon);
        print("\t-Mass: " + mass + " Earth masses");
        print("\t-Radius: " + radius + " Earth radii");
        print("\t-Orbit Size: " + orbit + " Earth orbits");
    }


    // MODIFIES: SolarSystem
    // EFFECT: remove a planet from a solar system
    private void removePlanet(HashMap<String, Planet> planets, SolarSystem solarSystem) {
        boolean keepGoing = true;
        String command;
        Planet planet = null;

        displayRemovePlanet(planets);
        while (keepGoing) {
            command = input.next();
            command = command.toLowerCase();
            if (command.equals("back")) {
                return;
            } else {
                try {
                    planet = processPlanetCommand(command, planets);
                    keepGoing = false;
                } catch (BadCommandException e) {
                    print("That planet wasn't found :( Try again!");
                }
            }
        }
        solarSystem.removePlanet(planet.getName());
        print("Success! " + planet.getName() + " was removed.");
    }


    // EFFECT: display planets that you can remove
    private void displayRemovePlanet(HashMap<String, Planet> planets) {
        Set<String> planetNames = planets.keySet();
        print("\nChoose a planet to remove - !This cannot be undone!");
        for (String planetName : planetNames) {
            print("\t" + planetName);
        }
        print("\tback -> Back to main page");
    }


    // EFFECT: searches for planet to remove and returns its name if found, throws error otherwise
    private Planet processPlanetCommand(String command, HashMap<String, Planet> planets) throws BadCommandException {
        Set<String> planetNames = planets.keySet();
        for (String planetName : planetNames) {
            if (command.equals(planetName)) {
                return planets.get(planetName);
            }
        }
        throw new BadCommandException();
    }


    // MODIFIES: SolarSystem
    // EFFECT: add a planet to a solar system from user input
    private void addPlanet(String solarSystemName) {
        String planetName;
        boolean moon;
        double radius;
        double orbitSize;
        Planet planet;
        boolean collide;

        SolarSystem solarSystem = galaxy.getSolarSystem(solarSystemName);
        print("\nName your planet!");
        planetName = namePlanet(solarSystem);

        print("\nNow choose your planet's size:" + "\n\t(Entering 2 is twice as big as Earth)");
        radius = inputPositiveNumber();
        print("\nAnd now choose your planet's orbit:" + "\n\t(Again, 2 is twice the orbit size of Earth)");
        orbitSize = inputPositiveNumber();
        print("\nFinally, would you like your planet to have a moon?" + "\n\ty -> yes" + "\n\tn -> no");
        moon = processMoonCommand();

        planet = new Planet(planetName, radius, orbitSize, moon);
        collide = solarSystem.addPlanet(planet);

        madePlanet(collide, radius);
    }


    // EFFECT: display message depending on what happened with newly made planet
    private void madePlanet(boolean collide, double radius) {
        if (collide) {
            print("\nOh! The planet you added had the same orbit as a pre-existing one!");
            print("They collided, and combined! The new planet has the same name as the old one.");
            print("The collision also gave the planet a moon!");
            print("Look in Edit Solar Systems -> Solar System -> Display solar system data to see more!");
        } else if (radius > 5) {
            print("\nBased on the size you entered, a new Gas Giant planet has been added!");
        } else {
            print("\nBased on the size you entered, a new rocky planet has been added!");
        }
    }


    // EFFECT:  name a planet, won't accept duplicate names in a solar system
    private String namePlanet(SolarSystem solarSystem) {
        boolean keepGoing = true;
        String planetName = "";

        while (keepGoing) {
            planetName = input.next();
            planetName = planetName.toLowerCase();
            try {
                checkPlanetName(planetName, solarSystem.getPlanets());
                keepGoing = false;
            } catch (NameAlreadyUsedException e) {
                print("\nThis solar system has a planet with that name already. Pick a different name");
            }
        }
        return planetName;
    }


    // EFFECT: process whether user wants a moon for a planet
    private boolean processMoonCommand() {
        String command;

        while (true) {
            command = input.next();
            command = command.toLowerCase();
            if (command.equals("y")) {
                return true;
            } else if (command.equals("n")) {
                return false;
            } else {
                print("Please enter y or n :)");
            }
        }
    }


    // EFFECT: check that a planet doesn't already have a given name in a solar system
    private void checkPlanetName(String searchPlanetName, HashMap<String, Planet> planets)
            throws NameAlreadyUsedException {
        Set<String> planetNames = planets.keySet();
        for (String planetName : planetNames) {
            if (planetName.equals(searchPlanetName)) {
                throw new NameAlreadyUsedException();
            }
        }
    }


    // EFFECT: display options to choose from for a central body
    private void displayCentralBodies(boolean binary) {
        print("\nChoose a central body!:");
        print("\tbh -> Black Hole");
        print("\tns -> Neutron Star");
        print("\tgs -> Giant Star");
        print("\twd -> White Dwarf");
        if (!binary) {
            print("\tby -> Binary (You will choose two more central bodies)");
        }
    }


    // EFFECT: has the user input a positive number
    private double inputPositiveNumber() {
        boolean keepGoing = true;
        double number = 0;

        while (keepGoing) {
            try {
                number = parseDouble(input.next());
                if (number <= 0) {
                    throw new NegativeNumberException();
                }
                keepGoing = false;
            } catch (Exception e) {
                print("Please enter a positive number >:O");
            }
        }
        return number;
    }


    // EFFECT: prints a given string, easier to write than System.out.println()
    private void print(String println) {
        System.out.println(println);
    }
}
