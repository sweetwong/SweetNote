package sweet.wong.gmark.repolist

import sweet.wong.gmark.data.Repo
import sweet.wong.gmark.utils.UIState

/**
 * Represent repository list item view model
 */
class RepoUIState(val repo: Repo) : UIState() {

    var progress: Int = 0

    var statusText = ""

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RepoUIState

        if (repo != other.repo) return false

        return true
    }

    override fun hashCode(): Int {
        return repo.hashCode()
    }

}