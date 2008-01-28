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

package org.webical.ical;

import java.util.Date;

/**
 * Wrapper for recurrence information used in the fronend
 * @author ivo
 *
 */
public class Recurrence {
	public static final int DAILY = 1;
	public static final int WEEKLY = 2;
	public static final int MONTHLY = 3;
	public static final int YEARLY = 4;

	private int frequency;
	private int count = -1;
	private int interval;
	private Date endDay;

	public Recurrence() {

	}

	/**
	 * @param frequency
	 * @param count
	 * @param interval
	 * @param endDay
	 */
	public Recurrence(int frequency, int count, int interval, Date endDay) {
		this.frequency = frequency;
		this.count = count;
		this.interval = interval;
		this.endDay = endDay;
	}

	/**
	 * @param frequency
	 * @param interval
	 * @param count
	 */
	public Recurrence(int frequency, int interval, Date endDay) {
		this.frequency = frequency;
		this.interval = interval;
		this.endDay = endDay;
	}

	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public Date getEndDay() {
		return endDay;
	}
	public void setEndDay(Date endDay) {
		this.endDay = endDay;
	}
	public int getFrequency() {
		return frequency;
	}
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	public int getInterval() {
		return interval;
	}
	public void setInterval(int interval) {
		this.interval = interval;
	}
}
