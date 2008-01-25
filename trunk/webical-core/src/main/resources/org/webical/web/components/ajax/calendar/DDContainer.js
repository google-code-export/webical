/**
 * @author jochem
 */
YAHOO.example.DDContainer = function(id, sGroup, config) {
    this.initContainer(id, sGroup);
};

YAHOO.extend(YAHOO.example.DDContainer, YAHOO.util.DDTarget);

YAHOO.example.DDContainer.TYPE = "DDContainer";

YAHOO.example.DDContainer.prototype.initContainer = function(id, sGroup, config) {
    if (!id) { return; }

	this.init(id, sGroup, config);
	this.players = new Array();
	this.head = this;

    this.type = YAHOO.example.DDContainer.TYPE;	  
    
	YAHOO.util.Event.addListener(this.getEl(), "click", this.onClick, this); 
};

YAHOO.example.DDContainer.prototype.addPlayer = function(p){
	this.players[this.players.length] = p;
};

YAHOO.example.DDContainer.prototype.removePlayer = function(p){
	var i, count;
	var temp = new Array();
	
	count = 0;
	for(i = 0; i < this.players.length; i++){
		if(this.players[i] != p){
			temp[count] = this.players[i];
			count++;
		}
	}
	
	this.players = temp;
};

YAHOO.example.DDContainer.prototype.start = function(){
	for(var i = 0; i < this.players.length; i++){
		var posY = this.players[i].startPosY + this.players[i].date.getHours() * 4 * 12;
		posY = posY + parseInt((this.players[i].date.getMinutes() / 5) * 4);
		YAHOO.util.Dom.setY(this.players[i].getEl(),posY);
	}
}

YAHOO.example.DDContainer.prototype.paintPlayers = function(){
	var i, playersOverlay, w, posX,j,number,posY, posY_temp, times, done;
	for(i = 0; i < this.players.length; i++){
		this.players[i].overlayCount = 0;
	}
	for(i = 0; i < this.players.length; i++){
		playersOverlay = this.getNumberOffOverlays(this.players[i], new Array());

		w = (this.players[i].initialWidth / (playersOverlay.length));
		//set the new width with animation
		var myAnim = new YAHOO.util.Anim(this.players[i].getEl(), { width: { to: w } }, 0.1);
		myAnim.animate(); 
			
		if(this.players[i].getDragEl().id == this.players[i].getEl().id){
			posY = YAHOO.util.DDM.getPosY(this.players[i].getDragEl());
		} else {
			posY = YAHOO.util.DDM.getPosY(this.players[i].getEl());
		} 
		//paint the time
		var realOffSet = posY - this.players[i].startPosY + this.players[i].scrollElement.scrollTop;
		
		var hours = parseInt((realOffSet) / 48);
		var minutes = parseInt((realOffSet - (hours * 48)) / 4) * 5;

		this.players[i].start = this.players[i].leadingZero(hours) + ':' + this.players[i].leadingZero(minutes);
		this.players[i].getEl().innerHTML = this.players[i].startHTML + '' + this.players[i].description +' ' + this.players[i].start;
		
		if(playersOverlay.length == 1){
			posX = this.players[i].startPosX;
		} else {
			number = 0;
			times = 0;
			done = false;
			
			for(j = 0; j < playersOverlay.length;j++){
				if(playersOverlay[j].getDragEl().id == playersOverlay[j].getEl().id){
					posY_temp = YAHOO.util.DDM.getPosY(playersOverlay[j].getDragEl());
				} else {
					posY_temp = YAHOO.util.DDM.getPosY(playersOverlay[j].getEl());
				} 

				if(playersOverlay[j] != this.players[i] && posY_temp < posY){
					number++;
				} else if(playersOverlay[j] != this.players[i] && posY_temp == posY){
					if(!done){
						number = number + this.players[i].overlayCount;
						done = true;
					}
						playersOverlay[j].overlayCount++;
				} 
					
			}
			if(this.players[i].getDragEl().id == this.players[i].getEl().id){
				posX = this.players[i].startPosX + number * w;
			} else {
				posX = this.players[i].startPosX + number * w;
			} 
		}

		YAHOO.util.Dom.setX(this.players[i].getEl(), posX);
	}	
}

YAHOO.example.DDContainer.prototype.getNumberOffOverlays = function(player, doneAlreadyArray){

	var i, posY_temp, playerHeight_temp, posY;
	var returnArray = new Array();
	var returnArray_temp;
	
	if(player.getDragEl().id == player.getEl().id){
		posY = YAHOO.util.DDM.getPosY(player.getDragEl());
	} else {
		posY = YAHOO.util.DDM.getPosY(player.getEl());
	} 
	var playerHeight = player.getEl().offsetHeight;
	
	doneAlreadyArray[doneAlreadyArray.length] = player;
	
	for(i = 0; i < this.players.length; i++){			
		if(this.players[i].getDragEl().id == this.players[i].getEl().id){
			posY_temp = YAHOO.util.DDM.getPosY(this.players[i].getDragEl());
		} else {
			posY_temp = YAHOO.util.DDM.getPosY(this.players[i].getEl());
		} 
		playerHeight_temp = this.players[i].getEl().offsetHeight;
		
		if((posY_temp >= posY && posY_temp <= (posY + playerHeight)) 
			|| (posY_temp + playerHeight_temp >= posY && posY_temp + playerHeight_temp <= (posY + playerHeight))
			|| (posY >= posY_temp && posY <= (posY_temp + playerHeight_temp)) 
			|| (posY + playerHeight >= posY_temp && posY + playerHeight <= (posY_temp + playerHeight_temp))){
			
			if(this.players[i] == player){
				returnArray[returnArray.length] = this.players[i];
				
			} else if (!this.inArray(doneAlreadyArray, this.players[i])){				
				returnArray_temp = this.getNumberOffOverlays(this.players[i], doneAlreadyArray);
				returnArray = returnArray.concat(returnArray_temp);
			} 
		}
	}
	
	return returnArray;
}
YAHOO.example.DDContainer.prototype.inArray = function(array, player){
	var i;
	for (i = 0; i < array.length; i++){
		if(array[i] == player){
			return true;
		}
	}
	return false;
}

YAHOO.example.DDContainer.prototype.onClick = function(e, slot){
	
	var posy = 0; var i;
	// posx and posy contain the mouse position relative to the document
	if (!e) var e = window.event;
	if (e.pageX || e.pageY) 	{
		posx = e.pageX;
		posy = e.pageY;
	} else if (e.clientX || e.clientY) 	{
		posx = e.clientX + document.body.scrollLeft
					+ document.documentElement.scrollLeft;
		posy = e.clientY + document.body.scrollTop
					+ document.documentElement.scrollTop;
	}
	var eventClicked=new Boolean(false);

	//Check if there was clicked on an event, instead of this calendar
	
	for(i = 0; i < slot.players.length; i++){
		
		var posYPlayer = YAHOO.util.DDM.getPosY(slot.players[i].getEl());
		var posXPlayer = YAHOO.util.DDM.getPosX(slot.players[i].getEl());
		var heightPlayer = slot.players[i].getEl().clientHeight;
		var widthPlayer = slot.players[i].getEl().clientWidth;
		
		if(posYPlayer <= posy && posYPlayer + heightPlayer >= posy 
				&& posXPlayer <= posx && posXPlayer + widthPlayer >= posx){
			
			eventClicked = true;
			break;
		}
	}

	if(eventClicked == false){
		//Convert posY to a grid position (5 min position)
		var posSlot = YAHOO.util.DDM.getPosY(slot.getEl());
		
		posy = parseInt((posy-posSlot) / 4) * 4;
			
		var hours = parseInt((posy) / 48);
		if (hours < 10) hours = "0" + hours;
		var minutes = parseInt((posy - hours * 48) / 4) * 5;
		if (minutes < 10) minutes = "0" + minutes;
		var time = hours + ':' + minutes;
		
		var wicketPanelURL = createCalendarClickUrl(time);
		wicketAjaxGet(wicketPanelURL, function() { }, function() { });	  
	}  			
}