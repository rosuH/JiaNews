# 新嘉闻 JiaNews

### App 展示

![ezgif.com-gif-maker.gif](https://i.loli.net/2018/05/09/5af3089136cdd.gif)

### 简介

这是一个展示[嘉应学院官网](www.jyu.edu.cn)新闻动态的 Android App。

- 官网首页一共有五个栏目内容：
  - 四个新闻列表栏目
    - 综合要闻
    - 校园公告
    - 校园动态
    - 媒体报道
  - 一个新闻图片轮播图

### 技术原理

本程序的技术原理是：

- 使用 [Jsoup](https://jsoup.org) 获取并解析网站的 HTML 内容
- App 首页
  - 骨架
    - `ViewPager ` + `Fragment` 实现的 `Banner` 轮播图
    - `ViewPager` + `Fragment` + `TabLayout` 实现的多栏目文章列表视图
  - Material Design
    - `DrawerLayout` + `NavigationView` 实现侧滑菜单
    - `CoordinatorLayout`+`AppBarLayout`实现响应式布局
- App 阅读页面
  - `WebView `加载并渲染获取的 HTML 数据
- 其他
  - 下拉刷新
    - `SwipeRefreshLayout`布局
  - 上拉加载
    - 在 `RecyclerView`中添加一个`Footer`视图，判断列表可视的 `position`然后调用数据获取模块
  - 网络异步框架
    - `AsyncTask`
  - 线程控制
    - `ScheduledExecutorService`


### 其他声明

- 本程序开发系作者用来锻炼编码技能的作品，其所获取的数据和 App 所使用的 Logo 皆为嘉应学院所有
- 本项目所有权归作者所有的部分皆遵守 MIT 协议

MIT License

Copyright (c) [2018] [rosuH]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.