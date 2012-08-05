/*
 *    Webical - http://www.webical.org
 *    Copyright (C) 2007 Func. Internet Integration
 *
 *    This file is part of Webical.
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.webical.dao.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.UtcOffset;
import net.fortuna.ical4j.model.component.Daylight;
import net.fortuna.ical4j.model.component.Standard;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.Attach;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.Categories;
import net.fortuna.ical4j.model.property.Clazz;
import net.fortuna.ical4j.model.property.Comment;
import net.fortuna.ical4j.model.property.Contact;
import net.fortuna.ical4j.model.property.Created;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.ExDate;
import net.fortuna.ical4j.model.property.Geo;
import net.fortuna.ical4j.model.property.LastModified;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.Priority;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.RDate;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.RelatedTo;
import net.fortuna.ical4j.model.property.RequestStatus;
import net.fortuna.ical4j.model.property.Resources;
import net.fortuna.ical4j.model.property.Sequence;
import net.fortuna.ical4j.model.property.Status;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Transp;
import net.fortuna.ical4j.model.property.TzId;
import net.fortuna.ical4j.model.property.TzOffsetFrom;
import net.fortuna.ical4j.model.property.TzOffsetTo;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Url;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.model.property.XProperty;

import org.webical.Calendar;
import org.webical.Event;
import org.webical.util.CalendarUtils;

/**
 * This class takes care of conversions between Ical4J and webical calendar types
 * @author ivo
 *
 */
public class ComponentFactory {

	/**
	 * 
	 * @param calendar the calendar where the iCal4jCalendar has to parse his components in
	 * @param ical4jCalendar the calendar file to parse from the remote location
	 * @return the calendar with all the components from the remote calendar and sets the calendars timezone
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	public static List<Event> buildComponentsFromIcal4JCalendar(Calendar calendar, net.fortuna.ical4j.model.Calendar ical4jCalendar) throws ParseException  {
		List<Event> eventList = new ArrayList<Event>();
		
		for (Component component : (List<Component>)ical4jCalendar.getComponents()) {
			if(component.getName().equals(Component.VEVENT)) {
				eventList.add(buildEventFromComponent(calendar,component));
			} else if(component.getName().equals(Component.VTIMEZONE)) {
				Long offSet = buildOffSetFromIcal4JTimeZone((VTimeZone)component);

				calendar.setOffSetFrom(offSet);
			}
		}
		return eventList;
	}
	/**
	 * Retrieve the offset of a given VTimeZone
	 * @param vTimeZone the given timeZone
	 * @return Long offSet
	 */
	@SuppressWarnings("unchecked")
	public static Long buildOffSetFromIcal4JTimeZone(VTimeZone vTimeZone){
		
		Long offSet = null;
		
		for(Component comp : (List<Component>)vTimeZone.getObservances()){
			if (comp instanceof Standard) {
				for(Property property : (List<Property>)comp.getProperties()){
					if(property.getName().equals(Property.TZOFFSETFROM)){
						offSet = new Long(new UtcOffset(property.getValue()).getOffset() / CalendarUtils.getHourInMs());
					}
				}
			}
		}
		if(offSet == null){
			offSet = Long.valueOf(0);
		}
		return offSet;
	}
	
	/**
	 * Builds the Ical4J representation of a list of events
	 * @param eventList the events to add to the Ical4J calendar
	 * @return an Ical4J Calendar
	 * @throws IOException
	 * @throws URISyntaxException
	 * @throws ParseException
	 */
	public static net.fortuna.ical4j.model.Calendar buildIcal4JCalendarFromComponents(List<Event> eventList, Calendar calendar) throws IOException, URISyntaxException, ParseException{
		ComponentList componentList = new ComponentList();
		for (Event event : eventList){
			VEvent vevent = buildVEventFromEvent(event);
			
			componentList.add(vevent);	
		}
		VTimeZone vTimeZone = buildVTimeZoneFromTimeZone(calendar);
		
		componentList.add(vTimeZone);
		
		net.fortuna.ical4j.model.Calendar ical4jCalendar = new net.fortuna.ical4j.model.Calendar(componentList);
		ProdId prodId = new ProdId();
		prodId.setValue("-//Webical.org/NONSGML Webical Calendar V1.1//EN");
		ical4jCalendar.getProperties().add(prodId);
		Version version = new Version();
		version.setValue("2.0");
		ical4jCalendar.getProperties().add(version);
		return ical4jCalendar;
	}
	/**
	 * Return an ical VTIMEZONE component for an IcalCalendar
	 * @param calendar Use calendar arguments offsetfrom and offsetto
	 * @return a VTIMEZONE component
	 */
	public static VTimeZone buildVTimeZoneFromTimeZone(Calendar calendar){

		ComponentList componentList = new ComponentList();
		/*
		 * First construct a standard, required for VTimeZone
		 * , which is used to go from summer to winter
		 * */
		PropertyList standardPropertyList = new PropertyList();
		Standard standard = new Standard();
		
		DtStart dtStart = new DtStart();
		dtStart = new DtStart(new net.fortuna.ical4j.model.DateTime(new Date(0)));
		standardPropertyList.add(dtStart);
		
		Long offSet;
		if(calendar.getOffSetFrom() != null){
			offSet = calendar.getOffSetFrom();
		} else {
			offSet = new Long(0);
		}
		String str = buildIcalUtcOffsetString(offSet);		
		TzOffsetFrom offsetFrom = new TzOffsetFrom(new UtcOffset(str));
		standardPropertyList.add(offsetFrom);
		
		Long savings;
		if(calendar.getOffSetTo() != null){
			savings = calendar.getOffSetTo();
		} else if(calendar.getOffSetFrom() == null){
			savings = new Long(0);
		} else{
			savings = calendar.getOffSetFrom();
		}
		str = buildIcalUtcOffsetString(savings);	
		TzOffsetTo offsetTo = new TzOffsetTo(new UtcOffset(str));
		standardPropertyList.add(offsetTo);
		
		for(int i = 0; i < standardPropertyList.size(); i++){
			standard.getProperties().add(standardPropertyList.get(i));
		}		
		componentList.add(standard);
		
		/*Second construct a daylight, which is used to go from winter to summer
		 * So offsetfrom and offsetto are reverse
		 * */

		PropertyList daylightPropertyList = new PropertyList();
		Daylight daylight = new Daylight();
		
		dtStart = new DtStart();
		dtStart = new DtStart(new net.fortuna.ical4j.model.DateTime(new Date(0)));
		daylightPropertyList.add(dtStart);
		
		if(calendar.getOffSetFrom() != null){
			offSet = calendar.getOffSetFrom();
		} else {
			offSet = new Long(0);
		}
		str = buildIcalUtcOffsetString(offSet);	
		offsetTo = new TzOffsetTo(new UtcOffset(str));
		daylightPropertyList.add(offsetTo);
		
		if(calendar.getOffSetTo() != null){
			savings = calendar.getOffSetTo();
		} else if(calendar.getOffSetFrom() == null){
			savings = new Long(0);
		} else{
			savings = calendar.getOffSetFrom();
		}
		str = buildIcalUtcOffsetString(savings);	
		offsetFrom = new TzOffsetFrom(new UtcOffset(str));
		daylightPropertyList.add(offsetFrom);
		
		for(int i = 0; i < daylightPropertyList.size(); i++){
			
			daylight.getProperties().add(daylightPropertyList.get(i));
			
		}		
		componentList.add(daylight);
		
		/*Create a VTIMEZONE and add the standard and daylight component*/
		VTimeZone vTimeZone = new VTimeZone(componentList);		
		
		PropertyList vTimeZonePropertyList = new PropertyList();

		TzId tzId = new TzId("/webical.org/");
		vTimeZonePropertyList.add(tzId);
		
		for(int i = 0; i < vTimeZonePropertyList.size(); i++){			
			vTimeZone.getProperties().add(vTimeZonePropertyList.get(i));			
		}
		return vTimeZone;
	}
	
	private static String buildIcalUtcOffsetString(Long offSet){

		int rawOffset = (int) ((offSet.floatValue() * CalendarUtils.getHourInMs()) / 60000);
        int hours = rawOffset / 60;
        int minutes = Math.abs(rawOffset) % 60;
        String hrStr = "";

        if (Math.abs(hours) < 10) {
            if (hours < 0) {
                hrStr = "-0" + Math.abs(hours);
            } else {
                hrStr = "0" + Math.abs(hours);
            }
        } else {
            hrStr = Integer.toString(hours);
        }

        String minStr = (minutes < 10) ? ("0" + Integer.toString(minutes)) : Integer.toString(minutes);
        String str =((offSet >= 0) ? "+" : "") + hrStr + minStr;

		return str;
	}
	
	/**
	 * @param calendar the calendar where the component should be inserted
	 * @param component the component to be inserted in the calendar
	 * @return Event the event to be insterted in de calendar
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	public static Event buildEventFromComponent(Calendar calendar,Component component) throws ParseException {
		Event event = new Event();
		component.getProperties().getProperty(Property.ACTION);
		
		event.setCalendar(calendar);
		
		for(Property property : (List<Property>)component.getProperties()){
			
			if(property.getName().equals(Property.ATTACH)){
				event.getAttach().add(property.getValue());
			} else if(property.getName().equals(Property.ATTENDEE)) {
				event.getAttendee().add(property.getValue());
			} else if(property.getName().equals(Property.CATEGORIES)){
				event.getCategories().add(property.getValue());
			} else if(property.getName().equals(Property.COMMENT)){
				event.getComment().add(property.getValue());
			} else if(property.getName().equals(Property.CONTACT)){
				event.getContact().add(property.getValue());
			} else if(property.getName().equals(Property.EXDATE)){					
				Parameter parameter = property.getParameter("VALUE");		
				
				if(parameter != null && parameter.getValue().equals(Value.DATE.getValue())){
					event.getExDate().add(new Date(new net.fortuna.ical4j.model.Date(property.getValue()).getTime()));
				} else {
					event.getExDate().add(new Date(new net.fortuna.ical4j.model.DateTime(property.getValue()).getTime()));	
				}	
			} else if(property.getName().equals(Property.EXRULE)){
				event.getExRule().add(property.getValue());
			} else if(property.getName().equals(Property.REQUEST_STATUS)){
				event.getrStatus().add(property.getValue());
			} else if(property.getName().equals(Property.RELATED_TO)){
				event.getRelated().add(property.getValue());
			} else if(property.getName().equals(Property.RESOURCES)){
				event.getResources().add(property.getValue());
			} else if(property.getName().equals(Property.RDATE)){
				event.getrDate().add(new Date(new net.fortuna.ical4j.model.DateTime(property.getValue()).getTime()));
			} else if(property.getName().equals(Property.RRULE)){
				event.getrRule().add(property.getValue());
			} else if(property.getName().equals(Property.CLASS)){
				event.setClazz(property.getValue());
			} else if(property.getName().equals(Property.CREATED)){
				event.setCreated(new Date(new net.fortuna.ical4j.model.DateTime(property.getValue()).getTime()));
			} else if(property.getName().equals(Property.DESCRIPTION)){
				event.setDescription(property.getValue());
			} else if(property.getName().equals(Property.DTSTART)){					
				Parameter parameter = property.getParameter("VALUE");				
				
				if(parameter != null && parameter.getValue().equals(Value.DATE.getValue())){
					event.setDtStart(new Date(new net.fortuna.ical4j.model.Date(property.getValue()).getTime()));
					event.setAllDay(true);
				} else {
					event.setDtStart(new Date(new net.fortuna.ical4j.model.DateTime(property.getValue()).getTime()));	
				}
			} else if(property.getName().equals(Property.DTEND)){				
				Parameter parameter = property.getParameter("VALUE");				
				
				if(parameter != null && parameter.getValue().equals(Value.DATE.getValue())){
					event.setDtEnd(new Date(new net.fortuna.ical4j.model.Date(property.getValue()).getTime()));
				} else {
					event.setDtEnd(new Date(new net.fortuna.ical4j.model.DateTime(property.getValue()).getTime()));	
				}
			} else if(property.getName().equals(Property.GEO)){
				event.setGeo(property.getValue());
			} else if(property.getName().equals(Property.LAST_MODIFIED)){
				event.setLastMod(new Date(new net.fortuna.ical4j.model.DateTime(property.getValue()).getTime()));
			} else if(property.getName().equals(Property.LOCATION)) {
				event.setLocation(property.getValue());
			} else if(property.getName().equals(Property.ORGANIZER)) {
				event.setOrganizer(property.getValue());
			} else if(property.getName().equals(Property.PRIORITY)) {
				event.setPriority(Integer.parseInt(property.getValue()));
			} else if(property.getName().equals(Property.DTSTAMP)) {
				event.setDtStamp(new Date(new net.fortuna.ical4j.model.DateTime(property.getValue()).getTime()));
			} else if(property.getName().equals(Property.SEQUENCE)) {
				event.setSeq(Integer.parseInt(property.getValue()));
			} else if(property.getName().equals(Property.STATUS)) {
				event.setStatus(property.getValue());
			} else if(property.getName().equals(Property.SUMMARY)) {
				event.setSummary(property.getValue());
			} else if(property.getName().equals(Property.TRANSP)) {
				event.setTransp(property.getValue());
			} else if(property.getName().equals(Property.UID)) {
				event.setUid(property.getValue());
			} else if(property.getName().equals(Property.URL)) {
				event.setUrl(property.getValue());
			} else if(property.getName().toLowerCase().startsWith("x-")){
				event.addXProp(property.getName(), property.getValue());
			}
		}
		return event;
	}
	
	/**
	 * Builds an Ical4J VEvent from a webical Event
	 * @param event the webical Event
	 * @return the Ical4J VEvent
	 * @throws IOException
	 * @throws URISyntaxException
	 * @throws ParseException
	 */
	public static VEvent buildVEventFromEvent(Event event) throws IOException, URISyntaxException, ParseException {
		PropertyList propertyList = new PropertyList();
		
		VEvent vevent = new VEvent();
		
		for(String attachString : event.getAttach()){
			Attach attach = new Attach();
			attach.setValue(attachString);	
			propertyList.add(attach);		
		}
		for(String attendeeString : event.getAttendee()){
			Attendee attendee = new Attendee();
			attendee.setValue(attendeeString);	
			propertyList.add(attendee);		
		}
		for(String categoriesString : event.getCategories()){
			Categories categories = new Categories();
			categories.setValue(categoriesString);	
			propertyList.add(categories);
		}
		for(String commentString : event.getComment()){
			Comment comment = new Comment();
			comment.setValue(commentString);	
			propertyList.add(comment);
		}
		for(String contactString : event.getContact()){
			Contact contact = new Contact();
			contact.setValue(contactString);	
			propertyList.add(contact);
		}
		DateList dateList;
		if(event.getExDate().size() > 0){
			for(Date exDateDate : event.getExDate()){
				dateList = new DateList();
				dateList.add(new net.fortuna.ical4j.model.Date(exDateDate));
				ExDate exDate = new ExDate(dateList);
				propertyList.add(exDate);
			}
		}

		for(String rStatusString : event.getrStatus()){
			RequestStatus rStatus = new RequestStatus();
			rStatus.setValue(rStatusString);
			propertyList.add(rStatus);
		}
		for(String relatedString : event.getRelated()){
			RelatedTo related = new RelatedTo();
			related.setValue(relatedString);
			propertyList.add(related);
		}
		for(String resourcesString : event.getResources()){
			Resources resources = new Resources();
			resources.setValue(resourcesString);
			propertyList.add(resources);
		}
		
		if(event.getrDate().size() > 0){
			dateList = new DateList();
			
			for(Date rDateDate : event.getrDate()){
				dateList.add(new net.fortuna.ical4j.model.Date(rDateDate));
			}
			RDate rDate = new RDate(dateList);
			propertyList.add(rDate);
		}
		
		if(event.getrRule().size() > 0){
			String rRuleString = event.getrRule().iterator().next();
			RRule rRule = new RRule();
			rRule.setValue(rRuleString);
			propertyList.add(rRule);
		} 
		if(event.getClazz() != null){
			Clazz clazz = new Clazz();
			clazz.setValue(event.getClazz());
			propertyList.add(clazz);
		}
		
		if(event.getCreated() != null){
			Created created = new Created();
			created.setDateTime(new DateTime(event.getCreated()));
			propertyList.add(created);
		}
		if(event.getDescription() != null){
			Description description = new Description();
			description.setValue(event.getDescription());
			propertyList.add(description);
		}
		
		DtStart dtStart;
		if(event.isAllDay()){
			dtStart = new DtStart(new net.fortuna.ical4j.model.Date(event.getDtStart()));
			dtStart.getParameters().add(Value.DATE);
			propertyList.add(dtStart);
		} else if (event.getDtStart() != null){
			dtStart = new DtStart(new net.fortuna.ical4j.model.DateTime(event.getDtStart()));
			dtStart.getParameters().add(Value.DATE_TIME);
			propertyList.add(dtStart);
		}
		DtEnd dtEnd;
		if(event.isAllDay()){
			dtEnd = new DtEnd(new net.fortuna.ical4j.model.Date(event.getDtEnd()));
			dtEnd.getParameters().add(Value.DATE);
			propertyList.add(dtEnd);
		} else if (event.getDtEnd() != null){
			dtEnd = new DtEnd(new net.fortuna.ical4j.model.DateTime(event.getDtEnd()));
			dtEnd.getParameters().add(Value.DATE_TIME);
			propertyList.add(dtEnd);
		}
		
		if(event.getGeo() != null){
			Geo geo = new Geo();
			geo.setValue( event.getGeo() );
			propertyList.add(geo);
		}

		if(event.getLastMod() != null){
			LastModified lastModified = new LastModified();		
			lastModified.setDateTime(new DateTime(event.getLastMod()));
			propertyList.add(lastModified);
		}
		if(event.getLocation() != null){
			Location location = new Location();
			location.setValue(event.getLocation());
			propertyList.add(location);
		}
		if(event.getOrganizer() != null){
			Organizer organizer = new Organizer();
			organizer.setValue(event.getOrganizer());
			propertyList.add(organizer);
		}
		if(event.getPriority() != null){
			Priority priority = new Priority();
			priority.setLevel( event.getPriority() );
			propertyList.add(priority);
		}
		if(event.getSeq() != null){
			Sequence sequence = new Sequence();
			sequence.setValue( "" + event.getSeq() );
			propertyList.add(sequence);
		}
		if(event.getStatus() != null){
			Status status = new Status();
			status.setValue(event.getStatus());
			propertyList.add(status);
		}
		if(event.getSummary() != null){
			Summary summary = new Summary();
			summary.setValue(event.getSummary());
			propertyList.add(summary);
		}
		if(event.getTransp() != null){
			Transp transp = new Transp();
			transp.setValue(event.getTransp());
			propertyList.add(transp);
		}
		if(event.getUid() != null){
			Uid uid = new Uid();
			uid.setValue(event.getUid());
			propertyList.add(uid);
		} 
		if(event.getUrl() != null){
			Url url = new Url();
			url.setValue(event.getUrl());
			propertyList.add(url);
		}
		Map<String, String> xProps = event.getxProps();
		
		if(xProps != null){
			Iterator it = xProps.keySet().iterator();
			
			while (it.hasNext()) {
				String key = (String) it.next();
				String value = (String) xProps.get(key);
				
				XProperty xProperty = new XProperty(key, value);
				propertyList.add(xProperty);
			}
		}
		
		for(int i = 0; i < propertyList.size(); i++){
			
			vevent.getProperties().add(propertyList.get(i));
			
		}
		
		return vevent;
	}
}
