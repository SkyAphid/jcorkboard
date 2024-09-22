package com.nokoriware.corkboard;

public enum NodeType {
	TEXT_AREA,
	TEXT_FIELD,
	COMPONENT,
	NOTE;
	
	public static NodeType getType(String type) {
		switch(type) {
		case "text-area": return TEXT_AREA;
		case "text-field": return TEXT_FIELD;
		case "component": return COMPONENT;
		case "note": return NOTE;
		default: return null;
		}
	}
}