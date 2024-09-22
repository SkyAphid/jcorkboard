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
	
	public boolean hasAttributes() {
		return (attributes != null && !attributes.isEmpty());
	}
	
	public boolean hasComponents() {
		return (components != null && !components.isEmpty());
	}
	
	public ArrayList<Node> getComponents() {
		return components;
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
	
	public ArrayList<Connection> getSourceConnectionsByLabel(LabelType labelType) {
		return getConnectionsByLabel(labelType, getSourceConnections());
	}
	
	public ArrayList<Connection> getTargetConnectionsByLabel(LabelType labelType) {
		return getConnectionsByLabel(labelType, getTargetConnections());
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
	 * Connect/Disconnect
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
