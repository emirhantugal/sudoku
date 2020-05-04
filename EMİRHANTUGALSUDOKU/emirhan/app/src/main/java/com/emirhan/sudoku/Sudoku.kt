package com.emirhan.sudoku

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley


//HOCAM BURADA ILK ONCE SINIFIMIN ICINE DEGERLERIMI ATIYORUM
class Sudoku(context: Context) {
    var solved = false
    var ready = false
    var puzzle: Array<IntArray>? = null
    var solution: Array<IntArray>? = null
    var checked = false
    private var curContext: Context = context


    //HOCAM BURASI DA BENIM TABLOMUN OLUSTUGU FONKSIYON
    private fun parseTablo(string: String) {
        var i = 0;
        this.puzzle = Array(6) { IntArray(6) }
        this.solution = Array(6) { IntArray(6) }
        while (i < string.length) {
            if (string[i] == '.') {
                this.puzzle!![i / 6][i % 6] = -1
                this.solution!![i / 6][i % 6] = -1
            } else {
                this.puzzle!![i / 6][i % 6] = Character.getNumericValue(string[i])
                this.solution!![i / 6][i % 6] = Character.getNumericValue(string[i])
            }
            i++
        }
        ready = true
    }
    fun cozum(): Boolean {
        return solved
    }
    private fun markKontrol() {
        this.checked = true
    }
    fun inDogrulama() {
        this.checked = false
    }
    private fun markCozum() {
        this.solved = true
    }
    fun isOnay(): Boolean {
        return checked
    }
    fun isValidPositionToFill (i: Int, j: Int): Boolean {
        return this.puzzle?.get(i)?.get(j) == -1
    }
    fun hazir(): Boolean {
        return ready
    }

    //BURADA BULMACAYI GETIREN FONKSIYONU YAZIYORUZ
    fun SudokunuGetir() {
        val queue = Volley.newRequestQueue(curContext)
        //NILGUN HOCAM BURADA BU SITEDEN YENI RAKAMLAR CEKIYORUM//
        val url = "https://agarithm.com/sudoku/new"
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            Response.Listener<String> { response ->
                Log.i("Bilgi", "bilgi mesajı $response")
                parseTablo(response)
            },
            Response.ErrorListener { error: VolleyError? ->
                Log.e("Hata", "Hata mesajı: $error")
            }
        )
        queue.add(stringRequest)
    }


    //BURADA ISE KULLANICIYA RESETLEMESINE IZIN VERIYORUM
    fun hepsiniResetle() {
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
    fun hepsiniKontrol() {
        this.markKontrol()
        var hash : HashMap<Int, Int> = HashMap<Int, Int>()
        for(i in 1..9)
            hash[i] = 0
        //check each row
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
        this.markCozum()
    }


    //BURASI DA NUMARA/SAYI EKLEMEMIZI SAGLAYAN FONKSIYON HOCAM
    fun sayiEkle(i: Int, j: Int, value: Int) {
        Log.i("Bilgi", "Ekleniyor $value")
        if (i != -1 && j != -1) {
            this.solution?.get(i)?.set(j, value)
        } else {
            Log.i("Numara Ekle", "Hiçbir kutu seçilmedi")
        }
    }

    //HOCAM BURADA DA NUMARA-SAYI SILMEK ICIN OLAN FONKSIYON
    fun sayiSil(i: Int, j: Int) {
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