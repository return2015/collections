package com.returnsoft.collection.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import com.returnsoft.collection.entity.Bank;
import com.returnsoft.collection.entity.CollectionPeriod;
import com.returnsoft.collection.entity.CreditCard;
import com.returnsoft.collection.entity.Payer;
import com.returnsoft.collection.entity.Product;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.entity.SaleState;
import com.returnsoft.collection.entity.User;
import com.returnsoft.collection.enumeration.DocumentTypeEnum;
import com.returnsoft.collection.enumeration.SaleStateEnum;
import com.returnsoft.collection.exception.BankNotSelectedException;
import com.returnsoft.collection.exception.FileExtensionException;
import com.returnsoft.collection.exception.FileNotFoundException;
import com.returnsoft.collection.exception.FileRowsInvalidException;
import com.returnsoft.collection.exception.FileRowsZeroException;
import com.returnsoft.collection.exception.FormatException;
import com.returnsoft.collection.exception.MultipleErrorsException;
import com.returnsoft.collection.exception.NullException;
import com.returnsoft.collection.exception.OverflowException;
import com.returnsoft.collection.exception.SaleAlreadyExistException;
import com.returnsoft.collection.exception.SaleDuplicateException;
import com.returnsoft.collection.exception.UserLoggedNotFoundException;
import com.returnsoft.collection.service.CollectionPeriodService;
import com.returnsoft.collection.service.ProductService;
import com.returnsoft.collection.service.SaleService;
import com.returnsoft.collection.vo.SaleFile;
import com.returnsoft.generic.util.SessionBean;

@ManagedBean
@ViewScoped
public class LoadSalesController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -185393107214608269L;

	@Inject
	private SessionBean sessionBean;

	@EJB
	private CollectionPeriodService collectionPeriodService;

	@EJB
	private ProductService productService;

	@EJB
	private SaleService saleService;

	private UploadedFile file;

	private StreamedContent downloadFile;

	public LoadSalesController() {
		System.out.println("LoadSalesController");
		InputStream stream = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext())
				.getResourceAsStream("/resources/templates/tramas_ventas.xlsx");
		downloadFile = new DefaultStreamedContent(stream,
				"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "tramas_ventas.xlsx");
	}


	public void uploadFile() {

		System.out.println("uploadFile");

		try {

			if (sessionBean == null || sessionBean.getUser() == null || sessionBean.getUser().getId() == null) {
				throw new UserLoggedNotFoundException();
			}

			if (sessionBean.getBank() == null) {
				throw new BankNotSelectedException();
			}

			if (file == null) {
				throw new FileNotFoundException();
			}

			if (!file.getContentType().equals("text/plain")) {
				throw new FileExtensionException();
			}

			////////////////////
			// SE LEE ARCHIVO //
			////////////////////

			List<SaleFile> dataList = readFile(file);
			SaleFile headers = dataList.get(0);
			dataList.remove(0);

			if (dataList.size() == 0) {
				throw new FileRowsZeroException();
			}

			//////////////////////////
			// VALIDANDO DUPLICADOS //
			//////////////////////////

			Set<String> dataSet = new HashSet<String>();
			Set<Integer> errorSet = new HashSet<Integer>();
			int lineNumber = 2;

			for (SaleFile saleFile : dataList) {
				String dataString = saleFile.getNuicInsured() + saleFile.getDate() + saleFile.getProduct()
						+ saleFile.getBank() + saleFile.getCollectionPeriod();
				if (!dataSet.add(dataString)) {
					errorSet.add(lineNumber);
				}
				lineNumber++;
			}

			if (errorSet.size() > 0) {
				List<Exception> errors = new ArrayList<Exception>();
				for (Integer errorLineNumber : errorSet) {
					errors.add(new SaleDuplicateException(errorLineNumber));
				}
				throw new MultipleErrorsException(errors);
			}

			/////////////////////
			// VALIDANDO DATOS //
			/////////////////////

			lineNumber = 2;
			Bank bank = sessionBean.getBank();
			List<Sale> sales = new ArrayList<Sale>();
			List<Exception> errors = new ArrayList<Exception>();

			for (SaleFile saleFile : dataList) {
				try {
					Sale sale = validateSaleFile(saleFile, headers, lineNumber, bank);
					sales.add(sale);
				} catch (MultipleErrorsException e) {
					for (Exception exception : e.getErrors()) {
						errors.add(exception);
					}
				}
				lineNumber++;
			}

			if (errors.size() > 0) {
				throw new MultipleErrorsException(errors);
			}

			/////////////////////////////////////
			// SE ENVIAN LAS VENTAS AL SERVICE //
			/////////////////////////////////////

			User user = sessionBean.getUser();
			saleService.addSaleList(sales, headers, file.getFileName(), user);
			RequestContext.getCurrentInstance().closeDialog(null);

		} catch (Exception e) {
			e.printStackTrace();
			RequestContext.getCurrentInstance().closeDialog(e);
		}

	}

	private List<SaleFile> readFile(UploadedFile file) throws Exception {

		String strLine = null;
		Integer lineNumber = 1;
		SaleFile headers = new SaleFile();
		List<SaleFile> dataList = new ArrayList<SaleFile>();
		Integer FILE_ROWS = 49;

		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(file.getInputstream(), StandardCharsets.UTF_8));

			while ((strLine = br.readLine()) != null) {

				String[] values = strLine.split("\\|", -1);

				
				if (values.length != FILE_ROWS) {
					throw new FileRowsInvalidException(lineNumber,FILE_ROWS);
				}
				
				

				// SE LEE CABECERA
				if (lineNumber == 1) {

					headers.setDocumentType(values[0]);
					headers.setNuicResponsible(values[1]);
					headers.setLastnamePaternalResponsible(values[2]);
					headers.setLastnameMaternalResponsible(values[3]);
					headers.setFirstnameResponsible(values[4]);

					headers.setCreditCardNumber(values[5]);
					headers.setAccountNumber(values[6]);
					headers.setCreditCardExpirationDate(values[7]);
					headers.setCreditCardState(values[8]);
					headers.setCreditCardDaysOfDefault(values[9]);

					headers.setNuicContractor(values[10]);
					headers.setLastnamePaternalContractor(values[11]);
					headers.setLastnameMaternalContractor(values[12]);
					headers.setFirstnameContractor(values[13]);
					headers.setNuicInsured(values[14]);

					headers.setLastnamePaternalInsured(values[15]);
					headers.setLastnameMaternalInsured(values[16]);
					headers.setFirstnameInsured(values[17]);
					headers.setPhone1(values[18]);
					headers.setPhone2(values[19]);

					headers.setMail(values[20]);
					headers.setDepartment(values[21]);
					headers.setProvince(values[22]);
					headers.setDistrict(values[23]);
					headers.setAddress(values[24]);

					headers.setDate(values[25]);
					headers.setChannel(values[26]);
					headers.setPlace(values[27]);
					headers.setVendorCode(values[28]);
					headers.setVendorName(values[29]);

					headers.setPolicyNumber(values[30]);
					headers.setCertificateNumber(values[31]);
					headers.setProposalNumber(values[32]);
					headers.setCommercialCode(values[33]);
					headers.setProduct(values[34]);

					headers.setProductDescription(values[35]);
					headers.setCollectionPeriod(values[36]);
					headers.setCollectionType(values[37]);
					headers.setBank(values[38]);
					headers.setInsurancePremium(values[39]);

					headers.setAuditDate(values[40]);
					headers.setAuditUser(values[41]);
					headers.setState(values[42]);
					headers.setStateDate(values[43]);
					headers.setDownUser(values[44]);

					headers.setDownChannel(values[45]);
					headers.setDownReason(values[46]);
					headers.setDownObservation(values[47]);
					headers.setCreditCardUpdatedAt(values[48]);

					dataList.add(headers);

				} else {

					SaleFile saleFile = new SaleFile();
					saleFile.setDocumentType(values[0].trim());
					saleFile.setNuicResponsible(values[1].trim());
					saleFile.setLastnamePaternalResponsible(values[2].trim());
					saleFile.setLastnameMaternalResponsible(values[3].trim());
					saleFile.setFirstnameResponsible(values[4].trim());
					saleFile.setCreditCardNumber(values[5].trim());
					saleFile.setAccountNumber(values[6].trim());
					saleFile.setCreditCardExpirationDate(values[7].trim());
					saleFile.setCreditCardState(values[8].trim());
					saleFile.setCreditCardDaysOfDefault(values[9].trim());
					saleFile.setNuicContractor(values[10].trim());
					saleFile.setLastnamePaternalContractor(values[11].trim());
					saleFile.setLastnameMaternalContractor(values[12].trim());
					saleFile.setFirstnameContractor(values[13].trim());
					saleFile.setNuicInsured(values[14].trim());
					saleFile.setLastnamePaternalInsured(values[15].trim());
					saleFile.setLastnameMaternalInsured(values[16].trim());
					saleFile.setFirstnameInsured(values[17].trim());
					saleFile.setPhone1(values[18].trim());
					saleFile.setPhone2(values[19].trim());
					saleFile.setMail(values[20].trim());
					saleFile.setDepartment(values[21].trim());
					saleFile.setProvince(values[22].trim());
					saleFile.setDistrict(values[23].trim());
					saleFile.setAddress(values[24].trim());
					saleFile.setDate(values[25].trim());
					saleFile.setChannel(values[26].trim());
					saleFile.setPlace(values[27].trim());
					saleFile.setVendorCode(values[28].trim());
					saleFile.setVendorName(values[29].trim());
					saleFile.setPolicyNumber(values[30].trim());
					saleFile.setCertificateNumber(values[31].trim());
					saleFile.setProposalNumber(values[32].trim());
					saleFile.setCommercialCode(values[33].trim());
					saleFile.setProduct(values[34].trim());
					saleFile.setProductDescription(values[35].trim());
					saleFile.setCollectionPeriod(values[36].trim());
					saleFile.setCollectionType(values[37].trim());
					saleFile.setBank(values[38].trim());
					saleFile.setInsurancePremium(values[39].trim());
					saleFile.setAuditDate(values[40].trim());
					saleFile.setAuditUser(values[41].trim());
					saleFile.setState(values[42].trim());
					saleFile.setStateDate(values[43].trim());
					saleFile.setDownUser(values[44].trim());
					saleFile.setDownChannel(values[45].trim());
					saleFile.setDownReason(values[46].trim());
					saleFile.setDownObservation(values[47].trim());
					saleFile.setCreditCardUpdatedAt(values[48].trim());

					dataList.add(saleFile);

				}

				lineNumber++;

			}

			return dataList;

		} catch (FileRowsInvalidException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Ocurrio un error al leer el archivo.");
		}

	}

	private Sale validateSaleFile(SaleFile saleFile, SaleFile headers, Integer lineNumber, Bank bank)
			throws MultipleErrorsException {

		List<Exception> errors = new ArrayList<Exception>();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdf2 = new SimpleDateFormat("MM/yyyy");

		// DOCUMENT_TYPE
		DocumentTypeEnum documentType = null;
		if (saleFile.getDocumentType().length() == 0) {
			errors.add(new NullException(headers.getDocumentType(), lineNumber));
		} else if (saleFile.getDocumentType().length() > 5) {
			errors.add(new OverflowException(headers.getDocumentType(), lineNumber, 5));
		} else {
			documentType = DocumentTypeEnum.findByName(saleFile.getDocumentType());
			if (documentType == null) {
				errors.add(new FormatException(headers.getDocumentType(), lineNumber));
			} else {
				// payer.setDocumentType(documentTypeEnum);
			}
		}

		// NUIC RESPONSIBLE
		Long nuicResponsible = null;
		if (saleFile.getNuicResponsible().length() == 0) {
			errors.add(new NullException(headers.getNuicResponsible(), lineNumber));
		} else if (saleFile.getNuicResponsible().length() > 11) {
			errors.add(new OverflowException(headers.getNuicResponsible(), lineNumber, 11));
		} else {
			try {
				nuicResponsible = Long.parseLong(saleFile.getNuicResponsible());
				// payer.setNuicResponsible(nuicResponsible);
			} catch (NumberFormatException e) {
				errors.add(new FormatException(headers.getNuicResponsible(), lineNumber));
			}
		}

		// APELLIDO PATERNO RESPONSABLE DE PAGO
		if (saleFile.getLastnamePaternalResponsible().length() == 0) {
			errors.add(new NullException(headers.getLastnamePaternalResponsible(), lineNumber));
		} else if (saleFile.getLastnamePaternalResponsible().length() > 50) {
			errors.add(new OverflowException(headers.getLastnamePaternalResponsible(), lineNumber, 50));
		} else {
			// payer.setLastnamePaternalResponsible(saleFile.getLastnamePaternalResponsible());
		}

		// APELLIDO MATERNO RESPONSABLE DE PAGO
		if (saleFile.getLastnameMaternalResponsible().length() == 0) {
			errors.add(new NullException(headers.getLastnameMaternalResponsible(), lineNumber));
		} else if (saleFile.getLastnameMaternalResponsible().length() > 50) {
			errors.add(new OverflowException(headers.getLastnameMaternalResponsible(), lineNumber, 50));
		} else {
			// payer.setLastnameMaternalResponsible(saleFile.getLastnameMaternalResponsible());
		}

		// NOMBRES DE RESPONSABLE DE PAGO
		if (saleFile.getFirstnameResponsible().length() == 0) {
			errors.add(new NullException(headers.getFirstnameResponsible(), lineNumber));
		} else if (saleFile.getFirstnameResponsible().length() > 50) {
			errors.add(new OverflowException(headers.getFirstnameResponsible(), lineNumber, 50));
		} else {
			// payer.setFirstnameResponsible(saleFile.getFirstnameResponsible());
		}

		// NUMERO TARJETA DE CREDITO
		Long creditCardNumber = null;
		if (saleFile.getCreditCardNumber().length() > 0 && saleFile.getCreditCardNumber().length() != 16) {
			errors.add(new OverflowException(headers.getCreditCardNumber(), lineNumber, 16));
		} else if (saleFile.getCreditCardNumber().length() > 0) {
			try {
				creditCardNumber = Long.parseLong(saleFile.getCreditCardNumber());
				// creditCard.setNumber(creditCardNumber);
			} catch (NumberFormatException e) {
				errors.add(new FormatException(headers.getCreditCardNumber(), lineNumber));
			}
		}

		// NUMERO DE CUENTA
		Long accountNumber = null;
		if (saleFile.getAccountNumber().length() == 0) {
			errors.add(new NullException(headers.getAccountNumber(), lineNumber));
		} else if (saleFile.getAccountNumber().length() > 10) {
			errors.add(new OverflowException(headers.getAccountNumber(), lineNumber, 10));
		} else {
			try {
				accountNumber = Long.parseLong(saleFile.getAccountNumber());
				// sale.setAccountNumber(accountNumber);
			} catch (NumberFormatException e) {
				errors.add(new FormatException(headers.getAccountNumber(), lineNumber));
			}
		}

		// FECHA DE VENCIMIENTO DE TARJETA
		Date expirationDate = null;
		if (saleFile.getCreditCardExpirationDate().length() > 0
				&& saleFile.getCreditCardExpirationDate().length() != 7) {
			errors.add(new OverflowException(headers.getCreditCardExpirationDate(), lineNumber, 7));
		} else {
			try {
				expirationDate = sdf2.parse(saleFile.getCreditCardExpirationDate());
				// creditCard.setExpirationDate(expirationDate);
			} catch (ParseException e1) {
				errors.add(new FormatException(headers.getCreditCardExpirationDate(), lineNumber));
			}
		}

		// ESTADO DE TARJETA DE CREDITO
		if (saleFile.getCreditCardState().length() > 20) {
			errors.add(new OverflowException(headers.getCreditCardState(), lineNumber, 20));
		} else {
			// creditCard.setState(saleFile.getCreditCardState());
		}

		// DIAS DE MORA
		Integer daysOfDefault = null;
		if (saleFile.getCreditCardDaysOfDefault().length() > 4) {
			errors.add(new OverflowException(headers.getCreditCardDaysOfDefault(), lineNumber, 4));
		} else if (saleFile.getCreditCardDaysOfDefault().length() > 0) {
			try {
				daysOfDefault = Integer.parseInt(saleFile.getCreditCardDaysOfDefault());
				// creditCard.setDaysOfDefault(daysOfDefault);
			} catch (NumberFormatException e) {
				errors.add(new FormatException(headers.getCreditCardDaysOfDefault(), lineNumber));
			}
		}

		// NUIC DE CONTRATANTE
		Long nuicContractor = null;
		if (saleFile.getNuicContractor().length() > 11) {
			errors.add(new OverflowException(headers.getNuicContractor(), lineNumber, 11));
		} else if (saleFile.getNuicContractor().length() > 0) {
			try {
				nuicContractor = Long.parseLong(saleFile.getNuicContractor());
				// sale.setNuicContractor(nuicContractor);
			} catch (NumberFormatException e) {
				errors.add(new FormatException(headers.getNuicContractor(), lineNumber));
			}
		}

		// APELLIDO PATERNO DE CONTRATANTE
		if (saleFile.getLastnamePaternalContractor().length() > 50) {
			errors.add(new OverflowException(headers.getLastnamePaternalContractor(), lineNumber, 50));
		} else {
			// sale.setLastnamePaternalContractor(saleFile.getLastnamePaternalContractor());
		}

		// APELLIDO MATERNO CONTRATANTE
		if (saleFile.getLastnameMaternalContractor().length() > 50) {
			errors.add(new OverflowException(headers.getLastnameMaternalContractor(), lineNumber, 50));
		} else {
			// sale.setLastnameMaternalContractor(saleFile.getLastnameMaternalContractor());
		}

		// NOMBRES DE CONTRATANTE
		if (saleFile.getFirstnameContractor().length() > 50) {
			errors.add(new OverflowException(headers.getFirstnameContractor(), lineNumber, 50));
		} else {
			// sale.setFirstnameContractor(saleFile.getFirstnameContractor());
		}

		// NUIC DE ASEGURADO
		Long nuicInsured = null;
		if (saleFile.getNuicInsured().length() == 0) {
			errors.add(new NullException(headers.getNuicInsured(), lineNumber));
		} else if (saleFile.getNuicInsured().length() > 11) {
			errors.add(new OverflowException(headers.getNuicInsured(), lineNumber, 11));
		} else {
			try {
				nuicInsured = Long.parseLong(saleFile.getNuicInsured());
				// sale.setNuicInsured(nuicInsured);
			} catch (NumberFormatException e) {
				errors.add(new FormatException(headers.getNuicInsured(), lineNumber));
			}
		}

		// APELLIDO PATERNO ASEGURADO
		if (saleFile.getLastnamePaternalInsured().length() > 50) {
			errors.add(new OverflowException(headers.getLastnamePaternalInsured(), lineNumber, 50));
		} else {
			// sale.setLastnamePaternalInsured(saleFile.getLastnamePaternalInsured());
		}

		// APELLIDO MATERNO ASEGURADO
		if (saleFile.getLastnameMaternalInsured().length() > 50) {
			errors.add(new OverflowException(headers.getLastnameMaternalInsured(), lineNumber, 50));
		} else {
			// sale.setLastnameMaternalInsured(saleFile.getLastnameMaternalInsured());
		}

		// NOMBRES ASEGURADO
		if (saleFile.getFirstnameInsured().length() > 50) {
			errors.add(new OverflowException(headers.getFirstnameInsured(), lineNumber, 50));
		} else {
			// sale.setFirstnameInsured(saleFile.getFirstnameInsured());
		}

		// TELEFONO1
		Integer phone1 = null;
		if (saleFile.getPhone1().length() > 9) {
			errors.add(new OverflowException(headers.getPhone1(), lineNumber, 9));
		} else {
			if (saleFile.getPhone1().length() > 0) {
				try {
					phone1 = Integer.parseInt(saleFile.getPhone1());
					// sale.setPhone1(phone1);
				} catch (NumberFormatException e) {
					errors.add(new FormatException(headers.getPhone1(), lineNumber));
				}
			}
		}

		// TELEFONO2
		Integer phone2 = null;
		if (saleFile.getPhone2().length() > 9) {
			errors.add(new OverflowException(headers.getPhone2(), lineNumber, 9));
		} else {
			if (saleFile.getPhone2().length() > 0) {
				try {
					phone2 = Integer.parseInt(saleFile.getPhone2());
					// sale.setPhone2(phone2);
				} catch (NumberFormatException e) {
					errors.add(new FormatException(headers.getPhone2(), lineNumber));
				}
			}
		}

		// MAIL
		if (saleFile.getMail().length() > 45) {
			errors.add(new OverflowException(headers.getMail(), lineNumber, 45));
		} else {
			// payer.setMail(saleFile.getMail());
		}

		// DEPARTAMENTO
		if (saleFile.getDepartment().length() > 20) {
			errors.add(new OverflowException(headers.getDepartment(), lineNumber, 20));
		} else {
			// payer.setDepartment(saleFile.getDepartment());
		}

		// PROVINCIA
		if (saleFile.getProvince().length() > 50) {
			errors.add(new OverflowException(headers.getProvince(), lineNumber, 50));
		} else {
			// payer.setProvince(saleFile.getProvince());
		}

		// DISTRITO
		if (saleFile.getDistrict().length() > 50) {
			errors.add(new OverflowException(headers.getDistrict(), lineNumber, 50));
		} else {
			// payer.setDistrict(saleFile.getDistrict());
		}

		// DIRECCION
		if (saleFile.getAddress().length() > 150) {
			errors.add(new OverflowException(headers.getAddress(), lineNumber, 150));
		} else {
			// payer.setAddress(saleFile.getAddress());
		}

		// FECHA DE VENTA
		Date saleDate = null;
		if (saleFile.getDate().length() == 0) {
			errors.add(new NullException(headers.getDate(), lineNumber));
		} else if (saleFile.getDate().length() != 10) {
			errors.add(new OverflowException(headers.getDate(), lineNumber, 10));
		} else {
			try {
				saleDate = sdf1.parse(saleFile.getDate());
				// sale.setDate(saleDate);
			} catch (ParseException e1) {
				errors.add(new FormatException(headers.getDate(), lineNumber));
			}
		}

		// CANAL DE VENTA
		if (saleFile.getChannel().length() > 15) {
			errors.add(new OverflowException(headers.getChannel(), lineNumber, 15));
		} else {
			// sale.setChannel(saleFile.getChannel());
		}

		// LUGAR DE VENTA
		if (saleFile.getPlace().length() > 25) {
			errors.add(new OverflowException(headers.getPlace(), lineNumber, 25));
		} else {
			// sale.setPlace(saleFile.getPlace());
		}

		// CODIGO DE VENDEDOR
		if (saleFile.getVendorCode().length() > 10) {
			errors.add(new OverflowException(headers.getVendorCode(), lineNumber, 10));
		} else {
			// sale.setVendorCode(saleFile.getVendorCode());
		}

		// NOMBRE DE VENDEDOR
		if (saleFile.getVendorName().length() > 50) {
			errors.add(new OverflowException(headers.getVendorName(), lineNumber, 50));
		} else {
			// sale.setVendorName(saleFile.getVendorName());
		}

		// NUMERO DE POLIZA
		if (saleFile.getPolicyNumber().length() > 10) {
			errors.add(new OverflowException(headers.getPolicyNumber(), lineNumber, 10));
		} else {
			// sale.setPolicyNumber(saleFile.getPolicyNumber());
		}

		// NUMERO DE CERTIFICADO
		if (saleFile.getCertificateNumber().length() > 10) {
			errors.add(new OverflowException(headers.getCertificateNumber(), lineNumber, 10));
		} else {
			// sale.setCertificateNumber(saleFile.getCertificateNumber());
		}

		// NUMERO DE PROPUESTA
		if (saleFile.getProposalNumber().length() > 25) {
			errors.add(new OverflowException(headers.getProposalNumber(), lineNumber, 25));
		} else {
			// sale.setProposalNumber(saleFile.getProposalNumber());
		}

		// CODIGO DE COMERCIO
		if (saleFile.getCommercialCode().length() == 0) {
			errors.add(new NullException(headers.getCommercialCode(), lineNumber));
		} else if (saleFile.getCommercialCode().length() > 10) {
			errors.add(new OverflowException(headers.getCommercialCode(), lineNumber, 10));
		} else {
			// sale.setCommerceCode(saleFile.getCommercialCode());
		}

		// PRODUCTO
		Product product = null;
		if (saleFile.getProduct().length() == 0) {
			errors.add(new NullException(headers.getProduct(), lineNumber));
		} else if (saleFile.getProduct().length() != 2) {
			errors.add(new OverflowException(headers.getProduct(), lineNumber, 2));
		} else {
			product = productService.checkIfExist(saleFile.getProduct());
			if (product == null) {
				errors.add(new FormatException(headers.getProduct(), lineNumber));
			} else {
				// sale.setProduct(product);
			}
		}

		// DESCRIPCION DEL PRODUCTO
		if (saleFile.getProductDescription().length() > 45) {
			errors.add(new OverflowException(headers.getProductDescription(), lineNumber, 45));
		} else {
			// sale.setProductDescription(saleFile.getProductDescription());
		}

		// PERIODO DE COBRO
		short collectionPeriodId = 0;
		if (saleFile.getCollectionPeriod().length() == 0) {
			errors.add(new NullException(headers.getCollectionPeriod(), lineNumber));
		} else if (saleFile.getCollectionPeriod().length() > 45) {
			errors.add(new OverflowException(headers.getCollectionPeriod(), lineNumber, 45));
		} else {
			collectionPeriodId = collectionPeriodService.checkIfExist(saleFile.getCollectionPeriod());
			if (collectionPeriodId == 0) {
				errors.add(new FormatException(headers.getCollectionPeriod(), lineNumber));
			} else {
				// sale.setCollectionPeriod(collectionPeriod);
			}
		}

		// TIPO DE COBRO
		if (saleFile.getCollectionType().length() == 0) {
			errors.add(new NullException(headers.getCollectionType(), lineNumber));
		} else if (saleFile.getCollectionType().length() > 15) {
			errors.add(new OverflowException(headers.getCollectionType(), lineNumber, 15));
		} else {
			// sale.setCollectionType(saleFile.getCollectionType());
		}

		// BANCO
		if (saleFile.getBank().length() == 0) {
			errors.add(new NullException(headers.getBank(), lineNumber));
		} else if (saleFile.getBank().length() != 2) {
			errors.add(new OverflowException(headers.getBank(), lineNumber, 2));
		} else if (!saleFile.getBank().equals(bank.getCode())) {
			errors.add(new FormatException(headers.getBank(), lineNumber));
		} else {
			// sale.setBank(bank);
		}

		// PRIMA
		BigDecimal insurancePremium = null;
		if (saleFile.getInsurancePremium().length() == 0) {
			errors.add(new NullException(headers.getInsurancePremium(), lineNumber));
		} else if (saleFile.getInsurancePremium().length() > 8) {
			errors.add(new OverflowException(headers.getInsurancePremium(), lineNumber, 8));
		} else {
			try {
				insurancePremium = new BigDecimal(saleFile.getInsurancePremium());
				if (insurancePremium.scale() > 2) {
					errors.add(new FormatException(headers.getInsurancePremium(), lineNumber));
				} else {
					// sale.setInsurancePremium(insurancePremium);
				}
			} catch (NumberFormatException e) {
				errors.add(new FormatException(headers.getInsurancePremium(), lineNumber));
			}
		}

		// FECHA DE AUDITORIA
		Date auditDate = null;
		if (saleFile.getAuditDate().length() == 0) {
			errors.add(new NullException(headers.getAuditDate(), lineNumber));
		} else if (saleFile.getAuditDate().length() != 10) {
			errors.add(new OverflowException(headers.getAuditDate(), lineNumber, 10));
		} else {
			try {
				auditDate = sdf1.parse(saleFile.getAuditDate());
				// sale.setAuditDate(auditDate);
			} catch (ParseException e) {
				errors.add(new FormatException(headers.getAuditDate(), lineNumber));
			}
		}

		// USUARIO DE AUDITORIA
		if (saleFile.getAuditUser().length() == 0) {
			errors.add(new NullException(headers.getAuditUser(), lineNumber));
		} else if (saleFile.getAuditUser().length() > 15) {
			errors.add(new OverflowException(headers.getAuditUser(), lineNumber, 15));
		} else {
			// sale.setAuditUser(saleFile.getAuditUser());
		}

		// ESTADO DE VENTA
		SaleStateEnum state = null;
		if (saleFile.getState().length() == 0) {
			errors.add(new NullException(headers.getState(), lineNumber));
		} else {
			state = SaleStateEnum.findByName(saleFile.getState());
			if (state == null) {
				errors.add(new FormatException(headers.getState(), lineNumber));
			} else {
				// saleState.setState(saleStateEnum);
			}
		}

		// FECHA DE ESTADO DE VENTA
		Date stateDate = null;
		if (saleFile.getStateDate().length() > 0 && saleFile.getStateDate().length() != 10) {
			errors.add(new OverflowException(headers.getStateDate(), lineNumber, 10));
		} else if (saleFile.getStateDate().length() > 0) {
			try {
				stateDate = sdf1.parse(saleFile.getStateDate());
				// saleState.setDate(stateDate);
			} catch (ParseException e) {
				errors.add(new FormatException(headers.getStateDate(), lineNumber));
			}
		}

		// USUARIO DE ESTADO
		if (saleFile.getDownUser().length() > 15) {
			errors.add(new OverflowException(headers.getDownUser(), lineNumber, 15));
		} else {
			// saleState.setUser(saleFile.getDownUser());
		}

		// CANAL DE ESTADO
		if (saleFile.getDownChannel().length() > 15) {
			errors.add(new OverflowException(headers.getDownChannel(), lineNumber, 15));
		} else {
			// saleState.setChannel(saleFile.getDownChannel());
		}

		// MOTIVO DE ESTADO
		if (saleFile.getDownReason().length() > 50) {
			errors.add(new OverflowException(headers.getDownReason(), lineNumber, 50));
		} else {
			// saleState.setReason(saleFile.getDownReason());
		}

		// OBSERVACION DE ESTADO
		if (saleFile.getDownObservation().length() > 2500) {
			errors.add(new OverflowException(headers.getDownObservation(), lineNumber, 2500));
		} else {
			// saleState.setObservation(saleFile.getDownObservation());
		}

		// FECHA DE ACTUALIZACION DE TARJETA
		Date creditCardDate = null;
		if (saleFile.getCreditCardUpdatedAt().length() != 10) {
			errors.add(new OverflowException(headers.getCreditCardUpdatedAt(), lineNumber, 10));
		} else if (saleFile.getCreditCardUpdatedAt().length() > 0) {
			try {
				creditCardDate = sdf1.parse(saleFile.getCreditCardUpdatedAt());
				// creditCard.setDate(creditCardDate);
			} catch (ParseException e) {
				errors.add(new FormatException(headers.getCreditCardUpdatedAt(), lineNumber));
			}
		}

		// VERIFICA SI EXISTE LA VENTA
		if (saleDate != null && product !=null && collectionPeriodId > 0 && nuicInsured > 0 && bank != null) {
			long saleId = saleService.checkIfExistSale(nuicInsured, saleDate, bank.getId(), product.getId(),
					collectionPeriodId);
			if (saleId > 0) {
				errors.add(new SaleAlreadyExistException(lineNumber));
			}
		}

		if (errors.size() > 0) {
			throw new MultipleErrorsException(errors);
		}

		CollectionPeriod collectionPeriod = new CollectionPeriod();
		collectionPeriod.setId(collectionPeriodId);

		//Product product = new Product();
		//product.setId(productId);

		Sale sale = new Sale(accountNumber, nuicContractor, saleFile.getLastnamePaternalContractor(),
				saleFile.getLastnameMaternalContractor(), saleFile.getFirstnameContractor(), nuicInsured,
				saleFile.getLastnamePaternalInsured(), saleFile.getLastnameMaternalInsured(),
				saleFile.getFirstnameInsured(), phone1, phone2, saleDate, saleFile.getChannel(), saleFile.getPlace(),
				saleFile.getVendorCode(), saleFile.getVendorName(), saleFile.getPolicyNumber(),
				saleFile.getCertificateNumber(), saleFile.getProposalNumber(), saleFile.getProductDescription(),
				saleFile.getCollectionType(), insurancePremium, auditDate, saleFile.getAuditUser(),
				saleFile.getCommercialCode(), collectionPeriod, product, bank);

		Payer payer = new Payer(documentType, nuicResponsible, saleFile.getLastnamePaternalResponsible(),
				saleFile.getLastnameMaternalResponsible(), saleFile.getFirstnameResponsible(), saleFile.getMail(),
				saleFile.getDepartment(), saleFile.getProvince(), saleFile.getDistrict(), saleFile.getAddress(), sale);

		SaleState saleState = new SaleState(state, stateDate, saleFile.getDownUser(), saleFile.getDownChannel(),
				saleFile.getDownReason(), saleFile.getDownObservation(), sale);

		CreditCard creditCard = new CreditCard(creditCardNumber, expirationDate, saleFile.getCreditCardState(),
				daysOfDefault, sale, creditCardDate);

		sale.setPayer(payer);
		sale.setCreditCard(creditCard);
		sale.setSaleState(saleState);

		return sale;

	}

	public StreamedContent getDownloadFile() {
		return downloadFile;
	}

	public void setDownloadFile(StreamedContent downloadFile) {
		this.downloadFile = downloadFile;
	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

}
