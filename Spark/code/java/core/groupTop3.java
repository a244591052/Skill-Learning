package cn.spark.study.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Iterator;
import java.util.PriorityQueue;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.tools.ant.types.resources.selectors.Compare;

import parquet.org.codehaus.jackson.map.util.Comparators;

import java.util.Collections;


import scala.Tuple2;
import tachyon.thrift.WorkerService.Processor.returnSpace;

public class groupTop3 {
	public static void main(String[] args) {
		SparkConf conf = new SparkConf()
				.setAppName("SecondarySort")
				.setMaster("local");
		
		JavaSparkContext sContext = new JavaSparkContext(conf);
		
		JavaRDD<String> lines = sContext.textFile("C://Users//Administrator//Desktop//score.txt");
		
		JavaPairRDD<String, Integer> paris = lines.mapToPair(new PairFunction<String, String, Integer>() {

			@Override
			public Tuple2<String, Integer> call(String line) throws Exception {
				String[] split = line.split(" ");
				
				return new Tuple2<String, Integer>(split[0], Integer.valueOf(split[1]));
			}
		});
		
		JavaPairRDD<String, Iterable<Integer>> groupParis = paris.groupByKey();
		
		JavaPairRDD<String, Iterable<Integer>> groupTop3 = groupParis.mapToPair(new PairFunction<Tuple2<String, Iterable<Integer>>, String, Iterable<Integer>>() {

			@Override
			public Tuple2<String, Iterable<Integer>> call(Tuple2<String, Iterable<Integer>> value) throws Exception {
				// 使用最小堆，里面存放最大的三个数，每次拿最小的那个数跟别人比
				// 如果别人比最小的那个都小，就跳过，否则弹出最小的，添加该元素
				PriorityQueue<Integer> minHeap = new PriorityQueue<Integer>();
				Iterator<Integer> it = value._2.iterator();
				while(it.hasNext()){
					int temp = it.next();
					if(minHeap.size() < 3)
						minHeap.offer(temp);
					else {
						if(minHeap.peek() < temp){
							minHeap.poll();
							minHeap.offer(temp);
						}
					}
				}
				ArrayList<Integer> ret = new ArrayList();
				for(Integer num: minHeap)
					ret.add(num);
						
				// 自定义了降序，需要实现Comparator的compare方法
				Collections.sort(ret, new Comparator<Integer>(){
					@Override
					public int  compare (Integer v1, Integer v2){
						return v2.compareTo(v1);
					}
				});
				
				return new Tuple2<String, Iterable<Integer>>(value._1, ret);
			}
		});
		
		groupTop3.foreach(new VoidFunction<Tuple2<String,Iterable<Integer>>>() {
			
			@Override
			public void call(Tuple2<String, Iterable<Integer>> v) throws Exception {
				// TODO Auto-generated method stub
				System.out.println(v._1);
				Iterator<Integer> it = v._2.iterator();
				while(it.hasNext()){
					System.out.println(it.next());
				}
			}
		});
		
		sContext.close();
		
	}
}
