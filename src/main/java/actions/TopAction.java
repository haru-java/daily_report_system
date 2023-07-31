/*

Lesson 17Chapter 7.1
トップページ表示のアクションとビューを作成するその１

ログイン後に表示するトップページを作成。
→topPage フォルダの中にのindex.jspのビューへ。


Lesson 17Chapter 10
トップページに自分の日報一覧を表示するその１アクション
日報の管理機能が作成できました。最後に、トップページに自分の日報の一覧を表示するように変更しましょう。
→続きのビューへ/src/main/webapp/WEB-INF/views/topPage/index.jsp

*/

package actions;

import java.io.IOException;
import java.util.List; //追記

import javax.servlet.ServletException;

import actions.views.EmployeeView; //追記
import actions.views.ReportView; //追記
import constants.AttributeConst;
import constants.ForwardConst;
import constants.JpaConst;  //追記
import services.ReportService;  //追記

/**
 * トップページに関する処理を行うActionクラス
 *
 */
public class TopAction extends ActionBase {

    private ReportService service; //追記

    /**
     * indexメソッドを実行する
     */
    @Override
    public void process() throws ServletException, IOException {

        service = new ReportService(); //追記

        //メソッドを実行
        invoke();

        service.close(); //追記

    }

    /**
     * 一覧画面を表示する
     */
    public void index() throws ServletException, IOException {

        // 以下追記

        //セッションからログイン中の従業員情報を取得
        EmployeeView loginEmployee = (EmployeeView) getSessionScope(AttributeConst.LOGIN_EMP);

        //ログイン中の従業員が作成した日報データを、指定されたページ数の一覧画面に表示する分取得する
        int page = getPage();
        List<ReportView> reports = service.getMinePerPage(loginEmployee, page);

        //ログイン中の従業員が作成した日報データの件数を取得
        long myReportsCount = service.countAllMine(loginEmployee);

        putRequestScope(AttributeConst.REPORTS, reports); //取得した日報データ
        putRequestScope(AttributeConst.REP_COUNT, myReportsCount); //ログイン中の従業員が作成した日報の数
        putRequestScope(AttributeConst.PAGE, page); //ページ数
        putRequestScope(AttributeConst.MAX_ROW, JpaConst.ROW_PER_PAGE); //1ページに表示するレコードの数

        //↑ここまで追記

        //セッションにフラッシュメッセージが設定されている場合はリクエストスコープに移し替え、セッションからは削除する
        String flush = getSessionScope(AttributeConst.FLUSH);
        if (flush != null) {
            putRequestScope(AttributeConst.FLUSH, flush);
            removeSessionScope(AttributeConst.FLUSH);
        }

        //一覧画面を表示
        forward(ForwardConst.FW_TOP_INDEX);
    }

}