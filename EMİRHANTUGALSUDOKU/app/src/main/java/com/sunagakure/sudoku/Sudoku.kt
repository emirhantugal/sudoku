package com.sunagakure.sudoku

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class Sudoku(context: Context) {
    var solved = false
    var ready = false
    var puzzle: Array<IntArray>? = null
    var solution: Array<IntArray>? = null
    var checked = false
    private var curContext: Context = context

    private fun parseString(string: String) {
        var i = 0;
        this.puzzle = Array(9) { IntArray(9) }
        this.solution = Array(9) { IntArray(9) }
        while (i < string.length) {
            if (string[i] == '.') {
                this.puzzle!![i / 9][i % 9] = -1
                this.solution!![i / 9][i % 9] = -1
            } else {
                this.puzzle!![i / 9][i % 9] = Character.getNumericValue(string[i])
                this.solution!![i / 9][i % 9] = Character.getNumericValue(string[i])
            }
            i++
        }
        ready = true
    }
    fun isSolved(): Boolean {
        return solved
    }
    private fun markChecked() {
        this.checked = true
    }
    fun inValidate() {
        this.checked = false
    }
    private fun markSolved() {
        this.solved = true
    }
    fun isValidated(): Boolean {
        return checked
    }
    fun isValidPositionToFill (i: Int, j: Int): Boolean {
        return this.puzzle?.get(i)?.get(j) == -1
    }
    fun isReady(): Boolean {
        return ready
    }
    fun getPuzzle() {
        val queue = Volley.newRequestQueue(curContext)
        //NILGUN HOCAM BURADA BU SITEDEN YENI RAKAMLAR CEKIYORUM//
        val url = "https://agarithm.com/sudoku/new"
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            Response.Listener<String> { response ->
                Log.i("Bilgi", "Yanıt $response")
                parseString(response)
            },
            Response.ErrorListener { error: VolleyError? ->
                Log.e("Hata", "Hatası: $error")
            }
        )
        queue.add(stringRequest)
    }

    //BURADA ISE KULLANICIYA RESETLEMESINE IZIN VERIYORUM
    fun resetSolution() {
        var counter = 0
        while (counter < 81) {
            this.puzzle?.get(counter/9)?.get(counter%9)?.let {
                this.solution?.get(counter/9)?.set(counter%9,
                    it
                )
            };
            counter++
        }
    }

    //BURADA PROGRAM SONUCUN DOGRU OLUP OLMADIGINI KONTROL EDIYOR HOCAM
    fun checkSolution() {
        this.markChecked()// değer false ile başlıyor.
        var hash : HashMap<Int, Int> = HashMap<Int, Int>()
        for(i in 1..9)
            hash[i] = 0
        //Satırları kontrol ediyoruz.
        for (i in 0..8) {
            for (j in 1..9) {
                hash[j] = 0
            }
            for(j in 0..8) {
                if (solution?.get(i)?.get(j) != -1)
                    hash[solution?.get(i)?.get(j)]?.plus(1)
            }
            for (j in 1..9) {
                if (hash[j] != 1)
                    return
            }
        }

        //HER KOLONU KONTROL EDIYORUZ HOCAM
        for (i in 0..8) {
            for (j in 1..9) {
                hash[j] = 0
            }
            for(j in 0..8) {
                if (solution?.get(j)?.get(i) != -1)
                    hash[solution?.get(j)?.get(i)]?.plus(1)
            }
            for (j in 1..9) {
                if (hash[j] != 1)
                    return
            }
        }

        //BURADA ISE KUTULARI KONTROL EDIYORUZ HOCAM
        for (i in 0..2) {
            for(j in 0..2) {
                for (k in 1..9) {
                    hash[k] = 0
                }

                for (k in 0..2) {
                    for(l in 0..2) {
                        if (solution?.get(i * 3 + k)?.get(j * 3 + l) != -1)
                            hash[solution?.get(i * 3 + k)?.get(j * 3 + l)]?.plus(1)
                    }
                }
                for (k in 1..9) {
                    if (hash[k] != 1)
                        return
                }
            }
        }
        this.markSolved() // değerimiz true oldu.
    }

    fun addNumber(i: Int, j: Int, value: Int) {
        Log.i("Bilgi", "Eklenen $value")
        if (i != -1 && j != -1) {
            this.solution?.get(i)?.set(j, value)
        } else {
            Log.i("SayıEkle", "Hiçbir kutu seçilmediğinden sayı eklenemiyor")
        }
    }
    fun removeNumber(i: Int, j: Int) {
        Log.i("Sil", "Numara kaldırılıyor")
        if (i == -1 || j == -1) {
            Log.i("Sil","Hiçbir kutu seçilmediğinden numara kaldırılamadı")
        } else if (this.solution?.get(i)?.get(j) == -1) {
            Log.i("Sil","Seçilen kutu sorundaki bir sabit olduğundan sayı kaldırılamadı")
        } else {
            this.solution?.get(i)?.set(j, -1)
            Log.i("Sil", "Kaldırılan sayılar ($i, $j)")
        }
    }
}