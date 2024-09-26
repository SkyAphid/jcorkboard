package com.nokoriware.corkboard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue.ValueType;

import com.nokoriware.corkboard.Element.ElementSearch;

public class CorkboardJSONImporter {
	
	/**
	 * A utility function that allows you to simply pass in a <code>File</code> containing the location of the Corkboard Project you wish to parse.
	 * 
	 * @throws Exception - any exceptions encountered during parsing will be reported.
	 */
	public static CorkboardProject read(File f) throws FileNotFoundException {
		FileInputStream inputStream = new FileInputStream(f);
		return read(f.getName().substring(0, f.getName().lastIndexOf(".")), inputStream);
	}
	
	/**
	 * Read the given input stream containing the JSON data for the given Corkboard Project. All data will be read and recorded onto an object based system that will allow for easier Java integration.
	 * 
	 * @throws Exception - any exceptions encountered during parsing will be reported.
	 */
	public static CorkboardProject read(String projectName, InputStream inputStream) {

		/*
		 * Load JSON file and prepare it for reading
		 */

		JsonReader jsonReader = Json.createReader(inputStream);
		JsonObject projectObject = jsonReader.readObject();

		/*
		 * Create new CorkboardProject
		 */

		CorkboardProject project = createProject(projectName, projectObject);
		
		/*
		 * Begin reading the file and assigning data
		 */

		//Load nodes and put them in containers with component IDs
		ArrayList<NodeContainer> nodeContainers = readNodes(projectObject);
		
		//Load connections
		project.getConnections().addAll(readEdges(projectObject, nodeContainers));
		
		//Iterate through node containers and connect nodes to components using IDs, now that every node is loaded
		for (NodeContainer nodeContainer : nodeContainers) {
			Node node = nodeContainer.node;
			
			//Add node
			project.getNodes().add(node);
			
			//Connect components
			for (String componentID : nodeContainer.componentIDs) {
				node.getComponents().add(project.getNodeByID(componentID));
			}
			
			//Set starting node
			if (nodeContainer.isStartingNode) {
				project.setStartingNode(node);
			}
			
			//Link jumper nodes if applicable
			for (NodeContainer nodeContainer2 : nodeContainers) {
				Node node2 = nodeContainer2.node;
				
				if (node.getType()!= NodeType.JUMPER || nodeContainer == nodeContainer2) {
					continue;
				}
				
				if (node.getLabel().contentEquals(node2.getLabel())) {
					node.setJumperTarget(node2);
					break;
				}
			}
		}

		/*
		 * Return the completed CorkboardProject
		 */

		return project;
	}
	
	private static CorkboardProject createProject(String projectName, JsonObject projectObject) {
		double viewportX = 0.0;
		double viewportY = 0.0;
		double viewportZoom = 0.0;
		
		ArrayList<Node> nodes = new ArrayList<>();
		ArrayList<Connection> connections = new ArrayList<>();

		if (containsValidKey(projectObject, "viewport")) {
			JsonObject viewport = projectObject.getJsonObject("viewport");
			
			viewportX = viewport.getJsonNumber("x").doubleValue();
			viewportY = viewport.getJsonNumber("y").doubleValue();
			viewportZoom = viewport.getJsonNumber("zoom").doubleValue();

		}
		
		return new CorkboardProject(projectName, viewportX, viewportY, viewportZoom, nodes, connections);
		
	}
	
	private static ArrayList<NodeContainer> readNodes(JsonObject projectObject) {
		
		ArrayList<NodeContainer> nodes = new ArrayList<>();
		
		if (containsValidKey(projectObject, "nodes")) {
			
			JsonArray nodesObject = projectObject.getJsonArray("nodes");

			nodesObject.forEach((nodeValue) -> {
				
				JsonObject nodeObject = nodeValue.asJsonObject();
				
				String ID = nodeObject.getString("id");
				
				/*
				 * Type
				 */
				
				String nodeType = nodeObject.getString("type");
				NodeType type = NodeType.getType(nodeType);
				
				/*
				 * Position
				 */
				
				double x = 0.0;
				double y = 0.0;
				
				if (containsValidKey(nodeObject, "position")) {
					
					JsonObject nodePosition = nodeObject.getJsonObject("position");
					
					x = nodePosition.getJsonNumber("x").doubleValue();
					y = nodePosition.getJsonNumber("y").doubleValue();
					
				}
				
				double width = 0.0;
				double height = 0.0;
				
				if (containsValidKey(nodeObject, "style")) {
					
					JsonObject nodeStyle = nodeObject.getJsonObject("style");
					
					String w = nodeStyle.getJsonString("width").getString().replaceAll("px", "");
					String h = nodeStyle.getJsonString("height").getString().replaceAll("px", "");
					
					width = Double.parseDouble(w);
					height = Double.parseDouble(h);
					
				}
				
				/*
				 * Data
				 */
				
				String label = "";
				Content body = new Content("");
				String[] componentIDs = new String[0];
				String[] attributes = new String[0];
				boolean isStartingNode = false;
				
				if (containsValidKey(nodeObject, "data")) {
					
					JsonObject nodeData = nodeObject.getJsonObject("data");
					
					//Label
					if (containsValidKey(nodeData, "label")) {
						label = nodeData.getString("label");
					}
					
					//Body
					if (containsValidKey(nodeData, "body")) {
						body = new Content(nodeData.getString("body"));
					}
					
					//Attributes
					if (containsValidKey(nodeData, "attributes")) {
						JsonArray attributeArray = nodeData.getJsonArray("attributes");
						
						attributes = new String[attributeArray.size()];
						
						for (int i = 0; i < attributes.length; i++) {
							attributes[i] = attributeArray.getString(i);
						}

					}
					
					//Components
					if (containsValidKey(nodeData, "components")) {
						JsonArray componentIDArray = nodeData.getJsonArray("components");
						
						componentIDs = new String[componentIDArray.size()];
						
						for (int i = 0; i < componentIDs.length; i++) {
							componentIDs[i] = componentIDArray.getString(i);
						}

					}
					
					//Starting Node
					if (containsValidKey(nodeData, "isStartingNode")) {
						if (nodeData.getBoolean("isStartingNode")) {
							isStartingNode = true;
						}
					}
				}
				
				Node node = new Node(ID, type, x, y, width, height, label, body, attributes);
				nodes.add(new NodeContainer(node, componentIDs, isStartingNode));
				
			});
			
		}
		
		return nodes;
		
	}
	
	private static ArrayList<Connection> readEdges(JsonObject projectObject, ArrayList<NodeContainer> nodeContainers) {
		ArrayList<Connection> connections = new ArrayList<>();
		
		if (containsValidKey(projectObject, "edges")) {
			
			JsonArray edgesObject = projectObject.getJsonArray("edges");
			
			edgesObject.forEach((edgeValue) -> {
				
				JsonObject edgeObject = edgeValue.asJsonObject();
				
				String ID = edgeObject.getString("id");
				String sourceID = edgeObject.getString("source");
				String targetID = edgeObject.getString("target");
				String label = edgeObject.getString("label");
				
				Node source = getNodeByID(nodeContainers, sourceID);
				Node target = getNodeByID(nodeContainers, targetID);
				
				connections.add(new Connection(ID, label, source, target));
				
			});
			
		}
		
		return connections;
	}
	
	private static class NodeContainer extends Element{
		
		private Node node;
		private String[] componentIDs;
		private boolean isStartingNode;
		
		public NodeContainer(Node node, String[] componentIDs, boolean isStartingNode) {
			super(node.getID(), node.getLabel());
			this.node = node;
			this.componentIDs = componentIDs;
			this.isStartingNode = isStartingNode;
		}

	}
	
	private static Node getNodeByID(ArrayList<NodeContainer> nodeContainers, String ID) {
		NodeContainer nodeContainer = ((NodeContainer) Element.getElement(ElementSearch.ID, nodeContainers, ID));
		
		if (nodeContainer != null && nodeContainer.node != null) {
			return nodeContainer.node;
		}

		return null;
	}
	
	private static boolean containsValidKey(JsonObject object, String key) {
		return object.containsKey(key) && object.get(key).getValueType() != ValueType.NULL;
	}
	
}
