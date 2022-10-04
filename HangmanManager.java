// add imports as necessary
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.TreeMap;
import java.util.Collections;
import java.util.Set;

/**
 * Manages the details of EvilHangman. This class keeps
 * tracks of the possible words from a dictionary during
 * rounds of hangman, based on guesses so far.
 */
public class HangmanManager
{
    // instance variables / fields
    private HashMap<String, ArrayList<String>> mapOfWordFamilies;
    private ArrayList<String> wordList;
    private Set<String> originalSet;
    private boolean debug;
    private int wordLength;
    private int numWrongGuess;
    private HangmanDifficulty difficulty;
    private String currentPattern;
    private ArrayList<Character> guesses;
    private int diffCounter;

    /**
     * Create a new HangmanManager from the provided set of words and phrases.
     * pre: words != null, words.size() > 0
     *
     * @param words   A set with the words for this instance of Hangman.
     * @param debugOn true if we should print out debugging to System.out.
     */
    public HangmanManager(Set<String> words, boolean debugOn)
    {
        if (words == null || words.size() <= 0)
        {
            throw new IllegalArgumentException("words can't equal null and words.size must be " +
                    "greater than 0");
        }
        wordList = new ArrayList<>();
        wordList.addAll(words);
        debug = debugOn;
        originalSet = new TreeSet<>();
        originalSet.addAll(words);
    }

    /**
     * Create a new HangmanManager from the provided set of words and phrases.
     * Debugging is off.
     * pre: words != null, words.size() > 0
     *
     * @param words A set with the words for this instance of Hangman.
     */
    public HangmanManager(Set<String> words)
    {
        if (words == null || words.size() <= 0)
        {
            throw new IllegalArgumentException("words cant be null and words.size() must be " +
                    "greater than zero");
        }
        wordList = new ArrayList<>();
        wordList.addAll(words);
        debug = false;
    }

    /**
     * Get the number of words in this HangmanManager of the given length.
     * pre: none
     *
     * @param length The given length to check.
     * @return the number of words in the original Dictionary
     * with the given length
     */
    public int numWords(int length)
    {
        int count = 0;
        for (int i = 0; i < wordList.size(); i++)
        {
            if (wordList.get(i).length() == length)
            {
                count++;
            }
        }
        return count;
    }

    /**
     * Get for a new round of Hangman. Think of a round as a
     * complete game of Hangman.
     *
     * @param wordLen    the length of the word to pick this time.
     *                   numWords(wordLen) > 0
     * @param numGuesses the number of wrong guesses before the
     *                   player loses the round. numGuesses >= 1
     * @param diff       The difficulty for this round.
     */
    public void prepForRound(int wordLen, int numGuesses, HangmanDifficulty diff)
    {
        // check preconditions
        if (wordLen <= 0 || numGuesses < 1)
        {
            throw new IllegalArgumentException("wordLen must be greater than 0 and numguesses " +
                    "must be greater than or equal to 1");
        }
        // prepares for a new Round. Sets all variables to their default/starting values
        wordList = new ArrayList<>();
        wordList.addAll(originalSet);
        diffCounter = 1;
        wordLength = wordLen;
        numWrongGuess = numGuesses;
        difficulty = diff;
        String current = "";
        for (int i = 0; i < wordLen; i++)
        {
            current += "-";
        }
        currentPattern = current;
        guesses = new ArrayList<>();
        for (int i = wordList.size() - 1; i >= 0; i--)
        {
            if (wordList.get(i).length() != wordLength)
            {
                wordList.remove(i);
            }
        }
        mapOfWordFamilies = new HashMap<>();
    }

    /**
     * The number of words still possible (live) based on the guesses so far.
     * Guesses will eliminate possible words.
     *
     * @return the number of words that are still possibilities based on the
     * original dictionary and the guesses so far.
     */
    public int numWordsCurrent()
    {
        return wordList.size();
    }

    /**
     * Get the number of wrong guesses the user has left in
     * this round (game) of Hangman.
     *
     * @return the number of wrong guesses the user has left
     * in this round (game) of Hangman.
     */
    public int getGuessesLeft()
    {
        return numWrongGuess;
    }

    /**
     * Return a String that contains the letters the user has guessed
     * so far during this round.
     * The characters in the String are in alphabetical order.
     * The String is in the form [let1, let2, let3, ... letN].
     * For example [a, c, e, s, t, z]
     *
     * @return a String that contains the letters the user
     * has guessed so far during this round.
     */
    public String getGuessesMade()
    {
        Collections.sort(guesses);
        StringBuilder guessesMade = new StringBuilder("[");
        for (int i = 0; i < guesses.size(); i++)
        {
            guessesMade.append(guesses.get(i));
            if (i < guesses.size() - 1)
            {
                guessesMade.append(", ");
            }
        }
        guessesMade.append("]");
        return guessesMade.toString();
    }

    /**
     * Check the status of a character.
     *
     * @param guess The characater to check.
     * @return true if guess has been used or guessed this round of Hangman,
     * false otherwise.
     */
    public boolean alreadyGuessed(char guess)
    {
        if (guesses.contains(guess))
        {
            return true;
        }
        return false;
    }

    /**
     * Get the current pattern. The pattern contains '-''s for
     * unrevealed (or guessed) characters and the actual character
     * for "correctly guessed" characters.
     *
     * @return the current pattern.
     */
    public String getPattern()
    {
        return currentPattern;
    }

    /**
     * Update the game status (pattern, wrong guesses, word list),
     * based on the give guess.
     *
     * @param guess pre: !alreadyGuessed(ch), the current guessed character
     * @return return a tree map with the resulting patterns and the number of
     * words in each of the new patterns.
     * The return value is for testing and debugging purposes.
     */
    public TreeMap<String, Integer> makeGuess(char guess)
    {
        // check preconditions
        if (alreadyGuessed(guess))
        {
            throw new IllegalStateException(guess + " has already been guessed");
        }
        for (int i = wordList.size() - 1; i >= 0; i--)
        {
            if (wordList.get(i).length() != wordLength)
            {
                wordList.remove(i);
            }
        }
        // adds the guess to the list of current guesses
        guesses.add(guess);
        // creates new Tree Map of String and Integers word FamiliesCount. Populates the Tree Map
        // with keys of all possible patterns and the amount of each pattern that is possible
        // Populates Hashmap mapOfWordFamilies with the same keys, and values that represent an
        // arraylist of Strings of all the words that fit the key pattern
        TreeMap<String, Integer> wordFamiliesCount = new TreeMap<>();
        mapOfWordFamilies = new HashMap<>();
        makeMaps(wordFamiliesCount, mapOfWordFamilies, guess);
        String cheatingKey = "";
        // Calls the cheatKey method to find the hardest or second hardest key to use depending on
        // difficulty
        cheatingKey = cheatKey(cheatingKey, mapOfWordFamilies, difficulty);
        // if the new cheatingKey is still the same as the currentPattern it was a wrong guess,
        // else set currentPattern to the cheatingKey
        if (cheatingKey.equals(currentPattern))
        {
            numWrongGuess--;
        }
        else
        {
            currentPattern = cheatingKey;
        }
        wordList = mapOfWordFamilies.get(cheatingKey);

        return wordFamiliesCount;
    }

    /**
     * Gets the hardest or second hardest word list from the mapOfWordFamilies based off the
     * difficulty
     * pre: cheatingKey != null, mapOfWordFamilies != null
     */
    private String cheatKey(String cheatingKey,
                            HashMap<String, ArrayList<String>> mapOfWordFamilies,
                            HangmanDifficulty difficulty)
    {
        // check preconditions
        if (cheatingKey == null || mapOfWordFamilies == null)
        {
            throw new IllegalArgumentException("cheatingKey != null and mapOfWordFamilies != null");
        }
        // checks if difficulty is medium or easy and it's time to get the second hardest word.
        // for medium the 2nd hardest is every 3 words and for easy its every other word
        int mediumModulus = 4;
        int easyModulus = 2;
        if ((difficulty == HangmanDifficulty.MEDIUM && diffCounter % mediumModulus == 0) ||
                (difficulty == HangmanDifficulty.EASY && diffCounter % easyModulus == 0))
        {
            diffCounter++;
            if (mapOfWordFamilies.size() > 1)
            {
                String toBeRemoved = getHardestCheatKey(cheatingKey, mapOfWordFamilies);
                mapOfWordFamilies.remove(toBeRemoved);
            }
            return getHardestCheatKey(cheatingKey, mapOfWordFamilies);
        }
        else if (difficulty == HangmanDifficulty.MEDIUM || difficulty == HangmanDifficulty.EASY)
        {
            diffCounter++;
            return getHardestCheatKey(cheatingKey, mapOfWordFamilies);
        }
        return getHardestCheatKey(cheatingKey, mapOfWordFamilies);
    }

    /**
     * Gets the hardest word key from the mapOfWordFamilies(the pattern that has the most words)
     * if there are 2 or more with the same words, then the word with the most blanks is the hardest
     * if 2 or more patterns have the same amount of words and blanks, the hardest key is the one
     * with lowest ASCIIbetical value;
     * pre: Already checked in calling method
     */
    private String getHardestCheatKey(String cheatingKey,
                                      HashMap<String, ArrayList<String>> mapOfWordFamilies)
    {
        // loops through every key(word pattern) finds the key with the most words. if 2 keys
        // have the same number of words then the hardest is the one with more blanks. if they
        // have the same blanks the one with a lower ASCII value is the hardestKey
        int max = 0;
        for (String word : mapOfWordFamilies.keySet())
        {
            ArrayList<String> values = mapOfWordFamilies.get(word);
            if (values.size() > max)
            {
                max = values.size();
                cheatingKey = word;
            }
            else if (values.size() == max)
            {
                int blanksDifference = countBlanks(word, cheatingKey);
                if (blanksDifference > 0)
                {
                    max = values.size();
                    cheatingKey = word;
                }
                else if (blanksDifference == 0)
                {
                    int asciiDiff = word.compareTo(cheatingKey);
                    if (asciiDiff < 0)
                    {
                        cheatingKey = word;
                    }
                }
            }
        }
        return cheatingKey;
    }

    /**
     * Fills the maps with corresponding keys and values from the WordList and the guessed word
     * pre: Already checked preCons in calling method
     */
    private void makeMaps(TreeMap<String, Integer> wordFamiliesCount, HashMap<String,
            ArrayList<String>> mapOfWordFamilies, char guess)
    {
        //check preconditions
        // loops through the wordList. If a word has a char equal to the guessed char the secret
        // pattern is set to dashes with the corresponding character. Checks to see if that
        // pattern is already in the maps. If already in the maps add to the current maps values
        // else create new values and put the new key and its corresponding initial value into
        // the map
        for (int i = 0; i < wordList.size(); i++)
        {
            String secretPattern = "";
            String current = wordList.get(i);
            for (int j = 0; j < wordLength; j++)
            {
                if (current.charAt(j) == guess)
                {
                    secretPattern += guess;
                }
                else
                {
                    secretPattern += currentPattern.charAt(j);
                }
            }
            if (!mapOfWordFamilies.containsKey(secretPattern))
            {
                ArrayList<String> values = new ArrayList<>();
                values.add(current);
                mapOfWordFamilies.put(secretPattern, values);
                wordFamiliesCount.put(secretPattern, 1);
            }
            else
            {
                mapOfWordFamilies.get(secretPattern).add(current);
                int oldValue = wordFamiliesCount.get(secretPattern);
                wordFamiliesCount.put(secretPattern, oldValue + 1);
            }
        }
    }

    /**
     * Counts the difference in blanks between two strings
     * pre: Already checked preCons in calling method
     */
    private static int countBlanks(String a, String b)
    {
        int countA = 0;
        int countB = 0;
        for (int i = 0; i < a.length(); i++)
        {
            if (a.charAt(i) == '-')
            {
                countA++;
            }
            if (b.charAt(i) == '-')
            {
                countB++;
            }
        }
        return countA - countB;
    }

    /**
     * Return the secret word this HangmanManager finally ended up
     * picking for this round.
     * If there are multiple possible words left one is selected at random.
     * <br> pre: numWordsCurrent() > 0
     *
     * @return return the secret word the manager picked.
     */
    public String getSecretWord()
    {
        if (numWordsCurrent() <= 0)
        {
            throw new IllegalStateException("There must be at least one word in the word list");
        }
        wordList = new ArrayList<>();
        wordList.addAll(originalSet);
        if (debug)
        {
            System.out.println("Game Is Over!!!");
        }
        int randomWord = (int) (Math.random() * mapOfWordFamilies.get(currentPattern).size());
        return mapOfWordFamilies.get(currentPattern).get(randomWord);
    }
}
