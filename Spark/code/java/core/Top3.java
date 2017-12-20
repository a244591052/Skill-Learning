package cn.spark.study.core;

import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;



import scala.Tuple2;

public class Top3 {
	public static void main(String[] args) {
		SparkConf conf = new SparkConf()
				.setAppName("SecondarySort")
				.setMaster("local");
		
		JavaSparkContext sContext = new JavaSparkContext(conf);
		
		JavaRDD<String> lines = sContext.textFile("C://Users//Administrator//Desktop//top.txt");
		
		JavaPairRDD<Integer, String> Integermap = lines.mapToPair(new PairFunction<String, Integer, String>() {

			@Override
			public Tuple2<Integer, String> call(String line) throws Exception {
				// TODO Auto-generated method stub
				return new Tuple2<Integer, String>(Integer.valueOf(line), line);
			}
		});
		
		JavaPairRDD<Integer, String> sortedMap = Integermap.sortByKey(false);
		
		JavaRDD<String> sortedString = sortedMap.map(new Function<Tuple2<Integer,String>, String>() {

			@Override
			public String call(Tuple2<Integer, String> arg0) throws Exception {
				// TODO Auto-generated method stub
				return arg0._2;
			}
		});
		
		List<String> top3 = sortedString.take(3);
		
		for(String i: top3){
			System.out.println(i);
		}
		sContext.close();

	}

}
