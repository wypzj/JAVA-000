# GC总结：

## 1.串行GC （Serial GC）

> 简单原理：单线程GC，在GC收集器启动时，会暂停所有的应用线程，直到GC线程垃圾回收完成。
>
> GC算法：年轻代（mark-copy标记-复制算法），老年代（mark-sweep-compact 标记-清除-整理算法）

**缺点：**

1. 对于多核CPU来说，单线程GC无法充分利用多核性能
2. GC垃圾回收时，会暂停应用线程，所以只适合Heap只有几百M的，因为Heap一旦很大，GC暂停的时间就会很长，应用程序直接反应出来的就是卡顿不动

**GC信息图：**

![image-20201027112852077](https://gitee.com/javaboyteam/typora-images/raw/master/img/image-20201027112852077.png)

**运行示意图：**

![image](https://gitee.com/javaboyteam/typora-images/raw/master/img/077ff50e-d399-442a-a476-133380a7ceda-105686.jpg)

## 2.ParNew GC

> 简单原理：串行GC的改进版本，多线程处理垃圾回收，能充分发挥多核CPU的性能，同样的GC回收时，会暂停所有的应用线程，直到GC线程垃圾回收完成。
>
> GC算法：年轻代（mark-copy标记-复制算法），老年代（mark-sweep-compact 标记-清除-整理算法）

优点：

1. 多线程处理GC回收，能充分发挥多核CPU的优势（默认GC线程数=CPU核数）

缺点：

1. 发生GC时会暂停用户线程

## 3.并行GC （Parallel GC）

> 简单原理：跟ParNew GC很像，多线程处理垃圾回收，能充分发挥多核CPU的性能，同样的GC回收时，会暂停所有的应用线程，直到GC线程垃圾回收完成。
>
> GC算法：年轻代使用 标记-复制（mark-copy）算法，老年代使用 标记-清除-整理（mark-sweepcompact）算法

优点：

1. 多线程处理GC回收，能充分发挥多核CPU的优势（默认GC线程数=CPU核数，JDK8默认GC策略）
2. 并行GC可以设置GC Pause的时间，可对CPU资源进行充分利用，提高应用的吞吐量
3. 2次GC期间，GC线程不再运行，所以不占用系统资源

缺点：

1. 发生GC时会暂停用户线程

**GC信息图：**

![image-20201027115149654](https://gitee.com/javaboyteam/typora-images/raw/master/img/image-20201027115149654.png)

**运行示意图：**

![image](https://gitee.com/javaboyteam/typora-images/raw/master/img/cc9e5cea-2272-48f5-9afc-e48686d9b5dd-105686.jpg)

## 4.CMS GC (ConcMarkSweep GC)--一般作为老年代的GC

> **简单原理：**将整个GC阶段分成6个阶段，
> 阶段 1: Initial Mark（初始标记） 
> 阶段 2: Concurrent Mark（并发标记） 
> 阶段 3: Concurrent Preclean（并发预清理） 
> 阶段 4: Final Remark（最终标记） 
> 阶段 5: Concurrent Sweep（并发清除） 
> 阶段 6: Concurrent Reset（并发重置）
> 只在阶段1和4 STW，能有效控制STW的时间，CMS GC设计的**根本目的**是避免在老年代GC收集时出现长时间的卡顿，主要使用；两种手段保证：
>
> 1. 不对老年代空闲碎片进行整理，使用freeList空闲列表来管理内存
> 2. 在 mark-and-sweep （标记-清除） 阶段的大部分工作和应用线程一起并发执行。
>
> **默认CMS使用的并发线程数等与CPU核心数的1/4**
>
> GC算法：年轻代采用并行 STW 方式的 mark-copy (标记-复制)算法，对老年代主要使用并发 marksweep (标记-清除)算法

**优点：**

1. GC停顿时长可控制（GC线程跟业务线程可以并发执行）
2. 避免老年代GC收集时出现长时间的卡顿
3. 可以设置回收的实际，不需要等满了才回收

**缺点：**



**运行示意图：**

![image](https://gitee.com/javaboyteam/typora-images/raw/master/img/b759e096-c0e7-4a37-afd4-cfe486cc77fd-105686.jpg)

## 5.G1 GC

> 简单原理：
>
> GC算法：这块算法不了解，但是对于小块堆来说，感觉还是年轻代使用 标记-复制（mark-copy）算法，老年代使用 标记-清除（mark-sweep）算法