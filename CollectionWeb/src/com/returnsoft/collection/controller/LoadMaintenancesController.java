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
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.entity.SaleState;
import com.returnsoft.collection.entity.User;
import com.returnsoft.collection.enumeration.SaleStateEnum;
import com.returnsoft.collection.enumeration.UserTypeEnum;
import com.returnsoft.collection.exception.DataCollectionCreateException;
import com.returnsoft.collection.exception.DataColumnDateException;
import com.returnsoft.collection.exception.DataColumnLengthException;
import com.returnsoft.collection.exception.DataColumnNullException;
import com.returnsoft.collection.exception.DataCommerceCodeException;
import com.returnsoft.collection.exception.DataMaintenanceDuplicateException;
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
import com.returnsoft.collection.service.SaleStateService;
import com.returnsoft.collection.service.SaleService;
import com.returnsoft.collection.util.FacesUtil;

@ManagedBean
@ViewScoped
public class LoadMaintenancesController implements Serializable {


	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4578242184575371868L;

	private UploadedFile file;

	private List<Commerce> commerces;
	private Integer FILE_ROWS = 7;
	
	private List<Exception> errors;
	private Map<String, String> headers;
	private List<Map<String, String>> dataList;
	
	//private final String[] saleStates = { "ACTIVO", "BAJA" };
	
	@EJB
	private SaleStateService saleStateService;
	
	@EJB
	private SaleService saleService;
	
	@EJB
	private CommerceService commerceService;
	
	private FacesUtil facesUtil;

	public LoadMaintenancesController() {
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
						new FileColumnsTotalException(lineNumber, values.length, FILE_ROWS);
						errors.add(new FileColumnsTotalException(lineNumber, values.length, FILE_ROWS));

					} else {

						// for (String value : values) {
						headers.put("code", values[0]);
						headers.put("saleState", values[1]);
						headers.put("stateDate", values[2]);
						headers.put("downUser", values[3]);
						headers.put("downChannel", values[4]);
						headers.put("downReason", values[5]);
						headers.put("downObservation", values[6]);
					}
				} else {

					String[] values = strLine.split("\\|");
					if (values.length != FILE_ROWS) {
						new FileColumnsTotalException(lineNumber, values.length, FILE_ROWS);
						errors.add(new FileColumnsTotalException(lineNumber, values.length, FILE_ROWS));
					} else {

						Map<String, String> data = new HashMap<String, String>();

						data.put("lineNumber", lineNumber.toString());
						data.put("code", values[0]);
						data.put("saleState", values[1]);
						data.put("stateDate", values[2]);
						data.put("downUser", values[3]);
						data.put("downChannel", values[4]);
						data.put("downReason", values[5]);
						data.put("downObservation", values[6]);
						
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
					
					errors.add(new DataMaintenanceDuplicateException(i,j, headers.get("code"), data1.get("code")));
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
					
					if (data.get("saleState").length() == 0) {
						errors.add(new DataColumnNullException(lineNumber,headers.get("saleState")));
					}

					if (data.get("stateDate").length() == 0) {
						errors.add(new DataColumnNullException(lineNumber,headers.get("stateDate")));
					}

					if (data.get("downUser").length() == 0) {
						errors.add(new DataColumnNullException(lineNumber,headers.get("downUser")));
					}

					if (data.get("downChannel").length() == 0) {
						errors.add(new DataColumnNullException(lineNumber,headers.get("downChannel")));
					}

					if (data.get("downReason").length() == 0) {
						errors.add(new DataColumnNullException(lineNumber,headers.get("downReason")));
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
					
					if (data.get("downUser").length()>15) {
						errors.add(new DataColumnLengthException(lineNumber,headers.get("downUser"),data.get("downUser").length(),15));
					}
					
					if (data.get("downChannel").length()>15) {
						errors.add(new DataColumnLengthException(lineNumber,headers.get("downChannel"),data.get("downChannel").length(),15));
					}
					
					if (data.get("downReason").length()>30) {
						errors.add(new DataColumnLengthException(lineNumber,headers.get("downReason"),data.get("downReason").length(),30));
					}
					
					if (data.get("downObservation").length()>2500) {
						errors.add(new DataColumnLengthException(lineNumber,headers.get("downObservation"),data.get("downObservation").length(),2500));
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
						if (data.get("stateDate").length() > 0) {
							Date stateDate = sdf1.parse(data.get("stateDate"));
						}
					} catch (ParseException e) {
						errors.add(new DataColumnDateException(lineNumber, headers.get("stateDate"),"dd/mm/yyyy"));
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

		//SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");

	
				for (Map<String, String> data : dataList) {


						Sale sale = saleService.findByCode(data
								.get("code"));

						// VALIDA SI LA VENTA EXISTE
						if (sale != null && sale.getId() > 0) {
							
							// VALIDA SI LA VENTA ESTA DE BAJA
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
	
	
	public void createMaintenances() {

		System.out.println("ingreso a createSales");

		// sales = new ArrayList<Sale>();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
		//SimpleDateFormat sdf2 = new SimpleDateFormat("MM/yyyy");

		//Integer salesCreated = 0;
		//Integer salesNotCreated = 0;
		
		SessionBean sessionBean = (SessionBean) FacesContext
				.getCurrentInstance().getExternalContext()
				.getSessionMap().get("sessionBean");
		
		User user = new User();
		user.setId(sessionBean.getUser().getId());
		
		Integer lineNumber =1;
		Date current = new Date();

		
				for (Map<String, String> data : dataList) {

					try {
						// / SE CREA LA VENTA
						
						Sale sale = saleService.findByCode(data.get("code"));
						
						SaleState maintenance = new SaleState();
						//maintenance.setCode(data.get("code"));
						maintenance.setSale(sale);
						maintenance.setState(SaleStateEnum.findByName(data.get("saleState")));
						maintenance.setDate(!data.get("stateDate").equals("") ? sdf1
								.parse(data.get("stateDate")) : null);
						maintenance.setDownUser(data.get("downUser"));
						maintenance.setDownChannel(data.get("downChannel"));
						maintenance.setDownReason(data.get("downReason"));
						maintenance.setDownObservation(data.get("downObservation"));
						
						maintenance.setCreatedBy(user);
						maintenance.setCreatedAt(current);

						saleStateService.add(maintenance);

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
					facesUtil.sendConfirmMessage("Se realizaron correctamente las actualizaciones de estado.","");
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
											createMaintenances();
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
		/*try {

			if (file != null && file.getFileName().length() > 0) {
				if (file.getFileName().endsWith(".csv")
						|| file.getFileName().endsWith(".CSV")) {
					System.out.println("file NO es nulo:" + file.getFileName());

					getData();

					validateDataNull();

					validateDataSize();

					validateDataType();

					validateData();

					createMaintenances();

				} else {
					FacesMessage msg = new FacesMessage(
							"Solo se admite archivos con extensión .csv");
					msg.setSeverity(FacesMessage.SEVERITY_ERROR);
					FacesContext.getCurrentInstance().addMessage(null, msg);
				}

			} else {
				FacesMessage msg = new FacesMessage(
						"Debe seleccionar un archivo");
				msg.setSeverity(FacesMessage.SEVERITY_ERROR);
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}

			return null;
			

		} catch (Exception e) {
			if (!(e instanceof ServiceException)) {
				e.printStackTrace();
			}
			FacesMessage msg = new FacesMessage(e.getMessage(), e.getMessage());
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, msg);
			return null;

		}*/

	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}
	
	
	

}
