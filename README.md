# BazaarAPI
To get the API it's as simple as:
```java
Bazaar.getAPI()
```
All functions can be found in the source.
Only the documented functions are intended to be used by third party plugins. Of course all the undocumented functions can be called as well, however due to their complex nature it's not recommended to do so.

It is important to notice that all calls to the API should be made async. Depending on the settings, the calls may be blocking.
