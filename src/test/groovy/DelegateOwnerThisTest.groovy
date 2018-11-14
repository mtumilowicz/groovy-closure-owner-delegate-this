import spock.lang.Specification


/**
 * Created by mtumilowicz on 2018-11-14.
 */
class DelegateOwnerThisTest extends Specification {

    def value = "fromDelegateOwnerThisTest"

    def "local variables first"() {
        given:
        def value = "this method"

        def closure = {
            methodFromDelegate(value)
        }

        def rehydratedClosure = closure.rehydrate(new Delegate(), new Owner(), new This())

        expect:
        rehydratedClosure() == "this method"
    }
    
    def "class This has field value, and we use this.value -> get from This"() {
        given:
        def closure = {
            methodFromDelegate(this.value)
        }

        def rehydratedClosure = closure.rehydrate(new Delegate(), new Owner(), new This())
        
        expect:
        rehydratedClosure() == "fromThis"
    }

    def "class This has field value, and we use just value -> get from Owner"() {
        given:
        def closure = {
            methodFromDelegate(value)
        }

        def rehydratedClosure = closure.rehydrate(new Delegate(), new Owner(), new This())

        expect:
        rehydratedClosure() == "fromOwner"
    }

    def "Owner does not have field value -> get from Delegate"() {
        given:
        def closure = {
            methodFromDelegate(value)
        }

        def rehydratedClosure = closure.rehydrate(new Delegate(), new EmptyOwner(), new This())

        expect:
        rehydratedClosure() == "fromDelegate"
    }

    def "Owner, Delegate does not have field value -> class where closure is defined"() {
        given:
        def closure = {
            methodFromDelegate(value)
        }

        def rehydratedClosure = closure.rehydrate(new EmptyDelegate(), new EmptyOwner(), new This())

        expect:
        rehydratedClosure() == "fromDelegateOwnerThisTest"
    }
    
    class Delegate {
        String value = "fromDelegate"
        
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

    class EmptyOwner {
    }

    class EmptyDelegate {
        String methodFromDelegate(String string) {
            string
        }
    }
}