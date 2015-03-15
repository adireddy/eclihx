package eclihx.core.haxe.model;

/**
 * Haxe code simple formatter.
 */
public final class CodeFormatter {
	
	/**
	 *  Don't allow to instantiate this class.
	 */
	private CodeFormatter() {}
	
	/**
	 * Storage for format options. 
	 */
	public static class FormatOptions {
		
		/**
		 * @return the bracketNewLines
		 */
		public boolean isBracketNewLines() {
			return bracketNewLines;
		}

		/**
		 * @param bracketNewLines the bracketNewLines to set
		 */
		public void setBracketNewLines(boolean bracketNewLines) {
			this.bracketNewLines = bracketNewLines;
		}

		/**
		 * @return the insertTabs
		 */
		public boolean isInsertTabs() {
			return insertTabs;
		}

		/**
		 * @param insertTabs the insertTabs to set
		 */
		public void setInsertTabs(boolean insertTabs) {
			this.insertTabs = insertTabs;
		}

		/**
		 * @return the intendWidth
		 */
		public int getIntendWidth() {
			return intendWidth;
		}

		/**
		 * @param intendWidth the intendWidth to set
		 */
		public void setIntendWidth(int intendWidth) {
			this.intendWidth = intendWidth;
		}

		/**
		 * @return the oneOperatorOnLine
		 */
		public boolean isOneOperatorOnLine() {
			return oneOperatorOnLine;
		}

		/**
		 * @param oneOperatorOnLine the oneOperatorOnLine to set
		 */
		public void setOneOperatorOnLine(boolean oneOperatorOnLine) {
			this.oneOperatorOnLine = oneOperatorOnLine;
		}

		/**
		 * @return the indentOnEmptyLines
		 */
		public boolean isIndentOnEmptyLines() {
			return indentOnEmptyLines;
		}

		/**
		 * @param indentOnEmptyLines the indentOnEmptyLines to set
		 */
		public void setIndentOnEmptyLines(boolean indentOnEmptyLines) {
			this.indentOnEmptyLines = indentOnEmptyLines;
		}

		/**
		 * Move curly bracket to new line.
		 */
		private boolean bracketNewLines = false;
		
		/**
		 * Use tabs for indentation.
		 */
		private boolean insertTabs = false;
		
		/**
		 * Number of spaces in indentation.
		 */
		private int intendWidth = 4;
		
		/**
		 * Allows only one operator on the line.
		 * Will make forced new line after the <code>;</code> char.
		 */
		private boolean oneOperatorOnLine = true;

		/**
		 * Make indent on empty lines.
		 */
		private boolean indentOnEmptyLines = false;
	}
	
	/**
	 * Format text.
	 * @param text text to format.
	 * @param options set of options for the formatter
	 * @return formatted text.
	 */
	public static String format(
			final String text, 
			final FormatOptions options) {
		
		// Store indentation string for one gap. 
		String indentationString = options.insertTabs ? "\t" : 
								   multiply(" ", options.intendWidth);
		
		// Global variable o count number of indentation for the current code.
		int numberOfIndentation = 0;
		
		// This variable is a buffer for the method result		
		final StringBuilder outputBuilder = new StringBuilder();
		
		// This variable will accumulate one line of the output.
		final StringBuilder outputLineBuffer = new StringBuilder();
		
		// Ask algorithm to omit one line break because it was placed manually 
		// before. This is a little hack for working with numberOfIndentation. 
		boolean omitLineBreak = false;
		
		for (int index = 0; index < text.length(); ++index) {
			
			char currentChar = text.charAt(index); 
			
			switch (currentChar) {
				case '{':
					if (outputLineBuffer.toString().trim().isEmpty() ||
						!options.bracketNewLines) {
						
						// Insert additional space before the brace
						if (!checkChar(text, index-1, ' ')) {
							outputLineBuffer.append(' ');
						}
						
						outputLineBuffer.append('{');
						
						// Check {> construction
						if (checkChar(text, index+1, '>')) {
							outputLineBuffer.append('>');
							++index;
						}						
						
						omitLineBreak = existValidNewLine(text, index+1);
						
						// Write everything on one line
						outputBuilder.append(
								generateLine(
										outputLineBuffer, 
										options.indentOnEmptyLines, 
										numberOfIndentation, 
										indentationString));
						
					} else {
						// Write line content first
						outputBuilder.append(
								generateLine(
										outputLineBuffer, 
										options.indentOnEmptyLines, 
										numberOfIndentation, 
										indentationString));
						
						omitLineBreak = existValidNewLine(text, index+1);
						
						// After that append brace to output
						outputBuilder.append(
								generateLine(
										"{", 
										options.indentOnEmptyLines, 
										numberOfIndentation, 
										indentationString));
					
					}
					
					++numberOfIndentation;
					
					break;
					
				case '}':
					if (!outputLineBuffer.toString().trim().isEmpty()) {
						outputBuilder.append(
								generateLine(
										outputLineBuffer, 
										options.indentOnEmptyLines, 
										numberOfIndentation, 
										indentationString));
					}
					
					--numberOfIndentation;
					
					omitLineBreak = existValidNewLine(text, index+1);
					
					outputBuilder.append(
							generateLine(
									"}", 
									options.indentOnEmptyLines, 
									numberOfIndentation, 
									indentationString));
					
					break;
					
				case ';':
					
					outputLineBuffer.append(currentChar);
					
					if (options.oneOperatorOnLine) {
						
						// Check that there are no valid new-line char in the
						// original text and we place it manually.
						omitLineBreak = existValidNewLine(text, index+1);
							
						outputBuilder.append(
								generateLine(
										outputLineBuffer, 
										options.indentOnEmptyLines, 
										numberOfIndentation, 
										indentationString));
					}
					break;
					
				case '\n':
					if (omitLineBreak) {
						outputLineBuffer.delete(0, outputLineBuffer.length());
						omitLineBreak = false;
					} else {
					
						// Write down the buffered line
						outputBuilder.append(
								generateLine(
										outputLineBuffer, 
										options.indentOnEmptyLines, 
										numberOfIndentation, 
										indentationString));
					}
					
					break;
					
				default: 
					outputLineBuffer.append(currentChar);
					break;
			}
			
		}
		
		return outputBuilder.toString();
	}
	
	/**
	 * Method checks there is exist new-line char separated from the 
	 * specified place only with spaces and tab chars.
	 * @param str the string for searching.
	 * @param index index of the start point.
	 * @return <code>true</code> if search was success.
	 */
	static private boolean existValidNewLine(String str, int index) {
		int newLineIndex = str.indexOf('\n', index);
		return (newLineIndex != -1 && 
				str.substring(index, newLineIndex).trim().isEmpty());
	}
	
	
	/**
	 * Method returns number of char occurrences in the string.
	 * @param text the string to inspect.
	 * @param ch the char for searching.
	 * @return Number of char entries to the string. 
	 */
	/*
	static private int countNumberOfOccurrences(String text, char ch) {
		
		int number = 0;
		
		// ++offset to start search for the next occurrence. If we won't 
		// increment indexOf will be finding the same char again and again. 
		for (int offset = 0; 
			 (offset = text.indexOf(ch, offset)) != -1; 
			 ++number, ++offset) {
		}
			
		return number;
	}*/
	
	/**
	 * Checks if char on the specified place is equal to another char.
	 * @param str the string where the check should be done.
	 * @param index the position of the char.
	 * @param ch the char to compare with.
	 * @return <code>true</code> if char is exist and is equal to the specified.
	 *  
	 */
	static private boolean checkChar(String str, int index, char ch) {
		// Check that index is valid and equality of the chars.
		return index >= 0 && index < str.length() && str.charAt(index) == ch;			
	}
	
	/**
	 * Generate one line of the new text.
	 * @param content the string with the line content.
	 * @param indentOnEmptyLines flag with the info should be 
	 *        indentation done on the empty lines.
	 * @param numberOfIndentation number of indentations
	 * @param indentationString string for one indentation.
	 * @return generated line.
	 */
	static private String generateLine(
			String content, 
			boolean indentOnEmptyLines,
			int numberOfIndentation, 
			String indentationString) {
		
		String trimmedLine = content.trim();
		
		if (trimmedLine.isEmpty() && !indentOnEmptyLines) {
			return "\n";
		}
		
		return multiply(indentationString, numberOfIndentation) + 
				trimmedLine + "\n";
	}
	
	/**
	 * Generate one line of the new text. This method will also reset the 
	 * StringBuilder with the content.
	 * @param contentBuilder builder with the line content.
	 * @param indentOnEmptyLines flag with the info should be 
	 *        indentation done on the empty lines.
	 * @param numberOfIndentation number of indentations
	 * @param indentationString string for one indentation.
	 * @return generated line.
	 */
	static private String generateLine(
			StringBuilder contentBuilder, 
			boolean indentOnEmptyLines,
			int numberOfIndentation, 
			String indentationString) {
		
		String line = contentBuilder.toString();
		
		// Reset the content builder
		contentBuilder.delete(0, contentBuilder.length());
		
		return generateLine(line, indentOnEmptyLines, 
				numberOfIndentation, indentationString);
	}
	
	/**
	 * This method multiplies the strings.
     *
	 * @param str the string to multiply.
	 * @param number number of times to repeat the string. You can get valid
	 *        result only if number >= 0. In other cases you'll get an 
	 *        empty string.
	 *        
	 * @return Multiplied string.
	 */
	static private String multiply(String str, int number) {
		
		StringBuilder newStrBuilder = new StringBuilder();
		
		for (int i = 0; i < number; ++i) {
			newStrBuilder.append(str);
		}
		
		return newStrBuilder.toString();
	}
}