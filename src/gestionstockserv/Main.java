/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gestionstockserv;

import java.io.File;
import org.glassfish.embeddable.*;

/**
 *
 * @author D@niel Rub
 */
public class Main {

    public static void main(String[] args) throws GlassFishException {

        GlassFishProperties glassfishProperties = new GlassFishProperties();

        // Do not use setPort if you are using setInstanceRoot or setConfigFileURI. These methods set the port automatically.
        glassfishProperties.setPort("http-listener", 10);
        glassfishProperties.setPort("https-listener", 11);
        GlassFish glassfish = GlassFishRuntime.bootstrap().newGlassFish(glassfishProperties);
        glassfish.start();

        //deploying war
        File war = new File("c:\\simple.war");
        Deployer deployer = glassfish.getDeployer();
        deployer.deploy(war, "--name=simple", "--contextroot=simple", "--force=true");
        // deployer.deploy(war) can be invoked instead. Other parameters are optional.

        //use below code to undeploy
        //  deployer.undeploy(war, "--droptables=true", "--cascade=true");
        //creating jdbc-ressource-  in embebbed asadmin command
//        String command = "create-jdbc-resource";
//        String poolid = "--connectionpoolid=DerbyPool";
//        String dbname = "jdbc/DerbyPool";
//        CommandRunner commandRunner = glassfish.getCommandRunner();
//        CommandResult commandResult = commandRunner.run(command, poolid, dbname);


    }

}
