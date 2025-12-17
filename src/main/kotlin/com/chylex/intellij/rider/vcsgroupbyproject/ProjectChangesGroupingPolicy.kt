package com.chylex.intellij.rider.vcsgroupbyproject

import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.changes.ui.ChangesBrowserNode
import com.intellij.openapi.vcs.changes.ui.ChangesGroupingPolicyFactory
import com.intellij.openapi.vcs.changes.ui.SimpleChangesGroupingPolicy
import com.intellij.openapi.vcs.changes.ui.StaticFilePath
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.backend.workspace.WorkspaceModel
import com.intellij.vcsUtil.VcsImplUtil
import com.jetbrains.rider.model.RdProjectDescriptor
import com.jetbrains.rider.model.RdProjectModelItemDescriptor
import com.jetbrains.rider.model.RdUnloadProjectDescriptor
import com.jetbrains.rider.projectView.workspace.ProjectModelEntity
import com.jetbrains.rider.projectView.workspace.containingProjectEntity
import com.jetbrains.rider.projectView.workspace.getProjectModelEntities
import com.jetbrains.rider.projectView.workspace.isProject
import com.jetbrains.rider.projectView.workspace.isProjectFile
import com.jetbrains.rider.projectView.workspace.isProjectFolder
import com.jetbrains.rider.projectView.workspace.isUnloadedProject
import javax.swing.tree.DefaultTreeModel

class ProjectChangesGroupingPolicy(private val project: Project, model: DefaultTreeModel) : SimpleChangesGroupingPolicy<RdProjectModelItemDescriptor>(model) {
	override fun getGroupRootValueFor(nodePath: StaticFilePath, node: ChangesBrowserNode<*>): RdProjectModelItemDescriptor? {
		val file = VcsImplUtil.findValidParentAccurately(nodePath.filePath)
		if (file == null) {
			return null
		}
		
		val descriptor = getSingleProjectEntity(file, project)?.descriptor
		if (descriptor !is RdProjectDescriptor && descriptor !is RdUnloadProjectDescriptor) {
			return null
		}
		
		return descriptor
	}
	
	override fun createGroupRootNode(value: RdProjectModelItemDescriptor): ChangesBrowserNode<*> {
		return ChangesBrowserProjectNode(value).also { it.markAsHelperNode() }
	}
	
	internal class Factory : ChangesGroupingPolicyFactory() {
		override fun createGroupingPolicy(project: Project, model: DefaultTreeModel) = ProjectChangesGroupingPolicy(project, model)
	}
	
	private companion object {
		private fun getSingleProjectEntity(file: VirtualFile, project: Project): ProjectModelEntity? {
			val workspaceModel = WorkspaceModel.getInstance(project)
			val entities = walkFileParentsUntilResultIsNotEmpty(file) { workspaceModel.getProjectModelEntities(it, project) }
			if (entities == null) {
				return null
			}
			
			return entities
				.filter(::isProjectOrProjectFile)
				.map(ProjectModelEntity::containingProjectEntity)
				.toSet()
				.singleOrNull()
		}
		
		private inline fun <T> walkFileParentsUntilResultIsNotEmpty(leafFile: VirtualFile, getValue: (VirtualFile) -> List<T>?): List<T>? {
			var file: VirtualFile? = leafFile
			
			while (file != null) {
				val value = getValue(file)
				if (value.isNullOrEmpty()) {
					file = file.parent
				}
				else {
					return value
				}
			}
			
			return null
		}
		
		private fun isProjectOrProjectFile(entity: ProjectModelEntity): Boolean {
			return entity.isProjectFile() || entity.isProjectFolder() || entity.isProject() || entity.isUnloadedProject()
		}
	}
}
