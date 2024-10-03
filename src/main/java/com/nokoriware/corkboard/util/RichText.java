package com.nokoriware.corkboard.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * This utility class allows you to parse html and convert it to a RichText object.
 * RichText objects in turn store the parsed plain text, and is able to tell you how to format it at each character index.
 */
public class RichText {
	
	private String plainText;
	private List<CharacterStyle> characterStyles;
	private CustomTag[] customTags;
	
	private List<String> codeBlocks;

	public RichText(String html) {
		this(html, false);
	}
	
	public RichText(String html, boolean extractCodeBlocks, CustomTag...customTags) {
		this(Jsoup.parse(decodeHtml(html)), extractCodeBlocks, customTags);
	}
	
	private RichText(Document document, boolean extractCodeBlocks, CustomTag...customTags) {
		this.customTags = customTags;
		
		parse(document.body(), extractCodeBlocks, customTags);
	}
	
	private void parse(Element body, boolean extractCodeBlocks, CustomTag...customTags){
		
		//Extract code blocks into this list to be run separately, and also remove them from the body
		if (extractCodeBlocks) {
			codeBlocks = extractCodeBlocks(body);
		}
		
		System.err.println(customTags.length);
		Thread.dumpStack();
		
		//Some tags are designed to be replaced with text only available at runtime. Names, data, etc
		for (CustomTag tag : customTags) {
			System.err.println(tag + " " + tag.name());
			if (tag.isReplacer()) {
				Elements elements = body.select(tag.name());
				
				for (Element element : elements) {
					element.replaceWith(new TextNode(tag.replacement()));
					System.out.println("Replaced Custom Tag " + tag.name());
				}
			}
		}
		
		StringBuilder plainText = new StringBuilder();
		List<CharacterStyle> characterStyles = new ArrayList<>();

		// Traverse the nodes to build the plain text and keep track of styles
		body.traverse(new NodeVisitor() {
			
			int currentIndex = 0;
			
			boolean isBold = false;
			boolean isItalic = false;
			boolean isUnderline = false;
			boolean isCustomTag[] = new boolean[customTags.length];

			@Override
			public void head(Node node, int depth) {
				if (node instanceof Element) {
					
					Element element = (Element) node;
					String tagName = element.tagName();
					
					System.err.println(tagName);

					switch (tagName) {
					case "strong":
					case "b":
						isBold = true;
						break;
					case "em":
					case "i":
						isItalic = true;
						break;
					case "u":
						isUnderline = true;
						break;
					default:
						
						if (customTags != null) {
							for (int i = 0; i < customTags.length; i++) {
								CustomTag tag = customTags[i];
								
								if (tagName.equalsIgnoreCase(tag.name())) {
									isCustomTag[i] = true;
									tag.onStart(currentIndex);
								}
							}
						}
						
						break;
					}
					
				} else if (node instanceof TextNode) {
					
					TextNode textNode = (TextNode) node;
					String text = textNode.text();
					
					if (!text.isEmpty()) {

						//If the last style never found an endIndex, set one here.
						if (!characterStyles.isEmpty()) {
							CharacterStyle lastStyle = characterStyles.getLast();
							
							if (lastStyle.endIndex == 0) {
								lastStyle.endIndex = currentIndex;
							}

						}
						
						//Create a new style here
						plainText.append(text);
						characterStyles.add(new CharacterStyle(currentIndex, isBold, isItalic, isUnderline, isCustomTag));
						currentIndex += text.length();
					}
					
				}
			}

			@Override
			public void tail(Node node, int depth) {
				
				if (node instanceof Element) {
					Element element = (Element) node;
					String tagName = element.tagName();
					
					switch (tagName) {
					case "strong":
					case "b":
						isBold = false;
						break;
					case "em":
					case "i":
						isItalic = false;
						break;
					case "u":
						isUnderline = false;
						break;
					default:
						
						if (customTags != null) {
							for (int i = 0; i < customTags.length; i++) {
								CustomTag tag = customTags[i];
								
								if (tagName.equalsIgnoreCase(tag.name())) {
									isCustomTag[i] = false;
									tag.onEnd(currentIndex);
								}
							}
						}
						
						break;
					}
					
					// Update the end index of the most recent style if it matches the tag
					if (!characterStyles.isEmpty()) {
						CharacterStyle lastStyle = characterStyles.getLast();
						
						if (lastStyle.endIndex == 0) {
							lastStyle.endIndex = currentIndex;
						}
					}
				}
			}
		});

		this.plainText = plainText.toString();
		this.characterStyles = characterStyles;
		
	}
	
	public String getPlainText() {
		return plainText;
	}
	
	public List<CharacterStyle> getCharacterStyles() {
		return characterStyles;
	}
	
	public CustomTag[] getCustomTags() {
		return customTags;
	}
	
	public List<String> getCodeBlocks() {
		return codeBlocks;
	}
	
	/**
	 * @param charIndex
	 * @return the style of the character at charIndex of this RichText
	 */
	public CharacterStyle getCharacterStyleAt(int charIndex) {
	    for (CharacterStyle style : characterStyles) {
	        if (charIndex >= style.startIndex && charIndex < style.endIndex) {
	            return style;
	        }
	    }
	    
	    return null;
	}

	public class CharacterStyle {
		int startIndex;
		int endIndex;
		
		boolean isBold;
		boolean isItalic;
		boolean isUnderlined;
		boolean isCustomTag[];

		public CharacterStyle(int startIndex, boolean isBold, boolean isItalic, boolean isUnderlined, boolean[] isCustomTag) {
			this.startIndex = startIndex;
			this.isBold = isBold;
			this.isItalic = isItalic;
			this.isUnderlined = isUnderlined;
			this.isCustomTag = isCustomTag;
		}
		
		public int getStartIndex() {
			return startIndex;
		}
		
		public int getEndIndex() {
			return endIndex;
		}

		public boolean isBold() {
			return isBold;
		}

		public boolean isItalic() {
			return isItalic;
		}

		public boolean isUnderlined() {
			return isUnderlined;
		}
		
		public boolean isCustomTag(String tag) {
			for (int i = 0; i < isCustomTag.length; i++) {
				if (isCustomTag[i] && customTags[i].name().equals(tag)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public String toString() {
			return "Character Style from " + startIndex + " to " + endIndex + ": [Bold = " + isBold + ", Italic = " + isItalic + ", Underlined = " + isUnderlined + "]";
		}
	}
	
	public interface CustomTag {
		public String name();
		
		public void onStart(int charIndex);
		public void onEnd(int charIndex);
		
		public boolean isReplacer();
		public String replacement();
	}
	
	public static List<String> extractCodeBlocks(Element body){
        List<String> codeBlocks = new ArrayList<>();

        // Select all <code> elements
        Elements codeElements = body.select("code");

        // Iterate through each code element
        for (Element codeElement : codeElements) {
            // Extract text from the code block
            codeBlocks.add(codeElement.text());

            // Remove the code element from the document
            codeElement.remove();
        }

        return codeBlocks;
	}
	
	/**
	 * Decode any html in the text, so that support for custom tags can be added.
	 * @param html
	 * @return
	 */
	private static String decodeHtml(String html) {
		return html.replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&amp;", "&")
                .replace("&quot;", "\"")
                .replace("&apos;", "'");
	}
	
	public static void main(String[] args) {
		String html = "<html><body><p>This is <b>bold</b> and <i>italic</i> and <u>underlined</u> and <b><u>this</u> is <i><u>all</></i> of them.</b></u></p></body></html>";
		
		RichText richText = new RichText(html);
		
		// Print the plain text and the styles
		System.out.println("Plain text: " + richText.getPlainText());
		for (CharacterStyle style : richText.getCharacterStyles()) {
			System.out.println(style + " " + richText.getPlainText().substring(style.startIndex, style.endIndex));
		}
	}
	
}
