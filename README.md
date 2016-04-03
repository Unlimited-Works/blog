##Web app build by Play Framework

####Depencency
* [mongodb project](https://github.com/Unlimited-Works/mongodb)
* [RxSocket](https://github.com/LoranceChen/RxSocket)
* [RxScala](https://github.com/ReactiveX/RxScala)

#### Start app
sbt run

#### Completed
http://localhost:9000/signin  
verify data from browser->web server->dao->mongo is reachable

####Readmap
* add time limit of dao socket communicate.
* handle reconnect
* 需要让rxsocket的presentation层提供终止Observable观察条件的函数参数,因为
find查询不是一次返回的.不能认为只要收到一个taskId就结束.另外,sendWithTask方法
应该返回Observable而不是Future