package com.returnsoft.collection.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import com.returnsoft.collection.entity.Bank;
import com.returnsoft.collection.entity.Commerce;
import com.returnsoft.collection.entity.CreditCard;
import com.returnsoft.collection.entity.Payer;
import com.returnsoft.collection.entity.Product;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.entity.SaleState;
import com.returnsoft.collection.entity.User;
import com.returnsoft.collection.enumeration.CreditCardValidationEnum;
import com.returnsoft.collection.enumeration.SaleStateEnum;
import com.returnsoft.collection.enumeration.UserTypeEnum;
import com.returnsoft.collection.exception.BankNotSelectedException;
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
import com.returnsoft.collection.service.BankService;
import com.returnsoft.collection.service.CommerceService;
import com.returnsoft.collection.service.ProductService;
import com.returnsoft.collection.service.SaleService;
import com.returnsoft.collection.service.UserService;
import com.returnsoft.collection.util.FacesUtil;

@ManagedBean
@ViewScoped
public class SearchSalesController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@EJB
	private CommerceService commerceService;
	
	@EJB
	private BankService bankService;
	
	@EJB
	private ProductService productService;

	@EJB
	private SaleService saleService;
	
	@EJB
	private UserService userService;

	private String searchTypeSelected;
	private String personTypeSelected;

	private List<Sale> sales;
	private Sale saleSelected;

	private String creditCardNumber;
	private Date dateOfSaleStarted;
	private Date dateOfSaleEnded;

	private Date affiliationDate;

	private String nuicResponsible;
	private String lastnamePaternalResponsible;
	private String lastnameMaternalResponsible;
	private String firstnameResponsible;

	private String nuicContractor;
	private String lastnamePaternalContractor;
	private String lastnameMaternalContractor;
	private String firstnameContractor;

	private String nuicInsured;
	private String lastnamePaternalInsured;
	private String lastnameMaternalInsured;
	private String firstnameInsured;

	private List<SelectItem> banks;
	private String bankSelected;

	private List<SelectItem> products;
	private String productSelected;

	
	private List<SelectItem> saleStates;
	private String saleStateSelected;

	private Boolean searchByCreditCardNumberRendered;
	private Boolean searchByDocumentNumberResponsibleRendered;
	private Boolean searchByNamesRendered;
	private Boolean searchByDateSaleRendered;

	private Boolean searchByContractorRendered;
	private Boolean searchByInsuredRendered;
	private Boolean searchByResponsibleRendered;

	private List<Commerce> commerces;
	
	private FacesUtil facesUtil;
		
	
	
	private Integer salesCount;
	
	///////
	
	 private UploadedFile file;
	 
	 private Integer FILE_ROWS = 49;

		private List<Exception> errors;
		private Map<String, String> headers;
		private List<Map<String, String>> dataList;
		
		private Integer progress;

	public SearchSalesController() {
		System.out.println("Se construye SearchSaleController");
		facesUtil = new FacesUtil();
	}
	
	public void download() {
		try {

			ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext()
					.getContext();
			String separator = System.getProperty("file.separator");
			String rootPath = servletContext.getRealPath(separator);
			String fileName = rootPath + "resources" + separator + "templates" + separator + "tramas_ventas.xlsx";
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
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
		}

	}
	
	
	public void getData() {

		System.out.println("LoadSalesController: obteniendo datos desde archivo");

		BufferedReader br = null;
		try {
			//br = new BufferedReader(new InputStreamReader(file.getInputStream()));
			br = new BufferedReader(new InputStreamReader(file.getInputstream(),StandardCharsets.UTF_8));
		} catch (IOException e1) {
			e1.printStackTrace();
			facesUtil.sendErrorMessage(e1.getClass().getSimpleName(), e1.getMessage());
		} catch (Exception e1) {
			e1.printStackTrace();
			facesUtil.sendErrorMessage(e1.getClass().getSimpleName(), e1.getMessage());
		}

		String strLine = null;
		Integer lineNumber = 0;

		errors = new ArrayList<Exception>();
		headers = new HashMap<String, String>();
		dataList = new ArrayList<Map<String, String>>();

		try {
			while ((strLine = br.readLine()) != null) {
				
				System.out.println("strLine2:"+strLine);

				if (lineNumber == 0) {
					// SE LEE CABECERA
					String[] values = strLine.split("\\|", -1);
					if (values.length != FILE_ROWS) {
						// new DataColumnsTotalException(lineNumber,
						// values.length, FILE_ROWS);
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
						// new DataColumnsTotalException(lineNumber,
						// values.length, FILE_ROWS);
						errors.add(new FileColumnsTotalException(lineNumber, values.length, FILE_ROWS));
					} else {

						Map<String, String> data = new HashMap<String, String>();

						// data.put("lineNumber", lineNumber.toString());
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
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
		}

	}

	public void validateDuplicates() {

		System.out.println("Validando duplicados");

		// Integer lineNumber = 1;

		System.out.println("tamaño de dataList:" + dataList.size());
		for (int i = 0; i < dataList.size(); i++) {
			System.out.println("i:" + i);
			Map<String, String> data1 = dataList.get(i);
			for (int j = i + 1; j < dataList.size(); j++) {
				// System.out.println("j:"+j);
				Map<String, String> data2 = dataList.get(j);
				if (data1.get("nuicInsured").equals(data2.get("nuicInsured"))
						&& data1.get("dateOfSale").equals(data2.get("dateOfSale"))) {
					errors.add(new DataSaleDuplicateException(i, j, headers.get("nuicInsured"),
							headers.get("dateOfSale"), data1.get("nuicInsured"), data1.get("dateOfSale")));
				}

			}

			// lineNumber++;
		}

		System.out.println("errors:" + errors.size());

	}

	public void validateDataNull() {

		System.out.println("LoadSalesController: validando valores requeridos");

		Integer lineNumber = 1;

		for (Map<String, String> data : dataList) {

			// SE VALIDA NOT NULL

			if (data.get("nuicResponsible").length() == 0) {
				errors.add(new DataColumnNullException(lineNumber, headers.get("nuicResponsible")));
			}

			if (data.get("firstnameResponsible").length() == 0) {
				errors.add(new DataColumnNullException(lineNumber, headers.get("firstnameResponsible")));
			}

			if (data.get("lastnamePaternalResponsible").length() == 0) {
				errors.add(new DataColumnNullException(lineNumber, headers.get("lastnamePaternalResponsible")));
			}

			if (data.get("lastnameMaternalResponsible").length() == 0) {
				errors.add(new DataColumnNullException(lineNumber, headers.get("lastnameMaternalResponsible")));
			}

			if (data.get("accountNumber").length() == 0) {
				errors.add(new DataColumnNullException(lineNumber, headers.get("accountNumber")));
			}

			if (data.get("nuicInsured").length() == 0) {
				errors.add(new DataColumnNullException(lineNumber, headers.get("nuicInsured")));
			}

			if (data.get("dateOfSale").length() == 0) {
				errors.add(new DataColumnNullException(lineNumber, headers.get("dateOfSale")));
			}

			if (data.get("commercialCode").length() == 0) {
				errors.add(new DataColumnNullException(lineNumber, headers.get("commercialCode")));
			}

			if (data.get("product").length() == 0) {
				errors.add(new DataColumnNullException(lineNumber, headers.get("product")));
			}

			if (data.get("collectionPeriod").length() == 0) {
				errors.add(new DataColumnNullException(lineNumber, headers.get("collectionPeriod")));
			}

			if (data.get("collectionType").length() == 0) {
				errors.add(new DataColumnNullException(lineNumber, headers.get("collectionType")));
			}

			if (data.get("paymentMethod").length() == 0) {
				errors.add(new DataColumnNullException(lineNumber, headers.get("paymentMethod")));
			}

			if (data.get("insurancePremium").length() == 0) {
				errors.add(new DataColumnNullException(lineNumber, headers.get("insurancePremium")));
			}

			if (data.get("auditDate").length() == 0) {
				errors.add(new DataColumnNullException(lineNumber, headers.get("auditDate")));
			}

			if (data.get("auditUser").length() == 0) {
				errors.add(new DataColumnNullException(lineNumber, headers.get("auditUser")));
			}

			if (data.get("state").length() == 0) {
				errors.add(new DataColumnNullException(lineNumber, headers.get("state")));
			}

			lineNumber++;

		}

		System.out.println("errors:" + errors.size());

	}

	public void validateDataSize() {

		System.out.println("LoadSalesController: validando tamaño de los valores");

		Integer lineNumber = 1;

		for (Map<String, String> data : dataList) {

			if (data.get("documentType").length() > 3) {
				errors.add(new DataColumnLengthException(lineNumber, headers.get("documentType"),
						data.get("documentType").length(), 3));
			}

			if (data.get("nuicResponsible").length() > 8) {
				errors.add(new DataColumnLengthException(lineNumber, headers.get("nuicResponsible"),
						data.get("nuicResponsible").length(), 8));
			}

			if (data.get("lastnamePaternalResponsible").length() > 50) {
				errors.add(new DataColumnLengthException(lineNumber, headers.get("lastnamePaternalResponsible"),
						data.get("lastnamePaternalResponsible").length(), 50));
			}

			if (data.get("lastnameMaternalResponsible").length() > 50) {
				errors.add(new DataColumnLengthException(lineNumber, headers.get("lastnameMaternalResponsible"),
						data.get("lastnameMaternalResponsible").length(), 50));
			}

			if (data.get("firstnameResponsible").length() > 50) {
				errors.add(new DataColumnLengthException(lineNumber, headers.get("firstnameResponsible"),
						data.get("firstnameResponsible").length(), 50));
			}

			if (data.get("creditCardNumber").length() > 16) {
				errors.add(new DataColumnLengthException(lineNumber, headers.get("creditCardNumber"),
						data.get("creditCardNumber").length(), 16));
			}

			if (data.get("accountNumber").length() > 10) {
				errors.add(new DataColumnLengthException(lineNumber, headers.get("accountNumber"),
						data.get("accountNumber").length(), 10));
			}

			if (data.get("creditCardState").length() > 20) {
				errors.add(new DataColumnLengthException(lineNumber, headers.get("creditCardState"),
						data.get("creditCardState").length(), 20));
			}

			if (data.get("creditCardDaysOfDefault").length() > 4) {
				errors.add(new DataColumnLengthException(lineNumber, headers.get("creditCardDaysOfDefault"),
						data.get("creditCardDaysOfDefault").length(), 4));
			}

			if (data.get("nuicContractor").length() > 8) {
				errors.add(new DataColumnLengthException(lineNumber, headers.get("nuicContractor"),
						data.get("nuicContractor").length(), 8));
			}

			if (data.get("lastnamePaternalContractor").length() > 50) {
				errors.add(new DataColumnLengthException(lineNumber, headers.get("lastnamePaternalContractor"),
						data.get("lastnamePaternalContractor").length(), 50));
			}

			if (data.get("lastnameMaternalContractor").length() > 50) {
				errors.add(new DataColumnLengthException(lineNumber, headers.get("lastnameMaternalContractor"),
						data.get("lastnameMaternalContractor").length(), 50));
			}

			if (data.get("firstnameContractor").length() > 50) {
				errors.add(new DataColumnLengthException(lineNumber, headers.get("firstnameContractor"),
						data.get("firstnameContractor").length(), 50));
			}

			if (data.get("nuicInsured").length() > 8) {
				errors.add(new DataColumnLengthException(lineNumber, headers.get("nuicInsured"),
						data.get("nuicInsured").length(), 8));
			}

			if (data.get("lastnamePaternalInsured").length() > 50) {
				errors.add(new DataColumnLengthException(lineNumber, headers.get("lastnamePaternalInsured"),
						data.get("lastnamePaternalInsured").length(), 50));
			}

			if (data.get("lastnameMaternalInsured").length() > 50) {
				errors.add(new DataColumnLengthException(lineNumber, headers.get("lastnameMaternalInsured"),
						data.get("lastnameMaternalInsured").length(), 50));
			}

			if (data.get("firstnameInsured").length() > 50) {
				errors.add(new DataColumnLengthException(lineNumber, headers.get("firstnameInsured"),
						data.get("firstnameInsured").length(), 50));
			}

			if (data.get("phone1").length() > 9) {
				errors.add(new DataColumnLengthException(lineNumber, headers.get("phone1"), data.get("phone1").length(),
						9));
			}

			if (data.get("phone2").length() > 9) {
				errors.add(new DataColumnLengthException(lineNumber, headers.get("phone2"), data.get("phone2").length(),
						9));
			}

			if (data.get("mail").length() > 45) {
				errors.add(
						new DataColumnLengthException(lineNumber, headers.get("mail"), data.get("mail").length(), 45));
			}

			if (data.get("department").length() > 20) {
				errors.add(new DataColumnLengthException(lineNumber, headers.get("department"),
						data.get("department").length(), 20));
			}

			if (data.get("province").length() > 20) {
				errors.add(new DataColumnLengthException(lineNumber, headers.get("province"),
						data.get("province").length(), 20));
			}

			if (data.get("district").length() > 40) {
				errors.add(new DataColumnLengthException(lineNumber, headers.get("district"),
						data.get("district").length(), 40));
			}

			if (data.get("address").length() > 150) {
				errors.add(new DataColumnLengthException(lineNumber, headers.get("address"),
						data.get("address").length(), 150));
			}

			if (data.get("channelOfSale").length() > 15) {
				errors.add(new DataColumnLengthException(lineNumber, headers.get("channelOfSale"),
						data.get("channelOfSale").length(), 15));
			}

			if (data.get("placeOfSale").length() > 25) {
				errors.add(new DataColumnLengthException(lineNumber, headers.get("placeOfSale"),
						data.get("placeOfSale").length(), 25));
			}

			if (data.get("vendorCode").length() > 10) {
				errors.add(new DataColumnLengthException(lineNumber, headers.get("vendorCode"),
						data.get("vendorCode").length(), 10));
			}

			if (data.get("vendorName").length() > 35) {
				errors.add(new DataColumnLengthException(lineNumber, headers.get("vendorName"),
						data.get("vendorName").length(), 35));
			}

			if (data.get("policyNumber").length() > 10) {
				errors.add(new DataColumnLengthException(lineNumber, headers.get("policyNumber"),
						data.get("policyNumber").length(), 10));
			}

			if (data.get("certificateNumber").length() > 10) {
				errors.add(new DataColumnLengthException(lineNumber, headers.get("certificateNumber"),
						data.get("certificateNumber").length(), 10));
			}

			if (data.get("proposalNumber").length() > 10) {
				errors.add(new DataColumnLengthException(lineNumber, headers.get("proposalNumber"),
						data.get("proposalNumber").length(), 10));
			}

			if (data.get("commercialCode").length() > 10) {
				errors.add(new DataColumnLengthException(lineNumber, headers.get("commercialCode"),
						data.get("commercialCode").length(), 10));
			}

			if (data.get("product").length() > 45) {
				errors.add(new DataColumnLengthException(lineNumber, headers.get("product"),
						data.get("product").length(), 45));
			}

			if (data.get("productDescription").length() > 45) {
				errors.add(new DataColumnLengthException(lineNumber, headers.get("productDescription"),
						data.get("productDescription").length(), 45));
			}

			if (data.get("collectionPeriod").length() > 45) {
				errors.add(new DataColumnLengthException(lineNumber, headers.get("collectionPeriod"),
						data.get("collectionPeriod").length(), 45));
			}

			if (data.get("collectionType").length() > 15) {
				errors.add(new DataColumnLengthException(lineNumber, headers.get("collectionType"),
						data.get("collectionType").length(), 15));
			}

			if (data.get("paymentMethod").length() > 10) {
				errors.add(new DataColumnLengthException(lineNumber, headers.get("paymentMethod"),
						data.get("paymentMethod").length(), 10));
			}

			if (data.get("auditUser").length() > 15) {
				errors.add(new DataColumnLengthException(lineNumber, headers.get("auditUser"),
						data.get("auditUser").length(), 15));
			}

			if (data.get("downUser").length() > 15) {
				errors.add(new DataColumnLengthException(lineNumber, headers.get("downUser"),
						data.get("downUser").length(), 15));
			}

			if (data.get("downChannel").length() > 15) {
				errors.add(new DataColumnLengthException(lineNumber, headers.get("downChannel"),
						data.get("downChannel").length(), 15));
			}

			if (data.get("downReason").length() > 30) {
				errors.add(new DataColumnLengthException(lineNumber, headers.get("downReason"),
						data.get("downReason").length(), 30));
			}

			if (data.get("downObservation").length() > 2500) {
				errors.add(new DataColumnLengthException(lineNumber, headers.get("downObservation"),
						data.get("downObservation").length(), 2500));
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
				errors.add(new DataColumnDateException(lineNumber, headers.get("creditCardExpirationDate"), "mm/yyyy"));

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
				errors.add(new DataColumnDateException(lineNumber, headers.get("dateOfSale"), "dd/mm/yyyy"));
			}

			try {
				Long.parseLong(data.get("commercialCode"));
			} catch (NumberFormatException e) {
				errors.add(new DataColumnNumberException(lineNumber, headers.get("commercialCode")));
			}

			try {
				BigDecimal insurancePremium = new BigDecimal(data.get("insurancePremium"));
				if (insurancePremium.scale() > 2) {
					errors.add(new DataColumnDecimalException(lineNumber, headers.get("insurancePremium"), 2));
				}
			} catch (NumberFormatException e) {
				errors.add(new DataColumnNumberException(lineNumber, headers.get("insurancePremium")));
			}

			try {
				sdf1.parse(data.get("auditDate"));
			} catch (ParseException e) {
				errors.add(new DataColumnDateException(lineNumber, headers.get("auditDate"), "dd/mm/yyyy"));
			}

			try {
				if (data.get("stateDate").length() > 0) {
					sdf1.parse(data.get("stateDate"));
				}
			} catch (ParseException e) {
				errors.add(new DataColumnDateException(lineNumber, headers.get("stateDate"), "dd/mm/yyyy"));
			}

			try {
				if (data.get("creditCardUpdatedAt").length() > 0) {
					sdf1.parse(data.get("creditCardUpdatedAt"));
				}

			} catch (ParseException e) {
				errors.add(new DataColumnDateException(lineNumber, headers.get("creditCardUpdatedAt"), "dd/mm/yyyy"));
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
				if (saleState == null) {
					errors.add(new DataSaleStateNotFoundException(lineNumber, headers.get("state"), data.get("state")));
				}

				// VALIDATE COMMERCIAL CODE
				Commerce commercialCodeObject = null;
				for (Commerce commerce : commerces) {
					if (data.get("commercialCode").equals(commerce.getCode())) {
						commercialCodeObject = commerce;
					}
				}
				if (commercialCodeObject == null) {
					errors.add(new DataCommerceCodeException(lineNumber, headers.get("commercialCode"),
							data.get("commercialCode")));
				} else {

					// VALIDATE PRODUCT
					if (!data.get("product").equals(commercialCodeObject.getProduct().getName())) {
						errors.add(
								new DataSaleProductException(lineNumber, headers.get("product"), data.get("product")));
					}

					// VALIDATE PAYMENTMETHOS
					if (!data.get("paymentMethod").equals(commercialCodeObject.getPaymentMethod().getName())) {
						errors.add(new DataSalePaymentMethodException(lineNumber, headers.get("paymentMethod"),
								data.get("paymentMethod")));
					}

				}

				// VALIDATE IF EXIST
				Integer nuicInsured = Integer.parseInt(data.get("nuicInsured"));
				Date dateOfSale = sdf1.parse(data.get("dateOfSale"));
				Long saleId = saleService.findByNuicInsuredAndDateOfSale(nuicInsured, dateOfSale);
				if (saleId != null && saleId > 0) {
					errors.add(new DataSaleDuplicateException(lineNumber, headers.get("nuicInsured"),
							headers.get("dateOfSale"), data.get("nuicInsured"), data.get("dateOfSale")));
				}

				lineNumber++;

			}

			System.out.println("errors:" + errors.size());

		} catch (Exception e) {
			e.printStackTrace();
			errors.add(new ServiceException());

		}

	}

	public void createSales() {

		System.out.println("ingreso a createSales");

		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdf2 = new SimpleDateFormat("MM/yyyy");

		/*
		 * Integer salesCreated = 0; Integer salesNotCreated = 0;
		 */

		SessionBean sessionBean = (SessionBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
				.get("sessionBean");

		User user = new User();
		user.setId(sessionBean.getUser().getId());

		Date current = new Date();

		Integer lineNumber = 1;

		for (Map<String, String> data : dataList) {

			System.out.println("lineNumber:" + lineNumber);

			try {

				// / SE CREA LA VENTA
				Sale sale = new Sale();
				sale.setAccountNumber(Long.parseLong(data.get("accountNumber")));

				sale.setAuditDate(sdf1.parse(data.get("auditDate")));
				sale.setAuditUser(data.get("auditUser"));

				//////
				CreditCard creditCard = new CreditCard();
				creditCard.setExpirationDate(data.get("creditCardExpirationDate").length() > 0
						? sdf2.parse(data.get("creditCardExpirationDate")) : null);
				creditCard.setDaysOfDefault(data.get("creditCardDaysOfDefault").length() > 0
						? Integer.parseInt(data.get("creditCardDaysOfDefault")) : null);
				creditCard.setNumber(data.get("creditCardNumber").length() > 0
						? Long.parseLong(data.get("creditCardNumber")) : null);
				creditCard.setState(data.get("creditCardState"));

				creditCard.setUpdateDate(!data.get("creditCardUpdatedAt").equals("")
						? sdf1.parse(data.get("creditCardUpdatedAt")) : null);

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
				sale.setInsurancePremium(new BigDecimal(data.get("insurancePremium")));
				sale.setLastnameMaternalContractor(data.get("lastnameMaternalContractor"));
				sale.setLastnameMaternalInsured(data.get("lastnameMaternalInsured"));
				sale.setLastnamePaternalContractor(data.get("lastnamePaternalContractor"));
				sale.setLastnamePaternalInsured(data.get("lastnamePaternalInsured"));
				sale.setFirstnameInsured(data.get("firstnameInsured"));
				sale.setNuicContractor(
						data.get("nuicContractor").length() > 0 ? Integer.parseInt(data.get("nuicContractor")) : null);
				sale.setNuicInsured(Integer.parseInt(data.get("nuicInsured")));
				sale.setFirstnameContractor(data.get("firstnameContractor"));
				sale.setPhone1(data.get("phone1").length() > 0 ? Integer.parseInt(data.get("phone1")) : null);
				sale.setPhone2(data.get("phone2").length() > 0 ? Integer.parseInt(data.get("phone2")) : null);
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
				payer.setLastnameMaternalResponsible(data.get("lastnameMaternalResponsible"));
				payer.setLastnamePaternalResponsible(data.get("lastnamePaternalResponsible"));
				payer.setMail(data.get("mail"));
				payer.setFirstnameResponsible(data.get("firstnameResponsible"));
				payer.setNuicResponsible(data.get("nuicResponsible").length() > 0
						? Integer.parseInt(data.get("nuicResponsible")) : null);
				payer.setProvince(data.get("province"));

				payer.setCreatedAt(current);
				payer.setCreatedBy(user);

				sale.setPayer(payer);

				////////////////
				SaleState saleState = new SaleState();
				saleState.setDate(!data.get("stateDate").equals("") ? sdf1.parse(data.get("stateDate")) : null);

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
					if (data.get("commercialCode").equals(commerce.getCode())) {
						commercialCodeObject = commerce;
					}
				}
				sale.setCommerce(commercialCodeObject);

				// CAMPOS NUEVOS
				sale.setVirtualNotifications((short) 0);
				sale.setPhysicalNotifications((short) 0);

				sale.setCreatedBy(user);
				sale.setCreatedAt(current);

				saleService.add(sale);

			} catch (Exception e) {
				e.printStackTrace();
				errors.add(new DataSaleCreateException(lineNumber, e.getMessage()));

			}
			progress = (lineNumber * 100) / dataList.size();
			lineNumber++;

		}

		

	}

	/*private String getFilename(Part part) {
		for (String cd : part.getHeader("content-disposition").split(";")) {
			if (cd.trim().startsWith("filename")) {
				String filename = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
				return filename.substring(filename.lastIndexOf('/') + 1).substring(filename.lastIndexOf('\\') + 1); // MSIE
																													// fix.
			}
		}
		return null;
	}*/

	//public void handleFileUpload(/*AjaxBehaviorEvent event*/) {
	/*System.out.println("file size: " + file.getSize());
	System.out.println("file type: " + file.getContentType());
	System.out.println("file info: " + file.getHeader("Content-Disposition"));*/
	public void handleFileUpload(FileUploadEvent event) {
			
		System.out.println(event.getFile().getFileName());
		
		file = event.getFile();
				

		try {

			//String fileName = getFilename(file);

			//System.out.println("nombre del archivo: " + fileName);
			
			SessionBean sessionBean = (SessionBean) FacesContext
					.getCurrentInstance().getExternalContext().getSessionMap()
					.get("sessionBean");
			
			if (sessionBean.getBank()==null ) {
				Exception e = new BankNotSelectedException();
				facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
			}else{
				if (file != null && file.getFileName().length() > 0) {

					if (file.getFileName().endsWith(".csv") || file.getFileName().endsWith(".CSV")) {
						System.out.println("file NO es nulo:" + file.getFileName());

						getData();
						if (dataList == null && dataList.size() == 0) {
							Exception e = new FileRowsZeroException();
							facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
						} else if (errors != null && errors.size() > 0) {
							dataList=null;
							for (Exception e : errors) {
								facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
							}
						} else {
							validateDuplicates();
							if (errors != null && errors.size() > 0) {
								dataList=null;
								for (Exception e : errors) {
									facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
								}
							} else {
								validateDataNull();
								if (errors != null && errors.size() > 0) {
									dataList=null;
									for (Exception e : errors) {
										facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
									}
								} else {
									validateDataSize();
									if (errors != null && errors.size() > 0) {
										dataList=null;
										for (Exception e : errors) {
											facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
										}
									} else {
										validateDataType();
										if (errors != null && errors.size() > 0) {
											for (Exception e : errors) {
												dataList=null;
												facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
											}
										} else {
											validateData();
											if (errors != null && errors.size() > 0) {
												dataList=null;
												for (Exception e : errors) {
													facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
															e.getMessage());
												}
											} else {
												//facesUtil.sendConfirmMessage("", "Existen "+dataList.size()+" ventas en el archivo "+getFilename(file));
												facesUtil.sendConfirmMessage("", "Existen "+dataList.size()+" ventas en el archivo "+file.getFileName());
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
			}
			

			

		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());

		}

	}

	public void load() {
		try {


			if (dataList == null && dataList.size() == 0) {
				Exception e = new FileRowsZeroException();
				facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
			} else if (errors != null && errors.size() > 0) {
				dataList=null;
				for (Exception e : errors) {
					facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
				}
			} else {
				validateDuplicates();
				if (errors != null && errors.size() > 0) {
					dataList=null;
					for (Exception e : errors) {
						facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
					}
				} else {
					validateDataNull();
					if (errors != null && errors.size() > 0) {
						dataList=null;
						for (Exception e : errors) {
							facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
						}
					} else {
						validateDataSize();
						if (errors != null && errors.size() > 0) {
							dataList=null;
							for (Exception e : errors) {
								facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
							}
						} else {
							validateDataType();
							if (errors != null && errors.size() > 0) {
								dataList=null;
								for (Exception e : errors) {
									facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
								}
							} else {
								validateData();
								if (errors != null && errors.size() > 0) {
									dataList=null;
									for (Exception e : errors) {
										facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
									}
								} else {
									createSales();
									
									//
									
									if (errors != null && errors.size() > 0) {
										dataList=null;
										for (Exception e : errors) {
											facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
										}
									} else {
										facesUtil.sendConfirmMessage("","Se crearon "+dataList.size()+" ventas.");
										dataList=null;
										progress=0;
									}
									
									
								}
							}
						}

					}
				}

			}

			/*
			 * } else { throw new FileExtensionException(); }
			 * 
			 * } else { throw new FileNotFoundException(); }
			 */

		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());

		}
		//return null;

	}
	

	public String initialize() {
		
		System.out.println("inicializando SearchSaleController");

		try {
			
			SessionBean sessionBean = (SessionBean) FacesContext
					.getCurrentInstance().getExternalContext().getSessionMap()
					.get("sessionBean");
			
			if (sessionBean!=null && sessionBean.getUser()!=null && sessionBean.getUser().getId()>0) {
				
				if (!sessionBean.getUser().getUserType().equals(UserTypeEnum.ADMIN)
						&& !sessionBean.getUser().getUserType().equals(UserTypeEnum.AGENT)) {
					throw new UserPermissionNotFoundException();
				}
				
				if (sessionBean.getBank()!=null && sessionBean.getBank().getId()!=null) {
					Short bankId = sessionBean.getBank().getId();
					commerces = commerceService.findByBank(bankId);
				}
				
				List<Bank> banksEntity = bankService.getAll();
				banks = new ArrayList<SelectItem>();
				for (Bank bank : banksEntity) {
					SelectItem item = new SelectItem();
					item.setValue(bank.getId().toString());
					item.setLabel(bank.getName());
					banks.add(item);
				}

				List<Product> productsEntity = productService.getAll();
				products = new ArrayList<SelectItem>();
				for (Product product : productsEntity) {
					SelectItem item = new SelectItem();
					item.setValue(product.getId().toString());
					item.setLabel(product.getName());
					products.add(item);
				}
				
				saleStates = new ArrayList<SelectItem>();
				for (SaleStateEnum saleStateEnum : SaleStateEnum.values()) {
					SelectItem item = new SelectItem();
					item.setValue(saleStateEnum.getId());
					item.setLabel(saleStateEnum.getName());
					saleStates.add(item);
				}
				
				searchTypeSelected="";
				
				System.out.println("searchTypeSelected:"+searchTypeSelected);
				
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

	public void onChangeSearchType() {
		try {
			// if (fromRequest) {
			searchTypeSelected = FacesContext.getCurrentInstance()
					.getExternalContext().getRequestParameterMap()
					.get("form:searchType_input");
			// }

			System.out.println("onChangeSearchBy" + searchTypeSelected);

			searchByContractorRendered = false;
			searchByInsuredRendered = false;
			searchByResponsibleRendered = false;
			personTypeSelected = null;

			if (searchTypeSelected.equals("creditCard")) {
				searchByCreditCardNumberRendered = true;
				searchByDocumentNumberResponsibleRendered = false;
				searchByNamesRendered = false;
				searchByDateSaleRendered = false;
			} else if (searchTypeSelected.equals("dni")) {
				searchByCreditCardNumberRendered = false;
				searchByDocumentNumberResponsibleRendered = true;
				searchByNamesRendered = false;
				searchByDateSaleRendered = false;
			} else if (searchTypeSelected.equals("saleData")) {
				searchByCreditCardNumberRendered = false;
				searchByDocumentNumberResponsibleRendered = false;
				searchByNamesRendered = false;
				searchByDateSaleRendered = true;
			} else if (searchTypeSelected.equals("personalData")) {
				searchByCreditCardNumberRendered = false;
				searchByDocumentNumberResponsibleRendered = false;
				searchByNamesRendered = true;
				searchByDateSaleRendered = false;
			} else {
				searchByCreditCardNumberRendered = false;
				searchByDocumentNumberResponsibleRendered = false;
				searchByNamesRendered = false;
				searchByDateSaleRendered = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
					e.getMessage());
		}
	}

	public void onChangePersonType() {
		try {
			// if (fromRequest) {
			personTypeSelected = FacesContext.getCurrentInstance()
					.getExternalContext().getRequestParameterMap()
					.get("form:personType_input");
			// }

			System.out.println("onChangeSearchBy" + searchTypeSelected);
			if (personTypeSelected.equals("contractor")) {
				searchByContractorRendered = true;
				searchByInsuredRendered = false;
				searchByResponsibleRendered = false;
			} else if (personTypeSelected.equals("insured")) {
				searchByContractorRendered = false;
				searchByInsuredRendered = true;
				searchByResponsibleRendered = false;
			} else if (personTypeSelected.equals("responsible")) {
				searchByContractorRendered = false;
				searchByInsuredRendered = false;
				searchByResponsibleRendered = true;
			} else {
				searchByContractorRendered = false;
				searchByInsuredRendered = false;
				searchByResponsibleRendered = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
					e.getMessage());
		}
	}

	public void search() {

		try {
			
			saleSelected=null;

			if (searchTypeSelected.equals("creditCard")) {
				Long creditCardNumberLong = Long.parseLong(creditCardNumber);
				sales = saleService
						.findSalesByCreditCardNumber(creditCardNumberLong);
			} else if (searchTypeSelected.equals("dni")) {
				Long nuicResponsibleLong = Long.parseLong(nuicResponsible);
				sales = saleService
						.findSalesByNuicResponsible(nuicResponsibleLong);
			} else if (searchTypeSelected.equals("saleData")) {
				Short productId = null;
				if (productSelected != null && productSelected.length() > 0) {
					productId = Short.parseShort(productSelected);
				}
				Short bankId = null;
				if (bankSelected != null && bankSelected.length() > 0) {
					bankId = Short.parseShort(bankSelected);
				}
				SaleStateEnum saleState = null;
				if (saleStateSelected != null && saleStateSelected.length() > 0) {
					saleState = SaleStateEnum.findById(Short.parseShort(saleStateSelected));
				}
				sales = saleService.findSalesBySaleData(dateOfSaleStarted,
						dateOfSaleEnded, affiliationDate, bankId, productId,
						saleState);

			} else if (searchTypeSelected.equals("personalData")) {
				if (personTypeSelected.equals("contractor")) {

					if ((nuicContractor != null && nuicContractor.length() > 0)
							|| (firstnameContractor != null && firstnameContractor
									.length() > 0)
							|| (lastnamePaternalContractor != null && lastnamePaternalContractor
									.length() > 0)
							|| (lastnameMaternalContractor != null && lastnameMaternalContractor
									.length() > 0)) {

						Long nuicContractorLong = null;
						if (nuicContractor != null
								&& nuicContractor.length() > 0) {
							nuicContractorLong = Long.parseLong(nuicContractor);
						}

						sales = saleService.findSalesByNamesContractor(
								nuicContractorLong, firstnameContractor,
								lastnamePaternalContractor,
								lastnameMaternalContractor);

					} else {
						FacesMessage msg = new FacesMessage(
								"Debe ingresar al menos un dato");
						msg.setSeverity(FacesMessage.SEVERITY_ERROR);
						FacesContext.getCurrentInstance().addMessage(null, msg);
					}

				} else if (personTypeSelected.equals("insured")) {

					if ((nuicInsured != null && nuicInsured.length() > 0)
							|| (firstnameInsured != null && firstnameInsured
									.length() > 0)
							|| (lastnamePaternalInsured != null && lastnamePaternalInsured
									.length() > 0)
							|| (lastnameMaternalInsured != null && lastnameMaternalInsured
									.length() > 0)) {

						Long nuicInsuredLong = null;
						if (nuicInsured != null && nuicInsured.length() > 0) {
							nuicInsuredLong = Long.parseLong(nuicInsured);
						}

						sales = saleService.findSalesByNamesInsured(
								nuicInsuredLong, firstnameInsured,
								lastnamePaternalInsured,
								lastnameMaternalInsured);

					} else {
						FacesMessage msg = new FacesMessage(
								"Debe ingresar al menos un dato");
						msg.setSeverity(FacesMessage.SEVERITY_ERROR);
						FacesContext.getCurrentInstance().addMessage(null, msg);
					}

				} else if (personTypeSelected.equals("responsible")) {

					if ((nuicResponsible != null && nuicResponsible.length() > 0)
							|| (firstnameResponsible != null && firstnameResponsible
									.length() > 0)
							|| (lastnamePaternalResponsible != null && lastnamePaternalResponsible
									.length() > 0)
							|| (lastnameMaternalResponsible != null && lastnameMaternalResponsible
									.length() > 0)) {

						Long nuicResponsibleLong = null;
						if (nuicResponsible != null
								&& nuicResponsible.length() > 0) {
							nuicResponsibleLong = Long
									.parseLong(nuicResponsible);
						}

						sales = saleService.findSalesByNamesResponsible(
								nuicResponsibleLong, firstnameResponsible,
								lastnamePaternalResponsible,
								lastnameMaternalResponsible);

					} else {
						FacesMessage msg = new FacesMessage(
								"Debe ingresar al menos un dato");
						msg.setSeverity(FacesMessage.SEVERITY_ERROR);
						FacesContext.getCurrentInstance().addMessage(null, msg);
					}
				}
				
				

			}
			
			

		} catch (Exception e) {

			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
					e.getMessage());
		}

	}

	public void exportExcel() throws IOException {

		try {

			search();

			SimpleDateFormat sdf1 = new SimpleDateFormat("MM/yyyy");
			SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat sdf3 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

			XSSFWorkbook wb = new XSSFWorkbook();
			XSSFSheet sheet = wb.createSheet();
			XSSFRow row = sheet.createRow(0);

			row.createCell(0).setCellValue("COD UNICO");
			row.createCell(1).setCellValue("TIPO DOC");
			row.createCell(2).setCellValue("NUIC RESP PAGO");
			row.createCell(3).setCellValue("AP PAT RESP PAGO");
			row.createCell(4).setCellValue("AP MAT RESP PAGO");
			row.createCell(5).setCellValue("NOMBRES RESP PAGO");
			row.createCell(6).setCellValue("N° TARJETA DE CREDITO");
			row.createCell(7).setCellValue("N° DE CUENTA");
			row.createCell(8).setCellValue("FECHA VENC TARJETA");
			row.createCell(9).setCellValue("DIAS MORA");
			row.createCell(10).setCellValue("NUIC CONTRATANTE");
			row.createCell(11).setCellValue("AP PAT CONTRATANTE");
			row.createCell(12).setCellValue("AP MAT CONTRATANTE");
			row.createCell(13).setCellValue("NOMBRES CONTRATANTE");
			row.createCell(14).setCellValue("NUIC ASEGURADO");
			row.createCell(15).setCellValue("AP PAT ASEGURADO");
			row.createCell(16).setCellValue("AP MAT ASEGURADO");
			row.createCell(17).setCellValue("NOMBRES ASEGURADO");
			row.createCell(18).setCellValue("TELEFONO 1");
			row.createCell(19).setCellValue("TELEFONO 2");
			row.createCell(20).setCellValue("E-MAIL");
			row.createCell(21).setCellValue("DEPARTAMENTO");
			row.createCell(22).setCellValue("PROVINCIA");
			row.createCell(23).setCellValue("DISTRITO");
			row.createCell(24).setCellValue("DIRECCION");
			row.createCell(25).setCellValue("FECHA DE VENTA");
			row.createCell(26).setCellValue("CANAL DE VENTA");
			row.createCell(27).setCellValue("LUGAR DE VENTA");
			row.createCell(28).setCellValue("COD DE VENDEDOR");
			row.createCell(29).setCellValue("NOMBRE DE VENDEDOR");
			row.createCell(30).setCellValue("# DE POLIZA");
			row.createCell(31).setCellValue("# CERTIFICADO");
			row.createCell(32).setCellValue("# PROPUESTA");
			row.createCell(33).setCellValue("CODIGO DE COMERCIO");
			row.createCell(34).setCellValue("PRODUCTO");
			row.createCell(35).setCellValue("DESCRIPCION DEL PRODUCTO");
			row.createCell(36).setCellValue("PERIODO DE COBRO");
			row.createCell(37).setCellValue("TIPO DE COBRO");
			row.createCell(38).setCellValue("MEDIO DE PAGO");
			row.createCell(39).setCellValue("PRIMA");
			row.createCell(40).setCellValue("FECHA DE AUDITORIA");
			row.createCell(41).setCellValue("USUARIO DE AUDITORIA");
			row.createCell(42).setCellValue("ESTADO");
			row.createCell(43).setCellValue("FECHA ESTADO");
			row.createCell(44).setCellValue("USUARIO BAJA");
			row.createCell(45).setCellValue("CANAL BAJA");
			row.createCell(46).setCellValue("MOTIVO BAJA");
			row.createCell(47).setCellValue("OBSERVACION BAJA");
			row.createCell(48).setCellValue("FECHA ACT TC");

			row.createCell(49).setCellValue("BANCO");
			row.createCell(50).setCellValue("FECHA CREACION");
			row.createCell(51).setCellValue("USUARIO CREACION");

			for (int i = 0; i < sales.size(); i++) {
				Sale sale = sales.get(i);
				XSSFRow rowBody = sheet.createRow(i + 1);

				rowBody.createCell(0).setCellValue(sale.getCode());
				rowBody.createCell(1).setCellValue(sale.getDocumentType());
				rowBody.createCell(2).setCellValue(sale.getPayer().getNuicResponsible());
				rowBody.createCell(3).setCellValue(
						sale.getPayer().getLastnamePaternalResponsible());
				rowBody.createCell(4).setCellValue(
						sale.getPayer().getLastnameMaternalResponsible());
				rowBody.createCell(5).setCellValue(
						sale.getPayer().getFirstnameResponsible());
				// System.out.println("credi card number: "+sale.getCreditCardNumber());
				rowBody.createCell(6).setCellValue(
						sale.getCreditCard().getNumber() + "");
				rowBody.createCell(7)
						.setCellValue(sale.getAccountNumber() + "");
				rowBody.createCell(8).setCellValue(
						sale.getCreditCard().getExpirationDate() != null ? sdf1
								.format(sale.getCreditCard().getExpirationDate())
								: null);
				rowBody.createCell(9).setCellValue(""/*
						sale.getCreditCard().getDaysOfDefault() != null
						?sale.getCreditCard().getDaysOfDefault():null*/);
				
				rowBody.createCell(10).setCellValue(""/*sale.getNuicContractor()*/);
				rowBody.createCell(11).setCellValue(
						sale.getLastnamePaternalContractor());
				rowBody.createCell(12).setCellValue(
						sale.getLastnameMaternalContractor());
				rowBody.createCell(13).setCellValue(
						sale.getFirstnameContractor());
				rowBody.createCell(14).setCellValue(sale.getNuicInsured());
				rowBody.createCell(15).setCellValue(
						sale.getLastnamePaternalInsured());
				rowBody.createCell(16).setCellValue(
						sale.getLastnameMaternalInsured());
				rowBody.createCell(17).setCellValue(sale.getFirstnameInsured());
				rowBody.createCell(18).setCellValue(sale.getPhone1());
				rowBody.createCell(19).setCellValue(sale.getPhone2());
				rowBody.createCell(20).setCellValue(sale.getPayer().getMail());
				rowBody.createCell(21).setCellValue(sale.getPayer().getDepartment());
				rowBody.createCell(22).setCellValue(sale.getPayer().getProvince());
				rowBody.createCell(23).setCellValue(sale.getPayer().getDistrict());
				rowBody.createCell(24).setCellValue(sale.getPayer().getAddress());
				rowBody.createCell(25).setCellValue(
						sdf2.format(sale.getDateOfSale()));
				rowBody.createCell(26).setCellValue(sale.getChannelOfSale());
				rowBody.createCell(27).setCellValue(sale.getPlaceOfSale());
				rowBody.createCell(28).setCellValue(sale.getVendorCode());
				rowBody.createCell(29).setCellValue(sale.getVendorName());
				rowBody.createCell(30).setCellValue(sale.getPolicyNumber());
				rowBody.createCell(31)
						.setCellValue(sale.getCertificateNumber());
				rowBody.createCell(32).setCellValue(sale.getProposalNumber());
				rowBody.createCell(33).setCellValue(sale.getCommerce().getCode());
				rowBody.createCell(34)
						.setCellValue(sale.getCommerce().getProduct().getName());
				rowBody.createCell(35).setCellValue(
						sale.getProductDescription());
				rowBody.createCell(36).setCellValue(sale.getCollectionPeriod());
				rowBody.createCell(37).setCellValue(sale.getCollectionType());
				rowBody.createCell(38).setCellValue(
						sale.getCommerce().getPaymentMethod().getName());
				rowBody.createCell(39).setCellValue(
						sale.getInsurancePremium().doubleValue());
				rowBody.createCell(40).setCellValue(
						sdf2.format(sale.getAuditDate()));
				rowBody.createCell(41).setCellValue(sale.getAuditUser());
				rowBody.createCell(42).setCellValue(sale.getSaleState().getState().getName());
				rowBody.createCell(43).setCellValue(
						sale.getSaleState().getDate() != null ? sdf2.format(sale
								.getSaleState().getDate()) : "");
				rowBody.createCell(44).setCellValue(sale.getSaleState().getDownUser());
				rowBody.createCell(45).setCellValue(sale.getSaleState().getDownChannel());
				rowBody.createCell(46).setCellValue(sale.getSaleState().getDownReason());
				rowBody.createCell(47).setCellValue(sale.getSaleState().getDownObservation());
				rowBody.createCell(48).setCellValue(
						sale.getCreditCard().getUpdateDate() != null ? sdf2
								.format(sale.getCreditCard().getUpdateDate()) : "");

				rowBody.createCell(49).setCellValue(sale.getCommerce().getBank().getName());
				rowBody.createCell(50).setCellValue(
						sdf3.format(sale.getCreatedAt()));
				rowBody.createCell(51).setCellValue(
						sale.getCreatedBy().getUsername());

			}

			/*
			 * HSSFWorkbook workbook = new HSSFWorkbook(); HSSFSheet sheet =
			 * workbook.createSheet(); HSSFRow row = sheet.createRow(0);
			 * HSSFCell cell = row.createCell(0); cell.setCellValue(0.0);
			 */

			FacesContext facesContext = FacesContext.getCurrentInstance();
			ExternalContext externalContext = facesContext.getExternalContext();
			// externalContext.setResponseContentType("application/vnd.ms-excel");
			externalContext
					.setResponseContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			externalContext.setResponseHeader("Content-Disposition",
					"attachment; filename=\"ventas.xlsx\"");

			wb.write(externalContext.getResponseOutputStream());
			facesContext.responseComplete();

		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
					e.getMessage());
		}

	}

	

	
	
	
	

	public List<Sale> getSales() {
		return sales;
	}

	public void setSales(List<Sale> sales) {
		this.sales = sales;
	}

	public Date getDateOfSaleStarted() {
		return dateOfSaleStarted;
	}

	public void setDateOfSaleStarted(Date dateOfSaleStarted) {
		this.dateOfSaleStarted = dateOfSaleStarted;
	}

	public Date getDateOfSaleEnded() {
		return dateOfSaleEnded;
	}

	public void setDateOfSaleEnded(Date dateOfSaleEnded) {
		this.dateOfSaleEnded = dateOfSaleEnded;
	}

	public String getCreditCardNumber() {
		return creditCardNumber;
	}

	public void setCreditCardNumber(String creditCardNumber) {
		this.creditCardNumber = creditCardNumber;
	}

	public String getLastnamePaternalResponsible() {
		return lastnamePaternalResponsible;
	}

	public void setLastnamePaternalResponsible(
			String lastnamePaternalResponsible) {
		this.lastnamePaternalResponsible = lastnamePaternalResponsible;
	}

	public String getLastnameMaternalResponsible() {
		return lastnameMaternalResponsible;
	}

	public void setLastnameMaternalResponsible(
			String lastnameMaternalResponsible) {
		this.lastnameMaternalResponsible = lastnameMaternalResponsible;
	}

	public String getFirstnameResponsible() {
		return firstnameResponsible;
	}

	public void setFirstnameResponsible(String firstnameResponsible) {
		this.firstnameResponsible = firstnameResponsible;
	}

	public String getFirstnameContractor() {
		return firstnameContractor;
	}

	public void setFirstnameContractor(String firstnameContractor) {
		this.firstnameContractor = firstnameContractor;
	}

	public String getFirstnameInsured() {
		return firstnameInsured;
	}

	public void setFirstnameInsured(String firstnameInsured) {
		this.firstnameInsured = firstnameInsured;
	}

	public String getLastnamePaternalContractor() {
		return lastnamePaternalContractor;
	}

	public void setLastnamePaternalContractor(String lastnamePaternalContractor) {
		this.lastnamePaternalContractor = lastnamePaternalContractor;
	}

	public String getLastnameMaternalContractor() {
		return lastnameMaternalContractor;
	}

	public void setLastnameMaternalContractor(String lastnameMaternalContractor) {
		this.lastnameMaternalContractor = lastnameMaternalContractor;
	}

	public String getLastnamePaternalInsured() {
		return lastnamePaternalInsured;
	}

	public void setLastnamePaternalInsured(String lastnamePaternalInsured) {
		this.lastnamePaternalInsured = lastnamePaternalInsured;
	}

	public String getLastnameMaternalInsured() {
		return lastnameMaternalInsured;
	}

	public void setLastnameMaternalInsured(String lastnameMaternalInsured) {
		this.lastnameMaternalInsured = lastnameMaternalInsured;
	}

	public List<SelectItem> getBanks() {
		return banks;
	}

	public void setBanks(List<SelectItem> banks) {
		this.banks = banks;
	}

	public String getBankSelected() {
		return bankSelected;
	}

	public void setBankSelected(String bankSelected) {
		this.bankSelected = bankSelected;
	}

	public List<SelectItem> getProducts() {
		return products;
	}

	public void setProducts(List<SelectItem> products) {
		this.products = products;
	}

	public String getProductSelected() {
		return productSelected;
	}

	public void setProductSelected(String productSelected) {
		this.productSelected = productSelected;
	}

	public String getSearchTypeSelected() {
		return searchTypeSelected;
	}

	public void setSearchTypeSelected(String searchTypeSelected) {
		this.searchTypeSelected = searchTypeSelected;
	}

	public Boolean getSearchByCreditCardNumberRendered() {
		return searchByCreditCardNumberRendered;
	}

	public void setSearchByCreditCardNumberRendered(
			Boolean searchByCreditCardNumberRendered) {
		this.searchByCreditCardNumberRendered = searchByCreditCardNumberRendered;
	}

	public Boolean getSearchByDocumentNumberResponsibleRendered() {
		return searchByDocumentNumberResponsibleRendered;
	}

	public void setSearchByDocumentNumberResponsibleRendered(
			Boolean searchByDocumentNumberResponsibleRendered) {
		this.searchByDocumentNumberResponsibleRendered = searchByDocumentNumberResponsibleRendered;
	}

	public Boolean getSearchByNamesRendered() {
		return searchByNamesRendered;
	}

	public void setSearchByNamesRendered(Boolean searchByNamesRendered) {
		this.searchByNamesRendered = searchByNamesRendered;
	}

	public Boolean getSearchByDateSaleRendered() {
		return searchByDateSaleRendered;
	}

	public void setSearchByDateSaleRendered(Boolean searchByDateSaleRendered) {
		this.searchByDateSaleRendered = searchByDateSaleRendered;
	}

	public String getPersonTypeSelected() {
		return personTypeSelected;
	}

	public void setPersonTypeSelected(String personTypeSelected) {
		this.personTypeSelected = personTypeSelected;
	}

	public Boolean getSearchByContractorRendered() {
		return searchByContractorRendered;
	}

	public void setSearchByContractorRendered(Boolean searchByContractorRendered) {
		this.searchByContractorRendered = searchByContractorRendered;
	}

	public Boolean getSearchByInsuredRendered() {
		return searchByInsuredRendered;
	}

	public void setSearchByInsuredRendered(Boolean searchByInsuredRendered) {
		this.searchByInsuredRendered = searchByInsuredRendered;
	}

	public Boolean getSearchByResponsibleRendered() {
		return searchByResponsibleRendered;
	}

	public void setSearchByResponsibleRendered(
			Boolean searchByResponsibleRendered) {
		this.searchByResponsibleRendered = searchByResponsibleRendered;
	}

	public String getNuicResponsible() {
		return nuicResponsible;
	}

	public void setNuicResponsible(String nuicResponsible) {
		this.nuicResponsible = nuicResponsible;
	}

	public String getNuicContractor() {
		return nuicContractor;
	}

	public void setNuicContractor(String nuicContractor) {
		this.nuicContractor = nuicContractor;
	}

	public String getNuicInsured() {
		return nuicInsured;
	}

	public void setNuicInsured(String nuicInsured) {
		this.nuicInsured = nuicInsured;
	}

	

	public Sale getSaleSelected() {
		return saleSelected;
	}

	public void setSaleSelected(Sale saleSelected) {
		this.saleSelected = saleSelected;
	}

	public String getSaleStateSelected() {
		return saleStateSelected;
	}

	public void setSaleStateSelected(String saleStateSelected) {
		this.saleStateSelected = saleStateSelected;
	}

	

	public Date getAffiliationDate() {
		return affiliationDate;
	}

	public void setAffiliationDate(Date affiliationDate) {
		this.affiliationDate = affiliationDate;
	}


	public List<SelectItem> getSaleStates() {
		return saleStates;
	}

	public void setSaleStates(List<SelectItem> saleStates) {
		this.saleStates = saleStates;
	}

	

	public Integer getSalesCount() {
		return salesCount;
	}

	public void setSalesCount(Integer salesCount) {
		this.salesCount = salesCount;
	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	public List<Map<String, String>> getDataList() {
		return dataList;
	}

	public void setDataList(List<Map<String, String>> dataList) {
		this.dataList = dataList;
	}

	public Integer getProgress() {
		return progress;
	}

	public void setProgress(Integer progress) {
		this.progress = progress;
	}
	

}
