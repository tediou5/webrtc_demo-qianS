package cn.teclub.ha.net.serv;



/**
 * <h1>Object mapped to record in database table. </h1>
 * 
 * All database table classes must extend this class. So that the hibernate manager can handle them.
 * 
 * @author mancook
 */
interface StDbTable  
{
	StringBuffer dump();
}

