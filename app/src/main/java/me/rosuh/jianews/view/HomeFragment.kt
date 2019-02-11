package me.rosuh.jianews.view

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.design.widget.TabLayout.Tab
import android.support.design.widget.TabLayout.TabLayoutOnPageChangeListener
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
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
    private val endTextSize = 18f
    private var startTextSize = 14f
    private val animationDuration = 200L
    private val indicatorSelectedColor:Int by lazy {
        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            resources.getColor(R.color.tab_selected_color, activity!!.theme)
        }else{
            resources.getColor(R.color.tab_selected_color)
        }
    }

    private val indicatorNormalColor:Int by lazy {
        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            resources.getColor(R.color.indicator_normal, activity!!.theme)
        }else{
            resources.getColor(R.color.indicator_normal)
        }
    }

    var mStatePagerAdapter: FragmentPagerAdapter? = null
    private lateinit var tabLayoutRef: WeakReference<TabLayout>
    private var tabCustomViewList = ArrayList<TextView>()

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
        produceTabs(
            arrayListOf(
                R.string.tab_home,
                R.string.tab_announce,
                R.string.tab_activity,
                R.string.tab_media
            ), tabLayout
        )
        (tabLayoutRef.get()?.getTabAt(0)?.customView as TextView).let{
            startTextSize = (it.textSize / resources.displayMetrics.scaledDensity)
            it
        }
        // 初始化首个 Tab 的样式
        (tabLayout.getTabAt(0)?.customView as TextView).also {
            it.textSize = endTextSize
            it.setTypeface(it.typeface, Typeface.BOLD)
            it.setTextColor(indicatorSelectedColor)
        }
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
                for (customView in tabCustomViewList){
                    if (customView == tab?.customView as TextView){
                        // 被选中的 Tab 的样式
                        customView.also {
                            ObjectAnimator
                                .ofFloat(it, "textSize", startTextSize, endTextSize)
                                .setDuration(animationDuration)
                                .start()
                            it.setTypeface(it.typeface, Typeface.BOLD)
                            it.setTextColor(indicatorSelectedColor)
                        }
                    }else{
                        customView.also {
                            // 只能使用颜色作为判断，不能使用字体大小，因为上面属性动画有可能还未执行完毕，就进行下面的判断
                            if (it.currentTextColor == indicatorSelectedColor){
                                ObjectAnimator
                                    .ofFloat(it, "textSize", endTextSize, startTextSize)
                                    .setDuration(animationDuration)
                                    .start()
                                it.setTypeface(it.typeface, Typeface.NORMAL)
                                it.setTextColor(indicatorNormalColor)
                            }
                        }
                    }
                }
            }
        })
    }

    private fun produceTabs(resIdList: List<Int>, tabLayout: TabLayout) {
        tabLayout.removeAllTabs()
        for (resId in resIdList) {
            tabLayout.addTab(
                tabLayout.newTab().setCustomView(
                    TextView(activity).apply {
                        layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
                        setText(resId)
                        tabCustomViewList.add(this)
                    }
                ).setContentDescription(resId)
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
