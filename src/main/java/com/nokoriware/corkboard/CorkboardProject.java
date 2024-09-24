package com.nokoriware.corkboard;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import com.nokoriware.corkboard.Element.ElementSearch;

public class CorkboardProject {
	
	private String name;
	private double viewportX, viewportY, viewportZoom;
	
	private ArrayList<Node> nodes;
	private ArrayList<Connection> connections;
	
	private Node startingNode;

	public CorkboardProject(String name, double viewportX, double viewportY, double viewportZoom, ArrayList<Node> nodes, ArrayList<Connection> connections) {
		this.name = name;
		this.viewportX = viewportX;
		this.viewportY = viewportY;
		this.viewportZoom = viewportZoom;
		this.nodes = nodes;
		this.connections = connections;
	}
	
	public static CorkboardProject importJSON(File corkboardJSONFile) throws FileNotFoundException {
		return CorkboardJSONImporter.read(corkboardJSONFile);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String projectName) {
		this.name = projectName;
	}

	/*
	 * 
	 * Viewport
	 * 
	 */
	
	public double getViewportX() {
		return viewportX;
	}

	public void setViewportX(double viewportX) {
		this.viewportX = viewportX;
	}

	public double getViewportY() {
		return viewportY;
	}

	public void setViewportY(double viewportY) {
		this.viewportY = viewportY;
	}

	public double getViewportZoom() {
		return viewportZoom;
	}

	public void setViewportZoom(double viewportZoom) {
		this.viewportZoom = viewportZoom;
	}
	
	/*
	 * 
	 * Nodes
	 * 
	 */

	public ArrayList<Node> getNodes() {
		return nodes;
	}

	public Node getNodeByID(String ID) {
		return (Node) Element.getElement(ElementSearch.ID, nodes, ID);
	}
	
	public Node getNodeByLabel(String label) {
		return (Node) Element.getElement(ElementSearch.LABEL, nodes, label);
	}
	
	/*
	 * 
	 * Connections
	 * 
	 */
	
	public ArrayList<Connection> getConnections() {
		return connections;
	}
	
	public Connection getConnectionByID(String ID) {
		return (Connection) Element.getElement(ElementSearch.ID, connections, ID);
	}

	public Connection getConnectionByLabel(String label) {
		return (Connection) Element.getElement(ElementSearch.LABEL, connections, label);
	}
	
	/*
	 * 
	 * Starting Node
	 * 
	 */
	
	public Node getStartingNode() {
		return startingNode;
	}
	
	public void setStartingNode(Node startingNode) {
		this.startingNode = startingNode;
	}
}
