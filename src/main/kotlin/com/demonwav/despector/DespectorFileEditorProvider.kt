package com.demonwav.despector

import com.intellij.codeHighlighting.BackgroundEditorHighlighter
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorLocation
import com.intellij.openapi.fileEditor.FileEditorPolicy
import com.intellij.openapi.fileEditor.FileEditorProvider
import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.fileEditor.impl.FileDocumentManagerImpl.HARD_REF_TO_DOCUMENT_KEY
import com.intellij.openapi.fileEditor.impl.text.PsiAwareTextEditorProvider
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.components.JBTabbedPane
import com.intellij.util.PlatformIcons
import java.beans.PropertyChangeListener
import javax.swing.JTabbedPane
import javax.swing.SwingConstants

class DespectorFileEditorProvider : FileEditorProvider {

    override fun getEditorTypeId() = "DESPECTOR_EDITOR_TYPE_ID"
    override fun accept(project: Project, file: VirtualFile) = file.extension == "class"

    override fun createEditor(project: Project, file: VirtualFile): FileEditor {
        val first = PsiAwareTextEditorProvider()
        val second = PsiAwareTextEditorProvider()

        // Make sure the key isn't set
        file.putUserData(DESPECTOR_KEY, null)
        val firstFile = first.createEditor(project, file)

        file.putUserData(DESPECTOR_KEY, true)
        // Clear document cache, force it to reload
        file.removeDocumentCache()
        val secondFile = second.createEditor(project, file)

        file.putUserData(DESPECTOR_KEY, null)

        return TabbedFileEditor(firstFile, secondFile)
    }

    override fun getPolicy() = FileEditorPolicy.HIDE_DEFAULT_EDITOR

    // TODO: Hack :\
    private fun VirtualFile.removeDocumentCache() {
        this.putUserData(HARD_REF_TO_DOCUMENT_KEY, null)
        val instance = FileDocumentManager.getInstance()
        val method = instance.javaClass.getDeclaredMethod("removeDocumentFromCache", VirtualFile::class.java) ?: return
        method.isAccessible = true
        method.invoke(instance, this)
    }
}

val DESPECTOR_KEY = Key.create<Boolean>("DESPECTOR_KEY")

class TabbedFileEditor(private val firstEditor: FileEditor, private val secondEditor: FileEditor) : FileEditor {

    val pane: JBTabbedPane = JBTabbedPane(SwingConstants.TOP, JTabbedPane.SCROLL_TAB_LAYOUT)
    val firstIsOpen
        get() = pane.selectedIndex == 0

    init {
        pane.insertTab("Despector", PlatformIcons.CLASS_ICON, firstEditor.component, "Decompiled by Despector", 0)
        pane.insertTab("FernFlower", PlatformIcons.CLASS_ICON, secondEditor.component, "Decompile by FernFlower", 1)

        pane.selectedComponent = firstEditor.component
    }

    override fun isModified() = firstEditor.isModified || secondEditor.isModified

    override fun addPropertyChangeListener(listener: PropertyChangeListener) {
        firstEditor.addPropertyChangeListener(listener)
        secondEditor.addPropertyChangeListener(listener)
    }

    override fun getName() = "Decompiler"

    override fun setState(state: FileEditorState) {
    }

    override fun getComponent() = pane

    override fun getPreferredFocusedComponent() = pane

    override fun <T : Any?> getUserData(key: Key<T>): T? {
        if (firstIsOpen) {
            return firstEditor.getUserData(key)
        } else {
            return secondEditor.getUserData(key)
        }
    }

    override fun selectNotify() {
        firstEditor.selectNotify()
        secondEditor.selectNotify()
    }

    override fun <T : Any?> putUserData(key: Key<T>, value: T?) {}

    override fun getCurrentLocation(): FileEditorLocation? {
        if (firstIsOpen) {
            return firstEditor.currentLocation
        } else {
            return secondEditor.currentLocation
        }
    }

    override fun deselectNotify() {
        firstEditor.deselectNotify()
        secondEditor.deselectNotify()
    }

    override fun getBackgroundHighlighter(): BackgroundEditorHighlighter? {
        if (firstIsOpen) {
            return firstEditor.backgroundHighlighter
        } else {
            return secondEditor.backgroundHighlighter
        }
    }

    override fun isValid() = firstEditor.isValid && secondEditor.isValid

    override fun removePropertyChangeListener(listener: PropertyChangeListener) {
        firstEditor.removePropertyChangeListener(listener)
        secondEditor.removePropertyChangeListener(listener)
    }

    override fun dispose() {
        Disposer.dispose(firstEditor)
        Disposer.dispose(secondEditor)
    }
}
