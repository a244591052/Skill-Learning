# 面向对象——Trait

## 基础知识
### 1 将trait作为接口使用
此时Trait就与Java中的接口非常类似，不过注意，在Scala中无论继承还是trait，统一都是extends关键字。

**Scala跟Java 8前一样不支持对类进行多继承，但是支持多重继承trait，使用with关键字即可**

```scala
trait HelloTrait{
  def sayHello(name: String)
}

trait MakeFriends{
  def makeFriends(p: Person)
}

class Person(val name: String) extends HelloTrait with MakeFriends {
  def sayHello(name: String) = println("Hello, " + name)
  def makeFriends(p: Person) = println("hello " + p.name + ", I'm " + name)
}

defined trait HelloTrait
defined trait MakeFriends
defined class Person

scala> val p = new Person("spark")
p: Person = Person@2f29e630

scala> val p2 = new Person("jack")
p2: Person = Person@52f118aa

scala> p.sayHello("jack")
Hello, jack

scala> p.makeFriends(p2)
hello jack, I'm spark
```
&ensp;
### 2 在trait中定义具体方法
Trait不仅可以定以抽象方法，还可以定以具体方法，此时Trait更像是包含了通过工具方法的东西。

有一个专有名词来形容这种情况，叫做**Trait功能混入了类**

举例：trait中可以包含一些很多类都通用的方法，比如说打印日志等，Spark中就是用了trait来定义了通用的日志打印方法。

```scala
trait Logger {
  def log(msg: String) = println("log: " + msg)
}

class Person(val name: String) extends Logger {
  def sayHello { println("Hello, I'm " + name); log("sayHello is invoked") }
}

defined trait Logger
defined class Person

scala> val p = new Person("leo")
p: Person = Person@3b0c38f2

scala> p.sayHello
Hello, I'm leo
log: sayHello is invoked
```
&ensp;
### 3 在trait中定义具体字段
Trait可以定以具体field， 但是这种继承trait field的方式与继承class是原理不同的：如果是继承class获取的field，实际是定以在父类中的；**而继承trait获取的field，就直接被添加到了继承类中**。
&ensp;
### 4 在trait中定义抽象字段
Trait中可以定以抽象field， **而trait中的具体方法可以使用抽象field，但是继承trait的类必须要覆盖抽象field，提供具体的值，否则程序会运行出错。**

```scala
// trait中的具体方法可以使用抽象field
trait SayHello{
  val msg: String
  def sayHello(name: String) = println(msg + "," + name)
}
// 继承trait中必须覆盖抽象field
class Person(val name: String) extends SayHello{
  val msg: String = "hello"
  def makeFriends(p: Person){
    sayHello(p.name)
    println("I'm" + name + ", want to make friends with you")
  }
}

defined trait SayHello
defined class Person
// 测试
scala> val p1 = new Person("leo")
p1: Person = Person@67372d20

scala> val p2 = new Person("Sparks")
p2: Person = Person@4f1f2f84

scala> p1.makeFriends(p2)
hello,Sparks
I'mleo, want to make friends with you
```
&ensp;
## Trait进阶

### 为实例混入trait
**有时我们可以在创建类的对象时，指定该对象混入某个trait，这样就只有这个对象混入该trait的方法，而类的其他对象则没有**

```scala
trait Logged {
  def log(msg: String) {}
}

trait MyLogger extends Logged {
  override def log(msg: String) {println("log: " + msg)}
}

class Person (val name: String) extends Logged {
  def sayHello { println("Hi, I'm "+ name); log("sayHello is invokend!")}
}

defined trait Logged
defined trait MyLogger
defined class Person

scala> val p1 = new Person("leo")
p1: Person = Person@36f80ceb

scala> p1.sayHello
Hi, I'm leo
// 混入trait，覆盖log方法！
scala> val p2 = new Person("jack") with MyLogger
p2: Person with MyLogger = $anon$1@30a6984c

scala> p2.sayHello
Hi, I'm jack
log: sayHello is invokend!
```
&ensp;
### trait调用链
Scala中支持让类继承多个trait后，依次调用多个trait中的同一个方法（Java中做不到），**只要让多个trait的同一个方法中，在最后都执行super.method即可**。

**类中调用多个trait中都有的这个方法时，首先会从最右边的trait方法开始执行，然后依次往左，最终形成一个调用链**

这种特性非常强大，其实就相当于设计模式中责任链模式的一种具体实现。

```scala
trait Handler{
  def handle(data: String) {}
}

trait DataValidHandler extends Handler {
  override def handle(data: String){
    println("check data:" + data)
    // 最后都执行super.method
    super.handle(data)
  }
}

trait SignatureValidHandler extends Handler {
  override def handle(data: String){
    println("check signature: " + data)
    // 最后都执行super.method
    super.handle(data)
  }
}

class Person(val name: String) extends SignatureValidHandler with DataValidHandler {
  def sayHello = {println("hello, " + name); handle(name)}
}

defined trait Handler
defined trait DataValidHandler
defined trait SignatureValidHandler
defined class Person

scala> val p = new Person("Sparks")
p: Person = Person@4b37d1a4
// 从右往左执行方法
scala> p.sayHello
hello, Sparks
check data:Sparks
check signature: Sparks
```
&ensp;
### 混合使用trait的具体方法和抽象方法
**可以让具体方法依赖于抽象方法，而抽象方法则放到继承trati的类中去实现**

这种trait其实就是设计模式中模板设计模式的体现

```scala
trait Valid{
  // 将getName交给继承类实现，这里直接在具体方法中使用抽象方法
  def getName: String
  def valid: Boolean = {
    getName == "Sparks"
  }
}

class Person(val name: String) extends Valid {
  println(valid)
  def getName = name
}

defined trait Valid
defined class Person
// 测试
scala> val p = new Person("Sparks")
true
p: Person = Person@351fadfa
```
&ensp;
### trait的构造机制
在Scala中，trait也是有构造代码的，也就是trait中除了method中的所有代码
而继承了trait的类的构造顺序如下：
1. 父类的构造函数
2. trait的构造代码，多个trait从左到右依次执行
3. 构造trait时先构造父trait，如果多个trait继承同一个父trait，则父trait只会构造一次
4. 所有trait构造完毕后，自身构造函数执行

```scala
class Person{ println("Person's constructor!")}
trait Logger { println("Logger's constuctor!")}
trait MyLogger extends Logger { println("MyLogger's constructor!")}
trait TimeLogger extends Logger { println("TimeLogger constructor")}
class Student extends Person with MyLogger with TimeLogger {
  println("Student's constructor")
}

defined class Person
defined trait Logger
defined trait MyLogger
defined trait TimeLogger
defined class Student
// 测试构造顺序
scala> val s = new Student
Person's constructor!
Logger's constuctor!
MyLogger's constructor!
TimeLogger constructor
Student's constructor
s: Student = Student@467421cc
```
&ensp;
### trait field初始化
在Scala中，trait是没有接收参数的构造函数的，这是trait与class的唯一区别，但是如果需求就是要trait能够对field进行初始化，那该怎么办呢？

这时候就需要使用Scala中非常特殊的一种高级特性——提前定义

出错示例：
```scala
trait SayHello {
  val msg: String
  println(msg.toString)
}

class Person extends SayHello{
  val msg: String = "init"
}

defined trait SayHello
defined class Person
// 因为要首先初始化trait，但是println中使用了抽象field，所以报错
scala> val p = new Person
java.lang.NullPointerException
```
&ensp;
使用提前定义特性初始化trait field
```scala
trait SayHello {
  val msg: String
  println(msg.toString)
}
class Person

defined trait SayHello
defined class Person
// 提前定义
scala> val p = new {
     |   val msg: String = "init"
     | }with Person with SayHello
init
p: Person with SayHello = $anon$1@445c693
// 提前定义另一种写法
scala> class Person extends {
     |   val msg: String = "init"
     | } with SayHello{}
defined class Person

scala> val p = new Person
init
p: Person = Person@121c1a08
```
&ensp;
使用lazy + override初始化trait field
```scala
scala> trait SayHello {
     |   lazy val msg: String = null
     |   println(msg.toString)
     | }
defined trait SayHello
// 覆盖lazy值
scala> class Person extends SayHello {
     |   override lazy val msg: String = "init"
     | }
defined class Person

scala> val p = new Person
init
p: Person = Person@753c7411
```
&ensp;
### trait继承class
在Scala中，trait也可以继承自class，此时这个class就会成为所有继承该trait的类的父类。

```scala
class MyUtil {
  def printMessage(msg: String) = println(msg)
}

trait Logger extends MyUtil{
  def log(msg: String) = printMessage("log: " + msg)
}

class Person(val name:String) extends Logger{
  def sayHello{
    log("Hi, I'm" + name)
    printMessage("hi,I'm " + name)
  }
}

defined class MyUtil
defined trait Logger
defined class Person

scala> val p = new Person("sparks")
p: Person = Person@5bc44d78
// 既可以调用Logger中的方法也可以调用MyUtil中的方法
scala> p.sayHello
log: Hi, I'msparks
hi,I'm sparks
```