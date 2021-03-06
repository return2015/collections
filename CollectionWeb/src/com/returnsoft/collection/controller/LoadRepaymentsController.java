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
import com.returnsoft.collection.entity.Repayment;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.entity.User;
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
import com.returnsoft.collection.service.RepaymentService;
import com.returnsoft.collection.service.SaleService;
import com.returnsoft.collection.vo.RepaymentFile;
import com.returnsoft.generic.util.SessionBean;

@ManagedBean
@ViewScoped
public class LoadRepaymentsController implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1028392915692233854L;

	private UploadedFile file;

	private StreamedContent downloadFile;

	
	// private List<SaleState> states;
	/*private Integer FILE_ROWS = 5;

	private List<Exception> errors;
	private Map<String, String> headers;
	private List<Map<String, String>> dataList;*/
	
	//private final String[] saleStates = { "ACTIVO", "BAJA" };

	@EJB
	private RepaymentService repaymentService;
	
	@EJB
	private CollectionService collectionService;
	
	@EJB
	private SaleService saleService;
	
	@Inject
	private SessionBean sessionBean;
	
	//private FacesUtil facesUtil;

	public LoadRepaymentsController() {
		//System.out.println("Ingreso al constructor");
		//facesUtil = new FacesUtil();
		System.out.println("LoadRepaymentsController");
		InputStream stream = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext())
				.getResourceAsStream("/resources/templates/tramas_extornos.xlsx");
		downloadFile = new DefaultStreamedContent(stream,
				"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "tramas_extornos.xlsx");
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

			List<RepaymentFile> dataList = readFile(file);
			RepaymentFile headers = dataList.get(0);
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

			for (RepaymentFile repaymentFile : dataList) {
				String dataString = repaymentFile.getCode();
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
			List<Repayment> repayments = new ArrayList<Repayment>();
			List<Exception> errors = new ArrayList<Exception>();

			for (RepaymentFile repaymentFile : dataList) {
				try {
					Repayment repayment = validateFile(repaymentFile, headers, lineNumber, bank);
					repayments.add(repayment);
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
			repaymentService.addRepaymentList(repayments, headers, file.getFileName(), user);
			RequestContext.getCurrentInstance().closeDialog(null);

		} catch (Exception e) {
			e.printStackTrace();
			RequestContext.getCurrentInstance().closeDialog(e);
		}

	}
	
	private List<RepaymentFile> readFile(UploadedFile file) throws Exception {

		String strLine = null;
		Integer lineNumber = 1;
		RepaymentFile headers = new RepaymentFile();
		List<RepaymentFile> dataList = new ArrayList<RepaymentFile>();
		Integer FILE_ROWS = 5;

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

					headers.setCode(values[0]);
					headers.setInsurancePremiumNumber(values[1]);
					headers.setReturnedAmount(values[2]);
					headers.setReturnedDate(values[3]);
					headers.setPaymentDate(values[4]);
					
					dataList.add(headers);

				} else {

					RepaymentFile repaymentFile = new RepaymentFile();

					repaymentFile.setCode(values[0].trim());
					repaymentFile.setInsurancePremiumNumber(values[1].trim());
					repaymentFile.setReturnedAmount(values[2].trim());
					repaymentFile.setReturnedDate(values[3].trim());
					repaymentFile.setPaymentDate(values[4].trim());


					dataList.add(repaymentFile);

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
	
	private Repayment validateFile(RepaymentFile repaymentFile, RepaymentFile headers, Integer lineNumber,
			Bank bank) throws MultipleErrorsException {

		List<Exception> errors = new ArrayList<Exception>();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
		// SimpleDateFormat sdf2 = new SimpleDateFormat("MM/yyyy");

		// CODE
		Sale saleFound = null;
		if (repaymentFile.getCode().length() == 0) {
			errors.add(new NullException(headers.getCode(), lineNumber));
		} else if (repaymentFile.getCode().length() != 10) {
			errors.add(new OverflowException(headers.getCode(), lineNumber, 10));
		} else {
			saleFound = saleService.findByCode(repaymentFile.getCode());
			if (saleFound == null) {
				errors.add(new SaleNotFoundException(headers.getCode(), lineNumber));
			} else {
				// saleState = saleFound.getSaleState();
			}
		}
		
		// NUMERO DE PRIMAS
		Integer insurancePremiumNumber = null;
		if (repaymentFile.getInsurancePremiumNumber().length() > 4) {
			errors.add(new OverflowException(headers.getInsurancePremiumNumber(), lineNumber, 4));
		} else if (repaymentFile.getInsurancePremiumNumber().length() > 0) {
			try {
				insurancePremiumNumber = Integer.parseInt(repaymentFile.getInsurancePremiumNumber());
				// creditCard.setDaysOfDefault(daysOfDefault);
			} catch (NumberFormatException e) {
				errors.add(new FormatException(headers.getInsurancePremiumNumber(), lineNumber));
			}
		}
				

		// IMPORTE MAXIMO
		BigDecimal returnedAmount = null;
		if (repaymentFile.getReturnedAmount().length() == 0) {
			errors.add(new NullException(headers.getReturnedAmount(), lineNumber));
		} else if (repaymentFile.getReturnedAmount().length() > 11) {
			errors.add(new OverflowException(headers.getReturnedAmount(), lineNumber, 11));
		} else {
			try {
				returnedAmount = new BigDecimal(repaymentFile.getReturnedAmount());
				if (returnedAmount.scale() > 2) {
					errors.add(new DecimalException(lineNumber, headers.getReturnedAmount(), 2));
				}
			} catch (NumberFormatException e) {
				errors.add(new FormatException(headers.getReturnedAmount(), lineNumber));
			}
		}


		// FECHA DE DEVOLUCION
		Date returnedDate = null;
		if (repaymentFile.getReturnedDate().length() == 0) {
			errors.add(new NullException(headers.getReturnedDate(), lineNumber));
		} else if (repaymentFile.getReturnedDate().length() != 10) {
			errors.add(new OverflowException(headers.getReturnedDate(), lineNumber, 10));
		} else {
			try {
				returnedDate = sdf1.parse(repaymentFile.getReturnedDate());
				
			} catch (ParseException e1) {
				errors.add(new FormatException(headers.getReturnedDate(), lineNumber));
			}
		}
		
		// FECHA DE PAGO
				Date paymentDate = null;
				if (repaymentFile.getPaymentDate().length() == 0) {
					errors.add(new NullException(headers.getPaymentDate(), lineNumber));
				} else if (repaymentFile.getPaymentDate().length() != 10) {
					errors.add(new OverflowException(headers.getPaymentDate(), lineNumber, 10));
				} else {
					try {
						paymentDate = sdf1.parse(repaymentFile.getPaymentDate());
						
					} catch (ParseException e1) {
						errors.add(new FormatException(headers.getPaymentDate(), lineNumber));
					}
				}
			


		
		// VERIFICA SI EXISTE EL EXTORNO
				if (returnedAmount != null && saleFound !=null) {
					long collectionId = repaymentService.checkIfExist(saleFound.getCode(),returnedAmount);
					if (collectionId > 0) {
						errors.add(new SaleAlreadyExistException(lineNumber));
					}
				}
		

		if (errors.size() > 0) {
			throw new MultipleErrorsException(errors);
		}

		Repayment repayment = new Repayment(insurancePremiumNumber, returnedAmount, returnedDate, paymentDate, saleFound);

		return repayment;

	}

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
	

	
	
	/*public void validateDuplicates(){
		
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
		
	}*/

	/*public void getData() {

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

	}*/

	/*public void validateDataNull() {

		System.out.println("ingreso a validateDataNull");
		
		Integer lineNumber = 1;

		
				for (Map<String, String> data : dataList) {

					// SE VALIDA NOT NULL

					if (data.get("code").length() == 0) {
						errors.add(new DataColumnNullException(lineNumber,headers.get("code")));
					}


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

	}*/

	/*public void validateDataSize() {
		
		Integer lineNumber = 1;
		
				for (Map<String, String> data : dataList) {

					if (data.get("code").length() > 20) {
						errors.add(new DataColumnLengthException(lineNumber,headers.get("code"),data.get("code").length(),20));
					}

					

					if (data.get("insurancePremiumNumber").length() > 6) {
						errors.add(new DataColumnLengthException(lineNumber,headers.get("insurancePremiumNumber"),data.get("insurancePremiumNumber").length(),6));
					}
					
					lineNumber++;

				}

				
	}*/

	/*@SuppressWarnings("unused")
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
						
						Integer insurancePremiumNumber = Integer.parseInt(data
								.get("insurancePremiumNumber"));
					} catch (NumberFormatException e) {
						errors.add(new DataColumnNumberException(lineNumber, headers.get("insurancePremiumNumber")));
					}
					
					lineNumber++;

				}

				

		System.out.println("dataList:" + dataList.size());
		System.out.println("errors:" + errors.size());

	}*/
	
	/*
	public void validateData() {

		System.out.println("ingreso a validateData");

		try {
		Integer lineNumber = 1;
		
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");

		//SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");

		
				for (Map<String, String> data : dataList) {


						Sale sale = saleService.findByCode(data
								.get("code"));

						// VALIDA SI LA VENTA EXISTE
						if (sale != null && sale.getId() > 0) {

							// VALIDA SI LA VENTA ESTA DE BAJA
							// SE RETIRO ESTA VALIDACION
							

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
							
							//VALIDA QUE el n�mero de primas sea menor o igual que "la cantidad de cobranzas aprobadas de la venta" - "cantidad de extornos de la venta").
							Integer collectionsAllowCount=0;
							List<Collection> collections = collectionService.findAllowsBySale(sale.getId());
							if (collections!=null) {
								collectionsAllowCount=collections.size();
							}
							Integer repaymentsCount=0;
							List<Repayment> repayments = repaymentService.findBySale(sale.getId());
							if (repayments!=null) {
								repaymentsCount=repayments.size();
							}
							
							Integer collectionsTotal=collectionsAllowCount-repaymentsCount;
							
							Integer insurancePremiumNumber = Integer.parseInt(data.get("insurancePremiumNumber"));
							
							if (insurancePremiumNumber>collectionsTotal) {
								errors.add(new DataRepaymentInsurancePremiumException(lineNumber, headers.get("insurancePremiumNumber"),data.get("insurancePremiumNumber"),collectionsTotal));
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

	}*/
	
	
	/*public void createRepayments() {

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

			

	}*/

	/*public String load() {
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

	}*/
	
	

}
