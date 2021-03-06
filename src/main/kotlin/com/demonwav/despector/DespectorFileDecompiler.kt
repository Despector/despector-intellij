package com.demonwav.despector

import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.compiled.ClassFileDecompilers
import org.spongepowered.despector.Despector
import org.spongepowered.despector.Language

class DespectorFileDecompiler(private val language: Language = Language.ANY) : ClassFileDecompilers.Light() {

    override fun accepts(file: VirtualFile) = file.extension == "class"

    override fun getText(file: VirtualFile): CharSequence {
        try {
            val stream = file.inputStream
            return BANNER + Despector.decompile(stream, language) { internalName ->
                val name = internalName.split("/").toTypedArray()
                val firstDir = name[0]

                var dir = file.parent
                while (dir.name != firstDir) {
                    dir = dir.parent ?: return@decompile null
                }
                // One more time, get it's parent so we can find the class relative to it
                dir = dir.parent

                name[name.lastIndex] = name[name.lastIndex] + ".class"
                return@decompile VfsUtil.findRelativeFile(dir, *name)?.inputStream
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw CannotDecompileException(e)
        }
    }

    companion object {
        private const val BANNER =
            "//\n" +
            "// Decompiled source code generated by Despector\n" +
            "// https://github.com/Deamon5550/Despector\n" +
            "//\n\n"
    }
}
