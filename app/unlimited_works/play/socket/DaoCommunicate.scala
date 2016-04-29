package unlimited_works.play.socket

import lorance.rxscoket.session._
import lorance.rxscoket._
import lorance.rxscoket.presentation.json._
import rx.lang.scala.{Subject, Observable}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Promise}
import scala.concurrent.duration._

object DaoCommunicate {
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

    connectFuture.map(c => new JProtocol(c, c.startReading))
  }

  //todo support reconnect with some complex machines(a litter complex)
  private def reconnect = {
    logLevel = 10
    log(s"Instance a DaoCommunicate")
    val client =  new ClientEntrance("127.0.0.1", 10010)
    val connectFuture = client.connect
    connectFuture.map(c => new JProtocol(c, c.startReading))
  }

  lazy val modelSocket = {
    reconnect
  }

  lorance.rxscoket.logAim++=List("get protocol", "taskId-onNext", "proto-json", "task-json")
}
