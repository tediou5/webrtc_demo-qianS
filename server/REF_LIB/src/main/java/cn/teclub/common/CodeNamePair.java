package cn.teclub.common;

/**
 * <h1> value-name pair class. </h1>
 * 
 * @author mancook
 *
 */
public class CodeNamePair{
    private byte   value;
    private String name;
    private String dscp;
    
    public CodeNamePair(byte value, String name, String dscp){
        this.setValue(value);
        this.setName(name);
        this.setDscp(dscp);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDscp() {
        return dscp;
    }

    public void setDscp(String dscp) {
        this.dscp = dscp;
    }

	public byte getValue() {
		return value;
	}

	public void setValue(byte value) {
		this.value = value;
	}
} // end of class CmdNamePair