package com.returnsoft.collection.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.UploadedFile;

import com.returnsoft.collection.entity.Collection;
import com.returnsoft.collection.entity.Commerce;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.entity.User;
import com.returnsoft.collection.enumeration.CollectionResponseEnum;
import com.returnsoft.collection.enumeration.SaleStateEnum;
import com.returnsoft.collection.enumeration.UserTypeEnum;
import com.returnsoft.collection.exception.DataCollectionAllowMaximumByDayException;
import com.returnsoft.collection.exception.DataCollectionAllowMaximumByMonthException;
import com.returnsoft.collection.exception.DataCollectionChargeAmountException;
import com.returnsoft.collection.exception.DataCollectionCreateException;
import com.returnsoft.collection.exception.DataCollectionMaximumByDayException;
import com.returnsoft.collection.exception.DataCollectionMaximumByMonthException;
import com.returnsoft.collection.exception.DataCollectionResponseMessageNotFoundException;
import com.returnsoft.collection.exception.DataColumnDateException;
import com.returnsoft.collection.exception.DataColumnDecimalException;
import com.returnsoft.collection.exception.DataColumnLengthException;
import com.returnsoft.collection.exception.DataColumnNullException;
import com.returnsoft.collection.exception.DataColumnNumberException;
import com.returnsoft.collection.exception.DataCommerceCodeException;
import com.returnsoft.collection.exception.DataSaleNotFoundException;
import com.returnsoft.collection.exception.DataSaleStateNoActiveException;
import com.returnsoft.collection.exception.FileColumnsTotalException;
import com.returnsoft.collection.exception.FileExtensionException;
import com.returnsoft.collection.exception.FileNotFoundException;
import com.returnsoft.collection.exception.FileRowsZeroException;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.exception.UserLoggedNotFoundException;
import com.returnsoft.collection.exception.UserPermissionNotFoundException;
import com.returnsoft.collection.service.CollectionService;
import com.returnsoft.collection.service.CommerceService;
import com.returnsoft.collection.service.SaleService;
import com.returnsoft.collection.util.FacesUtil;

@ManagedBean
@ViewScoped
public class LoadCollectionsController implements Serializable {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 5987333554254645202L;

	private UploadedFile file;

	private List<Commerce> commerces;
	private Integer FILE_ROWS = 16;

	private List<Exception> errors;
	private Map<String, String> headers;
	private List<Map<String, String>> dataList;

	//private final String[] responseMessages = { "DENEGADO", "APROBADO" };
	
	//private final String[] saleStates = { "ACTIVO", "BAJA" };

	@EJB
	private CollectionService collectionService;
	
	@EJB
	private SaleService saleService;
	
	@EJB
	private CommerceService commerceService;
	
	private FacesUtil facesUtil;
	
	

	public LoadCollectionsController() {
		System.out.println("Ingreso al constructor");
		facesUtil = new FacesUtil();
	}

	//@PostConstruct
	public String initialize() {
		try {
			
			SessionBean sessionBean = (SessionBean) FacesContext
					.getCurrentInstance().getExternalContext().getSessionMap()
					.get("sessionBean");
			
			if (sessionBean!=null && sessionBean.getUser()!=null && sessionBean.getUser().getId()>0) {
				
				if (!sessionBean.getUser().getUserType().equals(UserTypeEnum.ADMIN)
						&& !sessionBean.getUser().getUserType().equals(UserTypeEnum.AGENT)) {
					throw new UserPermissionNotFoundException();
				}
					
				Short bankId = (Short) sessionBean.getBank().getId();
				
				commerces = commerceService.findByBank(bankId);
				
				return null;
				
			} else{
				throw new UserLoggedNotFoundException();
			}
			

		} catch (UserLoggedNotFoundException e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
					e.getMessage());
			return "login.xhtml?faces-redirect=true";
		} catch (UserPermissionNotFoundException e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
					e.getMessage());
			return "login.xhtml?faces-redirect=true";		
		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
					e.getMessage());
			return null;
		}
	}

	public void getData() {

		System.out.println("ingreso a getData");

		BufferedReader br = null;
		try {
			br = new BufferedReader(
					new InputStreamReader(file.getInputstream()));
		} catch (IOException e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
					e.getMessage());
		} catch (Exception e1) {
			e1.printStackTrace();
			facesUtil.sendErrorMessage(e1.getClass().getSimpleName(),
					e1.getMessage());
		}

		String strLine = null;
		Integer lineNumber = 0;

		errors = new ArrayList<Exception>();
		headers = new HashMap<String, String>();
		dataList = new ArrayList<Map<String, String>>();

		try {
			while ((strLine = br.readLine()) != null) {

				if (lineNumber == 0) {
					// SE LEE CABECERA
					String[] values = strLine.split("\\|");
					if (values.length != FILE_ROWS) {
						//new DataColumnsTotalException(lineNumber, values.length, FILE_ROWS);
						errors.add(new FileColumnsTotalException(lineNumber, values.length, FILE_ROWS));
					} else {

						headers.put("code", values[0]);
						headers.put("affiliationDate", values[1]);
						headers.put("maximumAmount", values[2]);
						headers.put("chargeAmount", values[3]);
						headers.put("creditCardUpdated", values[4]);
						headers.put("estimatedDate", values[5]);
						headers.put("authorizationDate", values[6]);
						headers.put("depositDate", values[7]);
						headers.put("responseCode", values[8]);
						headers.put("authorizationCode", values[9]);
						headers.put("responseMessage", values[10]);
						headers.put("action", values[11]);
						headers.put("transactionState", values[12]);
						headers.put("loteNumber", values[13]);
						headers.put("channel", values[14]);
						headers.put("disaffiliationDate", values[15]);

					}
				} else {

					String[] values = strLine.split("\\|");
					if (values.length != FILE_ROWS) {
						//new DataColumnsTotalException(lineNumber, values.length, FILE_ROWS);
						errors.add(new FileColumnsTotalException(lineNumber, values.length, FILE_ROWS));
					} else {

						Map<String, String> data = new HashMap<String, String>();

						data.put("lineNumber", lineNumber.toString());
						data.put("code", values[0]);
						data.put("affiliationDate", values[1]);
						data.put("maximumAmount", values[2]);
						data.put("chargeAmount", values[3]);
						data.put("creditCardUpdated", values[4]);
						data.put("estimatedDate", values[5]);
						data.put("authorizationDate", values[6]);
						data.put("depositDate", values[7]);
						data.put("responseCode", values[8]);
						data.put("authorizationCode", values[9]);
						data.put("responseMessage", values[10]);
						data.put("action", values[11]);
						data.put("transactionState", values[12]);
						data.put("loteNumber", values[13]);
						data.put("channel", values[14]);
						data.put("disaffiliationDate", values[15]);

						dataList.add(data);
					}
				}

				lineNumber++;

			}


			System.out.println("dataList:" + dataList.size());
			System.out.println("errors:" + errors.size());
			System.out.println("headers:" + headers.size());

		} catch (IOException e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(),e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(),e.getMessage());
		}

	}
	
	/*public void validateDuplicates(){
		
		System.out.println("Validando duplicados");
		
		for (int i=0; i< dataList.size();i++ ) {
			Map<String, String> data1 = dataList.get(i);
			for (int j=i+1; j< dataList.size();j++ ) {
				Map<String, String> data2 = dataList.get(j);
				if (data1.get("authorizationDate").equals(data2.get("authorizationDate")) 
						&& data1.get("responseMessage").equals(data2.get("responseMessage"))
						&& data1.get("code").equals(data2.get("code"))) {
					errors.add(new CollectionDuplicateException(i,j, data1.get("code"), data1.get("responseMessage"),data1.get("authorizationDate")));
				}
			}
		}
		
		System.out.println("errors:" + errors.size());
		
	}*/


	public void validateDataNull() {

		System.out.println("ingreso a validateDataNull");
		
		Integer lineNumber = 1;

				for (Map<String, String> data : dataList) {

					// SE VALIDA NOT NULL

					if (data.get("code").length() == 0) {
						errors.add(new DataColumnNullException(lineNumber,headers.get("code")));
					}

					if (data.get("affiliationDate").length() == 0) {
						errors.add(new DataColumnNullException(lineNumber,headers.get("affiliationDate")));
					}

					if (data.get("maximumAmount").length() == 0) {
						errors.add(new DataColumnNullException(lineNumber,headers.get("maximumAmount")));
					}

					if (data.get("chargeAmount").length() == 0) {
						errors.add(new DataColumnNullException(lineNumber,headers.get("chargeAmount")));
					}


					if (data.get("estimatedDate").length() == 0) {
						errors.add(new DataColumnNullException(lineNumber,headers.get("estimatedDate")));
					
					}

					if (data.get("authorizationDate").length() == 0) {
						errors.add(new DataColumnNullException(lineNumber,headers.get("authorizationDate")));
						
					}

					if (data.get("depositDate").length() == 0) {
						errors.add(new DataColumnNullException(lineNumber,headers.get("depositDate")));
						
					}

					if (data.get("responseCode").length() == 0) {
						errors.add(new DataColumnNullException(lineNumber,headers.get("responseCode")));
						
					}

					if (data.get("responseMessage").length() == 0) {
						errors.add(new DataColumnNullException(lineNumber,headers.get("responseMessage")));
						
					}

					if (data.get("action").length() == 0) {
						errors.add(new DataColumnNullException(lineNumber,headers.get("action")));
						
					}

					if (data.get("transactionState").length() == 0) {
						errors.add(new DataColumnNullException(lineNumber,headers.get("transactionState")));
						
					}

					if (data.get("channel").length() == 0) {
						errors.add(new DataColumnNullException(lineNumber,headers.get("channel")));
					
					}

				
					
					lineNumber++;

				}

				

		System.out.println("dataList:" + dataList.size());
		System.out.println("errors:" + errors.size());

	}

	public void validateDataSize() {
		
		Integer lineNumber = 1;

				for (Map<String, String> data : dataList) {
					
					if (data.get("code").length() > 20) {
						errors.add(new DataColumnLengthException(lineNumber,headers.get("code"),data.get("code").length(),20));
						
					}
					

					if (data.get("responseCode").length() > 2) {
						errors.add(new DataColumnLengthException(lineNumber,headers.get("responseCode"),data.get("responseCode").length(),2));
						
					}

					if (data.get("authorizationCode").length() > 6) {
						errors.add(new DataColumnLengthException(lineNumber,headers.get("authorizationCode"),data.get("authorizationCode").length(),6));
						
					}

					if (data.get("responseMessage").length() > 10) {
						errors.add(new DataColumnLengthException(lineNumber,headers.get("responseMessage"),data.get("responseMessage").length(),10));
						
					}

					if (data.get("action").length() > 20) {
						errors.add(new DataColumnLengthException(lineNumber,headers.get("action"),data.get("action").length(),20));
						
					}

					if (data.get("transactionState").length() > 20) {
						errors.add(new DataColumnLengthException(lineNumber,headers.get("transactionState"),data.get("transactionState").length(),20));
						
					}

					if (data.get("loteNumber").length() > 10) {
						errors.add(new DataColumnLengthException(lineNumber,headers.get("loteNumber"),data.get("loteNumber").length(),10));
						
					}

					if (data.get("channel").length() > 20) {
						errors.add(new DataColumnLengthException(lineNumber,headers.get("channel"),data.get("channel").length(),20));
						
					}
					
					lineNumber++;

					
				}

			

			
	}

	@SuppressWarnings("unused")
	public void validateDataType() {

		System.out.println("ingreso a validateDataType");

		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
		Integer lineNumber = 1;

				for (Map<String, String> data : dataList) {

					try {
						if (data.get("affiliationDate").length() > 0) {
							Date affiliationDate = sdf1.parse(data
									.get("affiliationDate"));
						}
					} catch (ParseException e1) {
						errors.add(new DataColumnDateException(lineNumber, headers.get("affiliationDate"),"dd/mm/yyyy"));
						
					}

					try {
						BigDecimal maximumAmount = new BigDecimal(
								data.get("maximumAmount"));

						if (maximumAmount.scale() > 2) {
							errors.add(new DataColumnDecimalException(lineNumber, headers.get("maximumAmount"),2));
							
						}
					} catch (NumberFormatException e) {
						errors.add(new DataColumnNumberException(lineNumber, headers.get("maximumAmount")));
					}

					try {
						BigDecimal chargeAmount = new BigDecimal(
								data.get("chargeAmount"));

						if (chargeAmount.scale() > 2) {
							errors.add(new DataColumnDecimalException(lineNumber, headers.get("chargeAmount"),2));
							
						}
					} catch (NumberFormatException e) {
						errors.add(new DataColumnNumberException(lineNumber, headers.get("chargeAmount")));
					}

					try {
						if (data.get("creditCardUpdated")!=null && data.get("creditCardUpdated").length()>0) {
							Date creditCardUpdated = sdf1.parse(data
									.get("creditCardUpdated"));	
						}
					} catch (ParseException e1) {
						errors.add(new DataColumnDateException(lineNumber, headers.get("creditCardUpdated"),"dd/mm/yyyy"));
						
					}

					try {
						Date estimatedDate = sdf1.parse(data
								.get("estimatedDate"));
					} catch (ParseException e1) {
						errors.add(new DataColumnDateException(lineNumber, headers.get("estimatedDate"),"dd/mm/yyyy"));
						
					}

					try {
						Date authorizationDate = sdf1.parse(data
								.get("authorizationDate"));
					} catch (ParseException e1) {
						errors.add(new DataColumnDateException(lineNumber, headers.get("authorizationDate"),"dd/mm/yyyy"));

					}

					try {
						Date depositDate = sdf1.parse(data.get("depositDate"));
					} catch (ParseException e1) {
						errors.add(new DataColumnDateException(lineNumber, headers.get("depositDate"),"dd/mm/yyyy"));
						
					}

					try {
						/*System.out.println("codigo de respuesta:"
								+ data.get("responseCode"));*/
						Long responseCode = Long.parseLong(data
								.get("responseCode"));
					} catch (NumberFormatException e) {
						errors.add(new DataColumnNumberException(lineNumber, headers.get("responseCode")));
					}

					try {
						/*System.out.println("codigo de autorizacion:"
								+ data.get("authorizationCode") + ":");*/
						if (data.get("authorizationCode").length() > 0) {
							Long authorizationCode = Long.parseLong(data
									.get("authorizationCode"));
						}

					} catch (NumberFormatException e) {
						errors.add(new DataColumnNumberException(lineNumber, headers.get("authorizationCode")));
					}

					try {
						if (data.get("loteNumber").length() > 0) {
							Long loteNumber = Long.parseLong(data
									.get("loteNumber"));
						}

					} catch (NumberFormatException e) {
						errors.add(new DataColumnNumberException(lineNumber, headers.get("loteNumber")));

					}

					try {
						if (data.get("disaffiliationDate").length() > 0) {
							Date disaffiliationDate = sdf1.parse(data
									.get("disaffiliationDate"));
						}
					} catch (ParseException e) {
						errors.add(new DataColumnDateException(lineNumber, headers.get("disaffiliationDate"),"dd/mm/yyyy"));
						
					}
					
					lineNumber++;

				}

			

		System.out.println("dataList:" + dataList.size());
		System.out.println("errors:" + errors.size());

	}

	public void validateData() {

		System.out.println("ingreso a validateData");

		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
		
		Integer lineNumber = 1;

		try {

				for (Map<String, String> data : dataList) {

						Sale sale = saleService.findByCode(data.get("code"));

						// VALIDA SI LA VENTA EXISTE
						if (sale != null && sale.getId() > 0) {

							// VALIDA SI LA VENTA ESTA DE BAJA
							if (!sale.getSaleState().getState().equals(SaleStateEnum.ACTIVE)) {
								errors.add(new DataSaleStateNoActiveException(lineNumber, headers.get("code"),data.get("code")));
							}

							// VALIDA COMMERCIAL CODE
							Commerce commercialCodeObject = null;
							for (Commerce commerce : commerces) {
								if (sale.getCommerce().getCode().equals(
										commerce.getCode())) {
									commercialCodeObject = commerce;
								}
							}
							if (commercialCodeObject == null) {
								errors.add(new DataCommerceCodeException(lineNumber, headers.get("code"),""));
							}
							
							

							// VALIDATE MESSAGE RESPONSE
							CollectionResponseEnum collectionResponseEnum = CollectionResponseEnum.findByName(data.get("responseMessage"));
							if (collectionResponseEnum==null) {
								errors.add(new DataCollectionResponseMessageNotFoundException(lineNumber, headers.get("responseMessage"),data.get("responseMessage")));
							}else{
								
								BigDecimal chargeAmount = new BigDecimal(data.get("chargeAmount"));
								
								//OBTIENE CANTIDAD DE COBRANZAS POR VENTA
								Date authorizationDate = sdf1.parse(data
										.get("authorizationDate"));
								
								//OBTIENE CANTIDAD DE COBRANZAS DENEGADAS POR DÍA
								Integer collectionsDenyByDayLength=0;
								List<Collection> collectionsDenyByDay = collectionService
										.findByResponseAndAuthorizationDay(CollectionResponseEnum.DENY,authorizationDate,sale.getCode());
								if (collectionsDenyByDay!=null) {
									collectionsDenyByDayLength=collectionsDenyByDay.size();
								}
								//OBTIENE CANTIDAD DE COBRANZAS APROBADAS POR DÍA
								Integer collectionsAllowByDayLength=0;
								List<Collection> collectionsAllowByDay = collectionService
										.findByResponseAndAuthorizationDay(CollectionResponseEnum.ALLOW,authorizationDate,sale.getCode());
								if (collectionsAllowByDay!=null) {
									collectionsAllowByDayLength=collectionsAllowByDay.size();
								}
								//OBTIENE CANTIDAD DE COBRANZAS DENEGADAS POR MES
								Integer collectionsDenyByMonthLength=0;
								List<Collection> collectionsDenyByMonth = collectionService
										.findByResponseAndAuthorizationMonth(CollectionResponseEnum.DENY,authorizationDate,sale.getCode());
								if (collectionsDenyByMonth!=null) {
									collectionsDenyByMonthLength=collectionsDenyByMonth.size();
								}
								//OBTIENE CANTIDAD DE COBRANZAS APROBADAS POR MES
								Integer collectionsAllowByMonthLength=0;
								List<Collection> collectionsAllowByMonth = collectionService
										.findByResponseAndAuthorizationMonth(CollectionResponseEnum.ALLOW,authorizationDate,sale.getCode());
								if (collectionsAllowByMonth!=null) {
									collectionsAllowByMonthLength=collectionsAllowByMonth.size();
								}
								//OBTIENE TOTALES
								Integer collectionsByMonthLength=collectionsAllowByMonthLength+collectionsDenyByMonthLength;
								Integer collectionsByDayLength=collectionsAllowByDayLength+collectionsDenyByDayLength;
								
								//OBTIENE CANTIDAD DE COBRANZAS A REGISTRAR
								int totalCollectionsAllow=0;
								int totalCollectionsDeny=0;
								for (int i=0; i< dataList.size();i++ ) {
									Map<String, String> data1 = dataList.get(i);
										if (data.get("authorizationDate").equals(data1.get("authorizationDate")) 
												&& data.get("code").equals(data1.get("code"))) {
											if (data1.get("responseMessage").equals(CollectionResponseEnum.ALLOW.getName())) {
												totalCollectionsAllow++;
											}else if (data1.get("responseMessage").equals(CollectionResponseEnum.DENY.getName())) {
												totalCollectionsDeny++;
											}
										}
								}
								//OBTIENE TOTALES
								int totalCollections=totalCollectionsAllow+totalCollectionsDeny;
								
								//si la cantidad de cobranzas por día + la cantidad de cobranzas a insertar es menor o igual a 2
								//si la cantidad de cobranzas por mes + cantidad de cobranzas a insertar es menor o igual a 4
								
								if (collectionsByDayLength+totalCollections>5){//CAMBIA TEMPORALMENTE DE 2 A 5
									errors.add(new DataCollectionMaximumByDayException(lineNumber, headers.get("code"),headers.get("authorizationDate"),data.get("code"),data.get("authorizationDate"),collectionsByDayLength,totalCollections));
								}else if(collectionsByMonthLength+totalCollections>5){
									errors.add(new DataCollectionMaximumByMonthException(lineNumber, headers.get("code"),headers.get("authorizationDate"),data.get("code"),data.get("authorizationDate"),collectionsByMonthLength,totalCollections));
								}else{
								
									switch (collectionResponseEnum) {
									
									case ALLOW:
										if (collectionsAllowByDayLength+totalCollectionsAllow>5) {//CAMBIA TEMPORALMENTE DE 2 A 5
											errors.add(new DataCollectionAllowMaximumByDayException(lineNumber, headers.get("code"),headers.get("authorizationDate"),data.get("code"),data.get("authorizationDate"),collectionsAllowByDayLength,totalCollectionsAllow));
										}else if(collectionsAllowByMonthLength+totalCollectionsAllow>5){//CAMBIA TEMPORALMENTE DE 2 A 5
											errors.add(new DataCollectionAllowMaximumByMonthException(lineNumber, headers.get("code"),headers.get("authorizationDate"),data.get("code"),data.get("authorizationDate"),collectionsAllowByMonthLength,totalCollectionsAllow));
										}
										
										// MONTO A CARGAR DEBE SER IGUAL A LA PRIMA DE LA VENTA.
										if (sale.getInsurancePremium().compareTo(chargeAmount)!=0) {
											errors.add(new DataCollectionChargeAmountException(lineNumber,headers.get("chargeAmount"),sale.getInsurancePremium(),chargeAmount));
										}
										break;
									case DENY:
										
										if (collectionsDenyByDayLength+totalCollectionsDeny>5) {//CAMBIA TEMPORALMENTE DE 2 A 5
											errors.add(new DataCollectionAllowMaximumByMonthException(lineNumber, headers.get("code"),headers.get("authorizationDate"),data.get("code"),data.get("authorizationDate"),collectionsDenyByDayLength,totalCollectionsDeny));
										}else if(collectionsDenyByMonthLength+totalCollectionsDeny>5){
											errors.add(new DataCollectionAllowMaximumByMonthException(lineNumber, headers.get("code"),headers.get("authorizationDate"),data.get("code"),data.get("authorizationDate"),collectionsDenyByMonthLength,totalCollectionsDeny));
										}
										
										//MONTO A CARGAR DEBE SER CERO.
										if (chargeAmount.intValue() != 0) {
											errors.add(new DataCollectionChargeAmountException(lineNumber,headers.get("chargeAmount"),headers.get("responseMessage")));
										}
										break;	

									default:
										break;
									}
								}
								
							}


						} else {
							errors.add(new DataSaleNotFoundException(lineNumber,headers.get("code"),data.get("code")));
						}
						
						lineNumber++;

				}
				
				
		} catch (Exception e) {
			e.printStackTrace();
			errors.add( new ServiceException());
			/*facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
					e.getMessage());*/
		}	

		System.out.println("dataList:" + dataList.size());
		System.out.println("errors:" + errors.size());

	}

	public void createCollections() {

		System.out.println("ingreso a createCollections");

		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");

		SessionBean sessionBean = (SessionBean) FacesContext
				.getCurrentInstance().getExternalContext()
				.getSessionMap().get("sessionBean");
		
		User user = new User();
		user.setId(sessionBean.getUser().getId());
		
		Integer lineNumber =1;

				for (Map<String, String> data : dataList) {

					try {
						// / SE CREA LA VENTA
						Collection collection = new Collection();

						collection.setSaleCode(data.get("code"));
						collection.setAffiliationDate(data.get(
								"affiliationDate").length() > 0 ? sdf1
								.parse(data.get("affiliationDate")) : null);
						collection.setMaximumAmount(new BigDecimal(data
								.get("maximumAmount")));
						collection.setChargeAmount(new BigDecimal(data
								.get("chargeAmount")));
						collection.setCreditCardUpdated(data
								.get("creditCardUpdated").length()>0 ? sdf1.parse(data
								.get("creditCardUpdated")):null);
						collection.setEstimatedDate(sdf1.parse(data
								.get("estimatedDate")));
						collection.setAuthorizationDate(sdf1.parse(data
								.get("authorizationDate")));
						collection.setDepositDate(sdf1.parse(data
								.get("depositDate")));
						collection.setResponseCode(Short.parseShort((data.get("responseCode"))));
						collection
								.setAuthorizationCode(data.get(
										"authorizationCode").length() > 0 ? Integer
										.parseInt(data.get("authorizationCode"))
										: null);
						collection.setResponseMessage(CollectionResponseEnum.findByName(data
								.get("responseMessage")));
						collection.setAction(data.get("action"));
						collection.setTransactionState(data
								.get("transactionState"));
						collection.setLoteNumber(data.get("loteNumber")
								.length() > 0 ? Integer.parseInt(data
								.get("loteNumber")) : null);
						collection.setChannel(data.get("channel"));
						collection.setDisaffiliationDate(data.get(
								"disaffiliationDate").length() > 0 ? sdf1
								.parse(data.get("disaffiliationDate")) : null);

						collection.setCreatedBy(user);
						collection.setCreatedAt(new Date());
						collectionService.add(collection);

					} catch (Exception e) {
						e.printStackTrace();
						errors.add(new DataCollectionCreateException(lineNumber,e.getMessage()));
					}
					
					lineNumber++;

				}
				
				if (errors != null && errors.size() > 0) {
					for (Exception e : errors) {
						facesUtil.sendErrorMessage(e.getClass().getSimpleName(),e.getMessage());
					}
				}else{
					facesUtil.sendConfirmMessage("Se crearon correctamente las cobranzas.","");
				}

				
	}
	
	public String load() {
		try {

			if (file != null && file.getFileName().length() > 0) {
				if (file.getFileName().endsWith(".csv")
						|| file.getFileName().endsWith(".CSV")) {
					System.out.println("file NO es nulo:" + file.getFileName());

					getData();
					if (dataList == null && dataList.size() == 0) {
						Exception e = new FileRowsZeroException();
						facesUtil.sendErrorMessage(e.getClass().getSimpleName(),e.getMessage());
					} else if (errors != null && errors.size() > 0) {
						for (Exception e : errors) {
							facesUtil.sendErrorMessage(e.getClass().getSimpleName(),e.getMessage());
						}
					}else{
						
						/*validateDuplicates();
						if (errors != null && errors.size() > 0) {
							for (Exception e : errors) {
								facesUtil.sendErrorMessage(e.getClass().getSimpleName(),e.getMessage());
							}
						}else{*/
							validateDataNull();
							if (errors != null && errors.size() > 0) {
								for (Exception e : errors) {
									facesUtil.sendErrorMessage(e.getClass().getSimpleName(),e.getMessage());
								}	
							}else{
								validateDataSize();
								if (errors != null && errors.size() > 0) {
									for (Exception e : errors) {
										facesUtil.sendErrorMessage(e.getClass().getSimpleName(),e.getMessage());
									}	
								}else{
									validateDataType();
									if (errors != null && errors.size() > 0) {
										for (Exception e : errors) {
											facesUtil.sendErrorMessage(e.getClass().getSimpleName(),e.getMessage());
										}	
									}else{
										validateData();
										if (errors != null && errors.size() > 0) {
											for (Exception e : errors) {
												facesUtil.sendErrorMessage(e.getClass().getSimpleName(),e.getMessage());
											}	
										}else{
											createCollections();
										}
									}
								}
								
							}
						//}
						
						
					}

				} else {
					throw new FileExtensionException();
				}

			} else {
				throw new FileNotFoundException();
			}

			

		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(),e.getMessage());

		}
		return null;

	}


	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

}
