package unlimited_works.play.socket

import lorance.rxscoket.session._
import lorance.rxscoket._
import lorance.rxscoket.presentation.json._
import rx.lang.scala.{Subject, Observable}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Promise}
import scala.concurrent.duration._

object DaoCommunicate {

  /**
    * it works but just for test data is reachable
    */
  private lazy val getInstanceByAwaitConnect = {
    logLevel = 10
    log(s"Instance a DaoCommunicate")
    val client =  new ClientEntrance("127.0.0.1", 10001)
    val connectFuture = client.connect
    val connect = Await.result(client.connect, DurationInt(5).seconds)
    val socketWithRead = connect.startReading
    new JProtocol(connect, socketWithRead)
  }
  /**
    * todo
    * Q: why use `.publish` and `.connect` NOT send data to server?
    * NOTE: remove `.publish` can able to arrive to server but in terms of semantics it should be hot Observable, otherwise,
    * it will cased multi execute `.startReading` throws Exception.
    */
  private lazy val getDefaultInstanceByPublish = {
    logLevel = 10
    log(s"Instance a DaoCommunicate")
    val client =  new ClientEntrance("127.0.0.1", 10001)
    val connectFuture = client.connect
    val connect = Observable.from(connectFuture)
    val socketWithRead = connect.map(c => c -> c.startReading).publish
    socketWithRead.connect

    val p = Promise[JProtocol]
    socketWithRead.subscribe{ sr =>
      log(s"receive connect", 3)
      p.trySuccess(new JProtocol(sr._1, sr._2))
    }
    p.future
  }

  /**
    * todo
    * Q: why use `Subject` also NOT send data to server?
    */
  private lazy val getInstanceBySubject = {
    logLevel = 10
    log(s"Instance a DaoCommunicate")
    val client =  new ClientEntrance("127.0.0.1", 10001)
    val connectFuture = client.connect

    val socketWithRead = Subject[(ConnectedSocket, Observable[Vector[CompletedProto]])]()

    val connect = Observable.from(connectFuture)
    connect.subscribe{x => socketWithRead.onNext(x -> x.startReading); socketWithRead.onCompleted()}
    val p = Promise[JProtocol]
    socketWithRead.subscribe{ sr =>
      log(s"receive connect", 3)
      p.trySuccess(new JProtocol(sr._1, sr._2))
    }

    p.future
  }

  //reconnect
  //1. java.net.ConnectException when client begin start
  //2. java.nio.channels.ClosedChannelException when server disconnect
  /**
    * this stage use Future is naturally.
    */
  lazy val getDefaultInstance = {
    logLevel = 10
    log(s"Instance a DaoCommunicate")
    val client =  new ClientEntrance("127.0.0.1", 10001)
    val connectFuture = client.connect

    lorance.rxscoket.presentation.TIMEOUT = 10
    connectFuture.map(c => new JProtocol(c, c.startReading))
  }

  def reconnect = {
    logLevel = 10
    log(s"Instance a DaoCommunicate")
    val client =  new ClientEntrance("127.0.0.1", 10010)
    val connectFuture = client.connect
    lorance.rxscoket.presentation.TIMEOUT = 10
    connectFuture.map(c => new JProtocol(c, c.startReading))
  }

  lazy val modelSocket = {
    reconnect
  }
}
