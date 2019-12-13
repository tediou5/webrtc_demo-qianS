package cn.teclub.ha.net.serv;

import org.hibernate.HibernateException;

public class StExpHibernate extends StExpServer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public final HibernateException hiberExp;
	
	public StExpHibernate(HibernateException e){
		this.hiberExp = e;
	}
}
