package cn.spark.study.core;

import java.util.Arrays;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function2;



/**
 * 并行化集合创建RDD
 * @author Administrator
 *
 */

public class ParallelizeCollection {
	public static void main(String[] args) {
		// 创建SparkConf
		SparkConf conf = new SparkConf()
				.setAppName("ParallelizeCollection")
				.setMaster("local");
		
		// 创建javaSparkContext
		JavaSparkContext sContext = new JavaSparkContext(conf);
		
		// 要通过并行化集合的方式创建RDD，那么就调用parallelize方法
//		注意这里不需要new
		List<Integer> numbers = Arrays.asList(1,2,3,4,5,6,7,8,9,10);
		JavaRDD<Integer> numberRdd = sContext.parallelize(numbers, 3); // 定义Partition的个数
		
		
		// 执行reduce算子操作
		int sum = numberRdd.reduce(new Function2<Integer, Integer, Integer>() {
			
			@Override
			public Integer call(Integer v1, Integer v2) throws Exception {
				// TODO Auto-generated method stub
				return v1+v2;
			}
		});
		System.out.println("一到十的累加和" + sum);
		sContext.close();
	}
}
