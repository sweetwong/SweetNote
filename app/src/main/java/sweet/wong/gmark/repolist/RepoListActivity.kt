package sweet.wong.gmark.repolist

import android.os.Bundle
import android.view.Menu
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import sweet.wong.gmark.R
import sweet.wong.gmark.core.EventObserver
import sweet.wong.gmark.databinding.ActivityRepoListBinding
import sweet.wong.gmark.repo.RepoActivity
import java.util.*

/**
 * Repository list page
 */
class RepoListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRepoListBinding
    private lateinit var repoListAdapter: RepoListAdapter
    private val viewModel: RepoListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Bind view
        binding = ActivityRepoListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Init ActionBar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Repository List"

        // When click fab
        binding.fab.imageTintList =
            ResourcesCompat.getColorStateList(resources, R.color.ck_red, null)
        binding.fab.setOnClickListener {
            binding.fab.isEnabled = false
            val dialogFragment = RepoAuthDialogFragment(viewModel)
            dialogFragment.onDismiss = { binding.fab.isEnabled = true }
            dialogFragment.show(supportFragmentManager, RepoAuthDialogFragment::class.java.name)
        }

        // Init RecyclerView
        repoListAdapter = RepoListAdapter(viewModel)
        binding.repoList.adapter = repoListAdapter
        viewModel.repoUIStates.observe(this@RepoListActivity) {
            repoListAdapter.submitList(it.toMutableList())
        }

        // Refresh repo list
        viewModel.refreshRepoList()

        viewModel.repoSelectEvent.observe(this, EventObserver {
            RepoActivity.start(this, it)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        })

        viewModel.repoUpdateEvent.observe(this, EventObserver {
            if (it != -1) {
                repoListAdapter.notifyItemChanged(it)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_repo_list, menu)
        return true
    }


}