package com.n9mtq4.hrm2j.gui

import com.n9mtq4.hrm2j.compiler.getCompiledClass
import com.n9mtq4.hrm2j.interpreter.Interpreter
import com.n9mtq4.hrm2j.parser.DataConverter
import com.n9mtq4.hrm2j.parser.parseProgram
import com.n9mtq4.kotlin.extlib.ignoreAndGiven
import com.n9mtq4.kotlin.extlib.pstAndGiven
import org.fife.ui.autocomplete.AutoCompletion
import org.fife.ui.autocomplete.BasicCompletion
import org.fife.ui.autocomplete.CompletionProvider
import org.fife.ui.autocomplete.DefaultCompletionProvider
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
import javax.swing.text.JTextComponent
import javax.swing.text.Segment
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
		
		val provider = createProvider()
		val ac = object : AutoCompletion(provider) {
			override fun getAutoActivationDelay(): Int {
				return 0
			}
		}
		ac.isAutoCompleteEnabled = true
		ac.isAutoActivationEnabled = true
		ac.autoActivationDelay = 0
		ac.autoCompleteSingleChoices = false
		ac.setChoicesWindowSize(100, 200)
		ac.install(codeArea)
		
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
				menuItem("Run (Interpret)").shortcut('r').onAction { thread(start = true) { runInterpretCode() } }
				menuItem("Run (Compile)").shortcut('r', shift = true).onAction { thread(start = true) { compileRunCode() } }
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
	
	fun runInterpretCode() {
		
		clearOutput()
		
		val program = parseProgram(codeArea.text) { stackTrace.append(it + "\n") }
		
		val interpreter = Interpreter(program, parseInboxValues(), parseFloorSize(), parseFloorValues(), 0xfff)
		interpreter.run()
		
		val out = interpreter.toString()
		output.append(out)
		
		stackTrace.append(interpreter.stackTrace?.toText())
		
	}
	
	fun compileRunCode() {
		
		clearOutput()
		
		val program = parseProgram(codeArea.text) { stackTrace.append(it + "\n") }
		
		val clazz = getCompiledClass(program, parseInboxValues(), parseFloorSize(), parseFloorValues(), 0xfff, true)
		
		val instance = clazz.newInstance()
		val method = clazz.getDeclaredMethod("run")
		val out = method.invoke(instance) as String
		val field = clazz.getDeclaredField("stackTrace")
		
		output.append(out)
		
		field.get(instance)?.let {
			it as Throwable
			stackTrace.append(it.toText())
		}
		
	}
	
	fun parseFloorValues(): IntArray {
		return pstAndGiven(intArrayOf()) {
			val text = floorData.text.trim()
			val lines = text.split("\n").filterNot(String::isBlank).map(String::trim).map { it.split(" ") }
			val intArray = IntArray(lines.size * 2)
			lines.forEachIndexed { i, value ->
				intArray[i] = value[0].toInt()
				intArray[i + 1] = parseData(value[1])
			}
//			lines.map { it.trim() }.flatMap { it.split(" ") }.map { it.toInt() }.forEachIndexed { index, value -> intArray[index] = value }
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

private val dataRegex = """'.'""".toRegex(RegexOption.IGNORE_CASE)
private fun parseData(it: String) = ignoreAndGiven(
		if (it.matches(dataRegex))
			DataConverter.toData(it.toCharArray()[1])
		else
			DataConverter.toData(it.toCharArray()[0])
) {
	it.toInt()
}

private fun createProvider(): CompletionProvider {
	
	val provider = object : DefaultCompletionProvider() {
		private val s1 = Segment()
		/**
		 * requires a custom implementation of this, or autocomplete wont auto activate
		 * */
		override fun isAutoActivateOkay(tc: JTextComponent?): Boolean {
			if (tc == null) return false
			return ignoreAndGiven(false) {
				val doc = tc.document
				doc.getText(tc.caretPosition, 1, s1)
				val ch = s1.first()
				Character.isLetter(ch)
			}
//			return super.isAutoActivateOkay(tc)
		}
	}
	
//	TODO: automate this. this is just another thing that has to be manually updated when a command is added :(
	provider.addCmd(
			"inbox",
			"input",
			"outbox",
			"output",
			"copyto",
			"copyto [",
			"copyfrom",
			"copyfrom [",
			"add",
			"add [",
			"sub",
			"sub [",
			"bumpup",
			"inc",
			"bumpdn",
			"dec",
			"jump",
			"jumpn",
			"jumpz",
			"load",
			"jumpeq",
			"crash"
	)
	
	return provider
	
}

fun DefaultCompletionProvider.addCmd(vararg str: String) {
	str.forEach { this.addCompletion(BasicCompletion(this, it)) }
}
