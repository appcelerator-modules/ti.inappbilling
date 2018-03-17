/**
 * Appcelerator Titanium Mobile Modules
 * Copyright (c) 2010-present by Appcelerator, Inc. All Rights Reserved.
 * Apache 2 License
 */

package ti.inappbilling;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;

import ti.inappbilling.util.Purchase;

@Kroll.proxy
public class PurchaseProxy extends KrollProxy
{
	private Purchase purchase;

	public PurchaseProxy(Purchase p)
	{
		super();

		purchase = p;
	}

	@Kroll.method
  @Kroll.getProperty
  public String getType()
	{
		return purchase.getItemType();
	}

	@Kroll.method
  @Kroll.getProperty
	public String getOrderId()
	{
		return purchase.getOrderId();
	}

	@Kroll.method
  @Kroll.getProperty
	String getPackageName()
	{
		return purchase.getPackageName();
	}

	@Kroll.method
  @Kroll.getProperty
	public String getProductId()
	{
		return purchase.getSku();
	}

  @Kroll.method
  @Kroll.getProperty
	public int getPurchaseTime()
	{
		return purchase.getPurchaseTime();
	}

  @Kroll.method
  @Kroll.getProperty
	public int getPurchaseState()
	{
		return purchase.getPurchaseState();
	}

	@Kroll.method
  @Kroll.getProperty
	public String getDeveloperPayload()
	{
		return purchase.getDeveloperPayload();
	}

	@Kroll.method
	@Kroll.getProperty
	public String getToken()
	{
		return purchase.getToken();
	}

	@Kroll.method
  @Kroll.getProperty
	public String getSignature()
	{
		return purchase.getSignature();
	}

  public Purchase getPurchase()
	{
		return purchase;
	}

  @Kroll.method
  @Kroll.getProperty
	public KrollDict getReceipt()
	{
		KrollDict receipt = new KrollDict();
		receipt.put("data", purchase.getOriginalJson());
		receipt.put("signature", purchase.getSignature());
		return receipt;
	}
}
