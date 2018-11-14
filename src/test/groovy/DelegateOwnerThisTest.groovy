import spock.lang.Specification


/**
 * Created by mtumilowicz on 2018-11-14.
 */
class DelegateOwnerThisTest extends Specification {

    def value = ""

    def "changing owner, this, delegate"() {
        given:
        def closure = {
            methodFromDelegate(value)
        }

        def rehydratedClosure = closure.rehydrate(new Delegate(), new Owner(), new This())
        
        expect:
        rehydratedClosure() == "fromOwner"
    }

    def "changing owner, this, delegate - value in the method scope"() {
        given:
        def value = "this method"
        
        def closure = {
            methodFromDelegate(value)
        }

        def rehydratedClosure = closure.rehydrate(new Delegate(), new Owner(), new This())

        expect:
        rehydratedClosure() == "this method"
    }
    
    class Delegate {
        String field = "fromDelegate"
        
        String methodFromDelegate(String string) {
            string
        }
    }
    
    class This {
        String value = "fromThis"
    }

    class Owner {
        String value = "fromOwner"
    }
}