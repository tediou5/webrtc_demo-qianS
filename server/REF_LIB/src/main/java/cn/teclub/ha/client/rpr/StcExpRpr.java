package cn.teclub.ha.client.rpr;

import cn.teclub.ha.client.StcException;
import cn.teclub.ha.request.StNetPacket;


public class StcExpRpr extends StcException
{
	private static final long serialVersionUID = 1L;


	/**
     * Request to remote client/server aborts, due to various reasons.
     * e.g. Remote client is offline, ping timeout, etc...
     *
     * @author mancook
     *
     * @deprecated
     */
    public static class ExpRequestAbort extends StcException {
		private static final long serialVersionUID = 1L;

		public ExpRequestAbort(String msg){
    		super(msg);
    	}
    }


    /**
     * throw when wait on an rpr request
     */
    public static class ExpReqTimeout  extends StcException {
		private static final long serialVersionUID = 1L; 
	}


    /**
     * @author mancook
     *
     */
    public static class ExpRequestDeny extends StcException {
    	private static final long serialVersionUID = 1L;
    	
    	public final int 	code; // DENY code
    	public final Object result;

    	public ExpRequestDeny(int deny_code, Object deny_result, String err_msg){
    		super(err_msg);
    		this.code = deny_code;
    		this.result = deny_result;
    	}

    	public ExpRequestDeny(int deny_code, String err_msg){
    		super(err_msg);
    		this.code = deny_code;
    		this.result = null;
    	}

    	public ExpRequestDeny(){
    		super("Operation Failure");
    		this.code = StNetPacket.Code.NONE;
    		this.result = null;
    	}

    	public ExpRequestDeny(String err_msg){
    		super(err_msg);
    		this.code = StNetPacket.Code.NONE;
    		this.result = null;
    	}
    }
}
