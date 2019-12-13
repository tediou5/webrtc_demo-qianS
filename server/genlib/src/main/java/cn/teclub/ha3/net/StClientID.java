package cn.teclub.ha3.net;

import cn.teclub.common.ChuyuLongID;
import cn.teclub.common.ChuyuUtil;
import cn.teclub.ha3.utils.StConst;

import java.nio.ByteBuffer;
import java.util.ArrayList;


@SuppressWarnings("ALL")
public class StClientID extends ChuyuLongID
{
	public static final StClientID   GEN_ID = new StClientID(0);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8983389300029838191L;

	public static ByteBuffer toBuffer(final StClientID[]  clt_list ){
		ChuyuUtil.getInstance().assertTrue(clt_list.length <= StConst.MAX_FRIEND, "Too many friends!");
		ByteBuffer data_buf = ByteBuffer.allocate(2 + clt_list.length * StClientID.OBJLEN);
		data_buf.putShort((short) clt_list.length);
		for(StClientID e: clt_list){
			data_buf.putLong(e.getId());
		}
		data_buf.rewind();
		return data_buf;
	}
	
	
	public static ArrayList<StClientID> fromBuffer(final ByteBuffer data){
		final int count  = data.getShort();
		util.assertTrue( count <= StConst.MAX_FRIEND, "Too many friends!");
		ArrayList<StClientID> list = new ArrayList<StClientID>();
		for(int i=0; i<count; i++){
			list.add(new StClientID(data.getLong()));
		}
		return list;	
	}
	
	
	public StClientID(){
		super(GEN_ID.getId());
	}
	
	public StClientID(long id) {
		super(id);
	}


	public StClientID(StClientID id){
		super(id.getId());
	}


	public boolean valid(){
		return getId() > GEN_ID.getId();
	}


	public String toString(){
		return "0x" + Long.toHexString(getId());
	}
} 
