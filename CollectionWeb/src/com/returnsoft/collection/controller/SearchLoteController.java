package com.returnsoft.collection.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
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
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.Part;

import com.returnsoft.collection.entity.Bank;
import com.returnsoft.collection.entity.Collection;
import com.returnsoft.collection.entity.Lote;
import com.returnsoft.collection.entity.PaymentMethod;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.entity.User;
import com.returnsoft.collection.enumeration.CollectionResponseEnum;
import com.returnsoft.collection.enumeration.LoteTypeEnum;
import com.returnsoft.collection.enumeration.SaleStateEnum;
import com.returnsoft.collection.exception.BankInvalidException;
import com.returnsoft.collection.exception.BankNotFoundException;
import com.returnsoft.collection.exception.BankNotSelectedException;
import com.returnsoft.collection.exception.CollectionChargeAmountException;
import com.returnsoft.collection.exception.CollectionDuplicateException;
import com.returnsoft.collection.exception.FileExtensionException;
import com.returnsoft.collection.exception.FileNotFoundException;
import com.returnsoft.collection.exception.FileRowsInvalidException;
import com.returnsoft.collection.exception.FileRowsZeroException;
import com.returnsoft.collection.exception.FormatException;
import com.returnsoft.collection.exception.MultipleErrorsException;
import com.returnsoft.collection.exception.NullException;
import com.returnsoft.collection.exception.OverflowException;
import com.returnsoft.collection.exception.SaleNotFoundException;
import com.returnsoft.collection.exception.SaleStateNoActiveException;
import com.returnsoft.collection.exception.SaleStateNotFoundException;
import com.returnsoft.collection.exception.UserLoggedNotFoundException;
import com.returnsoft.collection.lazy.LoteLazyModel;
import com.returnsoft.collection.service.BankService;
import com.returnsoft.collection.service.CollectionPeriodService;
import com.returnsoft.collection.service.CollectionService;
import com.returnsoft.collection.service.LoteService;
import com.returnsoft.collection.service.PaymentMethodService;
import com.returnsoft.collection.service.ProductService;
import com.returnsoft.collection.service.SaleService;
import com.returnsoft.collection.vo.CollectionFile;
import com.returnsoft.generic.util.FacesUtil;
import com.returnsoft.generic.util.SessionBean;

@ManagedBean
@ViewScoped
public class SearchLoteController implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5267773436408492212L;

	@EJB
	private LoteService loteService;
	
	@Inject
	private FacesUtil facesUtil;
	
	@Inject
	private SessionBean sessionBean;

	//SEARCH
	private Date loteDate;
	private String loteTypeSelected;
	private LoteLazyModel lotes;
	private Lote loteSelected;
	private List<SelectItem> loteTypes;
	
	///////
	
	//SALES
	private Part saleFile;
	private Integer SALE_FILE_ROWS = 49;
	
	//COLLECTIONS
	private Part collectionFile;
	private Integer COLLECTION_FILE_ROWS = 14;
	
	//CREDIT CARD
	private Part creditCardFile;
	private Integer CREDITCARD_FILE_ROWS = 6;
	
	//SALE STATE
	private Part saleStateFile;
	private Integer SALESTATE_FILE_ROWS = 7;
	
	//REPAYMENT
	private Part repaymentFile;
	private Integer REPAYMENT_FILE_ROWS = 5;
	
	
	
	@EJB
	private CollectionPeriodService collectionPeriodService;

	@EJB
	private BankService bankService;

	@EJB
	private ProductService productService;
	
	@EJB
	private SaleService saleService;
	
	@EJB
	private PaymentMethodService paymentMethodService;
	
	@EJB
	private CollectionService collectionService;
	
	
	public SearchLoteController(){
		//System.out.println("Se construye SearchLoteController");
		//facesUtil = new FacesUtil();
	}
	
	public String initialize() {
		//System.out.println("inicializando SearchLoteController");
		try {
			if (sessionBean == null || sessionBean.getUser() == null || sessionBean.getUser().getId() == null) {
				throw new UserLoggedNotFoundException();
			}
			
			loteTypes = new ArrayList<SelectItem>();
			for (LoteTypeEnum loteTypeEnum : LoteTypeEnum.values()) {
				SelectItem loteTypeItem = new SelectItem();
				loteTypeItem.setValue(loteTypeEnum.getId().toString());
				loteTypeItem.setLabel(loteTypeEnum.getName());
				loteTypes.add(loteTypeItem);
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
	
	public void search(){
		try {
			
			System.out.println("Ingreso a search");
			
			if (sessionBean == null || sessionBean.getUser() == null || sessionBean.getUser().getId() == null) {
				throw new UserLoggedNotFoundException();
			}
			
			LoteTypeEnum loteType = null;
			
			if (loteTypeSelected!=null) {
				loteType = LoteTypeEnum.findById(Short.parseShort(loteTypeSelected));
			}
			
			//System.out.println("loteDate:"+loteDate);
			//if (loteDate!=null) {
				lotes = new LoteLazyModel(loteService, loteDate, loteType);
			//}
		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
		}
	}
	
	
	
	
	public void downloadCollectionsFile() {
		try {
			
			if (sessionBean == null || sessionBean.getUser() == null || sessionBean.getUser().getId() == null) {
				throw new UserLoggedNotFoundException();
			}

			ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext()
					.getContext();
			String separator = System.getProperty("file.separator");
			String rootPath = servletContext.getRealPath(separator);
			String fileName = rootPath + "resources" + separator + "templates" + separator + "tramas_.xlsx";
			File file = new File(fileName);
			InputStream pdfInputStream = new FileInputStream(file);

			FacesContext facesContext = FacesContext.getCurrentInstance();
			ExternalContext externalContext = facesContext.getExternalContext();
			externalContext.setResponseContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"tramas_ventas.xlsx\"");

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
	
	

	public Date getLoteDate() {
		return loteDate;
	}

	public void setLoteDate(Date loteDate) {
		this.loteDate = loteDate;
	}

	

	public LoteLazyModel getLotes() {
		return lotes;
	}

	public void setLotes(LoteLazyModel lotes) {
		this.lotes = lotes;
	}

	public Lote getLoteSelected() {
		return loteSelected;
	}

	public void setLoteSelected(Lote loteSelected) {
		this.loteSelected = loteSelected;
	}

	public Part getSaleFile() {
		return saleFile;
	}

	public void setSaleFile(Part saleFile) {
		this.saleFile = saleFile;
	}

	public Part getCollectionFile() {
		return collectionFile;
	}

	public void setCollectionFile(Part collectionFile) {
		this.collectionFile = collectionFile;
	}

	public Part getCreditCardFile() {
		return creditCardFile;
	}

	public void setCreditCardFile(Part creditCardFile) {
		this.creditCardFile = creditCardFile;
	}

	public Part getSaleStateFile() {
		return saleStateFile;
	}

	public void setSaleStateFile(Part saleStateFile) {
		this.saleStateFile = saleStateFile;
	}

	public Part getRepaymentFile() {
		return repaymentFile;
	}

	public void setRepaymentFile(Part repaymentFile) {
		this.repaymentFile = repaymentFile;
	}

	public String getLoteTypeSelected() {
		return loteTypeSelected;
	}

	public void setLoteTypeSelected(String loteTypeSelected) {
		this.loteTypeSelected = loteTypeSelected;
	}

	public List<SelectItem> getLoteTypes() {
		return loteTypes;
	}

	public void setLoteTypes(List<SelectItem> loteTypes) {
		this.loteTypes = loteTypes;
	}

	/*public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}*/


	
	
	
	

}
