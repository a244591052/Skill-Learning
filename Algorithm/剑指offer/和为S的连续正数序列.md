# 和为S的连续正数序列
## 题目描述
小明很喜欢数学,有一天他在做数学作业时,要求计算出9~16的和,他马上就写出了正确答案是100。但是他并不满足于此,他在想究竟有多少种连续的正数序列的和为100(至少包括两个数)。没多久,他就得到另一组连续正数和为100的序列:18,19,20,21,22。现在把问题交给你,你能不能也很快的找出所有和为S的连续正数序列? Good Luck!

### 输入描述
输出所有和为S的连续正数序列。序列内按照从小至大的顺序，序列间按照开始数字从小到大的顺序

## 解题思路
用两个数字small和big分别表示序列的最大值和最小值，
首先将small初始化为1，big初始化为2.
如果从small到big的和大于s，我们就从序列中去掉较小的值(即增大small),
相反，只需要增大big。
终止条件为：`一直增加small到(1+sum)/2并且small小于sum为止，很巧妙！`
时间复杂度为O(n)

> 总结：当题目中涉及到数组的区间问题时，可以考虑使用双指针来解决问题。

```java
import java.util.ArrayList;
public class Solution {
    public ArrayList<ArrayList<Integer> > FindContinuousSequence(int sum) {
       	ArrayList<ArrayList<Integer>> ret = new  ArrayList<ArrayList<Integer>>();
        int small = 1;
        int big = 2;
        int sumTemp = 0;
        while(small<=(sum/2)){
            sumTemp = sumSection(small, big);
            if (sumTemp == sum){
                addOneResult(ret, small, big);
                big++;
            }
            else{
                if(sumTemp > sum)
                    small++;
                else
                    big++;
            }
        }
        return ret;
    }
    public int sumSection(int small, int big){
        int sum = 0;
        while(small <= big){
            sum += small;
            small++;
        }
        return sum;
    }
    public void addOneResult(ArrayList<ArrayList<Integer>> ret, int small, int big){
        ArrayList<Integer> temp = new ArrayList<Integer>();
        while(small <= big){
            temp.add(small);
            small++;
        }
        ret.add(temp);
    }
}
```

## 拓展：和为S的两个数字
### 题目描述
输入一个递增排序的数组和一个数字S，在数组中查找两个数，是的他们的和正好是S，如果有多对数字的和等于S，输出两个数的乘积最小的。

### 解题思路
数列满足递增，设两个头尾两个指针i和j，
若a[i] + a[j] == sum，就是答案（`相差越远乘积越小，所以第一个结果就是答案！`）
若a[i] + a[j] > sum，aj肯定不是答案之一（前面已得出 i 前面的数已是不可能），j -= 1
若a[i] + a[j] < sum，ai肯定不是答案之一（前面已得出 j 后面的数已是不可能），i += 1
时间复杂度为O(n)

```java
import java.util.ArrayList;
public class Solution {
    public ArrayList<Integer> FindNumbersWithSum(int [] array,int sum) {
        ArrayList<Integer> ret = new ArrayList<Integer>();
        if(array==null || array.length ==0) return ret;
        int start = 0;
        int end = array.length - 1;
        while(start<end){
            if((array[start]+array[end]) == sum){
                ret.add(array[start]);
                ret.add(array[end]);
                break;
            } else if((array[start]+array[end]) > sum){
                end--;
            } else{
                start++;
            }
        }
        return ret;
        
    }
}
```