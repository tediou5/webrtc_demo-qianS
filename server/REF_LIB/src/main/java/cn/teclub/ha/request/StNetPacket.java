package cn.teclub.ha.request;

import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Vector;

import cn.teclub.common.*;
import cn.teclub.ha.lib.StBasicCipher;
import cn.teclub.ha.lib.StCoder;
import cn.teclub.ha.lib.StConst;
import cn.teclub.ha.lib.StErrUserError;
import cn.teclub.ha.lib.StExpUserError;
import cn.teclub.ha.lib.StGenPkt;
import cn.teclub.ha.net.StClientID;
import cn.teclub.ha.net.StClientInfo;
import cn.teclub.ha.net.StExpNet;
import static cn.teclub.ha.request.StNetPacket.Flow.CLIENT_TO_CLIENT;
import static cn.teclub.ha.request.StNetPacket.Flow.CLIENT_TO_SERVER;
import static cn.teclub.ha.request.StNetPacket.Flow.SERVER_TO_CLIENT;


/**
 * <h1>Define a Network Packet.</h1>
 * 
 * <p> In most cases, this class is enough to manipulate packets with different codes.
 * Especially the packets without DATA. <br/>
 * e.g. P2P_START_CALL, P2P_CHG_CAM, CLT_STATUS, YOU_LOGOUT, ...
 * 
 * <p> Only define its sub-class for packets, which has complex data structure and 
 * need special methods to get correct properties from packet data. <br/>
 * e.g. LOGIN, CLIENT_A_QUERY_B, ...
 *
 *
 * Logs
 * [2017-7-21] Add P2P_GET_FILE request and updated docs.
 *
 * @author mancook
 *
 *
 */
@SuppressWarnings({"unused"})
public class StNetPacket
	extends StGenPkt
	implements ChuyuObj.DumpAttribute
{
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS 
	////////////////////////////////////////////////////////////////////////////	
	static final int MAX_CLIENT_B_COUNT 	= StConst.MAX_QUERY_B_COUNT;
	static final int PKT_DATA_MAX_LENGTH 	= StConst.PKT_MAX_LENGTH - StNetPacket.Header.OBJLEN;

	
	@SuppressWarnings("serial")
	public static class ExpReceiveTooFewBytes extends StExpNet {
		public ExpReceiveTooFewBytes(String msg){
			super(msg);
		}
	}
	
	
	@SuppressWarnings("serial")
	public static class ConstructPacketFailure extends StExpNet {
		public ConstructPacketFailure(String msg){
			super(msg);
		}
	}
	
	
	/**
	 * <h2>Build from a received buffer. </h2>
	 * 
	 * @param pkt_buf
	 * @return
	 * @throws ExpReceiveTooFewBytes 
	 */
	private static StNetPacket buildFromPlainBuffer(ByteBuffer pkt_buf) 
			throws ExpReceiveTooFewBytes
	{
		StNetPacket pkt0 = new StNetPacket(pkt_buf);
		Command cmd = pkt0.getCmd();
		
		if(cmd == Command.Login){
			return new StPktLogin(pkt0);
		}
		if(cmd == Command.Signup){
			return new StPktSignup(pkt0);
		}
		if(cmd == Command.Signout){
			return new StPktSignout(pkt0);
		}
		if(cmd == Command.PublicAddr){
			return new StPktPublicAddr(pkt0);
		}
		
		// no sub-class for this packet, just return the original packet
		return pkt0;
	}
	
	
	public static StNetPacket buildFromBuffer(ByteBuffer raw_buf) 
	throws ExpReceiveTooFewBytes, ConstructPacketFailure  
	{
		util.assertNotNull(raw_buf, "Can build a packet from empty buffer!");
		if(raw_buf.remaining() < Const.ENC_HDR_LEN){
			throw new ExpReceiveTooFewBytes(
					"remainingg bytes ("+raw_buf.remaining()+") " + 
				    "is smaller than encrypt hdrlen: " + Const.ENC_HDR_LEN);
		}
		final int BUF_START_REMAIN 	= raw_buf.remaining();
		final int BUF_START_POS 	= raw_buf.position();
		
		
		// [2018-1-11] get version from encrypt or plain header 
		// ATTENTION: 'enc_len' is ONLY valid for version 1.0!!!!!
		//
		byte chsum 	= raw_buf.get();
		byte version= raw_buf.get();
		short enc_pkt_len = raw_buf.getShort(); 
		raw_buf.position(BUF_START_POS); // restore the buffer pos at once! 
		
		if(version == Const.VERSION_1_0){
			return buildFromPlainBuffer(raw_buf);
		}else if(version == Const.VERSION_1_1){
			if( BUF_START_REMAIN < enc_pkt_len ){
				throw new ExpReceiveTooFewBytes("Length attribute in enc-header (" + enc_pkt_len + ") " +
						"IS BIGGER THAN remaining of input buffer (" + BUF_START_REMAIN + ")");
			}
			final int enc_arr_pos = raw_buf.arrayOffset() + BUF_START_POS + Const.ENC_HDR_LEN;
			final int enc_len =  enc_pkt_len - Const.ENC_HDR_LEN;
			byte[] dec_data;
			try{
				dec_data = StBasicCipher.instance().decrypt(raw_buf.array(), enc_arr_pos, enc_len);
			} catch (GeneralSecurityException e) {
				e.printStackTrace();
				throw new ConstructPacketFailure("fail to decrypt packet!");
			}
			raw_buf.position(BUF_START_POS + enc_pkt_len);
			return  buildFromPlainBuffer( ByteBuffer.wrap(dec_data));
		}else{
			throw new ConstructPacketFailure("unsupported version: " + version);
		}
	}
	
	
	
	
	/**
	 * <h2>Build a packet and froze it!</h2>
	 * 
	 * @param cmd  	Use members in Command
	 * @param code	Use members in Code (TODO)
	 * @param s
	 * @param f
	 * @param ss_id
	 * @param src_clt
	 * @param dst_clt
	 * @param data
	 * @return
	 */
	public static StNetPacket build (
			final Command cmd, final Service s, final Flow f, final byte code, 
			final StRequestID ss_id, 
			final StClientID src_clt, final StClientID dst_clt, 
			final ByteBuffer data) 
	{
		StNetPacket pkt = new StNetPacket(cmd, code, s, f, ss_id, src_clt, dst_clt, data);
		pkt.froze();
		return pkt;
	}
	
	
	/**
	 * 
	 * @param cmd
	 * @param f
	 * @param code
	 * @param req_id
	 * @param src_clt
	 * @param dst_clt
	 * @param data
	 * @return
	 * @throws StExpUserError
	 */
	public static StNetPacket buildReq (
			final Command cmd, final Flow f, final byte code, 
			final StRequestID req_id,
			final StClientID src_clt, final StClientID dst_clt,
			final ByteBuffer data) 
	{
		return	build(cmd, Service.REQUEST, f, code, req_id, src_clt, dst_clt, data);
	}
	
	
	public static StNetPacket buildReq (
			final Command cmd, final Flow f, final byte code, 
			final StRequestID req_id, 
			final StClientID src_clt, final StClientID dst_clt ) 
	{
		return	build(cmd, Service.REQUEST, f, code, req_id, src_clt, dst_clt, null);
	}
	
	
	/**
	 * @deprecated 
	 * 
	 * @param cmd
	 * @param s
	 * @param f
	 * @param code
	 * @param req_id
	 * @param src_clt
	 * @param dst_clt
	 * @param data
	 * @return
	 */
	public static StNetPacket buildNoFroze22 (
			Command cmd, Service s, Flow f, byte code, 
			StRequestID req_id,
			StClientID src_clt, StClientID dst_clt, 
			ByteBuffer data) 
	{
        return new StNetPacket(cmd, code, s, f, req_id, src_clt, dst_clt, data);
	}
	
	////////////////////////////////////////////////////////////////////////////
    // Inner Classes
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * <pre>
	 * Header attribute 'cmd'
	 * ~~~~~~~~~~~~~~~~~~~~~
	 * 'cmd' is the ID of network packet. 
	 *  Some 'cmd' have a sub-class, e.g. CMD_LOGIN/StPktLogin. 
	 *  Others do not, e.g. CMD_CALL_START.
	 * 
	 * NOTE: [2015-12-30] We recommend you use StNetPacket for new 'cmd'. 
	 * 
	 * 
	 * Header attribute 'type'
	 * ~~~~~~~~~~~~~~~~~~~~~~
	 * DESCRIPTION: 'type' specifies the general feature of a packet. Every packet must have all following types. 
	 * Currently, there are TWO types: request type and flow direction type. 
	 * 
	 * Values for these types: 
	 * 1) Request type: REQUEST / REQ-ALLOW / REQ-DENY
	 * 2) Flow direction type: Client -> Server / Server -> Client / Client -> Client;
	 * 				
	 * Bits for type values:
	 * [0-1]:  packet request/allow/deny types
	 *         00: none. no request/response is required.
	 *         01: request
	 *         10: response allow
	 *         11: response deny
	 * 
	 * [2-3]:  packet flow direction flag
	 *         00: Client -> Server;		 
	 *         01: Server -> Client;	
	 *         10: Client -> Client; 
	 * 
	 * [4-7]: <not used>
	 * 
	 * 
	 * 
	 * Header attribute 'code'
	 * ~~~~~~~~~~~~~~~~~~~~~~
	 * DESCRIPTION: 'code' indicates additional information of a type-specific packet. 
	 * e.g. For a LOGIN-DENY server-to-client packet, 'code' value indicates the reason for the failure of login. 
	 * 
	 * </pre>
	 * 
	 * @author mancook
	 *
	 */
	public class Header{
		public static final int OBJLEN = 8 + StClientID.OBJLEN * 3;
				
		private byte 	checksum;
		private byte 	version;
		private short 	cmd;
		private byte  	type;
		private byte 	code;  
		
		private int length;
		 				// used by sub-classes (specific packets) to indicate status
		private StRequestID    	ssId;		// ID of session on client, can be NULL;
		private StClientID    	srcClt; 	// client ID which sends this packet;
		private StClientID		dstClt;		// Client ID which receives this packet, NULL for server;
		
		Header(){
			this.checksum =  0; // checksum is calculated when all data is written
			this.version = Const.VERSION_1_1;
			this.cmd = Command.None.value;
			
			this.length = 0; 	// length is updated when all data is written
			this.type = 0;
			this.code = 0;
			this.srcClt = StClientID.GEN_ID;
			this.dstClt = StClientID.GEN_ID;
			this.ssId  =  StRequestID.NULL_ID;
		}
		
		
		
		byte getVersion(){
			return version;
		}
		
	} // end of class Header
	

    public static class Code {
		/**
		 * Reserved Code for All Requests: 0x00, 0xE0 ~ 0xFF
		 */
		public static final byte NONE 						= (byte) 0x00;


        /**
         * server will not block at any request.
         * But it may denies requests when it has performance issue.
         *
         * reserved: for future use.
         */
        public static final byte DENY_SRV_Service_BUSY  	= (byte) 0xE0;


        /**
         * client is running an SYNC service, and denies any incoming SYNC requests
         * see: class StRequest
         */
        public static final byte DENY_P2P_Service_BUSY  	= (byte) 0xE1;


        /**
         * When client is running an operation, some request will be denied.
         *
         * e.g. RecordStart, RecordStop are operations.
         * When they are running, call-start request will be denied.
         * Because in old implementation, start-call request stops current recording.
         *
         */
        public static final byte DENY_P2P_RUNNING_OPT  		= (byte) 0xE2;


        /**
         * something wrongn with DB.
         */
        public static final byte DENY_DB_BUSY 				= (byte) 0xE3;
		public static final byte DENY_DB_ERROR				= (byte) 0xE4;


        /**
         * current state is NOT ready for incoming request.
         */
        public static final byte DENY_STATE_NOT_READY		= (byte) 0xE5;


        /**
         * other errors
         */
		public static final byte DENY_ERROR					= (byte) 0xFF;
		

        public static class AdminGetInfo{
		    // cmd: CMD_ADMIN_GET_INFO
	        public static final byte REQUEST_CACHE_CLIENT 	= (byte) 0x01;
	        public static final byte REQUEST_ONLINE_CLIENT 	= (byte) 0x02;
	        public static final byte REQUEST_CONN_GRP 		= (byte) 0x03;
	        public static final byte REQUEST_SUM_CLIENT 	= (byte) 0x04;
	        public static final byte REQUEST_COUNT_CLIENT 	= (byte) 0x05;
	        public static final byte REQUEST_DUMP_ALL 		= (byte) 0x06;
	        public static final byte REQUEST_DUMP_CLIENT 	= (byte) 0x07;
	        public static final byte REQUEST_VERSION_SRV 	= (byte) 0x08;
	        
	        public static final byte REQUEST_PULSE_POOL 	= (byte) 0x09;
	        public static final byte REQUEST_COUNT 			= (byte) 0x0A;
	        public static final byte REQUEST_CONFIG 		= (byte) 0x0B;
	        public static final byte REQUEST_CONNECTION 	= (byte) 0x0C;
		}
		
        
        
       
        public static class SmsVerifyCode{ 
        	public static final byte REQ_SIGNUP			= (byte) 0x01;
        	public static final byte REQ_RESET_PASSWD 	= (byte) 0x02;
        	
        	public static final byte  DENY_SEND_SMS_FAILURE 	= 0x01; 
        	public static final byte  DENY_UNKNOWN_REQ 			= 0x02; 
        	public static final byte  DENY_UNREG_PHONE_NUM 		= 0x03; // cellphone is not registered in DB.
        }
        
        
        
        public static class ResetPasswd{
        	public static final byte DENY_PHONE_NOT_FOUND		= (byte) 0x01;	
        	public static final byte DENY_SMS_CODE_INVALID		= (byte) 0x02;	
        }
        

        
        public static class Login{
	        // cmd: CMD_LOGIN, server-to-client DENY CODE
	        public static final byte  DENY_USER_NAME_ERROR 		= (byte) 0x01;
	        public static final byte  DENY_PASSWD_ERROR 		= (byte) 0x02;
	        // public static final byte  DENY_LOCK_FAILURE 		= (byte) 0x03;
		}
		

        public static class Signup{
	        // cmd: CMD_SIGNUP, client-to-server REQUEST CODE
	        public static final byte  REQUST_GATEWAY 				= (byte) 0x01;
	        public static final byte  REQUST_USER 					= (byte) 0x02;
	        public static final byte  REQUST_MONITOR 				= (byte) 0x03;
	        
	        public static final byte  ALLOW_USE_OLD 				= (byte) 0x01;
	        
	        public static final byte DENY_NAME_EXIST				= (byte) 0x01;	
	        public static final byte DENY_SMS_CODE_INVALID			= (byte) 0x02;	
	        public static final byte DENY_UN_SUPPORTED_CLIENT		= (byte) 0x03;	
		}
		

        public static class ApplyForAdmin {
			 public static final byte DENY_ADMIN_EXIST	= (byte) 0x01;	
		}
		

        public static class MessageToSrv{
			public static final byte DENY_FRIEND_EXIST		= (byte) 0x01;	
			public static final byte DENY_DEVICE_NO_MASTER	= (byte) 0x02;	
		}
		

        public static class PreLoginQuery {
        	// TODO: add prefix "REQ_"
			public static final byte QUERY_NAME_BY_MAC 	 	= (byte)0x01;
			public static final byte QUERY_NAME_BY_PHONE	= (byte)0x02;
			//public static final byte ALLOW_QUERY_NAME_BY_MAC 		= (byte)0x01;
			//public static final byte ALLOW_QUERY_NAME_BY_PHONE	= (byte)0x02;
        }
        
       
        // TODO: add prefix 'P2p';
        public static class CallStart{
	        // cmd: CMD_CALL_START, P2P DENY CODE 
	        public static final byte  DENY_RECORDING 		= 0x01;
	        public static final byte  DENY_SIP_NOT_READY 	= 0x02;
	        public static final byte  DENY_USER_REJECT 		= 0x03;
	        public static final byte  DENY_RUNTIME_ERROR 	= 0x04;
	        public static final byte  DENY_NOT_IDEAL 		= 0x05;
	        public static final byte  DENY_SIP_UNAVAILABLE  = 0x06;
		}
		

        public static class EditInfo{
	        public static final byte  REQ_LABEL 		= 0x01;
	        public static final byte  REQ_DSCP			= 0x02;
	        public static final byte  REQ_PASSWORD		= 0x03;
	        public static final byte  REQ_CELLPHONE		= 0x04;
	        public static final byte  REQ_ICON_TS		= 0x05;
		}
        
        
        public static class SlaveManage{
        	// edit client info of the slave
	        public static final byte  REQ_EDIT_INFO 		= (byte) 0x01;
	        
	        public static final byte  DENY_INPUT_ERROR 		= (byte) 0xA0;
	        public static final byte  DENY_SLAVE_NOT_FOUND	= (byte) 0xA1;
		}
		

        public static class P2PRecordStart{
	        // cmd: P2P_RECORD_START, P2P DENY CODE 
			public static final byte  DENY_STAT_NOT_IDEAL 	= 0x01;
			public static final byte  DENY_NO_ENOUGH_SPACE 	= 0x02;
			public static final byte  DENY_IS_RECORDING 	= 0x03;
		}
		

        public static class P2PQueryTL{
	        // cmd: P2P_QUERY_TIME_LAPSE, P2P DENY CODE 
			public static final byte  DENY_TL_NOT_EXIST 	= 0x01;
			public static final byte  DENY_TL_IN_FUTURE 	= 0x02;
		}
		

        public static class P2PUploadFile{
			public static final byte  DENY_IS_UPLOADING 	= 0x01;
		}


        public static class P2PGetFile{
            // request code: values of StcFileType
        }
        


	}
	
	
	
	
	/**
	 * Type: bit[0-1] 
	 * 
	 * @author mancook
	 *
	 */
    public static class Service{
		// bits [0-1]
		// ---- --**
		// 0000 0000	-- not a request/allow pattern packets
		// 0000 0001	-- request
		// 0000 0010	-- allow
		// 0000 0011	-- deny
		//
		public static final Service NONE		=  new Service( (byte) 0x00);
		public static final Service REQUEST 	=  new Service( (byte) 0x01);
		public static final Service ALLOW 		=  new Service( (byte) 0x02);
		public static final Service DENY 		=  new Service( (byte) 0x03);
		
		private final byte v;
		private Service(byte s){
			this.v = s;
		}


		public String toString(){
			if(this == NONE){
				return "NONE";
			}
			if(this == REQUEST){
				return "REQUEST";
			}
			if(this == ALLOW){
				return "ALLOW";
			}
			if(this == DENY){
				return "DENY";
			}
			return "<unknown>";
		}
	}
	
	
	/**
	 * Type: bit[2-3] 
	 * 
	 * @author mancook
	 *
	 */
	public static class Flow{
		// bits [2-3]
		// ---- **--
		// 0000 0000	-- client to server
		// 0000 0100	-- server to client
		// 0000 1000	-- client to client
		// 0000 1100	-- (not used)
		public static final Flow CLIENT_TO_SERVER = new Flow((byte)0x00);
		public static final Flow SERVER_TO_CLIENT = new Flow((byte)0x04);
		public static final Flow CLIENT_TO_CLIENT = new Flow((byte)0x08);

		private final byte v;
		private Flow(byte f){
			this.v = f;
		}


		public String toString(){
			if(this == CLIENT_TO_SERVER){
				return "CLIENT_TO_SERVER";
			}
			if(this == SERVER_TO_CLIENT){
				return "SERVER_TO_CLIENT";
			}
			if(this == CLIENT_TO_CLIENT){
				return "CLIENT_TO_CLIENT";
			}
			return "<unknown>";
		}
	}
	
	
	/**
	 * <p> Based on org.linphone.core.LinphoneCall.State 
	 * 
	 * @author mancook
	 *
	 */
    public static class Command extends ChuyuObj
	{
		// ---------------------------------------------
		// non-static members
		// ---------------------------------------------
		
		private final short value;
		private final String name;
		
		private Command(int v, String n){
			value =(short) v;
			name = n;
			values.addElement(this);
			stLog.trace("Construct Command object: " + name + "(" + value + ")");
		}

		public String toString(){
			return name;
		}
		
		
		// ---------------------------------------------
		// STATIC MEMBERS 
		// ---------------------------------------------
		public static StringBuffer dumpAll(){
			StringBuffer sbuf = new StringBuffer(256);
			sbuf.append("---- All defined commands ----");
			for(Command cmd: values){
				sbuf.append("\n    ").append(cmd.toString() + "[" + cmd.value + "]");
			}
			return sbuf;
		}
		
		private static Vector<Command>  values = new Vector<>();
		public static Command fromInt(int v){
			for (int i=0; i<values.size();i++) {
				Command s = values.get(i);
				if(s.value == v) return s;
			}
			throw new RuntimeException("Command value [" + v + "] is NOT defined! \n" + dumpAll() );
		}
		
		
	    // =====================================================================
        // ====  CMD value 0x0000 ~ 0x0FFF: reserved  
        // ====  CMD value 0x1000 ~ 0x7FFF: for packet between client <---> server  
        // ====  CMD value 0x8000 ~ 0xFFFF: for P2P commands 
        // =====================================================================
		

		public static final Command None 			= new Command(0x00, "CMD_NONE");
		public static final Command PublicAddr 		= new Command(0x01, "CMD_PUBLIC_ADDR");
		public static final Command AdminGetInfo 	= new Command(0x02, "CMD_ADMIN_GET_INFO");
		public static final Command PreLoginOpr	 	= new Command(0x03, "CMD_PRE_LOGIN_OPR"); 
		
		public static final Command ResetPasswd	 	= new Command(0x04, "CMD_RESET_PASSWD"); 
		
		
		
		// REQUEST: client -> server 
		public static final Command Signup 			= new Command(0x0100, "CMD_SIGNUP");
		public static final Command Signout 		= new Command(0x0101, "CMD_SIGNOUT");
		public static final Command QueryGwInWifi 	= new Command(0x0102, "CMD_QUERY_GW_IN_WIFI");
		public static final Command ApplyForMaster 	= new Command(0x0103, "CMD_APPLY_FOR_MASTER");
		
		public static final Command Login 			= new Command(0x0104, "CMD_LOGIN");
		public static final Command Logout 			= new Command(0x0105, "CMD_LOGOUT");
		public static final Command ClientAQueryB 	= new Command(0x0106, "CMD_CLIENT_A_QUERY_B");
		public static final Command CltStatus 		= new Command(0x0107, "CMD_CLT_STATUS");
		
		public static final Command SearchContact 	= new Command(0x0108, "CMD_SEARCH_CONTACT");
		public static final Command AddContact 		= new Command(0x0109, "CMD_ADD_CONTACT");
		public static final Command DelContact 		= new Command(0x010A, "CMD_DEL_CONTACT");
		public static final Command EditInfo 		= new Command(0x010B, "CMD_EDIT_INFO");
		
		public static final Command MessageToSrv 	= new Command(0x010C, "CMD_MESSAGE_TO_SRV");
		public static final Command SmsVerifyCode 	= new Command(0x010D, "CMD_SMS_VERIFY_CODE");
		public static final Command QueryFriends 	= new Command(0x010E, "CMD_QUERY_FRIENDS");
		public static final Command SlaveDelContact = new Command(0x010F, "CMD_SLAVE_DEL_CONTACT");
		
		public static final Command PreLoginQuery 	= new Command(0x0110, "CMD_PRE_LOGIN_QUERY");
		public static final Command QueryDevInWifi 	= new Command(0x0111, "CMD_QUERY_DEV_IN_WIFI");
		public static final Command SlaveManage 	= new Command(0x0112, "CMD_SLAVE_EDIT_INFO");

		
		// REQUEST: server -> client
		public static final Command YouLogout 		= new Command(0x200, "CMD_YOU_LOGOUT");
		public static final Command SrvCheckClt 	= new Command(0x201, "CMD_SRV_CHECK_CLT");
		public static final Command SrvUpdateClt 	= new Command(0x202, "CMD_SRV_UPDATE_CLT");
		public static final Command SrvUpdateB 		= new Command(0x203, "CMD_SRV_UPDATE_B");
		
		public static final Command SrvMessageToClt = new Command(0x204, "CMD_SRV_MESSAGE_TO_CLT");

		
		// REQUEST: client -> client
		public static final Command P2pPing 			= new Command(0x8000, "CMD_P2P_PING");
		public static final Command P2pData 			= new Command(0x8001, "CMD_P2P_DATA"); // ???


		// Android/iOS P2P commands
		public static final Command P2pCallStart 		= new Command(0x8010, "CMD_P2P_CALL_START");
		public static final Command P2pCallChangeParm 	= new Command(0x8011, "CMD_P2P_CALL_CHANGE_PARAM");		// e.g. user -> GW: change camera during a video-call
		public static final Command P2pCallEnd 			= new Command(0x8012, "CMD_P2P_CALL_END");
		public static final Command P2pQueryTimelapse 	= new Command(0x8013, "CMD_P2P_QUERY_TIMELIPSE");
		
		public static final Command P2pUploadFile 		= new Command(0x8014, "CMD_P2P_UPLOAD_FILE");	
		public static final Command P2pQueryState 		= new Command(0x8015, "CMD_P2P_QUERY_STATE");
		public static final Command P2pRecordStart 		= new Command(0x8016, "CMD_P2P_RECORD_START");
		public static final Command P2pRecordStop 		= new Command(0x8017, "CMD_P2P_RECORD_STOP");
		
		public static final Command P2pCallGetRec 		= new Command(0x8018, "CMD_P2P_CALL_GET_REC");
		public static final Command P2pRestartApp 		= new Command(0x8019, "CMD_P2P_RESTART_APP");
		public static final Command P2pQueryFileCache	= new Command(0x801A, "CMD_P2P_QUERY_FILE_CACHE");
		public static final Command P2pGetFile			= new Command(0x801B, "CMD_P2P_GET_FILE");

		public static final Command P2pConn				= new Command(0x801C, "CMD_P2P_CONN");
		public static final Command P2pSetConfig		= new Command(0x801D, "CMD_P2P_SET_CONFIG");
		public static final Command P2pGetConfig		= new Command(0x801E, "CMD_P2P_GET_CONFIG");
		
		
		
		public boolean isPreLoginReq(){
			return this == Signup || this == Login || this == PreLoginQuery || this == SmsVerifyCode || this == ResetPasswd;
		}

	} // EOF: Command
	
	
    public static class Const{
        public static final byte VERSION_1_0        	= (byte)0x10;  	// PLAIN   packet
        public static final byte VERSION_1_1        	= (byte)0x11;	// ENCRYPT packet
        public static final int STRING_BLOCK_LEN 		= 64;
        public static final int ENC_HDR_LEN = 8;
        
        ////////////////////////////////////////////////////////////////////////
        //
        // CODE values in header of packet:
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        //
        // 1) REQUEST CODE: 
        //	  Current only AdminGetInfo request uses 'code' property.
        //
        // 2) ALLOW CODE:   TODO un-used, currently 
        //
        // 3) DENY CODE:
        //	  Used by most request to specify reason of DENY.
        //	  - 0x00: 			SUCCESS;
        // 	  - 0xF0 - 0xFF: 	COMMON ERROR. (reserved)
        //	  - (others):		DENY CODE for a type-specific packet. e.g. Login-deny packet.  
        //
        //////////////////////////////////////////////////////////////////////////
        
        // for all commands 
        //public static final byte  CODE_REQUEST_NULL 		= 0; 			// for an allow packet
        //public static final byte  CODE_ALLOW_NULL 		= 0; 			// for an allow packet
        //public static final byte  CODE_DENY_NULL 			= 0; 			// for an allow packet
        //public static final byte  CODE_DENY_NETWORK_ERROR 	= (byte) 0xfd; 	// NETWORK error
        //public static final byte  CODE_DENY_TIMEOUT 			= (byte) 0xfe; 	// TIMEOUT when waiting for the response
        //public static final byte  CODE_DENY_UNKNONW_ERROR 	= (byte) 0xff; 	// unknown error occurs
    }//end of class Const

	
	////////////////////////////////////////////////////////////////////////////
    // Instance Attributes
	////////////////////////////////////////////////////////////////////////////	
	protected Header 		header;
	
	
	////////////////////////////////////////////////////////////////////////////
    // Instance Methods
	////////////////////////////////////////////////////////////////////////////	
	
	protected void assertEnoughBytes(boolean condition, String msg) throws ExpReceiveTooFewBytes{
		if(!condition){
			throw new ExpReceiveTooFewBytes("NOT enough bytes! --  " + msg);
		}
	}
	
	
	/**
	 * <h2>Construct from a ByteBuffer. </h2>
	 * 
	 * <p> Receive a packet from input byte buffer. <br/>
	 * If no enough bytes, the position of input byte buffer is reset! 
	 * 
	 * <pre>
	 * Algorithm:
	 * - create header from the buffer; 
	 * - get packet length 'PKT_SIZE' in bytes from the header; 
	 * - copy PKT_SIZE bytes from input 'buffer', starting from the original position; 
	 * - set the 'finalized' bit;
	 * </pre>
	 * 
	 * <p> ATTENTION: DO NOT call 'froze' method when building from byte array! 
	 * Just set 'finalized' bit. <br/>
	 * Reason: 'froze' method is called when building a sending packet. 
	 * It modifies both header and buffer. 
	 * 
	 * 
	 * 
	 * @param pkt_buf
	 *
	 * @throws ExpReceiveTooFewBytes 
	 */
	StNetPacket(ByteBuffer pkt_buf) throws ExpReceiveTooFewBytes{
		stLog.trace(">>>> build StPkt from a ByteBuffer ");
		
		try{
			// ERROR CHECKING
			util.assertNotNull(pkt_buf, "Can build a packet from empty buffer!");
			assertEnoughBytes(pkt_buf.remaining() >= Header.OBJLEN, 
					"Buffer size (" + pkt_buf.remaining() + 
					") is smaller than packet's header size: " + Header.OBJLEN );

			int buf_start_pos  = pkt_buf.position();
			int buf_remaining  = pkt_buf.remaining();
			
			Header hdr = new Header();
			hdr.checksum 	= pkt_buf.get();
			hdr.version  	= pkt_buf.get();
			hdr.cmd			= pkt_buf.getShort();
			hdr.type		= pkt_buf.get();
			hdr.code		= pkt_buf.get();
			hdr.length		= (0xFFFF & pkt_buf.getShort());
			hdr.ssId		= new StRequestID(pkt_buf.getLong());
			hdr.srcClt		= new StClientID(pkt_buf.getLong());
			hdr.dstClt		= new StClientID(pkt_buf.getLong());
			
			// ERROR CHECKING 
			if(buf_remaining < hdr.length){
				pkt_buf.position(buf_start_pos);
				throw new ExpReceiveTooFewBytes("Length attribute in packet header (" + hdr.length + ") " +
						"IS BIGGER THAN remaining of input buffer (" + buf_remaining + ")");
			}
					
			this.header = hdr;
			this.buffer = ByteBuffer.allocate(hdr.length);
			pkt_buf.position(buf_start_pos);
			pkt_buf.get(this.buffer.array());
			
			//this.positionAtData();
			this.buffer.rewind();
			this.finalized = true;
		}finally{
			stLog.trace("<<<<");
		}
	}
	
	 
	/**
	 *
	 * 
	 * @param src_clt The source node (client or server), which sends this packet.
	 * <p> dst-clt and src-clt must match with packet flow in TYPE attributes.
	 * <p> ... (see 'FLOW checking' in packet constructor) ...
	 * 
	 * @param dst_clt The destination node (client or server) which this packet will reach.
	 * 
	 * @param data_buf packet data
	 * @
	 */
	StNetPacket(
			final Command cmd, final byte code, final Service s, final Flow f, 
			final StRequestID ss_id, 
			final StClientID src_clt, final StClientID dst_clt,  
			ByteBuffer data_buf )   
	{
		final int pkt_len = (Header.OBJLEN + (data_buf==null ? 0 :data_buf.remaining()) );

		// [2017-11-7]: keep this log to track packet building error
		stLog.trace("construct net packet: " + cmd + ", [C]" + code + ", [S]" + s + ", [F] " + f
				+ ",  " + src_clt + " --> " + dst_clt );


		// TODO: throw a normal exception, not a runtime-exception.
		util.assertTrue( pkt_len <= StConst.PKT_MAX_LENGTH, 
				"Packet length must between [" + Header.OBJLEN+"," +  StConst.PKT_MAX_LENGTH  + "]. " +
				"But it is " + pkt_len );
		
		this.header = new Header();
		this.header.cmd	 = cmd.value;
		this.header.code = code;
		this.header.type = (byte) ( s.v | f.v);
		if(src_clt != null){
			this.header.srcClt	= src_clt;
		}
		if(dst_clt != null){
			this.header.dstClt	= dst_clt;
		}
		if(ss_id != null){
			this.header.ssId	= ss_id;
		}
		
		this.buffer = ByteBuffer.allocate(pkt_len);
		this.positionAtData();		// HEADER part of the packet buffer is NOT written now. It is written in froze method.
		this.put(data_buf);  		// copy data from input 'data_buf' into packet buffer
		
		// checking packet FLOW 
		if( cmd.isPreLoginReq() ||
			cmd == Command.PublicAddr || 
			cmd == Command.SrvCheckClt )
		{
			// These packets are NOT checked.
			return;
		}
		
		if(this.isTypeFlowFromClientToSrv()){
			util.assertTrue(! header.srcClt.equalWith(StClientID.GEN_ID), "SRC client ID("+ header.srcClt.toString() +") is NULL!"); 
			util.assertTrue(  header.dstClt.equalWith(StClientID.GEN_ID), "DST client ID("+ header.dstClt.toString() +") is NOT NULL!"); 
		}
		if(this.isTypeFlowFromSrvToClient() ){
			util.assertTrue( header.srcClt.equalWith(StClientID.GEN_ID), "SRC client ID("+ header.srcClt.toString() +") is NOT NULL!"); 
			util.assertTrue( !header.dstClt.equalWith(StClientID.GEN_ID), "DST client ID("+ header.dstClt.toString() +") is NULL!"); 
		}
		// NOTE: although the dest-client is NOT-NULL, this packet is still sent to server in RELAY mode.
		if(this.isTypeFlowFromClientToClient()){
			util.assertTrue( !header.srcClt.equalWith(StClientID.GEN_ID), "SRC client ID("+ header.srcClt.toString() +") is NULL!"); 
			util.assertTrue( !header.dstClt.equalWith(StClientID.GEN_ID), "DST client ID("+ header.dstClt.toString() +") is NULL!"); 
		}
	}
	
	
	
	/**
	 * <h2>Construct from sub-class </h2>
	 * 
	 * @param pkt0
	 */
	public StNetPacket(StNetPacket pkt0) {
		this.header = pkt0.header;
		this.buffer = pkt0.buffer;
		this.finalized = pkt0.finalized;
	}
	
	
    /**
     * encrypt the buffer of a frozen packet.
     * packet object is NOT modified. 
     * 
     * starting 8 bytes:
     * [0]   checksum
     * [1]   version
     * [2-3] total length of the encrypted packet (HDR + encrypted data);
     * [4-7] (reserved)
     * 
     * @return
     */
    public ByteBuffer encrypt(){
    	util.assertTrue(isFrozen(), "packet must be frozen when encrypted");
    	ByteBuffer enc_buf = null;
    	try {
    		byte[] enc_data = StBasicCipher.instance().encrypt(buffer.array(), 0, buffer.limit());
    		enc_buf = ByteBuffer.allocate(enc_data.length + Const.ENC_HDR_LEN );
    		enc_buf.put((byte)0);
    		enc_buf.put(Const.VERSION_1_1);
    		enc_buf.putShort((short) (enc_data.length + Const.ENC_HDR_LEN) );
    		enc_buf.putInt(0); 
    		enc_buf.put(enc_data);
    		enc_buf.rewind();
    		//stLog.trace("encrypt packet success ---- len(raw/enc_data/enc_buf): " +
    		//			buffer.limit() + "/" + enc_data.length + "/" + enc_buf.limit() );
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
			stLog.error(util.getExceptionDetails(e, "fail to encrypt the packet!"));
		}
    	return enc_buf;
    }
    
	
    public ByteBuffer getBuffer(){
    	util.assertTrue(isFrozen(), "packet is NOT frozen!");
    	
    	if(header.version == Const.VERSION_1_0){
    		stLog.debug("this is a PLAIN packet!");
    		return this.buffer;
    	}
    	else if (header.version == Const.VERSION_1_1){
    		//stLog.warn("## ENCRYPTED packet");
    		return encrypt();
    	}
    	else{
    		throw new StErrUserError("unsupported packet version: " + header.version );
    	}
    }
    
	
	/**
	 * Get Client Info from data of current packet. <br/><br/>
	 * 
	 * NOTE: In super class StPkt, dataGetXXX method accesses DATA in the packet buffer. <br/>
	 * 
	 * @param offset
	 * @return
	 */
	public StClientInfo dataGetClientInfo(int offset)
    {
    	util.assertTrue( this.isFrozen(), "DO NOT get data from an un-frozen  packet");
    	this.positionAtData();
    	this.posMove(offset);
    	return new StClientInfo(this.buffer);
    }
    
    
    public StClientID dataGetClientID(int offset) {
    	StClientID id = new StClientID (this.dataGetLongID(offset).getId());
    	return new StClientID(id.getId());
    }
    
    
    public int dataGetInt(int offset){
    	return super.dataGetInt(offset);
    }
    
    
    public String dataGetString(int offset){
    	short len = this.dataGetShort(offset);
      	this.positionAtData();
    	this.posMove(offset+2);
    	return new String(this.getFixedBytes(len));
    }
    
    
    public String dataGetEncString(int offset){
    	ByteBuffer buffer = this.dataGetBufferBlock(offset,  StCoder.N_ENC_STR_LEN);
		return StCoder.getInstance().decString64(buffer);
    }
    
    
    public ArrayList<StClientID> dataGetIdListB() {
		ArrayList<StClientID> list = new ArrayList<>();
		int count = this.dataGetShort(0);
		util.assertTrue( count <= MAX_CLIENT_B_COUNT, "too many B objects");
		for(int i=0; i<count; i++){
			list.add(new StClientID(this.buffer.getLong()));
		}
		return list;
	}
	
    public ArrayList<StClientInfo> dataGetClientInfoListB() {
		ArrayList<StClientInfo> list = new ArrayList<>();
		int count = this.dataGetShort(0);
		util.assertTrue(count<= MAX_CLIENT_B_COUNT, "too many B objects");
		for(int i=0; i<count; i++){
			list.add(new StClientInfo(this.buffer));
		}
		return list;
	}
	
    
    /**
     * <h2> Build  ALLOW packet, with default 'code' value </h2>
     * 
     * @param data - NULL if no date
     */
    public StNetPacket buildAlw( ByteBuffer data){
    	return this.buildResponse(Service.ALLOW, Code.NONE, data);
    }
    
    
    public StNetPacket buildAlw(byte code, ByteBuffer data){
    	return this.buildResponse(Service.ALLOW, code, data);
    }
    
    
    public StNetPacket buildResponse(Service s, byte code,  ByteBuffer data){
    	Flow f ;
    	if(this.isTypeFlowFromClientToSrv()){
    		f = SERVER_TO_CLIENT;
    	}
    	else if(this.isTypeFlowFromClientToClient()){
    		f = CLIENT_TO_CLIENT;
    	}
    	else if(this.isTypeFlowFromSrvToClient()){
    		f = CLIENT_TO_SERVER;
    	}
    	else{
    		throw new StErrUserError("This packet is NOT correct!");
    	}
    	
    	if(s != Service.ALLOW && s!= Service.DENY){
    		throw new StErrUserError("Sevice response must be DENY or ALLOW!");
    	}
	
    	return StNetPacket.build(this.getCmd(), s, f, code, 
    			this.getRequestId(), 
    			this.getDstClientId(), this.getSrcClientId(), 
    			data);
    }
    
    
    /**
     * <h2> Build the server DENY packet. </h2>
     * @param data
     * @return
     * @throws StExpUserError
     */
    public StNetPacket buildDny(final byte code, final ByteBuffer data){
    	return this.buildResponse(Service.DENY, code, data);
    }
    public StNetPacket buildDny(final byte code){
    	return this.buildResponse(Service.DENY, code, null);
    }
    public StNetPacket buildDny(final ByteBuffer data){
    	return this.buildResponse(Service.DENY, Code.NONE, data);
    }
    
	// ------------------------------------------------------------------------------
	// Type Checkers and setters
    // - flow mask: 0000 1100 (0x0C)
    // - req-res mask: 0000 0011 (0x03)
	// ------------------------------------------------------------------------------

	public boolean isTypeNoReq(){
        return (this.header.type & 0x03) == 0x00;
    }
	public boolean isTypeRequest(){
        return (this.header.type & 0x03) == 0x01;
    }
	public boolean isTypeResponseAllow(){
        return (this.header.type & 0x03) == 0x02;
    }
	public boolean isTypeResponseDeny(){
        return (this.header.type & 0x03) == 0x03;
    }
	
	
	public boolean isTypeFlowFromClientToSrv(){
        return ((this.header.type & 0x0C) >> 2) == 0x00;
    }
	public boolean isTypeFlowFromSrvToClient(){
        return ((this.header.type & 0x0C) >> 2) == 0x01;
    }
	public boolean isTypeFlowFromClientToClient(){
        return ((this.header.type & 0x0C) >> 2) == 0x02;
    }
	public boolean isTypeFlowFromAny(){
        return ((this.header.type & 0x0C) >> 2) == 0x03;
    }
	
	public StClientID getSrcClientId(){
		// clone the source client ID
		return new StClientID(this.header.srcClt.getId());
	}
	public StClientID getDstClientId(){
		// clone the dest client ID
		return new StClientID(this.header.dstClt.getId());
	}
	
	
	/**
	 * @return
	 */
	public StRequestID getRequestId(){
		return new StRequestID(header.ssId.getId());
	}
	
	/**
	 * @deprecated set request ID when building 
	 *  
	 * @param ss_id
	 */
	public void setRequestId(StRequestID ss_id) {
		util.assertTrue(!this.isFrozen(), "DO NOT change an frozen packet!");
		util.assertTrue(isTypeRequest(), "DO NOT set request ID in a NON-REQUEST packet!");
		util.assertTrue(header.ssId.equalWith(StRequestID.NULL_ID), "DO NOT set request ID, again!");
		header.ssId = ss_id;
	}
	
	
	/**
	 * get the header type string
	 * 
	 * @return
	 */
	public String getTypeStr(){
		StringBuilder sbuf = new StringBuilder(64);
		sbuf.append("[");
		int tt = ( (0xFF & this.header.type) & 0x0C ) >>> 2; // flow mask: 0000 1100 (0x0C)
		switch(tt){
		case 0x00:
			sbuf.append("clt->srv ");
			break;
		case 0x01:
			sbuf.append("srv->clt ");
			break;
		case 0x02:
			sbuf.append("clt->clt ");
			break;

		default:
			sbuf.append("<unknown-flow>");
		}

		tt = (this.header.type & 0xFF) & 0x03 ;  // req-res mask: 0000 0011 (0x03)
		switch(tt){
		case 0x00:
			sbuf.append("NONE");
			break;
		case 0x01:
			sbuf.append("REQ");
			break;
		case 0x02:
			sbuf.append("ALLOW");
			break;
		case 0x03:
			sbuf.append("DENY");
			break;
		default:
			sbuf.append("<??>");
			break;
		}
		
		sbuf.append("]");
		return sbuf.toString();
	}


	// ------------------------------------------------------------------------------
    // override methods
	// ------------------------------------------------------------------------------
	
	public int getDataLen() {
		return (this.header.length - Header.OBJLEN);
	}
	
	public int getLen(){
		return this.header.length;
	}
	
	/**
	 * <p> Get a new byte buffer with data of the packet. 
	 * Buffer position is put at the beginning.
	 * 
	 * @return
	 * @
	 */
	public ByteBuffer getDataBuffer() {
		if(this.getDataLen() == 0){
			return null;
		}
		return this.dataGetBufferBlock(0, this.getDataLen());
	}

	/**
	 * <h2> Get user defined code. </h2>
	 * @return
	 */
	public byte getCode(){
		return this.header.code;
	}
	
	/**
	 * Set user defined attribute 'code'. <br/>
	 * 
	 * @param code
	 */
	public void setCode(byte code){
		this.header.code = code;
	}
	
	
	public StNetPacket.Command getCmd(){
		return Command.fromInt(this.header.cmd);
	}
	

	protected void assertClientRequest() {
		util.assertTrue(this.isTypeRequest() && this.isTypeFlowFromClientToSrv(), 
				"CANNOT call this method for current packet type: " + this.getTypeStr());
	}
	
	@Override
	protected void positionAtData() {
		// [2014-12-13] You can call this method on a frozen packet
		//		util.assertTrue(!this.isFrozen(), 
		//				"DO NOT move buffer position on a frozen packet.");
		this.buffer.position(Header.OBJLEN);
	}
    
	/**
	 * This method is called in 'froze' method in super class! 
	 * 
	 * 2016-9-5: seems useless, as writeHeaderToBuffer() does this job.
	 */
	@Override
	protected void updateLength() {
		this.header.length = buffer.position();
	}
	
	
	/**
     * Update [Theodore: 2014-11-13] <br/>
     * Write packet header attributes into the starting 32 bytes of the bytebuffer. <br/>
     *
     * ATTENTION: 
     *
     * 1. Attribute 'header.length' must be set to value of buffer.position() before
     * writing header into ByteBuffer object. 
     *
     * 2. The 1st byte in ByteBuffer is for 'checksum' attribute, which is 
     * NOT calculated in this method. You can ignore it here. 
     * 
     * 3. buffer position points to the staring of DATA. 
     * 
     *          
     *                   /--------------------------------- header      
     *                  /
     *                 /         
     * [[ HEADER Attributes ]]   
     *     ||					  /---- <position at end> 
     *     ||                    /                  
     *    \||/                  |                   
     *     \/                   V                  
     * [xx xx xx xx xx xx xx xx][xx xx xx xx xx xx xx xx xx xx ... ]
     * |                       ||                                  |
     * |       HEADER          ||           DATA                   |
     * |<---- 32 bytes  ------>||<------- (data size) ------------>|
     * |                                                           |
     * |----------------- PACKET BUFFER ---------------------------|
     *          
     */ 
	@Override
	protected void writeHeaderToBuffer() {
        // update 'header.length' according to position in ByteBuffer
        this.header.length = buffer.position();

        this.buffer.position(0);
        this.buffer.put(this.header.checksum);
        this.buffer.put(this.header.version);
        this.buffer.putShort(this.header.cmd);
        
        this.buffer.put(this.header.type);
        this.buffer.put(this.header.code);
        this.buffer.putShort((short) (header.length & 0xFFFF));
        
        this.buffer.putLong(this.header.ssId.getId());
        this.buffer.putLong(this.header.srcClt.getId());
        this.buffer.putLong(this.header.dstClt.getId());
        
        // 2014-11-18 : restore buffer position (from cn.teclub.ha.net.StCloudPkt)
        //this.buffer.position(this.header.length);
	}
	
	
	public boolean isStatusChecking(){
		return (this.getCmd() == Command.SrvCheckClt || this.getCmd() == Command.CltStatus);
	}
	
	
	/**
	 * <p> Log the RECEIVING socket flow, when receiving a packet from socket, in the recv-from-server thread;
	 * 
	 * <p> Log the SENDING socket flow, when sending a packet via s socket.
	 * 
	 * @param is_out 
	 * @param is_detail - dump the whole net packet
	 * @return
	 */
	public StringBuffer makeSocketFlow(boolean is_out, boolean is_detail){
		StringBuffer sbuf = new StringBuffer(256);
		sbuf.append("\n\t ");
		if(is_out){
			sbuf.append("[Network Flow] >>>>>>>>");
		}else{
			sbuf.append("[Network Flow] <<<<<<<<");
		}
		sbuf.append(": ");
		sbuf.append(this.toString());
		if(is_detail){
			sbuf.append("\n\t #### Packet Details #### \n");
			if( isStatusChecking() ){
				sbuf.append(dumpSimple());
			}else{
				sbuf.append(dump() );
			}
		}
		return sbuf;
	}
	
	
	
	/**
	 * <p> Log the RECEIVING logic flow, when handling RecvFromServer and RecvFromP2p event. 
	 * 
	 * <p> Log the SENDING logic flow, when ???
	 * 
	 * 
	 * 
	 * @param is_out
	 * @param is_detail
	 * @return
	 */
	public StringBuffer makeLogicFlow(boolean is_out, boolean is_detail){
		StringBuffer sbuf = new StringBuffer(256);
		sbuf.append("\n\t ");
		String flow_arrow;
		if(is_out){
			flow_arrow = "-->>"; 	// send arrow
			if(this.isTypeFlowFromClientToClient()){
				sbuf.append("[Logic Flow ] C "+ flow_arrow +" C");
			}else if(this.isTypeFlowFromClientToSrv()){
				sbuf.append("[Logic Flow ] C "+ flow_arrow +" S");
			}else if(this.isTypeFlowFromSrvToClient()){
				sbuf.append("[Logic Flow ] S "+ flow_arrow +" C");
			}else{
				sbuf.append("[Logic Flow ] <NONE> "+ flow_arrow +" <NONE>");
			}
		}else{
			flow_arrow = "<<--"; 	// receive arrow
			if(this.isTypeFlowFromClientToClient()){
				sbuf.append("[Logic Flow ] C "+ flow_arrow +" C");
			}else if(this.isTypeFlowFromClientToSrv()){
				sbuf.append("[Logic Flow ] S "+ flow_arrow +" C");
			}else if(this.isTypeFlowFromSrvToClient()){
				sbuf.append("[Logic Flow ] C "+ flow_arrow +" S");
			}else{
				sbuf.append("[Logic Flow ] <NONE> "+ flow_arrow +" <NONE>");
			}
		}
		
		sbuf.append(": ");
		sbuf.append(this.toString());
		if(is_detail){
			sbuf.append("\n\t #### Packet Details #### \n");
			if( isStatusChecking() ){
				sbuf.append(dumpSimple());
			}else{
				sbuf.append(dump());
			}
		}
		return sbuf;
	}
	
	
	
	/**
	 * <h2>Log the socket packet flow.</h2>
	 * 
	 * @param is_out
	 * @param is_detail
	 * 
	 * 
	 * @deprecated call log4j at the real place
	 */
	void logSocketFlow22(boolean is_out, boolean is_detail){
		if(	this.getCmd() == Command.CltStatus  || this.getCmd() == Command.SrvCheckClt ){
			return;
		}
		
		StringBuffer sbuf = new StringBuffer(256);
		if(is_out){
			sbuf.append("[Socket Flow] >>>>>>>>");
		}else{
			sbuf.append("[Socket Flow] <<<<<<<<");
		}
		sbuf.append(": ");
		sbuf.append(this.toString());
		if(is_detail){
			sbuf.append("\n\t #### Packet Details #### \n");
			sbuf.append(dump());
		}
		stLog.info(sbuf);
	}
	
	
	/**
	 * <h2>Dump flow of a network packet.</h2>
     * 
     * <p> Such packets are excluded: CMD_CLT_STATUS, CMD_SRV_CHECK_CLT 
	 * 
	 * @param is_out
	 * @param is_detail
	 * 
	 * @deprecated call log4j at the real place
	 */
	void logLogicFlow22(boolean is_out, boolean is_detail){
		if(	this.getCmd() == Command.CltStatus  || this.getCmd() == Command.SrvCheckClt ){
			return;
		}
				
		StringBuffer sbuf = new StringBuffer(256);
		String flow_arrow;
		if(is_out){
			flow_arrow = "---->>>>"; 	// send arrow
			if(this.isTypeFlowFromClientToClient()){
				sbuf.append("[Logic Flow] Client "+ flow_arrow +" Client");
			}else if(this.isTypeFlowFromClientToSrv()){
				sbuf.append("[Logic Flow] Client "+ flow_arrow +" Server");
			}else if(this.isTypeFlowFromSrvToClient()){
				sbuf.append("[Logic Flow] Server "+ flow_arrow +" Client");
			}else{
				sbuf.append("[Logic Flow] <NONE> "+ flow_arrow +" <NONE>");
			}
		}else{
			flow_arrow = "<<<<----"; 	// receive arrow
			if(this.isTypeFlowFromClientToClient()){
				sbuf.append("[Logic Flow] Client "+ flow_arrow +" Client");
			}else if(this.isTypeFlowFromClientToSrv()){
				sbuf.append("[Logic Flow] Server "+ flow_arrow +" Client");
			}else if(this.isTypeFlowFromSrvToClient()){
				sbuf.append("[Logic Flow] Client "+ flow_arrow +" Server");
			}else{
				sbuf.append("[Logic Flow] <NONE> "+ flow_arrow +" <NONE>");
			}
		}
		
		sbuf.append(": ");
		sbuf.append(this.toString());
		if(is_detail){
			sbuf.append("\n\t #### Packet Details #### \n");
			sbuf.append(dump());
		}
		stLog.info(sbuf);
	}
	
	
	
	public String toString(){
		String PREFIX = header.version == Const.VERSION_1_0 ? "[PLAIN]" : "[ENC]";
        return  PREFIX + getTypeStr() +
                getCmd() +
                ",Code:0x" + util.to2CharHex(this.header.code) +
                "," + header.ssId +
                "," + header.length + "B; ";
	}
	
	
	
	public StringBuffer dump22(){
		StringBuffer sbuf = new StringBuffer(256);
		util.dumpFunc.addDumpStartLine(sbuf, " {StNetPaket} ");
		util.dumpFunc.addDumpLine(sbuf, "Net Packet Header: ");
		util.dumpFunc.addDumpLine(sbuf, "     Cmd:     " + this.getCmd());
		util.dumpFunc.addDumpLine(sbuf, "     Code:    " + util.to2CharHex(this.header.code));
		util.dumpFunc.addDumpLine(sbuf, "     Type:    " + this.getTypeStr());
		util.dumpFunc.addDumpLine(sbuf, "     Length:  " + this.header.length);
		util.dumpFunc.addDumpLine(sbuf, "     Src Clt: " + this.header.srcClt );
		util.dumpFunc.addDumpLine(sbuf, "     Dst Clt: " + this.header.dstClt );
		util.dumpFunc.addDumpLine(sbuf, "     Req ID : " + this.header.ssId );
		util.dumpFunc.addDumpLine(sbuf, "Net Packet Data: ");
		util.dumpFunc.addDumpLine(sbuf, "     Data Length: " + this.getDataLen() );
		util.dumpFunc.addDumpSectionLine(sbuf);
		util.dumpFunc.addDumpLine(sbuf, "Net Packet Buffer Info: ");
		util.dumpFunc.addDumpLine(sbuf, "     Capacity: " + this.buffer.capacity());
		util.dumpFunc.addDumpLine(sbuf, "     Limit:    " + this.buffer.limit());
		util.dumpFunc.addDumpLine(sbuf, "     Postion:  " + this.buffer.position());
		util.dumpFunc.addDumpLine(sbuf, "     Remaining:" + this.buffer.remaining());
		util.dumpFunc.addDumpLine(sbuf, "Net Packet Buffer Content: ");
		util.dumpFunc.addDumpAttributeString(sbuf, util.toCharHexBuf(this.buffer.array(), this.header.length));
		util.dumpFunc.addDumpEndLine(sbuf);
		return sbuf;
	}
	
	
	public StringBuffer dumpSimple(){
		StringBuffer sbuf = new StringBuffer(256);
		sbuf.append("\n\t ---------  Net Packet (Simple)  -------------------------"); 
		sbuf.append("\n\t   Command: " + getCmd() + ", Length:  " + header.length);
		sbuf.append("\n\t   Type:    " + getTypeStr());
		sbuf.append("\n\t   Src Clt:    " + this.getSrcClientId());
		sbuf.append("\n\t   Dst Clt:    " + this.getDstClientId());
		sbuf.append("\n\t   Session:    " + this.getRequestId());
		sbuf.append("\n\t ---------------------------------------------------------"); 
		sbuf.append("\n\t");
		return sbuf;
	}


	@Override
	public void dumpSetup() {
		dumpSetTitle(" { " + getClass() + " } ");
		dumpAddLine("Packet Header");
		dumpAddLine("  Cmd:     " + this.getCmd());
		dumpAddLine("  Code:    " + util.to2CharHex(this.header.code));
		dumpAddLine("  Type:    " + this.getTypeStr());
		dumpAddLine("  Length:  " + this.header.length);
		dumpAddLine("  Src Clt: " + this.header.srcClt );
		dumpAddLine("  Dst Clt: " + this.header.dstClt );
		dumpAddLine("  Req ID : " + this.header.ssId );
		dumpAddLine("Packet Data Length: " + this.getDataLen() );
		dumpAddLine("-------------------------------------------------");
		dumpAddLine("Data ByteBuffer Info" );
		dumpAddLine("  Capacity: " + this.buffer.capacity());
		dumpAddLine("  Limit:    " + this.buffer.limit());
		dumpAddLine("  Postion:  " + this.buffer.position());
		dumpAddLine("  Remaining:" + this.buffer.remaining());
		dumpAddLine(util.toCharHexBuf(this.buffer.array(), this.header.length).toString());
	}
}


