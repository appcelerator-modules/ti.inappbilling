# Titanium InAppBilling Module [![Build Status](https://travis-ci.org/appcelerator-modules/ti.inappbilling.png)](https://travis-ci.org/appcelerator-modules/ti.inappbilling)

The InAppBilling Module for Appcelerator Titanium

## Contributors

* Alexander Conway (Logical Labs)
* Jon Alter

Interested in contributing? Read the [contributors/committer's](https://wiki.appcelerator.org/display/community/Home) guide.

## Legal

This module is Copyright (c) 2010-2014 by Appcelerator, Inc. All Rights Reserved. Usage of this module is subject to 
the Terms of Service agreement with Appcelerator, Inc.  

## Methods of module

### startSetup()



### subscriptionsSupported()

### queryInventory()
This method has a JS object as property with the properties below:

* queryDetails, Boolean
* moreItemSkus
* moreSubsSkus
* history, new since 2.0.0
 
```js
const IAP = require("ti.inappbilling");
IAP.onQueryinventorycomplete = function(e) {
	console.log(e);
};
IAP.queryInventory({
	details : true,
	moreItemsSKUs : ['1223','56467'],
	moreSubsSKUs : ['437364']	
})
```

### purchase()

### consume()

