package me.rosuh.jianews.view

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import me.rosuh.android.jianews.R
import me.rosuh.jianews.util.Const
import me.rosuh.jianews.adapter.FragmentPagerAdapter

/**
 * @author rosu
 * @date 2018/9/29
 */
class HomeFragment : Fragment() {

    var mStatePagerAdapter: FragmentPagerAdapter?= null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.home_fragment, container, false)

        val viewPager = view.findViewById<ViewPager>(R.id.vp_article_list)
        // 文章列表
        mStatePagerAdapter = FragmentPagerAdapter(activity!!.supportFragmentManager)
        viewPager.adapter = mStatePagerAdapter
        viewPager.currentItem = Const.VALUE_ARTICLE_START_PAGE
        initTabLayout(viewPager, view)

        return view
    }

    /**
     * 初始化导航栏
     * @param viewPager 传入设置好的 ViewPager 和 TabLayout 关联起来
     */
    private fun initTabLayout(viewPager: ViewPager, view: View) {
        val tabLayout = view.findViewById<TabLayout>(R.id.tb_layout_nav)

        tabLayout.addTab(tabLayout.newTab())
        tabLayout.addTab(tabLayout.newTab())
        tabLayout.addTab(tabLayout.newTab())
        tabLayout.addTab(tabLayout.newTab())

        tabLayout.setupWithViewPager(viewPager)
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
        })

        tabLayout.getTabAt(0)?.setText(R.string.tab_home)?.setContentDescription(R.string.tab_home)
        tabLayout.getTabAt(1)?.setText(R.string.tab_announce)?.setContentDescription(R.string.tab_announce)
        tabLayout.getTabAt(2)?.setText(R.string.tab_activity)?.setContentDescription(R.string.tab_activity)
        tabLayout.getTabAt(3)?.setText(R.string.tab_media)?.setContentDescription(R.string.tab_media)
    }

    companion object {
        val instance: HomeFragment
            get() = HomeFragment()
        private val TAG = "HomeFragment"
    }
}
