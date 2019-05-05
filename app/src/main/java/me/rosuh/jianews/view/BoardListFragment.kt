package me.rosuh.jianews.view

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.support.design.button.MaterialButton
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TextInputEditText
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import com.orhanobut.hawk.Hawk
import io.reactivex.disposables.Disposable
import me.rosuh.android.jianews.R
import me.rosuh.jianews.adapter.BoardListAdapter
import me.rosuh.jianews.bean.BoardBean
import me.rosuh.jianews.bean.User
import me.rosuh.jianews.network.ApiService
import me.rosuh.jianews.util.CustomLoadMoreView
import me.rosuh.jianews.util.LoginUtil
import me.rosuh.jianews.util.LoginUtil.LoginStatusCallBack

/**
 *
 * @author rosuh
 * @date 2019/5/4
 */
class BoardListFragment : Fragment(){

    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAddBoard: FloatingActionButton
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var boardAdapter: BoardListAdapter
    private var boardsList: ArrayList<BoardBean> = ArrayList()
    private var disposableList: ArrayList<Disposable> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.boards_list_fragment, container, false)
        swipeRefreshLayout = view.findViewById(R.id.swf_board_list)
        swipeRefreshLayout.setOnRefreshListener {
            loadData(0)
        }
        recyclerView = view.findViewById(R.id.rv_boards_list)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        val itemAnimator = DefaultItemAnimator()
        itemAnimator.apply {
            addDuration = 1400
            changeDuration = 1400
            moveDuration = 1400
            removeDuration = 1400
        }
        recyclerView.itemAnimator = itemAnimator

        boardAdapter = BoardListAdapter(ArrayList())
        boardAdapter.setLoadMoreView(CustomLoadMoreView())
        boardAdapter.setEmptyView(R.layout.empty_view, recyclerView)
        boardAdapter.setOnLoadMoreListener({
            loadData((recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition())
        }, recyclerView)
        boardAdapter.setOnItemClickListener { adapter, view, position ->
            val boardBean = adapter.data[position] as BoardBean
            BoardDetailActivity.start(this.activity!!, boardBean)
        }
        recyclerView.adapter = boardAdapter

        fabAddBoard = view.findViewById(R.id.fab_board_add)
        fabAddBoard.setOnClickListener {
            LoginUtil.checkLogin(this@BoardListFragment.activity!!, object : LoginStatusCallBack {
                override fun logged() {
                    goConnect()
                }
            })
        }
        return view
    }

    private fun goConnect() {
        if (Hawk.contains("user")) {
            val addBoardDialog= AddBoardDialog()
            addBoardDialog.boardListFragment = this@BoardListFragment
            addBoardDialog.show(fragmentManager, "addBoardDialog")
        }
    }

    @SuppressLint("CheckResult")
    private fun loadData(offset: Int) {
        ApiService
            .getBoards(offset)
            .doOnSubscribe {
                disposableList.add(it)
            }
            .subscribe(
                {
                    when {
                        it.isEmpty() -> boardAdapter.loadMoreEnd()
                        offset == 0 -> {
                            boardAdapter.setNewData(it)
                            boardAdapter.loadMoreComplete()
                        }
                        else -> {
                            boardAdapter.addData(ArrayList(it))
                            boardAdapter.loadMoreComplete()
                        }
                    }
                    swipeRefreshLayout.isRefreshing = false
                },
                {
                    Toast.makeText(this@BoardListFragment.activity, "请求发生错误\n" + it.cause, Toast.LENGTH_SHORT).show()
                    it.printStackTrace()
                    swipeRefreshLayout.isRefreshing = false
                    disposeAll()
                }
            )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefreshLayout.isRefreshing = true
        loadData(0)
    }

    class AddBoardDialog: DialogFragment() {
        private lateinit var tiTitle: TextInputEditText
        private lateinit var tiContent: TextInputEditText
        private lateinit var tiTag: TextInputEditText
        private lateinit var btnPost: MaterialButton
        var boardListFragment:BoardListFragment? = null

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val view = inflater.inflate(R.layout.add_board_dialog, container, false)
            tiTitle = view.findViewById(R.id.ti_title)
            tiContent = view.findViewById(R.id.ti_content)
            tiTag = view.findViewById(R.id.ti_tag)
            btnPost = view.findViewById(R.id.btn_post_board)
            btnPost.setOnClickListener {
                if (checkInfo()) {
                    val pDialog = SweetAlertDialog(this@AddBoardDialog.activity, SweetAlertDialog.PROGRESS_TYPE)
                    pDialog.titleText = getString(R.string.board_creating)
                    pDialog.setCancelable(true)
                    var user: User? = null
                    if (Hawk.contains("user")) {
                        user = Hawk.get<User>("user")
                    }
                    if (user == null) {
                        Toast.makeText(this.activity, "貌似还没有登录？", Toast.LENGTH_SHORT).show()
                        LoginUtil.checkLogin(this.activity!!, object : LoginStatusCallBack {
                            override fun logged() {
                                user = Hawk.get<User>("user")
                                postBoard(pDialog, user!!)
                            }
                        })
                    } else {
                        postBoard(pDialog, user!!)
                    }
                }
            }
            return view
        }

        override fun onResume() {
            super.onResume()
            val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
            val height = (resources.displayMetrics.heightPixels * 0.8).toInt()
            val params = dialog.window!!.attributes
            params.width = width
            params.height = height
            dialog.window!!.attributes = params as android.view.WindowManager.LayoutParams
        }

        @SuppressLint("CheckResult")
        private fun postBoard(pDialog: SweetAlertDialog, user: User) {
            val boardEntity = ApiService.BoardEntity(
                title = tiTitle.text.toString(),
                content = tiContent.text.toString(),
                board_tag = tiTag.text.toString(),
                author = user.name
            )
            ApiService
                .addBoard(boardEntity)
                .doOnSubscribe {
                    pDialog.show()
                }
                .subscribe(
                    {
                        pDialog.run {
                            titleText = "创建成功"
                            changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                            setOnDismissListener {
                                this@AddBoardDialog.dismiss()
                            }
                        }
                    },
                    {
                        pDialog.run {
                            titleText = "创建失败"
                            contentText = it.message
                            changeAlertType(SweetAlertDialog.ERROR_TYPE)
                        }
                    }
                )
        }

        private fun checkInfo(): Boolean {
            var user: User? = null
            if (Hawk.contains("user")) {
                user = Hawk.get<User>("user")
            }
            return !(tiTag.text.isNullOrEmpty() || tiContent.text.isNullOrBlank() || user?.account?.isEmpty() ?: true)
        }

        override fun onDismiss(dialog: DialogInterface?) {
            super.onDismiss(dialog)
            boardListFragment?.loadData(0)
        }
    }

    private fun disposeAll() {
        for (dispose in disposableList) {
            dispose.dispose()
        }
    }
}
