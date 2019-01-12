package me.rosuh.jianews.view

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.design.widget.TabLayout.Tab
import android.support.design.widget.TabLayout.TabLayoutOnPageChangeListener
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TextView

import me.rosuh.android.jianews.R
import me.rosuh.jianews.util.Const
import me.rosuh.jianews.adapter.FragmentPagerAdapter
import java.lang.ref.WeakReference

/**
 * @author rosu
 * @date 2018/9/29
 */
class HomeFragment : Fragment() {

    var mStatePagerAdapter: FragmentPagerAdapter? = null
    lateinit var tabLayoutRef: WeakReference<TabLayout>
    lateinit var customViewList: ArrayList<View>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.home_container_fragment, container, false)

        val viewPager = view.findViewById<ViewPager>(R.id.vp_article_list)
        // 文章列表
        mStatePagerAdapter = FragmentPagerAdapter(activity!!.supportFragmentManager)
        viewPager.adapter = mStatePagerAdapter
        viewPager.currentItem = Const.VALUE_ARTICLE_START_PAGE
        initTabLayout(viewPager, view)
        viewPager.addOnPageChangeListener(TabLayoutOnPageChangeListener(tabLayoutRef.get()))
        return view
    }

    /**
     * 初始化导航栏
     * @param viewPager 传入设置好的 ViewPager 和 TabLayout 关联起来
     */
    private fun initTabLayout(viewPager: ViewPager, view: View) {
        val tabLayout = view.findViewById<TabLayout>(R.id.tb_layout_nav)
        tabLayout.setupWithViewPager(viewPager)
        tabLayoutRef = WeakReference(tabLayout)
        customViewList = ArrayList()
        fillList(
            arrayListOf(
                R.string.tab_home,
                R.string.tab_announce,
                R.string.tab_activity,
                R.string.tab_media
            ), tabLayout
        )
        tabLayout.addOnTabSelectedListener(object : TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
            private val sDoubleClickedInterval = 400L
            private var mLastSelectedTime: Long = 0

            override fun onTabReselected(tab: TabLayout.Tab?) {
                super.onTabReselected(tab)
                val newestSelectedTime = System.currentTimeMillis()
                if (newestSelectedTime - mLastSelectedTime < sDoubleClickedInterval) {
                    val tabPos = tab!!.position
                    val adapter = viewPager.adapter as FragmentPagerAdapter?
                    val articleListFragment = adapter!!.getItemFromContainer(viewPager, tabPos) as ArticleListFragment
                    articleListFragment.scrollToTop()
                }
                mLastSelectedTime = newestSelectedTime
            }

            override fun onTabSelected(tab: Tab?) {
                super.onTabSelected(tab)
                viewPager.currentItem = tab!!.position
                tabLayoutRef.get() ?: return
                if (customViewList.isNullOrEmpty()) return
                for (customView in customViewList) {
                    val tv = customView.findViewById<TextView>(R.id.tab_item_text)
                    val indicatorView: View = customView.findViewById(R.id.tab_item_indicator)
                    if (customViewList.indexOf(customView) == tab.position) {
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                            tv.setTextColor(resources.getColor(R.color.indicator_select, activity!!.theme))
                        } else {
                            tv.setTextColor(resources.getColor(R.color.indicator_select))
                        }
                        indicatorView.background =
                                resources.getDrawable(R.drawable.shape_tab_indicator_color, activity!!.theme)
                        indicatorView.visibility = View.VISIBLE
                    } else {
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                            tv.setTextColor(resources.getColor(R.color.indicator_normal, activity!!.theme))
                        } else {
                            tv.setTextColor(resources.getColor(R.color.indicator_normal))
                        }
                        indicatorView.visibility = View.INVISIBLE
                    }
                }
            }
        })
    }

    private fun fillList(resIdList: List<Int>, tabLayout: TabLayout) {
        tabLayout.removeAllTabs()
        for (resId in resIdList) {
            val view = getTabView(activity!!, resources.getString(resId), 78, 16, 13, resIdList.indexOf(resId) == 0)
            customViewList.add(view)
            tabLayout.addTab(
                tabLayout.newTab().setCustomView(view).setContentDescription(resId)
            )
        }
    }

    private fun getTabView(
        activity: Context,
        text: String,
        indicatorWidth: Int,
        indicatorHeight: Int,
        textSize: Int, isFirstTab:Boolean
    ): View {
        val view = LayoutInflater.from(activity).inflate(R.layout.tab_item_layout, null)
        val tabTextView = view.findViewById<TextView>(R.id.tab_item_text)
        val indicatorView = view.findViewById<View>(R.id.tab_item_indicator)
        if (indicatorWidth > 0) {
            val indicator = view.findViewById<View>(R.id.tab_item_indicator)
            val layoutParams = indicator.layoutParams
            layoutParams.width = indicatorWidth
            layoutParams.height = indicatorHeight
            indicator.layoutParams = layoutParams
        }
        tabTextView.textSize = textSize.toFloat()
        tabTextView.text = text
        if (isFirstTab){
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                tabTextView.setTextColor(resources.getColor(R.color.indicator_select, activity!!.theme))
            } else {
                tabTextView.setTextColor(resources.getColor(R.color.indicator_select))
            }
            indicatorView.background =
                    resources.getDrawable(R.drawable.shape_tab_indicator_color, activity!!.theme)
            indicatorView.visibility = View.VISIBLE
        }else{
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                tabTextView.setTextColor(resources.getColor(R.color.indicator_normal, activity!!.theme))
            } else {
                tabTextView.setTextColor(resources.getColor(R.color.indicator_normal))
            }
            indicatorView.visibility = View.INVISIBLE
        }
        return view
    }

    companion object {
        val instance: HomeFragment
            get() = HomeFragment()
        private val TAG = "HomeFragment"
    }
}
