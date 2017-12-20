package cn.spark.study.core;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.hive.ql.parse.HiveParser.databaseComment_return;
import org.apache.hadoop.hive.ql.parse.HiveParser_FromClauseParser.joinSource_return;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.VoidFunction;

import scala.Tuple2;


public class TransformationOperation {
	public static void main(String[] args) {
//		groupbykey();
//		join();
		cogroup();
	}
	public static void join() {
//		案例：合并学生id的姓名和分数
		SparkConf conf = new SparkConf()
				.setAppName("groupbykey")
				.setMaster("local");
		
		JavaSparkContext sContext = new JavaSparkContext(conf);
		
		@SuppressWarnings("unchecked")
		List<Tuple2<Integer, String>> name = Arrays.asList(
				new Tuple2<Integer, String>(1, "sparks"),
				new Tuple2<Integer, String>(2, "leo"),
				new Tuple2<Integer, String>(3, "anli"));
		
		@SuppressWarnings("unchecked")
		List<Tuple2<Integer, Integer>> score = Arrays.asList(
				new Tuple2<Integer, Integer>(1, 90),
				new Tuple2<Integer, Integer>(2, 80),
				new Tuple2<Integer, Integer>(2, 60),
				new Tuple2<Integer, Integer>(3, 70));
		
		// 注意：这里需要调用parallelizePairs而不是parallelize
		// 而在scala中无论输入是什么格式，都是parallelize方法
		JavaPairRDD<Integer, String> names = sContext.parallelizePairs(name);
		JavaPairRDD<Integer, Integer> scores = sContext.parallelizePairs(score);
		
		// join算子根据key关联两个RDD
		// 类似于groupbykey，只不过join是针对于不同的RDD之间，而且
		// groupbykey生成Iterable，但是join生成Tuple2，
		// Tuple2的两个泛型分别为原始RDD的value类型。
		// 上面的得到的结果： (1, ("sparks", 90)) (2, ("leo", 80))
		JavaPairRDD<Integer, Tuple2<String, Integer>> ret = names.join(scores);
		
		ret.foreach(new VoidFunction<Tuple2<Integer,Tuple2<String,Integer>>>() {

			@Override
			public void call(Tuple2<Integer, Tuple2<String, Integer>> id)
					throws Exception {
				System.out.println(id._1 + " " + id._2._1 + " " + id._2._2 );
				
			}
		});
		
	}
	public static void cogroup() {
//		案例：合并学生id的姓名和分数
		SparkConf conf = new SparkConf()
				.setAppName("groupbykey")
				.setMaster("local");
		
		JavaSparkContext sContext = new JavaSparkContext(conf);
		
		@SuppressWarnings("unchecked")
		List<Tuple2<Integer, String>> name = Arrays.asList(
				new Tuple2<Integer, String>(1, "sparks"),
				new Tuple2<Integer, String>(2, "leo"),
				new Tuple2<Integer, String>(3, "anli"));
		
		@SuppressWarnings("unchecked")
		List<Tuple2<Integer, Integer>> score = Arrays.asList(
				new Tuple2<Integer, Integer>(1, 90),
				new Tuple2<Integer, Integer>(1, 50),
				new Tuple2<Integer, Integer>(2, 80),
				new Tuple2<Integer, Integer>(2, 60),
				new Tuple2<Integer, Integer>(3, 70));
		
		// 注意：这里需要调用parallelizePairs而不是parallelize
		// 而在scala中无论输入是什么格式，都是parallelize方法
		JavaPairRDD<Integer, String> names = sContext.parallelizePairs(name);
		JavaPairRDD<Integer, Integer> scores = sContext.parallelizePairs(score);
		
		// cogroup与join不同
		// 一个key join上的所有value都放到一个Iterable里面去了
		// 而join一个key匹配到多个相同key，就输入多少条结果
		// Tuple2的两个泛型分别为原始RDD的value类型。
		// 上面的得到的结果： (1, ([sparks], [90, 50])) (2, ([leo], [80, 60]))
		JavaPairRDD<Integer, Tuple2<Iterable<String>, Iterable<Integer>>> ret = names.cogroup(scores);
		
		ret.foreach(new VoidFunction<Tuple2<Integer,Tuple2<Iterable<String>,Iterable<Integer>>>>() {

			@Override
			public void call(Tuple2<Integer, Tuple2<Iterable<String>, Iterable<Integer>>> id)
					throws Exception {
				System.out.println(id._1 + " " + id._2._1 + " " + id._2._2 );
				
			}
		});
		
	}
	public static void groupbykey() {
//		案例：对每个班级的成绩进行分组
		SparkConf conf = new SparkConf()
				.setAppName("groupbykey")
				.setMaster("local");
		
		JavaSparkContext sContext = new JavaSparkContext(conf);
		
		@SuppressWarnings("unchecked")
		List<Tuple2<String, Integer>> data = Arrays.asList(
				new Tuple2<String, Integer>("class1", 80),
				new Tuple2<String, Integer>("class2", 20),
				new Tuple2<String, Integer>("class1", 40),
				new Tuple2<String, Integer>("class2", 60));
		
		// 注意：这里需要调用parallelizePairs而不是parallelize
		// 而在scala中无论输入是什么格式，都是parallelize方法
		JavaPairRDD<String, Integer> scores = sContext.parallelizePairs(data);
		
		// groupByKey算子返回的还是JavaPairRDD，但是JavaPairRDD的第二个泛型参数变成Iterable
		JavaPairRDD<String, Iterable<Integer>> groupClass = scores.groupByKey();
		
		groupClass.foreach(new VoidFunction<Tuple2<String,Iterable<Integer>>>() {
			
			@Override
			public void call(Tuple2<String, Iterable<Integer>> t) throws Exception {
				// TODO Auto-generated method stub
				System.out.println("class: " + t._1);
				Iterator<Integer> iterator = t._2.iterator();
				while(iterator.hasNext()){
					System.out.println(iterator.next());
				}
				
			}
		});
		sContext.close();
		
	}
}
