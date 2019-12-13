package cn.teclub.ha.request;


/**
 * Used by calling thread of a request, to get the result of a request. 
 * @author mancook
 * 
 * @deprecated use 'network request'
 *
 */
public interface StRequestCallback {
	public void onResponse(final boolean allowed, final byte code, final Object result);
	
	public void onTimeout();
}
