package com.hastagaming.lapkey

import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.view.View
import android.view.inputmethod.EditorInfo

class LapKeyService : InputMethodService(), KeyboardView.OnKeyboardActionListener {

    private lateinit var keyboardView: KeyboardView
    private lateinit var qwertyKeyboard: Keyboard
    private lateinit var candidateContainer: LinearLayout    

    override fun onCreateInputView(): View {
        keyboardView = layoutInflater.inflate(R.layout.keyboard_view, null) as KeyboardView
        qwertyKeyboard = Keyboard(this, R.xml.qwerty)
        candidateContainer = root.findViewById(R.id.candidate_container)
        keyboardView.keyboard = qwertyKeyboard
        keyboardView.setOnKeyboardActionListener(this)
        return keyboardView
    }

    override fun onCreateInputView(): View {
    val root = layoutInflater.inflate(R.layout.keyboard_view, null)
    keyboardView = root.findViewById(R.id.keyboard_view)
     root.findViewById<ImageButton>(R.id.btn_ai_menu).setOnClickListener{
           val intent = Intent(this, SettingsActivity::class.java)
           intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
           startActivity(intent)
       }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
       super.onStartInputView(info, restarting)
       // Logika untuk mendeteksi tipe input (search, send, go) bisa ditaruh di sini
   }

   fun setSuggestions(suggestions: List<String>) {
   candidateContainer.removeAllViews()
    
   for (word in suggestions) {
       val itemView = layoutInflater.inflate(R.layout.candidate_item, null)
       val textView = itemView.findViewById<TextView>(R.id.candidate_word)
        
       textView.text = word
       textView.setOnClickListener {
           // Masukkan kata yang diklik ke input field
           currentInputConnection.commitText(word + " ", 1)
           // Kosongkan saran setelah dipilih
           candidateContainer.removeAllViews()
       }
        
       candidateContainer.addView(itemView)
       }
   }

       keyboardView.keyboard = Keyboard(this, R.xml.qwerty)
       keyboardView.setOnKeyboardActionListener(this)
       return root
   }

    override fun onKey(primaryCode: Int, keyCodes: IntArray?) {
        val ic = currentInputConnection ?: return
        when (primaryCode) {
            -5 -> ic.deleteSurroundingText(1, 0) // Backspace
            10 -> ic.sendKeyEvent(android.view.KeyEvent(android.view.KeyEvent.ACTION_DOWN, android.view.KeyEvent.KEYCODE_ENTER))
            else -> ic.commitText(primaryCode.toChar().toString(), 1)
        }
    }

    override fun onPress(primaryCode: Int) {}
    override fun onRelease(primaryCode: Int) {}
    override fun onText(text: CharSequence?) {
        currentInputConnection?.commitText(text, 1)
    }
    override fun swipeLeft() {}
    override fun swipeRight() {}
    override fun swipeDown() {}
    override fun swipeUp() {}
}
