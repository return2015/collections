package com.returnsoft.collection.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
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
import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;
import javax.servlet.http.Part;

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
import com.returnsoft.collection.exception.MultipleErrorsException;
import com.returnsoft.collection.exception.FileNotFoundException;
import com.returnsoft.collection.exception.FileRowsInvalidException;
import com.returnsoft.collection.exception.FileRowsZeroException;
import com.returnsoft.collection.exception.FormatException;
import com.returnsoft.collection.exception.NullException;
import com.returnsoft.collection.exception.OverflowException;
import com.returnsoft.collection.exception.SaleAlreadyExistException;
import com.returnsoft.collection.exception.SaleDuplicateException;
import com.returnsoft.collection.exception.UserLoggedNotFoundException;
import com.returnsoft.collection.service.CollectionPeriodService;
import com.returnsoft.collection.service.ProductService;
import com.returnsoft.collection.service.SaleService;
import com.returnsoft.collection.util.FacesUtil;
import com.returnsoft.collection.util.SaleFile;
import com.returnsoft.collection.util.SessionBean;

@Named
@RequestScoped
public class LoadSalesController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -185393107214608269L;

	@Inject
	private FacesUtil facesUtil;

	@Inject
	private SessionBean sessionBean;


	// SALES
	private Part saleFile;
	private Integer SALE_FILE_ROWS = 49;

	@EJB
	private CollectionPeriodService collectionPeriodService;

	@EJB
	private ProductService productService;

	@EJB
	private SaleService saleService;

	public void downloadSalesFile() {
		try {

			if (sessionBean == null || sessionBean.getUser() == null || sessionBean.getUser().getId() == null) {
				throw new UserLoggedNotFoundException();
			}

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

	public void validateSalesFile() {
		/* AjaxBehaviorEvent event */
		System.out.println("validateSalesFile()");

		Part file = saleFile;
		Integer FILE_ROWS = SALE_FILE_ROWS;

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

			/////////

			BufferedReader br = new BufferedReader(
					new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));

			String strLine = null;
			Integer numLine = 0;

			SaleFile headers = new SaleFile();
			List<SaleFile> dataList = new ArrayList<SaleFile>();

			while ((strLine = br.readLine()) != null) {

				System.out.println("numLine:" + numLine);

				if (numLine == 0) {
					// SE LEE CABECERA
					String[] values = strLine.split("\\|", -1);
					if (values.length != FILE_ROWS) {
						throw new FileRowsInvalidException(FILE_ROWS);
					} else {

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

					}
				} else {

					String[] values = strLine.split("\\|", -1);

					if (values.length != FILE_ROWS) {
						throw new FileRowsInvalidException(FILE_ROWS);
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
				}

				numLine++;

			}

			if (dataList.size() > 0) {

				////////// VALIDANDO DUPLICADOS
				/////////////////////////////
				Set<String> dataSet = new HashSet<String>();
				Set<Integer> errorSet = new HashSet<Integer>();
				List<Exception> errors = new ArrayList<Exception>();
				numLine = 1;

				for (SaleFile saleFile : dataList) {
					String dataString = saleFile.getNuicInsured() + saleFile.getDate() + saleFile.getProduct()
							+ saleFile.getBank() + saleFile.getCollectionPeriod();
					numLine++;
					if (!dataSet.add(dataString)) {
						errorSet.add(numLine);
					}
				}

				for (Integer errorLineNumber : errorSet) {
					errors.add(new SaleDuplicateException(errorLineNumber));
				}

				/////////////////////////////////////////
				//////////////////////////
				//////////////////////////////////////////

				if (errors.size() == 0) {
					validateSalesData(headers, dataList, file.getSubmittedFileName());
				} else {
					throw new MultipleErrorsException(errors);
				}

			} else {
				throw new FileRowsZeroException();
			}

			facesUtil.sendConfirmMessage("Se creó el lote satisfactorimente.");

		} catch (MultipleErrorsException e) {
			e.printStackTrace();
			for (Exception err : e.getErrors()) {
				facesUtil.sendErrorMessage(err.getMessage());
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage("Existen valores nulos.");
		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getMessage());
		}

	}

	public void validateSalesData(SaleFile headers, List<SaleFile> dataList, String filename) throws Exception {

		System.out.println("Validando vacíos");

		// try {

		Bank bank = sessionBean.getBank();
		User user = sessionBean.getUser();
		Date currentDate = new Date();
		List<Product> productsEntity = productService.getAll();
		List<CollectionPeriod> collectionPeriodsEntity = collectionPeriodService.getAll();

		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdf2 = new SimpleDateFormat("MM/yyyy");
		Integer lineNumber = 1;

		List<Sale> sales = new ArrayList<Sale>();
		List<Exception> errors = new ArrayList<Exception>();

		for (SaleFile saleFile : dataList) {

			System.out.println("verificando ..");

			Sale sale = new Sale();
			Payer payer = new Payer();
			SaleState saleState = new SaleState();
			CreditCard creditCard = new CreditCard();

			// DOCUMENT_TYPE
			if (saleFile.getDocumentType().length() == 0) {
				errors.add(new NullException(headers.getDocumentType(), lineNumber));
			} else if (saleFile.getDocumentType().length() != 3) {
				errors.add(new OverflowException(headers.getDocumentType(), lineNumber, 3));
			} else {
				DocumentTypeEnum documentTypeEnum = DocumentTypeEnum.findByName(saleFile.getDocumentType());
				if (documentTypeEnum == null) {
					errors.add(new FormatException(headers.getDocumentType(), lineNumber));
				} else {
					payer.setDocumentType(documentTypeEnum);
				}
			}

			// NUIC RESPONSIBLE
			if (saleFile.getNuicResponsible().length() == 0) {
				errors.add(new NullException(headers.getNuicResponsible(), lineNumber));
			} else if (saleFile.getNuicResponsible().length() > 11) {
				errors.add(new OverflowException(headers.getNuicResponsible(), lineNumber, 11));
			} else {
				try {
					Long nuicResponsible = Long.parseLong(saleFile.getNuicResponsible());
					payer.setNuicResponsible(nuicResponsible);
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
				payer.setLastnamePaternalResponsible(saleFile.getLastnamePaternalResponsible());
			}

			// APELLIDO MATERNO RESPONSABLE DE PAGO
			if (saleFile.getLastnameMaternalResponsible().length() == 0) {
				errors.add(new NullException(headers.getLastnameMaternalResponsible(), lineNumber));
			} else if (saleFile.getLastnameMaternalResponsible().length() > 50) {
				errors.add(new OverflowException(headers.getLastnameMaternalResponsible(), lineNumber, 50));
			} else {
				payer.setLastnameMaternalResponsible(saleFile.getLastnameMaternalResponsible());
			}

			// NOMBRES DE RESPONSABLE DE PAGO
			if (saleFile.getFirstnameResponsible().length() == 0) {
				errors.add(new NullException(headers.getFirstnameResponsible(), lineNumber));
			} else if (saleFile.getFirstnameResponsible().length() > 50) {
				errors.add(new OverflowException(headers.getFirstnameResponsible(), lineNumber, 50));
			} else {
				payer.setFirstnameResponsible(saleFile.getFirstnameResponsible());
			}

			// NUMERO TARJETA DE CREDITO
			if (saleFile.getCreditCardNumber().length() > 0 && saleFile.getCreditCardNumber().length() != 16) {
				errors.add(new OverflowException(headers.getCreditCardNumber(), lineNumber, 16));
			} else if (saleFile.getCreditCardNumber().length() > 0) {
				try {
					Long creditCardNumber = Long.parseLong(saleFile.getCreditCardNumber());
					creditCard.setNumber(creditCardNumber);
				} catch (NumberFormatException e) {
					errors.add(new FormatException(headers.getCreditCardNumber(), lineNumber));
				}
			}

			// NUMERO DE CUENTA
			if (saleFile.getAccountNumber().length() == 0) {
				errors.add(new NullException(headers.getAccountNumber(), lineNumber));
			} else if (saleFile.getAccountNumber().length() > 10) {
				errors.add(new OverflowException(headers.getAccountNumber(), lineNumber, 10));
			} else {
				try {
					Long accountNumber = Long.parseLong(saleFile.getAccountNumber());
					sale.setAccountNumber(accountNumber);
				} catch (NumberFormatException e) {
					errors.add(new FormatException(headers.getAccountNumber(), lineNumber));
				}
			}

			// FECHA DE VENCIMIENTO DE TARJETA
			if (saleFile.getCreditCardExpirationDate().length() > 0
					&& saleFile.getCreditCardExpirationDate().length() != 7) {
				errors.add(new OverflowException(headers.getCreditCardExpirationDate(), lineNumber, 7));
			} else {
				try {
					Date expirationDate = sdf2.parse(saleFile.getCreditCardExpirationDate());
					creditCard.setExpirationDate(expirationDate);
				} catch (ParseException e1) {
					errors.add(new FormatException(headers.getCreditCardExpirationDate(), lineNumber));
				}
			}

			// ESTADO DE TARJETA DE CREDITO
			if (saleFile.getCreditCardState().length() > 20) {
				errors.add(new OverflowException(headers.getCreditCardState(), lineNumber, 20));
			} else {
				creditCard.setState(saleFile.getCreditCardState());
			}

			// DIAS DE MORA
			if (saleFile.getCreditCardDaysOfDefault().length() > 4) {
				errors.add(new OverflowException(headers.getCreditCardDaysOfDefault(), lineNumber, 4));
			} else if (saleFile.getCreditCardDaysOfDefault().length() > 0) {
				try {
					Integer daysOfDefault = Integer.parseInt(saleFile.getCreditCardDaysOfDefault());
					creditCard.setDaysOfDefault(daysOfDefault);
				} catch (NumberFormatException e) {
					errors.add(new FormatException(headers.getCreditCardDaysOfDefault(), lineNumber));
				}
			}

			// NUIC DE CONTRATANTE
			if (saleFile.getNuicContractor().length() > 8) {
				errors.add(new OverflowException(headers.getNuicContractor(), lineNumber, 8));
			} else if (saleFile.getNuicContractor().length() > 0) {
				try {
					Integer nuicContractor = Integer.parseInt(saleFile.getNuicContractor());
					sale.setNuicContractor(nuicContractor);
				} catch (NumberFormatException e) {
					errors.add(new FormatException(headers.getNuicContractor(), lineNumber));
				}
			}

			// APELLIDO PATERNO DE CONTRATANTE
			if (saleFile.getLastnamePaternalContractor().length() > 50) {
				errors.add(new OverflowException(headers.getLastnamePaternalContractor(), lineNumber, 50));
			} else {
				sale.setLastnamePaternalContractor(saleFile.getLastnamePaternalContractor());
			}

			// APELLIDO MATERNO CONTRATANTE
			if (saleFile.getLastnameMaternalContractor().length() > 50) {
				errors.add(new OverflowException(headers.getLastnameMaternalContractor(), lineNumber, 50));
			} else {
				sale.setLastnameMaternalContractor(saleFile.getLastnameMaternalContractor());
			}

			// NOMBRES DE CONTRATANTE
			// System.out.println("saleFile.getFirstnameContractor()" +
			// saleFile.getFirstnameContractor());
			if (saleFile.getFirstnameContractor().length() > 50) {
				errors.add(new OverflowException(headers.getFirstnameContractor(), lineNumber, 50));
			} else {
				sale.setFirstnameContractor(saleFile.getFirstnameContractor());
			}

			// NUIC DE ASEGURADO
			// System.out.println("saleFile.getNuicInsured():" +
			// saleFile.getNuicInsured());
			if (saleFile.getNuicInsured().length() == 0) {
				errors.add(new NullException(headers.getNuicInsured(), lineNumber));
			} else if (saleFile.getNuicInsured().length() > 8) {
				errors.add(new OverflowException(headers.getNuicInsured(), lineNumber, 8));
			} else {
				try {
					Integer nuicInsured = Integer.parseInt(saleFile.getNuicInsured());
					sale.setNuicInsured(nuicInsured);
				} catch (NumberFormatException e) {
					errors.add(new FormatException(headers.getNuicInsured(), lineNumber));
				}
			}

			// APELLIDO PATERNO ASEGURADO
			if (saleFile.getLastnamePaternalInsured().length() > 50) {
				errors.add(new OverflowException(headers.getLastnamePaternalInsured(), lineNumber, 50));
			} else {
				sale.setLastnamePaternalInsured(saleFile.getLastnamePaternalInsured());
			}

			// APELLIDO MATERNO ASEGURADO
			if (saleFile.getLastnameMaternalInsured().length() > 50) {
				errors.add(new OverflowException(headers.getLastnameMaternalInsured(), lineNumber, 50));
			} else {
				sale.setLastnameMaternalInsured(saleFile.getLastnameMaternalInsured());
			}

			// NOMBRES ASEGURADO
			if (saleFile.getFirstnameInsured().length() > 50) {
				errors.add(new OverflowException(headers.getFirstnameInsured(), lineNumber, 50));
			} else {
				sale.setFirstnameInsured(saleFile.getFirstnameInsured());
			}

			// TELEFONO1
			if (saleFile.getPhone1().length() > 9) {
				errors.add(new OverflowException(headers.getPhone1(), lineNumber, 9));
			} else {
				if (saleFile.getPhone1().length() > 0) {
					try {
						Integer phone1 = Integer.parseInt(saleFile.getPhone1());
						sale.setPhone1(phone1);
					} catch (NumberFormatException e) {
						errors.add(new FormatException(headers.getPhone1(), lineNumber));
					}
				}
			}

			// TELEFONO2
			if (saleFile.getPhone2().length() > 9) {
				errors.add(new OverflowException(headers.getPhone2(), lineNumber, 9));
			} else {
				if (saleFile.getPhone2().length() > 0) {
					try {
						Integer phone2 = Integer.parseInt(saleFile.getPhone2());
						sale.setPhone2(phone2);
					} catch (NumberFormatException e) {
						errors.add(new FormatException(headers.getPhone2(), lineNumber));
					}
				}
			}

			// MAIL
			if (saleFile.getMail().length() > 45) {
				errors.add(new OverflowException(headers.getMail(), lineNumber, 45));
			} else {
				payer.setMail(saleFile.getMail());
			}

			// DEPARTAMENTO
			if (saleFile.getDepartment().length() > 20) {
				errors.add(new OverflowException(headers.getDepartment(), lineNumber, 20));
			} else {
				payer.setDepartment(saleFile.getDepartment());
			}

			// PROVINCIA
			if (saleFile.getProvince().length() > 50) {
				errors.add(new OverflowException(headers.getProvince(), lineNumber, 50));
			} else {
				payer.setProvince(saleFile.getProvince());
			}

			// DISTRITO
			if (saleFile.getDistrict().length() > 50) {
				errors.add(new OverflowException(headers.getDistrict(), lineNumber, 50));
			} else {
				payer.setDistrict(saleFile.getDistrict());
			}

			// DIRECCION
			if (saleFile.getAddress().length() > 150) {
				errors.add(new OverflowException(headers.getAddress(), lineNumber, 150));
			} else {
				payer.setAddress(saleFile.getAddress());
			}

			// FECHA DE VENTA
			if (saleFile.getDate().length() == 0) {
				errors.add(new NullException(headers.getDate(), lineNumber));
			} else if (saleFile.getDate().length() != 10) {
				errors.add(new OverflowException(headers.getDate(), lineNumber, 10));
			} else {
				try {
					Date saleDate = sdf1.parse(saleFile.getDate());
					sale.setDate(saleDate);
				} catch (ParseException e1) {
					errors.add(new FormatException(headers.getDate(), lineNumber));
				}
			}

			// CANAL DE VENTA
			if (saleFile.getChannel().length() > 15) {
				errors.add(new OverflowException(headers.getChannel(), lineNumber, 15));
			} else {
				sale.setChannel(saleFile.getChannel());
			}

			// LUGAR DE VENTA
			if (saleFile.getPlace().length() > 25) {
				errors.add(new OverflowException(headers.getPlace(), lineNumber, 25));
			} else {
				sale.setPlace(saleFile.getPlace());
			}

			// CODIGO DE VENDEDOR
			if (saleFile.getVendorCode().length() > 10) {
				errors.add(new OverflowException(headers.getVendorCode(), lineNumber, 10));
			} else {
				sale.setVendorCode(saleFile.getVendorCode());
			}

			// NOMBRE DE VENDEDOR
			if (saleFile.getVendorName().length() > 50) {
				errors.add(new OverflowException(headers.getVendorName(), lineNumber, 50));
			} else {
				sale.setVendorName(saleFile.getVendorName());
			}

			// NUMERO DE POLIZA
			if (saleFile.getPolicyNumber().length() > 10) {
				errors.add(new OverflowException(headers.getPolicyNumber(), lineNumber, 10));
			} else {
				sale.setPolicyNumber(saleFile.getPolicyNumber());
			}

			// NUMERO DE CERTIFICADO
			if (saleFile.getCertificateNumber().length() > 10) {
				errors.add(new OverflowException(headers.getCertificateNumber(), lineNumber, 10));
			} else {
				sale.setCertificateNumber(saleFile.getCertificateNumber());
			}

			// NUMERO DE PROPUESTA
			if (saleFile.getProposalNumber().length() > 25) {
				errors.add(new OverflowException(headers.getProposalNumber(), lineNumber, 25));
			} else {
				sale.setProposalNumber(saleFile.getProposalNumber());
			}

			// CODIGO DE COMERCIO
			if (saleFile.getCommercialCode().length() == 0) {
				errors.add(new NullException(headers.getCommercialCode(), lineNumber));
			} else if (saleFile.getCommercialCode().length() > 10) {
				errors.add(new OverflowException(headers.getCommercialCode(), lineNumber, 10));
			} else {
				sale.setCommerceCode(saleFile.getCommercialCode());
			}

			// PRODUCTO
			if (saleFile.getProduct().length() == 0) {
				errors.add(new NullException(headers.getProduct(), lineNumber));
			} else if (saleFile.getProduct().length() != 2) {
				errors.add(new OverflowException(headers.getProduct(), lineNumber, 2));
			} else {
				Product product = null;
				for (Product productEntity : productsEntity) {
					if (saleFile.getProduct().equals(productEntity.getCode())) {
						product = productEntity;
						break;
					}
				}
				if (product == null) {
					errors.add(new FormatException(headers.getProduct(), lineNumber));
				} else {
					sale.setProduct(product);
				}
			}

			// DESCRIPCION DEL PRODUCTO
			if (saleFile.getProductDescription().length() > 45) {
				errors.add(new OverflowException(headers.getProductDescription(), lineNumber, 45));
			} else {
				sale.setProductDescription(saleFile.getProductDescription());
			}

			// PERIODO DE COBRO
			if (saleFile.getCollectionPeriod().length() == 0) {
				errors.add(new NullException(headers.getCollectionPeriod(), lineNumber));
			} else if (saleFile.getCollectionPeriod().length() > 45) {
				errors.add(new OverflowException(headers.getCollectionPeriod(), lineNumber, 45));
			} else {
				CollectionPeriod collectionPeriod = null;
				for (CollectionPeriod collectionPeriodEntity : collectionPeriodsEntity) {
					if (saleFile.getCollectionPeriod().equals(collectionPeriodEntity.getName())) {
						collectionPeriod = collectionPeriodEntity;
						break;
					}
				}
				if (collectionPeriod == null) {
					errors.add(new FormatException(headers.getCollectionPeriod(), lineNumber));
				} else {
					sale.setCollectionPeriod(collectionPeriod);
				}
			}

			// TIPO DE COBRO
			if (saleFile.getCollectionType().length() == 0) {
				errors.add(new NullException(headers.getCollectionType(), lineNumber));
			} else if (saleFile.getCollectionType().length() > 15) {
				errors.add(new OverflowException(headers.getCollectionType(), lineNumber, 15));
			} else {
				sale.setCollectionType(saleFile.getCollectionType());
			}

			// BANCO
			if (saleFile.getBank().length() == 0) {
				errors.add(new NullException(headers.getBank(), lineNumber));
			} else if (saleFile.getBank().length() != 2) {
				errors.add(new OverflowException(headers.getBank(), lineNumber, 2));
			} else if (!saleFile.getBank().equals(bank.getCode())) {
				errors.add(new FormatException(headers.getBank(), lineNumber));
			} else {
				sale.setBank(bank);
			}

			// PRIMA
			if (saleFile.getInsurancePremium().length() == 0) {
				errors.add(new NullException(headers.getInsurancePremium(), lineNumber));
			} else if (saleFile.getInsurancePremium().length() > 8) {
				errors.add(new OverflowException(headers.getInsurancePremium(), lineNumber, 8));
			} else {
				try {
					BigDecimal insurancePremium = new BigDecimal(saleFile.getInsurancePremium());
					if (insurancePremium.scale() > 2) {
						errors.add(new FormatException(headers.getInsurancePremium(), lineNumber));
					} else {
						sale.setInsurancePremium(insurancePremium);
					}
				} catch (NumberFormatException e) {
					errors.add(new FormatException(headers.getInsurancePremium(), lineNumber));
				}
			}

			// FECHA DE AUDITORIA
			if (saleFile.getAuditDate().length() == 0) {
				errors.add(new NullException(headers.getAuditDate(), lineNumber));
			} else if (saleFile.getAuditDate().length() != 10) {
				errors.add(new OverflowException(headers.getAuditDate(), lineNumber, 10));
			} else {
				try {
					Date auditDate = sdf1.parse(saleFile.getAuditDate());
					sale.setAuditDate(auditDate);
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
				sale.setAuditUser(saleFile.getAuditUser());
			}

			// ESTADO DE VENTA
			if (saleFile.getState().length() == 0) {
				errors.add(new NullException(headers.getState(), lineNumber));
			} else {
				SaleStateEnum saleStateEnum = SaleStateEnum.findByName(saleFile.getState());
				if (saleStateEnum == null) {
					errors.add(new FormatException(headers.getState(), lineNumber));
				} else {
					saleState.setState(saleStateEnum);
				}
			}

			// FECHA DE ESTADO DE VENTA
			if (saleFile.getStateDate().length() > 0 && saleFile.getStateDate().length() != 10) {
				errors.add(new OverflowException(headers.getStateDate(), lineNumber, 10));
			} else if (saleFile.getStateDate().length() > 0) {
				try {
					Date stateDate = sdf1.parse(saleFile.getStateDate());
					saleState.setDate(stateDate);
				} catch (ParseException e) {
					errors.add(new FormatException(headers.getStateDate(), lineNumber));
				}
			}

			// USUARIO DE ESTADO
			if (saleFile.getDownUser().length() > 15) {
				errors.add(new OverflowException(headers.getDownUser(), lineNumber, 15));
			} else {
				saleState.setUser(saleFile.getDownUser());
			}

			// CANAL DE ESTADO
			if (saleFile.getDownChannel().length() > 15) {
				errors.add(new OverflowException(headers.getDownChannel(), lineNumber, 15));
			} else {
				saleState.setChannel(saleFile.getDownChannel());
			}

			// MOTIVO DE ESTADO
			if (saleFile.getDownReason().length() > 50) {
				errors.add(new OverflowException(headers.getDownReason(), lineNumber, 50));
			} else {
				saleState.setReason(saleFile.getDownReason());
			}

			// OBSERVACION DE ESTADO
			if (saleFile.getDownObservation().length() > 2500) {
				errors.add(new OverflowException(headers.getDownObservation(), lineNumber, 2500));
			} else {
				saleState.setObservation(saleFile.getDownObservation());
			}

			// FECHA DE ACTUALIZACION DE TARJETA
			if (saleFile.getCreditCardUpdatedAt().length() != 10) {
				errors.add(new OverflowException(headers.getCreditCardUpdatedAt(), lineNumber, 10));
			} else if (saleFile.getCreditCardUpdatedAt().length() > 0) {
				try {
					Date creditCardDate = sdf1.parse(saleFile.getCreditCardUpdatedAt());
					creditCard.setDate(creditCardDate);
				} catch (ParseException e) {
					errors.add(new FormatException(headers.getCreditCardUpdatedAt(), lineNumber));
				}
			}

			// VERIFICA SI EXISTE LA VENTA
			if (sale.getBank() != null && sale.getProduct() != null && sale.getCollectionPeriod() != null) {
				Boolean saleExist = saleService.checkIfExistSale(sale.getNuicInsured(), sale.getDate(),
						sale.getBank().getId(), sale.getProduct().getId(), sale.getCollectionPeriod().getId());
				if (saleExist) {
					errors.add(new SaleAlreadyExistException(lineNumber));
				}
			}

			// sale.setCreatedBy(user);
			sale.setCreatedAt(currentDate);

			sale.setPayer(payer);
			sale.setCreditCard(creditCard);
			sale.setSaleState(saleState);

			payer.setSale(sale);
			creditCard.setSale(sale);
			saleState.setSale(sale);

			sales.add(sale);

			lineNumber++;

		}

		System.out.println("errors:" + errors.size());

		if (errors.size() == 0) {
			saleService.addSaleList(sales, filename, headers, user.getId(), bank.getId());

		} else {
			throw new MultipleErrorsException(errors);
		}

		// } catch (Exception e) {
		// e.printStackTrace();
		// }

	}

	public Part getSaleFile() {
		return saleFile;
	}

	public void setSaleFile(Part saleFile) {
		this.saleFile = saleFile;
	}

}
