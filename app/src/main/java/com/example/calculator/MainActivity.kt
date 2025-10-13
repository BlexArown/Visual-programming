package com.example.calculator

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView

class CalcButton(
    private val button: Button,
    private val mainText: TextView,
) {
    init {
        if (button.text == "AC"){
            button.setOnClickListener {
                mainText.text = ""
            }
        }
        else if (button.text == "⌫"){
            button.setOnClickListener {
                val str = mainText.text.toString()
                if(str.isNotEmpty()){
                    mainText.text = str.substring(0, str.length - 1)
                }
            }
        }
        else if (button.text == "="){
            button.setOnClickListener {
                mainText.text = calculate(mainText.text.toString())
            }
        }
        else{button.setOnClickListener {
            mainText.append(button.text)
        }}

            }
        }


private fun calculate(expression: String): String {
    try {
        if (expression.isEmpty()) return ""

        var exp = expression.replace('x', '*').replace('÷', '/')
        if (exp[0] == '+' || exp[0] == '-') exp = "0$exp"

        var currentNumber = ""
        var lastOperator = '+'
        var currentResult = 0.0
        var lastTerm = 0.0

        fun applyOperator(op: Char, num: Double) {
            when (op) {
                '+' -> {
                    currentResult += lastTerm
                    lastTerm = num
                }
                '-' -> {
                    currentResult += lastTerm
                    lastTerm = -num
                }
                '*' -> lastTerm *= num
                '/' -> {
                    if (num == 0.0) throw ArithmeticException("Деление на 0")
                    lastTerm /= num
                }
            }
        }

        for ((index, char) in (exp + "+").withIndex()) {
            if (char.isDigit() || char == '.') {
                currentNumber += char
            } else if (char == '+' || char == '-' || char == '*' || char == '/' || index == exp.length) {
                if (currentNumber.isEmpty()) continue
                val num = currentNumber.toDouble()
                applyOperator(lastOperator, num)
                lastOperator = char
                currentNumber = ""
            }
        }

        val result = currentResult + lastTerm

        return if (result % 1 == 0.0) {
            result.toInt().toString()
        } else {
            result.toString()
        }
    } catch (e: ArithmeticException) {
        return e.message ?: "Ошибка"
    } catch (e: Exception) {
        return "Ошибка"
    }
}



class MainActivity : AppCompatActivity() {
    private lateinit var mainText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        mainText = findViewById(R.id.main_text)

        listOf(
            R.id.btn_0, R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4,
            R.id.btn_5, R.id.btn_6, R.id.btn_7, R.id.btn_8, R.id.btn_9,
            R.id.btn_plus, R.id.btn_minus, R.id.btn_umnozenie,
            R.id.delenie, R.id.btn_zapyataya,
            R.id.AC, R.id.btn_delete,R.id.btn_ravno
        ).forEach { id ->
            CalcButton(findViewById(id), mainText)
        }
    }
}