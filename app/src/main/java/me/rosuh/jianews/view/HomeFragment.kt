package me.rosuh.jianews.view

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.design.widget.TabLayout.Tab
import android.support.design.widget.TabLayout.TabLayoutOnPageChangeListener
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation.RELATIVE_TO_SELF
import android.view.animation.AnimationUtils
import android.view.animation.RotateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import me.rosuh.android.jianews.R
import me.rosuh.jianews.adapter.SearchListAdapter
import me.rosuh.jianews.bean.ArticleBean
import me.rosuh.jianews.precenter.ArticleListViewPresenter
import me.rosuh.jianews.util.Const
import java.lang.ref.WeakReference

/**
 * @author rosu
 * @date 2018/9/29
 */
class HomeFragment : BaseFragment(), IListClickedView {

    private val indicatorSelectedColor: Int by lazy {
        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            resources.getColor(R.color.tab_selected_color, activity!!.theme)
        } else {
            resources.getColor(R.color.tab_selected_color)
        }
    }

    private val indicatorNormalColor: Int by lazy {
        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            resources.getColor(R.color.indicator_normal, activity!!.theme)
        } else {
            resources.getColor(R.color.indicator_normal)
        }
    }

    private var mStatePagerAdapter: FragmentStatePagerAdapter? = null

    private lateinit var tabLayoutRef: WeakReference<TabLayout>

    private var tabCustomViewList = ArrayList<TextView>()


    private val iConResIdList:List<Int> = arrayListOf(
        R.drawable.selector_bottom_bar_new,
        R.drawable.selector_bottom_bar_announcement,
        R.drawable.selector_bottom_bar_reports
    )

    private val titleResIdList:List<Int> = arrayListOf(
        R.string.tab_home,
        R.string.tab_announce,
        R.string.tab_media
    )

    lateinit var viewPager: ViewPager

    private var searchPopWindow: PopupWindow? = null

    private var searchResultBeans: ArrayList<ArticleBean> = java.util.ArrayList(Const.VALUE_LIST_DEFAULT_SIZE)

    private val mViewPresenter by lazy { ArticleListViewPresenter }

    private lateinit var rvSearch: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.home_fragment, container, false)

        viewPager = view.findViewById(R.id.vp_article_list)
        // 文章列表
        mStatePagerAdapter = object : FragmentStatePagerAdapter(activity!!.supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return ArticleListFragment.getInstances(Const.getCorrectURL(position))
            }

            override fun getCount(): Int {
                return Const.VALUE_ARTICLE_MAX_PAGES
            }
        }
        viewPager.adapter = mStatePagerAdapter
        viewPager.currentItem = Const.VALUE_ARTICLE_START_PAGE
        viewPager.offscreenPageLimit = Const.VALUE_ARTICLE_MAX_PAGES
        initTabLayout(view)
        viewPager.addOnPageChangeListener(TabLayoutOnPageChangeListener(tabLayoutRef.get()))
        return view
    }

    override fun bindMenu(): Int = R.menu.home_activity

    override fun initToolBar() {
        val toolbar = activity?.findViewById<Toolbar>(R.id.tb_home) ?: return
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        toolbar.inflateMenu(R.menu.home_activity)
        toolbar.setNavigationIcon(R.drawable.ic_menu_home)

        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(false)
            setDisplayShowTitleEnabled(false)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        if (menu == null) return
        val searchView = menu.findItem(R.id.menu_item_search).actionView as SearchView
        initSearchView(searchView)
    }

    private fun initSearchView(searchView: SearchView) {
        searchView.queryHint = "请输入关键词"
        val ivIcon = searchView.findViewById<ImageView>(android.support.v7.appcompat.R.id.search_button)
        ivIcon.setImageResource(R.drawable.ic_search)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (searchPopWindow == null) {
                    searchPopWindow = initPopUpWindows(searchView)
                    rvSearch = searchPopWindow?.contentView?.findViewById(R.id.rv_search_frag) ?: return false
                }
                searchPopWindow?.showAsDropDown(searchView)
                applyDim(activity?.window?.decorView?.rootView!! as ViewGroup, 0.5f)
                hideSoftKeyBoard(searchView)
                mViewPresenter.searchData(
                    this@HomeFragment,
                    keyWord = query
                )
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
    }

    /**
     * 弹出窗口出现时，应用背景变暗效果
     */
    private fun applyDim(parent: ViewGroup, dimAmount:Float){
        val dim = ColorDrawable(Color.BLACK)
        dim.apply{
            setBounds(0, 0, parent.width, parent.height)
            alpha = (255 * dimAmount).toInt()
        }
        parent.overlay.add(dim)
    }

    private fun clearDim(parent:ViewGroup){
        parent.overlay.clear()
    }

    private fun hideSoftKeyBoard(view:View){
        (this@HomeFragment.activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onHeaderRequestFinished(list: ArrayList<ArticleBean>) {
        searchResultBeans = list
        if (rvSearch.adapter == null){
            rvSearch.adapter = SearchListAdapter(activity!!, searchResultBeans, this@HomeFragment)
            rvSearch.layoutManager = LinearLayoutManager(activity!!)
        }else {
            (rvSearch.adapter as SearchListAdapter).updateData(searchResultBeans)
            rvSearch.adapter?.notifyDataSetChanged()
        }

    }



    override fun onUpdateDataFailed(t: Throwable) {
        t.printStackTrace()
        Toast.makeText(activity, t.message + "\n 请稍后重试", Toast.LENGTH_LONG).show()
    }

    override fun onItemClick(v:View, clickedBean: ArticleBean) {
        if (searchPopWindow?.isShowing == true){
            searchPopWindow?.dismiss()
        }
        (activity as HomeActivity).onItemClick(clickedBean)
    }

    private fun initPopUpWindows(view: View): PopupWindow {
        return PopupWindow(context).run {
            width = ViewGroup.LayoutParams.MATCH_PARENT
            height = 800
            contentView = LayoutInflater.from(context).inflate(R.layout.search_fragment, null)
            setBackgroundDrawable(resources.getDrawable(R.color.white, activity!!.theme))
            isOutsideTouchable = true
            isFocusable = true
            animationStyle = R.style.pop_animation
            this.setOnDismissListener {
                clearDim(activity?.window?.decorView?.rootView!! as ViewGroup)
            }
            this
        }
    }

    /**
     * 初始化导航栏
     * @param viewPager 传入设置好的 ViewPager 和 TabLayout 关联起来
     */
    private fun initTabLayout(view: View) {
        val tabLayout = view.findViewById<TabLayout>(R.id.tb_layout_nav)
        tabLayout.setupWithViewPager(viewPager)
        setCustomViewFroTabs(titleResIdList, iConResIdList, tabLayout)
        tabLayoutRef = WeakReference(tabLayout)

        // 初始化第一个 item 样式
        (tabLayout.getTabAt(0)?.customView as? TextView)?.setTextColor(indicatorSelectedColor)

        tabLayout.addOnTabSelectedListener(object : TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
            private val sDoubleClickedInterval = 400L
            private var mLastSelectedTime: Long = 0

            override fun onTabReselected(tab: TabLayout.Tab?) {
                super.onTabReselected(tab)
                val newestSelectedTime = System.currentTimeMillis()
                if (newestSelectedTime - mLastSelectedTime < sDoubleClickedInterval) {
                    val listFrag =
                        viewPager.adapter?.instantiateItem(viewPager, viewPager.currentItem) as? ArticleListFragment
                    listFrag?.scrollToTop()
                }
                mLastSelectedTime = newestSelectedTime
            }

            override fun onTabSelected(tab: Tab?) {
                super.onTabSelected(tab)
                val iv = (tab?.customView)?.findViewById<ImageView>(R.id.iv_tab_item_custom) ?: return
                when(tab.position){
                    0 -> {
                        val rotateAnimation = RotateAnimation(0f, 360f, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f)
                        rotateAnimation.duration = 250
                        rotateAnimation.interpolator = AccelerateDecelerateInterpolator()
                        iv.startAnimation(rotateAnimation)
                    }
                    else -> {
                        val rotateAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_tab_item_rotate)
                        rotateAnimation.repeatCount = 3
                        iv.startAnimation(rotateAnimation)
                    }
                }
            }

            override fun onTabUnselected(tab: Tab?) {
                super.onTabUnselected(tab)
//                (tab?.customView as? TextView)?.setTextColor(indicatorNormalColor)
            }
        })
    }

    private fun setCustomViewFroTabs(titleResList: List<Int>, iConResIdList:List<Int>, tabLayout: TabLayout) {
        for (i in 0..tabLayout.tabCount) {
            tabLayout.getTabAt(i)
                ?.setCustomView(R.layout.tab_item_custom_layout)
                ?.setContentDescription(titleResList[i])

            tabLayout.getTabAt(i)?.customView?.apply {
                findViewById<TextView>(R.id.tv_tab_item_custom).apply {
                    setText(titleResList[i])
                }
                findViewById<ImageView>(R.id.iv_tab_item_custom).apply{
                    setBackgroundResource(iConResIdList[i])
                }
            }
        }
    }

    companion object {
        val instance: HomeFragment
            get() = HomeFragment()
        const val HOME_FRAGMENT_TAG = "HomeFragment"
    }
}
