package com.returnsoft.collection.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
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

import com.returnsoft.collection.entity.CreditCard;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.entity.User;
import com.returnsoft.collection.enumeration.CreditCardValidationEnum;
import com.returnsoft.collection.exception.BankNotSelectedException;
import com.returnsoft.collection.exception.FileExtensionException;
import com.returnsoft.collection.exception.FileNotFoundException;
import com.returnsoft.collection.exception.FileRowsInvalidException;
import com.returnsoft.collection.exception.FileRowsZeroException;
import com.returnsoft.collection.exception.FormatException;
import com.returnsoft.collection.exception.MultipleErrorsException;
import com.returnsoft.collection.exception.NullException;
import com.returnsoft.collection.exception.OverflowException;
import com.returnsoft.collection.exception.SaleDuplicateException;
import com.returnsoft.collection.exception.SaleNotFoundException;
import com.returnsoft.collection.exception.UserLoggedNotFoundException;
import com.returnsoft.collection.service.CreditCardService;
import com.returnsoft.collection.service.SaleService;
import com.returnsoft.collection.vo.CreditCardFile;
import com.returnsoft.generic.util.SessionBean;

@ManagedBean
@ViewScoped
public class LoadCreditCardsController implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1559544306814870412L;

	
	@Inject
	private SessionBean sessionBean;

	@EJB
	private CreditCardService creditCardService;

	@EJB
	private SaleService saleService;

	private UploadedFile file;

	private StreamedContent downloadFile;

	public LoadCreditCardsController() {
		System.out.println("Ingreso al constructor");
		InputStream stream = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext())
				.getResourceAsStream("/resources/templates/tramas_tarjetas.xlsx");
		downloadFile = new DefaultStreamedContent(stream,
				"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "tramas_tarjetas.xlsx");
		
	}
	
	public void uploadFile() {
		/* AjaxBehaviorEvent event */
		System.out.println("uploadFile()");

		// Part file = saleStateFile;
		// Integer FILE_ROWS = SALE_FILE_ROWS;

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

			List<CreditCardFile> dataList = readFile(file);
			CreditCardFile headers = dataList.get(0);
			dataList.remove(0);

			// VALIDA SI EL ARCHIVO ESTA VACIO
			if (dataList.size() == 0) {
				throw new FileRowsZeroException();
			}

			//////////////////////////
			// VALIDANDO DUPLICADOS //
			//////////////////////////

			Set<String> dataSet = new HashSet<String>();
			Set<Integer> errorSet = new HashSet<Integer>();
			int lineNumber = 2;

			for (CreditCardFile creditCardFile : dataList) {
				String dataString = creditCardFile.getCode();
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
			List<CreditCard> creditCards = new ArrayList<CreditCard>();
			List<Exception> errors = new ArrayList<Exception>();

			for (CreditCardFile creditCardFile : dataList) {
				try {
					CreditCard creditCard = validateFile(creditCardFile, headers, lineNumber);
					creditCards.add(creditCard);
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

			//////////////////////////////////////////////
			// SE ENVIAN LAS ACTUALIZACIONES AL SERVICE //
			//////////////////////////////////////////////

			User createdBy = sessionBean.getUser();
			creditCardService.updateCreditCardList(creditCards, headers, file.getFileName(), createdBy);
			RequestContext.getCurrentInstance().closeDialog(null);

		} catch (Exception e) {
			e.printStackTrace();
			RequestContext.getCurrentInstance().closeDialog(e);
		}

	}

	private List<CreditCardFile> readFile(UploadedFile file) throws Exception {

		String strLine = null;
		Integer lineNumber = 1;
		CreditCardFile headers = new CreditCardFile();
		List<CreditCardFile> dataList = new ArrayList<CreditCardFile>();
		Integer FILE_ROWS = 6;

		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(file.getInputstream(), StandardCharsets.UTF_8));
			while ((strLine = br.readLine()) != null) {

				String[] values = strLine.split("\\|", -1);
				if (values.length != FILE_ROWS) {
					throw new FileRowsInvalidException(lineNumber,FILE_ROWS);
				}

				if (lineNumber == 1) {

					headers.setCode(values[0]);
					headers.setNumber(values[1]);
					headers.setExpirationDate(values[2]);
					headers.setState(values[3]);
					headers.setDaysOfDefault(values[4]);
					headers.setValidation(values[5]);
					dataList.add(headers);

				} else {
					CreditCardFile creditCardFile = new CreditCardFile();
					creditCardFile.setCode(values[0].trim());
					creditCardFile.setNumber(values[1].trim());
					creditCardFile.setExpirationDate(values[2].trim());
					creditCardFile.setState(values[3].trim());
					creditCardFile.setDaysOfDefault(values[4].trim());
					creditCardFile.setValidation(values[5].trim());
					dataList.add(creditCardFile);
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

	private CreditCard validateFile(CreditCardFile creditCardFile, CreditCardFile headers, Integer lineNumber)
			throws MultipleErrorsException {

		List<Exception> errors = new ArrayList<Exception>();
		//SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdf2 = new SimpleDateFormat("MM/yyyy");

		// CODE
		Sale saleFound = null;
		if (creditCardFile.getCode().length() == 0) {
			errors.add(new NullException(headers.getCode(), lineNumber));
		} else if (creditCardFile.getCode().length() != 10) {
			errors.add(new OverflowException(headers.getCode(), lineNumber, 10));
		} else {
			saleFound = saleService.findByCode(creditCardFile.getCode());
			if (saleFound == null) {
				errors.add(new SaleNotFoundException(headers.getCode(), lineNumber));
			} else {
				// saleState = saleFound.getSaleState();
			}
		}
		
		// NUMERO TARJETA DE CREDITO
		Long creditCardNumber = null;
		if (creditCardFile.getNumber().length() > 0 && creditCardFile.getNumber().length() != 16) {
			errors.add(new OverflowException(headers.getNumber(), lineNumber, 16));
		} else if (creditCardFile.getNumber().length() > 0) {
			try {
				creditCardNumber = Long.parseLong(creditCardFile.getNumber());
				// creditCard.setNumber(creditCardNumber);
			} catch (NumberFormatException e) {
				errors.add(new FormatException(headers.getNumber(), lineNumber));
			}
		}
		
		// FECHA DE VENCIMIENTO DE TARJETA
		Date expirationDate = null;
		if (creditCardFile.getExpirationDate().length() > 0
				&& creditCardFile.getExpirationDate().length() != 7) {
			errors.add(new OverflowException(headers.getExpirationDate(), lineNumber, 7));
		} else {
			try {
				expirationDate = sdf2.parse(creditCardFile.getExpirationDate());
				// creditCard.setExpirationDate(expirationDate);
			} catch (ParseException e1) {
				errors.add(new FormatException(headers.getExpirationDate(), lineNumber));
			}
		}
		
		// ESTADO DE TARJETA DE CREDITO
		if (creditCardFile.getState().length() > 20) {
			errors.add(new OverflowException(headers.getState(), lineNumber, 20));
		} else {
			// creditCard.setState(saleFile.getCreditCardState());
		}
		
		// DIAS DE MORA
		Integer daysOfDefault = null;
		if (creditCardFile.getDaysOfDefault().length() > 4) {
			errors.add(new OverflowException(headers.getDaysOfDefault(), lineNumber, 4));
		} else if (creditCardFile.getDaysOfDefault().length() > 0) {
			try {
				daysOfDefault = Integer.parseInt(creditCardFile.getDaysOfDefault());
				// creditCard.setDaysOfDefault(daysOfDefault);
			} catch (NumberFormatException e) {
				errors.add(new FormatException(headers.getDaysOfDefault(), lineNumber));
			}
		}

		// VALIDATION
		CreditCardValidationEnum validation = null;
		if (creditCardFile.getValidation().length() == 0) {
			errors.add(new NullException(headers.getValidation(), lineNumber));
		} else {
			System.out.println();
			validation = CreditCardValidationEnum.findByName(creditCardFile.getValidation());
			if (validation == null) {
				errors.add(new FormatException(headers.getValidation(), lineNumber));
			} else {
				// saleState.setState(saleStateEnum);
			}
		}

		

		if (errors.size() > 0) {
			throw new MultipleErrorsException(errors);
		}

		CreditCard creditCard = saleFound.getCreditCard();
		creditCard.setNumber(creditCardNumber);
		creditCard.setExpirationDate(expirationDate);
		creditCard.setState(creditCardFile.getState());
		creditCard.setDaysOfDefault(daysOfDefault);
		creditCard.setValidation(validation);
		
		
		return creditCard;

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
