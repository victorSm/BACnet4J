package com.serotonin.bacnet4j.type.eventParameter;

import com.serotonin.bacnet4j.type.constructed.DeviceObjectPropertyReference;
import com.serotonin.bacnet4j.type.primitive.Real;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;
import com.serotonin.util.queue.ByteQueue;

public class FloatingLimit extends EventParameter {
    public static final byte TYPE_ID = 4;
    
    private UnsignedInteger timeDelay;
    private DeviceObjectPropertyReference setpointReference;
    private Real lowDiffLimit;
    private Real highDiffLimit;
    private Real deadband;
    
    public FloatingLimit(UnsignedInteger timeDelay, DeviceObjectPropertyReference setpointReference, Real lowDiffLimit, Real highDiffLimit, Real deadband) {
        this.timeDelay = timeDelay;
        this.setpointReference = setpointReference;
        this.lowDiffLimit = lowDiffLimit;
        this.highDiffLimit = highDiffLimit;
        this.deadband = deadband;
    }

    protected void writeImpl(ByteQueue queue) {
        timeDelay.write(queue, 0);
        setpointReference.write(queue, 1);
        lowDiffLimit.write(queue, 2);
        highDiffLimit.write(queue, 3);
        deadband.write(queue, 4);
    }
    
    protected int getTypeId() {
        return TYPE_ID;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((deadband == null) ? 0 : deadband.hashCode());
        result = PRIME * result + ((highDiffLimit == null) ? 0 : highDiffLimit.hashCode());
        result = PRIME * result + ((lowDiffLimit == null) ? 0 : lowDiffLimit.hashCode());
        result = PRIME * result + ((setpointReference == null) ? 0 : setpointReference.hashCode());
        result = PRIME * result + ((timeDelay == null) ? 0 : timeDelay.hashCode());
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
        final FloatingLimit other = (FloatingLimit) obj;
        if (deadband == null) {
            if (other.deadband != null)
                return false;
        }
        else if (!deadband.equals(other.deadband))
            return false;
        if (highDiffLimit == null) {
            if (other.highDiffLimit != null)
                return false;
        }
        else if (!highDiffLimit.equals(other.highDiffLimit))
            return false;
        if (lowDiffLimit == null) {
            if (other.lowDiffLimit != null)
                return false;
        }
        else if (!lowDiffLimit.equals(other.lowDiffLimit))
            return false;
        if (setpointReference == null) {
            if (other.setpointReference != null)
                return false;
        }
        else if (!setpointReference.equals(other.setpointReference))
            return false;
        if (timeDelay == null) {
            if (other.timeDelay != null)
                return false;
        }
        else if (!timeDelay.equals(other.timeDelay))
            return false;
        return true;
    }
}