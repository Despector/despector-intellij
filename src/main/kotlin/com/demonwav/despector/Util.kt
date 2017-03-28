package com.demonwav.despector

import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.java.decompiler.IdeaDecompiler
import org.spongepowered.despector.Language

fun VirtualFile.decompile(isDespector: Boolean, language: Language = Language.ANY): CharSequence {
    return if (isDespector) {
        DespectorFileDecompiler(language).getText(this)
    } else {
        IdeaDecompiler().getText(this)
    }
}
