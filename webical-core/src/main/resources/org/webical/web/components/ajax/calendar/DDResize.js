/**
 * @author jochem
 */

/**
 * @extends YAHOO.util.DragDrop
 * @constructor
 * @param {String} handle the id of the element that will cause the resize
 * @param {String} panel id of the element to resize
 * @param {String} sGroup the group of related DragDrop items
 */
YAHOO.example.DDResize = function(panelElId, handleElId, sGroup, slot, uid, calendarId, config) {
    if (panelElId) {
        this.init(panelElId, sGroup, config);
        this.handleElId = handleElId;
        this.setHandleElId(handleElId);

		this.initialWidth = this.getEl().offsetWidth - 4;
		this.slot = slot;
		
		this.duration = 60;
		this.calendarId = calendarId;
		this.uid = uid;
    }
};

YAHOO.extend(YAHOO.example.DDResize, YAHOO.util.DragDrop);

YAHOO.example.DDResize.prototype.onMouseDown = function(e) {
    var panel = this.getEl();
    this.startWidth = panel.offsetWidth;
    this.startHeight = panel.offsetHeight;

    this.startPos = [YAHOO.util.Event.getPageX(e),
                     YAHOO.util.Event.getPageY(e)];
};

YAHOO.example.DDResize.prototype.startDrag = function(x,y){
	this.getDragEl().style.zindex = 999;
};

YAHOO.example.DDResize.prototype.onDragDrop = function(e,id){};

YAHOO.example.DDResize.prototype.onDrag = function(e) {

	
	var newPos = [YAHOO.util.Event.getPageX(e),
                  YAHOO.util.Event.getPageY(e)];

    var offsetY = newPos[1] - this.startPos[1];
    var newHeight = Math.max(this.startHeight + offsetY, 24);
    
    var player  = YAHOO.util.DDM.getDDById(this.getDragEl().id);
    

	//Resize with steps of 4px (five minutes)
	if ((newHeight % 4) == 0){
		if (newHeight != 24){
			var newHeight = Math.min(this.startHeight + offsetY, 496 );	
		} 	
		
	    player.getEl().style.height = newHeight + "px";
	}	
};

YAHOO.example.DDResize.prototype.endDrag = function(e) {

	var player  = YAHOO.util.DDM.getDDById(this.getDragEl().id);
	var posY = YAHOO.util.DDM.getPosY(player.getDragEl());
	player.resetConstraints;
	
    player.setYConstraint(player.initialPosY
    						, player.initialYConstraint - (player.getEl().offsetHeight - player.initialHeight)
    						, 4);

	this.slot.paintPlayers();
	this.getDragEl().style.zindex = 1;
	
	this.duration = parseInt(parseInt(this.getEl().style.height) / 4) * 5;

	this.onEndResize(e, this.uid, this.calendarId, this.duration);
}

YAHOO.example.DDResize.prototype.onEndResize = function(e, uid, calendarId, duration) {};