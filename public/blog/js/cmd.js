
//https://cdnjs.cloudflare.com/ajax/libs/rxjs/4.1.0/rx.aggregates.js
//does matter of html dom loaded
$(function(){
  var keymap = []; // Or you could call it "key"
  onkeydown = onkeyup = function(e){
      e = e || event; // to deal with IE
      keymap[e.keyCode] = e.type == 'keydown';

      //http://www.cambiaresearch.com/articles/15/javascript-key-codes
      //ctrl/cmd + x
      if((keymap[17] || keymap[91]) && keymap[88]) {
        $('#cmd-pop-window').modal({
            show: 'true'
        });
      }
  }
});