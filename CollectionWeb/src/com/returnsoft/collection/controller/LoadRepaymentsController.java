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
import com.returnsoft.collection.entity.Repayment;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.entity.User;
import com.returnsoft.collection.enumeration.SaleStateEnum;
import com.returnsoft.collection.enumeration.UserTypeEnum;
import com.returnsoft.collection.exception.DataCollectionCreateException;
import com.returnsoft.collection.exception.DataCommerceCodeException;
import com.returnsoft.collection.exception.DataColumnDateException;
import com.returnsoft.collection.exception.DataColumnDecimalException;
import com.returnsoft.collection.exception.DataColumnLengthException;
import com.returnsoft.collection.exception.DataColumnNullException;
import com.returnsoft.collection.exception.DataColumnNumberException;
import com.returnsoft.collection.exception.DataSaleNotFoundException;
import com.returnsoft.collection.exception.DataSaleStateNoActiveException;
import com.returnsoft.collection.exception.FileColumnsTotalException;
import com.returnsoft.collection.exception.FileRowsZeroException;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.exception.FileExtensionException;
import com.returnsoft.collection.exception.FileNotFoundException;
import com.returnsoft.collection.exception.DataMaintenanceDuplicateException;
import com.returnsoft.collection.exception.DataRepaymentChargeAmountException;
import com.returnsoft.collection.exception.DataRepaymentDuplicateException;
import com.returnsoft.collection.exception.DataRepaymentInsurancePremiumException;
import com.returnsoft.collection.exception.UserLoggedNotFoundException;
import com.returnsoft.collection.exception.UserPermissionNotFoundException;
import com.returnsoft.collection.service.RepaymentService;
import com.returnsoft.collection.util.FacesUtil;

@ManagedBean
@ViewScoped
public class LoadRepaymentsController implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1028392915692233854L;

	private UploadedFile file;

	private List<Commerce> commerces;
	// private List<SaleState> states;
	private Integer FILE_ROWS = 5;

	private List<Exception> errors;
	private Map<String, String> headers;
	private List<Map<String, String>> dataList;
	
	//private final String[] saleStates = { "ACTIVO", "BAJA" };

	@EJB
	private RepaymentService repaymentService;
	
	private FacesUtil facesUtil;

	public LoadRepaymentsController() {
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
				
				commerces = repaymentService.findCommercesByBankId(bankId);
				
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
	
	public void validateDuplicates(){
		
		System.out.println("Validando duplicados");
		
		for (int i=0; i< dataList.size();i++ ) {
			Map<String, String> data1 = dataList.get(i);
			for (int j=i+1; j< dataList.size();j++ ) {
				Map<String, String> data2 = dataList.get(j);
				if (data1.get("code").equals(data2.get("code"))
					&& data1.get("returnedDate").equals(data2.get("returnedDate")) ) {
					
					errors.add(new DataRepaymentDuplicateException(i,j, headers.get("code"),headers.get("returnedDate"), data1.get("code"),data1.get("returnedDate")));
				}
			}
		}
		
		System.out.println("errors:" + errors.size());
		
	}

	public void getData() {

		System.out.println("ingreso a getData");

		BufferedReader br = null;
		try {
			br = new BufferedReader(
					new InputStreamReader(file.getInputstream()));
		} catch (IOException e1) {
			e1.printStackTrace();
			facesUtil.sendErrorMessage(e1.getClass().getSimpleName(),
					e1.getMessage());
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
						errors.add(new FileColumnsTotalException(lineNumber, values.length, FILE_ROWS));
					} else {

						headers.put("code", values[0]);
						headers.put("insurancePremiumNumber", values[1]);
						headers.put("chargeAmount", values[2]);
						headers.put("returnedDate", values[3]);
						headers.put("paymentDate", values[4]);

					}
				} else {

					String[] values = strLine.split("\\|");
					if (values.length != FILE_ROWS) {
						errors.add(new FileColumnsTotalException(lineNumber, values.length, FILE_ROWS));
					} else {

						Map<String, String> data = new HashMap<String, String>();

						//data.put("lineNumber", lineNumber.toString());
						data.put("code", values[0]);
						//data.put("receiptNumber", values[1]);
						data.put("insurancePremiumNumber", values[1]);
						data.put("chargeAmount", values[2]);
						data.put("returnedDate", values[3]);
						data.put("paymentDate", values[4]);

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

	public void validateDataNull() {

		System.out.println("ingreso a validateDataNull");
		
		Integer lineNumber = 1;

		
				for (Map<String, String> data : dataList) {

					// SE VALIDA NOT NULL

					if (data.get("code").length() == 0) {
						errors.add(new DataColumnNullException(lineNumber,headers.get("code")));
					}

					/*if (data.get("receiptNumber").length() == 0) {
						errors.add(new DataCellValueNullException(lineNumber,headers.get("receiptNumber")));
					}*/

					if (data.get("insurancePremiumNumber").length() == 0) {
						errors.add(new DataColumnNullException(lineNumber,headers.get("insurancePremiumNumber")));
					}

					if (data.get("chargeAmount").length() == 0) {
						errors.add(new DataColumnNullException(lineNumber,headers.get("chargeAmount")));
					}

					if (data.get("returnedDate").length() == 0) {
						errors.add(new DataColumnNullException(lineNumber,headers.get("returnedDate")));
					}

					if (data.get("paymentDate").length() == 0) {
						errors.add(new DataColumnNullException(lineNumber,headers.get("code")));
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

					/*if (data.get("receiptNumber").length() > 20) {
						errors.add(new DataCellValueLengthException(lineNumber,headers.get("receiptNumber"),data.get("receiptNumber").length(),20));
					}*/

					if (data.get("insurancePremiumNumber").length() > 6) {
						errors.add(new DataColumnLengthException(lineNumber,headers.get("insurancePremiumNumber"),data.get("insurancePremiumNumber").length(),6));
					}
					
					lineNumber++;

				}

				
	}

	@SuppressWarnings("unused")
	public void validateDataType() {

		System.out.println("ingreso a validateDataType");
		
		Integer lineNumber = 1;

		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
		// SimpleDateFormat sdf2 = new SimpleDateFormat("MM/yyyy");

		
				for (Map<String, String> data : dataList) {

					try {
						if (data.get("paymentDate").length() > 0) {
							Date affiliationDate = sdf1.parse(data
									.get("paymentDate"));
						}
					} catch (ParseException e1) {
						errors.add(new DataColumnDateException(lineNumber, headers.get("paymentDate"),"dd/mm/yyyy"));
					}

					try {
						if (data.get("returnedDate").length() > 0) {
							Date affiliationDate = sdf1.parse(data
									.get("returnedDate"));
						}
					} catch (ParseException e1) {
						errors.add(new DataColumnDateException(lineNumber, headers.get("returnedDate"),"dd/mm/yyyy"));
					}

					try {
						BigDecimal returnedAmount = new BigDecimal(
								data.get("chargeAmount"));

						if (returnedAmount.scale() > 2) {
							errors.add(new DataColumnDecimalException(lineNumber, headers.get("chargeAmount"),2));
						}
					} catch (NumberFormatException e) {
						errors.add(new DataColumnNumberException(lineNumber, headers.get("chargeAmount")));
					}

					try {
						/*
						 * System.out.println("codigo de respuesta:" +
						 * data.get("responseCode"));
						 */
						Integer insurancePremiumNumber = Integer.parseInt(data
								.get("insurancePremiumNumber"));
					} catch (NumberFormatException e) {
						errors.add(new DataColumnNumberException(lineNumber, headers.get("insurancePremiumNumber")));
					}
					
					lineNumber++;

				}

				

		System.out.println("dataList:" + dataList.size());
		System.out.println("errors:" + errors.size());

	}
	
	
	public void validateData() {

		System.out.println("ingreso a validateData");

		try {
		Integer lineNumber = 1;
		
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");

		//SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");

		
				for (Map<String, String> data : dataList) {


						Sale sale = repaymentService.findSaleByCode(data
								.get("code"));

						// VALIDA SI LA VENTA EXISTE
						if (sale != null && sale.getId() > 0) {

							// VALIDA SI LA VENTA ESTA DE BAJA
							// SE RETIRO ESTA VALIDACION
							/*if (!sale.getSaleState().getState().equals(SaleStateEnum.ACTIVE)) {
								errors.add(new DataSaleStateNoActiveException(lineNumber, headers.get("code"),data.get("code")));
							}*/

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
							
							//VALIDA SI EL EXTORNO YA EXISTE
							Date returnedDate = sdf1.parse(data.get("returnedDate"));
							Repayment repayment = repaymentService.findBySaleIdAndReturnedDate(sale.getId(), returnedDate);
							if (repayment!=null) {
								errors.add(new DataRepaymentDuplicateException(lineNumber, headers.get("code"),headers.get("returnedDate"), data.get("code"),data.get("returnedDate")));
							}
							
							
							//VALIDA QUE EL IMPORTE CARGO SEA IGUAL A LA PRIMA DE LA VENTA
							BigDecimal chargeAmount = new BigDecimal(data.get("chargeAmount"));
							if (!chargeAmount.equals(sale.getInsurancePremium())) {
								errors.add(new DataRepaymentChargeAmountException(lineNumber, headers.get("chargeAmount"),data.get("chargeAmount"),sale.getInsurancePremium()));
							}
							
							//VALIDA QUE el número de primas sea menor o igual que "la cantidad de cobranzas aprobadas de la venta" - "cantidad de extornos de la venta").
							Integer collectionsAllowCount=0;
							List<Collection> collections = repaymentService.findCollectionsAllowBySaleId(sale.getId());
							if (collections!=null) {
								collectionsAllowCount=collections.size();
							}
							Integer repaymentsCount=0;
							List<Repayment> repayments = repaymentService.findRepaymentsBySaleId(sale.getId());
							if (repayments!=null) {
								repaymentsCount=repayments.size();
							}
							
							Integer collectionsTotal=collectionsAllowCount-repaymentsCount;
							
							Integer insurancePremiumNumber = Integer.parseInt(data.get("insurancePremiumNumber"));
							
							if (insurancePremiumNumber>collectionsTotal) {
								errors.add(new DataRepaymentInsurancePremiumException(lineNumber, headers.get("insurancePremiumNumber"),data.get("insurancePremiumNumber"),collectionsTotal));
							}
							
							
							
							
							
							//VALIDATE IF RECEIPT EXIST
							
							/*Collection collection = repaymentService.findCollectionByReceiptNumber(data
									.get("receiptNumber"));
							
							if (collection == null || collection.getId() == 0) {
								
								errors.add("Error en linea "
										+ data.get("lineNumber") + ": La columna "
										+ headers.get("receiptNumber")
										+ " No se encontro numero de recibo");
							}*/


						} else {
							errors.add(new DataSaleNotFoundException(lineNumber,headers.get("code"),data.get("code")));
						}
						
						
						
						
					
					
					
					lineNumber++;

				}
				} catch (Exception e) {
					e.printStackTrace();
					errors.add( new ServiceException());
				}
				
		System.out.println("dataList:" + dataList.size());
		System.out.println("errors:" + errors.size());

	}
	
	
	public void createRepayments() {

		System.out.println("ingreso a createRepayments");
		
		Integer lineNumber = 1;

		// sales = new ArrayList<Sale>();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
		// SimpleDateFormat sdf2 = new SimpleDateFormat("MM/yyyy");
		
		SessionBean sessionBean = (SessionBean) FacesContext
				.getCurrentInstance().getExternalContext()
				.getSessionMap().get("sessionBean");
		
		User user = new User();
		user.setId(sessionBean.getUser().getId());


		
				for (Map<String, String> data : dataList) {

					try {
						// / SE CREA LA VENTA
						Repayment repayment = new Repayment();
						
						repayment.setCode(data.get("code"));
						//repayment.setReceiptNumber(data.get("receiptNumber"));
						repayment.setInsurancePremiumNumber(Integer
								.parseInt(data.get("insurancePremiumNumber")));
						repayment.setReturnedAmount(new BigDecimal(data
								.get("returnedAmount")));
						
						repayment.setReturnedDate(sdf1.parse(data.get("returnedDate")));
						
						repayment.setPaymentDate(data.get(
								"paymentDate").length() > 0 ? sdf1
								.parse(data.get("paymentDate")) : null);
						
						
						repayment.setCreatedBy(user);
						repayment.setCreatedAt(new Date());
						repaymentService.add(repayment);


					} catch (Exception e) {
						e.printStackTrace();
						errors.add(new DataCollectionCreateException(lineNumber,e.getMessage()));

					}
					
					lineNumber++;

				}

				
				/////////////////
				
				if (errors != null && errors.size() > 0) {
					for (Exception e : errors) {
						facesUtil.sendErrorMessage(e.getClass().getSimpleName(),e.getMessage());
					}
				}else{
					facesUtil.sendConfirmMessage("Se crearon correctamente los extornos.","");
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
						validateDuplicates();
						if (errors != null && errors.size() > 0) {
							for (Exception e : errors) {
								facesUtil.sendErrorMessage(e.getClass().getSimpleName(),e.getMessage());
							}
						}else{
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
											createRepayments();
										}
									}
								}
								
							}	
						}
						
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

}
