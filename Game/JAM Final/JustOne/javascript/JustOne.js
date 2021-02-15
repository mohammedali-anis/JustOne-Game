var currentGuessWord = "";
var subChar = "?";
var selectedWords = [];

addListener('CLEAN', function(event){
	for(var i = 1; i <= 6; i++){
		document.getElementById("myspan" + i).textContent = "";
	}
	document.getElementById("hintOwnHint").value = "";
	document.getElementById("guessOwnGuess").value = "";
	document.getElementById("hintSpan").textContent = "";
	document.getElementById("guessResult").textContent = "";
});

addListener('GAMEOVER', function(event){
	document.getElementById("gameOverText").style.visibility = "visible";
	document.getElementById("hintShow").style.visibility = "hidden";
	document.getElementById("guessResult").textContent = event.data;
	document.getElementById("role").textContent = "-";
});

addListener('WRONGGUESS', function(event){
	document.getElementById("guessResult").textContent = event.data;
	document.getElementById("guess").style.visibility = "hidden";
	document.getElementById("hintShow").style.visibility = "hidden";
});

addListener('RIGHTGUESS', function(event){
	document.getElementById("guessResult").textContent = event.data;
	document.getElementById("guess").style.visibility = "hidden";
	document.getElementById("hintShow").style.visibility = "hidden";
});

addListener('ROUNDSTATUS', function(event){
	document.getElementById("currentRound").textContent = event.data;
});

addListener('WINSTATUS', function(event){
	document.getElementById("winStatus").textContent = event.data;
});

addListener('ROLESTATUS', function(event){
	document.getElementById("role").textContent = event.data;
});

addListener('GIVEHINT', function(event){
	currentGuessWord = event.data;
	document.getElementById("hintSpan").textContent = currentGuessWord;
	document.getElementById("hint").style.visibility = "visible";
});

addListener('ENABLEGUESS', function(event){
	document.getElementById("guessResult").value = "";
	document.getElementById("guess").style.visibility = "visible";
});

addListener('NOVALIDHINT', function(event){
	document.getElementById("guessResult").textContent = event.data;
});

addListener('VALIDHINT', function(event){
	var stringFromServer = event.data;
	var arr = stringFromServer.split(',');
	for(var i = 0; i < arr.length; i++){
		var spanCounter = i + 1;
		document.getElementById("myspan" + spanCounter).textContent = arr[i];
	}
	document.getElementById("hintShow").style.visibility = "visible";
});

addListener('ENABLEHOSTVISION', function(event){
	document.getElementById("hostMenu").style.visibility = "visible";
});

addListener('ENABLESTARTBUTTON', function(event){
	document.getElementById("startButton").style.visibility = "visible";
	document.getElementById("customWordTutorial").style.visibility = "visible";
	document.getElementById("customWords").style.visibility = "visible";
});

addListener('WAIT', function(event){
	document.getElementById("guessResult").textContent = event.data;
	document.getElementById("hintShow").style.visibility = "hidden";
});

addListener('SHOWNEXTROUNDBUTTON', function(event){
	document.getElementById("nextRoundButton").style.visibility = "visible";
});

addListener('SHOWRESTARTBUTTON', function(event){
	document.getElementById("restartButton").style.visibility = "visible";
});

addListener('PLAYER', function(event){
	var stringFromServer = event.data;
	var arr = stringFromServer.split(',');
	for(var i = 0; i < 7; i++){
		var spanCounter = i + 1;
		if(i < arr.length){
			document.getElementById("player" + spanCounter).textContent = arr[i];
		}else{
			document.getElementById("player" + spanCounter).textContent = "-";
		}
	}
});

addListener('CLOSE', function(event){
	document.getElementById("guessResult").textContent = event.data;
	document.getElementById("currentRound").textContent = "-";
	document.getElementById("winStatus").textContent = "-";
	document.getElementById("role").textContent = "-";
	document.getElementById("hostMenu").style.visibility = "hidden";
	document.getElementById("playerMenu").style.visibility = "hidden";
	document.getElementById("hint").style.visibility = "hidden";
	document.getElementById("hintShow").style.visibility = "hidden";
	document.getElementById("guess").style.visibility = "hidden";
	document.getElementById("nextRoundButton").style.visibility = "hidden";

});

addListener('ENOUGHPLAYER', function(event){
	document.getElementById("guessResult").textContent = event.data;
});

addListener('RESTART', function(event){
	document.getElementById("hint").style.visibility = "visible";
	document.getElementById("hintShow").style.visibility = "visible";
	document.getElementById("guess").style.visibility = "visible";
	document.getElementById("gameOverText").style.visibility = "visible";
	document.getElementById("myspan1").textContent = "";
	document.getElementById("myspan2").textContent = "";
	document.getElementById("myspan3").textContent = "";
	document.getElementById("myspan4").textContent = "";
	document.getElementById("myspan5").textContent = "";
	document.getElementById("myspan6").textContent = "";
	document.getElementById("hintSpan").textContent = "";
	document.getElementById("guessResult").textContent = "";
	document.getElementById("role").textContent = "-";
	document.getElementById("winStatus").textContent = "-";
	document.getElementById("currentRound").textContent = "-";
	document.getElementById("hint").style.visibility = "hidden";
	document.getElementById("hintShow").style.visibility = "hidden";
	document.getElementById("guess").style.visibility = "hidden";
	document.getElementById("gameOverText").style.visibility = "hidden";
});

function startGame(){
	var customWordList = document.getElementById("customWords").value;
	var data = "SETUP" + subChar + customWordList;
	document.getElementById("startButton").style.visibility = "hidden";
	document.getElementById("customWordTutorial").style.visibility = "hidden";
	document.getElementById("customWords").style.visibility = "hidden";
	sendDataToServer(data);
}

function confirmHint(){
	var hint = document.getElementById("hintOwnHint").value;
	if(!hint || hint.length === 0 || hint.trim().includes(" ")){
		invalidString();
	}else{
		document.getElementById("guessResult").textContent = "";
		document.getElementById("hintOwnHint").textContent = "";
		document.getElementById("hint").style.visibility = "hidden";
		sendDataToServer(hint.trim());
	}
}

function confirmGuess(){
	var guess = document.getElementById("guessOwnGuess").value;
	if(!guess || guess.length === 0){
		invalidString();
	}else{
		document.getElementById("guessOwnGuess").textContent = "";
		document.getElementById("guess").style.visibility = "hidden";
		for(var i = 1; i <= 6; i++){
			document.getElementById("myspan" + i).textContent = "";
		}
		sendDataToServer(guess.trim());
	}
}

function restart(){
	document.getElementById("restartButton").style.visibility = "hidden";
	sendDataToServer("RESTART");
}

function nextRound(){
	document.getElementById("nextRoundButton").style.visibility = "hidden";
	sendDataToServer("NEXTROUND");
}

function closeGame(){
	sendDataToServer("CLOSE");
}

function invalidString(){
	document.getElementById("guessResult").textContent = "UNGUELTIGE EINGABE";
}