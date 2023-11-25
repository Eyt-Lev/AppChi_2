@file:Suppress("SpellCheckingInspection")

package com.nosh.appchi.productivity.langs

    import android.Manifest
    import android.animation.Animator
    import android.animation.AnimatorInflater
    import android.animation.AnimatorSet
    import android.animation.ValueAnimator
    import android.annotation.SuppressLint
    import android.content.Intent
    import android.content.pm.PackageManager
    import android.graphics.Bitmap
    import android.graphics.Color
    import android.net.Uri
    import android.os.Bundle
    import android.os.Environment
    import android.provider.MediaStore
    import android.speech.tts.TextToSpeech
    import android.text.Editable
    import android.text.InputType
    import android.text.TextWatcher
    import android.util.Log
    import android.view.Menu
    import android.view.MenuItem
    import android.widget.*
    import androidx.appcompat.app.AlertDialog
    import androidx.appcompat.app.AppCompatActivity
    import androidx.cardview.widget.CardView
    import androidx.core.app.ActivityCompat
    import androidx.core.content.ContextCompat
    import androidx.core.content.FileProvider
    import androidx.core.content.res.ResourcesCompat
    import androidx.core.view.allViews
    import androidx.core.view.isVisible
    import androidx.recyclerview.widget.LinearLayoutManager
    import androidx.recyclerview.widget.RecyclerView
    import com.nosh.appchi.R
    import java.io.*
    import java.util.*
    import java.util.concurrent.TimeUnit
    import kotlin.properties.Delegates

@SuppressLint("SetTextI18n")
class Lang : AppCompatActivity() {
    private var continToQuiz by Delegates.notNull<Boolean>()
    private var continToHear by Delegates.notNull<Boolean>()
    private var continToTyping by Delegates.notNull<Boolean>()
    private var continToTf by Delegates.notNull<Boolean>()
    private lateinit var wordsNameNotSved: String
    private lateinit var rightRadioButton: RadioButton
    private lateinit var wordsName: String
    private lateinit var tts: TextToSpeech
    private lateinit var cTrueOrFalse: CheckBox
    private lateinit var cHear: CheckBox
    private lateinit var cLearn: CheckBox
    private lateinit var cType: CheckBox
    private lateinit var cOptions: CheckBox
    private lateinit var allCbs: List<CheckBox>
    private val englishWords = mutableListOf<String>()
    private val hebrewWords = mutableListOf<String>()
    private val en = mutableListOf<String>()
    private val he = mutableListOf<String>()
    private val failedEn = mutableListOf<String>()
    private val failedHe = mutableListOf<String>()
    private var score = 0
    private var currentQuestion = 0
    private var totalQuestions = 0
    private var questionsDone = 0
    private var startTime = 0L
    private var totalTime = 0L
    private var random = 0
    private var wordsNow = ""
    private lateinit var word: String
    private var isFront = true

    companion object {
        private const val READ_REQUEST_CODE = 42
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        main()
    }
    private fun main(){
        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", MODE_PRIVATE)
        val allEntries = sharedPreference.all
        val arrayly = allEntries.filter { it.key.endsWith("_words_en") }.keys

        fun setNameDialog() {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(R.string.add_wordlist)
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_TEXT
            builder.setView(input)
            builder.setPositiveButton(R.string.done) { _, _ ->
                val name = input.text.toString()
                if (name.isNotEmpty()) {
                    if (!arrayly.contains(name + "_words_en")) {
                        wordsNameNotSved = name
                        words(false, "", "")
                    }
                } else {
                    Toast.makeText(this, R.string.field_empty, Toast.LENGTH_SHORT).show()
                }
            }
            builder.setNegativeButton(R.string.cancel) { _, _ -> }
            val dialog = builder.create()
            dialog.show()
        }
        setContentView(R.layout.p_a_langs)
        val savedWords: RecyclerView = findViewById(R.id.saved_words)
        val thers: LinearLayout = findViewById(R.id.theres)
        val noSaved: TextView = findViewById(R.id.no_saved)
        val newBtn: Button = findViewById(R.id.newWordsBtn)
        val importListBtn: Button = findViewById(R.id.import_list)
        //saved words
        thers.isVisible = arrayly.isNotEmpty()
        if (arrayly.isNotEmpty()) {
            val adapter = SavedAdapter(arrayly.toList(), this)
            savedWords.adapter = adapter
            savedWords.layoutManager = LinearLayoutManager(this)
            adapter.setOnItemClickListener(object : OnItemClickListener {
                override fun onItemClick(name: String) {
                    wordsName = name
                    words(
                        true,
                        sharedPreference.getString(name + "_words_he", "error").toString(),
                        sharedPreference.getString(name + "_words_en", "error").toString()
                    )
                }
            })
        }
        importListBtn.setOnClickListener{
            readListsFromFile()
        }

        //new words
        noSaved.isVisible = arrayly.isEmpty()
        newBtn.setOnClickListener {
            setNameDialog()
        }
    }
    private fun readListsFromFile() {
//        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
//            type = "application/octet-stream"
//        }
//        startActivityForResult(intent, READ_REQUEST_CODE)
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
//            try {
//                val uri = data?.data
//                wordsName = File(uri?.path!!).name.replace(".data", "")
//                val inputStream = contentResolver.openInputStream(uri)
//                val objectInputStream = ObjectInputStream(inputStream!!)
//                val heFromFle = objectInputStream.readObject().toString()
//                val enFromFle = objectInputStream.readObject().toString()
//                objectInputStream.close()
//
//                words(
//                    true,
//                    heFromFle,
//                    enFromFle
//                )
//            }catch (e: Exception){
//                Toast.makeText(this, getString(R.string.open_lists_err), Toast.LENGTH_LONG).show()
//            }
//        }
//    }


    private var permissionGranted = false
    private fun checkPerm() {
        if (
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 123)
            return
        }
        permissionGranted = true
        writeListsToFile()
    }
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            123 -> {
                permissionGranted = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                if (permissionGranted) {
                    writeListsToFile()
                }
            }
        }
    }
    private fun writeListsToFile() {
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            try {
                val dir = File(Environment.getExternalStorageDirectory(), "AppChi")
                dir.mkdir()
                val file = File(
                    dir,
                    "$wordsNow.data"
                )
                val fileOutputStream = FileOutputStream(file)
                val outputStream = ObjectOutputStream(fileOutputStream)
                outputStream.writeObject(hebrewWords)
                outputStream.writeObject(englishWords)
                outputStream.close()

                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "application/octet-stream"
                shareIntent.putExtra(
                    Intent.EXTRA_STREAM,
                    FileProvider.getUriForFile(
                        this,
                        "com.nosh.appchi.fileprovider",
                        file
                    )
                )
                startActivity(Intent.createChooser(shareIntent, "Share File"))
            } catch (e: Exception) {
                Log.e("e:" ,e.toString())}
        }
    }


    private fun words(readyWords:Boolean, he:String, en:String){
        englishWords.clear()
        hebrewWords.clear()
        var count = 0
        wordsNow = if (readyWords){wordsName}else{wordsNameNotSved}
        setContentView(R.layout.p_a_langs_words)
        fun saveWords(){
            val sharedPreference = getSharedPreferences("PREFERENCE_NAME", MODE_PRIVATE)
            val editor = sharedPreference.edit()
            editor.putString(wordsNow + "_words_he", hebrewWords.toString())
            editor.putString(wordsNow + "_words_en", englishWords.toString())
            editor.apply()
        }
        fun startQuiz(words:String) {
            learn()
            wordsName = words
            continToTf = cTrueOrFalse.isChecked
            continToQuiz = cOptions.isChecked
            continToTyping = cType.isChecked
            continToHear = cHear.isChecked
            totalQuestions = allCbs.count { it.isChecked } * hebrewWords.size
            if (cLearn.isChecked){learn()}
            else if (cTrueOrFalse.isChecked) { trueOrFalse() }
            else if (cOptions.isChecked) { quiz() }
            else if (cType.isChecked) { typing() }
            else if (cHear.isChecked) { hear() }
        }
        fun compareWords() :Boolean{
            val sharedPreference = getSharedPreferences("PREFERENCE_NAME", MODE_PRIVATE)
            val newEn = englishWords.toString()
            val newHe = hebrewWords.toString()
            val oldEn = sharedPreference.getString(wordsName + "_words_en", "error").toString()
            val oldHe =  sharedPreference.getString(wordsName + "_words_he", "error").toString()
            return newEn == oldEn && newHe == oldHe
        }
        fun saveChagesOrDeleteDialog(remove:String ,save: Boolean) {
            val sharedPreference = getSharedPreferences("PREFERENCE_NAME", MODE_PRIVATE)
            val editor = sharedPreference.edit()
            val builder = AlertDialog.Builder(this)
            val message:String = if (save){
                getString(R.string.words_list_save_changes)
            }else{
                getString(R.string.del_words_question)}
            builder.setMessage(message)
            builder.setPositiveButton(R.string.yes) { _, _ ->
                if (save) {
                    saveWords()
                    startQuiz(wordsName)
                }
                else{
                    editor.remove(remove + "_words_en")
                    editor.remove(remove + "_words_he")
                    editor.apply()
                    main()
                }
            }
            builder.setNegativeButton(R.string.no) { _, _ ->
                if (save) {
                    startQuiz(wordsName)
                }
            }
            val dialog = builder.create()
            dialog.show()
        }
        val allWords: LinearLayout = findViewById(R.id.allWords)
        fun addWords(textHe: String, textEn: String) {
            val editTextContainer = LinearLayout(this)
            editTextContainer.orientation = LinearLayout.HORIZONTAL
            editTextContainer.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            val leftEditText = EditText(this)
            leftEditText.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
            )
            leftEditText.id = count * 2
            leftEditText.setText(textHe)

            val delBtn = ImageButton(this)
            delBtn.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_baseline_remove_circle_24,
                    theme
                )
            )
            delBtn.imageTintList = ContextCompat.getColorStateList(this, R.color.main_color)
            delBtn.setBackgroundColor(Color.TRANSPARENT)
            delBtn.setOnClickListener {
                allWords.removeView(editTextContainer)
                count--
            }

            val rightEditText = EditText(this)
            rightEditText.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
            )
            rightEditText.id = count * 2 + 1
            rightEditText.setText(textEn)

            editTextContainer.addView(leftEditText)
            editTextContainer.addView(delBtn)
            editTextContainer.addView(rightEditText)
            allWords.addView(editTextContainer)

            count++
        }
        fun checkWords(): Boolean{
            allWords.allViews.forEach { view ->
                if (view is LinearLayout) {
                    view.allViews.forEach { childView ->
                        if (childView is EditText) {

                            val input = childView.text.toString()

                            if (input.isEmpty()) {
                                childView.error = getString(R.string.field_empty)
                                englishWords.clear()
                                hebrewWords.clear()
                                return false
                            }

                            if (childView.id % 2 == 0) {
                                if (!hebrewWords.contains(input)) hebrewWords.add(input)
                            } else {
                                if (!englishWords.contains(input)) englishWords.add(input)
                            }
                        }
                    }
                }
            }
            return if (count > 4){
                true
            } else{
                Toast.makeText(this@Lang, R.string.more7, Toast.LENGTH_SHORT).show()
                false
            }
        }

        val copyBtn: Button = findViewById(R.id.copy_btn)
        val addEditTextsBtn: ImageButton = findViewById(R.id.addEditTextsBtn)
        val wordsNameT: TextView = findViewById(R.id.words_name)
        val delBtn: Button = findViewById(R.id.delete_list)
        val startBtn = findViewById<Button>(R.id.add_button)
        cOptions = findViewById(R.id.c_options)
        cTrueOrFalse = findViewById(R.id.c_tf)
        cType = findViewById(R.id.c_type)
        cHear = findViewById(R.id.cHear)
        cHear = findViewById(R.id.cHear)
        cLearn = findViewById(R.id.c_learn)
        allCbs = listOf(cOptions, cHear, cType, cTrueOrFalse)
        //set title
        wordsNameT.text = wordsNow
        //add word button
        addEditTextsBtn.setOnClickListener { addWords("", "") }
        //rstore list if readyWords
        if (readyWords){
            val listHE = he.split()
            val listEn = en.split()
            for (i in listHE.indices){
                addWords(listHE[i], listEn[i])
            }
        }
        else{
            var i = 0
            while (i < 5) {
                addWords("", "")
                i++
            }
        }
        //copy button
        copyBtn.setOnClickListener {
            if (checkWords()){
                checkPerm()
            }
        }
        //delete list
        delBtn.isVisible = readyWords
        delBtn.setOnClickListener { saveChagesOrDeleteDialog(wordsName, false) }
        //start quiz
        startBtn.setOnClickListener{
            if (checkWords()) {
                if (allCbs.all { !it.isChecked }){Toast.makeText(this, R.string.no_quiz_types_error, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener}
                if (!readyWords){
                    saveWords()
                    startQuiz(wordsNameNotSved)
                }
                else{
                    if (compareWords()) {
                        saveWords()
                        startQuiz(wordsName)
                    }
                    else{
                        saveChagesOrDeleteDialog(wordsName, true) } } } }
    }
    private fun learn(){
        //prepare
        setContentView(R.layout.p_a_langs_learn)
        shuffleLists()
        preapareTTS()
        val front = findViewById<TextView>(R.id.card_front)
        val back = findViewById<TextView>(R.id.card_back)
        val nextBtn = findViewById<Button>(R.id.next_word)
        val prevBtn = findViewById<Button>(R.id.prevBtn)
        val backCard = findViewById<CardView>(R.id.backCard)
        val frontCard = findViewById<CardView>(R.id.frontCard)
        val startafterlearn = findViewById<Button>(R.id.startafterlearn)
        val tryAgainBtn = findViewById<Button>(R.id.startafterlearn2)
        startafterlearn.isVisible = false
        tryAgainBtn.isVisible = false
        frontCard.elevation = 12f

        //sets the words in the card
        fun setWords(){
            front.text = en[currentQuestion]
            back.text = he[currentQuestion]
            val progressTextView = findViewById<TextView>(R.id.progress)
            progressTextView.text = getString(R.string.question) + ": ${currentQuestion + 1}/${englishWords.size}"
        }
        setWords()

        //prepare the animation
        val scale = applicationContext.resources.displayMetrics.density
        frontCard.cameraDistance = 8000 * scale
        backCard.cameraDistance = 8000 * scale

        // setting the animations and enables shadow when there finished
        val frontAnimation = AnimatorInflater.loadAnimator(applicationContext, R.animator.card) as AnimatorSet
        val backAnimation = AnimatorInflater.loadAnimator(applicationContext, R.animator.card_back) as AnimatorSet
        frontAnimation.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator, isReverse: Boolean) {}
            override fun onAnimationStart(p0: Animator) {}
            override fun onAnimationEnd(animation: Animator, isReverse: Boolean) { frontCard.elevation = 12f }
            override fun onAnimationEnd(p0: Animator) {}
            override fun onAnimationCancel(p0: Animator) {}
            override fun onAnimationRepeat(p0: Animator) {}
        })
        backAnimation.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator, isReverse: Boolean) {}
            override fun onAnimationStart(p0: Animator) {}
            override fun onAnimationEnd(animation: Animator, isReverse: Boolean) { backCard.elevation = 12f }
            override fun onAnimationEnd(p0: Animator) {}
            override fun onAnimationCancel(p0: Animator) {}
            override fun onAnimationRepeat(p0: Animator) {}
        })

        //calls the animation
        fun filpCrads(){
            backCard.elevation = 0f
            frontCard.elevation = 0f
            isFront = if(isFront) {
                frontAnimation.setTarget(frontCard)
                backAnimation.setTarget(backCard)
                frontAnimation.start()
                backAnimation.start()
                false
            } else {
                frontAnimation.setTarget(backCard)
                backAnimation.setTarget(frontCard)
                backAnimation.start()
                frontAnimation.start()
                tts.speak(en[currentQuestion], TextToSpeech.QUEUE_ADD, null, null)
                true
            }
        }
        backCard.setOnClickListener{filpCrads()}
        frontCard.setOnClickListener{filpCrads()}

        //previous word btn
        prevBtn.setOnClickListener{
            if (currentQuestion > 0){
                currentQuestion--
                if (!isFront) {
                    isFront = false
                    filpCrads()
                }
                else{tts.speak(en[currentQuestion], TextToSpeech.QUEUE_ADD, null, null) }
                setWords()
            }
            startafterlearn.isVisible = false
            tryAgainBtn.isVisible = false
        }

        //next word btn
        nextBtn.setOnClickListener{
            //if words arn't finshed
            if (currentQuestion < englishWords.size-1) {
                //next word
                currentQuestion++
                if (!isFront) {
                    isFront = false
                    filpCrads()
                }
                else{tts.speak(en[currentQuestion], TextToSpeech.QUEUE_ADD, null, null) }
                setWords()
            }
            //when words finished
            if (currentQuestion >= englishWords.size - 1){
                //show the try agian btn and set it to try again
                tryAgainBtn.isVisible = true
                tryAgainBtn.setOnClickListener {
                    currentQuestion = 0
                    learn()
                }
                //show the start quiz btn and set it to start quiz
                startafterlearn.isVisible = true
                startafterlearn.setOnClickListener{
                    whenPartDone(0)
                }
            }
        }
    }
    private fun trueOrFalse(){
        setContentView(R.layout.p_a_langs_tf)
        val trueRadio = findViewById<RadioButton>(R.id.rtrue)
        val falseRadio = findViewById<RadioButton>(R.id.rfalse)
        val next: Button = findViewById(R.id.submit_button4)
        val submitButton = findViewById<Button>(R.id.submit_button)
        shuffleLists()
        setTextsHeader(score, questionsDone)
        setWord(next, submitButton)
        // Set an onClickListener for the submit button
        submitButton.setOnClickListener {
            submitButton.isEnabled = false
            next.isVisible = true
            stopStopwatch()
            setTextsHeader(score, questionsDone)
            checkCorrectAnswer(1, trueRadio, falseRadio, null, null)
        }
        next.setOnClickListener {
            trueRadio.setBackgroundColor(Color.TRANSPARENT)
            falseRadio.setBackgroundColor(Color.TRANSPARENT)
            currentQuestion++
            questionsDone++
            if (currentQuestion < englishWords.size) {
                setWord(next, submitButton)
            } else {
                whenPartDone(1)
            }
        }
    }
    private fun quiz() {
        setContentView(R.layout.p_a_langs_quiz)
        val next: Button = findViewById(R.id.submit_button3)
        val englishWordTextView = findViewById<TextView>(R.id.english_word)
        val radioGroup = findViewById<RadioGroup>(R.id.radio_group)
        val submitButton = findViewById<Button>(R.id.submit_button)
        shuffleLists()
        setTextsHeader(score, questionsDone)
        englishWordTextView.text = en[currentQuestion]
        //function to set the answers options
        //calling setup of options
        setRadio(radioGroup, next, submitButton)
        // Set an onClickListener for the submit button
        submitButton.setOnClickListener {
            submitButton.isEnabled = false
            next.isVisible = true
            stopStopwatch()
            setTextsHeader(score, questionsDone)
            val selectedId = radioGroup.checkedRadioButtonId
            val selectedRadioButton = findViewById<RadioButton>(selectedId)
            val selectedWord = selectedRadioButton.text.toString()
            // Check if the selected word is the correct translation
            checkCorrectAnswer(2, rightRadioButton, selectedRadioButton, selectedWord, null)
        }
        next.setOnClickListener {
            questionsDone++
            currentQuestion++
            // If there are more questions, update the English word and clear the radio group selection
            if (currentQuestion < englishWords.size) {
                englishWordTextView.text = en[currentQuestion]
                setRadio(radioGroup, next, submitButton)
            } else {
                whenPartDone(2)
            }
        }
    }
    private fun typing(){
        setContentView(R.layout.p_a_langs_typing)
        val englishWordTextView = findViewById<TextView>(R.id.english_word)
        val inputEdit: EditText = findViewById(R.id.editTextTextPersonName3)
        val next: Button = findViewById(R.id.submit_button5)
        val submitButton = findViewById<Button>(R.id.submit_button)
        shuffleLists()
        submitButton.isEnabled = false
        setTextsHeader(score, questionsDone)
        englishWordTextView.text = he[currentQuestion]
        next.isVisible = false
        startStopwatch()
        submitButton.setOnClickListener {
            submitButton.isEnabled = false
            next.isVisible = true
            stopStopwatch()
            checkCorrectAnswer(3, null, null, null, inputEdit)
            inputEdit.setText(en[currentQuestion])
            inputEdit.isEnabled = false
            setTextsHeader(score, questionsDone)
        }
        next.setOnClickListener {
            next.isVisible = false
            inputEdit.isEnabled = true
            inputEdit.text.clear()
            questionsDone++
            currentQuestion++
            startStopwatch()
            // If there are more questions, update the English word and clear the radio group selection
            if (currentQuestion < englishWords.size) {
                englishWordTextView.text = he[currentQuestion]
            } else {
                whenPartDone(3)
            }
        }
        inputEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                submitButton.isEnabled = inputEdit.text.isNotEmpty()
            }
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int,
            ) {
            }
            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int,
            ) {
            } })
    }
    private fun hear(){
        setContentView(R.layout.p_a_langs_hear)
        val next: Button = findViewById(R.id.next_word)
        val englishWordTextView = findViewById<ImageButton>(R.id.imageButton)
        val radioGroup = findViewById<RadioGroup>(R.id.radio_group)
        val submitButton = findViewById<Button>(R.id.submit_button)
        preapareTTS()
        submitButton.isEnabled = false
        setTextsHeader(score, questionsDone)
        shuffleLists()
        tts.speak(en[currentQuestion], TextToSpeech.QUEUE_ADD, null, null)
        englishWordTextView.setOnClickListener{tts.speak(en[currentQuestion], TextToSpeech.QUEUE_ADD, null, null)
        }
        //function to set the answers options
        //calling setup of options
        setRadio(radioGroup, next, submitButton)
        // Set an onClickListener for the submit button
        submitButton.setOnClickListener {
            submitButton.isEnabled = false
            next.isVisible = true
            stopStopwatch()
            setTextsHeader(score, questionsDone)
            val selectedId = radioGroup.checkedRadioButtonId
            val selectedRadioButton = findViewById<RadioButton>(selectedId)
            val selectedWord = selectedRadioButton.text.toString()
            // Check if the selected word is the correct translation
            checkCorrectAnswer(2, rightRadioButton, selectedRadioButton, selectedWord, null)
        }
        next.setOnClickListener {
            submitButton.isEnabled = true
            questionsDone++
            currentQuestion++
            englishWordTextView.setOnClickListener{tts.speak(en[currentQuestion], TextToSpeech.QUEUE_ADD, null, null) }
            // If there are more questions, update the English word and clear the radio group selection
            if (currentQuestion < englishWords.size) {
                tts.speak(en[currentQuestion], TextToSpeech.QUEUE_ADD, null, null)
                // Select four random Hebrew words, including the correct translation
                setRadio(radioGroup, next, submitButton)
            } else {
                questionAreDone()
            }
        }
    }
    private fun questionAreDone() {
        @Suppress("DEPRECATION")
        fun takeScreenshot(): Uri {
            val view = window.decorView
            view.isDrawingCacheEnabled = true
            val bitmap = view.drawingCache
            val bytes = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "Title", null)
            return Uri.parse(path)
        }
        fun convertToTimeAvg(time: Long, avg:Int): String {
            val timeInSecs = (TimeUnit.MILLISECONDS.convert(time, TimeUnit.NANOSECONDS)/avg).toDouble() / 1000
            return timeInSecs.toString() + getString(R.string.secs)
        }
        fun findPerfect(allWords: List<String>, failed: List<String>): List<String> {
            return allWords.filter { !failed.contains(it) }
        }
        fun startScoresAnim(scorePercent: Int, totalpoinstv: TextView, precentPb: ProgressBar){
            val valueAnimator = ValueAnimator.ofInt(0, scorePercent)
            valueAnimator.duration = 1500
            valueAnimator.addUpdateListener {totalpoinstv.text = valueAnimator.animatedValue.toString() + "%"}
            valueAnimator.start()
            val animator = ValueAnimator.ofInt(0, scorePercent)
            animator.duration = 1000 // set duration to 1 second
            animator.addUpdateListener {
                val currentValue = it.animatedValue as Int
                precentPb.progress = currentValue
            }
            animator.start()
        }

        setContentView(R.layout.p_a_langs_done)
        val totalpoinstv: TextView = findViewById(R.id.totalpoinstv)
        val precentPb: ProgressBar = findViewById(R.id.precentPb)
        val answerdtotaltext: Button = findViewById(R.id.answerdtotaltext)
        val perfect: Button = findViewById(R.id.prfectly_knew)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val recyclerViewR: RecyclerView = findViewById(R.id.perfect_wordsR)
        val wrongWords: LinearLayout = findViewById(R.id.wrong_words)
        val perfectWords: LinearLayout = findViewById(R.id.perfect_words)
        val failedWords: Button = findViewById(R.id.failed_words)
        val totaltime: Button = findViewById(R.id.totalTime)
        val shareBtn: Button = findViewById(R.id.shareBtn)
        val retryBtn: Button = findViewById(R.id.retry)
        //animation
        precentPb.isIndeterminate = false
        val scorePercent = ((score.toDouble() / totalQuestions) * 100).toInt()
        startScoresAnim(scorePercent, totalpoinstv, precentPb)
        //answered correctly
        answerdtotaltext.text = getString(R.string.you_answered) +" "+ score+"/"+totalQuestions +" "+ getString(R.string.questions)
        //time
        val avgTime = convertToTimeAvg(totalTime, totalQuestions)
        totaltime.text = getString(R.string.avg_ans) + " $avgTime"
        //perfectly
        perfect.text = getString(R.string.perfectly_knew) +" ${englishWords.size - failedEn.distinct().size}"
        if (findPerfect(englishWords, failedEn).isNotEmpty()){
            perfect.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_expand_more_24, 0, 0, 0)
            perfect.setOnClickListener {
                perfectWords.isVisible = !perfectWords.isVisible
            }
            val adapter = PerfectAdapter(findPerfect(englishWords, failedEn), findPerfect(hebrewWords, failedHe), this)
            recyclerViewR.adapter = adapter
            recyclerViewR.layoutManager = LinearLayoutManager(this)
        }
        //failed
        failedWords.text = getString(R.string.failed_words) + " ${failedEn.distinct().size}"
        wrongWords.isVisible = false
        if (scorePercent != 100) {
            failedWords.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_expand_more_24, 0, 0, 0)
            failedWords.setOnClickListener {
                wrongWords.isVisible = !wrongWords.isVisible
            }
            val adapter = FailedAdapter(failedEn, failedHe, this)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(this)
        }
        //share
        shareBtn.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT,
                            getString(R.string.share_lang_score1)
                            + " $scorePercent% "
                            + getString(R.string.share_lang_score2)
                            + "\n"
                            + "\n"
                            + getString(R.string.app_link)
                )
                putExtra(Intent.EXTRA_STREAM, takeScreenshot())
                type = "image/*"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }
        //retry
        retryBtn.setOnClickListener {
            failedEn.clear()
            failedHe.clear()
            score = 0
            currentQuestion = 0
            totalQuestions = 0
            questionsDone = 0
            val sharedPreference = getSharedPreferences("PREFERENCE_NAME", MODE_PRIVATE)
            val words = wordsName
            val heWords = sharedPreference.getString(words + "_words_he", "error").toString()
            val enWords = sharedPreference.getString(words + "_words_en", "error").toString()
            words(
                true,
                heWords,
                 enWords
            )
        }
    }
    private fun setRadio(radioGroup: RadioGroup, next: Button, submitButton: Button) {
        //taking 4 random words from hebrewWords, include the correct answer
        val options = mutableListOf(he[currentQuestion])
        val takeFrom = mutableListOf<String>()
        takeFrom.addAll(he)
        takeFrom.removeAt(currentQuestion)
        options.addAll(takeFrom.shuffled().take(3))
        // Update the radio buttons with the new options
        radioGroup.removeAllViews()
        for (hebrewWord in options.shuffled()) {
            val radioButton = RadioButton(this)
            radioButton.text = hebrewWord
            if (radioButton.text == he[currentQuestion]){
                rightRadioButton = radioButton
            }
            radioGroup.addView(radioButton)
            radioButton.layoutParams = RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT)
        }
        radioGroup.check(radioGroup.getChildAt(0).id)
        startStopwatch()
        next.isVisible = false
        submitButton.isEnabled = true
    }
    private fun setTextsHeader(score: Int, questionsDone: Int){
        val progressTextView = findViewById<TextView>(R.id.progress)
        val pointsTextView = findViewById<TextView>(R.id.points)
        progressTextView.text = getString(R.string.question) + ": ${questionsDone + 1}/${totalQuestions}"
        pointsTextView.text = getString(R.string.score) +": $score/${totalQuestions}"
    }
    private fun startStopwatch() { startTime = System.nanoTime() }
    private fun stopStopwatch() {
        val endTime = System.nanoTime()
        val elapsedTime = endTime - startTime
        totalTime += elapsedTime
    }
    private fun checkCorrectAnswer(type: Int, trueRadio: RadioButton?, falseRadio: RadioButton?, selectedWord: String?, inputEdit: EditText?){
        fun correct(){
            score++
            Toast.makeText(this, R.string.correct_answer, Toast.LENGTH_SHORT).show()
        }
        fun fail(){
            Toast.makeText(this, R.string.wrong_answer, Toast.LENGTH_SHORT).show()
            failedEn.add(en[currentQuestion])
            failedHe.add(he[currentQuestion])
        }
        when (type){
            1 -> {
                if (trueRadio!!.isChecked && random == 0) {
                    trueRadio.setBackgroundResource(R.color.correct)
                    correct() }
                else if (falseRadio!!.isChecked && random == 1){
                    falseRadio.setBackgroundResource(R.color.correct)
                    correct() }
                else if (trueRadio.isChecked && random == 1) {
                    trueRadio.setBackgroundResource(R.color.wrong)
                    falseRadio.setBackgroundResource(R.color.correct)
                    fail() }
                else if (falseRadio.isChecked && random == 0) {
                    falseRadio.setBackgroundResource(R.color.wrong)
                    trueRadio.setBackgroundResource(R.color.correct)
                    fail() }
            }
            2 -> {
                if (selectedWord == he[currentQuestion]) { correct() }
                else{
                    falseRadio!!.setBackgroundResource(R.color.wrong)
                    fail()
                }
                trueRadio!!.setBackgroundResource(R.color.correct)
            }
            3 -> {
                val re = Regex("[ ,\"`~:{}.]")
                // Check if the selected word is the correct translation
                if (re.replace(inputEdit!!.text.toString(), "").lowercase(Locale.getDefault()) == re.replace(en[currentQuestion], "").lowercase(Locale.getDefault())) { correct() }
                else{ fail() }
            }
        }
    }
    private fun setWord(next: Button, submitButton: Button) {
        val englishWordTextView = findViewById<TextView>(R.id.english_word)
        random = (0..1).random()
        word = if (random == 0){
            en[currentQuestion] + " = " + he[currentQuestion]
        } else{
            val allWords = he.toMutableList()
            allWords.removeAt(currentQuestion)
            val bad = allWords.random()
            en[currentQuestion] + " = " + bad
        }
        englishWordTextView.text = word
        startStopwatch()
        next.isVisible = false
        submitButton.isEnabled = true
    }
    private fun whenPartDone(type: Int){
        when (type){
            0 -> {
                    currentQuestion = 0
                    if (continToTf){ trueOrFalse() }
                    else if (continToQuiz){ quiz() }
                    else if (continToTyping){ typing() }
                    else { hear() }
            }
            1 -> {
                if (continToQuiz || continToTyping || continToHear){
                    currentQuestion = 0
                    if (continToQuiz){ quiz() }
                    else if (continToTyping){ typing() }
                    else { hear() }
                }
                else{ questionAreDone() }
            }
            2 -> {
                if (continToTyping || continToHear){
                    currentQuestion = 0
                    if (continToTyping){ typing() }
                    else{ hear() }
                }
                else{ questionAreDone() }
            }
            3 -> {
                currentQuestion = 0
                if (continToHear){ hear() }
                else{ questionAreDone() }
            }
        }
    }
    private fun preapareTTS(): Boolean {
        var returni = false
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts.setLanguage(Locale.US)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "not supported", Toast.LENGTH_LONG).show()
                } else {
                    // The TTS engine has been successfully initialized.
                    // You can now call speak to synthesize speech.
                    returni = true
                }
            } else {
                Toast.makeText(this, "not supported", Toast.LENGTH_LONG).show()
            }
        }
        return returni
    }
    private fun shuffleLists(){
        val indices = (0 until englishWords.size).toMutableList()
        indices.shuffle()
        val shuffledListA = mutableListOf<String>()
        val shuffledListB = mutableListOf<String>()
        for (index in indices) {
            shuffledListA.add(englishWords[index])
            shuffledListB.add(hebrewWords[index])
        }
        en.clear()
        en.addAll(shuffledListA)
        he.clear()
        he.addAll(shuffledListB)
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.explain, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.explain -> {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(R.string.lang_explain)
                builder.setPositiveButton("OK") { _, _ ->
                }
                val dialog = builder.create()
                dialog.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun String.split(): List<String>{
        return this.replace("[","").replace("]","").split(", ")
    }
}
