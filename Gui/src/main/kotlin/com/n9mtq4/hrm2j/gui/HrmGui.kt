package com.n9mtq4.hrm2j.gui

import com.n9mtq4.hrm2j.compiler.DataConverter
import com.n9mtq4.hrm2j.compiler.getCompiledClass
import com.n9mtq4.hrm2j.compiler.parseProgram
import com.n9mtq4.kotlin.extlib.ignoreAndGiven
import com.n9mtq4.kotlin.extlib.pstAndGiven
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
import kotlin.concurrent.thread

/**
 * Created by will on 7/17/16 at 11:41 PM.
 *
 * @author Will "n9Mtq4" Bresnahan
 */
private val COLUMN_COUNT = 5

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
			tabSize = 4
			columns = COLUMN_COUNT
		}
		
		// input column
		this.floorSize = JTextField("64")
		this.floorData = JTextArea().apply { lineWrap = true; columns = COLUMN_COUNT }
		this.inputData = JTextArea().apply { lineWrap = true; columns = COLUMN_COUNT }
		
		// output column
		this.output = JTextArea().apply { lineWrap = true; columns = COLUMN_COUNT }
		this.stackTrace = JTextArea().apply { 
			tabSize = 4
			columns = COLUMN_COUNT
		}
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
				menuItem("nothing in this menu works yet")
				menuItem("Save").shortcut('s')
				menuItem("Open").shortcut('o')
			}
			
			menuList("Build") {
				menuItem("Run").shortcut('r').onAction { thread(start = true) { runCode() } }
				menuItem("Show Java").shortcut('j').onAction { showJava() }
			}
			
		}
		
		frame.run {
			defaultCloseOperation = JFrame.EXIT_ON_CLOSE
			jMenuBar = menuBar
			add(splitPane)
			pack()
			setSize(920, 600)
			isVisible = true
			setLocationRelativeTo(null)
		}
	
	}
	
	fun clearOutput() {
		output.text = ""
		stackTrace.text = ""
	}
	
	fun showJava() {
		// TODO: to be implemented
	}
	
	fun runCode() {
		
		clearOutput()
		
		val program = parseProgram(codeArea.text) { stackTrace.append(it) }
		val clazz = getCompiledClass(program, parseInboxValues(), parseFloorSize(), parseFloorValues(), 0xfff, true)
		
		val instance = clazz.newInstance()
		val method = clazz.getDeclaredMethod("run")
		val out = method.invoke(instance) as String
		val field = clazz.getDeclaredField("stackTrace")
		
		val stackTraceEx = field.get(instance) as Throwable
		
		output.append(out)
		stackTrace.append(stackTraceEx.toText())
		
	}
	
	fun parseFloorValues(): IntArray {
		return pstAndGiven(intArrayOf()) {
			val text = floorData.text.trim()
			val lines = text.split("\n").filterNot { it.isBlank() }
			val intArray = IntArray(lines.size * 2)
			lines.map { it.trim() }.flatMap { it.split(" ") }.map { it.toInt() }.forEachIndexed { index, value -> intArray[index] = value }
			intArray
		}
	}
	
	fun parseInboxValues(): IntArray {
		return pstAndGiven(intArrayOf()) {
			val text = inputData.text
			val items = text.split(",")
			val dataRegex = """'.'""".toRegex(RegexOption.IGNORE_CASE)
			val list = items.map {
				ignoreAndGiven(
						if (it.matches(dataRegex))
							DataConverter.toData(it.toCharArray()[1])
						else
							DataConverter.toData(it.toCharArray()[0])
				) {
					it.toInt()
				}
			}
			list.toTypedArray().toIntArray()
		}
	}
	
	fun parseFloorSize(): Int {
		return pstAndGiven(64) {
			floorSize.text.toInt()
		}
	}
	
}

private fun Component.withTitle(title: String) = JPanel(BorderLayout()).apply { 
	add(JLabel(title), BorderLayout.NORTH)
	add(this@withTitle, BorderLayout.CENTER)
}

private fun JScrollPane.applyScrollBar(): JScrollPane {
	verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
//	horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
	return this
}
