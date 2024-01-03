package me.dio.credit.application.system.controller

import com.fasterxml.jackson.databind.ObjectMapper
import me.dio.credit.application.system.dto.request.CreditDto
import me.dio.credit.application.system.entity.Customer
import me.dio.credit.application.system.repository.CreditRepository
import me.dio.credit.application.system.repository.CustomerRepository
import me.dio.credit.application.system.service.impl.CreditService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.math.BigDecimal
import java.time.LocalDate

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration
class CreditResourceTest {

    @Autowired
    private lateinit var creditRepository: CreditRepository

    @Autowired
    private lateinit var customerRepository: CustomerRepository

    @Autowired
    private lateinit var creditService: CreditService

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    companion object {
        const val URL: String = "/api/credits"
    }
    @BeforeEach
    fun setup() = creditRepository.deleteAll()

    @AfterEach
    fun tearDown() = creditRepository.deleteAll()

    @Test
    fun `should create credit`(){
        // given
        val customCreditDTO: CreditDto = builderCreditDto()
        customerRepository.save(Customer(id = 1L, email = "vitor@vitor.com"))
        val valueAsString: String = objectMapper.writeValueAsString(customCreditDTO)
        creditService.save(customCreditDTO.toEntity())

        // when
        mockMvc.perform(
            MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)

        )// then
            .andExpect(MockMvcResultMatchers.jsonPath("$.creditCode").isNotEmpty())
            .andExpect(MockMvcResultMatchers.jsonPath("$.creditValue").value(0))
            .andExpect(MockMvcResultMatchers.jsonPath("$.numberOfInstallment").value(48))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("IN_PROGRESS"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.emailCustomer").value("vitor@vitor.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.incomeCustomer").value("0.0"))
            .andDo(MockMvcResultHandlers.print())
    }

    private fun builderCreditDto(
        creditValue: BigDecimal = BigDecimal(0),
        dayFirstOfInstallment: LocalDate = LocalDate.now().plusDays(5),
        numberOfInstallments: Int = 48,
        customerId: Long = 1L
    ) = CreditDto(
        creditValue = creditValue,
        dayFirstOfInstallment = dayFirstOfInstallment,
        numberOfInstallments = numberOfInstallments,
        customerId = customerId
    )
}
