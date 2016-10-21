/*
 * jQuery Editable Select
 * https://github.com/shaoyun/jquery-editable-select
 *
 * Copyright (c) 2016 Shaoyun
 * Licensed under the GPL license.
 */

(function($) {

    $.fn.editableSelect = function(options) {
        var defaults = {
            warpClass: 'ui-select-wrap',
            editable: true
        };
        var opts = $.extend(defaults,options);

        $(this).each(function(){
            var $select     = $(this);
            if($select.attr('data-editable') && $select.attr('data-editable') == 'false') {
                opts.editable = false;
            }
            var html = '<div class="' + opts.warpClass + '" tabindex="-1">'
            + '<div class="selected"><span></span>'
            + '<a class="ui-button"><i class="fa fa-angle-down"></i></a></div>'
            + '<div class="dropdown-box">';
            html += (opts.editable ?
                '<div class="inputbox"><input type="text" placeholder="Enter your text" /></div>' : '');

            html += '<ul></ul></div></div>';

            var $selectWrap = $(html).insertAfter(this);
            var $options    = $selectWrap.find('ul');
            var $downbox = $selectWrap.find(".dropdown-box");
            var bEdit = false;
            $select.hide();
            $downbox.css({
                'width' : $selectWrap.width() + 'px'
            });
            var _opt_click = function() {
                var val  = $(this).attr('data-val'), txt = $(this).text();
                $selectWrap.find(".selected").attr("data-val",val).find('span').text(txt);
                $selectWrap.find(".selected").find('i').removeClass('fa-angle-up').addClass('fa-angle-down');
                $select.val(val);
                $downbox.hide();
                $(this).addClass('over selected').siblings().removeClass('over selected');
            };
            var _new_opt = function(txt, val, isSelected) {
                var $opt = $("<li>").text(txt).attr("data-val",val);
                if(isSelected) {
                    $selectWrap.find(".selected").attr("data-val",val).find('span').text(txt);
                    $opt.addClass('over').addClass('selected');
                }
                $opt.click(_opt_click);
                $opt.mouseenter(function(){
                    $(this).addClass('over').siblings().removeClass('over');
                });
                return $opt;
            };
            var _show = function(bShow) {
                if(bShow) {
                    $selectWrap.find('i').removeClass('fa-angle-down').addClass('fa-angle-up');
                    if(!$options.find(".selected").hasClass('over')) {
                        $options.find(".selected").addClass('over').siblings().removeClass('over');
                    }
                    $downbox.show();
                } else {
                    $selectWrap.find("i").removeClass('fa-angle-up').addClass('fa-angle-down');
                    $downbox.hide();
                }
            };
            $select.find('option').each(function() {
                var $opt = _new_opt($(this).text(), $(this).val(), $(this).is(":selected"));
                $opt.appendTo($options);
            });
            $selectWrap.find(".selected").click(function() {
                _show($(this).find('i').hasClass('fa-angle-down'));
            });
            $selectWrap.blur(function(event) {
                 event.preventDefault();
                 setTimeout(function() { if(bEdit) return; _show(false);}, 100);
                 $selectWrap.removeClass('focus');
            });
            if(opts.editable) {
                var $input = $downbox.find("input");
                $downbox.find(".inputbox").css('width', $selectWrap.width() - 22 + 'px');
                $input.focus(function(){
                      $selectWrap.addClass('focus');
                      bEdit = true;
                });
                $input.blur(function() {
                    bEdit = false;
                    $selectWrap.trigger('blur');
                });
                $input.bind('keypress',function(event){
                    if(event.keyCode == "13"){
                        var val = $input.val();
                        var $opt = _new_opt(val, val, true);
                        $opt.appendTo($options);
                        $input.val('');
                        $("<option>").val(val).text(val).attr('data-is-input', 1).appendTo($select);
                        $opt.trigger('click');
                    }
                });
            }
        });
    }
})(jQuery);
