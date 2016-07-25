package com.n9mtq4.hrm2j.gui

import com.n9mtq4.hrm2j.util.AddMenuAccelerator
import com.n9mtq4.kotlin.extlib.ignore
import com.n9mtq4.kotlin.extlib.pst
import java.awt.Component
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import javax.swing.JCheckBoxMenuItem
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem

/**
 * Created by will on 3/18/16 at 1:01 PM.
 *
 * Allows you to build a JMenuBar easily.
 *
 * based loosely off of
 * http://try.kotlinlang.org/#/Examples/Longer%20examples/HTML%20Builder/HTML%20Builder.kt
 *
 * @author Will "n9Mtq4" Bresnahan
 */
abstract class MenuElement(val parent: MenuElement?) {
	val children = arrayListOf<MenuElement>()
	val component: Component by lazy { generateComponent() }
	init {
		parent?.let {
			parent.children.add(this)
		}
	}
	abstract fun generateComponent(): Component
}

interface Actionable

class MenuBarKt() : MenuElement(null) {
	override fun generateComponent() = JMenuBar().apply {
		children.map { it.component }.forEach { add(it) }
	}
}

class MenuListKt(parent: MenuElement, val text: String) : MenuElement(parent) {
	override fun generateComponent() = JMenu(text).apply { children.map { it.component }.forEach { add(it) } }
}

class MenuItemKt(parent: MenuElement, val text: String) : MenuElement(parent), Actionable {
	override fun generateComponent() = JMenuItem(text)
}

class MenuCheckboxItemKt(parent: MenuElement, val text: String) : MenuElement(parent), Actionable {
	override fun generateComponent() = JCheckBoxMenuItem(text)
}

inline fun menuBar(init: MenuBarKt.() -> Unit) = MenuBarKt().apply { init() }.component as JMenuBar

inline fun MenuListKt.menuItem(text: String) = MenuItemKt(this, text)
inline fun MenuBarKt.menuItem(text: String) = MenuItemKt(this, text)

inline fun MenuListKt.menuCheckboxItem(text: String) = MenuCheckboxItemKt(this, text)
inline fun MenuBarKt.menuCheckboxItem(text: String) = MenuCheckboxItemKt(this, text)

inline fun MenuListKt.menuList(text: String, init: MenuListKt.() -> Unit) = MenuListKt(this, text).apply(init)
inline fun MenuBarKt.menuList(text: String, init: MenuListKt.() -> Unit) = MenuListKt(this, text).apply(init)

inline fun MenuListKt.applyOnMenu(init: JMenu.() -> Unit) = this.apply { init(component as JMenu) }
inline fun MenuItemKt.applyOnMenuItem(init: JMenuItem.() -> Unit) = this.apply { init(component as JMenuItem) }
inline fun MenuCheckboxItemKt.applyOnMenuCheckboxItem(init: JCheckBoxMenuItem.() -> Unit) = this.apply { init(component as JCheckBoxMenuItem) }

inline fun MenuItemKt.onAction(crossinline body: (ActionEvent) -> Unit) = this.applyOnMenuItem { addActionListener { body.invoke(it) } }
inline fun MenuCheckboxItemKt.onAction(crossinline body: (ActionEvent) -> Unit) = this.applyOnMenuCheckboxItem { addActionListener { body.invoke(it) } }

inline fun MenuCheckboxItemKt.onValueUpdate(crossinline body: (Boolean) -> Unit) = this.applyOnMenuCheckboxItem { addActionListener { body.invoke((it.source as JCheckBoxMenuItem).isSelected) } }

fun MenuItemKt.shortcut(key: Char, shift: Boolean = false, alt: Boolean = false, maskKey: Int = getMaskKey()) = this.applyOnMenuItem {
	val mask = maskKey or (if (shift) KeyEvent.SHIFT_DOWN_MASK else 0) or (if (alt) KeyEvent.ALT_DOWN_MASK else 0)
//	TODO: wtf kotlin? you mess up accelerators!?
	AddMenuAccelerator.addKey(this, key, mask)
/*	this.accelerator = KeyStroke.getKeyStroke(key,
			maskKey or 
			(if (shift) KeyEvent.SHIFT_DOWN_MASK else 0) or 
			(if (alt) KeyEvent.ALT_DOWN_MASK else 0))*/
}

private fun getMaskKey() = if (System.getProperty("os.name").contains("mac", ignoreCase = true)) KeyEvent.META_DOWN_MASK else KeyEvent.CTRL_DOWN_MASK

//TODO: requires KotlinExtLibs
inline fun MenuItemKt.onActionSafePst(crossinline body: (ActionEvent) -> Unit) = onAction { pst { body.invoke(it) } }
inline fun MenuItemKt.onActionSafeIgnore(crossinline body: (ActionEvent) -> Unit) = onAction { ignore { body.invoke(it) } }
