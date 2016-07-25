package com.n9mtq4.hrm2j.gui

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.fife.ui.rsyntaxtextarea.SyntaxConstants
import org.fife.ui.rtextarea.RTextScrollPane
import org.jdesktop.swingx.JXMultiSplitPane
import java.awt.BorderLayout
import java.awt.Component
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JSplitPane
import javax.swing.JTextArea
import javax.swing.JTextField

/**
 * Created by will on 7/17/16 at 11:41 PM.
 *
 * @author Will "n9Mtq4" Bresnahan
 */
class HrmGui {
	
	private val frame: JFrame
	
	private val codeArea: RSyntaxTextArea
	
	private val floorSize: JTextField
	private val floorData: JTextArea
	private val inputData: JTextArea
	private val output: JTextArea
	private val stackTrace: JTextArea
	private val splitPane: JXMultiSplitPane
	
	init {
		
		this.frame = JFrame("HRM IDE")
		
		this.splitPane = JXMultiSplitPane()
		
		splitPane.setModel(ThreeVerticalModel(.5, .25, .25))
		
		// code column
		this.codeArea = RSyntaxTextArea().apply {
			syntaxEditingStyle = SyntaxConstants.SYNTAX_STYLE_LUA // HRM more closely matches lua than assembly
			isCodeFoldingEnabled = true
			tabSize = 4
			columns = COLUMN_COUNT
		}
		
		// input column
		this.floorSize = JTextField("64")
		this.floorData = JTextArea().apply { lineWrap = true; columns = COLUMN_COUNT }
		this.inputData = JTextArea().apply { lineWrap = true; columns = COLUMN_COUNT }
		
		// output column
		this.output = JTextArea().apply { lineWrap = true; columns = COLUMN_COUNT }
		this.stackTrace = JTextArea()
		output.isEditable = false
		stackTrace.isEditable = false
		
		// set up input column
		val inputColumn = JSplitPane(JSplitPane.VERTICAL_SPLIT)
		val floorPanel = JPanel(BorderLayout())
		floorPanel.add(floorSize.withTitle("Floor Size"), BorderLayout.NORTH)
		floorPanel.add(JScrollPane(floorData).applyScrollBar().withTitle("Floor Data"), BorderLayout.CENTER)
		
		inputColumn.topComponent = floorPanel
		inputColumn.bottomComponent = JScrollPane(inputData).applyScrollBar().withTitle("Input Data")
		inputColumn.resizeWeight = .5
		
		// set up output column
		val outputColumn = JSplitPane(JSplitPane.VERTICAL_SPLIT)
		outputColumn.topComponent = JScrollPane(output).applyScrollBar().withTitle("Text")
		outputColumn.bottomComponent = JScrollPane(stackTrace).applyScrollBar().withTitle("StackTrace")
		outputColumn.resizeWeight = .5
		
		val codeAreaScroll = RTextScrollPane(codeArea).apply { 
			lineNumbersEnabled = true
		}.applyScrollBar()
		
		splitPane.run {
			add(codeAreaScroll.withTitle("Code"), ThreeVerticalModel.P1)
			add(inputColumn.withTitle("Input"), ThreeVerticalModel.P2)
			add(outputColumn.withTitle("Output"), ThreeVerticalModel.P3)
		}
		
		val menuBar = menuBar { 
			
			menuList("File") {
				menuItem("Test")
			}
			
		}
		
		frame.run {
			jMenuBar = menuBar
			add(splitPane)
			pack()
			setSize(400, 300)
			isVisible = true
			setLocationRelativeTo(null)
		}
	
	}
	
}

private val COLUMN_COUNT = 5

private fun Component.withTitle(title: String) = JPanel(BorderLayout()).apply { 
	add(JLabel(title), BorderLayout.NORTH)
	add(this@withTitle, BorderLayout.CENTER)
}

private fun JScrollPane.applyScrollBar(): JScrollPane {
	verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
//	horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
	return this
}
