package uk.ac.warwick.camcat.search

import org.junit.Test
import uk.ac.warwick.camcat.mvc.IntegrationTest
import uk.ac.warwick.camcat.search.queries.ModuleQuery
import uk.ac.warwick.util.termdates.AcademicYear
import java.math.BigDecimal

class ModuleQueryTest : IntegrationTest() {
  @Test
  fun testIsKeywordsOnly() {
    // acad year is always there, not considered as user input
    var query = ModuleQuery(
      keywords = "CS126-15",
      academicYear = AcademicYear.starting(2020)
    )
    assert(query.isKeywordsOnly())

    query = ModuleQuery(
      keywords = "CS126-15",
      academicYear = AcademicYear.starting(2020),
      levels = listOf("1"),
      creditValues = listOf(BigDecimal(1.2)),
      assessmentTypes = listOf("1"),
      departments = listOf("d")
    )
    assert(!query.isKeywordsOnly())

    query = ModuleQuery(
      keywords = " "
    )
    assert(!query.isKeywordsOnly())

    query = ModuleQuery(
      keywords = null
    )
    assert(!query.isKeywordsOnly())

    query = ModuleQuery(
      keywords = "CS126-15"
    )
    assert(query.isKeywordsOnly())

    query = ModuleQuery(
      keywords = "CS126"
    )
    assert(query.isKeywordsOnly())
  }
}
