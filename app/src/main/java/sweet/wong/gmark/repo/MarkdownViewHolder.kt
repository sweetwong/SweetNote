package sweet.wong.gmark.repo

import android.os.Build
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import sweet.wong.gmark.data.Page
import sweet.wong.gmark.databinding.FragmentMarkdownBinding
import sweet.wong.gmark.ext.MAIN_CATCH
import sweet.wong.gmark.ext.inflater
import sweet.wong.gmark.repo.markdown.MarkdownDelegate
import sweet.wong.gmark.utils.SnappingLinearLayoutManager

class MarkdownViewHolder(
    private val activity: AppCompatActivity,
    private val repoViewModel: RepoViewModel,
    private val binding: FragmentMarkdownBinding,
) : RecyclerView.ViewHolder(binding.root) {

    private val delegate = MarkdownDelegate(repoViewModel)

    init {
        binding.markList.itemAnimator = null
        binding.markList.layoutManager = SnappingLinearLayoutManager(itemView.context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            binding.markList.isForceDarkAllowed = false
        }
    }

    fun bind(page: Page) {
        LogUtils.d("MarkdownViewHolder bind page, position: $adapterPosition, page: $page")
        activity.lifecycleScope.launch(Dispatchers.MAIN_CATCH) {
            val source = withContext(Dispatchers.IO) { page.file.readText() }
            delegate.setMarkdown(page.file.name, binding.markList, source)

            binding.markList.scrollBy(0, page.scrollY)

            page.scrollY = 0
            binding.markList.clearOnScrollListeners()
            binding.markList.addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    page.scrollY += dy
                }
            })

        }
    }

    companion object {

        fun create(activity: AppCompatActivity, viewModel: RepoViewModel, parent: ViewGroup) =
            MarkdownViewHolder(
                activity,
                viewModel,
                FragmentMarkdownBinding.inflate(parent.inflater, parent, false)
            )

    }

}
