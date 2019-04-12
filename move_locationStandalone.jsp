<%@page pageEncoding="UTF-8"%>
<%@ page language="java"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="fh" uri="http://java.sun.com/jsf/html"%>
<%@ taglib prefix="h" uri="http://java.sap.com/jsf/html"%>
<%@ taglib prefix="sap" uri="http://java.sap.com/jsf/html/extended"%>
<%@ taglib prefix="ls" uri="http://java.sap.com/jsf/html/internal"%>
<f:view>
	<sap:html>
	<sap:body title="Material Movement Location-Standalone"
		id="garbodyStandaloneDashboard" height="100%" browserHistory="disabled" focusId="dashBoardPanel">
		<h:form id="garkanbanStandaloneMainForm">
			<f:attribute name="height" value="100%" />
			<f:attribute name="sap-delta-id" value="#{sap:toClientId('garkanbanStandaloneMainForm')}" />
			<sap:panel id="gardashStandaloneBoardPanel" width="100%" height="100%"	isCollapsible="false" contentAreaDesign="transparent" isCollapsed="false">
				  <sap:include page="/com/vmsdev/carrier/movelocation/move_location.jsp"/>
			 </sap:panel>
			</h:form>
	</sap:body>
	</sap:html>
</f:view>