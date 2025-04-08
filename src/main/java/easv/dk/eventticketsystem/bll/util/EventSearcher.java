package easv.dk.eventticketsystem.bll.util;

import easv.dk.eventticketsystem.be.Event;
import java.util.ArrayList;
import java.util.List;

public class EventSearcher {
    public List<Event> searchEvent(List<Event> searchBase, String query) {
        List<Event> searchResult = new ArrayList<>();
        for (Event event : searchBase) {
            if (compareToEventName(query, event))
            {
                searchResult.add(event);
            }
            else {
                System.out.println("No match found : " + event);
            }
        }
        return searchResult;
    }

    private boolean compareToEventName(String query, Event event) {
        return event.getEventName().toLowerCase().contains(query.toLowerCase());
    }
}
