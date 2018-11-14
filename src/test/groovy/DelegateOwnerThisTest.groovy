import spock.lang.Specification
import utils.*


/**
 * Created by mtumilowicz on 2018-11-14.
 */
class DelegateOwnerThisTest extends Specification {

    def "local variables first"() {
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
    }

    def "class This has field value, and we use this.value -> get from This"() {
        given:
        def closure = {
            methodFromDelegate(this.value)
        }

        when:
        def rehydratedClosure = closure.rehydrate(new Delegate(), new Owner(), new This())

        then:
        rehydratedClosure() == "fromThis"
    }

    def "class This has field value, and we use just value -> get from Owner"() {
        given:
        def closure = {
            methodFromDelegate(value)
        }

        when:
        def rehydratedClosure = closure.rehydrate(new Delegate(), new Owner(), new This())

        then:
        rehydratedClosure() == "fromOwner"
    }

    def "Owner does not have field value -> get from Delegate"() {
        given:
        def closure = {
            methodFromDelegate(value)
        }

        when:
        def rehydratedClosure = closure.rehydrate(new Delegate(), new EmptyOwner(), new This())

        then:
        rehydratedClosure() == "fromDelegate"
    }
}