## Map & Tuple

### Map
1. 创建Map

```scala
// 创建一个不可变的Map
scala> val ages = Map("Leo" -> 30, "Sparks" -> 25)
ages: scala.collection.immutable.Map[String,Int] = Map(Leo -> 30, Sparks -> 25)
// 创建一个可变的Map
scala> val ages = scala.collection.mutable.Map("Leo" -> 20, "JEN" -> 23)
ages: scala.collection.mutable.Map[String,Int] = Map(JEN -> 23, Leo -> 20)
scala> ages("Leo") = 31
// 使用另外一种方式定义Map元素
scala> val ages = Map(("leo", 30), ("sparks", 20))
ages: scala.collection.immutable.Map[String,Int] = Map(leo -> 30, sparks -> 20)
// 创建一个空的HashMap，必须是实现类而不是抽象接口
scala> val ages = new scala.collection.mutable.HashMap[String, Int]
ages: scala.collection.mutable.HashMap[String,Int] = Map()
```

2. 访问Map元素

```scala
// 使用contains函数检查key是否存在
scala> val leoAge = if (ages.contains("Leo")) ages("Leo") else 0
leoAge: Int = 0
// getOrElse函数
scala> val leoAge = ages.getOrElse("Leo", 0)
leoAge: Int = 0
```

3. 修改Map元素
```scala
// 添加或者更新元素
scala> ages("Leo") = 31
// 添加多个元素
scala> ages += ("Mike" -> 34, "Tom" -> 40)
// 移除元素
scala> ages -= "Mike"
// 变相更新不可变map
scala> val ages2 = ages + ("Mike" -> 34, "Tom" -> 40)
```

4. 遍历Map
```scala
// 遍历map的entrySet
scala> for((key, value) <- ages) println(key + " " + value)
// 遍历key
scala> for(key <- ages.keySet) println(key)
// 遍历value
scala> for(value <- ages.values) println(value)
// 生成新map，反转key和value
scala> for((key, value) <- ages) yield (value, key)
```

5. SortedMap & LinkedHashMap

```scala
// SortedMap可以自动对Map的key排序，按照字母顺序
scala> val ages = scala.collection.immutable.SortedMap("leo" -> 30, "alice" -> 15)
ages: scala.collection.immutable.SortedMap[String,Int] = Map(alice -> 15, leo -> 30)

// LinkedHashMap可以记住插入entry的顺序
scala> val ages = new scala.collection.mutable.LinkedHashMap[String,Int]
ages: scala.collection.mutable.LinkedHashMap[String,Int] = Map()
scala> ages("leo") = 30
scala> ages("Sparks") = 20
scala> ages
res70: scala.collection.mutable.LinkedHashMap[String,Int] = Map(leo -> 30, Sparks -> 20)
```

### 元组Tuple
Scala元组将固定数量的项目组合在一起，以便它们可以作为一个整体传递。 与数组或列表不同，元组可以容纳不同类型的对象，但它们也是不可变的。（可以用作自定义数据类型）
```scala
// 创建Tuple
scala> val t = ("leo", 30, "hello")
t: (String, Int, String) = (leo,30,hello)
// 访问Tuple
scala> t._1
```

&ensp;
```scala
// zip操作
scala> val names = Array("leo", "jack", "mike")
names: Array[String] = Array(leo, jack, mike)

scala> val ages = Array(30, 24, 25)
ages: Array[Int] = Array(30, 24, 25)

// 将两个Array合并成一个个Tuple
scala> val nameAges = names.zip(ages)
nameAges: Array[(String, Int)] = Array((leo,30), (jack,24), (mike,25))

scala> for ((name, age) <- nameAges) println(name + ":" + age)
leo:30
jack:24
mike:25

// 如果Array的元素类型是个Tuple，调用toMap方法可以将Array转换为Map
scala> val maps = nameAges.toMap
maps: scala.collection.immutable.Map[String,Int] = Map(leo -> 30, jack -> 24, mike -> 25)

scala> maps("leo")
res24: Int = 30
```