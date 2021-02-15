package games.JustOne;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.naming.NameNotFoundException;

import gameClasses.Game;
import gameClasses.GameState;
import global.FileHelper;
import userManagement.User;

public class JustOne extends Game{

	private final int amountOfRounds = 13;
	private final int maxPlayerAmount = 7;
	private final int minPlayerAmount = 3;
	private final String subChar = "?";

	private int winCounter = 0;
	private int turnCounter = 0;
	private int hintsGiven = 0;
	private String currentGuessWord = null;
	private String customWords = null;
	private Random randomGenerator = new Random();
	private User guesser = null;
	private ArrayList<User> chooser = new ArrayList<User>();
	private ArrayList<User> playerList = new ArrayList<User>();
	private ArrayList<User> spectatorList = new ArrayList<User>();
	private ArrayList<String> possibleWords = new ArrayList<String>();
	private ArrayList<String> allHints = new ArrayList<String>();
	
	@Override
	public String getSite() {
		// TODO Auto-generated method stub
		try {
			return FileHelper.getFile("JustOne/index.html");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public String getCSS() {
		try {
			return global.FileHelper.getFile("JustOne/css/design.css");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public String getJavaScript() {
		return "<script src=\"javascript/JustOne.js\"></script>";
		// TODO Auto-generated method stub
	}
	
	@Override
	public int getMaxPlayerAmount() {
		// TODO Auto-generated method stub
		return maxPlayerAmount;
	}
	
	@Override
	public int getCurrentPlayerAmount() {
		// TODO Auto-generated method stub
		return playerList.size();
	}

	private int getMinPlayerAmount() {
		// TODO Auto-generated method stub
		return minPlayerAmount;
	}

	private int getAmountOfRounds() {
		// TODO Auto-generated method stub
		return amountOfRounds;
	}
	
	private String selectRandomWord() {
		if(!possibleWords.isEmpty()){
			int index = randomGenerator.nextInt(possibleWords.size());
			String nextWord = possibleWords.get(index);
			possibleWords.remove(index);
			return nextWord;
		}
		return null;
	}
	
	private User getNextGuesser() throws NameNotFoundException {
		ArrayList<User> player = new ArrayList<User>(getPlayerList());
		User nextGuesser = null;
		if(guesser != null){
			int current = player.indexOf(guesser);
			if(current == -1){
				throw new NameNotFoundException();
			}
			try{
				nextGuesser = player.get(current+1);
			}catch(IndexOutOfBoundsException e){
				nextGuesser = player.get(0);
			}
			return nextGuesser;
		}
		return player.get(0);
	}
	
	private boolean isUserGuesser(User user) {
		if(guesser != null){
			if(guesser == user) {
				return true;
			}
		}
		return false;
	}
	
	private void loadWords(){
		possibleWords.clear();
		if(customWords != null){
			while(!customWords.isBlank()){
				int index = customWords.indexOf(";");
				if(index != -1){
					possibleWords.add(customWords.substring(0, index));
					customWords = customWords.substring(index + 1).strip();
				}else{
					break;
				}
			}
			customWords = null;
		}
		BufferedReader br = null;
		try{
			br = new BufferedReader(new FileReader("JustOne/Nomen.txt"));
			String word;
			while((word = br.readLine()) != null) {
				possibleWords.add(word);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(br != null) {
					br.close();
				}
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
		return;
	}

	private void checkAllHints(){
		if(allHints.isEmpty()){
			return;
		}
		ArrayList<String> tempHints = new ArrayList<String>(allHints);
		for (int i = 0; i < allHints.size(); i++) {
			ArrayList<String> copyAllHints = new ArrayList<String>(allHints);
			String currentHint = allHints.get(i);
			copyAllHints.remove(i);
			if(copyAllHints.contains(currentHint)){
				while(tempHints.contains(currentHint)){
					tempHints.remove(currentHint);
				}
			}
		}
		ArrayList<String> finalHints = new ArrayList<String>(tempHints);
		for (String string : tempHints) {
			if(currentGuessWord.toLowerCase().strip().contains(string.toLowerCase().strip()) || string.toLowerCase().strip().contains(currentGuessWord.toLowerCase().strip())){
				finalHints.remove(string);
			}
		}
		allHints = finalHints;
	}

	@Override
	public void execute(User user, String gsonString) {
		if(gsonString.equals("")) return;

		if(gsonString.contains(subChar)){
			customWords = gsonString.substring(gsonString.indexOf(subChar) + subChar.length()).strip();
			gsonString = gsonString.substring(0, gsonString.indexOf(subChar));
		}

		if(gsonString.equals("SETUP")) {
			this.gState = GameState.SETUP;
			loadWords();
			turnCounter = 0;
			winCounter = 0;
			hintsGiven = 0;
			allHints.clear();
			currentGuessWord = null;
			guesser = null;
			chooser = null;
			if(getCurrentPlayerAmount() >= getMinPlayerAmount()){
				gsonString = "NEXTROUND";
			}else{
				this.gState = GameState.SETUP;
				sendGameDataToClients("WAIT");
				sendGameDataToUser(creator, "ENABLESTARTBUTTON");
			}
		}

		if(gsonString.equals("NEXTROUND")){
			this.gState = GameState.RUNNING;
			sendGameDataToClients("CLEAN");
			turnCounter++;
			if(turnCounter > getAmountOfRounds()) {
				this.gState = GameState.FINISHED;
				sendGameDataToClients("GAMEOVER");
				sendGameDataToUser(getGameCreator(), "SHOWRESTARTBUTTON");
				return;
			}
			if(turnCounter == 1){
				sendGameDataToClients("WINSTATUS");
			}
			sendGameDataToClients("ROUNDSTATUS");
			hintsGiven = 0;
			allHints.clear();
			try {
				guesser = getNextGuesser();
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			chooser = null;
			chooser = new ArrayList<User>(getPlayerList());
			chooser.remove(guesser);
			currentGuessWord = selectRandomWord();
			sendGameDataToClients("ROLESTATUS");
			for(User choosePlayer : chooser){
				sendGameDataToUser(choosePlayer, "GIVEHINT");
			}
			return;
		}

		if(gsonString.equals("RESTART")){
			this.gState = GameState.SETUP;
			sendGameDataToClients("RESTART");
			addUser(null);
			sendGameDataToUser(getGameCreator(), "ENABLESTARTBUTTON");
		}
		
		// Wenn Spieler das Spiel beenden.
		if(gsonString.equals("CLOSE")){
			sendGameDataToClients("CLOSE");
			closeGame();
			return;
		}
		
		boolean isGuesser = isUserGuesser(user);		
		// Wenn ein Chooser seine Wortwahl mitteilt.
		if(!isGuesser) {
			allHints.add(gsonString.toLowerCase().strip());
			hintsGiven++;
			if(hintsGiven == chooser.size()) {
				checkAllHints();
				if(allHints.isEmpty()){
					sendGameDataToClients("NOVALIDHINT");
					sendGameDataToUser(guesser, "SHOWNEXTROUNDBUTTON");
				}else{
					sendGameDataToClients("VALIDHINT");
					sendGameDataToUser(guesser, "ENABLEGUESS");
				}
			return;
			}
		}
		
		// Wenn der Guesser das richtige Wort erraten hat.
		if(isGuesser) {
			String answerWord = gsonString;
			if(answerWord.toLowerCase().strip().equals(currentGuessWord.toLowerCase().strip())) {
				winCounter++;
				sendGameDataToClients("RIGHTGUESS");
			}
			else {
				sendGameDataToClients("WRONGGUESS");
			}
			sendGameDataToClients("WINSTATUS");
			sendGameDataToUser(guesser, "SHOWNEXTROUNDBUTTON");
			return;			
		}
		return;
	}

	@Override
	public ArrayList<User> getPlayerList() {
		// TODO Auto-generated method stub
		return playerList;
	}

	@Override
	public ArrayList<User> getSpectatorList() {
		// TODO Auto-generated method stub
		return spectatorList;
	}	

	@Override
	public String getGameData(String eventName, User user) {
		String gameData = "";

		if(eventName.equals("CLEAN")) {
			return gameData;
		}
		
		if(eventName.equals("WRONGGUESS")) {
			return "Das Wort wurde nicht erraten!";
		}
		
		if(eventName.equals("RIGHTGUESS")) {
			return "Das Wort wurde richtig erraten!";
		}

		if(eventName.equals("WINSTATUS")){
			gameData = "" + winCounter;
			return gameData;
		}

		if(eventName.equals("ROUNDSTATUS")){
			gameData = "" + turnCounter;
			return gameData;
		}

		if(eventName.equals("ROLESTATUS")){
			if(isUserGuesser(user)){
				gameData = "Ratender";
			}else{
				gameData = "Hinweisgeber";
			}
			return gameData;
		}
		
		if(eventName.equals("GIVEHINT")) {
			return currentGuessWord;
		}
		
		if(eventName.equals("WAIT")) {
			return "Warten Sie auf weitere Spieler!";
		}

		if(eventName.equals("CLOSE")){
			return "Das Spiel wurde beendet.";
		}

		if(eventName.equals("VALIDHINT")) {
			for (String string : allHints) {
				gameData += string + ",";
			}
			gameData = gameData.substring(0, gameData.length() - 1);
			return gameData;
		}	

		if(eventName.equals("NOVALIDHINT")) {
			return "Keine gueltigen Hinweise! Vielleicht in der naechsten Runde.";
		}

		if(eventName.equals("GAMEOVER")) {
			switch(winCounter){
				case 0:
				case 1:
				case 2:
				case 3:
					gameData = "Uebung macht den Meister.";
					break;
				case 4:
				case 5:
				case 6:
					gameData = "Das ist ein guter Anfang. Versucht es noch einmal!";
					break;
				case 7:
				case 8:
					gameData = "Das ist ein durchschnittliches Ergebnis. Koennt ihr das noch besser?";
					break;
				case 9:
				case 10:
					gameData = "Wow, gar nicht mal so schlecht!";
					break;
				case 11:
					gameData = "Genial! Dieses Ergebnis kann sich sehen lassen!";
					break;
				case 12:
					gameData = "Unglaublich! Eure Freunde mue*ssen beeindruckt sein!";
					break;
				case 13:
					gameData = "Perfektes Ergebnis! Schafft ihr das noch mal?";
					break;
				default:
					gameData = "Es ist ein Fehler aufgetreten...";
					break;
			}
			return gameData;
		}
		
		if(eventName.equals("PLAYER")) {
			for (User player : playerList) {
				gameData += player.getName() + ",";
			}
			gameData = gameData.substring(0, gameData.length() - 1);
			return gameData;
		}

		if(eventName.equals("ENOUGHPLAYER")) {
			if(user == getGameCreator()){
				gameData = "Das Spiel kann jetzt gestartet werden!";
			}else{
				gameData = "Warten Sie bitte, bis der Host das Spiel startet.";
			}
			return gameData;
		}

		return gameData;
	}

	@Override
	public void addUser(User user) {
		// TODO Auto-generated method stub
		if (playerList.size() < getMaxPlayerAmount() && !playerList.contains(user) && getGameState() != GameState.RUNNING) {
			if(user != null){
				playerList.add(user);
				if(user == getGameCreator()){
					sendGameDataToUser(user, "ENABLEHOSTVISION");
				}
			}
			if(getCurrentPlayerAmount() >= getMinPlayerAmount()){
				sendGameDataToClients("ENOUGHPLAYER");
			}else{
				sendGameDataToClients("WAIT");
			}
			sendGameDataToClients("PLAYER");
		}
	}

	@Override
	public void addSpectator(User user) {
		// TODO Auto-generated method stub
		spectatorList.add(user);
	}

	@Override
	public boolean isJoinable() {
		// TODO Auto-generated method stub
		if (playerList.size() < getMaxPlayerAmount() && getGameState() != GameState.RUNNING) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void playerLeft(User user) {
		// TODO Auto-generated method stub
		playerList.remove(user);
		sendGameDataToClients("PLAYER");
		if(playerList.size() < getMinPlayerAmount()){
			sendGameDataToClients("WAIT");
			if(getGameState() == GameState.RUNNING){
				sendGameDataToClients("CLOSE");
			}
		}
	}

	@Override
	public GameState getGameState() {
		// TODO Auto-generated method stub
		return this.gState;
	}
}
