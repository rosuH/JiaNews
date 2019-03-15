package me.rosuh.jianews.view

import android.animation.ObjectAnimator
import android.graphics.Typeface
import android.graphics.drawable.Animatable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.design.widget.TabLayout.Tab
import android.support.design.widget.TabLayout.TabLayoutOnPageChangeListener
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v4.widget.ContentLoadingProgressBar
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
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast

import me.rosuh.android.jianews.R
import me.rosuh.jianews.adapter.SearchListAdapter
import me.rosuh.jianews.bean.ArticleBean
import me.rosuh.jianews.precenter.ArticleListViewPresenter
import me.rosuh.jianews.util.Const
import me.rosuh.jianews.util.ViewUtils
import java.lang.ref.WeakReference

/**
 * @author rosu
 * @date 2018/9/29
 */
class HomeFragment : BaseFragment(), IListClickedView {

    // Tab 选中时字体大小
    private val endTextSize = 18f
    // Tab 未选中时字体大小
    private var startTextSize = 14f
    // Tab 字体大小变换时长
    private val animationDuration = 85L

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
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (searchPopWindow == null) {
                    searchPopWindow = initPopUpWindows(searchView)
                    rvSearch = searchPopWindow?.contentView?.findViewById(R.id.rv_search_frag) ?: return false
                } else {
                    searchPopWindow?.showAsDropDown(searchView)
                }

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

//    private fun switchProgressBar(){
//        searchPopWindow?.contentView?.findViewById<ContentLoadingProgressBar>(R.id.pb_searching).apply {
//            if (this?.isShown  == true){
//                this.hide()
//            }else {
//                this?.show()
//            }
//        }
//    }


    override fun onUpdateDataFailed(t: Throwable) {
        t.printStackTrace()
        Toast.makeText(activity, t.message + "\n 请稍后重试", Toast.LENGTH_LONG).show()
    }

    override fun onItemClick(clickedBean: ArticleBean) {
        if (searchPopWindow?.isShowing == true){
            searchPopWindow?.dismiss()
        }
        (activity as HomeActivity).onItemClick(clickedBean)
    }

    private fun initPopUpWindows(view: View): PopupWindow {
        return PopupWindow(context).run {
            width = ViewGroup.LayoutParams.MATCH_PARENT
            height = 600
            contentView = LayoutInflater.from(context).inflate(R.layout.search_fragment, null)
            setBackgroundDrawable(resources.getDrawable(R.color.very_light_gray, activity!!.theme))
            isOutsideTouchable = true
            isFocusable = true
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
        tabLayoutRef = WeakReference(tabLayout)
        setCustomViewFroTabs(
            arrayListOf(
                R.string.tab_home,
                R.string.tab_announce,
                R.string.tab_media
            ), tabLayout
        )
        (tabLayoutRef.get()?.getTabAt(0)?.customView as TextView).let {
            startTextSize = (it.textSize / resources.displayMetrics.scaledDensity)
            it
        }
        // 初始化首个 Tab 的样式
        (tabLayout.getTabAt(0)?.customView as TextView).also {
            it.textSize = endTextSize
            it.setTypeface(Typeface.DEFAULT, Typeface.BOLD)
            it.setTextColor(indicatorSelectedColor)
        }

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
                for (customView in tabCustomViewList) {
                    if (customView == tab?.customView as TextView) {
                        // 被选中的 Tab 的样式
                        customView.also {
                            ObjectAnimator
                                .ofFloat(it, "textSize", startTextSize, endTextSize)
                                .setDuration(animationDuration)
                                .start()
                            it.setTypeface(Typeface.DEFAULT, Typeface.BOLD)
                            it.setTextColor(indicatorSelectedColor)
                        }
                    } else {
                        customView.also {
                            // 只能使用颜色作为判断，不能使用字体大小，因为上面属性动画有可能还未执行完毕，就进行下面的判断
                            if (it.currentTextColor == indicatorSelectedColor) {
                                ObjectAnimator
                                    .ofFloat(it, "textSize", endTextSize, startTextSize)
                                    .setDuration(animationDuration)
                                    .start()
                                it.setTypeface(Typeface.DEFAULT, Typeface.NORMAL)
                                it.setTextColor(indicatorNormalColor)
                            }
                        }
                    }
                }
            }
        })
    }

    private fun setCustomViewFroTabs(resIdList: List<Int>, tabLayout: TabLayout) {
        for (i in 0..tabLayout.tabCount) {
            tabLayout.getTabAt(i)?.setCustomView(
                TextView(activity).apply {
                    layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
                    setText(resIdList[i])
                    tabCustomViewList.add(this)
                    setTypeface(Typeface.DEFAULT, Typeface.NORMAL)
                }
            )?.setContentDescription(resIdList[i])
        }
    }

    companion object {
        val instance: HomeFragment
            get() = HomeFragment()
        const val HOME_FRAGMENT_TAG = "HomeFragment"
    }
}
