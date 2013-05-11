/**
 * @author jochem
 */

/**
 * @class a YAHOO.util.DDFramed implementation. During the drag over event, the
 * dragged element is inserted before the dragged-over element.
 *
 * @extends YAHOO.util.DDProxy
 * @constructor
 * @param {String} id the id of the linked element
 * @param {String} sGroup the group of related DragDrop objects
 */
YAHOO.example.DDPlayer = function(id, sGroup, slot, dtStart, dtEnd, start, containerHeight, scrollElement, uid, calendarId, description, config) {
    this.initPlayer(id, sGroup, slot);
	this.initialWidth = this.getEl().offsetWidth;
	
	this.startHTML = this.getEl().innerHTML;
	
	var d = new Date();
	d.setTime(dtStart);
    this.setXConstraint(0, 0, 4);
		
	this.start = this.getEl().innerHTML + description + " " +this.leadingZero(d.getHours())+":"+this.leadingZero(d.getMinutes());
	this.getEl().innerHTML = this.start;
    
    this.startPos = YAHOO.util.Dom.getXY( slot.getEl() );  
    this.startPosX = YAHOO.util.Dom.getX( slot.getEl() );  
    this.startPosY = YAHOO.util.Dom.getY( slot.getEl() );  
        
    this.containerHeight = containerHeight; 
    this.scrollElement = scrollElement;
    this.date=d;
    
	var posY = this.date.getHours() * 4 * 12;
	posY = posY + parseInt((this.date.getMinutes() / 5) * 4);
	YAHOO.util.Dom.setY(this.getEl(),posY + this.startPosY );
    
    //Get the endtime, then set the duration (height)
    d = new Date();
    d.setTime(dtEnd);
 	var posYDuration = d.getHours() * 4 * 12;
	posYDuration = posYDuration + parseInt((d.getMinutes() / 5) * 4);
	
	if(parseInt(this.date.getMinutes() / 10) * 10 == this.date.getMinutes())
		this.getEl().style.height = posYDuration - posY + 4 + "px"
	else
		this.getEl().style.height = posYDuration - posY + "px";
			
    this.setYConstraint(posY, containerHeight-69-posY- (posYDuration - posY - 48), 4);
    this.initialPosY = posY;
    this.initialYConstraint = containerHeight-69-posY- (posYDuration - posY - 48);
    this.initialHeight = posYDuration - posY;
	this.uid = uid;
    this.calendarId = calendarId;
    this.description = description;
};

YAHOO.extend(YAHOO.example.DDPlayer, YAHOO.util.DDProxy);

YAHOO.example.DDPlayer.TYPE = "DDPlayer";

YAHOO.example.DDPlayer.prototype.initPlayer = function(id, sGroup, slot, config) {
    if (!id) { return; }

    this.init(id, sGroup, config);
    this.initFrame();

	this.slot = slot;

    this.type = YAHOO.example.DDPlayer.TYPE;	
	this.overlayCount;   
};

YAHOO.example.DDPlayer.prototype.startDrag = function(x, y) {

    var dragEl = this.getDragEl();
    var clickEl = this.getEl();

    dragEl.className = clickEl.className;

    var s = clickEl.style;
    s.opacity = .100;
    s.filter = "alpha(opacity=10)";
    
    this.posYDrag = YAHOO.util.DDM.getPosY(this.getDragEl());
};

YAHOO.example.DDPlayer.prototype.getTargetDomRef = function(oDD) {};

YAHOO.example.DDPlayer.prototype.endDrag = function(e) {
    // reset the linked element styles
    var s = this.getEl().style;
    s.opacity = 1;
    s.filter = "alpha(opacity=100)";
	
	this.slot.paintPlayers();	

	this.onEndDrag(e, this.uid, this.calendarId, this.start);
};
YAHOO.example.DDPlayer.prototype.onEndDrag = function(e, uid, calendarId, starttime) {};

YAHOO.example.DDPlayer.prototype.resetTargets = function() {};

YAHOO.example.DDPlayer.prototype.onDragDrop = function(e, id) {
	
	var posX;
	var posY;

	posY = YAHOO.util.DDM.getPosY(this.getDragEl());
	
	YAHOO.util.Dom.setY(this.getEl(), posY);
};

YAHOO.example.DDPlayer.prototype.onDragOver = function(e, id) {};

YAHOO.example.DDPlayer.prototype.onDrag = function(e, id) {

	var posY = YAHOO.util.DDM.getPosY(this.getDragEl());
	
	var offSet = posY - this.startPosY;
		
	if ( this.containerHeight - offSet - this.getEl().offsetHeight < 100 && this.scrollElement.scrollHeight - this.scrollElement.clientHeight != this.scrollElement.scrollTop) {
		YAHOO.util.Dom.setY(this.getDragEl(), this.posYDrag);
		this.scrollElement.scrollTop = this.scrollElement.scrollTop + posY - this.posYDrag;
	} else if ( offSet < 10 && this.scrollElement.scrollTop != 0 ) {
		YAHOO.util.Dom.setY(this.getDragEl(), this.posYDrag);
		this.scrollElement.scrollTop = this.scrollElement.scrollTop + posY - this.posYDrag;
	} else {	
    	this.posYDrag = posY;
	}
	//paint the time
	var realOffSet = posY - this.startPosY + this.scrollElement.scrollTop;
		
	var hours = parseInt((realOffSet) / 48);
	var minutes = parseInt((realOffSet - (hours * 48)) / 4) * 5;

	this.getDragEl().innerHTML = this.startHTML + '' + this.startHTML + ''+ this.description + ' ' + this.leadingZero(hours) + ':' + this.leadingZero(minutes);
};

YAHOO.example.DDPlayer.prototype.leadingZero = function(nr)
{
	if (nr < 10) nr = "0" + nr;
	return nr;
}



