package cn.teclub.ha3.server.core;

import org.hibernate.HibernateException;


@SuppressWarnings("ALL")
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
