1>Redis

	本质上是一个Key-Value类型的内存数据库，很像memcached，整个数据库统加载在内存当中进行操作，定期通过
    异步操作把数据库数据flush到硬盘上进行保存。因为是纯内存操作，Redis的性能非常出色，每秒可以处理超过 
    10万次读写操作， 是已知性能最快的Key-Value DB。 Redis的出色之处不仅仅是性能，Redis最大的魅力是支持
    保存多种数据结构，此外单个value的最大限制是1GB，不像 memcached只能保存1MB的数据，因此Redis可以用
    来实现很多有用的功能，比方说用他的List来做FIFO双向链表，实现一个轻量级的高性 能消息队列服务，
    用他的Set可以做高性能的tag系统等等。另外Redis也可以对存入的Key-Value设置expire时间，因此也可
    以被当作一 个功能加强版的memcached来用。Redis的主要缺点是数据库容量受到物理内存的限制，
    不能用作海量数据的高性能读写，因此Redis适合的场景主要局限在较小数据量的高性能操作和运算上。

redis常用命令:
https://www.cnblogs.com/cxxjohnson/p/9072383.html

2>redis相比memcached有哪些优势

	① memcached所有的值均是简单的字符串，redis作为其替代者，支持更为丰富的数据类型
    ② redis的速度比memcached快很多
    ③ redis可以持久化其数据

3>redis的数据类型,应用场景

	Redis支持5种数据类型：string（字符串）,hash（哈希）,list（列表）,set（集合）及zset(sorted set：有序集合)。
    ① string : 常规key-value缓存应用。常规计数: 微博数,粉丝数。
    ② hash : 存储用户信息，商品信息，订单信息
    ③ list : 好友列表，粉丝列表，消息队列，最新消息排行等。
    ④ set : 共同好友、共同兴趣、分类标签
    ⑤ sorted set : 网站排行榜
    参考链接 : https://blog.csdn.net/hi_alan/article/details/86530769

4>redis淘汰策略

	① volatile-lru ：从已设置过期时间的数据集（server.db[i].expires）中挑选最近最少使用的数据淘汰
    ② volatile-ttl ：从已设置过期时间的数据集中挑选将要过期的数据淘汰
    ③ volatile-random ：从已设置过期时间的数据集中任意选择数据淘汰
    ④ allkeys-lru ：从数据集（server.db[i].dict）中挑选最近最少使用的数据淘汰
    ⑤ allkeys-random ：从数据集中任意选择数据淘汰
    ⑥ no-enviction（驱逐）：禁止驱逐数据,这也是默认策略。意思是当内存不足以容纳新入数据时，
    新写入操作就会报错，请求可以继续进行，线上任务也不能持续进行，采用no-enviction策略可以保证数据不被丢失。

5>Redis key的过期时间和永久有效分别怎么设置
EXPIRE和PERSIST命令。

6>Redis 持久化方式

	① RDB持久化方式能够在指定的时间间隔能对你的数据进行快照存储.

    ② AOF持久化方式记录每次对服务器写的操作,当服务器重启的时候会重新执行这些命令来恢复原始的数据,
        AOF命令以redis协议追加保存每次写的操作到文件末尾.Redis还能对AOF文件进行后台重写,使得AOF文件的体积不至于过大.
        如果你只希望你的数据在服务器运行的时候存在,你也可以不使用任何持久化方式.
        你也可以同时开启两种持久化方式, 在这种情况下, 当redis重启的时候会优先载入AOF文件来恢复原始的数据,
        因为在通常情况下AOF文件保存的数据集要比RDB文件保存的数据集要完整.

redis问题补充:
Redis缓存雪崩、缓存穿透、热点Key

	① 缓存穿透
	缓存系统，按照KEY去查询VALUE,当KEY对应的VALUE一定不存在的时候并对KEY并发请求量很大的时候，就会对后端造成很大的压力。
		(1)使用互斥锁排队,根据key获取value值为空时，锁上，从数据库中load数据后再释放锁。若其它线程获取锁失败，则等待一段时间后重试
		(2)布隆过滤器,类似于一个hash set，用于快速判某个元素是否存在于集合中，其典型的应用场景就是快速判断一个key是否存在于某容器，
		   不存在就直接返回。布隆过滤器的关键就在于hash算法和容器大小
	② 缓存雪崩问题
	缓存在同一时间内大量键过期（失效），接着来的一大波请求瞬间都落在了数据库中导致连接异常。
		(1)在缓存失效后，通过加锁或者队列来控制读数据库写缓存的线程数量。比如对某个key只允许一个线程查询数据和写缓存，其他线程等待。
		(2)可以通过缓存reload机制，预先去更新缓存，再即将发生大并发访问前手动触发加载缓存
		(3)不同的key，设置不同的过期时间，让缓存失效的时间点尽量均匀
		(4)做二级缓存，或者双缓存策略。A1为原始缓存，A2为拷贝缓存，A1失效时，可以访问A2，A1缓存失效时间设置为短期，A2设置为长期。
	③ 热点key
		(1) 这个key是一个热点key（例如一个重要的新闻，一个热门的八卦新闻等等），所以这种key访问量可能非常大。
		(2) 缓存的构建是需要一定时间的。（可能是一个复杂计算，例如复杂的sql、多次IO、多个依赖(各种接口)等等）
		    于是就会出现一个致命问题：在缓存失效的瞬间，有大量线程来构建缓存，造成后端负载加大，甚至可能会让系统崩溃 。

			
------------------------------------------------------------------------------------------------------------------------------------------------------------------------

7>Spring支持的几种bean的作用域

	① singleton : bean在每个Springioc容器中只有一个实例。
    ② prototype ：一个bean的定义可以有多个实例。
    ③ request ：每次http请求都会创建一个bean，该作用域仅在基于web的SpringApplicationContext情形下有效。
    ④ session ：在一个HTTPSession中，一个bean定义对应一个实例。该作用域仅在基于web的SpringApplicationContext情形下有效。
    ⑤ global-session ：在一个全局的HTTPSession中，一个bean定义对应一个实例。该作用域仅在基于web的SpringApplicationContext情形下有效。

8>Spring框架中的单例bean是线程安全的吗
不，Spring框架中的单例bean不是线程安全的。

9>java线程池大小设置多少合适

	最佳线程数目 = （线程等待时间与线程CPU时间之比 + 1）* CPU数目
    线程等待时间所占比例越高，需要越多线程。线程CPU时间所占比例越高，需要越少线程。

	https://blog.csdn.net/lieyanhaipo/article/details/87877853

10>Java线程池七个参数详解
https://blog.csdn.net/ye17186/article/details/89467919

	分别是corePoolSize、maximumPoolSize、keepAliveTime、unit、workQueue、threadFactory、handler
	① corePoolSize 线程池核心线程大小
		线程池中会维护一个最小的线程数量，即使这些线程处理空闲状态，他们也不会被销毁，除非设置了allowCoreThreadTimeOut。这里的最小线程数量即是corePoolSize。
	② maximumPoolSize 线程池最大线程数量
		一个任务被提交到线程池以后，首先会找有没有空闲存活线程，如果有则直接将任务交给这个空闲线程来执行，如果没有则会缓存到工作队列中，如果工作队列满了，
		才会创建一个新线程，然后从工作队列的头部取出一个任务交由新线程来处理，
		而将刚提交的任务放入工作队列尾部。线程池不会无限制的去创建新线程，它会有一个最大线程数量的限制，这个数量即由maximunPoolSize的数量减去corePoolSize的数量来确定。
	③ keepAliveTime 空闲线程存活时间
		一个线程如果处于空闲状态，并且当前的线程数量大于corePoolSize，那么在指定时间后，这个空闲线程会被销毁，这里的指定时间由keepAliveTime来设定
	④ unit 空闲线程存活时间单位
		keepAliveTime的计量单位
	⑤ workQueue 工作队列
		新任务被提交后，会先进入到此工作队列中，任务调度时再从队列中取出任务。
		jdk中提供了四种工作队列：
		(1)ArrayBlockingQueue
		基于数组的有界阻塞队列，按FIFO排序。新任务进来后，会放到该队列的队尾，有界的数组可以防止资源耗尽问题。当线程池中线程数量达到corePoolSize后，
		再有新任务进来，则会将任务放入该队列的队尾，等待被调度。如果队列已经是满的，则创建一个新线程，如果线程数量已经达到maxPoolSize，则会执行拒绝策略。
		(2)LinkedBlockingQuene
		基于链表的无界阻塞队列（其实最大容量为Interger.MAX），按照FIFO排序。由于该队列的近似无界性，当线程池中线程数量达到corePoolSize后，再有新任务进来，
		会一直存入该队列，而不会去创建新线程直到maxPoolSize，因此使用该工作队列时，参数maxPoolSize其实是不起作用的。
		(3)SynchronousQuene
		一个不缓存任务的阻塞队列，生产者放入一个任务必须等到消费者取出这个任务。也就是说新任务进来时，不会缓存，
		而是直接被调度执行该任务，如果没有可用线程，则创建新线程，如果线程数量达到maxPoolSize，则执行拒绝策略。
		(4)PriorityBlockingQueue
		具有优先级的无界阻塞队列，优先级通过参数Comparator实现。
	⑥ threadFactory 线程工厂
		创建一个新线程时使用的工厂，可以用来设定线程名、是否为daemon线程等等
	⑦ handler 拒绝策略
		(1)CallerRunsPolicy
		该策略下，在调用者线程中直接执行被拒绝任务的run方法，除非线程池已经shutdown，则直接抛弃任务。
		(2)AbortPolicy
		该策略下，直接丢弃任务，并抛出RejectedExecutionException异常。
		(3)DiscardPolicy
		该策略下，直接丢弃任务，什么都不做。
		(4)DiscardOldestPolicy
		该策略下，抛弃进入队列最早的那个任务，然后尝试把这次拒绝的任务放入队列

11>常见线程池

	① newSingleThreadExecutor
        单个线程的线程池，即线程池中每次只有一个线程工作，单线程串行执行任务
    ② newFixedThreadExecutor(n)
        固定数量的线程池，没提交一个任务就是一个线程，直到达到线程池的最大数量，
        然后后面进入等待队列直到前面的任务完成才继续执行
    ③ newCacheThreadExecutor（推荐使用）
        可缓存线程池，当线程池大小超过了处理任务所需的线程，那么就会回收部分空闲
        （一般是60秒无执行）的线程，当有任务来时，又智能的添加新线程来执行。
    ④ newScheduleThreadExecutor
        大小无限制的线程池，支持定时和周期性的执行线程

12>创建线程的主要方式

	①继承Thread类
    通过继承(extends)Thread并且重写其run()，run方法中即线程执行任务。创建后的子类通过调用 start() 方法即可执行线程方法。

	②通过Runnable接口创建线程类
	该方法需要先 定义一个类实现(implements)Runnable接口，并重写该接口的 run() 方法，
	此run方法是线程执行体。接着创建 Runnable实现类的对象，作为创建Thread对象的参数target，
	此Thread对象才是真正的线程对象。通过实现Runnable接口的线程类，是互相共享资源的。
	
	③使用Callable和Future创建线程
	从继承Thread类和实现Runnable接口可以看出，上述两种方法都不能有返回值，且不能声明抛出异常。而Callable接口则实现了此两点，
	Callable接口如同Runable接口的升级版，其提供的call()方法将作为线程的执行体，同时允许有返回值。
    但是Callable对象不能直接作为Thread对象的target，因为Callable接口是 Java 5 新增的接口，
    不是Runnable接口的子接口。对于这个问题的解决方案，就引入 Future接口，
    此接口可以接受call() 的返回值，RunnableFuture接口是Future接口和Runnable接口的子接口，
    可以作为Thread对象的target 。并且， Future 接口提供了一个实现类：FutureTask 。
    FutureTask实现了RunnableFuture接口，可以作为 Thread对象的target。

-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
																									INTERVIEW SUMMARY																												
-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
1>kafka重复消费,堆积等问题
原因分析
Kafka消费者有两个配置参数：
max.poll.interval.ms
两次poll操作允许的最大时间间隔。单位毫秒。默认值300000（5分钟）。
两次poll超过此时间间隔，Kafka服务端会进行rebalance操作，导致客户端连接失效，无法提交offset信息，从而引发重复消费。
max.poll.records
一次poll操作获取的消息数量。默认值50。
如果每条消息处理时间超过60秒，那么一批消息处理时间将超过5分钟，从而引发poll超时，最终导致重复消费。

	消息重复消费和消息丢包的解决办法
	保证不丢失消息：生产者（ack=all 代表至少成功发送一次)     重试机制
	消费者 （offset手动提交，业务逻辑成功处理后，提交offset） 
	保证不重复消费：落表（主键或者唯一索引的方式，避免重复数据） 
	业务逻辑处理（选择唯一主键存储到Redis或者mongdb中，先查询是否存在，若存在则不处理；若不存在，先插入Redis或Mongdb,再进行业务逻辑处理）

	①什么是kafka
		是一个分布式发布-订阅消息系统
	②为什么要使用 kafka，为什么要使用消息队列
		(1)缓冲和削峰;上游数据时有突发流量，下游可能扛不住，或者下游没有足够多的机器来保证冗余，
		kafka在中间可以起到一个缓冲的作用，把消息暂存在kafka中，下游服务就可以按照自己的节奏进行慢慢处理。
		(2)
	③
	④

2>HashMap相关

	HashMap的主要成员变量：
        transient Node<K,V>[] table; 
        Node类型的数组（也有称作Hash桶）,多个Node节点构成链表，当链表长度大于8的时候转换为红黑树。
    HashMap的put方法：
        首次Put键值对的时候会先计算对应Key的hash值通过hash值来确定存放的地址，紧接着调用了putVal方法，
        在刚刚初始化之后的table值为null因此程序会进入到resize()方法中。而resize方法就是用来进行扩容的（稍后提到）。
        扩容后得到了一个table的节点（Node）数组，接着根据传入的hash值去获得一个对应节点p并去判断是否为空，
        是的话就存入一个新的节点（Node）。反之如果当前存放的位置已经有值了就会进入到else中去。接着根据前面得到
        的节点p的hash值以及key跟传入的hash值以及参数进行比较，如果一样则替覆盖。如果存在Hash碰撞就会以链表的形
        式保存，把当前传进来的参数生成一个新的节点保存在链表的尾部（JDK1.7保存在首部）。而如果链表的长度大于8那
        么就会以红黑树的形式进行保存。
    HashMap的put方法：
        get方法一样要通过hash code来获取数据。可以看到如果当前table没有数据的话直接返回null反之通过传进
        来的hash值找到对应节点（Node）first，如果first的hash值以及Key跟传入的参数匹配就返回对应的value反之判断
        是否是红黑树，如果是红黑树则从根节点开始进行匹配如果有对应的数据则结果否则返回Null，如果
        是链表的话就会循环查询链表，如果当前的节点不匹配的话就会从当前节点获取下一个节点来进行循环匹配，
        如果有对应的数据则返回结果否则返回Null。


3>ThreadLocal

	作用:线程安全
    应用场景:数据库连接,session管理
    ThreadLocal是线程本地存储，在每个线程中都创建了一个ThreadLocalMap对象，
    每个线程可以访问自己内部ThreadLocalMap对象内的value。

    ①实际的通过ThreadLocal创建的副本是存储在每个线程自己的threadLocals中的；

    ②为何threadLocals的类型ThreadLocalMap的键值为ThreadLocal对象，因为每个线程中可有多个threadLocal变量，
        就像上面代码中的longLocal和stringLocal；

    ③在进行get之前，必须先set，否则会报空指针异常；如果想在get之前不需要调用set就能正常访问的话，
        必须重写initialValue()方法。

    ④ThreadLocal不支持继承性

4>
①String类被final关键字修饰，意味着String类不能被继承，并且它的成员方法都默认为final方法；字符串一旦创建就不能再修改。
②String类实现了Serializable、CharSequence、 Comparable接口。
③String实例的值是通过字符数组实现字符串存储的。


5>结装箱和拆箱的实现过程：
装箱过程是通过调用包装器的valueOf方法实现的，而拆箱过程是通过引用类型调用xxxValue实现的。

6>AOP
面向切面编程
通俗理解:使用动态代理的方式在执行方法前后或者出现异常的时候做加入相关的逻辑


7>JDK动态代理跟CGLIB动态代理
JDK动态代理是对实现了接口的类去生成代理，而不能针对类

	//用户管理接口
	public interface UserManager
	{
		//新增用户抽象方法
		void addUser(String userName,String password);
		//删除用户抽象方法
		void delUser(String userName);
	}
	
	//实现该接口
	public class UserManagerImpl implements UserManager
	{
		@Override
		public void addUser(String userName, String password)
		{
			System.out.println("调用了新增的方法！");
			System.out.println("传入参数为 userName: "+userName+" password: "+password);
		}

		@Override
		public void delUser(String userName)
		{
			System.out.println("调用了删除的方法！");
			System.out.println("传入参数为 userName: "+userName);
		}
	}
	
	//JDK动态代理实现InvocationHandler接口
	public class JdkProxy implements InvocationHandler
	{
		private Object target ;//需要代理的目标对象

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable
		{
			System.out.println("JDK动态代理，监听开始！");
			Object result = method.invoke(target, args);
			System.out.println("JDK动态代理，监听结束！");
			return result;
		}
		//定义获取代理对象方法
		private Object getJDKProxy(Object targetObject)
		{
			//为目标对象target赋值
			this.target = targetObject;
			//JDK动态代理只能针对实现了接口的类进行代理，newProxyInstance 函数所需参数就可看出
			return Proxy.newProxyInstance(targetObject.getClass().getClassLoader(), targetObject.getClass().getInterfaces(), this);
		}

		public static void main(String[] args)
		{
			JdkProxy jdkProxy = new JdkProxy();//实例化JDKProxy对象
			UserManager user = (UserManager) jdkProxy.getJDKProxy(new UserManagerImpl());//获取代理对象
			user.addUser("admin", "123123");//执行新增方法
			user.delUser("admin");//执行删除方法
		}
	}

CGLIB是针对类实现代理，主要是对指定的类生成一个子类，覆盖其中的方法（继承）
JDK Proxy里是InvocationHandler，而cglib里一般就是MethodInterceptor，所有被代理的方法的调用是通过它们的invoke和intercept方法进行转接的，AOP的逻辑也是在这一层实现。

8>synchronized跟lock的区别
   
    ①lock是一个接口，而synchronized是java的一个关键字
    ②异常是否释放锁：
        synchronized在发生异常时候会自动释放占有的锁，因此不会出现死锁；
        而lock发生异常时候，不会主动释放占有的锁，必须手动unlock来释放锁，
        可能引起死锁的发生。（所以最好将同步代码块用try catch包起来，finally中写入unlock，避免死锁的发生。）
    ③是否响应中断:
        lock等待锁过程中可以用interrupt来中断等待，而synchronized只能等待锁的释放，不能响应中断；
    ④是否知道获取锁
        Lock可以通过trylock来知道有没有获取锁，而synchronized不能；

9>	String  StringBuild  StringBuffer
   
    String:对String对象的任何改变都不影响到原对象，相关的任何change操作都会生成新的对象
    执行效率:StringBuilder > StringBuffer > String
    当字符串相加操作或者改动较少的情况下，建议使用 String str="hello"这种形式；
    当字符串相加操作较多的情况下，建议使用StringBuilder，如果采用了多线程，则使用StringBuffer。

10>什么是线程:
	
    线程是操作系统能够进行运算调度的最小单位，它被包含在进程之中，是进程中的实际运作单位，可以使用多线程对进行运算提速。

11>JVM内存结构主要有三大块：堆内存、方法区和栈。
	
    ①java堆（Java Heap）:可通过参数 -Xms 和-Xmx设置
    Java堆是被所有线程共享,是Java虚拟机所管理的内存中最大的一块 Java堆在虚拟机启动时创建
    Java堆唯一的目的是存放对象实例，几乎所有的对象实例和数组都在这里
    Java堆为了便于更好的回收和分配内存，可以细分为：新生代和老年代；再细致一点的有Eden空间、From Survivor空间、To Survivor区
    新生代：包括Eden区、From Survivor区、To Survivor区，系统默认大小8:1:1。
    老年代：在年轻代中经历了N次垃圾回收后仍然存活的对象，就会被放到年老代中。因此，可以认为年老代中存放的都是一些生命周期较长的对象。

	②java虚拟机栈(stack):可通过参数 栈帧是方法运行期的基础数据结构栈容量可由-Xss设置
	Java虚拟机栈是线程私有的，它的生命周期与线程相同。
	每一个方法被调用直至执行完成的过程，就对应着一个栈帧在虚拟机栈中从入栈到出栈的过程。
	虚拟机栈是执行Java方法的内存模型(也就是字节码)服务：每个方法在执行的同时都会创建一个栈帧，用于存储 局部变量表、操作数栈、动态链接、方法出口等信息
	
	③方法区（Method Area）:可通过参数-XX:MaxPermSize设置
	线程共享内存区域，用于储存已被虚拟机加载的类信息、常量、静态变量，即编译器编译后的代码，方法区也称持久代（Permanent Generation）。
	方法区主要存放java类定义信息，与垃圾回收关系不大，方法区可以选择不实现垃圾回收,但不是没有垃圾回收。
	方法区域的内存回收目标主要是针对常量池的回收和对类型的卸载。
	运行时常量池，也是方法区的一部分，虚拟机加载Class后把常量池中的数据放入运行时常量池。
	
	垃圾收集器：
	新生代：
		serial 单线程收集，进行垃圾收集时，必须暂停所有工作线程
		ParNew 多线程收集，其余的行为、特点和Serial收集器一样
		Parallel Scavenge 比起关注用户线程停顿时间，更关注系统的吞吐量（适合后台运算，不需要太多交互运算的场景）
	老年代：
		CMS：CMS收集器是以获取最短停顿时间为目标的收集器
		serialOld：
		ParallelOld：
	G1（Garbage First）：G1收集器是JDK9的默认垃圾收集器，而且不再区分年轻代和老年代进行回收。
12>JDBC执行流程
	
    ①加载驱动
    //加载MySql驱动
    Class.forName("com.mysql.jdbc.Driver")
    //加载Oracle驱动
    Class.forName("oracle.jdbc.driver.OracleDriver")

	②获取数据库连接
	DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/imooc", "username", "password");
	
	③创建Statement/PreparedStatement对象（传输器对象）
	conn.createStatement();
	conn.prepareStatement(sql);
	executeQuery(String sql) -- 用于向数据库发送查询类型的sql语句，返回一个ResultSet对象中
	executeUpdate(String sql) -- 用于向数据库发送更新(增加、删除、修改)类型的sql语句，返回一个int值，表示影响的记录行数
	
	④获取结果集
	ResultSet对象 遍历数据行 .next()
	
	⑤释放资源
	越晚获取的越先关闭;
	关闭 Resultset对象，再关闭Statement/PreparedStatement对象，最后关闭Connection对象。


13>3次握手的过程
TCP握手协议 ：在TCP/IP协议中,TCP协议提供可靠的连接服务,采用三次握手建立一个连接。
SYN：同步序列编号(Synchronize Sequence Numbers)

	一个SYN包就是仅SYN标记设为1的TCP包;
	ACK包就是仅ACK 标记设为1的TCP包;
	SYN/ACK包是仅SYN 和 ACK 标记为1的包.
	
	假如服务器A和客户机B通讯. 当A要和B通信时，B首先向A发一个SYN (Synchronize Sequence Numbers)标记的包，告诉A请求建立连接. 
	只有当A受到B发来的SYN包，才可建立连接，除此之外别无他法。因此，如果你的防火墙丢弃所有的发往外网接口的SYN包，那么你将不能让外部任何主机主动建立连接。
	
	(客户端A) --> [SYN] --> (服务器B) 
	①第一次握手：建立连接时，客户端A发送SYN包(SYN=j)到服务器B，并进入SYN_SEND状态，等待服务器B确认。
	
	(客户端A) <-- [SYN/ACK] <--(服务器B)
	②第二次握手：服务器B收到SYN包，必须确认客户A的SYN(ACK=j+1)，同时自己也发送一个SYN包(SYN=k)，即SYN+ACK包，此时服务器B进入SYN_RECV状态。

	(客户端A) --> [ACK] --> (服务器B)
	③第三次握手：客户端A收到服务器B的SYN＋ACK包，向服务器B发送确认包ACK(ACK=k+1)，此包发送完毕，客户端A和服务器B进入ESTABLISHED状态，完成三次握手。

14>4次挥手过程
四次挥手用来关闭已建立的TCP连接.

	(客户端A) --> ACK/FIN --> (服务器B)
	①第一次挥手:客户端A发送一个FIN，用来关闭客户A到服务器B的数据传送。

	(客户端A) <-- ACK <-- (服务器B)
	②第二次挥手:服务器B收到这个FIN，它发回一个ACK，确认序号为收到的序号加1。和SYN一样，一个FIN将占用一个序号。

	(客户端A) <-- ACK/FIN <-- (服务器B) 
	③第三次挥手:服务器B关闭与客户端A的连接，发送一个FIN给客户端A。

	(客户端A) --> ACK --> (服务器B)
	④第四次挥手:客户端A发回ACK报文确认，并将确认序号设置为收到序号加1。

15>事务四大特性(ACID)
原子性(Atomicity):事务开始后所有操作要么全部成功，要么全部失败，不可能停滞在中间某个环节;
一致性(Consistency):一个事务执行之前和执行之后都必须处于一致性状态;假设用户A和用户B两者的钱加起来一共是5000，那么不管A和B之间如何转账，转几次账，事务结束后两个用户的钱相加起来应该还是5000，这就是事务的一致性
隔离性(Isolation):指当多个用户并发访问数据库时，数据库为每一个用户开启的事务不能被其他的事务操作所干扰，多个并发事务之间要相互隔离。同一时间，只允许一个事务请求同一组数据。不同的事务彼此之间没有干扰。
持久性(Durability):一个事务一旦被提交，则对数据库的所有更新将被保存到数据库中，不能回滚。

16>	Mysql跟Oracle的分页
①Mysql分页: select * from stu limit m, n;//m = (startPage-1)*pageSize,n = pageSize
第一个参数值m表示起始行，第二个参数表示取多少行（页面大小）
m、n参数值不能在语句当中写计算表达式，写到语句之前必须计算好值。
②Oracle分页:
select * from (select rownum rn,a.* from table_name a where rownum <= x) where rn >= y;
//结束行，x = startPage*pageSize
//起始行，y = (startPage-1)*pageSize+1
rownum只能比较小于，不能比较大于，因为rownum是先查询后排序的

17>	Mysql跟Oracle的主键自增
①Mysql主键自增,mybatis中添加数据
<insert id="saveSmartUser" parameterType="com.sun.repository.bean.SmartUser"
useGeneratedKeys="true" keyProperty="id">
INSERT INTO SMART_USER(USERNAME, PASSWORD, AGE, GENDER) VALUES
(#{userName},#{password},#{age},#{gender})
</insert>

	②Oracle主键自增:
	(1)序列（SEQUENCE）
	创建序列:
	CREATE SEQUENCE SEQ_SMART_USER_ID MINVALUE 1 MAXVALUE 999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  CYCLE ;
	获取序列值: .NEXTVAL
	SELECT SEQ_SMART_USER_ID.NEXTVAL FROM DUAL
	
	(2)创建序列（SEQUENCE）后创建触发器（TRIGGER）


18>脏读、不可重复读、幻读

    ①脏读:
        指当一个事务正在访问数据，并且对数据进行了修改，而这种数据还没有提交到数据库中，这时，另外一个事务也访问这个数据，然后使用了这个数据。
        因为这个数据还没有提交那么另外一个事务读取到的这个数据我们称之为脏数据;
    ②不可重复读:
        指在一个事务内，多次读同一数据。在这个事务还没有执行结束，另外一个事务也访问该同一数据，那么在第一个事务中的两次读取数据之间，
        由于第二个事务的修改第一个事务两次读到的数据可能是不一样的，这样就发生了在一个事物内两次连续读到的数据是不一样的，这种情况被称为是不可重复读。
    ③幻读:
        一个事务先后读取一个范围的记录，但两次读取的纪录数不同，我们称之为幻象读（两次执行同一条 select 语句会出现不同的结果，第二次读会增加一数据行，并没有说这两次执行是在同一个事务中）
        不可重复读侧重于修改，幻读侧重于新增或删除
19>隔离级别

	①读未提交(Read uncommitted):一个事务可以读取另一个未提交事务的数据
    ②读已提交(Read Committed):Oracle的默认隔离级别,这种隔离级别能够有效的避免脏读
    ③可重复读(Repeated Read):有效的避免不可重复读,MySql的默认隔离级别
    ④串行化(Serializable):它是在每个读的数据行上加上共享锁。在这个级别，可能导致大量的超时现象和锁竞争。


20>spring事务传播行为（propagation behavior）指的就是当一个事务方法被另一个事务方法调用时，这个事务方法应该如何进行。 	

    ①PROPAGATION_REQUIRED:如果存在一个事务，则支持当前事务。如果没有事务则开启一个新的事务。

    ②PROPAGATION_SUPPORTS:如果存在一个事务，支持当前事务。如果没有事务，则非事务的执行。

    ③PROPAGATION_MANDATORY:如果已经存在一个事务，支持当前事务。如果没有一个活动的事务，则抛出异常。

    ④PROPAGATION_REQUIRES_NEW:使用PROPAGATION_REQUIRES_NEW,需要使用 JtaTransactionManager作为事务管理器。 它会开启一个新的事务。如果一个事务已经存在，则先将这个存在的事务挂起。

    ⑤PROPAGATION_NOT_SUPPORTED:PROPAGATION_NOT_SUPPORTED 总是非事务地执行，并挂起任何存在的事务。使用PROPAGATION_NOT_SUPPORTED,也需要使用JtaTransactionManager作为事务管理器。

    ⑥PROPAGATION_NEVER:总是非事务地执行，如果存在一个活动事务，则抛出异常。

    ⑦PROPAGATION_NESTED:如果一个活动的事务存在，则运行在一个嵌套的事务中。 如果没有活动事务, 则按TransactionDefinition.PROPAGATION_REQUIRED 属性执行。

21>http1.0与http1.1的区别

    ①HTTP 1.1支持长连接,HTTP 1.0每次请求都要与服务器建立tcp连接;
        需要增加新的请求头来实现长连接:
        Connection请求头的值为Keep-Alive时,客户端通知服务器返回本次请求结果后保持连接;
        Connection请求头的值为close时，客户端通知服务器返回本次请求结果后关闭连接;
        HTTP 1.1还提供了与身份认证、状态管理和Cache缓存等机制相关的请求头和响应头。

    ②HTTP 1.1增加host字段
        由于服务器上可以部署多台虚拟机, HTTP1.1的请求消息和响应消息都应支持Host头域，
        且请求消息中如果没有Host头域会报告一个错误（400 Bad Request）。

    ③100(Continue) Status(节约带宽)
        客户端事先发送一个只带头域的请求，如果服务器因为权限拒绝了请求，就回送响应码401（Unauthorized）；
        如果服务器接收此请求就回送响应码100，客户端就可以继续发送带实体的完整请求了.

    ④HTTP/1.1中引入了Chunked transfer-coding来解决上面这个问题，发送方将消息分割成若干个任意大小的数据块，
        每个数据块在发送时都会附上块的长度，最后用一个零长度的块作为消息结束的标志。这种方法允许发送方只缓冲消息的一个片段，
        避免缓冲整个消息带来的过载。

    ⑤HTTP/1.1在1.0的基础上加入了一些cache的新特性，当缓存对象的Age超过Expire时变为stale对象，cache不需要直接抛弃stale对象，
        而是与源服务器进行重新激活（revalidation）。

22>	http 请求报文

        ①请求行: 求方法字段、URL字段和HTTP协议版本

        ②请求头(key value形式):
            User-Agent：产生请求的浏览器类型。
            Accept：客户端可识别的内容类型列表。
            Host：主机地址

        ③请求数据:post方法中，会把数据以key value形式发送请求

        ④空行:发送回车符和换行符，通知服务器以下不再有请求头

23>响应报文
状态行:
消息报头:
响应正文:

24>post请求的body体有数据大小限制么
长度限制是有浏览器和web应用服务器决定的,tomcat默认限制post大的大小是2M
设置post请求的大小（以tomcat为例），在conf/server.xml 文件中，在 <Connector/> 标签中设置  maxPostSize="0"，maxPostSize="0"，就代表post请求的参数无限制。

25>常见Mysql的慢查询优化方式
第一步.开启mysql慢查询
方式一:修改配置文件  在 my.ini 增加几行:  主要是慢查询的定义时间（超过2秒就是慢查询），以及慢查询log日志记录（ slow_query_log）
//开启慢查询,定义超过多少秒的查询算是慢查询
slow_query_log = on
long_query_time=2
//记录下没有使用索引的查询
log-querise-not-using-indexes=ON;

	方法二：通过MySQL数据库开启慢查询
	set global slow_query_log = ON;
	set global long_query_time = 3600;
	set global log_querise_not_using_indexes = ON;
	
	利用explain关键字可以模拟优化器执行SQL查询语句

26>双亲委派模型
如果一个类加载器收到类加载的请求，它首先不会自己去尝试加载这个类，而是把这个请求委派给父类加载器完成。
每个类加载器都是如此，只有当父加载器在自己的搜索范围内找不到指定的类时（即ClassNotFoundException），子加载器才会尝试自己去加载。

27>TCP释放连接时为什么time_wait状态必须等待2MSL时间?
①为了保证客户端发送的最后一个ACK报文段能够到达服务器。因为这个ACK有可能丢失，从而导致处在LAST-ACK状态的服务器收不到对FIN-ACK的确认报文。服务器会超时重传这个FIN-ACK，
接着客户端再重传一次确认，重新启动时间等待计时器。最后客户端和服务器都能正常的关闭。假设客户端不等待2MSL，而是在发送完ACK之后直接释放关闭，一但这个ACK丢失的话，服务器就无法正常的进入关闭连接状态。
②他还可以防止已失效的报文段。客户端在发送最后一个ACK之后，再经过经过2MSL，就可以使本链接持续时间内所产生的所有报文段都从网络中消失。从保证在关闭连接后不会有还在网络中滞留的报文段去骚扰服务器。
注意：在服务器发送了FIN-ACK之后，会立即启动超时重传计时器。客户端在发送最后一个ACK之后会立即启动时间等待计时器。

28>MyBatis 有哪些拦截器？如何实现拦截功能？
①Executor：拦截内部执行器，它负责调用 StatementHandler 操作数据库，并把结果
集通过 ResultSetHandler 进行自动映射，另外它还处理了二级缓存的操作。
②StatementHandler：拦截 SQL 语法构建的处理，它是 MyBatis 直接和数据库执行SQL 脚本的对象，另外它也实现了 MyBatis 的一级缓存。
③ParameterHandler：拦截参数的处理。
④ResultSetHandler：拦截结果集的处理。

28>类的加载

    Java 的类加载过程可以分为 5 个阶段：加载、验证、准备、解析和初始化。这 5 个阶段一般是顺序发生的，
    但在动态绑定的情况下，解析阶段发生在初始化阶段之后。
	(1)加载
        主要目的是将字节码从不同的数据源（可能是 class 文件、也可能是 jar 包，甚至网络）
        转化为二进制字节流加载到内存中，并生成一个代表该类的 java.lang.Class 对象。
	(2)验证
        JVM 会在该阶段对二进制字节流进行校验，只有符合 JVM 字节码规范的才能被 JVM 正确执行。
        该阶段是保证 JVM 安全的重要屏障，下面是一些主要的检查。
            确保二进制字节流格式符合预期。
            是否所有方法都遵守访问控制关键字的限定。
            方法调用的参数个数和类型是否正确。
            确保变量在使用之前被正确初始化了。
            检查变量是否被赋予恰当类型的值。
	(3)准备
        JVM 会在该阶段对类变量（也称为静态变量，static 关键字修饰的）
        分配内存并初始化（对应数据类型的默认初始值，如 0、0L、null、false 等）。
        也就是说，假如有这样一段代码：
        public String chenmo = "沉默";
        public static String wanger = "王二";
        public static final String cmower = "沉默王二";
        chenmo 不会被分配内存，而 wanger 会；但 wanger 的初始值不是“王二”而是 null。
        需要注意的是，static final 修饰的变量被称作为常量，和类变量不同。
        常量一旦赋值就不会改变了，所以 cmower 在准备阶段的值为“沉默王二”而不是 null。
	(4)解析
        该阶段将常量池中的符号引用转化为直接引用。
        在编译时，Java 类并不知道所引用的类的实际地址，因此只能使用符号引用来代替。
        直接引用通过对符号引用进行解析，找到引用的实际内存地址。
        比如 com.Wanger 类引用了 com.Chenmo 类，编译时 Wanger 类并不知道 Chenmo 类的实际内存地址，因此只能使用符号 com.Chenmo。
	(5)初始化
        初始化阶段是执行类构造器方法的过程。
        String cmower = new String("沉默王二");
        上面这段代码使用了 new 关键字来实例化一个字符串对象，
        那么这时候，就会调用 String 类的构造方法对 cmower 进行实例化。
	
29>类加载器
    
        (1)启动类加载器（Bootstrap Class-Loader），加载 jre/lib 包下面的 jar 文件，比如说常见的 rt.jar。
        (2)扩展类加载器（Extension or Ext Class-Loader），加载 jre/lib/ext 包下面的 jar 文件。
        (3)应用类加载器（Application or App Clas-Loader），根据程序的类路径（classpath）来加载 Java 类。
	    
        来来来，通过一段简单的代码了解下。
        public class Test {
            public static void main(String[] args) {
                //com.example.whdemo.Panda
                ClassLoader loader = Panda.class.getClassLoader();
                while (loader != null) {
                    System.out.println(loader.toString());
                    loader = loader.getParent();
                }
            }
        }
        每个 Java 类都维护着一个指向定义它的类加载器的引用，通过 类名.class.getClassLoader() 可以获取到此引用；
        然后通过 loader.getParent() 可以获取类加载器的上层类加载器。
        
        这段代码的输出结果如下：
        
        sun.misc.Launcher$AppClassLoader@18b4aac2
        sun.misc.Launcher$ExtClassLoader@5b6f7412
        第一行输出为 Panda 的类加载器，即应用类加载器，它是 sun.misc.Launcher$AppClassLoader 类的实例；
        第二行输出为扩展类加载器，是 sun.misc.Launcher$ExtClassLoader 类的实例。那启动类加载器呢？
        按理说，扩展类加载器的上层类加载器是启动类加载器，但在我这个版本的 JDK 中， 
        扩展类加载器的 getParent() 返回 null。所以没有输出。


29>双亲委派模型跟破坏双亲委派模型

        (1)双亲委派模型
            某个特定的类加载器在接到加载类的请求时，首先将加载任务委托给父类加载器，
            依次递归，如果父类加载器可以完成类加载任务，
            就成功返回；只有父类加载器无法完成此加载任务时，才自己去加载。
            
        为什么使用双亲委派模型
            安全性，避免加载一些自定义的类，例如自定义的一个java.lang.String类
        (2)破坏双亲委派模型
            原生的JDBC中Driver驱动本身只是一个接口，并没有具体的实现，具体的实现是由不同数据库类型去实现的。
            例如，MySQL的mysql-connector-.jar中的Driver类具体实现的。 原生的JDBC中的类是放在rt.jar包的，
            是由启动类加载器进行类加载的，在JDBC中的Driver类中需要动态去加载不同数据库类型的Driver类，
            而mysql-connector-.jar中的Driver类是用户自己写的代码，那启动类加载器肯定是不能进行加载的，
            既然是自己编写的代码，那就需要由应用程序启动类去进行类加载。于是乎，
            这个时候就引入线程上下文件类加载器(Thread Context ClassLoader)。有了这个东西之后，
            程序就可以把原本需要由启动类加载器进行加载的类，由应用程序类加载器去进行加载了。


30>网络分层

        网络分层
        <1>物理层
        <2>链路层
        <3>网络层
        <4>传输层
        <5>应用层

30>sleep跟wait的区别

        <1>sleep()不释放同步锁,wait()释放同步锁. 
            sleep(milliseconds)可以用时间指定来使他自动醒过来,如果时间不到你只能调用interreput()来强行打断;
            wait()可以用notify()直接唤起.
        <2>sleep必须捕获异常，而wait，notify和notifyAll不需要捕获异常

30>反射

        JAVA反射机制是在运行状态中，对于任意一个类，都能够知道这个类的所有属性和方法；
        对于任意一个对象，都能够调用它的任意方法和属性；
        这种动态获取信息以及动态调用对象方法的功能称为java语言的反射机制。

31>spring bean的生命周期

        概述：通过构造方法或工厂方法创建bean对象——>为bean属性赋值——>调用 bean 的初始化方法，
                即init-method指定方法——>bean实例化完毕，
                可以使用——>容器关闭, 调用 bean 的销毁方法，即destroy-method指定方法。

32>目前java中可作为GC Root的对象有

        1，虚拟机栈中引用的对象（本地变量表）

        2，方法区中静态属性引用的对象
        
        3，方法区中常量引用的对象
        
        4，本地方法栈中引用的对象（Native Object）	

33>多线程join的作用

        示例：
        public static void main(String[] args) throws InterruptedException
        {
            System.out.println("main start");
    
            Thread t1 = new Thread(new Worker("thread-1"));
            t1.start();
            t1.join();
            System.out.println("main end");
        }
	main线程要等到t1线程运行结束后，才会输出“main end”。如果不加t1.join(),
    main线程和t1线程是并行的。而加上t1.join(),程序就变成是顺序执行了


34>JDK动态代理与CGLIB动态代理

        JDK1.8下JDK动态代理要优于CGLIB动态代理
        参考：ProxyPerformanceTest类的验证

        Spring同时使用了两种动态代理机制，依据如下：
        ①当Bean实现接口时，Spring就会用JDK的动态代理
        ②当Bean没有实现接口时，Spring使用CGlib是实现
        ③可以强制使用CGlib（@EnableAspectJAutoProxy(proxyTargetClass = true)）

