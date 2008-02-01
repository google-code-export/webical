package org.webical.web.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.webical.Event;

/**
 * Action to store one or multiple events
 * 
 * @author mattijs
 *
 */
public class StoreEventAction implements IAction {
	private static final long serialVersionUID = 1L;

	private List<Event> events = new ArrayList<Event>();
	private AjaxRequestTarget target;
	private boolean justStore;

	public StoreEventAction(Event eventToStore) {
		setEvent(eventToStore);
		setAjaxRequestTarget(null);
	}
	
	public StoreEventAction(List<Event> eventsToStore) {
		setEvents(eventsToStore);
		setAjaxRequestTarget(null);
	}

	public StoreEventAction(Event eventToStore, AjaxRequestTarget target) {
		this(eventToStore);
		setAjaxRequestTarget(target);
	}
	
	public StoreEventAction(List<Event> eventsToStore, AjaxRequestTarget target) {
		setEvents(eventsToStore);
		setAjaxRequestTarget(target);
	}

	public boolean storeMultiple() {
		return (this.events != null) && (this.events.size() > 1);
	}
	
	public Event getEvent() {
		return events.get(0);
	}
	
	public List<Event> getEvents() {
		return events;
	}

	public void setEvent(Event event) {
		if(this.events.contains(event)) {
			this.events.remove(event);
		}
		this.events.add(event);
	}
	
	public void setEvents(List<Event> events) {
		this.events = events;
	}

	public AjaxRequestTarget getAjaxRequestTarget() {
		return target;
	}

	public void setAjaxRequestTarget(AjaxRequestTarget target) {
		this.target = target;
	}
	
	public boolean isJustStore() {
		return justStore;
	}
	
	public void setJustStore(boolean justStore) {
		this.justStore = justStore;
	}
}
