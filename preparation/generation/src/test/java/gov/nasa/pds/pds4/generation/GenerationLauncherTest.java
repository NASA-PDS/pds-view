//*********************************************************************************/
//Copyright (C) NASA/JPL  California Institute of Technology.                     */
//PDS Imaging Node                                                                */
//All rights reserved.                                                            */
//U.S. Government sponsorship is acknowledged.                                    */
//******************************************************************* *************/
package gov.nasa.pds.pds4.generation;

import gov.nasa.pds.imaging.generation.GenerationLauncher;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;


import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

public class GenerationLauncherTest {
    
    /**
     * Test Transformer with MER data
     */
    @Ignore
    @Test
    public void testGenerationMER() {
        String[] args = {"-d", "-p","src/main/resources/examples/1p216067135edn76pop2102l2m1.img",
        		"-t","src/main/resources/examples/mer_template.vm"};
        GenerationLauncher.main(args);
    }

    /**
     * Test Transformer with Voyager Data
     */
    @Ignore
    @Test
    public void testGenerationVoyager() {
        String[] args = {"src/main/resources/voyager/c0903842.img", 
                "src/main/resources/voyager/voyager_mapping.xml",
                "src/main/resources/voyager/Product_Browse_0311B.vm"};
        GenerationLauncher.main(args);
    }
}
