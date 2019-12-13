package cn.teclub.ha.client.rpr;

import cn.teclub.ha.request.StNetPacket;


/**
 * Client Service: for P2P request
 * 
 * @author mancook
 *
 */
public abstract class StcServiceP2p 
		extends StcService
{
	protected StcServiceP2p(final StNetPacket.Command cmd) {
		super(cmd);
	}
}
