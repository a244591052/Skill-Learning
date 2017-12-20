package cn.spark.study.core;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;

import scala.Tuple2;
import tachyon.thrift.WorkerService.Processor.returnSpace;

/**
 * 二次排序
 * @author Administrator
 *
 */
public class SecondarySort {
	public static void main(String[] args) {
		SparkConf conf = new SparkConf()
				.setAppName("SecondarySort")
				.setMaster("local");
		
		JavaSparkContext sContext = new JavaSparkContext(conf);
		
		JavaRDD<String> lines = sContext.textFile("C://Users//Administrator//Desktop//spark.txt");
		
		JavaPairRDD<SecondarySortKey, String> pairs = lines.mapToPair(new PairFunction<String, SecondarySortKey, String>() {

			@Override
			public Tuple2<SecondarySortKey, String> call(String line) throws Exception {
				// TODO Auto-generated method stub
				String[] lineStrings = line.split(" ");
				SecondarySortKey key = new SecondarySortKey(
						Integer.valueOf(lineStrings[0]),
						Integer.valueOf(lineStrings[1]));
				return new Tuple2<SecondarySortKey, String>(key, line);
			}
		});
		
		JavaPairRDD<SecondarySortKey, String> pairsSorted = pairs.sortByKey();
		
		JavaPairRDD<Integer, Integer> originSorted = pairsSorted.mapToPair(new PairFunction<Tuple2<SecondarySortKey, String>, Integer, Integer>() {

			@Override
			public Tuple2<Integer, Integer> call(Tuple2<SecondarySortKey, String> key) throws Exception {
				// TODO Auto-generated method stub
				String[] lineSplit = key._2.split(" ");
				return new Tuple2<Integer, Integer>(Integer.valueOf(lineSplit[0]), Integer.valueOf(lineSplit[1]));
			}
		});
		
		originSorted.foreach(new VoidFunction<Tuple2<Integer,Integer>>() {
			
			@Override
			public void call(Tuple2<Integer, Integer> v1) throws Exception {
				// TODO Auto-generated method stub
				System.out.println(v1._1 + " " + v1._2);
			}
		});
		sContext.close();
	}
}
