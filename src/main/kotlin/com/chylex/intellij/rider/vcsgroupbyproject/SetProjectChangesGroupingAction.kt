package com.chylex.intellij.rider.vcsgroupbyproject

import com.intellij.openapi.vcs.changes.actions.SetChangesGroupingAction
import com.intellij.openapi.vcs.changes.ui.ChangesGroupingSupport

class SetProjectChangesGroupingAction : SetChangesGroupingAction() {
	/**
	 * Uses [ChangesGroupingSupport.MODULE_GROUPING] because of [com.intellij.openapi.vcs.changes.ui.ChangesTree.DEFAULT_GROUPING_KEYS].
	 */
	override val groupingKey
		get() = ChangesGroupingSupport.MODULE_GROUPING
	
	init {
		// If the hardcoded name changes, the changesGroupingPolicy key in plugin.xml must be updated too.
		assert(ChangesGroupingSupport.MODULE_GROUPING == "module")
	}
}
