package app.itgungnir.kwa.hierarchy

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import app.itgungnir.kwa.R
import app.itgungnir.kwa.common.WebActivity
import app.itgungnir.kwa.common.widget.easy_adapter.BaseDelegate
import app.itgungnir.kwa.common.widget.easy_adapter.EasyAdapter
import kotlinx.android.synthetic.main.listitem_hierarchy_child.view.*
import my.itgungnir.apt.router.api.Router

class HierarchyChildDelegate : BaseDelegate<HierarchyChildState.ArticleVO>() {

    override fun layoutId(): Int = R.layout.listitem_hierarchy_child

    override fun onCreateVH(container: View) {}

    @SuppressLint("SetTextI18n")
    override fun onBindVH(
        item: HierarchyChildState.ArticleVO,
        holder: EasyAdapter.VH,
        position: Int,
        payloads: MutableList<Bundle>
    ) {

        holder.render(item) {

            this.setOnClickListener {
                Router.instance.with(context)
                    .target(WebActivity)
                    .addParam("title", item.title)
                    .addParam("url", item.link)
                    .go()
            }

            author.text = "\ue830 ${item.author}"

            title.text = item.title

            date.text = item.date
        }
    }
}