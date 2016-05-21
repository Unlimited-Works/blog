import java.nio.ByteBuffer

import lorance.rxscoket._
//import lorance.rxscoket.presentation.json.JsonParse
import lorance.rxscoket.session.ClientEntrance
import rx.lang.scala.Observable
//import unlimited_works.play.socket.dao._
//import unlimited_works.play.socket.dao.module.{AccountVerifyResult, PenName, Email, Phone, UserName, Match, Params, AccountVerify, LoginModule}

//import scala.concurrent.{Promise, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.StdIn
import lorance.rxscoket.session.implicitpkg._
/**
  *
  */
object DaoSendTest extends App {
  val client =  new ClientEntrance("127.0.0.1", 10002)
  val socket = client.connect
//  val connect =
  val reading = Observable.from(socket).flatMap(_.startReading)
  val m = reading.subscribe(r => r.foreach{x => log(s"protocol - ${new String(x.loaded.array())}")})

//  val dBName = "blog"
//  val collName = "account"
//  val account = "1"
//  val password = "2"
//
//  val proto = AccountVerify(getTaskId, dBName, collName, Method.COUNT,
//    Params(Match(List(UserName(account), Phone(account), Email(account), PenName(account)), password)) )
//

//
//  def taskResult[T <: IdentityTask](taskId: String)(implicit mf: Manifest[T]): Future[Int] = {
//    log(s"def taskResult[T <: Identi")
//    val p = Promise[Int]
//    p.future
//  }
//
//  def sendWithResult[Result <: IdentityTask, Req <: IdentityTask](any: Req)(implicit mf: Manifest[Result]): Future[Int] = {
//    val bytes = JsonParse.enCode(any)
//    connectFuture.foreach(_.send(ByteBuffer.wrap(bytes)))
//
//    log(s"def taskResult[T <: Identi")
//    val p = Promise[Int]
//
//    val x = read.map{x => println(x); 1}
//    x.foreach{y  => println("xxxxxx" + y);p.trySuccess(y)}
//    p.future
//  }

  //  DaoCommunicate.getDefaultInstance.sendWithResult[AccountVerifyResult, AccountVerify](proto)
  try {
//    LoginModule.verify("1", "2")
//    sendWithResult[AccountVerifyResult, AccountVerify](proto)
//    val x = read.map{x => println(x); 1}
//    x.foreach{y  => println("xxxxxx" + y)}
//    val one = read.map{x =>
//      log(s"read.map{")
//      1
//    }
//
//    val y = one.foreach { x =>
//      log(s"receive one - $x")
//      //    p.trySuccess(x)
//    }
  } catch {
    case e: Throwable => e.printStackTrace()
  }

  val one = reading.map{x =>
    log(s"read.map{")
    1
  }

  val y = one.foreach { x =>
    log(s"receive one - $x")
    //    p.trySuccess(x)
  }

  socket.flatMap{s =>
    val firstMsg = enCoding("hello server!")
    val secondMsg = enCoding("北京,你好!")

    val data = ByteBuffer.wrap(firstMsg ++ secondMsg)
    s.send(data)
  }

  /**
    * simulate user
    */
  def inputLoop = {
    while (true) {
      log(s"input message:")
      val line = StdIn.readLine()
      val data = ByteBuffer.wrap(enCoding(line))
      socket.flatMap { s => {
        s.send(data)
      }}
    }
  }

  //helps
  def enCoding(msg: String) = {
    val msgBytes = msg.getBytes
    val length = msgBytes.length.getByteArray
    val bytes = 1.toByte +: length
    bytes ++ msgBytes
  }

  def encodeFromRead = {
    val line = StdIn.readLine()
    ByteBuffer.wrap(enCoding(line))
  }

  Thread.currentThread().join()
}
