package me.rosuh.jianews.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import me.rosuh.android.jianews.R
import me.rosuh.jianews.bean.BoardBean
import me.rosuh.jianews.util.StringUtils

/**
 *
 * @author rosuh
 * @date 2019/5/4
 */
class BoardListAdapter(data: List<BoardBean>) :
    BaseQuickAdapter<BoardBean, BaseViewHolder>(
        R.layout.item_board_normal, data
    ) {

    override fun convert(helper: BaseViewHolder?, item: BoardBean?) {
        if (item == null) return
        helper?.run {
            setText(R.id.tv_title, item.title)
            setText(R.id.tv_publish_time, StringUtils.getFormattedTime(item.created))
            setText(R.id.tv_views, if (item.views.toString().isEmpty()) "" else item.views.toString())
            setText(R.id.tv_tag, item.tag)
//            addOnClickListener(R.id.tv_title, R.id.tv_title, R.id.tv_views, R.id.tv_tag)
        }
    }
}