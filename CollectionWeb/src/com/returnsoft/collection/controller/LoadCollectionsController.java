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
import com.returnsoft.collection.entity.Collection;
import com.returnsoft.collection.entity.PaymentMethod;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.entity.User;
import com.returnsoft.collection.enumeration.CollectionResponseEnum;
import com.returnsoft.collection.enumeration.MoneyTypeEnum;
import com.returnsoft.collection.exception.BankNotSelectedException;
import com.returnsoft.collection.exception.DecimalException;
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
import com.returnsoft.collection.exception.SaleNotFoundException;
import com.returnsoft.collection.exception.UserLoggedNotFoundException;
import com.returnsoft.collection.service.CollectionService;
import com.returnsoft.collection.service.PaymentMethodService;
import com.returnsoft.collection.service.SaleService;
import com.returnsoft.collection.vo.CollectionFile;
import com.returnsoft.generic.util.SessionBean;

@ManagedBean
@ViewScoped
public class LoadCollectionsController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5987333554254645202L;

	private UploadedFile file;

	private StreamedContent downloadFile;

	@EJB
	private CollectionService collectionService;

	@EJB
	private PaymentMethodService paymentMethodService;

	@EJB
	private SaleService saleService;

	@Inject
	private SessionBean sessionBean;

	public LoadCollectionsController() {
		System.out.println("LoadCollectionsController");
		InputStream stream = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext())
				.getResourceAsStream("/resources/templates/tramas_cobranzas.xlsx");
		downloadFile = new DefaultStreamedContent(stream,
				"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "tramas_cobranzas.xlsx");
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

			List<CollectionFile> dataList = readFile(file);
			CollectionFile headers = dataList.get(0);
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

			for (CollectionFile collectionFile : dataList) {
				String dataString = collectionFile.getEstimatedDate() + collectionFile.getSaleCode();
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
			List<Collection> collections = new ArrayList<Collection>();
			List<Exception> errors = new ArrayList<Exception>();

			for (CollectionFile collectionFile : dataList) {
				try {
					Collection collection = validateFile(collectionFile, headers, lineNumber, bank);
					collections.add(collection);
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
			collectionService.addCollectionList(collections, headers, file.getFileName(), user);
			RequestContext.getCurrentInstance().closeDialog(null);

		} catch (Exception e) {
			e.printStackTrace();
			RequestContext.getCurrentInstance().closeDialog(e);
		}

	}

	// @PostConstruct
	/*
	 * public String initialize() { try {
	 * 
	 * SessionBean sessionBean = (SessionBean) FacesContext
	 * .getCurrentInstance().getExternalContext().getSessionMap()
	 * .get("sessionBean");
	 * 
	 * if (sessionBean!=null && sessionBean.getUser()!=null &&
	 * sessionBean.getUser().getId()>0) {
	 * 
	 * if (!sessionBean.getUser().getUserType().equals(UserTypeEnum.ADMIN) &&
	 * !sessionBean.getUser().getUserType().equals(UserTypeEnum.AGENT)) { throw
	 * new UserPermissionNotFoundException(); }
	 * 
	 * Short bankId = (Short) sessionBean.getBank().getId();
	 * 
	 * //commerces = commerceService.findByBank(bankId);
	 * 
	 * return null;
	 * 
	 * } else{ throw new UserLoggedNotFoundException(); }
	 * 
	 * 
	 * } catch (UserLoggedNotFoundException e) { e.printStackTrace();
	 * facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
	 * return "login.xhtml?faces-redirect=true"; } catch
	 * (UserPermissionNotFoundException e) { e.printStackTrace();
	 * facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
	 * return "login.xhtml?faces-redirect=true"; } catch (Exception e) {
	 * e.printStackTrace();
	 * facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
	 * return null; } }
	 */

	private List<CollectionFile> readFile(UploadedFile file) throws Exception {

		String strLine = null;
		Integer lineNumber = 1;
		CollectionFile headers = new CollectionFile();
		List<CollectionFile> dataList = new ArrayList<CollectionFile>();
		Integer FILE_ROWS = 15;

		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(file.getInputstream(), StandardCharsets.UTF_8));

			while ((strLine = br.readLine()) != null) {

				String[] values = strLine.split("\\|", -1);
				//System.out.println("values.length:" + values.length);
				if (values.length != FILE_ROWS) {
					throw new FileRowsInvalidException(lineNumber,FILE_ROWS);
				}

				// SE LEE CABECERA
				if (lineNumber == 1) {

					headers.setSaleCode(values[0]);
					headers.setMaximumAmount(values[1]);
					headers.setChargeAmount(values[2]);
					headers.setEstimatedDate(values[3]);
					headers.setDepositDate(values[4]);
					headers.setMonthLiquidation(values[5]);
					headers.setResponseCode(values[6]);
					headers.setAuthorizationCode(values[7]);
					headers.setResponseMessage(values[8]);
					headers.setAction(values[9]);
					headers.setTransactionState(values[10]);

					headers.setLoteNumber(values[11]);
					headers.setChannel(values[12]);
					headers.setPaymentMethod(values[13]);
					headers.setMoneyType(values[14]);

					dataList.add(headers);

				} else {

					CollectionFile collectionFile = new CollectionFile();

					collectionFile.setSaleCode(values[0].trim());
					collectionFile.setMaximumAmount(values[1].trim());
					collectionFile.setChargeAmount(values[2].trim());
					collectionFile.setEstimatedDate(values[3].trim());
					collectionFile.setDepositDate(values[4].trim());
					collectionFile.setMonthLiquidation(values[5].trim());

					collectionFile.setResponseCode(values[6].trim());
					collectionFile.setAuthorizationCode(values[7].trim());
					collectionFile.setResponseMessage(values[8].trim());
					collectionFile.setAction(values[9].trim());
					collectionFile.setTransactionState(values[10].trim());

					collectionFile.setLoteNumber(values[11].trim());
					collectionFile.setChannel(values[12].trim());
					collectionFile.setPaymentMethod(values[13].trim());
					collectionFile.setMoneyType(values[14].trim());

					dataList.add(collectionFile);

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

	private Collection validateFile(CollectionFile collectionFile, CollectionFile headers, Integer lineNumber,
			Bank bank) throws MultipleErrorsException {

		List<Exception> errors = new ArrayList<Exception>();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdf2 = new SimpleDateFormat("MM/yyyy");

		// CODE
		Sale saleFound = null;
		if (collectionFile.getSaleCode().length() == 0) {
			errors.add(new NullException(headers.getSaleCode(), lineNumber));
		} else if (collectionFile.getSaleCode().length() != 10) {
			errors.add(new OverflowException(headers.getSaleCode(), lineNumber, 10));
		} else {
			saleFound = saleService.findByCode(collectionFile.getSaleCode());
			if (saleFound == null) {
				errors.add(new SaleNotFoundException(headers.getSaleCode(), lineNumber));
			} else {
				// saleState = saleFound.getSaleState();
			}
		}

		// IMPORTE MAXIMO
		BigDecimal maximumAmount = null;
		if (collectionFile.getMaximumAmount().length() == 0) {
			errors.add(new NullException(headers.getMaximumAmount(), lineNumber));
		} else if (collectionFile.getMaximumAmount().length() > 11) {
			errors.add(new OverflowException(headers.getMaximumAmount(), lineNumber, 11));
		} else {
			try {
				maximumAmount = new BigDecimal(collectionFile.getMaximumAmount());
				if (maximumAmount.scale() > 2) {
					errors.add(new DecimalException(lineNumber, headers.getMaximumAmount(), 2));
				}
			} catch (NumberFormatException e) {
				errors.add(new FormatException(headers.getMaximumAmount(), lineNumber));
			}
		}

		// IMPORTE CARGO
		BigDecimal chargeAmount = null;
		if (collectionFile.getChargeAmount().length() == 0) {
			errors.add(new NullException(headers.getChargeAmount(), lineNumber));
		} else if (collectionFile.getChargeAmount().length() > 11) {
			errors.add(new OverflowException(headers.getChargeAmount(), lineNumber, 11));
		} else {
			try {
				chargeAmount = new BigDecimal(collectionFile.getChargeAmount());
				if (chargeAmount.scale() > 2) {
					errors.add(new DecimalException(lineNumber, headers.getChargeAmount(), 2));
				}
			} catch (NumberFormatException e) {
				errors.add(new FormatException(headers.getChargeAmount(), lineNumber));
			}
		}

		// FECHA DE ESTIMADA
		Date estimatedDate = null;
		if (collectionFile.getEstimatedDate().length() == 0) {
			errors.add(new NullException(headers.getEstimatedDate(), lineNumber));
		} else if (collectionFile.getEstimatedDate().length() != 10) {
			errors.add(new OverflowException(headers.getEstimatedDate(), lineNumber, 10));
		} else {
			try {
				estimatedDate = sdf1.parse(collectionFile.getEstimatedDate());
				
			} catch (ParseException e1) {
				errors.add(new FormatException(headers.getEstimatedDate(), lineNumber));
			}
		}

		// FECHA DE DEPOSITO
		Date depositDate = null;
		if (collectionFile.getDepositDate().length() == 0) {
			errors.add(new NullException(headers.getDepositDate(), lineNumber));
		} else if (collectionFile.getDepositDate().length() != 10) {
			errors.add(new OverflowException(headers.getDepositDate(), lineNumber, 10));
		} else {
			try {
				depositDate = sdf1.parse(collectionFile.getDepositDate());
			} catch (ParseException e1) {
				e1.printStackTrace();
				errors.add(new FormatException(headers.getDepositDate(), lineNumber));
			}
		}
		
		// MES LIQUIDACION
				Date monthLiquidationDate = null;
				if (collectionFile.getMonthLiquidation().length() > 0
						&& collectionFile.getMonthLiquidation().length() != 7) {
					errors.add(new OverflowException(headers.getMonthLiquidation(), lineNumber, 7));
				} else {
					try {
						monthLiquidationDate = sdf2.parse(collectionFile.getMonthLiquidation());
						// creditCard.setExpirationDate(expirationDate);
					} catch (ParseException e1) {
						errors.add(new FormatException(headers.getMonthLiquidation(), lineNumber));
					}
				}


		// CODIGO RESPUESTA
		Short responseCode = null;
		//System.out.println("headers.getResponseCode():"+headers.getResponseCode());
		if (collectionFile.getResponseCode().length()>0) {
			if (collectionFile.getResponseCode().length() > 2) {
				errors.add(new OverflowException(headers.getResponseCode(), lineNumber, 2));
			} else {
				try {
					responseCode = Short.parseShort(collectionFile.getResponseCode());
				} catch (NumberFormatException e) {
					errors.add(new FormatException(headers.getResponseCode(), lineNumber));
				}
			}
		}

		// CODIGO AUTORIZACION
		Integer authorizationCode = null;
		if (collectionFile.getAuthorizationCode().length() > 0) {
			if (collectionFile.getAuthorizationCode().length() > 6) {
				errors.add(new OverflowException(headers.getAuthorizationCode(), lineNumber, 6));
			} else {
				try {
					authorizationCode = Integer.parseInt(collectionFile.getAuthorizationCode());
				} catch (NumberFormatException e) {
					errors.add(new FormatException(headers.getAuthorizationCode(), lineNumber));
				}
			}
		}
		

		// RESPONSE MESSAGE
		CollectionResponseEnum messageResponse = null;
		if (collectionFile.getResponseMessage().length() == 0) {
			errors.add(new NullException(headers.getResponseMessage(), lineNumber));
		} else if (collectionFile.getResponseMessage().length() != 8) {
			errors.add(new OverflowException(headers.getResponseMessage(), lineNumber, 8));
		} else {
			messageResponse = CollectionResponseEnum.findByName(collectionFile.getResponseMessage());
			if (messageResponse == null) {
				errors.add(new FormatException(headers.getResponseMessage(), lineNumber));
			} else {
				// payer.setDocumentType(documentTypeEnum);
			}
		}

		// ACCION
		if (collectionFile.getAction().length() == 0) {
			errors.add(new NullException(headers.getAction(), lineNumber));
		} else if (collectionFile.getAction().length() > 20) {
			errors.add(new OverflowException(headers.getAction(), lineNumber, 20));
		} else {
			// payer.setLastnameMaternalResponsible(saleFile.getLastnameMaternalResponsible());
		}

		// ESTADO TRANSAXXION
		if (collectionFile.getTransactionState().length() == 0) {
			errors.add(new NullException(headers.getTransactionState(), lineNumber));
		} else if (collectionFile.getTransactionState().length() > 20) {
			errors.add(new OverflowException(headers.getTransactionState(), lineNumber, 20));
		} else {
			// payer.setFirstnameResponsible(saleFile.getFirstnameResponsible());
		}

		// NUMERO LOTE
		Integer nroLote = null;
		if (collectionFile.getLoteNumber().length()> 0) {
			if (collectionFile.getLoteNumber().length() > 10) {
				errors.add(new OverflowException(headers.getLoteNumber(), lineNumber, 10));
			} else {
				try {
					nroLote = Integer.parseInt(collectionFile.getLoteNumber());
				} catch (NumberFormatException e) {
					errors.add(new FormatException(headers.getLoteNumber(), lineNumber));
				}
			}
		}

		// CANAL
		if (collectionFile.getChannel().length() > 0) {
			
		if (collectionFile.getChannel().length() > 20) {
			errors.add(new OverflowException(headers.getChannel(), lineNumber, 20));
		} else {
			// payer.setFirstnameResponsible(saleFile.getFirstnameResponsible());
		}
		}

		// MEDIO DE PAGO
		PaymentMethod paymentMethod = null;
		if (collectionFile.getPaymentMethod().length() == 0) {
			errors.add(new NullException(headers.getPaymentMethod(), lineNumber));
		} else if (collectionFile.getPaymentMethod().length() != 2) {
			errors.add(new OverflowException(headers.getPaymentMethod(), lineNumber, 2));
		} else {
			paymentMethod = paymentMethodService.checkIfExist(collectionFile.getPaymentMethod());
			if (paymentMethod == null) {
				errors.add(new FormatException(headers.getPaymentMethod(), lineNumber));
			} 
		}

		// TIPO DE MONEDA
		MoneyTypeEnum moneyType = null;
		if (collectionFile.getMoneyType().length() == 0) {
			errors.add(new NullException(headers.getMoneyType(), lineNumber));
		} else if (collectionFile.getMoneyType().length() > 7) {
			errors.add(new OverflowException(headers.getMoneyType(), lineNumber, 7));
		} else {
			moneyType = MoneyTypeEnum.findByName(collectionFile.getMoneyType());
			if (moneyType == null) {
				errors.add(new FormatException(headers.getMoneyType(), lineNumber));
			} 
		}
		
		// VERIFICA SI EXISTE LA COBRANZA
				if (estimatedDate != null && saleFound !=null) {
					long collectionId = collectionService.checkIfExist(estimatedDate, saleFound.getCode());
					if (collectionId > 0) {
						errors.add(new SaleAlreadyExistException(lineNumber));
					}
				}
		

		if (errors.size() > 0) {
			throw new MultipleErrorsException(errors);
		}

		Collection collection = new Collection(maximumAmount, chargeAmount, estimatedDate, depositDate, monthLiquidationDate, responseCode,
				authorizationCode, messageResponse, collectionFile.getAction(), collectionFile.getTransactionState(),
				nroLote, collectionFile.getChannel(), moneyType, saleFound, paymentMethod);

		return collection;

	}

	/*
	 * public void validateData() {
	 * 
	 * System.out.println("ingreso a validateData");
	 * 
	 * SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
	 * 
	 * Integer lineNumber = 1;
	 * 
	 * try {
	 * 
	 * for (Map<String, String> data : dataList) {
	 * 
	 * Sale sale = saleService.findByCode(data.get("code"));
	 * 
	 * // VALIDA SI LA VENTA EXISTE if (sale != null && sale.getId() > 0) {
	 * 
	 * // VALIDATE MESSAGE RESPONSE CollectionResponseEnum
	 * collectionResponseEnum = CollectionResponseEnum
	 * .findByName(data.get("responseMessage")); if (collectionResponseEnum ==
	 * null) { errors.add(new
	 * DataCollectionResponseMessageNotFoundException(lineNumber,
	 * headers.get("responseMessage"), data.get("responseMessage"))); } else {
	 * 
	 * BigDecimal chargeAmount = new BigDecimal(data.get("chargeAmount"));
	 * 
	 * // OBTIENE CANTIDAD DE COBRANZAS POR VENTA Date authorizationDate =
	 * sdf1.parse(data.get("authorizationDate"));
	 * 
	 * // OBTIENE CANTIDAD DE COBRANZAS DENEGADAS POR DÍA Integer
	 * collectionsDenyByDayLength = 0; List<Collection> collectionsDenyByDay =
	 * collectionService.findByResponseAndAuthorizationDay(
	 * CollectionResponseEnum.DENY, authorizationDate, sale.getCode()); if
	 * (collectionsDenyByDay != null) { collectionsDenyByDayLength =
	 * collectionsDenyByDay.size(); } // OBTIENE CANTIDAD DE COBRANZAS APROBADAS
	 * POR DÍA Integer collectionsAllowByDayLength = 0; List<Collection>
	 * collectionsAllowByDay =
	 * collectionService.findByResponseAndAuthorizationDay(
	 * CollectionResponseEnum.ALLOW, authorizationDate, sale.getCode()); if
	 * (collectionsAllowByDay != null) { collectionsAllowByDayLength =
	 * collectionsAllowByDay.size(); } // OBTIENE CANTIDAD DE COBRANZAS
	 * DENEGADAS POR MES Integer collectionsDenyByMonthLength = 0;
	 * List<Collection> collectionsDenyByMonth =
	 * collectionService.findByResponseAndAuthorizationMonth(
	 * CollectionResponseEnum.DENY, authorizationDate, sale.getCode()); if
	 * (collectionsDenyByMonth != null) { collectionsDenyByMonthLength =
	 * collectionsDenyByMonth.size(); } // OBTIENE CANTIDAD DE COBRANZAS
	 * APROBADAS POR MES Integer collectionsAllowByMonthLength = 0;
	 * List<Collection> collectionsAllowByMonth = collectionService
	 * .findByResponseAndAuthorizationMonth(CollectionResponseEnum.ALLOW,
	 * authorizationDate, sale.getCode()); if (collectionsAllowByMonth != null)
	 * { collectionsAllowByMonthLength = collectionsAllowByMonth.size(); } //
	 * OBTIENE TOTALES Integer collectionsByMonthLength =
	 * collectionsAllowByMonthLength + collectionsDenyByMonthLength; Integer
	 * collectionsByDayLength = collectionsAllowByDayLength +
	 * collectionsDenyByDayLength;
	 * 
	 * // OBTIENE CANTIDAD DE COBRANZAS A REGISTRAR int totalCollectionsAllow =
	 * 0; int totalCollectionsDeny = 0; for (int i = 0; i < dataList.size();
	 * i++) { Map<String, String> data1 = dataList.get(i); if
	 * (data.get("authorizationDate").equals(data1.get("authorizationDate")) &&
	 * data.get("code").equals(data1.get("code"))) { if
	 * (data1.get("responseMessage").equals(CollectionResponseEnum.ALLOW.getName
	 * ())) { totalCollectionsAllow++; } else if
	 * (data1.get("responseMessage").equals(CollectionResponseEnum.DENY.getName(
	 * ))) { totalCollectionsDeny++; } } } // OBTIENE TOTALES int
	 * totalCollections = totalCollectionsAllow + totalCollectionsDeny;
	 * 
	 * 
	 * if (collectionsByDayLength + totalCollections > 5) {// CAMBIA //
	 * TEMPORALMENTE // DE // 2 // A // 5 errors.add(new
	 * DataCollectionMaximumByDayException(lineNumber, headers.get("code"),
	 * headers.get("authorizationDate"), data.get("code"),
	 * data.get("authorizationDate"), collectionsByDayLength,
	 * totalCollections)); } else if (collectionsByMonthLength +
	 * totalCollections > 5) { errors.add(new
	 * DataCollectionMaximumByMonthException(lineNumber, headers.get("code"),
	 * headers.get("authorizationDate"), data.get("code"),
	 * data.get("authorizationDate"), collectionsByMonthLength,
	 * totalCollections)); } else {
	 * 
	 * switch (collectionResponseEnum) {
	 * 
	 * case ALLOW: if (collectionsAllowByDayLength + totalCollectionsAllow > 5)
	 * {// CAMBIA // TEMPORALMENTE // 5 errors.add(new
	 * DataCollectionAllowMaximumByDayException(lineNumber, headers.get("code"),
	 * headers.get("authorizationDate"), data.get("code"),
	 * data.get("authorizationDate"), collectionsAllowByDayLength,
	 * totalCollectionsAllow)); } else if (collectionsAllowByMonthLength +
	 * totalCollectionsAllow > 5) {// CAMBIA // A // 5 errors.add(new
	 * DataCollectionAllowMaximumByMonthException(lineNumber,
	 * headers.get("code"), headers.get("authorizationDate"), data.get("code"),
	 * data.get("authorizationDate"), collectionsAllowByMonthLength,
	 * totalCollectionsAllow)); }
	 * 
	 * // MONTO A CARGAR DEBE SER IGUAL A LA PRIMA DE // LA VENTA. if
	 * (sale.getInsurancePremium().compareTo(chargeAmount) != 0) {
	 * errors.add(new DataCollectionChargeAmountException(lineNumber,
	 * headers.get("chargeAmount"), sale.getInsurancePremium(), chargeAmount));
	 * } break; case DENY:
	 * 
	 * if (collectionsDenyByDayLength + totalCollectionsDeny > 5) {// CAMBIA //
	 * TEMPORALMENTE // DE // 2 // A // 5 errors.add(new
	 * DataCollectionAllowMaximumByMonthException(lineNumber,
	 * headers.get("code"), headers.get("authorizationDate"), data.get("code"),
	 * data.get("authorizationDate"), collectionsDenyByDayLength,
	 * totalCollectionsDeny)); } else if (collectionsDenyByMonthLength +
	 * totalCollectionsDeny > 5) { errors.add(new
	 * DataCollectionAllowMaximumByMonthException(lineNumber,
	 * headers.get("code"), headers.get("authorizationDate"), data.get("code"),
	 * data.get("authorizationDate"), collectionsDenyByMonthLength,
	 * totalCollectionsDeny)); }
	 * 
	 * // MONTO A CARGAR DEBE SER CERO.
	 * 
	 * //if (chargeAmount.intValue() != 0) { // errors.add(new //
	 * DataCollectionChargeAmountException( //
	 * lineNumber,headers.get("chargeAmount"), //
	 * headers.get("responseMessage"))); }
	 * 
	 * if (sale.getInsurancePremium().compareTo(chargeAmount) != 0) {
	 * errors.add(new DataCollectionChargeAmountException(lineNumber,
	 * headers.get("chargeAmount"), sale.getInsurancePremium(), chargeAmount));
	 * }
	 * 
	 * break;
	 * 
	 * default: break; } }
	 * 
	 * }
	 * 
	 * } else { errors.add(new DataSaleNotFoundException(lineNumber,
	 * headers.get("code"), data.get("code"))); }
	 * 
	 * lineNumber++;
	 * 
	 * }
	 * 
	 * } catch (Exception e) { e.printStackTrace(); errors.add(new
	 * ServiceException());
	 * 
	 * }
	 * 
	 * System.out.println("dataList:" + dataList.size());
	 * System.out.println("errors:" + errors.size());
	 * 
	 * }
	 */

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	public StreamedContent getDownloadFile() {
		return downloadFile;
	}

	public void setDownloadFile(StreamedContent downloadFile) {
		this.downloadFile = downloadFile;
	}

}
