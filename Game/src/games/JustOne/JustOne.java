package games.JustOne;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import gameClasses.Game;
import gameClasses.GameState;
import global.FileHelper;

import userManagement.User;

public class JustOne extends Game {

	public int teamWins = 0;

	public int hintsGiven = 0;

	public String currentGuessWord;

	public int turnCounter = 0;

	public Random randomGenerator;

	public User guesser = null;
	public ArrayList<User> chooser;

	public ArrayList<String> possibleWords = new ArrayList<String>();
	public ArrayList<String> selectedWords = new ArrayList<String>();

	public String s = "test";

	public String playerLeft = null;

	public ArrayList<User> playerList = new ArrayList<User>();
	public ArrayList<User> spectatorList = new ArrayList<User>();

	public boolean isUserGuesser(User user) {
		if (guesser.getName() == user.getName()) {
			return true;
		} else {
			return false;
		}
	}

	public String selectRandomWord() {
		int index = randomGenerator.nextInt(possibleWords.size());
		String word = possibleWords.get(index);
		return word;
	}

	public User selectRandomUser() {
		System.out.println("in der funktion.");
		int index = randomGenerator.nextInt(playerList.size());
		return playerList.get(index);
	}

	public void loadWords() throws FileNotFoundException, IOException {
		try (BufferedReader br = new BufferedReader(new FileReader("JustOne/Nomen.txt"))) {
			String word;
			while ((word = br.readLine()) != null) {
				possibleWords.add(word);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void setGameData() {
		// beide Seiten werden ausgewaehlt.
		guesser = selectRandomUser();
		chooser = new ArrayList<User>(playerList);
		chooser.remove(guesser);

		currentGuessWord = selectRandomWord();
		turnCounter = 0;
		selectedWords = new ArrayList<String>();
	}

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
			System.err.println("Loading of file JustOne/css/JustOne.css failed");
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
		return 7;
	}

	@Override
	public int getCurrentPlayerAmount() {
		// TODO Auto-generated method stub
		return playerList.size();
	}

	@Override
	public void execute(User user, String gsonString) {

		System.out.println("In execute");
		System.out.println("GSON: " + gsonString);

		if (gsonString.equals("HINT")) {
			for (User chooser : chooser) {
				sendGameDataToUser(chooser, "GUESSWORD");
			}

		}

		// NUR FUER TEST
		if (gsonString.equals("Test")) {

			sendGameDataToClients("Testantwort");
			return;
		}

		if (gsonString.equals("START")) {
			// GuessWort erstellen und Teams aufteilen.
			if (playerList.size() >= 3) {
				this.gState = GameState.RUNNING;
				possibleWords.add("Tennis");
				possibleWords.add("Essen");
				possibleWords.add("Stricken");
				possibleWords.add("Nonne");

				chooser = new ArrayList<User>(playerList);
				guesser = chooser.get(chooser.size() - 1);
				chooser.remove(chooser.size() - 1);

				currentGuessWord = possibleWords.get(1);
				for (User chooser : chooser) {
					sendGameDataToUser(chooser, "START");
				}
				sendGameDataToClients("ROUND");
				sendGameDataToClients("WIN");
//				sendGameDataToClients("msg");
				return;
			} else {
				sendGameDataToClients("WAIT");
				return;
			}
		}

		if (gsonString.equals(""))
			return;

		// TODO Auto-generated method stub
		if (this.gState == GameState.CLOSED)
			return;

		// Wenn Spieler das Spiel beenden.
		if (gsonString.equals("CLOSE")) {
			sendGameDataToClients("CLOSE");
			closeGame();
			return;
		}
		// Wenn Spieler das Spiel neustarten wollen.
		if (gsonString.equals("RESTART")) {
			if (playerList.size() >= 3)
				return;

			setGameData();
			this.gState = GameState.RUNNING;
			sendGameDataToClients("standardEvent");
			return;
		}
		// Wenn das Spiel nicht mehr laeuft.
		if (gState != GameState.RUNNING)
			return;

		// Nach 13 Zuegen ist das Spiel vorbei.
		if (turnCounter == 13) {
			this.gState = GameState.FINISHED;
			sendGameDataToClients("GameOver");
			sendGameDataToClients("msg");

			return;

		}

		// Wenn der Guesser das richtige Wort erraten hat.
		boolean isGuesser = isUserGuesser(user);
		if (isGuesser == true) {
			String answerWord = gsonString;
			if (answerWord.toLowerCase().strip().equals(currentGuessWord.toLowerCase().strip())) {
				teamWins++;
				turnCounter++;
				System.out.println("richtiges Wort erraten.");
				System.err.println(teamWins);
				sendGameDataToUser(user, "RIGHT");
				sendGameDataToClients("ROUND");
				// sendGameDataToClients("msg");
				sendGameDataToClients("WIN");
				if (teamWins == 10) {
					sendGameDataToClients("Gewonnen");
					return;
				} else if (turnCounter == 13) {
					sendGameDataToClients("GAMEOVER");
					sendGameDataToClients("msg");
					return;
				}
				chooser = new ArrayList<User>(playerList);
				guesser = chooser.get(chooser.size() - 2);
				chooser.remove(chooser.size() - 2);
				selectedWords = new ArrayList<String>();
				hintsGiven = 0;

				currentGuessWord = possibleWords.get(2);
				// Der bisherige Guesser wechselt das Team und
				// ein neuer Guesser wird gezogen.
				sendGameDataToClients("CLEAN");

				for (User chooser : chooser) {
					sendGameDataToUser(chooser, "HINT");
				}

				return;
			} else if (!answerWord.equals(currentGuessWord)) {
				turnCounter++;
				System.out.println("falsch!!");
				sendGameDataToUser(user, "WRONG");
				sendGameDataToClients("ROUND");
				// sendGameDataToClients("msg");

				if (turnCounter == 13) {
					sendGameDataToClients("GAMEOVER");
					sendGameDataToClients("msg");
					return;
				}
				sendGameDataToClients("CLEAN");

				chooser = new ArrayList<User>(playerList);
				guesser = chooser.get(chooser.size() - 2);
				chooser.remove(chooser.size() - 2);
				selectedWords = new ArrayList<String>();
				hintsGiven = 0;

				currentGuessWord = possibleWords.get(2);
				// Der bisherige Guesser wechselt das Team und
				// ein neuer Guesser wird gezogen.
				sendGameDataToClients("RIGHT");

				for (User chooser : chooser) {
					sendGameDataToUser(chooser, "HINT");
				}

				return;
			}
			return;

		}
		// Wenn ein Chooser seine Wortwahl mitteilt.
		boolean isChooser = !isUserGuesser(user);
		if (isChooser == true) {
			System.out.println("Hint erhalten.");
			if (selectedWords.contains(gsonString)) {

			} else {
				selectedWords.add(gsonString);
			}
			hintsGiven++;
			if (hintsGiven == chooser.size()) {
				sendGameDataToClients("WORDS");
				for (User chooser : chooser) {
					sendGameDataToUser(chooser, "HIDE");
					sendGameDataToUser(guesser, "READY");
				}
				return;
			}
		}

		// sendGameDataToClients("standardEvent");

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

	private String isHost(User user) {
		if (user == creator)
			return "HOST,";
		else
			return "NOTTHEHOST,";
	}

	@Override
	public String getGameData(String eventName, User user) {

		String gameData = "";

		if (eventName.equals("CLEAN")) {
			return gameData;
		}

		if (eventName.equals("HINT")) {
			gameData = currentGuessWord;
			return gameData;
		}

		if (eventName.equals("READY")) {
			return gameData;
		}

		if (eventName.equals("WRONG")) {
			gameData = "Das Wort wurde nicht erraten!";
			return gameData;
		}

		if (eventName.equals("msg")) {

			if (teamWins <= 3) {
				s = "Übung macht den Meister";
			} else if (teamWins >= 4 && teamWins <= 6) {
				s = "Das ist ein guter Anfang. Versucht es noch einmal!";
			} else if (teamWins >= 4 && teamWins <= 6) {
				s = "Das ist ein durchschnittliches Ergebnis. Könnt ihr das noch besser?";
			} else if (teamWins >= 4 && teamWins <= 6) {
				s = "Wow, gar nicht mal so schlecht!";
			} else if (teamWins >= 4 && teamWins <= 6) {
				s = "Genial! Dieses Ergebnis kann sich sehen lassen";
			} else if (teamWins >= 4 && teamWins <= 6) {
				s = "Unglaublich! Eure Freunde müssen beeindruckt sein!";
			} else if (teamWins >= 4 && teamWins <= 6) {
				s = "Perfektes Ergebnis! Schafft ihr das noch mal?";
			}

			gameData = "" + s;
			return gameData;
		}

		if (eventName.equals("RIGHT")) {
			gameData = "Das Wort wurde richtig erraten!";
			return gameData;
		}

		if (eventName.equals("ROUND")) {
			gameData = "" + turnCounter;
			return gameData;
		}

		if (eventName.equals("WIN")) {
			gameData = "" + teamWins;
			return gameData;
		}

		if (eventName.equals("HIDE")) {
			return gameData;
		}

		if (eventName.equals("START")) {
			gameData = currentGuessWord + ",";
			gameData += isHost(user);

			return gameData;
		}

		if (eventName.equals("GUESSWORD")) {
			gameData = currentGuessWord;
			return gameData;
		}

		if (eventName.equals("WAIT")) {
			gameData = "WAIT";
			return gameData;
		}

		if (eventName.equals("Testantwort")) {
			ArrayList<String> testWords = new ArrayList<String>();
			testWords.add("Essen");
			testWords.add("Fußball");
			testWords.add("Singen");
			testWords.add("Essen");
			testWords.add("Fußball");
			testWords.add("Singen");
			testWords.add("Essen");
			testWords.add("Fußball");
			testWords.add("Singen");
			testWords.add("Essen");
			testWords.add("Fußball");
			testWords.add("Singen");
			testWords.add("Fußball");
			testWords.add("Singen");

			for (String s : testWords) {
				gameData += s + ",";
			}
			gameData = gameData.substring(0, gameData.length() - 1);
			return gameData;
		}

		if (eventName.equals("standardEvent")) {
			gameData += isHost(user);
			gameData += currentGuessWord + ",";
			for (String selectedWord : selectedWords) {
				gameData += selectedWord + ",";
			}
			gameData = gameData.substring(0, gameData.length() - 1);
			return gameData;
		}

		if (eventName.equals("PLAYERLEFT")) {
			return playerLeft + " hat das Spiel verlassen!";
		}
		if (eventName.equals("CLOSE")) {
			return "CLOSE";
		}
		if (eventName.equals("WORDS")) {
			for (String s : selectedWords) {
				gameData += s + ",";
			}
			gameData = gameData.substring(0, gameData.length() - 1);
			return gameData;
		}
		if (eventName.equals("GUESSWORD")) {
			gameData = currentGuessWord;
			return gameData;
		}

		if (playerList.size() < 3) {
			gameData += "Warte Auf weiteren Spieler...";
			gameData += isHost(user);
			return gameData;
		}

		if (this.gState == GameState.FINISHED) {
			if (turnCounter == 13) {
				gameData += "Das Spiel ist vorbei.";
				gameData += isHost(user);
				return gameData;
			}
		}
		return gameData;
	}

	public void startGame() {
		if (playerList.size() >= 3) {
			sendGameDataToClients("START");
		}
	}

	@Override
	public void addUser(User user) {
		// TODO Auto-generated method stub
		if (playerList.size() < 7 && !playerList.contains(user)) {
			playerList.add(user);

		}
		if (playerList.size() == 7) {
			this.gState = GameState.RUNNING;
			sendGameDataToClients("START");
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
		if (playerList.size() < 7) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void playerLeft(User user) {
		// TODO Auto-generated method stub
		playerList.remove(user);
		playerLeft = user.getName();
		sendGameDataToClients("PLAYERLEFT");

	}

	@Override
	public GameState getGameState() {
		// TODO Auto-generated method stub
		return this.gState;
	}

}
