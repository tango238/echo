package models;

import java.util.*;

import play.libs.*;
import play.libs.F.*;

public class Notification {
    
    final ArchivedEventStream<Notification.Event> messageEvents = new ArchivedEventStream<Notification.Event>(100);
    
    public EventStream<Notification.Event> join() {
        notify("join!");
        return messageEvents.eventStream();
    }

    /**
     */
    public void notify(String text) {
        if(text == null || text.trim().equals("")) {
            return;
        }
        messageEvents.publish(new Message(text));
    }
    
    /**
     */
    public Promise<List<IndexedEvent<Notification.Event>>> nextMessages(long lastReceived) {
        return messageEvents.nextEvents(lastReceived);
    }
    
    /**
     */
    public List<Notification.Event> archive() {
        return messageEvents.archive();
    }
    
    public static abstract class Event {
        
        final public String type;
        final public Long timestamp;
        
        public Event(String type) {
            this.type = type;
            this.timestamp = System.currentTimeMillis();
        }
        
    }
    
    public static class Message extends Event {
        
        final public String text;
        
        public Message(String text) {
            super("message");
            this.text = text;
        }
        
    }
    
    static Notification instance = null;

    public static Notification get() {
        if(instance == null) {
            instance = new Notification();
        }
        return instance;
    }
    
}

