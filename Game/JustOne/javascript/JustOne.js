var currentGuessWord = "";
var selectedWords = [];
var currentRound = 0;


addListener('standardEvent', function(event) {
		var stringFromServer = event.data;
		var arr = stringFromServer.split(',');
		console.log(arr);
		
		selectedWords = [];
		var isHost = arr[0];
		currentGuessWord = arr[1];
		document.getElementById("hintSpan").textContent = currentGuessword;
		
		if(arr.length > 1){
			for(var i = 2; i <= arr.length; i++){
				selectedWords[i] = arr[i];
			}
		}
		if(selectedWords.length >= 1){
			
			for(var i = 0; i < selectedWords; i++){
				var spanCounter = i + 1;
				document.getElementById("myspan" + spanCounter).textContent = selectedWords[i];
			}
		}
		
		if(currentRound != 0){
			currentRound = currentRound + 1;
			document.getElementById("currentRound").textContent = String(currentRound);
		}
		
		if(isHost == "Host"){
			setVisible();
		}
		cosole.log(selectedWords);

		redraw();
		
	});
	
addListener('starttest', function(event) {
		var stringFromServer = event.data;
		var arr = stringFromServer.split(',');
		currentGuessWord = arr[0];
		document.getElementById("hintSpan").textContent = currentGuessWord;
		console.log(currentGuessWord);
		
	});
	
	
	addListener('Testantwort', function(event) {
		var stringFromServer = event.data;
		var arr = stringFromServer.split(',');
		console.log(arr);
		console.log("Die Laenge betraegt: " + arr.length);
		for(var i = 0; i < arr.length; i++){
			var spanCounter = i + 1;
			document.getElementById("myspan" + spanCounter).textContent = arr[i];
		}
		
	});
addListener('WAIT', function(event){
	console.log("Warten");
});

addListener('CLEAN', function(event){
	for(var i = 1; i <= 6; i++){
		document.getElementById("myspan" + i).textContent = "";
	}
	document.getElementById("hintSpan").textContent = "";
	
});

addListener('GEWONNEN', function(event){
	console.log("gewonnen");
	document.getElementById("winStatus").textContent = "Gewonnen!";
	document.getElementById("hintConfirm").style.visibility = "hidden";
	document.getElementById("guessConfirm").style.visibility = "hidden";
	
	for(var i = 1; i <= 6; i++){
		document.getElementById("myspan" + i).textContent = "";
	}
	
});

addListener('READY', function(event){
	document.getElementById("guessConfirm").style.visibility = "visible";
});

addListener('WRONG', function(event){
	console.log("falsches Wort erraten");
	document.getElementById("guessResult").textContent = "Das Wort wurde falsch Erraten!"
});

addListener('RIGHT', function(event){
	console.log("richtiges Wort erraten.");
	document.getElementById("guessResult").textContent = "Das Wort wurde richtig Erraten!"
});

addListener('ROUND', function(event){
	var arr = event.data.split(',');
	console.log(arr)
	document.getElementById("currentRound").textContent = arr[0]
});



addListener('msg', function(event){
	var arr = event.data.split(',');
	console.log(arr)
	document.getElementById("msg").textContent = arr[0]
	
});



addListener('WIN', function(event){
	var arr = event.data.split(',');
	console.log(arr)
	document.getElementById("winStatus").textContent = arr[0]
});

addListener('HINT', function(event){
	document.getElementById("hintSpan").textContent = event.data;
	document.getElementById("hintConfirm").style.visibility = "visible";

	
});

addListener('GAMEOVER', function(event){
	document.getElementById("winStatus").textContent = "Die Runde ist vorbei.";	
	document.getElementById("hintConfirm").style.visibility = "hidden";
	document.getElementById("guessConfirm").style.visibility = "hidden";
	
	for(var i = 1; i <= 6; i++){
		document.getElementById("myspan" + i).textContent = "";
	}
});
	
	
addListener('START', function(event){
	var stringFromServer = event.data;
	currentRound = 1;
	var arr = stringFromServer.split(',');
	currentGuessWord = arr[0];
	document.getElementById("hintSpan").textContent = currentGuessWord;
	
	if(arr[1]=="HOST") setVisible();
	document.getElementById("hintConfirm").style.visibility = "visible";
});
addListener('PLAYERLEFT', function(event){
	var stringFromServer = event.data;
	playerMessage = stringFromServer;
	document.getElementById("Player").innerHTML = playerMessage;
});
addListener('CLOSE', function(event){
	document.getElementById("Player").innerHTML = "Spiel wurde vom Host beendet!";
	
});

addListener('HIDE', function(event){
	document.getElementById("guessConfirm").style.visibility = "hidden";
});

addListener('WORDS', function(event){
	var stringFromServer = event.data;
	var arr = stringFromServer.split(',');
	console.log(arr);
	console.log("Die Laenge betraegt: " + arr.length);
	for(var i = 0; i < arr.length; i++){
		var spanCounter = i + 1;
		document.getElementById("myspan" + spanCounter).textContent = arr[i];
	}
	document.getElementById("hintConfirm").style.visibility = "hidden";
});


	function startGame(){
		sendDataToServer("START");
	}
	
	function confirmHint(){
		var hint = document.getElementById("hintOwnHint").value;
		document.getElementById("hintOwnHint").value = "";
		document.getElementById("hintConfirm").style.visibility = "hidden";
		sendDataToServer(hint);
	}
	
	function sendGuess(){
		var guess = document.getElementById("guessOwnGuess").value;
		document.getElementById("guessOwnGuess").value = "";
		document.getElementById("guessConfirm").style.visibility = "hidden";
		for(var i = 1; i <= 6; i++){
		document.getElementById("myspan" + i).textContent = "";
		}
		sendDataToServer(guess);
	}

	function updateGameState(){
		var input = document.getElementById("hintOwnHint");
		var text = input.innerHTML;
		sendDataToServer(text);
	}

	function redraw(){
		
	}
	
	function restart(){
			statusWait = true;
			sendDataToServer("RESTART");
	}
	function setVisible(){
		document.getElementById("restartButton").style.visibility ="visible";
		document.getElementById("closeButton").style.visibility ="visible";
	}
	function closeGame(){
		
		sendDataToServer("CLOSE");
	}
	
	function hallo(){
		console.log("hallo");
	}

	
	function sendeTestNachricht(){
		var inputText = document.getElementById('hintOwnHint').value;
		console.log(inputText);
		sendDataToServer("Test");
	}
	function sentHint(){
		var inputText = document.getElementById('hintOwnHint').value;
		sendDataToServer(inputText);
		
	}
	
