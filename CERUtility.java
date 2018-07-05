package com.deere.u90950.eproductsreporter.utility;

import com.deere.u90950.eproductsreporter.application.EProductsReporterProperties;
import com.deere.u90950.eproductsreporter.domain.*;
import com.deere.u90950.eproductsreporter.view.beans.*;
import com.deere.u90950.eproductsreporter.literals.*;
import java.util.*;
import java.text.*;
import static com.deere.u90950.eproductsreporter.literals.IfcEProductsReporterLiterals.EMPTY_STRING;
/**
 * Insert the type's description here.
 * Creation date: (2/3/2003 5:04:36 PM)
 * @author: Sujit
 */
public class CERUtility implements BREAttributes, IMSResources
{
/**
 * CERUtility constructor comment.
 */
public static final String EURO_CURRENCY = "EUR";
public CERUtility() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (2/4/2003 3:30:58 PM)
 */
public static void AddBaseCodeHeading(StringBuffer sb,String typeOfReport) 
{
	if(typeOfReport.equalsIgnoreCase("HTML"))
		sb.append("\t<table border=1>\n");

	sb.append("\t\t<tr valign=\"top\"><td><b>BASE CODE</b></td><td><b>OPTION CODE/ ATTACHMENT</b></td><td><b>SPC LIST PRICE</b></td><td><b>SPC TRD DSCNT</b></td><td><b>SPC MDP</b></td><td><b>SPC START DATE</b></td><td><b>COMAR LIST PRICE</b></td><td><b>COMAR TRD DSCNT</b></td><td><b>COMAR MDP</b></td><td><b>COMAR START DATE</b></td></tr>\n");
	
}
/**
 * Insert the method's description here.
 * Creation date: (2/4/2003 3:18:14 PM)
 */
public static void AddBaseCodeToReport(StringBuffer sb, ProductBaseCode currentBaseCode, TranResultBean comarPrice, boolean baseCodeHeadingAdded,String typeOfReport)
{
	boolean validPCIData = false;
	
	if((comarPrice != null) && (comarPrice.getErrorMessage().startsWith(SUCCESSFUL_COMPLETION)))
	{
		validPCIData = true;
	}
	BREPrice brePrice = currentBaseCode.getPrice();
	//Check if the bre price is valid. The price is valid if the price indicator is N or blank.
	boolean validPrice = brePrice.getAlternatePriceIndicator().equalsIgnoreCase(BREAttributes.PRICE_INDICATOR_N) ||
						 brePrice.getAlternatePriceIndicator().equalsIgnoreCase(EMPTY_STRING);
	if(!baseCodeHeadingAdded)
	{
		AddBaseCodeHeading(sb,typeOfReport);
	}
	sb.append("\t\t<tr>\n");
	sb.append("\t\t\t<td align=right>" + currentBaseCode.getBaseCodeID() + "&nbsp;</td>\n");
	if(currentBaseCode.isFakeBaseCode())
	{
			//sb.append("\t\t\t<td colspan=9 align=center><font color='Red'>" + "Attachment BaseCode" + "&nbsp;</font></td>\n");

	}
	else if(validPCIData)
	{
		sb.append("\t\t\t<td>&nbsp;</td>\n");
		
		//List Price
		//If price instance is missing or the list price is 0 and price is valid replace it with special symbol and display in the red.
		if((Double.isNaN(brePrice.getListPrice())) || ((brePrice.getListPrice() == 0) && validPrice))
		{
			sb.append("\t\t\t<td align=right><font color='Red'>" + EProductsReporterProperties.GetListPriceSpecialSymbol() + "&nbsp;</font></td>\n");		
		}
		else
		{
			if(brePrice.getListPrice() != comarPrice.getListPrice())
			{
				sb.append("\t\t\t<td align=right><font color='Red'>" + brePrice.getListPrice() + "&nbsp;</font></td>\n");
			}
			else
			{
				sb.append("\t\t\t<td align=right>" + brePrice.getListPrice() + "&nbsp;</td>\n");
			}
		}
		
		//Trade Discount
		if(Double.isNaN(brePrice.getTradeDiscount()))
		{
			//Trade discount is Nan. Display it blank.
			sb.append("\t\t\t<td align=right>&nbsp;</td>\n");
		}
		else
		{
			if(brePrice.getTradeDiscount()*100 != Double.parseDouble(comarPrice.getTradeDiscount()))
			{
				sb.append("\t\t\t<td align=right><font color='Red'>" + brePrice.getTradeDiscount() + "&nbsp;</font></td>\n");
			}
			else
			{
				sb.append("\t\t\t<td align=right>" + brePrice.getTradeDiscount() + "&nbsp;</td>\n");
			}
		}
		
		/*//Trade Discount
		if(Double.isNaN(brePrice.getDnsPartsAdditive()))
		{
			//Trade discount is Nan. Display it blank.
			sb.append("\t\t\t<td align=right>&nbsp;</td>\n");
		}
		else
		{
			if(brePrice.getDnsPartsAdditive()*100 != Double.parseDouble(comarPrice.getDNSPartsAdditive()))
			{
				sb.append("\t\t\t<td align=right><font color='Red'>" + brePrice.getDnsPartsAdditive() + "&nbsp;</font></td>\n");
			}
			else
			{
				sb.append("\t\t\t<td align=right>" + brePrice.getDnsPartsAdditive() + "&nbsp;</td>\n");
			}
		}*/
		
		//MDP
		if(Double.isNaN(brePrice.getDealerCost()))
		{
			//MDP is Nan. Display it blank.
			sb.append("\t\t\t<td align=right>&nbsp;</td>\n");
		}
		else
		{
			if(brePrice.getDealerCost() != comarPrice.getMdp())
			{
				sb.append("\t\t\t<td align=right><font color='Red'>" + brePrice.getDealerCost() + "&nbsp;</font></td>\n");
			}
			else
			{
				sb.append("\t\t\t<td align=right>" + brePrice.getDealerCost() + "&nbsp;</td>\n");
			}
		}
		
		//Start Date
		String BREStartDate = FormatStartDateForReport(brePrice.getStartDate(), false);
		String PCIStartDate = FormatStartDateForReport(comarPrice.getStartDate(), true);
		if(BREStartDate.equalsIgnoreCase(PCIStartDate))
		{
			sb.append("\t\t\t<td align=right>" + BREStartDate + "&nbsp;</td>\n");
		}
		else
		{
			sb.append("\t\t\t<td align=right><font color='Red'>" + BREStartDate + "&nbsp;</font></td>\n");
		}
		sb.append("\t\t\t<td align=right>" + comarPrice.getListPrice() + "&nbsp;</td>\n");
		double pciTradeDiscount;
		if((comarPrice.getTradeDiscount() != null) && (comarPrice.getTradeDiscount().length()>0))
		{
			pciTradeDiscount = Double.parseDouble(comarPrice.getTradeDiscount()) /100;		
			sb.append("\t\t\t<td align=right>" + pciTradeDiscount + "&nbsp;</td>\n");
		}
		else
		{
			sb.append("\t\t\t<td align=right>&nbsp;</td>\n");
		}
		sb.append("\t\t\t<td align=right>" + comarPrice.getMdp() + "&nbsp;</td>\n");
		sb.append("\t\t\t<td align=right>" + PCIStartDate + "</td>\n");
	}
	else
	{
		sb.append("\t\t\t<td>&nbsp;</td>\n");
		//If the price instance is missing or list price is 0 and price is valid then use the special symbol.
		if((Double.isNaN(brePrice.getListPrice())) || ((brePrice.getListPrice() == 0) && validPrice))
		{
			sb.append("\t\t\t<td align=right><font color='Red'>" + EProductsReporterProperties.GetListPriceSpecialSymbol() + "&nbsp;</font></td>\n");
		}
		else
		{
			sb.append("\t\t\t<td align=right><font color='Red'>" + brePrice.getListPrice() + "&nbsp;</font></td>\n");
		}
		if(Double.isNaN(brePrice.getTradeDiscount()))
		{
			sb.append("\t\t\t<td align=right><font color='Red'>&nbsp;</font></td>\n");
		}
		else
		{
			sb.append("\t\t\t<td align=right><font color='Red'>" + brePrice.getTradeDiscount() + "&nbsp;</font></td>\n");
		}
/*		if(Double.isNaN(brePrice.getDnsPartsAdditive()))
		{
			sb.append("\t\t\t<td align=right><font color='Red'>&nbsp;</font></td>\n");
		}
		else
		{
			sb.append("\t\t\t<td align=right><font color='Red'>" + brePrice.getDnsPartsAdditive() + "&nbsp;</font></td>\n");
		}
*/		if(Double.isNaN(brePrice.getDealerCost()))
		{
			sb.append("\t\t\t<td align=right><font color='Red'>&nbsp;</font></td>\n");
		}
		else
		{
			sb.append("\t\t\t<td align=right><font color='Red'>" + brePrice.getDealerCost() + "&nbsp;</font></td>\n");
		}
		sb.append("\t\t\t<td align=right><font color='Red'>" + FormatStartDateForReport(brePrice.getStartDate(), false) + "&nbsp;</font></td>\n");
		if(comarPrice != null)
			sb.append("\t\t\t<td colspan=4 align=center><font color='Red'>" + comarPrice.getErrorMessage() + "&nbsp;</font></td>\n");
		else
			sb.append("\t\t\t<td colspan=4>" + "&nbsp;" + "&nbsp;</td>\n");
	}
	
	sb.append("\t\t</tr>\n");

}
/**
 * Insert the method's description here.
 * Creation date: (2/11/2003 3:48:16 PM)
 */
public static void AddModelMessageTable(StringBuffer sb, String message, String typeOfReport) 
{
	if(typeOfReport.equalsIgnoreCase("HTML")){
		sb.append("<div align='left'>\n");
		sb.append("\t<table>\n");
		sb.append("\t\t<tr><td align='left' colspan=7><i>" + message + "</i></td></tr>\n");
		sb.append("\t</table>\n");
		sb.append("</div>\n");
	}else{
		sb.append("\t\t<tr><td class=\"special\">&nbsp;</td><td class=\"special\" align='left' colspan=9><i>" + message + "</i></td></tr>\n");
	}
}
/**
 * Insert the method's description here.
 * Creation date: (2/4/2003 3:18:14 PM)
 */
public static void AddOptionToReport(StringBuffer sb, ProductOption currentOption, ProductBaseCode currentBaseCode, TranResultBean comarPrice,boolean baseCodeLineAdded, boolean baseCodeHeadingAdded, boolean useAttachId, String typeOfReport)
{
	boolean validPCIData = false;
	
	if((comarPrice != null) && (comarPrice.getErrorMessage().startsWith(SUCCESSFUL_COMPLETION)))
	{
		validPCIData = true;
	}
	BREPrice brePrice = currentOption.getPrice();
	//Check if the bre price is valid. The price is valid if the price indicator is N or blank.
	boolean validPrice = brePrice.getAlternatePriceIndicator().equalsIgnoreCase(BREAttributes.PRICE_INDICATOR_N) ||
						 brePrice.getAlternatePriceIndicator().equalsIgnoreCase(EMPTY_STRING);	
	if(!baseCodeHeadingAdded)
	{
		AddBaseCodeHeading(sb,typeOfReport);
	}
	
	if(!baseCodeLineAdded)
	{

		if(currentBaseCode.isFakeBaseCode() && !currentBaseCode.isBlockFromComarTransaction())
		{
			sb.append("\t\t<tr>\n");
			sb.append("\t\t\t<td align=right>" + currentBaseCode.getBaseCodeID() + "&nbsp;</td>\n");
			sb.append("\t\t\t<td colspan=9 align=center><font color='Red'>" + "Attachment BaseCode (PCIModelID not available in SPC)" + "&nbsp;</font></td>\n");
			sb.append("\t\t</tr>\n");		
		}
		else if(currentBaseCode.isFakeBaseCode() && currentBaseCode.isBlockFromComarTransaction())
		{
			sb.append("\t\t<tr>\n");
			sb.append("\t\t\t<td align=right>" + currentBaseCode.getBaseCodeID() + "&nbsp;</td>\n");
			sb.append("\t\t\t<td colspan=9 align=center><font color='Red'>" + "Attachment BaseCode (PCIModelID not available in SPC) and Non-Comar Basecode" + "&nbsp;</font></td>\n");
			sb.append("\t\t</tr>\n");		
		}
		else if(!currentBaseCode.isFakeBaseCode() && currentBaseCode.isBlockFromComarTransaction())
		{
			sb.append("\t\t<tr>\n");
			sb.append("\t\t\t<td align=right>" + currentBaseCode.getBaseCodeID() + "&nbsp;</td>\n");
			sb.append("\t\t\t<td colspan=9 align=center><font color='Red'>" + "Non-Comar Basecode" + "&nbsp;</font></td>\n");
			sb.append("\t\t</tr>\n");		
		}		
		else
		{		
			sb.append("\t\t<tr>\n");
			sb.append("\t\t\t<td align=right>" + currentBaseCode.getBaseCodeID() + "</td>\n");
			sb.append("\t\t\t<td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td>\n");
			sb.append("\t\t</tr>\n");
		}
	}

	String displayId = EMPTY_STRING;
	if(useAttachId)
	{
		if(!currentOption.isAttachment() && !currentOption.getOptionID().equals(currentOption.getAttachmentID()))
		{
			displayId = currentOption.getOptionID() + " / " +currentOption.getAttachmentID(); 
		} else {
			displayId = currentOption.getAttachmentID(); 
		}
	}
	else
		displayId = currentOption.getOptionID();
		
	sb.append("\t\t<tr>\n");
	sb.append("\t\t\t<td>&nbsp;</td>\n");
	sb.append("\t\t\t<td align=right>" + displayId + "&nbsp;</td>\n");
	if(validPCIData)
	{
		//List Price
		//If Price instance is missing or if the list price is 0 and price is valid replace it with special symbol and display in the red.
		if((Double.isNaN(brePrice.getListPrice())) || (brePrice.getListPrice() == 0 && validPrice))
		{
			sb.append("\t\t\t<td align=right><font color='Red'>" + EProductsReporterProperties.GetListPriceSpecialSymbol() + "&nbsp;</font></td>\n");
		}
		else
		{	
			if(brePrice.getListPrice() != comarPrice.getListPrice())
			{
				sb.append("\t\t\t<td align=right><font color='Red'>" + brePrice.getListPrice() + "&nbsp;</font></td>\n");
			}
			else
			{
				sb.append("\t\t\t<td align=right>" + brePrice.getListPrice() + "&nbsp;</td>\n");
			}
		}

		//Trade Discount
		if(Double.isNaN(brePrice.getTradeDiscount()))
		{
			sb.append("\t\t\t<td align=right>&nbsp;</td>\n");
		}
		else
		{
			if(brePrice.getTradeDiscount()*100 != Double.parseDouble(comarPrice.getTradeDiscount()))
			{
				sb.append("\t\t\t<td align=right><font color='Red'>" + brePrice.getTradeDiscount() + "&nbsp;</font></td>\n");
			}
			else
			{
				sb.append("\t\t\t<td align=right>" + brePrice.getTradeDiscount() + "&nbsp;</td>\n");
			}
		}
		
/*		if(Double.isNaN(brePrice.getDnsPartsAdditive()))
		{
			sb.append("\t\t\t<td align=right>&nbsp;</td>\n");
		}
		else
		{
			if(brePrice.getDnsPartsAdditive() != Double.parseDouble(comarPrice.getDNSPartsAdditive()))
			{
				sb.append("\t\t\t<td align=right><font color='Red'>" + brePrice.getDnsPartsAdditive() + "&nbsp;</font></td>\n");
			}
			else
			{
				sb.append("\t\t\t<td align=right>" + brePrice.getDnsPartsAdditive() + "&nbsp;</td>\n");
			}
		}*/
		
		//MDP
		if(Double.isNaN(brePrice.getDealerCost()))
		{
			sb.append("\t\t\t<td align=right>&nbsp;</td>\n");
		}
		else
		{
			if(brePrice.getDealerCost() != comarPrice.getMdp())
			{
				sb.append("\t\t\t<td align=right><font color='Red'>" + brePrice.getDealerCost() + "&nbsp;</font></td>\n");
			}
			else
			{
				sb.append("\t\t\t<td align=right>" + brePrice.getDealerCost() + "&nbsp;</td>\n");
			}
		}
		
		//Start Date
		String BREStartDate = FormatStartDateForReport(brePrice.getStartDate(), false);
		String PCIStartDate = FormatStartDateForReport(comarPrice.getStartDate(), true);
		if(BREStartDate.equalsIgnoreCase(PCIStartDate))
		{
			sb.append("\t\t\t<td align=right>" + BREStartDate + "&nbsp;</td>\n");
		}
		else
		{
			sb.append("\t\t\t<td align=right><font color='Red'>" + BREStartDate + "&nbsp;</font></td>\n");
		}
		
		
		sb.append("\t\t\t<td align=right>" + comarPrice.getListPrice() + "&nbsp;</td>\n");
		double pciTradeDiscount;
		if((comarPrice.getTradeDiscount() != null) && (comarPrice.getTradeDiscount().length()>0))
		{
			pciTradeDiscount = Double.parseDouble(comarPrice.getTradeDiscount()) /100;		
			sb.append("\t\t\t<td align=right>" + pciTradeDiscount + "&nbsp;</td>\n");
		}
		else
		{
			sb.append("\t\t\t<td align=right>&nbsp;</td>\n");
		}
		sb.append("\t\t\t<td align=right>" + comarPrice.getMdp() + "&nbsp;</td>\n");
		sb.append("\t\t\t<td align=right>" + PCIStartDate + "&nbsp;</td>\n");
	}
	else
	{
		//If price instance is missing or list price is 0 and price indicator is N then use the special symbol.
		if((Double.isNaN(brePrice.getListPrice())) || ((brePrice.getListPrice() == 0) && validPrice))
		{
			sb.append("\t\t\t<td align=right><font color='Red'>" + EProductsReporterProperties.GetListPriceSpecialSymbol() + "&nbsp;</font></td>\n");
		}
		else
		{
			sb.append("\t\t\t<td align=right><font color='Red'>" + brePrice.getListPrice() + "&nbsp;</font></td>\n");
		}
		if(Double.isNaN(brePrice.getTradeDiscount()))
		{
			sb.append("\t\t\t<td align=right><font color='Red'>&nbsp;</font></td>\n");
		}
		else
		{
			sb.append("\t\t\t<td align=right><font color='Red'>" + brePrice.getTradeDiscount() + "&nbsp;</font></td>\n");
		}
/*		if(Double.isNaN(brePrice.getDnsPartsAdditive()))
		{
			sb.append("\t\t\t<td align=right><font color='Red'>&nbsp;</font></td>\n");
		}
		else
		{
			sb.append("\t\t\t<td align=right><font color='Red'>" + brePrice.getDnsPartsAdditive() + "&nbsp;</font></td>\n");
		}*/
		if(Double.isNaN(brePrice.getDealerCost()))
		{
			sb.append("\t\t\t<td align=right><font color='Red'>&nbsp;</font></td>\n");
		}
		else
		{
			sb.append("\t\t\t<td align=right><font color='Red'>" + brePrice.getDealerCost() + "&nbsp;</font></td>\n");
		}
		sb.append("\t\t\t<td align=right><font color='Red'>" + FormatStartDateForReport(brePrice.getStartDate(), false) + "&nbsp;</font></td>\n");
		if(comarPrice != null)
			sb.append("\t\t\t<td colspan=4 align=center><font color='Red'>" + comarPrice.getErrorMessage() + "&nbsp;</font></td>\n");
		else
			sb.append("\t\t\t<td colspan=4>" + "&nbsp;" + "&nbsp;</td>\n");

	}
	
	sb.append("\t\t</tr>\n");

}
/**
 * Insert the method's description here.
 * Creation date: (2/5/2003 8:57:31 AM)
 */
public static void CloseReport(StringBuffer sb) 
{
	sb.append("\t</table>");
}

public static void AddClosingLine(StringBuffer sb, String typeOfReport)
{
	if(typeOfReport.equalsIgnoreCase("HTML")){
		sb.append("\n<hr color='Green'>\n");	
	}else{
		sb.append("<tr><td class=\"greenBar\" colspan=10>&nbsp;</td></tr>\n");
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (2/5/2003 12:11:09 PM)
 */
public static String FormatStartDateForReport(String startDate, boolean isPCI) 
{
	String retVal = EMPTY_STRING;
	if((startDate != null) && (startDate.trim().length()> 0))
	{
		//If condition not valid as new RTF structure does not have a S
		//if(startDate.trim().startsWith("S") || isPCI)
		//{
		try
		{
			GregorianCalendar cal = null;
			if(isPCI){
				cal = DateFormatterUtility.ConvertDateYYYYMMDDToCalendar(startDate);
			}	
			else{
				cal = DateFormatterUtility.ConvertDateMMDDYYYYToCalendar(startDate);
			}	
			if(cal != null)
			{
				SimpleDateFormat formatter = new SimpleDateFormat ("d-MMM-yy");
				retVal  = formatter.format(cal.getTime());
			}
			else
			{
				retVal = "<font color=red>" + startDate + "</font>";
			}
		}
		catch(Exception ex)
		{
			retVal = startDate;
		}

		//}
	}
	return retVal;
}
/**
 * Insert the method's description here.
 * Creation date: (2/3/2003 5:06:45 PM)
 */
public static TranResultBean GetComarDataForBaseCode(ProductBaseCode baseCode, String effectiveDate, String countryCode,  String currencyCode)
{
	TranResultBean comarPrice = null;
	try
	{
		String tempCountryCode = countryCode;
		if(EURO_CURRENCY.equalsIgnoreCase(currencyCode))
			tempCountryCode = "XE";
		if("CAD".equalsIgnoreCase(currencyCode))
			tempCountryCode = "CA";
		if("USD".equalsIgnoreCase(currencyCode))
			tempCountryCode = "US";
		
		if("UY".equalsIgnoreCase(countryCode))
		{
			if(EURO_CURRENCY.equalsIgnoreCase(currencyCode))
			     tempCountryCode = "BE";
		    if("USD".equalsIgnoreCase(currencyCode))
			     tempCountryCode = "BU";
			
		}
		if("BR".equalsIgnoreCase(countryCode))
		{
			if("BRL".equalsIgnoreCase(currencyCode))
			tempCountryCode = "BR";
		    if("USD".equalsIgnoreCase(currencyCode))
			tempCountryCode = "BD";
			
		}
		comarPrice = XDXBRE10TranUtility.Run(baseCode.getBaseCodeID(), effectiveDate, tempCountryCode);
	}
	catch (Exception ex)
	{
		throw new com.deere.u90950.eproductsreporter.exceptions.ApplicationException(0, "GetComarDataForBaseCode()", ex);
	}

	return comarPrice;
}
/**
 * Insert the method's description here.
 * Creation date: (2/4/2003 4:45:16 PM)
 */
public static void InitializeModelLevelReport(StringBuffer sb, String eDate, String countryCode, String categoryDescription, String subCategoryDescription, String model) 
{
		
	SimpleDateFormat formatter = new SimpleDateFormat ("MMM d,yyyy");
	Date currentTime = new Date();
	String today  = formatter.format(currentTime); 
	
	GregorianCalendar effDate = DateFormatterUtility.ConvertDateDDMMMYYYYToCalendar(eDate);
	String effectiveDate =  formatter.format(effDate.getTime());
	sb.append("<div align='center'>\n<hr color='Green'>\n");
	sb.append("<table>\n");
	sb.append("<tr><td colspan=2  align='center'>&nbsp;</td></tr>\n");
	sb.append("<tr><td colspan=10></td></tr>\n");
	sb.append("<tr><td>Report Generation Date</td><td>" + today + "</td></tr>\n");
	sb.append("<tr><td>Requested Price Effective Date (Current & Future)</td><td>" + effectiveDate + "</td></tr>\n");

	sb.append("<tr><td></td></tr>\n");
	if(categoryDescription != null && !categoryDescription.equals(EMPTY_STRING)) {
		sb.append("<tr><td>JD_Category</td><td>" + categoryDescription + "</td></tr>\n");
	}
	if(subCategoryDescription != null && !subCategoryDescription.equals(EMPTY_STRING)) {
		sb.append("<tr><td>JD_SubCategory</td><td>" + subCategoryDescription + "</td></tr>\n");
	}	
	if( model != null && !model.equals(EMPTY_STRING))
	{
		sb.append("<tr><td>Model</td><td>" + model + "</td></tr>\n");
	}
	sb.append("<tr><td>Currency</td><td>" + countryCode + "</td></tr>\n");

	sb.append("</table>\n");
	sb.append("</div>\n");

}
public static void InitializeModelLevelReportForExcel(StringBuffer sb, String eDate, String countryCode, String categoryDescription, String subCategoryDescription, String model) 
{
		
	SimpleDateFormat formatter = new SimpleDateFormat ("MMM d,yyyy");
	Date currentTime = new Date();
	String today  = formatter.format(currentTime); 
	
	GregorianCalendar effDate = DateFormatterUtility.ConvertDateDDMMMYYYYToCalendar(eDate);
	String effectiveDate =  formatter.format(effDate.getTime());
	sb.append("<tr><td class=\"greenBar\" colspan=10>&nbsp;</td></tr>\n");
	sb.append("<tr><td class=\"special\">&nbsp;</td><td class=\"special\" nowrap colspan=3>&nbsp;&nbsp;Report Generation Date</td><td class=\"special\" colspan=6>" + today + "</td></tr>\n");
	sb.append("<tr><td class=\"special\">&nbsp;</td><td class=\"special\" nowrap colspan=3>&nbsp;&nbsp;Requested Price Effective Date (Current & Future)</td><td class=\"special\" colspan=6>" + effectiveDate + "</td></tr>\n");

	sb.append("<tr><td class=\"special\" colspan=10></td></tr>\n");
	if( categoryDescription != null && !categoryDescription.equals(EMPTY_STRING)) {
		sb.append("<tr><td class=\"special\">&nbsp;</td><td class=\"special\" nowrap colspan=3>&nbsp;&nbsp;JD_Category</td><td class=\"special\" colspan=6>" + categoryDescription + "</td></tr>\n");
	}
	if( subCategoryDescription != null && !subCategoryDescription.equals(EMPTY_STRING)) {		
		sb.append("<tr><td class=\"special\">&nbsp;</td><td class=\"special\" nowrap colspan=3>&nbsp;&nbsp;JD_SubCategory</td><td class=\"special\" colspan=6>" + subCategoryDescription + "</td></tr>\n");
	}	
	if(model != null && !model.equals(EMPTY_STRING)) {
		sb.append("<tr><td class=\"special\">&nbsp;</td><td class=\"special\" nowrap colspan=3>&nbsp;&nbsp;Model</td><td class=\"special\" colspan=6>" + model + "</td></tr>\n");
	}
	sb.append("<tr><td class=\"special\">&nbsp;</td><td class=\"special\" nowrap colspan=3>&nbsp;&nbsp;Currency</td><td class=\"special\" colspan=6>" + countryCode + "</td></tr>\n");
	sb.append("<tr><td class=\"special\"></td></tr>\n");

}
/**
 * Insert the method's description here.
 * Creation date: (2/4/2003 11:43:45 AM)
 */
public static boolean ValidateBaseCodePrices(BREPrice brePrice, TranResultBean comarPrice) 
{
	boolean retVal = true;
	if((comarPrice != null) && (comarPrice.getErrorMessage().startsWith(SUCCESSFUL_COMPLETION)))
	{
		//no need to check the price indicator for the basecode as per spec given.
		//Check if the bre price is valid. The price is valid if the price indicator is N or blank.
		boolean validPrice = brePrice.getAlternatePriceIndicator().equalsIgnoreCase(BREAttributes.PRICE_INDICATOR_N) ||
							 brePrice.getAlternatePriceIndicator().equalsIgnoreCase(EMPTY_STRING);
		if(Double.isNaN(brePrice.getListPrice()))
		{
			//Return false if the price instance is missing.
			return false;
		}
		if(brePrice.getListPrice() == 0 && validPrice)
		{
			//Return false if the BRE list price is 0 and the price is valid.
			retVal=false;
		}
		else if(brePrice.getListPrice() != comarPrice.getListPrice())
		{
			retVal=false;
		}
		else if(brePrice.getDealerCost() != comarPrice.getMdp())
		{
			retVal=false;
		}
		else if(brePrice.getTradeDiscount() != Double.parseDouble(comarPrice.getTradeDiscount())/100)
		{
			retVal=false;
		}
		/*else if(brePrice.getDnsPartsAdditive() != Double.parseDouble(comarPrice.getDNSPartsAdditive()))
		{
			retVal=false;
		}*/
	}
	else
	{
		retVal = false;
	}
	return retVal;		
		
}
/**
 * Insert the method's description here.
 * Creation date: (2/4/2003 12:21:49 PM)
 * @return boolean
 */
public static boolean ValidateOptionPrices(BREPrice brePrice, TranResultBean comarPrice) 
{
	boolean retVal = true;
	//if Price indicator is N/Y/P/C do the validation. Else return true so that it wont land in the report.
	//Also include Price indicator of "".This will include those options whose price instance is missing.
	if(brePrice.getAlternatePriceIndicator().equalsIgnoreCase(BREAttributes.PRICE_INDICATOR_N)
		|| brePrice.getAlternatePriceIndicator().equalsIgnoreCase(BREAttributes.PRICE_INDICATOR_Y)
		|| brePrice.getAlternatePriceIndicator().equalsIgnoreCase(BREAttributes.PRICE_INDICATOR_P)
		|| brePrice.getAlternatePriceIndicator().equalsIgnoreCase(BREAttributes.PRICE_INDICATOR_C)
		|| brePrice.getAlternatePriceIndicator().equalsIgnoreCase(BREAttributes.PRICE_INDICATOR_R)
		|| brePrice.getAlternatePriceIndicator().equalsIgnoreCase(BREAttributes.PRICE_INDICATOR_M)
		|| brePrice.getAlternatePriceIndicator().equalsIgnoreCase(BREAttributes.PRICE_INDICATOR_D)
		|| brePrice.getAlternatePriceIndicator().equalsIgnoreCase(BREAttributes.PRICE_INDICATOR_G)
		|| brePrice.getAlternatePriceIndicator().equalsIgnoreCase(EMPTY_STRING))
	{

		if((comarPrice != null) && (comarPrice.getErrorMessage().startsWith(SUCCESSFUL_COMPLETION)))
		{	
			//Check if the bre price is valid. The price is valid if the price indicator is N or blank.
			boolean validPrice = brePrice.getAlternatePriceIndicator().equalsIgnoreCase(BREAttributes.PRICE_INDICATOR_N) ||
								 brePrice.getAlternatePriceIndicator().equalsIgnoreCase(EMPTY_STRING);		
			if(Double.isNaN(brePrice.getListPrice()))
			{
				//If price instance is missing
				retVal=false;
			}
			else if((brePrice.getListPrice() == 0) && validPrice)
			{
				//Return false if the BRE list price is 0 and price is valid.
				retVal=false;
			}
			else if(brePrice.getListPrice() != comarPrice.getListPrice())
			{
				retVal=false;
			}
			else if(brePrice.getDealerCost() != comarPrice.getMdp())
			{
				retVal=false;
			}
			else if(brePrice.getTradeDiscount() != Double.parseDouble(comarPrice.getTradeDiscount())/100)
			{
				retVal=false;
			}
			/*else if(brePrice.getDnsPartsAdditive() != Double.parseDouble(comarPrice.getDNSPartsAdditive())/100)
			{
				retVal=false;
			}*/
		}
		else
		{
			retVal = false;
		}
	}
	return retVal;
		
		
}

/**
 * Insert the method's description here.
 * Creation date: (2/3/2003 5:06:45 PM)
 */
public static TranResultBean GetComarDataForOption(String optionOrAttachId, String effectiveDate, String countryCode, String currencyCode)
{
	TranResultBean comarPrice = null;
	try
	{
		String tempCountryCode = countryCode;
		if(EURO_CURRENCY.equalsIgnoreCase(currencyCode))
			tempCountryCode = "XE";
		if("CAD".equalsIgnoreCase(currencyCode))
			tempCountryCode = "CA";
		if("USD".equalsIgnoreCase(currencyCode))
			tempCountryCode = "US";
		
		if("UY".equalsIgnoreCase(countryCode))
		{
			if(EURO_CURRENCY.equalsIgnoreCase(currencyCode))
			     tempCountryCode = "BE";
		    if("USD".equalsIgnoreCase(currencyCode))
			     tempCountryCode = "BU";
			
		}
		if("BR".equalsIgnoreCase(countryCode))
		{
			if("BRL".equalsIgnoreCase(currencyCode))
			tempCountryCode = "BR";
		    if("USD".equalsIgnoreCase(currencyCode))
			tempCountryCode = "BD";
			
		}
		
		comarPrice = XDXBRE10TranUtility.Run(optionOrAttachId, effectiveDate, tempCountryCode);
	}
	catch (Exception ex)
	{
		throw new com.deere.u90950.eproductsreporter.exceptions.ApplicationException(0, "GetComarDataForOption()", ex);
	}

	return comarPrice;
}

public static void CleanupConnection()
{
	
	try
	{
		XDXBRE10TranUtility.Cleanup();
		
	}
	catch (Exception ex)
	{
		throw new com.deere.u90950.eproductsreporter.exceptions.ApplicationException(0, "CleanupConnection()", ex);
	}
	
}
}
