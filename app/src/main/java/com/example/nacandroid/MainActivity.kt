package com.example.nacandroid

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.requisicoes.cursoandroid.requisicoeshttp.EditorCep
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class MainActivity : AppCompatActivity() {

    private var cepPesquisa: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var inputManager: InputMethodManager = getSystemService (Context.INPUT_METHOD_SERVICE) as InputMethodManager
        setContentView(R.layout.activity_main)
        cep.addTextChangedListener( EditorCep("#####-###"))

        buttonRecuperar.setOnClickListener(View.OnClickListener {
            val task = MyTask()
            inputManager.hideSoftInputFromWindow(currentFocus!!.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS)
            cepPesquisa = cep.text.toString().replace("-", "")
            val urlCep = "https://viacep.com.br/ws/$cepPesquisa/json/"
            task.execute(urlCep)
        })

    }

    internal inner class MyTask : AsyncTask<String, Void, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg strings: String): String? {

            val stringUrl = strings[0]
            var inputStream: InputStream? = null
            var inputStreamReader: InputStreamReader? = null
            var buffer: StringBuffer? = null

            try {

                val url = URL(stringUrl)
                val conexao = url.openConnection() as HttpURLConnection
                if (conexao.responseCode == 200) {
                    // Recupera os dados em Bytes
                    inputStream = conexao.inputStream

                    //inputStreamReader lê os dados em Bytes e decodifica para caracteres
                    inputStreamReader = InputStreamReader(inputStream!!)

                    //Objeto utilizado para leitura dos caracteres do InpuStreamReader
                    val reader = BufferedReader(inputStreamReader)
                    buffer = StringBuffer()
                    var linha: String? = ""

                    while ( linha != null) {
                        linha = reader.readLine()
                        buffer.append(linha)
                    }
                }
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return buffer?.toString()
        }

        override fun onPostExecute(resultado: String?) {
            super.onPostExecute(resultado)
            cep.setText("")
            var logradouro: String? = null
            var cep: String? = null
            var complemento: String? = null
            var bairro: String? = null
            var localidade: String? = null
            var uf: String? = null
            if (resultado != null) {

                try {
                    val jsonObject = JSONObject(resultado)
                    logradouro = jsonObject.getString("logradouro")
                    cep = jsonObject.getString("cep")
                    complemento = jsonObject.getString("complemento")
                    bairro = jsonObject.getString("bairro")
                    localidade = jsonObject.getString("localidade")
                    uf = jsonObject.getString("uf")
                    resultadoGeral.text = "CEP pesquisado com sucesso"
                    resultadoGeral.visibility = View.VISIBLE
                    resultadoCep.text = "CEP: " + cep!!
                    resultadoCep.visibility = View.VISIBLE
                    resultadoLogradouro.text = "Logradouro: " + logradouro!!
                    resultadoLogradouro.visibility = View.VISIBLE
                    resultadoComplemento.text = "Complemento: " + complemento!!
                    resultadoComplemento.visibility = View.VISIBLE
                    resultadoBairro.text = "Bairro: " + bairro!!
                    resultadoBairro.visibility = View.VISIBLE
                    resultadoLocalidade.text = "Localidade: " + localidade!!
                    resultadoLocalidade.visibility = View.VISIBLE
                    resultadoUf.text = "UF: " + uf!!
                    resultadoUf.visibility = View.VISIBLE
                } catch (e: JSONException) {
                    resultadoGeral.text = "CEP não existe"
                    resultadoGeral.visibility = View.VISIBLE
                    resultadoCep.visibility = View.INVISIBLE
                    resultadoLogradouro.visibility = View.INVISIBLE
                    resultadoComplemento.visibility = View.INVISIBLE
                    resultadoBairro.visibility = View.INVISIBLE
                    resultadoLocalidade.visibility = View.INVISIBLE
                    resultadoUf.visibility = View.INVISIBLE
                }

            } else {
                resultadoGeral.text = "CEP mal formado"
                resultadoGeral.visibility = View.VISIBLE
                resultadoCep.visibility = View.INVISIBLE
                resultadoLogradouro.visibility = View.INVISIBLE
                resultadoComplemento.visibility = View.INVISIBLE
                resultadoBairro.visibility = View.INVISIBLE
                resultadoLocalidade.visibility = View.INVISIBLE
                resultadoUf.visibility = View.INVISIBLE
            }
        }
    }
}
