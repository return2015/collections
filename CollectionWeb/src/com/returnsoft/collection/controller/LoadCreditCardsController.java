package com.returnsoft.collection.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
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

import com.returnsoft.collection.entity.Commerce;
import com.returnsoft.collection.entity.CreditCard;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.entity.User;
import com.returnsoft.collection.enumeration.CreditCardValidationEnum;
import com.returnsoft.collection.enumeration.SaleStateEnum;
import com.returnsoft.collection.enumeration.UserTypeEnum;
import com.returnsoft.collection.exception.DataCollectionCreateException;
import com.returnsoft.collection.exception.DataColumnDateException;
import com.returnsoft.collection.exception.DataColumnLengthException;
import com.returnsoft.collection.exception.DataColumnNullException;
import com.returnsoft.collection.exception.DataColumnNumberException;
import com.returnsoft.collection.exception.DataCommerceCodeException;
import com.returnsoft.collection.exception.DataCreditCardDuplicateException;
import com.returnsoft.collection.exception.DataCreditCardValidationNotFoundException;
import com.returnsoft.collection.exception.DataSaleNotFoundException;
import com.returnsoft.collection.exception.DataSaleStateNoActiveException;
import com.returnsoft.collection.exception.FileColumnsTotalException;
import com.returnsoft.collection.exception.FileExtensionException;
import com.returnsoft.collection.exception.FileNotFoundException;
import com.returnsoft.collection.exception.FileRowsZeroException;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.exception.UserLoggedNotFoundException;
import com.returnsoft.collection.exception.UserPermissionNotFoundException;
import com.returnsoft.collection.service.CommerceService;
import com.returnsoft.collection.service.CreditCardService;
import com.returnsoft.collection.service.SaleService;
import com.returnsoft.collection.util.FacesUtil;
import com.returnsoft.collection.util.SessionBean;

@ManagedBean
@ViewScoped
public class LoadCreditCardsController implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1559544306814870412L;

	private UploadedFile file;

	private List<Commerce> commerces;
	// private List<SaleState> states;
	private Integer FILE_ROWS = (6);

	private List<Exception> errors;
	private Map<String, String> headers;
	private List<Map<String, String>> dataList;

	//private final String[] saleStates = { "ACTIVO", "BAJA" };

	/*private final String[] validations = { "NO ENCONTRADO", "MISMA TARJETA",
			"ACTUALIZADO" };*/

	@EJB
	private CreditCardService creditCardUpdateService;
	
	@EJB
	private CommerceService commerceService;
	
	@EJB
	private SaleService saleService;
	
	private FacesUtil facesUtil;

	public LoadCreditCardsController() {
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
						headers.put("creditCardNumber", values[1]);
						headers.put("creditCardExpirationDate", values[2]);
						headers.put("creditCardState", values[3]);
						headers.put("creditCardDaysOfDefault", values[4]);
						headers.put("validation", values[5]);

					}
				} else {

					String[] values = strLine.split("\\|");
					if (values.length != FILE_ROWS) {
						errors.add(new FileColumnsTotalException(lineNumber, values.length, FILE_ROWS));
					}else {

						Map<String, String> data = new HashMap<String, String>();

						data.put("lineNumber", lineNumber.toString());
						data.put("code", values[0]);
						data.put("creditCardNumber", values[1]);
						data.put("creditCardExpirationDate", values[2]);
						data.put("creditCardState", values[3]);
						data.put("creditCardDaysOfDefault", values[4]);
						data.put("validation", values[5]);

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
	
	
public void validateDuplicates(){
		
		
	System.out.println("Validando duplicados");
		
		for (int i=0; i< dataList.size();i++ ) {
			Map<String, String> data1 = dataList.get(i);
			for (int j=i+1; j< dataList.size();j++ ) {
				Map<String, String> data2 = dataList.get(j);
				if (data1.get("code").equals(data2.get("code"))) {
					
					errors.add(new DataCreditCardDuplicateException(i,j, headers.get("code"), data1.get("code")));
				}
			}
		}
		
		System.out.println("errors:" + errors.size());
		
	}


	public void validateDataNull() {

		System.out.println("ingreso a validateDataNull");
		
		Integer lineNumber = 1;

			for (Map<String, String> data : dataList) {

					// SE VALIDA NOT NULL

					if (data.get("code").length() == 0) {
						errors.add(new DataColumnNullException(lineNumber,headers.get("code")));
					}

					if (data.get("validation").length() == 0) {
						errors.add(new DataColumnNullException(lineNumber,headers.get("validation")));
					} else {
						
						/////
						// CHECK VALIDATE
						CreditCardValidationEnum creditCardValidation = CreditCardValidationEnum.findByName(data.get("validation"));
						if (creditCardValidation==null) {
							errors.add(new DataCreditCardValidationNotFoundException(lineNumber, headers.get("validation"),data.get("validation")));
						}else{
							
							if (creditCardValidation.equals(CreditCardValidationEnum.SAME) || creditCardValidation.equals(CreditCardValidationEnum.UPDATE)) {
								if (data.get("creditCardNumber").length() == 0) {
									errors.add(new DataColumnNullException(lineNumber,headers.get("creditCardNumber")));
								}

								if (data.get("creditCardExpirationDate")
										.length() == 0) {
									errors.add(new DataColumnNullException(lineNumber,headers.get("creditCardExpirationDate")));
								}

								if (data.get("creditCardDaysOfDefault")
										.length() == 0) {
									errors.add(new DataColumnNullException(lineNumber,headers.get("creditCardDaysOfDefault")));
								}

								if (data.get("creditCardState").length() == 0) {
									errors.add(new DataColumnNullException(lineNumber,headers.get("creditCardState")));
								}
							}
							
						}
						
						/*if (data.get("validation").toUpperCase()
								.equals(validations[0])
								|| data.get("validation").toUpperCase()
										.equals(validations[1])
								|| data.get("validation").toUpperCase()
										.equals(validations[2])) {*/

							
						/*} else {
							errors.add("Error en linea "
									+ data.get("lineNumber") + ": La columna "
									+ headers.get("validation")
									+ " codigo inexistente.");
						}*/
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
					

					if (data.get("creditCardNumber").length() > 16) {
						errors.add(new DataColumnLengthException(lineNumber,headers.get("creditCardNumber"),data.get("cocreditCardNumberde").length(),16));
					}

					if (data.get("creditCardState").length() > 20) {
						errors.add(new DataColumnLengthException(lineNumber,headers.get("creditCardState"),data.get("creditCardState").length(),20));
					}

					if (data.get("creditCardDaysOfDefault").length() > 4) {
						errors.add(new DataColumnLengthException(lineNumber,headers.get("creditCardDaysOfDefault"),data.get("creditCardDaysOfDefault").length(),4));
					}

					if (data.get("validation").length() > 15) {
						errors.add(new DataColumnLengthException(lineNumber,headers.get("validation"),data.get("validation").length(),15));
					}
					
					lineNumber++;

				}
			
	}

	@SuppressWarnings("unused")
	public void validateDataType() {

		System.out.println("ingreso a validateDataType");
		
		Integer lineNumber = 1;

		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdf2 = new SimpleDateFormat("MM/yyyy");

		
				for (Map<String, String> data : dataList) {

					try {
						if (data.get("creditCardNumber").length() > 0) {
							Long creditCardNumber = Long.parseLong(data
									.get("creditCardNumber"));
						}
					} catch (NumberFormatException e) {
						errors.add(new DataColumnNumberException(lineNumber, headers.get("creditCardNumber")));
					}

					try {
						if (data.get("creditCardExpirationDate").length() > 0) {
							Date creditCardExpirationDate = sdf2.parse(data
									.get("creditCardExpirationDate"));
						}
					} catch (ParseException e1) {
						errors.add(new DataColumnDateException(lineNumber, headers.get("creditCardExpirationDate"),"mm/yyyy"));
					}

					try {
						if (data.get("creditCardDaysOfDefault").length() > 0) {
							Integer creditCardDaysOfDefault = Integer
									.parseInt(data
											.get("creditCardDaysOfDefault"));
						}
					} catch (NumberFormatException e) {
						errors.add(new DataColumnNumberException(lineNumber, headers.get("creditCardDaysOfDefault")));
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

		
				for (Map<String, String> data : dataList) {

					

						// VALIDA SI LA VENTA EXISTE
						Sale sale = saleService.findByCode(data.get("code"));
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

	public void updateCreditCards() {

		System.out.println("ingreso a createSales");
		
		Integer lineNumber = 1;

		// sales = new ArrayList<Sale>();
		// SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdf2 = new SimpleDateFormat("MM/yyyy");

		
		SessionBean sessionBean = (SessionBean) FacesContext
				.getCurrentInstance().getExternalContext()
				.getSessionMap().get("sessionBean");
				
		Integer userId = sessionBean.getUser().getId();
		User user = new User();
		user.setId(userId);
		Date current = new Date();

		
				for (Map<String, String> data : dataList) {

					try {
						// / SE CREA LA TARJETA
						
						Sale sale = saleService.findByCode(data.get("code"));
						
						CreditCard creditCard = new CreditCard();
						creditCard.setSale(sale);
						
						creditCard.setExpirationDate(data.get(
								"creditCardExpirationDate").length() > 0 ? sdf2
								.parse(data.get("creditCardExpirationDate"))
								: null);
						creditCard.setDaysOfDefault(data.get(
										"creditCardDaysOfDefault").length() > 0 ? Integer.parseInt(data
										.get("creditCardDaysOfDefault"))
										: null);
						creditCard.setNumber(data.get(
										"creditCardNumber").length() > 0 ? Long
										.parseLong(data.get("creditCardNumber"))
										: null);
						creditCard.setState(data.get("creditCardState"));
						creditCard.setValidation(CreditCardValidationEnum.findByName(data.get("validation")));

						
						creditCard.setCreatedBy(user);
						creditCard.setCreatedAt(current);

						creditCardUpdateService.add(creditCard);

						//salesCreated++;

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
					facesUtil.sendConfirmMessage("Se crearon correctamente las tarjetas.","");
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
											updateCreditCards();
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

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

}
