<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<main class="container" style="text-align:center;margin-top:50px">
	<H3>我有話要說</H3>
	<table class="table">
		<tr>
			<td align="right" style="width:50%">請輸入起始日期：</td>
			<td align="left"><input type="text" ng-model="startDate" maxLength="7" size="5" ng-change="endDate = startDate"></td>
		</tr>
		<tr>
			<td align="right">請輸入結束日期：</td>
			<td align="left"><input type="text" ng-model="endDate" maxLength="7" size="5"></td>
		</tr>
		<tr>
			<td colspan="2">
				<form method="post" target="_blank">
					<input type="hidden" name="startDate" value="{{startDate}}">
					<input type="hidden" name="endDate" value="{{endDate}}">
				</form>
				<input type="button" value="產生我有話要說報表" class="btn btn-primary" ng-click="say()">
			</td>
		</tr>
	</table>
	
	<form method="post" target="_blank"></form>
	<form method="post" target="_blank"></form>
	<form method="post" target="_blank"></form>
	<form method="post" target="_blank"></form>
	<input type="button" value="產生動擔與公示月結報表" class="btn btn-primary" ng-click="report()">
	
	<br><br>
	<button type="button" class="btn btn-primary" id="stock" ng-click="update($event)" ng-disabled="stockDisabled">{{stockStatus}}</button>
	<br><br>
	<button type="button" class="btn btn-primary" id="trade" ng-click="update($event)" ng-disabled="tradeDisabled">{{tradeStatus}}</button>
	<br><br>
	<button type="button" class="btn btn-primary" id="test" ng-click="update($event)" ng-disabled="testDisabled">{{testStatus}}</button>
</main>