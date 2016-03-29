package com.returnsoft.collection.controller;

import java.io.BufferedReader;
import java.io.IOException;
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

import com.returnsoft.collection.entity.Bank;
import com.returnsoft.collection.entity.Notification;
import com.returnsoft.collection.entity.User;
import com.returnsoft.collection.enumeration.NotificationStateEnum;
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
import com.returnsoft.collection.service.NotificationService;
import com.returnsoft.collection.vo.NotificationFile;
import com.returnsoft.generic.util.SessionBean;

@ManagedBean
@ViewScoped
public class LoadUpdateNotificationsController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3885606490310790204L;

	@Inject
	private SessionBean sessionBean;

	@EJB
	private NotificationService notificationService;

	private UploadedFile file;

	private StreamedContent downloadFile;

	public LoadUpdateNotificationsController() {
		System.out.println("LoadUpdateNotificationsController");
		InputStream stream = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext())
				.getResourceAsStream("/resources/templates/tramas_actualizacion_notificaciones.xlsx");
		downloadFile = new DefaultStreamedContent(stream,
				"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
				"tramas_actualizacion_notificaciones.xlsx");
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

			List<NotificationFile> dataList = readFile(file);
			NotificationFile headers = dataList.get(0);
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

			for (NotificationFile notificationFile : dataList) {
				String dataString = notificationFile.getNuicResponsible() + notificationFile.getOrderNumber();
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
			List<Notification> notifications = new ArrayList<Notification>();
			List<Exception> errors = new ArrayList<Exception>();

			for (NotificationFile notificationFile : dataList) {
				try {
					Notification notification = validateFile(notificationFile, headers, lineNumber, bank);
					notifications.add(notification);
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
			
			

			/////////////////////////////////////////////
			// SE ENVIAN LAS NOTIFICACIONES AL SERVICE //
			/////////////////////////////////////////////

			User user = sessionBean.getUser();
			notificationService.updatePhysicalList(notifications, headers, file.getFileName(), user);
			RequestContext.getCurrentInstance().closeDialog(null);

		} catch (Exception e) {
			e.printStackTrace();
			RequestContext.getCurrentInstance().closeDialog(e);
		}

	}

	private List<NotificationFile> readFile(UploadedFile file) throws Exception {

		String strLine = null;
		Integer lineNumber = 1;
		NotificationFile headers = new NotificationFile();
		List<NotificationFile> dataList = new ArrayList<NotificationFile>();
		Integer FILE_ROWS = 7;

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

					headers.setOrderNumber(values[0]);
					headers.setNuicResponsible(values[1]);
					headers.setState(values[2]);
					headers.setCorrelativeNumber(values[3]);
					headers.setSendingAt(values[4]);

					headers.setAnsweringAt(values[5]);
					headers.setReason(values[6]);

					dataList.add(headers);

				} else {

					NotificationFile notificationFile = new NotificationFile();
					notificationFile.setOrderNumber(values[0].trim());
					notificationFile.setNuicResponsible(values[1].trim());
					notificationFile.setState(values[2].trim());
					notificationFile.setCorrelativeNumber(values[3].trim());
					notificationFile.setSendingAt(values[4].trim());
					notificationFile.setAnsweringAt(values[5].trim());
					notificationFile.setReason(values[6].trim());

					dataList.add(notificationFile);

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

	private Notification validateFile(NotificationFile notificationFile, NotificationFile headers, Integer lineNumber,
			Bank bank) throws MultipleErrorsException {

		List<Exception> errors = new ArrayList<Exception>();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
		//SimpleDateFormat sdf2 = new SimpleDateFormat("MM/yyyy");

		// NUIC RESPONSIBLE
		Long nuicResponsible = null;
		if (notificationFile.getNuicResponsible().length() == 0) {
			errors.add(new NullException(headers.getNuicResponsible(), lineNumber));
		} else if (notificationFile.getNuicResponsible().length() > 11) {
			errors.add(new OverflowException(headers.getNuicResponsible(), lineNumber, 11));
		} else {
			try {
				nuicResponsible = Long.parseLong(notificationFile.getNuicResponsible());
			} catch (NumberFormatException e) {
				errors.add(new FormatException(headers.getNuicResponsible(), lineNumber));
			}
		}

		// NUMERO DE ORDEN
		if (notificationFile.getOrderNumber().length() == 0) {
			errors.add(new NullException(headers.getOrderNumber(), lineNumber));
		} else if (notificationFile.getOrderNumber().length() > 45) {
			errors.add(new OverflowException(headers.getOrderNumber(), lineNumber, 45));
		} 

		// MOTIVO
		if (notificationFile.getReason().length() > 100) {
			errors.add(new OverflowException(headers.getReason(), lineNumber, 100));
		}
		
		// FECHA DE ENVIO
		Date sendingAt = null;
		if (notificationFile.getSendingAt().length() > 0
				&& notificationFile.getSendingAt().length() != 10) {
			errors.add(new OverflowException(headers.getSendingAt(), lineNumber, 10));
		} else {
			try {
				sendingAt = sdf1.parse(notificationFile.getSendingAt());
			} catch (ParseException e1) {
				errors.add(new FormatException(headers.getSendingAt(), lineNumber));
			}
		}
		
		// FECHA DE RESPUESTA
				Date answeringAt = null;
				if (notificationFile.getAnsweringAt().length() > 0
						&& notificationFile.getAnsweringAt().length() != 10) {
					errors.add(new OverflowException(headers.getAnsweringAt(), lineNumber, 10));
				} else {
					try {
						answeringAt = sdf1.parse(notificationFile.getAnsweringAt());
					} catch (ParseException e1) {
						errors.add(new FormatException(headers.getSendingAt(), lineNumber));
					}
				}

		// ESTADO DE NOTIFICACION
		NotificationStateEnum state = null;
		if (notificationFile.getState().length() == 0) {
			errors.add(new NullException(headers.getState(), lineNumber));
		} else {
			state = NotificationStateEnum.findByName(notificationFile.getState());
			if (state == null) {
				errors.add(new FormatException(headers.getState(), lineNumber));
			}
		}
		
		// BUSCA LA NOTIFICACION
		Notification notification=null;
		if (nuicResponsible != null && notificationFile.getOrderNumber().length()>0) {
			notification = notificationService.findByKey(nuicResponsible, notificationFile.getOrderNumber());
			if (notification==null) {
				errors.add(new Exception("No se encontró la notificación de la línea "+lineNumber));
			}
		}

		if (errors.size() > 0) {
			throw new MultipleErrorsException(errors);
		}
		
		notification.setSendingAt(sendingAt);
		notification.setAnsweringAt(answeringAt);
		notification.setReason(notificationFile.getReason());
		notification.setState(state);

		return notification;

	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	public StreamedContent getDownloadFile() {
		System.out.println("getDownloadFile");
		if (downloadFile==null) {
			System.out.println("es nulo");
		}else{
			System.out.println("no es nulo");
		}
		return downloadFile;
	}

	public void setDownloadFile(StreamedContent downloadFile) {
		this.downloadFile = downloadFile;
	}
	
	

}
