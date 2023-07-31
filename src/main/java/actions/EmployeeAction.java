/*
Lesson 17Chapter 6.6
Employeeに対するアクションとビューの作成1：indexの作成その１

Lesson 17Chapter 6.7
Employeeに対するアクションとビューの作成2：newその１
entryNew() メソッドを以下の内容で追記

Lesson 17Chapter 6.8
Employeeに対するアクションの作成3：createその１
 create() メソッドを以下の内容で追記すると従業員登録できるように。

Lesson 17Chapter 6.9
Employeeに対するアクションとビューの作成4：showその１
show() メソッドを以下の内容で追記

Lesson 17Chapter 6.10
Employeeに対するアクションとビューの作成5：editその１
edit() メソッドを以下の内容で追記→edit.jspのビューへ。

Lesson 17Chapter 6.11
Employeeに対するアクションの作成6：update
update() メソッドを以下の内容で追記。既存の登録情報を更新できるようになった。

Lesson 17Chapter 6.12
Employeeに対するアクションの作成7：destroy
destroy() メソッドを以下の内容で追記→削除できるようになった。
 http://localhost:8080/daily_report_system/?action=Employee&command=index

 Lesson 17Chapter 8.6
従業員の各アクションは管理者のみ実行できるようにするその１
 「管理者」以外の従業員がアクセスした場合はエラー画面 お探しのページは見つかりませんでした。 を表示
ログイン者が管理者かどうかチェックするメソッドを追加
管理者かどうかを boolean（true：管理者／false：管理者ではない）で返却
index()、entryNew()、create()、show()、edit()、update()、destroy() すべてについてこの判定を追加
動作確認：一般ユーザでログイン、従業員管理のアクションを発生させるURLを入力
http://localhost:8080/daily_report_system/?action=Employee&command=index

*/

package actions;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import actions.views.EmployeeView;
import constants.AttributeConst;
import constants.ForwardConst;
import constants.JpaConst;
import constants.MessageConst;
import constants.PropertyConst;
import services.EmployeeService;

/**
 * 従業員に関わる処理を行うActionクラス
 *
 */
public class EmployeeAction extends ActionBase {

    private EmployeeService service;

    /**
     * メソッドを実行する
     */
    @Override
    public void process() throws ServletException, IOException {

        service = new EmployeeService();

        //メソッドを実行
        invoke();

        service.close();
    }

    /**
     * 一覧画面を表示する
     * @throws ServletException
     * @throws IOException
     */
    public void index() throws ServletException, IOException {

        //管理者かどうかのチェック //追記
        if (checkAdmin()) { //追記

            //指定されたページ数の一覧画面に表示するデータを取得
            int page = getPage();
            List<EmployeeView> employees = service.getPerPage(page);

            //全ての従業員データの件数を取得
            long employeeCount = service.countAll();

            putRequestScope(AttributeConst.EMPLOYEES, employees); //取得した従業員データ
            putRequestScope(AttributeConst.EMP_COUNT, employeeCount); //全ての従業員データの件数
            putRequestScope(AttributeConst.PAGE, page); //ページ数
            putRequestScope(AttributeConst.MAX_ROW, JpaConst.ROW_PER_PAGE); //1ページに表示するレコードの数

            //セッションにフラッシュメッセージが設定されている場合はリクエストスコープに移し替え、セッションからは削除する
            String flush = getSessionScope(AttributeConst.FLUSH);
            if (flush != null) {
                putRequestScope(AttributeConst.FLUSH, flush);
                removeSessionScope(AttributeConst.FLUSH);
            }

            //一覧画面を表示
            forward(ForwardConst.FW_EMP_INDEX);

        } //追記

    }

    /**
     * 新規登録画面を表示する
     * @throws ServletException
     * @throws IOException
     */
    public void entryNew() throws ServletException, IOException {

        //管理者かどうかのチェック //追記
        if (checkAdmin()) { //追記
            putRequestScope(AttributeConst.TOKEN, getTokenId()); //CSRF対策用トークン
            putRequestScope(AttributeConst.EMPLOYEE, new EmployeeView()); //空の従業員インスタンス

            //新規登録画面を表示
            forward(ForwardConst.FW_EMP_NEW);
        } //追記
    }

    /**
     * 新規登録を行う
     * @throws ServletException
     * @throws IOException
     */
    public void create() throws ServletException, IOException {

        //CSRF対策 tokenのチェック
        if (checkAdmin() && checkToken()) { //追記

            //パラメータの値を元に従業員情報のインスタンスを作成する
            EmployeeView ev = new EmployeeView(
                    null,
                    getRequestParam(AttributeConst.EMP_CODE),
                    getRequestParam(AttributeConst.EMP_NAME),
                    getRequestParam(AttributeConst.EMP_PASS),
                    toNumber(getRequestParam(AttributeConst.EMP_ADMIN_FLG)),
                    null,
                    null,
                    AttributeConst.DEL_FLAG_FALSE.getIntegerValue());

            //アプリケーションスコープからpepper文字列を取得
            String pepper = getContextScope(PropertyConst.PEPPER);

            //従業員情報登録
            List<String> errors = service.create(ev, pepper);

            if (errors.size() > 0) {
                //登録中にエラーがあった場合

                putRequestScope(AttributeConst.TOKEN, getTokenId()); //CSRF対策用トークン
                putRequestScope(AttributeConst.EMPLOYEE, ev); //入力された従業員情報
                putRequestScope(AttributeConst.ERR, errors); //エラーのリスト

                //新規登録画面を再表示
                forward(ForwardConst.FW_EMP_NEW);

            } else {
                //登録中にエラーがなかった場合

                //セッションに登録完了のフラッシュメッセージを設定
                putSessionScope(AttributeConst.FLUSH, MessageConst.I_REGISTERED.getMessage());

                //一覧画面にリダイレクト
                redirect(ForwardConst.ACT_EMP, ForwardConst.CMD_INDEX);
            }

        }
    }

    /**
     * 詳細画面を表示する
     * @throws ServletException
     * @throws IOException
     */
    public void show() throws ServletException, IOException {

        //管理者かどうかのチェック //追記
        if (checkAdmin()) { //追記

            //idを条件に従業員データを取得する
            EmployeeView ev = service.findOne(toNumber(getRequestParam(AttributeConst.EMP_ID)));

            if (ev == null || ev.getDeleteFlag() == AttributeConst.DEL_FLAG_TRUE.getIntegerValue()) {

                //データが取得できなかった、または論理削除されている場合はエラー画面を表示
                forward(ForwardConst.FW_ERR_UNKNOWN);
                return;
            }

            putRequestScope(AttributeConst.EMPLOYEE, ev); //取得した従業員情報

            //詳細画面を表示
            forward(ForwardConst.FW_EMP_SHOW);
        } //追記

    }

    /**
     * 編集画面を表示する
     * @throws ServletException
     * @throws IOException
     */
    public void edit() throws ServletException, IOException {

        //管理者かどうかのチェック //追記
        if (checkAdmin()) { //追記

            //idを条件に従業員データを取得する
            EmployeeView ev = service.findOne(toNumber(getRequestParam(AttributeConst.EMP_ID)));

            if (ev == null || ev.getDeleteFlag() == AttributeConst.DEL_FLAG_TRUE.getIntegerValue()) {

                //データが取得できなかった、または論理削除されている場合はエラー画面を表示
                forward(ForwardConst.FW_ERR_UNKNOWN);
                return;
            }

            putRequestScope(AttributeConst.TOKEN, getTokenId()); //CSRF対策用トークン
            putRequestScope(AttributeConst.EMPLOYEE, ev); //取得した従業員情報

            //編集画面を表示する
            forward(ForwardConst.FW_EMP_EDIT);

        } //追記
    }

    /**
     * 更新を行う
     * @throws ServletException
     * @throws IOException
     */
    public void update() throws ServletException, IOException {

        //CSRF対策 tokenのチェック
        if (checkAdmin() && checkToken()) { //追記
            //パラメータの値を元に従業員情報のインスタンスを作成する
            EmployeeView ev = new EmployeeView(
                    toNumber(getRequestParam(AttributeConst.EMP_ID)),
                    getRequestParam(AttributeConst.EMP_CODE),
                    getRequestParam(AttributeConst.EMP_NAME),
                    getRequestParam(AttributeConst.EMP_PASS),
                    toNumber(getRequestParam(AttributeConst.EMP_ADMIN_FLG)),
                    null,
                    null,
                    AttributeConst.DEL_FLAG_FALSE.getIntegerValue());

            //アプリケーションスコープからpepper文字列を取得
            String pepper = getContextScope(PropertyConst.PEPPER);

            //従業員情報更新
            List<String> errors = service.update(ev, pepper);

            if (errors.size() > 0) {
                //更新中にエラーが発生した場合

                putRequestScope(AttributeConst.TOKEN, getTokenId()); //CSRF対策用トークン
                putRequestScope(AttributeConst.EMPLOYEE, ev); //入力された従業員情報
                putRequestScope(AttributeConst.ERR, errors); //エラーのリスト

                //編集画面を再表示
                forward(ForwardConst.FW_EMP_EDIT);
            } else {
                //更新中にエラーがなかった場合

                //セッションに更新完了のフラッシュメッセージを設定
                putSessionScope(AttributeConst.FLUSH, MessageConst.I_UPDATED.getMessage());

                //一覧画面にリダイレクト
                redirect(ForwardConst.ACT_EMP, ForwardConst.CMD_INDEX);
            }
        }
    }

    /**
     * 論理削除を行う
     * @throws ServletException
     * @throws IOException
     */
    public void destroy() throws ServletException, IOException {

        //CSRF対策 tokenのチェック
        if (checkAdmin() && checkToken()) { //追記

            //idを条件に従業員データを論理削除する
            service.destroy(toNumber(getRequestParam(AttributeConst.EMP_ID)));

            //セッションに削除完了のフラッシュメッセージを設定
            putSessionScope(AttributeConst.FLUSH, MessageConst.I_DELETED.getMessage());

            //一覧画面にリダイレクト
            redirect(ForwardConst.ACT_EMP, ForwardConst.CMD_INDEX);
        }
    }

    /**
     * ログイン中の従業員が管理者かどうかチェックし、管理者でなければエラー画面を表示
     * true: 管理者 false: 管理者ではない
     * @throws ServletException
     * @throws IOException
     */
    private boolean checkAdmin() throws ServletException, IOException {

        //セッションからログイン中の従業員情報を取得
        EmployeeView ev = (EmployeeView) getSessionScope(AttributeConst.LOGIN_EMP);

        //管理者でなければエラー画面を表示
        if (ev.getAdminFlag() != AttributeConst.ROLE_ADMIN.getIntegerValue()) {

            forward(ForwardConst.FW_ERR_UNKNOWN);
            return false;

        } else {

            return true;
        }

    }

}












/*
Lesson 17Chapter 6.12までのもの

package actions;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import actions.views.EmployeeView;
import constants.AttributeConst;
import constants.ForwardConst;
import constants.JpaConst;
import constants.MessageConst;
import constants.PropertyConst;
import services.EmployeeService;


public class EmployeeAction extends ActionBase {

    private EmployeeService service;


    @Override
    public void process() throws ServletException, IOException {

        service = new EmployeeService();

        //メソッドを実行
        invoke();

        service.close();
    }


    public void index() throws ServletException, IOException {

        //指定されたページ数の一覧画面に表示するデータを取得
        int page = getPage();
        List<EmployeeView> employees = service.getPerPage(page);

        //全ての従業員データの件数を取得
        long employeeCount = service.countAll();

        putRequestScope(AttributeConst.EMPLOYEES, employees); //取得した従業員データ
        putRequestScope(AttributeConst.EMP_COUNT, employeeCount); //全ての従業員データの件数
        putRequestScope(AttributeConst.PAGE, page); //ページ数
        putRequestScope(AttributeConst.MAX_ROW, JpaConst.ROW_PER_PAGE); //1ページに表示するレコードの数

        //セッションにフラッシュメッセージが設定されている場合はリクエストスコープに移し替え、セッションからは削除する
        String flush = getSessionScope(AttributeConst.FLUSH);
        if (flush != null) {
            putRequestScope(AttributeConst.FLUSH, flush);
            removeSessionScope(AttributeConst.FLUSH);
        }

        //一覧画面を表示
        forward(ForwardConst.FW_EMP_INDEX);

    }


public void entryNew() throws ServletException, IOException {

    putRequestScope(AttributeConst.TOKEN, getTokenId()); //CSRF対策用トークン
    putRequestScope(AttributeConst.EMPLOYEE, new EmployeeView()); //空の従業員インスタンス

    //新規登録画面を表示
    forward(ForwardConst.FW_EMP_NEW);
 }

public void create() throws ServletException, IOException {

    //CSRF対策 tokenのチェック
    if (checkToken()) {

        //パラメータの値を元に従業員情報のインスタンスを作成する
        EmployeeView ev = new EmployeeView(
                null,
                getRequestParam(AttributeConst.EMP_CODE),
                getRequestParam(AttributeConst.EMP_NAME),
                getRequestParam(AttributeConst.EMP_PASS),
                toNumber(getRequestParam(AttributeConst.EMP_ADMIN_FLG)),
                null,
                null,
                AttributeConst.DEL_FLAG_FALSE.getIntegerValue());

        //アプリケーションスコープからpepper文字列を取得
        String pepper = getContextScope(PropertyConst.PEPPER);

        //従業員情報登録
        List<String> errors = service.create(ev, pepper);

        if (errors.size() > 0) {
            //登録中にエラーがあった場合

            putRequestScope(AttributeConst.TOKEN, getTokenId()); //CSRF対策用トークン
            putRequestScope(AttributeConst.EMPLOYEE, ev); //入力された従業員情報
            putRequestScope(AttributeConst.ERR, errors); //エラーのリスト

            //新規登録画面を再表示
            forward(ForwardConst.FW_EMP_NEW);

        } else {
            //登録中にエラーがなかった場合

            //セッションに登録完了のフラッシュメッセージを設定
            putSessionScope(AttributeConst.FLUSH, MessageConst.I_REGISTERED.getMessage());

            //一覧画面にリダイレクト
            redirect(ForwardConst.ACT_EMP, ForwardConst.CMD_INDEX);
        }

    }
}

public void show() throws ServletException, IOException {

    //idを条件に従業員データを取得する
    EmployeeView ev = service.findOne(toNumber(getRequestParam(AttributeConst.EMP_ID)));

    if (ev == null || ev.getDeleteFlag() == AttributeConst.DEL_FLAG_TRUE.getIntegerValue()) {

        //データが取得できなかった、または論理削除されている場合はエラー画面を表示
        forward(ForwardConst.FW_ERR_UNKNOWN);
        return;
    }

    putRequestScope(AttributeConst.EMPLOYEE, ev); //取得した従業員情報

    //詳細画面を表示
    forward(ForwardConst.FW_EMP_SHOW);
}

public void edit() throws ServletException, IOException {

    //idを条件に従業員データを取得する
    EmployeeView ev = service.findOne(toNumber(getRequestParam(AttributeConst.EMP_ID)));

    if (ev == null || ev.getDeleteFlag() == AttributeConst.DEL_FLAG_TRUE.getIntegerValue()) {

        //データが取得できなかった、または論理削除されている場合はエラー画面を表示
        forward(ForwardConst.FW_ERR_UNKNOWN);
        return;
    }

    putRequestScope(AttributeConst.TOKEN, getTokenId()); //CSRF対策用トークン
    putRequestScope(AttributeConst.EMPLOYEE, ev); //取得した従業員情報

    //編集画面を表示する
    forward(ForwardConst.FW_EMP_EDIT);

}


public void update() throws ServletException, IOException {

    //CSRF対策 tokenのチェック
    if (checkToken()) {
        //パラメータの値を元に従業員情報のインスタンスを作成する
        EmployeeView ev = new EmployeeView(
                toNumber(getRequestParam(AttributeConst.EMP_ID)),
                getRequestParam(AttributeConst.EMP_CODE),
                getRequestParam(AttributeConst.EMP_NAME),
                getRequestParam(AttributeConst.EMP_PASS),
                toNumber(getRequestParam(AttributeConst.EMP_ADMIN_FLG)),
                null,
                null,
                AttributeConst.DEL_FLAG_FALSE.getIntegerValue());

        //アプリケーションスコープからpepper文字列を取得
        String pepper = getContextScope(PropertyConst.PEPPER);

        //従業員情報更新
        List<String> errors = service.update(ev, pepper);

        if (errors.size() > 0) {
            //更新中にエラーが発生した場合

            putRequestScope(AttributeConst.TOKEN, getTokenId()); //CSRF対策用トークン
            putRequestScope(AttributeConst.EMPLOYEE, ev); //入力された従業員情報
            putRequestScope(AttributeConst.ERR, errors); //エラーのリスト

            //編集画面を再表示
            forward(ForwardConst.FW_EMP_EDIT);
        } else {
            //更新中にエラーがなかった場合

            //セッションに更新完了のフラッシュメッセージを設定
            putSessionScope(AttributeConst.FLUSH, MessageConst.I_UPDATED.getMessage());

            //一覧画面にリダイレクト
            redirect(ForwardConst.ACT_EMP, ForwardConst.CMD_INDEX);
        }
    }
}


public void destroy() throws ServletException, IOException {

    //CSRF対策 tokenのチェック
    if (checkToken()) {

        //idを条件に従業員データを論理削除する
        service.destroy(toNumber(getRequestParam(AttributeConst.EMP_ID)));

        //セッションに削除完了のフラッシュメッセージを設定
        putSessionScope(AttributeConst.FLUSH, MessageConst.I_DELETED.getMessage());

        //一覧画面にリダイレクト
        redirect(ForwardConst.ACT_EMP, ForwardConst.CMD_INDEX);
    }
}
}
*/
