package cn.teclub.ha3.net;


@SuppressWarnings("ALL")
public enum StJniNatType
{
    /**
     * NAT type is unknown because the detection has not been performed.
     */
    UNKNOWN_NAT,

    /**
     * NAT type is unknown because there is failure in the detection
     * process, possibly because server does not support RFC 3489.
     */
    ERR_UNKNOWN,

    /**
     * This specifies that the client has open access to Internet (or
     * at least, its behind a firewall that behaves like a full-cone NAT,
     * but without the translation)
     */
    OPEN,

    /**
     * This specifies that communication with server has failed, probably
     * because UDP packets are blocked.
     */
    BLOCKED,

    /**
     * Firewall that allows UDP out, and responses have to come back to
     * the source of the request (like a symmetric NAT, but no
     * translation.
     */
    SYMMETRIC_UDP,

    /**
     * A full cone NAT is one where all requests from the same internal 
     * IP address and port are mapped to the same external IP address and
     * port.  Furthermore, any external host can send a packet to the 
     * internal host, by sending a packet to the mapped external address.
     */
    FULL_CONE,

    /**
     * A symmetric NAT is one where all requests from the same internal 
     * IP address and port, to a specific destination IP address and port,
     * are mapped to the same external IP address and port.  If the same 
     * host sends a packet with the same source address and port, but to 
     * a different destination, a different mapping is used.  Furthermore,
     * only the external host that receives a packet can send a UDP packet
     * back to the internal host.
     */
    SYMMETRIC,

    /**
     * A restricted cone NAT is one where all requests from the same 
     * internal IP address and port are mapped to the same external IP 
     * address and port.  Unlike a full cone NAT, an external host (with 
     * IP address X) can send a packet to the internal host only if the 
     * internal host had previously sent a packet to IP address X.
     */
    RESTRICTED,

    /**
     * A port restricted cone NAT is like a restricted cone NAT, but the 
     * restriction includes port numbers. Specifically, an external host 
     * can send a packet, with source IP address X and source port P, 
     * to the internal host only if the internal host had previously sent
     * a packet to IP address X and port P.
     */
    PORT_RESTRICTED

}
