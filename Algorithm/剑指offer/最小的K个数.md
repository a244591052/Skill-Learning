# 最小的K个数
> 输入n个整数，找出其中最小的K个数。例如输入4,5,1,6,2,7,3,8这8个数字，则最小的4个数字是1,2,3,4,。

## 解题思路
1. 先将数组排序，找前K个数，时间复杂度为O(nlogn)
&ensp;
2. 利用快速排序中的获取分割（中轴）点位置函数Partiton思想，基于数组的第k个数字来调整，使得比第k个数字小的所有数字都位于数组的左边，比第k个数字大的所有数字都位于数组的右边。调整之后，位于数组左边的k个数字就是最小的k个数字（这k个数字不一定是排序的） 时间复杂度O(n)

```java
import java.util.*;
public class Solution {
    public ArrayList<Integer> GetLeastNumbers_Solution(int [] input, int k) {
        ArrayList<Integer> ret = new ArrayList<Integer>();
        if (input == null || k > input.length || k <= 0) return ret;
        int end = input.length -1;
        int start = 0;
        int index = Partition(start, end, input);
        while(index != k - 1){
            if(index > k){
                end = index - 1;
                index = Partition(start, end, input);
            }else{
                start = index + 1;
                index = Partition(start, end, input);
            }
        }
        for(int i=0; i<k; i++){
            ret.add(input[i]);
        }
        Collections.sort(ret);
        return ret;
    }
    public int Partition(int start, int end, int [] input){
        if(start < 0 || end >= input.length || start>end) return -1;
        int compare = input[start];
        int index = start;
        for (int i=start+1; i<=end; i++){
            if (input[i] < compare){
                index ++;
                if(i != index){
                    swap(index, i, input);
                }
            }
        }
        swap(index, start, input);
        return index;
    }
     
    public void swap(int start, int end, int [] array){
        int temp = array[start];
        array[start] = array[end];
        array[end] = temp;
    }
}
```
&ensp;
3. 利用堆排序，时间复杂度为O(N logK)，**适合处理海量数据**
	1. 遍历输入数组，将前k个数插入到堆中；
	2. 继续从输入数组中读入元素做为待插入整数，并将它与堆中最大值比较：如果待插入的值比当前已有的最大值小，则用这个数替换当前已有的最大值；如果待插入的值比当前已有的最大值还大，则抛弃这个数，继续读下一个数。
	3. 动态维护堆中这k个数，以保证它只储存输入数组中的前k个最小的数，最后输出堆即可。

```java
import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.Comparator;
public class Solution {
    public ArrayList<Integer> GetLeastNumbers_Solution(int [] input, int k) {
        ArrayList<Integer> ret = new ArrayList<Integer>();
        if (input==null || k<=0 || k > input.length) return ret;
        
        // Java中的优先队列是基于堆实现的，不过默认是最小堆，所以需要重写compare方法，使之变为最大堆
        PriorityQueue<Integer> maxHeap = new PriorityQueue<Integer>(k, new Comparator<Integer>(){
            
            @Override
            public int compare(Integer o1, Integer o2){
                return o2.compareTo(o1);
            }
        });
        
        for (int i=0; i<input.length; i++){
            if(maxHeap.size() != k)
                maxHeap.offer(input[i]);
            else{
                if(maxHeap.peek()>input[i]){
                    maxHeap.poll();
                    maxHeap.offer(input[i]);
                }
            }
        }
        for(Integer num: maxHeap){
            ret.add(num);
        }
        return ret;
    }
}
```
> 注意：该算法适合海量数据的输入，由于内存的大小是有限的，有可能不能把这些海量数据一次性全部载入内存。这个时候，我们可以从辅助存储空间（如硬盘）中每次读入一个数字，这种思路只要求内存能够容纳K即可。**因此最适合的情形就是n很大并且k较小的问题**。

## 知识点补充：优先队列
优先队列的作用是能保证每次取出的元素都是队列中权值最小的（Java的优先队列每次取最小元素，C++的优先队列每次取最大元素）。这里牵涉到了大小关系，元素大小的评判可以通过元素本身的自然顺序（natural ordering），也可以通过构造时传入的比较器（Comparator，类似于C++的仿函数）。

Java中PriorityQueue实现了Queue接口，不允许放入null元素；其通过堆实现，具体说是通过完全二叉树（complete binary tree）实现的小顶堆（任意一个非叶子节点的权值，都不大于其左右子节点的权值），也就意味着可以通过数组来作为PriorityQueue的底层实现。
![Alt text](./1513387249168.png)

上图中我们给每个元素按照层序遍历的方式进行了编号，如果你足够细心，会发现父节点和子节点的编号是有联系的，更确切的说父子节点的编号之间有如下关系：

```
leftNo = parentNo*2+1

rightNo = parentNo*2+2

parentNo = (nodeNo-1)/2
```

通过上述三个公式，可以轻易计算出某个节点的父节点以及子节点的下标。这也就是为什么可以直接用数组来存储堆的原因。

**PriorityQueue的peek()和element操作是常数时间，add(), offer(), 无参数的remove()以及poll()方法的时间复杂度都是log(N)。**