package cn.teclub.ha3.server.core;



/**
 * <h1>Object mapped to record in database table. </h1>
 * 
 * All database table classes must extend this class. So that the hibernate manager can handle them.
 * 
 * @author mancook
 */

@SuppressWarnings("ALL")
interface StDbTable
{
	StringBuffer dump();
}

