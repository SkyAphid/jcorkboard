package com.nokoriware.corkboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Node extends Element {
	
	private final NodeType type;
	
	private double x, y;
	private double width, height;
	
	private Content body;
	private ArrayList<String> attributes;
	private ArrayList<Node> components;
	
	private Node jumperTarget;
	
	private ArrayList<Connection> connections;

	public enum LabelType {
		LABELLED,
		UNLABELLED;
	}

	public Node(String ID, NodeType type, double x, double y, double width, double height, String label, Content body, String[] attributes) {
		super(ID, label);
		this.type = type;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.body = body;
		this.attributes = new ArrayList<>(Arrays.asList(attributes));
		
		components = new ArrayList<>();
		connections = new ArrayList<>();
	}

	public NodeType getType() {
		return type;
	}
	
	/*
	 * Metadata
	 */
	
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}
	
	/*
	 * Content
	 */

	public Content getBody() {
		return body;
	}

	public void setBody(Content body) {
		this.body = body;
	}
	
	public boolean hasBody() {
		return body != null;
	}
	
	public ArrayList<String> getAttributes() {
		return attributes;
	}
	
	public boolean containsAttribute(String attribute) {
		for (String a : attributes) {
			if (a.contentEquals(attribute)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Get a list of attributes that start with the given prefix, parse th
	 * 
	 * @param prefix - string to search for at beginning of attributes
	 * @return - return the  Strings that contained the <code>prefix</code> before being removed and returned by this function
	 */
	public String[] getAttributesStartsWith(String prefix) {
		ArrayList<String> strings = new ArrayList<>();
		
		for (String a : attributes) {
			if (a.startsWith(prefix)) {
				strings.add(a.substring(0, prefix.length()));
			}
		}
		
		return strings.toArray(String[]::new);
	}
	
	/**
	 * 
	 * @param prefix
	 * @return true if an attribute in this Node <code>startsWith(prefix)</code>.
	 */
	public boolean containsAttributeStartsWith(String prefix) {
		for (String a : attributes) {
			if (a.startsWith(prefix)) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean hasAttributes() {
		return (attributes != null && !attributes.isEmpty());
	}
	
	public ArrayList<Node> getComponents() {
		return components;
	}
	
	public boolean hasComponents() {
		return (components != null && !components.isEmpty());
	}
	
	
	/*
	 * 
	 * Connections & utility functions for them
	 * 
	 */
	
	/**
	 * @return an unmodifiable list of this Node's various connections to other Nodes
	 */
	public List<Connection> getConnections() {
		return Collections.unmodifiableList(connections);
	}

	public boolean hasConnections() {
		return !connections.isEmpty();
	}
	
	public ArrayList<Connection> getConnectionsByLabel(LabelType labelType) {
		return getConnectionsByLabel(labelType, connections);
	}

	private static ArrayList<Connection> getConnectionsByLabel(LabelType labelType, ArrayList<Connection> connections) {
		ArrayList<Connection> valid = new ArrayList<>();
		
		for (Connection connection : connections) {
			boolean isLabelBlank = connection.getLabel().isBlank();
			
			switch(labelType) {
			case LABELLED:
				if (!isLabelBlank) {
					valid.add(connection);
				}
				break;
			case UNLABELLED:
				if (isLabelBlank) {
					valid.add(connection);
				}
				break;
			default:
				continue;
			};

		}
		
		return valid;
	}
	
	/*
	 * 
	 * 
	 * Jumpers
	 * 
	 * 
	 */
	
	public boolean hasJumperTarget() {
		return (jumperTarget != null);
	}
	
	public Node getJumperTarget() {
		return jumperTarget;
	}
	
	void setJumperTarget(Node jumperTarget){
		this.jumperTarget = jumperTarget;
	}
	
	/*
	 * 
	 * 
	 * Source/Target Connection getters
	 * 
	 * 
	 */
	
	/**
	 * @return an array of Connections that are coming into this Node
	 */
	public ArrayList<Connection> getSourceConnections() {
		ArrayList<Connection> sources = new ArrayList<>();
		
		for (Connection c : connections) {
			if (c.getSource() != this && c.getTarget() == this) {
				sources.add(c);
			}
		}
		
		return sources;
	}
	
	public boolean hasSourceConnections() {
		return !getSourceConnections().isEmpty();
	}
	
	/**
	 * @return true if the out-bound connections have labels on them.
	 */
	public boolean hasLabelledSourceConnections() {
		for (Connection c : getSourceConnections()) {
			if (!c.getLabel().isBlank()) {
				return true;
			}
		}
		
		return false;
	}
	
	public ArrayList<Connection> getSourceConnectionsByLabel(LabelType labelType) {
		return getConnectionsByLabel(labelType, getSourceConnections());
	}
	
	/**
	 * @return an array of Connections that are going out of this Node
	 */
	public ArrayList<Connection> getTargetConnections() {
		ArrayList<Connection> targets = new ArrayList<>();
		
		for (Connection c : connections) {
			if (c.getSource() == this && c.getTarget() != this) {
				targets.add(c);
			}
		}
		
		return targets;
	}
	
	public boolean hasTargetConnections() {
		return !getTargetConnections().isEmpty();
	}
	
	/**
	 * @return true if the out-bound connections have labels on them.
	 */
	public boolean hasLabelledTargetConnections() {
		for (Connection c : getTargetConnections()) {
			if (!c.getLabel().isBlank()) {
				return true;
			}
		}
		
		return false;
	}
	
	public ArrayList<Connection> getTargetConnectionsByLabel(LabelType labelType) {
		return getConnectionsByLabel(labelType, getTargetConnections());
	}
	
	
	/*
	 * 
	 * Connect/Disconnect
	 * 
	 */
	
	public Connection addConnection(String label, Node targetNode) {
		return new Connection(UUID.randomUUID().toString(), label, this, targetNode);
	}
	
	/**
	 * Removes Connection. Alternative to Connection.disconnect()
	 * @param connection
	 * @return true if the Connection is inside this Node and was disconnected
	 */
	public boolean removeConnection(Connection connection) {
		if (connections.contains(connection)) {
			connection.disconnect();
			return true;
		}
		
		return false;
	}
	
	void disconnect(Connection connection) {
		connections.remove(connection);
	}

	Node connect(Connection connection) {
		connections.add(connection);
		return this;
	}
	
}
