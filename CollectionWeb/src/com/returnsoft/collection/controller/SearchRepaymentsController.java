package com.returnsoft.collection.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

import com.returnsoft.collection.entity.Bank;
import com.returnsoft.collection.entity.Product;
import com.returnsoft.collection.entity.Repayment;
import com.returnsoft.collection.exception.MultipleErrorsException;
import com.returnsoft.collection.exception.UserLoggedNotFoundException;
import com.returnsoft.collection.lazy.RepaymentLazyModel;
import com.returnsoft.collection.service.BankService;
import com.returnsoft.collection.service.ProductService;
import com.returnsoft.collection.service.RepaymentService;
import com.returnsoft.generic.util.FacesUtil;
import com.returnsoft.generic.util.SessionBean;

@ManagedBean
@ViewScoped
public class SearchRepaymentsController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5939761131007882940L;

	@Inject
	private FacesUtil facesUtil;

	@Inject
	private SessionBean sessionBean;
	
	@EJB
	private RepaymentService repaymentService;
	
	@EJB
	private BankService bankService;
	
	@EJB
	private ProductService productService;

	private RepaymentLazyModel repayments;
	private Repayment repaymentSelected;

	private List<SelectItem> banks;
	private String bankSelected;
	private List<SelectItem> products;
	private String productSelected;
	private Long documentNumber;
	private Date paymentDate;
	private Date returnedDate;

	public String initialize() {

		try {
			if (sessionBean == null || sessionBean.getUser() == null || sessionBean.getUser().getId() == null) {
				throw new UserLoggedNotFoundException();
			}
			
			List<Bank> banksEntity = bankService.getAll();
			banks = new ArrayList<SelectItem>();
			for (Bank bank : banksEntity) {
				SelectItem item = new SelectItem();
				item.setValue(bank.getId().toString());
				item.setLabel(bank.getName());
				banks.add(item);
			}
			
			List<Product> productsEntity = productService.getAll();
			products = new ArrayList<SelectItem>();
			for (Product product : productsEntity) {
				SelectItem item = new SelectItem();
				item.setValue(product.getId().toString());
				item.setLabel(product.getName());
				products.add(item);
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

	//@SuppressWarnings("unchecked")
	public void search() {

		try {

			if (sessionBean == null || sessionBean.getUser() == null || sessionBean.getUser().getId() < 1) {
				throw new UserLoggedNotFoundException();
			}

			repaymentSelected = null;
			
			Short bankId=null;
			if (bankSelected!=null && bankSelected.length()>0) {
				bankId=Short.parseShort(bankSelected);
			}
			Short productId=null;
			if (productSelected!=null && productSelected.length()>0) {
				productId=Short.parseShort(productSelected);
			}

			repayments = new RepaymentLazyModel(repaymentService, paymentDate, returnedDate, bankId, productId, documentNumber);

			//System.out.println("Termino busqueda");

			/*if (collections != null) {
				if (collections.getWrappedData() != null) {
					System.out.println(
							"cantidad de ventas en el controller: " + ((List<Collection>) collections.getWrappedData()).size());
				} else {
					System.out.println("wrapped data es nulo");
				}

			} else {
				System.out.println("sales es nulo");
			}*/

		} catch (Exception e) {

			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getMessage());
		}

	}
	
	public void loadRepayments() {
		try {

			System.out.println("loadrepayments");

			if (sessionBean == null || sessionBean.getUser() == null || sessionBean.getUser().getId() < 1) {
				throw new UserLoggedNotFoundException();
			}
			Map<String, Object> options = new HashMap<String, Object>();
			options.put("modal", true);
			options.put("draggable", true);
			options.put("resizable", false);
			options.put("contentHeight", 200);
			options.put("contentWidth", 500);

			RequestContext.getCurrentInstance().openDialog("load_repayments", options, null);

		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getMessage());
		}
	}
	
	public void afterLoadRepayments(SelectEvent event) {
		try {

			System.out.println("afterLoadRepayments");

			Exception exceptionReturn = (Exception) event.getObject();
			if (exceptionReturn != null) {
				if (exceptionReturn instanceof MultipleErrorsException) {
					for (Exception err : ((MultipleErrorsException) exceptionReturn).getErrors()) {
						facesUtil.sendErrorMessage(err.getMessage());
					}
				} else if (exceptionReturn instanceof NullPointerException) {
					facesUtil.sendErrorMessage("Existen valores nulos.");
				} else {
					facesUtil.sendErrorMessage(exceptionReturn.getMessage());
				}
			} else {
				facesUtil.sendConfirmMessage("Se creó el lote satisfactorimente.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getMessage());
		}
	}
	
	public void exportTxt() throws IOException {

		try {

			if (sessionBean == null || sessionBean.getUser() == null || sessionBean.getUser().getId() < 1) {
				throw new UserLoggedNotFoundException();
			}

			List<Repayment> repaymentsFound = new ArrayList<Repayment>();

				Short bankId=null;
				if (bankSelected!=null && bankSelected.length()>0) {
					bankId=Short.parseShort(bankSelected);
				}
				Short productId=null;
				if (productSelected!=null && productSelected.length()>0) {
					productId=Short.parseShort(productSelected);
				}

				repaymentsFound = repaymentService.findList(paymentDate, returnedDate, bankId, productId, documentNumber);


			StringBuilder cadena = new StringBuilder();
			String separator = "|";
			String header = "";

			header += "CODIGO VENTA" + separator;
			header += "NUMERO DOCUMENTO" + separator;
			header += "PRODUCTO" + separator;
			header += "BANCO" + separator;
			
			header += "NUMERO PRIMAS" + separator;
			header += "IMPORTE DEVOLVER" + separator;
			
			header += "FECHA DEVOLUCION" + separator;
			header += "FECHA ABONO" + separator;
			
			
			header += "FECHA CREACION" + separator;
			header += "USUARIO CREACION";

			cadena.append(header);
			cadena.append("\r\n");

			SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat sdf3 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			
			//int count=0;

			for (Repayment collection : repaymentsFound) {
				
				//count++;
				//System.out.println("count:"+count);

				cadena.append(collection.getSale().getCode() + separator);
				cadena.append(collection.getSale().getPayer().getDocumentType().getName() + separator);
				cadena.append(collection.getSale().getProduct().getName() + separator);
				cadena.append(collection.getSale().getBank().getName() + separator);

				
				cadena.append(collection.getInsurancePremiumNumber() + separator);
				cadena.append(collection.getReturnedAmount() + separator);
				
				cadena.append(sdf2.format(collection.getReturnedDate()) + separator);
				cadena.append(sdf2.format(collection.getPaymentDate()) + separator);
				
				cadena.append(sdf3.format(collection.getCreatedAt()) + separator);
				cadena.append(collection.getCreatedBy().getUsername());
				cadena.append("\r\n");

			}

			FacesContext fc = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();

			response.reset();
			response.setContentType("text/comma-separated-values");
			response.setHeader("Content-Disposition", "attachment; filename=\"extornos.txt\"");

			OutputStream output = response.getOutputStream();

			// for (String s : strings) {
			output.write(cadena.toString().getBytes());
			// }

			output.flush();
			output.close();

			fc.responseComplete();

		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
		}

	}

	public List<SelectItem> getBanks() {
		return banks;
	}

	public void setBanks(List<SelectItem> banks) {
		this.banks = banks;
	}

	public String getBankSelected() {
		return bankSelected;
	}

	public void setBankSelected(String bankSelected) {
		this.bankSelected = bankSelected;
	}

	public List<SelectItem> getProducts() {
		return products;
	}

	public void setProducts(List<SelectItem> products) {
		this.products = products;
	}

	public String getProductSelected() {
		return productSelected;
	}

	public void setProductSelected(String productSelected) {
		this.productSelected = productSelected;
	}

	

	public Long getDocumentNumber() {
		return documentNumber;
	}

	public void setDocumentNumber(Long documentNumber) {
		this.documentNumber = documentNumber;
	}

	public RepaymentLazyModel getRepayments() {
		return repayments;
	}

	public void setRepayments(RepaymentLazyModel repayments) {
		this.repayments = repayments;
	}

	public Repayment getRepaymentSelected() {
		return repaymentSelected;
	}

	public void setRepaymentSelected(Repayment repaymentSelected) {
		this.repaymentSelected = repaymentSelected;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public Date getReturnedDate() {
		return returnedDate;
	}

	public void setReturnedDate(Date returnedDate) {
		this.returnedDate = returnedDate;
	}

	
	
	

}
