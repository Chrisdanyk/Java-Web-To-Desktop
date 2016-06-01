/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gestionstockserv;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import org.glassfish.embeddable.Deployer;
import org.glassfish.embeddable.GlassFish;
import org.glassfish.embeddable.GlassFishProperties;
import org.glassfish.embeddable.GlassFishRuntime;

/**
 *
 * @author D@niel Rub
 */
public class FromDomainXml {

    public static void main(String[] args) throws IOException {
        File configFile = new File("c:\\myapp\\embeddedserver\\domains\\domain1\\config\\domain.xml");
        File war = new File("c:\\samples\\simple.war");
        try {
            GlassFishRuntime glassfishRuntime = GlassFishRuntime.bootstrap();

            GlassFishProperties glassfishProperties = new GlassFishProperties();

            glassfishProperties.setConfigFileURI(configFile.toURI().toString());
            glassfishProperties.setConfigFileReadOnly(false);
            GlassFish glassfish = glassfishRuntime.newGlassFish(glassfishProperties);
            glassfish.start();
            Deployer deployer = glassfish.getDeployer();
            deployer.deploy(war, "--force=true");

            System.out.println("Press Enter to stop server");
// wait for Enter
            new BufferedReader(new java.io.InputStreamReader(System.in)).readLine();
            try {
                glassfish.dispose();
                glassfishRuntime.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
