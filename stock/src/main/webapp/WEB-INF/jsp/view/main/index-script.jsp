<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<script type="text/javascript">
	app.controller('controller', function ($scope,$http,$translate) {
		$scope.test = function() {
			var pathName = window.location.pathname;
			document.forms[0].action = pathName + 'getStockInfo';
			document.forms[0].submit();
		}
		
	});
</script>