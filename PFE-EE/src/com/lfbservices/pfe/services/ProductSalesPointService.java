package com.lfbservices.pfe.services;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.lfbservices.pfe.AuthenticationFilter;
import com.lfbservices.pfe.FcmNotifications;
import com.lfbservices.pfe.dao.Access;
import com.lfbservices.pfe.model.ProductSalesPoint;

@Path("/ProductSalesPoint")
public class ProductSalesPointService {

		
	@Path("/UpdateProductSalesPoint")
	@POST
	@RolesAllowed({AuthenticationFilter.MERCHANT})
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void updateProductSalesPoint(
			@DefaultValue("") @QueryParam("sales_point_id") String salesPointId,
			@DefaultValue("") @QueryParam("product_barcode") String productBarcode,
			@DefaultValue("-1") @QueryParam("product_quantity_old") int productQuantityOld,
			@DefaultValue("-1") @QueryParam("product_price_old") double productPriceOld,
			@DefaultValue("-1") @QueryParam("product_quantity_new") int productQuantityNew,
			@DefaultValue("-1") @QueryParam("product_price_new") double productPriceNew) throws Exception {
		
		final List<String> missingParameters = new ArrayList<String>();
		
		if (salesPointId.trim().equals("")) missingParameters.add("sales_point_id");
		if (productBarcode.trim().equals("")) missingParameters.add("product_barcode");
		if (productQuantityOld == -1) missingParameters.add("product_quantity_old");
		if (productPriceOld == -1) missingParameters.add("product_price_old");
		if (productQuantityNew == -1) missingParameters.add("product_quantity_new");
		if (productPriceNew == -1) missingParameters.add("product_price_new");
		if (missingParameters.size() != 0) {
		    throw new WebApplicationException(
		      Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
		        .entity("Missing parameters : " + missingParameters)
		        .build()
		    );
		}
		
		final ProductSalesPoint productSalesPoint = new ProductSalesPoint(productBarcode, salesPointId, productQuantityNew, productPriceNew);
		final boolean updated = Access.updateProductSalesPoint(productSalesPoint);
		if (updated) {
			final List<String> devicesIds = Access.getDevicesIdsForNotification(salesPointId, productBarcode);
			if (devicesIds != null) {
				for (String deviceId : devicesIds) {
					FcmNotifications.pushFCMNotification(deviceId, salesPointId, productBarcode, productQuantityOld, productPriceOld, productQuantityNew, productPriceNew);
				}
			}
		}
	}
}
