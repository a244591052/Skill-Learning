package cn.spark.study.core;

import java.io.Serializable;

import scala.math.Ordered;
import tachyon.thrift.WorkerService.Processor.returnSpace;

/**
 * 自定义二次排序key
 * @author Administrator
 *
 */
public class SecondarySortKey implements Ordered<SecondarySortKey>, Serializable{
	
	/* 1. 首先在自定义key里面，定义需要进行排序的列
	 * 2. 为进行排序的多个列提供getter和setter方法，以及hashcode和equal方法, 构造方法
	 * 3. 实现接口方法,大于等于，小于等于，compare, compareTo
	 * 4. compare, compareTo方法体相同，均为先判断第一个元素是否相等，如果相等在判断第二个元素
	 */
	private int first;
	private int second;
	
	
	public SecondarySortKey(int first, int second) {
		this.first = first;
		this.second = second;
	}

	@Override
	public boolean $greater(SecondarySortKey other) {
		if (this.first > other.getFirst()){
			return true;
		} else if(this.first == other.getFirst() &&
				this.second > other.getSecond()){
			return true;
		}
		return false;
	}

	@Override
	public boolean $greater$eq(SecondarySortKey other) {
		if (this.$greater(other)){
			return true;
		} else if(this.first == other.getFirst() &&
				this.second == other.getSecond()){
			return true;
		}
		return false;
	}

	@Override
	public boolean $less(SecondarySortKey other) {
		if (this.first < other.getFirst()){
			return true;
		} else if(this.first == other.getFirst() &&
				this.second < other.getSecond()){
			return true;
		}
		return false;
	}

	@Override
	public boolean $less$eq(SecondarySortKey other) {
		if (this.$less(other)){
			return true;
		} else if(this.first == other.getFirst() &&
				this.second == other.getSecond()){
			return true;
		}
		return false;
	}

	@Override
	public int compare(SecondarySortKey other) {
		if(this.first - other.getFirst() != 0){
			return this.first - other.getFirst();
		} else {
			return this.second - other.getSecond();
		}
	}

	@Override
	public int compareTo(SecondarySortKey other) {
		if(this.first - other.getFirst() != 0){
			return this.first - other.getFirst();
		} else {
			return this.second - other.getSecond();
		}
	}

	public int getFirst() {
		return first;
	}

	public void setFirst(int first) {
		this.first = first;
	}

	public int getSecond() {
		return second;
	}

	public void setSecond(int second) {
		this.second = second;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + first;
		result = prime * result + second;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SecondarySortKey other = (SecondarySortKey) obj;
		if (first != other.first)
			return false;
		if (second != other.second)
			return false;
		return true;
	}
	

}
