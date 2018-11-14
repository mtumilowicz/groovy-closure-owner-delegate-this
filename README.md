# groovy-closure-owner-delegate-this
Groovy closures overview: owner vs delegate vs this.

_Reference_: http://groovy-lang.org/closures.html#closure-owner

Please refer also my other github projects about closures:
* https://github.com/mtumilowicz/groovy-dsl
* https://github.com/mtumilowicz/groovy-dsl-statemachine

# preface
## definition
A closure in Groovy is **an open, anonymous, block of code** that can 
take arguments, return a value and be assigned to a variable. A closure 
may reference variables declared in its surrounding scope. In opposition to 
the formal definition of a closure, Closure in the Groovy language **can 
also contain free variables which are defined outside of its surrounding 
scope**. While breaking the formal concept of a closure, it offers a 
variety of advantages which are described in this chapter.

Note that local variables are always looked up first, independently of 
the resolution strategy.

## syntax
A closure definition follows this syntax:

`{ [closureParameters -> ] statements }`
Where [closureParameters->] is an optional comma-delimited list of 
parameters, and statements are 0 or more Groovy statements.

## Groovy closures vs lambda expressions
Groovy defines closures as instances of the Closure class. It makes 
it very different from lambda expressions in Java 8. Delegation is a 
key concept in Groovy closures which has no equivalent in lambdas.

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
itself and go through the usual MetaClass look-up process. This 
means that properties and methods are neither resolved from the 
owner nor the delegate, but only on the closure object itself. 
This allows the developer to override getProperty using 
`ExpandoMetaClass` of the closure itself.

* **Note that local variables are always looked up first, 
independently of the resolution strategy.**