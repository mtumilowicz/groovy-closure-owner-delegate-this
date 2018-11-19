[![Build Status](https://travis-ci.com/mtumilowicz/groovy-closure-owner-delegate-this.svg?branch=master)](https://travis-ci.com/mtumilowicz/groovy-closure-owner-delegate-this)

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

## syntax
A closure definition follows this syntax:

`{ [closureParameters -> ] statements }`

Where `[closureParameters->]` is an optional comma-delimited list of 
parameters, and statements are 0 or more Groovy statements.

## Groovy closures vs lambda expressions
Groovy defines closures as instances of the Closure class. It makes 
it very different from lambda expressions in Java 8. Delegation is a 
key concept in Groovy closures which has no equivalent in lambdas.

# Owner, delegate and this
_Reference_: Thanks to https://github.com/JackKarichkovskiy for
helping me with described below intricacies.

* **this** corresponds to the enclosing class where the closure is 
defined.
* **owner** corresponds to the enclosing object where the closure is 
defined, which may be either a class or a closure.
* **delegate** corresponds to a third party object where methods 
calls or properties are resolved whenever the receiver of the message 
is not defined.

## this
* if the closure is defined in an inner class
`this` in the closure will return the inner class, not the top-level one
* in case of nested closures - `this` corresponds to the closest outer 
class, not the enclosing closure!

Tests are in `ThisTest`:
* `this` inside closure
    ```
    given:
    Closure closure = { this }
    
    expect:
    closure() == this
    closure().getClass() == ThisTest.class
    ```
* `this` inside closure inside closure
    ```
    given:
    Closure closure = {
        Closure inner = { this }
        return inner
    }
    
    expect:
    closure()() == this
    closure()().getClass() == ThisTest.class
    ```
* `this` inside closure inside inner class
    ```
    class InnerClass {
        Closure inner = { this }
    }
    ```
    ```
    given:
    def innerClass = new InnerClass()
    
    expect:
    innerClass.inner() == innerClass
    innerClass.inner().getClass() == InnerClass.class 
    ```

## owner
The owner of a closure is very similar to the definition of this 
in a closure with a subtle difference: **it will return the direct 
enclosing object, be it a closure or a class**.

* if the closure is defined in a inner class
`owner` in the closure will return the inner class, not the top-level one
* in case of nested closures `owner` corresponds to the enclosing 
closure, hence a different object from `this`!

Tests are in `OwnerTest`:
* `owner` inside closure
    ```
    given:
    Closure closure = { owner }
    
    expect:
    closure() == this
    closure().getClass() == OwnerTest.class
    ```
* `owner` inside closure inside closure
    ```
    given:
    Closure closure = {
        Closure inner = { owner }
        return inner
    }
    
    expect:
    closure()() == closure
    closure()().getClass() == closure.getClass()
    ```
* `owner` inside closure inside inner class
    ```
    class InnerClass {
        Closure inner = { owner }
    }    
    ```
    ```
    given:
    def innerClass = new InnerClass()
    
    expect:
    innerClass.inner() == innerClass
    innerClass.inner().getClass() == InnerClass.class
    ```

## delegate
While `closure-this` and `closure-owner` refer to the lexical scope of a 
closure, the delegate is a user defined object that a closure will use. 
By default, the delegate is set to `owner`.

Tests are in `DelegateTest` and are similar to `OwnerTest`

# mixing this-owner-delegate
Tests are in `DelegateOwnerThisTest`. 

In general - we test order  and scope of loading values used in closures.

We have couple of util classes (used during testing):
```
class Delegate {
    String value = "fromDelegate"

    String methodFromDelegate(String string) {
        string
    }
}
```
```
class Owner {
    String value = "fromOwner"
}
```
```
class This {
    String value = "fromThis"
}
```
```
class EmptyOwner {
}
```

and we use rehydrate method from Closure class:
> Returns a copy of this closure for which the delegate, owner and thisObject are
replaced with the supplied parameters.
```
public Closure<V> rehydrate(Object delegate, Object owner, Object thisObject)
```

Order and scope of loading values used in closures:

* local variables are used first
    ```
    given:
    def value = "this method"
    
    and:
    def closure = {
        methodFromDelegate(value)
    }
    
    when:
    def rehydratedClosure = closure.rehydrate(new Delegate(), new Owner(), new This())
    
    then:
    rehydratedClosure() == "this method"
    ```
* when we use `this.value` in a closure, the `this` is resolved first
    ```
    given:
    def closure = {
        methodFromDelegate(this.value)
    }
    
    when:
    def rehydratedClosure = closure.rehydrate(new Delegate(), new Owner(), new This())
    
    then:
    rehydratedClosure() == "fromThis"
    ```
* when we use `value` in a closure, the `owner` is resolved first
    ```
    given:
    def closure = {
        methodFromDelegate(value)
    }
    
    when:
    def rehydratedClosure = closure.rehydrate(new Delegate(), new Owner(), new This())
    
    then:
    rehydratedClosure() == "fromOwner"
    ```
* when we use `value` in a closure, the `owner` is resolved first (if `value` is not found)
then `delegate` is resolved
    ```
    given:
    def closure = {
        methodFromDelegate(value)
    }
    
    when:
    def rehydratedClosure = closure.rehydrate(new Delegate(), new EmptyOwner(), new This())
    
    then:
    rehydratedClosure() == "fromDelegate"
    ```

# resolving strategies
## summary
**Note that local variables are always looked up first, 
independently of the resolution strategy.**

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
## tests
Tests are in `ResolvingStrategiesTest`. They are quite straightforward.
The most interesting is testing `Closure.TO_SELF` strategy, because
we use `ExpandoMetaClass` to show key feature:
```
given:
ExpandoMetaClass.enableGlobally()

and:
def closure = {
    value
}

and:
closure.metaClass.value = "inClosure"

when:
closure.resolveStrategy = Closure.TO_SELF

then:
closure() == "inClosure"
```
