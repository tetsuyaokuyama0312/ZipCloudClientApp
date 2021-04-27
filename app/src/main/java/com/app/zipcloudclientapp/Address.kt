package com.app.zipcloudclientapp

/**
 * ZipResponseのペイロードとなる、住所情報を格納するデータクラス。
 *
 * @property zipcode 郵便番号
 * @property prefcode 都道府県コード
 * @property address1 都道府県名
 * @property address2 市区町村名
 * @property address3 町域名
 * @property kana1 都道府県名カナ
 * @property kana2 市区町村名カナ
 * @property kana3 町域名カナ
 * @constructor 住所情報を作成する。
 */
data class Address(
    var zipcode: String? = null,
    var prefcode: String? = null,
    var address1: String? = null,
    var address2: String? = null,
    var address3: String? = null,
    var kana1: String? = null,
    var kana2: String? = null,
    var kana3: String? = null
)