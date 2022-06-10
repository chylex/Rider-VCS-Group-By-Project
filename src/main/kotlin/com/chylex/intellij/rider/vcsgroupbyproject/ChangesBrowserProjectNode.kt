package com.chylex.intellij.rider.vcsgroupbyproject

import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.fileTypes.FileTypeRegistry
import com.intellij.openapi.fileTypes.UnknownFileType
import com.intellij.openapi.vcs.changes.ui.ChangesBrowserNode
import com.intellij.openapi.vcs.changes.ui.ChangesBrowserNodeRenderer
import com.jetbrains.rider.model.RdCustomLocation
import com.jetbrains.rider.model.RdProjectModelItemDescriptor
import java.io.File

class ChangesBrowserProjectNode(private val descriptor: RdProjectModelItemDescriptor) : ChangesBrowserNode<RdProjectModelItemDescriptor>(descriptor) {
	private val type = getType(descriptor)
	
	override fun getTextPresentation(): String {
		return descriptor.name
	}
	
	override fun render(renderer: ChangesBrowserNodeRenderer, selected: Boolean, expanded: Boolean, hasFocus: Boolean) {
		super.render(renderer, selected, expanded, hasFocus)
		renderer.icon = type?.icon
	}
	
	override fun compareUserObjects(o2: RdProjectModelItemDescriptor): Int {
		return descriptor.name.compareTo(o2.name)
	}
	
	override fun getSortWeight(): Int {
		return MODULE_SORT_WEIGHT
	}
	
	companion object {
		private const val serialVersionUID = 2641007635909904963L
		
		private fun getType(descriptor: RdProjectModelItemDescriptor): FileType? {
			val location = descriptor.location
			if (location !is RdCustomLocation) {
				return null
			}
			
			val fileExtension = File(location.customLocation).extension
			val fileType = FileTypeRegistry.getInstance().getFileTypeByExtension(fileExtension)
			
			return fileType.takeUnless { it === UnknownFileType.INSTANCE }
		}
	}
}
