package app.itgungnir.kwa.search.result

import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import app.itgungnir.kwa.R
import app.itgungnir.kwa.common.SearchResultActivity
import app.itgungnir.kwa.common.popToast
import app.itgungnir.kwa.common.widget.easy_adapter.EasyAdapter
import app.itgungnir.kwa.common.widget.easy_adapter.bind
import app.itgungnir.kwa.common.widget.list_footer.ListFooter
import app.itgungnir.kwa.common.widget.status_view.StatusView
import kotlinx.android.synthetic.main.activity_search_result.*
import my.itgungnir.apt.router.annotation.Route
import my.itgungnir.rxmvvm.core.mvvm.BaseActivity
import my.itgungnir.rxmvvm.core.mvvm.buildActivityViewModel

@Route(SearchResultActivity)
class SearchResultActivity : BaseActivity() {

    private var listAdapter: EasyAdapter? = null

    private var footer: ListFooter? = null

    private val viewModel by lazy {
        buildActivityViewModel(
            activity = this,
            viewModelClass = SearchResultViewModel::class.java
        )
    }

    override fun layoutId(): Int = R.layout.activity_search_result

    override fun initComponent() {

        val key = intent.extras?.getString("key") ?: ""

        headBar.title("\"$key\"的搜索结果")
            .back { finish() }

        searchResultPage.apply {
            // Refresh Layout
            refreshLayout().setOnRefreshListener {
                viewModel.getSearchResult(key)
            }
            // Status View
            statusView().addDelegate(StatusView.Status.SUCCEED, R.layout.status_view_list) {
                val list = it.findViewById<RecyclerView>(R.id.list)
                listAdapter = list.bind(delegate = SearchResultDelegate())
                footer = ListFooter.Builder()
                    .bindTo(list)
                    .doOnLoadMore {
                        if (!searchResultPage.refreshLayout().isRefreshing) {
                            viewModel.loadMoreSearchResult(key)
                        }
                    }.build()
            }.addDelegate(StatusView.Status.EMPTY, R.layout.status_view_list_empty) {
                it.findViewById<TextView>(R.id.tip).text = "搜索结果为空~"
            }
        }

        // Init data
        viewModel.getSearchResult(key)
    }

    override fun observeVM() {

        viewModel.pick(SearchResultState::refreshing)
            .observe(this, Observer { refreshing ->
                refreshing?.a?.let {
                    searchResultPage.refreshLayout().isRefreshing = it
                }
            })

        viewModel.pick(SearchResultState::items, SearchResultState::hasMore)
            .observe(this, Observer { states ->
                states?.let {
                    when (it.a.isNotEmpty()) {
                        true -> searchResultPage.statusView().succeed { listAdapter?.update(states.a) }
                        else -> searchResultPage.statusView().empty { }
                    }
                    footer?.onLoadSucceed(it.b)
                }
            })

        viewModel.pick(SearchResultState::loading)
            .observe(this, Observer { loading ->
                if (loading?.a == true) {
                    footer?.onLoading()
                }
            })

        viewModel.pick(SearchResultState::error)
            .observe(this, Observer { error ->
                error?.a?.message?.let {
                    popToast(it)
                    footer?.onLoadFailed()
                }
            })
    }
}