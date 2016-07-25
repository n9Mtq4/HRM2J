package com.n9mtq4.hrm2j.util;

import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 * Created by will on 6/2/16 at 11:13 PM.
 *
 * @author Will "n9Mtq4" Bresnahan
 */
public class AddMenuAccelerator {
	
	private AddMenuAccelerator() {}
	
	public static void addKey(JMenuItem menuItem, char key, int mask) {
		menuItem.setAccelerator(KeyStroke.getKeyStroke(Character.toUpperCase(key), mask));
	}
	
}
