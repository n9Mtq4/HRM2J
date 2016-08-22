package com.n9mtq4.hrm2j.gui

import com.n9mtq4.kotlin.extlib.ignore
import com.n9mtq4.kotlin.extlib.loop.forever
import com.n9mtq4.kotlin.extlib.pstAndGiven
import org.fife.ui.autocomplete.BasicCompletion
import org.fife.ui.autocomplete.DefaultCompletionProvider
import org.fife.ui.autocomplete.TemplateCompletion
import javax.swing.text.JTextComponent
import javax.swing.text.Segment

/**
 * Created by will on 8/22/16 at 3:12 PM.
 *
 * @author Will "n9Mtq4" Bresnahan
 */
class HrmCompletionProvider : DefaultCompletionProvider() {
	
	private val s1 = Segment()
	
	/**
	 * requires a custom implementation of this, or autocomplete wont auto activate
	 * */
	override fun isAutoActivateOkay(tc: JTextComponent?): Boolean {
		if (tc == null) return false
		return pstAndGiven(false) stackTrace@{
			val doc = tc.document
			val pointerLoc = tc.caretPosition
			doc.getText(pointerLoc, 1, s1)
			val ch = s1.first()
			if (!Character.isLetter(ch)) return@stackTrace false
			// ok we want to make sure there isn't a text then a space. example: no autocomplete with "load a" <- on the a. also be careful of indents w/ spaces
			val text = Segment()
			// text.isPartialReturn = true // javadocs says that this will be faster for a large data. currently only uses 1 character
			var space = false
			var letter = false
			var offSet = 1
			ignore { forever {
				doc.getText(pointerLoc - offSet, 1, text)
				if (text.first() == ' ') space = true // ok, we saw a space
				if (Character.isLetter(text.first()) && space) letter = true // this prevents spaces in indents triggering the no autocomplete
				offSet++
				if (pointerLoc - offSet == -1 || text.length == 0 || text.first() == '\n') return@ignore
			} }
			if (space && letter) return@stackTrace false // they both have to be true to disable
			true // nothing else to check, so must be ok
		}
//		return super.isAutoActivateOkay(tc)
	}
	
}

fun HrmCompletionProvider.addCmd(input: String, template: String) {
	this.addCompletion(TemplateCompletion(this, input, input, template))
}
fun HrmCompletionProvider.addCmdBasic(vararg str: String) {
	str.forEach { this.addCompletion(BasicCompletion(this, it)) }
}
