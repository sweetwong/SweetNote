package sweet.wong.gmark.repolist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import sweet.wong.gmark.core.*
import sweet.wong.gmark.data.DaoManager
import sweet.wong.gmark.data.Repo
import sweet.wong.gmark.git.Clone

/**
 * Git repository list view model
 */
class RepoListViewModel : ViewModel() {

    val repoUIStates = NonNullLiveData<MutableList<RepoUIState>>(mutableListOf())

    val repoUpdateEvent = MutableLiveData<Event<Int>>()
    val repoSelectEvent = MutableLiveData<Event<Repo>>()

    fun refreshRepoList() {
        // FIXME: 2021/9/2 这里刷新可能会导致丢失UI状态
        io {
            try {
                val repos = DaoManager.repoDao.getAll()
                ui {
                    val repoUtiStatesValue = mutableListOf<RepoUIState>()
                    repos.forEach { repo ->
                        repoUtiStatesValue.add(RepoUIState(repo))
                    }
                    repoUIStates.value = repoUtiStatesValue
                }
            } catch (e: Exception) {
                toast("Refresh repo list failed", e)
            }
        }
    }

    fun addNewRepo(repo: Repo) {
        io {
            try {
                DaoManager.repoDao.insertAll(repo)
                ui {
                    val repoUIState = RepoUIState(repo)
                    repoUIStates.value = repoUIStates.value.apply { add(repoUIState) }
                    startClone(repoUIState)
                }
            } catch (e: Exception) {
                toast("Add new repo failed", e)
            }
        }
    }

    fun deleteRepo(repo: Repo) {
        io {
            try {
                DaoManager.repoDao.delete(repo)
                ui {
                    refreshRepoList()
                }
            } catch (e: Exception) {
                toast("Delete repo failed", e)
            }
        }
    }

    private fun startClone(repoUIState: RepoUIState) {
        val repo = repoUIState.repo

        // TODO: 2021/9/3 添加进度条
        Clone.clone(
            repo.url,
            repo.localPath,
            repo.username,
            repo.password,
            repo.ssh,
            RepoCloneMonitor(this, repoUIState)
        )
    }

}