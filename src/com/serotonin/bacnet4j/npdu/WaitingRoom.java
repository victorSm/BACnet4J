package com.serotonin.bacnet4j.npdu;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.LinkedList;

import com.serotonin.bacnet4j.apdu.APDU;
import com.serotonin.bacnet4j.apdu.Abort;
import com.serotonin.bacnet4j.apdu.AckAPDU;
import com.serotonin.bacnet4j.apdu.ConfirmedRequest;
import com.serotonin.bacnet4j.apdu.Segmentable;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.exception.BACnetRuntimeException;
import com.serotonin.bacnet4j.exception.BACnetTimeoutException;
import com.serotonin.bacnet4j.exception.SegmentedMessageAbortedException;

public class WaitingRoom {
    private HashMap<Key, Member> waitHere = new HashMap<Key, Member>();
    
//    public AckAPDU waitForAck(InetSocketAddress peer, byte id, boolean server, long timeout,
//            boolean throwTimeout) throws BACnetException {
//        return (AckAPDU)waitForAPDU(peer, id, server, timeout, throwTimeout);
//    }
//    
//    public ConfirmedRequest waitForRequest(InetSocketAddress peer, byte id, boolean server, long timeout,
//            boolean throwTimeout) throws BACnetException {
//        return (ConfirmedRequest)waitForAPDU(peer, id, server, timeout, throwTimeout);
//    }
//    
//    public APDU waitForAPDU(InetSocketAddress peer, byte id, boolean server, long timeout,
//            boolean throwTimeout) throws BACnetException {
//        Key key = enter(peer, id, !server);
//        try {
//            return getAPDU(key, timeout, throwTimeout);
//        }
//        finally {
//            leave(key);
//        }
//    }
    
    public Key enter(InetSocketAddress peer, byte id, boolean server) {
        Member member = new Member();
        Key key = new Key(peer, id, !server);
        
        synchronized (waitHere) {
            if (waitHere.get(key) != null)
                throw new BACnetRuntimeException("Waiting room too crowded. key="+ key);
            waitHere.put(key, member);
        }
        
        return key;
    }
    
    
    public AckAPDU getAck(Key key, long timeout, boolean throwTimeout) throws BACnetException {
        return (AckAPDU)getAPDU(key, timeout, throwTimeout);
    }
    
    public ConfirmedRequest getRequest(Key key, long timeout, boolean throwTimeout) throws BACnetException {
        return (ConfirmedRequest)getAPDU(key, timeout, throwTimeout);
    }
    
    public Segmentable getSegmentable(Key key, long timeout, boolean throwTimeout) throws BACnetException {
        APDU apdu = getAPDU(key, timeout, throwTimeout);
        if (apdu instanceof Abort)
            throw new SegmentedMessageAbortedException((Abort)apdu);
        return (Segmentable)apdu;
    }
    
    public APDU getAPDU(Key key, long timeout, boolean throwTimeout) throws BACnetException {
        Member member = getMember(key);
        APDU apdu = member.getAPDU(timeout);
        if (apdu == null && throwTimeout)
            throw new BACnetTimeoutException("Timeout while waiting for APDU id "+ key.invokeId);
        return apdu;
    }
    
    public void leave(Key key) {
        synchronized (waitHere) {
            waitHere.remove(key);
        }
    }
    
    
    public void notifyMember(InetSocketAddress peer, byte id, boolean isFromServer, APDU apdu)
            throws BACnetException {
        Key key = new Key(peer, id, isFromServer);
        Member member = getMember(key);
        if (member != null) {
            member.setAPDU(apdu);
            return;
        }
        
        // The member may not have gotten around to listening for a message yet, so enter a retry loop to
        // make sure that this message gets to where it's supposed to go if there is somewhere to go.
        int attempts = 5;
        long sleep = 50;
        while (attempts > 0) {
            member = getMember(key);
            if (member != null) {
                member.setAPDU(apdu);
                return;
            }
            
            attempts--;
            try {
                Thread.sleep(sleep);
            }
            catch (InterruptedException e) {}
        }
        throw new BACnetException("No waiting recipient for message: peer="+ peer +", id="+ (id & 0xff)
                +", isFromServer="+ isFromServer +", message="+ apdu);
    }
    
    
    private Member getMember(Key key) {
        synchronized (waitHere) {
            return waitHere.get(key);
        }
    }
    
    
    
    /**
     * This class is used by network message controllers to manage the blocking of thread sending confirmed messages.
     * The instance itself serves as a monitor upon which the sending thread can wait (with a timeout). When a response
     * is received, the message controller can set it in here, automatically notifying the sending thread that the 
     * response is available.
     * 
     * @author mlohbihler
     */
    class Member {
        private LinkedList<APDU> apdus = new LinkedList<APDU>();
        
        synchronized void setAPDU(APDU apdu) {
            apdus.add(apdu);
            notify();
        }
        
        synchronized APDU getAPDU(long timeout) {
            // Check if there is an APDU available now.
            APDU result = apdus.poll();
            if (result != null)
                return result;
            
            // If not, wait the timeout and then check again.
            waitNoThrow(timeout);
            return apdus.poll();
        }
        
        private void waitNoThrow(long timeout) {
            try {
                super.wait(timeout);
            }
            catch (InterruptedException e) {
                // Ignore
            }
        }
    }
    
    public class Key {
        private InetSocketAddress peer;
        private byte invokeId;
        private boolean fromServer;
        
        public Key(InetSocketAddress peer, byte invokeId, boolean fromServer) {
            this.peer = peer;
            this.invokeId = invokeId;
            this.fromServer = fromServer;
        }

        public InetSocketAddress getPeer() {
            return peer;
        }

        public byte getInvokeId() {
            return invokeId;
        }

        public boolean isFromServer() {
            return fromServer;
        }
        
        public String toString() {
            return "Key(peer="+ peer +", invokeId="+ invokeId +", fromServer="+ fromServer +")";
        }

        @Override
        public int hashCode() {
            final int PRIME = 31;
            int result = 1;
            result = PRIME * result + ((peer == null) ? 0 : peer.hashCode());
            result = PRIME * result + (fromServer ? 1231 : 1237);
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final Key other = (Key) obj;
            if (peer == null) {
                if (other.peer != null)
                    return false;
            }
            else if (!peer.equals(other.peer))
                return false;
            if (invokeId != other.invokeId)
                return false;
            if (fromServer != other.fromServer)
                return false;
            return true;
        }
    }
}