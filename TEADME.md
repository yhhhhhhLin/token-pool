# gpt调用token池

简易版

可以实现多线程调用gpt，并且按照频率去选择token调用gtp

## 使用
引入项目，添加配置文件
```
token-pool:
 tokens:
   - Bearer gptToken1
   - Bearer gptToken2
 #一个周期时间
 cycle: 1
 #一个周期可以调用的次数
 frequency: 3
```