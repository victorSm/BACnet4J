package com.serotonin.bacnet4j.type.constructed;

import java.util.ArrayList;
import java.util.List;

import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;
import com.serotonin.util.queue.ByteQueue;

public class SpecialEvent extends BaseType {
    private static List<Class<? extends Encodable>> classes;
    static {
        classes = new ArrayList<Class<? extends Encodable>>();
        classes.add(CalendarEntry.class);
        classes.add(ObjectIdentifier.class);
    }
    
    private Choice calendar;
    private SequenceOf<TimeValue> listOfTimeValues;
    private UnsignedInteger eventPriority;
    
    public SpecialEvent(CalendarEntry calendarEntry, SequenceOf<TimeValue> listOfTimeValues, 
            UnsignedInteger eventPriority) {
        calendar = new Choice(0, calendarEntry);
        this.listOfTimeValues = listOfTimeValues;
        this.eventPriority = eventPriority;
    }
    
    public SpecialEvent(ObjectIdentifier calendarReference, SequenceOf<TimeValue> listOfTimeValues, 
            UnsignedInteger eventPriority) {
        calendar = new Choice(1, calendarReference);
        this.listOfTimeValues = listOfTimeValues;
        this.eventPriority = eventPriority;
    }

    public void write(ByteQueue queue) {
        write(queue, calendar);
        write(queue, listOfTimeValues, 2);
        write(queue, eventPriority, 3);
    }
    
    public SpecialEvent(ByteQueue queue) throws BACnetException {
        calendar = new Choice(queue, classes);
        listOfTimeValues = readSequenceOf(queue, TimeValue.class, 2);
        eventPriority = read(queue, UnsignedInteger.class, 3);
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((calendar == null) ? 0 : calendar.hashCode());
        result = PRIME * result + ((eventPriority == null) ? 0 : eventPriority.hashCode());
        result = PRIME * result + ((listOfTimeValues == null) ? 0 : listOfTimeValues.hashCode());
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
        final SpecialEvent other = (SpecialEvent) obj;
        if (calendar == null) {
            if (other.calendar != null)
                return false;
        }
        else if (!calendar.equals(other.calendar))
            return false;
        if (eventPriority == null) {
            if (other.eventPriority != null)
                return false;
        }
        else if (!eventPriority.equals(other.eventPriority))
            return false;
        if (listOfTimeValues == null) {
            if (other.listOfTimeValues != null)
                return false;
        }
        else if (!listOfTimeValues.equals(other.listOfTimeValues))
            return false;
        return true;
    }
}