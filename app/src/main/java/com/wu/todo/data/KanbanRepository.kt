package com.wu.todo.data

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import java.io.IOException

/**
 * 通过 Storage Access Framework（SAF）读写 Obsidian 的看板 .md 文件。
 * 用 `content://` Uri 而不是文件路径，兼容 Android 11+ 的分区存储。
 */
class KanbanRepository(private val context: Context) {

    fun readText(uri: Uri): String {
        return context.contentResolver.openInputStream(uri)?.use { stream ->
            stream.bufferedReader(Charsets.UTF_8).readText()
        } ?: throw IOException("无法读取文件（Uri 无效或已失效）")
    }

    fun writeText(uri: Uri, text: String) {
        val out = context.contentResolver.openOutputStream(uri, "wt")
            ?: throw IOException("无法写入文件（可能没有写入权限）")
        out.use { it.write(text.toByteArray(Charsets.UTF_8)) }
    }

    fun displayName(uri: Uri): String? {
        return runCatching {
            DocumentFile.fromSingleUri(context, uri)?.name
                ?: DocumentFile.fromTreeUri(context, uri)?.name
        }.getOrNull()
    }

    /** 列出某个文件夹（文档树）下的所有 .md 文件 */
    fun listMarkdownInTree(uri: Uri): List<Pair<String, Uri>> {
        val tree = DocumentFile.fromTreeUri(context, uri) ?: return emptyList()
        return tree.listFiles()
            .filter { it.isFile && (it.name ?: "").endsWith(".md", ignoreCase = true) }
            .mapNotNull { doc -> doc.name?.let { name -> name to doc.uri } }
            .sortedBy { it.first }
    }
}
