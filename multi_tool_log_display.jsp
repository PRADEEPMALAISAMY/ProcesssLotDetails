<%@ page pageEncoding="UTF-8"%>
<%@ page language="java"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="fh" uri="http://java.sun.com/jsf/html"%>
<%@ taglib prefix="h" uri="http://java.sap.com/jsf/html"%>
<%@ taglib prefix="sap" uri="http://java.sap.com/jsf/html/extended"%>
<%@ taglib prefix="ls" uri="http://java.sap.com/jsf/html/internal"%>
<f:subview id="multiToolLogDisplay">
	<sap:panel id="displayPanel" width="100%" height="100%"
		isCollapsible="false" borderDesign="BOX"
		contentAreaDesign="transparent">
		<f:facet name="header">
			<h:outputText id="headerTitle"
				value="#{gapiI18nTransformer['Z_MULTI_TOOL_LOG_DISP.LABEL']} " />
		</f:facet>
		<ls:messageBar id="messageBar" rendered="false"
			text="#{ToolLogDispBean.message}"></ls:messageBar>
		<sap:panelGrid>
			<sap:panelGroup>
				<sap:panelRow>
					<ls:label text="" visibility="BLANK" />
				</sap:panelRow>
				<sap:panelGrid width="100%">
					<sap:panelGroup halign="left" height="100%" width="40%">
						<sap:panel id="tablePanel" height="40%" isCollapsible="false"
							isCollapsed="false" contentAreaDesign="transparent">
							<f:facet name="header">
								<h:outputText id="tableTitle"
									value="#{gapiI18nTransformer['Z_MULTI_TOOL_LOG_DISP_DETAIL.LABEL']} " />
							</f:facet>
							<sap:panelGrid cellHalign="start" cellValign="top" columns="1">
								<sap:panelGroup backgroundDesign="transparent">
									<sap:dataTable binding="#{toolLogDispConfig.table}"
										value="#{ToolLogDispBean.multiToolLogDispList}"
										rendered="true" browsingMode="scrollbar" id="tableValue"
										columnReorderingEnabled="true"  var="node" >
										<sap:rowSelector id="rowSelector" selectionMode="single"
											selectionBehaviour="client" value="#{node.selected}"
											submitOnRowDoubleClick="true"  rendered="true" 
											actionListener="#{ToolLogDispBean.tableRowSelection}">
										</sap:rowSelector>
									</sap:dataTable>
								</sap:panelGroup>
							</sap:panelGrid>
						</sap:panel>
					</sap:panelGroup>
				</sap:panelGrid>
				<sap:panelRow>
				 <sap:panelPopup id="multiToolDispPopup" rendered="#{ToolLogDispBean.multiToolDispRendered}" mode="modeless" height="400px" width="400px">
					<f:facet name="header">
							<h:outputText value="Tool Number And Group Details"/>
					</f:facet>
				<sap:panelRow>
					<ls:label text="" visibility="BLANK" />
				</sap:panelRow>
						<sap:panelGrid width="100%">
							<sap:panelGroup halign="left" height="100%" width="40%">
								<sap:outputLabel value="Tool Group :" align="right" />
								<ls:label text="" visibility="BLANK" />
								<sap:commandInputText id="toolGrpText" submitOnFieldHelp="false"
									submitOnChange="false" submitOnTabout="false" value="#{ToolLogDispBean.toolGroup}">
									<f:attribute name="upperCase" value="true" />
								</sap:commandInputText>
								<ls:label text="" visibility="BLANK" />
								<ls:label text="" visibility="BLANK" />
								<sap:outputLabel value="Tool Number" align="right" />
								<ls:label text="" visibility="BLANK" />
								<sap:commandInputText id="toolNumText" submitOnFieldHelp="false"
									submitOnChange="false" submitOnTabout="false" value="#{ToolLogDispBean.toolNumber}">
									<f:attribute name="upperCase" value="true" />
								</sap:commandInputText>
							</sap:panelGroup>
						</sap:panelGrid>
				</sap:panelPopup> 
				</sap:panelRow>
			</sap:panelGroup>
		</sap:panelGrid>
	</sap:panel>
</f:subview>