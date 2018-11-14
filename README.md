# groovy-closure-owner-delegate-this
Groovy closures overview: owner vs delegate vs this.

_Reference_: http://groovy-lang.org/closures.html#closure-owner  

Note that local variables are always looked up first, independently of 
the resolution strategy.

* **OWNER_FIRST** - the closure will attempt 
to resolve property references and methods to the owner first, then 
the delegate - **this is the default strategy**.

* **DELEGATE_FIRST** - the closure will attempt to resolve property 
references and methods to the delegate first then the owner.

* **OWNER_ONLY** - the closure will resolve property 
references and methods to the owner only and not call the delegate 
at all.

* **DELEGATE_ONLY** - the closure will resolve property 
references and methods to the delegate only and entirely bypass 
the owner.

* **TO_SELF** - the closure will resolve property references to 
itself and go through the usual MetaClass look-up process. utils.This 
means that properties and methods are neither resolved from the 
owner nor the delegate, but only on the closure object itself. 
utils.This allows the developer to override getProperty using 
ExpandoMetaClass of the closure itself.

* **Note that local variables are always looked up first, 
independently of the resolution strategy.**