package br.pucpr.authserver.costCenters.controller

import br.pucpr.authserver.SortDir
import br.pucpr.authserver.costCenters.CostCenterService
import br.pucpr.authserver.costCenters.controller.requests.CreateCostCenterRequest
import br.pucpr.authserver.costCenters.controller.requests.PatchCostCenterAdressRequest
import br.pucpr.authserver.costCenters.controller.requests.PatchCostCenterNameResquest
import br.pucpr.authserver.costCenters.controller.requests.PatchCostCenterValueRequest
import br.pucpr.authserver.costCenters.controller.responses.CostCenterResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/costCenter")
class CostCenterController(val service: CostCenterService) {

    @SecurityRequirement(name="AuthServer")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/insertCostCenter")
    fun insert(@Valid @RequestBody costCenter: CreateCostCenterRequest) =
        CostCenterResponse(service.insert(costCenter.toCostCenter()))
            .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }

    @SecurityRequirement(name="AuthServer")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/updateCenterCostName/{id}")
    fun updateName(
        @Valid
        @RequestBody request: PatchCostCenterNameResquest,
        @PathVariable id: Long,
    ): ResponseEntity <CostCenterResponse> {
        return service.updateName(id, request.nomeCentroDeCusto)
            ?.let{ ResponseEntity.ok(CostCenterResponse(it)) }
            ?: ResponseEntity.noContent().build()
    }

    @SecurityRequirement(name="AuthServer")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/updateCenterCostAdress/{id}")
    fun updateAdress(
        @Valid
        @RequestBody request: PatchCostCenterAdressRequest,
        @PathVariable id: Long,
    ): ResponseEntity <CostCenterResponse> {
        return service.updateAdress(id, request.enderecoCentroDeCusto)
            ?.let{ ResponseEntity.ok(CostCenterResponse(it)) }
            ?: ResponseEntity.noContent().build()
    }

    @SecurityRequirement(name="AuthServer")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/updateCenterCostValue/{id}")
    fun updateAdress(
        @Valid
        @RequestBody request: PatchCostCenterValueRequest,
        @PathVariable id: Long,
    ): ResponseEntity <CostCenterResponse> {
        return service.updateValueUndertaken(id, request.valorEmpreendido)
            ?.let{ ResponseEntity.ok(CostCenterResponse(it)) }
            ?: ResponseEntity.noContent().build()
    }

    @SecurityRequirement(name="AuthServer")
    @PreAuthorize("permitAll()")
    @GetMapping
    fun list(@RequestParam sortDir: String? = null) =
        service.findAll(SortDir.findOrThrow(sortDir ?: "ASC"))
            .map { CostCenterResponse(it) }.let { ResponseEntity.ok(it) }

    @SecurityRequirement(name="AuthServer")
    @PreAuthorize("permitAll()")
    @GetMapping("/getCostCenterByName/{nomeCentroDeCusto}")
    fun getByName(@PathVariable nomeCentroDeCusto: String) =
        service.findByName(nomeCentroDeCusto)
            ?.let { ResponseEntity.ok(CostCenterResponse(it)) }
            ?: ResponseEntity.notFound().build()

    @SecurityRequirement(name="AuthServer")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> =
        if (service.delete(id)) ResponseEntity.ok().build()
        else ResponseEntity.notFound().build()

}