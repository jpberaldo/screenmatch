package br.com.alura.screenmatch.controller;

import br.com.alura.screenmatch.dto.EpisodioDTO;
import br.com.alura.screenmatch.dto.SerieDTO;
import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.services.SerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/series")
public class SerieController {

    @Autowired
    private SerieService serieService;

    @GetMapping
    public List<SerieDTO> obterSeries() {
        return serieService.obterTodasSeries();
    }

    @GetMapping("/top5")
    public List<SerieDTO> obterTop5() {
        return serieService.obterTop5Series();
    }

    @GetMapping("/lancamentos")
    public List<SerieDTO> obterLancamentos(){
        return serieService.obterLancamentos();
    }

    @GetMapping("/{id}")
    public SerieDTO buscarPorId(@PathVariable Long id) {
        return serieService.buscarPorId(id);
    }

    @GetMapping("/{id}/temporadas/todas")
    public List<EpisodioDTO> buscarTemporadas(@PathVariable Long id){
        return serieService.buscarTodasAsTemporadas(id);
    }

    @GetMapping("/{id}/temporadas/{temporada}")
    public List<EpisodioDTO> buscarNumeroTemporada(@PathVariable Long id, @PathVariable Long temporada){
        return serieService.buscarTemporadaPorNumero(id, temporada);
    }

    @GetMapping("/categoria/{nomeCategoria}")
    public List<SerieDTO> buscarCategoriaPorGenero(@PathVariable String nomeCategoria){
        return serieService.buscarCategoriaPorGenero(nomeCategoria);
    }

    @GetMapping("/{id}/temporadas/top")
    public List<EpisodioDTO> buscarTopEpisodios(@PathVariable Long id){
        return serieService.obterTopEpisodios(id);
    }

}
