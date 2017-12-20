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

public class SortWordCount {
	public static void main(String[] args) {
		SparkConf conf = new SparkConf()
				.setAppName("SortWordCount")
				.setMaster("local");
		JavaSparkContext sContext = new JavaSparkContext(conf);
		
		JavaRDD<String> lines = sContext.textFile("C://Users//Administrator//Desktop//spark.txt");
		
		JavaRDD<String> words = lines.flatMap(new FlatMapFunction<String, String>() {

			@Override
			public Iterable<String> call(String line) throws Exception {
				// 容易写错，需要返回Iterable类型
				return Arrays.asList(line.split(" "));
			}
		}); 
		
		JavaPairRDD<String, Integer> wordmap = words.mapToPair(new PairFunction<String, String, Integer>() {

			@Override
			public Tuple2<String, Integer> call(String words) throws Exception {
				// TODO Auto-generated method stub
				return new Tuple2<String, Integer>(words, 1);
			}
		});
		
		JavaPairRDD<String, Integer> wordCount = wordmap.reduceByKey(new Function2<Integer, Integer, Integer>() {
			
			@Override
			public Integer call(Integer v1, Integer v2) throws Exception {
				// TODO Auto-generated method stub
				return v1 + v2;
			}
		});
		
		JavaPairRDD<Integer, String> wordmapReverser = wordCount.mapToPair(new PairFunction<Tuple2<String, Integer>, Integer, String>() {

			@Override
			public Tuple2<Integer, String> call(Tuple2<String, Integer> words) throws Exception {
				// TODO Auto-generated method stub
				return new Tuple2<Integer, String>(words._2, words._1);
			}
		});
		
		// 当我们获取wordcount原始结果后，我们对于结果做反转，然后排序，然后再反转输出！
		JavaPairRDD<Integer, String> wordmapsort = wordmapReverser.sortByKey(false);
		
		JavaPairRDD<String, Integer> wordCountSorted = wordmapsort.mapToPair(new PairFunction<Tuple2<Integer, String>, String, Integer>() {

			@Override
			public Tuple2<String, Integer> call(Tuple2<Integer, String> words) throws Exception {
				// TODO Auto-generated method stub
				return new Tuple2<String, Integer>(words._2, words._1);
			}
		});
		
		wordCountSorted.foreach(new VoidFunction<Tuple2<String,Integer>>() {
			
			@Override
			public void call(Tuple2<String, Integer> word) throws Exception {
				System.out.println(word._1 + " : " + word._2);
				
			}
		});
		
		sContext.close();
	}

}
