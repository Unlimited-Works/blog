package unlimited_works.play.socket.dao.module.account

import lorance.rxscoket.presentation.json.IdentityTask
import unlimited_works.play.socket.DaoCommunicate
import lorance.rxscoket.presentation

import scala.concurrent.Promise
import scala.concurrent.ExecutionContext.Implicits.global

/**
  *
  */
object Account{
  def accountExist(email: String, username: String, penName: String) = {
    DaoCommunicate.modelSocket.flatMap{s =>
      val rst = s.sendWithResult[AccountExistRsp, AccountExistReq](AccountExistReq(email, username, penName), Some(x => x.takeUntil(s => s.isCompleted.isDefined)))
      val p = Promise[AccountExistRsp]
      rst.subscribe(
        s => p.trySuccess(s)
      )
      p.future
    }
  }

  case class AccountEmailNameAndPenName(email: String, username: String, penName: String)
  case class AccountExistReq(email: String, username: String, penName: String,
                             model: String = "account/exist",
                             taskId: String = presentation.getTaskId) extends IdentityTask
  case class AccountExistRsp(accountData: Option[AccountEmailNameAndPenName], isCompleted: Option[Boolean], taskId: String) extends IdentityTask
  def register(
               email: String,
               username: String,
               penName: String,
               password: String) = {
    DaoCommunicate.modelSocket.flatMap{s =>
      val daoRst = s.sendWithResult[RegisterRsp, RegisterReq](RegisterReq(
        email,
        username, penName, password), Some(x => x.first))
      val p = Promise[Option[String]]
      daoRst.subscribe(
        s => p.trySuccess(s.error),
        e => p.tryFailure(e)
      )
      p.future
    }
  }

  case class RegisterReq(
                         email: String, username: String,
                         penName: String, password: String,
                         model: String = "signin/register/addAccount", taskId: String = presentation.getTaskId) extends IdentityTask
  case class RegisterRsp(error: Option[String], taskId: String) extends IdentityTask

  def invitationCodeUseable_? (invitationCode: String) = {
    DaoCommunicate.modelSocket.flatMap { s =>
      val rst = s.sendWithResult[GetInvitationCodeStatusRsp, GetInvitationCodeStatusReq](GetInvitationCodeStatusReq(invitationCode), Some(x => x.first))
      val p = Promise[Boolean]
      rst.subscribe(
        s => p.trySuccess(s.canUse)
      )
      p.future
    }
  }

  case class GetInvitationCodeStatusReq(invitCode: String, model: String = "signin/register/invitCodeStatus", taskId: String = presentation.getTaskId) extends IdentityTask
  case class GetInvitationCodeStatusRsp(canUse: Boolean, taskId: String) extends IdentityTask

  def useInvitationCode(invitationCode: String) = {
    DaoCommunicate.modelSocket.flatMap{s =>
      val rst = s.sendWithResult[UseInvitCodeRsp, UseInvitCodeReq](UseInvitCodeReq(invitationCode), Some(x => x.first))
      val p = Promise[Option[String]]
      rst.subscribe(
        s => p.trySuccess(s.error),
        e => p.tryFailure(e)
      )
      p.future
    }
  }

  case class UseInvitCodeReq(invitCode: String, model: String = "signin/register/invitCode", taskId: String = presentation.getTaskId) extends IdentityTask
  case class UseInvitCodeRsp(error: Option[String], taskId: String) extends IdentityTask
}

//case class FindaccountIdByPenName