现代图论 —— 找寻基本模块
================

### 项目说明
此项目为北京大学研究生课程《现在图论》的任务之一：找寻外圈为12的图的基本模块

<br />

基本模块是一个半极大平面图，要满足如下条件：

  * 可以从基础基本模块（内部只有两个点）通过变换生成
  * 里面是一棵树；且这棵树的内部节点的度数不小于5，叶子节点的度数不小于4
  * 如果将图边界1-2染色，内部节点3-4染色，则(1,3)子图的连通分支数赢等于(2,4)的连通分支树

<br />

本项目主要有如下特性：

  * 使用Java Swing进行可视化绘图；外部使用红蓝染色，内部使用绿色和紫红色染色。
  * 只支持内部节点为一条链的情况
  * 支持两类操作：**添加**，**变换**。添加是向当前链的最右边增加一个节点；变换则是对当前的图，选两条边进行变换。
  * 支持撤销操作，可以存档与读取；存档时会同时保存当前图的信息，和一张png图片
  * 支持同构判断，每次变换后都会判断变换后的图片是否与某一个已经保存的图同构。
  * 支持快捷键操作（a: 添加；n: 变换；u: 撤销；s: 存档；l: 读档；c: 检查所有保存的图是否存在同构）

<br />

### 编译运行

请使用IntelliJ IDEA进行编译运行。请确保有Java 1.8的环境。

**save/**: 存档文件夹  
> **\*.graph**&nbsp;&nbsp;&nbsp; 图的信息，可以读取  
> **\*.png**&nbsp;&nbsp;&nbsp; 对应图的png文件，可以直接查看  

**src/**: 源代码目录
> **Graph.java**&nbsp;&nbsp;&nbsp; 本项目中图的数据结构  
> **GraphIsomoriphism.java**&nbsp;&nbsp;&nbsp; 判断两张图是否同构  
> **GraphMap.java**&nbsp;&nbsp;&nbsp; 以邻接矩阵形式存放的图  
> **GraphPanel.java**&nbsp;&nbsp;&nbsp; 继承自`JPanel`，负责具体的图形绘制  
> **Main.java**&nbsp;&nbsp;&nbsp; 主函数，负责文件处理，以及添加、变换、撤销等操作

