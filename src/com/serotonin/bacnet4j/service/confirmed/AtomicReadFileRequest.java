package com.serotonin.bacnet4j.service.confirmed;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.exception.NotImplementedException;
import com.serotonin.bacnet4j.service.acknowledgement.AcknowledgementService;
import com.serotonin.bacnet4j.type.constructed.Address;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.type.primitive.SignedInteger;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;
import com.serotonin.util.queue.ByteQueue;

public class AtomicReadFileRequest extends ConfirmedRequestService {
    public static final byte TYPE_ID = 6;
    
    private ObjectIdentifier fileIdentifier;
    private boolean recordAccess;
    private SignedInteger fileStartPosition;
    private UnsignedInteger requestedCount;
    
    public AtomicReadFileRequest(ObjectIdentifier fileIdentifier, boolean recordAccess, SignedInteger fileStartPosition, UnsignedInteger requestedCount) {
        this.fileIdentifier = fileIdentifier;
        this.recordAccess = recordAccess;
        this.fileStartPosition = fileStartPosition;
        this.requestedCount = requestedCount;
    }

    @Override
    public byte getChoiceId() {
        return TYPE_ID;
    }
    
    @Override
    public AcknowledgementService handle(LocalDevice localDevice, Address from) throws BACnetException {
        throw new NotImplementedException();
    }

    @Override
    public void write(ByteQueue queue) {
        write(queue, fileIdentifier);
        writeContextTag(queue, recordAccess ? 1 : 0, true);
        write(queue, fileStartPosition);
        write(queue, requestedCount);
        writeContextTag(queue, recordAccess ? 1 : 0, false);
    }
    
    AtomicReadFileRequest(ByteQueue queue) throws BACnetException {
        fileIdentifier = read(queue, ObjectIdentifier.class);
        recordAccess = popStart(queue) == 1;
        fileStartPosition = read(queue, SignedInteger.class);
        requestedCount = read(queue, UnsignedInteger.class);
        popEnd(queue, recordAccess ? 1 : 0);
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((fileIdentifier == null) ? 0 : fileIdentifier.hashCode());
        result = PRIME * result + ((fileStartPosition == null) ? 0 : fileStartPosition.hashCode());
        result = PRIME * result + (recordAccess ? 1231 : 1237);
        result = PRIME * result + ((requestedCount == null) ? 0 : requestedCount.hashCode());
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
        final AtomicReadFileRequest other = (AtomicReadFileRequest) obj;
        if (fileIdentifier == null) {
            if (other.fileIdentifier != null)
                return false;
        }
        else if (!fileIdentifier.equals(other.fileIdentifier))
            return false;
        if (fileStartPosition == null) {
            if (other.fileStartPosition != null)
                return false;
        }
        else if (!fileStartPosition.equals(other.fileStartPosition))
            return false;
        if (recordAccess != other.recordAccess)
            return false;
        if (requestedCount == null) {
            if (other.requestedCount != null)
                return false;
        }
        else if (!requestedCount.equals(other.requestedCount))
            return false;
        return true;
    }
}