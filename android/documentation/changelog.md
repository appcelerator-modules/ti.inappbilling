# Change Log
<pre>
v3.0.2  Compiled module with latest SDK (platform 23) and NDK (r10e) [MOD-2154]

v3.0.1  Open sourcing module [MOD-1730]

v3.0.0  Updating to Google In-App Billing api v3 [MOD-1355]

v2.2.1	Fixed error when purchase is called and in-app billing is not supported [MOD-1251, MOD-1262]
		Building with 2.1.3.GA to support x86 devices [MOD-1104]
	
v2.2.0	Updating to Google In-App Billing api v2, adding support for subscriptions [MOD-816]

- Deprecated using the `RESPONSE_EVENT` eventListener to get the synchronous response from each method call. Use the event object that the method returns.

v2.1	Fixed crash after service is restarted with a null intent [MOD-455]

v2.0	Upgraded to module api version 2 for 1.8.0.1
		Fixed example's use of Ti.JSON [MOD-402]

v1.0    Initial Release
