package com.chylex.intellij.rider.vcsgroupbyproject

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NotNullLazyKey
import com.intellij.openapi.vcs.FilePath
import com.intellij.openapi.vcs.changes.ui.BaseChangesGroupingPolicy
import com.intellij.openapi.vcs.changes.ui.ChangesBrowserNode
import com.intellij.openapi.vcs.changes.ui.ChangesGroupingPolicyFactory
import com.intellij.openapi.vcs.changes.ui.StaticFilePath
import com.intellij.openapi.vcs.changes.ui.TreeModelBuilder
import com.intellij.vcsUtil.VcsUtil
import com.jetbrains.rider.model.RdCustomLocation
import com.jetbrains.rider.model.RdProjectDescriptor
import com.jetbrains.rider.model.RdProjectModelItemDescriptor
import com.jetbrains.rider.model.RdUnloadProjectDescriptor
import com.jetbrains.rider.projectView.workspace.ProjectModelEntity
import com.jetbrains.rider.projectView.workspace.containingEntity
import com.jetbrains.rider.projectView.workspace.containingProjectEntity
import java.io.File
import javax.swing.tree.DefaultTreeModel

class ProjectChangesGroupingPolicy(private val project: Project, private val model: DefaultTreeModel) : BaseChangesGroupingPolicy() {
	override fun getParentNodeFor(nodePath: StaticFilePath, subtreeRoot: ChangesBrowserNode<*>): ChangesBrowserNode<*>? {
		val nextPolicyParent = nextPolicy?.getParentNodeFor(nodePath, subtreeRoot)
		
		val file = resolveVirtualFile(nodePath)
		if (file == null) {
			return nextPolicyParent
		}
		
		val descriptor = file.containingEntity(project)?.let(ProjectModelEntity::containingProjectEntity)?.descriptor
		if (descriptor !is RdProjectDescriptor && descriptor !is RdUnloadProjectDescriptor) {
			return nextPolicyParent
		}
		
		val grandParent = nextPolicyParent ?: subtreeRoot
		val cachingRoot = getCachingRoot(grandParent, subtreeRoot)
		
		return NODE_CACHE.getValue(cachingRoot).getOrPut(descriptor) {
			ChangesBrowserProjectNode(descriptor).also {
				it.markAsHelperNode()
				model.insertNodeInto(it, grandParent, grandParent.childCount)
				NODE_CACHE.getValue(it)[descriptor] = it
				
				val projectFolder = getFolder(descriptor)
				if (projectFolder != null) {
					val folderKey = TreeModelBuilder.staticFrom(projectFolder).key
					TreeModelBuilder.DIRECTORY_CACHE.getValue(cachingRoot)[folderKey] = it
					TreeModelBuilder.IS_CACHING_ROOT.set(it, true)
					TreeModelBuilder.DIRECTORY_CACHE.getValue(it)[folderKey] = it
				}
			}
		}
	}
	
	internal class Factory : ChangesGroupingPolicyFactory() {
		override fun createGroupingPolicy(project: Project, model: DefaultTreeModel) = ProjectChangesGroupingPolicy(project, model)
	}
	
	private companion object {
		private val NODE_CACHE = NotNullLazyKey.create<MutableMap<RdProjectModelItemDescriptor?, ChangesBrowserNode<*>>, ChangesBrowserNode<*>>("ChangesTree.ProjectCache") {
			mutableMapOf()
		}
		
		private fun getFolder(descriptor: RdProjectModelItemDescriptor): FilePath? {
			val location = descriptor.location
			if (location !is RdCustomLocation) {
				return null
			}
			
			val projectFile = File(location.customLocation)
			val projectFolder = projectFile.parent
			
			return projectFolder?.let(VcsUtil::getFilePath)
		}
	}
}
