package cn.teclub.ha3.server.core;

import org.hibernate.dialect.MySQL5InnoDBDialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.IntegerType;


/**
 * <h1> User Defined hibernate Dialect.</h1>
 * 
 * Reference: 
 * - http://stackoverflow.com/questions/18834316/hibernate-bitwise-operation-function-not-getting-registered
 * - https://forum.hibernate.org/viewtopic.php?t=940978&highlight=bitwise
 * 
 * @author mancook
 *
 */
public class MySqlDialect extends MySQL5InnoDBDialect {

	  public MySqlDialect() {
	       super();
	       registerFunction("bitwise_and", 
	    		   new SQLFunctionTemplate(IntegerType.INSTANCE, "(?1 & ?2)"));
	   }
}
