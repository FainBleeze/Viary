# Viary
 一款多功能日记软件



### 工具版本信息

- gradle：'com.android.tools.build:gradle:3.6.2'

- ```xml
  applicationId "com.hyxy.viary"
  minSdkVersion 17
  targetSdkVersion 28
  versionCode 1
  versionName "1.0"
  ```



### 初步规划

##### 页面1：时间页

- 显示日期时间轴
  - 可选择日期记日记
  - 有日记的日期显示其内容
- 拥有密码功能，设置以后需要输入密码才能进入时间轴页面

##### 页面2：日记页

- 可以记录多种类型的笔记，包括：账单型、读后感、观后感、记事型、图片型
- 这些不同的类型配不同的背景色，以按钮或者卡片的方式呈现出来，可以选择一种或几种开始写

##### 页面3：设置页

- 设置是否打开密码
- 设置字号
- 

##### 其他内容

- 尝试将笔记分享到其他平台的功能
- 尝试将某类笔记导出到docx文件的功能
- 尝试从docx文件恢复到日记软件的功能



### UI设计

- 根据日记类型不同分为四个主题颜色。
  - 在colors.xml中有四种颜色的定义
  - 每种颜色都有饱和（透明度100）、深沉（透明度50）、浅色（透明度25）三种风格可以使用



### 数据库设计

- 数据库名称：使用params.DATABASE获取（DB_viary）
- 数据库形式：
  - 表名列名统一使用params类的静态参数获取
  - 表名：params.DBTABLENAME
  - 主键：时间戳（精确到秒，不会重名）

> - 时间戳 text   (params.DBODATE )
> - 标题  text   (params.DBTITLE)
> - 类型  integer   (params.DBTYPE)
> - 内容  text   (params.DBCONTENT)
> - 年 integer   (params.DBOYEAR)
> - 月 integer   (params.DBMONTH)
> - 日 integer   (params.DBDAY)

- 主页面仅读数据库，根据点击的日期查询年月日
  - 有内容显示出来，点击时直接跳到日记页面
  - 无内容显示 · ，点击时跳到选择新建类型的页面。

