package com.vmsdev.carrier.movelocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import com.sap.me.common.ObjectReference;
import com.sap.me.demand.ShopOrderConfiguration;
import com.sap.me.demand.ShopOrderServiceInterface;
import com.sap.me.extension.Services;
import com.sap.me.frame.service.CommonMethods;
import com.sap.me.productdefinition.BOMConfigurationServiceInterface;
import com.sap.me.productdefinition.ItemConfiguration;
import com.sap.me.productdefinition.ItemConfigurationServiceInterface;
import com.sap.me.productdefinition.OperationBOHandle;
import com.sap.me.production.FindProcessLotByNameRequest;
import com.sap.me.production.ProcessLotBasicConfiguration;
import com.sap.me.production.ProcessLotFullConfiguration;
import com.sap.me.production.ProcessLotMember;
import com.sap.me.production.ProcessLotServiceInterface;
import com.sap.me.production.SfcConfiguration;
import com.sap.me.production.SfcStateServiceInterface;
import com.sap.me.production.SfcStep;
import com.sap.me.production.SfcStepStatusEnum;
import com.sap.me.production.podclient.BasePodPlugin;
import com.sap.me.production.podclient.OperationChangeEvent;
import com.sap.me.production.podclient.OperationChangeListenerInterface;
import com.sap.me.production.podclient.ResourceChangeEvent;
import com.sap.me.production.podclient.ResourceChangeListenerInterface;
import com.sap.me.security.RunAsServiceLocator;
import com.sap.me.wpmf.CompositeEventListenerInterface;
import com.sap.me.wpmf.TableConfigurator;
import com.sap.me.wpmf.util.FacesUtility;
import com.sap.tc.logging.Location;
import com.sap.tc.ls.api.enumerations.LSMessageType;
import com.sap.tc.ls.internal.faces.component.UIMessageBar;
import com.visiprise.frame.configuration.ServiceReference;
import com.vmsdev.kanban.RefreshPluginEvent;
import com.vmsdev.kanban.RefreshPluginEventListener;

public class MoveLocationPlugin extends BasePodPlugin implements OperationChangeListenerInterface,
		ResourceChangeListenerInterface, CompositeEventListenerInterface, RefreshPluginEventListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Location logger = Location.getLocation(MoveLocationPlugin.class.getName());
	private String ACTIVITY_ID;
	private String message = "";
	private String carrier;
	private List<MoveLocationPluginObject> materialList;

	private ProcessLotServiceInterface processLotService;
	private SfcStateServiceInterface sfcStateService;
	private ShopOrderServiceInterface shopOrderService;
	private ItemConfigurationServiceInterface itemConfigurationService;
	private BOMConfigurationServiceInterface bOMConfigurationService;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCarrier() {
		return carrier;
	}

	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}

	public List<MoveLocationPluginObject> getMaterialList() {
		return materialList;
	}

	public void setMaterialList(List<MoveLocationPluginObject> materialList) {
		this.materialList = materialList;
	}

	public MoveLocationPlugin() {
		super();
		getPluginEventManager().addPluginListeners(this.getClass());
		Map<String, String> parameterMap = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap();
		String param = parameterMap.get("ACTIVITY_ID");

		if (param != null && !param.isEmpty())
			ACTIVITY_ID = param;
		else if (param == null) {
			String activityId = getActivityID();
			ACTIVITY_ID = activityId;
		} else {
			this.message = "Current Activity Id Not Found.";
			setMessageBar(true, LSMessageType.ERROR);
		}
	}

	@PostConstruct
	public void init() {
		initService();

	}

	private TableConfigurator moveLocationTableConfigBean;

	private String[] columnNames = new String[] { "MATERIAL", "ORDER", "BATCH", "QTY", "OPERATION", "QTY_IN_QUEUE" };

	private String[] columnDefs = new String[] { "material;Z_MOVE_LOCATION_material.LABEL",
			"order;Z_MOVE_LOCATION_order.LABEL", "batch;Z_MOVE_LOCATION_batch.LABEL",
			"quantity;Z_MOVE_LOCATION_qty.LABEL", "operation;Z_MOVE_LOCATION_operation.LABEL",
			"qtyInQueue ;Z_MOVE_LOCATION_QtyInQueue.LABEL" };

	private Map<String, String> getColumnFieldMaping(String[] columnDefs, String[] columnNames) {
		HashMap<String, String> columnFieldMap = new HashMap<String, String>();
		for (int i = 0; i < columnDefs.length; i++) {
			columnFieldMap.put(columnNames[i], columnDefs[i]);
		}
		return columnFieldMap;
	}

	public TableConfigurator getMoveLocationTableConfigBean() {
		return moveLocationTableConfigBean;
	}

	public void setMoveLocationTableConfigBean(TableConfigurator config) {
		this.moveLocationTableConfigBean = config;
		if (this.moveLocationTableConfigBean.getColumnBindings() == null
				|| this.moveLocationTableConfigBean.getColumnBindings().size() < 1) {

			this.moveLocationTableConfigBean.setListName(null);
			this.moveLocationTableConfigBean.setColumnBindings(getColumnFieldMaping(columnDefs, columnNames));
			this.moveLocationTableConfigBean.setListColumnNames(columnNames);

			this.moveLocationTableConfigBean.setAllowSelections(false);
			this.moveLocationTableConfigBean.setMultiSelectType(false);

			this.moveLocationTableConfigBean.configureTable();
		}
	}

	public void setMessageBar(boolean render, LSMessageType messageType) {
		UIMessageBar messageBar = (UIMessageBar) findComponent(FacesUtility.getFacesContext().getViewRoot(),
				"moveLocsubview:messageBar");
		messageBar.setRendered(render);
		messageBar.setType(messageType);
		UIComponent fieldButtonPanel = findComponent(FacesUtility.getFacesContext().getViewRoot(),
				"moveLocsubview:movecarrierSfcPanel");
		if (fieldButtonPanel != null)
			FacesUtility.addControlUpdate(fieldButtonPanel);
	}

	public void clear(ActionEvent event) {
		setMessageBar(false, null);
		
		this.carrier="";
		
		this.materialList=null;
		
		/*this.moveLocationTableConfigBean.configureTable();
		*/
		
		
	}

	@Override
	public void processRefreshEvent(RefreshPluginEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void processCompositeEvent(List<EventObject> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void processResourceChange(ResourceChangeEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void processOperationChange(OperationChangeEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void retrieve(ActionEvent event) {

		readProcessLotDetails(this.carrier);

	}

	private void readProcessLotDetails(String carrier) {

		try {
			materialList = new ArrayList<MoveLocationPluginObject>();

			FindProcessLotByNameRequest carrierNameRequest = new FindProcessLotByNameRequest(carrier);

			ProcessLotBasicConfiguration lotRef = processLotService.findProcessLotByName(carrierNameRequest);
			lotRef.getRef();
			ProcessLotFullConfiguration lotFullConfiguration = processLotService
					.readProcessLot(new ObjectReference(lotRef.getRef()));

			for (ProcessLotMember lotMember : lotFullConfiguration.getProcessLotMemberList()) {

				Collection<SfcStep> statusConfig = sfcStateService
						.findCurrentRouterSfcStepsBySfcRef(new ObjectReference(lotMember.getMemberContext()));

				for (SfcStep sfcStep : statusConfig) {

					if (!((sfcStep.getStatus() + "").equalsIgnoreCase((SfcStepStatusEnum.COMPLETED + "")))) {

						MoveLocationPluginObject pluginObject = new MoveLocationPluginObject();

						SfcConfiguration sfcConfiguration = sfcStateService.readSfc(lotMember.getMemberContext());
						pluginObject.setBatch(sfcConfiguration.getSfc());

						ShopOrderConfiguration shopOrderConfiguration = shopOrderService
								.readShopOrder(new ObjectReference(sfcConfiguration.getShopOrderRef()));
						pluginObject.setOrder(shopOrderConfiguration.getShopOrder());

						ItemConfiguration itemConfiguration = itemConfigurationService
								.readItem(new ObjectReference(sfcConfiguration.getItemRef()));
						pluginObject.setMaterial(itemConfiguration.getItem());

						pluginObject.setOperation(new OperationBOHandle(sfcStep.getOperationRef()).getOperation());

						if ((sfcStep.getStatus() == SfcStepStatusEnum.IN_QUEUE)
								|| (sfcStep.getStatus() == SfcStepStatusEnum.IN_WORK)) {

							pluginObject.setQuantity(sfcStep.getQuantityInWork() + "");

						}
						pluginObject.setQtyInQueue(sfcStep.getQuantityInQueue() + "");
						materialList.add(pluginObject);
					}

				}

			}

		} catch (Exception e) {

			this.message = e.getMessage();
			setMessageBar(true, LSMessageType.ERROR);

		}

	}

	public String getProcessLotRef(String processLot) {

		return null;
	}

	private void initService() {

		this.processLotService = (ProcessLotServiceInterface) Services.getService("com.sap.me.production",
				"ProcessLotService");

		ServiceReference sfcStateServiceRef = new ServiceReference("com.sap.me.production", "SfcStateService");
		this.sfcStateService = RunAsServiceLocator.getService(sfcStateServiceRef, SfcStateServiceInterface.class,
				CommonMethods.getUserId(), CommonMethods.getSite(), null);

		ServiceReference shopOrderServiceRef = new ServiceReference("com.sap.me.demand", "ShopOrderService");
		this.shopOrderService = RunAsServiceLocator.getService(shopOrderServiceRef, ShopOrderServiceInterface.class,
				CommonMethods.getUserId(), CommonMethods.getSite(), null);

		ServiceReference itemConfigServiceRef = new ServiceReference("com.sap.me.productdefinition",
				"ItemConfigurationService");

		this.itemConfigurationService = RunAsServiceLocator.getService(itemConfigServiceRef,
				ItemConfigurationServiceInterface.class, CommonMethods.getUserId(), CommonMethods.getSite(), null);

		ServiceReference bomConfigServiceRef = new ServiceReference("com.sap.me.productdefinition",
				"BOMConfigurationService");
		this.bOMConfigurationService = RunAsServiceLocator.getService(bomConfigServiceRef,
				BOMConfigurationServiceInterface.class, CommonMethods.getUserId(), CommonMethods.getSite(), null);

	}

}
