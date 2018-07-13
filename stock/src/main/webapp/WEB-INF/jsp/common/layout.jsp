<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<!DOCTYPE html>

<html>
	<head>
		<tiles:insertAttribute name="head" />
		<title></title>
	</head>
	<body ng-app="myApp" ng-controller="controller as ctrl">
		<tiles:insertAttribute name="body" />
	</body>
	
	<tiles:insertAttribute name="script-common" />
	<tiles:insertAttribute name="script" /> 
</html>
