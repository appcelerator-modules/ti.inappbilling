/**
 * Appcelerator Titanium Mobile Modules
 * Copyright (c) 2010-2014 by Appcelerator, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */

package ti.inappbilling;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.util.TiActivitySupport;

import ti.inappbilling.util.IabHelper;
import ti.inappbilling.util.IabResult;
import ti.inappbilling.util.Inventory;
import ti.inappbilling.util.Purchase;
import android.app.Activity;
import android.util.Log;

@Kroll.module(name = "Inappbilling", id = "ti.inappbilling", propertyAccessors = { "onInitCompleted",
		"onQueryInventoryCompleted" })
public class InappbillingModule extends KrollModule {

	// Standard Debugging variables
	private static final String TAG = "TiInAppBill";
	private boolean DBG = false;

	// The helper object
	IabHelper mHelper;

	private static InappbillingModule _instance;

	public InappbillingModule() {
		super();
		_instance = this;

		mHelper = null;
	}

	public static InappbillingModule getInstance() {
		return _instance;
	}

	// Response Constants
	@Kroll.constant
	public static final int RESULT_OK = IabHelper.BILLING_RESPONSE_RESULT_OK;
	@Kroll.constant
	public static final int RESULT_USER_CANCELED = IabHelper.BILLING_RESPONSE_RESULT_USER_CANCELED;
	@Kroll.constant
	public static final int RESULT_BILLING_UNAVAILABLE = IabHelper.BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE;
	@Kroll.constant
	public static final int RESULT_ITEM_UNAVAILABLE = IabHelper.BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE;
	@Kroll.constant
	public static final int RESULT_DEVELOPER_ERROR = IabHelper.BILLING_RESPONSE_RESULT_DEVELOPER_ERROR;
	@Kroll.constant
	public static final int RESULT_ERROR = IabHelper.BILLING_RESPONSE_RESULT_ERROR;
	@Kroll.constant
	public static final int RESULT_ITEM_ALREADY_OWNED = IabHelper.BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED;
	@Kroll.constant
	public static final int RESULT_ITEM_NOT_OWNED = IabHelper.BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED;

	// IABHELPER Constants
	@Kroll.constant
	public static final int IAB_RESULT_REMOTE_EXCEPTION = IabHelper.IABHELPER_REMOTE_EXCEPTION;
	@Kroll.constant
	public static final int IAB_RESULT_BAD_RESPONSE = IabHelper.IABHELPER_BAD_RESPONSE;
	@Kroll.constant
	public static final int IAB_RESULT_VERIFICATION_FAILED = IabHelper.IABHELPER_VERIFICATION_FAILED;
	@Kroll.constant
	public static final int IAB_RESULT_SEND_INTENT_FAILED = IabHelper.IABHELPER_SEND_INTENT_FAILED;
	@Kroll.constant
	public static final int IAB_RESULT_UNKNOWN_PURCHASE_RESPONSE = IabHelper.IABHELPER_UNKNOWN_PURCHASE_RESPONSE;
	@Kroll.constant
	public static final int IAB_RESULT_MISSING_TOKEN = IabHelper.IABHELPER_MISSING_TOKEN;
	@Kroll.constant
	public static final int IAB_RESULT_UNKNOWN_ERROR = IabHelper.IABHELPER_UNKNOWN_ERROR;
	@Kroll.constant
	public static final int IAB_RESULT_SUBSCRIPTIONS_NOT_AVAILABLE = IabHelper.IABHELPER_SUBSCRIPTIONS_NOT_AVAILABLE;
	@Kroll.constant
	public static final int IAB_RESULT_INVALID_CONSUMPTION = IabHelper.IABHELPER_INVALID_CONSUMPTION;

	// These are the types supported in the IAB v3
	@Kroll.constant
	public static final String ITEM_TYPE_INAPP = IabHelper.ITEM_TYPE_INAPP;
	@Kroll.constant
	public static final String ITEM_TYPE_SUBSCRIPTION = IabHelper.ITEM_TYPE_SUBS;

	// Purchase State Constants
	@Kroll.constant
	public static final int PURCHASE_STATE_PURCHASED = 0;
	@Kroll.constant
	public static final int PURCHASE_STATE_CANCELED = 1;
	@Kroll.constant
	public static final int PURCHASE_STATE_REFUNDED = 2;

	// Event name constants
	public static final String SETUP_COMPLETE = "	";
	public static final String QUERY_INVENTORY_COMPLETE = "queryinventorycomplete";
	public static final String PURCHASE_COMPLETE = "purchasecomplete";
	public static final String CONSUME_COMPLETE = "consumecomplete";
	public static final String PUBLIC_KEY = "publicKey";

	private KrollFunction onSetupCallback;
	private KrollFunction onQueryInventoryCallback;
	private KrollFunction onPurchaseCallback;
	private KrollFunction onConsumeCallback;

	@Override
	public void onDestroy(Activity activity) {
		logDebug("onDestroy");
		if (mHelper != null) {
			mHelper.dispose();
		}
		super.onDestroy(activity);
	}

	/*
	 * Public API
	 */
	@Kroll.method
	public void enableDebug() {
		DBG = true;
	}

	@Kroll.method
	public void setDebug(Boolean debug) {
		DBG = debug;
	}

	@Kroll.method
	public void startSetup(Object first, @Kroll.argument(optional = true) Object second) {
		String base64EncodedPublicKey = null;
		Boolean debug = false;
		if (first instanceof String) {
			base64EncodedPublicKey = importBase64String((String) first);
		} else {
			KrollDict args = new KrollDict((HashMap) first);
			checkRequired(args, PUBLIC_KEY);
			base64EncodedPublicKey = importBase64String((String) args.get(PUBLIC_KEY));
			if (args.containsKeyAndNotNull(SETUP_COMPLETE)) {
				onSetupCallback = (KrollFunction) args.get(SETUP_COMPLETE);
			}
			if (args.containsKeyAndNotNull("debug")) {
				DBG = args.getBoolean("debug");
			}
		}
		if (second != null) {
			onSetupCallback = (KrollFunction) second;
		}
		if (hasProperty("onSetupComplete")) {
			Object o = getProperty("onSetupComplete");
			if (o instanceof KrollFunction) {
				onSetupCallback = (KrollFunction) o;
			}
		}

		if (mHelper == null) {
			// Create the helper, passing it our context and the public key to verify
			// signatures with
			logDebug("Creating IAB helper.");
			mHelper = new IabHelper(TiApplication.getInstance().getApplicationContext(), base64EncodedPublicKey);
		}

		if (mHelper.isSetupDone()) {
			throw new IllegalStateException("Setup already completed.");
		}

		// enable debug logging (for a production application, you should set this to
		// false).
		mHelper.enableDebugLogging(debug);

		// Start setup. This is asynchronous and the specified listener
		// will be called once setup completes.
		logDebug("Starting setup.");
		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			public void onIabSetupFinished(IabResult result) {
				logDebug("Setup finished.");
				HashMap<String, Object> res = createEventObjectWithResult(result, null, null);
				if (hasListeners(SETUP_COMPLETE)) {
					fireEvent(SETUP_COMPLETE, res);
				}
				if (onSetupCallback != null) {
					onSetupCallback.callAsync(getKrollObject(), res);
				}

			}
		});
	}

	@Kroll.method
	public Boolean subscriptionsSupported() {
		checkSetupComplete();
		return mHelper.subscriptionsSupported();
	}

	@Kroll.method
	public void getPurchases(Object o) {
		final boolean history = false;
		final boolean details = false;
		
		if (o instanceof KrollFunction) {
			onPurchaseCallback = (KrollFunction) o;
			checkSetupComplete();

			mHelper.queryInventoryAsync(history, details, mGotInventoryListener);
		} else
			throw new IllegalArgumentException("Parameter of getPurchases() must be a callback function");

	}

	@Kroll.method
	public void getPurchaseHistory(Object o) {
		final boolean history = true;
		final boolean details = false;
		if (o instanceof KrollFunction) {
			onPurchaseCallback = (KrollFunction) o;
			checkSetupComplete();

			mHelper.queryInventoryAsync(history, details,  mGotInventoryListener);
		} else
			throw new IllegalArgumentException("Parameter of getPurchases() must be a callback function");

	}

	@Kroll.method
	public void queryInventory(@SuppressWarnings("rawtypes") @Kroll.argument(optional = true) HashMap hm) {
		checkSetupComplete();
		boolean history=false;
		if (hasProperty("onQueryInventoryComplete")) {
			Object o = getProperty("onQueryInventoryComplete");
			if (o instanceof KrollFunction) {
				onQueryInventoryCallback = (KrollFunction) o;
			}
		}
		boolean queryDetails = true;
		List<String> moreItemSkus = null;
		List<String> moreSubsSkus = null;

		if (hm != null) {
			@SuppressWarnings("unchecked")
			KrollDict args = new KrollDict(hm);
			if (args.containsKeyAndNotNull("history")) {
				history = args.getBoolean("history");
			}
			if (args.containsKeyAndNotNull("details")) {
				queryDetails = args.getBoolean("details");
			}
			if (args.containsKeyAndNotNull("queryDetails")) {
				queryDetails = args.getBoolean("queryDetails");
			}
			moreItemSkus = stringListFromDict(args, "moreItems", "queryInventory()");
			moreSubsSkus = stringListFromDict(args, "moreSubs", "queryInventory()");
		}

		mHelper.queryInventoryAsync(history, queryDetails, moreItemSkus, moreSubsSkus, mGotInventoryListener);
	}

	// Listener that's called when we finish querying the items and subscriptions we
	// own
	IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
		public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
			logDebug("Query inventory finished.");
			HashMap<String, Object> event = createEventObjectWithResult(result, inventory, null);
			if (onPurchaseCallback != null)
				onPurchaseCallback.callAsync(getKrollObject(), event);
			if (hasListeners(QUERY_INVENTORY_COMPLETE)) {
				fireEvent(QUERY_INVENTORY_COMPLETE, event);
			}
		}
	};

	@Kroll.method
	public void purchase(@SuppressWarnings("rawtypes") HashMap hm) {
		checkSetupComplete();

		@SuppressWarnings("unchecked")
		KrollDict args = new KrollDict(hm);

		checkRequired(args, "productId");
		checkRequired(args, "type");

		String sku = args.getString("productId");
		String itemType = args.getString("type");
		String payload = args.optString("developerPayload", "");

		if (!itemType.equals(IabHelper.ITEM_TYPE_INAPP) && !itemType.equals(IabHelper.ITEM_TYPE_SUBS)) {
			throw new IllegalArgumentException("Invalid `type` passed to purhcase()");
		}

		Activity activity = TiApplication.getAppCurrentActivity();
		TiActivitySupport activitySupport = (TiActivitySupport) activity;
		final int resultCode = activitySupport.getUniqueResultCode();

		mHelper.launchPurchaseFlow(activity, sku, itemType, resultCode, mPurchaseFinishedListener, payload);
	}

	// Callback for when a purchase is finished
	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
		public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
			logDebug("Purchase finished: " + result + ", purchase: " + purchase);

			if (hasListeners(PURCHASE_COMPLETE)) {
				fireEvent(PURCHASE_COMPLETE, createEventObjectWithResult(result, null, purchase));
			}

		}
	};

	@Kroll.method
	public void consume(HashMap hm) {
		checkSetupComplete();

		KrollDict args = new KrollDict(hm);

		checkRequired(args, "purchases");
		Object purchaseProxies = args.get("purchases");
		List<Purchase> purchases = new ArrayList<Purchase>();

		if (!(purchaseProxies instanceof Object[])) {
			throw new IllegalArgumentException(
					"Invalid argument type `" + purchaseProxies.getClass().getName() + "` passed to consume()");
		}

		for (int i = 0; i < ((Object[]) purchaseProxies).length; i++) {
			Object purchase = ((Object[]) purchaseProxies)[i];
			if (!(purchase instanceof PurchaseProxy)) {
				throw new IllegalArgumentException(
						"Invalid argument type `" + purchase.getClass().getName() + "` passed to consume()");
			}
			purchases.add(((PurchaseProxy) purchase).getPurchase());
		}

		mHelper.consumeAsync(purchases, mConsumeMiltiFinishedListener);
	}

	// Called when multi-consumption is complete
	IabHelper.OnConsumeMultiFinishedListener mConsumeMiltiFinishedListener = new IabHelper.OnConsumeMultiFinishedListener() {
		public void onConsumeMultiFinished(List<Purchase> purchases, List<IabResult> results) {
			logDebug("Consumption finished: " + results + ", purchase: " + purchases);

			if (hasListeners(CONSUME_COMPLETE)) {
				for (int i = 0; i < purchases.size(); i++) {
					logDebug("Consumption finished for Purchase: " + purchases.get(i) + ", result: " + results.get(i));
					fireEvent(CONSUME_COMPLETE, createEventObjectWithResult(results.get(i), null, purchases.get(i)));
				}
			}
		}
	};

	/**
	 * Utils
	 */
	void checkRequired(KrollDict args, String key) {
		if (!args.containsKey(key)) {
			throw new IllegalArgumentException("`" + key + "` is required");
		}
	}

	void checkSetupComplete() {
		if (mHelper == null || !mHelper.isSetupDone()) {
			throw new RuntimeException("'startSetup' must complete before calling any other module methods");
		}
	}

	HashMap<String, Object> createEventObjectWithResult(IabResult result, Inventory inventory, Purchase purchase) {
		HashMap<String, Object> event = new HashMap<String, Object>();
		event.put("success", result.isSuccess());
		event.put("responseCode", result.getResponse());

		if (purchase != null) {
			event.put("purchase", new PurchaseProxy(purchase));
		}
		if (inventory != null) {
			event.put("inventory", new InventoryProxy(inventory));
		}

		return event;
	}

	List<String> stringListFromDict(KrollDict args, String propertyName, String methodName) {
		List<String> list = null;
		if (args.containsKeyAndNotNull(propertyName)) {
			Object itemsArray = args.get(propertyName);
			if (!(itemsArray instanceof Object[])) {
				throw new IllegalArgumentException("Invalid argument type `" + itemsArray.getClass().getName()
						+ "` passed to " + methodName + " for '" + propertyName + "'");
			}
			list = new ArrayList<String>();
			for (int i = 0; i < ((Object[]) itemsArray).length; i++) {
				Object item = ((Object[]) itemsArray)[i];
				if (!(item instanceof String)) {
					throw new IllegalArgumentException("Invalid argument type `" + item.getClass().getName()
							+ "` passed to " + methodName + " in '" + propertyName + "'");
				}
				list.add((String) item);
			}
		}

		return list;
	}

	void logDebug(String msg) {
		if (DBG)
			Log.d(TAG, msg);
	}

	void logError(String msg) {
		Log.e(TAG, "In-app billing error: " + msg);
	}

	void logWarn(String msg) {
		Log.w(TAG, "In-app billing warning: " + msg);
	}
	
	private static String importBase64String(String value) {
		final String regex = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$";
		if (value.matches(regex)) return value;
		return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
	}
}
