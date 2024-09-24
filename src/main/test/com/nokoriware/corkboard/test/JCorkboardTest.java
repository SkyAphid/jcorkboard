package com.nokoriware.corkboard.test;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.nokoriware.corkboard.Connection;
import com.nokoriware.corkboard.CorkboardJSONImporter;
import com.nokoriware.corkboard.CorkboardProject;
import com.nokoriware.corkboard.Node;

public class JCorkboardTest {
	public static void main(String[] args) {
		System.out.println("Hello world. Select an Corkboard JSON file to test.");
		
		/*
		 * Set look and feel because I'm OCD
		 */
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/*
		 * Select the Corkboard file to test.
		 */
		
        JFileChooser chooser = new JFileChooser();
        
        chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
		
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Corkboard JSON Files", "json");
        chooser.setFileFilter(filter);
        
        int returnVal = chooser.showOpenDialog(null);
        
        if(returnVal == JFileChooser.APPROVE_OPTION) {
        	File f = chooser.getSelectedFile();
        	
        	/*
        	 * Run the program.
        	 */
        	
    		try {
    			CorkboardProject project = CorkboardJSONImporter.read(f);
    			
    			System.out.println("Corkboard Project successfully imported and processed.\n");

    			System.out.println("PROJECT:");
    			System.out.println(project.getName());
    			System.out.println(project.getViewportX());
    			System.out.println(project.getViewportY());
    			System.out.println(project.getViewportZoom());
    			
    			System.out.println();
    			
    			for (Node node : project.getNodes()) {
    				
    				System.out.println("NODE: " + node.getLabel());
    				
    				System.out.println(node.getID());
    				System.out.println(node.getX());
    				System.out.println(node.getY());
    				System.out.println(node.getWidth());
    				System.out.println(node.getHeight());
    				System.out.println(node.getBody());
    				System.out.println(node.getAttributes());
    				System.out.println(node.getComponents().size());
    				System.out.println(node.getConnections().size());
    				
        			System.out.println();
    			}
    			
    			for (Connection c : project.getConnections()) {
    				System.out.println("CONNECTION: " + c.getLabel());
    				
    				System.out.println(c.getSource().getLabel());
    				System.out.println(c.getTarget().getLabel());
    				
        			System.out.println();
    			}
    			
    			
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    		
        } else {
        	System.out.println("No file selected. Terminating program.");
        	System.exit(1);
        }
	}
}
