package com.n9mtq4.hrm2j.gui

import org.jdesktop.swingx.MultiSplitLayout

/**
 * Created by will on 7/25/16 at 5:36 PM.
 *
 * @author Will "n9Mtq4" Bresnahan
 */
class ThreeVerticalModel(vararg weights: Double) : MultiSplitLayout.Split() {
	
	companion object {
		val P1 = "1"
		val P2 = "2"
		val P3 = "3"
	}
	
	init {
		
		isRowLayout = true
		val p1 = MultiSplitLayout.Leaf(P1)
		val p2 = MultiSplitLayout.Leaf(P2)
		val p3 = MultiSplitLayout.Leaf(P3)
		
		p1.weight = weights[0]
		p2.weight = weights[1]
		p3.weight = weights[2]
		
		setChildren(p1, MultiSplitLayout.Divider(), p2, MultiSplitLayout.Divider(), p3)
		
	}
	
}
