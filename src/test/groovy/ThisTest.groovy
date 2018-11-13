import spock.lang.Specification

/**
 * Created by mtumilowicz on 2018-11-14.
 */
class ThisTest extends Specification {

    def "this"() {
        given:
        Closure closure = { this }

        expect:
        closure().getClass() == ThisTest.class
    }

    def "closure enclosed in closure"() {
        given:
        Closure closure = {
            Closure inner = { this }
            return inner
        }

        expect:
        closure()().getClass() == ThisTest.class
    }

    class InnerClass {
        Closure inner = { this }
    }
    
    def "closure enclosed in inner class"() {

        expect:
        new InnerClass().inner().getClass() == InnerClass.class
    }
}
