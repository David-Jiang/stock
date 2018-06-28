<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<script type="text/javascript">
	app.controller('controller', function ($scope,$http,$translate) {
		$scope.stockStatus = "股市資料更新";
		$scope.tradeStatus = "貿易資料更新";
		$scope.testStatus = "測試用";
		
		$scope.say = function() {
			var pathName = window.location.pathname;
			if (!$scope.startDate || !$scope.endDate) {
				return swal("錯誤!", "請輸入日期", "error");
			} else if ($scope.startDate > $scope.endDate) {
				return swal("錯誤!", "起日不可大於迄日", "error");
			}
			document.forms[0].action = pathName + 'say';
			document.forms[0].submit();
		}
		
		$scope.report = function() {
			var pathName = window.location.pathname;
			document.forms[1].action = pathName + 'rpt1';
			document.forms[2].action = pathName + 'rpt2';
			document.forms[3].action = pathName + 'rpt3';
			document.forms[4].action = pathName + 'rpt4';
			
			document.forms[1].submit();
			setTimeout(function(){ document.forms[2].submit(); }, 2000);
			setTimeout(function(){ document.forms[3].submit(); }, 4000);
			setTimeout(function(){ document.forms[4].submit(); }, 6000);
		}
		
		$scope.update = function(event) {
			var id = event.target.id;
			if (id == 'stock') {
				$scope.stockDisabled = true;
				$scope.stockStatus = "正在進行股市資料轉檔，請稍後";
			} else if (id == 'trade') {
				$scope.tradeDisabled = true;
				$scope.tradeStatus = "正在進行貿易資料轉檔，請稍後";
			} else {
				$scope.testDisabled = true;
				$scope.testStatus = "請稍後";
			}
			$http.get('./' + event.target.id)
	  		.then(
	  			function (response) {
	  				if (id == 'stock') {
	  					$scope.stockDisabled = false;
	  					$scope.stockStatus = "股市資料更新";
	  				} else if (id == 'trade') {
	  					$scope.tradeDisabled = false;
	  					$scope.tradeStatus = "貿易資料更新";
	  				} else {
	  					$scope.testDisabled = false;
	  					$scope.testStatus = "測試用";
	  				}
	  				if (response.data.indexOf('error') > -1) {
	  					swal("錯誤!", response.data, "error");
	  				} else {
	  					swal("成功!", response.data, "success");
	  				}
	 			}, 
	  			function (response) {
	 				if (id == 'stock') {
	  					$scope.stockDisabled = false;
	  					$scope.stockStatus = "股市資料更新";
	  				} else if (id == 'trade') {
	  					$scope.tradeDisabled = false;
	  					$scope.tradeStatus = "貿易資料更新";
	  				} else {
	  					$scope.testDisabled = false;
	  					$scope.testStatus = "測試用";
	  				}
	 				swal("錯誤!", "系統發生錯誤", "error");
	  			}
	  		);
		}
		
	});
</script>