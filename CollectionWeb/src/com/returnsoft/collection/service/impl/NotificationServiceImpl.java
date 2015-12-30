package com.returnsoft.collection.service.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Date;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.faces.context.FacesContext;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletContext;

import com.returnsoft.collection.eao.NotificationEao;
import com.returnsoft.collection.eao.SaleEao;
import com.returnsoft.collection.entity.Notification;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.enumeration.BankLetterEnum;
import com.returnsoft.collection.enumeration.NotificationTypeEnum;
import com.returnsoft.collection.exception.BankLetterNotFoundException;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.service.NotificationService;

@Stateless
//@TransactionManagement(TransactionManagementType.CONTAINER)

public class NotificationServiceImpl implements NotificationService {
	
	@EJB
	private SaleEao saleEao;
	
	@EJB
	private NotificationEao notificationEao;
	
	@Resource
	private SessionContext context;
	
	@Resource(lookup = "mail/Falabella")
	private Session mailFalabella;

	@Resource(lookup = "mail/GNB")
	private Session mailGNB;
	
	//@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void add(Notification notification) throws ServiceException {
		try {
			
			Sale saleFound = saleEao.findById(notification.getSale().getId());
			
			//se agrega notification 
			notificationEao.add(notification);
			/*int i=5;
			int j =0;
			int v = i/j; 
			System.out.println(v);*/
			
			//System.out.println("termino la transacción");
			
			if (notification.getType().equals(NotificationTypeEnum.PHYSICAL)) {
				if (saleFound.getPhysicalNotifications()==null) {
					saleFound.setPhysicalNotifications((short)1);
				}else{
					saleFound.setPhysicalNotifications((short)(saleFound.getPhysicalNotifications()+1));	
				}
				
				
			}else if (notification.getType().equals(NotificationTypeEnum.MAIL)) {
				
				BankLetterEnum bankLetterEnum = BankLetterEnum.findById(saleFound.getBank().getId());
				
				if (bankLetterEnum==null) {
					throw new BankLetterNotFoundException(saleFound.getBank().getName());
				}
				
				if (saleFound.getVirtualNotifications()==null) {
					saleFound.setVirtualNotifications((short)1);
				}else{
					saleFound.setVirtualNotifications((short)(saleFound.getVirtualNotifications()+1));	
				}
				
				
				// SE ENVIA EL EMAIL
				String email = saleFound.getPayer().getMail();
				String code = saleFound.getCode();
				String names = saleFound.getPayer().getFirstnameResponsible() + " "
				+ saleFound.getPayer().getLastnamePaternalResponsible() + " "
				+ saleFound.getPayer().getLastnameMaternalResponsible();
				sendMail(email, names, code, bankLetterEnum);
			}
			
			saleFound.setNotification(notification);
			saleFound = saleEao.update(saleFound);
			

		} catch (Exception e) {
			e.printStackTrace();
			context.setRollbackOnly();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
	}
	
	/*public void createPhysicalNotifications(Integer userId, Date sendingAt, String searchType, Long nuicResponsible, Date saleDateStartedAt,Date saleDateEndedAt,Date affiliationDate, Date sendingDate, List<NotificationStateEnum> notificationStates, Short bankId, SaleStateEnum saleState, NotificationTypeEnum notificationType) throws ServiceException, MultipleErrorsException{
		try {
			
			List<Sale> sales = null;
			
			if (searchType.equals("dni")) {
				sales = saleEao.findByNuicResponsible(nuicResponsible);
			} else if (searchType.equals("saleData")) {
				sales = saleEao.findBySaleData2(saleDateStartedAt, saleDateEndedAt, affiliationDate,
						sendingDate, notificationStates, bankId, saleState, notificationType);
			}
			
			List<Exception> errors = new ArrayList<Exception>();

			if (sales != null && sales.size() > 0) {

				for (Sale sale : sales) {

				

					// VALIDA EL ESTADO DE LA ULTIMA NOTIFICACION
					if (sale.getNotification() != null) {
						if (sale.getNotification().getState().getPending() == true) {
							errors.add(new NotificationPendingException(sale.getCode()));
						}
					}

					// VALIDA SI LA VENTA NO ESTA ACTIVA
					if (!sale.getSaleState().getState().equals(SaleStateEnum.ACTIVE)) {
						errors.add(new SaleStateNoActiveException(sale.getCode()));
					}

					// VALIDA DATOS DE RESPONSABLE
					if (sale.getSaleState().getState().equals(SaleStateEnum.DOWN)) {
						errors.add(new SaleStateNoActiveException(sale.getCode()));
					}
					if (sale.getPayer().getFirstnameResponsible() == null
							|| sale.getPayer().getFirstnameResponsible().trim().length() == 0) {
						errors.add(new PayerDataNullException("El nombre", sale.getCode()));
					}
					if (sale.getPayer().getLastnamePaternalResponsible() == null
							|| sale.getPayer().getLastnamePaternalResponsible().trim().length() == 0) {
						errors.add(new PayerDataNullException("El apellido paterno", sale.getCode()));
					}
					if (sale.getPayer().getLastnameMaternalResponsible() == null
							|| sale.getPayer().getLastnameMaternalResponsible().trim().length() == 0) {
						errors.add(new PayerDataNullException("El apellido materno", sale.getCode()));
					}
					if (sale.getPayer().getAddress() == null || sale.getPayer().getAddress().trim().length() == 0) {
						errors.add(new PayerDataNullException("La dirección", sale.getCode()));
					}
					if (sale.getPayer().getProvince() == null
							|| sale.getPayer().getProvince().trim().length() == 0) {
						errors.add(new PayerDataNullException("La provincia", sale.getCode()));//
					}
					if (sale.getPayer().getDepartment() == null
							|| sale.getPayer().getDepartment().trim().length() == 0) {
						errors.add(new PayerDataNullException("El departamento", sale.getCode()));//
					}
					if (sale.getPayer().getDistrict() == null
							|| sale.getPayer().getDistrict().trim().length() == 0) {
						errors.add(new PayerDataNullException("El distrito", sale.getCode()));//
					}

					// VALIDA CANTIDAD DE NOTIFICACIONES ENVIADAS

					// Si tiene menos de 3 envios virtuales
					if (sale.getVirtualNotifications() < 3) {
						if (sale.getPhysicalNotifications() > 1) {
							// no se agrega porque tiene 2 envíos físicos y
							// menos de 3 virtuales.
							errors.add(new NotificationLimit2Exception(sale.getCode()));
						}
					} else {
						if (sale.getPhysicalNotifications() > 0) {
							// No se agrega porque ya tiene 1 envío físico y
							// 3
							// envíos virtuales.
							errors.add(new NotificationLimit1Exception(sale.getCode()));
						}
					}

				}

				if (errors.size() > 0) {
					
					throw new MultipleErrorsException(errors);
					
				} else {

					List<Exception> errors2 = new ArrayList<Exception>();
					for (Sale sale : sales) {
						Notification notification = new Notification();
						notification.setSendingAt(sendingAt);
						notification.setSale(sale);
						notification.setType(NotificationTypeEnum.PHYSICAL);
						notification.setState(NotificationStateEnum.SENDING);
						User user = new User();
						user.setId(userId);
						notification.setCreatedBy(user);
						notification.setCreatedAt(new Date());
						try {
							notificationEao.add(notification);
						} catch (Exception e) {
							e.printStackTrace();
							errors2.add(e);
						}
					}


				}

			} else {
				throw new SalesNotFoundException();
			}
			
		} catch (MultipleErrorsException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);
			}else{
				throw new ServiceException();
			}
		}
	}*/
	
	/*public void createVirtualNotifications(Integer userId, String searchType, Long nuicResponsible, Date saleDateStartedAt,Date saleDateEndedAt,Date affiliationDate, Date sendingDate, List<NotificationStateEnum> notificationStates, Short bankId, SaleStateEnum saleState, NotificationTypeEnum notificationType) throws ServiceException, MultipleErrorsException{
		
		try {
			
			List<Sale> sales = null;
			
			if (searchType.equals("dni")) {
				sales = saleEao.findByNuicResponsible(nuicResponsible);
			} else if (searchType.equals("saleData")) {
				sales = saleEao.findBySaleData2(saleDateStartedAt, saleDateEndedAt, affiliationDate,
						sendingDate, notificationStates, bankId, saleState, notificationType);
			}
			
			List<Exception> errors = new ArrayList<Exception>();

			if (sales != null && sales.size() > 0) {

				for (Sale sale : sales) {

				

					// VALIDA EL ESTADO DE LA ULTIMA NOTIFICACION NO SEA
					// CERRADO
					if (sale.getNotification() != null) {
						if (sale.getNotification().getState().getPending() == false) {
							errors.add(new NotificationClosedException(sale.getCode()));
						}
					}

					// VALIDA SI LA VENTA NO ESTA ACTIVA
					if (!sale.getSaleState().getState().equals(SaleStateEnum.ACTIVE)) {
						errors.add(new SaleStateNoActiveException(sale.getCode()));
					}

					// VALIDA DATOS DE RESPONSABLE
					if (sale.getSaleState().getState().equals(SaleStateEnum.DOWN)) {
						errors.add(new SaleStateNoActiveException(sale.getCode()));
					}
					if (sale.getPayer().getFirstnameResponsible() == null
							|| sale.getPayer().getFirstnameResponsible().trim().length() == 0) {
						errors.add(new PayerDataNullException("El nombre", sale.getCode()));
					}
					if (sale.getPayer().getLastnamePaternalResponsible() == null
							|| sale.getPayer().getLastnamePaternalResponsible().trim().length() == 0) {
						errors.add(new PayerDataNullException("El apellido paterno", sale.getCode()));
					}
					if (sale.getPayer().getLastnameMaternalResponsible() == null
							|| sale.getPayer().getLastnameMaternalResponsible().trim().length() == 0) {
						errors.add(new PayerDataNullException("El apellido materno", sale.getCode()));
					}
					if (sale.getPayer().getAddress() == null || sale.getPayer().getAddress().trim().length() == 0) {
						errors.add(new PayerDataNullException("La dirección", sale.getCode()));
					}
					if (sale.getPayer().getProvince() == null
							|| sale.getPayer().getProvince().trim().length() == 0) {
						errors.add(new PayerDataNullException("La provincia", sale.getCode()));//
					}
					if (sale.getPayer().getDepartment() == null
							|| sale.getPayer().getDepartment().trim().length() == 0) {
						errors.add(new PayerDataNullException("El departamento", sale.getCode()));//
					}
					if (sale.getPayer().getDistrict() == null
							|| sale.getPayer().getDistrict().trim().length() == 0) {
						errors.add(new PayerDataNullException("El distrito", sale.getCode()));//
					}

					// VALIDA CANTIDAD DE NOTIFICACIONES VIRTUALES ENVIADAS
					// Solo puede tener 3 notificaciones virtuales.
					if (sale.getVirtualNotifications() > 2) {
						errors.add(new NotificationLimit3Exception(sale.getCode()));
					}

				}

				if (errors.size() > 0) {
					
					throw new MultipleErrorsException(errors);
					
				} else {

					//User user = sessionBean.getUser();
					//List<Exception> errors2 = new ArrayList<Exception>();
					for (Sale sale : sales) {

						// SE ENVIA EL EMAIL

						String email = sale.getPayer().getMail();

						String code = sale.getCode();

						String names = sale.getPayer().getFirstnameResponsible() + " "
								+ sale.getPayer().getLastnamePaternalResponsible() + " "
								+ sale.getPayer().getLastnameMaternalResponsible();

						

						sendMail(email, names, code, sale.getCommerce().getBank().getId());

						// SE CREA LA NOTIFICACION VIRTUAL
						Notification notification = new Notification();
						notification.setSendingAt(new Date());
						notification.setSale(sale);
						notification.setType(NotificationTypeEnum.MAIL);
						notification.setState(NotificationStateEnum.SENDING);
						User user = new User();
						user.setId(userId);
						notification.setCreatedBy(user);
						notification.setCreatedAt(new Date());
						try {
							notificationEao.add(notification);
						} catch (Exception e) {
							e.printStackTrace();
							
						}
					}

					//search();

				

				}

			} else {
				throw new SalesNotFoundException();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
		
	}*/
	
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Notification> findBySale(Long saleId) throws ServiceException{
		try {
			
			return notificationEao.findBySaleId(saleId);
			
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
		
	}
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Notification findById(Integer notificationId) throws ServiceException {
		try {

			Notification notification = notificationEao.findById(notificationId);

			return notification;

		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
	}
	
	
	
	public Notification update(Notification notification) throws ServiceException {

		try {
			
			notification = notificationEao.update(notification);
			return notification;

		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
	}
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void sendMail(String email, String names, String code, BankLetterEnum bankLetterEnum) throws Exception{
		//try {

			System.out.println("email:" + email);
			System.out.println("names:" + names);

			//if (email != null && names != null && bankId != null) {

				//BankLetterEnum bankLetterEnum = BankLetterEnum.findById(bankId);

				//if (bankLetterEnum != null) {
					
					

					

					try {
						
						
						ServletContext servletContext=(ServletContext) FacesContext.getCurrentInstance ().getExternalContext().getContext();
						String separator=System.getProperty("file.separator");
						String rootPath= servletContext.getRealPath(separator);
						//String fileName = rootPath+"resources"+separator+"templates"+separator+bankLetterEnum.getTemplateMail();
						Session sessionSelected = null;
						String subject = "";
						String filename = "";
						if (bankLetterEnum.equals(BankLetterEnum.FALABELLA)) {
							sessionSelected = mailFalabella;
							subject="Asistencia Falabella";
							filename=rootPath+"resources"+separator+"templates"+separator+"mailFalabella.html";
						}else if(bankLetterEnum.equals(BankLetterEnum.GNB)) {
							sessionSelected = mailGNB;
							subject="Asistencia GNB";
							filename=rootPath+"resources"+separator+"templates"+separator+"mailGNB.html";
						}
						
						String logoName = rootPath+"resources"+separator+"templates"+separator+"logoGEA.jpg";
						String htmlText="";
						BufferedReader br = new BufferedReader(new FileReader(filename));
						try {
						    StringBuilder sb = new StringBuilder();
						    String line = br.readLine();

						    while (line != null) {
						        sb.append(line);
						        sb.append(System.lineSeparator());
						        line = br.readLine();
						    }
						    htmlText = sb.toString();
						}catch(Exception e){
							e.printStackTrace();
							throw e;
						} finally {
						    br.close();
						}
						String urlConditioned="http://190.107.180.164:9096/siscob/faces/download_conditioned.xhtml?code="+code;
						String urlLetter="http://190.107.180.164:9096/siscob/faces/download_letter.xhtml?code="+code;
						htmlText=String.format(htmlText, names,urlLetter,urlConditioned);
						
						
						MimeMessage message = new MimeMessage(sessionSelected);
						message.setFrom(new InternetAddress(sessionSelected.getProperty("mail.from")));
						InternetAddress[] address = { new InternetAddress(email), new InternetAddress(sessionSelected.getProperty("mail.from"))};
						message.setRecipients(Message.RecipientType.TO, address);
						message.setSubject(subject);
						message.setSentDate(new Date());
						
						
						
						//
				        // This HTML mail have to 2 part, the BODY and the embedded image
				        //
				        MimeMultipart multipart = new MimeMultipart("related");
				        
				        // first part  (the html)
				        BodyPart messageBodyPart = new MimeBodyPart();
				        messageBodyPart.setContent(htmlText, "text/html");
				        // add it
				        multipart.addBodyPart(messageBodyPart);
				        
				        // second part (the image)
				        messageBodyPart = new MimeBodyPart();
				        DataSource fds = new FileDataSource(logoName);
				        messageBodyPart.setDataHandler(new DataHandler(fds));
				        messageBodyPart.setHeader("Content-ID","<image>");
				        // add it
				        multipart.addBodyPart(messageBodyPart);

				        // put everything together
				        message.setContent(multipart);

				        // para enviar condicionado
						// message.addHeader("Disposition-Notification-To",bankLetterEnum.getMail());
				        
						Transport.send(message);
						//System.out.println("se envío el mensaje");

					}  catch (Exception ex) {
						ex.printStackTrace();
						throw ex;
					}
				//}

			//}

		//}

	/*} catch (Exception e) {
		e.printStackTrace();
		
	}*/
	}


	
	

}
