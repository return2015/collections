package com.returnsoft.collection.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Date;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
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

import com.returnsoft.collection.eao.SaleEao;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.enumeration.BankLetterEnum;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.service.MailingService;

@Stateless
public class MailingServiceImpl implements MailingService {

	@EJB
	private SaleEao saleEao;

	/*
	 * @Resource(lookup = "EMailMe") private Session mailSession;
	 */

	@Resource(lookup = "mail/Falabella")
	private Session mailFalabella;

	@Resource(lookup = "mail/GNB")
	private Session mailGNB;

	@Override
	// @Schedule(minute="*/20", second="*",hour="*", persistent=false)
	public void mailerDaemon() throws ServiceException {
		try {
			
			System.out.println("mailerDaemon..");
			List<Sale> sales = saleEao.getNotConditioned();
			System.out.println("cantidad de ventas encontradas:" + sales.size());
			
			for (Sale sale : sales) {

				String email = sale.getPayer().getMail();
				System.out.println("email:" + email);
				String names = sale.getPayer().getFirstnameResponsible() + " "
						+ sale.getPayer().getLastnamePaternalResponsible() + " "
						+ sale.getPayer().getLastnameMaternalResponsible();
				System.out.println("names:" + names);

				if (email != null && names != null) {

					BankLetterEnum bankLetterEnum = BankLetterEnum.findById(sale.getCommerce().getBank().getId());

					if (bankLetterEnum != null) {
						
						Session sessionSelected = null;

						switch (bankLetterEnum) {
						case FALABELLA:
							sessionSelected = mailFalabella;
							break;
						case GNB:
							sessionSelected = mailGNB;
							break;
						default:
							break;
						}

						try {

							MimeMessage message = new MimeMessage(sessionSelected);
							message.setFrom(new InternetAddress(sessionSelected.getProperty("mail.from")));
							InternetAddress[] address = { new InternetAddress(email) };
							message.setRecipients(Message.RecipientType.TO, address);
							message.setSubject(bankLetterEnum.getSubject());
							message.setSentDate(new Date());
							
							ServletContext servletContext=(ServletContext) FacesContext.getCurrentInstance ().getExternalContext().getContext();
							String separator=System.getProperty("file.separator");
							String rootPath= servletContext.getRealPath(separator);
							String fileName = rootPath+"resources"+separator+"templates"+separator+bankLetterEnum.getTemplateMail();
							String logoName = rootPath+"resources"+separator+"templates"+separator+"logoGEA.jpg";
							String htmlText="";
							BufferedReader br = new BufferedReader(new FileReader(fileName));
							try {
							    StringBuilder sb = new StringBuilder();
							    String line = br.readLine();

							    while (line != null) {
							        sb.append(line);
							        sb.append(System.lineSeparator());
							        line = br.readLine();
							    }
							    htmlText = sb.toString();
							} finally {
							    br.close();
							}
							htmlText=String.format(htmlText, names);
							
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
							System.out.println("se envío el mensaje");

						} catch (MessagingException ex) {
							ex.printStackTrace();
						} catch (Exception ex) {
							System.out.println("INGRESO A EXCEPTIONNNN");
							System.out.println("INGRESO A EXCEPTIONNNN");
							System.out.println("INGRESO A EXCEPTIONNNN");
							System.out.println("INGRESO A EXCEPTIONNNN");
							System.out.println("INGRESO A EXCEPTIONNNN");
							ex.printStackTrace();
						}
					}

				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage() != null && e.getMessage().trim().length() > 0) {
				throw new ServiceException(e.getMessage(), e);
			} else {
				throw new ServiceException();
			}
		}

	}
	
	
	
	public void sendMail(String email, String names, String code, Short bankId) throws ServiceException {
		try {

				System.out.println("email:" + email);
				System.out.println("names:" + names);

				if (email != null && names != null && bankId != null) {

					BankLetterEnum bankLetterEnum = BankLetterEnum.findById(bankId);

					if (bankLetterEnum != null) {
						
						Session sessionSelected = null;

						switch (bankLetterEnum) {
						case FALABELLA:
							sessionSelected = mailFalabella;
							break;
						case GNB:
							sessionSelected = mailGNB;
							break;
						default:
							break;
						}

						try {

							MimeMessage message = new MimeMessage(sessionSelected);
							message.setFrom(new InternetAddress(sessionSelected.getProperty("mail.from")));
							InternetAddress[] address = { new InternetAddress(email) };
							message.setRecipients(Message.RecipientType.TO, address);
							message.setSubject(bankLetterEnum.getSubject());
							message.setSentDate(new Date());
							
							ServletContext servletContext=(ServletContext) FacesContext.getCurrentInstance ().getExternalContext().getContext();
							String separator=System.getProperty("file.separator");
							String rootPath= servletContext.getRealPath(separator);
							String fileName = rootPath+"resources"+separator+"templates"+separator+bankLetterEnum.getTemplateMail();
							String logoName = rootPath+"resources"+separator+"templates"+separator+"logoGEA.jpg";
							String htmlText="";
							BufferedReader br = new BufferedReader(new FileReader(fileName));
							try {
							    StringBuilder sb = new StringBuilder();
							    String line = br.readLine();

							    while (line != null) {
							        sb.append(line);
							        sb.append(System.lineSeparator());
							        line = br.readLine();
							    }
							    htmlText = sb.toString();
							} finally {
							    br.close();
							}
							String urlConditioned="http://172.28.0.23:8080/collectionWeb/faces/download_conditioned.xhtml?code="+code;
							String urlLetter="http://172.28.0.23:8080/collectionWeb/faces/download_letter.xhtml?code="+code;
							htmlText=String.format(htmlText, names,urlLetter,urlConditioned);
							
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
					        
					        // third part (the link)
					        /*messageBodyPart = new MimeBodyPart();
					        messageBodyPart.setText("un texto adicional");
					        messageBodyPart.setHeader("Content-ID","<link>");
					        // add it
					        multipart.addBodyPart(messageBodyPart);*/

					        // put everything together
					        message.setContent(multipart);

					        // para enviar condicionado
							// message.addHeader("Disposition-Notification-To",bankLetterEnum.getMail());
					        
							Transport.send(message);
							System.out.println("se envío el mensaje");

						} catch (MessagingException ex) {
							ex.printStackTrace();
						} catch (Exception ex) {
							System.out.println("INGRESO A EXCEPTIONNNN");
							System.out.println("INGRESO A EXCEPTIONNNN");
							System.out.println("INGRESO A EXCEPTIONNNN");
							System.out.println("INGRESO A EXCEPTIONNNN");
							System.out.println("INGRESO A EXCEPTIONNNN");
							ex.printStackTrace();
						}
					}

				}

			//}

		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage() != null && e.getMessage().trim().length() > 0) {
				throw new ServiceException(e.getMessage(), e);
			} else {
				throw new ServiceException();
			}
		}

	}

	/*
	 * public void sendEmail(String email, String names, String subject, String
	 * body) { MimeMessage message = new MimeMessage(mailSession); try {
	 * 
	 * message.setFrom(new
	 * InternetAddress(mailSession.getProperty("mail.from"))); InternetAddress[]
	 * address = {new InternetAddress(email)};
	 * message.setRecipients(Message.RecipientType.TO, address);
	 * message.setSubject(subject); message.setSentDate(new Date());
	 * message.setText("Estimado Señor(a) <br/>"
	 * +names+"<br/>"+body,"utf-8","html");
	 * message.addHeader("Disposition-Notification-To",
	 * "sanchezc@pe.geainternacional.com"); Transport.send(message); } catch
	 * (MessagingException ex) { ex.printStackTrace(); } }
	 */

}
