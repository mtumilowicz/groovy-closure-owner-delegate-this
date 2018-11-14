import spock.lang.Specification
import utils.*


/**
 * Created by mtumilowicz on 2018-11-14.
 */
class ResolvingStrategiesTest extends Specification {

    def "delegate first"() {
        given:
        def closure = {
            methodFromDelegate(value)
        }

        and:
        def rehydratedClosure = closure.rehydrate(new Delegate(), new Owner(), new This())
        
        when:
        rehydratedClosure.resolveStrategy = Closure.DELEGATE_FIRST

        then:
        rehydratedClosure() == "fromDelegate"
    }

    def "owner only"() {
        given:
        def closure = {
            methodFromDelegate(value)
        }

        and:
        def rehydratedClosure = closure.rehydrate(new Delegate(), new EmptyOwner(), new This())
        rehydratedClosure.resolveStrategy = Closure.OWNER_ONLY
        
        when:
        rehydratedClosure()

        then:
        thrown(MissingPropertyException)
    }
    
    def "to self"() {
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
    }
}