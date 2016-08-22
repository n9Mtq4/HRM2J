package com.n9mtq4.hrm2j.gui

import com.n9mtq4.hrm2j.compiler.fastCompile
import com.n9mtq4.hrm2j.compiler.getCompiledClass
import com.n9mtq4.hrm2j.interpreter.Interpreter
import com.n9mtq4.hrm2j.parser.getHrmValue
import com.n9mtq4.hrm2j.parser.parseProgram
import com.n9mtq4.hrm2j.parser.toBasesInt
import com.n9mtq4.kotlin.extlib.ignore
import com.n9mtq4.kotlin.extlib.pstAndGiven
import org.fife.ui.autocomplete.AutoCompletion
import org.fife.ui.autocomplete.BasicCompletion
import org.fife.ui.autocomplete.CompletionProvider
import org.fife.ui.autocomplete.DefaultCompletionProvider
import org.fife.ui.autocomplete.TemplateCompletion
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.fife.ui.rsyntaxtextarea.SyntaxConstants
import org.fife.ui.rtextarea.RTextScrollPane
import org.jdesktop.swingx.JXMultiSplitPane
import java.awt.BorderLayout
import java.awt.Component
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JOptionPane
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
	
	private val splitPane: JXMultiSplitPane
	
	private val codeArea: RSyntaxTextArea
	
	private val floorSize: JTextField
	private val floorData: JTextArea
	private val inputData: JTextArea
	
	private val outputSize: JTextField
	private val output: JTextArea
	private val stackTrace: JTextArea
	
	init {
		
		this.frame = JFrame("HRM IDE")
		
//		main thing
		this.splitPane = JXMultiSplitPane().apply { 
			setModel(ThreeVerticalModel(.5, .25, .25))
		}
		
		// code column
		this.codeArea = RSyntaxTextArea().apply {
			syntaxEditingStyle = SyntaxConstants.SYNTAX_STYLE_LUA // HRM more closely matches lua than assembly
			tabSize = 4
			columns = COLUMN_COUNT
		}
		
		val provider = createProvider()
		object : AutoCompletion(provider) {
			override fun getAutoActivationDelay(): Int {
				return 0
			}
		}.apply {
			isAutoCompleteEnabled = true
			isAutoActivationEnabled = true
			autoActivationDelay = 0
			autoCompleteSingleChoices = false
			isParameterAssistanceEnabled = true;
			setChoicesWindowSize(100, 200)
			install(codeArea)
		}
		val codeAreaScroll = RTextScrollPane(codeArea).apply {
			lineNumbersEnabled = true
		}.applyScrollBar()
		
		// input column objects
		this.floorSize = JTextField("64")
		this.floorData = JTextArea().apply { lineWrap = true; columns = COLUMN_COUNT }
		this.inputData = JTextArea().apply { lineWrap = true; columns = COLUMN_COUNT }
		// set up input column
		val inputTopPanel = JPanel(BorderLayout()).apply {
			val floorDataScroll = JScrollPane(floorData).applyScrollBar().withTitle("Floor Data")
			add(floorSize.withTitle("Floor Size"), BorderLayout.NORTH)
			add(floorDataScroll, BorderLayout.CENTER)
		}
		val inputColumn = JSplitPane(JSplitPane.VERTICAL_SPLIT).apply {
			topComponent = inputTopPanel
			bottomComponent = JScrollPane(inputData).applyScrollBar().withTitle("Input Data")
			resizeWeight = .5
		}
		
		// output column objects
		this.outputSize = JTextField("0xfff")
		this.output = JTextArea().apply {
			lineWrap = true
			columns = COLUMN_COUNT
			isEditable = false
		}
		this.stackTrace = JTextArea().apply { 
			tabSize = 4
			columns = COLUMN_COUNT
			isEditable = false
		}
		// set up output column
		val outputTopPanel = JPanel(BorderLayout()).apply {
			add(outputSize.withTitle("Output Size"), BorderLayout.NORTH)
			add(JScrollPane(output).applyScrollBar().withTitle("Text"), BorderLayout.CENTER)
		}
		val outputColumn = JSplitPane(JSplitPane.VERTICAL_SPLIT).apply {
			topComponent = outputTopPanel
			bottomComponent = JScrollPane(stackTrace).applyScrollBar().withTitle("Messages")
			resizeWeight = .5
		}
		
		// add them all
		splitPane.run {
			add(codeAreaScroll.withTitle("Code"), ThreeVerticalModel.P1)
			add(inputColumn.withTitle("Input"), ThreeVerticalModel.P2)
			add(outputColumn.withTitle("Output"), ThreeVerticalModel.P3)
		}
		
		// menu bar
		val menuBar = menuBar { 
			
			menuList("File") {
				menuItem("nothing in this menu works yet")
				menuItem("Save").shortcut('s')
				menuItem("Open").shortcut('o')
			}
			
			menuList("Edit") {
				menuItem("Clear Output").onAction { clearOutput() }
			}
			
			menuList("Build") {
				menuItem("Run (Interpret)").shortcut('r').onAction { thread(start = true) { runInterpretCode() } }
				menuItem("Run (Compile)").shortcut('r', shift = true).onAction { thread(start = true) { compileRunCode() } }
				menuItem("Show Java").shortcut('j').onAction { showJava() }
			}
			
		}
		
		// show the frame
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
		
		ignore {
			stackTrace.append("SHOW JAVA\n")
			val program = parseProgram(codeArea.text) { stackTrace.append(it + "\n") }
			
			val packageName = requestString(frame, "Name of java package?")!!
			val className = requestString(frame, "Name of java class?")!!
			var code = ""
			fastCompile(program, className, packageName, parseInboxValues(), parseFloorSize(), parseFloorValues(), parseOutboxSize()) {
				code += it + "\n"
			}
			ShowJava(code)
		}
		
	}
	
	fun runInterpretCode() {
		
		clearOutput()
		
		val program = parseProgram(codeArea.text) { stackTrace.append(it + "\n") }
		
		val interpreter = Interpreter(program, parseInboxValues(), parseFloorSize(), parseFloorValues(), parseOutboxSize()) {
			stackTrace.append(it + "\n")
		}
		interpreter.run()
		
		try {
			val out = interpreter.toString()
			output.append(out)
		}catch (e: Throwable) {
			stackTrace.append(e.toText())
		}
		
		stackTrace.append(interpreter.stackTrace?.toText())
		
	}
	
	fun compileRunCode() {
		
		clearOutput()
		
		val program = parseProgram(codeArea.text) { stackTrace.append(it + "\n") }
		
		val clazz = getCompiledClass(program, parseInboxValues(), parseFloorSize(), parseFloorValues(), parseOutboxSize(), true)
		
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
			for (i in 0..(lines.size - 1)) {
				intArray[i * 2] = lines[i][0].toInt()
				intArray[i * 2 + 1] = lines[i][1].getHrmValue()
			}
			intArray
		}
	}
	
	fun parseInboxValues(): IntArray {
		return pstAndGiven(intArrayOf()) {
			val text = inputData.text
			val items = text.split(",")
			val list = items.map { it.getHrmValue() }
			list.toTypedArray().toIntArray()
		}
	}
	
	fun parseFloorSize(): Int {
		return pstAndGiven(0x40) {
			floorSize.text.toBasesInt()
		}
	}
	
	fun parseOutboxSize(): Int {
		return pstAndGiven(0xfff) {
			outputSize.text.toBasesInt()
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

private fun createProvider(): CompletionProvider {
	
	val provider = HrmCompletionProvider()
	
//	TODO: automate this. this is just another thing that has to be manually updated when a command is added :(
	provider.run {
		addCmdBasic("inbox", "input")
		addCmdBasic("outbox", "output")
		addCmd("copyto", "copyto \${pointer}")
		addCmd("copyto [", "copyto [\${index}]")
		addCmd("copyfrom", "copyfrom \${pointer}")
		addCmd("copyfrom [", "copyfrom [\${index}]")
		addCmd("add", "add \${pointer}")
		addCmd("add [", "add [\${index}]")
		addCmd("sub", "sub \${pointer}")
		addCmd("sub [", "sub [\${index}]")
		addCmd("bumpup", "bumpup \${pointer}")
		addCmd("inc", "inc \${pointer}")
		addCmd("bumpdn", "bumpdn \${pointer}")
		addCmd("dec", "dec \${pointer}")
		addCmd("jump", "jump \${label}")
		addCmd("jumpn", "jumpn \${label}")
		addCmd("jumpz", "jumpz \${label}")
		addCmd("load", "load \${value}")
		addCmd("jumpeq", "jumpeq \${pointer} \${label}")
		addCmdBasic("crash")
	}
	
	return provider
	
}

internal fun requestString(parent: JFrame, text: String, initValue: String = "", onSuccess: (String) -> Unit = {}): String? {
	val response = JOptionPane.showInputDialog(parent, text, initValue)
	onSuccess.invoke(response ?: return null)
	return response
}
