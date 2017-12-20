package cn.spark.study.core;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

public class RDDcache {
	public static void main(String[] args) {
		SparkConf conf = new SparkConf()
				.setAppName("RDDcache")
				.setMaster("local");

		// 第二步：创建JavaSparkContext对象
		// 在Spark中，SparkContext是所用功能的一个入口，不同类型的spark应用程序context不同
		JavaSparkContext sContext = new JavaSparkContext(conf);
		
		// cache()或者persist()的对于RDD的使用必须是链式调用，否则无效
		JavaRDD<String> lines = sContext.textFile("C://Users//Administrator//Desktop//spark.txt")
				.cache();
		
		long beginTime = System.currentTimeMillis();
		
		long count = lines.count();
		
		long endTime = System.currentTimeMillis();
		System.out.println("cost "+ (endTime-beginTime));
		
		beginTime = System.currentTimeMillis();
		
		count = lines.count();
		
		endTime = System.currentTimeMillis();
		System.out.println("cost "+ (endTime-beginTime));
		sContext.close();
	}
}
