package com.example.wordle_clone

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.github.jinatonic.confetti.CommonConfetti

object FourLetterWordList {
    val fourLetterWords =
        "Area,Army,Baby,Back,Ball,Band,Bank,Base,Bill,Body,Book,Call,Card,Care,Case,Cash,City,Club,Cost,Date,Deal,Door,Duty,East,Edge,Face,Fact,Farm,Fear,File,Film,Fire,Firm,Fish,Food,Foot,Form,Fund,Game,Girl,Goal,Gold,Hair,Half,Hall,Hand,Head,Help,Hill,Home,Hope,Hour,Idea,Jack,John,Kind,King,Lack,Lady,Land,Life,Line,List,Look,Lord,Loss,Love,Mark,Mary,Mind,Miss,Move,Name,Need,News,Note,Page,Pain,Pair,Park,Part,Past,Path,Paul,Plan,Play,Post,Race,Rain,Rate,Rest,Rise,Risk,Road,Rock,Role,Room,Rule,Sale,Seat,Shop,Show,Side,Sign,Site,Size,Skin,Sort,Star,Step,Task,Team,Term,Test,Text,Time,Tour,Town,Tree,Turn,Type,Unit,User,View,Wall,Week,West,Wife,Will,Wind,Wine,Wood,Word,Work,Year,Bear,Beat,Blow,Burn,Call,Care,Cast,Come,Cook,Cope,Cost,Dare,Deal,Deny,Draw,Drop,Earn,Face,Fail,Fall,Fear,Feel,Fill,Find,Form,Gain,Give,Grow,Hang,Hate,Have,Head,Hear,Help,Hide,Hold,Hope,Hurt,Join,Jump,Keep,Kill,Know,Land,Last,Lead,Lend,Lift,Like,Link,Live,Look,Lose,Love,Make,Mark,Meet,Mind,Miss,Move,Must,Name,Need,Note,Open,Pass,Pick,Plan,Play,Pray,Pull,Push,Read,Rely,Rest,Ride,Ring,Rise,Risk,Roll,Rule,Save,Seek,Seem,Sell,Send,Shed,Show,Shut,Sign,Sing,Slip,Sort,Stay,Step,Stop,Suit,Take,Talk,Tell,Tend,Test,Turn,Vary,View,Vote,Wait,Wake,Walk,Want,Warn,Wash,Wear,Will,Wish,Work,Able,Back,Bare,Bass,Blue,Bold,Busy,Calm,Cold,Cool,Damp,Dark,Dead,Deaf,Dear,Deep,Dual,Dull,Dumb,Easy,Evil,Fair,Fast,Fine,Firm,Flat,Fond,Foul,Free,Full,Glad,Good,Grey,Grim,Half,Hard,Head,High,Holy,Huge,Just,Keen,Kind,Last,Late,Lazy,Like,Live,Lone,Long,Loud,Main,Male,Mass,Mean,Mere,Mild,Nazi,Near,Neat,Next,Nice,Okay,Only,Open,Oral,Pale,Past,Pink,Poor,Pure,Rare,Real,Rear,Rich,Rude,Safe,Same,Sick,Slim,Slow,Soft,Sole,Sore,Sure,Tall,Then,Thin,Tidy,Tiny,Tory,Ugly,Vain,Vast,Very,Vice,Warm,Wary,Weak,Wide,Wild,Wise,Zero,Ably,Afar,Anew,Away,Back,Dead,Deep,Down,Duly,Easy,Else,Even,Ever,Fair,Fast,Flat,Full,Good,Half,Hard,Here,High,Home,Idly,Just,Late,Like,Live,Long,Loud,Much,Near,Nice,Okay,Once,Only,Over,Part,Past,Real,Slow,Solo,Soon,Sure,That,Then,This,Thus,Very,When,Wide"

    fun getAllFourLetterWords(): List<String> {
        return fourLetterWords.split(",")
    }

    fun getRandomFourLetterWord(): String {
        val allWords = getAllFourLetterWords()
        return allWords.random().uppercase()
    }
}

class MainActivity : AppCompatActivity() {

    private var wordToGuess = FourLetterWordList.getRandomFourLetterWord()
    private var currentGuess = 1
    private var currLevel = 0
    private var guessable = true
    private var finished = false

    private val textViewsMap = HashMap<String, TextView>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.v("WordleWord", wordToGuess)

        val guessBtn = findViewById<Button>(R.id.guess)
        val wordG = findViewById<TextView>(R.id.word)
        val resetBtn = findViewById<Button>(R.id.reset)
        val container = findViewById<ConstraintLayout>(R.id.container)

        val numRows = 4
        val numCols = 3

        val r = resources
        val name = packageName
        var num = 1
        for (rowIndex in 1..numCols) {
            for (colIndex in 1..numRows) {
                val textViewId = "G$rowIndex" + "L$colIndex"
                Log.v("WordleWord", "$textViewId, $num")
                val textView = findViewById<TextView>(r.getIdentifier(textViewId, "id", name))
                textViewsMap[num.toString()] = textView
                textView.setOnClickListener {
                    showKeyboard()
                }
                num++
            }
        }

        guessBtn.setOnClickListener {
            Log.v("state", "guess: $currentGuess, word: $wordToGuess, $guessable, $currLevel")
            if ((currentGuess - 1) % 4 == 0 && (currentGuess - 1 != 0)) {
                val lst = checkGuess()
                currLevel += 4
                currentGuess = 1
                guessable = true
                if (lst.joinToString(separator = "") == "OOOO") {
                    CommonConfetti.rainingConfetti(container, intArrayOf(Color.RED, Color.GREEN, Color.BLUE)).oneShot()
                    guessable = false
                    wordG.text = wordToGuess
                    Toast.makeText(this, "You Are Correct, click reset to start again", Toast.LENGTH_SHORT).show()
                    finished = true
                }
                if (currLevel == 12 && guessable) {
                    finished = true
                    guessable = false
                    wordG.text = wordToGuess
                    Toast.makeText(this, "Sorry, click reset to start again", Toast.LENGTH_SHORT).show()
                }

            } else {
                if (!finished)
                    Toast.makeText(this, "Complete the guess", Toast.LENGTH_SHORT).show()
            }
        }

        resetBtn.setOnClickListener {
            wordToGuess = FourLetterWordList.getRandomFourLetterWord()
            currentGuess = 1
            currLevel = 0
            guessable = true
            finished = false
            wordG.text = "_ _ _ _"
            for (i in 1..12) {
                textViewsMap[i.toString()]?.text = ""
                textViewsMap[i.toString()]?.setBackgroundColor(Color.parseColor("#A1A1A1"))
            }
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_DOWN) {
            val keyCode = event.keyCode
            val keyChar = event.unicodeChar.toChar()
            Log.v("state", "guess: $currentGuess, key: $keyChar, word: $wordToGuess, $guessable, $currLevel")
            Log.v("WordleWord", "$keyCode, $keyChar")
            if (keyCode == 67 && currentGuess > 1) {
                currentGuess--
                textViewsMap[(currentGuess + currLevel).toString()]?.text = ""
                guessable = true
            }

            if (keyChar.isLetter() && guessable) {
                textViewsMap[(currentGuess + currLevel).toString()]?.text = keyChar.uppercase()
                if (currentGuess + currLevel <= 12) {
                    currentGuess += 1
                } else {
                    guessable = false
                }
                if ((currentGuess + currLevel - 1) % 4 == 0) {
                    guessable = false
                }
            }
            return true
        }

        return super.dispatchKeyEvent(event)
    }

    private fun createMap(word: String): MutableMap<Char, Int> {
        val mapC = mutableMapOf<Char, Int>()
        for (c in word) {
            mapC[c] = mapC.getOrDefault(c, 0) + 1
        }
        return mapC
    }

    private fun check(guess: String): List<String> {
        val mapC = createMap(wordToGuess)
        val result = mutableListOf("X", "X", "X", "X")

        for (i in 0 until 4) {
            if (guess[i] == wordToGuess[i] && mapC[guess[i]]!! > 0) {
                mapC[guess[i]] = mapC[guess[i]]!! - 1
                result[i] = "O"
            }
        }

        for (i in 0 until 4) {
            if (guess[i] != wordToGuess[i] && guess[i] in mapC && mapC[guess[i]]!! > 0) {
                mapC[guess[i]] = mapC[guess[i]]!! - 1
                result[i] = "I"
            }
        }

        return result
    }

    private fun checkGuess(): List<String> {
        var guess = ""
        for (i in 1 until 5) {
            val letter = textViewsMap[(currentGuess + currLevel - i).toString()]?.text ?: ""
            Log.v("guess", "$letter, $i, $currentGuess, $currLevel")
            guess += letter
        }
        guess = guess.reversed()
        val guessStr = check(guess)

        for (i in 1 until 5) {
            when (guessStr[4 - i]) {
                "O" -> {
                    textViewsMap[(currentGuess + currLevel - i).toString()]?.setBackgroundColor(Color.GREEN)
                }
                "I" -> {
                    textViewsMap[(currentGuess + currLevel - i).toString()]?.setBackgroundColor(Color.YELLOW)
                }
                else -> {
                    textViewsMap[(currentGuess + currLevel - i).toString()]?.setBackgroundColor(Color.RED)
                }
            }
        }

        Log.v("guess3", "$guessStr")
        Log.v("guess", "$guess, $currentGuess")
        guessable = false
        return guessStr
    }

    private fun showKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }
}
