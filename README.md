##Web app build by Play Framework

####Depencency
* [mongodb project](https://github.com/Unlimited-Works/mongodb)
* [RxSocket](https://github.com/LoranceChen/RxSocket)
* [RxScala](https://github.com/ReactiveX/RxScala)
* [RxJs](http://www.bootcdn.cn/rxjs/)
#### Start app
sbt run

#### Completed
http://localhost:9000/signin  
verify data from browser->web server->dao->mongo is reachable

####Readmap
* add time limit of dao socket communicate.
* handle reconnect
* 需要让rxsocket的presentation层提供终止Observable观察条件的函数参数,因为
find查询不是一次返回的.不能认为只要收到一个taskId就结束.另外,sendWithTask方法
应该返回Observable而不是Future

####Bug
* blog overview high chance get less content, suspect it because socket deal data with error sequence.
for detail, it occurred at JPrototol taskId.

* post overview功能显示出帖子,并提供翻页功能
* post 帖子的全文观看(包含修改,删除按钮)
* blog编辑帖子功能

####为什么写这个程序?
自己开发一个博客的根本目的是磨练自己编程的技巧.
1. 为什么是博客?
1) 我觉得自己的想法或者是每个人的想法都应该表达出来并能够相互了解.我之所以写这个博客,是希望每个人都有一个表达自己想法的地方.
1.1. 为什么不用其他写博客的平台,如豆瓣,csdn,新浪博客?
当前的环境,不论是社区的人还是平台都不能令人满意.
对于平台而言,更多的在商业的驱使下出现广告的现象,这种感觉就像自己的场所被污染了.另外,即使现在没有广告,也不能保证以后不会有广告,至少没有任何平台正式保证他们不打广告.
对于用户而言,在这些平台上发表文章更多的是抱着一种"希望被别人关注","提高自己在社交圈中的地位"的态度.在这种想法的驱使下,是很难写出内心真正的想法.
所以,从这一点上,与其说这是博客,倒不如说是一个相互交流感悟的地方.

1.1.1 如何让这个社区的人避免上面的那种心态?
不公布任何与作者相关的信息,唯一显示的只有笔名,并且弱化社交因素,比如可能认识的人这种功能
虽然会有部分人会明显受到关注,但大部分用户都不会得到关注,好文章是藏不住的.

1.1.2 如何让每个人的信息能相互交流?
主要使用两个系统:
1) 对文章:
在每篇文章上面实现"对语句进行评论的功能",现在的论坛都会讲有专门的评论区,这在某种程度上会引入话外题,并且不直观.
在原文的语句上进行评论,能够限制评论者思想的跳跃引入过多的话外题,并且能强调出交流也是文章的一部分.
2) 对用户:
2.1) 按照用户的收藏
2.3) 公告栏,每周按照不固定的话题选中比较好的文章
2.3) 搜索和推荐,使用者只要使用关键字就可以搜索出想要的东西.

2. 为什么用play框架?
scala下面成熟的框架,其实我更想尝试finatra,不过每次调试都要重启,所以暂时放弃了.

3. 功能的重点在哪里?
重点是面向个人方便使用的笔记.为了写出自己内心的期望表达的东西,就必须给用户一种方便使用的印象.
将用户的数据保存好,不管这些想法是一本书的草稿还是纯粹的是心情笔记,都能够在用户期望用到这些数据的时候能够立刻
派上用场.
