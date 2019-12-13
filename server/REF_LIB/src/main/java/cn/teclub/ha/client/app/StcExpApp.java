package cn.teclub.ha.client.app;

import cn.teclub.ha.client.StcException;

public class StcExpApp extends StcException 
{
	private static final long serialVersionUID = -1L;

	@SuppressWarnings("serial")
	public static class LoginFailure extends StcExpApp { } 
	
	
	@SuppressWarnings("serial")
	public static class SignupFailure extends StcExpApp { } 

	@SuppressWarnings("serial")
	public static class ResetPasswdFailure extends StcExpApp { } 
	
	
	@SuppressWarnings("serial")
	public static class SignoutFailure extends StcExpApp { } 
	
	
	@SuppressWarnings("serial")
	public static class SearchContactFailure extends StcExpApp { } 

	
	@SuppressWarnings("serial")
	public static class AddContactFailure extends StcExpApp { } 
	
	
	@SuppressWarnings("serial")
	public static class DelContactFailure extends StcExpApp { } 
}
