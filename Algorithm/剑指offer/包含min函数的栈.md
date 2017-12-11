## 包含min函数的栈
> 定义栈的数据结构，请在该类型中实现一个能够得到栈最小元素的min函数。

### 解题思路
用一个栈data保存数据，用另外一个栈min保存依次入栈最小的数， pop的时候同时抛出data和min。
例如：
data中依次入栈，5,  4,  3, 8, 10, 11, 12, 1
则min依次入栈， 5,  4,  3, 3,  3,   3,    3,  1

代码如下：
```java
mport java.util.Stack;
 
public class Solution {
    Stack<Integer> s1=new Stack<Integer>();
     Stack<Integer> s2=new Stack<Integer>();
    public void push(int node) {
        s1.push(node);
        if(s2.isEmpty()||s2.peek()>=node)
            s2.add(node);
        else{
            s2.add(s2.peek());
        }
    }
     
    public void pop() {
        s1.pop();
        s2.pop();
    }
     
    public int top() {
        return s1.peek();
    }
     
    public int min() {
      return s2.peek();  
    }
}
```