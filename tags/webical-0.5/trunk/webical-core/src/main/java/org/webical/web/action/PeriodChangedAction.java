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

package org.webical.web.action;

import org.apache.wicket.ajax.AjaxRequestTarget;


public class PeriodChangedAction implements IAction {
	private static final long serialVersionUID = 1L;

	/**
	 * Boolean to determine if we need to go the next or the previous period.
	 */
	private boolean nextPeriod;

	/**
	 * The identifier for the period this panel covers
	 */
	private int periodId;

	/**
	 * Length for the period this panel covers
	 */
	private int periodLength;

	/**
	 * Constructor
	 * @param nextPeriod
	 * @param viewPeriodId
	 * @param viewPeriodLength
	 * @param viewPeriodFormat
	 */
	public PeriodChangedAction(boolean nextPeriod, int viewPeriodId, int viewPeriodLength) {
		this.nextPeriod = nextPeriod;
		this.periodId = viewPeriodId;
		this.periodLength = viewPeriodLength;
	}

	public boolean isNextPeriod() {
		return nextPeriod;
	}

	public int getPeriodId() {
		return periodId;
	}

	public int getPeriodLength() {
		return periodLength;
	}

	public AjaxRequestTarget getAjaxRequestTarget() {
		return null;
	}

}
