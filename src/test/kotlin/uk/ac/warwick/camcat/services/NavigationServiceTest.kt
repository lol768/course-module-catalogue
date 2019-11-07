package uk.ac.warwick.camcat.services

import org.junit.Assert.assertEquals
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.context.support.WithAnonymousUser
import org.springframework.security.test.context.support.WithMockUser
import uk.ac.warwick.camcat.context.ContextTest
import uk.ac.warwick.camcat.system.security.Role

class NavigationServiceTest : ContextTest() {
  @Autowired
  private lateinit var navigationService: NavigationService

  private fun auth(): Authentication? = SecurityContextHolder.getContext().authentication

  private fun nav() = navigationService.navigation(auth())

  @Test
  @WithAnonymousUser
  fun testAnonymous() = assertEquals(emptyList<NavigationItem>(), nav())

  @Test
  @WithMockUser
  fun testUser() = assertEquals(listOf(NavigationPage("Home", "http://localhost/"), NavigationPage("Modules", "http://localhost/modules/")), nav())

  @Test
  @WithMockUser(roles = [Role.user, Role.masquerader])
  fun testMasquerader() = assertEquals(listOf("Home", "Modules", "Masquerade"), nav().map { it.label })

  @Test
  @WithMockUser(roles = [Role.user, Role.sysadmin])
  fun testSysadmin() = assertEquals(listOf("Home", "Modules", "Sysadmin"), nav().map { it.label })
}
