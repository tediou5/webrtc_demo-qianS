package cn.teclub.ha3.exception;

import java.io.Serializable;
import java.util.Date;

/**
 * Exception code
 * 
 * @author Debenson
 * @since 0.1
 */
//@XmlRootElement
public class FsExceptionBean implements Serializable {
  private static final long serialVersionUID = 452472665402145715L;

  public static final FsExceptionBean ok = new FsExceptionBean(200, "Successful operation");
  public static final FsExceptionBean badRequest = new FsExceptionBean(400, "Request parameter format does not meet the requirements");
  public static final FsExceptionBean unauthorized = new FsExceptionBean(401, "Unauthenticated or token expired");
  public static final FsExceptionBean forbidden = new FsExceptionBean(403, "Block access when updating someone else's password");
  public static final FsExceptionBean internalServerError = new FsExceptionBean(500, "Server error");


  //user exception code
  public static final FsExceptionBean userNotFound = new FsExceptionBean(2001, "not found the user");
  public static final FsExceptionBean passwdWrong = new FsExceptionBean(2002, "wrong password");
  public static final FsExceptionBean authCodeAndPhoneNotMatch = new FsExceptionBean(2003, "phone number and verification code do not match");
  public static final FsExceptionBean authCodeExpired = new FsExceptionBean(2004, "authCode has expired");
  public static final FsExceptionBean authCodeNotFound = new FsExceptionBean(2005, "authCode not found");
  public static final FsExceptionBean passwdUpdateError = new FsExceptionBean(2006, "fail to update password");
  public static final FsExceptionBean tokenUpdateError = new FsExceptionBean(2007, "fail to update token");
  public static final FsExceptionBean avatarUpdateError = new FsExceptionBean(2008, "fail to update avatar");
  public static final FsExceptionBean userSigined = new FsExceptionBean(2009, "this user has already registered");
  public static final FsExceptionBean userSiginedError = new FsExceptionBean(2010, "fail to signin user");
  public static final FsExceptionBean nameModifyError = new FsExceptionBean(2011, "user name can only be modified once");
  public static final FsExceptionBean userInfoUpdateError = new FsExceptionBean(2012, "fail to update user info");
  public static final FsExceptionBean usernameOrPhoneAlreadyExistsError = new FsExceptionBean(2013, "username or phone already exists");
  public static final FsExceptionBean macAddrAlreadyExistsError = new FsExceptionBean(2014, "mac address already exists");
  public static final FsExceptionBean userDeleteError = new FsExceptionBean(2015, "fail to delete client");

  //device
  public static final FsExceptionBean deviceAddError = new FsExceptionBean(3001, "device add error");
  public static final FsExceptionBean deviceInfoUpdateError = new FsExceptionBean(3002, "device information updated failed");
  public static final FsExceptionBean deviceDeleteError = new FsExceptionBean(3003, "device delete failed");
  public static final FsExceptionBean deviceUserAssociateError = new FsExceptionBean(3004, "user is not associated with this device");
  public static final FsExceptionBean notFoundDevice = new FsExceptionBean(3005, "not find this device");
  public static final FsExceptionBean notIsDevice = new FsExceptionBean(3006, "this client is not a device");
  public static final FsExceptionBean adminAlreadyExists = new FsExceptionBean(3007, "this device already exists as an administrator");
  public static final FsExceptionBean adminNotExists = new FsExceptionBean(3008, "this device has no an administrator");
  public static final FsExceptionBean isAlreadyFriends = new FsExceptionBean(3009, "both parties are already friends");

  //message
  public static final FsExceptionBean messageInsertError = new FsExceptionBean(4001, "fail to insert message");





  public FsExceptionBean() {
  }

  public FsExceptionBean(Integer errorCode, String message) {

    this.errorCode = errorCode;
    this.message = message;
  }

  private String message;

  private Integer errorCode;

  private Date timestamp;

  private int status;

  private String error;

  private String path;

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Integer getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(Integer errorCode) {
    this.errorCode = errorCode;
  }

  public Date getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }
}
