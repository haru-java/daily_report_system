<%--
Lesson 17Chapter 5.4
レイアウトファイルと共通エラー画面の作成その１
全画面共通のひな形となるレイアウトファイル

Lesson 17Chapter 8.4
ログアウトアクションの作成その２
ログインした従業員の権限によって表示メニューを変える
/src/main/java/actions/AuthAction.javaでlogout() メソッドを以下の内容で追記後、
ここで、
ログイン中の画面にログアウトへのリンクを表示するようメニュー部分を変更します。
また、従業員管理と日報管理へのリンク、トップページへのリンクも追加します。
→続きは/src/main/webapp/css/style.cssへ

--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="constants.ForwardConst" %>
<%@ page import="constants.AttributeConst" %>

<c:set var="actTop" value="${ForwardConst.ACT_TOP.getValue()}" />
<c:set var="actEmp" value="${ForwardConst.ACT_EMP.getValue()}" />
<c:set var="actRep" value="${ForwardConst.ACT_REP.getValue()}" />
<c:set var="actAuth" value="${ForwardConst.ACT_AUTH.getValue()}" />

<c:set var="commIdx" value="${ForwardConst.CMD_INDEX.getValue()}" />
<c:set var="commOut" value="${ForwardConst.CMD_LOGOUT.getValue()}" />

<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
    <title><c:out value="日報管理システム" /></title>
    <link rel="stylesheet" href="<c:url value='/css/reset.css' />">
    <link rel="stylesheet" href="<c:url value='/css/style.css' />">
</head>
<body>
    <div id="wrapper">
        <div id="header">
            <div id="header_menu">
                <h1><a href="<c:url value='/?action=${actTop}&command=${commIdx}' />">日報管理システム</a></h1>&nbsp;&nbsp;&nbsp;
                <c:if test="${sessionScope.login_employee != null}">
                    <c:if test="${sessionScope.login_employee.adminFlag == AttributeConst.ROLE_ADMIN.getIntegerValue()}">
                        <a href="<c:url value='?action=${actEmp}&command=${commIdx}' />">従業員管理</a>&nbsp;
                    </c:if>
                    <a href="<c:url value='?action=${actRep}&command=${commIdx}' />">日報管理</a>&nbsp;
                </c:if>
            </div>
            <c:if test="${sessionScope.login_employee != null}">
                <div id="employee_name">
                    <c:out value="${sessionScope.login_employee.name}" />
                    &nbsp;さん&nbsp;&nbsp;&nbsp;
                    <a href="<c:url value='?action=${actAuth}&command=${commOut}' />">ログアウト</a>
                </div>
            </c:if>
        </div>
        <div id="content">${param.content}</div>
        <div id="footer">by Taro Kirameki.</div>
    </div>
</body>
</html>







<%--
Lesson 17Chapter 5.4までのもの

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
    <title><c:out value="日報管理システム" /></title>
    <link rel="stylesheet" href="<c:url value='/css/reset.css' />">
    <link rel="stylesheet" href="<c:url value='/css/style.css' />">
</head>
<body>
    <div id="wrapper">
        <div id="header">
            <div id="header_menu">
                <h1>日報管理システム</h1>
            </div>
        </div>
        <div id="content">${param.content}</div>
        <div id="footer">by Haru.</div>
    </div>
</body>
</html>


--%>