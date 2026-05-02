package com.hastagaming.lapkey

import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.view.View
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.ImageButton
import android.widget.TextView
import android.content.Intent
import android.view.LayoutInflater

class LapKeyService : InputMethodService(), KeyboardView.OnKeyboardActionListener {

    private lateinit var keyboardView: KeyboardView
    private lateinit var candidateContainer: LinearLayout    

    override fun onCreateInputView(): View {
        // 1. Inflate layout utama (keyboard_view.xml)
        val root = layoutInflater.inflate(R.layout.keyboard_view, null)
        
        // 2. Inisialisasi komponen UI
        keyboardView = root.findViewById(R.id.keyboard_view)
        candidateContainer = root.findViewById(R.id.candidate_container)

        // 3. Setup Tombol Menu AI (Yang Dimerah Itu)
        root.findViewById<ImageButton>(R.id.btn_ai_menu).setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        // 4. Setup Keyboard Engine
        val qwertyKeyboard = Keyboard(this, R.xml.qwerty)
        keyboardView.keyboard = qwertyKeyboard
        keyboardView.setOnKeyboardActionListener(this)
        
        // Munculkan saran awal sebagai testing
        setSuggestions(listOf("s", "sudah", "sekarang"))

        return root
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        // Update status shift atau tipe input di sini jika perlu
    }

    // Fungsi untuk mengisi Baris Saran (Candidate View)
    fun setSuggestions(suggestions: List<String>) {
        candidateContainer.removeAllViews()

        for (word in suggestions) {
            val itemView = layoutInflater.inflate(R.layout.candidate_item, null, false)
            val textView = itemView.findViewById<TextView>(R.id.candidate_word)

            textView.text = word
            textView.setOnClickListener {
                // Masukkan kata ke kolom input
                currentInputConnection.commitText(word + " ", 1)
                // Bersihkan baris saran setelah dipilih
                candidateContainer.removeAllViews()
            }

            candidateContainer.addView(itemView)
        }
    }

    // --- Implementasi Keyboard Action ---
    override fun onKey(primaryCode: Int, keyCodes: IntArray?) {
        val ic = currentInputConnection ?: return
        
        when (primaryCode) {
            -5 -> ic.deleteSurroundingText(1, 0) // Backspace
            -4 -> { // Enter (Sesuai kode di qwerty.xml)
                ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
                ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER))
            }
            -1 -> { // Shift
                keyboardView.isShifted = !keyboardView.isShifted
                keyboardView.invalidateAllKeys()
            }
            else -> {
                val char = primaryCode.toChar()
                ic.commitText(char.toString(), 1)
                
                // Logika saran sederhana: Munculkan jika ngetik 's'
                if (char == 's' || char == 'S') {
                    setSuggestions(listOf("s", "sudah", "sekarang"))
                } else {
                    candidateContainer.removeAllViews()
                }
            }
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
