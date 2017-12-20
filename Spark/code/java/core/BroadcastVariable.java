package cn.spark.study.core;

import java.util.Arrays;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.broadcast.Broadcast;



/**
 * 广播变量
 * @author Administrator
 *
 */
public class BroadcastVariable {
	public static void main(String[] args) {
		SparkConf conf = new SparkConf()
				.setAppName("RDDcache")
				.setMaster("local");
		
		// 第二步：创建JavaSparkContext对象
		// 在Spark中，SparkContext是所用功能的一个入口，不同类型的spark应用程序context不同
		JavaSparkContext sContext = new JavaSparkContext(conf);
		
		// 调用sContext的broadcast方法创建广播变量，注意：这是只读的，必须要用final修饰
		final int factor = 3;
		final Broadcast<Integer> factorBroadcast = sContext.broadcast(factor);
		
		List<Integer> numberList = Arrays.asList(1,2,3,4);
		
		JavaRDD<Integer> numbers = sContext.parallelize(numberList);
		
		JavaRDD<Integer> doubleJavaRDD = numbers.map(new Function<Integer, Integer>() {

			@Override
			public Integer call(Integer v1) throws Exception {
				// 调用共享变量时只需要调用其value方法即可
				int factor = factorBroadcast.value();
				return v1 * factor;
			}
		});
		
		doubleJavaRDD.foreach(new VoidFunction<Integer>() {
			
			@Override
			public void call(Integer v1) throws Exception {
				// TODO Auto-generated method stub
				System.out.println(v1);
				
			}
		});
		sContext.close();
		
	}
}
