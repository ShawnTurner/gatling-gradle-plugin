import com.commercehub.gradle.plugin.MetricChecker

/**
 * Created by bmanley on 11/9/15.
 */
class MetricCheckerTest extends GroovyTestCase {

    void testFilterNulls() {
        // Given a list of values
        def list = [[null, 1234], ['null', 1234], ['nil', 1234], ['foo', 1234], [1, 1234], ['0', 1234]]

        // When the list is filtered
        def filteredList = MetricChecker.filterNulls(list)

        // Then null values are removed from the list
        assert filteredList.size() == 5
        assert !filteredList.contains(null)
    }
}
