package org.webical;

import org.webical.manager.WebicalException;

public class UserSettings extends Settings {
	private static final long serialVersionUID = 1L;

	private static final int DEFAULT_DEFAULT_CALENDAR_VIEW = 1;
	private static final int DEFAULT_FIRST_DAY_OF_WEEK = 1;
	private static final int DEFAULT_NUMBER_OF_AGENDA_DAYS = 4;

	private User user;
	private int defaultCalendarView, firstDayOfWeek, numberOfAgendaDays;
	private String dateFormat, timeFormat;

	public UserSettings() { }

	public UserSettings(User user) throws WebicalException {
		if(user == null) {
			throw new WebicalException("User cannot be null.");
		}
		this.user = user;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	public int getDefaultCalendarView() {
		return defaultCalendarView;
	}

	public void setDefaultCalendarView(int defaultCalendarView) {
		this.defaultCalendarView = defaultCalendarView;
	}

	public int getFirstDayOfWeek() {
		return firstDayOfWeek;
	}

	public void setFirstDayOfWeek(int firstDayOfWeek) {
		this.firstDayOfWeek = firstDayOfWeek;
	}

	public int getNumberOfAgendaDays() {
		return numberOfAgendaDays;
	}

	public void setNumberOfAgendaDays(int numberOfAgendaDays) {
		this.numberOfAgendaDays = numberOfAgendaDays;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public String getTimeFormat() {
		return timeFormat;
	}

	public void setTimeFormat(String timeFormat) {
		this.timeFormat = timeFormat;
	}
	
	// TODO mattijs: let default user settings depend on locale
	public void createDefaultSettings() {
		this.defaultCalendarView = DEFAULT_DEFAULT_CALENDAR_VIEW;
		this.firstDayOfWeek = DEFAULT_FIRST_DAY_OF_WEEK;
		this.numberOfAgendaDays = DEFAULT_NUMBER_OF_AGENDA_DAYS;
		this.dateFormat = "dd/MM/yyyy";
		this.timeFormat = "HH:mm";
	}

}
