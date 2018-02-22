/*
* Author:      Marco Kuiper (http://www.marcofolio.net/)
* Customizations by JBP noted in comments below
*/

var currentSelection = 0;
var currentUrl = '';


	// Register keydown events on the whole document
	$(document).keydown(function(e) {
		switch(e.keyCode) { 
			// User pressed "up" arrow
			case 38:
				navigate('up');
			break;
			// User pressed "down" arrow
			case 40:
				navigate('down');
			break;
			// User pressed "enter"
			case 13:
				if(currentUrl != '') {
					window.location = currentUrl;
				}
			break;
		}
	});
	
	// Add data to let the hover know which index they have
	for(var i = 0; i < $("#results ul li a").size(); i++) {
		$("#results ul li a").eq(i).data("number", i);
	}
	
	// Simulate the "hover" effect with the mouse
	$("#results ul li a").hover(
		function () {
			currentSelection = $(this).data("number");
			setSelected(currentSelection);
		}, function() {
			$("#results ul li a").removeClass("search_hover");
			currentUrl = '';
		}
	);


function navigate(direction) {

	// Check if any of the menu items is selected
	if($("#results ul li .search_hover").size() == 0) {
		currentSelection = -1;
	}
	
	//JBP - focus back on search field if up arrow pressed on top search result
	if(direction == 'up' && currentSelection == 0) {
		$("#s").focus();
	}
	//

	if(direction == 'up' && currentSelection != -1) {
		if(currentSelection != 0) {
			currentSelection--;
		}
	} else if (direction == 'down') {
		if(currentSelection != $("#results ul li").size() -1) {
			currentSelection++;
		}
	}
	setSelected(currentSelection);
}

function setSelected(menuitem) {

	//JBP - get search result to place in search field on hover
	var title = $("#results ul li a").eq(menuitem).attr('title');
	$("#s").val(title);
	//

	$("#results ul li a").removeClass("search_hover");
	$("#results ul li a").eq(menuitem).addClass("search_hover");
	currentUrl = $("#results ul li a").eq(menuitem).attr("href");
}