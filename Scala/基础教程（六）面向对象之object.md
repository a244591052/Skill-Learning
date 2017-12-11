# 面向对象——object

## object
object相当于class的单个实例，类似于Java中的static，通常在里面放一些静态的field和method。
&ensp;
**第一次调用object中的方法时，会执行object的constructor，也就是object内部不在method或者代码块中的所有代码，但是object不能定义接受参数的constructor**
&ensp;
> 注意：object的构造函数只会在第一次被调用时被执行一次，这点类似Java类中static的初始化。

```scala
object Person {
  private var eyeNum = 2
  println("this Person object")

  def getEyeNum = eyeNum
}

defined module Person
// 第一次调用会初始化
scala> Person.getEyeNum
this Person object
res14: Int = 2
// 第二次都没有再执行初始化
scala> Person.getEyeNum
res15: Int = 2
```
&ensp;
## 伴生对象、伴生类
如果有一个class， 还有一个与class同名的object，那么就称这个object是class的伴生对象，class是object的伴生类。

伴生类和伴生对象必须存放在一个.scala文件中

**`伴生类和伴生对象最大的特点在于，可以互相访问Private field`**

```scala
object Person{
  private val eyeNum = 2
  def getEyeNum = eyeNum
}
class Person(val name: String, val age: Int){
  def sayHello = println("Hi," + name + ", I guess you must have " + Person.eyeNum + " eyes.") // 访问同名object私有字段
}

defined module Person
defined class Person

scala> val s = new Person("sparks", 30)
s: Person = Person@3b30eec5

scala> s.sayHello
Hi,sparks, I guess you must have 2 eyes.
```
&ensp;
## apply方法(重要)
object中非常重要的一个特殊方法。
通常在伴生对象中实现apply方法，并在其中实现构造伴生类对象的功能；这样在**创建伴生类对象时，可以使用Class（）方式（而不仅仅是new Class方式），原理是隐式地调用了伴生对象的apply方法，让对象创建更加简洁。**

```scala
class Person(val name: String)
object Person{
  def apply(name: String) = new Person(name)
}
defined class Person
defined module Person
// 可以直接使用Class（）方式创建对象。
scala> val test = Person("sparks")
test: Person = Person@7cdfa824
```
&ensp;
## main方法
在scala中，如果要运行一个应用程序，那么必须有一个main方法，作为入口

**scala中main方法定义为def main(args: Array[String]), 并且必须定义在object中**

```scala
object HelloWorld {
	def main(args: Array[String]){
		println("Hello, Wrold")
	}
}
```

除了自己实现main方法外，还可以继承App Trait，然后将需要在main方法中运行的代码，直接作为object的constructor代码，而且用args可以接受传入的参数

```scala
object HelloWorld extends App {
	if (args.length > 0) println("hello, " + args(0))
	else println("HelloWorld")
}
```

如果要运行上述代码，需要将其放入.scala文件，然后使用scalac编译，再用scala执行。

```
// 编译
scalac HelloWorld.scala
// 运行（输出运行所花费的时间）
scala -Dscala.time HelloWorld
```

> App Trait的工作原理为： App Trait继承自DelayedInit Trait，scalac命令进行编译时，会把继承App Trait的object的constructor代码都放到DelayedInit Trait的dealyedInit方法中执行。

&ensp;
## 用object来实现枚举功能
Scala没有直接提供类似于Java中的Enum枚举特性，如果要实现枚举，则需要用object继承Enumeration类，并且调用Value方法来初始化枚举值。

```
object Season extends Enumeration {
  val SPRINT, SUMMER, AUTUMN, WINTER = Value
}

defined module Season

scala> Season.SPRINT
res17: Season.Value = SPRINT

// 还可以通过Value传入枚举值的id和name，通过id和toString来获取
object Season extends Enumeration {
  val SPRINT = Value(0, "spring")
  val SUMMER = Value(1, "summer")
  val AUTUMN = Value(2, "autumn")
  val WINTER = Value(3, "winter")
}

defined module Season

scala> Season.SPRINT.id
res20: Int = 0

scala> Season.SPRINT.toString
res21: String = spring

scala> Season(0)
res22: Season.Value = spring

scala> Season(2)
res23: Season.Value = autumn

scala> Season.withName("winter")
res24: Season.Value = winter
// 使用枚举object.values 可以遍历枚举值
scala> for (ele <- Season.values ) println(ele)
spring
summer
autumn
winter
```