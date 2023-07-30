<%--
Lesson 17Chapter 5.4
レイアウトファイルと共通エラー画面の作成その２
エラー画面

http://localhost:8080/daily_report_system/
--%>


<%@ page language="java" contentType="text/html; charset=UTF-8"
        pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:import url="../layout/app.jsp">
        <c:param name="content">
                <h2>お探しのページは見つかりませんでした。</h2>
        </c:param>
</c:import>