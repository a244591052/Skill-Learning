package cn.spark.study.core;

import java.util.Arrays;
import java.util.List;

import org.apache.spark.Accumulator;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.broadcast.Broadcast;

public class AccumulatorVariable {
	public static void main(String[] args) {
		SparkConf conf = new SparkConf()
				.setAppName("RDDcache")
				.setMaster("local");
		
		// 第二步：创建JavaSparkContext对象
		// 在Spark中，SparkContext是所用功能的一个入口，不同类型的spark应用程序context不同
		JavaSparkContext sContext = new JavaSparkContext(conf);
		
		// 创建Accumulator变量，需要调用SparkContext的accumulator方法
		// 需要使用final修饰
		final Accumulator<Integer> sum = sContext.accumulator(0);		
		
		List<Integer> numberList = Arrays.asList(1,2,3,4);
		
		JavaRDD<Integer> numbers = sContext.parallelize(numberList);
		
		
		numbers.foreach(new VoidFunction<Integer>() {
			
			@Override
			public void call(Integer v1) throws Exception {
				// 然后在函数内部调用其add方法，累加值，task是拿不到值的！
				sum.add(v1);
				
			}
		});
		// 在driver程序中，可以调用Accumulator的value方法，获取其值。
		System.out.println(sum.value());
		
		sContext.close();
	}
}
