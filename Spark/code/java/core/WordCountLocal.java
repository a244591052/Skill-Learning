package cn.spark.study.core;

import java.util.Arrays;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;

import scala.Tuple2;

/**
 * 本地测试的wordcount程序
 * @author Administrator
 *
 */
public class WordCountLocal {
	
	public static void main(String[] args) {
		// 创建SparkConf对象，设置spark应用的配置信息
		// setMaster()可以配置该应用是通过什么模式启动，local代表本地运行
		SparkConf conf = new SparkConf()
				.setAppName("WordCountLocal")
				.setMaster("local");
		
		// 第二步：创建JavaSparkContext对象
		// 在Spark中，SparkContext是所用功能的一个入口，不同类型的spark应用程序context不同
		JavaSparkContext sContext = new JavaSparkContext(conf);
		
		// 第三步：创建初始RDD
		JavaRDD<String> lines = sContext.textFile("C://Users//Administrator//Desktop//spark.txt");
		
		// 第四步：对初始RDD进行transformation操作，也就是计算操作
		// 通常操作会通过创建function， 并配合RDD的map、flatMap等算子来执行
		// function 如果比较简单，则创建指定的Function匿名内部类，
		// 但是如果function比较复杂，则会单独创建一个类，作为实现function接口的类。
		
		// 先将每一行拆分单个的单词
		// FlatMapFunction,有两个泛型参数，分别代表了输入和输出类型
		JavaRDD<String> words = lines.flatMap(new FlatMapFunction<String, String>() {

			@Override
			public Iterable<String> call(String line) throws Exception {
				// TODO Auto-generated method stub
				return Arrays.asList(line.split(" "));
			}
			
		});
		
		// 接着需要将每一个单词映射为（单词，1）这种格式
		/*
		 * mapToPair，其实就是将每个元素，映射为一个(v1, v2)这样的Tuple2类型的元素
		 * 这里的tuple2就是scala类型，包含了两个值
		 * mapToPair这个算子，要求的是与PairFunction配合使用，第一个泛型参数代表输入类型
		 * 第二个和第三个参数代表输入Tupel2的第一个值和第二个值的类型
		 */
		JavaPairRDD<String, Integer> pairs = words.mapToPair(new PairFunction<String, String, Integer>() {

			@Override
			public Tuple2<String, Integer> call(String word) throws Exception {
				// TODO Auto-generated method stub
				return new Tuple2<String, Integer>(word, 1);
			}
		});
		
		/*
		 * 接着需要以单词作为key，统计每个单词出现的次数
		 * 使用reduceByKey这个算子
		 */
		JavaPairRDD<String, Integer> wordCounts = pairs.reduceByKey(new Function2<Integer, Integer, Integer>() {
			
			@Override
			public Integer call(Integer v1, Integer v2) throws Exception {
				// TODO Auto-generated method stub
				return v1 + v2;
			}
		});
		
		/*
		 * 到这里为止，我们通过几个Spark算子操作，已经统计出了单词的次数
		 * 但是，之前我们使用的flatMap mapToPair reduceByKey都是transformation操作
		 * 一个Spark应用中，光有transformation是不能执行的，必须要有一叫做action的操作
		 * 因为spark的运行时惰性的
		 * 最后我们使用foreach的action操作来触发程序执行
		 */
		wordCounts.foreach(new VoidFunction<Tuple2<String,Integer>>() {
			
			@Override
			public void call(Tuple2<String, Integer> count) throws Exception {
				// TODO Auto-generated method stub
				System.out.println(count._1 + " : " + count._2);
				
			}
		});
		sContext.close();
		
	}

}
