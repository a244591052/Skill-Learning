# 166. Fraction to Recurring Decimal（循环小数）

## 题目描述
> Given two integers representing the numerator and denominator of a fraction, return the fraction in string format.

> If the fractional part is repeating, enclose the repeating part in parentheses.

> For example,

> - Given numerator = 1, denominator = 2, return "0.5".
> - Given numerator = 2, denominator = 1, return "2".
> - Given numerator = 2, denominator = 3, return "0.(6)".
> - Given numerator = 7, denominator = 6, return "1.1(6)".

## 解题思路
1. 首先拿到题目，很容易陷入直接拿除数除以被除数，然后对结果逐位分析找出循环节，但随着几个实例的分析你会发现那样做太复杂了，肯定有更好的方法。
&ensp;
2. 所以我们换个思路（余数检测），我们直接模拟除法，每次都作除，取整数部分，然后余数*10，直到余数为零（想想看这是不是我们手工除法的思路），
&ensp;
3. 那么如何发现循环节呢？我们发现当某一次作除的余数在以前的余数历史中出现过，就可以判定存在循环节了，因此我们可以通过哈希表来存余数的历史记录，一旦发现重复，后面将不再作除，输出带有循环节的结果。
&ensp;
4. 举例：比如我们要计算1/7这个小数的循环节，我们事先求出整数部分，也就是1/7=0。然后再求小数部分，求出第一组商con和它所对应的余数r，不难得出，con=（1×10）/7=1，r=（1×10）%7=3，如果想不明白，可以自己手算一下，然后进入循环，将第一组数据保存起来，接着算第二组，con=（3×10）/7=4，r=（3×10）%7=2。
每次求出一组商和余数的数据，我们需要判断一下：所求出的余数之前有没有出现过，如果余数重复出现，那就说明循环节出现了，然后我们将取出哈希表中该余数第一次出现的位置，即为循环节开始的位置。

> 注：当你觉得你的方法复杂无比的时候，通常代表你的思路有问题，这时候可以尝试换个思路，不要在死胡同里找出路，比方说这题你根本不可能通过直接分析作除结果的小数部分来找出循环节（连续重复几个数可以判定循环呢？），不过如果采用余数检测，只要是余数相同，那么肯定代码出现循环了。

## 代码

```java
class Solution {
    public String fractionToDecimal(int numerator, int denominator) {
        if (numerator == 0)
            return "0";
        StringBuilder fraction = new StringBuilder();
        // 精彩，这里使用异或操作，判断除数是否为负数，学习学习！
        if (numerator < 0 ^ denominator < 0){
            fraction.append("-");
        }
        // 将整数转换为长整形，避免溢出问题
        long num = Math.abs(Long.valueOf(numerator));
        long denom = Math.abs(Long.valueOf(denominator));

        // 计算整数部分
        fraction.append(num / denom);
        long remainder = num % denom;

        // 如果余数等于0，则没有小数
        if (remainder == 0)
            return fraction.toString();

        // 否则加上小数点，开始计算小数部分
        fraction.append(".");

        // 使用哈希表存储余数，如果遇到余数相等的情况，那么就代表后面的小数开始循环
        HashMap<Long, Integer> map = new HashMap<>();

        // 如果余数为0，代表整除，否则一直除下去
        while(remainder != 0){
            // 如果遇到相同的余数，代表时无限循环小数，输出循环节
            if(map.containsKey(remainder)){
                fraction.insert(map.get(remainder), "(");
                fraction.append(")");
                break;
            }
            // 模拟除法！，每次将余数乘10，在与被除数相除。
            map.put(remainder, fraction.length());
            remainder *= 10;
            fraction.append(remainder / denom);
            remainder = remainder % denom;
        }

        return fraction.toString();
    }
}
```