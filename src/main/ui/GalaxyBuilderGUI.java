package ui;

import exceptions.BadCommandException;
import exceptions.CancelException;
import exceptions.NameAlreadyUsedException;
import exceptions.NegativeNumberException;
import model.*;
import model.Event;
import persistence.JsonReader;
import persistence.JsonWriter;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Iterator;

import static java.lang.Double.parseDouble;

// Galaxy Builder GUI application
public class GalaxyBuilderGUI extends JFrame implements ListSelectionListener, ActionListener {
    private static final String JSON_STORE = "./data/galaxy.json";
    private static final int WIDTH = 1000;
    private static final int HEIGHT = 600;
    private Galaxy galaxy;
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;

    private final JSplitPane splitPaneTop;
    private final JSplitPane splitPane;

    private DefaultListModel solarSystemsModel;
    private JList solarSystems;


    // MODIFIES: this
    // EFFECT: sets up and formats main GUI frame and buttons
    /**
     * Citation: addWindowListener to prompt eventLog to print to console
     * code format taken from
     */
    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    public GalaxyBuilderGUI() {
        JFrame frame = new JFrame();
        init();
        Dimension minSize = new Dimension(200, 150);

        // set up three panes in the window
        JScrollPane solarSystemPane = makeSolarSystemPane();
        JScrollPane imagePane = makeImagePane();
        JPanel buttonsPane = addButtonsPane();
        //JScrollPane buttonsPane = addButtonsPane();

        splitPaneTop = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                solarSystemPane, imagePane);
        splitPaneTop.setDividerLocation(400);
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                splitPaneTop, buttonsPane);

        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(375);

        splitPane.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        solarSystemPane.setMinimumSize(minSize);
        buttonsPane.setMinimumSize(minSize);
        imagePane.setMinimumSize(minSize);
        frame.setMinimumSize(minSize);
        frame.add(splitPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("My Galaxy Builder");
        frame.pack();
        frame.setVisible(true);

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                printLog();
                System.exit(0);
            }
        });
    }

    private void printLog() {
        Iterator<Event> events = EventLog.getInstance().iterator();

        events.forEachRemaining((event) -> printEvent(event));

    }

    private void printEvent(Event event) {
        String eventString = event.toString();
        System.out.println("\n" + eventString);
    }


    // MODIFIES: this
    // EFFECT: initializes the galaxy and reader/writer
    private void init() {
        String name;
        name = JOptionPane.showInputDialog(null,
                "Name your galaxy! \n (You can change this later)",
                "Name galaxy",
                JOptionPane.QUESTION_MESSAGE);

        if (name != null) {
            galaxy = new Galaxy(name);
        } else {
            galaxy = new Galaxy("Default");
        }
        jsonWriter = new JsonWriter(JSON_STORE);
        jsonReader = new JsonReader(JSON_STORE);
    }


    // EFFECT: creates buttons to add, and edit solar systems,
    //         display data, change galaxy name, and save and load
    //         files
    //         IF: there are solar systems present, all buttons are enabled
    //         ELSE: edit and display solar system buttons are not enabled
    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    private JPanel addButtonsPane() {
        boolean hasSolarSystems = galaxy.getSolarSystemCount() != 0;

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 3));

        JButton addSolarSystemButton = new JButton(new AddSolarSystemAction());
        JButton editButton = new JButton(new EditSolarSystemsAction());
        JMenuBar displayMenuBar = new JMenuBar();
        JButton changeNameButton = new JButton(new ChangeGalaxyNameAction());
        JButton saveButton = new JButton(new SaveAction());
        JButton loadButton = new JButton(new LoadAction());

        JMenu menu = new JMenu("Solar System data");
        displayMenuBar.add(menu);
        buildSolarSystemMenu(menu);

        buttonPanel.add(addSolarSystemButton);
        buttonPanel.add(editButton);
        buttonPanel.add(displayMenuBar);
        buttonPanel.add(changeNameButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);

        if (hasSolarSystems) {
            editButton.setEnabled(true);
            displayMenuBar.setVisible(true);
            displayMenuBar.setEnabled(true);
        } else {
            editButton.setEnabled(false);
            displayMenuBar.setVisible(false);
            displayMenuBar.setEnabled(false);
        }
        return buttonPanel;
    }


    // MODIFIES: menu
    // EFFECT: builds a menu of all the solar systems in the galaxy
    private void buildSolarSystemMenu(JMenu menu) {
        JMenuItem menuItem;

        for (SolarSystem s : galaxy.getSolarSystems().values()) {
            menuItem = new JMenuItem(s.getName());
            menuItem.addActionListener(this);
            menu.add(menuItem);
        }
    }


    // EFFECT: Creates the pane that displays which solar systems
    //         are in your galaxy
    private JScrollPane makeSolarSystemPane() {
        solarSystemsModel = new DefaultListModel();
        updateSolarSystems();
        solarSystems = new JList(solarSystemsModel);

        solarSystems.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        solarSystems.setSelectedIndex(0);
        solarSystems.addListSelectionListener(this);

        return new JScrollPane(solarSystems);
    }


    // EFFECT: Update the solar systems which are displayed in solar system pane
    private void updateSolarSystems() {
        solarSystemsModel.clear();
        for (SolarSystem solarSystem : galaxy.getSolarSystems().values()) {
            String solarSystemString = solarSystem.getName() + "  -:-  Central Body: "
                    + solarSystem.getCentralBody().getCentralBodyType() + "  -:-  # of planets: "
                    + solarSystem.getPlanetCount();
            solarSystemsModel.addElement(solarSystemString);
        }
    }


    // EFFECT: create the pane with the galaxy name and image
    private JScrollPane makeImagePane() {

        ImageIcon icon = new ImageIcon("./data/galaxy.jpg");
        JLabel galaxyImage = new JLabel(icon);
        JLabel galaxyName = new JLabel("The " + galaxy.getName() + " galaxy!");
        galaxyName.setFont(new Font("Serif", Font.BOLD, 28));
        galaxyName.setForeground(Color.white);
        JPanel background = new JPanel();
        background.setPreferredSize(new Dimension(50, 50));
        background.setBackground(Color.black);
        background.add(galaxyName);
        background.add(galaxyImage);

        return new JScrollPane(background);
    }


    // MODIFIES: this
    // EFFECT: updates the galaxy name in the image by remaking the JPanel

    /**
     * Citation: Method of updating component of JSplitPane from
     * Wolfgang Fahl on stack exchange
     * (Question Title: How dynamically change components in JSplitPane)
     */
    private void updateNameLabel(JComponent oldComponent) {
        JSplitPane parent = (JSplitPane) oldComponent.getParent();
        int dividerLocation = parent.getDividerLocation();
        parent.remove(oldComponent);

        JScrollPane newChild = makeImagePane();

        parent.add(newChild);
        parent.setDividerLocation(dividerLocation);
        newChild.revalidate();
        newChild.repaint();
    }

    // MODIFIES: this
    // EFFECT: update the button pane by remaking the pane
    private void updateButtons(JComponent oldComponent) {
        JSplitPane parent = (JSplitPane) oldComponent.getParent();
        int dividerLocation = parent.getDividerLocation();
        parent.remove(oldComponent);

        //JScrollPane newChild = addButtonsPane();
        JPanel newChild = addButtonsPane();

        parent.add(newChild);
        parent.setDividerLocation(dividerLocation);
        newChild.revalidate();
        newChild.repaint();

    }

    // EFFECT: gets input from user in the form of a string
    //         IF: the user closes or cancels the input window
    //             throw CancelException
    private String getStringInput(String s) throws CancelException {
        String input;
        input = JOptionPane.showInputDialog(null,
                s,
                "User input",
                JOptionPane.QUESTION_MESSAGE);
        if (input != null) {
            return input;
        } else {
            throw new CancelException();
        }
    }


    // EFFECT: gets input from user in the form of a string and tries to convert
    //         it to a double between a minimum and maximum bound
    //         IF: a non-double is input, throw NumberFormatException (from parseDouble)
    //         IF: double is out of min/max bounds throw BadCommandException
    //         IF: the user closes or cancels the input window
    //             throw CancelException
    private double getPositiveDoubleInput(String s, double minValue, double maxValue)
            throws NumberFormatException, BadCommandException, NegativeNumberException, CancelException {
        String input;
        double doubleInput;
        input = JOptionPane.showInputDialog(null,
                s,
                "User input",
                JOptionPane.QUESTION_MESSAGE);
        if (input == null) {
            throw new CancelException();
        }
        doubleInput = parseDouble(input);
        if (doubleInput < 0) {
            throw new NegativeNumberException();
        }
        if ((doubleInput < minValue) || (doubleInput > maxValue)) {
            throw new BadCommandException();
        }

        return doubleInput;
    }


    // EFFECT: parse the central body choice of the user
    //         0 => Giant star     1 => White Dwarf
    //         2 => Neutron star   3 => Black Hole
    //         IF: the user cancels or closes window throw CancelException
    private int getCentralBodyTypeInput() throws CancelException {
        String[] options = new String[]{"Giant Star", "White Dwarf", "Neutron Star", "Black Hole"};
        int response = JOptionPane.showOptionDialog(null, "Choose a central body",
                "Central bodies", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, options, options[0]);
        if (response != -1) {
            return response;
        } else {
            throw new CancelException();
        }
    }


    // EFFECTS: creates a central body based on the central body type passed to it
    //          can create a giant star, white dwarf, neutron star or black hole
    private CentralBody makeCentralBody(int centralBodyType) throws CancelException {
        CentralBody centralBody = null;
        switch (centralBodyType) {
            case 0:
                centralBody = makeGiantStar();
                break;
            case 1:
                centralBody = makeWhiteDwarf();
                break;
            case 2:
                centralBody = makeNeutronStar();
                break;
            case 3:
                centralBody = makeBlackHole();
                break;
        }
        return centralBody;
    }


    // EFFECT: makes a black hole based on user inout for name and mass
    //         if input windows are closed or canceled throw CancelException
    private BlackHole makeBlackHole() throws CancelException {
        String name;
        double mass = 0;
        boolean keepGoing = true;
        name = getStringInput("Name your black hole");
        while (keepGoing) {
            try {
                mass = getPositiveDoubleInput("Choose the black hole's mass. \n Mass must be above 2.5",
                        2.5, Double.POSITIVE_INFINITY);
                keepGoing = false;
            } catch (NumberFormatException n) {
                JOptionPane.showMessageDialog(null, "Please enter a number");
            } catch (NegativeNumberException e) {
                JOptionPane.showMessageDialog(null, "Please enter a positive mass");
            } catch (BadCommandException b) {
                JOptionPane.showMessageDialog(null, "Enter a mass over 2.5 please");
            }
        }
        return new BlackHole(name, mass);
    }


    // EFFECT: makes a neutron star based on user inout for name and mass
    //         if input windows are closed or canceled throw CancelException
    private NeutronStar makeNeutronStar() throws CancelException {
        String name;
        double mass = 0;
        boolean keepGoing = true;
        name = getStringInput("Name your neutron star");
        while (keepGoing) {
            try {
                mass = getPositiveDoubleInput("Choose the neutron star's mass. \n Mass must be between 1.4 and 2.5",
                        1.4, 2.5);
                keepGoing = false;
            } catch (NumberFormatException n) {
                JOptionPane.showMessageDialog(null, "Please enter a number");
            } catch (NegativeNumberException e) {
                JOptionPane.showMessageDialog(null, "Please enter a positive mass");
            } catch (BadCommandException b) {
                JOptionPane.showMessageDialog(null, "Enter a mass between 1.4 and 2.5 please");
            }
        }
        return new NeutronStar(name, mass);
    }


    // EFFECT: makes a white dwarf based on user inout for name and mass
    //         if input windows are closed or canceled throw CancelException
    private WhiteDwarf makeWhiteDwarf() throws CancelException {
        String name;
        double mass = 0;
        boolean keepGoing = true;
        name = getStringInput("Name your neutron star");
        while (keepGoing) {
            try {
                mass = getPositiveDoubleInput("Choose the white dwarf's mass. \n Mass must be below 1.4",
                        0, 1.4);
                keepGoing = false;
            } catch (NumberFormatException n) {
                JOptionPane.showMessageDialog(null, "Please enter a number");
            } catch (NegativeNumberException e) {
                JOptionPane.showMessageDialog(null, "Please enter a positive mass");
            } catch (BadCommandException b) {
                JOptionPane.showMessageDialog(null, "Enter a mass below 1.4");
            }
        }
        return new WhiteDwarf(name, mass);
    }


    // EFFECT: makes a giant star based on user inout for name and mass
    //         if input windows are closed or canceled throw CancelException
    private GiantStar makeGiantStar() throws CancelException {
        String name;
        double luminosity = 0;
        boolean keepGoing = true;
        name = getStringInput("Name your star");
        while (keepGoing) {
            try {
                luminosity = getPositiveDoubleInput("Choose the star's Luminosity. \n (Units of solar luminosity)",
                        0, Double.POSITIVE_INFINITY);
                keepGoing = false;
            } catch (NumberFormatException n) {
                JOptionPane.showMessageDialog(null, "Please enter a number");
            } catch (NegativeNumberException e) {
                JOptionPane.showMessageDialog(null, "Please enter a positive luminosity");
            } catch (BadCommandException b) {
                JOptionPane.showMessageDialog(null, "n/a");
            }
        }
        return new GiantStar(name, luminosity);
    }


    // EFFECT: open a new frame which displays the solar system's data
    //         in the form of a tree
    //         data includes central body and planets
    private void displaySolarSystemData(SolarSystem solarSystem) {
        JFrame solarSystemDataFrame = new JFrame("Solar System Data");
        CentralBody centralBody = solarSystem.getCentralBody();
        ArrayList<Planet> planets = new ArrayList<>(solarSystem.getPlanets().values());
        int planetCount = solarSystem.getPlanetCount();

        DefaultMutableTreeNode topNode = new DefaultMutableTreeNode("Solar System: "
                + solarSystem.getName());

        DefaultMutableTreeNode centralBodyNode = makeCentralBodyNode(centralBody);
        DefaultMutableTreeNode planetsNode = makePlanetsNode(planets, planetCount);

        topNode.add(centralBodyNode);
        topNode.add(planetsNode);

        JTree tree = new JTree(topNode);
        JScrollPane scrollPane = new JScrollPane(tree);

        solarSystemDataFrame.add(scrollPane);
        solarSystemDataFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        solarSystemDataFrame.setPreferredSize(new Dimension(WIDTH / 2, HEIGHT / 2));
        solarSystemDataFrame.setMinimumSize(new Dimension(300, 200));
        solarSystemDataFrame.pack();
        solarSystemDataFrame.setVisible(true);
    }

    // EFFECT: creates a node for a tree containing all the planets that are passed to it
    private DefaultMutableTreeNode makePlanetsNode(ArrayList<Planet> planets, int planetCount) {
        DefaultMutableTreeNode planetsNode = new DefaultMutableTreeNode("Planets: ("
                + planetCount + " planets)");

        for (Planet p : planets) {
            makePlanetNode(p, planetsNode);
        }
        return planetsNode;
    }

    // EFFECT: creates a node for a planet with all the planet's data
    //         (type, mass, radius, orbit, if it has a moon)
    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    private void makePlanetNode(Planet p, DefaultMutableTreeNode planetsNode) {
        DefaultMutableTreeNode planetNode = new DefaultMutableTreeNode(p.getName());
        DefaultMutableTreeNode planetTypeNode;
        DefaultMutableTreeNode planetMoonNode;
        if (p.isRocky()) {
            planetTypeNode = new DefaultMutableTreeNode("Type: Rocky Planet");
        } else {
            planetTypeNode = new DefaultMutableTreeNode("Type: Gas Giant");
        }
        DefaultMutableTreeNode planetMassNode = new DefaultMutableTreeNode("Mass: "
                + String.format("%.2f", p.getMass()) + " Earth masses");
        DefaultMutableTreeNode planetRadiusNode = new DefaultMutableTreeNode("Radius: "
                + String.format("%.2f", p.getRadius()) + " Earth radii");
        DefaultMutableTreeNode planetOrbitNode = new DefaultMutableTreeNode("Orbit size: "
                + String.format("%.2f", p.getOrbitSize()) + " Earth orbit radii");
        if (p.isMoon()) {
            planetMoonNode = new DefaultMutableTreeNode("Has a moon");
        } else {
            planetMoonNode = new DefaultMutableTreeNode("No moons");
        }
        planetNode.add(planetTypeNode);
        planetNode.add(planetMassNode);
        planetNode.add(planetRadiusNode);
        planetNode.add(planetOrbitNode);
        planetNode.add(planetMoonNode);
        planetsNode.add(planetNode);
    }


    // EFFECT: creates a node for a central body with all the central body's data
    //         ( mass, radius, luminosity)
    private DefaultMutableTreeNode makeCentralBodyNode(CentralBody centralBody) {
        DefaultMutableTreeNode centralBodyNode = new DefaultMutableTreeNode("Central Body: "
                + centralBody.getName() + " - " + centralBody.getCentralBodyType());
        DefaultMutableTreeNode centralBodyMassNode = new DefaultMutableTreeNode("Mass: "
                + String.format("%.2f", centralBody.getMass()) + " Solar masses");

        centralBodyNode.add(centralBodyMassNode);

        if (centralBody.getCentralBodyType().equals("Giant Star")) {
            GiantStar giantStar = (GiantStar) centralBody;
            DefaultMutableTreeNode giantStarRadiusNode = new DefaultMutableTreeNode("Radius: "
                    + String.format("%.2f", centralBody.getRadius()) + " Solar Radii");
            DefaultMutableTreeNode centralBodyLuminosityNode = new DefaultMutableTreeNode("Luminosity: "
                    + String.format("%.2f", giantStar.getLuminosity()) + " Solar Luminosities");
            centralBodyNode.add(giantStarRadiusNode);
            centralBodyNode.add(centralBodyLuminosityNode);
        } else {
            DefaultMutableTreeNode centralBodyRadiusNode = new DefaultMutableTreeNode("Radius: "
                    + String.format("%.2f", centralBody.getRadius()) + " km");
            centralBodyNode.add(centralBodyRadiusNode);
        }
        return centralBodyNode;
    }


    @Override
    public void valueChanged(ListSelectionEvent e) {
    }

    // EFFECT: displays a solar system's data if it is selected from the menu
    @Override
    public void actionPerformed(ActionEvent e) {
        JMenuItem source = (JMenuItem) (e.getSource());
        SolarSystem solarSystem = galaxy.getSolarSystem(source.getText());
        displaySolarSystemData(solarSystem);
    }


    /**
     * Represents action to be taken when user wants to add a new solar
     * system to the galaxy.
     */
    private class AddSolarSystemAction extends AbstractAction {

        AddSolarSystemAction() {
            super("New Solar System");
        }

        // EFFECT: builds a new solar system based on the user input
        //         prompts creation of a central body
        @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
        @Override
        public void actionPerformed(ActionEvent evt) {
            String name = null;
            CentralBody centralBody;
            boolean keepGoing = true;
            try {
                int centralBodyType = getCentralBodyTypeInput();
                centralBody = makeCentralBody(centralBodyType);
            } catch (CancelException c) {
                return;
            }
            while (keepGoing) {
                try {
                    name = getStringInput("Enter a name for the solar system \n Don't use a name you've used already!");
                    SolarSystem solarSystem = new SolarSystem(name, centralBody);
                    galaxy.addSolarSystem(solarSystem);
                    keepGoing = false;
                } catch (NameAlreadyUsedException n) {
                    JOptionPane.showMessageDialog(null, "Uh oh! Looks like that name has been used already");
                } catch (CancelException c) {
                    return;
                }
            }
            updateSolarSystems();
            updateButtons((JComponent) splitPane.getBottomComponent());
            JOptionPane.showMessageDialog(null, "Successfully added new solar system: " + name);
        }
    }


    /**
     * Represents action to be taken when user wants to add a new solar
     * system to the galaxy.
     */
    private class EditSolarSystemsAction extends AbstractAction {

        EditSolarSystemsAction() {
            super("Edit Solar Systems (Not yet implemented)");
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            //TODO: Implement if I have free time
        }
    }


    /**
     * Represents action to be taken when user wants to
     * change the galaxy's name.
     */
    private class ChangeGalaxyNameAction extends AbstractAction {

        ChangeGalaxyNameAction() {
            super("Change galaxy name");
        }

        // MODIFIES: this
        // EFFECT: prompts the user to enter a new name for the galaxy
        //         IF: something is entered, name is changed
        //         ELSE: nothing happens
        @Override
        public void actionPerformed(ActionEvent evt) {
            String newName;
            newName = JOptionPane.showInputDialog(null,
                    "Rename your galaxy!",
                    "Rename galaxy",
                    JOptionPane.QUESTION_MESSAGE);
            if (newName != null) {
                galaxy.changeName(newName);
                updateNameLabel((JComponent) splitPaneTop.getRightComponent());
                JOptionPane.showMessageDialog(null, "Changed galaxy name to " + newName);
            }
        }
    }


    /**
     * Represents action to be taken when user wants to
     * save their galaxy
     */
    private class SaveAction extends AbstractAction {

        SaveAction() {
            super("Save");
        }

        // EFFECT: save the current galaxy and all of its contents to JSON_STORE
        //         if file cannot be found, fails to save data
        @Override
        public void actionPerformed(ActionEvent evt) {
            try {
                jsonWriter.open();
                jsonWriter.write(galaxy);
                jsonWriter.close();
                JOptionPane.showMessageDialog(null, "Saved " + galaxy.getName() + " to " + JSON_STORE);

            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(null, "Unable to write to file: " + JSON_STORE);
            }
        }
    }

    /**
     * Represents action to be taken when user wants to add a new solar
     * system to the galaxy.
     */
    private class LoadAction extends AbstractAction {

        LoadAction() {
            super("Load");
        }


        // EFFECT: Loads a saved galaxy from JSON store
        //         if file location cannot be found, fails to load data
        @Override
        public void actionPerformed(ActionEvent evt) {
            try {
                galaxy = jsonReader.read();
                JOptionPane.showMessageDialog(null, "Loaded " + galaxy.getName() + " from " + JSON_STORE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Unable to read from file: " + JSON_STORE);
            }
            updateButtons((JComponent) splitPane.getBottomComponent());
            updateNameLabel((JComponent) splitPaneTop.getRightComponent());
            updateSolarSystems();
        }
    }
}


