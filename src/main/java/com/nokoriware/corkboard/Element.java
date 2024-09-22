package com.nokoriware.corkboard;

import java.util.ArrayList;

/**
 * This class identifies and adds basic ID && label functionality to all classes that carry data within a Corkboard project.
 */
public abstract class Element {
	
	protected final String ID;
	protected String label;
	
	public enum ElementSearch {
		ID,
		LABEL;
	}
	
	public Element(String ID, String label) {
		this.ID = ID;
		this.label = label;
	}

	public String getID() {
		return ID;
	}

	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Returns a Element from a list of Element objects that has the given ID. 
	 * <br><br>
	 * Example: getElement(getNodes(), "ID");
	 */
	public static Element getElement(ElementSearch elementSearch, ArrayList<? extends Element> carriers, String IDorLabel){
		for (int i = 0; i < carriers.size(); i++) {
			Element e = carriers.get(i);

			switch (elementSearch) {
			case ID: {
					if (e.getID().equals(IDorLabel)) {
						return e;
					} else {
						break;
					}
				}
			case LABEL: {
					if (e.getLabel().equals(IDorLabel)) {
						return e;
					} else {
						break;
					}
				}
			}
		}
		
		return null;
	}
}
