package cn.spark.study.core;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;

/**
 * 使用本地文件创建RDD
 * 案例：统计文本总字符数
 * @author Administrator
 *
 */
public class LocalFile {
	public static void main(String[] args) {
		SparkConf conf = new SparkConf()
				.setAppName("LocalFile")
				.setMaster("local");
		
		JavaSparkContext sContext = new JavaSparkContext(conf);
		
		JavaRDD<String> lines = sContext.textFile("C://Users//Administrator//Desktop//spark.txt", 3);
		
		JavaRDD<Integer> length = lines.map(new Function<String, Integer>() {

			@Override
			public Integer call(String line) throws Exception {
				// TODO Auto-generated method stub
				return line.length();
			}
		});
		
		int sum = length.reduce(new Function2<Integer, Integer, Integer>() {
			
			@Override
			public Integer call(Integer v1, Integer v2) throws Exception {
				// TODO Auto-generated method stub
				return v1 + v2;
			}
		});
		
		System.out.println("总字符数： "+ sum);
		sContext.close();
		
	}
}
