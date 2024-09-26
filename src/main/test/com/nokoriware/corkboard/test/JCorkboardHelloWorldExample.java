package com.nokoriware.corkboard.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.nokoriware.corkboard.Connection;
import com.nokoriware.corkboard.CorkboardJSONImporter;
import com.nokoriware.corkboard.CorkboardProject;
import com.nokoriware.corkboard.Node;
import com.nokoriware.corkboard.Node.LabelType;

/**
 * This basic program will allow you to select an Corkboard JSON export, load it, and interact with it via the console.
 */
public class JCorkboardHelloWorldExample {
	
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
    			
    			System.out.println("Corkboard Project successfully imported and processed.");

    			basicDialogueProgram(project);
    			
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    		
        } else {
        	System.out.println("No file selected. Terminating program.");
        	System.exit(1);
        }
	}
	
	public static void basicDialogueProgram(CorkboardProject project) {
		
		/*
		 * Welcome text
		 */
		
		System.out.println("Beginning dialogue test."
				+ "\n\nThe selected project is \"" + project.getName() + ".\""
				+ "\nThe dialogue will begin at the Starting Element.\n");
		
		/*
		 * Begin dialogue
		 */
		
		Scanner scanner = new Scanner(System.in);
		
		//Get starting node
		Node currentNode = project.getStartingNode();
		
		if (currentNode != null) {
			System.out.println("Starting Node: " + currentNode.getLabel());
		} else {
			System.out.println("Project has no starting node and will not work with this example project.");
		}
		
		/*
		 * Play dialogue
		 */
		
		playDialogue(scanner, currentNode);
		
		/*
		 * Close Program
		 */
		
		System.out.println("Terminating program.");
		scanner.close();
		System.exit(0);
	}
	
	private static void playDialogue(Scanner scanner, Node currentNode) {
		
		while (currentNode != null) {
			
			//Print dialogue
			if (currentNode.hasBody() && !currentNode.getBody().isBlank()) {
				System.out.println("\"" + currentNode.getBody() + "\"");
			}
			
			ArrayList<Connection> options = currentNode.getTargetConnectionsByLabel(LabelType.LABELLED);
			
			//If the connections contain labels, we'll print them as selectable options.
			if (options.size() > 0) {
				
				System.out.println();
				
				for (int i = 0; i < options.size(); i++) {
					System.out.println(i + ": " + options.get(i).getLabel());
				}
				
				//Obtain user response
				int response = obtainUserResponse(scanner);
				
				if (response >= 0 && response < options.size()) {
					Connection selected = options.get(response);
					
					System.out.println("\n>" + selected.getLabel() + ".");
					
					//Proceed to next element
					currentNode = selected.getTarget();
					
				} else {
					System.err.println("Please input an available response number.\n");
				}

				
			} else {
				
				//Otherwise if there are no responses available, we'll skip straight to the next element.
				ArrayList<Connection> outputs = currentNode.getTargetConnections();
				
				//Warning in case there are unlabelled connection outputs
				if (currentNode.getTargetConnectionsByLabel(LabelType.UNLABELLED).size() > 1) {
					System.err.println("Warning: There are only unlabelled connections present in \"" + currentNode.getLabel() + "\". The next node will be the first detected target.");
				}
				

				
				if (outputs.size() > 0) {

					//Proceed to next element automatically, if available
					currentNode = outputs.get(0).getTarget();

				} else {
					
					//If there's no output connections, see if it's a jumper. If there's no jumper, it'll just end up as null (end of conversation).
					currentNode = currentNode.getJumperTarget();
					

				}

			}
		}
		
		System.out.println("\nEnd of dialogue.\n");
		
	}
	
	private static int obtainUserResponse(Scanner scanner) {
		System.out.println("\nType the corresponding number of the response you want to reply with:");
		String response = scanner.nextLine();
		
		try {
			
			int chosenOption = Integer.parseInt(response);
			return chosenOption;
			
		} catch (NumberFormatException e) {
			
			System.err.println("Invalid number.");
			return -1;
		}
	}

}
