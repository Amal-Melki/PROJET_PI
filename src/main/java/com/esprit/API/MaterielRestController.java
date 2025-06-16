package com.esprit.API;

import com.esprit.modules.Materiels;
import com.esprit.services.ServiceMateriel;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/materiels")
public class MaterielRestController {

    private final ServiceMateriel serviceMateriel = new ServiceMateriel();

    @GetMapping("/stock-faible")
    public List<Materiels> getStockFaible() {
        return serviceMateriel.recuperer().stream()
                .filter(m -> m.getQuantite() < 10)
                .toList();
    }
}
