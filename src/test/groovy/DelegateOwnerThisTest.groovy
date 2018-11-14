import spock.lang.Specification


/**
 * Created by mtumilowicz on 2018-11-14.
 */
class DelegateOwnerThisTest extends Specification {

    def value = "fromDelegateOwnerThisTest"

    def "resolving strategy: LOCAL VARIABLES FIRST -> owner -> delegate"() {
        given:
        def value = "this method"

        def closure = {
            methodFromDelegate(value)
        }

        def rehydratedClosure = closure.rehydrate(new Delegate(), new Owner(), new This())

        expect:
        rehydratedClosure() == "this method"
    }
    
    def "resolving strategy: local variables first -> OWNER -> delegate"() {
        given:
        def closure = {
            methodFromDelegate(value)
        }

        def rehydratedClosure = closure.rehydrate(new Delegate(), new Owner(), new This())
        
        expect:
        rehydratedClosure() == "fromOwner"
    }

    def "resolving strategy: local variables first -> owner -> ?"() {
        given:
        def closure = {
            methodFromDelegate(value)
        }

        def rehydratedClosure = closure.rehydrate(new Delegate(), new EmptyOwner(), new This())
        
        expect:
        rehydratedClosure() == "fromDelegateOwnerThisTest"
    }
    
    class Delegate {
        String value = "fromDelegate"
        
        String methodFromDelegate(String string) {
            string
        }

        String methodFromDelegate() {
            value
        }
    }
    
    class This {
        String value = "fromThis"
    }

    class Owner {
        String value = "fromOwner"
    }

    class EmptyOwner {
    }
}