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

The most methods works asynchronly. The result is available as module event, as module property or as callback binded to the method. We recommended the last method, because it is local capsulated to to calling method.

### enableDebug();
### setDebug(true|false);

### startSetup(first,second)

`first` can be a string or an object, `second` is optional and must be (if exists) a (callback-) function.


The initialization method has two alternative pattern:

* Object with properties public key and debug and a second, optional callback property

* String public key and  and a second, optional callback property

The publicKey can be in native format or in base64, this will automatically imported.

```js
const IAP = require("ti.inappbilling");

// first version:
IAP.startSetup({
	publicKey : 'ssshh27hwkw',
	debug : true
	setupComplete : function(e) {
		console.log(e);
	}
});

// second version:
IAP.startSetup(
	'ssshh27hwkw',
	function(e) {
		console.log(e);
});

// third version:
IAP.startSetup({
	publicKey : 'ssshh27hwkw',
	debug : true
	} ,function(e) {
		console.log(e);
});
```
Alternatively you can use a module event listener.

```js
IAP.addEventListener('setupcomplete'),function(){
});
```
In this case you can only use one parameter in this method (String or object).


There is a method alias named `init()`.

### subscriptionsSupported()
This method return true or false;

### queryInventory(first, second)

The parameter pattern is similar `startSetup(first,second)`.  `first` is an object with properties, `second` is optional and a callback function.
This method has a JS object as property with the properties below:

* queryDetails, Boolean
* history, Boolean
* moreItemSkus, list of strings
* moreSubsSkus, list of strings
* history, Boolean, new since 2.0.0
 
```js
const IAP = require("ti.inappbilling");
IAP.onQueryinventorycomplete = function(e) {
	console.log(e);
};
IAP.queryInventory({
	details : true,  // default true
	history : false, // default false
	moreItemsSKUs : ['1223','56467'],
	moreSubsSKUs : ['437364']	
},function(e) {
	console.log(e);
});
```
### getPurchases(callback)
It is a wrapper to queryInventory. The only paramter is the callback for result.



### getPurchaseHistory(callback)

### purchase()

### consume()


## OverView all callbacks/events

All asyncron methods has 3 methods to react to result:

* event
* callback in caller properties
* callback in module

See `startSetup()`.

| event       |  callback          |
| ------------- |:-------------:|
| 'setupcomplete'      | onSetupComplete  |
| 'queryinventorycomplete'      | onQeryinventoryComplete     |
| 'purchasecomplete'|onPurchaseComplete    |
| 'consumecomplete'| onConsumeComplete  |
