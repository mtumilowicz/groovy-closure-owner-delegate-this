import spock.lang.Specification


/**
 * Created by mtumilowicz on 2018-11-14.
 */
class OwnerTest extends Specification {

    def "owner"() {
        given:
        Closure closure = { owner }

        expect:
        closure() == this
        closure().getClass() == OwnerTest.class
    }

    def "closure enclosed in closure"() {
        given:
        Closure closure = {
            Closure inner = { owner }
            return inner
        }

        expect:
        closure()() == closure
        closure()().getClass() == closure.getClass()
    }

    class InnerClass {
        Closure inner = { owner }
    }

    def "closure enclosed in inner class"() {
        given:
        def innerClass = new InnerClass()

        expect:
        innerClass.inner() == innerClass
        innerClass.inner().getClass() == InnerClass.class
    }
}