import spock.lang.Specification


/**
 * Created by mtumilowicz on 2018-11-14.
 */
class DelegateTest extends Specification {

    def "delegate"() {
        given:
        Closure closure = { delegate }

        expect:
        closure() == this
        closure().getClass() == DelegateTest.class
    }

    def "closure enclosed in closure"() {
        given:
        Closure closure = {
            Closure inner = { delegate }
            return inner
        }

        expect:
        closure()() == closure
        closure()().getClass() == closure.getClass()
    }

    class InnerClass {
        Closure inner = { delegate }
    }

    def "closure enclosed in inner class"() {
        given:
        def innerClass = new InnerClass()

        expect:
        innerClass.inner() == innerClass
        innerClass.inner().getClass() == InnerClass.class
    }

}