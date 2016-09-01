package unlimited_works.play.socket

import lorance.rxscoket.session._
import lorance.rxscoket.presentation.json._
import scala.concurrent.ExecutionContext.Implicits.global
import unlimited_works.play.playLogger

object DaoCommunicate {
  lorance.rxscoket.presentation.JPROTO_TIMEOUT = 30

  //reconnect
  //1. java.net.ConnectException when client begin start
  //2. java.nio.channels.ClosedChannelException when server disconnect
  /**
    * this stage use Future is naturally.
    */
  lazy val getDefaultInstance = {
    playLogger.logLevel = 1000
    playLogger.log(s"Instance a DaoCommunicate")
    val client =  new ClientEntrance("127.0.0.1", 10001)
    val connectFuture = client.connect

    connectFuture.map(c => new JProtocol(c, c.startReading))
  }

  //todo support reconnect with some complex machines(a litter complex)
  private def reconnect = {
    playLogger.logLevel = 10
    playLogger.log(s"Instance a DaoCommunicate")
    val client =  new ClientEntrance("127.0.0.1", 10010)
    val connectFuture = client.connect
    connectFuture.map(c => new JProtocol(c, c.startReading))
  }

  lazy val modelSocket = {
    lorance.rxscoket.presentation.JPROTO_TIMEOUT = 20
    reconnect
  }

  playLogger.logAim++=List("get protocol", "taskId-onNext", "proto-json", "task-json")
}
