package com.demonwav.despector

import com.intellij.ide.highlighter.JavaFileType
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.highlighter.EditorHighlighterFactory
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.kotlin.idea.KotlinFileType
import org.spongepowered.despector.Language
import javax.swing.JComboBox
import javax.swing.JPanel

class DespectorToolbar(private val project: Project, private val file: VirtualFile, private val editor: EditorEx) {

    lateinit var panel: JPanel
        private set
    private lateinit var combo: JComboBox<String>

    init {
        combo.addActionListener {
            runWriteAction {
                when (combo.selectedIndex) {
                    0 -> setLang(editor, Language.ANY, JavaFileType.INSTANCE) // TODO: Determine language dynamically
                    1 -> setLang(editor, Language.JAVA, JavaFileType.INSTANCE)
                    2 -> setLang(editor, Language.KOTLIN, KotlinFileType.INSTANCE)
                }
            }
        }
    }

    private fun setLang(editor: EditorEx, lang: Language, fileType: FileType) {
        editor.document.setText(file.decompile(true, lang))
        editor.highlighter = EditorHighlighterFactory.getInstance().createEditorHighlighter(project, fileType)
    }
}
