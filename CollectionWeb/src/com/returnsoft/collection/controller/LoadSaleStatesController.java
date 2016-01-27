package com.returnsoft.collection.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
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
import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;
import javax.servlet.http.Part;

import com.returnsoft.collection.entity.Bank;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.entity.SaleState;
import com.returnsoft.collection.entity.User;
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
import com.returnsoft.collection.exception.SaleDuplicateException;
import com.returnsoft.collection.exception.SaleNotFoundException;
import com.returnsoft.collection.exception.UserLoggedNotFoundException;
import com.returnsoft.collection.service.SaleService;
import com.returnsoft.collection.service.SaleStateService;
import com.returnsoft.collection.util.FacesUtil;
import com.returnsoft.collection.util.SaleStateFile;
import com.returnsoft.collection.util.SessionBean;

@Named
@RequestScoped
public class LoadSaleStatesController implements Serializable {


	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4578242184575371868L;
	
	@Inject
	private FacesUtil facesUtil;

	@Inject
	private SessionBean sessionBean;
	

	private Part file;

	
	private Integer FILE_ROWS = 7;
	
	
	@EJB
	private SaleStateService saleStateService;
	
	@EJB
	private SaleService saleService;
	
	
	public void downloadSaleStatesFile() {
		try {

			if (sessionBean == null || sessionBean.getUser() == null || sessionBean.getUser().getId() == null) {
				throw new UserLoggedNotFoundException();
			}

			ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext()
					.getContext();
			String separator = System.getProperty("file.separator");
			String rootPath = servletContext.getRealPath(separator);
			String fileName = rootPath + "resources" + separator + "templates" + separator + "tramas_estados.xlsx";
			File file = new File(fileName);
			InputStream pdfInputStream = new FileInputStream(file);

			FacesContext facesContext = FacesContext.getCurrentInstance();
			ExternalContext externalContext = facesContext.getExternalContext();
			externalContext.setResponseContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"tramas_estados.xlsx\"");

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
	
	public void validateSaleStatesFile() {
		/* AjaxBehaviorEvent event */
		System.out.println("validateStatesFile()");

		//Part file = saleStateFile;
		//Integer FILE_ROWS = SALE_FILE_ROWS;

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

			SaleStateFile headers = new SaleStateFile();
			List<SaleStateFile> dataList = new ArrayList<SaleStateFile>();

			while ((strLine = br.readLine()) != null) {

				System.out.println("numLine:" + numLine);

				if (numLine == 0) {
					// SE LEE CABECERA
					String[] values = strLine.split("\\|", -1);
					if (values.length != FILE_ROWS) {
						throw new FileRowsInvalidException(FILE_ROWS);
					} else {

						headers.setCode(values[0]);
						headers.setState(values[1]);
						headers.setDate(values[2]);
						headers.setUser(values[3]);
						headers.setChannel(values[4]);

						headers.setReason(values[5]);
						headers.setObservation(values[6]);
						

					}
				} else {

					String[] values = strLine.split("\\|", -1);

					if (values.length != FILE_ROWS) {
						throw new FileRowsInvalidException(FILE_ROWS);
					} else {

						SaleStateFile saleStateFile = new SaleStateFile();
						saleStateFile.setCode(values[0].trim());
						saleStateFile.setState(values[1].trim());
						saleStateFile.setDate(values[2].trim());
						saleStateFile.setUser(values[3].trim());
						saleStateFile.setChannel(values[4].trim());
						saleStateFile.setReason(values[5].trim());
						saleStateFile.setObservation(values[6].trim());
						dataList.add(saleStateFile);

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

				for (SaleStateFile saleStateFile : dataList) {
					String dataString = saleStateFile.getCode();
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
					validateSaleStateData(headers, dataList, file.getSubmittedFileName());
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
	
	public void validateSaleStateData(SaleStateFile headers, List<SaleStateFile> dataList, String filename) throws Exception {

		System.out.println("Validando vacíos");

		// try {

		Bank bank = sessionBean.getBank();
		User user = sessionBean.getUser();
		//Date currentDate = new Date();
		//List<Product> productsEntity = productService.getAll();
		//List<CollectionPeriod> collectionPeriodsEntity = collectionPeriodService.getAll();

		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
		//SimpleDateFormat sdf2 = new SimpleDateFormat("MM/yyyy");
		Integer lineNumber = 1;

		List<SaleState> saleStates = new ArrayList<SaleState>();
		List<Exception> errors = new ArrayList<Exception>();

		for (SaleStateFile saleStateFile : dataList) {

			System.out.println("verificando ..");

			//Sale sale = new Sale();
			//Payer payer = new Payer();
			SaleState saleState = null;
			//CreditCard creditCard = new CreditCard();

			// CODE
			if (saleStateFile.getCode().length() == 0) {
				errors.add(new NullException(headers.getCode(), lineNumber));
			} else if (saleStateFile.getCode().length() != 10) {
				errors.add(new OverflowException(headers.getCode(), lineNumber, 10));
			} else {
				
				Sale saleFound = saleService.findByCode(saleStateFile.getCode());

				if (saleFound == null) {
					errors.add(new SaleNotFoundException(headers.getCode(), lineNumber));
				} else {
					saleState = saleFound.getSaleState();
				}
			}

			// ESTADO DE VENTA
			if (saleStateFile.getState().length() == 0) {
				errors.add(new NullException(headers.getState(), lineNumber));
			} else {
				SaleStateEnum saleStateEnum = SaleStateEnum.findByName(saleStateFile.getState());
				if (saleStateEnum == null) {
					errors.add(new FormatException(headers.getState(), lineNumber));
				} else {
					saleState.setState(saleStateEnum);
				}
			}

			// FECHA DE ESTADO DE VENTA
			if (saleStateFile.getDate().length() > 0 && saleStateFile.getDate().length() != 10) {
				errors.add(new OverflowException(headers.getDate(), lineNumber, 10));
			} else if (saleStateFile.getDate().length() > 0) {
				try {
					Date stateDate = sdf1.parse(saleStateFile.getDate());
					saleState.setDate(stateDate);
				} catch (ParseException e) {
					errors.add(new FormatException(headers.getDate(), lineNumber));
				}
			}

			// USUARIO DE ESTADO
			if (saleStateFile.getUser().length() > 15) {
				errors.add(new OverflowException(headers.getUser(), lineNumber, 15));
			} else {
				saleState.setUser(saleStateFile.getUser());
			}

			// CANAL DE ESTADO
			if (saleStateFile.getChannel().length() > 15) {
				errors.add(new OverflowException(headers.getChannel(), lineNumber, 15));
			} else {
				saleState.setChannel(saleStateFile.getChannel());
			}

			// MOTIVO DE ESTADO
			if (saleStateFile.getReason().length() > 50) {
				errors.add(new OverflowException(headers.getReason(), lineNumber, 50));
			} else {
				saleState.setReason(saleStateFile.getReason());
			}

			// OBSERVACION DE ESTADO
			if (saleStateFile.getObservation().length() > 2500) {
				errors.add(new OverflowException(headers.getObservation(), lineNumber, 2500));
			} else {
				saleState.setObservation(saleStateFile.getObservation());
			}
			
			
			//sale.setSaleState(saleState);

			//payer.setSale(sale);
			//creditCard.setSale(sale);
			//saleState.setSale(sale);

			saleStates.add(saleState);

			lineNumber++;

		}

		System.out.println("errors:" + errors.size());

		if (errors.size() == 0) {
			saleStateService.updateSaleStateList(saleStates, filename, headers, user.getId(), bank.getId());

		} else {
			throw new MultipleErrorsException(errors);
		}

		// } catch (Exception e) {
		// e.printStackTrace();
		// }

	}
	

	public Part getFile() {
		return file;
	}

	public void setFile(Part file) {
		this.file = file;
	}
	
	
	

}
