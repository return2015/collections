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
import com.returnsoft.collection.entity.Collection;
import com.returnsoft.collection.entity.CollectionPeriod;
import com.returnsoft.collection.entity.CreditCard;
import com.returnsoft.collection.entity.Lote;
import com.returnsoft.collection.entity.Payer;
import com.returnsoft.collection.entity.PaymentMethod;
import com.returnsoft.collection.entity.Product;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.entity.SaleState;
import com.returnsoft.collection.entity.User;
import com.returnsoft.collection.enumeration.CollectionResponseEnum;
import com.returnsoft.collection.enumeration.DocumentTypeEnum;
import com.returnsoft.collection.enumeration.SaleStateEnum;
import com.returnsoft.collection.exception.BankInvalidException;
import com.returnsoft.collection.exception.BankNotFoundException;
import com.returnsoft.collection.exception.BankNotSelectedException;
import com.returnsoft.collection.exception.CollectionChargeAmountException;
import com.returnsoft.collection.exception.CollectionDuplicateException;
import com.returnsoft.collection.exception.FileExtensionException;
import com.returnsoft.collection.exception.FileMultipleErrorsException;
import com.returnsoft.collection.exception.FileNotFoundException;
import com.returnsoft.collection.exception.FileRowsInvalidException;
import com.returnsoft.collection.exception.FileRowsZeroException;
import com.returnsoft.collection.exception.FormatException;
import com.returnsoft.collection.exception.NullException;
import com.returnsoft.collection.exception.OverflowException;
import com.returnsoft.collection.exception.SaleAlreadyExistException;
import com.returnsoft.collection.exception.SaleDuplicateException;
import com.returnsoft.collection.exception.SaleNotFoundException;
import com.returnsoft.collection.exception.SaleStateNoActiveException;
import com.returnsoft.collection.exception.SaleStateNotFoundException;
import com.returnsoft.collection.exception.UserLoggedNotFoundException;
import com.returnsoft.collection.service.BankService;
import com.returnsoft.collection.service.CollectionPeriodService;
import com.returnsoft.collection.service.CollectionService;
import com.returnsoft.collection.service.LoteService;
import com.returnsoft.collection.service.PaymentMethodService;
import com.returnsoft.collection.service.ProductService;
import com.returnsoft.collection.service.SaleService;
import com.returnsoft.collection.util.CollectionFile;
import com.returnsoft.collection.util.FacesUtil;
import com.returnsoft.collection.util.SaleFile;
import com.returnsoft.collection.util.SessionBean;

@Named
@RequestScoped
public class SearchLoteController implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5267773436408492212L;

	@EJB
	private LoteService loteService;
	
	@Inject
	private FacesUtil facesUtil;
	
	@Inject
	private SessionBean sessionBean;

	//SEARCH
	private Date loteDate;
	private List<Lote> lotes;
	private Lote loteSelected;
	
	///////
	
	//SALES
	private Part saleFile;
	private Integer SALE_FILE_ROWS = 49;
	
	//COLLECTIONS
	private Part collectionFile;
	private Integer COLLECTION_FILE_ROWS = 14;
	
	//CREDIT CARD
	private Part creditCardFile;
	private Integer CREDITCARD_FILE_ROWS = 6;
	
	//SALE STATE
	private Part saleStateFile;
	private Integer SALESTATE_FILE_ROWS = 7;
	
	//REPAYMENT
	private Part repaymentFile;
	private Integer REPAYMENT_FILE_ROWS = 5;
	
	
	
	@EJB
	private CollectionPeriodService collectionPeriodService;

	@EJB
	private BankService bankService;

	@EJB
	private ProductService productService;
	
	@EJB
	private SaleService saleService;
	
	@EJB
	private PaymentMethodService paymentMethodService;
	
	@EJB
	private CollectionService collectionService;
	
	
	public SearchLoteController(){
		//System.out.println("Se construye SearchLoteController");
		//facesUtil = new FacesUtil();
	}
	
	public String initialize() {
		//System.out.println("inicializando SearchLoteController");
		try {
			if (sessionBean == null || sessionBean.getUser() == null || sessionBean.getUser().getId() == null) {
				throw new UserLoggedNotFoundException();
			}
			return null;
			
		} catch (UserLoggedNotFoundException e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
			return "login.xhtml?faces-redirect=true";
		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
			return null;
		}

	}
	
	public void search(){
		try {
			
			if (sessionBean == null || sessionBean.getUser() == null || sessionBean.getUser().getId() == null) {
				throw new UserLoggedNotFoundException();
			}
			
			//System.out.println("loteDate:"+loteDate);
			if (loteDate!=null) {
				lotes = loteService.findByDate(loteDate);
			}
		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
		}
	}
	
	
	
	
	public void downloadCollectionsFile() {
		try {
			
			if (sessionBean == null || sessionBean.getUser() == null || sessionBean.getUser().getId() == null) {
				throw new UserLoggedNotFoundException();
			}

			ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext()
					.getContext();
			String separator = System.getProperty("file.separator");
			String rootPath = servletContext.getRealPath(separator);
			String fileName = rootPath + "resources" + separator + "templates" + separator + "tramas_.xlsx";
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
	
	
	
	
	
	
	
	public void validateCollectionFile() {
		/*AjaxBehaviorEvent event*/
		System.out.println("validateSalesFile()");
		
		Part file = collectionFile;
		Integer FILE_ROWS = COLLECTION_FILE_ROWS;
		
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
			
			
			BufferedReader 
			br = new BufferedReader(
					new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
		
		String strLine = null;
		Integer numLine = 0;
		
		CollectionFile headers = new CollectionFile();
		List<CollectionFile> dataList = new ArrayList<CollectionFile>();

		while ((strLine = br.readLine()) != null) {

			System.out.println("numLine:" + numLine);

			if (numLine == 0) {
				// SE LEE CABECERA
				String[] values = strLine.split("\\|", -1);
				if (values.length != FILE_ROWS) {
					throw new FileRowsInvalidException(FILE_ROWS);
				} else {
					
					headers.setSaleCode(values[0]);
					headers.setMaximumAmount(values[1]);
					headers.setChargeAmount(values[2]);
					headers.setReceiptNumber(values[3]);

					headers.setEstimatedDate(values[4]);
					headers.setAuthorizationDate(values[5]);
					headers.setDepositDate(values[6]);
					headers.setResponseCode(values[7]);

					headers.setAuthorizationCode(values[8]);
					headers.setResponseMessage(values[9]);
					headers.setAction(values[10]);
					headers.setTransactionState(values[11]);
					headers.setLoteNumber(values[12]);

					headers.setChannel(values[13]);
					headers.setPaymentMethod(values[14]);

				}
			} else {

				String[] values = strLine.split("\\|", -1);

				if (values.length != FILE_ROWS) {
					throw new FileRowsInvalidException(FILE_ROWS);
				} else {

					CollectionFile collectionFile = new CollectionFile();
					
					collectionFile.setSaleCode(values[0]);
					collectionFile.setMaximumAmount(values[1]);
					collectionFile.setChargeAmount(values[2]);
					collectionFile.setReceiptNumber(values[3]);

					collectionFile.setEstimatedDate(values[4]);
					collectionFile.setAuthorizationDate(values[5]);
					collectionFile.setDepositDate(values[6]);
					collectionFile.setResponseCode(values[7]);

					collectionFile.setAuthorizationCode(values[8]);
					collectionFile.setResponseMessage(values[9]);
					collectionFile.setAction(values[10]);
					collectionFile.setTransactionState(values[11]);
					collectionFile.setLoteNumber(values[12]);

					collectionFile.setChannel(values[13]);
					collectionFile.setPaymentMethod(values[14]);
					
					dataList.add(collectionFile);

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

			for (CollectionFile collectionFile : dataList) {
				String dataString = collectionFile.getPaymentMethod() + collectionFile.getSaleCode() + collectionFile.getAuthorizationDate();
				numLine++;
				if (!dataSet.add(dataString)) {
					errorSet.add(numLine);
				}
			}

			for (Integer errorLineNumber : errorSet) {
				errors.add(new CollectionDuplicateException(errorLineNumber));
			}
			
			/////////////////////////////////////////
			//////////////////////////
			//////////////////////////////////////////
			
			if (errors.size()==0) {
				validateCollectionData(headers, dataList, file.getSubmittedFileName());
			}else{
				throw new FileMultipleErrorsException(errors);
			}
			
		}else{
			throw new FileRowsZeroException();
		}
		
			
			facesUtil.sendConfirmMessage("Se creó el lote satisfactorimente.");
			
			
		} catch (FileMultipleErrorsException e) {
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
	
	
	public void validateCollectionData(CollectionFile headers, List<CollectionFile> dataList, String filename) throws Exception{

		System.out.println("Validando vacíos");

		//try {

			Bank bank = sessionBean.getBank();
			User user = sessionBean.getUser();
			Date currentDate = new Date();
			//List<Product> productsEntity = productService.getAll();
			//List<CollectionPeriod> collectionPeriodsEntity = collectionPeriodService.getAll();
			List<PaymentMethod> paymentMethodsEntity = paymentMethodService.getAll();
			
			SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat sdf2 = new SimpleDateFormat("MM/yyyy");
			Integer lineNumber = 1;
			
			List<Collection> collections = new ArrayList<Collection>();
			List<Exception> errors = new ArrayList<Exception>();

			for (CollectionFile collectionFile : dataList) {
				
				System.out.println("verificando ..");

				Collection collection = new Collection();
				/*Payer payer = new Payer();
				SaleState saleState = new SaleState();
				CreditCard creditCard = new CreditCard();*/

				// SALE CODE
				if (collectionFile.getSaleCode().length() == 0) {
					errors.add(new NullException(headers.getSaleCode(), lineNumber));
				} else if (collectionFile.getSaleCode().length() != 10) {
					errors.add(new OverflowException(headers.getSaleCode(), lineNumber,10));
				} else {
					Sale saleFound = saleService.findByCode(collectionFile.getSaleCode());
					//VALIDA QUE LA VENTA EXISTA
					if (saleFound == null) {
						errors.add(new SaleNotFoundException(headers.getSaleCode(), lineNumber));
					} 
					//VALIDA QUE EL BANCO NO SEA NULO
					if (saleFound.getBank() == null) {
						errors.add(new BankNotFoundException(headers.getSaleCode(), lineNumber));	
					}
					//VALIDA QUE EL BANCO SEA EL MISMO QUE LA SESION
					if (saleFound.getBank().getId() != bank.getId()) {
						errors.add(new BankInvalidException(headers.getSaleCode(), lineNumber));	
					}
					//VALIDA QUE EL ESTADO NO SEA NULO
					if (saleFound.getSaleState()== null || saleFound.getSaleState().getState()==null) {
						errors.add(new SaleStateNotFoundException(headers.getSaleCode(), lineNumber));	
					}
					//VALIDA QUE EL ESTADO SEA ACTIVO
					if (saleFound.getSaleState().getState()!= SaleStateEnum.ACTIVE) {
						errors.add(new SaleStateNoActiveException(headers.getSaleCode(), lineNumber));	
					}
					
					collection.setSale(saleFound);	
					
				}

				// MAXIMUM AMOUNT
				if (collectionFile.getMaximumAmount().length() == 0) {
					errors.add(new NullException(headers.getMaximumAmount(), lineNumber));
				} else if (collectionFile.getMaximumAmount().length() > 8) {
					errors.add(new OverflowException(headers.getMaximumAmount(), lineNumber,11));
				} else {
					try {
						BigDecimal maximumAmount = new BigDecimal(collectionFile.getMaximumAmount());
						if (maximumAmount.scale() > 2) {
							errors.add(new FormatException(headers.getMaximumAmount(), lineNumber));
						} else {
							collection.setMaximumAmount(maximumAmount);
						}
					} catch (NumberFormatException e) {
						errors.add(new FormatException(headers.getMaximumAmount(), lineNumber));
					}
					
				}

				// MENSAJE DE RESPUESTA
				if (collectionFile.getResponseMessage().length() == 0) {
					errors.add(new NullException(headers.getResponseMessage(), lineNumber));
				} else {
					CollectionResponseEnum collectionResponseEnum = CollectionResponseEnum.findByName(collectionFile.getResponseMessage());
					if (collectionResponseEnum == null) {
						errors.add(new FormatException(headers.getResponseMessage(), lineNumber));
					} else {
						collection.setResponseMessage(collectionResponseEnum);
						//IMPORTE CARGO
						if (collectionFile.getChargeAmount().length() == 0) {
							errors.add(new NullException(headers.getChargeAmount(), lineNumber));
						}else{
							try {
								BigDecimal chargeAmount = new BigDecimal(collectionFile.getChargeAmount());
								if (collectionResponseEnum.equals(CollectionResponseEnum.DENY) && chargeAmount.compareTo(BigDecimal.ZERO) > 0) {
									errors.add(new CollectionChargeAmountException(headers.getChargeAmount(), lineNumber));
								}else{
									if (chargeAmount.scale() > 2) {
									errors.add(new FormatException(headers.getChargeAmount(), lineNumber));
									} else {
										collection.setChargeAmount(chargeAmount);
									}
								}
							} catch (NumberFormatException e) {
								errors.add(new FormatException(headers.getChargeAmount(), lineNumber));
							}
						}
					}
				}
				
				
				 // FECHA ESTIMADA 
				if (collectionFile.getEstimatedDate().length() == 0) {
					errors.add(new NullException(headers.getEstimatedDate(), lineNumber));
				} else {
					try {
						Date estimatedDate = sdf1.parse(collectionFile.getEstimatedDate());
						collection.setEstimatedDate(estimatedDate);
					} catch (ParseException e1) {
						errors.add(new FormatException(headers.getEstimatedDate(), lineNumber));
					}
				}
				
				 // FECHA AUTORIZACION
				if (collectionFile.getAuthorizationDate().length() == 0) {
					errors.add(new NullException(headers.getAuthorizationDate(), lineNumber));
				} else {
					try {
						Date authorizationDate = sdf1.parse(collectionFile.getAuthorizationDate());
						
						
						//OBTIENE CANTIDAD DE COBRANZAS DENEGADAS POR DÍA
						Integer collectionsDenyByDayLength= collectionService
								.findByResponseAndAuthorizationDay(CollectionResponseEnum.DENY,authorizationDate,collectionFile.getSaleCode());
						//OBTIENE CANTIDAD DE COBRANZAS APROBADAS POR DÍA
						Integer collectionsAllowByDayLength=collectionService
								.findByResponseAndAuthorizationDay(CollectionResponseEnum.ALLOW,authorizationDate,collectionFile.getSaleCode());
						//OBTIENE CANTIDAD DE COBRANZAS DENEGADAS POR MES
						Integer collectionsDenyByMonthLength=collectionService
								.findByResponseAndAuthorizationMonth(CollectionResponseEnum.DENY,authorizationDate,collectionFile.getSaleCode());
						//OBTIENE CANTIDAD DE COBRANZAS APROBADAS POR MES
						Integer collectionsAllowByMonthLength= collectionService
								.findByResponseAndAuthorizationMonth(CollectionResponseEnum.ALLOW,authorizationDate,collectionFile.getSaleCode());
						//OBTIENE TOTALES
						Integer collectionsByMonthLength=collectionsAllowByMonthLength+collectionsDenyByMonthLength;
						Integer collectionsByDayLength=collectionsAllowByDayLength+collectionsDenyByDayLength;
						
						
						collection.setAuthorizationDate(authorizationDate);
						
					} catch (ParseException e1) {
						errors.add(new FormatException(headers.getAuthorizationDate(), lineNumber));
					}
				}
				
				
				 // FECHA DE DEPÓSITO
				if (collectionFile.getDepositDate().length() == 0) {
					errors.add(new NullException(headers.getDepositDate(), lineNumber));
				} else {
					try {
						Date depositDate = sdf1.parse(collectionFile.getDepositDate());
						collection.setDepositDate(depositDate);
					} catch (ParseException e1) {
						errors.add(new FormatException(headers.getDepositDate(), lineNumber));
					}
				}
				
				// RESPONSE CODE
				if (collectionFile.getResponseCode().length() == 0) {
					errors.add(new NullException(headers.getResponseCode(), lineNumber));
				} else if (collectionFile.getResponseCode().length() != 2) {
					errors.add(new OverflowException(headers.getResponseCode(), lineNumber,2));
				} else {
					try {
						Short responseCode = Short.parseShort(collectionFile.getResponseCode());
						collection.setResponseCode(responseCode);
					} catch (NumberFormatException e) {
						errors.add(new FormatException(headers.getResponseCode(), lineNumber));
					}
				}
				
				// AUTORIZATION CODE
				if (collectionFile.getAuthorizationCode().length() != 0 && collectionFile.getAuthorizationCode().length() > 2) {
					errors.add(new OverflowException(headers.getAuthorizationCode(), lineNumber,2));
				} else {
					try {
						Integer authorizationCode = Integer.parseInt(collectionFile.getAuthorizationCode());
						collection.setAuthorizationCode(authorizationCode);
					} catch (NumberFormatException e) {
						errors.add(new FormatException(headers.getAuthorizationCode(), lineNumber));
					}
				}
				
				
				// ACCION
				if (collectionFile.getAction().length() == 0) {
					errors.add(new NullException(headers.getAction(), lineNumber));
				} else if (collectionFile.getAction().length() > 20) {
					errors.add(new OverflowException(headers.getAction(), lineNumber,20));
				} else {
					collection.setAction(collectionFile.getAction());
				}
				
				// ESTADO TRANSACCION
				if (collectionFile.getTransactionState().length() == 0) {
					errors.add(new NullException(headers.getTransactionState(), lineNumber));
				} else if (collectionFile.getTransactionState().length() > 20) {
					errors.add(new OverflowException(headers.getTransactionState(), lineNumber,20));
				} else {
					collection.setTransactionState(collectionFile.getTransactionState());
				}
				
				
				// NUMERO DE LOTE	
				if (collectionFile.getLoteNumber().length() != 0 && collectionFile.getLoteNumber().length() > 10) {
					errors.add(new OverflowException(headers.getLoteNumber(), lineNumber,10));
				} else {
					try {
						Integer loteNumber = Integer.parseInt(collectionFile.getLoteNumber());
						collection.setLoteNumber(loteNumber);
					} catch (NumberFormatException e) {
						errors.add(new FormatException(headers.getLoteNumber(), lineNumber));
					}
				}
				
				//CANAL
				if (collectionFile.getChannel().length() == 0) {
					errors.add(new NullException(headers.getChannel(), lineNumber));
				} else if (collectionFile.getChannel().length() > 20) {
					errors.add(new OverflowException(headers.getChannel(), lineNumber,20));
				} else {
					collection.setChannel(collectionFile.getChannel());
				}
				
				// MEDIO DE PAGO
				if (collectionFile.getPaymentMethod().length() == 0) {
					errors.add(new NullException(headers.getPaymentMethod(), lineNumber));
				} else if (collectionFile.getPaymentMethod().length() != 2) {
					errors.add(new OverflowException(headers.getPaymentMethod(), lineNumber,2));
				} else {
					PaymentMethod paymentMethod = null;
					for (PaymentMethod paymentMethodEntity : paymentMethodsEntity) {
						if (collectionFile.getPaymentMethod().equals(paymentMethodEntity.getCode())) {
							paymentMethod = paymentMethodEntity;
							break;
						}
					}
					if (paymentMethod == null) {
						errors.add(new FormatException(headers.getPaymentMethod(), lineNumber));
					} else {
						collection.setPaymentMethod(paymentMethod);
					}
				}
				
				
				/*headers.setSaleCode(values[0]);
				headers.setMaximumAmount(values[1]);
				headers.setChargeAmount(values[2]);
				headers.setReceiptNumber(values[3]);

				headers.setEstimatedDate(values[4]);
				headers.setAuthorizationDate(values[5]);
				headers.setDepositDate(values[6]);
				headers.setResponseCode(values[7]);

				headers.setAuthorizationCode(values[8]);
				headers.setResponseMessage(values[9]);
				headers.setAction(values[10]);
				headers.setTransactionState(values[11]);
				headers.setLoteNumber(values[12]);

				headers.setChannel(values[13]);
				headers.setPaymentMethod(values[14]);*/
				
				
				
				/*
				// APELLIDO MATERNO RESPONSABLE DE PAGO
				if (saleFile.getLastnameMaternalResponsible().length() == 0) {
					errors.add(new NullException(headers.getLastnameMaternalResponsible(), lineNumber));
				} else if (saleFile.getLastnameMaternalResponsible().length() > 50) {
					errors.add(new OverflowException(headers.getLastnameMaternalResponsible(), lineNumber,50));
				} else {
					payer.setLastnameMaternalResponsible(saleFile.getLastnameMaternalResponsible());
				}

				// NOMBRES DE RESPONSABLE DE PAGO
				if (saleFile.getFirstnameResponsible().length() == 0) {
					errors.add(new NullException(headers.getFirstnameResponsible(), lineNumber));
				} else if (saleFile.getFirstnameResponsible().length() > 50) {
					errors.add(new OverflowException(headers.getFirstnameResponsible(), lineNumber,50));
				} else {
					payer.setFirstnameResponsible(saleFile.getFirstnameResponsible());
				}

				// NUMERO TARJETA DE CREDITO
				if (saleFile.getCreditCardNumber().length() > 0 && saleFile.getCreditCardNumber().length() != 16) {
					errors.add(new OverflowException(headers.getCreditCardNumber(), lineNumber,16));
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
					errors.add(new OverflowException(headers.getAccountNumber(), lineNumber,10));
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
					errors.add(new OverflowException(headers.getCreditCardExpirationDate(), lineNumber,7));
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
					errors.add(new OverflowException(headers.getCreditCardState(), lineNumber,20));
				} else {
					creditCard.setState(saleFile.getCreditCardState());
				}

				// DIAS DE MORA
				if (saleFile.getCreditCardDaysOfDefault().length() > 4) {
					errors.add(new OverflowException(headers.getCreditCardDaysOfDefault(), lineNumber,4));
				} else if (saleFile.getCreditCardDaysOfDefault().length() > 0) {
					try {
						Integer daysOfDefault = Integer.parseInt(saleFile.getCreditCardDaysOfDefault());
						creditCard.setDaysOfDefault(daysOfDefault);
					} catch (NumberFormatException e) {
						errors.add(new FormatException(headers.getCreditCardDaysOfDefault(), lineNumber));
					}
				}

				
				// TELEFONO2
				if (saleFile.getPhone2().length() > 9) {
					errors.add(new OverflowException(headers.getPhone2(), lineNumber,9));
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

			
				

				// FECHA DE VENTA
				if (saleFile.getDate().length() == 0) {
					errors.add(new NullException(headers.getDate(), lineNumber));
				} else if (saleFile.getDate().length() != 10) {
					errors.add(new OverflowException(headers.getDate(), lineNumber,10));
				} else {
					try {
						Date saleDate = sdf1.parse(saleFile.getDate());
						sale.setDate(saleDate);
					} catch (ParseException e1) {
						errors.add(new FormatException(headers.getDate(), lineNumber));
					}
				}

				// PRODUCTO
				if (saleFile.getProduct().length() == 0) {
					errors.add(new NullException(headers.getProduct(), lineNumber));
				} else if (saleFile.getProduct().length() != 2) {
					errors.add(new OverflowException(headers.getProduct(), lineNumber,2));
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

				

				// PERIODO DE COBRO
				if (saleFile.getCollectionPeriod().length() == 0) {
					errors.add(new NullException(headers.getCollectionPeriod(), lineNumber));
				} else if (saleFile.getCollectionPeriod().length() > 45) {
					errors.add(new OverflowException(headers.getCollectionPeriod(), lineNumber,45));
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
					errors.add(new OverflowException(headers.getCollectionType(), lineNumber,15));
				} else {
					sale.setCollectionType(saleFile.getCollectionType());
				}

				// BANCO
				if (saleFile.getBank().length() == 0) {
					errors.add(new NullException(headers.getBank(), lineNumber));
				} else if (saleFile.getBank().length() != 2) {
					errors.add(new OverflowException(headers.getBank(), lineNumber,2));
				} else if (!saleFile.getBank().equals(bank.getCode())) {
					errors.add(new FormatException(headers.getBank(), lineNumber));
				} else {
					sale.setBank(bank);
				}

				// PRIMA
				if (saleFile.getInsurancePremium().length() == 0) {
					errors.add(new NullException(headers.getInsurancePremium(), lineNumber));
				} else if (saleFile.getInsurancePremium().length() > 8) {
					errors.add(new OverflowException(headers.getInsurancePremium(), lineNumber,8));
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
					errors.add(new OverflowException(headers.getAuditDate(), lineNumber,10));
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
					errors.add(new OverflowException(headers.getAuditUser(), lineNumber,15));
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

				

				

				// VERIFICA SI EXISTE LA VENTA
				if (sale.getBank()!=null && sale.getProduct()!=null && sale.getCollectionPeriod() !=null) {
					Boolean saleExist = saleService.checkIfExistSale(sale.getNuicInsured(), sale.getDate(),
							sale.getBank().getId(), sale.getProduct().getId(), sale.getCollectionPeriod().getId());
					if (saleExist) {
						errors.add(new SaleAlreadyExistException(lineNumber));
					}
				}
				*/

				//sale.setCreatedBy(user);
				collection.setCreatedAt(currentDate);

				collections.add(collection);

				lineNumber++;
				
			}

			System.out.println("errors:" + errors.size());
			
			if (errors.size()==0) {
				loteService.addTypeCollection(collections, filename, headers, user.getId());
				
			}else{
				throw new FileMultipleErrorsException(errors);
			}

		//} catch (Exception e) {
		//	e.printStackTrace();
		//}

	}


	public Date getLoteDate() {
		return loteDate;
	}

	public void setLoteDate(Date loteDate) {
		this.loteDate = loteDate;
	}

	public List<Lote> getLotes() {
		return lotes;
	}

	public void setLotes(List<Lote> lotes) {
		this.lotes = lotes;
	}

	public Lote getLoteSelected() {
		return loteSelected;
	}

	public void setLoteSelected(Lote loteSelected) {
		this.loteSelected = loteSelected;
	}

	public Part getSaleFile() {
		return saleFile;
	}

	public void setSaleFile(Part saleFile) {
		this.saleFile = saleFile;
	}

	public Part getCollectionFile() {
		return collectionFile;
	}

	public void setCollectionFile(Part collectionFile) {
		this.collectionFile = collectionFile;
	}

	public Part getCreditCardFile() {
		return creditCardFile;
	}

	public void setCreditCardFile(Part creditCardFile) {
		this.creditCardFile = creditCardFile;
	}

	public Part getSaleStateFile() {
		return saleStateFile;
	}

	public void setSaleStateFile(Part saleStateFile) {
		this.saleStateFile = saleStateFile;
	}

	public Part getRepaymentFile() {
		return repaymentFile;
	}

	public void setRepaymentFile(Part repaymentFile) {
		this.repaymentFile = repaymentFile;
	}

	/*public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}*/


	
	
	
	

}
