package controllers;

import play.*;
import play.mvc.*;
import play.libs.*;
import play.libs.F.*;
import play.mvc.Http.*;

import static play.libs.F.*;
import static play.libs.F.Matcher.*;
import static play.mvc.Http.WebSocketEvent.*;

import java.util.*;

import models.*;

public class Echo extends Controller {

    public static void demo() {
        render();
    }

    public static class WebSocketEcho extends WebSocketController {

        public static void listen() {

            Notification notification = Notification.get();
            EventStream<Notification.Event> eventStream = notification.join();

            while(inbound.isOpen()) {
                

                Either<WebSocketEvent, Notification.Event> e = await(Promise.waitEither(
                    inbound.nextEvent(), 
                    eventStream.nextEvent()
                ));

                for(String message: TextFrame.match(e._1)) {
                    notification.notify(message);
                }
                for(Notification.Message message: ClassOf(Notification.Message.class).match(e._2)) {
                    outbound.send(message.text);
                }
            }
        }
    }
}
