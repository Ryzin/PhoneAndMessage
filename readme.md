# 拨打电话和发送短信demo
## Mobile Application and Development (Android) 移动智能应用开发课程作业
### 简介
* 该计算器是基于 Android Studio 开发，仿 One UI 设计风格的拨打电话和发
送短信 APP。 
* 该计算器的设计方面与 One UI 基本相同，采用扁平化风格。主界面
activity_main.xml 中放置了 ViewPager 控件，而 ViewPager 可左右滑动切换不
同的 fragment。activity_message.xml 放置了 RecycleView 控件，可以上下滑
动查看短信内容。控件也大多采用圆角。 
* 该 APP 实现了动态获取电话、短信权限功能。也可以直接进行电话拨打、发
送短信，短信内容存放在数据库当中，而且可以从数据库中读取，并在 APP 中显
示（但没有实现：数据库建立用户表，也就是说电话和短信没有一一对应）。并
且对短信发送状态 sentIntent 和接收状态 deliveryIntent 进行了处理。 
* 编程方面，MainActivity 中编写了整个 APP 通用的权限申请方法，供所有
Activity 或 Fragment 调用。在电话 Fragment 中依旧使用抽象工厂模式绑定控
件监听方法。在 MessageActivity 中尝试使用 butterknife，使用了更加简洁的
控件绑定方法。数据库使用了 greendao，实现了表创建，可以对（短信）记录
进行插入、查询等。 
 
<br/>
<br/>

### 展示
![pic1](https://github.com/Ryzin/PhoneAndMessage/raw/master/imgs/pic1.jpg)
![pic2](https://github.com/Ryzin/PhoneAndMessage/raw/master/imgs/pic2.jpg)