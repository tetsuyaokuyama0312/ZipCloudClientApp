package com.app.zipcloudclientapp

/**
 * ZipCloudAPIのレスポンスを表すデータクラス。
 *
 * @property status HTTPステータス
 * @property message エラーメッセージ
 * @property results ペイロード
 * @constructor ZipResponseを作成する。
 */
data class ZipResponse(
    var status: String? = null,
    var message: String? = null,
    var results: ArrayList<Address> = ArrayList()
)