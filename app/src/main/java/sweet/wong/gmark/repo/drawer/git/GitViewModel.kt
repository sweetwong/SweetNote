package sweet.wong.gmark.repo.drawer.git

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sweet.wong.gmark.R
import sweet.wong.gmark.core.getString
import sweet.wong.gmark.core.toast
import sweet.wong.gmark.data.Repo
import sweet.wong.gmark.ext.IO_CATCH
import sweet.wong.gmark.git.setCredential
import sweet.wong.gmark.sp.SPUtils

class GitViewModel : ViewModel() {

    lateinit var repo: Repo

    val diffUIStates = MutableLiveData<List<DiffUIState>>()

    val commitMessage = MutableLiveData<String>()

    fun refreshDiffList() = viewModelScope.launch(Dispatchers.IO_CATCH) {
        val uiStates = mutableListOf<DiffUIState>()
        repo.git.diff().call().forEach {
            uiStates.add(DiffUIState(it))
        }
        diffUIStates.postValue(uiStates)
    }

    fun commit() {
        val commitMessage = this.commitMessage.value
        if (commitMessage.isNullOrBlank()) {
            toast("Commit message is empty")
            return
        }

        val username = SPUtils.settings.getString(getString(R.string.pref_user_name), null)
        if (username.isNullOrEmpty()) {
            toast("Please set your username")
            return
        }

        val email = SPUtils.settings.getString(getString(R.string.pref_user_email), null)
        if (email.isNullOrEmpty()) {
            toast("Please set your email")
            return
        }

        viewModelScope.launch(Dispatchers.IO_CATCH) {
            val git = repo.git
            // Add all
            git.add()
                .addFilepattern(".")
                .call()

            // Commit
            git.commit()
                .setAuthor(username, email)
                .setAll(true)
                .setMessage(commitMessage)
                .call()

            toast("Commit success")
        }
    }

    fun push() {
        viewModelScope.launch(Dispatchers.IO_CATCH) {
            val git = repo.git

            git.push()
                .setCredential(repo)
                .call()

            toast("Push success")
        }
    }

}