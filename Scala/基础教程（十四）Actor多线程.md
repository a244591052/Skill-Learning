@(Scala)

# Actor（多线程）

## 引言
Scala的`Actor类似于Java中的多线程编程`。但是不同的是，**Scala的Actor提供的模型与多线程有所不同。Actor尽可能地避免锁和共享状态，从而避免多线程并发时出现资源争用、死锁等一系列传统多线程问题，进而提升多线程编程的性能。**

Spark中使用的分布式多线程框架是`Akka`。Akka也实现了类似Scala Actor的模型，其核心概念同样也是Actor。

&ensp;
## Actor的创建、启动和消息收发
Scala提供了Actor trait来让我们更方便地进行actor多线程编程，**Actor trait就类似于Java中的Thread和Runnable一样，是基础的多线程基类和接口。我们只要重写Actor trait的act方法，即可实现自己的线程执行体，与Java中重写run方法类似**。
&ensp;
- **使用start()方法启动actor;**
- **使用! 符号向actor发送消息;** 
- **actor内部使用receive和模式匹配接收消息;**
&ensp;
```scala
import scala.actors.Actor
// 定义类，继承Actor，重写act方法
class HelloActor extends Actor {
  def act() {
    while(true) {
      receive {
        case name: String => println("hello, " + name)
      }
    }
  }
}

import scala.actors.Actor
defined class HelloActor

// 测试：创建actor实例
scala> val helloActor = new HelloActor
helloActor: HelloActor = HelloActor@6ca372ef
// 启动actor
scala> helloActor.start()
res4: scala.actors.Actor = HelloActor@6ca372ef
// 向actor发送消息
scala> helloActor ! "sparks"

scala> helloActor ! "leo"
hello, leo
```

&ensp;
## 收发case calss类型的消息
要给一个actor发送消息，需要使用`actor ! 消息`语法，**在Scala中通常建议使用样例类，即case class来作为消息进行发送。然后在actor接收消息之后，可以使用强大的模式匹配来进行不同消息的处理**。
&ensp;
案例：用户注册登录后台接口

```scala
// 定义样例类，用于当做消息发送给Actor
case class Login(username: String, passwd: String)

case class Register(username: String, passwd: String)
// 定义注册登录监听Actor类
class UserManageActor extends Actor {
  def act() {
    while (true) {
      receive {
        case Login(username, passwd) => println("login, username is " + username + ", password is "
+ passwd)
        case Register(username, passwd) => println("register, username is " + username + ", password
 is " + passwd)
      }
    }
  }
}

defined class Login
defined class Register
defined class UserManageActor
// 测试，先发送一个注册类注册，然后发送一个登录类
scala> val userManageActor = new UserManageActor
userManageActor: UserManageActor = UserManageActor@2cfe272f

scala> userManageActor.start()
res7: scala.actors.Actor = UserManageActor@2cfe272f

scala> userManageActor ! Register("leo", "1234")

scala> register, username is leo, password is 1234

scala> userManageActor ! Login("leo", "1234")

login, username is leo, password is 1234

```

&ensp;
## Actor之间互相收发消息
Scala的Actor模型与Java的多线程之间有一个很大的区别就是：`Actor天然支持线程之间的精准通信，即一个actor可以给其他actor直接发送消息`。这个功能是非常强大和方便的。
&ensp;
**如果两个Actor之间要互相收发消息，那么Scala的建议是，一个Actor向另一个Actor发送消息时，同时带上自己的引用；这样当目标Actor收到源Actor的消息后，可以直接通过源Actor的引用给源Actor回复消息。**
&ensp;
案例：打电话
```scala
// 定义消息类
case class Message(content: String, sender: Actor)
// leo监听电话，表示自己现在很忙
class LeoTeleActor extends Actor {
  def act() {
    while (true) {
      receive {
        case Message(content, sender) => {
          println("leo telephone: " + content)
          sender ! "I'm leo, please call me after 10 minutes."
        }
      }
    }
  }
}

// jack给leo打电话，得到leo很忙的回复
class JackTeleActor(val leoTeleActor: Actor) extends Actor {
  def act() {
    leoTeleActor ! Message("hello, Leo, I'm Jack.Are you free now?", this) //这里加上自己引用，便于其他Actor给自己回复
    receive{
      case response: String => println("jack telephone: " + response)
    }
  }
}

// jack给leo打电话，得到leo很忙的回复
scala> val leoActor = new LeoTeleActor
leoActor: LeoTeleActor = LeoTeleActor@7308c820

scala> val jackActor = new JackTeleActor(leoActor)
jackActor: JackTeleActor = JackTeleActor@64bba0eb

scala> leoActor.start()
res10: scala.actors.Actor = LeoTeleActor@7308c820

scala> jackActor.start()
res11: scala.actors.Actor = JackTeleActor@64bba0eb

scala> leo telephone: hello, Leo, I'm Jack.Are you free now?
jack telephone: I'm leo, please call me after 10 minutes.
```
&ensp;
## 同步消息和Future
### 同步消息
默认情况下，消息都是异步的；但是如果希望发送的消息时同步的，即对方接收后，一定要给自己返回结果，没有返回就堵塞，那么可以使用 `!?` 的方式发送消息。即 `val reply = actor !? message`
&ensp;
### Future
如果要异步发送一个消息，但是在后续要获得消息的返回值，那么可以使用Future， 即`!!`语法。

```scala
val futrue = actor !! message
val reply = future()
```