package com.nokoriware.corkboard;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Content {
	
	private Document document;
	
	private Content(Document document) {
		this.document = document;
		document.outputSettings().prettyPrint(false);
	}
	
	public Content(String string) {
		this(Jsoup.parse(string));
	}
	
	/**
	 * @return the HTML document underlying this object, parsed with JSoup.
	 */
	public Document getDocument() {
		return document;
	}
	
	/**
	 * @return the document housed in this object as a parsed, combined, normalized string.
	 */
	public String getText() {
		return document.text();
	}
	
	/**
	 * Processes the text of each paragraph in the document into an index of an array, with their html tags intact so that string modifiers like italics, bold, and so on can still be processed.
	 * 
	 * @return the processed paragraphs array; each index containing a separate paragraph
	 */
	public String[] getParagraphHTML() {

	    Elements paragraphs = document.select("p");
	    String[] processed = new String[paragraphs.size()];
	    
	    for (int i = 0; i < paragraphs.size(); i++) {
	    	processed[i] = paragraphs.get(i).html();
	    }
	    
	    return processed;
	}
	
	/**
	 * @return true if the text of this element is blank. (<code>getText().isBlank()</code>)
	 */
	public boolean isBlank() {
		return getText().isBlank();
	}

	/**
	 * Overriding toString() allows Content to functionally work as a String when it comes to being presented in text-boxes.
	 */
	@Override
	public String toString() {
		return document.text();
	}

}
