/*
Lesson 17Chapter 4.4


定数ファイルの作成その５

 */


package constants;

/**
 * アプリケーションスコープのパラメータ名を定義するEnumクラス
 *
 */
public enum PropertyConst {

    //ペッパー文字列
    PEPPER("pepper");

    private final String text;
    private PropertyConst(final String text) {
        this.text = text;
    }

    public String getValue() {
        return this.text;
    }
}