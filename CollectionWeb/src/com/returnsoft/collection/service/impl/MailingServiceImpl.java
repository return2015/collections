package com.returnsoft.collection.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.event.TransportEvent;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.returnsoft.collection.eao.SaleEao;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.enumeration.BankLetterEnum;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.service.MailingService;
import com.sun.mail.smtp.SMTPTransport;
@Stateless
public class MailingServiceImpl implements MailingService {
	
	@EJB
	private SaleEao saleEao;
	
	/*@Resource(lookup = "EMailMe")
    private Session mailSession;*/
	
	@Resource(lookup = "EMailFalabella")
    private Session mailFalabella;
	
	@Resource(lookup = "EMailGNB")
    private Session mailGNB;
	
	
	
	
	@Override
	//@Schedule(minute="*/20", second="*",hour="*", persistent=false)
	public void mailerDaemon() throws ServiceException {
		try {
			System.out.println("enviando..");
			List<Sale> sales = saleEao.getNotConditioned();
			System.out.println("cantidad de ventas encontradas "+sales.size());
			for (Sale sale : sales) {
				
				String email = sale.getPayer().getMail();
				System.out.println("email:"+email);
				String names = sale.getFirstnameContractor() +" "+ sale.getLastnamePaternalContractor()+" "+sale.getLastnameMaternalContractor();
				System.out.println("names:"+names);
				
				BankLetterEnum bankLetterEnum = BankLetterEnum.findById(sale.getCommerce().getBank().getId());
				
				if (bankLetterEnum!=null) {
					
				//MimeMessage message = null;
				//InternetAddress internetAddress = null;
				
				Session sessionSelected = null;
				
				switch (bankLetterEnum) {
				case FALABELLA:
					sessionSelected = mailFalabella;
					//message = new MimeMessage(mailFalabella);
					//internetAddress = new InternetAddress(mailFalabella.getProperty("mail.from"));
					break;
				case GNB:
					sessionSelected = mailGNB;
					//message = new MimeMessage(mailGNB);
					//internetAddress = new InternetAddress(mailGNB.getProperty("mail.from"));
					break;	

				default:
					break;
				}
				
				try {
					
					MimeMessage message = new MimeMessage(sessionSelected);
		            message.setFrom(new InternetAddress(sessionSelected.getProperty("mail.from")));
		            InternetAddress[] address = {new InternetAddress(email)};
		            message.setRecipients(Message.RecipientType.TO, address);
		            message.setSubject(bankLetterEnum.getSubject());
		            message.setSentDate(new Date());
		            message.setText("Estimado Señor(a) <br/>"+names+"<br/>"+bankLetterEnum.getBody(),"utf-8","html");
		            message.addHeader("Disposition-Notification-To",bankLetterEnum.getMail());
		            
		            Transport.send(message);
		            System.out.println("antes de debug");
		            
		            
		            //System.out.println("Debug"+sessionSelected.getDebug());
		            //SMTPTransport t = (SMTPTransport)sessionSelected.getTransport();
		            
		            ///////////////Transport.send(message);
		           /* String response = t.getLastServerResponse();
		            System.out.println("response:::"+response);
		            boolean s = t.getReportSuccess();
		            System.out.println("s:::"+s);
		            int code = t.getLastReturnCode();
		            System.out.println("code:::"+code);*/
		            
		            //Transport
		            
		            
		            System.out.println("se envío el mensaje");
		            
		        } catch (MessagingException ex) {
		            ex.printStackTrace();
		        }catch (Exception ex) {
		        	System.out.println("INGRESO A EXCEPTIONNNN");
		        	System.out.println("INGRESO A EXCEPTIONNNN");
		        	System.out.println("INGRESO A EXCEPTIONNNN");
		        	System.out.println("INGRESO A EXCEPTIONNNN");
		        	System.out.println("INGRESO A EXCEPTIONNNN");
		        	ex.printStackTrace();
		        }
			}
				
			}
		/*} catch (EaoException e) {
			throw new ServiceException("EaoException:" + e.getMessage());*/
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
		
	}
	
	
	 /*public void sendEmail(String email, String names, String subject, String body) {
	        MimeMessage message = new MimeMessage(mailSession);
	        try {

	            message.setFrom(new InternetAddress(mailSession.getProperty("mail.from")));
	            InternetAddress[] address = {new InternetAddress(email)};
	            message.setRecipients(Message.RecipientType.TO, address);
	            message.setSubject(subject);
	            message.setSentDate(new Date());
	            message.setText("Estimado Señor(a) <br/>"+names+"<br/>"+body,"utf-8","html");
	            message.addHeader("Disposition-Notification-To","sanchezc@pe.geainternacional.com");
	            Transport.send(message);
	        } catch (MessagingException ex) {
	            ex.printStackTrace();
	        }
	    }*/
	 

}
