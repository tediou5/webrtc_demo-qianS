package cn.teclub.ha.client.session;

import java.io.IOException;

import cn.teclub.ha.lib.StExpBreak;
import cn.teclub.ha.lib.StExpFamily;
import cn.teclub.ha.lib.StLoopThread;
import cn.teclub.ha.net.StClientID;
import cn.teclub.ha.net.StExpConnectionLoss;
import cn.teclub.ha.request.StNetPacket;
import cn.teclub.ha.request.StSocket;


/**
 * Receive packets from server. 
 * 
 * @author mancook
 *
 */
class StcRecvThread extends StLoopThread {
	private final StSocket sock;
    private final StcSessionComp    ssComp  = StcSessionComp.getInstance() ;

	
	StcRecvThread(StSocket sock){
		this.sock = sock;
	}

	
	@Override
	protected void loopOnce() throws StExpFamily {
		try {
			final StNetPacket pkt = sock.recvPacket();
			if(pkt == null){
				stLog.debug("No Packet is Received!");
				util.sleep(50);
				return;
			}
			if(pkt.getSrcClientId().equalWith(StClientID.GEN_ID) && pkt.isTypeFlowFromSrvToClient()){
				stLog.debug("Receive a packet from server");
				ssComp.addNewEvent(new StcEvtRecvFromSrv(pkt));
			}else if( !pkt.getSrcClientId().equalWith(StClientID.GEN_ID) && pkt.isTypeFlowFromClientToClient()){
				stLog.debug("Receive a P2P packet");

				// [Theodore: 2016-07-15] As only one instance of P2P Packet,
				// DO NOT send the event to different modules (threads). It can conflict!
				// Only send event to core-module, which passes the event to user-module,
				// if it does not handle it! 
				//
				//   modUser.addNewEvent(new RecvP2p(pkt));
                //
				ssComp.addNewEvent(new StcEvtRecvP2p(pkt));
			}else{
				stLog.error("Ignore unexpected Packet: " + pkt.dumpSimple());
			}
		} catch (IOException | StExpConnectionLoss e) {
			stLog.debug(util.getExceptionDetails(e, "Exception When Receive"));
			sock.close();

			// [2017-4-7] StcEvtRecvThreadStop event causes core pulse to call disconnect()
			// and sends InfoDisconnect event to RPR main pulse.
			// If core pulse is closing, DO NOT send this event.
			if(!ssComp.isClosing()) {
				ssComp.addNewEvent(new StcEvtRecvThreadStop());
            }
            stLog.info("RecvThread Ends");
			throw new StExpBreak(); // end recv-thread
		}
	}
}
