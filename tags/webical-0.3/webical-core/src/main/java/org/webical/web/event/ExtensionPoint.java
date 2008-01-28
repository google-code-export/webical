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

package org.webical.web.event;

import java.io.Serializable;

import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;

/**
 * 
 * Defines a Point to extend
 * @author ivo
 *
 */
public class ExtensionPoint implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private RepeatingView extendingComponent;
	private IModel availableModel;

	public ExtensionPoint(RepeatingView extendingComponent, IModel availableModel) { 
		this.extendingComponent = extendingComponent;
		this.availableModel = availableModel;
	}

	public IModel getAvailableModel() {
		return availableModel;
	}

	public void setAvailableModel(IModel availableModel) {
		this.availableModel = availableModel;
	}

	public RepeatingView getExtendingComponent() {
		return extendingComponent;
	}

	public void setExtendingComponent(RepeatingView extendingComponent) {
		this.extendingComponent = extendingComponent;
	}
	
	public String getNewChildId() {
		return getExtendingComponent().newChildId();
	}
}
