package eclihx.ui.internal.ui.editors;

import org.eclipse.jface.text.rules.IWhitespaceDetector;

/**
 * Defines the spaces characters for Haxe source files.
 * 
 * @see IWhitespaceDetector
 */
public class CodeWhitespaceDetector implements IWhitespaceDetector {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.IWhitespaceDetector#isWhitespace(char)
	 */
	@Override
	public boolean isWhitespace(char c) {
		return Character.isWhitespace(c);
	}
}
