// Select the main list and add the class "hasSubmenu" in each LI that contains an UL
$('ul').each(function(){
	console.log("hello");
  $this = $(this);
  $this.find("li").has("ul").addClass("hasSubmenu");
});
// Find the last li in each level
$('li:last-child').each(function(){
  $this = $(this);
  // Check if LI has children
  if ($this.children('ul').length === 0){
    // Add border-left in every UL where the last LI has not children
    $this.closest('ul').css("border-left", "1px solid gray");
  } else {
    // Add border in child LI, except in the last one
    $this.closest('ul').children("li").not(":last").css("border-left","1px solid gray");
    // Add the class "addBorderBefore" to create the pseudo-element :defore in the last li
    $this.closest('ul').children("li").last().children("a").addClass("addBorderBefore");
    // Add margin in the first level of the list
    $this.closest('ul').css("margin-top","20px");
    // Add margin in other levels of the list
    $this.closest('ul').find("li").children("ul").css("margin-top","20px");
  };
});
// Add bold in li and levels above

// Add button to expand and condense - Using FontAwesome
$('ul li.hasSubmenu').each(function(){
  $this = $(this);
  // $this.prepend("<a href='#'><i class='fa fa-minus-circle' ></i><i style='display:none;' class='fa fa-plus-circle'></i></a>");
  $this.prepend("<a href='#'><span class='glyphicon glyphicon-minus' aria-hidden='true'></span><span style='display:none;' class='glyphicon glyphicon-plus' aria-hidden='true'></span></a>");
  $this.children("a").not(":last").removeClass().addClass("toogle");
});
// Actions to expand and consense
$('ul li.hasSubmenu > a').click(function(){
  $this = $(this);
  $this.closest("li").children("ul").toggle("slow");
  $this.children("span").toggle();
  return false;
});