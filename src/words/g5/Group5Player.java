package words.g5;

import words.core.Letter;
import words.core.PlayerBids;
import words.core.SecretState;
import words.core.Word;
import words.core.ScrabbleValues;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public class Group5Player extends words.core.Player {

    private HashMap<Character, Double> freqInWords = new HashMap<>();
    private HashMap<Character, Double> freqInLetters = new HashMap<>();

    private HashMap<Character, Double> playerLetterFreq = new HashMap<>();

    private int maxVowelBid = 8;
    private int vowelBid = 2;

    private boolean isVowel(char c) {
        return "AEIOUaeiou".indexOf(c) >= 0;
    }

    private double countOccurrences(char c) {
        double count = 0.0;
        Word[] wordList = super.wordlist;

        for (Word w : wordList) {
            String wString = w.word;
            for (int i=0; i<wString.length(); ++i) {
                if (c == '?') {
                    ++ count;
                }
                else if (c == wString.charAt(i)) {
                    ++count;
                }
            }
        }

        return count;
    }

    private void initFreqMaps() {
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        double[] freq = {9, 2, 2, 4, 12, 2, 3, 2, 9, 1, 1, 4, 2, 6, 8, 2, 1, 6, 4, 6, 4, 2, 2, 1, 2, 1};

        double totalNumChars = countOccurrences('?');
        for (int i=0; i<26; ++i) {
            freqInLetters.put(chars[i], freq[i] / 98.0);
            double charFreq = countOccurrences(chars[i]);
            freqInWords.put(chars[i], charFreq / totalNumChars);
        }
    }

    private int countLetterList(List<Character> letters, Character letter) {
        int count = 0;

        for (int i=0; i<letters.size(); ++i) {
            if (letters.get(i) ==letter) {
                ++count;
            }
        }

        return count;
    }

    private int countLetterArr(char[] chars, char letter) {
        int count = 0;

        for (int i=0; i<chars.length; ++i) {
            if (chars[i] ==letter) {
                ++count;
            }
        }

        return count;
    }

    @Override
    public int bid(Letter bidLetter, List<PlayerBids> playerBidList, int totalRounds, ArrayList<String> playerList, SecretState secretstate, int playerID) {
        String currentBest = returnWord();

        //if we can already make a 7+ letter word, bid only 1
        if (currentBest.length() >= 7) {
            return Math.min(1, secretstate.getScore());
        }

        //if we have vowels and consonants in an unhealthy ratio, bid more for vowels
        double numVowels = 0.0;
        double numConsonants = 0.0;
        for (Character c : myLetters) {
            if (isVowel(c)) {
                ++numVowels;
            }
            else {
                ++numConsonants;
            }
        }

        if ((numVowels / numConsonants) < 0.25) {
            if (vowelBid < maxVowelBid) {
                vowelBid += 1;
            }
        }

        Character bidChar = bidLetter.getCharacter();
        int numChar = countLetterList(myLetters, bidChar);

        myLetters.add(bidChar);
        String possibleBest = returnWord();
        myLetters.remove(bidChar);

        int numCharInBest = countLetterArr(possibleBest.toCharArray(), bidChar);
        int bid;

        //if we need this character to make the best word given our current letters
        if (possibleBest.contains(bidChar.toString()) && numChar < numCharInBest) {
            if (possibleBest.length() >= 7) {
                bid = 15;
            }
            else {
                bid = ScrabbleValues.getWordScore(possibleBest) / 3 + 1;
            }
        }
        else if (isVowel(bidChar)) {
            bid = vowelBid;
        }
        else {
            bid = bidLetter.getValue();
        }

        return Math.min(bid, secretstate.getScore());
    }
}
