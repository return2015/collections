package com.returnsoft.collection.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.primefaces.model.UploadedFile;

import com.returnsoft.collection.entity.Commerce;
import com.returnsoft.collection.entity.CreditCard;
import com.returnsoft.collection.entity.Payer;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.entity.SaleState;
import com.returnsoft.collection.entity.User;
import com.returnsoft.collection.enumeration.CreditCardValidationEnum;
import com.returnsoft.collection.enumeration.SaleStateEnum;
import com.returnsoft.collection.enumeration.UserTypeEnum;
import com.returnsoft.collection.exception.DataColumnDateException;
import com.returnsoft.collection.exception.DataColumnDecimalException;
import com.returnsoft.collection.exception.DataColumnLengthException;
import com.returnsoft.collection.exception.DataColumnNullException;
import com.returnsoft.collection.exception.DataColumnNumberException;
import com.returnsoft.collection.exception.DataCommerceCodeException;
import com.returnsoft.collection.exception.DataSaleCreateException;
import com.returnsoft.collection.exception.DataSaleDuplicateException;
import com.returnsoft.collection.exception.DataSalePaymentMethodException;
import com.returnsoft.collection.exception.DataSaleProductException;
import com.returnsoft.collection.exception.DataSaleStateNotFoundException;
import com.returnsoft.collection.exception.FileColumnsTotalException;
import com.returnsoft.collection.exception.FileExtensionException;
import com.returnsoft.collection.exception.FileNotFoundException;
import com.returnsoft.collection.exception.FileRowsZeroException;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.exception.UserLoggedNotFoundException;
import com.returnsoft.collection.exception.UserPermissionNotFoundException;
import com.returnsoft.collection.service.CommerceService;
import com.returnsoft.collection.service.SaleService;
import com.returnsoft.collection.util.FacesUtil;

@ManagedBean
@ViewScoped
public class LoadSalesController implements Serializable {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -4374647108976645077L;

	private UploadedFile file;

	private List<Commerce> commerces;
	private Integer FILE_ROWS = 49;

	private List<Exception> errors;
	private Map<String, String> headers;
	private List<Map<String, String>> dataList;
	
	//private final String[] saleStates = { "ACTIVO", "BAJA" };

	@EJB
	private CommerceService commerceService;
	
	@EJB
	private SaleService saleService;
	
	private FacesUtil facesUtil;

	public LoadSalesController() {
		
		//System.out.println("Construyendo LoadSalesController");
		
		facesUtil = new FacesUtil();
	}

	public String initialize() {
		
		//System.out.println("Inicializando LoadSalesController");
		
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
	
	public void download(){
		try {
			/*ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance()
					.getExternalContext().getContext();
			String separator = System.getProperty("file.separator");
			String rootPath = servletContext.getRealPath(separator);
			String fileName = rootPath + "resources" + separator + "templates" + separator + "tramas_ventas.xlsx";
			*/
			
			ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance()
					.getExternalContext().getContext();
			String separator = System.getProperty("file.separator");
			String rootPath = servletContext.getRealPath(separator);
			String fileName = rootPath + "resources" + separator + "templates" + separator
					+ "tramas_ventas.xlsx";
			File file = new File(fileName);
			InputStream pdfInputStream = new FileInputStream(file);
			
			FacesContext facesContext = FacesContext.getCurrentInstance();
			ExternalContext externalContext = facesContext.getExternalContext();
			externalContext.setResponseContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"tramas_ventas.xlsx\"");

			// Read PDF contents and write them to the output
			byte[] bytesBuffer = new byte[2048];
			int bytesRead;
			
			while ((bytesRead = pdfInputStream.read(bytesBuffer)) > 0) {
				externalContext.getResponseOutputStream().write(bytesBuffer, 0, bytesRead);
			}
			
			externalContext.getResponseOutputStream().flush();
			externalContext.getResponseOutputStream().close();
			pdfInputStream.close();
			facesContext.responseComplete();
			
		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
					e.getMessage());
		}
		
	}
	
	

	public void getData() {

		//System.out.println("LoadSalesController: obteniendo datos desde archivo");

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
					String[] values = strLine.split("\\|", -1);
					if (values.length != FILE_ROWS) {
						//new DataColumnsTotalException(lineNumber, values.length, FILE_ROWS);
						errors.add(new FileColumnsTotalException(lineNumber, values.length, FILE_ROWS));
					} else {

						headers.put("documentType", values[0]);
						headers.put("nuicResponsible", values[1]);
						headers.put("lastnamePaternalResponsible", values[2]);
						headers.put("lastnameMaternalResponsible", values[3]);
						headers.put("firstnameResponsible", values[4]);

						headers.put("creditCardNumber", values[5]);
						headers.put("accountNumber", values[6]);
						headers.put("creditCardExpirationDate", values[7]);
						headers.put("creditCardState", values[8]);
						headers.put("creditCardDaysOfDefault", values[9]);

						headers.put("nuicContractor", values[10]);
						headers.put("lastnamePaternalContractor", values[11]);
						headers.put("lastnameMaternalContractor", values[12]);
						headers.put("firstnameContractor", values[13]);
						headers.put("nuicInsured", values[14]);

						headers.put("lastnamePaternalInsured", values[15]);
						headers.put("lastnameMaternalInsured", values[16]);
						headers.put("firstnameInsured", values[17]);
						headers.put("phone1", values[18]);
						headers.put("phone2", values[19]);

						headers.put("mail", values[20]);
						headers.put("department", values[21]);
						headers.put("province", values[22]);
						headers.put("district", values[23]);
						headers.put("address", values[24]);

						headers.put("dateOfSale", values[25]);
						headers.put("channelOfSale", values[26]);
						headers.put("placeOfSale", values[27]);
						headers.put("vendorCode", values[28]);
						headers.put("vendorName", values[29]);

						headers.put("policyNumber", values[30]);
						headers.put("certificateNumber", values[31]);
						headers.put("proposalNumber", values[32]);
						headers.put("commercialCode", values[33]);
						headers.put("product", values[34]);

						headers.put("productDescription", values[35]);
						headers.put("collectionPeriod", values[36]);
						headers.put("collectionType", values[37]);
						headers.put("paymentMethod", values[38]);
						headers.put("insurancePremium", values[39]);

						headers.put("auditDate", values[40]);
						headers.put("auditUser", values[41]);
						headers.put("state", values[42]);
						headers.put("stateDate", values[43]);
						headers.put("downUser", values[44]);

						headers.put("downChannel", values[45]);
						headers.put("downReason", values[46]);
						headers.put("downObservation", values[47]);
						headers.put("creditCardUpdatedAt", values[48]);

					}
				} else {

					String[] values = strLine.split("\\|", -1);

					if (values.length != FILE_ROWS) {
						//new DataColumnsTotalException(lineNumber, values.length, FILE_ROWS);
						errors.add(new FileColumnsTotalException(lineNumber, values.length, FILE_ROWS));
					} else {

						Map<String, String> data = new HashMap<String, String>();

						//data.put("lineNumber", lineNumber.toString());
						data.put("documentType", values[0]);
						data.put("nuicResponsible", values[1]);
						data.put("lastnamePaternalResponsible", values[2]);
						data.put("lastnameMaternalResponsible", values[3]);
						data.put("firstnameResponsible", values[4]);

						data.put("creditCardNumber", values[5]);
						data.put("accountNumber", values[6]);
						data.put("creditCardExpirationDate", values[7]);
						data.put("creditCardState", values[8]);
						data.put("creditCardDaysOfDefault", values[9]);

						data.put("nuicContractor", values[10]);
						data.put("lastnamePaternalContractor", values[11]);
						data.put("lastnameMaternalContractor", values[12]);
						data.put("firstnameContractor", values[13]);
						data.put("nuicInsured", values[14]);

						data.put("lastnamePaternalInsured", values[15]);
						data.put("lastnameMaternalInsured", values[16]);
						data.put("firstnameInsured", values[17]);
						data.put("phone1", values[18]);
						data.put("phone2", values[19]);

						data.put("mail", values[20]);
						data.put("department", values[21]);
						data.put("province", values[22]);
						data.put("district", values[23]);
						data.put("address", values[24]);

						data.put("dateOfSale", values[25]);
						data.put("channelOfSale", values[26]);
						data.put("placeOfSale", values[27]);
						data.put("vendorCode", values[28]);
						data.put("vendorName", values[29]);

						data.put("policyNumber", values[30]);
						data.put("certificateNumber", values[31]);
						data.put("proposalNumber", values[32]);
						data.put("commercialCode", values[33]);
						data.put("product", values[34]);

						data.put("productDescription", values[35]);
						data.put("collectionPeriod", values[36]);
						data.put("collectionType", values[37]);
						data.put("paymentMethod", values[38]);
						data.put("insurancePremium", values[39]);

						data.put("auditDate", values[40]);
						data.put("auditUser", values[41]);
						data.put("state", values[42]);
						data.put("stateDate", values[43]);
						data.put("downUser", values[44]);

						data.put("downChannel", values[45]);
						data.put("downReason", values[46]);
						data.put("downObservation", values[47]);
						data.put("creditCardUpdatedAt", values[48]);

						dataList.add(data);
					}
				}

				lineNumber++;

			}

			System.out.println("datas:" + dataList.size());
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
		
		//Integer lineNumber = 1;
		
		for (int i=0; i< dataList.size();i++ ) {
			Map<String, String> data1 = dataList.get(i);
			for (int j=i+1; j< dataList.size();j++ ) {
				Map<String, String> data2 = dataList.get(j);
				if (data1.get("nuicInsured").equals(data2.get("nuicInsured")) 
						&& data1.get("dateOfSale").equals(data2.get("dateOfSale"))) {
					
					errors.add(new DataSaleDuplicateException(i,j, headers.get("nuicInsured"),headers.get("dateOfSale"),data1.get("nuicInsured"),data1.get("dateOfSale")));
				}
				
			}
			
			//lineNumber++;
		}
		
		System.out.println("errors:" + errors.size());
		
	}

	public void validateDataNull() {

		System.out.println("LoadSalesController: validando valores requeridos");
		
		Integer lineNumber = 1;


		for (Map<String, String> data : dataList) {

			// SE VALIDA NOT NULL

			if (data.get("nuicResponsible").length() == 0) {
				errors.add(new DataColumnNullException(lineNumber,headers.get("nuicResponsible")));
			}

			if (data.get("firstnameResponsible").length() == 0) {
				errors.add(new DataColumnNullException(lineNumber,headers.get("firstnameResponsible")));
			}

			if (data.get("lastnamePaternalResponsible").length() == 0) {
				errors.add(new DataColumnNullException(lineNumber,headers.get("lastnamePaternalResponsible")));
			}

			if (data.get("lastnameMaternalResponsible").length() == 0) {
				errors.add(new DataColumnNullException(lineNumber,headers.get("lastnameMaternalResponsible")));
			}

			if (data.get("accountNumber").length() == 0) {
				errors.add(new DataColumnNullException(lineNumber,headers.get("accountNumber")));
			}

			if (data.get("nuicInsured").length() == 0) {
				errors.add(new DataColumnNullException(lineNumber,headers.get("nuicInsured")));
			}

			if (data.get("dateOfSale").length() == 0) {
				errors.add(new DataColumnNullException(lineNumber,headers.get("dateOfSale")));
			}

			if (data.get("commercialCode").length() == 0) {
				errors.add(new DataColumnNullException(lineNumber,headers.get("commercialCode")));
			}

			if (data.get("product").length() == 0) {
				errors.add(new DataColumnNullException(lineNumber,headers.get("product")));
			}

			if (data.get("collectionPeriod").length() == 0) {
				errors.add(new DataColumnNullException(lineNumber,headers.get("collectionPeriod")));
			}

			if (data.get("collectionType").length() == 0) {
				errors.add(new DataColumnNullException(lineNumber,headers.get("collectionType")));
			}

			if (data.get("paymentMethod").length() == 0) {
				errors.add(new DataColumnNullException(lineNumber,headers.get("paymentMethod")));
			}

			if (data.get("insurancePremium").length() == 0) {
				errors.add(new DataColumnNullException(lineNumber,headers.get("insurancePremium")));
			}

			if (data.get("auditDate").length() == 0) {
				errors.add(new DataColumnNullException(lineNumber,headers.get("auditDate")));
			}

			if (data.get("auditUser").length() == 0) {
				errors.add(new DataColumnNullException(lineNumber,headers.get("auditUser")));
			}

			if (data.get("state").length() == 0) {
				errors.add(new DataColumnNullException(lineNumber,headers.get("state")));
			}
			
			lineNumber++;

		}
		
		System.out.println("errors:" + errors.size());

	}

	public void validateDataSize() {
		
		System.out.println("LoadSalesController: validando tamaño de los valores");
		
		Integer lineNumber = 1;

		for (Map<String, String> data : dataList) {
			
			
			if (data.get("documentType").length()>3) {
				errors.add(new DataColumnLengthException(lineNumber,headers.get("documentType"),data.get("documentType").length(),3));
			}
			
			if (data.get("nuicResponsible").length()>8) {
				errors.add(new DataColumnLengthException(lineNumber,headers.get("nuicResponsible"),data.get("nuicResponsible").length(),8));
			}
			
			if (data.get("lastnamePaternalResponsible").length()>50) {
				errors.add(new DataColumnLengthException(lineNumber,headers.get("lastnamePaternalResponsible"),data.get("lastnamePaternalResponsible").length(),50));
			}
			
			if (data.get("lastnameMaternalResponsible").length()>50) {
				errors.add(new DataColumnLengthException(lineNumber,headers.get("lastnameMaternalResponsible"),data.get("lastnameMaternalResponsible").length(),50));
			}
			
			if (data.get("firstnameResponsible").length()>50) {
				errors.add(new DataColumnLengthException(lineNumber,headers.get("firstnameResponsible"),data.get("firstnameResponsible").length(),50));
			}
			
			if (data.get("creditCardNumber").length()>16) {
				errors.add(new DataColumnLengthException(lineNumber,headers.get("creditCardNumber"),data.get("creditCardNumber").length(),16));
			}
			
			if (data.get("accountNumber").length()>10) {
				errors.add(new DataColumnLengthException(lineNumber,headers.get("accountNumber"),data.get("accountNumber").length(),10));
			}
			
			if (data.get("creditCardState").length()>20) {
				errors.add(new DataColumnLengthException(lineNumber,headers.get("creditCardState"),data.get("creditCardState").length(),20));
			}
			
			if (data.get("creditCardDaysOfDefault").length()>4) {
				errors.add(new DataColumnLengthException(lineNumber,headers.get("creditCardDaysOfDefault"),data.get("creditCardDaysOfDefault").length(),4));
			}
			
			if (data.get("nuicContractor").length()>8) {
				errors.add(new DataColumnLengthException(lineNumber,headers.get("nuicContractor"),data.get("nuicContractor").length(),8));
			}
			
			if (data.get("lastnamePaternalContractor").length()>50) {
				errors.add(new DataColumnLengthException(lineNumber,headers.get("lastnamePaternalContractor"),data.get("lastnamePaternalContractor").length(),50));
			}
			
			if (data.get("lastnameMaternalContractor").length()>50) {
				errors.add(new DataColumnLengthException(lineNumber,headers.get("lastnameMaternalContractor"),data.get("lastnameMaternalContractor").length(),50));
			}
			
			if (data.get("firstnameContractor").length()>50) {
				errors.add(new DataColumnLengthException(lineNumber,headers.get("firstnameContractor"),data.get("firstnameContractor").length(),50));
			}
			
			if (data.get("nuicInsured").length()>8) {
				errors.add(new DataColumnLengthException(lineNumber,headers.get("nuicInsured"),data.get("nuicInsured").length(),8));
			}
			
			if (data.get("lastnamePaternalInsured").length()>50) {
				errors.add(new DataColumnLengthException(lineNumber,headers.get("lastnamePaternalInsured"),data.get("lastnamePaternalInsured").length(),50));
			}
			
			if (data.get("lastnameMaternalInsured").length()>50) {
				errors.add(new DataColumnLengthException(lineNumber,headers.get("lastnameMaternalInsured"),data.get("lastnameMaternalInsured").length(),50));
			}
			
			if (data.get("firstnameInsured").length()>50) {
				errors.add(new DataColumnLengthException(lineNumber,headers.get("firstnameInsured"),data.get("firstnameInsured").length(),50));
			}
			
			if (data.get("phone1").length()>9) {
				errors.add(new DataColumnLengthException(lineNumber,headers.get("phone1"),data.get("phone1").length(),9));
			}
			
			if (data.get("phone2").length()>9) {
				errors.add(new DataColumnLengthException(lineNumber,headers.get("phone2"),data.get("phone2").length(),9));
			}
			
			if (data.get("mail").length()>45) {
				errors.add(new DataColumnLengthException(lineNumber,headers.get("mail"),data.get("mail").length(),45));
			}
			
			if (data.get("department").length()>20) {
				errors.add(new DataColumnLengthException(lineNumber,headers.get("department"),data.get("department").length(),20));
			}
			
			if (data.get("province").length()>20) {
				errors.add(new DataColumnLengthException(lineNumber,headers.get("province"),data.get("province").length(),20));
			}
			
			if (data.get("district").length()>40) {
				errors.add(new DataColumnLengthException(lineNumber,headers.get("district"),data.get("district").length(),40));
			}
			
			if (data.get("address").length()>150) {
				errors.add(new DataColumnLengthException(lineNumber,headers.get("address"),data.get("address").length(),150));
			}
			
			if (data.get("channelOfSale").length()>15) {
				errors.add(new DataColumnLengthException(lineNumber,headers.get("channelOfSale"),data.get("channelOfSale").length(),15));
			}
			
			if (data.get("placeOfSale").length()>25) {
				errors.add(new DataColumnLengthException(lineNumber,headers.get("placeOfSale"),data.get("placeOfSale").length(),25));
			}
			
			if (data.get("vendorCode").length()>10) {
				errors.add(new DataColumnLengthException(lineNumber,headers.get("vendorCode"),data.get("vendorCode").length(),10));
			}
			
			if (data.get("vendorName").length()>35) {
				errors.add(new DataColumnLengthException(lineNumber,headers.get("vendorName"),data.get("vendorName").length(),35));
			}
			
			if (data.get("policyNumber").length()>10) {
				errors.add(new DataColumnLengthException(lineNumber,headers.get("policyNumber"),data.get("policyNumber").length(),10));
			}
			
			if (data.get("certificateNumber").length()>10) {
				errors.add(new DataColumnLengthException(lineNumber,headers.get("certificateNumber"),data.get("certificateNumber").length(),10));
			}
			
			if (data.get("proposalNumber").length()>10) {
				errors.add(new DataColumnLengthException(lineNumber,headers.get("proposalNumber"),data.get("proposalNumber").length(),10));
			}
			
			if (data.get("commercialCode").length()>10) {
				errors.add(new DataColumnLengthException(lineNumber,headers.get("commercialCode"),data.get("commercialCode").length(),10));
			}
			
			if (data.get("product").length()>45) {
				errors.add(new DataColumnLengthException(lineNumber,headers.get("product"),data.get("product").length(),45));
			}
			
			if (data.get("productDescription").length()>45) {
				errors.add(new DataColumnLengthException(lineNumber,headers.get("productDescription"),data.get("productDescription").length(),45));
			}
			
			if (data.get("collectionPeriod").length()>45) {
				errors.add(new DataColumnLengthException(lineNumber,headers.get("collectionPeriod"),data.get("collectionPeriod").length(),45));
			}
			
			if (data.get("collectionType").length()>15) {
				errors.add(new DataColumnLengthException(lineNumber,headers.get("collectionType"),data.get("collectionType").length(),15));
			}
			
			if (data.get("paymentMethod").length()>10) {
				errors.add(new DataColumnLengthException(lineNumber,headers.get("paymentMethod"),data.get("paymentMethod").length(),10));
			}
			
			if (data.get("auditUser").length()>15) {
				errors.add(new DataColumnLengthException(lineNumber,headers.get("auditUser"),data.get("auditUser").length(),15));
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
		
		System.out.println("errors:" + errors.size());

	}

	public void validateDataType() {

		System.out.println("LoadSalesController: validando tipos de dato");
		
		Integer lineNumber = 1;

		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdf2 = new SimpleDateFormat("MM/yyyy");

		for (Map<String, String> data : dataList) {

			try {
				Integer.parseInt(data.get("nuicResponsible"));
			} catch (NumberFormatException e) {
				errors.add(new DataColumnNumberException(lineNumber, headers.get("nuicResponsible")));
			}

			try {
				if (data.get("creditCardNumber").length() > 0) {
					Long.parseLong(data.get("creditCardNumber"));
				}
			} catch (NumberFormatException e) {
				errors.add(new DataColumnNumberException(lineNumber, headers.get("creditCardNumber")));
			}

			try {
				Long.parseLong(data.get("accountNumber"));
			} catch (NumberFormatException e) {
				errors.add(new DataColumnNumberException(lineNumber, headers.get("accountNumber")));
			}

			try {
				if (data.get("creditCardExpirationDate").length() > 0) {
					sdf2.parse(data.get("creditCardExpirationDate"));
				}

			} catch (ParseException e1) {
				errors.add(new DataColumnDateException(lineNumber, headers.get("creditCardExpirationDate"),"mm/yyyy"));

			}

			try {

				if (data.get("creditCardDaysOfDefault").length() > 0) {
					Integer.parseInt(data.get("creditCardDaysOfDefault"));
				}

			} catch (NumberFormatException e) {
				errors.add(new DataColumnNumberException(lineNumber, headers.get("creditCardDaysOfDefault")));
			}

			try {
				if (data.get("nuicContractor").length() > 0) {
					Integer.parseInt(data.get("nuicContractor"));
				}

			} catch (NumberFormatException e) {
				errors.add(new DataColumnNumberException(lineNumber, headers.get("nuicContractor")));
			}

			try {
				Integer.parseInt(data.get("nuicInsured"));
			} catch (NumberFormatException e) {
				errors.add(new DataColumnNumberException(lineNumber, headers.get("nuicInsured")));
			}

			try {
				if (data.get("phone1").length() > 0) {
					Integer.parseInt(data.get("phone1"));
				}
			} catch (NumberFormatException e) {
				errors.add(new DataColumnNumberException(lineNumber, headers.get("phone1")));
			}

			try {
				if (data.get("phone2").length() > 0) {
					Integer.parseInt(data.get("phone2"));
				}
			} catch (NumberFormatException e) {
				errors.add(new DataColumnNumberException(lineNumber, headers.get("phone2")));
			}

			try {
				sdf1.parse(data.get("dateOfSale"));
			} catch (ParseException e1) {
				errors.add(new DataColumnDateException(lineNumber, headers.get("dateOfSale"),"dd/mm/yyyy"));
			}

			try {
				Long.parseLong(data.get("commercialCode"));
			} catch (NumberFormatException e) {
				errors.add(new DataColumnNumberException(lineNumber, headers.get("commercialCode")));
			}

			try {
				BigDecimal insurancePremium = new BigDecimal(
						data.get("insurancePremium"));
				if (insurancePremium.scale() > 2) {
					errors.add(new DataColumnDecimalException(lineNumber, headers.get("insurancePremium"),2));
				}
			} catch (NumberFormatException e) {
				errors.add(new DataColumnNumberException(lineNumber, headers.get("insurancePremium")));
			}

			try {
				sdf1.parse(data.get("auditDate"));
			} catch (ParseException e) {
				errors.add(new DataColumnDateException(lineNumber, headers.get("auditDate"),"dd/mm/yyyy"));
			}

			try {
				if (data.get("stateDate").length() > 0) {
					sdf1.parse(data.get("stateDate"));
				}
			} catch (ParseException e) {
				errors.add(new DataColumnDateException(lineNumber, headers.get("stateDate"),"dd/mm/yyyy"));
			}

			try {
				if (data.get("creditCardUpdatedAt").length() > 0) {
					sdf1.parse(data.get("creditCardUpdatedAt"));
				}

			} catch (ParseException e) {
				errors.add(new DataColumnDateException(lineNumber, headers.get("creditCardUpdatedAt"),"dd/mm/yyyy"));
			}

			lineNumber++;

		}

		System.out.println("errors:" + errors.size());

	}

	/**
	 * 
	 */
	public void validateData() {

		try {
			
			System.out.println("LoadSalesController: validando datos");
			
			Integer lineNumber = 1;
			SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
			
			

			for (Map<String, String> data : dataList) {
				
				// VALIDATE SALE STATE
				SaleStateEnum saleState = SaleStateEnum.findByName(data.get("state"));
				if (saleState==null) {
					errors.add(new DataSaleStateNotFoundException(lineNumber, headers.get("state"),data.get("state")));
				}
				
				// VALIDATE COMMERCIAL CODE
				Commerce commercialCodeObject = null;
				for (Commerce commerce : commerces) {
					if (data.get("commercialCode").equals(commerce.getCode())) {
						commercialCodeObject = commerce;
					}
				}
				if (commercialCodeObject == null) {
					errors.add(new DataCommerceCodeException(lineNumber, headers.get("commercialCode"),data.get("commercialCode")));
				} else {

					// VALIDATE PRODUCT
					if (!data.get("product").equals(commercialCodeObject.getProduct().getName())) {
						errors.add(new DataSaleProductException(lineNumber, headers.get("product"),data.get("product")));
					}

					// VALIDATE PAYMENTMETHOS
					if (!data.get("paymentMethod").equals(commercialCodeObject.getPaymentMethod().getName())) {
						errors.add(new DataSalePaymentMethodException(lineNumber, headers.get("paymentMethod"),data.get("paymentMethod")));
					}

				}

				// VALIDATE IF EXIST
				Integer nuicInsured = Integer.parseInt(data.get("nuicInsured"));
				Date dateOfSale = sdf1.parse(data.get("dateOfSale"));
				Sale sale = saleService.findByNuicInsuredAndDateOfSale(nuicInsured, dateOfSale);
				if (sale != null && sale.getId() > 0) {
					errors.add(new DataSaleDuplicateException(lineNumber, headers.get("nuicInsured"),headers.get("dateOfSale"), data.get("nuicInsured"),data.get("dateOfSale")));
				}
				
				lineNumber++;

			}

			

			System.out.println("errors:" + errors.size());

		} catch (Exception e) {
			e.printStackTrace();
			errors.add( new ServiceException());

		}

	}

	public void createSales() {

		System.out.println("ingreso a createSales");

		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdf2 = new SimpleDateFormat("MM/yyyy");

		/*Integer salesCreated = 0;
		Integer salesNotCreated = 0;*/
		
		SessionBean sessionBean = (SessionBean) FacesContext
				.getCurrentInstance().getExternalContext()
				.getSessionMap().get("sessionBean");
		
		User user = new User();
		user.setId(sessionBean.getUser().getId());
		
		Date current = new Date();
		

		Integer lineNumber =1;

		for (Map<String, String> data : dataList) {

			try {

				// / SE CREA LA VENTA
				Sale sale = new Sale();
				sale.setAccountNumber(Long.parseLong(data.get("accountNumber")));
				
				sale.setAuditDate(sdf1.parse(data.get("auditDate")));
				sale.setAuditUser(data.get("auditUser"));
				
				//////
				CreditCard creditCard = new CreditCard();
				creditCard.setExpirationDate(data.get(
						"creditCardExpirationDate").length() > 0 ? sdf2
						.parse(data.get("creditCardExpirationDate"))
						: null);
				creditCard.setDaysOfDefault(data.get(
						"creditCardDaysOfDefault").length() > 0 ? Integer
						.parseInt(data.get("creditCardDaysOfDefault"))
						: null);
				creditCard.setNumber(data.get("creditCardNumber")
						.length() > 0 ? Long.parseLong(data
						.get("creditCardNumber")) : null);
				creditCard.setState(data.get("creditCardState"));
				
				creditCard.setUpdateDate(!data.get(
						"creditCardUpdatedAt").equals("") ? sdf1
						.parse(data.get("creditCardUpdatedAt")) : null);
				
				creditCard.setValidation(CreditCardValidationEnum.UPDATE);
				
				creditCard.setCreatedAt(current);
				creditCard.setCreatedBy(user);
				
				sale.setCreditCard(creditCard);
				
				///////////
				
				
				sale.setCertificateNumber(data.get("certificateNumber"));
				sale.setChannelOfSale(data.get("channelOfSale"));
				sale.setCollectionPeriod(data.get("collectionPeriod"));
				sale.setCollectionType(data.get("collectionType"));
				sale.setDateOfSale(sdf1.parse(data.get("dateOfSale")));
				
				sale.setDocumentType(data.get("documentType"));
				sale.setInsurancePremium(new BigDecimal(data
						.get("insurancePremium")));
				sale.setLastnameMaternalContractor(data
						.get("lastnameMaternalContractor"));
				sale.setLastnameMaternalInsured(data
						.get("lastnameMaternalInsured"));
				sale.setLastnamePaternalContractor(data
						.get("lastnamePaternalContractor"));
				sale.setLastnamePaternalInsured(data
						.get("lastnamePaternalInsured"));
				sale.setFirstnameInsured(data.get("firstnameInsured"));
				sale.setNuicContractor(data.get("nuicContractor")
						.length() > 0 ? Integer.parseInt(data
						.get("nuicContractor")) : null);
				sale.setNuicInsured(Integer.parseInt(data
						.get("nuicInsured")));
				sale.setFirstnameContractor(data
						.get("firstnameContractor"));
				sale.setPhone1(data.get("phone1").length() > 0 ? Integer
						.parseInt(data.get("phone1")) : null);
				sale.setPhone2(data.get("phone2").length() > 0 ? Integer
						.parseInt(data.get("phone2")) : null);
				sale.setPlaceOfSale(data.get("placeOfSale"));
				sale.setPolicyNumber(data.get("policyNumber"));

				sale.setProductDescription(data.get("productDescription"));
				sale.setProposalNumber(data.get("proposalNumber"));
				
				sale.setVendorCode(data.get("vendorCode"));
				sale.setVendorName(data.get("vendorName"));
				
				
				/////////////
				
				Payer payer = new Payer();
				
				payer.setAddress(data.get("address"));
				
				payer.setDepartment(data.get("department"));
				payer.setDistrict(data.get("district"));
				payer.setLastnameMaternalResponsible(data
						.get("lastnameMaternalResponsible"));
				payer.setLastnamePaternalResponsible(data
						.get("lastnamePaternalResponsible"));
				payer.setMail(data.get("mail"));
				payer.setFirstnameResponsible(data
						.get("firstnameResponsible"));
				payer.setNuicResponsible(data.get("nuicResponsible")
						.length() > 0 ? Integer.parseInt(data
						.get("nuicResponsible")) : null);
				payer.setProvince(data.get("province"));
				
				payer.setCreatedAt(current);
				payer.setCreatedBy(user);
				
				
				sale.setPayer(payer);
				
				////////////////
				SaleState saleState = new SaleState();
				saleState.setDate(!data.get("stateDate").equals("") ? sdf1
						.parse(data.get("stateDate")) : null);
				
				saleState.setDownUser(data.get("downUser"));
				saleState.setDownChannel(data.get("downChannel"));
				saleState.setDownReason(data.get("downReason"));
				saleState.setDownObservation(data.get("downObservation"));
				saleState.setState(SaleStateEnum.findByName(data.get("state")));
				
				saleState.setCreatedAt(current);
				saleState.setCreatedBy(user);
				
				sale.setSaleState(saleState);
				
				//////////////////
				
				

				Commerce commercialCodeObject = null;
				for (Commerce commerce : commerces) {
					if (data.get("commercialCode")
							.equals(commerce.getCode())) {
						commercialCodeObject = commerce;
					}
				}
				sale.setCommerce(commercialCodeObject);
				
				//CAMPOS NUEVOS
				sale.setVirtualNotifications((short)0);
				sale.setPhysicalNotifications((short)0);

				sale.setCreatedBy(user);
				sale.setCreatedAt(current);

				saleService.add(sale);

			} catch (Exception e) {
				e.printStackTrace();
				errors.add(new DataSaleCreateException(lineNumber,e.getMessage()));

			}
			
			lineNumber++;

		}


		if (errors != null && errors.size() > 0) {
			for (Exception e : errors) {
				facesUtil.sendErrorMessage(e.getClass().getSimpleName(),e.getMessage());
			}
		}else{
			facesUtil.sendConfirmMessage("Se crearon correctamente las ventas.","");
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
					} else{
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
											createSales();
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
