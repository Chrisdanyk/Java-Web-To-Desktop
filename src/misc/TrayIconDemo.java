package misc;

/*
 * TrayIconDemo.java
 */
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.net.URL;
import javax.swing.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.embeddable.*;

public class TrayIconDemo {

    static TrayIcon trayIcon = new TrayIcon(createImage("images/tray.gif", "Gestion Stock"));

    public static void main(String[] args) {
        /* Use an appropriate Look and Feel */
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            //UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException | ClassNotFoundException ex) {
        }
        /* Turn off metal's use of bold fonts */
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        //Schedule a job for the event-dispatching thread:
        //adding TrayIcon.
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                trayIcon.setImageAutoSize(true);
                createAndShowGUI();
                //type = TrayIcon.MessageType.INFO;
                trayIcon.displayMessage("Gestion de Stock",
                        "L'application est en cours de démarrage", TrayIcon.MessageType.WARNING);

                //creating and starting server 
                GlassFishProperties glassfishProperties = new GlassFishProperties();
                
                

                // Do not use setPort if you are using setInstanceRoot or setConfigFileURI. These methods set the port indirectly.
                glassfishProperties.setPort("http-listener", 10);
                glassfishProperties.setPort("https-listener", 11);
                try {

                    GlassFish glassfish = GlassFishRuntime.bootstrap().newGlassFish(glassfishProperties);
                    glassfish.start();

                    //deploying war
                    File war = new File("c:\\myapp\\Web.war");
                    Deployer deployer = glassfish.getDeployer();
                    deployer.deploy(war, "--name=GestionStock", "--contextroot=GestionStock", "--force=true");
                    // deployer.deploy(war) can be invoked instead. Other parameters are optional.

                    //use below code to undeploy
                    //  deployer.undeploy(war, "--droptables=true", "--cascade=true");
                    //creating jdbc-ressource-  in embebbed asadmin command
//        String command = "create-jdbc-resource";
//        String poolid = "--connectionpoolid=DerbyPool";
//        String dbname = "jdbc/DerbyPool";
//        CommandRunner commandRunner = glassfish.getCommandRunner();
//        CommandResult commandResult = commandRunner.run(command, poolid, dbname);
                    trayIcon.setImage(createImage("images/tray1.gif", "Gestion Stock"));

                    System.out.println("The server has started: http port :10 https port :11 ");
                    trayIcon.displayMessage("Gestion de Stock",
                            "L'application a démarrée", TrayIcon.MessageType.INFO);
                    openWebpage(new URL("http://localhost:10//GestionStock"));

                } catch (Exception e) {
                    String chemin = "C:\\Users\\" + System.getProperty("user.name") + "\\log_gestionStock.txt";
                    trayIcon.displayMessage("Gestion de Stock",
                            "Une erreur empêche le programme de démarrer, Lisez le log à l'emplacemenent " + chemin, TrayIcon.MessageType.ERROR);
                    try {
                        File file = new File(chemin);
                        FileReader fileReader = new FileReader(file);
                        BufferedReader bufferedReader = new BufferedReader(fileReader);
                        String line = "", content = "";
                        while ((line = bufferedReader.readLine()) != null) {
                            content += line;
                        }
                        FileWriter fileWriter = new FileWriter(file);

                        fileWriter.write(content + " \n \n log du " + new SimpleDateFormat("dd-MM-yyyy à hh:mm:ss").format(new Date()) + "\n-----------------------------" + " \n " + e.getMessage());
                        fileWriter.close();
                        fileReader.close();
                        bufferedReader.close();
                    } catch (IOException ex) {
                        Logger.getLogger(TrayIconDemo.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }

            }
        });
    }

    private static void createAndShowGUI() {
        //Check the SystemTray support
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }
        final PopupMenu popup = new PopupMenu();

        final SystemTray tray = SystemTray.getSystemTray();

        // Create a popup menu components
        MenuItem HomeItem = new MenuItem("Page d'Accueil");
        MenuItem aboutItem = new MenuItem("A propos");
        CheckboxMenuItem cb1 = new CheckboxMenuItem("Set auto size");
        CheckboxMenuItem cb2 = new CheckboxMenuItem("Set tooltip");
        Menu displayMenu = new Menu("Afficher");
        MenuItem errorItem = new MenuItem("Error");
        MenuItem warningItem = new MenuItem("Warning");
        MenuItem infoItem = new MenuItem("Info");
        MenuItem noneItem = new MenuItem("None");
        MenuItem exitItem = new MenuItem("Quitter");

        //Add components to popup menu
        popup.add(HomeItem);
        popup.addSeparator();
        popup.add(aboutItem);

//        popup.add(cb1);
//        popup.add(cb2);
//        popup.addSeparator();
//        popup.add(displayMenu);
//        displayMenu.add(errorItem);
//        displayMenu.add(warningItem);
//        displayMenu.add(infoItem);
//        displayMenu.add(noneItem);
        popup.add(exitItem);

        trayIcon.setPopupMenu(popup);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
            return;
        }

        trayIcon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,
                        "Gestion de stock s'execute dans votre navigateur, \n "
                        + "Veillez ouvrir la page d'acceuil");
            }
        });

        aboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,
                        "Gestion de Stock  version 1.0.0 \n"
                        + "licence Accordée à...."
                        + "\n Tout droit reservé, Février 2016 ");
            }
        });
        HomeItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                try {
                    openWebpage(new URL("http://localhost:10//GestionStock"));
                } catch (MalformedURLException ex) {
                    Logger.getLogger(TrayIconDemo.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });

        cb1.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                int cb1Id = e.getStateChange();
                if (cb1Id == ItemEvent.SELECTED) {
                    trayIcon.setImageAutoSize(true);
                } else {
                    trayIcon.setImageAutoSize(false);
                }
            }
        });

        cb2.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                int cb2Id = e.getStateChange();
                if (cb2Id == ItemEvent.SELECTED) {
                    trayIcon.setToolTip("Sun TrayIcon");
                } else {
                    trayIcon.setToolTip(null);
                }
            }
        });

        ActionListener listener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MenuItem item = (MenuItem) e.getSource();
                //TrayIcon.MessageType type = null;
                System.out.println(item.getLabel());
                if ("Error".equals(item.getLabel())) {
                    //type = TrayIcon.MessageType.ERROR;
                    trayIcon.displayMessage("Sun TrayIcon Demo",
                            "This is an error message", TrayIcon.MessageType.ERROR);

                } else if ("Warning".equals(item.getLabel())) {
                    //type = TrayIcon.MessageType.WARNING;
                    trayIcon.displayMessage("Sun TrayIcon Demo",
                            "This is a warning message", TrayIcon.MessageType.WARNING);

                } else if ("Info".equals(item.getLabel())) {
                    //type = TrayIcon.MessageType.INFO;
                    trayIcon.displayMessage("Sun TrayIcon Demo",
                            "This is an info message", TrayIcon.MessageType.INFO);

                } else if ("None".equals(item.getLabel())) {
                    //type = TrayIcon.MessageType.NONE;
                    trayIcon.displayMessage("Sun TrayIcon Demo",
                            "This is an ordinary message", TrayIcon.MessageType.NONE);
                }
            }
        };

        errorItem.addActionListener(listener);
        warningItem.addActionListener(listener);
        infoItem.addActionListener(listener);
        noneItem.addActionListener(listener);

        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tray.remove(trayIcon);

                System.exit(0);
            }
        });
    }

    //Obtain the image URL
    protected static Image createImage(String path, String description) {
        URL imageURL = TrayIconDemo.class.getResource(path);

        if (imageURL == null) {
            System.err.println("Resource not found: " + path);
            return null;
        } else {
            return (new ImageIcon(imageURL, description)).getImage();
        }
    }

    //opening the webapp in the browser
    public static void openWebpage(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void openWebpage(URL url) {
        try {
            openWebpage(url.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

}
