/**
 * Appcelerator Titanium Mobile Modules
 * Copyright (c) 2010-present by Appcelerator, Inc. All Rights Reserved.
 * Apache 2 License
 */

package ti.inappbilling;

import java.util.HashMap;

import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;

import ti.inappbilling.util.Inventory;
import ti.inappbilling.util.Purchase;
import ti.inappbilling.util.SkuDetails;

@Kroll.proxy
public class InventoryProxy extends KrollProxy
{
	private Inventory inventory;

	public InventoryProxy(Inventory inv)
	{
		super();

		inventory = inv;
	}

	@Kroll.method
	public boolean hasDetails(String sku)
	{
		return inventory.hasDetails(sku);
	}

	/** Returns the listing details for an in-app product. */
	@Kroll.method
	public HashMap getDetails(String sku)
	{
		if (!inventory.hasDetails(sku)) {
			return null;
		}

		HashMap<String, String> details = new HashMap<String, String>();
		SkuDetails skuDetails = inventory.getSkuDetails(sku);

		details.put("productId", skuDetails.getSku());
		details.put("type", skuDetails.getType());
		details.put("price", skuDetails.getPrice());
		details.put("title", skuDetails.getTitle());
		details.put("description", skuDetails.getDescription());
		details.put("price_amount_micros", skuDetails.getPriceAmountMicros());
		details.put("price_currency_code", skuDetails.getPriceCurrencyCode());

		return details;
	}

	@Kroll.method
	public boolean hasPurchase(String sku)
	{
		return inventory.hasPurchase(sku);
	}

	@Kroll.method
	public PurchaseProxy getPurchase(String sku)
	{
		Purchase purchase = inventory.getPurchase(sku);
		if (purchase == null) {
			return null;
		}

		return new PurchaseProxy(purchase);
	}

	@Kroll.method
	public void erasePurchase(String sku)
	{
		inventory.erasePurchase(sku);
	}
}
