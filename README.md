
## 知识点梳理
面试被问到的知识点，然后查看别人的文章总结过来的，CV大法。
主要是这里 https://javaguide.cn/，其他的没有记录了。


### 1.redis
	本质上是一个Key-Value类型的内存数据库，很像memcached，整个数据库统加载在内存当中进行操作，定期通过
    异步操作把数据库数据flush到硬盘上进行保存。因为是纯内存操作，redis的性能非常出色，每秒可以处理超过 
    10万次读写操作， 是已知性能最快的Key-Value DB。 redis的出色之处不仅仅是性能，redis最大的魅力是支持
    保存多种数据结构，此外单个value的最大限制是1GB，不像 memcached只能保存1MB的数据，因此redis可以用
    来实现很多有用的功能，比方说用他的List来做FIFO双向链表，实现一个轻量级的高性 能消息队列服务，
    用他的Set可以做高性能的tag系统等等。另外redis也可以对存入的Key-Value设置expire时间，因此也可
    以被当作一 个功能加强版的memcached来用。redis的主要缺点是数据库容量受到物理内存的限制，
    不能用作海量数据的高性能读写，因此redis适合的场景主要局限在较小数据量的高性能操作和运算上。
    
	redis常用命令:
    https://www.cnblogs.com/cxxjohnson/p/9072383.html

### 2.redis相比memcached有哪些优势

	① memcached所有的值均是简单的字符串，redis作为其替代者，支持更为丰富的数据类型
    ② redis的速度比memcached快很多
    ③ redis可以持久化其数据

### 3.redis的数据类型,应用场景

	redis支持5种数据类型：string（字符串）,hash（哈希）,list（列表）,set（集合）及zset(sorted set：有序集合)。
    ① string : 常规key-value缓存应用。常规计数: 微博数,粉丝数。
    ② hash : 存储用户信息，商品信息，订单信息
    ③ list : 好友列表，粉丝列表，消息队列，最新消息排行等。
    ④ set : 共同好友、共同兴趣、分类标签
    ⑤ sorted set : 网站排行榜
    参考链接 : https://blog.csdn.net/hi_alan/article/details/86530769

### 4.redis淘汰策略

	① volatile-lru ：从已设置过期时间的数据集（server.db[i].expires）中挑选最近最少使用的数据淘汰
    ② volatile-ttl ：从已设置过期时间的数据集中挑选将要过期的数据淘汰
    ③ volatile-random ：从已设置过期时间的数据集中任意选择数据淘汰
    ④ allkeys-lru ：从数据集（server.db[i].dict）中挑选最近最少使用的数据淘汰
    ⑤ allkeys-random ：从数据集中任意选择数据淘汰
    ⑥ no-enviction（驱逐）：禁止驱逐数据,这也是默认策略。意思是当内存不足以容纳新入数据时，
    新写入操作就会报错，请求可以继续进行，线上任务也不能持续进行，采用no-enviction策略可以保证数据不被丢失。

### 5.redis key的过期时间和永久有效分别怎么设置

	EXPIRE和PERSIST命令。

### 6.redis 持久化方式

	① RDB持久化方式能够在指定的时间间隔能对你的数据进行快照存储.
    ② AOF持久化方式记录每次对服务器写的操作,当服务器重启的时候会重新执行这些命令来恢复原始的数据,
        AOF命令以redis协议追加保存每次写的操作到文件末尾.redis还能对AOF文件进行后台重写,使得AOF文件的体积不至于过大.
        如果你只希望你的数据在服务器运行的时候存在,你也可以不使用任何持久化方式.
        你也可以同时开启两种持久化方式, 在这种情况下, 当redis重启的时候会优先载入AOF文件来恢复原始的数据,
        因为在通常情况下AOF文件保存的数据集要比RDB文件保存的数据集要完整.

    redis问题补充:redis缓存雪崩、缓存穿透、热点Key

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
    
    redis事务的三个阶段
        1.事务开始 MULTI
        2.命令入队
        3.事务执行 EXEC
			
------------------------------------------------------------------------------------------------------------------------------------------------------------------------

### 7.Spring支持的几种bean的作用域

	① singleton : bean在每个Springioc容器中只有一个实例。
    ② prototype ：一个bean的定义可以有多个实例。
    ③ request ：每次http请求都会创建一个bean，该作用域仅在基于web的SpringApplicationContext情形下有效。
    ④ session ：在一个HTTPSession中，一个bean定义对应一个实例。该作用域仅在基于web的SpringApplicationContext情形下有效。
    ⑤ global-session ：在一个全局的HTTPSession中，一个bean定义对应一个实例。该作用域仅在基于web的SpringApplicationContext情形下有效。

### 8.Spring框架中的单例bean是线程安全的吗

	不，Spring框架中的单例bean不是线程安全的。

### 9.java线程池大小设置多少合适

	最佳线程数目 = （线程等待时间与线程CPU时间之比 + 1）* CPU数目
    线程等待时间所占比例越高，需要越多线程。线程CPU时间所占比例越高，需要越少线程。

	https://blog.csdn.net/lieyanhaipo/article/details/87877853

### 10.Java线程池七个参数详解

	https://blog.csdn.net/ye17186/article/details/89467919

	分别是corePoolSize、maximumPoolSize、keepAliveTime、unit、workQueue、threadFactory、handler
	① corePoolSize 线程池核心线程大小
		线程池中会维护一个最小的线程数量，即使这些线程处理空闲状态，他们也不会被销毁，除非设置了allowCoreThreadTimeOut。这里
		的最小线程数量即是corePoolSize。
		
	② maximumPoolSize 线程池最大线程数量
		一个任务被提交到线程池以后，首先会找有没有空闲存活线程，如果有则直接将任务交给这个空闲线程来执行，如果没有则会缓存到工
		作队列中，如果工作队列满了，
		才会创建一个新线程，然后从工作队列的头部取出一个任务交由新线程来处理，
		而将刚提交的任务放入工作队列尾部。线程池不会无限制的去创建新线程，它会有一个最大线程数量的限制，这个数量即由
		maximunPoolSize的数量减去corePoolSize的数量来确定。
		
	③ keepAliveTime 空闲线程存活时间
		一个线程如果处于空闲状态，并且当前的线程数量大于corePoolSize，那么在指定时间后，这个空闲线程会被销毁，这里的指定时间
		由keepAliveTime来设定
		
	④ unit 空闲线程存活时间单位
		keepAliveTime的计量单位
		
	⑤ workQueue 工作队列                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 
		新任务被提交后，会先进入到此工作队列中，任务调度时再从队列中取出任务。
		jdk中提供了四种工作队列：
		(1)ArrayBlockingQueue
		基于数组的有界阻塞队列，按FIFO排序。新任务进来后，会放到该队列的队尾，有界的数组可以防止资源耗尽问题。当线程池中线程
		数量达到corePoolSize后，再有新任务进来，则会将任务放入该队列的队尾，等待被调度。如果队列已经是满的，则创建一个新线程，
		如果线程数量已经达到maxPoolSize，则会执行拒绝策略。
		
		(2)LinkedBlockingQuene
		基于链表的无界阻塞队列（其实最大容量为Interger.MAX），按照FIFO排序。由于该队列的近似无界性，当线程池中线程数量达到
		corePoolSize后，再有新任务进来，会一直存入该队列，而不会去创建新线程直到maxPoolSize，因此使用该工作队列时，
		参数maxPoolSize其实是不起作用的。
		
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

### 11.常见线程池

	① newSingleThreadExecutor
        单个线程的线程池，即线程池中每次只有一个线程工作，单线程串行执行任务
        
    ② newFixedThreadExecutor(n)
        固定数量的线程池，没提交一个任务就是一个线程，直到达到线程池的最大数量，然后后面进入等待队列直到前面的任务
        完成才继续执行
        
    ③ newCacheThreadExecutor（推荐使用）
        可缓存线程池，当线程池大小超过了处理任务所需的线程，那么就会回收部分空闲（一般是60秒无执行）的线程，
        当有任务来时，又智能的添加新线程来执行。
        
    ④ newScheduleThreadExecutor
        大小无限制的线程池，支持定时和周期性的执行线程

    线程池的工作流程：
        1.用户提交任务给线程池；
        2.判断是否大于【核心线程数】；
            2-1>：是，判断缓存队列是否已满
                2-1-1>：是，判断是否大于【最大线程数】
                    2-1-1-1>：是，拒绝任务执行
                    2-1-1-2>：否，重新创建线程
                2-1-2>：否，缓存到队列中
            2-2>：否，创建线程，执行任务

### 12.创建线程的主要方式

	①继承Thread类
    通过继承(extends)Thread并且重写其run()，run方法中即线程执行任务。创建后的子类通过调用 start() 方法即可执行线程方法。

	②通过Runnable接口创建线程类
	该方法需要先 定义一个类实现(implements)Runnable接口，并重写该接口的 run() 方法，
	此run方法是线程执行体。接着创建 Runnable实现类的对象，作为创建Thread对象的参数target，
	此Thread对象才是真正的线程对象。通过实现Runnable接口的线程类，是互相共享资源的。
	
	③使用Callable和Future创建线程
	从继承Thread类和实现Runnable接口可以看出，上述两种方法都不能有返回值，且不能声明抛出异常。而Callable接口则实现了此两点，
	Callable接口如同Runable接口的升级版，其提供的call()方法将作为线程的执行体，同时允许有返回值。
    但是Callable对象不能直接作为Thread对象的target，因为Callable接口是Java5 新增的接口，
    不是Runnable接口的子接口。对于这个问题的解决方案，就引入 Future接口，
    此接口可以接受call() 的返回值，RunnableFuture接口是Future接口和Runnable接口的子接口，
    可以作为Thread对象的target 。并且， Future 接口提供了一个实现类：FutureTask 。
    FutureTask实现了RunnableFuture接口，可以作为 Thread对象的target。


### 13.kafka重复消费,堆积等问题

	原因分析
    Kafka消费者有两个配置参数：
    max.poll.interval.ms
    两次poll操作允许的最大时间间隔。单位毫秒。默认值300000（5分钟）。
    两次poll超过此时间间隔，Kafka服务端会进行rebalance操作，导致客户端连接失效，无法提交offset信息，从而引发重复消费。
    max.poll.records
    一次poll操作获取的消息数量。默认值50。
    如果每条消息处理时间超过60秒，那么一批消息处理时间将超过5分钟，从而引发poll超时，最终导致重复消费。

	消息重复消费和消息丢包的解决办法

	1.消息发送（生产者）
        Kafka消息发送有两种方式：同步（sync）和异步（async），默认是同步方式，可通过producer.type属性进行配置

        Kafka通过配置request.required.acks属性来确认消息的生产：

        0---表示不进行消息接收是否成功的确认；

        1---表示当Leader接收成功时确认；
        
        -1---表示Leader和Follower都接收成功时确认；

    2.消息消费（消费者）
        Kafka消息消费有两个consumer接口，Low-levelAPI和High-levelAPI：
        
        Low-levelAPI：消费者自己维护offset等值，可以实现对Kafka的完全控制；
        
        High-levelAPI：封装了对parition和offset的管理，使用简单；

    消息丢失：
        同步模式下，确认机制设置为-1，即让消息写入Leader和Follower之后再确认消息发送成功；
        设置 -1 保证produce写入所有副本算成功 
        producer.type = sync
        request.required.acks=-1
        
        异步模式下，为防止缓冲区满，可以在配置文件设置不限制阻塞超时时间，当缓冲区满时让生
        产者一直处于阻塞状态；
    
        消费者 （offset手动提交，业务逻辑成功处理后，提交offset）

    消息重复消费：
        将消息的唯一标识保存到外部介质中，每次消费时判断是否处理过即可。
        保证不重复消费：落表（主键或者唯一索引的方式，避免重复数据）
        业务逻辑处理（选择唯一主键存储到redis或者mongdb中，先查询是否存在，若存在则不处理；若不存在，
        先插入redis或Mongdb,再进行业务逻辑处理）


### 14.HashMap相关

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

    HashMap的get方法：
        get方法一样要通过hash code来获取数据。可以看到如果当前table没有数据的话直接返回null反之通过传进
        来的hash值找到对应节点（Node）first，如果first的hash值以及Key跟传入的参数匹配就返回对应的value反之判断
        是否是红黑树，如果是红黑树则从根节点开始进行匹配如果有对应的数据则结果否则返回Null，如果
        是链表的话就会循环查询链表，如果当前的节点不匹配的话就会从当前节点获取下一个节点来进行循环匹配，
        如果有对应的数据则返回结果否则返回Null。

    HashMap是先插入还是先扩容:
        HashMap初始化后首次插入数据时，先发生resize扩容再插入数据，之后每当插入的数据个数达到
        threshold时就会发生resize，此时是先插入数据再resize。
    
    (h = key.hashCode()) ^ (h >>> 16)含义：
        h = key.hashCode()是key对象的一个hashCode；
        h >>> 16的意思是将h右移16位，然后高位补0，然后再与(h = key.hashCode()) 异或运算得到最终的h值 

    为什么异或运算的散列性更好：
        为通过异或运算得到的h值会更加分散，哈希冲突也就更少
    1.7 HashMap的死循环问题：
        主要是1.7链表使用的是头插法，多线程并发情况下在扩容的时候可能会形成循环链表

### 15.ThreadLocal

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
    
    ThreadLocal、ThreadLocalMap、Thread之间的关系：
        ThreadLocalMap是ThreadLocal内部类，由ThreadLocal创建；
        Thread有ThreadLocal.ThreadLocalMap类型的属性
        
    Synchronized用于线程间的数据共享，而ThreadLocal则用于线程间的数据隔离

### 16.String类的理解

	①String类被final关键字修饰，意味着String类不能被继承，并且它的成员方法都默认为final方法；字符串一旦创建就不能再修改。
    ②String类实现了Serializable、CharSequence、 Comparable接口。
    ③String实例的值是通过字符数组实现字符串存储的。


### 17.结装箱和拆箱的实现过程：

	装箱过程是通过调用包装器的valueOf方法实现的，而拆箱过程是通过引用类型调用xxxValue实现的。

### 18.AOP

	面向切面编程
	基本概念：
	    Aspect：
	        切面，在Spring中意为所有通知方法所在的类
	        
	    Jointpoint（连接点）：
	        具体的切面点点抽象概念，可以是在字段、方法上，Spring中具体表现形式是PointCut（切入点），仅作用在方法上
       
        Advice（通知）: 
            在连接点进行的具体操作，如何进行增强处理的，分为前置、后置、异常、最终、环绕五种情况，Spring将通知抽象为拦截器
        
        Pointcut：
            切点，与通知一起出现，使用专门的切点表达式决定在何处执行通知方法
        
        目标对象：
            被AOP框架进行增强处理的对象，也被称为被增强的对象
        
        AOP代理：
            AOP框架创建的对象，简单的说，代理就是对目标对象的加强。Spring中的AOP代理可以是JDK动态代理，也可以是CGLIB代理
        
        Weaving（织入）：
            将增强处理添加到目标对象中，创建一个被增强的对象的过程 
            
	通俗理解:使用动态代理的方式在执行方法前后或者出现异常的时候做加入相关的逻辑


### 19.JDK动态代理跟CGLIB动态代理

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

    JDK Proxy里是InvocationHandler，而cglib里一般就是MethodInterceptor，所有被代理的方法的调用是通过它们的invoke
        和intercept方法进行转接的，AOP的逻辑也是在这一层实现。

### 20.synchronized跟lock的区别

    ①lock是一个接口，而synchronized是java的一个关键字
    
    ②异常是否释放锁：
        synchronized在发生异常时候会自动释放占有的锁，因此不会出现死锁；
        而lock发生异常时候，不会主动释放占有的锁，必须手动unlock来释放锁，
        可能引起死锁的发生。（所以最好将同步代码块用try catch包起来，finally中写入unlock，避免死锁的发生。）
    
    ③是否响应中断:
        lock等待锁过程中可以用interrupt来中断等待，而synchronized只能等待锁的释放，不能响应中断；
    
    ④是否知道获取锁
        Lock可以通过trylock来知道有没有获取锁，而synchronized不能；

### 21.String  StringBuild  StringBuffer

    String:对String对象的任何改变都不影响到原对象，相关的任何change操作都会生成新的对象
    
    执行效率:StringBuilder > StringBuffer > String
    
    当字符串相加操作或者改动较少的情况下，建议使用 String str="hello"这种形式；
    
    当字符串相加操作较多的情况下，建议使用StringBuilder，如果采用了多线程，则使用StringBuffer。

### 22.什么是线程

    线程是操作系统能够进行运算调度的最小单位，它被包含在进程之中，是进程中的实际运作单位，可以使用多线程对进行运算提速。

### 23.Java内存区域详解

	关于运行时数据区域，可分成线程私有跟线程共享
	# 线程私有：
	    程序计数器
	    虚拟机栈
	    本地方法栈
	# 线程共享
	    堆
	    方法区（JDK1.8之前版本存在，后面在本地内存中新增了元空间（Metaspace））
	    直接内存（非运行时数据区的一部分）
	
	1.程序计数器：
	    可以看作是当前线程所执行的字节码的行号指示器，字节码解释器工作时通过改变这个计数器的值来选取下一条需要执行的字节码指令，
	    分支、循环、跳转、异常处理、线程恢复等功能都需要依赖这个计数器来完成。另外，为了线程切换后恢复到正确的执行位置，每条线
	    程都需要有一个独立的程序计数器，各线程之间计数器互不影响，独立存储，我们称这类内存区域为“线程私有”的内存。
	    程序计数器是唯一一个不会出现 OutOfMemoryError 的内存区域，它的生命周期随着线程的创建而创建，随着线程的结束而死亡
	
	2.Java虚拟机栈：
	    与程序计数器一样，Java虚拟机栈也是线程私有的，它的生命周期和线程相同，描述的是Java方法执行的内存模型，每次方法调用
	    的数据都是通过栈传递的。
	    
	   Java内存可以粗糙的区分为堆内存（Heap）和栈内存 (Stack)，其中栈就是现在说的虚拟机栈，或者说是虚拟机栈中局部变量表部分。
	    （实际上，Java虚拟机栈是由一个个栈帧组成，而每个栈帧中都拥有：局部变量表、操作数栈、动态链接、方法出口信息。）
	    
	    局部变量表主要存放了编译期可知的各种数据类型（boolean、byte、char、short、int、float、long、double）、对象引用
	    （reference 类型，它不同于对象本身，可能是一个指向对象起始地址的引用指针，也可能是指向一个代表对象的句柄或其他与此对象
	    相关的位置）。
	    
	   Java虚拟机栈会出现两种错误：StackOverFlowError 和 OutOfMemoryError。
	    
	    StackOverFlowError：
	        若Java虚拟机栈的内存大小不允许动态扩展，那么当线程请求栈的深度超过当前Java虚拟机栈的最大深度的时候，
	        就抛出 StackOverFlowError错误。
	    OutOfMemoryError：
	       Java虚拟机栈的内存大小可以动态扩展，如果虚拟机在动态扩展栈时无法申请到足够的内存空间，
	        则抛出OutOfMemoryError异常。
	    
	3.本地方法栈：
	        和虚拟机栈所发挥的作用非常相似，区别是： 虚拟机栈为虚拟机执行Java方法 （也就是字节码）服务，而本地方法栈则
	        为虚拟机使用到的 Native 方法服务
	4.堆
	    虚拟机所管理的内存中最大的一块，堆是所有线程共享的一块内存区域，在虚拟机启动时创建。此内存区域的唯一目的就是存放对象
	    实例，几乎所有的对象实例以及数组都在这里分配内存。
	    
	    由于现在收集器基本都采用分代垃圾收集算法，所以Java堆还可以细分为：新生代和老年代；
	    再细致一点有：Eden空间、From Survivor、To Survivor 空间等。进一步划分的目的是更好地回收内存，或者更快地分配内存。
        
        在JDK 7版本及JDK 7版本之前，堆内存被通常分为下面三部分：
        # 新生代内存(Young Generation)
        # 老生代(Old Generation)
        # 永生代(Permanent Generation)
	    
	    JDK 8版本之后方法区（HotSpot的永久代）被彻底移除了（JDK1.7就已经开始了），取而代之是元空间，元空间使用的是直接内存。
	    
	    大部分情况，对象都会首先在 Eden 区域分配，在一次新生代垃圾回收后，如果对象还存活，则会进入 s0 或者 s1，并且对象的年龄
	    还会加 1(Eden 区->Survivor 区后对象的初始年龄变为 1)，当它的年龄增加到一定程度（默认为 15 岁），就会被晋升到老年代中。
	    对象晋升到老年代的年龄阈值，可以通过参数 -XX:MaxTenuringThreshold 来设置

        堆这里最容易出现的就是 OutOfMemoryError 错误
        
    5.方法区
        线程共享的内存区域，它用于存储已被虚拟机加载的类信息、常量、静态变量、即时编译器编译后的代码等数据。虽然 Java 虚拟机规
        范把方法区描述为堆的一个逻辑部分，但是它却有一个别名叫做 Non-Heap（非堆），目的应该是与 Java 堆区分开来。
	    
	    方法区也被称为永久代
	    
	    1.方法区和永久代的关系
	         方法区和永久代的关系很像 Java 中接口和类的关系，类实现了接口，而永久代就是 HotSpot 虚拟机对虚拟机规范中方法区的
	         一种实现方式。 也就是说，永久代是 HotSpot 的概念，方法区是 Java 虚拟机规范中的定义，是一种规范，而永久代是一种实
	         现，一个是标准一个是实现，其他的虚拟机实现并没有永久代这一说法。
	    2.为什么要将永久代 (PermGen) 替换为元空间 (MetaSpace) 
	        整个永久代有一个 JVM 本身设置的固定大小上限，无法进行调整，而元空间使用的是直接内存，受本机可用内存的限制，
	        虽然元空间仍旧可能溢出，但是比原来出现的几率会更小。
	 
	6.运行时常量池
	    运行时常量池是方法区的一部分，当常量池无法再申请到内存时会抛出 OutOfMemoryError 错误。
	    
	    # class文件中常量池保存的是字符串常量，类和接口名字，字段名，和其他一些在class中引用的常量。每个class都有一份。
	    
	    # 运行时常量池保存的是从class文件常量池构建的静态常量引用和符号引用。每个class都有一份。
	    
	    # 字符串常量池保存的是“字符”的实例，供运行时常量池引用。
	    
	7.直接内存
	    直接内存并不是虚拟机运行时数据区的一部分，也不是虚拟机规范中定义的内存区域，但是这部分内存也被频繁地使用。而且也可能
	    导致 OutOfMemoryError 错误出现。
	    

	8.垃圾回收详解：
	     目前主流的垃圾收集器都会采用分代回收算法，因此需要将堆内存分为新生代和老年代，这样我们就可以根据各个年代的特点选择
	     合适的垃圾收集算法。
	     
	     大多数情况下，对象在新生代中 eden 区分配。当 eden 区没有足够空间进行分配时，虚拟机将发起一次 Minor GC。
	     
	     1.什么样的对象进入老年代
	        # 大对象直接进入老年代，大对象就是需要大量连续内存空间的对象（比如：字符串、数组）。
	        # 长期存活的对象。
	            动态对象年龄判定，可设置-XX:MaxTenuringThreshold默认是15
	     
	     2.GC分类   
	        # 部分收集 (Partial GC)： 
                ## 新生代收集（Minor GC / Young GC）：只对新生代进行垃圾收集； 
                ## 老年代收集（Major GC / Old GC）：只对老年代进行垃圾收集。需要注意的是Major GC在有的语境中也用于指代整堆收集； 
                ## 混合收集（Mixed GC）：对整个新生代和部分老年代进行垃圾收集。
	        # 整堆收集 (Full GC)：收集整个 Java 堆和方法区。
	     
	     3.判断对象死亡
	        # 引用计数法
	            ## 给对象中添加一个引用计数器，每当有一个地方引用它，计数器就加 1；当引用失效，计数器就减 1；任何时候计数器
	            为 0 的对象就是不可能再被使用的，但是它很难解决对象之间相互循环引用的问题。

            #可达性分析算法
                ## 通过一系列的称为 “GC Roots” 的对象作为起点，从这些节点开始向下搜索，节点所走过的路径称为引用链，当一个对象
                到GC Roots 没有任何引用链相连的话，则证明此对象是不可用的。
                
                ## 可作为GC Roots 的对象包括下面几种：
                    ### 虚拟机栈(栈帧中的本地变量表)中引用的对象
                    ### 本地方法栈(Native 方法)中引用的对象
                    ### 方法区中类静态属性引用的对象
                    ### 方法区中常量引用的对象
                    ### 所有被同步锁持有的对象

         4.垃圾收集算法
            # 标记-清除算法
                ## 原理：
                    该算法分为“标记”和“清除”阶段：首先标记出所有不需要回收的对象，在标记完成后统一回收掉所有没有被标记的
                    对象。它是最基础的收集算法，后续的算法都是对其不足进行改进得到
                
                ## 缺陷：
                    ### 效率问题；
                    ### 空间问题（标记清除后会产生大量不连续的碎片）；
                    
            # 标记-复制算法
                ## 原理：
                    为了解决效率问题，它可以将内存分为大小相同的两块，每次使用其中的一块。当这一块的内存使用完后，就将还存
                    活的对象复制到另一块去，然后再把使用的空间一次清理掉。这样就使每次的内存回收都是对内存区间的一半进行回收。
                    
            # 标记-整理算法
                ## 原理：
                    根据老年代的特点提出的一种标记算法，标记过程仍然与“标记-清除”算法一样，但后续步骤不是直接对可回收对象回
                    收，而是让所有存活的对象向一端移动，然后直接清理掉端边界以外的内存。
                    
            # 分代收集算法
                ## 原理：
                    当前虚拟机的垃圾收集都采用分代收集算法，这种算法没有什么新的思想，只是根据对象存活周期的不同将内存分为几
                    块。一般将 java 堆分为新生代和老年代，这样我们就可以根据各个年代的特点选择合适的垃圾收集算法。
            
            新生代一般使用的复制算法，优点是效率高，缺点是内存利用率低；
            老生代一般使用标记-清除/标记-整理算法。
                    
         5.垃圾收集器
            # Serial 收集器
                ## 解释：单线程收集器，当它进行垃圾回收时，必须暂停其他所有的工作线程（ "Stop The World" ），直到它收集结束。
                
                ## 新生代采用标记-复制算法，老年代采用标记-整理算法。
                
            # ParNew 收集器
                ## 解释：ParNew 收集器其实就是 Serial 收集器的多线程版本，除了使用多线程进行垃圾收集外，其余行为（控制参数、
                收集算法、回收策略等等）和 Serial 收集器完全一样。
                
                ## 新生代采用标记-复制算法，老年代采用标记-整理算法。
            
            # Parallel Scavenge 收集器
                ## 解释：也是使用标记-复制算法的多线程收集器.Parallel Scavenge 收集器关注点是吞吐量（高效率的利用 CPU）。
                CMS 等垃圾收集器的关注点更多的是用户线程的停顿时间（提高用户体验）。所谓吞吐量就是 CPU 中用于运行用户代码
                的时间与 CPU 总消耗时间的比值。
                
                ## 新生代采用标记-复制算法，老年代采用标记-整理算法。
                
                ## 这是 JDK1.8 默认收集器
                
            # Serial Old 收集器
                ## 解释：Serial 收集器的老年代版本，它同样是一个单线程收集器。它主要有两大用途：一种用途是在 JDK1.5 以及以前
                的版本中与 Parallel Scavenge 收集器搭配使用，另一种用途是作为 CMS 收集器的后备方案
                
            # Parallel Old 收集器
                ## 解释：Parallel Scavenge 收集器的老年代版本。使用多线程和“标记-整理”算法。在注重吞吐量以及 CPU 资源的场
                合，都可以优先考虑 Parallel Scavenge 收集器和 Parallel Old 收集器。
                
            # CMS 收集器
                ## 解释：CMS（Concurrent Mark Sweep）收集器是一种以获取最短回收停顿时间为目标的收集器。它非常符合在注重用户
                体验的应用上使用。
                
                ## CMS 收集器是一种 “标记-清除”算法实现的，它的运作过程相比于前面几种垃圾收集器来说更加复杂一些。
                整个过程分为四个步骤：
                    
                    ### 初始标记： 暂停所有的其他线程，并记录下直接与 root 相连的对象，速度很快；
                    
                    ### 并发标记： 同时开启 GC 和用户线程，用一个闭包结构去记录可达对象。但在这个阶段结束，这个闭包结构并不能
                    保证包含当前所有的可达对象。因为用户线程可能会不断的更新引用域，所以 GC 线程无法保证可达性分析的实时性。
                    所以这个算法里会跟踪记录这些发生引用更新的地方。
                    
                    ### 重新标记： 重新标记阶段就是为了修正并发标记期间因为用户程序继续运行而导致标记产生变动的那一部分对象的
                    标记记录，这个阶段的停顿时间一般会比初始标记阶段的时间稍长，远远比并发标记阶段时间短
                    
                    ### 并发清除： 开启用户线程，同时 GC 线程开始对未标记的区域做清扫。
                
                ## 优点：并发收集、低停顿
                
                ## 缺点：
                    ### 对 CPU 资源敏感；
                    ### 无法处理浮动垃圾；
                    ### 它使用的回收算法-“标记-清除”算法会导致收集结束时会有大量空间碎片产生。
                    
            # G1 收集器
                ## 解释：G1 (Garbage-First) 是一款面向服务器的垃圾收集器,主要针对配备多颗处理器及大容量内存的机器. 以极高概
                率满足 GC 停顿时间要求的同时,还具备高吞吐量性能特征.
                
            # ZGC 收集器
                ## 解释：与 CMS 中的 ParNew 和 G1 类似，ZGC 也采用标记-复制算法，不过 ZGC 对该算法做了重大改进。
                ## JDK11 中加⼊的低延迟垃圾收集器，目标是尽可能在不受影响吞吐量的前提下，实现在任意堆内存大小都可以
                    把停顿时间限制在10MS以内的低延迟。    
         6.JVM怎么判断对象可以回收      
            # 对象没有引用
            # 作用域发生未捕获异常
            # 程序在作用域正常执行完毕
            # 程序执行了System.exit（）
            # 程序发生意外终止（被杀进程等）

### 24.JDBC执行流程

    ①加载驱动
    //加载MySql驱动
    Class.forName("com.MySql.jdbc.Driver")
    //加载Oracle驱动
    Class.forName("oracle.jdbc.driver.OracleDriver")

	②获取数据库连接
	DriverManager.getConnection("jdbc:MySql://127.0.0.1:3306/imooc", "username", "password");
	
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


### 25.3次握手的过程

	TCP握手协议 ：在TCP/IP协议中,TCP协议提供可靠的连接服务,采用三次握手建立一个连接。

	SYN：同步序列编号(Synchronize Sequence Numbers)

	一个SYN包就是仅SYN标记设为1的TCP包;
	ACK包就是仅ACK 标记设为1的TCP包;
	SYN/ACK包是仅SYN 和 ACK 标记为1的包.
	
	假如服务器A和客户机B通讯. 当A要和B通信时，B首先向A发一个SYN (Synchronize Sequence Numbers)标记的包，告诉A请求建立连接. 
	只有当A受到B发来的SYN包，才可建立连接，除此之外别无他法。因此，如果你的防火墙丢弃所有的发往外网接口的SYN包，那么你将不能让
	外部任何主机主动建立连接。
	
	(客户端A) --> [SYN] --> (服务器B) 
	①第一次握手：建立连接时，客户端A发送SYN包(SYN=j)到服务器B，并进入SYN_SEND状态，等待服务器B确认。
	
	(客户端A) <-- [SYN/ACK] <--(服务器B)
	②第二次握手：服务器B收到SYN包，必须确认客户A的SYN(ACK=j+1)，同时自己也发送一个SYN包(SYN=k)，即SYN+ACK包，此时服务器B进入
	SYN_RECV状态。

	(客户端A) --> [ACK] --> (服务器B)
	③第三次握手：客户端A收到服务器B的SYN＋ACK包，向服务器B发送确认包ACK(ACK=k+1)，此包发送完毕，客户端A和服务器B进入
	ESTABLISHED状态，完成三次握手。

### 26.4次挥手过程

	四次挥手用来关闭已建立的TCP连接.

	(客户端A) --> ACK/FIN --> (服务器B)
	①第一次挥手:客户端A发送一个FIN，用来关闭客户A到服务器B的数据传送。

	(客户端A) <-- ACK <-- (服务器B)
	②第二次挥手:服务器B收到这个FIN，它发回一个ACK，确认序号为收到的序号加1。和SYN一样，一个FIN将占用一个序号。

	(客户端A) <-- ACK/FIN <-- (服务器B) 
	③第三次挥手:服务器B关闭与客户端A的连接，发送一个FIN给客户端A。

	(客户端A) --> ACK --> (服务器B)
	④第四次挥手:客户端A发回ACK报文确认，并将确认序号设置为收到序号加1。

### 27.事务四大特性(ACID)

	    # 原子性(Atomicity):
	        事务开始后所有操作要么全部成功，要么全部失败，不可能停滞在中间某个环节;
        
        # 一致性(Consistency):
            一个事务执行之前和执行之后都必须处于一致性状态;假设用户A和用户B两者的钱加起来一共是5000，那么不管A和B之间如何
            转账，转几次账，事务结束后两个用户的钱相加起来应该还是5000，这就是事务的一致性
        
        # 隔离性(Isolation):
            指当多个用户并发访问数据库时，数据库为每一个用户开启的事务不能被其他的事务操作所干扰，多个并发事务之间要相互隔离。
            同一时间，只允许一个事务请求同一组数据。不同的事务彼此之间没有干扰。
        
        # 持久性(Durability):
            一个事务一旦被提交，则对数据库的所有更新将被保存到数据库中，不能回滚。
        
        # ACID 是靠什么保证的？
            ## 原子性 是由 undolog 日志保证的，它记录了需要回滚的日志信息，事务回滚是撤销已经执行成功的SQL；
            ## 一致性 是由其他三个特性保证的，程序代码要保持业务上的一致性；
            ## 隔离性 是由MVCC保证的；
            ## 持久性 是由 redolog 日志保证的，MYSQL修改数据的时候会在redolog中记录一份日志，就算数据没有保存成功，只要
                日志保存成功了，数据仍不会丢失。

### 28.MySql跟Oracle的分页

	①MySql分页: 
    select * from stu limit m, n;//m = (startPage-1)*pageSize,n = pageSize
        第一个参数值m表示起始行，第二个参数表示取多少行（页面大小）
        m、n参数值不能在语句当中写计算表达式，写到语句之前必须计算好值。
    ②Oracle分页:
    select * from (select rownum rn,a.* from table_name a where rownum <= x) where rn >= y;
        //结束行，x = startPage*pageSize
        //起始行，y = (startPage-1)*pageSize+1
        rownum只能比较小于，不能比较大于，因为rownum是先查询后排序的

### 29.MySql跟Oracle的主键自增

	①MySql主键自增,mybatis中添加数据
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


### 30.脏读、不可重复读、幻读

    ①脏读:
        指当一个事务正在访问数据，并且对数据进行了修改，而这种数据还没有提交到数据库中，这时，另外一个事务也访问这个数据，然后使用了这个数据。
        因为这个数据还没有提交那么另外一个事务读取到的这个数据我们称之为脏数据;
        
    ②不可重复读:
        指在一个事务内，多次读同一数据。在这个事务还没有执行结束，另外一个事务也访问该同一数据，那么在第一个事务中的两次读取数据之间，
        由于第二个事务的修改第一个事务两次读到的数据可能是不一样的，这样就发生了在一个事物内两次连续读到的数据是不一样的，这种情况被称为是不可重复读。
    
    ③幻读:
        一个事务先后读取一个范围的记录，但两次读取的纪录数不同，我们称之为幻象读（两次执行同一条 select 语句会出现不同的结果，第二次读会增加一数据行，并没有说这两次执行是在同一个事务中）
        不可重复读侧重于修改，幻读侧重于新增或删除

### 31.隔离级别

	①读未提交(Read uncommitted):一个事务可以读取另一个未提交事务的数据
    
    ②读已提交(Read Committed):Oracle的默认隔离级别,这种隔离级别能够有效的避免脏读
    
    ③可重复读(Repeated Read):有效的避免不可重复读,MySql的默认隔离级别
    
    ④串行化(Serializable):它是在每个读的数据行上加上共享锁。在这个级别，可能导致大量的超时现象和锁竞争。

    √：可能出现
    ×：不会出现
    ---------------------------------------------------------
                脏读      |     不可重复读   |      幻读
    ---------------------------------------------------------
    读未提交     √        |      √           |       √
    ---------------------------------------------------------
    读已提交     ×        |      √           |       √
    ---------------------------------------------------------
    可重复读     ×        |      ×           |       √
    ---------------------------------------------------------
    串行化       ×        |      ×           |       ×
    ---------------------------------------------------------

### 32.Spring事务传播行为（propagation behavior）指的就是当一个事务方法被另一个事务方法调用时，这个事务方法应该如何进行。

    ①PROPAGATION_REQUIRED:如果存在一个事务，则支持当前事务。如果没有事务则开启一个新的事务。

    ②PROPAGATION_SUPPORTS:如果存在一个事务，支持当前事务。如果没有事务，则非事务的执行。

    ③PROPAGATION_MANDATORY:如果已经存在一个事务，支持当前事务。如果没有一个活动的事务，则抛出异常。

    ④PROPAGATION_REQUIRES_NEW:使用PROPAGATION_REQUIRES_NEW,需要使用 JtaTransactionManager作为事务管理器。 它会开启一个新的事务。如果一个事务已经存在，则先将这个存在的事务挂起。

    ⑤PROPAGATION_NOT_SUPPORTED:PROPAGATION_NOT_SUPPORTED 总是非事务地执行，并挂起任何存在的事务。使用PROPAGATION_NOT_SUPPORTED,也需要使用JtaTransactionManager作为事务管理器。

    ⑥PROPAGATION_NEVER:总是非事务地执行，如果存在一个活动事务，则抛出异常。

    ⑦PROPAGATION_NESTED:如果一个活动的事务存在，则运行在一个嵌套的事务中。 如果没有活动事务, 则按TransactionDefinition.PROPAGATION_REQUIRED 属性执行。

### 33.http1.0与http1.1的区别

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

### 34.http 请求过程
    # 浏览器根据域名解析IP地址
        DNS查找过程：
        ## 浏览器缓存：首先搜索浏览器自身的DNS缓存（缓存时间较短，大概1分钟，只能容纳1000条缓存），看自身的缓存中是否是
            有域名对应的条目，而且没有过期，如果有且没有过期则解析到此结束。
        ## 系统缓存：如果浏览器自身的缓存里面没有找到对应的条目，那么浏览器会搜索操作系统自身的DNS缓存，如果找到且没有过
            期则停止搜索解析到此结束。
        ## 路由器缓存：如果系统缓存也没有找到，则会向路由器发送查询请求。
        ## ISP（互联网服务提供商） DNS缓存：如果在路由缓存也没找到，最后要查的就是ISP缓存DNS的服务器。
        
    # 浏览器与WEB服务器建立一个TCP连接
        ## TCP的3次握手。
        
    # 浏览器给WEB服务器发送一个HTTP请求
    
    # 服务器端响应HTTP请求，浏览器得到HTML代码
    
    # 浏览器解析HTML代码，并请求HTML代码中的资源
        ## 浏览器拿到HTML文件后，开始解析HTML代码，遇到静态资源时，就向服务器端去请求下载。
        
    # 关闭TCP连接，浏览器对页面进行渲染呈现给用户
        ##  浏览器利用自己内部的工作机制，把请求到的静态资源和HTML代码进行渲染，呈现给用户。

### 35.https 请求过程
    443 端口 
    # 客户端向服务端发起请求
        ## 客户端生成随机数R1 发送给服务端；
        ## 告诉服务端自己支持哪些加密算法；
    
    # 服务器向客户端发送数字证书
        ## 服务端生成随机数R2;
        ## 从客户端支持的加密算法中选择一种双方都支持的加密算法（此算法用于后面的会话密钥生成）;
        ## 服务端生成把证书、随机数R2、会话密钥生成算法，一同发给客户端;
    
    # 客户端验证数字证书
        ## 验证证书的可靠性，先用CA的公钥解密被加密过后的证书,能解密则说明证书没有问题，然后通过证书里提供的摘要算法进行对
            数据进行摘要，然后通过自己生成的摘要与服务端发送的摘要比对。
        ## 验证证书合法性，包括证书是否吊销、是否到期、域名是否匹配，通过后则进行后面的流程。
        ## 获得证书的公钥、会话密钥生成算法、随机数R2。
        ## 生成一个随机数R3。
        ## 根据会话秘钥算法使用R1、R2、R3生成会话秘钥。
        ## 用服务端证书的公钥加密随机数R3并发送给服务端。
    
    # 服务器得到会话密钥
        ## 服务器用私钥解密客户端发过来的随机数R3
        ## 根据会话秘钥算法使用R1、R2、R3生成会话秘钥
    
    # 客户端与服务端进行加密会话
        ## 客户端发送加密数据给服务端
            发送加密数据：客户端加密数据后发送给服务端。
        ## 服务端响应客户端
            解密接收数据：服务端用会话密钥解密客户端发送的数据；
            加密响应数据：用会话密钥把响应的数据加密发送给客户端。
        ## 客户端解密服务端响应的数据
            解密数据：客户端用会话密钥解密响应数据；


### 36.get/post请求的body体有数据大小限制么

	# 理论上GET请求数据长度没有限制的,http协议对url长度是没有限制的,真正起到限制的是浏览器对其长度进行了限制。
	
	# POST请求长度限制是由web应用服务器决定的,tomcat默认限制post大的大小是2M
        设置post请求的大小（以tomcat为例），在conf/server.xml 文件中，在 <Connector/> 标签中设置  maxPostSize="0"，
        maxPostSize="0"，就代表post请求的参数无限制。

### 37.常见MySql的慢查询优化方式

	第一步.开启MySql慢查询
    方式一:修改配置文件  在 my.ini 增加几行:  主要是慢查询的定义时间（超过2秒就是慢查询），以及慢查询log日志记录（ slow_query_log）
    //开启慢查询,定义超过多少秒的查询算是慢查询
    slow_query_log = on
    long_query_time=2
    //记录下没有使用索引的查询
    log-querise-not-using-indexes=ON;

	方法二：通过MySql数据库开启慢查询
	set global slow_query_log = ON;
	set global long_query_time = 3600;
	set global log_querise_not_using_indexes = ON;
	
	利用explain关键字可以模拟优化器执行SQL查询语句

### 38.双亲委派模型

	如果一个类加载器收到类加载的请求，它首先不会自己去尝试加载这个类，而是把这个请求委派给父类加载器完成。
    每个类加载器都是如此，只有当父加载器在自己的搜索范围内找不到指定的类时（即ClassNotFoundException），
    子加载器才会尝试自己去加载。
    # 避免重复加载 + 避免核心类篡改 （安全性，避免加载一些自定义的类）

### 39.TCP释放连接时为什么time_wait状态必须等待2MSL时间?

	①为了保证客户端发送的最后一个ACK报文段能够到达服务器。因为这个ACK有可能丢失，从而导致处在LAST-ACK状态
    的服务器收不到对FIN-ACK的确认报文。服务器会超时重传这个FIN-ACK，
    接着客户端再重传一次确认，重新启动时间等待计时器。最后客户端和服务器都能正常的关闭。假设客户端不等
    待2MSL，而是在发送完ACK之后直接释放关闭，一但这个ACK丢失的话，服务器就无法正常的进入关闭连接状态。
    
    ②他还可以防止已失效的报文段。客户端在发送最后一个ACK之后，再经过经过2MSL，就可以使本链接持续时间内所产
    生的所有报文段都从网络中消失。从保证在关闭连接后不会有还在网络中滞留的报文段去骚扰服务器。
    注意：在服务器发送了FIN-ACK之后，会立即启动超时重传计时器。客户端在发送最后一个ACK之后会立即启动时间等待计时器。

### 40.MyBatis有哪些拦截器？如何实现拦截功能？

	①Executor：拦截内部执行器，它负责调用StatementHandler操作数据库，并把结果
        集通过ResultSetHandler进行自动映射，另外它还处理了二级缓存的操作。

    ②StatementHandler：拦截SQL语法构建的处理，它是MyBatis直接和数据库执行SQL脚本的对象，
    另外它也实现了MyBatis的一级缓存。

    ③ParameterHandler：拦截参数的处理。

    ④ResultSetHandler：拦截结果集的处理。

### 41.类的加载

    Java的类加载过程可以分为 5 个阶段：加载、验证、准备、解析和初始化。这 5 个阶段一般是顺序发生的，
        但在动态绑定的情况下，解析阶段发生在初始化阶段之后。
        (1)加载
            主要目的是将字节码从不同的数据源（可能是 class 文件、也可能是 jar 包，甚至网络）
            转化为二进制字节流加载到内存中，并生成一个代表该类的Java.lang.Class 对象。
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
            在编译时，Java类并不知道所引用的类的实际地址，因此只能使用符号引用来代替。
            直接引用通过对符号引用进行解析，找到引用的实际内存地址。
            比如 com.Wanger 类引用了 com.Chenmo 类，编译时 Wanger 类并不知道 Chenmo 类的实际内存地址，因此只能使用符号 com.Chenmo。
        (5)初始化
            初始化阶段是执行类构造器方法的过程。
            String cmower = new String("沉默王二");
            上面这段代码使用了 new 关键字来实例化一个字符串对象，
            那么这时候，就会调用 String 类的构造方法对 cmower 进行实例化。

        反编译：
       Javap -p -v SynchronizedDemo.class

### 42.类加载器

        (1)启动类加载器（Bootstrap Class-Loader），加载 jre/lib 包下面的 jar 文件，比如说常见的 rt.jar。
        (2)扩展类加载器（Extension or Ext Class-Loader），加载 jre/lib/ext 包下面的 jar 文件。
        (3)应用类加载器（Application or App Clas-Loader），根据程序的类路径（classpath）来加载Java类。
	    
        通过一段简单的代码了解下：
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
        每个Java类都维护着一个指向定义它的类加载器的引用，通过 类名.class.getClassLoader() 可以获取到此引用；
        然后通过 loader.getParent() 可以获取类加载器的上层类加载器。
        
        这段代码的输出结果如下：
        
        sun.misc.Launcher$AppClassLoader@18b4aac2
        sun.misc.Launcher$ExtClassLoader@5b6f7412
        第一行输出为 Panda 的类加载器，即应用类加载器，它是 sun.misc.Launcher$AppClassLoader 类的实例；
        第二行输出为扩展类加载器，是 sun.misc.Launcher$ExtClassLoader 类的实例。那启动类加载器呢？
        按理说，扩展类加载器的上层类加载器是启动类加载器，但在我这个版本的 JDK 中， 
        扩展类加载器的 getParent() 返回 null。所以没有输出。


### 43.双亲委派模型跟破坏双亲委派模型

        (1)双亲委派模型
            某个特定的类加载器在接到加载类的请求时，首先将加载任务委托给父类加载器，
            依次递归，如果父类加载器可以完成类加载任务，
            就成功返回；只有父类加载器无法完成此加载任务时，才自己去加载。
            
        为什么使用双亲委派模型
            安全性，避免加载一些自定义的类，例如自定义的一个java.lang.String类
        (2)破坏双亲委派模型
            原生的JDBC中Driver驱动本身只是一个接口，并没有具体的实现，具体的实现是由不同数据库类型去实现的。
            例如，MySql的MySql-connector-.jar中的Driver类具体实现的。 原生的JDBC中的类是放在rt.jar包的，
            是由启动类加载器进行类加载的，在JDBC中的Driver类中需要动态去加载不同数据库类型的Driver类，
            而MySql-connector-.jar中的Driver类是用户自己写的代码，那启动类加载器肯定是不能进行加载的，
            既然是自己编写的代码，那就需要由应用程序启动类去进行类加载。于是乎，
            这个时候就引入线程上下文件类加载器(Thread Context ClassLoader)。有了这个东西之后，
            程序就可以把原本需要由启动类加载器进行加载的类，由应用程序类加载器去进行加载了。


### 44.网络分层

        <1>物理层
        
        <2>链路层
        
        <3>网络层
        
        <4>传输层
        
        <5>应用层

### 45.sleep跟wait的区别

        # sleep()不释放同步锁,wait()释放同步锁. 
            sleep(milliseconds)可以用时间指定来使他自动醒过来,如果时间不到你只能调用interreput()来强行打断;
            wait()可以用notify()直接唤起.
            
        # sleep必须捕获异常，而wait，notify和notifyAll不需要捕获异常

### 46.反射

        # Java反射机制是在运行状态中，对于任意一个类，都能够知道这个类的所有属性和方法；
        # 对于任意一个对象，都能够调用它的任意方法和属性；
        # 这种动态获取信息以及动态调用对象方法的功能称为java语言的反射机制。
        
        # 获取class对象的方式：
        	通过Class.forName("全类名")；
        	类名.class；
        	对象.getClass()

### 47.Spring bean的生命周期

        概述：通过构造方法或工厂方法创建bean对象——>为bean属性赋值——>调用 bean 的初始化方法，
                即init-method指定方法——>bean实例化完毕，
                可以使用——>容器关闭, 调用 bean 的销毁方法，即destroy-method指定方法。

### 48.目前java中可作为GC Root的对象有

        1，虚拟机栈中引用的对象（本地变量表）

        2，方法区中静态属性引用的对象
        
        3，方法区中常量引用的对象
        
        4，本地方法栈中引用的对象（Native Object）	

### 49.多线程join的作用

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


### 50.JDK动态代理与CGLIB动态代理

        JDK1.8下JDK动态代理要优于CGLIB动态代理
        参考：ProxyPerformanceTest类的验证

        Spring同时使用了两种动态代理机制，依据如下：
        ①当Bean实现接口时，Spring就会用JDK的动态代理
        ②当Bean没有实现接口时，Spring使用CGlib是实现
        ③可以强制使用CGlib（@EnableAspectJAutoProxy(proxyTargetClass = true)）

### 51.Spring事务失效的几种常见情况

        1>Spring事务的注解只能放在public修饰的方法上才会生效，放在非public修饰的方法上事务无法生效，但不会报错。
        
        2>如果采用 Spring + Spring MVC ，则 context:component-scan 重复扫描问题可能会引起事务失败。
            如果Spring和Spring MVC的配置文件中都扫描了service层，那么事务就会失效。
            原因：按照Spring加载配置文件的顺序来说，先加载Spring MVC的配置文件，然后再加载Spring的配置文件，一般
            事务是配置在Spring的配置文件中的，但要是在加载Spring MVC配置时把service也加载进去了，但是此时事务是
            还没加载的，就会导致后面的事务无法成功注入到service中。
        
        3>如使用MySql且引擎是MyISAM，则事务会不起作用，原因是MyISAM不支持事务，可以改成InnoDB引擎
        
        4> 在业务代码中如果抛出RuntimeException异常，事务回滚；但是抛出Exception，事务不回滚；
           解决方法@Transactional改为@Transactional(rollbackFor = Exception.class)
        
        5>如果在加有事务的方法内，使用了try...catch..语句块对异常进行了捕获，而catch语句块没有
           抛 (throw new RuntimeExecption) 异常，事务也不会回滚
        
        6>同一个类之中，方法互相调用，切面无效 ，而不仅仅是事务。这里事务之所以无效，
            是因为Spring的事务是通过AOP实现的。

### 52.Spring bean的循环依赖怎么解决

        通过三级缓存来解决：
        这个方法是Spring解决循环依赖的关键方法，在这个方法中，使用了三层列表来查询的方式，这三层列表分别是：
        
        # singletonObjects （第一级缓存，里面放置的是实例化好的单例对象；）

        # earlySingletonObjects （第二级缓存，里面存放的是提前曝光的单例对象；）
        
        # singletonFactories （第三级缓存，里面存放的是要被实例化的对象的对象工厂。）

        注意：构造器的循环依赖无法解决，属性注入的循环依赖可以通过三级缓存解决。
        
        分析一下“A的某个field或者setter依赖了B的实例对象，同时B的某个field或者setter依赖了A的实例对象”这种循环依赖的情况：
        # A首先完成了初始化的第一步，并且将自己提前曝光到singletonFactories中；
        # 此时进行初始化的第二步，发现自己依赖对象B，此时就尝试去get(B)，发现B还没有被create，所以走create流程；
        # B在初始化第一步的时候发现自己依赖了对象A，于是尝试get(A)；
        # 尝试一级缓存singletonObjects(肯定没有，因为A还没初始化完全)，尝试二级缓存earlySingletonObjects（也没有）；
        # 试三级缓存singletonFactories，由于A通过ObjectFactory将自己提前曝光了，所以B能够通过ObjectFactory.getObject
            拿到A对象(虽然A还没有初始化完全，但是总比没有好呀)；
        # B拿到A对象后顺利完成了初始化阶段1、2、3，完全初始化之后将自己放入到一级缓存singletonObjects中；
        # 此时返回A中，A此时能拿到B的对象顺利完成自己的初始化阶段2、3，最终A也完成了初始化


### 53.SpringMVC的执行流程

    1、用户发送请求至前端控制器DispatcherServlet。

    2、DispatcherServlet收到请求调用HandlerMapping处理器映射器。
    
    3、处理器映射器找到具体的处理器(可以根据xml配置、注解进行查找)，
        生成处理器对象及处理器拦截器(如果有则生成)一并返回给DispatcherServlet。
    
    4、DispatcherServlet调用HandlerAdapter处理器适配器。
    
    5、HandlerAdapter经过适配调用具体的处理器(Controller，也叫后端控制器)。
    
    6、Controller执行完成返回ModelAndView。
    
    7、HandlerAdapter将controller执行结果ModelAndView返回给DispatcherServlet。
    
    8、DispatcherServlet将ModelAndView传给ViewReslover视图解析器。
    
    9、ViewReslover解析后返回具体View.
    
    10、DispatcherServlet根据View进行渲染视图（即将模型数据填充至视图中）。
    
    11、DispatcherServlet响应用户。


### 54.聚簇索引跟非聚簇索引

    # 聚簇索引：
        数据跟索引放在一起，找到索引也就找到了数据；
        在 Mysql 中，InnoDB 引擎的表的 .ibd文件就包含了该表的索引和数据，
        对于 InnoDB 引擎表来说，该表的索引(B+树)的每个非叶子节点存储索引，叶子节点存储索引和索引对应的数据。
        
        ## 优点
            ### 聚集索引的查询速度非常的快，因为整个 B+树本身就是一颗多叉平衡树，叶子节点也都是有序的，定位到索引的节点，
                就相当于定位到了数据。
                
        ## 缺点
            ### 依赖于有序的数据 ：因为 B+树是多路平衡树，如果索引的数据不是有序的，那么就需要在插入时排序，如果数据是整型
                还好，否则类似于字符串或 UUID 这种又长又难比较的数据，插入或查找的速度肯定比较慢。
            ### 更新代价大 ： 如果对索引列的数据被修改时，那么对应的索引也将会被修改，而且况聚集索引的叶子节点还存放着数据，
                修改代价肯定是较大的，所以对于主键索引来说，主键一般都是不可被修改的。
             
    # 非聚簇索引：
        数据和索引分开放，索引结构的叶子节点指向了数据的对应行
        
        ## 优点
            ### 更新代价比聚集索引要小 。非聚集索引的更新代价就没有聚集索引那么大了，非聚集索引的叶子节点是不存放数据的
        
        ## 缺点
            ### 跟聚集索引一样，非聚集索引也依赖于有序的数据
            ### 可能会二次查询(回表) :这应该是非聚集索引最大的缺点了。 当查到索引对应的指针或主键后，可能还需要根据指针或
                主键再到数据文件或表中查询。
    
    # InnoDB使用的是聚簇索引：
        在聚簇索引之上创建的索引称之为辅助索引，辅助索引访问数据总是需要
        二次查找，非聚簇索引都是辅助索引，像复合索引、前缀索引、唯一索引，
        辅助索引叶子节点存储的不再是行的物理位置，而是主键值.
    
    # 聚簇索引默认是主键，如果表中没有定义主键，InnoDB 会选择一个唯一的非空
        索引代替。如果没有这样的索引，InnoDB 会隐式定义一个主键来作为聚簇索引。
    # InnoDB 只聚集在同一个页面中的记录。包含相邻健值的页面可能相距甚远。

### 55.MySql 的 MVCC 原理

    # 事务id：trx_id
    # 回滚指针：roll_pointer，指向旧版本被修改的记录
    # undo日志：每条记录进行改动时，都会把旧的版本写入到undo日志中
        
        对记录每次更新后，都会将旧值放到一条undo日志中，就算是该记录的一个旧版本，
        随着更新次数的增多，所有的版本都会被roll_pointer属性连接成一个链表，我们把这个链表称之为版本链，
        版本链的头节点就是当前记录最新的值。另外，每个版本中还包含生成该版本时对应的事务id
        
        分为两种
        ## insert undo log
            代表事务在 insert 新记录时产生的 undo log, 只在事务回滚时需要，并且在事务提交后可以被立即丢弃
            
        ## update undo log
            事务在进行 update 或 delete 时产生的 undo log ; 不仅在事务回滚时需要，在快照读时也需要；所以不能随便删除，
            只有在快速读或事务回滚不涉及该日志时，对应的日志才会被 purge 线程统一清除
        
    # Read View：做可见性判断
        就是事务进行快照读操作的时候生产的读视图(Read View)，在该事务执行的快照读的那一刻，会生成数据库系统当前的一个快照，
        记录并维护系统当前活跃事务的ID(当每个事务开启时，都会被分配一个ID, 这个ID是递增的，所以最新的事务，ID值越大)
    
    # purge
        ## 为了节省磁盘空间，InnoDB 有专门的 purge 线程来清理 deleted_bit 为 true 的记录。为了不影响 MVCC 的正常工作，
        purge 线程自己也维护了一个read view（这个 read view 相当于系统中最老活跃事务的 read view ）;如果某个记录的 
        deleted_bit 为 true ，并且 DB_TRX_ID 相对于 purge 线程的 read view 可见，那么这条记录一定是可以被安全清除的。

    
    # 版本链中的重要属性：    
        数值列表：用来维护Read View生成时刻系统正活跃的事务ID

    # 读已提交每次查询创建新的read view,可重复读使用的第一次创建的read view
    
    # RC （读已提交）, RR （可重复读）级别下的 InnoDB 快照读有什么不同？
        ## 在 RC 隔离级别下，是每个快照读都会生成并获取最新的 Read View；
        ## 在 RR 隔离级别下，则是同一个事务中的第一个快照读才会创建 Read View, 之后的快照读获取的都是同一个 Read View


### 56.数据库锁机制：

    悲观锁：(根据使用性质划分)
        共享锁(Share locks简记为S锁)：
            也称读锁，事务A对对象T加s锁，其他事务也只能对T加S，多个事务可以同时读，但不能有写操作，直到A释放S锁。

        排它锁(Exclusivelocks简记为X锁)：
            也称写锁，事务A对对象T加X锁以后，其他事务不能对T加任何锁，只有事务A可以读写对象T直到A释放X锁。
        
        更新锁(简记为U锁)：
            用来预定要对此对象施加X锁，它允许其他事务读，但不允许再施加U锁或X锁；当被读取的对象将要被更新时，则升级为X锁，
            主要是用来防止死锁的。因为使用共享锁时，修改数据的操作分为两步，首先获得一个共享锁，读取数据，然后将共享锁升
            级为排它锁，然后再执行修改操作。这样如果同时有两个或多个事务同时对一个对象申请了共享锁，在修改数据的时候，
            这些事务都要将共享锁升级为排它锁。这些事务都不会释放共享锁而是一直等待对方释放，这样就造成了死锁。如果一个
            数据在修改前直接申请更新锁，在数据修改的时候再升级为排它锁，就可以避免死锁。
    
    乐观锁：
        版本号（记为version）：
        就是给数据增加一个版本标识，在数据库上就是表中增加一个version字段，
        每次更新把这个字段加1，读取数据的时候把version读出来，更新的时候比较version，如果还是开始读取的version就可以更新了，
        如果现在的version比老的version大，说明有其他事务更新了该数据，并增加了版本号，这时候得到一个无法更新的通知，用户自行根
        据这个通知来决定怎么处理，比如重新开始一遍。这里的关键是判断version和更新两个动作需要作为一个原子单元执行，否则在你
        判断可以更新以后正式更新之前有别的事务修改了version.
        时间戳（timestamp）：原理同上。

### 57.CountDownLatch

    JDK提供的一个同步工具，它可以让一个或多个线程等待，一直等到其他线程中执行完成一组操作。
    CountDownLatch常用的方法:
        countDown:
            CountDownLatch在初始化时，需要指定用给定一个整数作为计数器。当调用countDown方法时，计数器会被减1；
        await:
            调用await方法时，如果计数器大于0时，线程会被阻塞，一直到计数器被countDown方法减到0时，
            线程才会继续执行。计数器是无法重置的，当计数器被减到0时，调用await方法都会直接返回。
    调用countDown方法时，线程也会阻塞嘛？
        不会的，调用countDown的线程可以继续执行，不需要等待计数器被减到0，只是调用await方法的线程需要等待。

    public boolean await(long timeout, TimeUnit unit)
    设置超时时间，超时就不需要等待
    
    CountDownLatch的实现原理
        内部类叫做Sync，它继承了AbstractQueuedSynchronizer类，其中维护了一个整数state，并且保证了修改state的可见性和原子性。
        创建CountDownLatch实例时，也会创建一个Sync的实例，同时把计数器的值传给Sync实例

### 58.volatile原理

    1.可见性    
    2.不保证原子性
    3.禁止指令重排

    JMM：内存模型

### 59.cas原理

    CAS加volatile关键字是实现并发包的基石
    cas ： CompareAndSwap 比较并替换
    
    底层原理：
        # CAS操作包括三个操作数：需要读写的内存位置(偏移量)、预期原值(A)、新值(B)。
        # 如果内存位置与预期原值的A相匹配，那么将内存位置的值更新为新值B。如果内存位置与预期原值的值不匹配，那么处
            理器不会做任何操作。
        # 放在一个do-while循环里

        Unsafe类，native修饰的方法

### 60.Oracle的优化器

    以PLSQL为例：

    执行计划的常用列字段解释：
    基数（Rows）：Oracle估计的当前操作的返回结果集行数
    字节（Bytes）：执行该步骤后返回的字节数
    耗费（COST）、CPU耗费：Oracle估计的该步骤的执行成本，用于说明SQL执行的代价，理论上越小越好（该值可能与实际有出入）
    时间（Time）：Oracle估计的当前操作所需的时间 
 
    根据description列的缩进来判断，缩进最多的最先执行，缩进相同时，最上面的最先执行。

    RBO（Rule-Based Optimization） 基于规则的优化器
    CBO（Cost-Based Optimization） 基于代价的优化器

    RBO：
    RBO有严格的使用规则，只要按照这套规则去写SQL语句，无论数据表中的内容怎样，也不会影响到你的执行计划；
    换句话说，RBO对数据“不敏感”，它要求SQL编写人员必须要了解各项细则；

    CBO：
    CBO通过计算各种可能的执行计划的“代价”，即COST，从中选用COST最低的执行方案作为实际运行方案；
    也就是对数据“敏感”。

    游标（cursor）：
    用来存储多条查询数据的一种数据结构（'结果集'），
    它有一个 '指针'，从上往下移动（'fetch'），从而能够 '遍历每条记录'
    优缺点
    (1) 提高 sql '执行效率'
    (2) 牺牲 '内存'

    存储过程：
    CREATE OR REPLACE PROCEDURE 存储过程名称(param1 student.id%TYPE)


### 61.ToStringBuilder替代重写toString方法

        为了防止暴露，我们可以重写accept方法，剔除不想暴露的属性。
        改进：利用ToStringBuilder弥补(通过ReflectionToStringBuilder子类,覆盖accept方法来加以筛选)
       
        public String toString() {
        return (new ReflectionToStringBuilder(this) {
            protected boolean accept(Field f) {
            return super.accept(f) && !f.getName().equals(“password”);
          }}).toString();
        }


### 62. B / B+ 树

        # 二叉查找树 的查找时间是 Ｏ(log N)，为什么要引入 B / B+ 树，那就是磁盘ＩＯ，平衡二叉树由于树深度过大而造成磁盘IO读写
            过于频繁，进而导致效率低下。
        
        # B树
            ## 所有键值分布在整颗树中（索引值和具体data都在每个节点里）；
            ## 任何一个关键字出现且只出现在一个结点中；
            ## 搜索有可能在非叶子结点结束（最好情况O(1)就能找到数据）；
            ## 在关键字全集内做一次查找,性能逼近二分查找；
            
        # B+树
            ## 所有关键字存储在叶子节点出现,内部节点(非叶子节点并不存储真正的 data)
            ## 为所有叶子结点增加了一个链指针
        
        # B-树和B+树的区别
            ## B+树内节点不存储数据，所有 data 存储在叶节点导致查询时间复杂度固定为 log n。而B-树查询时间复杂度不固定，
                与 key 在树中的位置有关，最好为O(1)。
            ##  B+树叶节点两两相连可大大增加区间访问性，可使用在范围查询等，而B-树每个节点 key 和 data 在一起，则无法区间查找
            ## B+树更适合外部存储。由于内节点无 data 域，每个节点能索引的范围更大更精确
        
        # 范围查找的区别！！！！！！！  


### 63. 二叉树的遍历

        # 先序遍历 
            ## 先输出根结点，再遍历左子树，最后遍历右子树，遍历左子树和右子树的时候，同样遵循先序遍历的规则，
                也就是说，我们可以递归实现先序遍历。
        
        # 中序遍历
            ## 先递归中序遍历左子树，再输出根结点的值，再递归中序遍历右子树，大家可以想象成一巴掌把树压扁，
                父结点被拍到了左子节点和右子节点的中间
        
        # 后序遍历
            ## 先递归后序遍历左子树，再递归后序遍历右子树，最后输出根结点的值

### 64. 红黑树

        # 每个节点非红即黑
        
        # 根节点总是黑色的
        
        # 每个叶子节点都是黑色的空节点（NIL节点）
        
        # 如果节点是红色的，则它的子节点必须是黑色的（反之不一定）
        
        # 从根节点到叶节点或空子节点的每条路径，必须包含相同数目的黑色节点（即相同的黑色高度）


### 65. 布隆过滤器

        # 什么是布隆过滤器
        
            ## 一种来检索元素是否在给定大集合中的数据结构
            
            ## 这种数据结构是高效且性能很好的，但缺点是具有一定的错误识别率和删除难度
            
            ## 理论情况下，添加到集合中的元素越多，误报的可能性就越大

### 66. 缓存读写策略

        # Cache Aside Pattern（旁路缓存模式）
            ## 平常使用较多，适合读请求比较多的场景；
            ## 服务端需要同时维系DB和cache，并且是以DB的结果为准。
               
            ## 步骤：
                ### 写
                    #### 先更新DB
                    #### 然后直接删除cache
                ### 读
                    #### 从cache中读取数据，读取到就直接返回
                    #### cache中读取不到的话，就从DB中读取数据返回
                    #### 再把数据放到cache中
            ## 缺陷
                ### 首次请求数据一定不在cache中的问题
                    #### 解决方案：可以将热点数据提前放入cache中
                ### 写操作比较频繁的话，导致cache中的数据会被频繁删除，影响缓存命中率
                    #### 解决方案：数据库跟缓存数据强一致场景，更新数据库的时候同样更新cache，不过需要加锁（比如分布式锁）来
                        保证更新cache时不存在线程安全问题。
                    #### 可以短暂地允许数据库和缓存数据不一致的场景 ：更新DB的时候同样更新cache，但是给缓存加一个比较短的过期
                        时间，这样的话就可以保证即使数据不一致的话影响也比较小
        
        # Read/Write Through Pattern（读写穿透）
            ## 平时在开发过程中非常少见；抛去性能方面的影响，大概率是因为我们经常使用的分布式缓存 Redis 并没有
                提供 cache 将数据写入DB的功能。
            ## 服务端把 cache 视为主要数据存储，从中读取数据并将数据写入其中，cache 服务负责将此数据读取和写入 DB，
                从而减轻了应用程序的职责。
                
            ## 步骤：
                ### 写（Write Through）
                    #### 先查cache，cache中不存在，直接更新DB。
                    #### cache中存在，则先更新cache，然后cache服务自己更新DB。
                ### 读（Read Through）
                    #### 从cache中读取数据，读取到就直接返回。
                    #### 读取不到的话，先从DB加载，写入到cache后返回响应。
            ## 缺陷
                ### 和 Cache Aside Pattern 一样， Read-Through Pattern 也有首次请求数据一定不在 cache 的问题，
                    对于热点数据可以提前放入缓存中。
            
            ## 对比Cache-Aside Pattern（旁路缓存模式）
                ### Read-Through Pattern实际只是在Cache-Aside Pattern之上进行了封装；
                ### 在 Cache-Aside Pattern 下，发生读请求的时候，如果 cache 中不存在对应的数据，
                    是由||客户端||自己负责把数据写入 cache；
                ### 而 Read Through Pattern 则是 ||cache|| 服务自己来写入缓存的，这对客户端是透明的。
        
        # Write Behind Pattern（异步缓存写入）
            ## Write Behind Pattern 和 Read/Write Through Pattern 很相似，两者都是由 cache 服务来负责 cache 和 DB 的读写；
            
            ## 对比Read/Write Through（读写穿透）
                ### Read/Write Through 是同步更新 cache 和 DB；
                ###  Write Behind Caching 则是只更新缓存，不直接更新 DB，而是改为异步批量的方式来更新 DB。
            ## 使用场景
                ### Write Behind Pattern 下 DB 的写性能非常高，非常适合一些数据经常变化又对数据一致性
                    要求没那么高的场景，比如浏览量、点赞量。
                ### 消息队列中消息的异步写入磁盘
                ### MySQL 的 InnoDB Buffer Pool 机制

### 67. Eureka和ZooKeeper的区别
        # 概述
            ## RDBMS ===》（MySql,Oracle,SqlServer等关系型数据库）遵循的原则：
                ### ACID原则（A：原子性。C：一致性。I：隔离性。D：持久性。）
            
            ## NoSql ===》（redis,Mogodb等非关系型数据库）遵循的原则是：CAP原则：
                ### CAP原则（C：强一致性。A:可用性。P：分区容错性）
        
        # 在分布式领域有一个很著名的CAP定理：C：数据一致性。A：服务可用性。P：分区容错性（服务对网络分区故障的容错性）
          在这个特性中任何分布式系统只能保证两个。
          
        # CAP理论
            ## 在分布式存储系统中，最多只能实现以上两点。而由于当前网络延迟故障会导致丢包等问题，所以我们分区容错性是必须
                实现的。也就是NoSqL数据库P肯定要有，我们只能在一致性和可用性中进行选择，没有Nosql数据库能同时保证三点。
                （==>AP 或者 CP）
                
            ## 一致性（Consistency） : 所有节点访问同一份最新的数据副本。
            ## 可用性（Availability）: 非故障的节点在合理的时间内返回合理的响应（不是错误或者超时的响应）。
            ## 分区容错性（Partition tolerance） : 分布式系统出现网络分区的时候，仍然能够对外提供服务。
        
        # 网络分区
            ## 分布式系统中，多个节点之前的网络本来是连通的，但是因为某些故障（比如部分节点网络出了问题）某些节点之间不连通了，
                整个网络就分成了几块区域，这就叫网络分区。
        
        # Eureka（保证AP），Zookeeper（保证CP）
            ## Zookeeper保证CP
                ### 当向注册中心查询服务列表时，我们可以容忍注册中心返回的是几分钟以前的注册信息，但是不能接收服务器直接down掉
                    不可用，服务注册功能对可用性的要求要高于一致性。但是zk会出现这样的一种情况，当master节点因为网络故障与其他
                    节点失去联系时，剩余节点会重新进行leader选举。问题在于，选举leader的时间太长，30~120S，且选举期间整个zk集
                    群都是不可用的，这就导致在选举期间注册服务瘫痪。在云部署的环境下，因网络问题使得zk集群失去master节点是较大
                    概率会发生的事，虽然服务能够恢复，但是漫长的选举时间导致注册长期不可用是不能容忍的。
            
            ## Eureka保证AP
                ### Eureka在设计时优先保证可用性，Eureka各个节点都是平等的，几个节点挂掉不会影响正常节点的工作，剩余的节点依然
                    可以提供注册和查询服务。而Eureka的客户端在向某个Eureka注册或查询时如果发现连接失败，则会自动切换至其他节点
                    ，只要有一台Eureka还在，就能保证注册服务可用（保证可用性），只不过查到的信息可能不是最新的（不保证强一致
                    性）。除此之外，Eureka还有一种自我保护机制，如果在15分钟内超过85%的节点都没有正常的心跳，那么Eureka就认为
                    客户端与注册中心出现了网络故障，此时会出现以下几种情况：
                        #### Eureka不再从注册列表中移除因长时间没收到心跳而应该过期的服务
                        #### Eureka仍然能够接受新服务的注册和查询请求，但是不会被同步到其他节点上（即保证当前节点依然可用）
                        #### 当网络稳定时，当前实例新的注册信息会被同步到其他节点中
        # Zookeeper的设计理念就是分布式协调服务，保证数据（配置数据，状态数据）在多个服务系统之间保证一致性，这也不难看出
            Zookeeper是属于CP特性（Zookeeper的核心算法是Zab，保证分布式系统下，数据如何在多个服务之间保证数据同步）。
            Eureka是吸取Zookeeper问题的经验，先保证可用性。
        
        --------------------------------------------------------------------
        |	        |Zookeeper	                |Eureka
        --------------------------------------------------------------------
        |设计原则	|CP	                        |AP
        --------------------------------------------------------------------
        |优点	    |数据最终一致	                |服务高可用
        --------------------------------------------------------------------
        |缺点	    |选举leader过程中集群不可用	|服务节点间的数据可能不一致
        --------------------------------------------------------------------
        |适用场景	|对数据一致性要求较高	        |对注册中心服务可用性要求较高
        --------------------------------------------------------------------


### 68. 设计模式原则

        # 开闭原则
            ## OOP 中最基础的原则，指⼀个软件实体（类、模块、⽅法等）应该对扩展开放，对修改关
               闭。强调⽤抽象构建框架，⽤实现扩展细节，提⾼代码的可复⽤性和可维护性。
               
        # 第一职责原则
            ## ⼀个类、接⼝或⽅法只负责⼀个职责，降低代码复杂度以及变更引起的⻛险。
            
        # 依赖倒置原则
            ## 程序应该依赖于抽象类或接⼝，⽽不是具体的实现类。
        
        # 接⼝隔离原则
            ## 将不同功能定义在不同接⼝中实现接⼝隔离，避免了类依赖它不需要的接⼝，减少了接
               ⼝之间依赖的冗余性和复杂性。
        
        # ⾥⽒替换原则
            ## 开闭原则的补充，规定了任何⽗类可以出现的地⽅⼦类都⼀定可以出现，可以约束继承
               泛滥，加强程序健壮性。
        
        # 迪⽶特原则
            ## 也叫最少知道原则，每个模块对其他模块都要尽可能少地了解和依赖，降低代码耦合度。
        
        # 合成/聚合原则
            ## 尽量使⽤组合(has-a)/聚合(contains-a)⽽不是继承(is-a)达到软件复⽤的⽬的，避免滥
               ⽤继承带来的⽅法污染和⽅法爆炸，⽅法污染指⽗类的⾏为通过继承传递给⼦类，但⼦类并不具备执⾏
               此⾏为的能⼒；⽅法爆炸指继承树不断扩⼤，底层类拥有的⽅法过于繁杂，导致很容易选择错误。


### 69. 什么是内存屏障

        # 内存屏障，也叫内存栅栏，是⼀种CPU指令，⽤于控制特定条件下的重排序和内存可⻅性问题

### 70. 什么是指令重排

        # 在实际运⾏时，代码指令可能并不是严格按照代码语句顺序执⾏的。⼤多数现代微处理器都会 采⽤将指
          令乱序执⾏（out-of-order execution，简称OoOE或OOE）的⽅法，在条件允许的 情况下，直接运⾏
          当前有能⼒⽴即执⾏的后续指令，避开获取下⼀条指令所需数据时造成的等 待。通过乱序执⾏的技术，
          处理器可以⼤⼤提⾼执⾏效率。⽽这就是指令重排。

### 71. 什么是虚拟内存？虚拟内存的优缺点？

        # 虚拟内存
            ## 电脑中所运行的程序均需经过内存执行，若执行的程序占用的内存很大很多，则会导致内存消耗殆尽，
                为解决该问题，WINDOWS运用了虚拟内存技术，即拿出一部分硬盘空间来充当内存使用，这部分空间即称为虚拟内存。
        
        # 优点
            ## 可以弥补物理内存大小的不足；一定程度的提高反映速度；
               减少对物理内存的读取从而保护内存延长内存使用寿命； 
        
        # 缺点
            ## 占用一定的物理硬盘空间；加大了对硬盘的读写；设置不得当会影响整机稳定性与速度。

### 72. 分布式 id 生成方案有哪些？什么是雪花算法？

        # 数据库主键自增
            ## 优点
                ### 实现起来比较简单、
                ### ID 有序递增、
                ### 存储消耗空间小
            ## 缺点
                ### 支持的并发量不大
                ### 每次获取 ID 都要访问一次数据库（增加了对数据库的压力，获取速度也慢）
            
        # UUID
            //输出示例：cb4a9ede-fa5e-4585-b9bb-d60bce986eaa
            UUID.randomUUID()
            ## 优点
                ### 生成速度比较快、简单易用
            ## 缺点
                ### 存储消耗空间大（32 个字符串，128 位）
                ### 不安全（基于 MAC 地址生成 UUID 的算法会造成 MAC 地址泄露)
                ### 无序（非自增）
                ### 没有具体业务含义、需要解决重复 ID 问题（当机器时间不对的情况下，可能导致会产生重复 ID）
                
        # Snowflake(雪花算法)
            是 Twitter 开源的分布式 ID 生成算法。Snowflake 由 64 bit 的二进制数字组成
            ## 优点
                ### 生成速度比较快、
                ### 生成的 ID 有序递增、
                ### 比较灵活（可以对 Snowflake 算法进行简单的改造比如加入业务 ID）
            ## 缺点
                ### 需要解决重复 ID 问题（依赖时间，当机器时间不对的情况下，可能导致会产生重复 ID）
                
        # Tinyid(滴滴)
        
        # Leaf(美团)
        
        # UidGenerator(百度)

### 73. 索引失效场景

        # 查询条件包含 or，可能导致索引失效
        # 如何字段类型是字符串，where 时一定用引号括起来，否则索引失效
        # like 通配符可能导致索引失效，前%开头
        # 联合索引，查询时的条件列不是联合索引中的第一个列，索引失效
        # 在索引列上使用 mysql 的内置函数，索引失效
        # 对索引列运算（如，+、-、*、/），索引失效
        # 索引字段上使用（！= 或者 < >，not in）时，可能会导致索引失效
        # 索引字段上使用 is null， is not null，可能导致索引失效
        # 左连接查询或者右连接查询查询关联的字段编码格式不一样，可能导致索引失效
        # mysql 估计使用全表扫描要比使用索引快,则不使用索引


### 74.  Redis 分布式锁实现

        # 命令 setnx + expire 分开写
            ## 示例：
                if（jedis.setnx(key,lock_value) == 1）{ //加锁
                    expire（key，100）; //设置过期时间
                    try {
                        do something  //业务请求
                    }catch(){
                  }
                  finally {
                       jedis.del(key); //释放锁
                    }
                }
                
            ## 问题：
                   如果执行完setnx加锁，正要执行expire设置过期时间时，进程 crash 掉或者要重启维护了，
                   那这个锁就“长生不老”了，别的线程永远获取不到锁啦，所以分布式锁不能这么实现。     
            
        
        # setnx + value 值是过期时间
            ## 示例：
                {
                	long expires = System.currentTimeMillis() + expireTime; //系统时间+设置的过期时间
                	String expiresStr = String.valueOf(expires);
                
                	// 如果当前锁不存在，返回加锁成功
                	if (jedis.setnx(key, expiresStr) == 1) {
                			return true;
                	}
                	// 如果锁已经存在，获取锁的过期时间
                	String currentValueStr = jedis.get(key);
                
                	// 如果获取到的过期时间，小于系统当前时间，表示已经过期
                	if (currentValueStr != null && Long.parseLong(currentValueStr) < System.currentTimeMillis()) {
                
                		 // 锁已过期，获取上一个锁的过期时间，并设置现在锁的过期时间（不了解redis的getSet命令的小伙伴，可以去官网看下哈）
                		String oldValueStr = jedis.getSet(key_resource_id, expiresStr);
                
                		if (oldValueStr != null && oldValueStr.equals(currentValueStr)) {
                			 // 考虑多线程并发的情况，只有一个线程的设置值和当前值相同，它才可以加锁
                			 return true;
                		}
                	}
                
                	//其他情况，均返回加锁失败
                	return false;
                }
                
                //其他情况，均返回加锁失败
                return false;
                }
                
            ## 问题：
                ### 过期时间是客户端自己生成的，分布式环境下，每个客户端的时间必须同步
                ### 没有保存持有者的唯一标识，可能被别的客户端释放/解锁
                ### 锁过期的时候，并发多个客户端同时请求过来，都执行了jedis.getSet()，最终只能有一个客户端加锁成功，
                    但是该客户端锁的过期时间，可能被别的客户端覆盖
        
        # set 的扩展命令（set ex px nx）
            ## 示例：
                if（jedis.set(key, lock_value, "NX", "EX", 100s) == 1）{ //加锁
                    try {
                        do something  //业务处理
                    }catch(){
                  }
                  finally {
                       jedis.del(key); //释放锁
                    }
                }
                
            ## 问题：
                ### 锁过期释放了，业务还没执行完
                ### 锁被别的线程误删
        
        # set ex px nx + 校验唯一随机值,再删除
            
        # Redisson


### 75.  MYSQL的复制原理
        
        # master 服务器将数据的改变记录到二进制文件 binlog 日志中，当master上的数据发生改变时，则将其改变写入到二进制日志中；

        # slave 服务器会在一定时间间隔内对master二进制日志进行探测其是否发生改变，如果发生改变，则开始一个I/O Thread 请求
            master二进制事件；

        # 同时主节点为每个I/O线程启动一个dump线程，用于向其发送二进制事件，并保存至从节点本地的中继日志中，从节点启动SQL线程
            从中继日志中读取二进制日志，在本地重放（就是在执行一遍），使得其数据跟主节点的保持一致，最后I/O线程跟SQL线程将进
            入睡眠，等待下次被唤醒。

        # 概括：
            ## 从库会生成两个线程：一个I/O线程，一个SQL线程；

            ## I/O线程去请求主库的binlog，并将得到的binlog写到本地的relay-log（中继日志）文件中；

            ## 主库会生成一个log dump 线程，用来给从库的I/O线程传binlog；

            ## SQL线程会会读取relay-log（中继日志）的数据，并解析成SQL语句执行；