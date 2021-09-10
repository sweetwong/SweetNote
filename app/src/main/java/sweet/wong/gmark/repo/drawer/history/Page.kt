package sweet.wong.gmark.repo.drawer.history

import java.io.File

data class Page(
    val file: File,
    var scrollY: Int = 0,
    var firstSelect: Boolean = true
) {

    override fun equals(other: Any?) = (other as? Page)?.file == file

    override fun hashCode() = file.hashCode()

}