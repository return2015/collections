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
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.returnsoft.collection.eao.SaleEao;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.enumeration.BankLetterEnum;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.service.MailingService;
@Stateless
public class MailingServiceImpl implements MailingService {
	
	@EJB
	private SaleEao saleEao;
	
	@Resource(lookup = "EMailMe")
    private Session mailSession;
	
	
	@Override
	//@Schedule(minute="*", second="*/10",hour="*", persistent=false)
	public void mailerDaemon() throws ServiceException {
		try {
			System.out.println("enviando..");
			List<Sale> sales = saleEao.getNotConditioned();
			for (Sale sale : sales) {
				String email = sale.getPayer().getMail();
				System.out.println("email:"+email);
				String names = sale.getFirstnameContractor() +" "+ sale.getLastnamePaternalContractor()+" "+sale.getLastnameMaternalContractor();
				System.out.println("names:"+names);
				
				BankLetterEnum bankLetterEnum = BankLetterEnum.findById(sale.getCommerce().getBank().getId());
				
				if (bankLetterEnum!=null) {
					sendEmail(email, names, bankLetterEnum.getSubject(), bankLetterEnum.getBody());
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
	
	
	 public void sendEmail(String email, String names, String subject, String body) {
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
	    }
	 

}
