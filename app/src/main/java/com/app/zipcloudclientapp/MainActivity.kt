package com.app.zipcloudclientapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class MainActivity : AppCompatActivity() {
    companion object {
        /** ログ出力に使用するタグ */
        const val TAG = "MainActivity"

        /** ZipCloudAPIのURL */
        const val URL = "https://zipcloud.ibsnet.co.jp/api/search?zipcode=%s"

        /** 郵便番号にマッチする正規表現 */
        val REGEX_ZIP_CODE = Regex("\\d{7}")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        zip_code_edittext.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable?) {
                // 入力テキストを取得
                val text = s.toString()
                if (!REGEX_ZIP_CODE.matches(text)) {
                    // 郵便番号でない場合は検索せず、テキストをクリアして終了
                    address_textview.text = ""
                    return
                }

                // APIを非同期で呼び出し
                callZipCloudApi(text)
                // 検索中メッセージ表示
                address_textview.text = getString(R.string.searching_message)
            }

            /**
             * ZipCloudAPIを呼び出し、レスポンスとして受け取った住所情報を画面上に表示する。
             *
             * @param zipCode 郵便番号
             */
            private fun callZipCloudApi(zipCode: String) {
                // WebAPI呼び出しをコルーチンで非同期実行
                GlobalScope.launch(Dispatchers.Main) {
                    withContext(Dispatchers.Default) {
                        getResponse(URL.format(zipCode))

                    }.let {
                        if (isCancelled(zipCode)) {
                            // キャンセルされた場合は何もせず終了
                            return@let
                        }

                        // JSONレスポンスをデシリアライズ
                        val results = Gson().fromJson(it, ZipResponse::class.java)?.results
                        if (results == null) {
                            // 検索失敗した場合はメッセージ表示
                            Log.d(TAG, "Not found zipcode=$zipCode")
                            address_textview.text = getString(R.string.search_not_found_message)
                            return@let
                        }

                        // ペイロードの1件目を表示
                        val address = results[0]
                        address_textview.text =
                            address.run {
                                arrayOf(address1, address2, address3).joinToString(" ")
                            }
                    }
                }
            }

            /**
             * 郵便番号検索がキャンセルされたかどうか判定する。
             *
             * @param zipCode 検索した郵便番号
             * @return キャンセルされていれば `true`
             */
            private fun isCancelled(zipCode: String): Boolean {
                // 検索時の郵便番号から変更されている場合はキャンセル扱い
                val text = zip_code_edittext.text.toString()
                return text != zipCode
            }

            /**
             * WebAPIを呼び出しレスポンスを受け取る。
             *
             * @param url WebAPIのURL
             * @return レスポンス
             */
            private fun getResponse(url: String): String? {
                return OkHttpClient()
                    .newCall(Request.Builder().url(url).build())
                    .execute()
                    .body?.string()
            }
        })
    }
}