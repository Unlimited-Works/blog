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