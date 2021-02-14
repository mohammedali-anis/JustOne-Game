//package games.JustOne;
//
//import games.JustOne.*;
//
//import java.io.BufferedReader;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Random;
//
//import gameClasses.GameState;
//import gameClasses.GameState;
//import userManagement.User;
//
//public class GameData {
//	public static String [] gameData = new String[9];
//	
//	public static int teamWins = 0;
//	
//	public static String currentGuessWord;
//	private String guessedWord;
//	
//	public static Random randomGenerator;
//	
//	public static ArrayList<User> chooser = new ArrayList<User>();
//	public static User guesser = null;
//	
//	static ArrayList<String> possibleWords = new ArrayList<String>();
//	static ArrayList<String> selectedWords = new ArrayList<String>();
//	
//	public static ArrayList<User> playerList = new ArrayList<User>();
//	public static ArrayList<User> spectatorList = new ArrayList<User>();
//	
//	public static boolean isUserGuesser(User user) {
//		if(guesser.getName() == user.getName()) {
//			return true;
//		}
//		else {
//			return false;
//		}
//	}
//
//	
//	public static String selectRandomWord() {
//		int index = randomGenerator.nextInt(GameData.possibleWords.size());
//		String word = GameData.possibleWords.get(index);
//		return word;
//	}
//	
//	public static User selectRandomUser() {
//		int index = randomGenerator.nextInt(chooser.size());
//		return chooser.get(index);
//	}
//	
//	
//	public String getGameData(String eventName, User user) {
//		String gameData = "";
//		if(eventName.equals("PLAYERLEFT")){
//			return playerLeft + " hat das Spiel verlassen!";
//		}
//		if(eventName.equals("CLOSE")){
//			return "CLOSE";
//		}
//		
//		int[] grid = getGridStatus();
//
//		for (int i = 0; i < 9; i++) {
//			gameData += String.valueOf(grid[i]);
//			gameData += ',';
//		}
//		
//		if(GameData.playerList.size()<2){
//			gameData += "Warte Auf 2ten Spieler...";
//			gameData += isHost(user);
//			return gameData;
//		}
//
//		if (this.gState == GameState.FINISHED) {
//			if (turnCounter == 9 && !gameOver()){
//				gameData += "Unentschieden!";
//				gameData += isHost(user);
//				return gameData;
//			}
//			if (playerTurn.equals(user)) {
//				gameData += "Du hast verloren!";
//			} else
//				gameData += "Du hast gewonnen!";
//		}
//
//		else if (playerTurn.equals(user)) {
//			gameData += "Du bist dran!";
//		} else
//			gameData += playerTurn.getName() + " ist dran!";
//
//		if (GameData.playerList.indexOf(user) == 0)
//			gameData += " (x)";
//		else
//			gameData += " (o)";
//		
//		gameData += isHost(user);
//
//		return gameData;
//	}
//
//	
//	public static void setGameData() {
//		GameData.gameData[0] = GameData.selectRandomWord();
//		GameData.gameData[1] = null;
//		int numberOfPlayers = GameData.playerList.size();
//		int counter = 2;
//		for(User user : GameData.playerList) {
//			if(counter != numberOfPlayers + 1) {
//				gameData[counter] = user.getName();
//				counter++;
//			}
//		}
//		int newSize = gameData.length;
//		int diff = 9 - newSize;
//		for(int i = 0; i < diff; i ++) {
//			gameData[9-i] = null;
//		}
//		for(String s : gameData) {
//			System.out.println(s);
//		}
//	}
//	public static void main(String[] args) {
//		setGameData();
//	}
//	
//	public String getCurrentGuessWord() {
//		return currentGuessWord;
//	}
//	public void setCurrentGuessWord(String currentGuessWord) {
//		this.currentGuessWord = currentGuessWord;
//	}
//	public ArrayList<String> getPossibleWords() {
//		return possibleWords;
//	}
//	public void setPossibleWords(ArrayList<String> possibleWords) {
//		this.possibleWords = possibleWords;
//	}
//	public ArrayList<String> getSelectedWords() {
//		return selectedWords;
//	}
//	public void setSelectedWords(ArrayList<String> selectedWords) {
//		this.selectedWords = selectedWords;
//	}
//	public String getGuessedWord() {
//		return guessedWord;
//	}
//	public void setGuessedWord(String guessedWord) {
//		this.guessedWord = guessedWord;
//	}
//	
//	public static void loadWords() throws FileNotFoundException, IOException{
//		try(BufferedReader br = new BufferedReader(new FileReader("JustOne/Nomen.txt"))){
//			String word;
//			while((word = br.readLine()) != null) {
//				GameData.possibleWords.add(word);
//			}
//		}
//		catch(Exception e) {
//			System.out.println(e.getMessage());
//		}
//	}
//	
//}
