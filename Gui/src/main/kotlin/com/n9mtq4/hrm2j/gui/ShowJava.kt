package com.n9mtq4.hrm2j.gui

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.fife.ui.rsyntaxtextarea.SyntaxConstants
import org.fife.ui.rtextarea.RTextScrollPane
import javax.swing.JFrame

/**
 * Created by will on 7/28/16 at 4:36 PM.
 *
 * @author Will "n9Mtq4" Bresnahan
 */
class ShowJava(val javaStr: String) {
	
	val frame: JFrame
	
	init {
		
		frame = JFrame("Java")
		
		val textArea = RSyntaxTextArea(javaStr).apply {
			syntaxEditingStyle = SyntaxConstants.SYNTAX_STYLE_JAVA
			tabSize = 4
			isEditable = false
		}
		
		frame.run {
			defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
			add(RTextScrollPane(textArea).apply {
				lineNumbersEnabled = true
			})
			pack()
			setSize(400, 600)
			isVisible = true
			setLocationRelativeTo(null)
		}
		
	}
	
}
