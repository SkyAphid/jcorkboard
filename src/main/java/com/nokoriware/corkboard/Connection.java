package com.nokoriware.corkboard;

public class Connection extends Element {
	
	private Node source, target;

	/**
	 * Creates a connection between the two nodes.
	 */
	public Connection(String ID, String label, Node source, Node target) {
		super(ID, label);
		this.source = source.connect(this);
		this.target = target.connect(this);
	}
	
	public void disconnect() {
		source.disconnect(this);
		target.disconnect(this);
	}
	
	public Node getSource() {
		return source;
	}
	
	public Node getTarget() {
		return target;
	}
	
}
