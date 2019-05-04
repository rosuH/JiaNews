package me.rosuh.jianews.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.board_detail_activity.btn_exit
import kotlinx.android.synthetic.main.board_detail_activity.tv_author
import kotlinx.android.synthetic.main.board_detail_activity.tv_content
import kotlinx.android.synthetic.main.board_detail_activity.tv_publish_time
import kotlinx.android.synthetic.main.board_detail_activity.tv_tag
import kotlinx.android.synthetic.main.board_detail_activity.tv_title
import me.rosuh.android.jianews.R
import me.rosuh.jianews.bean.BoardBean
import me.rosuh.jianews.util.StringUtils

/**
 *
 * @author rosuh
 * @date 2019/5/4
 */
class BoardDetailActivity : AppCompatActivity() {
    private var boardBean: BoardBean? = null

    companion object {
        fun start(context: Context, boardBean: BoardBean){
            val intent = Intent(context, BoardDetailActivity::class.java)
            intent.putExtra("boardBean", boardBean)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(android.support.design.R.style.Theme_MaterialComponents_Light_NoActionBar)
        setContentView(R.layout.board_detail_activity)
        boardBean = intent?.getParcelableExtra("boardBean")
        boardBean?.let {
            tv_title.text = it.title
            tv_content.text = it.content
            tv_publish_time.text = StringUtils.getFormattedTime(it.created)
            tv_tag.text = it.tag
            tv_author.text = it.author
        }
        btn_exit.setOnClickListener {
            finish()
        }
    }
}