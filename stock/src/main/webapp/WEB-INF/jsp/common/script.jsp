<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
	<script src="https://code.jquery.com/jquery-3.1.1.slim.min.js" integrity="sha384-A7FZj7v+d/sdmMqp/nOQwliLvUsJfDHW+k9Omg/a/EheAdgtzNs3hpfag6Ed950n" crossorigin="anonymous"></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/tether/1.4.0/js/tether.min.js" integrity="sha384-DztdAPBWPRXSA/3eYEEUWrWCy7G5KFbe8fFjk5JAIxUYHKkDx6Qin1DkWx51bBrb" crossorigin="anonymous"></script>
	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-alpha.6/js/bootstrap.min.js" integrity="sha384-vBWWzlZJ8ea9aCX4pEW3rVHjgjt7zpkNpZk+02D9phzyeVkE+jo0ieGizqPLForn" crossorigin="anonymous"></script>
	<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.4.8/angular.min.js"></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/limonte-sweetalert2/6.6.5/sweetalert2.min.js"></script>
	<script src="js/lodash.js"></script>
	<script src="js/angular-translate.min.js"></script>
	<script>
		var app = angular.module("myApp", ['pascalprecht.translate']);
		app.config(function ($translateProvider) {
			
			$translateProvider.translations('en', {
			    TITLE: 'Hello',
			    FOO: 'This is a paragraph{{test}}',
			    BUTTON_LANG_EN: 'english',
			    BUTTON_LANG_CH: 'chinese'
			});
			$translateProvider.translations('zh-tw', {
				TITLE: '哈囉',
			    FOO: '這是段落{{test}}',
			    BUTTON_LANG_EN: '英文',
			    BUTTON_LANG_CH: '中文'
			});
			//$translateProvider.preferredLanguage('ch');
			$translateProvider.determinePreferredLanguage();
		});
		app.directive('blur', function () {
		    return {
		        require: '?ngModel',
		        link: function (scope, elem, attrs, ctrl) {
		            if (!ctrl) return;
		           
		            elem.on('blur', function() {
		            	var val = elem[0].value;
		            	if (!!val && val < 1) {
		            		swal('錯誤','輸入數字不可小於1','error');
		            		ctrl.$setViewValue('');
			                ctrl.$render();
			                return '';
		            	}
		            });
		        }
		    };
		});
	</script>