package com.returnsoft.collection.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.transaction.UserTransaction;

import com.returnsoft.collection.eao.LoteEao;
import com.returnsoft.collection.eao.NotificationEao;
import com.returnsoft.collection.eao.SaleEao;
import com.returnsoft.collection.entity.Lote;
import com.returnsoft.collection.entity.Notification;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.entity.User;
import com.returnsoft.collection.enumeration.BankLetterEnum;
import com.returnsoft.collection.enumeration.LoteTypeEnum;
import com.returnsoft.collection.enumeration.NotificationStateEnum;
import com.returnsoft.collection.enumeration.NotificationTypeEnum;
import com.returnsoft.collection.enumeration.SaleStateEnum;
import com.returnsoft.collection.exception.BankLetterNotFoundException;
import com.returnsoft.collection.exception.NotificationAlreadyExistException;
import com.returnsoft.collection.exception.NotificationClosedException;
import com.returnsoft.collection.exception.NotificationLimit1Exception;
import com.returnsoft.collection.exception.NotificationLimit2Exception;
import com.returnsoft.collection.exception.NotificationLimit3Exception;
import com.returnsoft.collection.exception.NotificationPendingException;
import com.returnsoft.collection.exception.NullException;
import com.returnsoft.collection.exception.SaleNotFoundException;
import com.returnsoft.collection.exception.SaleStateNoActiveException;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.service.NotificationService;
import com.returnsoft.collection.util.NotificationFile;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class NotificationServiceImpl implements NotificationService {

	@EJB
	private SaleEao saleEao;

	@EJB
	private NotificationEao notificationEao;

	@EJB
	private LoteEao loteEao;

	@Resource
	private SessionContext context;

	@Resource(lookup = "mail/Falabella")
	private Session mailFalabella;

	@Resource(lookup = "mail/GNB")
	private Session mailGNB;

	/*
	 * @Override public void add(Notification notification) throws
	 * ServiceException { try {
	 * 
	 * Sale saleFound = saleEao.findById(notification.getSale().getId());
	 * 
	 * BankLetterEnum bankLetterEnum =
	 * BankLetterEnum.findById(saleFound.getBank().getId());
	 * 
	 * if (bankLetterEnum == null) { throw new
	 * BankLetterNotFoundException(saleFound.getBank().getName()); }
	 * 
	 * // se agrega notification notificationEao.add(notification);
	 * 
	 * //int i=5; int j =0; int v = i/j; System.out.println(v);
	 * 
	 * 
	 * // System.out.println("termino la transacción");
	 * 
	 * if (notification.getType().equals(NotificationTypeEnum.PHYSICAL)) { if
	 * (saleFound.getPhysicalNotifications() == null) {
	 * saleFound.setPhysicalNotifications((short) 1); } else {
	 * saleFound.setPhysicalNotifications((short)
	 * (saleFound.getPhysicalNotifications() + 1)); }
	 * 
	 * } else if (notification.getType().equals(NotificationTypeEnum.MAIL)) {
	 * 
	 * if (saleFound.getVirtualNotifications() == null) {
	 * saleFound.setVirtualNotifications((short) 1); } else {
	 * saleFound.setVirtualNotifications((short)
	 * (saleFound.getVirtualNotifications() + 1)); }
	 * 
	 * File file = generateLetter(saleFound.getCode());
	 * sendMail(saleFound.getCode(), file); }
	 * 
	 * saleFound.setNotification(notification); saleFound =
	 * saleEao.update(saleFound);
	 * 
	 * } catch (NullPointerException e) { e.printStackTrace(); throw new
	 * ServiceException(e.getClass().getName()); } catch (Exception e) {
	 * e.printStackTrace(); throw new ServiceException(e.getMessage()); } }
	 */

	/*
	 * public void createPhysicalNotifications(Integer userId, Date sendingAt,
	 * String searchType, Long nuicResponsible, Date saleDateStartedAt,Date
	 * saleDateEndedAt,Date affiliationDate, Date sendingDate,
	 * List<NotificationStateEnum> notificationStates, Short bankId,
	 * SaleStateEnum saleState, NotificationTypeEnum notificationType) throws
	 * ServiceException, MultipleErrorsException{ try {
	 * 
	 * List<Sale> sales = null;
	 * 
	 * if (searchType.equals("dni")) { sales =
	 * saleEao.findByNuicResponsible(nuicResponsible); } else if
	 * (searchType.equals("saleData")) { sales =
	 * saleEao.findBySaleData2(saleDateStartedAt, saleDateEndedAt,
	 * affiliationDate, sendingDate, notificationStates, bankId, saleState,
	 * notificationType); }
	 * 
	 * List<Exception> errors = new ArrayList<Exception>();
	 * 
	 * if (sales != null && sales.size() > 0) {
	 * 
	 * for (Sale sale : sales) {
	 * 
	 * 
	 * 
	 * // VALIDA EL ESTADO DE LA ULTIMA NOTIFICACION if (sale.getNotification()
	 * != null) { if (sale.getNotification().getState().getPending() == true) {
	 * errors.add(new NotificationPendingException(sale.getCode())); } }
	 * 
	 * // VALIDA SI LA VENTA NO ESTA ACTIVA if
	 * (!sale.getSaleState().getState().equals(SaleStateEnum.ACTIVE)) {
	 * errors.add(new SaleStateNoActiveException(sale.getCode())); }
	 * 
	 * // VALIDA DATOS DE RESPONSABLE if
	 * (sale.getSaleState().getState().equals(SaleStateEnum.DOWN)) {
	 * errors.add(new SaleStateNoActiveException(sale.getCode())); } if
	 * (sale.getPayer().getFirstnameResponsible() == null ||
	 * sale.getPayer().getFirstnameResponsible().trim().length() == 0) {
	 * errors.add(new PayerDataNullException("El nombre", sale.getCode())); } if
	 * (sale.getPayer().getLastnamePaternalResponsible() == null ||
	 * sale.getPayer().getLastnamePaternalResponsible().trim().length() == 0) {
	 * errors.add(new PayerDataNullException("El apellido paterno",
	 * sale.getCode())); } if (sale.getPayer().getLastnameMaternalResponsible()
	 * == null ||
	 * sale.getPayer().getLastnameMaternalResponsible().trim().length() == 0) {
	 * errors.add(new PayerDataNullException("El apellido materno",
	 * sale.getCode())); } if (sale.getPayer().getAddress() == null ||
	 * sale.getPayer().getAddress().trim().length() == 0) { errors.add(new
	 * PayerDataNullException("La dirección", sale.getCode())); } if
	 * (sale.getPayer().getProvince() == null ||
	 * sale.getPayer().getProvince().trim().length() == 0) { errors.add(new
	 * PayerDataNullException("La provincia", sale.getCode()));// } if
	 * (sale.getPayer().getDepartment() == null ||
	 * sale.getPayer().getDepartment().trim().length() == 0) { errors.add(new
	 * PayerDataNullException("El departamento", sale.getCode()));// } if
	 * (sale.getPayer().getDistrict() == null ||
	 * sale.getPayer().getDistrict().trim().length() == 0) { errors.add(new
	 * PayerDataNullException("El distrito", sale.getCode()));// }
	 * 
	 * // VALIDA CANTIDAD DE NOTIFICACIONES ENVIADAS
	 * 
	 * // Si tiene menos de 3 envios virtuales if
	 * (sale.getVirtualNotifications() < 3) { if
	 * (sale.getPhysicalNotifications() > 1) { // no se agrega porque tiene 2
	 * envíos físicos y // menos de 3 virtuales. errors.add(new
	 * NotificationLimit2Exception(sale.getCode())); } } else { if
	 * (sale.getPhysicalNotifications() > 0) { // No se agrega porque ya tiene 1
	 * envío físico y // 3 // envíos virtuales. errors.add(new
	 * NotificationLimit1Exception(sale.getCode())); } }
	 * 
	 * }
	 * 
	 * if (errors.size() > 0) {
	 * 
	 * throw new MultipleErrorsException(errors);
	 * 
	 * } else {
	 * 
	 * List<Exception> errors2 = new ArrayList<Exception>(); for (Sale sale :
	 * sales) { Notification notification = new Notification();
	 * notification.setSendingAt(sendingAt); notification.setSale(sale);
	 * notification.setType(NotificationTypeEnum.PHYSICAL);
	 * notification.setState(NotificationStateEnum.SENDING); User user = new
	 * User(); user.setId(userId); notification.setCreatedBy(user);
	 * notification.setCreatedAt(new Date()); try {
	 * notificationEao.add(notification); } catch (Exception e) {
	 * e.printStackTrace(); errors2.add(e); } }
	 * 
	 * 
	 * }
	 * 
	 * } else { throw new SalesNotFoundException(); }
	 * 
	 * } catch (MultipleErrorsException e) { throw e; } catch (Exception e) {
	 * e.printStackTrace(); if (e.getMessage()!=null &&
	 * e.getMessage().trim().length()>0) { throw new
	 * ServiceException(e.getMessage(), e); }else{ throw new ServiceException();
	 * } } }
	 */

	/*
	 * public void createVirtualNotifications(Integer userId, String searchType,
	 * Long nuicResponsible, Date saleDateStartedAt,Date saleDateEndedAt,Date
	 * affiliationDate, Date sendingDate, List<NotificationStateEnum>
	 * notificationStates, Short bankId, SaleStateEnum saleState,
	 * NotificationTypeEnum notificationType) throws ServiceException,
	 * MultipleErrorsException{
	 * 
	 * try {
	 * 
	 * List<Sale> sales = null;
	 * 
	 * if (searchType.equals("dni")) { sales =
	 * saleEao.findByNuicResponsible(nuicResponsible); } else if
	 * (searchType.equals("saleData")) { sales =
	 * saleEao.findBySaleData2(saleDateStartedAt, saleDateEndedAt,
	 * affiliationDate, sendingDate, notificationStates, bankId, saleState,
	 * notificationType); }
	 * 
	 * List<Exception> errors = new ArrayList<Exception>();
	 * 
	 * if (sales != null && sales.size() > 0) {
	 * 
	 * for (Sale sale : sales) {
	 * 
	 * 
	 * 
	 * // VALIDA EL ESTADO DE LA ULTIMA NOTIFICACION NO SEA // CERRADO if
	 * (sale.getNotification() != null) { if
	 * (sale.getNotification().getState().getPending() == false) {
	 * errors.add(new NotificationClosedException(sale.getCode())); } }
	 * 
	 * // VALIDA SI LA VENTA NO ESTA ACTIVA if
	 * (!sale.getSaleState().getState().equals(SaleStateEnum.ACTIVE)) {
	 * errors.add(new SaleStateNoActiveException(sale.getCode())); }
	 * 
	 * // VALIDA DATOS DE RESPONSABLE if
	 * (sale.getSaleState().getState().equals(SaleStateEnum.DOWN)) {
	 * errors.add(new SaleStateNoActiveException(sale.getCode())); } if
	 * (sale.getPayer().getFirstnameResponsible() == null ||
	 * sale.getPayer().getFirstnameResponsible().trim().length() == 0) {
	 * errors.add(new PayerDataNullException("El nombre", sale.getCode())); } if
	 * (sale.getPayer().getLastnamePaternalResponsible() == null ||
	 * sale.getPayer().getLastnamePaternalResponsible().trim().length() == 0) {
	 * errors.add(new PayerDataNullException("El apellido paterno",
	 * sale.getCode())); } if (sale.getPayer().getLastnameMaternalResponsible()
	 * == null ||
	 * sale.getPayer().getLastnameMaternalResponsible().trim().length() == 0) {
	 * errors.add(new PayerDataNullException("El apellido materno",
	 * sale.getCode())); } if (sale.getPayer().getAddress() == null ||
	 * sale.getPayer().getAddress().trim().length() == 0) { errors.add(new
	 * PayerDataNullException("La dirección", sale.getCode())); } if
	 * (sale.getPayer().getProvince() == null ||
	 * sale.getPayer().getProvince().trim().length() == 0) { errors.add(new
	 * PayerDataNullException("La provincia", sale.getCode()));// } if
	 * (sale.getPayer().getDepartment() == null ||
	 * sale.getPayer().getDepartment().trim().length() == 0) { errors.add(new
	 * PayerDataNullException("El departamento", sale.getCode()));// } if
	 * (sale.getPayer().getDistrict() == null ||
	 * sale.getPayer().getDistrict().trim().length() == 0) { errors.add(new
	 * PayerDataNullException("El distrito", sale.getCode()));// }
	 * 
	 * // VALIDA CANTIDAD DE NOTIFICACIONES VIRTUALES ENVIADAS // Solo puede
	 * tener 3 notificaciones virtuales. if (sale.getVirtualNotifications() > 2)
	 * { errors.add(new NotificationLimit3Exception(sale.getCode())); }
	 * 
	 * }
	 * 
	 * if (errors.size() > 0) {
	 * 
	 * throw new MultipleErrorsException(errors);
	 * 
	 * } else {
	 * 
	 * //User user = sessionBean.getUser(); //List<Exception> errors2 = new
	 * ArrayList<Exception>(); for (Sale sale : sales) {
	 * 
	 * // SE ENVIA EL EMAIL
	 * 
	 * String email = sale.getPayer().getMail();
	 * 
	 * String code = sale.getCode();
	 * 
	 * String names = sale.getPayer().getFirstnameResponsible() + " " +
	 * sale.getPayer().getLastnamePaternalResponsible() + " " +
	 * sale.getPayer().getLastnameMaternalResponsible();
	 * 
	 * 
	 * 
	 * sendMail(email, names, code, sale.getCommerce().getBank().getId());
	 * 
	 * // SE CREA LA NOTIFICACION VIRTUAL Notification notification = new
	 * Notification(); notification.setSendingAt(new Date());
	 * notification.setSale(sale);
	 * notification.setType(NotificationTypeEnum.MAIL);
	 * notification.setState(NotificationStateEnum.SENDING); User user = new
	 * User(); user.setId(userId); notification.setCreatedBy(user);
	 * notification.setCreatedAt(new Date()); try {
	 * notificationEao.add(notification); } catch (Exception e) {
	 * e.printStackTrace();
	 * 
	 * } }
	 * 
	 * //search();
	 * 
	 * 
	 * 
	 * }
	 * 
	 * } else { throw new SalesNotFoundException(); }
	 * 
	 * } catch (Exception e) { e.printStackTrace(); if (e.getMessage()!=null &&
	 * e.getMessage().trim().length()>0) { throw new
	 * ServiceException(e.getMessage(), e); }else{ throw new ServiceException();
	 * } }
	 * 
	 * }
	 */

	@Override
	public List<Notification> findBySale(Long saleId) throws ServiceException {
		try {
			return notificationEao.findBySaleId(saleId);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public Notification findById(Integer notificationId) throws ServiceException {
		try {
			return notificationEao.findById(notificationId);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public Notification update(Notification notification) throws ServiceException {
		try {
			return notificationEao.update(notification);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public Boolean verifyIfExist(Long nuicResponsible, String orderNumber) {
		try {
			return notificationEao.verifyIfExist(nuicResponsible, orderNumber);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public Notification findByKey(Long nuicResponsible, String orderNumber) {
		try {
			return notificationEao.findByKey(nuicResponsible, orderNumber);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Asynchronous
	@Override
	public void updatePhysicalList(List<Notification> notifications, NotificationFile headers, String filename, User createdBy) {

		Date current = new Date();
		Integer process = 1;
		Integer errors = 1;
		String messages = "";
		UserTransaction utx = context.getUserTransaction();

		try {

			Lote lote = new Lote(filename, notifications.size(), "En progreso", LoteTypeEnum.UPDATEPHYSICALNOTIFICACION, createdBy, current);
			loteEao.add(lote);

			for (Notification notification : notifications) {

				try {

					utx.begin();

					notification.setLoteUpdated(lote);
					notification.setCreatedBy(createdBy);
					notification.setCreatedAt(current);
					notificationEao.update(notification);

					lote.setProcess(process);
					loteEao.update(lote);

					utx.commit();

				} catch (Exception e) {
					e.printStackTrace();

					try {
						utx.rollback();
					} catch (Exception e2) {
						e2.printStackTrace();
					}

					if (messages.length() < 5000) {
						messages += " Error en línea " + process + 1 + " : ";
						if (e.getMessage()!=null) {
							if (e.getMessage().length() > 100) {
								messages += e.getMessage().substring(0, 100);
							} else {
								messages += e.getMessage();
							}	
						}else{
							messages += "NullPointerException";
						}
						messages += " \r\n ";
					}

					try {
						utx.begin();
						lote.setMessages(messages);
						lote.setProcess(process);
						lote.setErrors(errors);
						loteEao.update(lote);
						utx.commit();
						errors++;
					} catch (Exception e1) {
						e1.printStackTrace();
					}

				}

				process++;
			}

			if (messages.length() > 0) {
				lote.setState("Terminado con errores");
			} else {
				lote.setState("Terminado");
			}
			loteEao.update(lote);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	@Asynchronous
	public void addVirtualList(List<Sale> sales, User createdBy, String templatePath) {

		Date current = new Date();
		Integer process = 1;
		Integer errors = 1;
		String messages = "";
		UserTransaction utx = context.getUserTransaction();
		SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy");

		try {
			Lote lote = new Lote(sdf.format(current), sales.size(), "En progreso",
					LoteTypeEnum.CREATEVIRTUALNOTIFICACION, createdBy, current);
			loteEao.add(lote);

			for (Sale sale : sales) {

				try {

					utx.begin();

					sale = validateVirtualNotification(sale);

					Notification notification = new Notification();
					notification.setSendingAt(current);
					notification.setSale(sale);
					notification.setType(NotificationTypeEnum.MAIL);
					notification.setState(NotificationStateEnum.SENDING);
					notification.setLoteCreated(lote);
					notification.setCreatedBy(createdBy);
					notification.setCreatedAt(current);
					notificationEao.add(notification);

					//
					sale.setVirtualNotifications((short) (sale.getVirtualNotifications() + 1));
					sale.setNotification(notification);
					saleEao.update(sale);

					File file = generateLetter(sale.getCode(),templatePath);
					sendMail(sale, file,templatePath);
					lote.setProcess(process);
					loteEao.update(lote);
					

					utx.commit();

				} catch (Exception e) {
					e.printStackTrace();

					try {
						utx.rollback();
					} catch (Exception e2) {
						e2.printStackTrace();
					}

					if (messages.length() < 5000) {
						messages += " Error en línea " + process + 1 + " : ";
						if (e.getMessage()!=null) {
							if (e.getMessage().length() > 100) {
								messages += e.getMessage().substring(0, 100);
							} else {
								messages += e.getMessage();
							}	
						}else{
							messages += "NullPointerException";
						}
						messages += " \r\n ";
					}

					try {
						utx.begin();
						lote.setMessages(messages);
						lote.setProcess(process);
						lote.setErrors(errors);
						loteEao.update(lote);
						utx.commit();
						errors++;
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}

				process++;
			}

			if (messages.length() > 0) {
				lote.setState("Terminado con errores");
			} else {
				lote.setState("Terminado");
			}
			loteEao.update(lote);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void addPhysical(Sale sale, Date sendingAt, String orderNumber, User createdBy) throws ServiceException {

		Date current = new Date();
		UserTransaction utx = context.getUserTransaction();

		try {

			utx.begin();

			sale = validatePhysicalNotification(sale, orderNumber);

			Notification notification = new Notification();
			notification.setSendingAt(sendingAt);
			notification.setOrderNumber(orderNumber);
			notification.setSale(sale);
			notification.setType(NotificationTypeEnum.PHYSICAL);
			notification.setState(NotificationStateEnum.SENDING);
			notification.setCreatedBy(createdBy);
			notification.setCreatedAt(current);
			notificationEao.add(notification);
			//
			sale.setPhysicalNotifications((short) (sale.getPhysicalNotifications() + 1));
			sale.setNotification(notification);
			saleEao.update(sale);
			
			

			utx.commit();

		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		} finally {
			try {
				utx.rollback();
			} catch (Exception e2) {
				e2.printStackTrace();
			}

		}
	}

	@Override
	@Asynchronous
	public void addPhysicalList(List<Sale> sales, Date sendingAt, String orderNumber, User createdBy) {

		Date current = new Date();
		Integer process = 1;
		Integer errors = 1;
		String messages = "";
		UserTransaction utx = context.getUserTransaction();
		SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy");

		try {

			Lote lote = new Lote(sdf.format(current), sales.size(), "En progreso",
					LoteTypeEnum.CREATEPHYSICALNOTIFICACION, createdBy, current);
			loteEao.add(lote);

			for (Sale sale : sales) {

				try {

					utx.begin();

					sale = validatePhysicalNotification(sale, orderNumber);

					Notification notification = new Notification();
					notification.setSendingAt(sendingAt);
					notification.setOrderNumber(orderNumber);
					notification.setType(NotificationTypeEnum.PHYSICAL);
					notification.setState(NotificationStateEnum.SENDING);
					notification.setSale(sale);
					notification.setLoteCreated(lote);
					notification.setCreatedBy(createdBy);
					notification.setCreatedAt(current);
					notificationEao.add(notification);
					//
					sale.setPhysicalNotifications((short) (sale.getPhysicalNotifications() + 1));
					sale.setNotification(notification);
					saleEao.update(sale);

					utx.commit();

				} catch (Exception e) {
					e.printStackTrace();

					try {
						utx.rollback();
					} catch (Exception e2) {
						e2.printStackTrace();
					}

					if (messages.length() < 5000) {
						messages += " Error en línea " + process + 1 + " : ";
						if (e.getMessage()!=null) {
							if (e.getMessage().length() > 100) {
								messages += e.getMessage().substring(0, 100);
							} else {
								messages += e.getMessage();
							}	
						}else{
							messages += "NullPointerException";
						}
						messages += " \r\n ";
					}

					try {
						utx.begin();
						lote.setMessages(messages);
						lote.setProcess(process);
						lote.setErrors(errors);
						loteEao.update(lote);
						utx.commit();
						errors++;
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}

				process++;
			}

			if (messages.length() > 0) {
				lote.setState("Terminado con errores");
			} else {
				lote.setState("Terminado");
			}
			loteEao.update(lote);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private Sale validateVirtualNotification(Sale sale) throws Exception {

		sale = saleEao.findById(sale.getId());

		// VALIDA EL ESTADO DE LA ULTIMA NOTIFICACION NO SEA CERRADO
		if (sale.getNotification() != null) {
			if (sale.getNotification().getState().getPending() == false) {
				throw new NotificationClosedException(sale.getCode());
			}
		}

		// VALIDA SI LA VENTA NO ESTA ACTIVA
		if (!sale.getSaleState().getState().equals(SaleStateEnum.ACTIVE)) {
			throw new SaleStateNoActiveException(sale.getCode());
		}

		// VALIDA DATOS DE RESPONSABLE
		if (sale.getSaleState().getState().equals(SaleStateEnum.DOWN)) {
			throw new SaleStateNoActiveException(sale.getCode());
		}
		if (sale.getPayer().getFirstnameResponsible() == null
				|| sale.getPayer().getFirstnameResponsible().trim().length() == 0) {
			throw new NullException("NOMBRE", sale.getCode());
		}
		if (sale.getPayer().getLastnamePaternalResponsible() == null
				|| sale.getPayer().getLastnamePaternalResponsible().trim().length() == 0) {
			throw new NullException("APELLIDO PATERNO", sale.getCode());
		}
		if (sale.getPayer().getLastnameMaternalResponsible() == null
				|| sale.getPayer().getLastnameMaternalResponsible().trim().length() == 0) {
			throw new NullException("APELLIDO MATERNO", sale.getCode());
		}
		if (sale.getPayer().getAddress() == null || sale.getPayer().getAddress().trim().length() == 0) {
			throw new NullException("DIRECCIÓN", sale.getCode());
		}
		if (sale.getPayer().getProvince() == null || sale.getPayer().getProvince().trim().length() == 0) {
			throw new NullException("PROVINCIA", sale.getCode());//
		}
		if (sale.getPayer().getDepartment() == null || sale.getPayer().getDepartment().trim().length() == 0) {
			throw new NullException("DEPARTAMENTO", sale.getCode());//
		}
		if (sale.getPayer().getDistrict() == null || sale.getPayer().getDistrict().trim().length() == 0) {
			throw new NullException("DISTRITO", sale.getCode());//
		}

		// VALIDA CANTIDAD DE NOTIFICACIONES VIRTUALES ENVIADAS
		// Solo puede tener 3 notificaciones virtuales.
		if (sale.getVirtualNotifications() != null && sale.getVirtualNotifications() > 2) {
			throw new NotificationLimit3Exception(sale.getCode());
		}

		return sale;

	}

	private Sale validatePhysicalNotification(Sale sale, String orderNumber) throws Exception {

		sale = saleEao.findById(sale.getId());

		// VALIDA SI LA NOTIFICACION EXISTE
		Boolean exist = notificationEao.verifyIfExist(sale.getPayer().getNuicResponsible(), orderNumber);
		if (exist) {
			throw new NotificationAlreadyExistException(sale.getPayer().getNuicResponsible(),orderNumber);
		}

		// VALIDA EL ESTADO DE LA ULTIMA NOTIFICACION
		if (sale.getNotification() != null) {
			if (sale.getNotification().getType().equals(NotificationTypeEnum.PHYSICAL)
					&& sale.getNotification().getState().getPending() == true) {
				throw new NotificationPendingException(sale.getCode());
			}
		}

		// VALIDA SI LA VENTA NO ESTA ACTIVA
		if (!sale.getSaleState().getState().equals(SaleStateEnum.ACTIVE)) {
			throw new SaleStateNoActiveException(sale.getCode());
		}

		// VALIDA DATOS DE RESPONSABLE
		if (sale.getSaleState().getState().equals(SaleStateEnum.DOWN)) {
			throw new SaleStateNoActiveException(sale.getCode());
		}
		if (sale.getPayer().getFirstnameResponsible() == null
				|| sale.getPayer().getFirstnameResponsible().trim().length() == 0) {
			throw new NullException("NOMBRE", sale.getCode());
		}
		if (sale.getPayer().getLastnamePaternalResponsible() == null
				|| sale.getPayer().getLastnamePaternalResponsible().trim().length() == 0) {
			throw new NullException("APELLIDO PATERNO", sale.getCode());
		}
		if (sale.getPayer().getLastnameMaternalResponsible() == null
				|| sale.getPayer().getLastnameMaternalResponsible().trim().length() == 0) {
			throw new NullException("APELLIDO MATERNO", sale.getCode());
		}
		if (sale.getPayer().getAddress() == null || sale.getPayer().getAddress().trim().length() == 0) {
			throw new NullException("DIRECCIÓN", sale.getCode());
		}
		if (sale.getPayer().getProvince() == null || sale.getPayer().getProvince().trim().length() == 0) {
			throw new NullException("PROVINCIA", sale.getCode());
		}
		if (sale.getPayer().getDepartment() == null || sale.getPayer().getDepartment().trim().length() == 0) {
			throw new NullException("DEPARTAMENTO", sale.getCode());
		}
		if (sale.getPayer().getDistrict() == null || sale.getPayer().getDistrict().trim().length() == 0) {
			throw new NullException("DISTRITO", sale.getCode());
		}

		// VALIDA CANTIDAD DE NOTIFICACIONES ENVIADAS

		// Si tiene menos de 3 envios virtuales
		if (sale.getVirtualNotifications() == null || sale.getVirtualNotifications() < 3) {
			if (sale.getPhysicalNotifications() != null && sale.getPhysicalNotifications() > 1) {
				// no se agrega porque tiene 2 envíos físicos y
				// menos de 3 virtuales.
				throw new NotificationLimit2Exception(sale.getCode());
			}
		} else {
			if (sale.getPhysicalNotifications() != null && sale.getPhysicalNotifications() > 0) {
				// No se agrega porque ya tiene 1 envío físico y
				// 3
				// envíos virtuales.
				throw new NotificationLimit1Exception(sale.getCode());
			}
		}

		return sale;

	}

	private void sendMail(Sale sale, File letter, String templatePath) throws Exception {

		try {

			BankLetterEnum bankLetterEnum = BankLetterEnum.findById(sale.getBank().getId());

			///////////////////////////

			String names = sale.getPayer().getFirstnameResponsible() + " "
					+ sale.getPayer().getLastnamePaternalResponsible() + " "
					+ sale.getPayer().getLastnameMaternalResponsible();

			String email = sale.getPayer().getMail();

			/*ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext()
					.getContext();
			String separator = System.getProperty("file.separator");
			String rootPath = servletContext.getRealPath(separator);
			String templatePath = rootPath + "resources" + separator + "templates" + separator;*/

			Session sessionSelected = null;
			String subject = "";
			String mailPath = "";
			String conditionedPath = "";
			String logoPath = templatePath + "logoGEA.jpg";

			if (bankLetterEnum.equals(BankLetterEnum.FALABELLA)) {
				sessionSelected = mailFalabella;
				subject = "Asistencia Falabella";
				mailPath = templatePath + "mailFalabella.html";
				conditionedPath = templatePath + "condicionado_asistencia_falabella.pdf";
			} else if (bankLetterEnum.equals(BankLetterEnum.GNB)) {
				sessionSelected = mailGNB;
				subject = "Asistencia GNB";
				mailPath = templatePath + "mailGNB.html";
				conditionedPath = templatePath + "condicionado_asistencia_gnb.pdf";
			}

			String htmlText = "";
			BufferedReader br = new BufferedReader(new FileReader(mailPath));
			try {
				StringBuilder sb = new StringBuilder();
				String line = br.readLine();

				while (line != null) {
					sb.append(line);
					sb.append(System.lineSeparator());
					line = br.readLine();
				}
				htmlText = sb.toString();
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			} finally {
				br.close();
			}

			String urlConditioned = "http://190.107.180.164:9096/siscob/faces/download_conditioned.xhtml?code="
					+ sale.getCode();
			String urlLetter = "http://190.107.180.164:9096/siscob/faces/download_letter.xhtml?code=" + sale.getCode();
			htmlText = String.format(htmlText, names, urlLetter, urlConditioned);

			MimeMessage message = new MimeMessage(sessionSelected);
			message.setFrom(new InternetAddress(sessionSelected.getProperty("mail.from")));
			InternetAddress[] addressTO = { new InternetAddress(email) };
			InternetAddress[] addressCC = { new InternetAddress(sessionSelected.getProperty("mail.from")) };
			message.setRecipients(Message.RecipientType.TO, addressTO);
			message.setRecipients(Message.RecipientType.CC, addressCC);

			message.setSubject(subject);
			message.setSentDate(new Date());

			//
			// This HTML mail have to 2 part, the BODY and the embedded
			// image
			//
			MimeMultipart multipart = new MimeMultipart("related");

			// first part (the html)
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(htmlText, "text/html");
			// add it
			multipart.addBodyPart(messageBodyPart);

			/////
			BodyPart adjunto1 = new MimeBodyPart();

			adjunto1.setDataHandler(new DataHandler(new FileDataSource(conditionedPath)));
			adjunto1.setFileName("condicionado.pdf");
			multipart.addBodyPart(adjunto1);

			BodyPart adjunto2 = new MimeBodyPart();
			adjunto2.setDataHandler(new DataHandler(new FileDataSource(letter)));
			adjunto2.setFileName("carta.pdf");
			multipart.addBodyPart(adjunto2);

			////

			// second part (the image)
			messageBodyPart = new MimeBodyPart();
			DataSource fds = new FileDataSource(logoPath);
			messageBodyPart.setDataHandler(new DataHandler(fds));
			messageBodyPart.setHeader("Content-ID", "<image>");
			// add it
			multipart.addBodyPart(messageBodyPart);

			// put everything together
			message.setContent(multipart);

			// para enviar condicionado
			// message.addHeader("Disposition-Notification-To",bankLetterEnum.getMail());

			Transport.send(message);
			// System.out.println("se envío el mensaje");

			/*
			 * } else { throw new SaleNotFoundException(); }
			 */

		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}

	}

	private File generateLetter(String code, String templatePath) throws ServiceException {
		try {

			Sale sale = saleEao.findByCode(code);

			if (sale != null) {

				BankLetterEnum bankLetterEnum = BankLetterEnum.findById(sale.getBank().getId());

				if (bankLetterEnum == null) {
					throw new BankLetterNotFoundException(sale.getBank().getName());
				}

				/*
				 * if
				 * (sale.getSaleState().getState().equals(SaleStateEnum.DOWN)) {
				 * throw new SaleStateNoActiveException(sale.getCode()); }
				 */
				if (sale.getPayer().getFirstnameResponsible() == null
						|| sale.getPayer().getFirstnameResponsible().trim().length() == 0) {
					throw new NullException("NOMBRE", sale.getCode());
				}
				if (sale.getPayer().getLastnamePaternalResponsible() == null
						|| sale.getPayer().getLastnamePaternalResponsible().trim().length() == 0) {
					throw new NullException("APELLIDO PATERNO", sale.getCode());
				}
				if (sale.getPayer().getLastnameMaternalResponsible() == null
						|| sale.getPayer().getLastnameMaternalResponsible().trim().length() == 0) {
					throw new NullException("APELLIDO MATERNO", sale.getCode());
				}
				if (sale.getPayer().getAddress() == null || sale.getPayer().getAddress().trim().length() == 0) {
					throw new NullException("DIRECCIÓN", sale.getCode());
				}
				if (sale.getPayer().getProvince() == null || sale.getPayer().getProvince().trim().length() == 0) {
					throw new NullException("PROVINCIA", sale.getCode());
				}
				if (sale.getPayer().getDepartment() == null || sale.getPayer().getDepartment().trim().length() == 0) {
					throw new NullException("DEPARTAMENTO", sale.getCode());//
				}
				if (sale.getPayer().getDistrict() == null || sale.getPayer().getDistrict().trim().length() == 0) {
					throw new NullException("DISTRITO", sale.getCode());//
				}

				
				Map<String, Object> parameters = new HashMap<String, Object>();
				
				//System.out.println("resourcesssssssssssssssss");
				
				//String separator = System.getProperty("file.separator");
				
				//System.out.println(this.getClass().getClassLoader().getResource("").getPath());
				
				
				//System.out.println("resourcesssssssssssssssss");
				
				/*ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext()
						.getContext();*/
				
				//String rootPath = servletContext.getRealPath(separator);
				//String templatePath = rootPath + "resources" + separator + "templates" + separator;
				
				//String templatePath = this.getClass().getClassLoader().getResource("").getPath();
				
				
				System.out.println("templatePath:"+templatePath);

				String letterPath = "";
				if (bankLetterEnum.equals(BankLetterEnum.FALABELLA)) {
					letterPath = templatePath + "letterFalabella.jrxml";
					parameters.put("names",
							sale.getPayer().getFirstnameResponsible() + " "
									+ sale.getPayer().getLastnamePaternalResponsible() + " "
									+ sale.getPayer().getLastnameMaternalResponsible());

				} else if (bankLetterEnum.equals(BankLetterEnum.GNB)) {
					letterPath = templatePath + "letterGNB.jrxml";
					parameters.put("names",
							sale.getPayer().getFirstnameResponsible() + " "
									+ sale.getPayer().getLastnamePaternalResponsible() + " "
									+ sale.getPayer().getLastnameMaternalResponsible());
					parameters.put("address", sale.getPayer().getAddress());
					parameters.put("department",
							sale.getPayer().getDepartment() + " - " + sale.getPayer().getProvince());
				}

				parameters.put("ROOT_DIR", templatePath);
				parameters.put(JRParameter.REPORT_LOCALE, new Locale("es", "pe"));

				JasperReport report = JasperCompileManager.compileReport(letterPath);
				JasperPrint print = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());

				File file = new File("letter-" + sale.getCode());
				FileOutputStream fos = new FileOutputStream(file);

				JasperExportManager.exportReportToPdfStream(print, fos);
				fos.close();

				return file;

			} else {
				throw new SaleNotFoundException();
			}

		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

}
