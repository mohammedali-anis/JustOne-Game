var uri = document.URL.split("/");

if(uri.length>4) {
	var eventSource = new EventSource('/sse/'+uri[4]);
}
else {
	var eventSource = new EventSource('/sse/');
}

function addListener(name, func){
	eventSource.addEventListener(name, function(event){
		func(event);
	});
}

//Sends data to the Server. data is put into the querypart of the url. No refresh of the server
function sendDataToServer (d) {
		xmlHttp = new XMLHttpRequest();
		xmlHttp.open("HEAD", "/"+uri[3]+"/" + uri[4] + "/" + "gameData" + "?" + d, true);
		xmlHttp.send();
}

addListener('LOGOUT', function(event){
	var stringFromServer = event.data;
	window.location = "/logout";
	eventSource.close();
});

addListener('QUIT', function(event){
	var stringFromServer = event.data;
	window.location = "/";
	eventSource.close();
});